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
package org.testeditor.fitnesse.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.di.annotations.Optional;
import org.junit.Test;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestProjectConfig;
import org.testeditor.core.services.interfaces.ServiceLookUpForTest;
import org.testeditor.core.services.plugins.TestServerServicePlugIn;

/**
 * 
 * Integrationtests for the Fitnesse Server Controller.
 * 
 */
public class FitnesseServerControllerIntTest {

	/**
	 * test the getPathToFitnessejar Method to find the Path to fitnesse.
	 * 
	 * @throws Exception
	 *             for Test.
	 */
	@Test
	public void testGetPathToFitnessejar() throws Exception {
		FitnesseServerController serverControler = (FitnesseServerController) ServiceLookUpForTest
				.getService(TestServerServicePlugIn.class);
		String path = serverControler.getPathToFitnesseJar();
		assertNotNull(path);
		assertTrue("File must exist.", new File(path).exists());
		assertTrue("File is a Directory.", new File(path).isDirectory());
	}

	/**
	 * Creates a new TestProject and starts new FitNesse instance on given port.
	 * 
	 * @param serverControler
	 *            FitnesseServerController
	 * @param pathToTestFile
	 *            String path to the testfiles
	 * @return new TestProject
	 * 
	 * @throws IOException
	 *             IOException
	 * @throws URISyntaxException
	 *             URISyntaxException
	 */
	private TestProject createTestProjectAndStartFitNesseServer(FitnesseServerController serverControler,
			@Optional String pathToTestFile) throws IOException, URISyntaxException {

		TestProject tp1 = new TestProject();
		TestProjectConfig testProjectConfig = new TestProjectConfig();
		tp1.setTestProjectConfig(testProjectConfig);

		serverControler.startFitnesse(tp1);
		assertTrue("Server started", serverControler.isRunning(tp1));
		return tp1;

	}

	/**
	 * Creates a new TestProject and starts new FitNesse instance on given port.
	 * 
	 * @param serverControler
	 *            FitnesseServerController
	 * @param projectPath
	 *            path to the project
	 * @return new TestProject
	 * 
	 * @throws IOException
	 *             IOException
	 * @throws URISyntaxException
	 *             URISyntaxException
	 */
	private TestProject createTestProjectAndStartFitNesseServerWithProjectPath(FitnesseServerController serverControler,
			String projectPath) throws IOException, URISyntaxException {

		TestProject tp1 = new TestProject();
		TestProjectConfig testProjectConfig = new TestProjectConfig();
		testProjectConfig.setProjectPath(projectPath);
		tp1.setTestProjectConfig(testProjectConfig);

		serverControler.startFitnesse(tp1);
		assertTrue("Server started", serverControler.isRunning(tp1));

		return tp1;

	}

	/**
	 * tests the starting of a project with the projectPath instead of the
	 * testFielPath.
	 * 
	 * @throws Exception
	 *             caused by an error
	 */
	@Test
	public void testCreateProjectAndRunWithProejctPath() throws Exception {
		FitnesseServerController serverControler = (FitnesseServerController) ServiceLookUpForTest
				.getService(TestServerServicePlugIn.class);

		// FitNesse Projekt 4
		TestProject tp4 = createTestProjectAndStartFitNesseServerWithProjectPath(serverControler,
				Platform.getLocation().toFile().getAbsolutePath());

		serverControler.stopFitnesse(tp4);
		assertFalse("Server terminated", serverControler.isRunning(tp4));

	}

	/**
	 * Starts 3 Fitnesse Server instances on different Ports and shuts it down
	 * after launch.
	 * 
	 * @throws Exception
	 *             for Test.
	 */
	@Test
	public void testStartStopMultipleFitNesseServer() throws Exception {

		FitnesseServerController serverControler = (FitnesseServerController) ServiceLookUpForTest
				.getService(TestServerServicePlugIn.class);

		// FitNesse Projekt 1
		TestProject tp1 = createTestProjectAndStartFitNesseServer(serverControler, "");

		// FitNesse Projekt 2
		TestProject tp2 = createTestProjectAndStartFitNesseServer(serverControler,
				Platform.getLocation().toFile().getAbsolutePath() + "3898828989828");

		// FitNesse Projekt 3
		TestProject tp3 = createTestProjectAndStartFitNesseServer(serverControler, null);

		// Stop FitNesse 1
		serverControler.stopFitnesse(tp1);
		assertFalse("Server 1 on port: " + tp1.getTestProjectConfig().getPort() + " terminated",
				serverControler.isRunning(tp1));

		// Stop FitNesse 2
		serverControler.stopFitnesse(tp2);
		assertFalse("Server 2 on port: " + tp1.getTestProjectConfig().getPort() + " terminated",
				serverControler.isRunning(tp2));

		// Stop FitNesse 3
		serverControler.stopFitnesse(tp3);
		assertFalse("Server 3 on port: " + tp1.getTestProjectConfig().getPort() + " terminated",
				serverControler.isRunning(tp3));

	}

	/**
	 * Starts the Server, stop it and restart it under new port number.
	 * 
	 * @throws Exception
	 *             Exception
	 */
	@Test
	public void restartServer() throws Exception {

		FitnesseServerController serverControler = (FitnesseServerController) ServiceLookUpForTest
				.getService(TestServerServicePlugIn.class);

		// create new Project
		TestProject tp = new TestProject();
		TestProjectConfig testProjectConfig = new TestProjectConfig();
		tp.setTestProjectConfig(testProjectConfig);

		// start Project with Fitnesse on a dynamic port
		serverControler.startFitnesse(tp);
		String oldPort = tp.getTestProjectConfig().getPort();
		assertTrue("Server must be started on port: " + oldPort, serverControler.isRunning(tp));

		// Stop FitNesse-Server
		serverControler.stopFitnesse(tp);
		assertFalse("Server must be terminated on port: " + oldPort, serverControler.isRunning(tp));

		// restart project under new port
		tp.setTestProjectConfig(testProjectConfig);
		serverControler.startFitnesse(tp);
		assertTrue("Server must be started on port: " + tp.getTestProjectConfig().getPort(),
				serverControler.isRunning(tp));
		assertTrue("Server port must be equal : ", oldPort != tp.getTestProjectConfig().getPort());

	}

	/**
	 * 
	 * @return TestProject With Config for Test.
	 * @param port
	 *            of Testsystem
	 */
	private TestProject getTestProject(int port) {
		TestProject tp = new TestProject();
		TestProjectConfig testProjectConfig = new TestProjectConfig();
		testProjectConfig.setPort(String.valueOf(port));
		tp.setTestProjectConfig(testProjectConfig);
		return tp;
	}

	/**
	 * Tests starts a FitNesse Server on a free Port.
	 * 
	 * @throws IOException
	 *             if freeport can not bind a socket
	 * @throws URISyntaxException
	 *             if URI is not valid
	 * @throws SystemException
	 *             if FitNesse could not be stopped
	 */
	@Test
	public void testStartStopFitnesseOnFreePort() throws IOException, URISyntaxException, SystemException {
		FitnesseServerController serverController = (FitnesseServerController) ServiceLookUpForTest
				.getService(TestServerServicePlugIn.class);
		int freePort = serverController.getFreePort();
		assertTrue(freePort > 0);

		TestProject tp = getTestProject(freePort);
		serverController.startFitnesse(tp);

		assertTrue("Server must be started on port: " + freePort, serverController.isRunning(tp));
		serverController.stopFitnesse(tp);
	}

}
