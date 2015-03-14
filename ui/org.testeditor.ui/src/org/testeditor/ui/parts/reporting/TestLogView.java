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
package org.testeditor.ui.parts.reporting;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.services.interfaces.TestEditorPlugInService;
import org.testeditor.core.services.interfaces.TestStructureService;
import org.testeditor.ui.constants.CustomWidgetIdConstants;
import org.testeditor.ui.constants.TestEditorUIEventConstants;

/**
 * View part to display the test log of the last run.
 *
 */
public class TestLogView {

	public static final String ID = "org.testeditor.ui.parts.reporting.TestLogView";

	private MPart part;
	private StyledText testLog;

	private static final Logger LOGGER = Logger.getLogger(TestLogView.class);

	@Inject
	private TestEditorPlugInService testEditorPlugInService;

	/**
	 * Default Constructor of the TestLogView.
	 * 
	 * @param part
	 *            to be used to communicate with the application model.
	 */
	@Inject
	public TestLogView(MPart part) {
		this.part = part;
	}

	/**
	 * Constructs the UI after creating and building this object.
	 * 
	 * @param parent
	 *            composite to build the ui on.
	 */
	@PostConstruct
	public void postConstruct(Composite parent) {
		parent.setLayout(new GridLayout(1, false));
		testLog = new StyledText(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		testLog.setLayoutData(new GridData(GridData.FILL_BOTH));
		testLog.setData(CustomWidgetIdConstants.TEST_EDITOR_WIDGET_ID_SWT_BOT_KEY, CustomWidgetIdConstants.TESTLOG_TEXT);
	}

	/**
	 * Sets the Focus in the text widget of this view.
	 */
	@Focus
	public void setFocus() {
		testLog.setFocus();
	}

	/**
	 * 
	 * @param testStructure
	 *            which last log should be displayed.
	 */
	public void setTestStructure(TestStructure testStructure) {
		try {
			TestStructureService testStructureService = testEditorPlugInService
					.getTestStructureServiceFor(testStructure.getRootElement().getTestProjectConfig().getTestServerID());
			String logData = testStructureService.getLogData(testStructure);
			part.setLabel("Test log: " + testStructure.getName());
			testLog.setText(logData);
		} catch (SystemException e) {
			LOGGER.error("Reading Testlog", e);
			final String errorMessage = e.getCause().getMessage();
			Display.getCurrent().syncExec(new Runnable() {
				@Override
				public void run() {
					MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error", errorMessage);
				}
			});
		}
	}

	/**
	 * Consumes the
	 * <code>TestEditorUIEventConstants.TESTSTRUCTURE_EXECUTED</code> event and
	 * updates the log content with the new execution result.
	 * 
	 * @param testStructure
	 *            which was executed.
	 */
	@Inject
	@Optional
	public void onTestExecutionShowTestLogForLastRun(
			@UIEventTopic(TestEditorUIEventConstants.TESTSTRUCTURE_EXECUTED) TestStructure testStructure) {
		setTestStructure(testStructure);
	}

	/**
	 * Retrieves the active editor event and loads the log for the Testcase in
	 * the editor.
	 * 
	 * @param aTestStructure
	 *            to be used in the testlog view.
	 */
	@Inject
	@Optional
	public void onActiveEditorChanged(
			@UIEventTopic(TestEditorUIEventConstants.ACTIVE_TESTFLOW_EDITOR_CHANGED) TestStructure aTestStructure) {
		TestStructureService testStructureService = testEditorPlugInService.getTestStructureServiceFor(aTestStructure
				.getRootElement().getTestProjectConfig().getTestServerID());
		try {
			if ((aTestStructure.isExecutableTestStructure()) && testStructureService.hasLogData(aTestStructure)) {
				onTestExecutionShowTestLogForLastRun(aTestStructure);
			}
		} catch (SystemException e) {
			LOGGER.error("Can't change Testlog to " + aTestStructure, e);
		}
	}
}
