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
import org.junit.Test;
import org.testeditor.core.constants.TestEditorGlobalConstans;
import org.testeditor.core.services.interfaces.ServiceLookUpForTest;
import org.testeditor.core.services.interfaces.TestEditorConfigurationService;

/**
 * Integration Tests for TestEditorConfigurationServiceImple.
 * 
 */
public class TestEditorConfigurationServiceImplIntTest {

	/**
	 * Test the Loading of default Values for the global variables.
	 * 
	 * @throws Exception
	 *             on Test abort.
	 */
	@Test
	public void testLoadGlobalVariablesAsSystemProperties() throws Exception {
		TestEditorConfigurationService configService = ServiceLookUpForTest
				.getService(TestEditorConfigurationService.class);
		configService.exportGlobalVariablesToSystemProperties(true);
		assertEquals("", System.getProperty(TestEditorGlobalConstans.PATH_BROWSER));
	}

	/**
	 * Clears all existing properties about the preferences for the te node.
	 * 
	 * @throws Exception
	 *             on failure preparing for test.
	 * 
	 */
	public void setUp() throws Exception {
		IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(TestEditorConfigurationServiceImpl.ID_TE_PROPERTIES);
		prefs.clear();
	}
}
