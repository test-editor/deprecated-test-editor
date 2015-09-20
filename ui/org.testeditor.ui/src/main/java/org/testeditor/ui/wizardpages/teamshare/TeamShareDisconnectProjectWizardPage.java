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
import org.testeditor.core.services.plugins.TeamShareConfigurationServicePlugIn;
import org.testeditor.ui.constants.IconConstants;
import org.testeditor.ui.utilities.TestEditorTranslationService;

/**
 * 
 * @author dkuhlmann
 * 
 */
public class TeamShareDisconnectProjectWizardPage extends TeamShareWizardPage {

	@Inject
	private TestEditorTranslationService translationService;

	@Override
	String getTitleValue() {
		return translationService.translate("%popupmenu.label.disconnectProject");
	}

	@Override
	String getDescriptionValue() {
		return translationService.translate("%disconnect.project.wizard.msg");
	}

	@Override
	Image getIcon() {
		return IconConstants.ICON_DISCONNECT_PROJECT;
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
	protected void createTeamShareSpeceficDetailComposite(TeamShareConfigurationServicePlugIn configurationService) {
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
		return translationService.translate("%wizard.teamDisconnect.msgText");
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
