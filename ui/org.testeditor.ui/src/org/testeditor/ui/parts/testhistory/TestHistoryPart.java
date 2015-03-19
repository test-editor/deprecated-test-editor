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
package org.testeditor.ui.parts.testhistory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.testeditor.core.constants.TestEditorCoreEventConstants;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.model.testresult.TestResult;
import org.testeditor.core.model.teststructure.TestProjectConfig;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.model.teststructure.TestType;
import org.testeditor.core.services.interfaces.TestEditorPlugInService;
import org.testeditor.core.services.interfaces.TestStructureService;
import org.testeditor.ui.constants.IconConstants;
import org.testeditor.ui.constants.TestEditorUIEventConstants;
import org.testeditor.ui.utilities.TestEditorTranslationService;

/**
 * 
 * Part in the Application model to represent the TestHistory ViewPart. This
 * class contains the controller for the TestHistory-Part. UI elements are build
 * in <code>TestHistoryView</code>.
 * 
 * 
 */
public class TestHistoryPart {

	private static final Logger LOGGER = Logger.getLogger(TestHistoryPart.class);

	public static final String ID = "org.testeditor.ui.part.testhistory";

	@Inject
	private IEclipseContext context;

	@Inject
	private TestEditorPlugInService testEditorPlugInService;

	@Inject
	private TestEditorTranslationService translationService;

	private TestHistoryView testHistoryView;
	private ArrayList<Button> buttonArray = new ArrayList<Button>();

	private TestStructure testStructure;

	private List<TestResult> testHistory;

	/**
	 * Consumes the event of deleted Teststructues to remove ui informations on
	 * deleted ressources.
	 * 
	 * @param testStructureFullname
	 *            of the deleted Teststructure.
	 */
	@Inject
	@Optional
	public void onTestSturctureRenamedEvent(
			@UIEventTopic(TestEditorCoreEventConstants.TESTSTRUCTURE_HISTORY_DELETED) String testStructureFullname) {
		if (testStructure != null && testStructureFullname.equals(testStructure.getFullName())) {
			clearView();
			testStructure = null;
			testHistory = null;
		}
	}

	/**
	 * Consumes show history event for test structure.
	 * 
	 * @param testStructure
	 *            to be showed
	 */
	@Inject
	@Optional
	public void showTestHistory(
			@UIEventTopic(TestEditorUIEventConstants.TESTSTRUCTURE_EXECUTED) TestStructure testStructure) {
		getTestHistoryPart().setTitle(testStructure.getName());
		refreshHistoryTable(testStructure);
	}

	/**
	 * Retrieves the active editor event and loads the history for the Testcase
	 * in the editor.
	 * 
	 * @param aTestStructure
	 *            to be used in the history view.
	 */
	@Inject
	@Optional
	public void onActiveEditorChanged(
			@UIEventTopic(TestEditorUIEventConstants.ACTIVE_TESTFLOW_EDITOR_CHANGED) TestStructure aTestStructure) {
		if (aTestStructure != null) {
			if (aTestStructure.isExecutableTestStructure()) {
				showTestHistory(aTestStructure);
			}
		}
	}

	/**
	 * Refresh the history table with current history data of teststructure.
	 * 
	 * @param testStructure
	 *            the {@linkTestStructure}
	 */
	private void refreshHistoryTable(TestStructure testStructure) {

		this.testStructure = testStructure;
		if (!testStructure.getTypeName().equalsIgnoreCase(TestType.TESTSCENARIO.getName())) {

			clearButtonArray();
			getTestHistoryPart().clearTable();

			testHistory = null;
			try {
				TestStructureService testStructureService = testEditorPlugInService
						.getTestStructureServiceFor(testStructure.getRootElement().getTestProjectConfig()
								.getTestServerID());
				testHistory = testStructureService.getTestHistory(testStructure);
			} catch (SystemException e) {
				LOGGER.error(e);
			}

			for (TestResult testResult : testHistory) {

				addHistoryToTable(testResult);

			}
			packColumns(getTestHistoryPart().getTableViewer().getTable());
			getTestHistoryPart().setVisible();
		}

	}

	/**
	 * dispose all {@Link Button} and clears the buttonArray.
	 */
	private void clearButtonArray() {
		for (Button button : buttonArray) {
			button.dispose();
		}
		buttonArray.clear();

	}

