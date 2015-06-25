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

import javax.inject.Named;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.model.teststructure.TestScenario;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.ui.constants.TestEditorConstants;
import org.testeditor.ui.wizardpages.AbstractNewTestStructureWizardPage;
import org.testeditor.ui.wizardpages.NewTestScenarioWizardPage;
import org.testeditor.ui.wizards.NewTestStructureWizard;

/**
 * NewSuiteHandler Handler is called for creating a new TestScenario.
 * 
 */
public class NewScenarioHandler extends NewTestStructureHandler {

	@Override
	protected TestStructure createNewTestStructure(IEclipseContext context) {
		TestScenario testScenario = new TestScenario();
		return testScenario;

	}

	@Override
	protected String getWindowTitle() {
		return "%popupmenu.label.new.test.scenario";
	}

	@Override
	protected boolean canOpenObject() {
		return true;
	}

	/**
	 * special canExecute method for testScenario.
	 * 
	 * @param ignoreCanExecute
	 *            ignore this Method in File menu
	 * @param context
	 *            EclipseContext to retrieve the TestExplorer from
	 * @return true, if the selection is the TestKomponenten-Suite
	 */
	@CanExecute
	public boolean canExecute(
			IEclipseContext context,
			@Named("org.testeditor.ui.newtestscenario.command.parameter.canExecuteFromMainMenu") @Optional String ignoreCanExecute) {
		// the ignoreCanExecute comes from the Application.xmi, in case of menu
		// handling
		if (ignoreCanExecute != null && Boolean.parseBoolean(ignoreCanExecute)) {
			return !getTestProjectService().getProjects().isEmpty();
		}
		IStructuredSelection selection = (IStructuredSelection) context
				.get(TestEditorConstants.SELECTED_TEST_COMPONENTS);
		CanExecuteTestExplorerHandlerRules handlerRules = ContextInjectionFactory.make(
				CanExecuteTestExplorerHandlerRules.class, context);
		return handlerRules.canExecuteOnTestScenarienSuiteRule(selection)
				|| handlerRules.canExecuteOnDescendantFromTestScenarioSuite(selection);
	}

	@Override
	protected AbstractNewTestStructureWizardPage getNewTestStructureWizardPage(TestStructure selectedTS,
			IEclipseContext context) {
		NewTestScenarioWizardPage testScenarioWizardPage = ContextInjectionFactory.make(
				NewTestScenarioWizardPage.class, context);
		testScenarioWizardPage.setSelectedTestStructure(selectedTS);
		return testScenarioWizardPage;
	}

	@Override
	protected Wizard getWizard(IEclipseContext context) {
		return new NewTestStructureWizard(this);
	}

	@Override
	protected void createAndOpenTestStructure(TestStructure testStructure, IEclipseContext context)
			throws SystemException {
		super.createAndOpenTestStructure(testStructure, context);
		ContextInjectionFactory.make(OpenTestStructureHandler.class, context).execute((TestScenario) testStructure,
				context);
	}

}
