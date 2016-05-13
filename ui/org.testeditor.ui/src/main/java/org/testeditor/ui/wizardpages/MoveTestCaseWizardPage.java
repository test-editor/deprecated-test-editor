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

import org.testeditor.ui.utilities.TestEditorTranslationService;
import org.testeditor.ui.wizardpages.nameinspector.INameInspector;

/**
 * New WizardPage for TestSuites.
 */
public class MoveTestCaseWizardPage extends AbstractNewTestStructureWizardPage {
	@Inject
	private TestEditorTranslationService translationService;

	/**
	 * {@inheritDoc}
	 */
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getTitleValue() {
		return translationService.translate("%popupmenu.label.move.test.case");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDescriptionValue() {
		return translationService.translate("%move.wizard.whitemsg.testcase", getSelectedTestStructure().getName(),
				getSelectedTestStructure().getParent().getFullName());
	}

	@Override
	protected void setTreeFilter() {
		getTestStructureTree().showOnlyParentStructuresOfSuites(getSelectedTestStructure().getRootElement());
	}

	@Override
	protected INameInspector getNameInspector() {
		return null;
	}

}
