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
package org.testeditor.core.model.teststructure;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Test;
import org.testeditor.core.exceptions.CorrruptLibraryException;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.model.action.Action;
import org.testeditor.core.model.action.ActionElement;
import org.testeditor.core.model.action.ActionElementType;
import org.testeditor.core.model.action.Argument;
import org.testeditor.core.model.action.TechnicalBindingType;

/**
 * 
 * test for the testcase.
 * 
 */
public class TestCaseTest {

	private String line1 = "Eine Test Beschreibung";
	private String line2 = "Zweite Test Beschreibung";
	private String line3 = "Dritte Test Beschreibung";

	/**
	 * test add test component.
	 */
	@Test
	public void addTestComponentTest() {
		TestCase testCase = new TestCase();
		assertEquals(testCase.getSize(), 0);
		TestDescription testDescription = new TestDescriptionTestCase(line1);
		testCase.addTestComponent(testDescription);
		assertEquals(testCase.getSize(), 1);
		assertEquals(testCase.getLine(0).getSourceCode(), testDescription.getSourceCode());
	}

	/**
	 * test add three test components.
	 */
	@Test
	public void addThreeTestComponentsTest() {
		TestCase testCase = everyTest();
		assertEquals(testCase.getSize(), 3);
		assertEquals(testCase.getLine(1).getTexts().get(0), line2);
	}

	/**
	 * test add three test components and gets the source code.
	 */
	@Test
	public void getSourceCodeTest() {
		TestCase testCase = everyTest();
		assertEquals(testCase.getSize(), 3);
		assertEquals(testCase.getSourceCode().substring(0, line1.length()), testCase.getLine(0).getTexts().get(0));
	}

	/**
	 * test add three test components and remove test component.
	 */
	@Test
	public void removeTestComponentTest() {
		TestCase testCase = everyTest();
		TestComponent comp = testCase.getLine(0);
		assertEquals(comp.getTexts().get(0), line1);
		testCase.remove(comp);
		assertEquals(testCase.getSize(), 2);
		assertEquals(testCase.getSourceCode().substring(0, line2.length()), line2);
	}

	/**
	 * internal method for all junit-tests.
	 * 
	 * @return a default {@link TestCase}
	 */
	private TestCase everyTest() {
		TestCase testCase = new TestCase();
		assertEquals(testCase.getSize(), 0);
		ArrayList<TestComponent> testComponents = new ArrayList<TestComponent>();
		testComponents.add(new TestDescriptionTestCase(line1));
		testComponents.add(new TestDescriptionTestCase(line2));
		testComponents.add(new TestDescriptionTestCase(line3));
		testCase.addTestComponentsAtPos(0, testComponents);
		assertEquals(testCase.getSize(), 3);
		return testCase;
	}

	/**
	 * test add three test components and remove test component and at least
	 * adds a testcomponent at pos 0.
	 */
	@Test
	public void addTestComponentAtTest() {
		TestCase testCase = everyTest();
		TestComponent comp = testCase.getLine(0);
		assertEquals(comp.getTexts().get(0), line1);
		testCase.remove(comp);
		assertEquals(testCase.getSize(), 2);

		assertEquals(testCase.getSourceCode().substring(0, line2.length()), line2);
		String line4 = "Vierte Test Beschreibung";
		comp = new TestDescriptionTestCase(line4);
		testCase.addTestComponent(0, comp);
		assertEquals(testCase.getSourceCode().substring(0, line4.length()), line4);
		assertEquals(testCase.getSize(), 3);

	}

	/**
	 * test add three test components and remove test component and at least
	 * sets a testcomponent at pos 0.
	 */
	@Test
	public void setTestComponentTest() {
		TestCase testCase = everyTest();
		TestComponent comp = testCase.getLine(0);
		assertEquals(comp.getTexts().get(0), line1);
		testCase.remove(comp);
		assertEquals(testCase.getSize(), 2);

		assertEquals(testCase.getSourceCode().substring(0, line2.length()), line2);
		String line4 = "Vierte Test Beschreibung";
		comp = new TestDescriptionTestCase(line4);
		testCase.setLine(0, comp);
		assertEquals(testCase.getSourceCode().substring(0, line4.length()), line4);
		assertEquals(testCase.getSize(), 2);

	}

	/**
	 * test add three test components and remove test component and at least
	 * removes the line 1.
	 */
	@Test
	public void removeLineTest() {
		TestCase testCase = everyTest();
		assertEquals(testCase.getSourceCode().substring(0, line1.length()), testCase.getLine(0).getTexts().get(0));
		TestComponent comp = testCase.removeLine(0);
		assertEquals(testCase.getSize(), 2);
		assertEquals(testCase.getSourceCode().substring(0, line2.length()), line2);
		assertEquals(testCase.getSize(), 2);
		assertEquals(comp.getTexts().get(0), line1);
	}

