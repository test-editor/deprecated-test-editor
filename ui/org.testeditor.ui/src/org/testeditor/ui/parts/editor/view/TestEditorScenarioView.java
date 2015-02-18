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
package org.testeditor.ui.parts.editor.view;

import org.testeditor.core.model.teststructure.TestInvisibleContent;
import org.testeditor.ui.parts.editor.ITestEditorScenarioController;

/**
 * specialized Editor-View with functionality for the Scenario.
 * 
 * @author llipinski
 * 
 */
public class TestEditorScenarioView extends TestEditView {

	/**
	 * scan the TestScenario for the TestScenario-Parameters.
	 */
	private void scanForScenarioParameters() {
		ITestEditorScenarioController testCaseController = (ITestEditorScenarioController) getTestCaseController();
		if (testCaseController.getTestFlowSize() > 0
				&& !(testCaseController.getTestComponentAt(0) instanceof TestInvisibleContent)
				&& testCaseController.scanForScenarioParameters()) {
			rememberUnEditableLine(0);
			if (getStyledText().getText().length() > 0) {
				setClickedLine(getStyledText().getLineAtOffset(getStyledText().getCaretOffset()) + 1);
			}
		}
	}

	/**
	 * this method resets some internal variables before the refreshing of the
	 * styled texts.
	 * 
	 */
	protected void preRefreshStyledText() {
		super.preRefreshStyledText();
		scanForScenarioParameters();
	}
}
