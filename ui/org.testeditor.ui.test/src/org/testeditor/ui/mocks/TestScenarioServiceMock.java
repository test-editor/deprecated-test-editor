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
package org.testeditor.ui.mocks;

import java.util.ArrayList;
import java.util.List;

import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.model.teststructure.TestCase;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestScenario;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.services.interfaces.TestScenarioService;

/**
 * 
 * Mock object which says scenario with the names used is a used scenario.
 * 
 */
public class TestScenarioServiceMock implements TestScenarioService {

	@Override
	public boolean isLinkToScenario(TestProject testProject, String linkToFile) {
		return false;
	}

	@Override
	public List<String> getUsedOfTestSceneario(TestScenario testScenario) {
		ArrayList<String> usedList = new ArrayList<String>();
		if (testScenario.getName().equals("used")) {
			usedList.add(new TestCase().getName());
		}
		return usedList;
	}

	@Override
	public boolean isDescendantFromTestScenariosSuite(TestStructure testStructure) {
		return false;
	}

	@Override
	public TestScenario getScenarioByFullName(TestProject testProject, String includeOfScenario) {
		TestScenario testScenario = new TestScenario();
		testScenario.setName("MyScenario");
		return testScenario;
	}

	@Override
	public boolean isSuiteForScenarios(TestStructure element) {
		return false;
	}

	@Override
	public void readTestScenario(TestScenario testScenario, String testStructureText) throws SystemException {

	}

}
