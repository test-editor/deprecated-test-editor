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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.internal.events.EventBroker;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.event.EventHandler;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.model.action.Argument;
import org.testeditor.core.model.action.ProjectLibraryConfig;
import org.testeditor.core.model.teststructure.LibraryLoadingStatus;
import org.testeditor.core.model.teststructure.TestActionGroup;
import org.testeditor.core.model.teststructure.TestCase;
import org.testeditor.core.model.teststructure.TestComponent;
import org.testeditor.core.model.teststructure.TestDescription;
import org.testeditor.core.model.teststructure.TestDescriptionTestCase;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestProjectConfig;
import org.testeditor.core.model.teststructure.TestScenarioParameterTable;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.model.teststructure.TestSuite;
import org.testeditor.core.services.interfaces.ActionGroupService;
import org.testeditor.core.services.interfaces.ServiceLookUpForTest;
import org.testeditor.core.services.interfaces.TestProjectService;
import org.testeditor.core.services.interfaces.TestScenarioService;
import org.testeditor.core.services.interfaces.TestStructureContentService;
import org.testeditor.core.services.plugins.LibraryConfigurationServicePlugIn;
import org.testeditor.core.services.plugins.TestEditorPlugInService;
import org.testeditor.ui.adapter.MPartAdapter;
import org.testeditor.ui.adapter.PartServiceAdapter;
import org.testeditor.ui.adapter.TestStructureContentServiceAdapter;
import org.testeditor.ui.constants.TestEditorUIEventConstants;
import org.testeditor.ui.mocks.TestScenarioServiceMock;
import org.testeditor.ui.parts.editor.view.Adapter.ActionGroupServiceAdapter;
import org.testeditor.ui.parts.editor.view.Adapter.TestEditorControllerMock;
import org.testeditor.ui.parts.editor.view.handler.TestEditorInputObject;
import org.testeditor.ui.parts.inputparts.actioninput.TestEditorActionInputController;
import org.testeditor.ui.parts.inputparts.descriptioninput.TestEditorDescriptionInputController;
import org.testeditor.ui.utilities.TestEditorTranslationService;

/**
 * Test of testEditorController.
 * 
 * 
 */
public class TestEditorControllerTest {

	private TestEditorControllerMock testEditorController;
	private MPart mPartAdapter;
	private Shell shell;
	private TestCase testFlowForTest;
	private TestProject testProject;
	private TestDescriptionTestCase descOne = new TestDescriptionTestCase("New description one");
	private TestDescriptionTestCase descTwo = new TestDescriptionTestCase("New description two");
	private TestDescriptionTestCase descThree = new TestDescriptionTestCase("New description three");
	private TestDescriptionTestCase descFour = new TestDescriptionTestCase("New description four");
	private IEventBroker eventBroker;
	private final Set<String> events = new HashSet<String>();
	private TestStructure testStructureFromEvent;
	private TestStructureContentServiceAdapter testStructureContentServiceAdapter;

	/**
	 * test the save method.
	 */
	@Test
	public void saveTest() {
		TestCase testFlow = new TestCase();
		TestProjectConfig config = new TestProjectConfig();
		config.setTestServerID("test_adapter");
		TestProject tp = new TestProject();
		tp.setTestProjectConfig(config);
		tp.addChild(testFlow);
		testEditorController.setTestFlow(testFlow);
		mPartAdapter.setDirty(true);
		testFlow.setName("Test");
		testEditorController.save();
		assertEquals("Test_saved", testFlow.getName());
		assertFalse(mPartAdapter.isDirty());

	}

	/**
	 * test the getTestFlowSize() method.
	 */
	@Test
	public void getTestFlowSizeTest() {
		TestCase testFlow = new TestCase();
		testEditorController.setTestFlow(testFlow);

		assertEquals(0, testEditorController.getTestFlowSize());
		testFlow.addTestComponent(new TestDescriptionTestCase("New description"));
		assertEquals(1, testEditorController.getTestFlowSize());

	}

	/**
	 * test the getLine() method.
	 */
	@Test
	public void getLineTest() {
		TestDescription testDescription = initalizeForTest();

		assertEquals(testDescription.getTexts(), testEditorController.getLine(2));
	}

	/**
	 * test the setTestStructure with a testStructure != TestFlow method.
	 */
	@Test
	public void setTestStructureTest() {
		TestSuite suite = new TestSuite();
		testEditorController.setTestStructure(suite);
		assertNull(testEditorController.getTestFlow());
	}

	/**
	 * test the getTextTypes() method.
	 */
	@Test
	public void getTextTypesTest() {
		TestDescription testDescription = initalizeForTest();

		assertEquals(testDescription.getTextTypes(), testEditorController.getTextTypes(2));
	}

	/**
	 * initialize for the tests.
	 * 
	 * @return a testDescription
	 */
	private TestDescription initalizeForTest() {
		TestProject tp = new TestProject();
		tp.setName("MyProject");
		TestProjectConfig config = new TestProjectConfig();
		config.setTestServerID("test_adapter");
		tp.setTestProjectConfig(config);
		testFlowForTest = new TestCase();
		tp.addChild(testFlowForTest);
		testFlowForTest.getTestComponents().clear();
		testFlowForTest.addTestComponent(descOne);
		testFlowForTest.addTestComponent(descTwo);
		testFlowForTest.addTestComponent(descThree);
		testFlowForTest.addTestComponent(descFour);
		testEditorController.setTestStructure(testFlowForTest);
		return descThree;
	}

