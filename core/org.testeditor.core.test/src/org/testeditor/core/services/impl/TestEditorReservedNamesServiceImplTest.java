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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.testeditor.core.constants.TestEditorGlobalConstans;
import org.testeditor.core.services.interfaces.ServiceLookUpForTest;
import org.testeditor.core.services.interfaces.TestEditorReservedNamesService;

/**
 * 
 * IntegrationTests for TestEditorReservedNamesServiceImpl.
 * 
 */
public class TestEditorReservedNamesServiceImplTest {

	/**
	 * Checks the Registration of TestEditorReservedNamesServiceImpl as an
	 * service of TestEditorReservedNamesService.
	 * 
	 * @throws Exception
	 *             for Test
	 */
	@Test
	public void testServiceIsAvailable() throws Exception {
		TestEditorReservedNamesService service = ServiceLookUpForTest.getService(TestEditorReservedNamesService.class);
		assertNotNull("Expecting an implementation of the TestEditorReservedNamesService", service);
		assertTrue("Service implementation is not an instance of TestEditorReservedNamesServiceImpl",
				service instanceof TestEditorReservedNamesServiceImpl);
	}

	/**
	 * Tests the check method to identify a reserved name.
	 * 
	 * @throws Exception
	 *             for Test.
	 */
	@Test
	public void testIsValidAndInvalidNames() throws Exception {
		TestEditorReservedNamesService service = ServiceLookUpForTest.getService(TestEditorReservedNamesService.class);
		assertTrue("Expecting TestKomponente as a reserved name.",
				service.isReservedName(TestEditorGlobalConstans.TEST_SCENARIO_SUITE));
		assertFalse("Expecting TestProject as a free name.", service.isReservedName("TestProject"));
	}
}
