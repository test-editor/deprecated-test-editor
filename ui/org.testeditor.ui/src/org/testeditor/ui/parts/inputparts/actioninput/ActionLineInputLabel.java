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
package org.testeditor.ui.parts.inputparts.actioninput;

import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.testeditor.core.model.action.Argument;

/**
 * 
 * This class is a wrapper around the SWT.Label it implements
 * IActionLineInputWidget. So there are less instanceof operations necessary.
 * 
 * @author llipinski
 */
public class ActionLineInputLabel implements IActionLineInputWidget {

	private Label wrappedLabel;

	/**
	 * Constructor.
	 * 
	 * @param parent
	 *            compsite
	 * @param style
	 *            SWT.style
	 */
	public ActionLineInputLabel(Composite parent, int style) {
		wrappedLabel = new Label(parent, style);
	}

	@Override
	public void showText(String text) {
	}

	@Override
	public String getInputText() {
		return wrappedLabel.getText();
	}

	@Override
	public void dispose() {
		wrappedLabel.dispose();
	}

	/**
	 * Sets the text of the label.
	 * 
	 * @param text
	 *            string
	 */
	public void setText(String text) {
		wrappedLabel.setText(text);
	}

	@Override
	public String getText() {
		return null;
	}

	@Override
	public boolean isInputValid() {
		return true;
	}

	/**
	 * Adds a keyListern.
	 * 
	 * @param listener
	 *            the KeyListener
	 */
	public void addKeyListener(KeyListener listener) {
		wrappedLabel.addKeyListener(listener);
	}

	@Override
	public void setData(Object data) {
		wrappedLabel.setData(data);
	}

	@Override
	public void setData(String key, Object value) {
		wrappedLabel.setData(key, value);

	}

	@Override
	public Argument getArgument() {
		return null;
	}

	@Override
	public boolean setCursor(int posInWidget) {
		return false;
	}

	@Override
	public void setFocus() {
		wrappedLabel.setFocus();
	}

	@Override
	public boolean isInputField() {
		return false;
	}

	@Override
	public boolean isDisposed() {
		return wrappedLabel.isDisposed();
	}

	@Override
	public int getCursorPos() {
		return -1;
	}

}
