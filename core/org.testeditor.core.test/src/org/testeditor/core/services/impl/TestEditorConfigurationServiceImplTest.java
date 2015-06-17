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
package org.testeditor.core.services.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;
import org.testeditor.core.constants.TestEditorGlobalConstans;
import org.testeditor.core.services.interfaces.ServiceLookUpForTest;
import org.testeditor.core.services.interfaces.TestEditorConfigurationService;

/**
 * Module and Integration Tests for TestEditorConfigurationServiceImple.
 * 
 */
public class TestEditorConfigurationServiceImplTest {

	/**
	 * 
	 * Checks the update of the system property.
	 * 
	 * @throws Exception
	 *             on Test abort
	 */
	@Test
	public void testUpdatePair() throws Exception {
		String key = "myTestKey";
		System.setProperty(key, "Foo");
		TestEditorConfigurationService testEditorConfigurationService = ServiceLookUpForTest
				.getService(TestEditorConfigurationService.class);
		assertEquals("Before Updtae expecting foo", "Foo", System.getProperty(key));
		testEditorConfigurationService.updatePair(key, "Bar");
		assertEquals("After Updtae expecting bar", "Bar", System.getProperty(key));
	}

	/**
	 * 
	 * Check that on an update of a Testpair the system properties are updated
	 * with new values. A special check is to see slim command varaibles added
	 * with the quatet value of their base protperty. For Example:
	 * 
	 * JOG4J_PATH = /foo bar/PATH/log4j.xml
	 * 
	 * and
	 * 
	 * SLIM_CMD_VAR_JOG4J_PATH = /foo%20bar/PATH/log4j.xml
	 * 
	 * @throws Exception
	 *             on Test abort
	 */
	@Test
	public void testUpdatePairWithWhiteSpace() throws Exception {
		String key = "myTestKey";
		TestEditorConfigurationService testEditorConfigurationService = ServiceLookUpForTest
				.getService(TestEditorConfigurationService.class);
		testEditorConfigurationService.updatePair(key, "Foo Bar");
		assertEquals("Expecting quated blank in property value", "Foo Bar", System.getProperty(key));
		testEditorConfigurationService.updatePair(TestEditorGlobalConstans.LOG4J_PATH_VARIABLE, "Foo Bar");
		assertEquals("Expecting origanal property value", "Foo Bar",
				System.getProperty(TestEditorGlobalConstans.LOG4J_PATH_VARIABLE));
	}

	/**
	 * Test that global variables returns always an not empty Map.
	 * 
	 * @throws Exception
	 *             on Test abort.
	 */
	@Test
	public void testGetGlobalVariables() throws Exception {
		TestEditorConfigurationService testEditorConfigurationService = ServiceLookUpForTest
				.getService(TestEditorConfigurationService.class);
		String key = addTestKeyToProperties(testEditorConfigurationService);
		Map<String, String> variables = testEditorConfigurationService.getGlobalVariables();
		assertNotNull("Global Variables should be not null.", variables);
		assertEquals("Expecting Bar in the Global Variables.", "Bar", variables.get(key));
	}

	/**
	 * 
	 * @param testEditorConfigurationService
	 *            TestEditorConfigurationService
	 * @return the added key.
	 */
	private String addTestKeyToProperties(TestEditorConfigurationService testEditorConfigurationService) {
		String key = "myTestKey";
		testEditorConfigurationService.updatePair(key, "Bar");
		return key;
	}

	/**
	 * Test the Loading of default Values for the global variables.
	 * 
	 * @throws Exception
	 *             on Test abort.
	 */
	@Test
	public void testLoadGlobalVariablesAsSystemProperties() throws Exception {
		TestEditorConfigurationService testEditorConfigurationService = ServiceLookUpForTest
				.getService(TestEditorConfigurationService.class);
		testEditorConfigurationService.exportGlobalVariablesToSystemProperties();
		assertEquals("", System.getProperty(TestEditorGlobalConstans.PATH_BROWSER));
	}

