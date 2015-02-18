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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.ui.testing.TestableObject;
import org.junit.Ignore;
import org.junit.Test;

/**
 * 
 * Integration Tests for the TEAgentServer.
 * 
 */
public class TEAgentServerIntTest {

	private boolean finished;

	/**
	 * Test the Logic for Launching.
	 * 
	 * @throws Exception
	 *             for Test
	 */
	@Test
	public void testLaunchedWorkFlow() throws Exception {
		TEAgentServer teAgentServer = new TEAgentServer(getTestObject());
		assertFalse("Agent Server is not launched.", teAgentServer.isLaunched());
		teAgentServer.runTests();
		assertTrue("Agent Server reports it is launched.", teAgentServer.isLaunched());
	}

	/**
	 * Test the callback to the TestHarness.
	 * 
	 * @throws Exception
	 *             for Test
	 */
	@Test
	@Ignore
	public void testTestAbleFrameworkCall() throws Exception {
		TEAgentServer teAgentServer = new TEAgentServer(getTestObject());
		finished = false;
		teAgentServer.stopApplication();
		assertTrue("Application is Finished", finished);
	}

	/**
	 * 
	 * @return TestableObject Mock for test.
	 */
	private TestableObject getTestObject() {
		return new TestableObject() {
			@Override
			public void testingFinished() {
				finished = true;
			}
		};
	}

}
