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
package org.testeditor.ui.parts.editor.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.testeditor.core.constants.TestEditorCoreEventConstants;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.exceptions.TestCycleDetectException;
import org.testeditor.core.model.action.Argument;
import org.testeditor.core.model.action.ProjectActionGroups;
import org.testeditor.core.model.action.TextType;
import org.testeditor.core.model.teststructure.LibraryLoadingStatus;
import org.testeditor.core.model.teststructure.TestActionGroup;
import org.testeditor.core.model.teststructure.TestComponent;
import org.testeditor.core.model.teststructure.TestDescription;
import org.testeditor.core.model.teststructure.TestFlow;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestScenario;
import org.testeditor.core.model.teststructure.TestScenarioParameterTable;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.services.interfaces.ActionGroupService;
import org.testeditor.core.services.interfaces.LibraryConstructionException;
import org.testeditor.core.services.interfaces.LibraryReaderService;
import org.testeditor.core.services.interfaces.TeamShareStatusService;
import org.testeditor.core.services.interfaces.TestProjectService;
import org.testeditor.core.services.interfaces.TestScenarioService;
import org.testeditor.core.services.interfaces.TestStructureContentService;
import org.testeditor.metadata.core.MetaDataService;
import org.testeditor.ui.ITestStructureEditor;
import org.testeditor.ui.constants.ColorConstants;
import org.testeditor.ui.constants.TestEditorEventConstants;
import org.testeditor.ui.constants.TestEditorUIEventConstants;
import org.testeditor.ui.parts.editor.ITestEditorController;
import org.testeditor.ui.parts.editor.ITestEditorTabContollerProvider;
import org.testeditor.ui.parts.editor.ITestEditorTabController;
import org.testeditor.ui.parts.editor.view.handler.TestEditorInputObject;
import org.testeditor.ui.parts.inputparts.actioninput.TestEditorActionInputController;
import org.testeditor.ui.parts.inputparts.descriptioninput.TestEditorDescriptionInputController;
import org.testeditor.ui.parts.inputparts.scenarioselection.TestEditorScenarioSelectionController;
import org.testeditor.ui.utilities.TestEditorTranslationService;

/**
 * 
 * TestEditor controls view and model for test description and test table, the
 * right hand side of the ui.
 * 
 */
public abstract class TestEditorController implements ITestEditorController, ITestStructureEditor {

	private static final String EDITOR_OBJECT_ID_FOR_RESTORE = "editor_object_id_for_restore";

	public static final String ID = "org.testeditor.ui.partdescriptor.testStructureView";
	public static final String TESTCASE_ID = "org.testeditor.ui.partdescriptor.testCaseView";
	public static final String TESTSUITE_ID = "org.testeditor.ui.partdescriptor.testSuiteEditor";
	public static final String TESTSCENARIO_ID = "org.testeditor.ui.partdescriptor.testScenarioView";

	@Inject
	private TeamShareStatusService teamShareStatusService;

	@Inject
	private IEclipseContext context;
	@Inject
	private EPartService partService;
	@Inject
	private IEventBroker eventBroker;
	@Inject
	private static TestEditorTranslationService translationService;
	@Inject
	private ActionGroupService actionGroupService;
	@Inject
	private TestScenarioService testScenarioService;
	@Inject
	@Optional
	private MetaDataService metaDataService;

	@Inject
	private TestStructureContentService testStructureContentService;

	@Optional
	@Inject
	private ITestEditorTabContollerProvider iTestEditorTabControllerProvider;

	private ITestEditorTabController iTestEditorTabController;

	private CTabItem metaDataTab;

	private TestEditorActionInputController actionInputController;

	private TestEditorDescriptionInputController descriptionController;

	private TestEditorScenarioSelectionController scenarioController;

	private TestEditorActionInputController testEditorInputControllerBackup;

	private TestEditorDescriptionInputController descriptionControllerBackup;

	private MPart mpart;

	private Composite compositeForView = null;

	private TestFlow testFlow;

	private TestEditView testEditViewArea;

	private static final Logger LOGGER = Logger.getLogger(TestEditorController.class);

	private Map<String, TestEditorInputObject> cachedTestComponentInputMap = new HashMap<String, TestEditorInputObject>();

	private boolean hasFocus = false;

	private UnEditableLinesStore unEditableLinesStore = new UnEditableLinesStore();

	private Composite messsageArea;

	/**
	 * 
	 * @param part
	 *            of gui
	 */
	@Inject
	public TestEditorController(MPart part) {
		mpart = part;
	}

	/**
	 * 
	 * @return the part.
	 */
	@Override
	public MPart getPart() {
		return mpart;
	}

	/**
	 * saves the changed TestFlow.
	 */
	@Override
	@Persist
	public void save() {
		try {
			if (getTestEditorTabController() != null) {
				getTestEditorTabController().save();
			}
			testStructureContentService.saveTestStructureData(testFlow);
			mpart.setDirty(false);
		} catch (final SystemException e) {
			final String message = translationService.translate("%editController.ErrorStoringTestFlow");
			Display.getDefault().syncExec(new Runnable() {

				@Override
				public void run() {
					MessageDialog.openError(Display.getDefault().getActiveShell(), message, e.getLocalizedMessage());
				}
			});
			LOGGER.error("Error storing Testcase :: FAILED", e);
		}
		updateTeamStateInformation();
	}

