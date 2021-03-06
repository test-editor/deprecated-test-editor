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

import java.util.Collection;

import javax.inject.Inject;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.services.interfaces.TeamShareStatusServiceNew;
import org.testeditor.core.services.interfaces.TestStructureService;
import org.testeditor.core.services.plugins.TeamShareConfigurationServicePlugIn;
import org.testeditor.ui.constants.IconConstants;
import org.testeditor.ui.utilities.TestEditorTranslationService;

/**
 * Wizardpage to show the local changes. The page supports two display ways:
 * 
 * Teststruture based: Testproject.TestSuite.Testcase Developerview with file
 * based information:
 * /paht_to_ws/project/some_dir/testsuite/testcase/content.txt
 *
 */
public class TeamShareShowChangesWizardPage extends TeamShareWizardPage {

	@Inject
	private TestEditorTranslationService translationService;

	@Inject
	private TeamShareStatusServiceNew teamShareStatusHandlerService;

	@Inject
	private TestStructureService teStructureService;

	private Collection<String> changedDatas;

	private TestStructure testStructure;

	private List changesList;

	private Button developerView;

	@Override
	String getTitleValue() {
		return translationService.translate("%popupmenu.label.showChanges");
	}

	@Override
	String getDescriptionValue() {
		return translationService.translate("%showChanges.element.wizard.msg", testStructure.getFullName());
	}

	@Override
	Image getIcon() {
		return IconConstants.ICON_SHOW_CHANGES;
	}

	/**
	 * creates the control.
	 * 
	 * @param parent
	 *            the parent Composite
	 */
	@Override
	public void createControl(Composite parent) {
		setChangedDatas(teamShareStatusHandlerService.getModified(testStructure.getRootElement()));
		super.createControl(parent);
		setPageComplete(true);
		refreshList();
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
	 * creates a special-group. can be used in the children of this class, to
	 * add some special widgets.
	 * 
	 * @param parent
	 *            the parent-composite of group.
	 */
	@Override
	protected void createSpecialGroup(Composite parent) {

		changesList = new List(parent, SWT.BORDER | SWT.SCROLLBAR_OVERLAY | SWT.V_SCROLL | SWT.H_SCROLL);
		GridData data = new GridData(GridData.FILL_BOTH);
		data.horizontalSpan = 2;
		changesList.setLayoutData(data);

		developerView = new Button(parent, SWT.CHECK);
		developerView.setLayoutData(new GridData(NONE, NONE, false, false, 1, 1));
		developerView.setText("Developer View");
		developerView.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				refreshList();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				refreshList();
			}
		});
	}

	/**
	 * @return the hintText
	 */
	@Override
	protected String getHintTextValue() {
		return translationService.translate("%wizard.teamShowChanges.msgText");
	}

	/**
	 * 
	 * @return the header of the hint-text.
	 */
	@Override
	protected String getHintTextHeaderValue() {
		return translationService.translate("%wizard.teamShare.msgHead");
	}

	/**
	 * 
	 * @param testStructure
	 *            TestStructure
	 */
	public void setTestStructure(TestStructure testStructure) {
		this.testStructure = testStructure;
	}

	/**
	 * @return the changedDatas
	 */
	public Collection<String> getChangedDatas() {
		return changedDatas;
	}

	/**
	 * 
	 */
	public void refreshList() {
		if (changesList != null) {
			changesList.removeAll();

			for (String data : changedDatas) {

				if (developerView.getSelection()) {
					changesList.add(data);
				} else {
					String name = teStructureService
							.lookUpTestStructureFullNameMatchedToPath(testStructure.getRootElement(), data);
					if (changesList.getItemCount() == 0
							|| !changesList.getItem(changesList.getItemCount() - 1).equals(name)) {
						changesList.add(name);
					}
				}
			}

		}
	}

	/**
	 * @param list
	 *            the changedDatas to set
	 */
	public void setChangedDatas(Collection<String> list) {
		this.changedDatas = list;
	}
}
