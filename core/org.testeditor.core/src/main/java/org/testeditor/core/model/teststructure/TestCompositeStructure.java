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
import java.util.List;

/**
 * 
 * Abstract Class for all TestStructures which can contain TestStructures.
 * 
 */
public abstract class TestCompositeStructure extends TestStructure {

	private List<TestStructure> testChildren = new ArrayList<TestStructure>();

	private int childCountInBackend;

	private Runnable lazyLoader;

	/**
	 * this method sets the loader for the children.
	 * 
	 * @param lazyLoader
	 *            lazyloader
	 */
	public void setLazyLoader(Runnable lazyLoader) {
		this.lazyLoader = lazyLoader;
	}

	/**
	 * Returns the children of this suite (that could be sub suites or/and test
	 * cases).
	 * 
	 * @return test children
	 */
	public List<TestStructure> getTestChildren() {
		boolean areAllChildsOfTheBackendLoaded = childCountInBackend != testChildren.size() && lazyLoader != null;
		if (areAllChildsOfTheBackendLoaded) {
			childCountInBackend = 0;
			lazyLoader.run();
		}
		return testChildren;
	}

	/**
	 * Sets the children of this suite (that could be sub suites or/and test
	 * cases).
	 * 
	 * @param testChildren
	 *            test children
	 */
	public void setTestChildren(List<TestStructure> testChildren) {
		for (TestStructure testStructure : testChildren) {
			testStructure.setParent(this);
		}
		setChildCountInBackend(testChildren.size());
		this.testChildren = testChildren;
	}

	/**
	 * Adds a new {@link TestStructure} as child.
	 * 
	 * @param child
	 *            can be a TestStructure TestProjects a no valid Argument.
	 */
	public void addChild(TestStructure child) {
		if (child instanceof TestProject) {
			throw new IllegalArgumentException();
		}
		child.setParent(this);
		if (!testChildren.contains(child)) {
			if (childCountInBackend == testChildren.size()) {
				childCountInBackend++;
			}
			testChildren.add(child);
		}
	}

	/**
	 * Remove the given child from the TestStructure.
	 * 
	 * @param child
	 *            can be a TestStructure TestProjects a no valid Argument.
	 */
	public void removeChild(TestStructure child) {
		if (child instanceof TestProject) {
			throw new IllegalArgumentException();
		}

		if (testChildren.contains(child)) {
			if (childCountInBackend == testChildren.size()) {
				childCountInBackend--;
			}
			testChildren.remove(child);
		}
	}

	/**
	 * Traverse all children and their children to get a List of children of it.
	 * It returns testcases and testsuites with all of their children.
	 * 
	 * @return a List of all tests-structures that are children of this one.
	 */
	public List<TestStructure> getAllTestChildren() {
		List<TestStructure> result = new ArrayList<TestStructure>();
		for (TestStructure testStructure : getTestChildren()) {
			if (testStructure instanceof TestSuite) {
				result.addAll(((TestSuite) testStructure).getAllTestChildren());
			}
			if (!(testStructure instanceof ScenarioSuite)) {
				result.add(testStructure);
			}
		}
		return result;
	}

	/**
	 * Traverse all children and their children to get of children of it. It
	 * returns objects of type {@link TestCase}, {@link TestSuite},
	 * {@link ScenarioSuite}, {@link TestScenario} with all their children.
	 * 
	 * @return a List of all tests-structures that are children of this one.
	 */
	public List<TestStructure> getAllTestChildrenWithScenarios() {
		List<TestStructure> result = new ArrayList<TestStructure>();
		for (TestStructure testStructure : getTestChildren()) {
			if (testStructure instanceof TestSuite || testStructure instanceof ScenarioSuite) {
				result.addAll(((TestCompositeStructure) testStructure).getAllTestChildrenWithScenarios());
			}
			result.add(testStructure);
		}
		return result;
	}

	/**
	 * Sets the child count of the teststructure reported by the Backend System.
	 * 
	 * This Property is used to store the count of childs stored in the backend
	 * to trigger the logic of the lazy loader. If the property is greater than
	 * zero the lazy loader will load teststructures from the backend on first
	 * access of the getChildren.
	 * 
	 * @param childCount
	 *            count of children
	 */
	public void setChildCountInBackend(int childCount) {
		this.childCountInBackend = childCount;
	}

	/**
	 * Returns true if element has more than zero children.
	 * 
	 * @return boolean
	 */
	public boolean hasChildren() {
		return childCountInBackend > 0;
	}

	/**
	 * 
	 * @param testStructureName
	 *            the name of the testStructure
	 * @return the first TestStructure with this name, if a testStructure exist,
	 *         else null
	 */
	public TestStructure getTestChildByName(String testStructureName) {
		for (TestStructure ts : getAllTestChildren()) {
			if (ts.getName().equalsIgnoreCase(testStructureName)) {
				return ts;
			}
		}
		return null;
	}

	/**
	 * Searches a TestStructure with the full qualified name.
	 * 
	 * @param fullName
	 *            the fullName of the testStructure
	 * @return the first TestStructure with this name, if a testStructure exist,
	 *         else null
	 */
	public TestStructure getTestChildByFullName(String fullName) {
		return getRootElement().getTestChildByFullName(fullName);
	}

}
