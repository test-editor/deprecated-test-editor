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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.SocketTimeoutException;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.log4j.Logger;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.services.plugins.TestServerServicePlugIn;
import org.testeditor.core.util.FileLocatorService;

import fitnesseMain.Arguments;
import fitnesseMain.FitNesseMain;

/**
 * 
 * Controls the launch and stop of the Fitnesse Server. Informations of the
 * State are available too.
 * 
 */
public class FitnesseServerController implements TestServerServicePlugIn {

	private static final Logger LOGGER = Logger.getLogger(FitnesseServerController.class);
	private FileLocatorService fileLocatorService;

	@Override
	public boolean isRunning(TestProject testProject) {

		HttpURLConnection urlConnection = null;
		BufferedInputStream bufferedInputStream = null;
		try {
			URL url2 = new URL("http://localhost:" + testProject.getTestProjectConfig().getPort() + "/FrontPage");
			urlConnection = (HttpURLConnection) url2.openConnection();
			urlConnection.setReadTimeout(2000);
			bufferedInputStream = new BufferedInputStream(urlConnection.getInputStream());

		} catch (java.net.ConnectException ex) {
			// Port is not in use
			return false;
		} catch (SocketTimeoutException ste) {
			LOGGER.info(
					"FitNesse Server under Port " + testProject.getTestProjectConfig().getPort() + " is not running !");
			return false;
		} catch (Exception exp) {
			LOGGER.error(exp);
		} finally {
			if (urlConnection != null) {
				urlConnection.disconnect();
				if (bufferedInputStream != null) {
					try {
						bufferedInputStream.close();
					} catch (IOException e) {
						LOGGER.error(e);
					}
				}
			}
		}

		LOGGER.info("FitNesse Server under Port " + testProject.getTestProjectConfig().getPort()
				+ " is running and ready to rock !");

		return true;

	}

	/**
	 * Starts the Fitnesse Server.
	 * 
	 * @param testProject
	 *            get the server config
	 * 
	 * @throws IOException
	 *             from Process execution
	 * @throws URISyntaxException
	 *             from bundle File lookup
	 */
	public void startFitnesse(final TestProject testProject) throws IOException, URISyntaxException {

		long start = System.currentTimeMillis();

		String fitnessePath = getTestFilePath(testProject);

		// Arguments arguments = FitNesseMain.parseCommandLine(new String[] {
		// "-e", "0" });
		// arguments.setRootPath(fitnessePath);
		// arguments.setOmitUpdates(true);

		// set the dynamic port
		testProject.getTestProjectConfig().setPort(String.valueOf(getFreePort()));

		// arguments.setPort(testProject.getTestProjectConfig().getPort());

		Arguments arguments = new Arguments(new String[] { "-e", "0", "-d", fitnessePath, "-o", "-p",
				testProject.getTestProjectConfig().getPort() });

		try {

			FitNesseMain fm = new FitNesseMain();
			fm.launchFitNesse(arguments);

			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("Dauer: " + String.valueOf(System.currentTimeMillis() - start) + " mSek.");
			}

		} catch (Exception e) {
			LOGGER.error("Problem during Fitnesse startup.", e);
			return;
		}
	}

	/**
	 * If the path in the configuration is not valid, the path to the server is
	 * used.
	 * 
	 * @param testProject
	 *            to find testcases
	 * @return path to Testcases
	 * @throws IOException
	 *             accessing bundle path
	 */
	private String getTestFilePath(TestProject testProject) throws IOException {
		if (!testProject.getTestProjectConfig().getProjectPath().isEmpty()) {
			return testProject.getTestProjectConfig().getProjectPath();
		}
		return getPathToFitnesseJar();
	}

	/**
	 * Stops the running Fitnesse Server.
	 * 
	 * @param testProject
	 *            to identfy the server to stop
	 * @throws IOException
	 *             io exception
	 * @throws SystemException
	 *             system exception
	 */
	public void stopFitnesse(TestProject testProject) throws IOException, SystemException {

		LOGGER.info("Trying to Stop Fitnesse Server");

		FitNesseRestClient.stopTestServer("localhost", testProject.getTestProjectConfig().getPort());

		// waiting for stopping
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			LOGGER.info("Intterupting stop process.");
		}

		LOGGER.info("Fitness Server on Port: " + testProject.getTestProjectConfig().getPort() + " stopped.");

	}

	/**
	 * 
	 * @return path to the fitnesse jar.
	 * @throws IOException
	 *             on file location.
	 */
	protected String getPathToFitnesseJar() throws IOException {
		return fileLocatorService.findBundleFileLocationAsString("org.testeditor.fixture.lib");
	}

	@Override
	public void startTestServer(TestProject testProject) throws IOException, URISyntaxException {
		startFitnesse(testProject);
	}

	@Override
	public void stopTestServer(TestProject testProject) throws IOException {
		try {
			stopFitnesse(testProject);
		} catch (SystemException e) {
			LOGGER.error(e);
		}
	}

	/**
	 * 
	 * @param fileLocatorService
	 *            used in this service
	 * 
	 */
	public void bind(FileLocatorService fileLocatorService) {
		this.fileLocatorService = fileLocatorService;
		LOGGER.info("Bind FileLocatorService");
	}

	/**
	 * 
	 * @param fileLocatorService
	 *            removed from system
	 */
	public void unBind(FileLocatorService fileLocatorService) {
		this.fileLocatorService = null;
		LOGGER.info("Unbind FileLocatorService");
	}

	/**
	 * 
	 * @return a free port
	 */
	public synchronized int getFreePort() {
		ServerSocket serverSocket = null;
		int freePort = -1;
		try {

			serverSocket = new ServerSocket(0);
			freePort = serverSocket.getLocalPort();
			LOGGER.trace("freePort is : " + freePort);
			serverSocket.close();

		} catch (IOException e) {
			LOGGER.error(e.getMessage());
		}
		return freePort;
	}

	@Override
	public String getId() {
		return "fitnesse_based_1.2";
	}

}
