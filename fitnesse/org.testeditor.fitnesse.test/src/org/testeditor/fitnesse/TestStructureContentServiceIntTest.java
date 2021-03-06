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
package org.testeditor.fitnesse;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testeditor.core.model.teststructure.TestCompositeStructure;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestProjectConfig;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.model.teststructure.TestSuite;
import org.testeditor.core.services.interfaces.ServiceLookUpForTest;
import org.testeditor.core.services.interfaces.TestServerService;
import org.testeditor.core.services.interfaces.TestStructureContentService;

/**
 * Tests the test structure service.
 */
public class TestStructureContentServiceIntTest {

	private static TestStructureContentService testStructureContentService;

	/**
	 * Setup for all tests.
	 * 
	 * @throws Exception
	 *             for test
	 */
	@BeforeClass
	public static void beforeClass() throws Exception {
		testStructureContentService = ServiceLookUpForTest.getService(TestStructureContentService.class);
		ServiceLookUpForTest.getService(TestServerService.class).startTestServer(getTestProject());
	}

	/**
	 * Shutdown Server.
	 * 
	 * @throws Exception
	 *             for Test
	 */
	@AfterClass
	public static void shutDown() throws Exception {
		ServiceLookUpForTest.getService(TestServerService.class).stopTestServer(getTestProject());
	}

	/**
	 * 
	 * @return TestProject With Config for Test.
	 */
	private static TestProject getTestProject() {
		TestProject tp = new TestProject();
		tp.setName("DemoWebTests");
		TestProjectConfig testProjectConfig = new TestProjectConfig();
		tp.setTestProjectConfig(testProjectConfig);
		return tp;
	}

	/**
	 * Tests the "get content" for all test cases at the given project.
	 * 
	 * @throws Exception
	 *             occurs if any system exception is thrown
	 */
	@Test
	public void testGetTestCaseContent() throws Exception {
		TestProject testProject = getTestProject();
		getContentForAllTestCaseChildren(testProject);
	}

	/**
	 * Invokes the internal service to get the content of each test case inside
	 * the given test suite structure.
	 * 
	 * @param testSuite
	 *            root test suite
	 * @throws Exception
	 *             occurs if any system exception is thrown
	 */
	private void getContentForAllTestCaseChildren(TestCompositeStructure testSuite) throws Exception {
		for (TestStructure testStructure : testSuite.getTestChildren()) {
			if (testStructure instanceof TestSuite) {
				getContentForAllTestCaseChildren((TestSuite) testStructure);
			} else {
				testStructureContentService.refreshTestCaseComponents(testStructure);
			}
		}
	}

}
