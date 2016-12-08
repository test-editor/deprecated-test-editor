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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.testeditor.core.services.interfaces.TestProjectService;
import org.testeditor.ui.constants.CustomWidgetIdConstants;
import org.testeditor.ui.constants.IconConstants;
import org.testeditor.ui.utilities.TestEditorTranslationService;

/**
 * WizardPage for the import of projects.
 * 
 */
public class TeamShareImportProjectWizardPage extends TeamShareWizardPage {

	@Inject
	private TestEditorTranslationService translationService;
	@Inject
	private TestProjectService testProjectService;

	private Text projectNameText;
	private String projectName = "";

	/**
	 * constructor.
	 */
	public TeamShareImportProjectWizardPage() {
		super("");
	}

	/**
	 * 
	 * @param pageName
	 *            name of the page.
	 */
	protected TeamShareImportProjectWizardPage(String pageName) {
		super(pageName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getTitleValue() {
		return translationService.translate("%popupmenu.label.importProject");
	}

	/**
	 * @return the hintText
	 */
	@Override
	protected String getHintTextValue() {
		return translationService.translate("%wizard.teamShareload.msgText", "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDescriptionValue() {
		return translationService.translate("%import.project.wizard.msg");
	}

	@Override
	protected Image getIcon() {
		return IconConstants.ICON_IMPORT_PROJECT;
	}

	@Override
	protected void createSpecialGroup(Composite parent) {

		Composite innerComposite = new Composite(parent, SWT.FILL);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		innerComposite.setLayoutData(gd);
		innerComposite.setLayout(new GridLayout(2, false));

		Label label = new Label(innerComposite, SWT.NORMAL);
		label.setText(translationService.translate("%wizard.label.importProjectName"));
		projectNameText = new Text(innerComposite, SWT.NORMAL);
		projectNameText.setText(projectName);
		projectNameText.setData(CustomWidgetIdConstants.TEST_EDITOR_WIDGET_ID_SWT_BOT_KEY,
				CustomWidgetIdConstants.TEAM_SHARE_IMPORT_PROJECTNAME);
		projectNameText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				projectName = projectNameText.getText();
				validatePageAndSetComplete();
			}
		});
		projectNameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	/**
	 * validates the input-fields are not empty and sets than the pageComlete to
	 * true.
	 */
	@Override
	protected void validatePageAndSetComplete() {
		super.validatePageAndSetComplete();
		boolean existsProjectWithName = testProjectService.existsProjectWithName(projectName);
		if (existsProjectWithName) {
			setErrorMessage(translationService.translate("%wizard.team.error.projectexists"));
		} else {
			setErrorMessage(null);
		}
		setPageComplete(isPageComplete() && !projectName.isEmpty() && !existsProjectWithName);
	}

	/**
	 * 
	 * @return the name of the project
	 */
	public String getProjectName() {
		return projectName;
	}

	/**
	 * 
	 * @param projectName
	 *            name of the project to be imported.
	 */
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

}
