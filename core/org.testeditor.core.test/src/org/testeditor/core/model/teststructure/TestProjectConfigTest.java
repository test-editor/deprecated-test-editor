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
package org.testeditor.core.model.teststructure;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.junit.Test;
import org.testeditor.core.constants.TestEditorCoreConstants;

/**
 * 
 * Modultest for TestProjectConfig.
 * 
 */
public class TestProjectConfigTest {

	/**
	 * Check that every Attribute is Not Null. Without Fields for codecoverage
	 * or PlugIn Fields.
	 * 
	 * @throws Exception
	 *             for test.
	 */
	@Test
	public void testInit() throws Exception {
		TestProjectConfig testProjectConfig = new TestProjectConfig();
		Field[] fields = testProjectConfig.getClass().getDeclaredFields();
		for (Field field : fields) {
			if (!(field.getName().startsWith("$") || field.getName().equals("projectLibraryConfig")
					|| field.getName().equals("teamShareConfig"))) {
				String methodName = "get" + Character.toUpperCase(field.getName().charAt(0))
						+ field.getName().substring(1);
				Method method = testProjectConfig.getClass().getMethod(methodName, new Class[] {});
				assertNotNull(method.invoke(testProjectConfig, new Object[] {}));
			}
		}
	}

	/**
	 * Tests the Equals Method.
	 */
	@Test
	public void testEquals() {
		TestProjectConfig testProjectConfig = new TestProjectConfig();
		TestProjectConfig testProjectConfig2 = new TestProjectConfig();
		assertEquals(testProjectConfig, testProjectConfig2);
		testProjectConfig2.setPort("80");
		assertFalse(testProjectConfig.equals(testProjectConfig2));
		testProjectConfig.setPort("80");
		assertEquals(testProjectConfig, testProjectConfig2);
	}

	/**
	 * Test the usesTastAgent method. THis one should retrun true if an
	 * testagent is configured, otherwise null.
	 */
	@Test
	public void testUsesTestAgent() {
		TestProjectConfig tpConfig = new TestProjectConfig();
		assertFalse(tpConfig.usesTestAgent());
		tpConfig.setTestEnvironmentConfiguration("");
		assertTrue(tpConfig.usesTestAgent());
		tpConfig.setTestEnvironmentConfiguration(TestEditorCoreConstants.NONE_TEST_AGENT);
		assertFalse(tpConfig.usesTestAgent());
	}

}
