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
package org.testeditor.fitnesse.filesystem;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.Platform;
import org.junit.Test;
import org.testeditor.core.model.teststructure.TestCase;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestProjectConfig;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.model.teststructure.TestSuite;
import org.testeditor.core.util.FileUtils;

/**
 * Tests for the FitnesseFileSystemUtility to lookup an access Files in the
 * Workspace.
 * 
 */
public class FitnesseFileSystemUtilityTest {

	/**
	 * Tests the lookup of the Path to the teststructure.
	 */
	@Test
	public void testGetPathToTestStructure() {
		TestStructure testStructure = createTestStructureForTest();
		String pathPart = File.separator + "FitNesseRoot" + File.separator
				+ "MyTestPrj" + File.separator + "MySuite" + File.separator
				+ "ATestCase";
		assertTrue(
				"Path ends with",
				FitnesseFileSystemUtility.getPathToTestStructureDirectory(
						testStructure).endsWith(pathPart));
	}

	/**
	 * Test the Lookup of the Error path.
	 */
	@Test
	public void testGetPathToTestStructureError() {
		TestStructure testStructure = createTestStructureForTest();
		String pathToTestStructureErrorDirectory = FitnesseFileSystemUtility
				.getPathToTestStructureErrorDirectory(testStructure);
		String pathPart = File.separator + "FitNesseRoot" + File.separator
				+ "ErrorLogs" + File.separator + "MyTestPrj" + File.separator
				+ "MySuite" + File.separator + "ATestCase";
		assertTrue("Path ends with",
				pathToTestStructureErrorDirectory.endsWith(pathPart));
	}

	/**
	 * 
	 * @return TestStructure used in the tests.
	 */
	private TestStructure createTestStructureForTest() {
		TestProject testProject = new TestProject();
		testProject.setName("MyTestPrj");
		TestSuite testSuite = new TestSuite();
		testSuite.setName("MySuite");
		TestStructure testStructure = new TestCase();
		testStructure.setName("ATestCase");
		testProject.addChild(testSuite);
		testSuite.addChild(testStructure);
		return testStructure;
	}

	/**
	 * Tests the Method
	 * {@link FitnesseFileSystemUtility#existsContentTxtInPathOfTestStructureInErrorDirectory(TestStructure)}
	 * .
	 * 
	 * @throws IOException IOException
	 * @throws IllegalStateException IllegalStateException
	 */
	@Test
	public void testExistsContentTxtInPathOfTestStructureInErrorDirectory() throws IllegalStateException, IOException {

		FileUtils.copyFolder(new File("./ressources/MyTestPrj"), new File(
				Platform.getLocation().toFile(), "MyTestPrj"));

		TestStructure testStructure = createTestStructureForTest();

		TestProjectConfig testProjectConfig = new TestProjectConfig();
		testProjectConfig.setPathToTestFiles(Platform.getLocation().toFile()
				.getAbsolutePath());

		// content.txt for test must be exist
		assertTrue(
				"content.txt for suite "
						+ testStructure.getParent().getFullName()
						+ " must exist",
				FitnesseFileSystemUtility
						.existsContentTxtInPathOfTestStructureInErrorDirectory(testStructure));

		// content.txt for suite must not be exist
		assertFalse(
				"content.txt for suite "
						+ testStructure.getParent().getFullName()
						+ " must not exist",
				FitnesseFileSystemUtility
						.existsContentTxtInPathOfTestStructureInErrorDirectory(testStructure
								.getParent()));

	}

}
