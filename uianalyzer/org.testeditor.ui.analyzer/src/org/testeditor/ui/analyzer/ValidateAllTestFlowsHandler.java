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
package org.testeditor.ui.analyzer;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;
import org.testeditor.core.model.teststructure.TestCompositeStructure;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.ui.constants.TestEditorConstants;
import org.testeditor.ui.parts.testExplorer.TestExplorer;

/**
 * 
 * Handler to execute the validation operation.
 * 
 */
public class ValidateAllTestFlowsHandler {

	private static final Logger LOGGER = Logger.getLogger(ValidateAllTestFlowsHandler.class);
	private static final String BUNDLE_URI = "platform:/plugin/org.testeditor.ui.analyzer";

	@Inject
	private TranslationService translate;

	/**
	 * Runs a Validation operation on the selected TestComposite.
	 * 
	 * @param context
	 *            of the Application to get the TestExplorer.
	 * @param shell
	 *            to open ProgressDialog.
	 */
	@Execute
	public void execute(final IEclipseContext context, @Named(IServiceConstants.ACTIVE_SHELL) final Shell shell) {
		TestExplorer explorer = (TestExplorer) context.get(TestEditorConstants.TEST_EXPLORER_VIEW);
		TestCompositeStructure testComp = (TestCompositeStructure) explorer.getSelection().getFirstElement();
		List<TestStructure> allTestChildren = getTestStructuresToWorkOn(testComp, shell);
		ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);
		try {
			ValidadateTestFlowRunnable validadateTestFlowRunnable = ContextInjectionFactory.make(
					ValidadateTestFlowRunnable.class, context);
			validadateTestFlowRunnable.setTestFlowsToBeValidated(allTestChildren);
			dialog.run(true, true, validadateTestFlowRunnable);
			String id = "org.testeditor.ui.analyzer.validateResults";
			EPartService partService = context.get(EPartService.class);
			MPart part = partService.findPart(id);
			if (part == null) {
				part = partService.createPart(id);
			}
			partService.showPart(part, PartState.ACTIVATE);
			((ValidateResultsView) part.getObject()).setErrorContainers(
					validadateTestFlowRunnable.getValidationResult(), testComp);
		} catch (InvocationTargetException | InterruptedException e1) {
			LOGGER.error("Validation aborted.", e1);
		}
	}

	/**
	 * Executes the getAllTestChildrenWithScenarios on the TestComposite with a
	 * ProgressDialog to show the UI is running.
	 * 
	 * @param testComp
	 *            to query all TestCases.
	 * @param shell
	 *            to show Progress Dialog.
	 * @return List with all TestStructures and TestScenarios of the testComp.
	 */
	protected List<TestStructure> getTestStructuresToWorkOn(final TestCompositeStructure testComp, Shell shell) {
		ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);
		final List<TestStructure> result = new ArrayList<TestStructure>();
		try {
			dialog.run(true, false, new IRunnableWithProgress() {

				@Override
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					monitor.setTaskName(translate.translate("%analyzer.load.teststructures", BUNDLE_URI));
					result.addAll(testComp.getAllTestChildrenWithScenarios());
				}
			});
		} catch (InvocationTargetException | InterruptedException e1) {
			LOGGER.error("Validation aborted.", e1);
		}

		return result;
	}

	/**
	 * This Action is available on TestProjects or TestSuites.
	 * 
	 * @param context
	 *            context of the application.
	 * @return true if a testproject or testsuite is selected.
	 */
	@CanExecute
	public boolean canExecute(IEclipseContext context) {
		TestExplorer explorer = (TestExplorer) context.get(TestEditorConstants.TEST_EXPLORER_VIEW);
		if (explorer.getSelection().isEmpty()) {
			return false;
		}
		Object element = explorer.getSelection().getFirstElement();
		return element instanceof TestCompositeStructure;
	}

}
