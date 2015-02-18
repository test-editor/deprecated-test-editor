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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.model.teststructure.TestCase;
import org.testeditor.core.model.teststructure.TestCompositeStructure;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestProjectConfig;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.model.teststructure.TestSuite;
import org.testeditor.core.services.interfaces.ServiceLookUpForTest;
import org.testeditor.core.services.interfaces.TestServerService;
import org.testeditor.core.services.interfaces.TestStructureContentService;
import org.testeditor.core.services.interfaces.TestStructureService;

/**
 * Tests the test structure service.
 */
public class TestStructureServiceTest {

	private static TestStructureService testStructureService;
	private static TestStructureContentService testStructureContentService;

	/**
	 * Setup for all tests.
	 * 
	 * @throws Exception
	 *             for test
	 */
	@BeforeClass
	public static void beforeClass() throws Exception {
		testStructureService = ServiceLookUpForTest.getService(TestStructureService.class);
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

	/**
	 * Tests the Function to create, rename and remove a TestStructure.
	 * 
	 * @throws SystemException
	 *             for Test
	 */
	@Test
	@Ignore
	// No Centent found on server after move fitnesse lib to fixturelib
	public void testCreateRenameRemoveTestStructure() throws SystemException {
		// Initializing for the Test
		final String name = "JuniTestCreateTestStructure";
		final String newName = "JuniTestRemoveTestStructure";
		// final int parent = 0;
		TestStructure testStructure = new TestCase();
		testStructure.setName(name);

		TestProject testProject = getTestProject();
		testStructureService.loadTestStructuresChildrenFor(testProject);
		int beforeCreate = testProject.getTestChildren().size();
		testProject.addChild(testStructure);

		// Tests the Function to create the new testStructure
		testStructureService.createTestStructure(testStructure);

		testProject = getTestProject();
		testStructureService.loadTestStructuresChildrenFor(testProject);
		int afterCreate = testProject.getTestChildren().size();
		assertTrue("testStructure was not created", beforeCreate < afterCreate);

		// Tests the Function to rename the new testStructre
		testStructureService.renameTestStructure(testStructure, newName);
		// TestSuite testSuiteCheck =
		// (TestSuite)testStructureService.getTestStructures().get(parent);
		// TODO for-schleife um das element wieder zu finden
		// String checkNewName =
		// testSuiteCheck.getTestChildren().get(afterCreate - 1).getName();
		// assertTrue("testStructure was not renamed",
		// newName.equals(checkNewName));

		testStructure.setName(newName);
		// Tests the Function to remove the new testStructre
		testStructureService.removeTestStructure(testStructure);

		testProject = getTestProject();
		testStructureService.loadTestStructuresChildrenFor(testProject);
		afterCreate = testProject.getTestChildren().size();
		assertTrue("testStructure was not removed", beforeCreate == afterCreate);

	}

	/**
	 * Tests the get test structure.
	 * 
	 * @throws SystemException
	 *             is thrown if a system exception occurred (e.g. third party
	 *             system unavailable)
	 */
	@Test
	@Ignore
	// No Centent found on server after move fitnesse lib to fixturelib
	public void testLoadTestStructuresChildrenFor() throws SystemException {
		TestProject projectMock = getTestProject();
		testStructureService.loadTestStructuresChildrenFor(projectMock);
		List<TestStructure> testStructures = projectMock.getTestChildren();

		assertNotNull("test struture is null", testStructures);
		assertTrue("test structure is empty", !testStructures.isEmpty());

		assertTrue("invalid size of children", projectMock.getTestChildren().size() > 0);
	}

	/**
	 * Tests the Storage of a old name under the new fullname as key.
	 */
	@Test
	public void testStoreOldNameOnTheFullNewNameAsKey() {
		TestStructureServiceImpl service = new TestStructureServiceImpl();
		TestProject tp = new TestProject();
		tp.setName("TestPrj");
		TestSuite suite = new TestSuite();
		suite.setName("MySuite");
		tp.addChild(suite);
		service.storeOldNameOnTheFullNewNameAsKey(suite, "NewSuite");
		assertTrue(service.getRenamedTestStructures().containsKey("TestPrj.NewSuite"));
		assertEquals("MySuite", service.getRenamedTestStructures().get("TestPrj.NewSuite"));
	}

}
