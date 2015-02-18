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
package org.testeditor.core.util;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Tests the util class regarding the action line splitting.
 */
public class ActionLineSplitterTest {

	/**
	 * test split whit a delimiter.
	 */
	@Test
	public void testSpliteBevore() {
		String line = "|Gib in Feld|Passwort|den Wert|hugendubel|ein|";
		String delimiter = "|";
		String begin = ActionLineSplitter.splitLineBeforePatternNo(line, delimiter, 4);
		assertTrue(begin.equalsIgnoreCase("|Gib in Feld|Passwort|den Wert|"));
	}

	/**
	 * test split whit an other delimiter.
	 */
	@Test
	public void testSpliteBevoreOtherDelimiter() {
		String line = "@Gib in Feld@Passwort@den Wert@hugendubel@ein@";
		String delimiter = "@";
		String begin = ActionLineSplitter.splitLineBeforePatternNo(line, delimiter, 4);
		assertTrue(begin.equalsIgnoreCase("@Gib in Feld@Passwort@den Wert@"));
	}

	/**
	 * test remove a part of a line.
	 */
	@Test
	public void testRemovePatternNofromLine() {
		String line = "|Gib in Feld|Passwort|den Wert|hugendubel|ein|";
		String delimiter = "|";
		String begin = ActionLineSplitter.removePatternNofromLine(line, delimiter, 4);
		assertTrue(begin.equalsIgnoreCase("|Gib in Feld|Passwort|den Wert|ein|"));
	}
}
