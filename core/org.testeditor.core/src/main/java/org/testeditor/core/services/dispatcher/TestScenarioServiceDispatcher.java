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
package org.testeditor.core.services.dispatcher;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestScenario;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.services.interfaces.TestScenarioService;
import org.testeditor.core.services.plugins.TestScenarioServicePlugIn;

/**
 * 
 * Dispatcher class of the TestScenarioService. It looks up the corresponding
 * plug-in for the test structure to work on.
 *
 */
public class TestScenarioServiceDispatcher implements TestScenarioService {

	private static final Logger LOGGER = Logger.getLogger(TestScenarioServiceDispatcher.class);
	private Map<String, TestScenarioServicePlugIn> testScenarioServices = new HashMap<String, TestScenarioServicePlugIn>();

	/**
	 * 
	 * @param testScenarioService
	 *            used in this service
	 * 
	 */
	public void bind(TestScenarioServicePlugIn testScenarioService) {
		this.testScenarioServices.put(testScenarioService.getId(), testScenarioService);
		LOGGER.info("Bind TestScenarioService Plug-In" + testScenarioService.getClass().getName());
	}

	/**
	 * 
	 * @param testScenarioService
	 *            removed from system
	 */
	public void unBind(TestScenarioServicePlugIn testScenarioService) {
		this.testScenarioServices.remove(testScenarioService.getId());
		LOGGER.info("UnBind TestScenarioService Plug-In" + testScenarioService.getClass().getName());
	}

	@Override
	public boolean isLinkToScenario(TestProject testProject, String linkToFile) throws SystemException {
		return testScenarioServices.get(testProject.getTestProjectConfig().getTestServerID()).isLinkToScenario(
				testProject, linkToFile);
	}

	@Override
	public List<String> getUsedOfTestSceneario(TestScenario testScenario) {
		return testScenarioServices.get(testScenario.getRootElement().getTestProjectConfig().getTestServerID())
				.getUsedOfTestSceneario(testScenario);
	}

	@Override
	public boolean isDescendantFromTestScenariosSuite(TestStructure testStructure) {
		return testScenarioServices.get(testStructure.getRootElement().getTestProjectConfig().getTestServerID())
				.isDescendantFromTestScenariosSuite(testStructure);
	}

	@Override
	public TestScenario getScenarioByFullName(TestProject testProject, String includeOfScenario) throws SystemException {
		return testScenarioServices.get(testProject.getTestProjectConfig().getTestServerID()).getScenarioByFullName(
				testProject, includeOfScenario);
	}

	@Override
	public boolean isSuiteForScenarios(TestStructure element) {
		return testScenarioServices.get(element.getRootElement().getTestProjectConfig().getTestServerID())
				.isSuiteForScenarios(element);
	}

	@Override
	public void readTestScenario(TestScenario testScenario, String testStructureText) throws SystemException {
		testScenarioServices.get(testScenario.getRootElement().getTestProjectConfig().getTestServerID())
				.readTestScenario(testScenario, testStructureText);
	}

}
