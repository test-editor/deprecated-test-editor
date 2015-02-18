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

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.testeditor.core.model.teststructure.ScenarioSuite;
import org.testeditor.core.model.teststructure.TestScenario;
import org.testeditor.core.services.interfaces.ServiceLookUpForTest;
import org.testeditor.core.services.interfaces.TestScenarioService;

/**
 * 
 * Integrationtests for TestScenarioServiceImpl.
 * 
 */
public class TestScenarioServiceImplTest {

	/**
	 * 
	 * 
	 * 
	 * @throws Exception
	 *             for Test
	 */
	@Test
	public void testReadTestScenario() throws Exception {
		TestScenarioService testScenarioService = ServiceLookUpForTest.getService(TestScenarioService.class);
		TestScenario testScenario = new TestScenario();
		ScenarioSuite scenarioSuite = new ScenarioSuite();
		scenarioSuite.setName("TestSzenarien");
		scenarioSuite.addChild(testScenario);
		TestProjectDataFactory.createTestProjectForFitnesseTests().addChild(scenarioSuite);
		String testScenarioText = "!|scenario |QuellcodeDurchsuchenSzenario _|Element, TextVorhanden, TextNichtVorhanden|\n"
				+ "|note| Maske: Allgemein SWT-Bot|\n"
				+ "|navigiere zum Element|@Element|\n"
				+ "|wähle aus dem Kontextmenü den Eintrag|view.testExplorer.openSource|aus|";
		testScenarioService.readTestScenario(testScenario, testScenarioText);
		assertTrue(testScenario.getTestParameters().contains("TextVorhanden"));
		assertTrue(testScenario.getTestParameters().contains("TextNichtVorhanden"));
	}

}
