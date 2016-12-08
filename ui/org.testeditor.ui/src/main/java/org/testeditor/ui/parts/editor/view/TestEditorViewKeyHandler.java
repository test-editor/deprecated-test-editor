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

import javax.inject.Inject;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.testeditor.ui.constants.TestEditorUIEventConstants;

/**
 * 
 * Identification of key events. Supports IEventBroker (event bus).
 * 
 */
public class TestEditorViewKeyHandler {

	@Inject
	private IEventBroker eventBroker;

	/**
	 * handles the key-events.
	 * 
	 * @param e
	 *            the KeyEvent
	 */
	protected void doHandleKeyEvent(KeyEvent e) {

		if (e.stateMask == SWT.None) {
			if (e.keyCode == SWT.F6) {
				eventBroker.send(TestEditorUIEventConstants.EDIT_CONTEXTMENU_F6, "");
			} else if (e.keyCode == SWT.F7) {
				// start the edit of the description
				eventBroker.send(TestEditorUIEventConstants.EDIT_CONTEXTMENU_F7, "");
				// openDialogForF7();
			} else if (e.keyCode == SWT.F8) {
				// start the edit of the action
				eventBroker.send(TestEditorUIEventConstants.EDIT_CONTEXTMENU_F8, "");
				// openDialogForF8();
			} else if (e.keyCode == SWT.DEL) {
				// delete line
				eventBroker.send(TestEditorUIEventConstants.EDIT_CONTEXTMENU_DEL, "");
			}
		} else if ((e.stateMask & SWT.CTRL) == SWT.CTRL || (e.stateMask & SWT.COMMAND) == SWT.COMMAND) {
			// CTRL (Windows + Linux ) COMMAND (APPLE)?
			ctrlkeyPressed(e);
		}

	}

	/**
	 * method is called, when Ctrl-Key is pressed.
	 * 
	 * @param e
	 *            KeyEvent
	 */
	private void ctrlkeyPressed(KeyEvent e) {
		if (e.keyCode == 'c') {
			eventBroker.send(TestEditorUIEventConstants.EDIT_CONTEXTMENU_CNTRL_C, "");
		} else if (e.keyCode == 'x') {
			eventBroker.send(TestEditorUIEventConstants.EDIT_CONTEXTMENU_CNTRL_X, "");
		} else if (e.keyCode == 'v') {
			eventBroker.send(TestEditorUIEventConstants.EDIT_CONTEXTMENU_CNTRL_V, "");
		} else if (e.keyCode == SWT.HOME || e.keyCode == SWT.END) {
			eventBroker.send(TestEditorUIEventConstants.EDIT_CONTEXTMENU_HOME_OR_END, "");
		} else if (e.keyCode == 'a') {
			eventBroker.send(TestEditorUIEventConstants.EDIT_CONTEXTMENU_CNTRL_A, "");
		} else if (e.keyCode == SWT.INSERT) {
			eventBroker.send(TestEditorUIEventConstants.EDIT_CONTEXTMENU_CNTRL_INSERT, "");
		}
	}

}
