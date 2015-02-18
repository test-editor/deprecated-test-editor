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
package org.testeditor.core.services;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.osgi.framework.FrameworkUtil;
import org.testeditor.core.services.interfaces.ServiceLookUpForTest;

/**
 * Tests the service look up service.
 */
public class ServiceLookUpTest {
	/**
	 * Dummy service interface for tests.
	 */
	private interface DummyService {
	};

	/**
	 * Tests the register and get service.
	 */
	@Test
	public void testGetRegisteredService() {
		FrameworkUtil.getBundle(getClass()).getBundleContext()
				.registerService(DummyService.class.getName(), getDummyService(), null);
		DummyService dummyService = ServiceLookUpForTest.getService(DummyService.class);
		assertNotNull("service is unavailable", dummyService);
	}

	/**
	 * Creates the dummy service.
	 * 
	 * @return dummy service
	 */
	private Object getDummyService() {
		return new DummyService() {
		};
	}

}