	/**
	 * test the removeLine() method.
	 */
	@Test
	public void removeLineTest() {
		TestDescription testDescription = initalizeForTest();

		assertEquals(testDescription, testEditorController.removeLine(2));
		assertEquals(3, testEditorController.getTestFlowSize());
	}

	/**
	 * test the removeLine() method.
	 */
	@Test
	public void addLineTest() {
		TestDescription testDescription = initalizeForTest();

		testEditorController.addLine(0, testDescription);
		assertEquals(5, testEditorController.getTestFlowSize());
		assertEquals(testDescription.getTexts(), testEditorController.getLine(0));
		assertEquals(testDescription.getTexts(), testEditorController.getLine(3));
		int testFlowSize = testEditorController.getTestFlowSize();
		testEditorController.addLine(7, testDescription);
		assertEquals(testDescription.getTexts(), testEditorController.getLine(testFlowSize));

	}

	/**
	 * test the setDescription() method.
	 */
	@Test
	public void setDescriptionTest() {
		initalizeForTest();

		List<String> descriptionText = moreDescriptionTexts();
		testEditorController.setDescription(2, descriptionText, false);
		assertEquals(10, testEditorController.getTestFlowSize());
	}

	/**
	 * test the setDescription() method.
	 */
	@Test
	public void setChangeDescriptionTest() {
		initalizeForTest();

		List<String> descriptionText = moreDescriptionTexts();
		testEditorController.setDescription(2, descriptionText, true);
		assertEquals(9, testEditorController.getTestFlowSize());
	}

	/**
	 * test the setDescription() method.
	 */
	@Test
	public void setChangeDescriptionEmptyTestCaseTest() {

		List<String> descriptionText = moreDescriptionTexts();
		testEditorController.setTestFlow(new TestCase());
		testEditorController.setDescription(2, descriptionText, true);
		assertEquals(6, testEditorController.getTestFlowSize());
	}

	/**
	 * test the setDirty() method.
	 */
	@Test
	public void setDirtyTest() {
		initalizeForTest();
		testEditorController.setDirty();
		assertTrue(mPartAdapter.isDirty());
	}

	/**
	 * tests the getTestStructure() method.
	 */
	@Test
	public void getTestStructureTest() {
		initalizeForTest();
		assertEquals(testFlowForTest, testEditorController.getTestStructure());
	}

	/**
	 * test the method removeSelectedLinesAndCleanUp(). The selected lines are
	 * fixed in the code of the TestEditorControllerMock.
	 */
	@Test
	public void removeSelectedLinesAndCleanUpTest() {
		initalizeForTest();
		List<String> descriptionText = moreDescriptionTexts();
		testEditorController.setDescription(4, descriptionText, false);
		testEditorController.removeSelectedLinesAndCleanUp();
		assertEquals(6, testEditorController.getTestFlowSize());

	}

	/**
	 * test the method cutSelectedTestComponents(). The selected lines are fixed
	 * in the code of the TestEditorControllerMock.
	 */
	@Test
	public void cutSelectedTestComponentsTest() {
		initializeSpecialTestFlowAndProject();
		List<String> descriptionText = moreDescriptionTexts();
		testEditorController.setDescription(4, descriptionText, false);
		TestEditorTestDataTransferContainer testEditorTestDataTransferContainer = testEditorController
				.cutSelectedTestComponents();
		assertEquals(testProject.getName(), testEditorTestDataTransferContainer.getTestProjectName());
		assertEquals(4, testEditorTestDataTransferContainer.getStoredTestComponents().split("\n").length);
		assertEquals(6, testEditorController.getTestFlowSize());

	}

	/**
	 * test the closePart method.
	 */
	@Test
	public void closePart() {
		initalizeForTest();
		mPartAdapter.setDirty(true);
		assertTrue(mPartAdapter.isDirty());
		testEditorController.closePart();
		assertFalse(mPartAdapter.isDirty());
	}

	/**
	 * test the isInputValid method.
	 */
	@Test
	public void isInputValidTest() {
		assertFalse(testEditorController.isInputValid(" "));
		assertTrue(testEditorController.isInputValid("X"));
	}

	/**
	 * test the uneditable Lines.
	 */
	@Test
	public void testUneditableLines() {
		testEditorController.rememberUnEditableLine(7);
		testEditorController.rememberUnEditableLine(12);
		testEditorController.rememberUnEditableLine(8);
		testEditorController.rememberUnEditableLine(9);
		assertTrue(testEditorController.isSelectionEditable(1, 1));
		assertTrue(testEditorController.isLineEditable(1));
		assertFalse(testEditorController.isSelectionEditable(7, 8));
		assertTrue(testEditorController.isSelectionEditable(7, 12));
		testEditorController.clearUnEditableLines();
		assertTrue(testEditorController.isSelectionEditable(7, 8));
	}

