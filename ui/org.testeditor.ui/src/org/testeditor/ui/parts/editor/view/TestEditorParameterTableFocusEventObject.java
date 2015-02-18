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

import org.testeditor.core.model.teststructure.TestFlow;

/**
 * 
 * this object contains information about the context of the focus-event.
 * 
 * @author llipinski
 */
public class TestEditorParameterTableFocusEventObject {
	private TestFlow testFlow;
	private TestEditorViewTableViewer tableViewer;

	/**
	 * constructor with parameters.
	 * 
	 * @param testFlow
	 *            TestFlow
	 * @param tableViewer
	 *            TestEditorViewTableViewer
	 */
	public TestEditorParameterTableFocusEventObject(TestFlow testFlow, TestEditorViewTableViewer tableViewer) {
		this.testFlow = testFlow;
		this.tableViewer = tableViewer;
	}

	/**
	 * 
	 * @return the TestFlow
	 */
	public TestFlow getTestFlow() {
		return testFlow;
	}

	/**
	 * 
	 * @return the TestEditorViewTableViewer
	 */
	public TestEditorViewTableViewer getTableViewer() {
		return tableViewer;
	}

}
