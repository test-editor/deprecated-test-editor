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
package org.testeditor.core.headless;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.testeditor.core.constants.TestEditorGlobalConstans;

/**
 * 
 * Integration tests for HeadlessTestRunnerApplication.
 *
 */
public class HeadlessTestRunnerApplicationIntTest {

	/**
	 * Tests the initialization of the testeditor.
	 * 
	 * @throws Exception
	 *             on test failure.
	 */
	@Test
	public void testInitSystem() throws Exception {
		HeadlessTestRunnerApplication headlessApp = new HeadlessTestRunnerApplication();
		headlessApp.initializeSystemConfiguration();
		assertEquals("1", System.getProperty(TestEditorGlobalConstans.DEFINE_WAITS_AFTER_TEST_STEP));
		assertNotNull(System.getProperty("APPLICATION_WORK"));
	}

}
