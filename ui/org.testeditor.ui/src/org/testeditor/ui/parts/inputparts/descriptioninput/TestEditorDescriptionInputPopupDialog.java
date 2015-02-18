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
package org.testeditor.ui.parts.inputparts.descriptioninput;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.testeditor.ui.parts.editor.ITestEditorController;
import org.testeditor.ui.parts.inputparts.TestEditorInputPopupDialog;

/**
 * 
 * specialize of TestEditorInputPopupDialog for input of descriptions.
 * 
 * @author llipinski
 */
public class TestEditorDescriptionInputPopupDialog extends TestEditorInputPopupDialog {
	/**
	 * constuctor.
	 * 
	 * @param titleText
	 *            String
	 * @param context
	 *            IEclipseContext
	 * @param testCaseController
	 *            ITestEditorController
	 * @param styledText
	 *            StyledText
	 */
	public TestEditorDescriptionInputPopupDialog(String titleText, IEclipseContext context,
			ITestEditorController testCaseController, StyledText styledText) {
		super(titleText, context, testCaseController, styledText);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		IEclipseContext popUpContext = getContext().createChild();
		popUpContext.set(Composite.class, area);

		TestEditorDescriptionInputController popupController = ContextInjectionFactory.make(
				TestEditorDescriptionInputController.class, popUpContext);
		popupController.setTestCaseController(getTestCaseController());
		getTestCaseController().setDescriptionControllerForPopupEditing(popupController);
		popupController.setPopupmode(true);
		return area;
	}
}
