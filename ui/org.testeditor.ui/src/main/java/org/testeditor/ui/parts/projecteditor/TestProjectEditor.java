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
package org.testeditor.ui.parts.projecteditor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MToolBarElement;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.testeditor.core.constants.TestEditorCoreEventConstants;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.model.action.ProjectLibraryConfig;
import org.testeditor.core.model.team.TeamShareConfig;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestProjectConfig;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.services.interfaces.FieldMappingExtension;
import org.testeditor.core.services.interfaces.TestExecutionEnvironmentService;
import org.testeditor.core.services.interfaces.TestProjectService;
import org.testeditor.core.services.plugins.LibraryConfigurationServicePlugIn;
import org.testeditor.core.services.plugins.TeamShareConfigurationServicePlugIn;
import org.testeditor.core.services.plugins.TestEditorPlugInService;
import org.testeditor.ui.ITestStructureEditor;
import org.testeditor.ui.constants.CustomWidgetIdConstants;
import org.testeditor.ui.constants.TestEditorEventConstants;
import org.testeditor.ui.constants.TestEditorUIEventConstants;
import org.testeditor.ui.parts.inputparts.TestEditorInputPartMouseAdapter;
import org.testeditor.ui.utilities.TestEditorTranslationService;

/**
 * 
 * EditorPart to edit the ProjectConfiguration.
 * 
 */
public class TestProjectEditor implements ITestStructureEditor {

	private static final Logger logger = Logger.getLogger(TestProjectEditor.class);

	public static final String ID = "org.testeditor.ui.partdescriptor.testProjectEditor";

	private static final String EDITOR_OBJECT_ID_FOR_RESTORE = "project_editor_object_id_for_restore";
	private MPart mpart;
	private TestProject testProject;
	private Text portText;
	private String errorMessage;

	@Inject
	private TestProjectService testProjectService;

	@Inject
	private TestEditorPlugInService plugInService;

	@Inject
	private TestEditorTranslationService translate;

	@Inject
	private IEventBroker eventBroker;

	@Inject
	private EPartService partService;

	@Inject
	private TestExecutionEnvironmentService testExecutionEnvironmentService;

	private TestProjectConfig newTestProjectConfig;

	private Combo libraryTypeCombo;

	private HashMap<String, String> libraryPlugInNameIdMap;

	private ScrolledComposite sc;
	private Composite subContainer;
	private Composite libraryDetailComposite;

	private boolean hasFocus = false;

	@Inject
	private TranslationService translationService;

	private HashMap<String, String> teamShareConfigPlugInNameIdMap;

	private Composite teamShareDetailComposite;

	private Text teamShareTypeLabel;

	private Combo testExecEnvCombo;

	private Map<String, String> availableTestEnvironmentConfigs;

	/**
	 * 
	 * @param part
	 *            of gui
	 */
	@Inject
	public TestProjectEditor(MPart part) {
		mpart = part;
	}

	/**
	 * 
	 * @param parent
	 *            composite
	 */
	@PostConstruct
	public void createControls(Composite parent) {
		parent.setLayout(new FillLayout());
		// Create the ScrolledComposite to scroll horizontally and vertically
		sc = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		// Expand both horizontally and vertically
		sc.setExpandHorizontal(true);
		sc.setExpandVertical(true);

		subContainer = new Composite(sc, SWT.NONE);
		sc.setContent(subContainer);

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		subContainer.setLayout(gridLayout);
		subContainer.setVisible(true);

		subContainer.setLayoutData(new GridData(GridData.FILL_BOTH));
		subContainer.setLayout(new GridLayout(1, true));
		createLibraryConfigGroup(subContainer);
		createServerConfigGroup(subContainer);
		createTeamShareConfigGroup(subContainer);
		sc.layout(true);

		String lastTestProjectName = mpart.getPersistedState().get(EDITOR_OBJECT_ID_FOR_RESTORE);
		if (lastTestProjectName != null) {
			TestProject project = testProjectService.getProjectWithName(lastTestProjectName);
			setTestProject(project);
		}

	}

