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
import org.testeditor.ui.wizardpages.AbstractTestStructureWizardPage;

/**
 * Specialized wizard for the NewTestStructureHandlers except of the
 * NewTestProjectHandler.
 * 
 */
public class NewTestStructureWizard extends Wizard {

	private String newTestStructureName;
	private TestStructure parentTestStructure;

	@Override
	public boolean performFinish() {// Called before wizard is closed
		AbstractTestStructureWizardPage ntp = (AbstractTestStructureWizardPage) this.getPages()[0];
		newTestStructureName = ntp.getTextInNameText();
		parentTestStructure = ntp.getSelectedTestStrucutureElement();
		return true;

	}

	/**
	 * 
	 * @return name of the new teststructure.
	 */
	public String getNewTestStructureName() {
		return newTestStructureName;
	}

	/**
	 * 
	 * @return parent of the new teststructure.
	 */
	public TestStructure getNewTestStructureParent() {
		return parentTestStructure;
	}
}
