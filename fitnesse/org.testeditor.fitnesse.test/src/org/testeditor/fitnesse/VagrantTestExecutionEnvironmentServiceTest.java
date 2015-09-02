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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Test;
import org.testeditor.core.constants.TestEditorCoreConstants;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestProjectConfig;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.model.teststructure.TestSuite;

/**
 * Modul tests for the vagrant TextExecution Environment Service.
 *
 */
public class VagrantTestExecutionEnvironmentServiceTest {

	/**
	 * Tests the building of a script to execute it on the windows platform.
	 */
	@Test
	public void testGetExecutionScriptForWindows() {
		VagrantTestExecutionEnvironmentService testExecService = new VagrantTestExecutionEnvironmentService();
		String script = testExecService.getExecutionScriptForWindows(createTestStructure());
		assertTrue(script.contains("c:/vagrant_bin/jre/bin/java"));
		osIndependentChecks(script);
	}

	/**
	 * Tests the building of a script to execute it on the linux platform.
	 */
	@Test
	public void testGetExecutionScriptForLinux() {
		VagrantTestExecutionEnvironmentService testExecService = new VagrantTestExecutionEnvironmentService();
		String script = testExecService.getExecutionScriptForLinux(createTestStructure(), false);
		assertTrue(script.contains("export DISPLAY="));
		assertTrue(script.contains("sudo /usr/bin/java"));
		osIndependentChecks(script);
	}

	/**
	 * Checks commons from different scripts.
	 * 
	 * @param script
	 *            to be verified.
	 */
	private void osIndependentChecks(String script) {
		assertTrue(script.contains("ExecuteTest=MyPrj.DeliverySuite"));
		assertTrue(script.contains("-application org.testeditor.core.headlesstestrunner"));
		assertTrue(script.contains("echo TE terminated.\n"));
	}

	/**
	 * Test that in the avilable configs is the localhost.
	 * 
	 * @throws Exception
	 *             on test failure
	 */
	@Test
	public void testGetAvailableTestExecEnvnamesContainsHost() throws Exception {
		VagrantTestExecutionEnvironmentService testExecService = new VagrantTestExecutionEnvironmentService();
		TestProject testProject = new TestProject();
		testProject.setUrl(new File(System.getProperty("java.io.tmpdir"), "testDir"));
		assertTrue(testExecService.getAvailableTestEnvironmentConfigs(testProject).keySet().contains("localhost"));
		assertEquals(TestEditorCoreConstants.NONE_TEST_AGENT,
				testExecService.getAvailableTestEnvironmentConfigs(testProject).get("localhost"));
	}

	/**
	 * 
	 * Test the reading of a file system to get the directories with vagrant
	 * files as available configs.
	 * 
	 * @throws Exception
	 *             on test failure
	 */
	@Test
	public void testGetAvailableTestExecEnvnamesFindsVagrantFiles() throws Exception {
		TestProject testProject = createTestProjectWithVagrantFiles();
		VagrantTestExecutionEnvironmentService testExecService = new VagrantTestExecutionEnvironmentService();
		Map<String, String> configs = testExecService.getAvailableTestEnvironmentConfigs(testProject);
		assertTrue(configs.keySet().contains("localhost"));
		assertTrue(configs.keySet().contains("windows"));
		assertTrue(configs.keySet().contains("linux"));
		assertEquals("vagrant/windows", configs.get("windows"));
		assertEquals("vagrant/linux", configs.get("linux"));
	}

	/**
	 * Tests that the implementation still works, without a properties file.
	 * 
	 * @throws Exception
	 *             on test failure.
	 */
	@Test
	public void testSetTestExecEnvSpeceficVariablesWithoutTEPropertyFile() throws Exception {
		File onlyVagrantDir = new File(System.getProperty("java.io.tmpdir"), "onlyvagrant");
		if (!onlyVagrantDir.mkdir()) {
			throw new RuntimeException("can't create testdirectory");
		}
		VagrantTestExecutionEnvironmentService testExecService = new VagrantTestExecutionEnvironmentService();
		try {
			testExecService.setSpecificGlobalVariables(onlyVagrantDir);
		} catch (Exception e) {
			fail("No error on empty directory expected.");
		}
	}

	/**
	 * Test the read and setting of the properties from file.
	 * 
	 * @throws Exception
	 *             on test failure.
	 */
	@Test
	public void testSetTestExecEnvSpeceficVariablesReadFromTEPropertyFile() throws Exception {
		File vagrantAndProps = new File(System.getProperty("java.io.tmpdir"), "vagrantandteprops");
		if (!vagrantAndProps.mkdir()) {
			throw new RuntimeException("can't create testdirectory");
		}
		File props = new File(vagrantAndProps, VagrantTestExecutionEnvironmentService.TE_PROPERTIES_FILENAME);
		FileUtils.writeLines(props, Arrays.asList(new String[] { "myProp=foo" }));
		System.setProperty("myProp", "bar");
		VagrantTestExecutionEnvironmentService testExecService = new VagrantTestExecutionEnvironmentService();
		testExecService.setSpecificGlobalVariables(vagrantAndProps);
		assertEquals("foo", System.getProperty("myProp"));
		testExecService.resetProperties();
		assertEquals("bar", System.getProperty("myProp"));
	}

	/**
	 * Creates a Testproejct and a directory structure with vagrant files.
	 * 
	 * @return test project that url leads to a file system.
	 * @throws Exception
	 *             on creation failure.
	 */
	private TestProject createTestProjectWithVagrantFiles() throws Exception {
		File vagrantParentDir = new File(System.getProperty("java.io.tmpdir"), "testvagrant");
		if (vagrantParentDir.mkdir()) {
			File vagrantDir = new File(vagrantParentDir, "vagrant");
			if (vagrantDir.mkdir()) {
				File winparent = new File(vagrantDir, "windows");
				File osFile = null;
				if (winparent.mkdir()) {
					osFile = new File(winparent, "Vagrantfile");
				}
				File linuxparent = new File(vagrantDir, "linux");
				if (linuxparent.mkdir()) {
					osFile = new File(linuxparent, "Vagrantfile");
				}
				if (!osFile.createNewFile()) {
					throw new RuntimeException("can't create test file");
				}
			}
		}
		TestProject testProject = new TestProject();
		testProject.setUrl(vagrantParentDir);
		return testProject;
	}

	/**
	 * 
	 * @return a simple TestStructure in a project.
	 */
	private TestStructure createTestStructure() {
		TestProject testProject = new TestProject();
		testProject.setTestProjectConfig(new TestProjectConfig());
		testProject.setName("MyPrj");
		TestSuite testSuite = new TestSuite();
		testSuite.setName("DeliverySuite");
		testProject.addChild(testSuite);
		return testSuite;
	}

	/**
	 * Deletes Temp directories.
	 * 
	 * @throws IOException
	 *             on cleanup.
	 */
	@After
	public void tearDown() throws IOException {
		File file = new File(System.getProperty("java.io.tmpdir"), "testvagrant");
		deleteDir(file);
		file = new File(System.getProperty("java.io.tmpdir"), "onlyvagrant");
		deleteDir(file);
		file = new File(System.getProperty("java.io.tmpdir"), "vagrantandteprops");
		deleteDir(file);
	}

	/**
	 * Delete directory if exists.
	 * 
	 * @param dir
	 *            to be deleted.
	 * @throws IOException
	 *             on delete.
	 */
	private void deleteDir(File dir) throws IOException {
		if (dir.exists()) {
			FileUtils.deleteDirectory(dir);
		}
	}

}
