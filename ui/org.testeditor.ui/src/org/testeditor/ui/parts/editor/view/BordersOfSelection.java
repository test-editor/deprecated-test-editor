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

import java.util.Arrays;

/**
 * 
 * this class is a container for the lower- and upperBorder of a selection.
 * 
 * @author llipinski
 */
class BordersOfSelection {

	private int lowerBorder = -1;
	private int upperBorder = -1;

	/**
	 * default constructor.
	 */
	public BordersOfSelection() {
		super();
	}

	/**
	 * constructor with the parameters klicked- and releasedLine.
	 * 
	 * @param klickedLine
	 *            int klickedLine
	 * @param releasedLine
	 *            int releasedLine
	 */
	public BordersOfSelection(int klickedLine, int releasedLine) {
		int[] a = new int[] { klickedLine, releasedLine };
		Arrays.sort(a);
		setLowerBorder(a[0]);
		setUpperBorder(a[1]);
	}

	/**
	 * setter for the lower border.
	 * 
	 * @param lowerBorder
	 *            int lower border
	 */
	public void setLowerBorder(int lowerBorder) {
		this.lowerBorder = lowerBorder;
	}

	/**
	 * getter for the lower border.
	 * 
	 * @return int the lower border
	 */
	public int getLowerBorder() {
		return lowerBorder;
	}

	/**
	 * setter for the upper border of the selection.
	 * 
	 * @param upperBorder
	 *            int upper border
	 */
	public void setUpperBorder(int upperBorder) {
		this.upperBorder = upperBorder;
	}

	/**
	 * getter for the upper border.
	 * 
	 * @return the upper border
	 */
	public int getUpperBorder() {
		return upperBorder;
	}

}