	/**
	 * if the current structure is child of a team shared project, it updates
	 * the Team modification information of the object in the test project.
	 */
	private void updateTeamStateInformation() {
		TestProject testProject = getTestStructure().getRootElement();
		if (testProject.getTestProjectConfig().isTeamSharedProject()) {
			teamShareStatusService.setTeamStatusForProject(testProject);
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
	public void reloadAndRefreshByRevert(@UIEventTopic(TestEditorUIEventConstants.TESTSTRUCTURE_REVERTED) String data) {
		if (data.equals(getTestStructure().getFullName())) {
			loadAndRerender();
		}
	}

	/**
	 * will be called by the eventBroker, if a TEST_DATA_RELOAD-event is sent.
	 * 
	 * @param data
	 *            Object
	 */
	@Inject
	@Optional
	public void reloadAndRefresh(
			@UIEventTopic(TestEditorCoreEventConstants.TESTSTRUCTURE_MODEL_CHANGED_RELOADED) String data) {
		if (mpart.isDirty()) {
			if (userWantsToReplaceContent()) {
				loadAndRerender();
			}
		}
	}

	/**
	 * Ask user about replacing the unsaved content of the editor.
	 * 
	 * @return true if the user accepts the replacement.
	 */
	protected boolean userWantsToReplaceContent() {
		return MessageDialog.openConfirm(Display.getCurrent().getActiveShell(),
				translationService.translate("%testeditor.dirty.replacetitle"),
				translationService.translate("%testeditor.dirty.replacequestion"));
	}

	/**
	 * reload the TestStructure from the content.txt and refresh the StyleText.
	 */
	protected void loadAndRerender() {
		if (getTestStructure() != null) {
			TestFlow foundTestStructure = findTestStructureByFullName(getTestStructure().getFullName());
			if (getMetaDataService() != null) {
				getMetaDataService().refresh(foundTestStructure.getRootElement());
			}
			if (foundTestStructure == null) {
				partService.hidePart(mpart, true);
			}
			setTestStructure(foundTestStructure);
		}
	}

	/**
	 * Refreshs the view after the REFRESH_TESTFLOW_VIEWS_TO_PROJECT is catched,
	 * if the project equal to the project of the testflow.
	 * 
	 * @param testProject
	 *            TestProject
	 */
	@Inject
	@Optional
	public void refreshView(
			@UIEventTopic(TestEditorEventConstants.REFRESH_TESTFLOW_VIEWS_TO_PROJECT) TestProject testProject) {
		if (getTestFlow() != null && getTestFlow().getRootElement().equals(testProject)) {
			try {
				testStructureContentService.reparseChangedTestFlow(getTestFlow());
			} catch (SystemException e) {
				LOGGER.error("reparse Testcase:: FAILED", e);
			}
			getTestEditViewArea().removeStyledTextModifyListener();
			refreshStyledText();
		}
	}

	/**
	 * close the part, if the testflow is equal to the testStructure given by
	 * the parameter or if the teststructure is the root-TestStructure of the
	 * TestFlwo.
	 * 
	 * @param deletedTestStructureName
	 *            of deleted TestStructure
	 */
	@Inject
	@Optional
	public void closeChildOfDeletedTestStructure(
			@UIEventTopic(TestEditorCoreEventConstants.TESTSTRUCTURE_MODEL_CHANGED_DELETED) String deletedTestStructureName) {
		if (deletedTestStructureName != null && getTestStructure().getFullName().startsWith(deletedTestStructureName)) {
			closePart();
		}
	}

	/**
	 * if a scenario is deleted, than the reference in the testflow should be
	 * shown as unresolved.
	 * 
	 * @param deletedTestStructureName
	 *            of deleted TestStructure
	 */
	@Inject
	@Optional
	public void checkReferedScenarioIsDeleted(
			@UIEventTopic(TestEditorCoreEventConstants.TESTSTRUCTURE_MODEL_CHANGED_DELETED) String deletedTestStructureName) {
		TestStructure deletedTestStructure = testFlow.getRootElement().getTestChildByFullName(deletedTestStructureName);
		if (deletedTestStructureName != null && deletedTestStructure instanceof TestScenario) {
			for (int i = 0; i < getTestFlowSize(); i++) {
				TestComponent testComponentAt = getTestComponentAt(i);
				if (testComponentAt instanceof TestScenarioParameterTable
						&& ((TestScenarioParameterTable) testComponentAt).getInclude().endsWith(
								((TestScenario) deletedTestStructure).getFullName())) {
					((TestScenarioParameterTable) testComponentAt).setScenarioOfProject(false);
					getTestEditViewArea().removeStyledTextModifyListener();
					refreshStyledText();
					return;
				}
			}
		}
	}

	/**
	 * 
	 * @param parent
	 *            composite
	 */
	@PostConstruct
	public void createControls(Composite parent) {

		parent.setLayout(new GridLayout(1, false));
		GridLayout gridLayout = new GridLayout(1, false);

		compositeForView = new CTabFolder(parent, SWT.BORDER);
		CTabItem item = new CTabItem((CTabFolder) compositeForView, SWT.NONE);
		item.setText("Editor");

		compositeForView.setLayout(gridLayout);
		GridData gridDataInner = new GridData(GridData.FILL_BOTH);
		gridDataInner.grabExcessVerticalSpace = true;
		gridDataInner.grabExcessHorizontalSpace = true;
		compositeForView.setLayoutData(gridDataInner);

		messsageArea = new Composite(compositeForView, SWT.BORDER);
		messsageArea.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));
		messsageArea.setLayout(new FillLayout());
		createTestCaseView();
		item.setControl(testEditViewArea.getStyledText());

		LOGGER.trace("Check if this editor should restore an older state.");
		String testStructureFullName = mpart.getPersistedState().get(EDITOR_OBJECT_ID_FOR_RESTORE);

		metaDataTab = null;
		if (iTestEditorTabControllerProvider != null) {
			iTestEditorTabController = iTestEditorTabControllerProvider.get();
			metaDataTab = new CTabItem((CTabFolder) compositeForView, SWT.NONE);
			metaDataTab.setText(translationService.translate("%testeditor.tab.metadata.label"));
			Composite composite = getTestEditorTabController().createTab((CTabFolder) compositeForView, mpart,
					translationService);
			metaDataTab.setControl(composite);
		}
		((CTabFolder) compositeForView).setSelection(0);

		if (testStructureFullName != null) {
			LOGGER.trace("Restoring Editor state for: " + testStructureFullName);
			TestFlow structure = findTestStructureByFullName(testStructureFullName);
			if (structure != null) {
				setTestFlow(structure);
			}

		}

	}

	/**
	 * 
	 * @param testStructureFullName
	 *            Full Name of the TestStructure builded with:
	 *            ProjectName.TestSuiteName.TestStructureName
	 * @return TestFlow found by that name.
	 * 
	 */
	protected TestFlow findTestStructureByFullName(String testStructureFullName) {
		TestProjectService projectService = context.get(TestProjectService.class);
		try {
			return (TestFlow) projectService.findTestStructureByFullName(testStructureFullName);
		} catch (SystemException e) {
			LOGGER.error("Error on reading projects", e);
		}
		return null;
	}

