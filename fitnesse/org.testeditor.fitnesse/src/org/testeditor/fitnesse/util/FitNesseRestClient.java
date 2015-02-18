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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.model.testresult.TestResult;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.fitnesse.filesystem.FitnesseFileSystemTestStructureService;

/**
 * Client implementation calls the FitNesse REST service. The interface
 * documentation could be found here:
 * http://fitnesse.org/FitNesse.UserGuide.RestfulServices
 */
public final class FitNesseRestClient {

	/**
	 * Class can not be intantiated.
	 */
	private FitNesseRestClient() {
	}

	private static final Logger LOGGER = Logger.getLogger(FitNesseRestClient.class);

	/**
	 * Looks up the TestProjectConfiguration to build the Fitnesse URL.
	 * 
	 * @param testStructure
	 *            to retrive the TestProjectConfiguration
	 * @return the url to the fitnesse server.
	 */
	public static String getFitnesseUrl(TestStructure testStructure) {
		TestProject tp = testStructure.getRootElement();
		return getFitnesseUrl(tp.getTestProjectConfig().getPort());
	}

	/**
	 * 
	 * @param port
	 *            the port as String
	 * @return the url to the fitnesse server.
	 */
	// TODO [TE-1545] Port
	private static String getFitnesseUrl(String port) {
		StringBuilder sb = new StringBuilder();
		sb.append("http://localhost:").append(port).append("/");
		return sb.toString();
	}

	/**
	 * Execute a test case. in case of not running test, it will be return an
	 * default TestResult object
	 * 
	 * @param testStructure
	 *            test case @link {@link TestStructure}
	 * @param monitor
	 *            monitors cancel of test
	 * @return test result @link {@link TestResult}
	 * @throws SystemException
	 *             is thrown in case of IO or connection exceptions
	 * @throws InterruptedException
	 *             will thrown when user terminates the test
	 */
	public static TestResult execute(final TestStructure testStructure, IProgressMonitor monitor)
			throws SystemException, InterruptedException {

		final String fullName = testStructure.getFullName();

		int testHistoryBeforeTestSize = new FitnesseFileSystemTestStructureService().getTestHistory(testStructure)
				.size();

		try {

			Thread testExecutor = new Thread() {
				@Override
				public void run() {
					try {
						URL url = new URL(getFitnesseUrl(testStructure) + fullName + "?" + testStructure.getTypeName());
						URLConnection con = url.openConnection();
						InputStream in = con.getInputStream();
						BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
						String line = bufferedReader.readLine();
						while (line != null) {
							line = bufferedReader.readLine();
						}
						bufferedReader.close();
						in.close();
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				};
			};
			testExecutor.start();
			while (testExecutor.isAlive()) {
				if (monitor != null && monitor.isCanceled()) {

					// stop test via REST-Call
					URL urlStopTest = new URL(getFitnesseUrl(testStructure) + fullName + "?stoptest");
					URLConnection conStopTest = urlStopTest.openConnection();
					InputStream inputStream = conStopTest.getInputStream();

					inputStream.close();
					testExecutor.interrupt();
					throw new InterruptedException();
				}
				Thread.sleep(5);
			}
		} catch (InterruptedException e) {
			throw e;
		} catch (Exception e) {
			SystemException systemException = new SystemException("execute test failed", e);
			throw systemException;
		} finally {
			if (monitor != null) {
				monitor.done();
			}
		}

		List<TestResult> testHistoryAfterTest = new FitnesseFileSystemTestStructureService()
				.getTestHistory(testStructure);

		boolean isTestSystemExecuted = testHistoryAfterTest.size() > testHistoryBeforeTestSize;
		if (isTestSystemExecuted) {
			return testHistoryAfterTest.get(0);
		} else {
			return new TestResult();
		}
	}

	/**
	 * Stopps the FitNesse Server.
	 * 
	 * @param host
	 *            Machine where FitNesse is running
	 * @param port
	 *            Port of FitNesse Server
	 * @throws SystemException
	 *             Exception.
	 */
	public static void stopTestServer(String host, String port) throws SystemException {

		HttpGet httpGet = new HttpGet("http://" + host + ":" + port + "/resource?shutdown");
		httpGet.setHeader("Content-Type", "application/text");

		try {
			HttpClient httpclient = HttpClientBuilder.create().setMaxConnPerRoute(5).setMaxConnTotal(5).build();
			HttpResponse httpResponse = httpclient.execute(httpGet);

			new BasicResponseHandler().handleResponse(httpResponse);

		} catch (Exception e) {
			SystemException systemException = new SystemException("stopServer failed", e);
			LOGGER.error("stopTestServer :: FAILED host: " + host + " port: " + port, systemException);
			throw systemException;
		}

	}

	/**
	 * Returns a list of the direct children from the given level. Method
	 * doesn't include the grand children to the object structure.
	 * 
	 * @param testStructure
	 *            TestStructure i.e. TestScenario
	 * @return resultPage from usedWhere request.
	 * @throws SystemException
	 *             Exception.
	 */
	public static String getUsedWhere(TestStructure testStructure) throws SystemException {

		long start = System.currentTimeMillis();

		final String fullName = testStructure.getFullName();

		HttpGet httpGet = new HttpGet(getFitnesseUrl(testStructure) + fullName + "?whereUsed");
		httpGet.setHeader("Content-Type", "application/json");

		String strOfWikiPages;

		try {

			HttpClient httpclient = HttpClientBuilder.create().setMaxConnPerRoute(5).setMaxConnTotal(5).build();
			HttpResponse httpResponse = httpclient.execute(httpGet);

			strOfWikiPages = new BasicResponseHandler().handleResponse(httpResponse);
		} catch (Exception e) {
			SystemException systemException = new SystemException("No Testcases found in: "
					+ getFitnesseUrl(testStructure) + fullName + "\n", e);
			LOGGER.error("names :: FAILED", systemException);
			throw systemException;
		}

		LOGGER.trace(System.currentTimeMillis() - start);

		return strOfWikiPages;
	}

	/**
	 * This method searches for the FitNesse-server for the project. It searches
	 * on the FitNesse-server, that is running under the configured port, after
	 * the main page of project. If the main-page is found, than it returns
	 * true, else false.
	 * 
	 * @param testProject
	 *            the {@link TestProject}
	 * @return true if a FitNesse-server is for this project.
	 */
	public static boolean isFitNesseProjectServerRunning(TestProject testProject) {

		long start = System.currentTimeMillis();

		final String projectName = testProject.getName();

		HttpGet httpGet = new HttpGet(getFitnesseUrl(testProject) + projectName + "?search&searchString=" + projectName
				+ "&searchType=title");
		httpGet.setHeader("Content-Type", "application/json");

		String strOfWikiPages;

		try {

			HttpClient httpclient = HttpClientBuilder.create().setMaxConnPerRoute(5).setMaxConnTotal(5).build();
			HttpResponse httpResponse = httpclient.execute(httpGet);

			strOfWikiPages = new BasicResponseHandler().handleResponse(httpResponse);
			String searchString = "<a href=\"" + projectName + "\">" + projectName + "</a>";
			if (strOfWikiPages.contains(searchString)) {

				LOGGER.trace(System.currentTimeMillis() - start);

				return true;
			}
		} catch (Exception e) {
			SystemException systemException = new SystemException("No FitNesse found in: "
					+ getFitnesseUrl(testProject) + projectName + "\n", e);
			LOGGER.error("No FitNesse found: ", systemException);
		}

		return false;
	}

}