	/**
	 * test the set- and getLastUnSavedTestComponentInput methods.
	 */
	@Test
	public void lastUnSavedTestComponentInputTest() {
		TestEditorInputObject testEditorInputObject = new TestEditorInputObject(testFlowForTest,
				new TestDescriptionTestCase("Hallo World!"), 1, 0, true);
		testEditorController.setLastUnSavedTestComponentInput(testEditorInputObject);
		assertNull(testEditorController.getLastUnSavedTestComponentInput(new TestActionGroup().getClass().getName()));
		assertEquals(testEditorInputObject,
				testEditorController.getLastUnSavedTestComponentInput(new TestDescriptionTestCase().getClass()
						.getName()));
	}

	/**
	 * test the testflow canExecutePasteTestFlow method.
	 */
	@Test
	public void testCanPaste() {
		initializeSpecialTestFlowAndProject();
		TestEditorTestFlowTransferContainer transferContainer = getTransferContainer();
		testEditorController.setTestFlowTranfer(transferContainer);
		assertTrue(testEditorController.canExecutePasteTestFlow());
	}

	/**
	 * test the canExecuteDelete method.
	 */
	@Test
	public void canExecuteDeleteTest() {
		initializeSpecialTestFlowAndProject();
		assertTrue(testEditorController.canExecuteDelete());
	}

	/**
	 * test the canExecuteCutCopy method.
	 */
	@Test
	public void canExecuteCutCopyTest() {
		initializeSpecialTestFlowAndProject();
		assertTrue(testEditorController.canExecuteCutCopy());
	}

	/**
	 * test the isTestDataTableSelected method.
	 */
	@Test
	public void isTestDataTableSelectedTest() {
		initializeSpecialTestFlowAndProject();
		assertTrue(testEditorController.isTestDataTableSelected());

	}

	/**
	 * test the testflow transfer method.
	 */
	@Test
	public void testCanPasteWrongProject() {
		initializeSpecialTestFlowAndProject();
		TestEditorTestDataTransferContainer transferContainer = getTransferContainer();
		testEditorController.setTestFlowTranfer(transferContainer);
		TestProject testProject2 = new TestProject();
		TestCase testCase = new TestCase();
		testProject2.addChild(testCase);
		testProject2.setName("Project_two");
		testEditorController.setTestFlow(testCase);
		assertFalse(testEditorController.canExecutePasteTestFlow());
	}

	/**
	 * initialize a testFlow and add it to a new TestProject.
	 */
	private void initializeSpecialTestFlowAndProject() {
		initalizeForTest();
		testProject = new TestProject();
		TestProjectConfig config = new TestProjectConfig();
		config.setTestServerID("test_adapter");
		testProject.setTestProjectConfig(config);
		testProject.setName("Project_one");
		testProject.addChild(testFlowForTest);
	}

	/**
	 * test the copy of selected TestComponents.
	 */
	@Test
	public void copySelectedTestcomponentsTest() {
		initializeSpecialTestFlowAndProject();
		List<String> moreDescriptionTexts = moreDescriptionTexts();
		testEditorController.setDescription(4, moreDescriptionTexts, false);
		TestEditorTestDataTransferContainer testcomponents = testEditorController.copySelectedTestcomponents();
		assertEquals(testProject.getName(), testcomponents.getTestProjectName());
		StringBuilder testComps = new StringBuilder();
		testComps.append(testFlowForTest.getTestComponents().get(2).getSourceCode()).append("\n");
		testComps.append(testFlowForTest.getTestComponents().get(3).getSourceCode()).append("\n");
		testComps.append(testFlowForTest.getTestComponents().get(4).getSourceCode()).append("\n");
		testComps.append(testFlowForTest.getTestComponents().get(5).getSourceCode()).append("\n");
		assertEquals(testComps.toString(), testcomponents.getStoredTestComponents());
	}

	/**
	 * test the isTransferObjTestflowTransForParentProject with right project.
	 */
	@Test
	public void isTransferObjTestflowTransForParentProjectTest() {
		initializeSpecialTestFlowAndProject();
		assertTrue(testEditorController.isTransferObjTestflowTransForParentProject(getTransferContainer()));
	}

	/**
	 * test the isTransferObjTestflowTransForParentProject with wrong project.
	 */
	@Test
	public void isTransferObjTestflowTransForParentProjectTestWrongProject() {
		initializeSpecialTestFlowAndProject();
		testProject.setName("NewProject");
		assertFalse(testEditorController.isTransferObjTestflowTransForParentProject(getTransferContainer()));
	}

	/**
	 * test the isTransferObjTestflowTransForParentProject with wrong project.
	 */
	@Test
	public void isTransferObjTestflowTransForParentProjectTestWrongTestProject() {
		initializeSpecialTestFlowAndProject();
		testProject.setName("NewProject");
		assertFalse(testEditorController.isTransferObjTestflowTransForParentProject(getTestRowTransferContainer()));
	}

	/**
	 * test the isTransferObjTestflowTransForParentProject with wrong
	 * TransferObject.
	 */
	@Test
	public void isTransferObjTestflowTransForParentProjectTestWrongTransferContainer() {
		initializeSpecialTestFlowAndProject();
		TestEditorTestDataTransferContainer testEditorTestDataTransferContainer = new TestEditorTestDataTransferContainer();
		testEditorTestDataTransferContainer.setTestProjectName(testProject.getName());
		assertFalse(testEditorController
				.isTransferObjTestflowTransForParentProject(testEditorTestDataTransferContainer));
	}

