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
package org.testeditor.ui.parts.inputparts.scenarioselection;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.testeditor.core.constants.TestEditorCoreEventConstants;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.model.teststructure.TestData;
import org.testeditor.core.model.teststructure.TestDataRow;
import org.testeditor.core.model.teststructure.TestScenario;
import org.testeditor.core.model.teststructure.TestScenarioParameterTable;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.services.interfaces.TestEditorPlugInService;
import org.testeditor.core.services.interfaces.TestProjectService;
import org.testeditor.core.services.interfaces.TestScenarioService;
import org.testeditor.ui.constants.TestEditorEventConstants;
import org.testeditor.ui.handlers.OpenTestStructureHandler;
import org.testeditor.ui.parts.commons.tree.filter.TestScenarioRecursiveFilter;
import org.testeditor.ui.parts.editor.ITestEditorController;
import org.testeditor.ui.parts.editor.view.TestEditorController;
import org.testeditor.ui.parts.inputparts.AbstractTestEditorInputPartController;

/**
 * 
 * this class is the controller for the edit view part.
 * 
 */
public class TestEditorScenarioSelectionController extends AbstractTestEditorInputPartController implements
		ITestScenarioSelectionController {

	public static final String ID = "org.testeditor.ui.partdescriptor.testStructureEditor.ScenarioSelection";

	@Inject
	private IEclipseContext context;
	@Inject
	private TestEditorPlugInService pluginService;
	@Inject
	private TestProjectService testProjectService;

	private TestEditorScenarioSelectionView editArea;

	private ITestEditorController testEditorController;

	private TestScenarioTreeInput testScenarioTreeInput;

	@Inject
	private IEventBroker eventBroker;

	private Composite parent;
	private static final Logger LOGGER = Logger.getLogger(TestEditorScenarioSelectionController.class);

	/**
	 * this method is called when this part gets the focus. This method is
	 * necessary to handle the CTRL+Enter-Event. Don't delete the empty method.
	 */
	@Focus
	public void onFocus() {
		editArea.setCommitToDefaultButton();
	}

	/**
	 * 
	 * @param parent
	 *            composite
	 */
	@PostConstruct
	public void createControls(Composite parent) {
		this.parent = parent;
		editArea = ContextInjectionFactory.make(TestEditorScenarioSelectionView.class, context);
	}

	/**
	 * this method is called, when the TestCaseView-part is changed.
	 * 
	 * @param testEditorController
	 *            ITestEditorController
	 */
	public void setTestCaseController(ITestEditorController testEditorController) {
		this.testEditorController = testEditorController;
		if (!editArea.newTestEditorController(testEditorController)) {
			if (testScenarioTreeInput == null
					|| testScenarioTreeInput != null
					&& (testScenarioTreeInput.getTestProject() == null || !testScenarioTreeInput.getTestProject()
							.equals(testEditorController.getTestFlow().getRootElement()))) {

				testScenarioTreeInput = new TestScenarioTreeInput(
						testProjectService.getProjectWithName(testEditorController.getTestFlow().getRootElement()
								.getName()));

				editArea.changeTreeInput(testScenarioTreeInput);
				editArea.removeTreeFilter();
				if (testEditorController.getTestFlow() instanceof TestScenario) {
					editArea.setRecursiveFilter(new TestScenarioRecursiveFilter((TestScenario) testEditorController
							.getTestFlow()));
				}
			}
			editArea.createUI(parent);
			editArea.setTestScenarioSelectionController(this);
			editArea.setTestCaseControler(testEditorController);
			editArea.cleanInput();
			setAddMode(true);

		}
		editArea.enableViews();
	}

	/**
	 * put the text from the view area to the edit area.
	 * 
	 * @param includeOfScenario
	 *            the selected text
	 * @param selectedLine
	 *            the line-number of the begin of the selection
	 * @throws SystemException
	 *             on reading scenario
	 * 
	 */
	public void putTextToInputArea(String includeOfScenario, int selectedLine) throws SystemException {
		if (includeOfScenario.length() > 10) {
			TestScenarioService scenarioService = pluginService.getTestScenarioService(testEditorController
					.getTestFlow().getRootElement().getTestProjectConfig().getTestServerID());
			TestScenario scenarioByFullName = scenarioService.getScenarioByFullName(testEditorController.getTestFlow()
					.getRootElement(), includeOfScenario.substring(10));
			editArea.setScenarioSelectionToChangeable(scenarioByFullName, selectedLine);
			editArea.setScenarioSelectionActive();
			setAddMode(false);
		}
	}

	/**
	 * adds the input, in the input area, to the test case.
	 */
	public void addInputLine() {
		editArea.changeInputInView();
	}

	/**
	 * set the scenarioSelection input active.
	 * 
	 * @param selectedLine
	 *            selectedLine in testcase
	 * @param releasedLine
	 *            relesedLine in testcase
	 * 
	 */
	public void setScenarioSelectionActive(int selectedLine, int releasedLine) {
		editArea.setSelectedLine(selectedLine);
		editArea.cleanInput();
		editArea.setScenarioSelectionActive();

	}

	/**
	 * set the add mode in the editArea.
	 * 
	 * @param b
	 *            boolean
	 */
	public void setAddMode(boolean b) {
		editArea.setAddMode(b);

	}

	/**
	 * clean the ScenarioSelectionArea and close them.
	 */
	@Override
	public void cleanViewsSynchron() {
		editArea.cleanInput();
		if (testEditorController != null) {
			refreshSceanriosInTree("");
		}
		setAddMode(true);
	}

	/**
	 * clean the ScenarioSelectionArea and close them.
	 */
	@Override
	public void cleanViewsAsynchron() {
		editArea.cleanInput();
		if (testEditorController != null) {
			eventBroker.post(TestEditorEventConstants.REFRESH_FILTER_FOR_SCENARIOS_IN_TREE, "");
		}
		setAddMode(true);
	}

	/**
	 * set the parameter enable-close-after-commit.
	 * 
	 * @param b
	 *            boolean
	 */
	public void setPopupmode(boolean b) {
		editArea.setPopupmode(b);
	}

	/**
	 * disables the views.
	 */
	@Override
	public void disableViews() {
		editArea.disabelViews();

	}

	/**
	 * removes the testEditorController, if its equal to the stored in this
	 * object.
	 * 
	 * @param testEditorController
	 *            ITestEditorController
	 */
	public void removeTestCaseController(TestEditorController testEditorController) {
		editArea.removeTestEditorController(testEditorController);
	}

	/**
	 * Puts the names of the scenarios into the tree. Will be invoked from
	 * Eventbroker.
	 * 
	 * @param testStructure
	 *            TestStructure
	 */
	@Inject
	@Optional
	public void refreshSceanriosInTreeBySvnLoaded(
			@UIEventTopic(TestEditorCoreEventConstants.TEAM_STATE_LOADED) TestStructure testStructure) {
		refreshSceanriosInTree("");
	}

	/**
	 * Puts the names of the scenarios into the tree. Will be invoked from
	 * Eventbroker.
	 * 
	 * @param data
	 *            data of the refresh
	 */
	@Inject
	@Optional
	public void refreshSceanriosInTree(
			@UIEventTopic(TestEditorCoreEventConstants.TESTSTRUCTURE_MODEL_CHANGED) String data) {
		if (testEditorController != null && testEditorController.getTestFlow() != null
				&& testEditorController.getTestFlow().getRootElement() != null) {
			Object[] expandedElements = editArea.getVisibleExpandedTreeElements();
			TestStructure selectedElement = editArea.getSelectedTreeElement();

			testScenarioTreeInput = new TestScenarioTreeInput(
					testProjectService
							.getProjectWithName(testEditorController.getTestFlow().getRootElement().getName()));

			editArea.changeTreeInput(testScenarioTreeInput);
			editArea.removeTreeFilter();
			if (testEditorController.getTestFlow() instanceof TestScenario) {
				editArea.setRecursiveFilter(new TestScenarioRecursiveFilter((TestScenario) testEditorController
						.getTestFlow()));
			}
			editArea.expandTreeElements(expandedElements);
			editArea.selectTreeElement(selectedElement);
		}
	}

	/**
	 * refreshes the filter in the tree or change the hole tree input, if the
	 * project of the TestStructure in the editor-view is changed. Will be
	 * invoked from Eventbroker.
	 * 
	 * @param param
	 *            be empty
	 */
	@Inject
	@Optional
	public void refreshFilterForScenarioTree(
			@UIEventTopic(TestEditorEventConstants.REFRESH_FILTER_FOR_SCENARIOS_IN_TREE) String param) {
		if (testEditorController != null && testEditorController.getTestFlow() != null) {
			if (testScenarioTreeInput != null
					&& testScenarioTreeInput.getTestProject() != null
					&& !testScenarioTreeInput.getTestProject().getFullName()
							.equalsIgnoreCase(testEditorController.getTestFlow().getRootElement().getFullName())) {
				refreshSceanriosInTree("");
			} else if (testScenarioTreeInput != null && testScenarioTreeInput.getTestProject() == null) {
				testScenarioTreeInput.setTestProject(testEditorController.getTestFlow().getRootElement());
				refreshSceanriosInTree("");
			} else {
				Object[] expandedElements = editArea.getVisibleExpandedTreeElements();
				TestStructure selectedElement = editArea.getSelectedTreeElement();
				editArea.removeTreeFilter();
				if (testEditorController.getTestFlow() instanceof TestScenario) {
					editArea.setRecursiveFilter(new TestScenarioRecursiveFilter((TestScenario) testEditorController
							.getTestFlow()));
				}
				if (expandedElements != null) {
					editArea.expandTreeElements(expandedElements);
				}
				if (selectedElement != null) {
					editArea.selectTreeElement(selectedElement);
				}
			}
		}
	}

	@Override
	public void setScenarioIntoTestFlow(TestStructure selectedTestStructure, int lineInTestCase, boolean addMode) {
		TestScenario scenario = (TestScenario) selectedTestStructure;
		if (scenario != null) {
			TestScenarioParameterTable testScenarioParameterTable = new TestScenarioParameterTable();
			testScenarioParameterTable.setTitle(scenario.getName());
			// ToDo hier die FitNesse spezifischen Elemente ("!include <")
			// entfernen.
			testScenarioParameterTable.setInclude(scenario.getFullName());
			try {
				TestScenarioService scenarioService = pluginService.getTestScenarioService(testEditorController
						.getTestFlow().getRootElement().getTestProjectConfig().getTestServerID());

				testScenarioParameterTable.setScenarioOfProject(scenarioService.isLinkToScenario(testEditorController
						.getTestFlow().getRootElement(), scenario.getFullName()));
				if (scenario.getTestComponents().isEmpty()) {

					scenario = scenarioService.getScenarioByFullName(scenario.getRootElement(), scenario.getFullName());
				}
				if (scenario != null && scenario.getTestParameters().size() > 0
						&& !scenario.getTestParameters().get(0).equals("")) {
					String[] params = new String[scenario.getTestParameters().size()];
					for (int i = 0; i < scenario.getTestParameters().size(); i++) {
						params[i] = scenario.getTestParameters().get(i);
					}

					TestDataRow testDataRow = new TestDataRow(params);
					TestData testData = new TestData();
					testData.addRow(testDataRow);
					testData.addEmptyRow(1);

					testScenarioParameterTable.addTestData(testData);
				} else {
					testScenarioParameterTable.setSimpleScriptStatement(true);
				}
				testEditorController
						.setTestScenarioParameterTable(testScenarioParameterTable, lineInTestCase, !addMode);
			} catch (SystemException e) {
				MessageDialog.openError(Display.getCurrent().getActiveShell(), "System-Exception",
						e.getLocalizedMessage());
				LOGGER.error("error by getting the scenario", e);
			}

		}
	}

	@Override
	public void openSelectedScenario(String nameOfScenario) {
		TestScenario scenario;
		try {
			scenario = testEditorController.getScenarioByFullName(nameOfScenario);
			ContextInjectionFactory.make(OpenTestStructureHandler.class, context).execute(scenario, context);
		} catch (SystemException e) {
			MessageDialog.openError(Display.getCurrent().getActiveShell(), "System-Exception", e.getLocalizedMessage());
			LOGGER.error("Error on getting the scenario", e);
		}
	}

	@Override
	public void cacheInput(@UIEventTopic(TestEditorEventConstants.CACHE_TEST_COMPONENT_OF_PART_TEMPORARY) Object obj) {
		if (editArea != null) {
			editArea.cacheInput();
		}

	}

	@Override
	public void removeTestEditorController() {
		if (editArea != null) {
			editArea.setTestCaseControler(null);
		}
	}

	/**
	 * cleans the scenario-selection in the tree.
	 */
	public void cleanScenarioSelectionInTree() {
		editArea.cleanSceanrioSelectionInTree();

	}

	/**
	 * removes the testProject in the {@link TestScenarioTreeInput}.
	 * 
	 * @param testStructureFullName
	 *            TestStructure which was deleted.
	 */
	@Inject
	@Optional
	public void removeTestScenariosOfProject(
			@UIEventTopic(TestEditorCoreEventConstants.TESTSTRUCTURE_MODEL_CHANGED_DELETED) String testStructureFullName) {
		if (testScenarioTreeInput != null && testScenarioTreeInput.getTestProject() != null) {
			if (testScenarioTreeInput.getTestProject().getFullName().equals(testStructureFullName)) {
				testScenarioTreeInput.setTestProject(null);
			}
		}
	}
}