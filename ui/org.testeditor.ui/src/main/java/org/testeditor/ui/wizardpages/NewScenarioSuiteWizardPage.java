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
package org.testeditor.ui.wizardpages;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.testeditor.core.services.interfaces.TestScenarioService;
import org.testeditor.ui.utilities.TestEditorTranslationService;
import org.testeditor.ui.wizardpages.nameinspector.INameInspector;
import org.testeditor.ui.wizardpages.nameinspector.TestScenarioNameInspector;

/**
 * New WizardPage for TestSuites.
 */
public class NewScenarioSuiteWizardPage extends AbstractNewTestStructureWizardPage {
	@Inject
	private TestEditorTranslationService translationService;
	@Inject
	private IEclipseContext context;
	@Inject
	private TestScenarioService scenarioService;
	private INameInspector nameInspector;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getTitleValue() {
		return translationService.translate("%popupmenu.label.new.scenario.suite");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDescriptionValue() {
		return translationService.translate("%new.wizard.whitemsg.scenariosuite");
	}

	@Override
	protected void setTreeFilter() {
		getTestStructureTree().showOnlyTestScenarioSuites();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getHintTextValue() {
		return super.getHintTextValue() + translationService.translate("%wizard.error.msgAddForScemario");
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @return the name-inspector for the testcase.
	 */
	@Override
	protected INameInspector getNameInspector() {
		if (nameInspector == null) {
			nameInspector = ContextInjectionFactory.make(TestScenarioNameInspector.class, context);
		}
		return nameInspector;
	}

	@Override
	protected boolean isNameValid(String name) {
		return super.isNameValid(name) && isSelectedTestStructureDescendedFromTestScenariosSuite();
	}

	/**
	 * 
	 * @return true, if the selected TestStructure is a descended of the
	 *         TestScenarioSuite.
	 */
	private boolean isSelectedTestStructureDescendedFromTestScenariosSuite() {
		if (scenarioService.isDescendantFromTestScenariosSuite(getSelectedTestStructure())) {
			return true;
		}
		this.setErrorMessage(translationService.translate("%wizard.error.msg.isNotDescendedOfScenarioSuite",
				getSelectedTestStructure().getFullName()));
		return false;
	}

}