	/**
	 * With setting the TestFlow, the parts for description, ScenarioView and
	 * adding/changing TestFlow will be set as well.
	 * 
	 * @param testFlow
	 *            whole test
	 */
	@Override
	public void setTestFlow(TestFlow testFlow) {
		initializeControllerForToolboxes();

		this.testFlow = testFlow;

		if (getTestEditorTabController() != null && metaDataTab != null) {
			getTestEditorTabController().setTestFlow(testFlow);
			if (!getTestEditorTabController().isVisible()) {
				metaDataTab.dispose();
			}
		}
		mpart.getPersistedState().put(EDITOR_OBJECT_ID_FOR_RESTORE, testFlow.getFullName());
		afterSetTestFlow();
		eventBroker.send(TestEditorUIEventConstants.ACTIVE_TESTFLOW_EDITOR_CHANGED, testFlow);

		wireUpToolBoxControllerWithEditorController();

	}

	/**
	 * Wires up the Controller of the Toolbox parts with this Controller.
	 */
	protected void wireUpToolBoxControllerWithEditorController() {
		this.actionInputController.setTestCaseController(this);
		this.descriptionController.setTestCaseController(this);
		this.scenarioController.setTestCaseController(this);
	}

	/**
	 * Initialize controller and activates parts.
	 */
	protected void initializeControllerForToolboxes() {
		TestEditorActionInputController testEditorActionInputController = null;
		TestEditorDescriptionInputController testEditorDescriptionInputController = null;
		TestEditorScenarioSelectionController testEditorScenarioSelectionController = null;
		Collection<MPart> allParts = partService.getParts();

		for (MPart part : allParts) {
			if (part.getElementId().equals(TestEditorActionInputController.ID)) {
				partService.activate(part);
				testEditorActionInputController = (TestEditorActionInputController) part.getObject();
			} else if (part.getElementId().equals(TestEditorDescriptionInputController.ID)) {
				partService.activate(part);
				testEditorDescriptionInputController = (TestEditorDescriptionInputController) part.getObject();
			} else if (part.getElementId().equals(TestEditorScenarioSelectionController.ID)) {
				partService.activate(part);
				testEditorScenarioSelectionController = (TestEditorScenarioSelectionController) part.getObject();
			}
		}

		this.setTestEditorActionInputController(testEditorActionInputController);
		this.setTestEditorDescriptionInputController(testEditorDescriptionInputController);
		this.setTestEditorScenarioSelectionController(testEditorScenarioSelectionController);
	}

	/**
	 * operations, after the testflow is set.
	 */
	protected void afterSetTestFlow() {
		LibraryLoadingStatus libraryStatus = testFlow.getRootElement().getTestProjectConfig().getLibraryLoadingStatus();
		if (!libraryStatus.isLoaded()) {
			readingLibrary(testFlow);
		}
		if (libraryStatus.isLoaded() && libraryStatus.isErrorLessLoaded()) {
			refreshTestComponents(testFlow);
		}
		if (testEditViewArea != null) {
			testEditViewArea.removeStyledTextModifyListener();

			refreshStyledText();
			setSelectedLineInView(0);
			mpart.setLabel(testFlow.getName());
			mpart.setDirty(false);
		}
	}

	/**
	 * handles the TEST_GET_FOCUS_IN_TABLE-Event.
	 * 
	 * @param testEditorParameterTableFocusEventObject
	 *            TestEditorParameterTableFocusEventObject that get the focus
	 */
	@Inject
	@Optional
	public void getFocusInTable(
			@UIEventTopic(TestEditorEventConstants.TEST_GET_FOCUS_IN_TABLE) TestEditorParameterTableFocusEventObject testEditorParameterTableFocusEventObject) {
		if (testEditorParameterTableFocusEventObject.getTestFlow().equals(testFlow)) {
			testEditViewArea.setFocusInSubComponent(testEditorParameterTableFocusEventObject.getTableViewer());
		}
	}

	/**
	 * handles the store CACHE_TEST_COMPONENT_TEMPORARY-Event.
	 * 
	 * @param testEditorInputObject
	 *            input object to store
	 */
	@Inject
	@Optional
	public void cacheTestComponent(
			@UIEventTopic(TestEditorEventConstants.CACHE_TEST_COMPONENT_TEMPORARY) TestEditorInputObject testEditorInputObject) {
		if (testEditorInputObject.getTestflow().equals(getTestFlow())) {
			this.setLastUnSavedTestComponentInput(testEditorInputObject);
		}
	}

	/**
	 * set dirty with a event.
	 * 
	 * @param testFlow
	 *            optional parameter
	 */
	@Inject
	@Optional
	public void setDirtyByEvent(
			@UIEventTopic(TestEditorUIEventConstants.TEST_FLOW_STATE_CHANGED_TO_DIRTY) TestFlow testFlow) {
		if (this.testFlow.equals(testFlow)) {
			setDirty();
		}
	}

	/**
	 * will be called by the eventBroker, after a TEST_STRUCTURE_UPDATED is send
	 * by updating a teststructure with the teamsharing-operation.
	 * 
	 * @param testStructureFullName
	 *            update TestStructure
	 */
	@Inject
	@Optional
	public void objectUpdatetByTeamshare(
			@UIEventTopic(TestEditorCoreEventConstants.TESTSTRUCTURE_MODEL_CHANGED_UPDATE) String testStructureFullName) {
		if (testFlow != null) {
			if (testStructureFullName.equals(testFlow.getFullName())) {
				setTestStructure(testFlow);
			}
		}
	}

	/**
	 * handle the event library is reloaded for the TestProject.
	 * 
	 * @param testProject
	 *            the TestProject
	 */
	@Inject
	@Optional
	public void handleLibraryLoadedEvent(
			@UIEventTopic(TestEditorUIEventConstants.LIBRARY_LOADED_FOR_PROJECT) TestProject testProject) {
		setTestFlow(testFlow);
	}

