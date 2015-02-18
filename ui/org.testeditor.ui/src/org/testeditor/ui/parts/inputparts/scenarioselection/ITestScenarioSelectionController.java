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

import org.testeditor.core.model.teststructure.TestStructure;

/**
 * 
 * small interface in front of the @link
 * {@link TestEditorScenarioSelectionController} in the link from the
 * {@link TestEditorScenarioSelectionView}.
 * 
 * @author llipinski
 */
public interface ITestScenarioSelectionController {
	/**
	 * set the scenario with the same name as the parameter nameOfScenario in
	 * the TestFlow.
	 * 
	 * @param selectedScenario
	 *            TestStructure
	 * @param lineInTestCase
	 *            line in the testcase
	 * @param addMode
	 *            true if add, false if change
	 */
	void setScenarioIntoTestFlow(TestStructure selectedScenario, int lineInTestCase, boolean addMode);

	/**
	 * opens the selected scenario.
	 * 
	 * @param nameOfScenario
	 *            with spaces in between
	 */
	void openSelectedScenario(String nameOfScenario);
}
