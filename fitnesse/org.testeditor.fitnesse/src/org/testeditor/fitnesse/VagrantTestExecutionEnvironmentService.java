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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.testeditor.core.constants.TestEditorCoreConstants;
import org.testeditor.core.model.testresult.TestResult;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.services.interfaces.TestExecutionEnvironmentService;
import org.testeditor.core.util.FileLocatorService;
import org.testeditor.fitnesse.filesystem.FitnesseFileSystemUtility;
import org.testeditor.fitnesse.resultreader.FitNesseResultReader;
import org.testeditor.fitnesse.resultreader.FitnesseTestExecutionResultReader;

/**
 * Vagrant based implementation of the
 * <code>TestExceutionEnvironmentService</code>. Vagrant manages boxes for
 * different vm providers: http://docs.vagrantup.com/ The implementation
 * supports unix and windows based boxed. But there are several requirements for
 * the boxes:
 */
public class VagrantTestExecutionEnvironmentService implements TestExecutionEnvironmentService {

	private static final Logger logger = Logger.getLogger(VagrantTestExecutionEnvironmentService.class);
	public static final String TE_PROPERTIES_FILENAME = "te_test_env.properties";
	private boolean testsRunFlag;

	private Set<TestProject> runningEnvironments = new HashSet<TestProject>();
	private Properties previosProps;

	@Override
	public void setUpEnvironment(TestProject testProject, IProgressMonitor monitor)
			throws IOException, InterruptedException {
		File vagrantFileDir = getVagrantFileDirectory(testProject);
		setSpecificGlobalVariables(vagrantFileDir);
		try {
			logger.info("Vagrant path: " + vagrantFileDir);
			ProcessBuilder builder = new ProcessBuilder("vagrant", "up");
			configureBuilder(builder, vagrantFileDir);
			Process upPrc = builder.start();
			createAndRunLoggerOnStream(upPrc.getInputStream(), false, monitor);
			createAndRunLoggerOnStream(upPrc.getErrorStream(), true, null);
			while (isAlive(upPrc)) {
				Thread.sleep(100);
			}
			runningEnvironments.add(testProject);
		} catch (Exception e) {
			logger.error("error start", e);
		}
	}

	@Override
	public TestResult executeTests(TestStructure testStructure, IProgressMonitor monitor)
			throws IOException, InterruptedException {
		monitor.setTaskName("Execute Test... " + testStructure.getFullName());

		File vagrantFileDir = getVagrantFileDirectory(testStructure.getRootElement());
		String execCommand = createExecCommand(testStructure);

		ProcessBuilder builder = new ProcessBuilder("vagrant", "ssh", "-c", execCommand);
		configureBuilder(builder, vagrantFileDir);
		Process execPrc = builder.start();
		testsRunFlag = true;
		createAndRunLoggerOnStream(execPrc.getInputStream(), false, null);
		createAndRunLoggerOnStream(execPrc.getErrorStream(), true, null);
		while (isAlive(execPrc) && testsRunFlag && !monitor.isCanceled()) {
			Thread.sleep(100);
		}
		execPrc.destroy();
		final File resultFile = new File(new FileLocatorService().getWorkspace().getAbsoluteFile() + File.separator
				+ ".metadata" + File.separator + "logs", "latestResult.xml");
		FitNesseResultReader reader = new FitnesseTestExecutionResultReader();
		FileInputStream fileInputStream = new FileInputStream(resultFile);
		TestResult result = reader.readTestResult(fileInputStream);
		fileInputStream.close();
		resetProperties();
		return result;
	}

	/**
	 * Restores the old Properties before the Test was laumched.
	 */
	public void resetProperties() {
		for (String key : previosProps.stringPropertyNames()) {
			System.setProperty(key, previosProps.getProperty(key));
		}

	}

	@Override
	public void shutDownEnvironment(TestProject testProject, IProgressMonitor monitor)
			throws IOException, InterruptedException {
		monitor.setTaskName("Shutdown TestAgent..");
		ProcessBuilder builder = new ProcessBuilder("vagrant", "suspend");
		internalExecutionOfShutdownTestEnvironment(builder, testProject);
	}

	@Override
	public void resetEnvironment(TestProject testProject, IProgressMonitor monitor)
			throws IOException, InterruptedException {
		monitor.setTaskName("Shutdown and resetting TestAgent..");
		ProcessBuilder builder = new ProcessBuilder("vagrant", "destroy", "-f");
		internalExecutionOfShutdownTestEnvironment(builder, testProject);
	}

	/**
	 * Executes the shutdown of the vagrant box. The command is defined in the
	 * builder.
	 * 
	 * @param builder
	 *            to create the process with the predefined command.
	 * @param testProject
	 *            of the test environment.
	 * @throws IOException
	 *             on failure
	 * @throws InterruptedException
	 *             on user interrupt.
	 */
	private void internalExecutionOfShutdownTestEnvironment(ProcessBuilder builder, TestProject testProject)
			throws IOException, InterruptedException {
		File vagrantFileDir = getVagrantFileDirectory(testProject);
		configureBuilder(builder, vagrantFileDir);
		Process destroyPrc = builder.start();
		createAndRunLoggerOnStream(destroyPrc.getInputStream(), false, null);
		createAndRunLoggerOnStream(destroyPrc.getErrorStream(), true, null);
		while (isAlive(destroyPrc)) {
			Thread.sleep(100);
		}
		runningEnvironments.remove(testProject);
	}

