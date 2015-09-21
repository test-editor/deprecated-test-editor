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
package org.testeditor.ui.parts.editor.view;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * 
 * test-class for the class BordersOfSelection.
 * 
 */
public class BordersOfSelectionTest {
	/**
	 * test klickedLine < releasedLine.
	 */
	@Test
	public void testKlickedLineSmaller() {
		int klickedLine = 2;
		int relesedLine = 12;
		BordersOfSelection bos = new BordersOfSelection(klickedLine, relesedLine);
		assertTrue("testKlickedLineSmaller", bos.getLowerBorder() < bos.getUpperBorder());
	}

	/**
	 * test klickedLine > releasedLine.
	 */
	@Test
	public void testKlickedLineBigger() {
		int klickedLine = 22;
		int relesedLine = 12;
		BordersOfSelection bos = new BordersOfSelection(klickedLine, relesedLine);
		assertTrue("testKlickedLineBigger", bos.getLowerBorder() < bos.getUpperBorder());
	}
}
