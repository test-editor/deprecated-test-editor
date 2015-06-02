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
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.testeditor.core.constants.TestEditorCoreEventConstants;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.exceptions.TestCycleDetectException;
import org.testeditor.core.model.teststructure.TestCase;
import org.testeditor.core.model.teststructure.TestFlow;
import org.testeditor.core.model.teststructure.TestScenario;
import org.testeditor.core.services.interfaces.TestEditorPlugInService;
import org.testeditor.core.services.interfaces.TestProjectService;
import org.testeditor.core.services.interfaces.TestStructureContentService;
import org.testeditor.ui.constants.TestEditorConstants;
import org.testeditor.ui.parts.testExplorer.TestExplorer;
import org.testeditor.ui.utilities.TestEditorTranslationService;

/**
 * Handler to clone the current selection in the TestExplorer, to create a new
 * one.
 *
 */
public class CloneTestStructureHandler {

	private static final Logger LOGGER = Logger.getLogger(CloneTestStructureHandler.class);

	private TestFlow lastSelection;

	@Inject
	private TestEditorPlugInService pluginService;

	@Inject
	private TestProjectService testProjectService;

	@Inject
	private TestEditorTranslationService translationService;

	/**
	 * Executes the clone TestStructure operation.
	 * 
	 * @param context
	 *            to lookup the TestExplorer and execute new handler.
	 */
	@Execute
	public void execute(IEclipseContext context) {
		TestExplorer explorer = (TestExplorer) context.get(TestEditorConstants.TEST_EXPLORER_VIEW);
		lastSelection = (TestFlow) explorer.getSelection().getFirstElement();
		if (lastSelection instanceof TestCase) {
			NewCaseHandler newCaseHandler = ContextInjectionFactory.make(NewCaseHandler.class, context);
			if (context.containsKey(IWorkbench.class)) {
				ContextInjectionFactory.invoke(newCaseHandler, Execute.class, context);
			}
		}
		if (lastSelection instanceof TestScenario) {
			NewScenarioHandler newSecHandler = ContextInjectionFactory.make(NewScenarioHandler.class, context);
			if (context.containsKey(IWorkbench.class)) {
				ContextInjectionFactory.invoke(newSecHandler, Execute.class, context);
			}
		}
	}

	/**
	 * Consumes the event:
	 * <code>TestEditorCoreEventConstants.TESTSTRUCTURE_MODEL_CHANGED_UPDATE_BY_ADD</code>
	 * and stores the content of the last selected TestStructure in the new one.
	 * 
	 * 
	 * @param testStructureFullName
	 *            of the new created one.
	 */
	@Inject
	@Optional
	public void updateNewTestStructureWithLastSelection(
			@UIEventTopic(TestEditorCoreEventConstants.TESTSTRUCTURE_MODEL_CHANGED_UPDATE_BY_ADD) String testStructureFullName) {
		try {
			TestFlow newTs = (TestFlow) testProjectService.findTestStructureByFullName(testStructureFullName);
			TestStructureContentService testStructureContentService = pluginService
					.getTestStructureContentServiceFor(newTs.getRootElement().getTestProjectConfig().getTestServerID());
			testStructureContentService.refreshTestCaseComponents(lastSelection);
			newTs.setTestComponents(lastSelection.getTestComponents());
			testStructureContentService.saveTestStructureData(newTs);
		} catch (SystemException e) {
			LOGGER.error("saving ", e);
			MessageDialog.openError(Display.getCurrent().getActiveShell(), translationService.translate("%error"),
					e.getLocalizedMessage());
		} catch (TestCycleDetectException e) {
			LOGGER.error("cycle in: " + lastSelection.getFullName(), e);
		}
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
		TestExplorer explorer = (TestExplorer) context.get(TestEditorConstants.TEST_EXPLORER_VIEW);
		CanExecuteTestExplorerHandlerRules canExecuteTestExplorerHandlerRules = new CanExecuteTestExplorerHandlerRules();
		return canExecuteTestExplorerHandlerRules.canExecuteOnlyOneElementRule(explorer)
				&& canExecuteTestExplorerHandlerRules.canExecuteOnTestFlowRule(explorer);
	}

}