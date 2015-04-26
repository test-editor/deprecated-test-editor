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
import java.util.Map;

import org.testeditor.core.model.team.TeamChangeType;

/**
 * 
 * Root Element of a TestProject. It contains Testsuites and Testcases.
 * 
 */
public class TestProject extends TestCompositeStructure {

	private TestProjectConfig testProjectConfig;

	private Map<String, TeamChangeType> teamChangeFileList;

	/**
	 * set the TeamChangeType from the child`s of the project. (sets only
	 * modified ones).
	 */
	public void setTeamStatusInChilds() {
		setTeamStatusInChilds(teamChangeFileList, null);
	}

	/**
	 * Set the TeamChangeType in the childs from the given changeFilelist to the
	 * type. if the given type is null it will set every child on his own State
	 * (gets the TeamChangeType from the given list). if the type is given type
	 * is != it will set every Child the same given type.
	 * 
	 * @param changeFilelist
	 *            Map<String, TeamChangeType> list of childs with their
	 *            TeamChangeTypes
	 * @param type
	 *            TeamChangeType if its null the TeamChangeType from the list
	 *            will be used, else all childs will be set this TeamChangeType
	 */
	private void setTeamStatusInChilds(Map<String, TeamChangeType> changeFilelist, TeamChangeType type) {
		if (changeFilelist != null) {
			for (String fullname : changeFilelist.keySet()) {
				if (!fullname.equals(getName())) {
					TestStructure child = getTestChildByFullName(fullname);
					if (child != null) {
						if (type == null) {
							child.setTeamChangeType(changeFilelist.get(fullname));
						} else {
							child.setTeamChangeType(type);
						}
					}
				} else {
					if (type == null) {
						setTeamChangeType(changeFilelist.get(fullname));
					} else {
						setTeamChangeType(type);
					}
				}
			}
		}
	}

	/**
	 * @param teamChangeFileList
	 *            the teamChangeFileList (Map<String, TeamChangeType>) to set
	 */
	public void setTeamChangeFileList(Map<String, TeamChangeType> teamChangeFileList) {
		this.teamChangeFileList = teamChangeFileList;
	}

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
	public TeamChangeType getTeamChangeType() {
		if (teamChangeFileList == null || teamChangeFileList.isEmpty()) {
			return TeamChangeType.NONE;
		} else {
			return TeamChangeType.MODIFY;
		}
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
