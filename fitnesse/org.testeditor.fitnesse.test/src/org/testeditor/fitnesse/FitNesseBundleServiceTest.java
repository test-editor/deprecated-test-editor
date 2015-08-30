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
package org.testeditor.fitnesse;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.testeditor.core.services.interfaces.ServiceLookUpForTest;
import org.testeditor.core.services.interfaces.TestExecutionEnvironmentService;

/**
 * Integrationtest to check the bundle configurtion.
 *
 */
public class FitNesseBundleServiceTest {

	/**
	 * Checks that the service is correct registered.
	 */
	@Test
	public void testServiceRegistrarion() {
		assertNotNull(ServiceLookUpForTest.getService(TestExecutionEnvironmentService.class));
	}

}