	/**
	 * reading the library.
	 * 
	 * @param testStructure
	 *            the {@link TestFlow}
	 */
	private void readingLibrary(TestFlow testStructure) {
		LibraryReaderService libraryReaderService = context.get(LibraryReaderService.class);
		ProjectActionGroups projectActionGroups;
		try {
			projectActionGroups = libraryReaderService.readBasisLibrary(testStructure.getRootElement()
					.getTestProjectConfig().getProjectLibraryConfig());
			projectActionGroups.setProjectName(testStructure.getRootElement().getName());
			LOGGER.debug("ProjectGroups read for: " + projectActionGroups.getProjectName());
			actionGroupService.addProjectActionGroups(projectActionGroups);
		} catch (LibraryConstructionException e) {
			String mappingErrorPartOne = translationService.translate("%editController.ErrorObjectMappingPartOne");
			String mappingErrorPartTow = translationService.translate("%editController.ErrorObjectMappingPartTow");
			MessageDialog.openError(
					Display.getCurrent().getActiveShell(),
					translationService.translate("%editController.LibraryNotLoaded"),
					translationService.translate("%editController.ErrorReadingLibrary") + mappingErrorPartOne + " "
							+ e.getMessage() + " " + mappingErrorPartTow);
			LOGGER.error("Error reading library :: FAILED" + mappingErrorPartOne + " " + e.getMessage() + " "
					+ mappingErrorPartTow, e);
		} catch (SystemException e) {
			MessageDialog.openError(
					Display.getCurrent().getActiveShell(),
					translationService.translate("%editController.LibraryNotLoaded"),
					translationService.translate("%editController.ErrorReadingLibrary") + ": "
							+ e.getLocalizedMessage());
			LOGGER.error("Error reading library :: FAILED", e);
		}
	}

	/**
	 * @return whole test
	 */
	@Override
	public TestFlow getTestFlow() {
		return testFlow;
	}

	/** 
         * 
         */
	@PreDestroy
	protected void partDestroyed() {
		compositeForView.dispose();
	}

	@Override
	public void setDirty() {
		if (mpart != null) {
			mpart.setDirty(true);
		}
	}

	/**
	 * this method creates the TestCaseView.
	 */
	protected void createTestCaseView() {
		testEditViewArea = ContextInjectionFactory.make(TestEditView.class, context);
		testEditViewArea.setTestCaseController(this);
		testEditViewArea.createUI(compositeForView);
	}

	@Override
	public int getTestFlowSize() {
		return testFlow.getSize();
	}

	@Override
	public List<String> getLine(int line) {
		TestComponent component = testFlow.getLine(line);
		return component.getTexts();
	}

	@Override
	public List<TextType> getTextTypes(int i) {
		TestComponent component = testFlow.getLine(i);
		return component.getTextTypes();
	}

	@Override
	public TestComponent removeLine(int selectedLine) {
		return testFlow.removeLine(selectedLine);
	}

	@Override
	public void addLine(int position, TestComponent next) {
		if (testFlow.getSize() < position) {
			position = testFlow.getSize();
		}
		testFlow.addTestComponent(position, next);
	}

	@Override
	public void setDescription(int selectedLine, List<String> newLines, boolean changeMode) {

		int position = selectedLine;

		List<TestDescription> descriptions = createDescriptionsArray(newLines);

		// TODO this code will be used only in test
		if (changeMode && !testFlow.getTestComponents().isEmpty()) {
			testFlow.setLine(position, descriptions.get(0));
			position++;
			descriptions.remove(0);
		}

		if (!descriptions.isEmpty()) {

			// starting new testflow
			if (testFlow.getTestComponents().isEmpty() || position == -1) {
				position = testFlow.getSize();
			}

			// description will never insert in row position 0 in case of
			// scenario
			if (position == 0 && !testFlow.getTestComponents().isEmpty() && testFlow instanceof TestScenario) {
				testFlow.getTestComponents().addAll(position + 1, descriptions);
			} else {
				testFlow.getTestComponents().addAll(position, descriptions);
			}

		}
	}

	/**
	 * creates the descriptionArray.
	 * 
	 * @param newLines
	 *            List<String>
	 * @return List<TestDescription>
	 */
	protected abstract List<TestDescription> createDescriptionsArray(List<String> newLines);

	@Override
	public TestComponent getTestComponentAt(int i) {
		return testFlow.getLine(i);
	}

	@Override
	public void setActionToEditArea(int lineNumber, List<String> texts, int cursorPosInLine) {
		cleanupAndCloseDescriptionInputArea();
		TestActionGroup testActionGr = (TestActionGroup) getTestComponentAt(lineNumber);
		actionInputController.setActionToEditArea(lineNumber, texts, testActionGr, cursorPosInLine);
	}

	@Override
	public abstract void setActionGroup(String mask, String actionName, ArrayList<String> inputLineParts,
			ArrayList<Argument> arguments, int selectedLine, boolean changeMode);

	/**
	 * sets the ActionGroup in the TestFlow.
	 * 
	 * @param mask
	 *            String
	 * @param changeMode
	 *            changeMode
	 * @param position
	 *            int
	 * @param testComponent
	 *            TestActionGroup
	 */
	protected void setActionGroup(String mask, boolean changeMode, int position, TestActionGroup testComponent) {
		actionInputController.setLastSelectedMask(mask);
		if (changeMode) {
			testFlow.setLine(position, testComponent);
		} else {
			if (testFlow.getTestComponents().isEmpty() || position == -1) {
				position = testFlow.getSize();
			}
			addLine(position, testComponent);
		}
		cleanupAndCloseActionInputArea();
	}

	/**
	 * set the controller for the action editarea.
	 * 
	 * @param testInputController
	 *            controller for the action editarea
	 */
	public void setTestEditorActionInputController(TestEditorActionInputController testInputController) {
		actionInputController = testInputController;
		testEditorInputControllerBackup = testInputController;
		actionInputController.setAddMode(true);
		actionInputController.cleanLastMaskValue();
		actionInputController.setActionActive();
	}

	/**
	 * set the controller for the description editarea.
	 * 
	 * @param testEditorDescriptionInputController
	 *            controller for the description editarea
	 */
	public void setTestEditorDescriptionInputController(
			TestEditorDescriptionInputController testEditorDescriptionInputController) {
		descriptionController = testEditorDescriptionInputController;
		descriptionControllerBackup = testEditorDescriptionInputController;
		descriptionController.setAddMode(true);
		setDescriptionActive(0, 0);
	}

	@Override
	public void refreshStyledText() {
		if (testEditViewArea != null) {
			testEditViewArea.refreshStyledText();
		}
	}

