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
 * Root Element of a TestProject. It contains Testsuites and Testcases.
 * 
 */
public class TestProject extends TestCompositeStructure {

	private TestProjectConfig testProjectConfig;

	@Override
	protected void setParent(TestStructure parent) {
	}

	@Override
	public String getSourceCode() {
		return null;
	}

	@Override
	public int hashCode() {
		return getName().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		TestProject other = (TestProject) obj;
		return this.getName().equals(other.getName());
	}

	@Override
	public String getPageType() {
		return null;
	}

	@Override
	public String getTypeName() {
		return TestType.TESTPROJECT.getName();
	}

	/**
	 * 
	 * @return the Configuration of the TestProject.
	 */
	public TestProjectConfig getTestProjectConfig() {
		return testProjectConfig;
	}

	/**
	 * 
	 * @param testProjectConfig
	 *            TestProjectConfig to be set for this TestProject.
	 */
	public void setTestProjectConfig(TestProjectConfig testProjectConfig) {
		this.testProjectConfig = testProjectConfig;
	}

	/**
	 * @return the Root of all TestScenarios.
	 */
	public ScenarioSuite getScenarioRoot() {
		List<TestStructure> list = getTestChildren();
		for (TestStructure testStructure : list) {
			if (testStructure instanceof ScenarioSuite) {
				return (ScenarioSuite) testStructure;
			}
		}
		return null;
	}

	@Override
	public TestStructure getTestChildByFullName(String fullName) {
		String[] split = fullName.split("\\.");
		int pathIndex = 1;
		List<TestStructure> list = getTestChildren();
		return subSearch(list, pathIndex, split, fullName);
	}

	/**
	 * 
	 * @param list
	 *            List<TestStructure>
	 * @param pathIndex
	 *            index of the path
	 * @param split
	 *            array of splits
	 * @param fullName
	 *            the FullName of the TestStructure, we are looking for.
	 * @return the founded TestStructure or null, if not found.
	 */
	private TestStructure subSearch(List<TestStructure> list, int pathIndex, String[] split, String fullName) {
		for (TestStructure testStructure : list) {
			if (testStructure.matchesFullName(fullName)) {
				return testStructure;
			}
			if (split.length > pathIndex) {
				if (testStructure.getName().equals(split[pathIndex]) && testStructure instanceof TestCompositeStructure) {
					TestStructure probant = subSearch(((TestCompositeStructure) testStructure).getTestChildren(),
							pathIndex + 1, split, fullName);
					if (probant != null) {
						return probant;
					}
				}
			}
		}
		return null;
	}

	@Override
	public List<TestStructure> getAllParents() {
		return new ArrayList<TestStructure>();
	}

}
