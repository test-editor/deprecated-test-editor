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

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.model.teststructure.ScenarioSuite;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.model.teststructure.TestSuite;
import org.testeditor.core.services.interfaces.TestProjectService;
import org.testeditor.core.services.interfaces.TestScenarioService;
import org.testeditor.core.services.interfaces.TestStructureService;
import org.testeditor.ui.constants.TestEditorConstants;
import org.testeditor.ui.parts.testExplorer.TestExplorer;
import org.testeditor.ui.utilities.TestEditorTranslationService;

/**
 * DeleteTestHandler Handler is called for deleting a teststructure.
 * 
 */
public class DeleteTestHandler {

	private static final Logger LOGGER = Logger.getLogger(DeleteTestHandler.class);

	/**
	 * Executes the deleteTestcase action.
	 * 
	 * @param context
	 *            to retrieve the TestExplorer instance.
	 * @param translationService
	 *            for internationalization of the Dialogs.
	 * @param testProjectService
	 *            {@link TestProjectService}
	 * @param testScenarioService
	 *            {@link TestScenarioService}
	 * @param testStructureService
	 *            {@link TestStructureService}
	 */
	@Execute
	public void execute(final IEclipseContext context, final TestEditorTranslationService translationService,
			final TestProjectService testProjectService, TestScenarioService testScenarioService,
			final TestStructureService testStructureService) {
		final TestExplorer explorer = (TestExplorer) context.get(TestEditorConstants.TEST_EXPLORER_VIEW);
		final IStructuredSelection selection = explorer.getSelection();
		boolean userConfirms = checkUserConfirmation(translationService, selection, testScenarioService);
		final TestStructure newSelection = ((TestStructure) selection.getFirstElement()).getParent();
		if (userConfirms) {
			ProgressMonitorDialog dialog = new ProgressMonitorDialog(Display.getCurrent().getActiveShell());
			try {
				dialog.run(true, false, new IRunnableWithProgress() {
					@Override
					public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
						monitor.beginTask(translationService.translate("%TestExplorer.deleteItem"),
								IProgressMonitor.UNKNOWN);
						Iterator<TestStructure> selectedTSIterator = selection.iterator();
						Set<TestStructure> selectedTS = new HashSet<TestStructure>();
						while (selectedTSIterator.hasNext()) {
							selectedTS.add(selectedTSIterator.next());
						}
						Set<TestStructure> parentElementsFromSelection = extractParentElementsFromSelection(selectedTS);
						for (TestStructure testStructure : parentElementsFromSelection) {
							try {
								if (testStructure instanceof TestProject) {
									testProjectService.deleteProject((TestProject) testStructure);
								} else {
									testStructureService.delete(testStructure);
								}
							} catch (SystemException | IOException e) {
								LOGGER.error("Error deleting", e);
								Display.getDefault().asyncExec(new Runnable() {

									@Override
									public void run() {
										MessageDialog.openError(Display.getDefault().getActiveShell(),
												translationService.translate("%error"), e.getLocalizedMessage());
									}
								});
							}
						}
					}
				});
			} catch (InvocationTargetException e1) {
				LOGGER.trace("deleting items", e1);
				MessageDialog.openError(Display.getCurrent().getActiveShell(), translationService.translate("%error"),
						e1.getLocalizedMessage());
			} catch (InterruptedException e1) {
				LOGGER.trace("deleting items", e1);
				MessageDialog.openError(Display.getCurrent().getActiveShell(), translationService.translate("%error"),
						e1.getLocalizedMessage());
			}
			explorer.setSelectionOn(newSelection);
		}
	}

	/**
	 * Extracts the minimum set of teststructure to be deleted. If a parent is
	 * selected, it is not use full
	 * 
	 * @param selectedTestStructures
	 *            selected elements in the tree.
	 * @return minumum set of selected test structures in the tree.
	 */
	protected Set<TestStructure> extractParentElementsFromSelection(Set<TestStructure> selectedTestStructures) {
		Set<TestStructure> result = new HashSet<TestStructure>();
		for (TestStructure testStructure : selectedTestStructures) {
			List<TestStructure> parents = testStructure.getAllParents();
			TestStructure parentProband = null;
			for (TestStructure parentTS : parents) {
				if (selectedTestStructures.contains(parentTS)) {
					parentProband = parentTS;
				}
			}
			if (parentProband != null) {
				result.add(parentProband);
			} else {
				result.add(testStructure);
			}
		}
		return result;
	}

	/**
	 * 
	 * @param translationService
	 *            for Internationalization
	 * @param selection
	 *            selection of Teststructures to delete
	 * @param testScenarioService
	 *            {@link TestScenarioService}
	 * @return true if user confirms delete or no UI Thread is running.
	 */
	private boolean checkUserConfirmation(TestEditorTranslationService translationService,
			IStructuredSelection selection, TestScenarioService testScenarioService) {
		if (Display.getCurrent() != null) {
			boolean runningInUIThread = Display.getCurrent().getActiveShell() != null;
			if (runningInUIThread) {
				return MessageDialog.openConfirm(
						Display.getCurrent().getActiveShell(),
						translationService.translate("%popupmenu.label.delete.item"),
						translationService.translate("%popupmenu.label.delete.item1")
								+ " "
								+ getCommaListOfTestStructuresNames(selection.iterator(), translationService, 1,
										testScenarioService).toString() + " "
								+ translationService.translate("%popupmenu.label.delete.item2"));
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
	 * @param testScenarioService
	 *            {@link TestScenarioService}
	 * @return comma separated String containing the <code>TestStructure</code>
	 *         names.
	 */
	protected StringBuilder getCommaListOfTestStructuresNames(Iterator<TestStructure> iterator,
			TestEditorTranslationService translationService, int childrenDepth, TestScenarioService testScenarioService) {

		StringBuilder sb = new StringBuilder();
		boolean first = true;
		while (iterator.hasNext()) {
			TestStructure testStructure = iterator.next();
			if (first) {
				first = false;
			} else {
				sb.append(", ");
			}
			if (!(testStructure instanceof TestSuite)) {
				sb.append(testStructure.getName());
			} else {
				if (!(testStructure instanceof ScenarioSuite && testScenarioService.isSuiteForScenarios(testStructure))) {
					if (!first) {
						sb.append(System.getProperty("line.separator"));
					}
					sb.append(testStructure.getName());
					TestSuite suite = (TestSuite) testStructure;
					if (childrenDepth > 0 && !suite.getTestChildren().isEmpty()) {
						sb.append(System.getProperty("line.separator")).append("  ");
						sb.append(translationService.translate("%popupmenu.label.delete.itemChildren"));
						sb.append(" ");
						sb.append(getCommaListOfTestStructuresNames(suite.getTestChildren().iterator(),
								translationService, childrenDepth--, testScenarioService));
					}
				}
			}

		}
		return sb;
	}

	/**
	 * @param context
	 *            to retrieve the TestExplorer
	 * @return true if one or more elements are selected in the TestCasetree.
	 */
	@CanExecute
	public boolean canExecute(IEclipseContext context) {
		TestExplorer explorer = (TestExplorer) context.get(TestEditorConstants.TEST_EXPLORER_VIEW);
		CanExecuteTestExplorerHandlerRules rules = ContextInjectionFactory.make(
				CanExecuteTestExplorerHandlerRules.class, context);
		return rules.canExecuteOnOneOrManyElementRule(explorer)
				&& !rules.canExecuteOnProjectMainScenarioSuite(explorer) && rules.canExecuteOnUnusedScenario(explorer)
				&& rules.canExecuteOnNonScenarioSuiteParents(explorer);
	}

}
