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
package org.testeditor.ui.wizardpages.teamshare;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;
import org.testeditor.core.model.teststructure.TestProject;

/**
 * Wizard Page to show available branches of the team provider and select one of
 * them. Multiple selection is not supported.
 *
 */
public class TeamShareBranchSelectionWizardPage extends WizardPage {

	private TestProject project;

	/**
	 * Default constructor.
	 */
	public TeamShareBranchSelectionWizardPage() {
		this("");
	}

	/**
	 * 
	 * @param pageName
	 *            of the wizard page.
	 */
	protected TeamShareBranchSelectionWizardPage(String pageName) {
		super(pageName);
	}

	@Override
	public void createControl(Composite parent) {

	}

	/**
	 * sets the project to work on.
	 * 
	 * @param project
	 *            to select the branches from.
	 */
	public void setProject(TestProject project) {
		this.project = project;
	}

}