	/**
	 * test add three test components and remove test component and at least
	 * removes the line 1.
	 */
	@Test
	public void removeNotExistLineTest() {
		TestCase testCase = everyTest();
		assertEquals(testCase.getSourceCode().substring(0, line1.length()), testCase.getLine(0).getTexts().get(0));
		TestComponent comp = testCase.removeLine(4);
		assertEquals(null, comp);
	}

	/**
	 * test the getName method.
	 */
	@Test
	public void getName() {
		TestCase testCase = new TestCase();
		assertEquals(testCase.getTypeName(), "test");
	}

	/**
	 * test removeLines method.
	 */
	@Test
	public void removeLinesTest() {
		TestCase testCase = everyTest();

		String line5 = "5. Test Beschreibung";
		testCase.addTestComponent(new TestDescriptionTestCase("4. Test Beschreibung"));
		testCase.addTestComponent(new TestDescriptionTestCase(line5));
		testCase.addTestComponent(new TestDescriptionTestCase("6. Test Beschreibung"));
		assertEquals(testCase.getSize(), 6);
		assertEquals(testCase.getSourceCode().substring(0, line1.length()), testCase.getLine(0).getTexts().get(0));
		ArrayList<TestComponent> comps = testCase.removeLines(0, 3);
		assertEquals(testCase.getSize(), 2);
		assertEquals(testCase.getSourceCode().substring(0, line5.length()), line5);
		assertEquals(testCase.getSize(), 2);
		assertEquals(comps.get(0).getTexts().get(0), line1);
	}

	/**
	 * test getSource for ActionGroup and TestScenarioParameterTable in a
	 * TestCase.
	 * 
	 * @throws CorrruptLibraryException
	 *             exception will be thrown if error in library was found
	 * @throws SystemException
	 *             SystemException
	 */
	@Test
	public void addActionGroupParameterTable() throws CorrruptLibraryException, SystemException {
		TestCase testCase = new TestCase();
		testCase.addTestComponent(new TestDescriptionTestCase(line1));
		TestActionGroupTestCase testActionGroup = new TestActionGroupTestCase();
		testActionGroup.setActionGroupName("Header");
		ActionElement actionElement = new ActionElement(1, ActionElementType.ACTION_NAME, "Action", null);
		ArrayList<ActionElement> actionElmList = new ArrayList<ActionElement>();
		actionElmList.add(actionElement);
		TechnicalBindingType technicalBindingType = new TechnicalBindingType("A", "A", actionElmList, 1);
		ArrayList<Argument> arguments = new ArrayList<Argument>();
		arguments.add(new Argument("Password", "Password"));
		arguments.add(new Argument("Ufhat869#+?", "Ufhat869#+?"));
		Action action = new Action("Action-A", arguments, null, technicalBindingType, null);
		testActionGroup.addActionLine(action);
		testCase.addTestComponent(testActionGroup);
		TestScenarioParameterTable testScenarioParameterTable = new TestScenarioParameterTable();
		testScenarioParameterTable.addParameterLine("|A1|B1|C1|");
		testScenarioParameterTable.setTitle("Scenario");
		testCase.addTestComponent(testScenarioParameterTable);
		String sourceCode = testCase.getSourceCode();
		String[] splits = sourceCode.split("\\n");
		assertTrue(splits[0].equalsIgnoreCase("Eine Test Beschreibung"));
		assertTrue(splits[1].equalsIgnoreCase("# Maske: Header"));
		assertTrue(splits[2].equalsIgnoreCase("-!|script|"));
		assertTrue(splits[6].equalsIgnoreCase("|A1|B1|C1|"));
	}

	/**
	 * test getSource for ActionGroup and TestScenarioParameterTable in a
	 * TestScenario.
	 * 
	 * @throws CorrruptLibraryException
	 *             exception will be thrown if error in library was found
	 * @throws SystemException
	 *             SystemException
	 */

