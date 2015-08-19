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

import static org.junit.Assert.assertTrue;

import org.junit.Test;
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
		assertTrue(script.contains("c:/vagrant_bin/jre/bin/java"));
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
