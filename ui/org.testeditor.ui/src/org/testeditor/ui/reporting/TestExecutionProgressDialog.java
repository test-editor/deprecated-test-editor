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

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.contexts.Active;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.model.testresult.TestResult;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.services.interfaces.TestStructureService;
import org.testeditor.ui.constants.CustomWidgetIdConstants;
import org.testeditor.ui.constants.IconConstants;
import org.testeditor.ui.constants.TestEditorConstants;
import org.testeditor.ui.constants.TestEditorFontConstants;
import org.testeditor.ui.utilities.TestEditorTranslationService;

/**
 * 
 * Progress Dialog for Test Execution.
 * 
 */
public class TestExecutionProgressDialog extends ProgressMonitorDialog {

	private TestLogViewer logViewer;

	@Inject
	private static TestEditorTranslationService translationService;

	@Inject
	private IEclipseContext context;

	private TestResult testResult;

	private Button closeButton;

	private Composite resultArea;

	private Label icon;

	private Label labelTitle;

	private Label lblNewLabel;

	private Composite parent;

	private Button detailsButton;

	private Composite logViewerComposite;
	private Composite resultTestArea;

	@Inject
	@Named("ActualTCService")
	private TestStructureService testStructureService;

	private Point defaultDialogSize = new Point(500, 400);
	private static final Point ENLARGED_DIALOG_SIZE = new Point(850, 600);

	/**
	 * Constructor of the Dialog.
	 * 
	 * @param parent
	 *            Shell
	 */
	@Inject
	public TestExecutionProgressDialog(@Active Shell parent) {
		super(parent);

		setShellStyle(getDefaultOrientation() | SWT.BORDER | SWT.TITLE | SWT.APPLICATION_MODAL | SWT.RESIZE | SWT.MAX);

	}

	/**
	 * This method generates a simple formatted message of the test result.
	 * 
	 * @return Returns the complete formatted message to be displayed.
	 */
	private String generateTestResult() {
		StringBuilder sb = new StringBuilder("");

		if (testResult.isSuite()) {
			sb.append(translationService.translate("%SuiteResultMessage", testResult.getRight(), testResult.getWrong()
					+ testResult.getException()));
		} else {
			sb.append(translationService.translate("%TestResultMessage", testResult.getRight(), testResult.getWrong()));
		}

		String runTimeHr = getHumanReadableFormatedRuntime(testResult.getRunTimesSec());
		sb.append("\n").append(runTimeHr);
		return sb.toString();
	}

	/**
	 * returns the runtime in human readable format as day, hour, minutes and
	 * seconds.
	 * 
	 * @param seconds
	 *            the runtime in seconds
	 * @return the formated String
	 */
	private String getHumanReadableFormatedRuntime(long seconds) {
		int day = (int) TimeUnit.SECONDS.toDays(seconds);
		long hours = TimeUnit.SECONDS.toHours(seconds) - TimeUnit.DAYS.toHours(day);
		long minute = TimeUnit.SECONDS.toMinutes(seconds) - TimeUnit.HOURS.toMinutes(TimeUnit.SECONDS.toHours(seconds));
		long second = TimeUnit.SECONDS.toSeconds(seconds)
				- TimeUnit.MINUTES.toSeconds(TimeUnit.SECONDS.toMinutes(seconds));
		if (day > 0) {
			return translationService.translate("%TestRuntimeDay", day, hours, minute, second);
		} else if (hours > 0) {
			return translationService.translate("%TestRuntimeHour", hours, minute, second);
		} else if (minute > 0) {
			return translationService.translate("%TestRuntimeMinute", minute, second);
		} else {
			return translationService.translate("%TestRuntimeSecond", second);
		}
	}

	@Override
	protected Control createDialogArea(final Composite parent) {
		parent.getShell().setImage(IconConstants.ICON_TESTEDITOR);
		this.parent = parent;
		Control control = super.createDialogArea(parent);

		resultArea = new Composite(parent, SWT.NONE);
		resultArea.setLayout(new GridLayout(2, false));
		icon = new Label(resultArea, SWT.CENTER);

		resultTestArea = new Composite(resultArea, SWT.NONE);
		resultTestArea.setLayout(new GridLayout(1, false));

		labelTitle = new Label(resultTestArea, SWT.CENTER);
		new Label(resultTestArea, SWT.NONE);
		lblNewLabel = new Label(resultTestArea, SWT.SHADOW_IN);

		logViewer = ContextInjectionFactory.make(TestLogViewer.class, context);
		logViewerComposite = logViewer.createUI(parent);
		logViewerComposite.setVisible(false);

		// Logfile is currently expected in the folder of the workspace of the
		// application not the jvm.

		File wsDir = Platform.getLocation().toFile();
		File interActionLogFile = new File(wsDir.getAbsolutePath() + File.separator + ".metadata/logs/"
				+ TestEditorConstants.INTERACTION_LOG_FILE_NAME);

		logViewer.setAbsolutelogFileName(interActionLogFile.getAbsolutePath());

		return control;
	}

