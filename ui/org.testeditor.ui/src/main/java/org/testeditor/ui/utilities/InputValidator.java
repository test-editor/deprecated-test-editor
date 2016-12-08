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
/**
 * 
 */
package org.testeditor.ui.utilities;

/**
 * Class for validation of inputs.
 * 
 */
public final class InputValidator {

	/**
	 */
	private InputValidator() {
	}

	/**
	 * Check if input value is a valid system property.
	 * 
	 * 
	 * @param text
	 *            checked input text.
	 * @return returns true if valid system property, otherwise false
	 */
	public static boolean isInputValidSystemProperty(String text) {

		String regEx = "\\$\\{[^\\s\\{\\}]*\\}";

		return text.matches(regEx);
	}

}
