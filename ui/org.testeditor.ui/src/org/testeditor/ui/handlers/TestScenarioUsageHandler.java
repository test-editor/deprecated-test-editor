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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Named;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.testeditor.core.model.teststructure.TestScenario;
import org.testeditor.core.services.interfaces.TestScenarioService;
import org.testeditor.ui.constants.TestEditorConstants;
import org.testeditor.ui.parts.testExplorer.TestExplorer;
import org.testeditor.ui.utilities.TestEditorTranslationService;

/**
 * 
 * this class shows the usage of the selected scenario.
 * 
 * @author llipinski
 */
public class TestScenarioUsageHandler {

	private List<TestScenarioUseage> scenariosUsedList;

	private TestEditorTranslationService translationService;
	private static final Logger LOGGER = Logger.getLogger(TestScenarioUsageHandler.class);

	/**
	 * special canExecute method for testScenario.
	 * 
	 * @param context
	 *            Eclipse Context
	 * @param ignoreCanExecute
	 *            ignore this Method in File menu
	 * 
	 * @return if the selection is the TestKomponenten-Suite
	 */
	@CanExecute
	public boolean canExecute(
			IEclipseContext context,
			@Named("org.testeditor.ui.newtestscenario.command.parameter.ignoreCanExecute") @Optional String ignoreCanExecute) {
		if (ignoreCanExecute != null && Boolean.parseBoolean(ignoreCanExecute)) {
			return true;
		}
		TestExplorer testExplorer = (TestExplorer) context.get(TestEditorConstants.TEST_EXPLORER_VIEW);
		CanExecuteTestExplorerHandlerRules handlerRules = new CanExecuteTestExplorerHandlerRules();
		return handlerRules.canExecuteOnTestScenarioRule(testExplorer);

		// return false;
	}

	/**
	 * open a information-dialog to show the usage of a scenario.
	 * 
	 * @param context
	 *            the IEclipseContext
	 * @param translationService
	 *            TranslationService
	 * @param scenarioService
	 *            TestScenarioService
	 */
	@Execute
	public void execute(IEclipseContext context, TestEditorTranslationService translationService,
			final TestScenarioService scenarioService) {

		this.translationService = translationService;
		// get at first the selected scenarios
		TestExplorer testExplorer = (TestExplorer) context.get(TestEditorConstants.TEST_EXPLORER_VIEW);
		// gets the usage.
		final Iterator<?> iterator = testExplorer.getSelection().iterator();
		scenariosUsedList = new ArrayList<TestScenarioUseage>();
		ProgressMonitorDialog dialog = new ProgressMonitorDialog(Display.getCurrent().getActiveShell());

		try {
			dialog.run(true, false, new IRunnableWithProgress() {

				@Override
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					monitor.beginTask(translate("%TestExplorer.searchingTheUsingOfTheScenario"),
							IProgressMonitor.UNKNOWN);
					Display.getDefault().asyncExec(new Runnable() {
						@Override
						public void run() {
							while (iterator.hasNext()) {
								TestScenario scenario = (TestScenario) iterator.next();
								List<String> usedOfTestSceneario = scenarioService.getUsedOfTestSceneario(scenario);
								// now show the usage.
								scenariosUsedList.add(new TestScenarioUseage(scenario.getName(), usedOfTestSceneario));
							}
						}
					});
				}
			});
		} catch (Exception e) {
			LOGGER.error("Error reading used of Scenario", e);
			MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error", e.getLocalizedMessage());
		}

		while (iterator.hasNext()) {
			TestScenario scenario = (TestScenario) iterator.next();
			List<String> usedOfTestSceneario = scenarioService.getUsedOfTestSceneario(scenario);
			// now show the usage.
			scenariosUsedList.add(new TestScenarioUseage(scenario.getName(), usedOfTestSceneario));
		}
		showUsageListToUser(scenariosUsedList);
	}

	/**
	 * 
	 * @param message
	 *            String
	 * @return the translated message
	 */
	protected String translate(String message) {
		return translationService.translate(message);
	}

	/**
	 * shows the list of the using of the selected scenarios.
	 * 
	 * @param scenariosUsedList
	 *            List<TestScenarioUseage>
	 */
	private void showUsageListToUser(List<TestScenarioUseage> scenariosUsedList) {
		if (Display.getCurrent() != null) {
			boolean runningInUIThread = Display.getCurrent().getActiveShell() != null;
			StringBuilder message = new StringBuilder();
			for (TestScenarioUseage useage : scenariosUsedList) {
				if (message.length() > 0) {
					message.append("\n");
				}
				if (useage.getScenarioUsedBy().isEmpty()) {
					message.append(translationService.translate("%popupmenu.label.show_scenario_usage1")).append(" ")
							.append(useage.scenarioName).append(" ")
							.append(translationService.translate("%popupmenu.label.show_scenario_usage_unused"));
				} else {
					message.append(translationService.translate("%popupmenu.label.show_scenario_usage1")).append(" ")
							.append(useage.scenarioName).append(" ")
							.append(translationService.translate("%popupmenu.label.show_scenario_usage2"))
							.append(" \"")
							.append(getCommaListOfTestStructuresNames(useage.getScenarioUsedBy()).toString())
							.append("\" ")
							.append(translationService.translate("%popupmenu.label.show_scenario_usage3")).append("\n");

				}
			}
			if (runningInUIThread) {
				MessageDialog.openInformation(Display.getCurrent().getActiveShell(),
						translationService.translate("%popupmenu.label.show_scenario_usage.item"), message.toString());
			}
		}
	}

	/**
	 * builds a comma separated list of the names of the TestFlows.
	 * 
	 * @param testFlowNames
	 *            List of the names of <TestFlow>
	 * @return the message
	 */
	private StringBuilder getCommaListOfTestStructuresNames(List<String> testFlowNames) {
		StringBuilder message = new StringBuilder();
		boolean first = true;
		for (String names : testFlowNames) {
			if (first) {
				first = false;
			} else {
				message.append(", ");
			}
			message.append(names);
		}
		return message;
	}

	/**
	 * 
	 * local class a a container for the usage-results.
	 * 
	 * @author llipinski
	 */
	private static class TestScenarioUseage {
		private String scenarioName;
		private List<String> scenarioUsedBy;

		/**
		 * constructor.
		 * 
		 * @param scenarioName
		 *            name of the scenario
		 * @param scenarioUsedBy
		 *            List of the names of the testflows, how are using the
		 *            scenario.
		 */
		public TestScenarioUseage(String scenarioName, List<String> scenarioUsedBy) {
			this.scenarioName = scenarioName;
			this.scenarioUsedBy = scenarioUsedBy;
		}

		/**
		 * 
		 * @return the name of the scenario.
		 */
		public String getScenarioName() {
			return scenarioName;
		}

		/**
		 * 
		 * @return List of the names of the testflows, how are using the
		 *         scenario.
		 */
		public List<String> getScenarioUsedBy() {
			return scenarioUsedBy;
		}
	}
}
