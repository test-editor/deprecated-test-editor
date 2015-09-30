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
package org.testeditor.ui.parts.reporting;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

/**
 * Switches the highlight state of the LogView.
 *
 */
public class HighlightLogHandler {

	/**
	 * Refreshes the TestLogView to check the new highlight state.
	 * 
	 * @param partService
	 *            used to lookup the TestLogView.
	 */
	@Execute
	public void execute(EPartService partService) {
		TestLogView testLogView = (TestLogView) partService.findPart(TestLogView.ID).getObject();
		testLogView.refreshView();
	}

}