	/**
	 * Add an entry in history table.
	 * 
	 * @param testResult
	 *            {@link TestResult}
	 */
	private void addHistoryToTable(final TestResult testResult) {

		TableViewer tableViewer = getTestHistoryPart().getTableViewer();

		final TestProjectConfig testProjectConfig = testStructure.getRootElement().getTestProjectConfig();

		TableItem item = new TableItem(tableViewer.getTable(), SWT.NONE);
		item.setText(getResultSummaryRowFrom(testResult));

		if (testResult.isSuccessfully()) {
			item.setImage(0, IconConstants.ICON_TESTCASE_SUCCESSED);
		} else {
			item.setImage(0, IconConstants.ICON_TESTCASE_FAILED);
		}

		Button button = new Button(tableViewer.getTable(), SWT.NONE);

		button.setText("...");
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				EPartService partService = context.get(EPartService.class);
				MPart part = partService.showPart("org.testeditor.ui.testresultexecutionpart", PartState.ACTIVATE);
				TestExecutionResultViewPart trPart = (TestExecutionResultViewPart) part.getObject();
				trPart.setTestResultURL("http://localhost:" + testProjectConfig.getPort() + "/"
						+ testResult.getResultLink());

			}
		});

		buttonArray.add(button);

		TableEditor editor = new TableEditor(item.getParent());
		editor.grabVertical = true;
		editor.horizontalAlignment = SWT.LEFT;
		editor.minimumWidth = 30;
		editor.setEditor(button, item, 3);
		editor.layout();
	}

	/**
	 * Extracts Summary String from TestResult to be used in the table.
	 * 
	 * @param testResult
	 *            as data for the extraction.
	 * @return string array used in the table.
	 */
	public String[] getResultSummaryRowFrom(TestResult testResult) {
		String formatedDateString = format(testResult.getResultDate());
		String error = translationService.translate("%error");
		String[] row = new String[] {
				"",
				formatedDateString,
				"Ok: " + testResult.getRight() + ";\t " + error + ": "
						+ (testResult.getWrong() + testResult.getException()), "" };
		return row;
	}

	/**
	 * formats the date to a string with county format.
	 * 
	 * @param date
	 *            Date
	 * @return a string representing the date
	 */
	private String format(Date date) {
		String dateFormat = translationService.translate("%DateFormatString");
		DateFormat df = new SimpleDateFormat(dateFormat);
		return df.format(date);
	}

	/**
	 * expands the columns, so that every entry is visible.
	 * 
	 * @param table
	 *            the Table
	 */
	private void packColumns(Table table) {

		for (int index = 0; index < table.getColumnCount() - 1/*
															 * only for visible
															 * columns
															 */; index++) {
			table.getColumn(index).pack();
		}
		if (!table.isVisible()) {
			table.setVisible(true);
		}
	}

	/**
	 * Creates the ui for history part.
	 * 
	 * @param parent
	 *            composite
	 */
	@PostConstruct
	public void createControls(Composite parent) {
		this.testHistoryView = ContextInjectionFactory.make(TestHistoryView.class, context);
	}

	/**
	 * 
	 * @return the testHistoryPart
	 */
	private TestHistoryView getTestHistoryPart() {
		return testHistoryView;
	}

	/**
	 * 
	 * @return the value of the {@link TestStructure}.
	 */
	public TestStructure getTestStructure() {
		return testStructure;
	}

	/**
	 * clears the testHistory.
	 * 
	 */
	public void clearHistory() {
		try {
			TestStructureService testStructureService = getTestStructureService(testStructure);
			testStructureService.clearHistory(testStructure);
		} catch (SystemException e) {
			LOGGER.error(e.getMessage());
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
	 * 
	 * @param aTestStructure
	 *            used to identify the correct service.
	 * 
	 * @return the TestStructureService used for teststructure.
	 */
	private TestStructureService getTestStructureService(TestStructure aTestStructure) {
		return testEditorPlugInService.getTestStructureServiceFor(aTestStructure.getRootElement()
				.getTestProjectConfig().getTestServerID());
	}

	/**
	 * Checks that the view has a test structure which has a test history.
	 * 
	 * @return true, if the testStructure is not null
	 */
	public boolean containsTestHistory() {
		if (testStructure != null && testHistory != null) {
			return testHistory.size() > 0;
		}
		return false;
	}

	/**
	 * clears the view.
	 */
	public void clearView() {
		testHistoryView.clearHistory();
	}

}
