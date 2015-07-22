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
package org.testeditor.ui.parts.testExplorer.handler;

import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.testeditor.core.constants.TestEditorCoreEventConstants;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.model.teststructure.TestCase;
import org.testeditor.core.model.teststructure.TestCompositeStructure;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.model.teststructure.TestSuite;
import org.testeditor.core.services.interfaces.TestStructureService;
import org.testeditor.core.util.TestStateProtocolService;
import org.testeditor.ui.constants.TestEditorConstants;
import org.testeditor.ui.handlers.CanExecuteTestExplorerHandlerRules;
import org.testeditor.ui.handlers.DeleteTestHandler;
import org.testeditor.ui.utilities.TestEditorTranslationService;
import static java.lang.System.lineSeparator;

/**
 * purges the test-history of the selected element.
 * 
 * 
 */
public class PurgeTestHistoryHandler {

	@Inject
	private TestStructureService testStructureService;
	@Inject
	private TestStateProtocolService testProtocolService;

	private static final Logger LOGGER = Logger.getLogger(DeleteTestHandler.class);

	/**
	 * Check if this handler is enabled. It is only enabled on
	 * <code>TestStructures</code> which contain tests or suites . It is only
	 * enabled on One Element. Enable is possible with an open Editor View or a
	 * selection in the TestExplorer.
	 * 
	 * @param context
	 *            of the eclipse to retrieve the current selection in the
	 *            testexplorer.
	 * 
	 * @return true if only one <code>TestStructure</code> is selected.
	 */
	@CanExecute
	public boolean canExecute(IEclipseContext context) {
		IStructuredSelection selection = (IStructuredSelection) context
				.get(TestEditorConstants.SELECTED_TEST_COMPONENTS);
		if (selection.size() == 1 && selection.getFirstElement() instanceof TestProject
				&& ((TestProject) selection.getFirstElement()).getTestProjectConfig() == null) {
			return false;
		}
		CanExecuteTestExplorerHandlerRules canExecuteTestExplorerHandlerRules = new CanExecuteTestExplorerHandlerRules();
		return !canExecuteTestExplorerHandlerRules.canExecuteOnTestScenarienSuiteRule(selection)
				&& !canExecuteTestExplorerHandlerRules.canExecuteOnTestScenarioRule(selection)
				&& (selection.getFirstElement() instanceof TestCase || selection.getFirstElement() instanceof TestSuite || selection
						.getFirstElement() instanceof TestProject);
	}

	/**
	 * execute the purge of the test history.
	 * 
	 * @param context
	 *            IEclipseContext
	 * @param translationService
	 *            TestEditorTranslationService
	 */
	@Execute
	public void execute(IEclipseContext context, TestEditorTranslationService translationService) {
		IStructuredSelection selection = (IStructuredSelection) context
				.get(TestEditorConstants.SELECTED_TEST_COMPONENTS);
		boolean userConfirms = checkUserConfirmation(translationService, selection);
		if (userConfirms) {
			try {
				Iterator<?> iterator = selection.iterator();
				while (iterator.hasNext()) {
					Object next = iterator.next();
					if (next instanceof TestStructure) {
						TestStructure ts = (TestStructure) next;
						if (ts instanceof TestSuite || ts instanceof TestProject) {
							List<TestStructure> allTestChildren = ((TestCompositeStructure) ts).getAllTestChildren();
							purgeTestHistories(allTestChildren);
						} else {
							purgeTestHistoryFromTestStructure(ts);
						}
					}
				}
			} catch (SystemException e) {
				LOGGER.error(e.getMessage(), e);
				MessageDialog.openError(Display.getCurrent().getActiveShell(), "System-Exception",
						e.getLocalizedMessage());
			}
			TestProject testProject = ((TestStructure) selection.getFirstElement()).getRootElement();
			context.get(IEventBroker.class).send(TestEditorCoreEventConstants.TESTSTRUCTURE_MODEL_CHANGED_RELOADED,
					testProject.getFullName());
		}
	}

	/**
	 * purges the TestHistory of the list of TestStructures.
	 * 
	 * @param allTestChildren
	 *            List<TestStructure>
	 * @throws SystemException
	 *             while operation
	 */
	private void purgeTestHistories(List<TestStructure> allTestChildren) throws SystemException {
		for (TestStructure ts : allTestChildren) {
			if (ts instanceof TestSuite || ts instanceof TestCase) {
				purgeTestHistoryFromTestStructure(ts);
			}
		}
	}

	/**
	 * purges the TestHistory of the TestStructure.
	 * 
	 * @param ts
	 *            TestStructure
	 * @throws SystemException
	 *             while operation
	 */
	private void purgeTestHistoryFromTestStructure(TestStructure ts) throws SystemException {
		testProtocolService.remove(ts);
		testStructureService.clearTestHistory(ts);
	}

	/**
	 * 
	 * @param translationService
	 *            for Internationalization
	 * @param selection
	 *            selection of Teststructures to delete
	 * @return true if user confirms delete or no UI Thread is running.
	 */
	private boolean checkUserConfirmation(TestEditorTranslationService translationService,
			IStructuredSelection selection) {
		if (Display.getCurrent() != null) {
			boolean runningInUIThread = Display.getCurrent().getActiveShell() != null;
			if (runningInUIThread) {
				return MessageDialog.openConfirm(
						Display.getCurrent().getActiveShell(),
						translationService.translate("%popupmenu.label.purgeHistory1"),
						translationService.translate("%popupmenu.label.purgeHistory1")
								+ " "
								+ getCommaListOfTestStructuresNames(selection.iterator(), translationService, 1, true)
										.toString() + " "
								+ translationService.translate("%popupmenu.label.purgeHistory2"));
			}
		}
		return true;
	}

	/**
	 * Gets a comma separated string with the names of a list of
	 * <code>TestStructure</code> objects.
	 * 
	 * @param iterator
	 *            of the TestStructure list.
	 * @param translationService
	 *            service to translate testchild elements.
	 * @param childrenDepth
	 *            how many recursive levels of children should be scanned.
	 * @param firstLayer
	 *            should be true in the firstlayer
	 * @return comma separated String containing the <code>TestStructure</code>
	 *         names.
	 */
	protected StringBuilder getCommaListOfTestStructuresNames(Iterator<?> iterator,
			TestEditorTranslationService translationService, int childrenDepth, boolean firstLayer) {

		StringBuilder sb = new StringBuilder();
		boolean first = true;
		while (iterator.hasNext()) {
			Object next = iterator.next();
			if (next instanceof TestStructure) {
				TestStructure testStructure = (TestStructure) next;
				if (first) {
					first = false;
				} else {
					sb.append(", ");
				}
				if (testStructure instanceof TestSuite) {
					if (!first) {
						sb.append(lineSeparator());
					}
					sb.append(testStructure.getName());
					TestSuite suite = (TestSuite) testStructure;
					if (childrenDepth > 0 && !suite.getTestChildren().isEmpty()) {
						sb.append(lineSeparator()).append("  ");
						sb.append(translationService.translate("%popupmenu.label.purgeHistory.itemChildren"));
						sb.append(" ");
						sb.append(getCommaListOfTestStructuresNames(suite.getTestChildren().iterator(), translationService,
								childrenDepth--, false));
					}
				} else if (!(testStructure instanceof TestSuite)) {
					sb.append(testStructure.getName());
				}
			}
			
		}
		return sb;
	}

}
