package org.testeditor.core.services.impl;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.testeditor.core.model.teststructure.TestProject;

public class FileWatcher {
	// A hardcoded path to a folder you are monitoring .
	public static final String FOLDER = "c:/transfer/";
	public static final List<String> WATCH_FILE_LIST = Arrays.asList("AllActionGroups.xml",
			"TechnicalBindingTypeCollection.xml");

	private TestProject testProjekt;

	public FileWatcher(TestProject testProjekt) {
		this.testProjekt = testProjekt;

	}

	public void watch() throws Exception {
		// The monitor will perform polling on the folder every 5 seconds
		final long pollingInterval = 5 * 1000;

		String pathToTestFiles = testProjekt.getTestProjectConfig().getProjectPath();

		File folder = new File(pathToTestFiles);

		if (!folder.exists()) {
			// Test to see if monitored folder exists
			throw new RuntimeException("Directory not found: " + pathToTestFiles);
		}

		FileAlterationObserver observer = new FileAlterationObserver(folder);
		FileAlterationMonitor monitor = new FileAlterationMonitor(pollingInterval);

		FileAlterationListener listener = new FileAlterationListenerAdaptor() {
			@Override
			public void onFileChange(File file) {

				if (WATCH_FILE_LIST.contains(file.getName())) {
					try {

						System.out.println("Projekt: " + testProjekt.getName());
						System.out.println("-------------------------------------------------------------");
						System.out.println("File changed: " + file.getCanonicalPath());
						System.out.println("File still exists in location: " + file.exists());
					} catch (IOException e) {
						e.printStackTrace(System.err);
					}
				}
			}
		};

		observer.addListener(listener);
		monitor.addObserver(observer);
		monitor.start();
	}
}