	@Override
	public void putTextToInputArea(String selText, int selectedLine, int releasedLine, int cursorPosInLine) {
		cleanupAndCloseActionInputArea();
		descriptionController.putTextToInputArea(selText, selectedLine, releasedLine, cursorPosInLine);
	}

	/**
	 * This method is called from the framework when this part gets the focus.
	 * It selects the TestFlow of the editor in the TestExplorer.
	 * 
	 * @param shell
	 *            the active shell injected
	 */
	@Override
	@Focus
	public void setFocus(@Named(IServiceConstants.ACTIVE_SHELL) Shell shell) {
		// TODO why is this needed.
		shell.setDefaultButton(null);
		eventBroker.send(TestEditorUIEventConstants.ACTIVE_TESTFLOW_EDITOR_CHANGED, testFlow);
		if (!hasFocus) {
			hasFocus = true;
			connectActionInputController();
			connectDescriptionController();
			connectScenarioController();
			eventBroker.send(TestEditorEventConstants.CACHE_TEST_COMPONENT_OF_PART_TEMPORARY, "");
			eventBroker.send(TestEditorEventConstants.GET_FOCUS_ON_INPUT_PART, testFlow);
			eventBroker.post(TestEditorEventConstants.REFRESH_FILTER_FOR_SCENARIOS_IN_TREE, "");
		}
	}

	/**
	 * connect the scenarioController and sets the old input.
	 */
	protected void connectScenarioController() {
		if (scenarioController != null) {
			scenarioController.setTestCaseController(this);
			TestEditorInputObject inputObjectWithScenario = cachedTestComponentInputMap
					.get(TestScenarioParameterTable.class.getName());
			if (inputObjectWithScenario != null
					&& !((TestScenarioParameterTable) inputObjectWithScenario.getTestcomponent()).getTitle()
							.equalsIgnoreCase("")) {
				try {
					setScenarioToEditArea((TestScenarioParameterTable) inputObjectWithScenario.getTestcomponent(),
							inputObjectWithScenario.getLineNumber());
				} catch (SystemException e) {
					MessageDialog.openError(Display.getCurrent().getActiveShell(), "System-Exception", e.getMessage());

					LOGGER.error("error by getting the scenario", e);
				}
				scenarioController.setAddMode(inputObjectWithScenario.isAddMode());
			}
		}
	}

	/**
	 * connect the descriptionController and sets the old input.
	 */
	protected void connectDescriptionController() {
		if (descriptionController != null) {
			descriptionController.setTestCaseController(this);
			TestEditorInputObject inputObjectWithDescription = cachedTestComponentInputMap.get(TestDescription.class
					.getName());
			if (inputObjectWithDescription != null
					&& inputObjectWithDescription.getTestcomponent().getTexts().get(0).length() > 0) {
				descriptionController.setDescriptonTextToChangeable(inputObjectWithDescription.getTestcomponent()
						.getTexts().get(0), inputObjectWithDescription.getLineNumber(),
						inputObjectWithDescription.getLineNumber(), inputObjectWithDescription.getCursorPosInLine());
			}
		}
	}

	/**
	 * connect the actionController and sets the old input.
	 */
	protected void connectActionInputController() {
		if (actionInputController != null) {
			actionInputController.setTestCaseController(this);
			TestEditorInputObject inputObjectWithAction = cachedTestComponentInputMap.get(TestActionGroup.class
					.getName());
			if (inputObjectWithAction != null && inputObjectWithAction.getTestcomponent().getTexts().size() > 0
					&& !inputObjectWithAction.getTestcomponent().getTexts().get(0).equalsIgnoreCase("|")) {
				actionInputController.setActionToEditArea(inputObjectWithAction.getLineNumber(),
						((TestActionGroup) inputObjectWithAction.getTestcomponent()).getTexts(),
						(TestActionGroup) inputObjectWithAction.getTestcomponent(),
						inputObjectWithAction.getCursorPosInLine());
			}
		}
	}

	/**
	 * Puts the part on the top, if the testflow is equal to the member-variable
	 * testflow, else set the part to setOnTop(false).
	 * 
	 * @param testflow
	 *            TestFlow
	 */
	@Inject
	@Optional
	public void setPartOnTop(@UIEventTopic(TestEditorEventConstants.GET_FOCUS_ON_INPUT_PART) TestFlow testflow) {
		if (testflow == null || !testflow.equals(getTestFlow())) {
			lostFocus();
			mpart.setOnTop(false);
		} else {
			mpart.setOnTop(true);
		}
	}

	@Override
	public ArrayList<TestComponent> removeSelectedLinesAndCleanUp() {
		int klickedLine = testEditViewArea.getSelectionStartInTestCase();
		int releasedLine = testEditViewArea.getSelectionEndInTestCase();

		ArrayList<TestComponent> removedComponents = new ArrayList<TestComponent>();
		if (klickedLine >= 0 && releasedLine >= 0) {
			removedComponents = removeLines(klickedLine, releasedLine);
			cleanupAndCloseActionInputArea();
			cleanupAndCloseDescriptionInputArea();
			refreshStyledText();
			markCorrespondingLine(klickedLine - 1);
			testEditViewArea.setCursor(testEditViewArea.getCorrespondingLine(klickedLine - 1), true);
			setDirty();
		}
		return removedComponents;
	}

	/**
	 * remove the selected lines.
	 * 
	 * @param klickedLine
	 *            lower border
	 * @param releasedLine
	 *            upper border of selection
	 * @return ArrayList<TestComponent>
	 */
	@Override
	public ArrayList<TestComponent> removeLines(int klickedLine, int releasedLine) {
		BordersOfSelection bordersOfSelection = new BordersOfSelection(klickedLine, releasedLine);
		return testFlow.removeLines(bordersOfSelection.getLowerBorder(), bordersOfSelection.getUpperBorder());
	}

	@Override
	public void setDescriptionActive(int selectedLine, int releasedLine) {
		cleanupAndCloseActionInputArea();
		descriptionController.setDescriptionActive(selectedLine, releasedLine);

	}

	@Override
	public void setScenarioSelectionActive(int selectedLine, int releasedLine) {
		cleanupAndCloseActionInputArea();
		scenarioController.setScenarioSelectionActive(selectedLine, releasedLine);

	}

