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
package org.testeditor.ui.parts.inputparts.scenarioselection;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.internal.workbench.PartServiceImpl;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;

/**
 * 
 * this class handles the cleanup-request for the sceanrio-input.
 * 
 */
@SuppressWarnings("restriction")
public class CleanScenarioSelectionInput {
	@Inject
	private PartServiceImpl partService;

	/**
	 * execution method.
	 */
	@Execute
	public void execute() {
		TestEditorScenarioSelectionController scenarioController = null;
		MPart activePart = partService.getActivePart();
		if (activePart.getElementId().equals(TestEditorScenarioSelectionController.ID)) {
			scenarioController = (TestEditorScenarioSelectionController) activePart.getObject();
		}

		if (scenarioController != null) {
			scenarioController.cleanScenarioSelectionInTree();
		}
	}

}
