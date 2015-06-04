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
package org.testeditor.core.headless;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Test;
import org.osgi.service.prefs.BackingStoreException;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.model.testresult.TestResult;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.model.teststructure.TestSuite;
import org.testeditor.core.services.impl.TestProjectServiceImpl;
import org.testeditor.core.services.interfaces.TestEditorGlobalConstans;
import org.testeditor.core.services.interfaces.TestProjectService;
import org.testeditor.core.services.interfaces.TestServerService;

/**
 * 
 * Tests for HeadlessTestRunnerApplication.
 *
 */
public class HeadlessTestRunnerApplicationTest {

	/**
	 * Tests the error code depending in test result.
	 * 
	 * @throws Exception
	 *             on test failure.
	 */
	@Test
	public void testStart() throws Exception {
		final TestResult testResult = new TestResult();
		HeadlessTestRunnerApplication headlessApp = new HeadlessTestRunnerApplication() {
			@Override
			public TestResult exeucteTest(String[] args) throws BackingStoreException, IOException,
					InvalidArgumentException, URISyntaxException, SystemException, InterruptedException {
				return testResult;
			}
		};
		assertEquals(Integer.valueOf(13), headlessApp.start(null));
		testResult.setRight(2);
		assertEquals(Integer.valueOf(0), headlessApp.start(null));
	}

	/**
	 * Tests the initialization of the testeditor.
	 * 
	 * @throws Exception
	 *             on test failure.
	 */
	@Test
	public void testInitSystem() throws Exception {
		HeadlessTestRunnerApplication headlessApp = new HeadlessTestRunnerApplication();
		headlessApp.initializeSystemConfiguration();
		assertEquals("1", System.getProperty(TestEditorGlobalConstans.DEFINE_WAITS_AFTER_TEST_STEP));
		assertNotNull(System.getProperty("APPLICATION_WORK"));
	}

	/**
	 * Tests the summary creation of the testcase execution.
	 * 
	 * @throws Exception
	 *             on test failure.
	 */
	@Test
	public void testGetTestSummaryFromSimpleTest() throws Exception {
		HeadlessTestRunnerApplication headlessApp = new HeadlessTestRunnerApplication();
		TestResult testResult = new TestResult();
		testResult.setRunTimeMillis(1000);
		testResult.setRight(10);
		testResult.setWrong(0);
		testResult.setException(0);
		String result = "\n*******************************************************************\n"
				+ "Test executed with: true in 00:01s details:\n" + "0 Tests passed."
				+ "\n*******************************************************************";
		assertEquals(result, headlessApp.getTestSummaryFrom(testResult));
	}

	/**
	 * Tests the summary creation of the testsuite execution.
	 * 
	 * @throws Exception
	 *             on test failure.
	 */
	@Test
	public void testGetTestSummaryFromTestSuite() throws Exception {
		HeadlessTestRunnerApplication headlessApp = new HeadlessTestRunnerApplication();
		TestResult testResult = new TestResult();
		testResult.setRunTimeMillis(23000);
		testResult.setWrong(0);
		testResult.setException(0);
		TestResult innerResult = new TestResult();
		innerResult.setWrong(0);
		innerResult.setException(0);
		testResult.add(innerResult);
		innerResult = new TestResult();
		innerResult.setWrong(0);
		innerResult.setException(0);
		testResult.add(innerResult);
		String result = "\n*******************************************************************\n"
				+ "Test executed with: true in 00:23s details:\n" + "2 Tests passed."
				+ "\n*******************************************************************";
		assertEquals(result, headlessApp.getTestSummaryFrom(testResult));
		innerResult.setWrong(1);
		innerResult.setFullName("MyWrongTest");
		result = "\n*******************************************************************\n"
				+ "Test executed with: true in 00:23s details:\n" + "MyWrongTest	 with: 	false\n"
				+ "1 of 2 are failed." + "\n*******************************************************************";
		assertEquals(result, headlessApp.getTestSummaryFrom(testResult));
	}

	/**
	 * Test the lookup for the teststructure to execute.
	 * 
	 * @throws Exception
	 *             on test failure.
	 */
	@Test
	public void testGetTestStructureToExecute() throws Exception {
		final TestProject testProject = new TestProject();
		testProject.setName("MyProject");
		TestSuite suite = new TestSuite();
		suite.setName("TestSuite");
		testProject.addChild(suite);
		HeadlessTestRunnerApplication headlessApp = new HeadlessTestRunnerApplication() {

			@Override
			protected <S> S getService(Class<S> clazz) {
				if (clazz == TestProjectService.class) {
					return (S) new TestProjectServiceImpl() {
						@Override
						public TestProject getProjectWithName(String testProjectName) {
							return testProject;
						}
					};
				}
				if (clazz == TestServerService.class) {
					return (S) new TestServerService() {

						@Override
						public void stopTestServer(TestProject testProject) throws IOException {
						}

						@Override
						public void startTestServer(TestProject testProject) throws IOException, URISyntaxException {
						}

						@Override
						public boolean isRunning(TestProject testProject) {
							return false;
						}
					};
				}
				return super.getService(clazz);
			}

		};
		String[] args = new String[] { HeadlessTestRunnerApplication.EXECUTE_TEST + "=MyProject.TestSuite" };
		TestStructure testStructure = headlessApp.getTestStructureToExecute(args);
		assertSame(suite, testStructure);
		assertSame(testProject, testStructure.getRootElement());
	}

}
