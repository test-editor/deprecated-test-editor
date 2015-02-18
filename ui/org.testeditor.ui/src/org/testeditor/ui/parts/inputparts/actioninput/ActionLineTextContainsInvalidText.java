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

/**
 * checks that their are none of the invalideChars in the given text.
 * 
 * @author llipinski
 * 
 */
public class ActionLineTextContainsInvalidText {

	private String allInvalidChars = "";

	/**
	 * 
	 * @return the invalid chars as an array
	 */
	private char[] getInvalidChars() {
		return allInvalidChars.toCharArray();
	}

	/**
	 * @param text
	 *            the text to inspect
	 * @return false, if an invalid char is in the text.
	 */
	protected boolean containsTextInvalidChar(String text) {
		for (char c : getInvalidChars()) {
			if (text.contains(String.valueOf(c))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * sets the invalidChars to check.
	 * 
	 * @param invalidChars
	 *            String
	 */
	public void setAllINvalidChars(String invalidChars) {
		this.allInvalidChars = invalidChars;
	}
}
