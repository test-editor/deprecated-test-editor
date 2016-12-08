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
package org.testeditor.ui.handlers;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.services.interfaces.TestProjectService;
import org.testeditor.ui.ApplicationLifeCycleHandler;
import org.testeditor.ui.utilities.TestEditorTranslationService;
import org.testeditor.ui.wizards.DemoWizard;

/**
 * This class creates a DemoProject.
 * 
 */
public class CreateDemoProjectsHandler {

	@Inject
	private static TestProjectService testProjectService;

	private static final Logger LOGGER = Logger.getLogger(CreateDemoProjectsHandler.class);

	/**
	 * This method creates new Test Project and restarts the FitNesse server.
	 * 
	 * @param context
	 *            {@link IEclipseContext}
	 * @param translationService
	 *            TestEditorTranslationService
	 * @param shell
	 *            the active Shell
	 */
	@Execute
	public void createProjectsAndStartsServers(final IEclipseContext context,
			final TestEditorTranslationService translationService, @Named(IServiceConstants.ACTIVE_SHELL) Shell shell) {
		DemoWizard wizard = ContextInjectionFactory.make(DemoWizard.class, context);

		WizardDialog wizDialog = new WizardDialog(shell, wizard);

		if (wizDialog.open() == Window.OK) {
			createDemoProjectsService(context, translationService, wizard.getDemoProjectsDirs());
		}
	}

	/**
	 * This method is to check if a demo project is missing and can be created.
	 * 
	 * @return true when Demo Projects are incomplete. Otherwise false
	 */
	@CanExecute
	public boolean canExecute() {

		try {
			return CanExecuteNewProjectOrDemoProjectsRules.canExecute(testProjectService);
		} catch (IOException e) {
			LOGGER.error(e);
			MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error", e.getLocalizedMessage());
		}
		return false;
	}

	/**
	 * Creates the selected demo projects.
	 * 
	 * @param context
	 *            UI
	 * @param translationService
	 *            localisation
	 * @param demoProjectsDir
	 *            list of selected demo projects
	 */
	public void createDemoProjectsService(final IEclipseContext context,
			final TestEditorTranslationService translationService, final List<File> demoProjectsDir) {
		ProgressMonitorDialog dialog = new ProgressMonitorDialog(Display.getCurrent().getActiveShell());
		try {
			dialog.run(true, false, new IRunnableWithProgress() {
				@Override
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					monitor.beginTask(translationService.translate("%TestExplorer.createDemoPorjects"),
							IProgressMonitor.UNKNOWN);
					Display.getDefault().asyncExec(new Runnable() {
						@Override
						public void run() {

							try {
								testProjectService.createAndConfigureDemoProjects(demoProjectsDir);
								ApplicationLifeCycleHandler lifeCycleHandler = ContextInjectionFactory.make(
										ApplicationLifeCycleHandler.class, context);
								lifeCycleHandler.startBackendServers();

							} catch (SystemException e) {
								LOGGER.trace("Error getting Projects", e);
								MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error", e.getMessage());
							}
						}

					});

				}
			});
		} catch (InvocationTargetException e1) {
			LOGGER.trace("createProjectsAndStartsServers", e1);
			MessageDialog.openError(Display.getCurrent().getActiveShell(), translationService.translate("%error"),
					e1.getLocalizedMessage());
		} catch (InterruptedException e1) {
			LOGGER.trace("createProjectsAndStartsServers", e1);
			MessageDialog.openError(Display.getCurrent().getActiveShell(), translationService.translate("%error"),
					e1.getLocalizedMessage());
		}
	}

}
