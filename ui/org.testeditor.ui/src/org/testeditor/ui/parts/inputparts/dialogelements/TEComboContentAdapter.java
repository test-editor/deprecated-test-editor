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
package org.testeditor.ui.parts.inputparts.dialogelements;

import org.eclipse.jface.fieldassist.ComboContentAdapter;
import org.eclipse.swt.widgets.Control;

/**
 * special ComboContentAdapter.
 * 
 * @author llipinski
 * 
 */
public class TEComboContentAdapter extends ComboContentAdapter {

	private TECombo teCombobox;

	/**
	 * 
	 * @param teCombobox
	 *            TECombo
	 */
	public TEComboContentAdapter(TECombo teCombobox) {
		super();
		this.teCombobox = teCombobox;
	}

	/**
	 * @param control
	 *            the control
	 * @param text
	 *            the inputText
	 * @param cursorPosition
	 *            the position of the cursor in the text
	 */
	public void setControlContents(final Control control, final String text, final int cursorPosition) {
		super.setControlContents(control, text, cursorPosition);
		teCombobox.fireSelectionChangeEvent();
	}
}
