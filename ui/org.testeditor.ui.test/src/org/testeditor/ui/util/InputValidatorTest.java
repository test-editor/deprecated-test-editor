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
package org.testeditor.ui.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.testeditor.ui.utilities.InputValidator;

/**
 * Testclass for Validation.
 * 
 */
public class InputValidatorTest {

	/**
	 * Test for valid system variable.
	 */
	@Test
	public void testSystemPropertyVariable() {

		String input = "${abcdef}";
		assertTrue(InputValidator.isInputValidSystemProperty(input));

		input = "${abcdef.efgaf}";
		assertTrue(InputValidator.isInputValidSystemProperty(input));

		input = "${abcdef.e$%/\\fgaf}";
		assertTrue(InputValidator.isInputValidSystemProperty(input));

		// with whitespaces
		input = "${abcdef.efgaf }";
		assertFalse(InputValidator.isInputValidSystemProperty(input));

		input = "${abcd  ef .efgaf }";
		assertFalse(InputValidator.isInputValidSystemProperty(input));

		// with brackets
		input = "${abc{def.efgaf }";
		assertFalse(InputValidator.isInputValidSystemProperty(input));

		input = "${abcdef.{}efgaf }";
		assertFalse(InputValidator.isInputValidSystemProperty(input));

	}
}
