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
package org.testeditor.core.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.osgi.service.event.Event;
import org.testeditor.core.constants.TestEditorCoreEventConstants;
import org.testeditor.core.model.testresult.TestResult;
import org.testeditor.core.model.teststructure.TestCase;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestSuite;

/**
 * Modul Test for TestProtocolService.
 * 
 */
public class TestStateProtocolServiceTest {

	/**
	 * Test run one Test successfully.
	 */
	@Test
	public void testRunsSuccessfully() {
		TestStateProtocolService protocolService = new TestStateProtocolService();
		TestCase testCase = new TestCase();
		testCase.setName("TestCase1");
		protocolService.set(testCase, new TestResult());

		TestResult testResult = protocolService.get(testCase);

		assertNotNull(testResult);
		assertTrue(!testResult.isSuccessfully());

	}

	/**
	 * Test run one Test not successfully.
	 */
	@Test
	public void testRunsNotSuccessfully() {
		TestStateProtocolService protocolService = new TestStateProtocolService();
		TestResult testResult = new TestResult();
		testResult.setException(0);
		testResult.setIgnored(0);
		testResult.setRight(0);
		testResult.setWrong(1);

		TestCase testCase = new TestCase();
		testCase.setName("TestCase2");
		protocolService.set(testCase, testResult);

		testResult = protocolService.get(testCase);

		assertNotNull(testResult);
		assertFalse(testResult.isSuccessfully());

	}

	/**
	 * Test was never run before.
	 */
	@Test
	public void testNeverRuns() {

		TestResult testResult = new TestResult();
		testResult.setWrong(1);

		TestCase testCase = new TestCase();
		testCase.setName("TestCase3");

		testResult = new TestStateProtocolService().get(testCase);

		assertNull(testResult);

	}

	/**
	 * Test the Remove of Tests and Test by name. Changing a Testcase in an
	 * Editor should remove the testcase from the TestProtocolService to
	 * indicate, that the changed test is'nt run yet.
	 */
	@Test
	public void testRemoveChangedTestCaseFromProtocol() {
		TestStateProtocolService protocolService = new TestStateProtocolService();
		TestCase testCase = new TestCase();
		testCase.setName("TestCase1");
		protocolService.set(testCase, new TestResult());
		assertNotNull("Expecting a testcase on the protocol service.", protocolService.get(testCase));
		protocolService.remove(testCase);
		assertNull("Expecting that the testcase is removed from  the protocol service.", protocolService.get(testCase));
	}

	/**
	 * Test Protocol on execute a TestSuite that has Children.
	 */
	@Test
	public void testRunSuiteWithChildren() {
		TestStateProtocolService protocolService = new TestStateProtocolService();
		TestCase testCase = new TestCase();
		testCase.setName("MyName");
		TestSuite testSuite = new TestSuite();
		testSuite.addChild(testCase);
		TestResult testResult = new TestResult();
		TestResult tcTestResult = new TestResult();
		tcTestResult.setFullName(testCase.getFullName());
		testResult.add(tcTestResult);
		assertTrue(testResult.isSuite());
		protocolService.set(testSuite, testResult);
		assertNotNull(protocolService.get(testSuite));
		assertNotNull(protocolService.get(testCase));
	}

	/**
	 * Test Protocol on execute a TestSuite that contains referred Testcases.
	 */
	@Test
	public void testRunSuiteWithReferedTestCases() {
		TestStateProtocolService protocolService = new TestStateProtocolService();
		TestResult testResult = new TestResult();
		TestResult tcTestResult = new TestResult();
		tcTestResult.setFullName("MyName");
		testResult.add(tcTestResult);
		TestSuite testSuite = new TestSuite();
		protocolService.set(testSuite, testResult);
		assertNotNull(protocolService.get(testSuite));
		TestCase testCase = new TestCase();
		testCase.setName("MyName");
		testSuite.addReferredTestStructure(testCase);
		assertNotNull(protocolService.get(testCase));
	}

	/**
	 * Test Protocol on execute a TestSuite that contains referred Testcases.
	 */
	@Test
	public void testRemoveChangedReferedTestCaseFromProtocol() {
		TestStateProtocolService protocolService = new TestStateProtocolService();
		TestResult testResult = new TestResult();
		TestResult tcTestResult = new TestResult();
		tcTestResult.setFullName("MyName");
		testResult.add(tcTestResult);
		TestSuite testSuite = new TestSuite();
		protocolService.set(testSuite, testResult);
		TestCase testCase = new TestCase();
		testCase.setName("MyName");
		testSuite.addReferredTestStructure(testCase);
		assertNotNull(protocolService.get(testCase));
		protocolService.remove(testCase);
		assertNull(protocolService.get(testCase));
	}

	/**
	 * Test delete of elements with fullqualified name and removing child
	 * elements on removing their parent.
	 */
	@Test
	public void testDeleteEventHandlerOnFullNameAndParentName() {
		TestStateProtocolService protocolService = new TestStateProtocolService();
		TestProject tp = new TestProject();
		tp.setName("TP");
		TestCase tc = new TestCase();
		tp.addChild(tc);
		protocolService.set(tc, new TestResult());
		assertNotNull(protocolService.get(tc));
		Map<String, String> properties = new HashMap<String, String>();
		properties.put("org.eclipse.e4.data", tc.getFullName());
		Event event = new Event(TestEditorCoreEventConstants.TESTSTRUCTURE_MODEL_CHANGED_DELETED, properties);
		protocolService.getDeletedTestStructureEventHandler().handleEvent(event);
		assertNull(protocolService.get(tc));
		protocolService.set(tc, new TestResult());
		assertNotNull(protocolService.get(tc));
		properties.put("org.eclipse.e4.data", tp.getFullName());
		event = new Event(TestEditorCoreEventConstants.TESTSTRUCTURE_MODEL_CHANGED_DELETED, properties);
		protocolService.getDeletedTestStructureEventHandler().handleEvent(event);
		assertNull("Deleting project also deletes child testcase.", protocolService.get(tc));
	}

	/**
	 * Tests that the Service still works on unknown projects with an zero
	 * information.
	 */
	@Test
	public void testGetZeroUpdatesOnunknownProject() {
		TestStateProtocolService protocolService = new TestStateProtocolService();
		assertEquals(0, protocolService.getAvailableUpdatesFor(new TestProject()));
	}

}
