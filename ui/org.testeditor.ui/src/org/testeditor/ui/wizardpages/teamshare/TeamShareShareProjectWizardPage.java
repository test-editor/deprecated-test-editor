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

import javax.inject.Inject;

import org.eclipse.swt.graphics.Image;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestProjectConfig;
import org.testeditor.ui.constants.IconConstants;
import org.testeditor.ui.utilities.TestEditorTranslationService;

/**
 * WizardPage to share a project.
 * 
 * 
 */
public class TeamShareShareProjectWizardPage extends TeamShareWizardPage {

	private TestProject testProject;

	@Inject
	private TestEditorTranslationService translationService;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getTitleValue() {
		return translationService.translate("%popupmenu.label.shareProject");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDescriptionValue() {
		return translationService.translate("%share.project.wizard.msg");
	}

	@Override
	Image getIcon() {
		return IconConstants.ICON_SHARE_PROJECT;
	}

	/**
	 * 
	 * @return the testProject
	 */
	public TestProject getTestProject() {
		return testProject;
	}

	/**
	 * setter for the memeber-variable testProject.
	 * 
	 * @param testProject
	 *            TestProject
	 */
	public void setTestProject(TestProject testProject) {
		this.testProject = testProject;
	}

	/**
	 * @return the TestProjectConfig of the TestProject
	 */
	public TestProjectConfig getTestProjectConfig() {
		return testProject.getTestProjectConfig();
	}

	/**
	 * 
	 * @return boolean true if the state has a SVN Comment.
	 */
	@Override
	protected boolean hasComment() {
		return true;
	}

}
