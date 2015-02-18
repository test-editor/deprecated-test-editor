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
package org.testeditor.ui.parts.testhistory.handler;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.testeditor.ui.constants.TestEditorConstants;
import org.testeditor.ui.parts.testhistory.TestHistoryPart;

/**
 * clears the testhistory of a testcase or suite.
 */
public class ClearTestHistoryHandler {

	/**
	 * execution method.
	 * 
	 * @param partService
	 *            to look up the HistoryPart.
	 */
	@Execute
	public void execute(EPartService partService) {
		MPart activePart = partService.getActivePart();
		if (activePart.getObject() != null && activePart.getElementId().equals(TestHistoryPart.ID)) {
			((TestHistoryPart) activePart.getObject()).clearHistory();

		}
	}

	/**
	 * @param partService
	 *            to look up the HistoryPart.
	 * 
	 * @return true, if a teststructure is set in the testhistory.
	 */
	@CanExecute
	public boolean canExecute(EPartService partService) {
		if (partService != null) {
			MPart mPart = partService.findPart(TestEditorConstants.TEST_HISTORY_VIEW);
			if (mPart != null) {
				TestHistoryPart testHistoryController = (TestHistoryPart) mPart.getObject();

				if (testHistoryController != null) {
					return testHistoryController.canExecute();
				}
			}
		}
		return false;
	}
}
