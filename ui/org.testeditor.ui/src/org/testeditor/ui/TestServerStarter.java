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
package org.testeditor.ui;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.inject.Inject;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.services.interfaces.TestServerService;
import org.testeditor.ui.utilities.TestEditorTranslationService;

/**
 * 
 * Handle Start and stop of a Testserver in the UI Context.
 * 
 */
public class TestServerStarter {

	@Inject
	private TestServerService testServerService;

	@Inject
	private TestEditorTranslationService translate;

	/**
	 * Starts the TestServer.
	 * 
	 * @param testProject
	 *            for this project
	 * @throws URISyntaxException
	 *             for Server URL
	 * @return true, if FitNesse - server is started.
	 * @throws IOException
	 *             IOException
	 */
	public boolean startTestServer(final TestProject testProject) throws IOException, URISyntaxException {
		if (testProject.getTestProjectConfig() == null) {
			return showNoConfigurationForProjectError(testProject.getName());
		}
		testServerService.startTestServer(testProject);

		return true;
	}

	/**
	 * shows the that their is no configuration for the project.
	 * 
	 * @param testProjectName
	 *            the name of the project without configuration
	 * @return false
	 */
	private boolean showNoConfigurationForProjectError(final String testProjectName) {
		final Display currentDisplay = Display.getDefault();
		currentDisplay.syncExec(new Runnable() {

			@Override
			public void run() {
				MessageDialog.openError(currentDisplay.getActiveShell(), translate.translate("%error"),
						translate.translate("%error.loading_project", testProjectName));

			}
		});
		return false;
	}

	/**
	 * Stops the testserver.
	 * 
	 * @param testProject
	 *            of this project
	 * @throws IOException
	 *             ioexception
	 */
	public void stopTestServer(TestProject testProject) throws IOException {
		testServerService.stopTestServer(testProject);
	}

}
