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
import org.testeditor.ui.utilities.TestEditorTranslationService;
import org.testeditor.ui.wizardpages.nameinspector.INameInspector;
import org.testeditor.ui.wizardpages.nameinspector.TestScenarioNameInspector;

/**
 * wizard for renaming of test-scenarios.
 * 
 * @author llipinski
 * 
 */
public class RenameScenarioSuiteWizardPage extends AbstractRenameTestStructureWizardPage {
	@Inject
	private TestEditorTranslationService translationService;
	@Inject
	private IEclipseContext context;
	private INameInspector nameInspector;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDescriptionValue() {
		return translationService.translate("%rename.wizard.whitemsg.scenariosuite");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getHintTextValue() {
		return super.getHintTextValue() + translationService.translate("%wizard.error.msgAddForSuiteScemario");
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
}
