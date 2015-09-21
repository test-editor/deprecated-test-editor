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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.testeditor.core.model.action.Argument;

/**
 * 
 * This class is a wrapper around the SWT.Text it implements
 * IActionLineInputWidget. So there are less instanceof operations necessary.
 * 
 * @author llipinski
 */
public class ActionLineInputText extends ActionLineTextContainsInvalidText implements IActionLineInputWidget {

	private Text wrappedText;

	/**
	 * Constructor.
	 * 
	 * @param parent
	 *            compsite
	 * @param style
	 *            SWT.Style
	 */
	public ActionLineInputText(Composite parent, int style) {
		wrappedText = new Text(parent, style);
		GridData gd = new GridData(SWT.NONE);
		gd.minimumWidth = 100;
		gd.widthHint = 100;
		wrappedText.setLayoutData(gd);
	}

	@Override
	public void showText(String text) {
		wrappedText.setText(text.substring(0, text.length() - 1));
	}

	@Override
	public String getInputText() {
		return wrappedText.getText();
	}

	/**
	 * Wrapper around the method setText(String) from the SWT.Text.
	 * 
	 * @param text
	 *            String
	 */
	public void setText(String text) {
		wrappedText.setText(text);
	}

	@Override
	public void dispose() {
		wrappedText.dispose();
	}

	/**
	 * Wrapper around the method setForeground(Color) from the SWT.Text.
	 * 
	 * @param color
	 *            Color
	 */
	public void setForeground(Color color) {
		wrappedText.setForeground(color);
	}

	/**
	 * Wrapper around the method setBackground(Color) from the SWT.Text.
	 * 
	 * @param color
	 *            Color
	 */
	public void setBackground(Color color) {
		wrappedText.setBackground(color);
	}

	/**
	 * Wrapper around the method addFocusListener(FocusListener) from the
	 * SWT.Text.
	 * 
	 * @param listener
	 *            FocusListener
	 */
	public void addFocusListener(FocusListener listener) {
		wrappedText.addFocusListener(listener);
	}

	/**
	 * Wrapper around the method addModifyListener(ModifyListener) from the
	 * SWT.Text.
	 * 
	 * @param listener
	 *            ModifyListener
	 */
	public void addModifyListener(ModifyListener listener) {
		wrappedText.addModifyListener(listener);
	}

	@Override
	public String getText() {
		return wrappedText.getText();
	}

	/**
	 * set the bounds of the wrappedText.
	 * 
	 * @param x
	 *            x
	 * @param y
	 *            y
	 * @param width
	 *            width
	 * 
	 * @param heigth
	 *            Height
	 * 
	 */
	public void setBounds(int x, int y, int width, int heigth) {
		wrappedText.setBounds(x, y, width, heigth);
	}

	/**
	 * gets the width of the wrappedText.
	 * 
	 * @return {@link Rectangle}
	 */
	public Rectangle getBounds() {
		return wrappedText.getBounds();
	}

	/**
	 * Returns true if the receiver is visible, and false otherwise.
	 * 
	 * If one of the receiver's ancestors is not visible or some other condition
	 * makes the receiver not visible, this method may still indicate that it is
	 * considered visible even though it may not actually be showing.
	 * 
	 * @return the receiver's visibility state
	 */
	public boolean getVisible() {
		return wrappedText.getVisible();
	}

	/**
	 * Returns true if the widget has been disposed, and false otherwise.
	 * 
	 * This method gets the dispose state for the widget. When a widget has been
	 * disposed, it is an error to invoke any other method (except dispose())
	 * using the widget.
	 * 
	 * 
	 * @return true when the widget is disposed and false otherwise
	 */
	@Override
	public boolean isDisposed() {
		return wrappedText.isDisposed();
	}

	@Override
	public boolean isInputValid() {
		return !getText().isEmpty() && !containsTextInvalidChar(getText());
	}

	/**
	 * Adds a KeyListener to the TextWidget.
	 * 
	 * @param keyListener
	 *            to add
	 */
	@Override
	public void addKeyListener(KeyListener keyListener) {
		wrappedText.addKeyListener(keyListener);
	}

	@Override
	public void setData(Object data) {
		wrappedText.setData(data);
	}

	@Override
	public void setData(String key, Object value) {
		wrappedText.setData(key, value);

	}

	@Override
	public Argument getArgument() {
		Argument argument = new Argument();
		argument.setValue(getText());
		return argument;
	}

	@Override
	public boolean setCursor(int posInWidget) {
		wrappedText.setSelection(posInWidget);
		return true;

	}

	@Override
	public void setFocus() {
		wrappedText.setFocus();

	}

	@Override
	public boolean isInputField() {
		return true;
	}

	@Override
	public int getCursorPos() {
		return wrappedText.getSelection().x;
	}
}
