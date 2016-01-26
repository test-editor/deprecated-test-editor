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

import javax.inject.Named;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.services.interfaces.TeamShareService;
import org.testeditor.ui.constants.TestEditorConstants;
import org.testeditor.ui.handlers.CanExecuteTestExplorerHandlerRules;
import org.testeditor.ui.parts.testExplorer.TestExplorer;
import org.testeditor.ui.wizardpages.teamshare.TeamShareBranchSelectionWizardPage;

/**
 * Handler to open a dialog to select a branch to work with.
 *
 */
public class SelectBranchHandler {

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
	 * 
	 * @param context
	 * @param shell
	 */
	@Execute
	public void execute(IEclipseContext context, @Named(IServiceConstants.ACTIVE_SHELL) Shell shell,
			TeamShareService teamShareService) {
		Object firstElement = getSelection(context).getFirstElement();
		if (firstElement instanceof TestStructure) {
			TestProject project = ((TestStructure) firstElement).getRootElement();
			Wizard newWizard = new Wizard() {

				@Override
				public boolean performFinish() {
					// TODO Auto-generated method stub
					return false;
				}
			};
			TeamShareBranchSelectionWizardPage page = ContextInjectionFactory
					.make(TeamShareBranchSelectionWizardPage.class, context);
			try {
				page.setAvailableReleaseNames(teamShareService.getAvailableReleaseNames(project));
			} catch (SystemException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			newWizard.addPage(page);
			WizardDialog dialog = new WizardDialog(shell, newWizard);
			dialog.open();
		}
	}

}