	/**
	 * Creates the UI widgets for the Team Share Configuration.
	 * 
	 * @param content
	 *            to add the TemShareGroup Widgets.
	 */
	private void createTeamShareConfigGroup(Composite content) {
		final Group group = new Group(content, SWT.NORMAL);
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		group.setLayout(new GridLayout(2, false));
		group.setText(translate.translate("%testprojecteditor.sharinggroup"));

		new Label(group, SWT.NORMAL).setText(translate.translate("%testprojecteditor.teamShareType"));
		teamShareTypeLabel = new Text(group, SWT.NORMAL);
		teamShareTypeLabel.setEnabled(false);
		teamShareTypeLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		resetTeamShareDetailCompositeToBlank(group);
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
			createTeamShareField(field);
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
	private void createTeamShareField(final FieldMappingExtension fieldDeclaration) {
		new Label(teamShareDetailComposite, SWT.NORMAL)
				.setText(fieldDeclaration.getTranslatedLabel(translationService));
		final Text text = new Text(teamShareDetailComposite, SWT.NORMAL);
		text.setToolTipText(fieldDeclaration.getTranslatedToolTip(translationService));
		text.addMouseListener(new TestEditorInputPartMouseAdapter(eventBroker, testProject));

		text.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				if (newTestProjectConfig.getTeamShareConfig() != null) {
					fieldDeclaration.updatePlugInConfig(newTestProjectConfig.getTeamShareConfig(), text.getText());
					mpart.setDirty(true);
				}
			}
		});
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		if (fieldDeclaration.isPassword()) {
			text.setEchoChar('*');
		}
		if (fieldDeclaration.isReadOnly()) {
			text.setEditable(false);
		}
		if (testProject.getTestProjectConfig().getTeamShareConfig() != null) {
			String stringValue = fieldDeclaration
					.getStringValue(testProject.getTestProjectConfig().getTeamShareConfig());

			if (stringValue != null) {
				text.setText(stringValue);
			}
		}
	}

	/**
	 * Updates the port after restarting TestServer because new Port will be
	 * generated automatically.
	 * 
	 */
	public void updatePort() {
		portText.setText(testProject.getTestProjectConfig().getPort());

	}

	/**
	 * Updates the Scrollbars of the Composite. If the size of a widget changes,
	 * this method should be called to layout the composite and update the
	 * scrollbars.
	 */
	protected void updateScrollBars() {
		subContainer.layout(true);
		sc.setMinSize(subContainer.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}

	/**
	 * 
	 * Disposes the TeamShareDetail Composite and creates a new blank one.
	 * 
	 * @param cmp
	 *            parent Composite of the Detail Composite.
	 */
	private void resetTeamShareDetailCompositeToBlank(Composite cmp) {
		if (teamShareDetailComposite != null && !teamShareDetailComposite.isDisposed()) {
			teamShareDetailComposite.dispose();
		}
		teamShareDetailComposite = new Composite(cmp, SWT.NORMAL);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		teamShareDetailComposite.setLayoutData(gd);
		teamShareDetailComposite.setLayout(new GridLayout(2, false));
	}

	/**
	 * Creates UI Part to configure Testspecific Attributes.
	 * 
	 * @param content
	 *            parent
	 */
	private void createLibraryConfigGroup(Composite content) {
		Group testGroup = new Group(content, SWT.NORMAL);
		testGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		testGroup.setLayout(new GridLayout(2, false));
		testGroup.setText(translate.translate("%testprojecteditor.groupgeneraltest"));

		new Label(testGroup, SWT.NORMAL).setText(translate.translate("%testprojecteditor.libraryType"));
		libraryTypeCombo = new Combo(testGroup, SWT.NORMAL | SWT.READ_ONLY);
		fillLibraryTypeCombo();
		libraryTypeCombo.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				LibraryConfigurationServicePlugIn configurationService = plugInService
						.getLibraryConfigurationServiceFor(libraryPlugInNameIdMap.get(libraryTypeCombo.getText()));
				if (newTestProjectConfig != null && configurationService != null) {
					if (newTestProjectConfig.getProjectLibraryConfig() != null) {
						if (!configurationService.getId()
								.equals(newTestProjectConfig.getProjectLibraryConfig().getId())) {
							ProjectLibraryConfig libraryConfig = configurationService.createEmptyProjectLibraryConfig();
							newTestProjectConfig.setProjectLibraryConfig(libraryConfig);
							createLibraryTypeSpeceficComposite(libraryConfig);
						}
					}
				}
			}
		});

		libraryDetailComposite = new Composite(testGroup, SWT.NORMAL);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		libraryDetailComposite.setLayoutData(gd);
		libraryDetailComposite.setLayout(new GridLayout(2, false));
		updateScrollBars();
	}

	/**
	 * Fills the LibraryType Combobox with all registered LibraryServices. The
	 * Services names are sorted by name.
	 */
	protected void fillLibraryTypeCombo() {
		Collection<LibraryConfigurationServicePlugIn> allLibraryConfigurationService = plugInService
				.getAllLibraryConfigurationServices();
		libraryPlugInNameIdMap = new HashMap<String, String>();
		for (LibraryConfigurationServicePlugIn libraryConfigurationService : allLibraryConfigurationService) {
			libraryPlugInNameIdMap.put(
					libraryConfigurationService.getTranslatedHumanReadableLibraryPlugInName(translationService),
					libraryConfigurationService.getId());
		}
		List<String> list = new ArrayList<String>(libraryPlugInNameIdMap.keySet());
		Collections.sort(list);
		libraryTypeCombo.setItems(list.toArray(new String[] {}));
	}

	/**
	 * Creates UI Part to configure TestsServer Attributes.
	 * 
	 * @param content
	 *            parent
	 */
	private void createServerConfigGroup(Composite content) {
		Group serverGroup = new Group(content, SWT.NORMAL);
		serverGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		serverGroup.setText(translate.translate("%testprojecteditor.grouptestserver"));
		serverGroup.setLayout(new GridLayout(2, false));
		Label l = new Label(serverGroup, SWT.NORMAL);
		l.setText(translate.translate("%testprojecteditor.port"));
		l.setToolTipText(translate.translate("%testprojecteditor.port.MouseOver"));
		portText = new Text(serverGroup, SWT.NORMAL);
		portText.setEnabled(false);
		portText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		portText.setData(CustomWidgetIdConstants.TEST_EDITOR_WIDGET_ID_SWT_BOT_KEY,
				CustomWidgetIdConstants.TEST_PROJECT_CONFIGURATION_PORT);
		Label testExecSelection = new Label(serverGroup, SWT.NORMAL);
		testExecSelection.setText(translate.translate("%testexecenv.select.label"));
		testExecEnvCombo = new Combo(serverGroup, SWT.NORMAL);
		testExecEnvCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		testExecEnvCombo.setData(CustomWidgetIdConstants.TEST_EDITOR_WIDGET_ID_SWT_BOT_KEY,
				CustomWidgetIdConstants.TEST_PROJECT_TESTEXECUTION_ENVIRONMENT_SELECTION);
		testExecEnvCombo.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				mpart.setDirty(true);
				newTestProjectConfig.setTestEnvironmentConfiguration(
						availableTestEnvironmentConfigs.get(testExecEnvCombo.getText()));
			}
		});
	}

	/**
	 * Save the TestProjectConfig.
	 */
	@Persist
	public void save() {
		try {
			TestProject oldTestProject = getTestProject();
			testProjectService.storeProjectConfig(testProject, newTestProjectConfig);
			/*
			 * We have to set the port form oldProject to new project because we
			 * do not save the port in the configuration file
			 * <code>config.tpr</code>
			 */
			newTestProjectConfig.setPort(oldTestProject.getTestProjectConfig().getPort());
			testProject.setTestProjectConfig(newTestProjectConfig);

			mpart.setDirty(false);
		} catch (SystemException e) {
			logger.error("Error saving Config", e);
			errorMessage = e.getCause().getMessage();
			getDisplay().syncExec(new Runnable() {
				@Override
				public void run() {
					MessageDialog.openError(getDisplay().getActiveShell(), "Error", errorMessage);
					errorMessage = "";
				}
			});
		}

	}

	/**
	 * gets the display.
	 * 
	 * @return the display
	 */
	private static Display getDisplay() {
		Display display = Display.getCurrent();
		// may be null if outside the UI thread
		if (display == null) {
			display = Display.getDefault();
		}
		return display;
	}

	/**
	 * 
	 * @param testProject
	 *            to edit.
	 */
	public void setTestProject(final TestProject testProject) {
		this.testProject = testProject;
		newTestProjectConfig = new TestProjectConfig();
		if (testProject != null && testProject.getTestProjectConfig() != null) {
			mpart.setLabel(testProject.getName());
			portText.setText(testProject.getTestProjectConfig().getPort());
			createLibraryTypeSpeceficComposite(testProject.getTestProjectConfig().getProjectLibraryConfig());
			createTeamShareOptionSpecificCompositeWithValuesFrom(
					testProject.getTestProjectConfig().getTeamShareConfig());
			newTestProjectConfig.setConfiguration(testProject.getTestProjectConfig());
			mpart.getPersistedState().put(EDITOR_OBJECT_ID_FOR_RESTORE, testProject.getName());
			availableTestEnvironmentConfigs = testExecutionEnvironmentService
					.getAvailableTestEnvironmentConfigs(testProject);
			String[] items = availableTestEnvironmentConfigs.keySet().toArray(new String[] {});
			testExecEnvCombo.setItems(items);
			for (String cfgName : availableTestEnvironmentConfigs.keySet()) {
				String cfgProbant = availableTestEnvironmentConfigs.get(cfgName);
				if (cfgProbant.equals(testProject.getTestProjectConfig().getTestEnvironmentConfiguration())) {
					testExecEnvCombo.setText(cfgName);
				}
			}
		}
		mpart.setDirty(false);
		updateScrollBars();
	}

	/**
	 * Set this part undirty and hide it when this project is deleted.
	 * 
	 * @param deletedProjectName
	 *            TestStructure which is deleted.
	 */
	@Inject
	@Optional
	public void projectStructureDeleted(
			@UIEventTopic(TestEditorCoreEventConstants.TESTSTRUCTURE_MODEL_CHANGED_DELETED) String deletedProjectName) {
		if (deletedProjectName != null && deletedProjectName.equals(testProject.getName())) {
			mpart.setDirty(false);
			partService.hidePart(mpart);
		}
	}

	/**
	 * Creates the Team share Option Composites with the values from the shared
	 * project.
	 * 
	 * @param data
	 *            Fullname of the shared Project.
	 */
	@Inject
	@Optional
	public void projectTeamShared(@UIEventTopic(TestEditorUIEventConstants.PROJECT_TEAM_SHARED) String data) {
		if (data.equals(testProject.getFullName())) {
			createTeamShareOptionSpecificCompositeWithValuesFrom(
					testProject.getTestProjectConfig().getTeamShareConfig());
		}
	}

	/**
	 * Refresh testProject in view.
	 * 
	 * @param data
	 *            String
	 */
	@Inject
	@Optional
	protected void refresh(
			@UIEventTopic(TestEditorCoreEventConstants.TESTSTRUCTURE_MODEL_CHANGED_RELOADED) String data) {
		testProject = testProjectService.getProjectWithName(testProject.getName());
		if (testProject == null) {
			partService.hidePart(mpart, true);
		}
	}

	/**
	 * Refresh testProject in view.
	 * 
	 * @param data
	 *            String
	 */
	@Inject
	@Optional
	protected void refreshAfterRevert(@UIEventTopic(TestEditorUIEventConstants.TESTSTRUCTURE_REVERTED) String data) {
		if (data.equals(getTestProject().getFullName())) {
			try {
				testProjectService.reloadTestProjectFromFileSystem(getTestProject());
				setTestProject(getTestProject());
			} catch (SystemException e) {
				logger.error(e.getMessage(), e);
				MessageDialog.openError(getDisplay().getActiveShell(), "Error", e.getLocalizedMessage());
			}
		}
	}

	/**
	 * Creates UI Elements for the Team Share Option and selects it in the
	 * Combobox.
	 * 
	 * @param teamShareConfig
	 *            to be used to build the UI
	 */
	private void createTeamShareOptionSpecificCompositeWithValuesFrom(TeamShareConfig teamShareConfig) {
		if (!teamShareDetailComposite.isDisposed()) {
			resetTeamShareDetailCompositeToBlank(teamShareDetailComposite.getParent());
			if (teamShareConfig == null) {
				teamShareTypeLabel.setText("local");
			} else {
				TeamShareConfigurationServicePlugIn configurationService = plugInService
						.getTeamShareConfigurationServiceFor(teamShareConfig.getId());
				createTeamShareSpeceficDetailComposite(configurationService);
				teamShareTypeLabel
						.setText(configurationService.getTranslatedHumanReadablePlugInName(translationService));
			}
		}
	}

	/**
	 * Creates UI Controls for the projectLibraryConfig.
	 * 
	 * @param projectLibraryConfig
	 *            .
	 */
	protected void createLibraryTypeSpeceficComposite(ProjectLibraryConfig projectLibraryConfig) {
		if (testProject.getTestProjectConfig().getProjectLibraryConfig() != null) {
			Composite parent = libraryDetailComposite.getParent();
			libraryDetailComposite.dispose();
			libraryDetailComposite = new Composite(parent, SWT.NORMAL);
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.horizontalSpan = 2;
			libraryDetailComposite.setLayoutData(gd);
			libraryDetailComposite.setLayout(new GridLayout(2, false));
			updateLibraryTypeComboSelection(projectLibraryConfig.getId());
			LibraryConfigurationServicePlugIn libraryConfigurationService = plugInService
					.getLibraryConfigurationServiceFor(projectLibraryConfig.getId());
			List<FieldMappingExtension> fields = libraryConfigurationService.getConfigUIExtensions();
			addWorkspacepathWidgets();
			for (FieldMappingExtension field : fields) {
				createLibraryField(field);
			}
			parent.layout(true);
		}
	}

	/**
	 * adds the workspace-path-widgets.
	 */
	private void addWorkspacepathWidgets() {
		new Label(libraryDetailComposite, SWT.NORMAL).setText(translate.translate("%workspacepath"));
		final Text text = new Text(libraryDetailComposite, SWT.NORMAL);
		String workspacepath = Platform.getLocation().toFile().getAbsolutePath();
		text.setText(workspacepath);
		text.setEnabled(false);
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	/**
	 * Creates a Text Widget with a label based on the Informations of the
	 * <code>FieldDeclaration</code>.
	 * 
	 * @param fieldDeclaration
	 *            to create UI.
	 */
	protected void createLibraryField(final FieldMappingExtension fieldDeclaration) {
		new Label(libraryDetailComposite, SWT.NORMAL).setText(fieldDeclaration.getTranslatedLabel(translationService));
		final Text text = new Text(libraryDetailComposite, SWT.NORMAL);
		text.setToolTipText(fieldDeclaration.getTranslatedToolTip(translationService));
		text.addMouseListener(new TestEditorInputPartMouseAdapter(eventBroker, testProject));

		text.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				if (newTestProjectConfig.getProjectLibraryConfig() != null) {
					fieldDeclaration.updatePlugInConfig(newTestProjectConfig.getProjectLibraryConfig(), text.getText());
					mpart.setDirty(true);
				}
			}
		});
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		text.setText(fieldDeclaration.getStringValue(testProject.getTestProjectConfig().getProjectLibraryConfig()));
	}

	/**
	 * 
	 * @param libraryConfigID
	 *            to identify the value of the Combo box to be selected.
	 */
	protected void updateLibraryTypeComboSelection(String libraryConfigID) {
		for (String displayName : libraryPlugInNameIdMap.keySet()) {
			if (libraryPlugInNameIdMap.get(displayName).equals(libraryConfigID)) {
				libraryTypeCombo.setText(displayName);
			}
		}
	}

	/**
	 * 
	 * @param portText
	 *            for changing widget content.
	 */
	protected void setPortText(String portText) {
		this.portText.setText(portText);
	}

	/**
	 * 
	 * @return testProject used in this Editor.
	 */
	public TestProject getTestProject() {
		return testProject;
	}

	/**
	 * this method is called, when the ProjectEditor gets the focus.
	 * 
	 * @param shell
	 *            the active shell injected
	 */
	@Focus
	public void onFocus(@Named(IServiceConstants.ACTIVE_SHELL) Shell shell) {
		eventBroker.send(TestEditorUIEventConstants.ACTIVE_TESTFLOW_EDITOR_CHANGED, testProject);
		shell.setDefaultButton(null);
		if (!hasFocus) {
			hasFocus = true;
			eventBroker.send(TestEditorEventConstants.CACHE_TEST_COMPONENT_OF_PART_TEMPORARY, "");
			eventBroker.send(TestEditorEventConstants.GET_FOCUS_ON_INPUT_PART, null);
			mpart.setOnTop(true);
		}
	}

	/**
	 * 
	 * @return libraryTypeCombo for Tests
	 */
	protected Combo getLibraryTypeCombo() {
		return libraryTypeCombo;
	}

	/**
	 * This Method is for Testing.
	 * 
	 * @param name
	 *            of the PlugIn
	 * @return id of the PlugIn
	 */
	protected String getLibraryIDFor(String name) {
		return libraryPlugInNameIdMap.get(name);
	}

	/**
	 * sets the hasFocus-variable to false.
	 * 
	 * @param obj
	 *            Object
	 */
	@Inject
	@Optional
	public void focusLost(@UIEventTopic(TestEditorEventConstants.GET_FOCUS_ON_INPUT_PART) Object obj) {
		hasFocus = false;
	}

	/**
	 * This Method is for Testing.
	 * 
	 * @param name
	 *            of the PlugIn
	 * @return id of the PlugIn
	 */
	protected String getTeamShareIDFor(String name) {
		return teamShareConfigPlugInNameIdMap.get(name);
	}

	/**
	 * 
	 * @return the mpart.
	 */
	public MPart getPart() {
		return mpart;
	}

	@Override
	public TestStructure getTestStructure() {
		return getTestProject();
	}

	@Override
	public void closePart() {
		mpart.setDirty(false);
		partService.hidePart(mpart, true);
	}

	@Override
	public void setTestStructure(TestStructure testStructure) {
		if (testStructure instanceof TestProject) {
			setTestProject((TestProject) testStructure);
		}
	}

	/**
	 * Sets the Focus on the UI.
	 */
	public void setFocus() {
		if (libraryTypeCombo != null) {
			libraryTypeCombo.setFocus();
		}
		if (mpart.getToolbar() != null) {
			List<MToolBarElement> children = mpart.getToolbar().getChildren();
			for (MToolBarElement mToolBarElement : children) {
				Object widget = mToolBarElement.getWidget();
				if (widget != null && widget instanceof Widget) {
					((Widget) widget).setData(CustomWidgetIdConstants.TEST_EDITOR_WIDGET_ID_SWT_BOT_KEY,
							mToolBarElement.getElementId());
				}
			}
		}
	}

}