	/**
	 * Executes the Test in this ProgressDialog.
	 * 
	 * @param toExecute
	 *            Test
	 * @throws InvocationTargetException
	 *             by error
	 * @throws InterruptedException
	 *             by user interrupt
	 * @return the TestResult of the Test execution.
	 */
	public TestResult executeTest(final TestStructure toExecute) throws InvocationTargetException, InterruptedException {
		testResult = null;
		this.run(true, true, new IRunnableWithProgress() {

			@Override
			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
				try {
					logViewer.startTailOnTestLog(toExecute);
					String executeTestName = translationService.translate("%execute.test.name", toExecute.getFullName());

					monitor.beginTask(executeTestName, IProgressMonitor.UNKNOWN);
					testResult = testStructureService.executeTestStructure(toExecute, monitor);
					monitor.done();
					logViewer.stopTailOnTestLog();
				} catch (SystemException e) {
					throw new InvocationTargetException(e);
				}
			}

		});
		return testResult;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.dialogs.ProgressMonitorDialog#createButtonsForButtonBar
	 * (org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {

		// adding new button for closing the dialog on demand
		closeButton = createButton(parent, IDialogConstants.CLOSE_ID, IDialogConstants.CLOSE_LABEL, true);
		closeButton.setData(CustomWidgetIdConstants.TEST_EDITOR_WIDGET_ID_SWT_BOT_KEY,
				CustomWidgetIdConstants.RUN_TEST_CLOSE_BUTTON);
		closeButton.setEnabled(false);

		closeButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				close();
				dispose();
			}
		});

		// adding new button for closing the dialog on demand
		detailsButton = createButton(parent, IDialogConstants.DETAILS_ID, IDialogConstants.SHOW_DETAILS_LABEL, true);

		detailsButton.addSelectionListener(getSwitchDetailSelectionListener());

		super.createButtonsForButtonBar(parent);
	}

	/**
	 * 
	 * @return SelectionListener to switch the detailed log view in and out in
	 *         the dialog.
	 */
	protected SelectionListener getSwitchDetailSelectionListener() {
		return new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if (logViewerComposite.getVisible()) {
					logViewerComposite.setVisible(false);
					detailsButton.setText(IDialogConstants.SHOW_DETAILS_LABEL);
					TestExecutionProgressDialog.this.parent.getShell().setSize(defaultDialogSize);
				} else {
					logViewerComposite.setVisible(true);
					detailsButton.setText(IDialogConstants.HIDE_DETAILS_LABEL);
					defaultDialogSize = TestExecutionProgressDialog.this.parent.getShell().getSize();
					TestExecutionProgressDialog.this.parent.getShell().setSize(ENLARGED_DIALOG_SIZE);
				}

				TestExecutionProgressDialog.this.parent.getShell().layout();
			}
		};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.ProgressMonitorDialog#finishedRun()
	 */
	@Override
	protected void finishedRun() {
		decrementNestingDepth();

		setResultData();

		// remove standard widgets of progressdialog
		messageLabel.dispose();
		imageLabel.dispose();
		progressIndicator.dispose();
		taskLabel.dispose();
		subTaskLabel.dispose();

		// after finishing test, close button will be enabled
		closeButton.setEnabled(true);
		closeButton.setFocus();
		getButton(IDialogConstants.CANCEL_ID).setEnabled(false);

		parent.layout();
	}

	/**
	 * Sets the test result data on dialog after finished test.
	 * 
	 */
	public void setResultData() {

		// opens a Message Dialog dependent on the test result
		if (testResult != null && !testResult.isNotRun()) {

			labelTitle.setFont(TestEditorFontConstants.FONT_BOLD);

			if (testResult.isSuccessfully()) {

				icon.setImage(IconConstants.ICON_TESTEXECUTION_OK);
				labelTitle.setText(translationService.translate("%TestResultSuccessfullTitle"));
				lblNewLabel.setText(generateTestResult());

			} else {

				icon.setImage(IconConstants.ICON_TESTEXECUTION_FAILED);
				labelTitle.setText(translationService.translate("%TestResultNotSuccessfullTitle"));
				lblNewLabel.setText(generateTestResult());

			}
		} else {

			icon.setImage(IconConstants.ICON_WARNING);
			labelTitle.setText(translationService.translate("%TestResultNotRanTitle"));
			lblNewLabel.setText("");
		}

	}

	/**
	 * dispose the resources.
	 */
	@PreDestroy
	public void dispose() {
		tryToDisposeWidget(closeButton);
		tryToDisposeWidget(detailsButton);
		tryToDisposeWidget(icon);
		tryToDisposeWidget(labelTitle);
		tryToDisposeWidget(lblNewLabel);
		tryToDisposeWidget(resultArea);
		tryToDisposeWidget(logViewerComposite);
		tryToDisposeWidget(resultTestArea);
		if (testResult != null) {
			testResult = null;
		}
	}

	/**
	 * Checks if the widget is disposable and dispose it and set the referenz to
	 * null.
	 * 
	 * @param widget
	 *            to be disposed
	 */
	private void tryToDisposeWidget(Widget widget) {
		if (widget != null && !widget.isDisposed()) {
			widget.dispose();
			widget = null;
		}
	}

}