	/**
	 * Configures the ProcessBuild with parameters to run vagrant.
	 * 
	 * @param builder
	 *            to be configured.
	 * @param vagrantFileDir
	 *            used for this vagrant launch.
	 */
	private void configureBuilder(ProcessBuilder builder, File vagrantFileDir) {
		builder.directory(vagrantFileDir);
		builder.redirectErrorStream(true);
		logger.trace("TESTEDITOR_HOME env variable: " + System.getProperty("TESTEDITOR_HOME"));
		if (System.getProperty("TESTEDITOR_HOME") != null) {
			builder.environment().put("TESTEDITOR_HOME", System.getProperty("TESTEDITOR_HOME"));
		}
	}

	/**
	 * Checks if a process is alive.
	 * 
	 * @param process
	 *            to be checked.
	 * @return true if process is alive otherwise false.
	 */
	private boolean isAlive(Process process) {
		try {
			process.exitValue();
			return false;
		} catch (IllegalThreadStateException e) {
			return true;
		}
	}

	/**
	 * Creates an script to execute the tests in the target system and returns
	 * the command to launch it with vagrant.
	 * 
	 * @param testStructure
	 *            to be executed as test.
	 * @return string with the command used in the vagrant ssh call.
	 * @throws IOException
	 *             on failure.
	 */
	protected String createExecCommand(TestStructure testStructure) throws IOException {
		File vagrantFileDir = getVagrantFileDirectory(testStructure.getRootElement());
		List<String> lines = Files.readAllLines(new File(vagrantFileDir, "Vagrantfile").toPath(),
				StandardCharsets.UTF_8);
		boolean isLinux = true;
		boolean isXvfb = false;
		for (String string : lines) {
			if (!string.trim().startsWith("#")) {
				if (string.contains("config.vm.communicator") && string.contains("winrm")) {
					isLinux = false;
				}
				if (string.contains("apt-get install") && string.contains("xvfb")) {
					isXvfb = true;
				}
			}
		}
		String executionScript = null;
		String execCommand = null;
		String execScript = null;
		if (isLinux) {
			executionScript = getExecutionScriptForLinux(testStructure, isXvfb);
			execCommand = "/vagrant/executeTest.sh";
			execScript = "executeTest.sh";
		} else {
			executionScript = getExecutionScriptForWindows(testStructure);
			execCommand = "/cygdrive/c/pstools/PsExec.exe -i 1 -u vagrant -p vagrant c:/vagrant/executeTest.bat";
			execScript = "executeTest.bat";
		}
		File launcher = new File(vagrantFileDir, execScript);
		Files.write(launcher.toPath(), executionScript.toString().getBytes());
		launcher.setExecutable(true);
		return execCommand;
	}

	/**
	 * Creates the content of a test execution script on windows.
	 * 
	 * @param testStructure
	 *            used for test execution.
	 * @return string with the script content.
	 */
	protected String getExecutionScriptForWindows(TestStructure testStructure) {
		StringBuilder sb = new StringBuilder();
		sb.append("@echo off").append("\n");
		sb.append("c:/vagrant_bin/jre/bin/java ");
		sb.append(getPropertiesString(testStructure));
		sb.append(" -jar c:/vagrant_bin/plugins/org.eclipse.equinox.launcher_1.3.100.v20150511-1540.jar");
		sb.append("  -application org.testeditor.core.headlesstestrunner -consoleLog");
		sb.append(" -data c:/vagrant_data/  ExecuteTest=");
		sb.append(testStructure.getFullName());
		sb.append("\n");
		sb.append("echo TE terminated.\n");
		return sb.toString();
	}

	/**
	 * Creates the content of a test execution script on linux.
	 * 
	 * @param testStructure
	 *            used for test execution.
	 * @return string with the script content.
	 */
	protected String getExecutionScriptForLinux(TestStructure testStructure, boolean isXvfb) {
		StringBuilder sb = new StringBuilder();
		sb.append("#!/bin/bash").append("\n");
		if (isXvfb) {
			sb.append("sudo Xvfb :99 -ac -screen 0 1280x1024x16 &").append("\n");
			sb.append("export DISPLAY=:99").append("\n");
			sb.append("firefox -CreateProfile testing").append("\n");
		} else {
			sb.append("sudo startx &").append("\n");
			sb.append("export DISPLAY=:0").append("\n");
		}
		sb.append("sudo /usr/bin/java");
		sb.append(getPropertiesString(testStructure));
		sb.append(
				" -jar /vagrant_bin/plugins/org.eclipse.equinox.launcher_*.jar -application org.testeditor.core.headlesstestrunner");
		sb.append(" -consoleLog -data /vagrant_data/  ExecuteTest=");
		sb.append(testStructure.getFullName());
		sb.append("\n");
		sb.append("sudo pkill X").append("\n");
		sb.append("echo TE terminated.\n");

		return sb.toString();
	}