	/**
	 * test the isTransferObjTestflowTransForParentProject with wrong
	 * TransferObject.
	 */
	@Test
	public void isTransferObjTestflowTransForParentProjectTestWithOutProjectName() {
		initializeSpecialTestFlowAndProject();
		assertFalse(testEditorController
				.isTransferObjTestflowTransForParentProject(new TestEditorTestDataTransferContainer()));
	}

	/**
	 * test the pasteStoredTestComponents method.
	 */
	@Test
	public void pasteStoredTestComponentsTest() {
		initializeSpecialTestFlowAndProject();
		int sizeOfTestFlow = testFlowForTest.getSize() + 1;
		testEditorController.pasteStoredTestComponents(1, false, getTransferContainer());
		assertEquals(sizeOfTestFlow, testFlowForTest.getSize());
	}

	/**
	 * test the pasteStoredTestComponents method.
	 */
	@Test
	public void pasteStoredWrongTestComponentsTest() {
		initializeSpecialTestFlowAndProject();
		int sizeOfTestFlow = testFlowForTest.getSize();
		TestEditorTestFlowTransferContainer transferContainer = getTransferContainer();
		transferContainer.setTestProjectName("Other_Project");
		testEditorController.pasteStoredTestComponents(1, true, transferContainer);
		assertEquals(sizeOfTestFlow, testFlowForTest.getSize());
	}

	/**
	 * test the pasteStoredTestComponents method.
	 */
	@Test
	public void pasteStoredTestComponentsAtTheEndTest() {
		initializeSpecialTestFlowAndProject();
		int sizeOfTestFlow = testFlowForTest.getSize() + 1;
		testEditorController.pasteStoredTestComponents(sizeOfTestFlow, true, getTransferContainer());
		assertEquals(sizeOfTestFlow, testFlowForTest.getSize());
	}

	/**
	 * test the moveRow method.
	 */
	@Test
	public void moveRowTest() {
		initializeSpecialTestFlowAndProject();
		testEditorController.moveRow(1, 2, 3, false);
		List<TestComponent> testComponents = testFlowForTest.getTestComponents();
		assertEquals(descThree, testComponents.get(3));
	}

	/**
	 * test the moveRow method destination is less than zero.
	 */
	@Test
	public void moveRowTestLessThanZero() {
		initializeSpecialTestFlowAndProject();
		testEditorController.moveRow(1, 2, -3, false);
		List<TestComponent> testComponents = testFlowForTest.getTestComponents();
		assertEquals(descThree, testComponents.get(2));
	}

	/**
	 * test the moveRow method destination is less than the source.
	 */
	@Test
	public void moveRowBeforeSourceTest() {
		initializeSpecialTestFlowAndProject();
		testEditorController.moveRow(3, 3, 0, true);
		List<TestComponent> testComponents = testFlowForTest.getTestComponents();
		assertEquals(descFour, testComponents.get(0));
	}

	/**
	 * test the moveRow method destination is less than the source.
	 */
	@Test
	public void moveRowBeforeSourceAfterFirstLineTest() {
		initializeSpecialTestFlowAndProject();
		testEditorController.moveRow(3, 3, 0, false);
		List<TestComponent> testComponents = testFlowForTest.getTestComponents();
		assertEquals(descFour, testComponents.get(1));
	}

	/**
	 * test the getChangePosition method.
	 */
	@Test
	public void getChangePositionTest() {
		initializeSpecialTestFlowAndProject();
		assertEquals(1, testEditorController.getChangePosition(false));
	}

	/**
	 * test the getChangePosition method.
	 */

	@Test
	public void getChangePositionTestinEmptyTestFlow() {
		initializeSpecialTestFlowAndProject();
		testFlowForTest.setTestComponents(new ArrayList<TestComponent>());
		assertEquals(1, testEditorController.getChangePosition(false));
	}

	/**
	 * test the getChangePosition method.
	 */

	@Test
	public void getChangePositionTestWithAddModeAndInsertBeforeFalse() {
		initializeSpecialTestFlowAndProject();
		testEditorController.getTestEditViewArea().setInsertBefore(false);
		assertEquals(2, testEditorController.getChangePosition(true));
	}

	/**
	 * test the getScenarioByFullName method.
	 * 
	 * @throws SystemException
	 *             by searching the TestScenario
	 */
	@Test
	public void getScenarioByFullNameTest() throws SystemException {
		initializeSpecialTestFlowAndProject();
		String sceanrioName = "MyScenario";
		assertTrue(testEditorController.getScenarioByFullName(sceanrioName).getName().equalsIgnoreCase(sceanrioName));
	}

	/**
	 * runs the method refreshStyledText.
	 */
	@Test
	public void refreshStyledTextTest() {
		initializeSpecialTestFlowAndProject();
		assertFalse(testEditorController.getRefreshed());
		testEditorController.refreshStyledText();
		assertTrue(testEditorController.getRefreshed());
	}

