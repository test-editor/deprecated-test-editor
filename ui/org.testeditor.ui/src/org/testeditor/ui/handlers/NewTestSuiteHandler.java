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
import org.eclipse.jface.wizard.Wizard;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.model.teststructure.TestSuite;
import org.testeditor.ui.constants.TestEditorConstants;
import org.testeditor.ui.parts.testExplorer.TestExplorer;
import org.testeditor.ui.wizardpages.AbstractNewTestStructureWizardPage;
import org.testeditor.ui.wizardpages.NewTestSuiteWizardPage;
import org.testeditor.ui.wizards.NewTestStructureWizard;

/**
 * NewSuiteHandler Handler is called for creating a new TestSuite.
 * 
 */
public class NewTestSuiteHandler extends NewTestStructureHandler {

	@Override
	protected boolean canOpenObject() {
		return false;
	}

	@Override
	protected String getWindowTitle() {
		return "%popupmenu.label.new.test.suite";
	}

	@Override
	protected TestStructure createNewTestStructure(IEclipseContext context) {
		return new TestSuite();
	}

	@Override
	protected AbstractNewTestStructureWizardPage getNewTestStructureWizardPage(TestStructure selectedTS,
			IEclipseContext context) {
		NewTestSuiteWizardPage testSuiteWizardPage = ContextInjectionFactory
				.make(NewTestSuiteWizardPage.class, context);
		testSuiteWizardPage.setSelectedTestStructure(selectedTS);
		return testSuiteWizardPage;
	}

	/**
	 * special canExecute method for TestSuite.
	 * 
	 * @param ignoreCanExecute
	 *            ignore this Method in File menu
	 * @param context
	 *            EclipseContext to retrieve the TestExplorer from
	 * @return false, if the selection is the TestKomponenten-Suite
	 */
	@CanExecute
	public boolean canExecute(
			IEclipseContext context,
			@Named("org.testeditor.ui.newsuite.command.parameter.canExecuteFromMainMenu") @Optional String ignoreCanExecute) {
		if (ignoreCanExecute != null && Boolean.parseBoolean(ignoreCanExecute)) {
			return !getTestProjectService().getProjects().isEmpty();
		}
		TestExplorer testExplorer = (TestExplorer) context.get(TestEditorConstants.TEST_EXPLORER_VIEW);
		CanExecuteTestExplorerHandlerRules handlerRules = new CanExecuteTestExplorerHandlerRules();
		return super.canExecute(context) && !handlerRules.canExecuteOnTestScenarienSuiteRule(testExplorer);
	}

	@Override
	protected Wizard getWizard(IEclipseContext context) {
		return new NewTestStructureWizard(this);
	}
}
