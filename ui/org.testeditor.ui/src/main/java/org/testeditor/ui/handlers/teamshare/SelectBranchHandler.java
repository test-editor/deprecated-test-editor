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
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.services.interfaces.ProgressListener;
import org.testeditor.core.services.interfaces.TeamShareService;
import org.testeditor.core.services.interfaces.TestProjectService;
import org.testeditor.ui.constants.TestEditorConstants;
import org.testeditor.ui.handlers.CanExecuteTestExplorerHandlerRules;
import org.testeditor.ui.parts.testExplorer.TestExplorer;
import org.testeditor.ui.utilities.TestEditorTranslationService;
import org.testeditor.ui.wizardpages.teamshare.TeamShareBranchSelectionWizardPage;

/**
 * Handler to open a dialog to select a branch to work with.
 *
 */
public class SelectBranchHandler {

	private static final Logger LOGGER = Logger.getLogger(SelectBranchHandler.class);

	@Inject
	private TestEditorTranslationService translationService;

	/**
	 * is it possible to execute the handler.
	 * 
	 * @param context
	 *            used to determine the application state.
	 * @return true if the selected element belongs to a team shared project.
	 */
	@CanExecute
	public boolean canExecute(IEclipseContext context) {
		IStructuredSelection selection = getSelection(context);
		return new CanExecuteTestExplorerHandlerRules().canExecuteOnTeamShareProject(selection);
	}

	/**
	 * Get the current selection of the TestExplorer.
	 * 
	 * @param context
	 *            used to look up the TestExplorer.
	 * @return the actual selection in the TestExplorer
	 */
	private IStructuredSelection getSelection(IEclipseContext context) {
		TestExplorer testExplorer = (TestExplorer) context.get(TestEditorConstants.TEST_EXPLORER_VIEW);
		return testExplorer.getSelection();
	}

	/**
	 * Executes the selection dalog of another test release. When the user
	 * selects another branch the project is switched to it.
	 * 
	 * @param context
	 * @param shell
	 * @throws SystemException
	 */
	@Execute
	public void execute(final IEclipseContext context, @Named(IServiceConstants.ACTIVE_SHELL) final Shell shell,
			final TeamShareService teamShareService, final TestProjectService testProjectService)
					throws SystemException {
		Object firstElement = getSelection(context).getFirstElement();
		if (firstElement instanceof TestStructure) {
			final TestProject project = ((TestStructure) firstElement).getRootElement();
			if (teamShareService.isDirty(project)) {
				MessageDialog.openError(shell, translationService.translate("%error"),
						translationService.translate("%switch.branch.dialog.notPossible.error"));
				return;
			}

			Wizard newWizard = new Wizard() {

				@Override
				public boolean performFinish() {
					return true;
				}
			};
			final TeamShareBranchSelectionWizardPage page = ContextInjectionFactory
					.make(TeamShareBranchSelectionWizardPage.class, context);
			try {
				final Map<String, String> availableReleases = teamShareService.getAvailableReleases(project);
				page.setAvailableReleaseNames(availableReleases.keySet());
				newWizard.addPage(page);
				WizardDialog dialog = new WizardDialog(shell, newWizard);
				if (dialog.open() == Dialog.OK) {
					new ProgressMonitorDialog(shell).run(true, false, new IRunnableWithProgress() {

						@Override
						public void run(final IProgressMonitor monitor)
								throws InvocationTargetException, InterruptedException {
							teamShareService.addProgressListener(project, new ProgressListener() {

								@Override
								public void log(String progressInfo) {
									monitor.subTask(progressInfo);
								}

								@Override
								public boolean isCanceled() {
									return false;
								}
							});
							monitor.beginTask("update", IProgressMonitor.UNKNOWN);
							try {
								teamShareService.switchToBranch(project,
										availableReleases.get(page.getSelectedRelease()));
								testProjectService.storeProjectConfig(project, project.getTestProjectConfig());

								context.get(TestProjectService.class).reloadProjectList();
							} catch (SystemException e) {
								LOGGER.error(e.getMessage());
								MessageDialog.openError(shell, translationService.translate("%error"),
										translationService.translate("%switch.branch.dialog.error"));
							}
							monitor.done();
						}
					});
				}
			} catch (SystemException | InvocationTargetException e) {
				LOGGER.error(e.getMessage(), e);
				MessageDialog.openError(shell, translationService.translate("%error"),
						translationService.translate("%select.branch.dialog.error"));
			} catch (InterruptedException e) {
				LOGGER.error(e.getMessage());
			}
		}
	}

}
