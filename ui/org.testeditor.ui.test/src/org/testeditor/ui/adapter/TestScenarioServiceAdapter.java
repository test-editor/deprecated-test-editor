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
package org.testeditor.ui.adapter;

import java.util.List;

import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestScenario;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.services.interfaces.TestScenarioService;

/**
 * a mockup for the TestScenarioService.
 * 
 * @author llipinski
 * 
 */
public class TestScenarioServiceAdapter implements TestScenarioService {

	@Override
	public boolean isSuiteForScenarios(TestStructure element) {
		return false;
	}

	@Override
	public boolean isLinkToScenario(TestProject testProject, String linkToFile) {
		return false;
	}

	@Override
	public boolean isDescendantFromTestScenariosSuite(TestStructure testStructure) {
		return true;
	}

	@Override
	public List<String> getUsedOfTestSceneario(TestScenario testScenario) {
		return null;
	}

	@Override
	public TestScenario getScenarioByFullName(TestProject testProject, String includeOfScenario) {
		return null;
	}

	@Override
	public boolean isReservedNameForRootSceanrioSuite(String pageName) {
		return false;
	}

	@Override
	public void readTestScenario(TestScenario testScenario, String testStructureText) throws SystemException {

	}

	@Override
	public String getId() {
		return null;
	}
}