	@Override
	public void setActionActive() {
		cleanupAndCloseDescriptionInputArea();
		actionInputController.setActionActive();
	}

	@Override
	public void setAddMode(boolean b) {
		actionInputController.setAddMode(b);
		descriptionController.setAddMode(b);
		scenarioController.setAddMode(b);
	}

	@Override
	public void removeTestEditView(TestEditView testEditViewToRemoved) {
		if (this.testEditViewArea == testEditViewToRemoved) {
			testEditViewArea = null;
			cleanupAndCloseActionInputArea();
			if (actionInputController != null) {
				actionInputController.removeTestCaseController(this);
			}
			cleanupAndCloseDescriptionInputArea();
			descriptionController.removeTestCaseController(this);
			cleanupAndCloseScenarioSelectionArea();
			if (scenarioController != null) {
				scenarioController.removeTestCaseController(this);
			}
			testFlow = null;
		}
	}

	/**
	 * cleans and closes the TestEditorActionInputArea.
	 */
	protected void cleanupAndCloseActionInputArea() {
		if (actionInputController != null) {
			actionInputController.cleanViewsSynchron();
		}
	}

	/**
	 * cleans and closes the TestEditorDescriptionInputArea.
	 */
	private void cleanupAndCloseDescriptionInputArea() {
		descriptionController.cleanViewsSynchron();
	}

	/**
	 * cleans and closes the TestEditorDescriptionInputArea.
	 */
	private void cleanupAndCloseScenarioSelectionArea() {
		if (scenarioController != null) {
			scenarioController.cleanScenarioSelectionInTree();
		}
	}

	@Override
	public void moveRow(int zeileFromLowerInTestCase, int zeileFromUpperInTestCase, int zeileToInTestCase,
			boolean insertBefore) {
		if (zeileToInTestCase >= 0) {
			if (zeileToInTestCase > testFlow.getSize() - 1) {
				zeileToInTestCase = testFlow.getSize() - 1;
			}
			LinkedList<TestComponent> removedLines = new LinkedList<TestComponent>();
			for (int line = zeileFromLowerInTestCase; line <= zeileFromUpperInTestCase; line++) {
				TestComponent testcomp = removeLine(zeileFromLowerInTestCase);
				removedLines.add(testcomp);
			}
			int countRemovedLines = removedLines.size();
			if ((zeileToInTestCase > zeileFromLowerInTestCase) && (zeileToInTestCase >= countRemovedLines)) {
				zeileToInTestCase = zeileToInTestCase - countRemovedLines;
			}
			if (!insertBefore) {
				zeileToInTestCase++;
			}
			for (int i = 0; i < countRemovedLines; i++) {

				addLine(zeileToInTestCase, removedLines.get(i));
				zeileToInTestCase++;
			}
		}
	}

	@Override
	public void setUnparsedActionGroup(String mask, String actionName, List<String> inputTexts, int selectedLine) {

		TestActionGroup testActionGr = createUnparsedActionLine(mask, inputTexts);
		try {
			testFlow.setLine(selectedLine,
					testStructureContentService.parseFromString(getTestFlow(), testActionGr.getSourceCode()).get(0));
		} catch (SystemException e) {
			LOGGER.error("Error on parsing String " + testActionGr.getSourceCode() + " to testcomponent. ", e);
		}
	}

	/**
	 * 
	 * creates an unparsedActionLine.
	 * 
	 * @param mask
	 *            Mask
	 * @param inputTexts
	 *            inputTexts
	 * @return TestActionGroup whit the unparsed Line
	 */
	protected abstract TestActionGroup createUnparsedActionLine(String mask, List<String> inputTexts);

	@Override
	public int getSelectedLine() {
		int klickedLineInTestcase = testEditViewArea.getKlickedLineInTestCase();
		if (klickedLineInTestcase >= 0) {
			return klickedLineInTestcase;
		}
		return -1;
	}

	@Override
	public void setSelectedLineInView(int selectedLine) {
		testEditViewArea.markSelectedLine(selectedLine);
		getPart().setOnTop(true);
		partService.activate(getPart());
	}

	@Override
	public void markCorrespondingLine(int selectedLineInTestCase) {
		testEditViewArea.markSelectedLine(testEditViewArea.getCorrespondingLine(selectedLineInTestCase));
		getPart().setOnTop(true);
		partService.activate(getPart());
	}

	@Override
	public boolean isEmptyLineInView(int searchLine) {
		return testEditViewArea.isLineEmpty(testEditViewArea.getCorrespondingLine(searchLine));
	}

	@Override
	public boolean isLineInTestFlow(int line) {
		return testFlow.getSize() > line;
	}

	@Override
	public TestEditorTestDataTransferContainer copySelectedTestcomponents() {
		BordersOfSelection bordersOfSelection = new BordersOfSelection(testEditViewArea.getSelectionStartInTestCase(),
				testEditViewArea.getSelectionEndInTestCase());
		if (getTestFlowSize() > 0) {

			ArrayList<TestComponent> testComps = getSelectedTestComponents(bordersOfSelection.getLowerBorder(),
					bordersOfSelection.getUpperBorder());
			return createDataContainerFromSelectedTestComponents(testComps);
		}
		return null;
	}

	/**
	 * gets the selected TestComponents.
	 * 
	 * @param lowerBorder
	 *            as int
	 * @param upperBorder
	 *            as int
	 * @return ArrayList<TestComponent>
	 */
	protected ArrayList<TestComponent> getSelectedTestComponents(int lowerBorder, int upperBorder) {
		ArrayList<TestComponent> components = new ArrayList<TestComponent>();
		for (int line = lowerBorder; line <= upperBorder; line++) {
			components.add(getTestComponentAt(line));
		}
		return components;
	}

	/**
	 * creates the {@link TestEditorTestDataTransferContainer} from the
	 * parameter testComps.
	 * 
	 * @param testComps
	 *            ArrayList with the TestComponents
	 * @return the {@link TestEditorTestDataTransferContainer}
	 */
	protected TestEditorTestDataTransferContainer createDataContainerFromSelectedTestComponents(
			ArrayList<TestComponent> testComps) {
		TestEditorTestFlowTransferContainer dataContainer = new TestEditorTestFlowTransferContainer();
		dataContainer.setTransferStructure(testComps);
		dataContainer.setTestProjectName(testFlow.getRootElement().getName());
		return dataContainer;
	}

