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
import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.testeditor.core.constants.TestEditorCoreEventConstants;
import org.testeditor.core.model.action.Argument;
import org.testeditor.core.model.action.IAction;
import org.testeditor.core.model.action.UnparsedActionLine;
import org.testeditor.core.model.teststructure.TestActionGroup;
import org.testeditor.core.model.teststructure.TestActionGroupTestScenario;
import org.testeditor.core.model.teststructure.TestComponent;
import org.testeditor.core.model.teststructure.TestDescription;
import org.testeditor.core.model.teststructure.TestDescriptionTestScenario;
import org.testeditor.core.model.teststructure.TestScenario;
import org.testeditor.ui.constants.IconConstants;
import org.testeditor.ui.constants.TestEditorEventConstants;
import org.testeditor.ui.parts.editor.ITestEditorScenarioController;

/**
 * 
 * specialized {@link TestEditorController} class for the
 * TestEditorTestScenarioController.
 * 
 */
public class TestEditorTestScenarioController extends TestEditorController implements ITestEditorScenarioController {

	public static final String ID = "org.testeditor.ui.partdescriptor.testScenarioView";

	@Inject
	private IEventBroker eventBroker;
	@Inject
	private IEclipseContext context;

	private static final String INVALID_CHARS = "";

	/**
	 * 
	 * @param part
	 *            of gui
	 */
	@Inject
	public TestEditorTestScenarioController(MPart part) {
		super(part);
		part.setIconURI(IconConstants.ICON_URI_SCENARIO);
	}

	@Override
	public void setActionGroup(String mask, String actionName, ArrayList<String> inputLineParts,
			ArrayList<Argument> arguments, int selectedLine, boolean changeMode) {
		int position = selectedLine;
		TestActionGroup testComponent = getActionGroupService().createTestActionGroupTestScenario(
				getTestFlow().getRootElement(), mask, inputLineParts, arguments);
		getActionInputController().setLastSelectedMask(mask);
		super.setActionGroup(mask, changeMode, position, testComponent);
	}

	/**
	 * * scans for scenarioParameters.
	 * 
	 * 
	 * @return true, if their are parameters added
	 */
	@Override
	public boolean scanForScenarioParameters() {
		boolean addedLine = false;
		int sizeTestFlow;
		((TestScenario) getTestFlow()).setTestParameters(new ArrayList<String>());
		sizeTestFlow = getTestFlowSize();
		for (int i = 0; i < sizeTestFlow; i++) {
			TestComponent testComp = getTestComponentAt(i);

			if (testComp instanceof TestActionGroupTestScenario) {
				List<String> actionsParams = ((TestActionGroupTestScenario) testComp).getParameterFromActionGroup();
				for (String param : actionsParams) {
					if (!((TestScenario) getTestFlow()).getTestParameters().contains(param)) {
						((TestScenario) getTestFlow()).addTestparameter(param);
						addedLine = true;
					}
				}
			}
		}
		return addedLine;

	}

	@Override
	public boolean isInputValid(String text) {
		return super.isInputValid(text) && text.startsWith("@") && text.length() > 1;
	}

	/**
	 * Saves the changes TestScenario. if a scenario, is persist, than refresh
	 * also the scenarioSelectionCache.
	 */
	@Override
	@Persist
	public void save() {
		super.save();
		eventBroker.post(TestEditorEventConstants.REFRESH_TESTFLOW_VIEWS_TO_PROJECT, getTestFlow().getRootElement());
	}

	/**
	 * this method creates the TestCaseView.
	 */
	@Override
	public void createTestCaseView() {
		TestEditorScenarioView testEditViewArea = ContextInjectionFactory.make(TestEditorScenarioView.class, context);
		testEditViewArea.setTestCaseController(this);
		testEditViewArea.createUI(getCompositeContent());
		setTestEditViewArea(testEditViewArea);
	}

	/**
	 * creates the descriptionArray.
	 * 
	 * @param newLines
	 *            List<String>
	 * @return List<TestDescription>
	 */
	@Override
	protected List<TestDescription> createDescriptionsArray(List<String> newLines) {
		List<TestDescription> descriptions = new ArrayList<TestDescription>();
		for (String line : newLines) {
			TestDescription desc = new TestDescriptionTestScenario(line);
			descriptions.add(desc);
		}
		return descriptions;
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
	@Override
	protected TestActionGroup createUnparsedActionLine(String mask, List<String> inputTexts) {
		ArrayList<Argument> arguments = new ArrayList<Argument>();
		TestActionGroup testActionGr = getActionGroupService().createTestActionGroupTestScenario(
				getTestFlow().getRootElement(), mask, inputTexts, arguments);
		getActionInputController().setLastSelectedMask(mask);
		IAction lcAction = testActionGr.getActionLines().get(0);
		UnparsedActionLine unparsed = new UnparsedActionLine(inputTexts.get(0));
		testActionGr.removeActionLine(lcAction);
		testActionGr.addActionLine(unparsed);
		return testActionGr;
	}

	@Override
	public String getInvalidChars() {
		return INVALID_CHARS;
	}

	@Override
	public TestEditorTestDataTransferContainer copySelectedTestcomponents() {
		BordersOfSelection bordersOfSelection = new BordersOfSelection(getTestEditViewArea()
				.getSelectionStartInTestCase(), getTestEditViewArea().getSelectionEndInTestCase());
		if (getTestFlowSize() > 0) {
			int lowerBorder = bordersOfSelection.getLowerBorder();
			if (lowerBorder == 0) {
				lowerBorder++;
			}

			ArrayList<TestComponent> testComps = getSelectedTestComponents(lowerBorder,
					bordersOfSelection.getUpperBorder());
			return createDataContainerFromSelectedTestComponents(testComps);
		}
		return null;
	}

	/**
	 * Callback Method for calling from Event-Broker if TestStructure will be
	 * deleted.
	 * 
	 * @param testStructureFullName
	 *            full name of teststructure
	 */
	@Inject
	@Optional
	public void onTestStructureModelChangedDeleted(
			@UIEventTopic(TestEditorCoreEventConstants.TESTSTRUCTURE_MODEL_CHANGED_DELETED) String testStructureFullName) {

		// close part if it belongs to project
		if (testStructureFullName != null && getTestStructure().getFullName().startsWith(testStructureFullName)) {
			closePart();
		}
	}

	@Override
	protected String getId() {
		return ID;
	}

}
