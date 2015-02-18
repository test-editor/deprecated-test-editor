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
package org.testeditor.fitnesse.resultreader;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.junit.Test;
import org.testeditor.core.model.testresult.TestResult;
import org.testeditor.core.model.teststructure.TestType;

/**
 * Tests the FitNesseResultReaderTest.
 */
public class FitNesseResultReaderTest {

	/**
	 * Test result xml of single test case.
	 * 
	 * @throws IOException
	 *             while file-operation
	 */
	@Test
	public void testTestResultXml() throws IOException {

		InputStream fileInputStream = FitNesseTestResultReader.class.getResourceAsStream("/test_result.xml");

		FitNesseResultReader reader = FitNesseResultReaderFactory.getReader(TestType.TEST);

		TestResult resultFile = reader.readTestResult(fileInputStream);

		assertEquals("sum of Rights have to be 0", 0, resultFile.getRight());
		assertEquals("sum of Wrong have to be 1", 1, resultFile.getWrong());
		assertEquals("sum of ignores have to be 10", 10, resultFile.getIgnored());
		assertEquals("sum of exceptions have to be 1", 1, resultFile.getException());
		assertEquals("runtime have to be 1445 ms", 1445, resultFile.getRunTimeMillis());

		fileInputStream.close();

	}

	/**
	 * Tests final counts of result xml of a suite.
	 * 
	 * @throws IOException
	 *             while file-operation
	 * 
	 */
	@Test
	public void testSuiteResultFinalCounts() throws IOException {

		InputStream fileInputStream = FitNesseTestResultReader.class.getResourceAsStream("/suite_result.xml");

		FitNesseResultReader reader = FitNesseResultReaderFactory.getReader(TestType.SUITE);

		TestResult suiteResult = reader.readTestResult(fileInputStream);

		// suite counts
		assertEquals("sum of Rights have to be 1", 1, suiteResult.getRight());
		assertEquals("sum of Wrong have to be 1", 1, suiteResult.getWrong());
		assertEquals("sum of ignores have to be 0", 0, suiteResult.getIgnored());
		assertEquals("sum of exceptions have to be 1", 1, suiteResult.getException());

		fileInputStream.close();

	}

	/**
	 * Tests the result of an suite. given result xml has 3 testcases.
	 * 
	 * @throws IOException
	 *             while file-operation
	 * 
	 */
	@Test
	public void testSuiteResultCountOfRunningTest() throws IOException {

		InputStream fileInputStream = FitNesseTestResultReader.class.getResourceAsStream("/suite_result.xml");

		FitNesseResultReader reader = FitNesseResultReaderFactory.getReader(TestType.SUITE);

		TestResult suiteResult = reader.readTestResult(fileInputStream);

		assertEquals("count of suite test have to be 3", 3, suiteResult.getChildren().size());

		// Testcase 1
		TestResult testResult = suiteResult.getChildren().get(0);
		assertEquals("DemoWebTests.LocalDemoSuite.LoginSuite.LoginInvalidClearTest", testResult.getFullName());
		assertEquals(5, testResult.getRight());
		assertEquals(0, testResult.getWrong());
		assertEquals(0, testResult.getIgnored());
		assertEquals(10, testResult.getException());

		// Testcase 2
		testResult = suiteResult.getChildren().get(1);
		assertEquals("DemoWebTests.LocalDemoSuite.LoginSuite.LoginInvalidTest", testResult.getFullName());
		assertEquals(13, testResult.getRight());
		assertEquals(0, testResult.getWrong());
		assertEquals(0, testResult.getIgnored());
		assertEquals(0, testResult.getException());

		// Testcase 3
		testResult = suiteResult.getChildren().get(2);
		assertEquals("DemoWebTests.LocalDemoSuite.LoginSuite.LoginValidTest", testResult.getFullName());
		assertEquals(11, testResult.getRight());
		assertEquals(2, testResult.getWrong());
		assertEquals(0, testResult.getIgnored());
		assertEquals(0, testResult.getException());

		fileInputStream.close();
	}

	/**
	 * Tests the getTestHistorie-method of a test.
	 * 
	 * @throws IOException
	 *             while file-operation
	 */
	@Test
	public void testGetTestResultHistorie() throws IOException {

		InputStream fileInputStream = FitNesseResultReaderTest.class.getResourceAsStream("/test_result_historie.xml");

		FitNesseResultReader reader = FitNesseResultReaderFactory.getReader(TestType.TEST);

		List<TestResult> resultFiles = reader.readResultHistory(fileInputStream);

		TestResult resultFile = resultFiles.get(0);
		assertEquals("LoginInvalidClearTest", resultFile.getFullName());
		assertEquals("sum of Rights have to be 10", 10, resultFile.getRight());
		assertEquals("sum of Wrong have to be 0", 0, resultFile.getWrong());
		resultFile = resultFiles.get(2);
		assertEquals("sum of Rights have to be 0", 0, resultFile.getRight());
		assertEquals("sum of Wrong have to be 2", 2, resultFile.getWrong());

		fileInputStream.close();

	}

	/**
	 * Tests the getTestHistorie-method of a test.
	 * 
	 * @throws IOException
	 *             while file-operation
	 */
	@Test
	public void testGetSuiteResultHistorie() throws IOException {

		InputStream fileInputStream = FitNesseResultReaderTest.class.getResourceAsStream("/suite_result_historie.xml");

		FitNesseResultReader reader = FitNesseResultReaderFactory.getReader(TestType.SUITE);

		List<TestResult> resultFiles = reader.readResultHistory(fileInputStream);

		TestResult resultFile = resultFiles.get(0);
		assertEquals("LoginSuite", resultFile.getFullName());
		assertEquals("DemoWebTests.LocalDemoSuite.LoginSuite?pageHistory&resultDate=20131121132430",
				resultFile.getResultLink());
		assertEquals("sum of Rights have to be 2", 2, resultFile.getRight());
		assertEquals("sum of Wrong have to be 1", 1, resultFile.getWrong());
		resultFile = resultFiles.get(2);
		assertEquals("sum of Rights have to be 2", 2, resultFile.getRight());
		assertEquals("sum of Wrong have to be 1", 1, resultFile.getWrong());

		fileInputStream.close();

	}
}
