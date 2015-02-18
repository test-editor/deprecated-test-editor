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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.testeditor.core.model.action.UnparsedActionLine;
import org.testeditor.core.model.teststructure.TestActionGroup;
import org.testeditor.core.model.teststructure.TestComponent;
import org.testeditor.core.model.teststructure.TestFlow;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.services.interfaces.TestStructureContentService;
import org.testeditor.ui.analyzer.errormodel.Error;
import org.testeditor.ui.analyzer.errormodel.ErrorContainer;

/**
 * 
 * RunnableWith Prograss to execute the Testflow Validation Operation and
 * informs the user about the progress.
 * 
 */
public class ValidadateTestFlowRunnable implements IRunnableWithProgress {

	private static final Logger LOGGER = Logger.getLogger(ValidadateTestFlowRunnable.class);
	private static final String BUNDLE_URI = "platform:/plugin/org.testeditor.ui.analyzer";
	@Inject
	private TestStructureContentService contentService;
	@Inject
	private TranslationService translate;
	private List<TestStructure> testflowsToBeValidated;
	private Map<TestFlow, ErrorContainer> errors;

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		monitor.beginTask(translate.translate("%analyzer.validate.tests", BUNDLE_URI), testflowsToBeValidated.size());
		List<String> messages = new ArrayList<String>();
		errors = new HashMap<TestFlow, ErrorContainer>();
		for (TestStructure testStructure : testflowsToBeValidated) {
			if (monitor.isCanceled()) {
				throw new InterruptedException();
			}
			if (testStructure instanceof TestFlow) {
				TestFlow testFlow = (TestFlow) testStructure;

				LOGGER.trace("check " + testFlow.getFullName());
				monitor.subTask(testFlow.getFullName());
				try {
					contentService.refreshTestCaseComponents(testFlow);
				} catch (Exception e) {

					messages.add("Error: " + testFlow.getFullName() + ": " + e.getMessage());
					LOGGER.error(testFlow.getFullName(), e);
				}

				List<TestComponent> testComponents = testFlow.getTestComponents();
				for (TestComponent testComponent : testComponents) {

					if (testComponent instanceof TestActionGroup) {
						for (org.testeditor.core.model.action.IAction action : ((TestActionGroup) testComponent)
								.getActionLines()) {

							if (action instanceof UnparsedActionLine) {

								String message = "Error: " + testFlow.getFullName();
								LOGGER.trace(message);
								if (!errors.containsKey(testFlow)) {
									errors.put(testFlow, new ErrorContainer(testFlow));
								}
								Error error = new Error(testFlow);
								error.setAction(action);
								errors.get(testFlow).add(error);
							}
						}
					}
				}
				monitor.worked(1);
			}

		}

	}

	/**
	 * Setting the TestFlow objects as a list for validation. This Method must
	 * be called before the validation runs.
	 * 
	 * @param testflows
	 *            input for validation.
	 */
	public void setTestFlowsToBeValidated(List<TestStructure> testflows) {
		this.testflowsToBeValidated = testflows;
	}

	/**
	 * Getter for the Validation results.
	 * 
	 * @return the result of the Validation.
	 */
	public Collection<ErrorContainer> getValidationResult() {
		return errors.values();
	}

}
