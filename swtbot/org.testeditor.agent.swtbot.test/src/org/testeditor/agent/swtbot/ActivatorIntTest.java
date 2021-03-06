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
package org.testeditor.agent.swtbot;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

/**
 * 
 * Integration Test of the Activator.
 * 
 */
public class ActivatorIntTest {

	/**
	 * Tests that the Activator has a valid Context.
	 * @throws Exception for test
	 */
	@Test
	public void testContextIsSet() throws Exception {
		assertNotNull(Activator.getContext());
	}
	
}
