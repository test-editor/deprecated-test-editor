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
import java.util.List;

/**
 * this class stores the linenumbers of the uneditable lines.
 * 
 * @author llipinski
 * 
 */
public class UnEditableLinesStore {
	private List<Integer> unEditableLines = new ArrayList<Integer>();

	/**
	 * this method adds a new int to the unEditableLine.
	 * 
	 * @param unEditableLine
	 *            int number of an uneditable line
	 */

	protected void rememberUnEditableLine(int unEditableLine) {
		unEditableLines.add(Integer.valueOf(unEditableLine));
	}

	/**
	 * clears the unEditableLines.
	 * 
	 */
	protected void clearUnEditableLines() {
		unEditableLines = new ArrayList<Integer>();

	}

	/**
	 * this method checks the editable of a line. a line is not editable, if it
	 * contains a add line or the mask-name
	 * 
	 * @param lineNumber
	 *            number of the line
	 * @return true if it editable else false
	 */
	protected boolean isLineEditable(int lineNumber) {
		return !unEditableLines.contains(Integer.valueOf(lineNumber));
	}

	/**
	 * 
	 * @param firstLine
	 *            first line of the selection
	 * @param lastLine
	 *            last line of the selection.
	 * @return true, if the one of the selected lines is editable.
	 */
	protected boolean isSelectionEditable(int firstLine, int lastLine) {
		boolean isEditable = false;
		for (int i = firstLine; i <= lastLine; i++) {
			if (isLineEditable(i)) {
				isEditable = true;
				break;
			}
		}
		return isEditable;
	}
}
