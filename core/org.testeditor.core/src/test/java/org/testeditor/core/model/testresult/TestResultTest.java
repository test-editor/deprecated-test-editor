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
package org.testeditor.core.model.testresult;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Test;

/**
 * 
 * Tests the methods of {@link TestResult}.
 */
public class TestResultTest {

	/**
	 * 
	 */
	@Test
	public void testToString() {
		TestResult testResult = new TestResult();
		testResult.setRight(9);
		testResult.setWrong(2);
		testResult.setIgnored(3);

		assertTrue("toString gives wrong result", testResult.toString().matches(".*exception=-1.*"));
	}

	/**
	 * Test {@link TestResult#isSuccessfully()}.
	 */
	@Test
	public void testIsSuccessfully() {
		TestResult testResult = new TestResult();
		testResult.setRight(1);
		testResult.setWrong(0);
		testResult.setIgnored(0);
		testResult.setException(0);

		assertTrue("test not run successfully", testResult.isSuccessfully());
	}

	/**
	 * Test {@link TestResult#isSuccessfully()} even with ignored.
	 */
	@Test
	public void testIsSuccessfullyAnyway() {
		TestResult testResult = new TestResult();
		testResult.setRight(1);
		testResult.setWrong(0);
		testResult.setIgnored(1);
		testResult.setException(0);

		assertTrue("test not run successfully", testResult.isSuccessfully());
	}

	/**
	 * Tests the method {@link TestResult#isNotRun()}.
	 */
	@Test
	public void testIsNotRun() {
		TestResult testResult = new TestResult();
		assertTrue("test does not run", testResult.isNotRun());
	}

	/**
	 * Tests the method {@link TestResult#isNotRun()} even with ignored.
	 */
	@Test
	public void testIsNotRunAnyway() {
		TestResult testResult = new TestResult();
		testResult.setIgnored(-1);
		assertTrue("test does not run", testResult.isNotRun());
	}

	/**
	 * Tests the management of the Result Detail lists. ActionResultTable and
	 * InstructionResultTable.
	 */
	@Test
	public void testResultDetailManagement() {
		TestResult testResult = new TestResult();
		assertNull("Expecting null if no data is set.", testResult.getActionResultTables());
		assertNull("Expecting null if no data is set.", testResult.getInstructionResultTables());
		testResult.setActionResultTables(new ArrayList<ActionResultTable>());
		testResult.setInstructionResultTables(new ArrayList<InstructionsResult>());
		assertNotNull("Expecting data is set", testResult.getActionResultTables());
		assertNotNull("Expecting data is set.", testResult.getInstructionResultTables());
	}
}
