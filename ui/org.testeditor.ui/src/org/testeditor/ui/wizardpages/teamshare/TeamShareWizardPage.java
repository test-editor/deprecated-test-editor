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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.css.core.utils.StringUtils;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.testeditor.core.constants.TestEditorGlobalConstans;
import org.testeditor.core.model.team.TeamShareConfig;
import org.testeditor.core.model.teststructure.TestProjectConfig;
import org.testeditor.core.services.interfaces.FieldMappingExtension;
import org.testeditor.core.services.plugins.TeamShareConfigurationServicePlugIn;
import org.testeditor.core.services.plugins.TestEditorPlugInService;
import org.testeditor.ui.constants.CustomWidgetIdConstants;
import org.testeditor.ui.utilities.TestEditorTranslationService;

/**
 * Wizardpage for the TeamShareWizards.
 * 
 */
public abstract class TeamShareWizardPage extends WizardPage {

	@Inject
	private TestEditorTranslationService translate;
	@Inject
	private TestEditorPlugInService plugInService;

	@Inject
	private TranslationService translationService;

	private HashMap<String, String> teamShareConfigPlugInNameIdMap;

	private Composite teamShareDetailComposite;

	private Combo teamShareTypeCombo;
	private TestProjectConfig testProjectConfig;
	private Font hintFont;
	private Composite container;

	private List<Text> inputWidgets = new ArrayList<Text>();
	private Composite teamShareGroup;

	private Text svnCommentText;
	private String svnComment = System.getProperty(TestEditorGlobalConstans.SVN_COMMENT_DEFAULT);

	/**
	 * constructor.
	 */
	public TeamShareWizardPage() {
		super("");
	}

	/**
	 * constructor.
	 * 
	 * @param pageName
	 *            the name of the page
	 */
	protected TeamShareWizardPage(String pageName) {
		super(pageName);
	}

	/**
	 * creates the control.
	 * 
	 * @param parent
	 *            the parent Composite
	 */
	@Override
	public void createControl(Composite parent) {
		setTitle(getTitleValue());
		setDescription(getDescriptionValue());
		parent.getShell().setImage(getIcon());
		// Initialize the frame object of this wizard-page
		container = new Composite(parent, SWT.NORMAL);
		container.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		createHintGroup(getWidgetContainer());
		createSpecialGroup(getWidgetContainer());
		getWidgetContainer().setLayout(new GridLayout(2, false));
		createTeamShareConfigGroup(getWidgetContainer());
		if (hasComment()) {
			createSvnComment(getWidgetContainer());
		}
		// On default disable the 'finish' button
		setPageComplete(false);

		// Required to avoid an error in the system
		setControl(parent);

		validatePageAndSetComplete();
	}

	/**
	 * creates a special-group. can be used in the children of this class, to
	 * add some special widgets.
	 * 
	 * @param parent
	 *            the parent-composite of group.
	 */
	protected void createSpecialGroup(Composite parent) {

	}

	/**
	 * 
	 * @return the composite container including all widgets.
	 */
	protected Composite getWidgetContainer() {
		return container;
	}

	/**
	 * set the container.
	 * 
	 * @param container
	 *            Composite
	 */
	protected void setWidgetContainer(Composite container) {
		this.container = container;
	}

	/**
	 * creates the hint-group.
	 * 
	 * @param parent
	 *            the parent-composite of group.
	 */
	protected void createHintGroup(Composite parent) {
		// Show the hint text
		Group hintGroup = new Group(parent, SWT.NORMAL);
		FontData fontData = hintGroup.getFont().getFontData()[0];
		hintFont = new Font(Display.getCurrent(), fontData.getName(), fontData.getHeight(), SWT.BOLD);
		hintGroup.setFont(hintFont);
		hintGroup.setText(getHintTextHeaderValue());
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		hintGroup.setLayoutData(gd);
		hintGroup.setLayout(new FillLayout());
		Label hintText = new Label(hintGroup, SWT.NORMAL);
		hintText.setText(getHintTextValue());

	}

