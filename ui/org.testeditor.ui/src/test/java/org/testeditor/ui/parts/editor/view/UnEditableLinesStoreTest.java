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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

/**
 * this class tests the class UnEditableLinesStore.
 * 
 */
public class UnEditableLinesStoreTest {

	private UnEditableLinesStore unEditableLinesStore;

	/**
	 * init the variables before the tests.
	 */
	@Before
	public void beforeTest() {
		unEditableLinesStore = new UnEditableLinesStore();
	}

	/**
	 * test the storing in the {@link UnEditableLinesStore}.
	 */
	@Test
	public void testStoringAndClearningUnEditableLine() {

		int lineNumber = 3;
		unEditableLinesStore.rememberUnEditableLine(lineNumber);
		unEditableLinesStore.rememberUnEditableLine(0);
		assertFalse(unEditableLinesStore.isLineEditable(lineNumber));
		unEditableLinesStore.clearUnEditableLines();
		assertTrue(unEditableLinesStore.isLineEditable(lineNumber));

	}

	/**
	 * tests the method IsSelectionEditable.
	 */
	@Test
	public void testIsSelectionEditable() {
		unEditableLinesStore.rememberUnEditableLine(3);
		unEditableLinesStore.rememberUnEditableLine(7);
		unEditableLinesStore.rememberUnEditableLine(8);
		unEditableLinesStore.rememberUnEditableLine(9);
		assertFalse(unEditableLinesStore.isSelectionEditable(7, 9));
		assertTrue(unEditableLinesStore.isSelectionEditable(3, 9));
		assertTrue(unEditableLinesStore.isSelectionEditable(1, 2));
		assertTrue(unEditableLinesStore.isSelectionEditable(1, 3));
		assertTrue(unEditableLinesStore.isSelectionEditable(1, 4));
	}
}
