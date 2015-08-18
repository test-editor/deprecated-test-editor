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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.testeditor.core.model.testresult.TestResult;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.services.interfaces.TestExceutionEnvironmentService;
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
public class VagrantTestExecutionEnvironmentService implements TestExceutionEnvironmentService {

	private static final Logger LOGGER = Logger.getLogger(VagrantTestExecutionEnvironmentService.class);
	private boolean testsRunFlag;

	@Override
	public void setUpEnvironment(TestProject testProject, IProgressMonitor monitor)
			throws IOException, InterruptedException {
		File vagrantFileDir = getVagrantFile(testProject);
		LOGGER.info("Vagrant path: " + vagrantFileDir);
		ProcessBuilder builder = new ProcessBuilder("vagrant", "up");
		builder.directory(vagrantFileDir);
		builder.redirectErrorStream(true);
		Process upPrc = builder.start();
		createAndRunLoggerOnStream(upPrc.getInputStream(), false, monitor);
		createAndRunLoggerOnStream(upPrc.getErrorStream(), true, null);
		while (isAlive(upPrc)) {
			Thread.sleep(100);
		}
	}

	@Override
	public TestResult executeTests(TestStructure testStructure, IProgressMonitor monitor)
			throws IOException, InterruptedException {
		monitor.setTaskName("Execute Test... " + testStructure.getFullName());

		File vagrantFileDir = getVagrantFile(testStructure.getRootElement());
		String execCommand = createExecCommand(testStructure);

		ProcessBuilder builder = new ProcessBuilder("vagrant", "ssh", "-c", execCommand);
		builder.directory(vagrantFileDir);
		builder.redirectErrorStream(true);
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

		return result;
	}

	@Override
	public void tearDownEnvironment(TestProject testProject, IProgressMonitor monitor)
			throws IOException, InterruptedException {
		monitor.setTaskName("Shutdown TestAgent..");
		ProcessBuilder builder = new ProcessBuilder("vagrant", "destroy", "-f");
		builder.directory(getVagrantFile(testProject));
		builder.redirectErrorStream(true);
		Process destroyPrc = builder.start();
		createAndRunLoggerOnStream(destroyPrc.getInputStream(), false, null);
		createAndRunLoggerOnStream(destroyPrc.getErrorStream(), true, null);
		while (isAlive(destroyPrc)) {
			Thread.sleep(100);
		}
	}

	/**
	 * Checks if a process is alive
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

	protected String createExecCommand(TestStructure testStructure) throws IOException {
		File vagrantFileDir = getVagrantFile(testStructure.getRootElement());
		List<String> lines = Files.readAllLines(new File(vagrantFileDir, "Vagrantfile").toPath(),
				StandardCharsets.UTF_8);
		boolean isLinux = true;
		for (String string : lines) {
			if (!string.trim().startsWith("#")) {
				if (string.contains("config.vm.communicator") && string.contains("winrm")) {
					isLinux = false;
				}
			}
		}
		String executionScript = null;
		String execCommand = null;
		String execScript = null;
		if (isLinux) {
			executionScript = getExecutionScriptForLinux(testStructure);
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

	protected String getExecutionScriptForWindows(TestStructure testStructure) {
		StringBuilder sb = new StringBuilder();
		sb.append("@echo off").append("\n");
		sb.append("c:/vagrant_bin/jre/bin/java -DPATH_TO_TESTEDITOR_AUT=c:/vagrant_bin/testeditor.exe");
		sb.append(" -Daut.workspace.path=c:/vagrant/autws -Dwaits.afterteststep=10ms");
		sb.append("  -jar c:/vagrant_bin/plugins/org.eclipse.equinox.launcher_1.3.0.v20140415-2008.jar");
		sb.append("  -application org.testeditor.core.headlesstestrunner -consoleLog");
		sb.append(" -data c:/vagrant_data/  ExecuteTest=");
		sb.append(testStructure.getFullName());
		sb.append("\n");
		sb.append("echo TE terminated.\n");
		return sb.toString();
	}

	protected String getExecutionScriptForLinux(TestStructure testStructure) {
		StringBuilder sb = new StringBuilder();
		sb.append("#!/bin/bash").append("\n");
		sb.append("sudo startx &").append("\n");
		sb.append("export DISPLAY=:0").append("\n");
		sb.append(
				"sudo /usr/bin/java -DPATH_TO_TESTEDITOR_AUT=/vagrant_bin/testeditor -Daut.workspace.path=/vagrant/autws -Dwaits.afterteststep=10ms");
		sb.append(
				" -jar /vagrant_bin/plugins/org.eclipse.equinox.launcher_1.3.0.v20140415-2008.jar -application org.testeditor.core.headlesstestrunner");
		sb.append(" -consoleLog -data /vagrant_data/  ExecuteTest=");
		sb.append(testStructure.getFullName());
		sb.append("\n");
		sb.append("echo TE terminated.\n");

		return sb.toString();
	}

	protected File getVagrantFile(TestProject testProject) {
		return new File(FitnesseFileSystemUtility.getPathToProject(testProject),
				testProject.getTestProjectConfig().getTestEnvironmentConfiguration());
	}

	private void createAndRunLoggerOnStream(final InputStream inputStream, final boolean errorStream,
			final IProgressMonitor monitor) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				char[] cbuf = new char[8192];
				int len = -1;
				try {
					InputStreamReader reader = new InputStreamReader(inputStream, "UTF-8");
					while ((len = reader.read(cbuf)) > 0) {
						String message = new String(cbuf, 0, len);
						if (message.contains("TE terminated.")) {
							testsRunFlag = false;
						}
						if (errorStream) {
							LOGGER.error(message);
						} else {
							LOGGER.info(message);
							if (monitor != null) {
								monitor.subTask(message);
							}
						}
					}
				} catch (IOException e) {
					LOGGER.debug("Error reading remote Process Stream", e);
				}
			}
		}).start();
	}

}
