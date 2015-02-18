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
package org.testeditor.core.util;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;
import org.testeditor.core.services.interfaces.ServiceLookUpForTest;

/**
 * Integration Tests for the FileLocatorService.
 */
public class FileLocatorServiceTest {

	/**
	 * Tests the lookup for a bundle file location.
	 * 
	 * @throws Exception
	 *             for Test
	 */
	@Test
	public void testFindBundleFileLocationAsString() throws Exception {
		FileLocatorService fileLocatorService = ServiceLookUpForTest.getService(FileLocatorService.class);
		assertTrue("Looking up the core Bundle of the TestEditor",
				new File(fileLocatorService.findBundleFileLocationAsString("org.testeditor.core")).exists());
	}

	/**
	 * Tests the lookup of the BundleContext of this service.
	 * 
	 * @throws Exception
	 *             for Test
	 */
	@Test
	public void testGetBundleContext() throws Exception {
		FileLocatorService fileLocatorService = ServiceLookUpForTest.getService(FileLocatorService.class);
		assertNotNull("Found a BundleContext: " + fileLocatorService.getBundleContext(),
				fileLocatorService.getBundleContext());
	}
}
