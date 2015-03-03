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
import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.testeditor.core.model.teststructure.TestFlow;
import org.testeditor.ui.parts.editor.ITestEditorController;

/**
 * 
 * Popup Dialog to edit the content of the actual line in the Testeditor.
 * 
 */
public class TestEditorInputTestFlowPopupDialog extends PopupDialog {

	private TestFlow testFlow;

	private StyledText styledText;

	private ITestEditorController testFlowController;

	/**
	 * Constructs the PopupDialog for editing a TestCase line.
	 * 
	 * @param titleText
	 *            of the Dialog
	 * @param context
	 *            EclipseContext
	 * @param testFlowController
	 *            TestFlowController
	 * @param styledText
	 *            to work with
	 */
	public TestEditorInputTestFlowPopupDialog(String titleText, IEclipseContext context,
			ITestEditorController testFlowController, StyledText styledText) {
		super(Display.getCurrent().getActiveShell(), SWT.POP_UP, true, false, false, true, true, titleText, "");
		this.testFlow = testFlowController.getTestFlow();
		this.styledText = styledText;
		this.testFlowController = testFlowController;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		return area;
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

	@Override
	protected Point getDefaultLocation(Point initialSize) {
		Point location = getLocationOfReferenz();
		location.x += initialSize.x + 15;
		location.y += initialSize.y / 2;
		return location;
	}

	@Override
	protected Point getDefaultSize() {
		return new Point(400, 400);
	}

	/**
	 * 
	 * @return ITestEditorController.
	 */
	public TestFlow getTestFlow() {
		return testFlow;
	}

	/**
	 * 
	 * @return the location of the selection in the styledText.
	 */
	public Point getLocationOfReferenz() {
		return styledText.getLocationAtOffset(styledText.getCaretOffset());
	}
}
