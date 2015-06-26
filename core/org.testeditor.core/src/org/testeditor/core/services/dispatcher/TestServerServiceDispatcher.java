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
package org.testeditor.core.services.dispatcher;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.services.interfaces.TestServerService;
import org.testeditor.core.services.plugins.TestServerServicePlugIn;

/**
 * 
 * Dispatcher class of the TestServerService. It looks up the corresponding
 * plug-in for the test structure to work on.
 *
 */
public class TestServerServiceDispatcher implements TestServerService {

	private static final Logger LOGGER = Logger.getLogger(TestServerServiceDispatcher.class);
	private Map<String, TestServerServicePlugIn> testServerServices = new HashMap<String, TestServerServicePlugIn>();

	/**
	 * 
	 * @param testStructureService
	 *            used in this service
	 * 
	 */
	public void bind(TestServerServicePlugIn testStructureService) {
		this.testServerServices.put(testStructureService.getId(), testStructureService);
		LOGGER.info("Bind TestStructureService Plug-In" + testStructureService.getClass().getName());
	}

	/**
	 * 
	 * @param testStructureService
	 *            removed from system
	 */
	public void unBind(TestServerServicePlugIn testStructureService) {
		this.testServerServices.remove(testStructureService.getId());
		LOGGER.info("UnBind TestStructureService Plug-In" + testStructureService.getClass().getName());
	}

	@Override
	public void startTestServer(TestProject testProject) throws IOException, URISyntaxException {
		testServerServices.get(testProject.getTestProjectConfig().getTestServerID()).startTestServer(testProject);
	}

	@Override
	public void stopTestServer(TestProject testProject) throws IOException {
		TestServerServicePlugIn serverServicePlugIn = testServerServices.get(testProject.getTestProjectConfig()
				.getTestServerID());
		if (serverServicePlugIn != null) {
			serverServicePlugIn.stopTestServer(testProject);
		}
	}

	@Override
	public boolean isRunning(TestProject testProject) {
		return testServerServices.get(testProject.getTestProjectConfig().getTestServerID()).isRunning(testProject);
	}

}
