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
package org.testeditor.core;

import static org.junit.Assert.assertNotNull;

import org.apache.log4j.LogManager;
import org.junit.Test;
import org.testeditor.core.services.interfaces.ServiceLookUpForTest;
import org.testeditor.core.services.interfaces.TestEditorConfigurationService;
import org.testeditor.core.services.interfaces.TestProjectService;
import org.testeditor.core.services.plugins.TestEditorPlugInService;
import org.testeditor.core.util.TestStateProtocolService;

/**
 * Tests the activator.
 */
public class CoreActivatorTest {

	/**
	 * Tests the Initialization of the logging system.
	 */
	@Test
	public void testInitLogging() {
		assertNotNull(LogManager.exists("org.testeditor.core.CoreActivator"));
	}

	/**
	 * Tests that the Service Implementations in this bundle are available.
	 */
	@Test
	public void testServiceRegistration() {
		assertNotNull(ServiceLookUpForTest.getService(TestStateProtocolService.class));
		assertNotNull(ServiceLookUpForTest.getService(TestProjectService.class));
		assertNotNull(ServiceLookUpForTest.getService(TestEditorPlugInService.class));
		assertNotNull(ServiceLookUpForTest.getService(TestEditorConfigurationService.class));
	}

}
