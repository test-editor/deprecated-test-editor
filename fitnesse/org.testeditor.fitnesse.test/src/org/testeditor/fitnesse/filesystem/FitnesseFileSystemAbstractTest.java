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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.core.runtime.Platform;
import org.junit.After;
import org.testeditor.core.model.teststructure.TestCase;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestSuite;

/**
 * Abstract class for common setup and cleanup for FitnesseFilesystem based
 * tests.
 * 
 */
public abstract class FitnesseFileSystemAbstractTest {

	static final String TEST_TEXT = "MyTest Content";

	/**
	 * Creates a Project with a small test tree for test purpose.
	 * 
	 * @throws IllegalStateException
	 *             fot test
	 * @throws IOException
	 *             for test
	 * 
	 * @return TestProject used by the tests.
	 */
	protected TestProject createTestProjectsInWS() throws IllegalStateException, IOException {
		TestProject result = new TestProject();
		result.setName("tp");
		Files.createDirectories(Paths.get(Platform.getLocation().toFile().toPath().toString() + "/tp/FitNesseRoot/tp"));
		Files.createDirectories(Paths.get(Platform.getLocation().toFile().toPath().toString()
				+ "/tp/FitNesseRoot/tp/ts"));
		Files.copy(this.getClass().getResourceAsStream("/history/ts_properties.xml"), Paths.get(Platform.getLocation()
				.toFile().toPath().toString()
				+ "/tp/FitNesseRoot/tp/ts/properties.xml"));
		TestSuite ts = new TestSuite();
		ts.setName("ts");
		result.addChild(ts);
		Files.createDirectories(Paths.get(Platform.getLocation().toFile().toPath().toString()
				+ "/tp/FitNesseRoot/tp/tc"));
		Files.write(
				Paths.get(Platform.getLocation().toFile().toPath().toString() + "/tp/FitNesseRoot/tp/tc/content.txt"),
				TEST_TEXT.getBytes(StandardCharsets.UTF_8));
		Files.createDirectories(Paths.get(Platform.getLocation().toFile().toPath().toString()
				+ "/tp/FitNesseRoot/files/testResults/tp.tc"));
		Files.copy(
				this.getClass().getResourceAsStream("/history/20141017221315_3_0_0_0.xml"),
				Paths.get(Platform.getLocation().toFile().toPath().toString()
						+ "/tp/FitNesseRoot/files/testResults/tp.tc/20141017221315_3_0_0_0.xml"));
		Files.copy(
				this.getClass().getResourceAsStream("/history/20141018135503_3_0_0_0.xml"),
				Paths.get(Platform.getLocation().toFile().toPath().toString()
						+ "/tp/FitNesseRoot/files/testResults/tp.tc/20141018135503_3_0_0_0.xml"));
		Files.copy(
				this.getClass().getResourceAsStream("/history/20141019135503_3_0_0_0.xml"),
				Paths.get(Platform.getLocation().toFile().toPath().toString()
						+ "/tp/FitNesseRoot/files/testResults/tp.tc/20141019135503_3_0_0_0.xml"));
		TestCase testCase = new TestCase();
		testCase.setName("tc");
		result.addChild(testCase);
		Files.copy(this.getClass().getResourceAsStream("/history/tc_properties.xml"), Paths.get(Platform.getLocation()
				.toFile().toPath().toString()
				+ "/tp/FitNesseRoot/tp/tc/properties.xml"));
		return result;
	}

	/**
	 * Cleans up the workspace after test execution.
	 * 
	 * @throws IOException
	 *             on deleting files
	 */
	@After
	public void cleanUPWorkspace() throws IOException {
		Path directory = Paths.get(Platform.getLocation().toFile().toString() + "/tp");

		if (directory.toFile().isDirectory()) {
			Files.walkFileTree(directory, FitnesseFileSystemUtility.getDeleteRecursiveVisitor());
		}
	}

}