	@Override
	public TestEditorTestDataTransferContainer cutSelectedTestComponents() {
		ArrayList<TestComponent> removedSelectedLines = removeSelectedLinesAndCleanUp();
		return createDataContainerFromSelectedTestComponents(removedSelectedLines);
	}

	@Override
	public void pasteStoredTestComponents(int lineTo, boolean insertBefore, Object transferObject) {
		if (!isTransferObjTestflowTransForParentProject(transferObject)) {
			return;
		}
		try {
			List<TestComponent> testComponents = ((TestEditorTestFlowTransferContainer) transferObject)
					.getStoredTestComponents(getTestFlow(), testStructureContentService);
			if (testComponents.isEmpty()) {
				return;
			}
			int position = lineTo;
			if (lineTo > 0) {
				position = testEditViewArea.getCodeLineMapper().getContentOfOffsetViewToTestListAt(lineTo);
			}
			if (!insertBefore) {
				position++;
			}

			if (position >= 0) {
				testFlow.addTestComponentsAtPos(position, testComponents);
				// try {
				// testStructureContentService.reparseChangedTestFlow(testFlow);
				// } catch (SystemException e) {
				// LOGGER.error("reparse Testcase:: FAILED", e);
				// }
				setDirty();
				refreshStyledText();
				int newCursorPositionInView = testEditViewArea.getCorrespondingLine(position + testComponents.size());
				boolean cursorAtLineEnd = false;
				if (position + testComponents.size() == getTestFlowSize()) {
					cursorAtLineEnd = true;
				}
				testEditViewArea.setClickedLine(newCursorPositionInView);
				testEditViewArea.setReleasedLine(newCursorPositionInView);
				testEditViewArea.setCursor(newCursorPositionInView, cursorAtLineEnd);
			}
		} catch (SystemException e) {
			LOGGER.error("paste TestComponents:: FAILED", e);
		}
	}

	/**
	 * checks the projects from the testFlow and the transferObject are equal
	 * and the transferObject is a TestEditorTestFlowTransferContainer.
	 * 
	 * @param transferObject
	 *            the transferObject
	 * @return true, if Projects are equal and transferObject is a
	 *         TestEditorTestFlowTransferContainer, else false.
	 */
	protected boolean isTransferObjTestflowTransForParentProject(Object transferObject) {
		return ((TestEditorTestDataTransferContainer) transferObject).getTestProjectName() != null
				&& ((TestEditorTestDataTransferContainer) transferObject).getTestProjectName().equals(
						testFlow.getRootElement().getName())
				&& transferObject instanceof TestEditorTestFlowTransferContainer;
	}

	/**
	 * Refresh TestComponents and clear old warnings. If there are new warnings
	 * they are shown.
	 * 
	 * @param testFlow
	 *            the TestFlow
	 */
	protected void refreshTestComponents(TestFlow testFlow) {
		if (messsageArea != null) {
			Control[] children = messsageArea.getChildren();
			messsageArea.setBackground(ColorConstants.COLOR_BACKROUND_NORMAL);
			for (Control control : children) {
				control.dispose();
			}
		}
		try {
			testStructureContentService.refreshTestCaseComponents(testFlow);
		} catch (SystemException e) {
			LOGGER.error("set Testcase:: FAILED", e);
			MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error", e.getLocalizedMessage());
		} catch (TestCycleDetectException e) {
			messsageArea.setBackground(ColorConstants.COLOR_YELLOW);
			Label label = new Label(messsageArea, SWT.WRAP);
			label.setText(translationService.translate("%TestEditView.messagearea.cycledetected", e.getCycleString()));
			compositeForView.layout(true, true);
		}

	}

	@Override
	public void cleanupAndCloseInputAreas() {
		cleanupAndCloseActionInputArea();
		cleanupAndCloseDescriptionInputArea();
		cleanupAndCloseScenarioSelectionArea();
	}

	@Override
	public int getChangePosition(boolean addMode) {
		int selectedLine = getSelectedLine();
		if (getTestFlowSize() == 0) {
			testEditViewArea.setInsertBefore(false);
		}
		if (addMode && (getTestFlowSize() >= selectedLine) && !testEditViewArea.isInsertBefore() || selectedLine == -1) {
			selectedLine++;
		}
		return selectedLine;
	}

	@Override
	public void setDescriptionControllerForPopupEditing(
			TestEditorDescriptionInputController testEditorDescriptionController) {
		descriptionController = testEditorDescriptionController;

	}

	@Override
	public void setActionControllerForPopupEditing(TestEditorActionInputController actionPopupInputController) {
		actionInputController = actionPopupInputController;

	}

	@Override
	public void removePopupEditingControllers() {
		descriptionController = descriptionControllerBackup;
		actionInputController = testEditorInputControllerBackup;
	}

	@Override
	public void closePopupDialog() {
		testEditViewArea.closePopupDialog();
	}

	/**
	 * setter for the local variable testEditorScenarioSelectionController.
	 * 
	 * @param testEditorScenarioSelectionController
	 *            TestEditorScenarioSelectionController
	 */
	public void setTestEditorScenarioSelectionController(
			TestEditorScenarioSelectionController testEditorScenarioSelectionController) {
		this.scenarioController = testEditorScenarioSelectionController;
		scenarioController.refreshFilterForScenarioTree("");
		scenarioController.setScenarioSelectionActive(0, 0);

	}

	@Override
	public void setScenarioToEditArea(TestScenarioParameterTable scenarioParameterTable, int lineNumber)
			throws SystemException {
		scenarioController.putTextToInputArea(scenarioParameterTable.getInclude(), lineNumber);

	}

	@Override
	public void setTestScenarioParameterTable(TestScenarioParameterTable testScenarioParameterTable, int selectedLine,
			boolean changeMode) {
		int position = selectedLine;
		if (changeMode && position >= 0) {
			testFlow.setLine(position, testScenarioParameterTable);
		} else {
			if (testFlow.getTestComponents().isEmpty() || position < 0) {
				position = testFlow.getSize();
			}
			addLine(position, testScenarioParameterTable);
		}
		refreshStyledText();
		setDirty();
		cleanupAndCloseScenarioSelectionArea();
	}

