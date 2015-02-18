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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 
 * Test flow includes the test steps and descriptions for a whole test flow.
 * 
 */
public abstract class TestFlow extends TestStructure {

	private LinkedList<TestComponent> testComponents = new LinkedList<TestComponent>();
	private static final String EMPTYTESTSTRUCTURECOMPONENT = "!contents";

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getSourceCode() {
		StringBuilder sourceCode = new StringBuilder("");
		TestComponent previousComp = null;
		if (testComponents.isEmpty()) {
			return EMPTYTESTSTRUCTURECOMPONENT;
		}
		for (TestComponent testComponent : testComponents) {
			if (testComponent instanceof TestScenarioParameters) {
				sourceCode.append("!|scenario |").append(getName()).append(" _|").append(testComponent.getSourceCode())
						.append("|");
			} else if (testComponent instanceof TestActionGroup && previousComp != null
					&& previousComp instanceof TestActionGroup
					&& ((TestActionGroup) testComponent).isStartOfSourceCodeEqual((TestActionGroup) previousComp)) {
				sourceCode.append(((TestActionGroup) testComponent).getTableSourcecode());
			} else {
				sourceCode.append(testComponent.getSourceCode());

			}
			sourceCode.append("\n");
			previousComp = testComponent;
		}
		return sourceCode.toString();
	}

	/**
	 * Adds a test component to the test case.
	 * 
	 * @param testComponent
	 *            test component
	 */
	public void addTestComponent(TestComponent testComponent) {
		testComponents.add(testComponent);
	}

	/**
	 * Returns the components of this test case (e.g. blocks of action groups
	 * and test description).
	 * 
	 * @return test components
	 */
	public List<TestComponent> getTestComponents() {
		return testComponents;
	}

	/**
	 * Sets the test components for this test case (e.g. blocks of action groups
	 * and test description).
	 * 
	 * @param testComponents
	 *            test components
	 */
	public void setTestComponents(List<TestComponent> testComponents) {
		this.testComponents.clear();
		this.testComponents.addAll(testComponents);
	}

	/**
	 * Removes the test component from the test case.
	 * 
	 * @param testComponent
	 *            to remove from TestCase
	 */
	public void remove(TestComponent testComponent) {
		testComponents.remove(testComponent);
	}

	/**
	 * adds the testComponent at the line lineTo.
	 * 
	 * @param lineTo
	 *            new line position
	 * @param testComponent
	 *            testComponent
	 */
	public void addTestComponent(int lineTo, TestComponent testComponent) {
		testComponents.add(lineTo, testComponent);
	}

	/**
	 * adds the testComponents at the selected line.
	 * 
	 * @param selectedLine
	 *            selectedLine
	 * @param inputlines
	 *            TestComponents
	 */
	public void addTestComponentsAtPos(int selectedLine, List<TestComponent> inputlines) {
		if (testComponents.size() == selectedLine) {
			for (TestComponent line : inputlines) {
				testComponents.add(line);
			}
		} else {
			testComponents.addAll(selectedLine, inputlines);
		}
	}

	/**
	 * remove the selected lines.
	 * 
	 * @param lowerBorder
	 *            lower border
	 * @param upperBorder
	 *            upper border
	 * @return ArrayList<TestComponent>
	 */
	public ArrayList<TestComponent> removeLines(int lowerBorder, int upperBorder) {
		ArrayList<TestComponent> removedComponents = new ArrayList<TestComponent>();

		for (int lineInTestcase = upperBorder; lineInTestcase >= lowerBorder; lineInTestcase--) {
			removedComponents.add(0, removeLine(lineInTestcase));
		}
		return removedComponents;
	}

	/**
	 * returns the size count of the testComponents.
	 * 
	 * @return count(size) of the TestCompents
	 */
	public int getSize() {
		return testComponents.size();
	}

	/**
	 * removes the testcomponent from the linkedList.
	 * 
	 * @param lineFrom
	 *            number of the line
	 * @return the removed TestComponent
	 */
	public TestComponent removeLine(int lineFrom) {
		if (lineFrom >= 0 && lineFrom < getSize()) {
			return testComponents.remove(lineFrom);
		}
		return null;
	}

	/**
	 * set the Testcomponent to the position named in the parameter selected
	 * line.
	 * 
	 * @param selectedLine
	 *            Selected Line
	 * @param testComponent
	 *            the TestComponent
	 */
	public void setLine(int selectedLine, TestComponent testComponent) {
		testComponents.set(selectedLine, testComponent);
	}

	/**
	 * returns the TestComponent at linenumber.
	 * 
	 * @param i
	 *            the linenumber
	 * @return TestComponent
	 */
	public TestComponent getLine(int i) {
		if (getSize() > 0 && getSize() >= i - 1 && i > -1) {
			return testComponents.get(i);
		}
		return null;
	}

	@Override
	public abstract String getTypeName();

	/**
	 * 
	 * @return a new instance of TestDescription.
	 */
	public abstract TestDescription getNewTestDescription();
}