	@Test
	public void addActionGroupParameterTableInScenario() throws CorrruptLibraryException, SystemException {
		TestScenario testScenario = new TestScenario();
		testScenario.addTestComponent(new TestDescriptionTestScenario(line1));
		TestActionGroupTestScenario testActionGroup = new TestActionGroupTestScenario();
		testActionGroup.setActionGroupName("Header");
		ActionElement actionElement = new ActionElement(1, ActionElementType.ACTION_NAME, "Action", null);
		ArrayList<ActionElement> actionElmList = new ArrayList<ActionElement>();
		actionElmList.add(actionElement);
		TechnicalBindingType technicalBindingType = new TechnicalBindingType("A", "A", actionElmList, 1);
		ArrayList<Argument> arguments = new ArrayList<Argument>();
		arguments.add(new Argument("Password", "Password"));
		arguments.add(new Argument("Ufhat869#+?", "Ufhat869#+?"));
		Action action = new Action("Action-A", arguments, null, technicalBindingType, null);
		testActionGroup.addActionLine(action);
		testScenario.addTestComponent(testActionGroup);
		TestScenarioParameterTable testScenarioParameterTable = new TestScenarioParameterTable();
		testScenarioParameterTable.addParameterLine("|A1|B1|C1|");
		testScenarioParameterTable.setTitle("Scenario");
		testScenario.addTestComponent(testScenarioParameterTable);
		String sourceCode = testScenario.getSourceCode();
		String[] splits = sourceCode.split("\\n");
		assertTrue(splits[0].equalsIgnoreCase("|note|Description: Eine Test Beschreibung|"));
		assertTrue(splits[1].equalsIgnoreCase("|note| Maske: Header|"));
		assertTrue(splits[2].equalsIgnoreCase("|Password|"));
		assertTrue(splits[4].equalsIgnoreCase("|Scenario|"));
	}

	/**
	 * Tests the method
	 * {@link TestScenarioParameterTable#addParameterLine(String)}.
	 * 
	 * @throws SystemException
	 *             SystemException
	 */
	@Test
	public void addActionGroupParameterTableInScenarioWithDataRowCountLessThanHeaderRowCount() throws SystemException {
		TestScenario testScenario = new TestScenario();

		TestScenarioParameterTable testScenarioParameterTable = new TestScenarioParameterTable();

		// Header row
		testScenarioParameterTable.addParameterLine("|A1|B1|C1|");
		// data row
		testScenarioParameterTable.addParameterLine("|X|Y|Z|Z|");

		testScenarioParameterTable.setTitle("Scenario");
		testScenario.addTestComponent(testScenarioParameterTable);
		String sourceCode = testScenario.getSourceCode();

		assertEquals("|note|scenario|\n|Scenario;|X|Y|Z|Z|\n", sourceCode);
		assertTrue(testScenarioParameterTable.getTestDataEvaluationReturnList() != null);
		assertFalse(testScenarioParameterTable.getTestDataEvaluationReturnList()
				.isDataRowColumnCountEqualsHeaderRowColumnCount());

	}

	/**
	 * Tests the method
	 * {@link TestScenarioParameterTable#addParameterLine(String)}.
	 * 
	 * @throws SystemException
	 *             SystemException
	 */
	@Test
	public void addActionGroupParameterTableInScenarioWithDataRowCountEqualsHeaderRowCount() throws SystemException {
		TestScenario testScenario = new TestScenario();

		TestScenarioParameterTable testScenarioParameterTable = new TestScenarioParameterTable();

		// Header row
		testScenarioParameterTable.addParameterLine("|A1|B1|C1|");
		// data row
		testScenarioParameterTable.addParameterLine("|X|Y||");

		testScenarioParameterTable.setTitle("Scenario");
		testScenario.addTestComponent(testScenarioParameterTable);
		String sourceCode = testScenario.getSourceCode();

		assertEquals("|note|scenario|\n|Scenario;|X|Y||\n", sourceCode);
		assertTrue(testScenarioParameterTable.getTestDataEvaluationReturnList() == null);
	}

	/**
	 * Tests the method
	 * {@link TestScenarioParameterTable#addParameterLine(String)}.
	 * 
	 * @throws SystemException
	 *             SystemException
	 */
	@Test
	public void addActionGroupParameterTableInScenarioWithDataRowCountHigherThanHeaderRowCount()
			throws SystemException {
		TestScenario testScenario = new TestScenario();
		TestScenarioParameterTable testScenarioParameterTable = new TestScenarioParameterTable();

		// Header row
		testScenarioParameterTable.addParameterLine("|A1|B1|C1|");
		// data row
		testScenarioParameterTable.addParameterLine("|X|Y|||");

		testScenarioParameterTable.setTitle("Scenario");
		testScenario.addTestComponent(testScenarioParameterTable);
		String sourceCode = testScenario.getSourceCode();

		assertEquals("|note|scenario|\n|Scenario;|X|Y|||\n", sourceCode);
		assertTrue(testScenarioParameterTable.getTestDataEvaluationReturnList() != null);
		assertFalse(testScenarioParameterTable.getTestDataEvaluationReturnList()
				.isDataRowColumnCountEqualsHeaderRowColumnCount());

	}

}
