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
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.testeditor.core.constants.TestEditorCoreEventConstants;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.model.testresult.TestResult;
import org.testeditor.core.model.teststructure.TestProjectConfig;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.services.interfaces.TestStructureService;
import org.testeditor.ui.constants.TestEditorUIEventConstants;

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
	private TestStructureService testStructureService;

	private TestHistoryView testHistoryView;

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
			if (aTestStructure.isExecutableTestStructure() && testStructure != aTestStructure) {
				refreshHistoryTable(aTestStructure);
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
		if (testStructure.isExecutableTestStructure()) {
			testHistoryView.setTitle(testStructure.getName());
			this.testStructure = testStructure;

			testHistoryView.clearTable();

			testHistory = null;
			try {
				testHistory = testStructureService.getTestHistory(testStructure);
			} catch (SystemException e) {
				LOGGER.error(e);
			}

			testHistoryView.setTestHistory(testHistory);
			final TestProjectConfig testProjectConfig = testStructure.getRootElement().getTestProjectConfig();

			testHistoryView.getTableViewer().addDoubleClickListener(new IDoubleClickListener() {

				@Override
				public void doubleClick(DoubleClickEvent event) {
					TestResult testResult = (TestResult) ((IStructuredSelection) event.getViewer().getSelection())
							.getFirstElement();
					EPartService partService = context.get(EPartService.class);
					MPart part = partService.showPart("org.testeditor.ui.testresultexecutionpart", PartState.ACTIVATE);
					TestExecutionResultViewPart trPart = (TestExecutionResultViewPart) part.getObject();
					trPart.setTestResultURL(
							"http://localhost:" + testProjectConfig.getPort() + "/" + testResult.getResultLink());
				}
			});

			testHistoryView.setVisible();
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
			testStructureService.clearTestHistory(testStructure);
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
