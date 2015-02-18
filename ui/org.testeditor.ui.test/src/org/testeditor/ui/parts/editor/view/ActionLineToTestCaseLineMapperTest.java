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

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

/**
 * 
 * Testing the mapping of code line to test case line.
 * 
 */
public class ActionLineToTestCaseLineMapperTest {

	private ActionLineToTestCaseLineMapper codeLineMapper = new ActionLineToTestCaseLineMapper();

	/**
	 * setup.
	 */
	@Before
	public void setup() {
		codeLineMapper.resetOffsetToTestList();
	}

	/**
	 * Test for GetValueOfMaxKey.
	 */
	@Test
	public void offsetHandlingTest() {

		codeLineMapper.rememberLineOffset(1, 10);
		assertEquals("Equals 11 if data set is correctly saved", codeLineMapper.getMaxKey(), 11);

		assertEquals("Equals 10 (value) if code line was correctly found",
				codeLineMapper.getContentOfOffsetViewToTestListAt(11), 10);

		assertEquals("Equals 10 (value) because it's max code line value",
				codeLineMapper.getContentOfOffsetViewToTestListAt(20), 10);

		assertEquals("Equals -1 because key was too small", codeLineMapper.getContentOfOffsetViewToTestListAt(4), -1);

		codeLineMapper.resetOffsetToTestList();
		assertEquals("Equals 0 because HashMap is empty", codeLineMapper.getMaxKey(), 0);
	}

	/**
	 * test getCorrespondingLine.
	 */
	@Test
	public void testGetCorrospondingLine() {
		codeLineMapper.rememberLineOffset(0, 0);
		codeLineMapper.rememberLineOffset(1, 0);
		codeLineMapper.rememberLineOffset(2, 0);
		codeLineMapper.rememberLineOffset(2, 1);
		codeLineMapper.rememberLineOffset(2, 3);
		codeLineMapper.rememberLineOffset(2, 4);
		codeLineMapper.rememberLineOffset(3, 5);

		assertEquals(5, codeLineMapper.getCorrespondingLine(3));

	}
}