	/**
	 * test the removing of a key from the properties.
	 * 
	 * @throws Exception
	 *             while removing.
	 */
	@Test
	public void testClearKey() throws Exception {
		TestEditorConfigurationService testEditorConfigurationService = ServiceLookUpForTest
				.getService(TestEditorConfigurationService.class);
		String key = addTestKeyToProperties(testEditorConfigurationService);
		Map<String, String> variables = testEditorConfigurationService.getGlobalVariables();
		assertNotNull("Global Variables should be not null.", variables);
		assertEquals("Expecting Bar in the Global Variables.", "Bar", variables.get(key));
		testEditorConfigurationService.clearKey(key);
		variables = testEditorConfigurationService.getGlobalVariables();
		assertNotNull("Global Variables should be not null.", variables);
		assertNull(variables.get(key));

	}

	/**
	 * test the removing of a key from the properties. Key is not in the
	 * properties.
	 * 
	 * @throws Exception
	 *             while removing.
	 */
	@Test
	public void testClearKeyWithoutKeyInProperties() throws Exception {
		TestEditorConfigurationService testEditorConfigurationService = ServiceLookUpForTest
				.getService(TestEditorConfigurationService.class);
		String key = addTestKeyToProperties(testEditorConfigurationService);
		String secondKey = "Foo";
		Map<String, String> variables = testEditorConfigurationService.getGlobalVariables();
		assertNotNull("Global Variables should be not null.", variables);
		assertEquals("Expecting Bar in the Global Variables.", "Bar", variables.get(key));
		assertNull(variables.get(secondKey));
		testEditorConfigurationService.clearKey(secondKey);
		variables = testEditorConfigurationService.getGlobalVariables();
		assertNotNull("Global Variables should be not null.", variables);
		assertEquals("Expecting Bar in the Global Variables.", "Bar", variables.get(key));
		assertNull(variables.get(secondKey));
	}

	/**
	 * Tests the reset state of the application.
	 * 
	 * @throws Exception
	 *             on storing in preference store.
	 */
	@Test
	public void testSetResetState() throws Exception {
		TestEditorConfigurationService testEditorConfigurationService = ServiceLookUpForTest
				.getService(TestEditorConfigurationService.class);
		assertTrue("Expecting true on new Workspace", testEditorConfigurationService.isResetApplicationState());
		testEditorConfigurationService.setResetApplicationState(true);
		assertTrue("Expecting true after set.", testEditorConfigurationService.isResetApplicationState());
		testEditorConfigurationService.setResetApplicationState(false);
		assertFalse("Expecting false after set.", testEditorConfigurationService.isResetApplicationState());
	}

	/**
	 * Test that there is no replacement on a path without white spaces.
	 * 
	 * @throws Exception
	 *             for test
	 */
	@Test
	public void testNonReplacementForStringsWithOutBlanks() throws Exception {
		TestEditorConfigurationServiceImpl client = new TestEditorConfigurationServiceImpl();
		String replaceStr = "/home/user/.testeditor/.metadata/testEditorLog4j/log4j.xml";
		String fixWhiteSpaceIfLoggingDir = client.fixWhiteSpaceOnPreferenceString(replaceStr);
		assertEquals("Expecting no changes in the string", replaceStr, fixWhiteSpaceIfLoggingDir);
	}

	/**
	 * Tests the replacement in a path with white spaces.
	 * 
	 * @throws Exception
	 *             for test
	 */
	@Test
	public void testReplacementForStrings() throws Exception {
		TestEditorConfigurationServiceImpl client = new TestEditorConfigurationServiceImpl();
		String replaceStr = "/home/user/.test editor/.metadata/testEditorLog4j/log4j.xml";
		String fixWhiteSpaceIfLoggingDir = client.fixWhiteSpaceOnPreferenceString(replaceStr);
		assertEquals("Expecting blank replacement in the path part of the string",
				"/home/user/.test%20editor/.metadata/testEditorLog4j/log4j.xml", fixWhiteSpaceIfLoggingDir);
	}
}
