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
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.model.teststructure.TestCase;
import org.testeditor.core.model.teststructure.TestCompositeStructure;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.model.teststructure.TestSuite;
import org.testeditor.core.services.interfaces.TestStructureService;
import org.testeditor.core.util.TestProtocolService;
import org.testeditor.ui.constants.TestEditorConstants;
import org.testeditor.ui.handlers.CanExecuteTestExplorerHandlerRules;
import org.testeditor.ui.handlers.DeleteTestHandler;
import org.testeditor.ui.parts.testExplorer.TestExplorer;
import org.testeditor.ui.utilities.TestEditorTranslationService;

/**
 * purges the test-history of the selected element.
 * 
 * 
 */
public class PurgeTestHistoryHandler {
	@Inject
	private EPartService partService;

	@Inject
	private TestStructureService testStructureService;
	@Inject
	private TestProtocolService testProtocolService;

	private static final Logger LOGGER = Logger.getLogger(DeleteTestHandler.class);

	/**
	 * Check if this handler is enabled. It is only enabled on
	 * <code>TestStructures</code> which contain tests or suites . It is only
	 * enabled on One Element. Enable is possible with an open Editor View or a
	 * selection in the TestExplorer.
	 * 
	 * @return true if only one <code>TestStructure</code> is selected.
	 */
	@CanExecute
	public boolean canExecute() {
		TestExplorer explorer = (TestExplorer) partService.findPart(TestEditorConstants.TEST_EXPLORER_VIEW).getObject();
		IStructuredSelection selection = explorer.getSelection();
		if (selection.size() == 1 && selection.getFirstElement() instanceof TestProject
				&& ((TestProject) selection.getFirstElement()).getTestProjectConfig() == null) {
			return false;
		}
		CanExecuteTestExplorerHandlerRules canExecuteTestExplorerHandlerRules = new CanExecuteTestExplorerHandlerRules();
		return !canExecuteTestExplorerHandlerRules.canExecuteOnTestScenarienSuiteRule(explorer)
				&& !canExecuteTestExplorerHandlerRules.canExecuteOnTestScenarioRule(explorer)
				&& (explorer.getSelection().getFirstElement() instanceof TestCase
						|| explorer.getSelection().getFirstElement() instanceof TestSuite || explorer.getSelection()
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
		TestExplorer explorer = (TestExplorer) context.get(TestEditorConstants.TEST_EXPLORER_VIEW);
		IStructuredSelection selection = explorer.getSelection();
		boolean userConfirms = checkUserConfirmation(translationService, selection);
		if (userConfirms) {
			try {
				Iterator<TestStructure> iterator = selection.iterator();
				while (iterator.hasNext()) {
					TestStructure ts = iterator.next();
					if (ts instanceof TestSuite || ts instanceof TestProject) {
						List<TestStructure> allTestChildren = ((TestCompositeStructure) ts).getAllTestChildren();
						purgeTestHistories(allTestChildren);
					} else {
						purgeTestHistoryFromTestStructure(ts);
					}
				}
			} catch (SystemException e) {
				LOGGER.error(e.getMessage(), e);
				MessageDialog.openError(Display.getCurrent().getActiveShell(), "System-Exception",
						e.getLocalizedMessage());
			}
			explorer.refreshTreeInput();
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
		testStructureService.clearHistory(ts);
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
	protected StringBuilder getCommaListOfTestStructuresNames(Iterator<TestStructure> iterator,
			TestEditorTranslationService translationService, int childrenDepth, boolean firstLayer) {

		StringBuilder sb = new StringBuilder();
		boolean first = true;
		while (iterator.hasNext()) {
			TestStructure testStructure = iterator.next();
			if (first) {
				first = false;
			} else {
				sb.append(", ");
			}
			if (testStructure instanceof TestSuite) {
				if (!first) {
					sb.append(System.getProperty("line.separator"));
				}
				sb.append(testStructure.getName());
				TestSuite suite = (TestSuite) testStructure;
				if (childrenDepth > 0 && !suite.getTestChildren().isEmpty()) {
					sb.append(System.getProperty("line.separator")).append("  ");
					sb.append(translationService.translate("%popupmenu.label.purgeHistory.itemChildren"));
					sb.append(" ");
					sb.append(getCommaListOfTestStructuresNames(suite.getTestChildren().iterator(), translationService,
							childrenDepth--, false));
				}
			} else if (!(testStructure instanceof TestSuite)) {
				sb.append(testStructure.getName());
			}
		}
		return sb;
	}

}
