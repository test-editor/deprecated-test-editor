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
package org.testeditor.ui.parts.editor.view.handler;

import javax.inject.Inject;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.testeditor.ui.parts.editor.view.TestEditorTestCaseController;
import org.testeditor.ui.parts.editor.view.TestEditorTestScenarioController;

/**
 * 
 * parent-class for all handler in the TestEditorView.
 * 
 */
public class TestEditorViewHandler {

	/**
	 * @param partService
	 *            EPartService
	 * 
	 * @return the top part of all open views with the
	 *         TestEditorTestCaseController.ID or
	 *         TestEditorTestScenarioController.ID.
	 */
	@Inject
	protected MPart getPartOnTop(EPartService partService) {
		MPart part = partService.getActivePart();
		if (part != null
				&& (part.getElementId().equals(TestEditorTestCaseController.ID)
						|| part.getElementId().equals(TestEditorTestScenarioController.ID))
				&& part.getObject() != null && part.isOnTop()) {
			return part;
		}
		return null;
	}

}
