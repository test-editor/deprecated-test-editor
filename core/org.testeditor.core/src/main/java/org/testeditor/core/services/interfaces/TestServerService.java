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
import java.net.URISyntaxException;

import org.testeditor.core.model.teststructure.TestProject;

/**
 * 
 * Service to manage a TestServer.
 * 
 */
public interface TestServerService {

	/**
	 * Starts the Test Server.
	 * 
	 * @param testProject
	 *            TestProject which must start
	 * 
	 * @throws IOException
	 *             from Process execution
	 * @throws URISyntaxException
	 *             from bundle File lookup
	 */
	void startTestServer(TestProject testProject) throws IOException, URISyntaxException;

	/**
	 * Stops the running TestServer.
	 * 
	 * @param testProject
	 *            TestProject which must stop
	 * 
	 * @throws IOException
	 *             io exception
	 */
	void stopTestServer(TestProject testProject) throws IOException;

	/**
	 * Returns true if test Server is running on given configuration.
	 * 
	 * @param testProject
	 *            to identify the serverprocess.
	 * 
	 * @return true if server is running.
	 */
	boolean isRunning(TestProject testProject);

}
