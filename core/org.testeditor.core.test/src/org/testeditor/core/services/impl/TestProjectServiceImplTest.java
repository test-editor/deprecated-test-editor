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
package org.testeditor.core.services.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.Test;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.model.teststructure.TestCase;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestProjectConfig;
import org.testeditor.core.model.teststructure.TestSuite;
import org.testeditor.core.services.interfaces.TestExecutionEnvironmentService;

/**
 * 
 * Modul tests for TestProjectServiceImpl.
 * 
 */
public class TestProjectServiceImplTest {
	private static final String PROJECT_NAME = "MyProject";

	/**
	 * Tests that the TestProjectConfig after convert to and back from
	 * properties is equals to the initial config.
	 * 
	 * @throws IOException
	 *             on reading the configuration
	 */
	@Test
	public void testEqualsAfterConvertToAndFromProperties() throws IOException {
		TestProjectConfig projectConfig = new TestProjectConfig();
		TestProjectServiceImpl service = new TestProjectServiceImpl();
		Properties properties = service.getPropertiesFrom(projectConfig);
		TestProjectConfig cfgAfterStoring = service.getTestProjectConfigFrom(properties, PROJECT_NAME);
		assertEquals("TestProjectConfig equals the loaded one.", projectConfig, cfgAfterStoring);
	}

	/**
	 * 
	 * Test the Search for a TestProject by name.
	 * 
	 * @throws Exception
	 *             for Test
	 */
	@Test
	public void testGetTestProjectByName() throws Exception {
		TestProjectServiceImpl service = new TestProjectServiceImpl() {
			@Override
			public List<TestProject> getProjects() {
				List<TestProject> list = new ArrayList<TestProject>();
				TestProject tp = new TestProject();
				tp.setName("Hello");
				list.add(tp);
				tp = new TestProject();
				tp.setName("MyTestProject");
				list.add(tp);
				tp = new TestProject();
				tp.setName("FooBar");
				list.add(tp);
				return list;
			}
		};
		TestProject project = service.getProjectWithName("MyTestProject");
		assertEquals("Expecting project found.", "MyTestProject", project.getName());
	}

	/**
	 * 
	 * Test the Search for a TestProject by name.
	 * 
	 * @throws Exception
	 *             for Test
	 */
	@Test
	public void testGetTestProjectByNameOnNotExisitngProject() throws Exception {
		TestProjectServiceImpl service = new TestProjectServiceImpl() {
			@Override
			public List<TestProject> getProjects() {
				List<TestProject> list = new ArrayList<TestProject>();
				TestProject tp = new TestProject();
				tp.setName("Hello");
				list.add(tp);
				list.add(tp);
				tp = new TestProject();
				tp.setName("FooBar");
				list.add(tp);
				return list;
			}
		};
		TestProject project = service.getProjectWithName("MyTestProjejct");
		assertNull("Expecting project not found.", project);
	}

	/**
	 * 
	 * Tests the lookup for a Teststructiure by full name.
	 * 
	 * @throws Exception
	 *             for Test
	 */
	@Test
	public void testFindTestStructureByFullName() throws Exception {
		List<TestProject> list = new ArrayList<TestProject>();
		TestProject tp = new TestProject();
		tp.setName("Hello");
		TestSuite suite = new TestSuite();
		suite.setName("TestSuite");
		tp.addChild(suite);
		TestCase testCase = new TestCase();
		testCase.setName("TestCase");
		suite.addChild(testCase);
		list.add(tp);
		list.add(tp);
		tp = new TestProject();
		tp.setName("FooBar");
		list.add(tp);
		TestProjectServiceImpl service = getTestProjectImplMockWithProjects(list);

		assertNotNull(service.findTestStructureByFullName("Hello.TestSuite.TestCase"));
		assertNotNull(service.findTestStructureByFullName("Hello.TestSuite"));
	}

	/**
	 * Tests if an project is in the list.
	 */
	@Test
	public void testExistsProjectWithName() {
		TestProject tp = new TestProject();
		tp.setName("MyTp");
		List<TestProject> tpList = new ArrayList<TestProject>();
		tpList.add(tp);
		TestProjectServiceImpl testProjectService = getTestProjectImplMockWithProjects(tpList);
		assertTrue(testProjectService.existsProjectWithName("MyTp"));
		assertFalse(testProjectService.existsProjectWithName("AnotherTp"));
	}

	/**
	 * Tests the setting of values from properties.
	 * 
	 * @throws IOException
	 *             - Exception during properties access.
	 * 
	 */
	@Test
	public void testSetValuesOnProjectConfig() throws IOException {
		TestProjectServiceImpl testProjectService = new TestProjectServiceImpl();
		Properties properties = new Properties();
		TestProjectConfig testProjectConfig = testProjectService.getTestProjectConfigFrom(properties, "test");
		assertEquals("localhost", testProjectConfig.getTestEnvironmentConfiguration());
		assertEquals("fitnesse_based_1.2", testProjectConfig.getTestServerID());

		properties.setProperty("testautomat.serverid", "server");
		testProjectConfig = testProjectService.getTestProjectConfigFrom(properties, "test");
		assertEquals("server", testProjectConfig.getTestServerID());

		properties.setProperty(TestExecutionEnvironmentService.CONFIG, "linux");
		testProjectConfig = testProjectService.getTestProjectConfigFrom(properties, "test");
		assertEquals("linux", testProjectConfig.getTestEnvironmentConfiguration());
	}

	/**
	 * Get TestProjectImplMock with projects.
	 * 
	 * @param projectList
	 *            list of projects
	 * @return TestProjectSvericeImpl
	 */
	private TestProjectServiceImpl getTestProjectImplMockWithProjects(final List<TestProject> projectList) {
		return new TestProjectServiceImpl() {
			@Override
			public List<TestProject> getProjects() {
				return projectList;
			}

			@Override
			protected void renameProjectInFileSystem(TestProject testProject, String newName)
					throws SystemException, IOException {
				getProject(testProject.getName()).setName(newName);
			}
		};
	}

}
