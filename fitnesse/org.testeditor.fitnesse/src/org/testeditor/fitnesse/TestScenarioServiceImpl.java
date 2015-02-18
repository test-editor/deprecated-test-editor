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
package org.testeditor.fitnesse;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.Platform;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.model.teststructure.ScenarioSuite;
import org.testeditor.core.model.teststructure.TestComponent;
import org.testeditor.core.model.teststructure.TestCompositeStructure;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestScenario;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.services.interfaces.TestEditorGlobalConstans;
import org.testeditor.core.services.interfaces.TestScenarioService;
import org.testeditor.core.services.interfaces.TestStructureContentService;
import org.testeditor.fitnesse.usedbyreader.FitNesseUsedByReaderImpl;
import org.testeditor.fitnesse.util.FitNesseRestClient;

/**
 * 
 * This class implements the ScenarioService.
 * 
 */
public class TestScenarioServiceImpl implements TestScenarioService {

	private static final Logger LOGGER = Logger.getLogger(TestScenarioServiceImpl.class);
	private TestStructureContentService testStructureContentService;

	/**
	 * reads the parameters of a scenario.
	 * 
	 * @param testScenario
	 *            the testScenario
	 * @param testStructureText
	 *            the testStructureText
	 * 
	 * @throws SystemException
	 *             on getting the content of the TestScenario file.
	 */
	@Override
	public void readTestScenario(TestScenario testScenario, String testStructureText) throws SystemException {
		if (!testStructureText.isEmpty()) {
			List<TestComponent> testComponents = testStructureContentService.parseFromString(testScenario,
					testStructureText);
			testScenario.setTestComponents(testComponents);
		}

	}

	@Override
	public boolean isLinkToScenario(TestProject testProject, final String linkToFile) throws SystemException {

		// check if url of scenario is really a scenario link
		if (linkToFile.contains(TestEditorGlobalConstans.TEST_KOMPONENTS)
				|| linkToFile.contains(TestEditorGlobalConstans.TEST_SCENARIO_SUITE)) {

			return existsFile(linkToFile);
		}

		return false;

	}

	@Override
	public List<String> getUsedOfTestSceneario(TestScenario testScenario) {
		String contentAsHtml;
		try {
			contentAsHtml = FitNesseRestClient.getUsedWhere(testScenario);
			return new FitNesseUsedByReaderImpl().readWhereUsedResult(contentAsHtml, testScenario.getRootElement()
					.getName());
		} catch (SystemException e) {
			LOGGER.error(e);
		}
		return new ArrayList<String>();
	}

	@Override
	public boolean isDescendantFromTestScenariosSuite(TestStructure testStructure) {
		return testStructure instanceof ScenarioSuite || testStructure.getParent() instanceof ScenarioSuite;
	}

	@Override
	public TestScenario getScenarioByFullName(TestProject testProject, String includeOfScenario) throws SystemException {

		String[] includeOfScenarioStrings = includeOfScenario.split("\\.");
		if (includeOfScenarioStrings.length >= 0 && testProject.getName().equalsIgnoreCase(includeOfScenarioStrings[0])) {
			return findTestStructureInOffspringOfProject(includeOfScenario, testProject);
		}
		return null;
	}

	/**
	 * 
	 * @param pageFullName
	 *            full-name of the page
	 * 
	 * @return true if the page exists
	 */
	private boolean existsFile(String pageFullName) {

		File wsDir = Platform.getLocation().toFile();

		String projectName = pageFullName.split("\\.")[0];

		File absolutePathOfPage = new File(wsDir.getAbsolutePath() + File.separator + projectName + File.separator
				+ "FitNesseRoot" + File.separator + pageFullName.trim().replace('.', File.separator.toCharArray()[0])
				+ File.separator + "content.txt");

		return absolutePathOfPage.exists();

	}

	/**
	 * 
	 * @param includeOfScenario
	 *            the include as a String
	 * @param parent
	 *            the {@link TestCompositeStructure}
	 * @return the TestScenario or null, if not found
	 * @throws SystemException
	 *             by reading the scenario
	 */
	private TestScenario findTestStructureInOffspringOfProject(String includeOfScenario, TestCompositeStructure parent)
			throws SystemException {
		TestScenario testScenario = (TestScenario) parent.getTestChildByFullName(includeOfScenario);
		if (testScenario != null && testScenario.getTestComponents().isEmpty()) {
			readTestScenario(testScenario, testStructureContentService.getTestStructureAsSourceText(testScenario));

		}
		return testScenario;

	}

	@Override
	public boolean isSuiteForScenarios(TestStructure element) {
		return isReservedNameForRootSceanrioSuite(element.getName());
	}

	@Override
	public boolean isReservedNameForRootSceanrioSuite(String pageName) {
		return pageName.equalsIgnoreCase(TestEditorGlobalConstans.TEST_SCENARIO_SUITE)
				|| pageName.equalsIgnoreCase(TestEditorGlobalConstans.TEST_KOMPONENTS);
	}

	/**
	 * 
	 * @param testStructureContentService
	 *            used in this service
	 * 
	 */
	public void bind(TestStructureContentService testStructureContentService) {
		this.testStructureContentService = testStructureContentService;
		LOGGER.info("Bind TestStructureContentService");
	}

	/**
	 * Removes the TestStructureContentService.
	 * 
	 * 
	 * @param testStructureContentService
	 *            removed from system
	 */
	public void unBind(TestStructureContentService testStructureContentService) {
		this.testStructureContentService = null;
		LOGGER.info("Unbind TestStructureContentService");
	}

	@Override
	public String getId() {
		return "fitnesse_based_1.2";
	}

}
