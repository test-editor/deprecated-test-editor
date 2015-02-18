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

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.testeditor.ui.constants.TestEditorUIEventConstants;

/**
 * 
 * Handler-Class for the copy-action.
 * 
 * @author llipinski
 */
public class TestEditorViewCopyHandler extends TestEditorViewCCHandler {

	/**
	 * execute.
	 * 
	 * @param eventBroker
	 *            {@link IEventBroker}
	 */
	@Execute
	public void execute(IEventBroker eventBroker) {
		eventBroker.send(TestEditorUIEventConstants.EDIT_CONTEXTMENU_CNTRL_C, "");
	}
}
