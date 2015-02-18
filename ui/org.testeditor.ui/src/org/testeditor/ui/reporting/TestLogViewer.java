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
package org.testeditor.ui.reporting;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.ui.constants.TestEditorConstants;
import org.testeditor.ui.utilities.TestEditorTestLogAddErrorStyle;

/**
 * 
 * Widget to show the Log of a Testcase Execution.
 * 
 */
public class TestLogViewer {

	private static final Logger LOGGER = Logger.getLogger(TestLogViewer.class);

	private StyledText testLogText;
	private String absolutelogFileName;
	private long refreshTime = 300;
	private Thread watchingLogFileThread;
	private boolean watchingLogFile;
	private TestStructure executingTestStructure;
	@Inject
	private IEventBroker eventBroker;

	/**
	 * 
	 * @param refreshTime
	 *            to be used in reading the logfile
	 */
	public void setRefreshTime(long refreshTime) {
		this.refreshTime = refreshTime;
	}

	/**
	 * 
	 * @param absolutelogFileName
	 *            Path to the Logfile of the Testexecution.
	 * 
	 */
	public void setAbsolutelogFileName(String absolutelogFileName) {
		this.absolutelogFileName = absolutelogFileName;
	}

	/**
	 * 
	 * @param composite
	 *            to build the logviewer ui on.
	 * @return root Composite of this UI Widget.
	 */
	public Composite createUI(Composite composite) {
		testLogText = new StyledText(composite, SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER | SWT.READ_ONLY);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalSpan = 2;
		testLogText.setLayoutData(gridData);
		return testLogText;
	}

	/**
	 * Starting the Tail on the Test Logfile.
	 * 
	 * @param toExecute
	 *            TestStructure to be executed.
	 */
	public void startTailOnTestLog(TestStructure toExecute) {
		this.executingTestStructure = toExecute;
		if (new File(absolutelogFileName).exists()) {
			Runnable runnable = createLogFileTailRunnable();
			watchingLogFile = true;
			watchingLogFileThread = new Thread(runnable);
			watchingLogFileThread.start();
		}
	}

	/**
	 * Creates a Runnable to watch the interaction log of the TestCase and send
	 * the content to the ui. It waits for a new interaction log file for 50
	 * seconds. If there is no new interaction log file a runtime exception is
	 * thrown. On thread interruption it collects the end of the log file and
	 * sends it to the ui.
	 * 
	 * @return Runnable to watch the Logfile.
	 */
	private Runnable createLogFileTailRunnable() {
		return new Runnable() {

			@Override
			public void run() {
				try {
					File wsDir = Platform.getLocation().toFile();
					File interActionLogFile = new File(wsDir.getAbsolutePath() + File.separator + ".metadata"
							+ File.separator + "logs" + File.separator + TestEditorConstants.INTERACTION_LOG_FILE_NAME);
					int noLogErrorCounter = 0;
					while (new Date().getTime()
							- Files.readAttributes(interActionLogFile.toPath(), BasicFileAttributes.class)
									.lastModifiedTime().toMillis() > 1000) {
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
					final BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(
							interActionLogFile), "UTF-8"));
					String fullTestName = executingTestStructure.getFullName().replace('.', '/');
					while (watchingLogFile) {
						Display.getDefault().asyncExec(getUIUpdateRunnable(br, fullTestName));
						try {
							Thread.sleep(getRefreshTime());
							if (testLogText.isDisposed()) {
								watchingLogFile = false;
							}
						} catch (InterruptedException e) {
							// Syncing the final log entry
							Display.getDefault().syncExec(getUIUpdateRunnable(br, fullTestName));
							if (LOGGER.isInfoEnabled()) {
								LOGGER.info("User " + System.getProperty("user.name")
										+ " Interrupt while tail on testlog");
							}
						}
					}
					br.close();
				} catch (FileNotFoundException e) {
					LOGGER.error("No Fitnesse Log found at path: " + absolutelogFileName, e);
				} catch (IOException e) {
					LOGGER.error("IO Error", e);
				}
			}
		};
	}

	/**
	 * 
	 * @param br
	 *            BufferedReader of the TestLog File
	 * @param fullTestName
	 *            Complete Name of the TestObject including project and parant
	 *            Testsuite sepreated by an /. For example:
	 *            MyProject/ATestSuite/MyTestCase
	 * @return Runnable to update the StyledText in the UI with fresh LogFile
	 *         Content.
	 */
	protected Runnable getUIUpdateRunnable(final BufferedReader br, final String fullTestName) {
		return new Runnable() {

			@Override
			public void run() {
				StringBuilder sb = new StringBuilder();
				try {
					while (br.ready()) {
						sb.append(br.readLine()).append("\n");
					}
				} catch (IOException e) {
					LOGGER.error("IO Error", e);
				}
				String testProgress = sb.toString();
				if (testProgress.length() > 0) {
					sendEvents(testProgress);
					testLogText.append(testProgress);
					testLogText.setSelection(testLogText.getText().length());

					TestEditorTestLogAddErrorStyle testEditorTestLogAddErrorStyle = new TestEditorTestLogAddErrorStyle();
					testLogText.setStyleRanges(testEditorTestLogAddErrorStyle.addErrorStyle(testLogText.getText()));
					testLogText.redraw();
				}
			}

			/**
			 * Extracts only TestEditorInteractionEvents to the EventBroker.
			 * 
			 * @param testProgress
			 *            to extract InteractionEvents from.
			 */
			protected void sendEvents(String testProgress) {
				String[] lines = testProgress.split("\n");
				for (String line : lines) {
					if (line.indexOf(TestEditorConstants.LOGGING_INTERACTION) > -1) {
						eventBroker.post(TestEditorConstants.TEST_EXECUTION_PRGRESS_EVENT + "/" + fullTestName, line);
					}
				}
			}
		};
	}

	/**
	 * 
	 * @return time to wait for the next refresh.
	 */
	protected long getRefreshTime() {
		return refreshTime;
	}

	/**
	 * 
	 * @return styledtext of this widget.
	 */
	protected String getTestLogText() {
		return testLogText.getText();
	}

	/**
	 * 
	 * @param executingTestStructure
	 *            to be used for callbacks
	 */
	protected void setExecutingTestStructure(TestStructure executingTestStructure) {
		this.executingTestStructure = executingTestStructure;
	}

	/**
	 * Stop the Tail watching on the Logfile.
	 */
	public void stopTailOnTestLog() {
		watchingLogFile = false;
		executingTestStructure = null;
		if (watchingLogFileThread != null) {
			watchingLogFileThread.interrupt();
		}
	}

}
