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

import java.util.ArrayList;
import java.util.List;

import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.services.interfaces.TestStructureTreeInputService;

/**
 * TestStructureTreeInputService for the TestScenarios of a TestProject.
 * 
 */
public class TestScenarioTreeInput implements TestStructureTreeInputService {

	private TestProject testProject;

	/**
	 * constructor.
	 * 
	 * @param testProject
	 *            TestProject
	 */
	TestScenarioTreeInput(TestProject testProject) {
		assert testProject != null;
		this.testProject = testProject;
	}

	@Override
	public List<TestStructure> getElements() throws SystemException {

		ArrayList<TestStructure> list = new ArrayList<TestStructure>();
		if (testProject != null) {
			TestStructure scenarioRoot = testProject.getScenarioRoot();
			if (scenarioRoot != null) {
				list.add(scenarioRoot);
			}
		}
		return list;

	}

	/**
	 * 
	 * @return the testProject.
	 */
	public TestProject getTestProject() {
		return testProject;
	}

	/**
	 * set the member testProject.
	 * 
	 * @param testProject
	 *            TestProject
	 */
	public void setTestProject(TestProject testProject) {
		this.testProject = testProject;
	}

}
