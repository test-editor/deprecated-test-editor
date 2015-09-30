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

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.junit.Before;
import org.junit.Test;
import org.testeditor.core.constants.TestEditorGlobalConstans;
import org.testeditor.core.services.interfaces.ServiceLookUpForTest;
import org.testeditor.core.services.interfaces.TestEditorConfigurationService;

/**
 * Integration Tests for TestEditorConfigurationServiceImple.
 * 
 */
public class TestEditorConfigurationServiceImplIntTest {

	private TestEditorConfigurationService configService;

	/**
	 * Test the Loading of default Values for the global variables.
	 * 
	 * @throws Exception
	 *             on Test abort.
	 */
	@Test
	public void testLoadGlobalVariablesAsSystemPropertiesWithDefaults() throws Exception {
		configService.exportGlobalVariablesToSystemProperties(true);
		assertEquals("", System.getProperty(TestEditorGlobalConstans.PATH_BROWSER));
	}

	/**
	 * Test loading from preference store.
	 * 
	 * @throws Exception
	 *             on Test abort.
	 */
	@Test
	public void testLoadGlobalVariablesAsSystemPropertiesFromPropsFile() throws Exception {
		configService.exportGlobalVariablesToSystemProperties(true);
		assertEquals("storevalue", System.getProperty("testkey"));
	}

	/**
	 * Test loading from preference store works with out override parameter.
	 * 
	 * @throws Exception
	 *             on Test abort.
	 */
	@Test
	public void testLoadGlobalVariablesAsSystemPropertiesFromPropsFileWithoutOverrideCommand() throws Exception {
		configService.exportGlobalVariablesToSystemProperties(false);
		assertEquals("storevalue", System.getProperty("testkey"));
	}

	/**
	 * test loading from preference store overrides not the system parameter
	 * until override is true.
	 * 
	 * @throws Exception
	 *             on Test abort.
	 */
	@Test
	public void testCommandLineOverProperties() throws Exception {
		System.setProperty("testkey", "testvalue");
		configService.exportGlobalVariablesToSystemProperties(false);
		assertEquals("testvalue", System.getProperty("testkey"));
		configService.exportGlobalVariablesToSystemProperties(true);
		assertEquals("storevalue", System.getProperty("testkey"));
	}

	/**
	 * Clears all existing and sets an test value properties in the preferences
	 * for the te node.
	 * 
	 * @throws Exception
	 *             on failure preparing for test.
	 * 
	 */
	@Before
	public void setUp() throws Exception {
		configService = ServiceLookUpForTest.getService(TestEditorConfigurationService.class);
		IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(TestEditorConfigurationServiceImpl.ID_TE_PROPERTIES);
		prefs.clear();
		prefs.put("testkey", "storevalue");
		prefs.flush();
	}

}
