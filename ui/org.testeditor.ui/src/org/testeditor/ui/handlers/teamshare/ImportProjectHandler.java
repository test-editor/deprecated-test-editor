/*******************************************************************************
 * Copyright (c) 2012 - 2015 Signal Iduna Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Signal Iduna Corporation - initial API and implementation
 * akquinet AG
 *******************************************************************************/
package org.testeditor.ui.handlers.teamshare;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.testeditor.core.constants.TestEditorCoreEventConstants;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.exceptions.TeamAuthentificationException;
import org.testeditor.core.model.team.TeamShareConfig;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestProjectConfig;
import org.testeditor.core.services.interfaces.ProgressListener;
import org.testeditor.core.services.interfaces.TeamShareService;
import org.testeditor.core.services.interfaces.TestProjectService;
import org.testeditor.ui.ApplicationLifeCycleHandler;
import org.testeditor.ui.utilities.TestEditorTranslationService;
import org.testeditor.ui.wizardpages.teamshare.TeamShareImportProjectWizardPage;

/**
 * ImportProjectHandler to import a project from a TeamShare Server to the local
 * Workspace.
 * 
 */
public class ImportProjectHandler {

	@Inject
	private TestProjectService testProjectService;

	@Inject
	private TeamShareService teamShareService;

	@Inject
	private TestEditorTranslationService translationService;

	@Inject
	private IEventBroker eventBroker;

	@Inject
	private TranslationService translate;

	private TeamShareImportProjectWizardPage importProjectPage;

	private static final Logger LOGGER = Logger.getLogger(ImportProjectHandler.class);

	private String errorMessage;

	/**
	 * 
	 * @return importing of a project is at any time possible. So this method
	 *         returns every time true.
	 */
	@CanExecute
	boolean canExecute() {
		return true;
	}

