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
package org.testeditor.core.services.interfaces;

import java.util.List;

import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestScenario;
import org.testeditor.core.model.teststructure.TestStructure;

/**
 * 
 * A ScenarioService provides informations about the scenarios of the actual
 * project.
 *
 * This interface is intended to be used by clients and not to be implemented by
 * plug-ins. Plug-In developer should implement the interface:
 * TestScenarioServicePlugIn.
 * 
 */
public interface TestScenarioService {

	/**
	 * this method returns true, if the linked file is a scenario, else false.
	 * 
	 * @param testProject
	 *            the actual TestProject
	 * @param linkToFile
	 *            link to a file in the actual project dir.
	 * @return true, if the linked file is a scenario, else false
	 * @throws SystemException
	 *             by reading the scenario
	 */
	boolean isLinkToScenario(TestProject testProject, String linkToFile) throws SystemException;

	/**
	 * this method returns the names of all TestFlow, which are using the given
	 * {@link TestScenario}.
	 * 
	 * @param testScenario
	 *            the TestSceanrio
	 * @return List <String>
	 */
	List<String> getUsedOfTestSceneario(TestScenario testScenario);

	/**
	 * checks, if the element is descendant from the teststructure with the name
	 * TestEditorGlobalConstans.TEST_SCENARIO_SUITE.
	 * 
	 * @param testStructure
	 *            a testStructure
	 * @return true, if the element is descended from the teststructure with the
	 *         name TestEditorGlobalConstans.TEST_SCENARIO_SUITE, else false
	 */
	boolean isDescendantFromTestScenariosSuite(TestStructure testStructure);

	/**
	 * returns the TestScenario getting by the include-path.
	 * 
	 * @param testProject
	 *            the TestProject
	 * @param includeOfScenario
	 *            include-path of the testflow.
	 * @return the TestScenario or null
	 * @throws SystemException
	 *             by reading the scenario
	 */
	TestScenario getScenarioByFullName(TestProject testProject, String includeOfScenario) throws SystemException;

	/**
	 * 
	 * @param element
	 *            a TestStructure
	 * @return true, if its a Suite for TestScenarios, else false
	 */
	boolean isSuiteForScenarios(TestStructure element);

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
	void readTestScenario(TestScenario testScenario, String testStructureText) throws SystemException;

}
