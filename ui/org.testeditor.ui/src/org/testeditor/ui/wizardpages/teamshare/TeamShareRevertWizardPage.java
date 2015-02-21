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
import org.eclipse.swt.widgets.Composite;
import org.testeditor.core.services.interfaces.TeamShareConfigurationService;
import org.testeditor.ui.constants.IconConstants;
import org.testeditor.ui.utilities.TestEditorTranslationService;

/**
 * Wizard Page to confirm the revert operation.
 *
 */
public class TeamShareRevertWizardPage extends TeamShareWizardPage {

	@Inject
	private TestEditorTranslationService translationService;

	@Override
	String getTitleValue() {
		return translationService.translate("%popupmenu.label.revertElement");
	}

	@Override
	String getDescriptionValue() {
		return translationService.translate("%revert.element.wizard.msg");
	}

	@Override
	Image getIcon() {
		return IconConstants.ICON_REVERT_TESTSTRUCTURE;
	}

	/**
	 * creates the control.
	 * 
	 * @param parent
	 *            the parent Composite
	 */
	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);
		setPageComplete(true);

	}

	/**
	 * 
	 * @param configurationService
	 *            used to get the Fields to display in the detail Composite for
	 *            Team-Sharing-Configuration.
	 */
	@Override
	protected void createTeamShareSpeceficDetailComposite(TeamShareConfigurationService configurationService) {
	}

	/**
	 * Creates the UI widgets for the Team Share Configuration.
	 * 
	 * @param content
	 *            to add the TemShareGroup Widgets.
	 */
	@Override
	protected void createTeamShareConfigGroup(Composite content) {
	}

	/**
	 * @return the hintText
	 */
	@Override
	protected String getHintTextValue() {
		return translationService.translate("%wizard.teamRevert.msgText");
	}

	/**
	 * 
	 * @return the header of the hint-text.
	 */
	@Override
	protected String getHintTextHeaderValue() {
		return translationService.translate("%wizard.teamShare.msgHead");
	}

}
