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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;
import org.testeditor.core.services.interfaces.ServiceLookUpForTest;
import org.testeditor.core.services.interfaces.TechnicalBindingsDSLMappingService;

/**
 * 
 * Integrationtests for FitnesseTechnicalBindingsDSLMappingService.
 * 
 * @author karsten
 */
public class FitnesseTechnicalBindingsDSLMappingServiceTest {

	private TechnicalBindingsDSLMappingService technicalBindingsDSLMappingService;

	/**
	 * Init the Object under Test.
	 */
	@Before
	public void initOUT() {
		technicalBindingsDSLMappingService = ServiceLookUpForTest.getService(TechnicalBindingsDSLMappingService.class);
	}

	/**
	 * Tests the Method name extraction.
	 * 
	 * @throws Exception
	 *             for test
	 */
	@Test
	public void testMethodNameExtraction() throws Exception {
		FitnesseTechnicalBindingsDSLMappingService fitnesseTecBindingService = (FitnesseTechnicalBindingsDSLMappingService) technicalBindingsDSLMappingService;
		assertEquals("click ()", fitnesseTecBindingService.extractMethodNameFromTechnicalBinding("click ( \"hugo\")"));
		assertEquals("click ()", fitnesseTecBindingService.extractMethodNameFromTechnicalBinding("click ( \"login\")"));
		assertEquals("insertIntoField ()",
				fitnesseTecBindingService.extractMethodNameFromTechnicalBinding("insertIntoField ( \"test\", \"usr\")"));
	}

	/**
	 * Test the Replacement for Parameters in the Fitnesse DSL with the actual
	 * Paramter of the technical binding method.
	 * 
	 * @throws Exception
	 *             for Test
	 */
	@Test
	public void testReplaceFitnesseDSLParametersWithTechnicalBindingParameters() throws Exception {
		FitnesseTechnicalBindingsDSLMappingService fitnesseTecBindingService = (FitnesseTechnicalBindingsDSLMappingService) technicalBindingsDSLMappingService;
		assertEquals("|klicke auf|login|",
				fitnesseTecBindingService.replaceFitnesseDSLParametersWithTechnicalBindingParameters(
						new TecBindDslParameteMapper("|klicke auf|guiElement|", "guiElement"), "click ( \"login\")"));
		assertEquals("|gebe in das Feld|usr|den Wert|test|ein|",
				fitnesseTecBindingService.replaceFitnesseDSLParametersWithTechnicalBindingParameters(
						new TecBindDslParameteMapper("|gebe in das Feld|guiid|den Wert|text|ein|", "text,guiid"),
						"insertIntoField ( \"test\", \"usr\")"));
	}

	/**
	 * Test Remove of quotation from a string.
	 * 
	 * @throws Exception
	 *             for Test
	 */
	@Test
	public void testFixPossibleQuotMarks() throws Exception {
		FitnesseTechnicalBindingsDSLMappingService fitnesseTecBindingService = (FitnesseTechnicalBindingsDSLMappingService) technicalBindingsDSLMappingService;
		assertEquals("1", fitnesseTecBindingService.fixPossibleQuotMarks("1"));
		assertEquals("login", fitnesseTecBindingService.fixPossibleQuotMarks("\"login\""));
		assertEquals("login", fitnesseTecBindingService.fixPossibleQuotMarks(" \"login\" "));
	}

	/**
	 * Test the Mapping for. click ( "Login" ) to: klicke auf|guiElement|
	 * 
	 * @throws Exception
	 *             for test
	 */
	@Test
	public void testMappingForClick() throws Exception {
		String mapping = technicalBindingsDSLMappingService.mapTechnicalBindingToTestDSL("click ( \"Login\" )");
		assertEquals("|klicke auf|Login|", mapping);
	}

	/**
	 * Test the Mapping for. navigateToUrl (
	 * "http://localhost:8060/files/demo/ExampleApplication/WebApplicationDe/index.html"
	 * ) to: navigiere auf die Seite|url|
	 * 
	 * @throws Exception
	 *             for test
	 */
	@Test
	public void testMappingForNavigate() throws Exception {
		String mapping = technicalBindingsDSLMappingService
				.mapTechnicalBindingToTestDSL("navigateToUrl ( \"http://localhost:8060/files/demo/ExampleApplication/WebApplicationDe/index.html\" )");
		assertEquals(
				"|navigiere auf die Seite|http://localhost:8060/files/demo/ExampleApplication/WebApplicationDe/index.html|",
				mapping);
	}

	/**
	 * Test the Mapping for. insertIntoField ( "test","Passwort" ) to: gebe in
	 * das Feld|guiid|den Wert|text|ein|
	 * 
	 * @throws Exception
	 *             for test
	 */
	@Test
	public void testMappingForInsertIntoField() throws Exception {
		String mapping = technicalBindingsDSLMappingService
				.mapTechnicalBindingToTestDSL("insertIntoField ( \"test\",\"Passwort\" )");
		assertEquals("|gebe in das Feld|Passwort|den Wert|test|ein|", mapping);
	}

	/**
	 * Test the Mapping for Elements that are technical nature and not used for
	 * the TestDSL. Examples are: waiting(2) setElementList("/path/")
	 * 
	 * @throws Exception
	 *             for test
	 */
	@Test
	public void testMappingForNonTestDSLElements() throws Exception {
		String mapping = technicalBindingsDSLMappingService.mapTechnicalBindingToTestDSL("wating ( 1 )");
		assertNull("NoValue for waiting", mapping);
		mapping = technicalBindingsDSLMappingService.mapTechnicalBindingToTestDSL("setElementlist ( \"/path\" )");
		assertNull("NoValue for setElementlist", mapping);
	}

}
