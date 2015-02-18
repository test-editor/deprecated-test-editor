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

import org.eclipse.swt.widgets.Composite;
import org.testeditor.ui.utilities.TestEditorTranslationService;

/**
 * abstract class for the renaming of test-structures.
 * 
 * @author llipinski
 * 
 */
public abstract class AbstractRenameTestStructureWizardPage extends AbstractTestStructureWizardPage {
	@Inject
	private TestEditorTranslationService translationService;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean isNameValid(String name) {
		if (!super.isNameValid(name)) {
			return false;
		}
		if (getSelectedTestStructure().getParent() != null && !(name.equals(getSelectedTestStructure().getName()))) {
			// ...by comparing all names within it's tree-level with the new
			// name

			if (isNamePartOfChildren(name, getSelectedTestStructure().getParent())) {
				return false;
			}
		}
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getTitleValue() {
		return translationService.translate("%popupmenu.label.rename.item") + ": "
				+ getSelectedTestStructure().getFullName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);

		// Show existing name
		getNameText().setText(getSelectedTestStructure().getName());
		// Select the name
		getNameText().selectAll();

		// Enable the 'finish' button
		setPageComplete(true);
	}

	/**
	 * no location-tree is in the rename-wizard-dialog necessary.
	 */
	protected void createLocationTree() {
	}
}
