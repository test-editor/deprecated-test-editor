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

import org.junit.Test;
import org.testeditor.core.model.teststructure.TestCase;
import org.testeditor.core.model.teststructure.TestFlow;

/**
 * 
 * test for the TestEditorParameterTableFocusEventObject.
 * 
 * @author llipinski
 */
public class TestEditorParameterTableFocusEventObjectTest {
	/**
	 * test the constructor and the getters for the memebervariables testFlow
	 * and tableViewer.
	 */
	@Test
	public void testConstructor() {
		TestFlow testFlow = new TestCase();
		TestEditorViewTableViewer tableViewer = new TestEditorViewTableViewer();
		TestEditorParameterTableFocusEventObject eventObject = new TestEditorParameterTableFocusEventObject(testFlow,
				tableViewer);
		assertEquals(tableViewer, eventObject.getTableViewer());
		assertEquals(testFlow, eventObject.getTestFlow());
	}
}
