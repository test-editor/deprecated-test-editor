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
package org.testeditor.ui.handlers.move;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.testeditor.core.model.teststructure.TestCase;
import org.testeditor.core.model.teststructure.TestFlow;
import org.testeditor.core.model.teststructure.TestScenario;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.ui.constants.TestEditorConstants;
import org.testeditor.ui.utilities.TestEditorTranslationService;
import org.testeditor.ui.wizardpages.AbstractNewTestStructureWizardPage;
import org.testeditor.ui.wizardpages.AbstractTestStructureWizardPage;
import org.testeditor.ui.wizardpages.MoveScenarioWizardPage;
import org.testeditor.ui.wizardpages.MoveTestCaseWizardPage;
import org.testeditor.ui.wizards.MoveItemWizard;

/**
 * rename-handler for the testcases.
 * 
 */
public class MoveItemHandler {

	@Inject
	private TestEditorTranslationService translationService;

	@CanExecute
	public boolean canExecute(IEclipseContext context) {
		IStructuredSelection selection = (IStructuredSelection) context
				.get(TestEditorConstants.SELECTED_TEST_COMPONENTS);
		TestFlow lastSelection = (TestFlow) selection.getFirstElement();
		return lastSelection instanceof TestCase || lastSelection instanceof TestScenario;
	}

	/**
	 * executes the disconnecting of the project.
	 * 
	 * @param context
	 *            IEclipseContext
	 */
	@Execute
	public void execute(IEclipseContext context, @Named(IServiceConstants.ACTIVE_SHELL) Shell shell) {
		MoveItemWizard nwiz = getWizard(context);

		IStructuredSelection selection = (IStructuredSelection) context
				.get(TestEditorConstants.SELECTED_TEST_COMPONENTS);
		TestFlow lastSelection = (TestFlow) selection.getFirstElement();

		nwiz.setWindowTitle(translationService.translate("%popupmenu.label.move.item"));

		// Add the new-page to the wizard
		AbstractTestStructureWizardPage newTestPage = getNewTestStructureWizardPage(lastSelection, context);
		nwiz.addPage(newTestPage);

		// Show the wizard...
		WizardDialog wizardDialog = new WizardDialog(shell, nwiz);

		wizardDialog.open();

		return;
	}

	protected MoveItemWizard getWizard(IEclipseContext context) {
		return new MoveItemWizard();
	}

	protected AbstractNewTestStructureWizardPage getNewTestStructureWizardPage(TestStructure selectedTS,
			IEclipseContext context) {
		AbstractNewTestStructureWizardPage moveItemWizardPage = null;
		if (selectedTS instanceof TestCase) {
			moveItemWizardPage = ContextInjectionFactory.make(MoveTestCaseWizardPage.class, context);
		} else if (selectedTS instanceof TestScenario) {
			moveItemWizardPage = ContextInjectionFactory.make(MoveScenarioWizardPage.class, context);
		} else {
			throw new IllegalArgumentException("Illegal type " + selectedTS.getClass().getName());
		}
		moveItemWizardPage.setRenderNameField(false);
		moveItemWizardPage.setSelectedTestStructure(selectedTS);
		return moveItemWizardPage;
	}

}
