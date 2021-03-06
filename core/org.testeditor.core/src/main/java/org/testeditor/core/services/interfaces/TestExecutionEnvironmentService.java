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
package org.testeditor.core.services.interfaces;

import java.io.IOException;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.testeditor.core.model.testresult.TestResult;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestStructure;

/**
 * Service to manage environments for the test execution. A test execution
 * environment should be a defined system which has a stable state. Possible
 * implementations are vm ore remote systems.
 *
 */
public interface TestExecutionEnvironmentService {

	String CONFIG = "test.execution.environment.config";

	/**
	 * Setup an Environment to execute the tests. The configuration of the
	 * project is used to setup the test environment.
	 * 
	 * @param testProject
	 *            with configuration to the test environment.
	 * @param monitor
	 *            to report the progress.
	 * @throws IOException
	 *             on failure to setup the environment.
	 * @throws InterruptedException
	 *             on interrupting the setup.
	 */
	void setUpEnvironment(TestProject testProject, IProgressMonitor monitor) throws IOException, InterruptedException;

	/**
	 * Executes a TestStructure in the test environment and collects the test
	 * result.
	 * 
	 * @param testStructure
	 *            to be executed.
	 * @param monitor
	 *            to report the progress.
	 * @return the result of the test execution.
	 * @throws IOException
	 *             on failure to setup the environment.
	 * @throws InterruptedException
	 *             on interrupting the setup.
	 */
	TestResult executeTests(TestStructure testStructure, IProgressMonitor monitor)
			throws IOException, InterruptedException;

	/**
	 * Stops the environment.
	 * 
	 * @param testProject
	 *            with configuration to the test environment.
	 * @param monitor
	 *            to report the progress.
	 * @throws IOException
	 *             on failure to setup the environment.
	 * @throws InterruptedException
	 *             on interrupting the setup.
	 */
	void shutDownEnvironment(TestProject testProject, IProgressMonitor monitor)
			throws IOException, InterruptedException;

	/**
	 * Cleans up the environment and releases any ressources.
	 * 
	 * @param testProject
	 *            with configuration to the test environment.
	 * @param monitor
	 *            to report the progress.
	 * @throws IOException
	 *             on failure to setup the environment.
	 * @throws InterruptedException
	 *             on interrupting the setup.
	 */
	void resetEnvironment(TestProject testProject, IProgressMonitor monitor) throws IOException, InterruptedException;

	/**
	 * Cleans up all running environments.
	 * 
	 * @throws InterruptedException
	 *             on user interrupt
	 * @throws IOException
	 *             on failure shutting down the environments.
	 */
	void tearDownAllEnvironments() throws IOException, InterruptedException;

	/**
	 * Checks if a testenvironment for the project is launched.
	 * 
	 * @param testProject
	 *            to be checked.
	 * @return true if their is a testenvironment for execution launched
	 *         otherwise false.
	 */
	boolean isTestEnvironmentLaunchedFor(TestProject testProject);

	/**
	 * Gets the Names of the available Test Environments for a test project and
	 * the configuration. Every test project has at least the host as a test
	 * environment.
	 * 
	 * @param testProject
	 *            TestProject
	 * 
	 * @return map with the names of the test Test Environments as key and the
	 *         path to the configuration as value.
	 */
	Map<String, String> getAvailableTestEnvironmentConfigs(TestProject testProject);

}
