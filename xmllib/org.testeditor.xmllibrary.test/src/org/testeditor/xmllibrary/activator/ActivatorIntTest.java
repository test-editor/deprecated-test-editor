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
package org.testeditor.xmllibrary.activator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.testeditor.core.services.interfaces.LibraryConfigurationService;
import org.testeditor.core.services.interfaces.LibraryReaderService;
import org.testeditor.core.services.interfaces.ServiceLookUpForTest;
import org.testeditor.xmllibrary.model.XMLProjectLibraryConfig;
/**
 * Tests the activator.
 */
public class ActivatorIntTest {

	/**
	 * Test if the bundle is started.
	 */
	@Test
	public void testInitActivator() {
		assertNotNull("plug-in not started and context unavailable", XMLLibraryActivator.getContext());
	}

	/**
	 * Test that the XMLLibrary Services are available and have the same id.
	 * 
	 * @throws Exception
	 *             for test
	 */
	@Test
	public void testXMLLibraryServiceRegistration() throws Exception {
		LibraryConfigurationService libraryConfigurationService = ServiceLookUpForTest
				.getService(LibraryConfigurationService.class);
		assertNotNull("No Library Configuration Service found.", libraryConfigurationService);
		assertEquals("Different Plug-In ID", new XMLProjectLibraryConfig().getId(), libraryConfigurationService.getId());
		LibraryReaderService libraryReaderService = ServiceLookUpForTest.getService(LibraryReaderService.class);
		assertNotNull("No Library Reader Service found.", libraryReaderService);
		assertEquals("Different Plug-In ID", new XMLProjectLibraryConfig().getId(), libraryReaderService.getId());
	}

}
