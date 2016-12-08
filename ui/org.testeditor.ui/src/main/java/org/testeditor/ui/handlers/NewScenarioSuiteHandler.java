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

import org.apache.log4j.Logger;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.testeditor.core.model.teststructure.ScenarioSuite;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.ui.constants.TestEditorConstants;
import org.testeditor.ui.wizardpages.AbstractNewTestStructureWizardPage;
import org.testeditor.ui.wizardpages.NewScenarioSuiteWizardPage;

/**
 * NewSceanrioSuiteHandler Handler is called for creating a new ScenarioSuite.
 * 
 */
public class NewScenarioSuiteHandler extends NewTestStructureHandler {

	private static final Logger LOGGER = Logger.getLogger(NewScenarioSuiteHandler.class);

	@Override
	protected boolean canOpenObject() {
		return false;
	}

	@Override
	protected String getWindowTitle() {
		return "%popupmenu.label.new.scenario.suite";
	}

	@Override
	protected TestStructure createNewTestStructure(IEclipseContext context) {
		ScenarioSuite scenarioSuite = new ScenarioSuite();
		return scenarioSuite;
	}

	@Override
	protected AbstractNewTestStructureWizardPage getNewTestStructureWizardPage(TestStructure selectedTS,
			IEclipseContext context) {
		NewScenarioSuiteWizardPage testSuiteWizardPage = ContextInjectionFactory.make(NewScenarioSuiteWizardPage.class,
				context);
		testSuiteWizardPage.setSelectedTestStructure(selectedTS);
		return testSuiteWizardPage;
	}

	/**
	 * special canExecute method for ScenarioSuite.
	 * 
	 * @param ignoreCanExecute
	 *            ignore this Method in File menu
	 * @param context
	 *            EclipseContext to retrieve the TestExplorer from
	 * @return false, if the selection is the TestKomponenten-Suite
	 */
	@CanExecute
	public boolean canExecute(IEclipseContext context,
			@Named("org.testeditor.ui.newscenariosuite.command.parameter.canExecuteFromMainMenu") @Optional String ignoreCanExecute) {
		// the ignoreCanExecute comes from the Application.xmi, in case of menu
		// handling
		if (ignoreCanExecute != null && Boolean.parseBoolean(ignoreCanExecute)) {
			return !getTestProjectService().getProjects().isEmpty();
		}
		IStructuredSelection selection = (IStructuredSelection) context
				.get(TestEditorConstants.SELECTED_TEST_COMPONENTS);
		CanExecuteTestExplorerHandlerRules handlerRules = ContextInjectionFactory
				.make(CanExecuteTestExplorerHandlerRules.class, context);
		return handlerRules.canExecuteOnTestScenarienSuiteRule(selection);
	}

}
