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
package org.testeditor.ui.parts.editor.view.listener;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.testeditor.core.model.teststructure.TestFlow;
import org.testeditor.ui.constants.TestEditorUIEventConstants;

/**
 * 
 * ModifyListener for the styledText.
 * 
 * @author llipinski
 */
public class TestEditorViewStyledTextModifyListener implements ModifyListener {

	private IEventBroker eventBroker;
	private TestFlow testFlow;

	/**
	 * constructor.
	 * 
	 * @param eventBroker
	 *            IEventBroker
	 * @param testflow
	 *            TestFlow
	 */
	public TestEditorViewStyledTextModifyListener(IEventBroker eventBroker, TestFlow testflow) {
		this.eventBroker = eventBroker;
		this.testFlow = testflow;
	}

	@Override
	public void modifyText(ModifyEvent e) {
		eventBroker.post(TestEditorUIEventConstants.TEST_FLOW_STATE_CHANGED_TO_DIRTY, testFlow);
	}
}
