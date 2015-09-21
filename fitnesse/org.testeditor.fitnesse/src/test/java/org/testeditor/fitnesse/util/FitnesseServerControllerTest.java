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
package org.testeditor.fitnesse.util;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * 
 * Modul Test for the Fitnesse Server Controller.
 * 
 */
public class FitnesseServerControllerTest {

	/**
	 * Tests if the returned port has a valid port number > 0.
	 */
	@Test
	public void testGetFreePort() {
		FitnesseServerController serverController = new FitnesseServerController();
		int freePort = serverController.getFreePort();
		assertTrue(freePort > 0);
	}

}
