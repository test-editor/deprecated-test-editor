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

import org.apache.log4j.Logger;
import org.testeditor.ui.wizardpages.NewTestProjectWizardPage;

/**
 * special wizard for the creation of a new TestProject with the ui-menu.
 * 
 */
public class NewTestProjectWizard extends NewTestStructureWizard {

	private NewTestProjectWizardPage newTestProjectWizardPage;

	private static final Logger LOGGER = Logger.getLogger(NewTestProjectWizard.class);

	private String nameText;

	@Override
	public boolean performFinish() {
		newTestProjectWizardPage = (NewTestProjectWizardPage) this.getPages()[0];
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("starting creation of new project");
		}
		nameText = newTestProjectWizardPage.getTextInNameText();
		return true;
	}

	/**
	 * 
	 * @return the name of the new project.
	 */
	public String getNameText() {
		return nameText;
	}

}
