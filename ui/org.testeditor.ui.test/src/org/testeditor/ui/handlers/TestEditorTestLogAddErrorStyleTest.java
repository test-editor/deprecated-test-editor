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
package org.testeditor.ui.handlers;

import static org.junit.Assert.assertEquals;

import org.eclipse.swt.custom.StyleRange;
import org.junit.Test;
import org.testeditor.ui.utilities.TestEditorTestLogAddErrorStyle;

/**
 * test class for TestEditorTestLogAddErrorStyle.
 * 
 * @author llipinski
 * 
 */
public class TestEditorTestLogAddErrorStyleTest {

	private TestEditorTestLogAddErrorStyle addErrorStyle = new TestEditorTestLogAddErrorStyle();

	/**
	 * test a simple message without an error.
	 */
	@Test
	public void addErrorStyle() {

		String message = "Hallo Welt!";
		StyleRange[] styleRanges = addErrorStyle.addErrorStyle(message);
		assertEquals(1, styleRanges.length);

	}

	/**
	 * test a message just with one error inside.
	 */
	@Test
	public void addErrorStyleWithError() {

		String message = "Hallo Welt!\nDies ist eine Fehler [ERROR]";
		StyleRange[] styleRanges = addErrorStyle.addErrorStyle(message);
		assertEquals(2, styleRanges.length);

	}

	/**
	 * test a message just with one error inside. But with an other pattern.
	 */

	@Test
	public void addErrorStyleWithErrorOtherPattern() {

		String message = "Hallo Welt!\nDies ist eine Fehler ] ERROR (";
		StyleRange[] styleRanges = addErrorStyle.addErrorStyle(message);
		assertEquals(2, styleRanges.length);

	}

	/**
	 * test a message just with one error inside. But with an other pattern and
	 * more lines.
	 * 
	 */
	@Test
	public void addErrorStyleWithErrorOtherPatternWithMoreLines() {

		String message = "Hallo Welt!\nDies ist eine Fehler ] ERROR (\nUnd noch mehr Zeilen\nZeile eins\nZeile zwei";
		StyleRange[] styleRanges = addErrorStyle.addErrorStyle(message);
		assertEquals(3, styleRanges.length);
	}

}
