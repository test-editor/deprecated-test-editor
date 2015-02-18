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

import javax.inject.Named;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.widgets.Shell;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.model.teststructure.TestType;
import org.testeditor.core.services.interfaces.TestStructureContentService;
import org.testeditor.ui.constants.TestEditorConstants;
import org.testeditor.ui.dialogs.InfoDialog;
import org.testeditor.ui.parts.testExplorer.TestExplorer;
import org.testeditor.ui.utilities.TestEditorTranslationService;

/**
 * Displays the source code of the test structure (e.g. testcase, suite) in a
 * dialog.
 */
public class OpenSourceCodeHandler {

	/**
	 * Executes the Handler to show the selected test structure in a dialog.
	 * 
	 * @param shell
	 *            The active shell
	 * @param partService
	 *            of the active window.
	 * @param testStructureService
	 *            used to read the Sourcecode of a teststructure.
	 * @param translation
	 *            to translate error messages
	 * 
	 */
	@Execute
	public void execute(@Named(IServiceConstants.ACTIVE_SHELL) Shell shell, EPartService partService,
			TestStructureContentService testStructureContentService, TestEditorTranslationService translation) {
		TestExplorer testExplorer = (TestExplorer) partService.findPart(TestEditorConstants.TEST_EXPLORER_VIEW)
				.getObject();

		// Receive selected tree path via TreeViewer object
		TreePath[] treePaths = ((TreeSelection) testExplorer.getTreeViewer().getSelection()).getPaths();
		if (treePaths.length == 0) {
			return; // Handler depends on a selected path
		}

		// Receive selected tree element of selected path
		TestStructure selected = (TestStructure) treePaths[0].getLastSegment();

		try {
			String testStruktureAsText = testStructureContentService.getTestStructureAsSourceText(selected);

			InfoDialog dialog = new InfoDialog(shell);
			dialog.create();
			dialog.setTitle(translation.translate("%sourcecode.dialog.title"));

			if (selected.getTypeName().equals(TestType.TEST.getName())) {
				dialog.setMessage(translation.translate("%sourcecode.dialog.message.testcase"),
						IMessageProvider.INFORMATION);
			} else if (selected.getTypeName().equals(TestType.SUITE.getName())) {
				dialog.setMessage(translation.translate("%sourcecode.dialog.message.suite"),
						IMessageProvider.INFORMATION);
			}

			dialog.setMessage(testStruktureAsText);
			dialog.open();

		} catch (SystemException e) {
			MessageDialog.openError(shell, "System-Exception", e.getLocalizedMessage());
		}

	}

	/**
	 * Check if this Handler is enabled on the selection. Only one Teststrucutre
	 * is valid as a selection.
	 * 
	 * @param context
	 *            to get the TestCaseExplorer
	 * @return true if only one element is selected.
	 */
	@CanExecute
	public boolean canExecute(IEclipseContext context) {
		TestExplorer testExplorer = (TestExplorer) context.get(TestEditorConstants.TEST_EXPLORER_VIEW);
		CanExecuteTestExplorerHandlerRules rules = new CanExecuteTestExplorerHandlerRules();
		return rules.canExecuteOnlyOneElementRule(testExplorer) && rules.canExecuteOnNoneRootRule(testExplorer);
	}

}
