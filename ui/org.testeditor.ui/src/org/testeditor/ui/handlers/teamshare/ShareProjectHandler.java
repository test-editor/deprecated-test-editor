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

import java.lang.reflect.InvocationTargetException;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.services.internal.events.EventBroker;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.services.interfaces.ProgressListener;
import org.testeditor.core.services.interfaces.TeamShareService;
import org.testeditor.core.services.interfaces.TestProjectService;
import org.testeditor.ui.constants.TestEditorConstants;
import org.testeditor.ui.constants.TestEditorUIEventConstants;
import org.testeditor.ui.handlers.CanExecuteTestExplorerHandlerRules;
import org.testeditor.ui.parts.testExplorer.TestExplorer;
import org.testeditor.ui.utilities.TestEditorTranslationService;
import org.testeditor.ui.wizardpages.teamshare.TeamShareShareProjectWizardPage;

/**
 * Handler to open a Wizard Dialog to select a Sharing Service to share a
 * Project.
 * 
 * This Handler is enabled an <Code>TestProject</code> entries without a team
 * configuration in the TestExplorer tree.
 * 
 */
@SuppressWarnings("restriction")
public class ShareProjectHandler {

	@Inject
	private TeamShareService teamShareService;
	@Inject
	private TestEditorTranslationService translationService;
	@Inject
	private TestProjectService testProjectService;
	@Inject
	private TranslationService translate;
	@Inject
	private EventBroker eventBroker;

	@Inject
	@Named(IServiceConstants.ACTIVE_SHELL)
	private Shell shell;

	private String errorMessage;

	private TeamShareShareProjectWizardPage shareProjectPage;

	private static final Logger LOGGER = Logger.getLogger(ShareProjectHandler.class);

	/**
	 * Checks if the handler can execute on the current selection in the
	 * TestExplorer.
	 * 
	 * @param context
	 *            current Context of the Application.
	 * @return true if the selection is only one TestProject without a Team
	 *         Share configuration in other cases false.
	 */
	@CanExecute
	public boolean canExecute(IEclipseContext context) {
		TestExplorer testExplorer = (TestExplorer) context.get(TestEditorConstants.TEST_EXPLORER_VIEW);
		CanExecuteTestExplorerHandlerRules handlerRules = new CanExecuteTestExplorerHandlerRules();
		if (handlerRules.canExecuteOnTestProjectRule(testExplorer.getSelection())
				&& handlerRules.canExecuteOnlyOneElementRule(testExplorer.getSelection())) {
			TestProject selection = (TestProject) testExplorer.getSelection().getFirstElement();
			return !selection.getTestProjectConfig().isTeamSharedProject();
		}
		return false;
	}

	/**
	 * executes the sharing of the project.
	 * 
	 * @param context
	 *            IEclipseContext
	 */
	@Execute
	public void execute(IEclipseContext context) {
		TestExplorer testExplorer = (TestExplorer) context.get(TestEditorConstants.TEST_EXPLORER_VIEW);
		final TestProject testProject = (TestProject) testExplorer.getSelection().getFirstElement();

		// New Wizard
		Wizard nwiz = new Wizard() {

			@Override
			public boolean performFinish() {
				return true;
			}
		};

		// Add the new-page to the wizard
		shareProjectPage = ContextInjectionFactory.make(TeamShareShareProjectWizardPage.class, context);

		shareProjectPage.setTestProject(testProject);
		nwiz.addPage(shareProjectPage);
		nwiz.setWindowTitle(shareProjectPage.getTitleValue());

		// Show the wizard...
		WizardDialog wizardDialog = new WizardDialog(shell, nwiz);

		if (wizardDialog.open() == Window.OK) {
			ProgressMonitorDialog dialog = new ProgressMonitorDialog(Display.getCurrent().getActiveShell());
			try {
				dialog.run(true, false, new IRunnableWithProgress() {

					@Override
					public void run(final IProgressMonitor monitor) throws InvocationTargetException,
							InterruptedException {
						monitor.beginTask(translationService.translate("%share.project.progress.msg"),
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

							shareProject(testProject, shareProjectPage.getSvnComment());

						} catch (SystemException e) {
							testProject.getTestProjectConfig().setTeamShareConfig(null); // an
																							// failure
																							// reset
																							// the
																							// teamShareConfiguration.
							errorMessage = e.getMessage();
							getDisplay().syncExec(new Runnable() {
								@Override
								public void run() {
									MessageDialog.openError(getDisplay().getActiveShell(), "Error", errorMessage);
									errorMessage = "";
								}
							});
						}

					}
				});
			} catch (InvocationTargetException e) {
				LOGGER.error(e.getMessage());
				testProject.getTestProjectConfig().setTeamShareConfig(null);
			} catch (InterruptedException e) {
				LOGGER.error(e.getMessage());
				testProject.getTestProjectConfig().setTeamShareConfig(null);
			}
			testExplorer.refreshTreeInput();
		} else {
			testProject.getTestProjectConfig().setTeamShareConfig(null);
		}
	}

	/**
	 * shares the testProject.
	 * 
	 * @param testProject
	 *            TestProject
	 * @param svnComment
	 *            String
	 * @throws SystemException
	 *             if the storing of the configuration or the teamsharing fails.
	 */
	private void shareProject(TestProject testProject, String svnComment) throws SystemException {

		testProjectService.storeProjectConfig(testProject, testProject.getTestProjectConfig());

		try {

			teamShareService.share(testProject, translate, svnComment);
			eventBroker.send(TestEditorUIEventConstants.PROJECT_TEAM_SHARED, testProject.getFullName());

		} catch (Exception e) {
			// rollback project configuration
			testProject.getTestProjectConfig().setTeamShareConfig(null);
			testProjectService.storeProjectConfig(testProject, testProject.getTestProjectConfig());

			LOGGER.error("Rollback ProjectConfig (config.tpr, svn-team-share.prefs)");
			throw e;
		}

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
