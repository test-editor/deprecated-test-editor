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
package org.testeditor.ui.handlers.rename;

import javax.inject.Named;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.swt.widgets.Shell;
import org.testeditor.core.model.teststructure.ScenarioSuite;
import org.testeditor.core.model.teststructure.TestCase;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestScenario;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.model.teststructure.TestSuite;
import org.testeditor.ui.constants.TestEditorConstants;
import org.testeditor.ui.parts.testExplorer.TestExplorer;
import org.testeditor.ui.utilities.TestEditorTranslationService;

/**
 * RenameTestHandlerFacade is called for renaming a teststructure. This Facade
 * delegates the work for a teststructure to the suitable RenameXYZHandler.
 * 
 */
public class RenameTestHandlerFacade {

	private AbstractRenameHandler renameHandler;

	/**
	 * execute method.
	 * 
	 * @param context
	 *            the {@link IEclipseContext}
	 * 
	 * @param translationService
	 *            the {@link TestEditorTranslationService}
	 * 
	 * @param shell
	 *            the active shell
	 */
	@Execute
	public void execute(IEclipseContext context, TestEditorTranslationService translationService,
			@Named(IServiceConstants.ACTIVE_SHELL) Shell shell) {

		if (renameHandler != null) {
			renameHandler.execute(context, translationService, shell);
		}

	}

	/**
	 * returns the specialized rename-handler for the selected
	 * {@link TestStructure}.
	 * 
	 * @param context
	 *            {@link IEclipseContext}
	 * @param selected
	 *            {@link TestStructure}
	 * @return {@link AbstractRenameHandler}
	 */
	private AbstractRenameHandler getAbstactRenameTestStructureHandler(IEclipseContext context, TestStructure selected) {
		if (selected instanceof TestCase) {
			return ContextInjectionFactory.make(RenameTestCaseHandler.class, context);
		} else if (selected instanceof TestSuite) {
			return ContextInjectionFactory.make(RenameTestSuiteHandler.class, context);
		} else if (selected instanceof TestScenario) {
			return ContextInjectionFactory.make(RenameTestScenarioHandler.class, context);
		} else if (selected instanceof TestProject) {
			return ContextInjectionFactory.make(RenameTestProjectHandler.class, context);
		} else if (selected instanceof ScenarioSuite) {
			return ContextInjectionFactory.make(RenameScenarioSuiteHandler.class, context);
		}
		return null;
	}

	/**
	 * Check if this Handler is enabled on the selection. Only one Teststrucutre
	 * is valid as a selection.
	 * 
	 * @param context
	 *            Eclipse Context to retrive the Viewer
	 * @return true if only one element is selected.
	 */
	@CanExecute
	public boolean canExecute(IEclipseContext context) {

		TestExplorer explorer = (TestExplorer) context.get(TestEditorConstants.TEST_EXPLORER_VIEW);
		TestStructure selected = (TestStructure) explorer.getSelection().getFirstElement();

		renameHandler = getAbstactRenameTestStructureHandler(context, selected);
		if (renameHandler != null) {
			return renameHandler.canExecute(context);
		}
		return false;
	}

}
