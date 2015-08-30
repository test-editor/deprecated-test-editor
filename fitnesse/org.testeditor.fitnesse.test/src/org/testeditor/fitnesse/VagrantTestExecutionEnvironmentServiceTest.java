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

import java.io.File;
import java.util.Map;

import org.junit.Test;
import org.testeditor.core.constants.TestEditorCoreConstants;
import org.testeditor.core.model.teststructure.TestProject;
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
		assertTrue(script.contains("c:/vagrant_bin/eclipsec"));
		osIndependentChecks(script);
	}

	/**
	 * Tests the building of a script to execute it on the linux platform.
	 */
	@Test
	public void testGetExecutionScriptForLinux() {
		VagrantTestExecutionEnvironmentService testExecService = new VagrantTestExecutionEnvironmentService();
		String script = testExecService.getExecutionScriptForLinux(createTestStructure());
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
		File vagrantParentDir = new File(System.getProperty("java.io.tmpdir"), "testvagrant");
		if (vagrantParentDir.mkdir()) {
			File vagrantDir = new File(vagrantParentDir, "vagrant");
			if (vagrantDir.mkdir()) {
				File winparent = new File(vagrantDir, "windows");
				if (winparent.mkdir()) {
					new File(winparent, "Vagrantfile").createNewFile();
				}
				File linuxparent = new File(vagrantDir, "linux");
				if (linuxparent.mkdir()) {
					new File(linuxparent, "Vagrantfile").createNewFile();
				}
			}
		}
		VagrantTestExecutionEnvironmentService testExecService = new VagrantTestExecutionEnvironmentService();
		TestProject testProject = new TestProject();
		testProject.setUrl(vagrantParentDir);
		Map<String, String> configs = testExecService.getAvailableTestEnvironmentConfigs(testProject);
		assertTrue(configs.keySet().contains("localhost"));
		assertTrue(configs.keySet().contains("windows"));
		assertTrue(configs.keySet().contains("linux"));
		assertEquals("vagrant/windows", configs.get("windows"));
		assertEquals("vagrant/linux", configs.get("linux"));
	}

	/**
	 * 
	 * @return a simple TestStructure in a project.
	 */
	private TestStructure createTestStructure() {
		TestProject testProject = new TestProject();
		testProject.setName("MyPrj");
		TestSuite testSuite = new TestSuite();
		testSuite.setName("DeliverySuite");
		testProject.addChild(testSuite);
		return testSuite;
	}

}
