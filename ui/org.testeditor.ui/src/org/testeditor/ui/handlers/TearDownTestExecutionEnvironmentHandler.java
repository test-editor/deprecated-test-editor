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
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.services.interfaces.TestExceutionEnvironmentService;
import org.testeditor.ui.constants.TestEditorConstants;

/**
 * Handler to shutdown a test execution environment. Without resetting the
 * state.
 *
 */
public class TearDownTestExecutionEnvironmentHandler {

	private static final Logger LOGGER = Logger.getLogger(ResetTestExecutionEnvironmentHandler.class);

	/**
	 * Check if the current selection belongs to project that has a launched
	 * test execution environment.
	 * 
	 * @param context
	 *            to get the selection.
	 * @param testExecService
	 *            used to get the information.
	 * @return true if their is an environment launched for the active
	 *         selection.
	 */
	@CanExecute
	public boolean canExecute(IEclipseContext context, TestExceutionEnvironmentService testExecService) {
		IStructuredSelection selection = (IStructuredSelection) context
				.get(TestEditorConstants.SELECTED_TEST_COMPONENTS);
		return ContextInjectionFactory.make(CanExecuteTestExplorerHandlerRules.class, context)
				.canExecuteOnTestStructureWithLaunchedTestExecutionEnvironment(selection);
	}

	/**
	 * Executes the tear down of a test environment. It will only stop it,
	 * nothing is reseted.
	 * 
	 * @param context
	 *            eclipse context to retrieve the current selection in the
	 *            testexplorer.
	 * @param testExecService
	 *            used to work with.
	 */
	@Execute
	public void execute(IEclipseContext context, TestExceutionEnvironmentService testExecService) {
		IStructuredSelection selection = (IStructuredSelection) context
				.get(TestEditorConstants.SELECTED_TEST_COMPONENTS);
		if (selection.getFirstElement() instanceof TestStructure) {
			TestStructure ts = (TestStructure) selection.getFirstElement();
			try {
				NullProgressMonitor monitor = new NullProgressMonitor();
				TestProject testProject = ts.getRootElement();
				testExecService.tearDownEnvironment(testProject, monitor);
			} catch (IOException | InterruptedException e) {
				LOGGER.error("Error tearDown Execution Environment.", e);
			}
		}
	}

}
