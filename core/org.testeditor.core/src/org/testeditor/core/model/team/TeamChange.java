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
package org.testeditor.core.model.team;

import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestStructure;

/**
 * Bean to store the changes of a team provider.
 * 
 */
public class TeamChange {

	private TeamChangeType teamChangeType;

	private String relativeTestStructureFullName;

	private TestProject testProject;

	/**
	 * 
	 * @param teamChangeType
	 *            of change
	 * @param relativeFullName
	 *            FullName of the TestStructure which is changed.
	 * @param testProject
	 *            of the change
	 */
	public TeamChange(TeamChangeType teamChangeType, String relativeFullName, TestProject testProject) {
		this.teamChangeType = teamChangeType;
		this.relativeTestStructureFullName = relativeFullName;
		this.testProject = testProject;
	}

	@Override
	public String toString() {
		return "Team Changed " + teamChangeType + " for: " + relativeTestStructureFullName;
	}

	/**
	 * 
	 * @return TeamChangeType of the change
	 */
	public TeamChangeType getTeamChangeType() {
		return teamChangeType;
	}

	/**
	 * 
	 * @return relative path based on the fitnesser server to the test
	 *         structure.
	 */
	public String getRelativeTestStructureFullName() {
		return relativeTestStructureFullName;
	}

	/**
	 * 
	 * @param teamChangeType
	 *            type of the change.
	 */
	public void setTeamChangeType(TeamChangeType teamChangeType) {
		this.teamChangeType = teamChangeType;
	}

	/**
	 * 
	 * @param relativeTestStructureFullName
	 *            FullName of the TestStructure which is changed.
	 */
	public void setRelativeTestStructureFullName(String relativeTestStructureFullName) {
		this.relativeTestStructureFullName = relativeTestStructureFullName;
	}

	/**
	 * Searches for the TestStructure which is affected by this change. It ueses
	 * the relative Path to lookup for the TestStructure.
	 * 
	 * @return TestStructure found by the path.
	 * @throws SystemException
	 *             on failure of the backend.
	 */
	public TestStructure getReleatedTestStructure() throws SystemException {
		if (relativeTestStructureFullName != null && !relativeTestStructureFullName.isEmpty()) {
			if (testProject.getFullName().equals(relativeTestStructureFullName)) {
				return testProject;
			} else {
				return testProject.getTestChildByFullName(relativeTestStructureFullName);
			}
		} else {
			return null;
		}
	}

}
