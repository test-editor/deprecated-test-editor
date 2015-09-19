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
package org.testeditor.core.services.impl;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.log4j.Logger;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.testeditor.core.constants.TestEditorCoreEventConstants;
import org.testeditor.core.model.teststructure.TestProject;

/**
 * Class for watching on filesystem if given list of files in given folder were
 * changed.
 *
 */
public class FileWatcher {

	private static final Logger LOGGER = Logger.getLogger(FileWatcher.class);

	private List<String> watchFileList = Arrays.asList("AllActionGroups.xml", "TechnicalBindingTypeCollection.xml");

	private TestProject testProject;

	@Inject
	private IEventBroker eventBroker;

	private FileAlterationMonitor monitor;

	/**
	 * Default Constructor.
	 * 
	 * @param testProject
	 *            {@link TestProject} for watching
	 */
	@Inject
	public FileWatcher(TestProject testProject) {
		this.testProject = testProject;
	}

	/**
	 * Watches changes on given list of files and sends a notification to
	 * observer.
	 * 
	 * @throws Exception
	 *             will be thrown if starting of monitor fails.
	 */
	public void watch() throws Exception {
		// The monitor will perform polling on the folder every 5 seconds
		final long pollingInterval = 5 * 1000;

		String pathToTestFiles = testProject.getTestProjectConfig().getProjectPath();

		monitor = new FileAlterationMonitor(pollingInterval);

		FileAlterationListener listener = new FileAlterationListenerAdaptor() {
			@Override
			public void onFileChange(File file) {

				try {

					LOGGER.info("Projekt: " + testProject.getName());
					LOGGER.info("File changed: " + file.getCanonicalPath());

					notifyObserver();

				} catch (IOException e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
		};

		File projectDir = new File(pathToTestFiles);
		for (String fileName : watchFileList) {
			FileAlterationObserver observer = new FileAlterationObserver(projectDir,
					FileFilterUtils.nameFileFilter(fileName));
			observer.addListener(listener);
			monitor.addObserver(observer);
		}
		monitor.start();
	}

	/**
	 * Send notification by eventbroker for inform observer.
	 */
	private void notifyObserver() {
		if (eventBroker != null) {
			eventBroker.post(TestEditorCoreEventConstants.LIBRARY_FILES_CHANGED_MODIFIED, testProject);
		}
	}

	/**
	 * Stops the file watcher.
	 */
	public void stop() {
		try {
			monitor.stop();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

}