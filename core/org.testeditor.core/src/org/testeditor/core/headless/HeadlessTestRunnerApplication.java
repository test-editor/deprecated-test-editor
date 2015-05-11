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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.service.prefs.BackingStoreException;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.exceptions.TestCycleDetectException;
import org.testeditor.core.model.testresult.TestResult;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.model.teststructure.TestSuite;
import org.testeditor.core.services.interfaces.TestEditorConfigurationService;
import org.testeditor.core.services.interfaces.TestEditorGlobalConstans;
import org.testeditor.core.services.interfaces.TestEditorPlugInService;
import org.testeditor.core.services.interfaces.TestProjectService;
import org.testeditor.core.services.interfaces.TestServerService;
import org.testeditor.core.services.interfaces.TestStructureContentService;
import org.testeditor.core.services.interfaces.TestStructureService;
import org.testeditor.core.util.FileLocatorService;

/**
 * 
 * OSGi Application to execute Tests headless.
 *
 */
public class HeadlessTestRunnerApplication implements IApplication {

	private static final Logger LOGGER = Logger.getLogger(HeadlessTestRunnerApplication.class);
	public static final String EXECUTE_TEST = "ExecuteTest";

	@Override
	public Object start(IApplicationContext context) throws Exception {
		String[] args = (String[]) context.getArguments().get(IApplicationContext.APPLICATION_ARGS);
		TestResult result = exeucteTest(args);
		if (result.getRight() > 0) {
			return IApplication.EXIT_OK;
		} else {
			return new Integer(13);
		}
	}

	/**
	 * Executes a test and places the backend specific test result file in the
	 * root of the workspace. this allows other systems like jenkins to use this
	 * file for retporting.
	 * 
	 * @param args
	 *            used to determine the teststructure.
	 * @throws BackingStoreException
	 *             on problems reading eclipse meta structure.
	 * @throws IOException
	 *             on reading project or file access.
	 * @throws InvalidArgumentException
	 *             on wrong arguments.
	 * @throws URISyntaxException
	 *             launching fitnesse server.
	 * @throws SystemException
	 *             on te error.
	 * @throws InterruptedException
	 *             stopping execution.
	 * 
	 * @return testResult of the test execution.
	 */
	public TestResult exeucteTest(String[] args) throws BackingStoreException, IOException, InvalidArgumentException,
			URISyntaxException, SystemException, InterruptedException {
		initializeSystemConfiguration();
		TestStructure test = getTestStructureToExecute(args);
		TestEditorPlugInService plugInService = getService(TestEditorPlugInService.class);
		TestStructureService testStructureService = plugInService.getTestStructureServiceFor(test.getRootElement()
				.getTestProjectConfig().getTestServerID());
		InterActionLogWatcherRunnable interActionLogWatcherRunnable = new InterActionLogWatcherRunnable(
				new NullProgressMonitor());
		if (test instanceof TestSuite) {
			TestSuite ts = (TestSuite) test;
			try {
				getService(TestStructureContentService.class).refreshTestCaseComponents(ts);
			} catch (TestCycleDetectException e) {
				LOGGER.warn("Cycle detected in: " + ts.getFullName());
			}
			interActionLogWatcherRunnable.setTestCaseCount(ts.getReferredTestStrcutures().size()
					+ ts.getAllTestChildren().size());
		}
		new Thread(interActionLogWatcherRunnable).start();
		TestResult testResult = testStructureService.executeTestStructure(test, new NullProgressMonitor());
		interActionLogWatcherRunnable.stopWatching();
		LOGGER.info(getTestSummaryFrom(testResult));
		publishTestResultFile(testResult);
		TestServerService serverService = getService(TestServerService.class);
		serverService.stopTestServer(test.getRootElement());
		LOGGER.info("Shutdown Testengine.");
		return testResult;
	}

	/**
	 * Publishes the test result file as latest test result to the workspace.
	 * 
	 * @param testResult
	 *            to be reported.
	 * @throws IOException
	 *             on failure copy test result.
	 */
	protected void publishTestResultFile(TestResult testResult) throws IOException {
		FileLocatorService locatorService = getService(FileLocatorService.class);
		File resultFile = new File(locatorService.getWorkspace(), "latestResult.xml");
		File srcFile = new File(locatorService.getWorkspace().getAbsoluteFile() + File.separator + ".metadata"
				+ File.separator + "logs", "latestResult.xml");
		FileOutputStream out = new FileOutputStream(resultFile);
		Files.copy(srcFile.toPath(), out);
		LOGGER.info("Published latets test result. " + resultFile.getCanonicalPath());
	}

