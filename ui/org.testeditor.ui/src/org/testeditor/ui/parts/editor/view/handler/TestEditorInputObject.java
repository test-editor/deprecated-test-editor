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
package org.testeditor.ui.parts.editor.view.handler;

import org.testeditor.core.model.teststructure.TestComponent;
import org.testeditor.core.model.teststructure.TestFlow;

/**
 * this class contains the input in the description, action or
 * scenarioselection.
 * 
 * @author llipinski
 * 
 */
public class TestEditorInputObject {

	private TestFlow testflow;
	private TestComponent testcomponent;
	private int lineNumber;
	private int cursorPosInLine;
	private boolean addMode;

	/**
	 * constructor.
	 * 
	 * @param testflow
	 *            the TestFlow
	 * @param testComponent
	 *            the TestComponent
	 * @param selectedLine
	 *            as int
	 * @param cursorPosInLine
	 *            as int
	 * @param addMode
	 *            the mode add or replace as boolean
	 */
	public TestEditorInputObject(TestFlow testflow, TestComponent testComponent, int selectedLine, int cursorPosInLine,
			boolean addMode) {
		super();
		this.testflow = testflow;
		this.testcomponent = testComponent;
		this.lineNumber = selectedLine;
		this.cursorPosInLine = cursorPosInLine;
		this.addMode = addMode;
	}

	/**
	 * 
	 * @return the TestFlow.
	 */
	public TestFlow getTestflow() {
		return testflow;
	}

	/**
	 * 
	 * @return the TestComponent.
	 */
	public TestComponent getTestcomponent() {
		return testcomponent;
	}

	/**
	 * 
	 * @return the lineNumber.
	 */
	public int getLineNumber() {
		return lineNumber;
	}

	/**
	 * 
	 * @return the cursor position of in the input
	 */
	public int getCursorPosInLine() {
		return cursorPosInLine;
	}

	/**
	 * 
	 * @return the value of the addMode.
	 */
	public boolean isAddMode() {
		return addMode;
	}
}
