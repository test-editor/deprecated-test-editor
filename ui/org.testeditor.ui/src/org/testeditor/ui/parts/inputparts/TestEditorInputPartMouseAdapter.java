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
package org.testeditor.ui.parts.inputparts;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.ui.constants.TestEditorUIEventConstants;

/**
 * special {@link MouseAdapter} to create a event for the selection in the tree,
 * if a component in the middle part is selected.
 * 
 */
public class TestEditorInputPartMouseAdapter extends MouseAdapter {

	private IEventBroker eventBroker;
	private TestStructure testStructure;

	/**
	 * constructor.
	 * 
	 * @param eventBroker
	 *            IEventBroker
	 * @param testStructure
	 *            TestStructure of the part with focus.
	 */
	public TestEditorInputPartMouseAdapter(IEventBroker eventBroker, TestStructure testStructure) {
		this.eventBroker = eventBroker;
		this.testStructure = testStructure;
	}

	@Override
	public void mouseDown(MouseEvent e) {
		eventBroker.send(TestEditorUIEventConstants.ACTIVE_TESTFLOW_EDITOR_CHANGED, testStructure);
	}

	@Override
	public void mouseDoubleClick(MouseEvent e) {
		eventBroker.send(TestEditorUIEventConstants.ACTIVE_TESTFLOW_EDITOR_CHANGED, testStructure);
	}
}
