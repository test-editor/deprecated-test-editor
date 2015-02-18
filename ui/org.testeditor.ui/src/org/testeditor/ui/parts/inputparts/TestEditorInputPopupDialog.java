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
package org.testeditor.ui.parts.inputparts;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.swt.custom.StyledText;
import org.testeditor.ui.parts.editor.ITestEditorController;

/**
 * 
 * Popup Dialog to edit the content of the actual line in the Testeditor.
 * 
 * @author karsten
 */
public class TestEditorInputPopupDialog extends TestEditorInputTestFlowPopupDialog {

	public static final int ACTION_TYPE = 0;
	public static final int DESCRIPTION_TYPE = 1;
	private ITestEditorController testFlowController;

	/**
	 * Constructs the PopupDialog for editing a TestCase line.
	 * 
	 * @param titleText
	 *            of the Dialog
	 * @param context
	 *            EclipseContext
	 * @param testFlowController
	 *            testFlowController to register the local controller
	 * @param styledText
	 *            to work with
	 */
	public TestEditorInputPopupDialog(String titleText, IEclipseContext context,
			ITestEditorController testFlowController, StyledText styledText) {
		super(titleText, context, testFlowController.getTestFlow(), styledText);
		this.testFlowController = testFlowController;
	}

	/**
	 * 
	 * @return ITestEditorController.
	 */
	public ITestEditorController getTestCaseController() {
		return testFlowController;
	}

	@Override
	public boolean close() {
		getTestCaseController().removePopupEditingControllers();
		return super.close();
	}
}
