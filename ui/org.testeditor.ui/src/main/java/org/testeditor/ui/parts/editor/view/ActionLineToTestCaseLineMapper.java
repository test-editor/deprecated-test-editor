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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * <pre>
 * Mapper for code line to test case line.
 * 
 * Example: 
 * Row 	| Action
 * 1	Description
 * 2
 * 3	Mask
 * 4	Navigate to URL: http:/...
 * 
 * Maps to FitNesse
 * Row|FitNesse
 * 1|Description
 * 2|Mask
 * 3|Navigate to URL: http:/...
 * 
 * Space row 2 in action will not be mapped to FitNesse row.
 * </pre>
 */
public class ActionLineToTestCaseLineMapper {

	private Map<Integer, Integer> offsetViewToTestList = new HashMap<Integer, Integer>();

	/**
	 * resets the offsetViewToTestList.
	 */
	public void resetOffsetToTestList() {
		offsetViewToTestList.clear();
	}

	/**
	 * store the translation of the lines in the view to the lines in the test
	 * case in the dictionary.
	 * 
	 * @param offsetLines
	 *            offset
	 * @param lineInTestCase
	 *            lineInTestCase
	 */
	public void rememberLineOffset(int offsetLines, int lineInTestCase) {
		offsetViewToTestList.put(lineInTestCase + offsetLines, lineInTestCase);
	}

	/**
	 * There are additional lines in the styledText to show the name of the
	 * masks and in front of the descriptions. So there is an offset between the
	 * lines in the styledText and the test case. This method translates from
	 * the lineNumber in the styledText to the lineNumber in the test case.
	 * 
	 * @param lineIndex
	 *            int Line in the styledText
	 * @return in Line in the TestCase
	 */
	public int getContentOfOffsetViewToTestListAt(int lineIndex) {

		if (offsetViewToTestList.containsKey(lineIndex)) {
			return offsetViewToTestList.get(lineIndex);
		} else if (getMaxKey() <= lineIndex && offsetViewToTestList.size() >= 1) {
			return getValueOfMaxKey();
		}
		return -1;
	}

	/**
	 * 
	 * @return the value at the max key
	 */
	public int getValueOfMaxKey() {
		if (offsetViewToTestList.isEmpty()) {
			return 0;
		}
		int maxKey = getMaxKey();
		return offsetViewToTestList.get(maxKey);
	}

	/**
	 * 
	 * @return the maximum of the key in the hashmap.
	 */
	public int getMaxKey() {

		if (offsetViewToTestList.isEmpty()) {
			return 0;
		}
		ArrayList<Integer> arrayListKeys = new ArrayList<Integer>(Arrays.asList(offsetViewToTestList.keySet().toArray(
				new Integer[offsetViewToTestList.keySet().size()])));

		Integer maxKey = Collections.max(arrayListKeys);
		return maxKey.intValue();
	}

	/**
	 * returns the line in the view, that correspondents to the line in the test
	 * case.
	 * 
	 * @param selectedLineInTestCase
	 *            line number in the testcase
	 * @return line number in the view
	 */
	public int getCorrespondingLine(int selectedLineInTestCase) {
		if (offsetViewToTestList.size() > 0) {

			for (int i = offsetViewToTestList.size() - 1; i >= 0; i--) {
				if (!offsetViewToTestList.containsKey(i)) {
					return 0;
				}
				if (offsetViewToTestList.get(i) == selectedLineInTestCase) {
					return i;
				}
			}
		}
		return 0;
	}
}