	/**
	 * test the setUnparsedActionGroup method.
	 */
	@Test
	public void setUnparsedActionGroupTest() {
		initializeSpecialTestFlowAndProject();
		List<String> inputTexts = new ArrayList<String>();
		String input = "|123|abc|";
		inputTexts.add(input);
		testEditorController.setUnparsedActionGroup("Browser", "Navigation", inputTexts, 0);
		String[] splits = ((TestActionGroup) testFlowForTest.getTestComponents().get(0)).getActionLines().get(0)
				.getTexts().get(0).split("\n");
		String compareString = splits[2];
		assertEquals(input, compareString);
	}

	/**
	 * runs the method setActionGroup. No explicit test.
	 */
	@Test
	public void setActionGroupTest() {
		initializeSpecialTestFlowAndProject();
		ArrayList<String> inputLineParts = new ArrayList<String>();
		inputLineParts.add("Öffne Seite:");
		inputLineParts.add("http://www.testeditor.org");
		ArrayList<Argument> arguments = new ArrayList<Argument>();
		arguments.add(new Argument(null, "http://www.testeditor.org"));
		TestComponent testComponent = testFlowForTest.getTestComponents().get(0);
		int size = testFlowForTest.getTestComponents().size();
		testEditorController.setActionGroup("Browser", "navigate to page", inputLineParts, arguments, 0, true);
		assertNotEquals(testComponent, testFlowForTest.getTestComponents().get(0));
		assertEquals(size, testFlowForTest.getTestComponents().size());
	}

	/**
	 * runs the method setActionGroup. No explicit test.
	 */
	@Test
	public void setActionGroupTestAddMode() {
		initializeSpecialTestFlowAndProject();
		ArrayList<String> inputLineParts = new ArrayList<String>();
		inputLineParts.add("Öffne Seite:");
		inputLineParts.add("http://www.testeditor.org");
		ArrayList<Argument> arguments = new ArrayList<Argument>();
		arguments.add(new Argument(null, "http://www.testeditor.org"));
		TestComponent testComponent = testFlowForTest.getTestComponents().get(0);
		int size = testFlowForTest.getTestComponents().size();
		testEditorController.setActionGroup("Browser", "navigate to page", inputLineParts, arguments, 0, false);
		assertNotEquals(testComponent, testFlowForTest.getTestComponents().get(0));
		assertEquals(testComponent, testFlowForTest.getTestComponents().get(1));
		assertEquals(size + 1, testFlowForTest.getTestComponents().size());
	}

	/**
	 * tests the cleanupAndCloseInputAreas method.
	 */
	@Test
	public void cleanupAndCloseInputAreasTest() {
		initializeSpecialTestFlowAndProject();
		testEditorController.setScenarioTreeCleaned(false);
		testEditorController.cleanupAndCloseInputAreas();
		assertTrue(testEditorController.isScenarioTreeCleaned());
	}

	/**
	 * test the setTestScenarioParameterTable method.
	 */
	@Test
	public void setTestScenarioParameterTableTest() {
		initializeSpecialTestFlowAndProject();
		TestScenarioParameterTable testScenarioParameterTableOne = new TestScenarioParameterTable();
		testScenarioParameterTableOne.setTitle("one");
		int beforeAddingTheParamTable = testEditorController.getTestFlowSize();
		testEditorController.setTestScenarioParameterTable(testScenarioParameterTableOne, 1, false);
		int afterAddingTheParamTable = testEditorController.getTestFlowSize();
		assertEquals("one", ((TestScenarioParameterTable) testFlowForTest.getTestComponents().get(1)).getTitle());
		assertEquals(beforeAddingTheParamTable + 1, afterAddingTheParamTable);
	}

	/**
	 * test the setTestScenarioParameterTable method.
	 */
	@Test
	public void secondSetTestScenarioParameterTableTest() {
		initializeSpecialTestFlowAndProject();
		TestScenarioParameterTable testScenarioParameterTableOne = new TestScenarioParameterTable();
		testScenarioParameterTableOne.setTitle("one");
		int beforeAddingTheParamTable = testEditorController.getTestFlowSize();
		testEditorController.setTestScenarioParameterTable(testScenarioParameterTableOne, 1, true);
		int afterAddingTheParamTable = testEditorController.getTestFlowSize();
		assertEquals("one", ((TestScenarioParameterTable) testFlowForTest.getTestComponents().get(1)).getTitle());
		assertEquals(beforeAddingTheParamTable, afterAddingTheParamTable);
	}

	/**
	 * test the setTestScenarioParameterTable method.
	 */
	@Test
	public void thirdSetTestScenarioParameterTableTest() {
		initializeSpecialTestFlowAndProject();
		TestScenarioParameterTable testScenarioParameterTableOne = new TestScenarioParameterTable();
		testScenarioParameterTableOne.setTitle("one");
		int beforeAddingTheParamTable = testEditorController.getTestFlowSize();
		testEditorController.setTestScenarioParameterTable(testScenarioParameterTableOne, -1, true);
		int afterAddingTheParamTable = testEditorController.getTestFlowSize();
		assertEquals("one",
				((TestScenarioParameterTable) testFlowForTest.getTestComponents().get(beforeAddingTheParamTable))
						.getTitle());
		assertEquals(beforeAddingTheParamTable + 1, afterAddingTheParamTable);
	}

