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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * 
 * Integration Test for AgentApplication.
 * 
 */
public class AgentApplicationIntTest {

	/**
	 * Test the extraction of the Application from the command line arguments.
	 * 
	 * @throws Exception
	 *             for Test
	 */
	@Test
	public void testGetApplicationToRun() throws Exception {
		AgentApplication agentApplication = new AgentApplication();
		String[] args = new String[] { "-aut", "MyApp" };
		assertEquals("MyApp", agentApplication.getApplicationToRun(args));
		args = new String[] { "something", "-aut", "MyApp", "-data", "/path" };
		assertEquals("MyApp", agentApplication.getApplicationToRun(args));
	}
	
}