	/**
	 * Creates the UI widgets for the Team Share Configuration.
	 * 
	 * @param content
	 *            to add the TemShareGroup Widgets.
	 */
	protected void createTeamShareConfigGroup(Composite content) {
		// testProjectConfig = new TestProjectConfig();
		teamShareGroup = new Composite(content, SWT.NORMAL);
		teamShareGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		teamShareGroup.setLayout(new GridLayout(2, false));

		new Label(teamShareGroup, SWT.NORMAL).setText(translate.translate("%testprojecteditor.teamShareType"));
		teamShareTypeCombo = new Combo(teamShareGroup, SWT.NORMAL | SWT.READ_ONLY);
		fillTeamShareTypeCombo();
		teamShareTypeCombo.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				actionAfterTeamShareComboModified();
			}
		});
	}

	/**
	 * 
	 * @param configurationService
	 *            used to get the Fields to display in the detail Composite for
	 *            Team-Sharing-Configuration.
	 */
	protected void createTeamShareSpeceficDetailComposite(TeamShareConfigurationServicePlugIn configurationService) {
		List<FieldMappingExtension> fields = configurationService.getFieldMappingExtensions();
		for (FieldMappingExtension field : fields) {
			createTeamShareField(field, field.getIdConstant());
		}
		teamShareDetailComposite.getParent().getParent().layout(true);
		teamShareDetailComposite.getParent().layout(true);
	}

	/**
	 * 
	 * @param fieldDeclaration
	 *            used to create Label and Text for a field of the Team Share
	 *            Configuration.
	 */
	/**
	 * 
	 * @param fieldDeclaration
	 *            used to create Label and Text for a field of the Team Share
	 *            Configuration.
	 * @param idConstant
	 *            used to identify element ID for TestEditorTests.
	 */
	private void createTeamShareField(final FieldMappingExtension fieldDeclaration, String idConstant) {
		new Label(teamShareDetailComposite, SWT.NORMAL)
				.setText(fieldDeclaration.getTranslatedLabel(translationService));
		final Text text = new Text(teamShareDetailComposite, SWT.NORMAL);
		text.setData(CustomWidgetIdConstants.TEST_EDITOR_WIDGET_ID_SWT_BOT_KEY, idConstant);
		inputWidgets.add(text);
		text.setToolTipText(fieldDeclaration.getTranslatedToolTip(translationService));
		String oldValue = fieldDeclaration.getStringValue(getTestProjectConfig().getTeamShareConfig());
		if (!StringUtils.isEmpty(oldValue)) {
			text.setText(oldValue);
		}
		text.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				if (getTestProjectConfig().getTeamShareConfig() != null) {
					fieldDeclaration.updatePlugInConfig(getTestProjectConfig().getTeamShareConfig(), text.getText());
					validatePageAndSetComplete();
				}
			}

		});
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		if (fieldDeclaration.isPassword()) {
			text.setEchoChar('*');
		}
	}

	/**
	 * Fills the Combobox for selection the Team Share Option of the
	 * TestProject. This Method asks the Plug-In System for the available
	 * Implementations of a Team Share Service. A default Service is the local
	 * Project Service. This one is always added. The Team share Opetions are
	 * sorted by name.
	 */
	protected void fillTeamShareTypeCombo() {
		Collection<TeamShareConfigurationServicePlugIn> allTeamShareConfigurationServices = plugInService
				.getAllTeamShareConfigurationServices();
		teamShareConfigPlugInNameIdMap = new HashMap<String, String>();
		for (TeamShareConfigurationServicePlugIn teamShareConfigService : allTeamShareConfigurationServices) {
			teamShareConfigPlugInNameIdMap.put(
					teamShareConfigService.getTranslatedHumanReadablePlugInName(translationService),
					teamShareConfigService.getId());
		}
		List<String> list = new ArrayList<String>(teamShareConfigPlugInNameIdMap.keySet());
		Collections.sort(list);
		teamShareTypeCombo.setItems(list.toArray(new String[] {}));
		if (list.size() == 1) {
			teamShareTypeCombo.setText(list.get(0));
			actionAfterTeamShareComboModified();
		}
	}

	/**
	 * 
	 * Disposes the TeamShareDetail Composite and creates a new blank one.
	 * 
	 */
	private void resetTeamShareDetailCompositeToBlank() {
		if (teamShareDetailComposite != null && !teamShareDetailComposite.isDisposed()) {
			teamShareDetailComposite.dispose();
		}
		teamShareDetailComposite = new Composite(teamShareGroup, SWT.NORMAL);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		teamShareDetailComposite.setLayoutData(gd);
		teamShareDetailComposite.setLayout(new GridLayout(2, false));
		inputWidgets.clear();
	}

	/**
	 * validates the input-fields are not empty and sets than the pageComlete to
	 * true.
	 */
	protected void validatePageAndSetComplete() {

		for (Text text : inputWidgets) {

			if (text.getData(CustomWidgetIdConstants.TEST_EDITOR_WIDGET_ID_SWT_BOT_KEY).equals(
					"wizard.share.project.url")
					&& !text.getText().isEmpty()) {
				setPageComplete(true);
				return;
			}
		}

		setPageComplete(false);
	}

	/**
	 * 
	 * @return the title of the window
	 */
	abstract String getTitleValue();

	/**
	 * @return the description for the wizard
	 */
	abstract String getDescriptionValue();

	/**
	 * @return the hintText
	 */
	protected String getHintTextValue() {
		if (hasComment()) {
			return translate.translate("%wizard.teamShare.msgText", "");
		} else {
			return translate.translate("%wizard.teamShare.msgText",
					translate.translate("%wizard.teamShareProjectName.msgText"));
		}
	}

	/**
	 * 
	 * @return the header of the hint-text.
	 */
	protected String getHintTextHeaderValue() {
		return translate.translate("%wizard.teamShare.msgHead");
	}

	/**
	 * 
	 * @return the icon for the wizard
	 */
	abstract Image getIcon();

	/**
	 * dispose the swt.resources.
	 */
	@Override
	public void dispose() {
		if (hintFont != null) {
			hintFont.dispose();
		}
		container.dispose();
		super.dispose();
	}

	/**
	 * returns the configuration of the teamShare.
	 * 
	 * @return the configuration of the teamShare.
	 */
	public TeamShareConfig getTeamShareConfig() {
		return getTestProjectConfig().getTeamShareConfig();
	}

	/**
	 * Set a new teamShareConfig to allow reusage of the Wizard(Page).
	 * 
	 * @param teamShareConfig
	 *            a teatShareConfig to use in the Wizard
	 */
	public void setTeamShareConfig(TeamShareConfig teamShareConfig) {
		getTestProjectConfig().setTeamShareConfig(teamShareConfig);
	}

	/**
	 * modifies the wizard after the selection in the combobox is changed and
	 * adds the needed fields.
	 */
	private void actionAfterTeamShareComboModified() {
		resetTeamShareDetailCompositeToBlank();
		String scmId = teamShareConfigPlugInNameIdMap.get(teamShareTypeCombo.getText());
		TeamShareConfigurationServicePlugIn configurationService = plugInService
				.getTeamShareConfigurationServiceFor(scmId);
		if (getTestProjectConfig() != null) {
			// create new TeamShareConfig only, if we actually switch from one
			// to another scm type
			// otherwise we kill the reusage of the former user entries
			if (getTestProjectConfig().getTeamShareConfig() == null
					|| !getTestProjectConfig().getTeamShareConfig().getId().equals(scmId)) {
				if (configurationService == null) {
					getTestProjectConfig().setTeamShareConfig(null);
				} else {
					getTestProjectConfig().setTeamShareConfig(configurationService.createAnEmptyTeamShareConfig());
				}
			}
			createTeamShareSpeceficDetailComposite(configurationService);
		}
	}

	/**
	 * create SVn Comment field.
	 * 
	 * @param parent
	 *            Composite
	 */
	private void createSvnComment(Composite parent) {

		Composite innerComposite = new Composite(parent, SWT.FILL);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		innerComposite.setLayoutData(gd);
		innerComposite.setLayout(new GridLayout(2, false));

		Label label = new Label(innerComposite, SWT.NORMAL);
		label.setText(translate.translate("%wizard.label.svnComment"));
		svnCommentText = new Text(innerComposite, SWT.NORMAL);
		svnCommentText.append(svnComment);
		svnCommentText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				svnComment = svnCommentText.getText();
			}
		});
		svnCommentText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

	}

	/**
	 * 
	 * @return the TestProjectConfig. May overwritten by child-class.
	 */
	public TestProjectConfig getTestProjectConfig() {
		return testProjectConfig;
	}

	/**
	 * 
	 * @param testProjectConfig
	 *            used as model in this wizard page.
	 */
	public void setTestProjectConfig(TestProjectConfig testProjectConfig) {
		this.testProjectConfig = testProjectConfig;
	}

	/**
	 * @return the svnComment
	 */
	public String getSvnComment() {
		return svnComment;
	}

	/**
	 * 
	 * @return boolean true if the state has a SVN Comment.
	 */
	protected boolean hasComment() {
		return false;
	}
}
