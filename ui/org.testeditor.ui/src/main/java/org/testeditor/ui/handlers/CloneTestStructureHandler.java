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

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.exceptions.TestCycleDetectException;
import org.testeditor.core.model.teststructure.TestCase;
import org.testeditor.core.model.teststructure.TestFlow;
import org.testeditor.core.model.teststructure.TestScenario;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.services.interfaces.TestStructureContentService;
import org.testeditor.ui.constants.TestEditorConstants;
import org.testeditor.ui.utilities.TestEditorTranslationService;

/**
 * Handler to clone the current selection in the TestExplorer, to create a new
 * one.
 *
 */
public class CloneTestStructureHandler {

	private static final Logger LOGGER = Logger.getLogger(CloneTestStructureHandler.class);

	@Inject
	private TestStructureContentService testStructureContentService;

	@Inject
	private TestEditorTranslationService translationService;

	/**
	 * Executes the clone TestStructure operation.
	 * 
	 * @param context
	 *            to lookup the TestExplorer and execute new handler.
	 * @return cloned TestStructure or null if no one is created.
	 */
	@Execute
	public TestStructure execute(IEclipseContext context) {
		TestStructure clonedTs = null;
		IStructuredSelection selection = (IStructuredSelection) context
				.get(TestEditorConstants.SELECTED_TEST_COMPONENTS);
		TestFlow lastSelection = (TestFlow) selection.getFirstElement();
		NewTestStructureHandler newHandler = createNewTestStructureHandler(lastSelection, context);
		Object result = ContextInjectionFactory.invoke(newHandler, Execute.class, context);
		if (result != null) {
			try {
				TestFlow testFlow = (TestFlow) result;
				testStructureContentService.refreshTestCaseComponents(lastSelection);
				testFlow.setTestComponents(lastSelection.getTestComponents());
				testStructureContentService.saveTestStructureData(testFlow);
				clonedTs = testFlow;
			} catch (SystemException e) {
				LOGGER.error("saving ", e);
				MessageDialog.openError(Display.getCurrent().getActiveShell(), translationService.translate("%error"),
						e.getLocalizedMessage());
			} catch (TestCycleDetectException e) {
				LOGGER.error("cycle in: " + lastSelection.getFullName(), e);
			}
		}
		return clonedTs;
	}

	/**
	 * Builder method to create the matching new handler.
	 * 
	 * @param context
	 *            EclipseContext to create the new handler.
	 * @param lastSelection
	 *            TestStructure to be cloned.
	 * @return new handler matching the teststructure.
	 */
	protected NewTestStructureHandler createNewTestStructureHandler(TestFlow lastSelection, IEclipseContext context) {
		if (lastSelection instanceof TestCase) {
			return ContextInjectionFactory.make(NewCaseHandler.class, context);
		}
		if (lastSelection instanceof TestScenario) {
			return ContextInjectionFactory.make(NewScenarioHandler.class, context);
		}
		return null;
	}

	/**
	 * Checks if the clone operation is possible on the selected element of the
	 * TestExplorer.
	 * 
	 * @param context
	 *            to lookup the TestExplorer.
	 * 
	 * @return true on TestCase and TestSceanrio, in all other cases it returns
	 *         false.
	 */
	@CanExecute
	public boolean canExecute(IEclipseContext context) {
		IStructuredSelection selection = (IStructuredSelection) context
				.get(TestEditorConstants.SELECTED_TEST_COMPONENTS);
		CanExecuteTestExplorerHandlerRules canExecuteTestExplorerHandlerRules = new CanExecuteTestExplorerHandlerRules();
		return canExecuteTestExplorerHandlerRules.canExecuteOnlyOneElementRule(selection)
				&& canExecuteTestExplorerHandlerRules.canExecuteOnTestFlowRule(selection);
	}

}