	/**
	 * test the setTestScenarioParameterTable method.
	 */
	@Test
	public void fouthSetTestScenarioParameterTableTest() {
		initializeSpecialTestFlowAndProject();
		TestScenarioParameterTable testScenarioParameterTableOne = new TestScenarioParameterTable();
		testScenarioParameterTableOne.setTitle("one");
		int beforeAddingTheParamTable = testEditorController.getTestFlowSize();
		testEditorController.setTestScenarioParameterTable(testScenarioParameterTableOne, -1, false);
		int afterAddingTheParamTable = testEditorController.getTestFlowSize();
		assertEquals("one",
				((TestScenarioParameterTable) testFlowForTest.getTestComponents().get(beforeAddingTheParamTable))
						.getTitle());
		assertEquals(beforeAddingTheParamTable + 1, afterAddingTheParamTable);
	}

	/**
	 * test the isLineInTestCase method.
	 */
	@Test
	public void isLineInTestFlowTest() {
		initializeSpecialTestFlowAndProject();
		assertFalse(testEditorController.isLineInTestFlow(20));
		assertTrue(testEditorController.isLineInTestFlow(2));
		testFlowForTest.getTestComponents().clear();
		assertFalse(testEditorController.isLineInTestFlow(0));
	}

	/**
	 * test the markSelectedLineInView method.
	 */
	@Test
	public void markSelectedLineInViewTest() {
		initializeSpecialTestFlowAndProject();
		int markedLine = 3;
		testEditorController.setSelectedLineInView(markedLine);
		assertEquals(markedLine, testEditorController.getSelectedLineInView());
	}

	/**
	 * test the setTestScenarioParameterTable method.
	 */
	@Test
	public void fifthSetTestScenarioParameterTableTest() {
		initializeSpecialTestFlowAndProject();
		TestScenarioParameterTable testScenarioParameterTableOne = new TestScenarioParameterTable();
		testScenarioParameterTableOne.setTitle("one");
		testFlowForTest.getTestComponents().clear();
		int beforeAddingTheParamTable = testEditorController.getTestFlowSize();
		testEditorController.setTestScenarioParameterTable(testScenarioParameterTableOne, 10, false);
		int afterAddingTheParamTable = testEditorController.getTestFlowSize();
		assertEquals("one",
				((TestScenarioParameterTable) testFlowForTest.getTestComponents().get(beforeAddingTheParamTable))
						.getTitle());
		assertEquals(beforeAddingTheParamTable + 1, afterAddingTheParamTable);
	}

	/**
	 * test the cutText method.
	 */
	@Test
	public void cutText() {
		initalizeForTest();
		events.clear();
		testEditorController.cutText();
		assertTrue(events.contains(TestEditorUIEventConstants.EDIT_CONTEXTMENU_CNTRL_X));
	}

	/**
	 * test the copyText method.
	 */
	@Test
	public void copyText() {
		initalizeForTest();
		events.clear();
		testEditorController.copyText();
		assertTrue(events.contains(TestEditorUIEventConstants.EDIT_CONTEXTMENU_CNTRL_C));
	}

	/**
	 * test the pasteComponents() method.
	 */
	@Test
	public void pasteComponents() {
		initalizeForTest();
		events.clear();
		testEditorController.pasteComponents();
		assertTrue(events.contains(TestEditorUIEventConstants.EDIT_CONTEXTMENU_CNTRL_V));
	}

	/**
	 * test the isEmptyLineInView method.
	 */
	@Test
	public void isLineEmptyTest() {
		assertTrue(testEditorController.isEmptyLineInView(0));
	}

	/**
	 * test the setDescriptionControllerForPopupEditing method.
	 */
	@Test
	public void setDescriptionControllerForPopupEditingTest() {
		TestEditorDescriptionInputController descriptionController = testEditorController.getDescriptionController();
		testEditorController.setDescriptionControllerForPopupEditing(new TestEditorDescriptionInputController());
		assertNotEquals(descriptionController, testEditorController.getDescriptionController());
		testEditorController.removePopupEditingControllers();
		assertEquals(descriptionController, testEditorController.getDescriptionController());
	}

	/**
	 * test the closePopupDialog method.
	 */
	@Test
	public void closePopupTest() {
		testEditorController.setPopupDialogOpend();
		testEditorController.closePopupDialog();
		assertTrue(testEditorController.isPopupClosed());
	}

	/**
	 * test the linkTestExplorerWithEditor method.
	 */
	@Test
	public void linkTestExplorerWithEditorTest() {
		initalizeForTest();
		events.clear();
		testEditorController.setTestFlow(testFlowForTest);
		assertTrue(events.contains(TestEditorUIEventConstants.ACTIVE_TESTFLOW_EDITOR_CHANGED));
		assertEquals(testStructureFromEvent, testFlowForTest);

	}

	/**
	 * test the setActionControllerForPopupEditing method.
	 */
	@Test
	public void setActionControllerForPopupEditingTest() {
		TestEditorActionInputController actionController = testEditorController.getActionInputController();
		testEditorController.setActionControllerForPopupEditing(new TestEditorActionInputController());
		assertNotEquals(actionController, testEditorController.getActionInputController());
		testEditorController.removePopupEditingControllers();
		assertEquals(actionController, testEditorController.getActionInputController());
	}

