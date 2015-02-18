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

package org.testeditor.dashboard.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.testeditor.dashboard.AllRunsResult;
import org.testeditor.dashboard.TestResult;

/**
 * Tests for {@link TestResult} and {@link AllRunsResult}.
 * 
 * @author nalpert
 * 
 */
public final class TestResultTest {

	/**
	 * Tests the children handling of a suite.
	 */
	@Test
	public void suiteManagesChildsCorrectly() {

		TestResult suite = suiteInstance("Suite 1", 1560, "23/10/2014", 1, 2, 3);
		TestResult firstTestCase = testCaseInstance("Test 1", 120, "11/10/2014", 4, 5, 6);
		suite.add(firstTestCase);

		assertEquals(1, suite.getChilds().size());

		TestResult secondTestCase = testCaseInstance("Test 2", 76, "12/10/2014", 2, 2, 5);
		suite.add(secondTestCase);

		assertEquals(2, suite.getChilds().size());

		assertTrue(suite.getChilds().contains(firstTestCase));
		assertTrue(suite.getChilds().contains(secondTestCase));
	}

	/**
	 * Tests the attribute handing.
	 */
	@Test
	public void testResultHandlesAttributesCorrrectly() {

		TestResult tr = new TestResult();
		fillTestResult(tr, "MyName", 3542, "05/03/2002", 4, 7, 11);
		tr.setResult("OK");

		assertEquals("MyName", tr.getName());
		assertEquals(3542, tr.getDuration());
		assertEquals("05/03/2002", tr.getDate());
		assertEquals(4, tr.getQuantityRight());
		assertEquals(7, tr.getQuantityWrong());
		assertEquals(11, tr.getRunCount());
		assertEquals("OK", tr.getResult());
	}

	/**
	 * Returns a new suite instance.
	 * 
	 * @param suiteName
	 *            the name of the suite
	 * @param duration
	 *            the duration
	 * @param date
	 *            a date (e.g. "23/10/2014")
	 * @param quantityRight
	 *            the quantity of right test steps
	 * @param quantityWrong
	 *            the quantity of wrong test steps
	 * @param runCount
	 *            the count of all test steps
	 * @return a new suite instance
	 */
	private static TestResult suiteInstance(String suiteName, int duration, String date, int quantityRight,
			int quantityWrong, int runCount) {

		TestResult suite = new TestResult();
		fillTestResult(suite, suiteName, duration, date, quantityRight, quantityWrong, runCount);
		suite.setSuite(true);
		return suite;
	}

	/**
	 * Returns a new test case instance.
	 * 
	 * @param testCaseName
	 *            the name of the test case
	 * @param duration
	 *            the duration
	 * @param date
	 *            a date (e.g. "23/10/2014")
	 * @param quantityRight
	 *            the quantity of right test steps
	 * @param quantityWrong
	 *            the quantity of wrong test steps
	 * @param runCount
	 *            the count of all test steps
	 * @return a new test case instance
	 */
	private static TestResult testCaseInstance(String testCaseName, int duration, String date, int quantityRight,
			int quantityWrong, int runCount) {

		TestResult testCase = new TestResult();
		fillTestResult(testCase, testCaseName, duration, date, quantityRight, quantityWrong, runCount);
		testCase.setTestcase(true);
		return testCase;
	}

	/**
	 * Fill test result values
	 * 
	 * @param tr
	 *            test result to fill
	 * @param name
	 *            the name
	 * @param duration
	 *            the duration
	 * @param date
	 *            a date (e.g. "23/10/2014")
	 * @param quantityRight
	 *            the quantity of right test steps
	 * @param quantityWrong
	 *            the quantity of wrong test steps
	 * @param runCount
	 *            the count of all test steps
	 */
	private static void fillTestResult(TestResult tr, String name, int duration, String date, int quantityRight,
			int quantityWrong, int runCount) {
		tr.setDate(date);
		tr.setDuration(duration);
		tr.setName(name);
		tr.setQuantityRight(quantityRight);
		tr.setQuantityWrong(quantityWrong);
		tr.setRunCount(runCount);
	}
}
