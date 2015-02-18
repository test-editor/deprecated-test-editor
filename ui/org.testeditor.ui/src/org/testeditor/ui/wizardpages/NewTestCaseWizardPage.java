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
import org.testeditor.ui.wizardpages.nameinspector.TestCaseNameInspector;

/**
 * 
 * this class provides the special parts of the NewTestCasePage an extends the
 * {@link AbstractNewTestStructureWizardPage}.
 * 
 */
public class NewTestCaseWizardPage extends AbstractNewTestStructureWizardPage {

	@Inject
	private TestEditorTranslationService translationService;
	@Inject
	private IEclipseContext context;
	private INameInspector nameInspector;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getTitleValue() {
		return translationService.translate("%popupmenu.label.new.test.case");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDescriptionValue() {
		return translationService.translate("%new.wizard.whitemsg.case");
	}

	@Override
	protected void setTreeFilter() {
		getTestStructureTree().showOnlyParentStructures();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getHintTextValue() {
		return super.getHintTextValue() + translationService.translate("%wizard.error.msgAddForTestCase");
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @return the name-inspector for the testcase.
	 */
	@Override
	protected INameInspector getNameInspector() {
		if (nameInspector == null) {
			nameInspector = ContextInjectionFactory.make(TestCaseNameInspector.class, context);
		}

		return nameInspector;
	}
}