	/**
	 * 
	 * @return the ActionGroupService.
	 */
	protected ActionGroupService getActionGroupService() {
		return actionGroupService;
	}

	/**
	 * 
	 * @return the TestEditorActionInputController
	 */
	public TestEditorActionInputController getActionInputController() {
		return actionInputController;
	}

	@Override
	public void pasteComponents() {
		eventBroker.send(TestEditorUIEventConstants.EDIT_CONTEXTMENU_CNTRL_V, "");
	}

	@Override
	public boolean isTestDataTableSelected() {
		return testEditViewArea.isTestDataTableSelected();
	}

	@Override
	public void cutText() {
		eventBroker.send(TestEditorUIEventConstants.EDIT_CONTEXTMENU_CNTRL_X, "");
	}

	@Override
	public boolean canExecuteCutCopy() {
		return testEditViewArea.canExecuteCutCopy();

	}

	@Override
	public void copyText() {
		eventBroker.send(TestEditorUIEventConstants.EDIT_CONTEXTMENU_CNTRL_C, "");

	}

	@Override
	public boolean canExecuteDelete() {
		return testEditViewArea.canExecuteDelete();
	}

	@Override
	public void executeFileImportToTable() {
		testEditViewArea.executeFileImportToTable();
	}

	/**
	 * 
	 * @return the TestEditorTestDataTransferContainer from the testEditViewArea
	 */
	private TestEditorTestDataTransferContainer getTransfer() {
		return testEditViewArea.getTestFlowTranfer();
	}

	@Override
	public boolean canExecutePasteTestFlow() {
		TestEditorTestDataTransferContainer transferObject = getTransfer();
		return transferObject != null && isTransferObjTestflowTransForParentProject(transferObject);
	}

	@Override
	public void doHandleKeyEvent(KeyEvent e) {
		testEditViewArea.getKeyHandler().doHandleKeyEvent(e);
	}

	@Override
	public TestEditorInputObject getLastUnSavedTestComponentInput(String classNameOfCachedTestComponent) {
		return cachedTestComponentInputMap.get(classNameOfCachedTestComponent);
	}

	@Override
	public void setLastUnSavedTestComponentInput(TestEditorInputObject lastUnSavedTestComponentInput) {
		this.cachedTestComponentInputMap.put(lastUnSavedTestComponentInput.getTestcomponent().getClass().getName(),
				lastUnSavedTestComponentInput);
	}

	@Override
	public void lostFocus() {
		hasFocus = false;
	}

	@Override
	public boolean isInputValid(String text) {
		return !text.contains(" ");
	}

	/**
	 * for tests only.
	 * 
	 * @return value of hasFocus
	 */
	protected boolean hasFocus() {
		return hasFocus;
	}

	@Override
	public void closePart() {
		mpart.setDirty(false);
		partService.hidePart(mpart, true);
	}

	@Override
	public TestScenario getScenarioByFullName(String fullNameOfScenario) throws SystemException {
		return testScenarioService.getScenarioByFullName(testFlow.getRootElement(), fullNameOfScenario);
	}

	@Override
	public TestStructure getTestStructure() {
		return getTestFlow();
	}

	@Override
	public void setTestStructure(TestStructure testStructure) {
		if (testStructure instanceof TestFlow) {
			setTestFlow((TestFlow) testStructure);
		}
	}

	@Override
	public void rememberUnEditableLine(int unEditableLine) {
		unEditableLinesStore.rememberUnEditableLine(unEditableLine);
	}

	@Override
	public void clearUnEditableLines() {
		unEditableLinesStore.clearUnEditableLines();
	}

	@Override
	public boolean isLineEditable(int lineNumber) {
		return unEditableLinesStore.isLineEditable(lineNumber);
	}

	@Override
	public boolean isSelectionEditable(int firstLine, int lastLine) {
		return unEditableLinesStore.isSelectionEditable(firstLine, lastLine);
	}

	/**
	 * 
	 * @return the {@link Composite} for the view.
	 */
	protected Composite getCompositeContent() {
		return compositeForView;
	}

	/**
	 * setter for the testEditViewArea.
	 * 
	 * @param testEditViewArea
	 *            a view to show a TestCase or a TestScenario
	 */
	protected void setTestEditViewArea(TestEditView testEditViewArea) {
		this.testEditViewArea = testEditViewArea;
	}

	/**
	 * 
	 * @return the testEditViewArea
	 */
	protected TestEditView getTestEditViewArea() {
		return testEditViewArea;
	}

	/**
	 * sets the compositeForView for tests.
	 * 
	 * @param compositeForView
	 *            Composite
	 */
	protected void setCompositeForView(Composite compositeForView) {
		this.compositeForView = compositeForView;
	}

	/**
	 * sets the scenarioController for tests.
	 * 
	 * @param scenarioController
	 *            TestEditorScenarioSelectionController
	 */
	protected void setScenarioController(TestEditorScenarioSelectionController scenarioController) {
		setTestEditorScenarioSelectionController(scenarioController);
	}

	@Override
	public boolean isLibraryErrorLessLoaded() {
		return getTestFlow().getRootElement().getTestProjectConfig().getLibraryLoadingStatus().isErrorLessLoaded();
	}

	/**
	 * 
	 * @return descriptionController
	 */
	protected TestEditorDescriptionInputController getDescriptionController() {
		return descriptionController;
	}

	/**
	 * Getter for the metaData Service. Checks if the service is set and throws
	 * an Exception with a message if the service was not configured.
	 * 
	 * @return the service
	 */
	private ITestEditorTabController getTestEditorTabController() {
		if (iTestEditorTabController == null) {
			LOGGER.info("MetaDataTab is not there. Probably the plugin 'org.testeditor.metadata.ui' is not activated");
		}
		return iTestEditorTabController;
	}

	/**
	 * Getter for the metaData Service. Checks if the service is set. If the
	 * service is not there, an infomessage will be displayed.
	 * 
	 * @return the service
	 */
	private MetaDataService getMetaDataService() {
		if (metaDataService == null) {
			LOGGER.info("MetaDataTabService is not there. Probably the plugin 'org.testeditor.metadata.core' is not activated");
		}
		return metaDataService;
	}

}