	/**
	 * executes the wizard.
	 * 
	 * @param context
	 *            the IEclipseContext
	 */
	@Execute
	public void execute(final IEclipseContext context, @Named(IServiceConstants.ACTIVE_SHELL) Shell shell) {

		// create temporary project, that is used as the model of the wizard.
		// After a successful checkout, this object is filled from the
		// Filesystem data
		final TestProject testProject = new TestProject();
		final TestProjectConfig testProjectConfig = new TestProjectConfig();
		testProject.setTestProjectConfig(testProjectConfig);

		String workDirPath = Platform.getLocation().toFile().getAbsolutePath();
		TeamShareConfig tsConfig = null;

		class Marker {
			public boolean flag = false;
		}

		final Marker ok = new Marker();
		while (!ok.flag) {
			final WizardDialog wizardDialog = new WizardDialog(shell, createNewTeamShareImportWizard(context));

			// reset state of the wizard after the first usage
			importProjectPage.setTestProjectConfig(testProjectConfig);
			if (tsConfig != null) {
				importProjectPage.setTeamShareConfig(tsConfig);
			}
			if (!testProject.getName().isEmpty()) {
				importProjectPage.setProjectName(testProject.getName());
			}
			if (wizardDialog.open() == Window.OK) {
				try {
					final StringBuilder prPathStringBuilder = new StringBuilder(workDirPath);
					prPathStringBuilder.append(File.separatorChar).append(importProjectPage.getProjectName());

					testProject.setName(importProjectPage.getProjectName());
					testProject.getTestProjectConfig().setProjectPath(prPathStringBuilder.toString());
					testProject.getTestProjectConfig().setTeamShareConfig(importProjectPage.getTeamShareConfig());

					// store the entries of the user in this variable to allow
					// reusage in consecutive dialogs (after errors)
					tsConfig = importProjectPage.getTeamShareConfig();

					ProgressMonitorDialog dialog = new ProgressMonitorDialog(getDisplay().getActiveShell());
					dialog.run(true, true, new IRunnableWithProgress() {

						@Override
						public void run(final IProgressMonitor monitor)
								throws InvocationTargetException, InterruptedException {
							monitor.beginTask(translationService.translate("%import.project.wizard.msg"),
									IProgressMonitor.UNKNOWN);

							try {
								teamShareService.addProgressListener(testProject, new ProgressListener() {

									@Override
									public void log(String progressInfo) {

										monitor.subTask(progressInfo);

									}

									@Override
									public boolean isCanceled() {
										return monitor.isCanceled();
									}
								});

								teamShareService.checkout(testProject, translate);
								if (!monitor.isCanceled()) {
									TeamShareConfig oldTeamShareconfig = testProject.getTestProjectConfig()
											.getTeamShareConfig();
									testProjectService.reloadTestProjectFromFileSystem(testProject);
									testProject.getTestProjectConfig().setTeamShareConfig(oldTeamShareconfig);
									ApplicationLifeCycleHandler lifeCycleHandler = ContextInjectionFactory
											.make(ApplicationLifeCycleHandler.class, context);
									lifeCycleHandler.startBackendServer(testProject);
								}
								ok.flag = true;
							} catch (SystemException e) {
								File projectDir = new File(prPathStringBuilder.toString());
								// on a failure delete the new created
								// directory
								if (projectDir.exists()) {
									if (!projectDir.delete()) {
										getDisplay().syncExec(new Runnable() {
											@Override
											public void run() {
												MessageDialog.openError(Display.getDefault().getActiveShell(),
														translationService.translate("%error"),
														translationService.translate("%CouldNotDeleteDirectory"));
											}
										});
									}
								}
								errorMessage = e.getMessage();
								getDisplay().syncExec(new Runnable() {
									@Override
									public void run() {
										MessageDialog.openError(Display.getDefault().getActiveShell(),
												translationService.translate("%error"), errorMessage);
										errorMessage = "";
									}
								});

								LOGGER.error(e.getMessage());
							} catch (TeamAuthentificationException e) {
								getDisplay().syncExec(new Runnable() {
									@Override
									public void run() {
										MessageDialog.openError(Display.getDefault().getActiveShell(),
												translationService.translate("%error"),
												translationService.translate("%teamConfigCredentials.error"));
									}
								});
							}

						}
					});
				} catch (InvocationTargetException | InterruptedException e) {
					LOGGER.error(e.getMessage(), e);
					MessageDialog.openError(Display.getDefault().getActiveShell(),
							translationService.translate("%error"), errorMessage);
				}
			} else {
				ok.flag = true;
			}
		}

		eventBroker.send(TestEditorCoreEventConstants.TESTSTRUCTURE_MODEL_CHANGED_UPDATE_BY_MODIFY, "");

	}

	/**
	 * 
	 * @return new TestProject based on the Fields in the ImportPage.
	 */
	protected TestProject createNewTestProject() {
		TestProject testProject = new TestProject();
		final TestProjectConfig testProjectConfig = new TestProjectConfig();
		testProject.setName(importProjectPage.getProjectName());
		testProjectConfig.setTeamShareConfig(importProjectPage.getTeamShareConfig());
		testProject.setTestProjectConfig(testProjectConfig);
		return testProject;
	}

	/**
	 * 
	 * @param context
	 *            used to create the WizardPages.
	 * @return a New Wizard to select a Team Provider and checkouts a shared
	 *         <code>TestProject</code>.
	 */
	IWizard createNewTeamShareImportWizard(IEclipseContext context) {
		Wizard nwiz = new Wizard() {

			@Override
			public boolean performFinish() {
				return true;
			}
		};
		importProjectPage = ContextInjectionFactory.make(TeamShareImportProjectWizardPage.class, context);
		nwiz.addPage(importProjectPage);
		return nwiz;
	}

	/**
	 * gets the display.
	 * 
	 * @return the display
	 */
	private static Display getDisplay() {
		Display display = Display.getCurrent();
		// may be null if outside the UI thread
		if (display == null) {
			display = Display.getDefault();
		}
		return display;
	}

}
