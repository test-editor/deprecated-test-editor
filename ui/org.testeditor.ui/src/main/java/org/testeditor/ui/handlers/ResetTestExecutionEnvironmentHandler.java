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

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.services.interfaces.TestExecutionEnvironmentService;
import org.testeditor.ui.constants.TestEditorConstants;

/**
 * Handler to Rest a Test execution environment.
 */
public class ResetTestExecutionEnvironmentHandler {

	private static final Logger LOGGER = Logger.getLogger(ResetTestExecutionEnvironmentHandler.class);

	/**
	 * Check if the current selection belongs to project that has a lanched test
	 * execution environment.
	 * 
	 * @param context
	 *            to get the selection.
	 * @param testExecService
	 *            used to get the information.
	 * @return true if their is an environment launched for the active
	 *         selection.
	 */
	@CanExecute
	public boolean canExecute(IEclipseContext context, TestExecutionEnvironmentService testExecService) {
		IStructuredSelection selection = (IStructuredSelection) context
				.get(TestEditorConstants.SELECTED_TEST_COMPONENTS);
		return ContextInjectionFactory.make(CanExecuteTestExplorerHandlerRules.class, context)
				.canExecuteOnTestStructureWithLaunchedTestExecutionEnvironment(selection);
	}

	/**
	 * Executes the tear down of a test environment. It will stop it and reset
	 * it to initial state.
	 * 
	 * @param context
	 *            eclipse context to retrieve the current selection in the
	 *            testexplorer.
	 * @param testExecService
	 *            used to work with.
	 */
	@Execute
	public void execute(IEclipseContext context, TestExecutionEnvironmentService testExecService) {
		IStructuredSelection selection = (IStructuredSelection) context
				.get(TestEditorConstants.SELECTED_TEST_COMPONENTS);
		if (selection.getFirstElement() instanceof TestStructure) {
			TestStructure ts = (TestStructure) selection.getFirstElement();
			try {
				testExecService.resetEnvironment(ts.getRootElement(), new NullProgressMonitor());
			} catch (IOException | InterruptedException e) {
				LOGGER.error("Error reset Execution Environment.", e);
			}
		}
	}

}
