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
package org.testeditor.core.exceptions;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.testeditor.core.model.teststructure.TestCase;
import org.testeditor.core.model.teststructure.TestFlow;
import org.testeditor.core.model.teststructure.TestScenario;

/**
 * Tests of the TestCycleDetectException.
 *
 */
public class TestCycleDetectExceptionTest {

	/**
	 * Tests the Cycle Detection STring of the Exception.
	 */
	@Test
	public void testGetCycleString() {
		List<TestFlow> flowStack = new ArrayList<TestFlow>();
		TestCase tc = new TestCase();
		tc.setName("TestCase");
		flowStack.add(tc);
		TestScenario ts1 = new TestScenario();
		ts1.setName("TestScenario1");
		flowStack.add(ts1);
		TestScenario ts2 = new TestScenario();
		ts2.setName("TestScenario2");
		flowStack.add(ts2);
		flowStack.add(ts1);
		List<String> flowNames = new ArrayList<String>();
		for (TestFlow testFlow : flowStack) {
			flowNames.add(testFlow.getFullName());
		}
		String cycleString = new TestCycleDetectException(flowNames).getCycleString();
		assertEquals("TestCase, \nTestScenario1, \nTestScenario2, \nTestScenario1", cycleString);
	}

}
