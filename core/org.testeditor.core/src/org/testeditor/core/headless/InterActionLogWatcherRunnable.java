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
package org.testeditor.core.headless;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.Platform;
import org.testeditor.core.constants.TestEditorCoreConstants;

/**
 * Runnable to watch the interaction log and reports Progress to the Log.
 *
 */
public class InterActionLogWatcherRunnable implements Runnable {

	private static final Logger LOGGER = Logger.getLogger(InterActionLogWatcherRunnable.class);

	private long refreshTime = 30;
	private boolean watchingLogFile;

	private int count = 1;

	@Override
	public void run() {
		try {
			LOGGER.info("Observing interaction log on execution...");
			File wsDir = Platform.getLocation().toFile();
			File interActionLogFile = new File(wsDir.getAbsolutePath() + File.separator + ".metadata" + File.separator
					+ "logs" + File.separator + TestEditorCoreConstants.INTERACTION_LOG_FILE_NAME);
			int noLogErrorCounter = 0;
			while (new Date().getTime()
					- Files.readAttributes(interActionLogFile.toPath(), BasicFileAttributes.class).lastModifiedTime()
							.toMillis() > 1000) {
				try {
					Thread.sleep(50);
					noLogErrorCounter++;
					if (noLogErrorCounter == 1000) {
						throw new RuntimeException("No Interaction Log found in 50 seconds.");
					}
				} catch (InterruptedException e) {
					LOGGER.info("Interrupt during wating for new interaction log.", e);
				}
			}
			wathingTheLog(interActionLogFile);
		} catch (FileNotFoundException e) {
			LOGGER.error("No Fitnesse Log found", e);
		} catch (IOException e) {
			LOGGER.error("IO Error", e);
		}
	}

	/**
	 * Watches the logfile.
	 * 
	 * @param interActionLogFile
	 *            File to be watched
	 * @throws IOException
	 *             on IO failure.
	 */
	protected void wathingTheLog(File interActionLogFile) throws IOException {
		final BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(interActionLogFile),
				"UTF-8"));
		watchingLogFile = true;
		int foundTests = 0;
		while (watchingLogFile) {
			while (br.ready()) {
				String line = br.readLine();
				if (line.contains("Method : setTestName")) {
					String testcasename = line.substring(line.indexOf("\"") + 1, line.lastIndexOf("\""));
					LOGGER.info("******* Executing TestCase: " + testcasename + " " + ++foundTests + " of " + count
							+ " *******");
				} else {
					if (!line.contains("Wait ")) {
						LOGGER.trace(line);
					}
				}
			}
			try {
				Thread.sleep(refreshTime);
			} catch (InterruptedException e) {
				if (LOGGER.isInfoEnabled()) {
					LOGGER.info("User " + System.getProperty("user.name") + " Interrupt while tail on testlog");
				}
			}
		}
		br.close();
	}

	/**
	 * 
	 * @return refresh time to watch in the log file.
	 */
	public long getRefreshTime() {
		return refreshTime;
	}

	/**
	 * 
	 * @param refreshTime
	 *            used to sleep between reading logfile
	 */
	public void setRefreshTime(long refreshTime) {
		this.refreshTime = refreshTime;
	}

	/**
	 * Indicates to stop watching the logfile.
	 */
	public void stopWatching() {
		watchingLogFile = false;
	}

	/**
	 * 
	 * @param count
	 *            of test cases to be executed.
	 */
	public void setTestCaseCount(int count) {
		this.count = count;
	}

}
