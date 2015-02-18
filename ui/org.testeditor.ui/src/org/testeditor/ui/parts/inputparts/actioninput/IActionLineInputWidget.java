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
import org.testeditor.core.model.action.Argument;

/**
 * 
 * this is a interface for the elements in the action line input.
 * 
 * @author llipinski
 */
public interface IActionLineInputWidget {

	/**
	 * this method should show the text.
	 * 
	 * @param text
	 *            text should be shown
	 */
	void showText(String text);

	/**
	 * the implementors should have a dispose method to prevent resource leaks.
	 */
	void dispose();

	/**
	 * the implementors should have a getInputText method to get the input.
	 * 
	 * @return the inputText
	 */
	String getInputText();

	/**
	 * the implementors should have the getText method to get the text.
	 * 
	 * @return the text of the widget
	 */
	String getText();

	/**
	 * returns true if the widget has a valid input.
	 * 
	 * @return boolean valid input
	 */

	boolean isInputValid();

	/**
	 * Adds a keyListern.
	 * 
	 * @param listener
	 *            the KeyListener
	 */
	void addKeyListener(KeyListener listener);

	/**
	 * setData - method for the wrapped {@link widget}.
	 * 
	 * @param data
	 *            Object
	 */
	void setData(Object data);

	/**
	 * setData - method for the wrapped {@link widget}.
	 * 
	 * @param key
	 *            the name of the property
	 * @param value
	 *            the new value for the property
	 */
	void setData(String key, Object value);

	/**
	 * 
	 * @return the argument, that is chosen (combobox) or set in the input-text
	 */
	Argument getArgument();

	/**
	 * sets the cursor in the widget, if possible.
	 * 
	 * @param posInWidget
	 *            position in the widget
	 * @return true, if the cursor is set, else false.
	 */
	boolean setCursor(int posInWidget);

	/**
	 * sets the focus on the on the widget.
	 */
	void setFocus();

	/**
	 * 
	 * @return true, if the widget is an input-field, else false.
	 */
	boolean isInputField();

	/**
	 * 
	 * @return true, if the wrappedwidget isDisposed.
	 */
	boolean isDisposed();

	/**
	 * 
	 * @return the cursorposition, if the cursor is in the widget, else -1.
	 */
	int getCursorPos();

}