	/**
	 * test the isLibraryErrorLessLoaded method.
	 */
	@Test
	public void isLibraryErrorLessLoadedTest() {
		initializeSpecialTestFlowAndProject();
		LibraryLoadingStatus libraryLoadingStatus = new LibraryLoadingStatus();
		libraryLoadingStatus.setLoaded(true);
		libraryLoadingStatus.setErrorLessLoaded(true);
		testProject.setTestProjectConfig(new TestProjectConfig());
		TestEditorPlugInService service = ServiceLookUpForTest.getService(TestEditorPlugInService.class);
		Properties properties = new Properties();
		properties.put(TestEditorPlugInService.LIBRARY_ID, "org.testeditor.xmllibrary");
		properties.put(TestProjectService.VERSION_TAG, TestProjectService.VERSION1_2);
		properties.put("library.xmllibrary.actiongroup",
				new StringBuffer(new File("").getAbsolutePath()).append(File.separatorChar).append("X").toString());
		properties.put("library.xmllibrary.technicalbindings",
				new StringBuffer(new File("").getAbsolutePath()).append(File.separatorChar).append("Y").toString());
		LibraryConfigurationServicePlugIn libraryConfigurationService = service
				.getLibraryConfigurationServiceFor("org.testeditor.xmllibrary");
		ProjectLibraryConfig libraryConfig = libraryConfigurationService.createProjectLibraryConfigFrom(properties);

		testProject.getTestProjectConfig().setProjectLibraryConfig(libraryConfig);
		testProject.getTestProjectConfig().setLibraryLoadingStatus(libraryLoadingStatus);
		assertTrue(testEditorController.isLibraryErrorLessLoaded());
		libraryLoadingStatus.setErrorLessLoaded(false);
		assertFalse(testEditorController.isLibraryErrorLessLoaded());
	}

	/**
	 * test the closeChildOfDeletedTestStructure method for the same
	 * teststructure.
	 */
	@Test
	public void closeChildOfDeletedTestStructureTest() {
		initalizeForTest();
		mPartAdapter.setDirty(true);
		assertTrue(mPartAdapter.isDirty());
		testEditorController.closeChildOfDeletedTestStructure(testFlowForTest.getFullName());
		assertFalse(mPartAdapter.isDirty());
	}

	/**
	 * test the closeChildOfDeletedTestStructure method for the parent
	 * structures. teststructure.
	 */
	@Test
	public void closeChildOfDeletedTestStructureParentTest() {
		initalizeForTest();
		mPartAdapter.setDirty(true);
		assertTrue(mPartAdapter.isDirty());
		testEditorController.closeChildOfDeletedTestStructure(testFlowForTest.getParent().getFullName());
		assertFalse(mPartAdapter.isDirty());
	}

	/**
	 * test the closeChildOfDeletedTestStructure method for the testProject as
	 * parameter.
	 */
	@Test
	public void closeChildOfDeletedTestStructureWithTestProjectTest() {
		initializeSpecialTestFlowAndProject();
		mPartAdapter.setDirty(true);
		assertTrue(mPartAdapter.isDirty());
		testEditorController.closeChildOfDeletedTestStructure(testFlowForTest.getFullName());
		assertFalse(mPartAdapter.isDirty());
	}

	/**
	 * this test calls the method objectUpdatetByTeamshare without any assert.
	 */
	@Test
	public void handleLibraryLoadedEventTest() {
		initializeSpecialTestFlowAndProject();
		testEditorController.handleLibraryLoadedEvent(testProject);
	}

	/**
	 * test the setDirtyByEvent method.
	 */
	@Test
	public void setDirtyByEventTest() {
		initializeSpecialTestFlowAndProject();
		testEditorController.setDirtyByEvent(new TestCase());
		assertFalse(mPartAdapter.isDirty());
		testEditorController.setDirtyByEvent(testFlowForTest);
		assertTrue(mPartAdapter.isDirty());
	}

	/**
	 * test the setPartOnTop method.
	 */
	@Test
	public void setPartOnTopTest() {
		initializeSpecialTestFlowAndProject();
		testFlowForTest.setName("TestFlowForTest");
		mPartAdapter.setOnTop(false);
		testEditorController.setPartOnTop(null);
		assertFalse(mPartAdapter.isOnTop());
		TestCase testCase = new TestCase();
		testProject.addChild(testCase);
		testCase.setName("newTestCase");
		testEditorController.setPartOnTop(testCase);
		assertFalse(mPartAdapter.isOnTop());
		testEditorController.setPartOnTop(testFlowForTest);
		assertTrue(mPartAdapter.isOnTop());
	}

	/**
	 * test the setFoscu method.
	 */
	@Test
	public void setFocusTest() {
		initializeSpecialTestFlowAndProject();
		assertFalse(testEditorController.hasFocus());
		testEditorController.setFocus(shell);
		assertTrue(testEditorController.hasFocus());
	}