	/**
	 * Get Proeprties as -D params from the TE Proeprties Config near the
	 * vagrant file to add this parameters to the TE headless.
	 * 
	 * @param testStructure
	 *            to detect the vagrant config dir.
	 * @return String with -d parameters.
	 */
	protected StringBuilder getPropertiesString(TestStructure testStructure) {
		StringBuilder sb = new StringBuilder();
		try {
			Properties properties = loadTEProperties(getVagrantFileDirectory(testStructure.getRootElement()));
			for (Object key : properties.keySet()) {
				sb.append(" -D").append(key).append("=").append(properties.get(key));
			}
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		sb.append(" -DexecInVagrant=true");
		return sb;
	}

	/**
	 * Looks up the directory containing the vagrant file in the testproject.
	 * 
	 * @param testProject
	 *            with vagrant config.
	 * @return file to the vagrantfile parent directory.
	 */
	protected File getVagrantFileDirectory(TestProject testProject) {
		return new File(FitnesseFileSystemUtility.getPathToProject(testProject),
				testProject.getTestProjectConfig().getTestEnvironmentConfiguration());
	}

	/**
	 * Sets the configured properties in the jvm of the TE.
	 * 
	 * @param vagrantFileDir
	 *            dir where the properties are expected.
	 * @throws IOException
	 *             on io problems.
	 */
	public void setSpecificGlobalVariables(File vagrantFileDir) throws IOException {
		previosProps = new Properties();
		Properties props = loadTEProperties(vagrantFileDir);
		for (String key : props.stringPropertyNames()) {
			logger.trace("Processing: " + key + ":" + props.getProperty(key));
			String property = System.getProperty(key);
			if (property != null) {
				previosProps.put(key, System.getProperty(key));
			}
			System.setProperty(key, props.getProperty(key));
		}
	}

	/**
	 * Loads the TE specific properties from file system. If there is no file it
	 * returns an empty propertie object.
	 * 
	 * @param vagrantFileDir
	 *            dir where the properties are expected.
	 * @return properties with the content of the file
	 * @throws IOException
	 *             on io problems.
	 */
	private Properties loadTEProperties(File vagrantFileDir) throws IOException {
		Properties result = new Properties();
		File teProperties = new File(vagrantFileDir, TE_PROPERTIES_FILENAME);
		if (teProperties.exists()) {
			FileInputStream fileInputStream = new FileInputStream(teProperties);
			result.load(fileInputStream);
			fileInputStream.close();
		}
		return result;
	}

	/**
	 * Creates and starts a runnable watching the input stream. The content of
	 * the stream is redirected to the logger.
	 * 
	 * @param inputStream
	 *            to be redirected to the logger.
	 * @param errorStream
	 *            to indicate the log level.
	 * @param monitor
	 *            to show log entries.
	 */
	private void createAndRunLoggerOnStream(final InputStream inputStream, final boolean errorStream,
			final IProgressMonitor monitor) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				char[] cbuf = new char[8192];
				int len = -1;
				try {
					InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
					while ((len = reader.read(cbuf)) > 0) {
						String message = new String(cbuf, 0, len);
						if (message.contains("TE terminated.")) {
							testsRunFlag = false;
						}
						if (errorStream) {
							logger.error(message);
						} else {
							logger.info(message);
							if (monitor != null) {
								monitor.subTask(message);
							}
						}
					}
				} catch (IOException e) {
					logger.debug("Error reading remote Process Stream", e);
				}
			}
		}).start();
	}

	@Override
	public void tearDownAllEnvironments() throws IOException, InterruptedException {
		for (TestProject testProject : runningEnvironments) {
			shutDownEnvironment(testProject, new NullProgressMonitor());
		}
	}

	@Override
	public boolean isTestEnvironmentLaunchedFor(TestProject testProject) {
		return runningEnvironments.contains(testProject);
	}

	@Override
	public Map<String, String> getAvailableTestEnvironmentConfigs(TestProject testProject) {
		Map<String, String> testExecEnvs = new HashMap<String, String>();
		testExecEnvs.put("localhost", TestEditorCoreConstants.NONE_TEST_AGENT);
		File vagrantDir = new File(testProject.getUrl().getFile(), "vagrant");
		if (vagrantDir.exists() && vagrantDir.isDirectory()) {
			File[] files = vagrantDir.listFiles();
			for (File file : files) {
				if (file.isDirectory()) {
					try {
						String configPath = Paths.get(testProject.getUrl().toURI()).relativize(file.toPath())
								.toString();
						testExecEnvs.put(file.getName(), configPath);
					} catch (URISyntaxException e) {
						logger.error(e.getMessage(), e);
					}
				}
			}
		}
		return testExecEnvs;
	}

}