	/**
	 * Creates a formated summary string of the test result.
	 * 
	 * @param testResult
	 *            to be formatted.
	 * @return a summary of the test result as string.
	 */
	protected String getTestSummaryFrom(TestResult testResult) {
		StringBuilder sb = new StringBuilder();
		sb.append("\n*******************************************************************\nTest executed with: ")
				.append(testResult.isSuccessfully()).append(" in ").append(testResult.getRunTimesSec())
				.append("s details: \n");
		List<TestResult> children = testResult.getChildren();
		int count = 0;
		int failed = 0;
		List<TestResult> failedTests = new ArrayList<TestResult>();
		for (TestResult detailResult : children) {
			count++;
			if (!detailResult.isSuccessfully()) {
				failedTests.add(detailResult);
				failed++;
			}
		}
		for (TestResult failedTest : failedTests) {
			sb.append(failedTest.getFullName()).append("\t with: \t").append(failedTest.isSuccessfully()).append("\n");
		}
		sb.append(failed).append(" of ").append(count).append(" are failed.");
		sb.append("\n*******************************************************************");
		return sb.toString();
	}

	/**
	 * Reads the Arguments of the application to find the teststructure to
	 * executes. The Teststructure is searched in the workspace.
	 * 
	 * @param args
	 *            commandline arguments of the application
	 * @return teststructure defined by the arguments.
	 * @throws InvalidArgumentException
	 *             on wrong arguments.
	 * @throws URISyntaxException
	 *             launching fitnesse server.
	 * @throws IOException
	 *             on reading project.
	 */
	public TestStructure getTestStructureToExecute(String[] args) throws InvalidArgumentException, IOException,
			URISyntaxException {
		Map<String, String> parameter = new HashMap<String, String>();
		for (String string : args) {
			if (string.contains("=")) {
				String[] split = string.split("=");
				parameter.put(split[0], split[1]);
			}
		}
		if (!parameter.containsKey(EXECUTE_TEST)) {
			throw new InvalidArgumentException("Missing Argument " + EXECUTE_TEST + " to select the excutable test.");
		}
		String testStructureName = parameter.get(EXECUTE_TEST);
		TestProjectService prjService = getService(TestProjectService.class);
		String projectName = testStructureName.substring(0, testStructureName.indexOf("."));
		TestProject testProject = prjService.getProjectWithName(projectName);
		if (testProject == null) {
			throw new InvalidArgumentException("No Project with name .");
		}
		TestServerService serverService = getService(TestServerService.class);
		serverService.startTestServer(testProject);
		return testProject.getTestChildByFullName(testStructureName);
	}

	/**
	 * Initialize the Test-Editor configuration. Loading system variables and
	 * setting path to the fixtures..
	 * 
	 * @throws BackingStoreException
	 *             on problems reading eclipse meta structure.
	 * @throws IOException
	 *             on file access.
	 */
	public void initializeSystemConfiguration() throws BackingStoreException, IOException {
		TestEditorConfigurationService testEditorConfigService = getService(TestEditorConfigurationService.class);
		String waittime = System.getProperty(TestEditorGlobalConstans.DEFINE_WAITS_AFTER_TEST_STEP);
		testEditorConfigService.exportGlobalVariablesToSystemProperties();
		testEditorConfigService.initializeSystemProperties();
		if (waittime != null) {
			LOGGER.info("Restoring Wait time to" + waittime);
			System.setProperty(TestEditorGlobalConstans.DEFINE_WAITS_AFTER_TEST_STEP, waittime);
		}
		LOGGER.info("Headless Test-Editor initialized.");
	}

	/**
	 * Looks up an OSGi Service.
	 * 
	 * @param clazz
	 *            of the service definition.
	 * @param <S>
	 *            Service type.
	 * @return a service implementation.
	 */
	private <S> S getService(Class<S> clazz) {
		BundleContext bundleContext = FrameworkUtil.getBundle(clazz).getBundleContext();
		ServiceReference<S> serviceReference = bundleContext.getServiceReference(clazz);
		return bundleContext.getService(serviceReference);
	}

	@Override
	public void stop() {

	}

}