	/**
	 * test the refreshTestComponents method.
	 */
	@Test
	public void refreshTestComponentsTest() {
		initalizeForTest();
		testEditorController.refreshTestComponents(testFlowForTest);
		assertEquals(testStructureContentServiceAdapter.getNewDescription(), testFlowForTest.getTestComponents().get(0)
				.getTexts().get(0));
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testReloadAndRefresh() {
		final Map<String, Boolean> testCtrl = new HashMap<String, Boolean>();
		testCtrl.put("dirty", false);
		MPartAdapter partAdapter = new MPartAdapter() {
			@Override
			public boolean isDirty() {
				return testCtrl.get("dirty");
			}
		};
		TestEditorController ctrl = new TestEditorController(partAdapter) {
			@Override
			protected boolean userWantsToReplaceContent() {
				return testCtrl.get("userSays");
			}

			@Override
			public String getInvalidChars() {
				return null;
			}

			@Override
			protected List<TestDescription> createDescriptionsArray(List<String> newLines) {
				return null;
			}

			@Override
			public void setActionGroup(String mask, String actionName, ArrayList<String> inputLineParts,
					ArrayList<Argument> arguments, int selectedLine, boolean changeMode) {

			}

			@Override
			protected TestActionGroup createUnparsedActionLine(String mask, List<String> inputTexts) {
				return null;
			}

			@Override
			protected void loadAndRerender() {
				testCtrl.put("loadRender", true);
			}

			@Override
			protected String getId() {
				return null;
			}

		};
		ctrl.reloadAndRefresh("");
		assertTrue(testCtrl.containsKey("loadRender"));
		testCtrl.remove("loadRender");
		testCtrl.put("userSays", true);
		testCtrl.put("dirty", true);
		ctrl.reloadAndRefresh("");
		assertTrue(testCtrl.containsKey("loadRender"));
		testCtrl.remove("loadRender");
		testCtrl.put("userSays", false);
		testCtrl.put("dirty", true);
		ctrl.reloadAndRefresh("");
		assertFalse(testCtrl.containsKey("loadRender"));
	}

	/**
	 * 
	 * @return more desceriptionText to add in the testflow.
	 */
	private List<String> moreDescriptionTexts() {
		List<String> descriptionText = new ArrayList<String>();
		descriptionText.add("line 1");
		descriptionText.add("line 2");
		descriptionText.add("line 3");
		descriptionText.add("line 4");
		descriptionText.add("line 5");
		descriptionText.add("line 6");
		return descriptionText;
	}

	/**
	 * setup.
	 */
	@Before
	public void setup() {
		shell = new Shell();
		IEclipseContext context = EclipseContextFactory.getServiceContext(FrameworkUtil.getBundle(
				TestEditorViewKeyHandler.class).getBundleContext());
		context.set(EPartService.class, new PartServiceAdapter());
		context.set(TestEditorTranslationService.class, null);
		testStructureContentServiceAdapter = new TestStructureContentServiceAdapter();
		FrameworkUtil.getBundle(getClass()).getBundleContext()
				.registerService(TestStructureContentService.class.getName(), testStructureContentServiceAdapter, null);
		context.set(TestStructureContentService.class, testStructureContentServiceAdapter);
		context.set(ActionGroupService.class, getActionGroupServiceMock());
		context.set(TestScenarioService.class, new TestScenarioServiceMock());
		eventBroker = new EventBroker();
		context.set(IEventBroker.class, eventBroker);
		context.set(Composite.class, new Composite(shell, SWT.NONE));
		context.set(Shell.class, shell);
		mPartAdapter = new MPartAdapter();
		context.set(MPart.class, mPartAdapter);
		testEditorController = ContextInjectionFactory.make(TestEditorControllerMock.class, context);
		eventBroker.subscribe("Edit/*", new EventHandler() {

			@Override
			public void handleEvent(org.osgi.service.event.Event arg0) {
				events.add(arg0.getTopic());
			}
		});
		eventBroker.subscribe("testflow/*", new EventHandler() {

			@Override
			public void handleEvent(org.osgi.service.event.Event arg0) {
				events.add(arg0.getTopic());
				testStructureFromEvent = (TestStructure) arg0.getProperty(IEventBroker.DATA);
			}
		});
	}

	/**
	 * 
	 * @return mockup for the ActionGroupService.
	 */
	private ActionGroupService getActionGroupServiceMock() {
		return new ActionGroupServiceAdapter();
	}

	/**
	 * 
	 * @return a mockup for TestEditorTestFlowTransferContainer.
	 */
	private TestEditorTestFlowTransferContainer getTransferContainer() {
		TestEditorTestFlowTransferContainer testEditorTestFlowDataTransferContainer = new TestEditorTestFlowTransferContainer();
		testEditorTestFlowDataTransferContainer.setStoredTestComponents("Dies sind die TestDaten");
		testEditorTestFlowDataTransferContainer.setTestProjectName("Project_one");
		return testEditorTestFlowDataTransferContainer;
	}

	/**
	 * 
	 * @return a mockup for TestEditorTestRowTransferContainer.
	 */
	private TestEditorTestRowTransferContainer getTestRowTransferContainer() {
		TestEditorTestRowTransferContainer container = new TestEditorTestRowTransferContainer();
		container.setTestProjectName("Project_one");
		return container;
	}

	/**
	 * Destroying Shell.
	 */
	@After
	public void tearDown() {
		shell.dispose();
	}
}
