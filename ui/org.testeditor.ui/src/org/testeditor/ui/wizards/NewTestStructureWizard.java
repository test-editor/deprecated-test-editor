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
package org.testeditor.ui.wizards;

import org.eclipse.jface.wizard.Wizard;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.ui.handlers.NewTestStructureHandler;
import org.testeditor.ui.wizardpages.AbstractTestStructureWizardPage;

/**
 * specialized wizard for the NewTestStructureHandlers except of the
 * NewTestProjectHandler.
 * 
 * @author llipinski
 * 
 */
public class NewTestStructureWizard extends Wizard {

	private NewTestStructureHandler newHandler;

	/**
	 * 
	 * @param newHandler
	 *            NewTestStructureHandler
	 */
	public NewTestStructureWizard(NewTestStructureHandler newHandler) {
		super();
		this.newHandler = newHandler;
	}

	@Override
	public boolean performFinish() {// Called before wizard is closed
		AbstractTestStructureWizardPage ntp = (AbstractTestStructureWizardPage) this.getPages()[0];
		setNewTestStructureName(ntp.getTextInNameText());
		setParentTestStructure(ntp.getSelectedTestStrucutureElement());
		return true;

	}

	/**
	 * set the parent of the TestStrucure.
	 * 
	 * @param selectedTestStrucutureElement
	 *            parent of the TestStrucure
	 */
	private void setParentTestStructure(TestStructure selectedTestStrucutureElement) {
		newHandler.setParentTestStructure(selectedTestStrucutureElement);

	}

	/**
	 * set the name of the TestStructure.
	 * 
	 * @param textInNameText
	 *            new name of the TestStructure
	 */
	private void setNewTestStructureName(String textInNameText) {
		newHandler.setNewTestStructureName(textInNameText);

	}

}
