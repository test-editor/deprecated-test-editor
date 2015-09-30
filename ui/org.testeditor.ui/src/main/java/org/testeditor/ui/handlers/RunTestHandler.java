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
package org.testeditor.ui.handlers;

import java.lang.reflect.InvocationTargetException;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.contexts.Active;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.testeditor.core.constants.TestEditorCoreEventConstants;
import org.testeditor.core.model.testresult.TestResult;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.model.teststructure.TestSuite;
import org.testeditor.core.util.TestStateProtocolService;
import org.testeditor.ui.constants.TestEditorConstants;
import org.testeditor.ui.constants.TestEditorUIEventConstants;
import org.testeditor.ui.reporting.TestExecutionProgressDialog;
import org.testeditor.ui.utilities.TestEditorTranslationService;

/**
 * Run Tests on a <code>TestStructure</code> (<code>TestCase</code> or
 * <code>TestSuite</code>).
 * 
 */
public class RunTestHandler {

	private static final Logger LOGGER = Logger.getLogger(RunTestHandler.class);

	@Inject
	private static TestEditorTranslationService translationService;

	private TestResult testResult;

	/**
	 * Check if this handler is enabled. It is only enabled on
	 * <code>TestStructures</code> which contain tests. It is only enabled on
	 * One Element. Enable is possible with an open Editor View or a selection
	 * in the TestExplorer.
	 * 
	 * @param context
	 *            of the active window.
	 * 
	 * @return true if only one <code>TestStructure</code> is selected.
	 */
	@CanExecute
	public boolean canExecute(IEclipseContext context) {
		IStructuredSelection selection = (IStructuredSelection) context
				.get(TestEditorConstants.SELECTED_TEST_COMPONENTS);
		CanExecuteTestExplorerHandlerRules canExecuteTestExplorerHandlerRules = new CanExecuteTestExplorerHandlerRules();
		return canExecuteTestExplorerHandlerRules.canExecuteOnlyOneElementRule(selection)
				&& !canExecuteTestExplorerHandlerRules.canExecuteOnTestScenarioRule(selection)
				&& ((TestStructure) selection.getFirstElement()).isExecutableTestStructure();
	}

	/**
	 * @param shell
	 *            Active Shell. Runs the selected <code>TestStructure</code>.
	 * 
	 * @param protocolService
	 *            to store the current execution state.
	 * @param context
	 *            the active Eclipse Context
	 * @param eventBroker
	 *            the eventBroker
	 * @param partService
	 *            of to handle the dirty editor parts.
	 */
	@Execute
	public void execute(@Active Shell shell, TestStateProtocolService protocolService, IEclipseContext context,
			IEventBroker eventBroker, EPartService partService) {
		IStructuredSelection selection = (IStructuredSelection) context
				.get(TestEditorConstants.SELECTED_TEST_COMPONENTS);
		final TestStructure selectedTestStructure = (TestStructure) selection.getFirstElement();
		try {
			if (partService.saveAll(true)) {
				LOGGER.info("Running Test: " + selectedTestStructure);

				TestExecutionProgressDialog dlg = ContextInjectionFactory.make(TestExecutionProgressDialog.class,
						context);

				testResult = dlg.executeTest(selectedTestStructure);
				// refresh the icon depends on test result
				protocolService.set(selectedTestStructure, testResult);
				refreshTestStructureInTree(selectedTestStructure, eventBroker);

				// refresh refferredTestCaseViewer
				eventBroker.send(TestEditorUIEventConstants.TESTSTRUCTURE_EXECUTED, selectedTestStructure);
			}
		} catch (InvocationTargetException e) {
			MessageDialog.openError(Display.getCurrent().getActiveShell(), "System-Exception", e.getTargetException()
					.getLocalizedMessage());
		} catch (InterruptedException e) {
			MessageDialog.openInformation(Display.getCurrent().getActiveShell(),
					translationService.translate("%TestInterruptedByUserTitle"),
					translationService.translate("%TestInterruptedByUserMessage"));
		}
	}

	/**
	 * refreshes the item in the treeviewer of selected TestStructure and, if
	 * the selected TestStructure is a TestScenario, also the children.
	 * 
	 * @param testStructure
	 *            the TestStructure of the Item in the TreeViewer to refresh
	 * @param eventBroker
	 *            used to inform the clients about changed state of
	 *            teststructures.
	 */
	private void refreshTestStructureInTree(TestStructure testStructure, IEventBroker eventBroker) {
		if (testStructure instanceof TestSuite) {
			for (TestStructure ts : ((TestSuite) testStructure).getAllTestChildrensAndReferedTestcases()) {
				refreshTestStructureInTree(ts, eventBroker);
			}
		}
		eventBroker.send(TestEditorCoreEventConstants.TESTSTRUCTURE_STATE_UPDATED, testStructure.getFullName());
	}

}
