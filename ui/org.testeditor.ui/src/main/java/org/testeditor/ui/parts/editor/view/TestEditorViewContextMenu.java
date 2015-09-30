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

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.widgets.Decorations;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.ui.constants.CustomWidgetIdConstants;
import org.testeditor.ui.constants.IconConstants;
import org.testeditor.ui.constants.TestEditorUIEventConstants;

/**
 * 
 * Building the context menu (right mouse button) for the TestEditorView.
 * 
 */
public class TestEditorViewContextMenu {
	private TestEditView testEditView;
	private static final Logger LOGGER = Logger.getLogger(TestEditorViewContextMenu.class);
	private MenuDetectEvent parentEvent;
	private Menu menu;

	/**
	 * Called from TestEditView to build the context menu.
	 * 
	 * @param parent
	 *            shell
	 * @param style
	 *            menu style
	 * @param testEditView
	 *            calling element
	 * @param e
	 *            menu detect event
	 * @return context menu
	 */
	public Menu getContextMenu(Decorations parent, int style, TestEditView testEditView, MenuDetectEvent e) {
		menu = new Menu(parent, style);
		this.testEditView = testEditView;
		parentEvent = e;
		init();
		return menu;
	}

	/**
	 * Initialization of context menu.
	 * 
	 */
	private void init() {
		testEditView.setStyledTextContextMenu(menu);
		/*
		 * create the menu for add description and add action
		 */
		createSpecialMenu(menu);
		// Create the first separator
		new MenuItem(menu, SWT.SEPARATOR);

		createMenuCutCopyPaste(menu);

		// Create the second separator
		new MenuItem(menu, SWT.SEPARATOR);

		MenuItem itemDel = new MenuItem(menu, SWT.PUSH);
		itemDel.setImage(IconConstants.ICON_DELETE);
		String messageDel = testEditView.translate("%TestEditViewContext_deleteLine");
		itemDel.setText(messageDel);

		itemDel.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				testEditView.getEventBroker().send(TestEditorUIEventConstants.EDIT_CONTEXTMENU_DEL, "");
			}

		});
		itemDel.setEnabled(testEditView.isSelectionEditable(testEditView.getClickedLineInView(),
				testEditView.getReleasedLineInView()));
		itemDel.setData(CustomWidgetIdConstants.TEST_EDITOR_WIDGET_ID_SWT_BOT_KEY,
				CustomWidgetIdConstants.TEST_FLOW_VIEW_DELETE_LINE);

		menu.setLocation(parentEvent.x, parentEvent.y);
		menu.setVisible(true);
		while (!menu.isDisposed() && menu.isVisible()) {
			if (!menu.getDisplay().readAndDispatch()) {
				menu.getDisplay().sleep();
			}
		}

		menu.dispose();

	}

	/**
	 * creates the menu for add description and add action.
	 * 
	 * @param menu
	 *            the contextmenu
	 */
	private void createSpecialMenu(Menu menu) {

		MenuItem itemEditLine = new MenuItem(menu, SWT.PUSH);
		itemEditLine.setImage(IconConstants.ICON_EDIT_LINE);
		String messageEditLine = testEditView.translate("%TestEditViewContext_editLine");

		itemEditLine.setText(messageEditLine);
		itemEditLine.setEnabled(testEditView.isLineEditable(testEditView.getClickedLineInView()));

		itemEditLine.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				int lineInTestCase = testEditView.getCodeLineMapper().getContentOfOffsetViewToTestListAt(
						testEditView.getClickedLineInView());
				try {
					testEditView.showSelectedContentInInputArea(lineInTestCase);
				} catch (SystemException e) {
					MessageDialog.openError(Display.getCurrent().getActiveShell(), "System-Exception", e.getMessage());
					LOGGER.error("error by getting the scenario", e);
				}
			}
		});

		MenuItem itemDesc = new MenuItem(menu, SWT.PUSH);
		itemDesc.setImage(IconConstants.ICON_DESCRIPTION);
		String message = testEditView.translate("%TestEditViewContext_addDescription");

		itemDesc.setText(message);

		itemDesc.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				testEditView.getEventBroker().send(TestEditorUIEventConstants.EDIT_CONTEXTMENU_F7, "");
				// openDialogForF7();

			}
		});
		MenuItem itemAction = new MenuItem(menu, SWT.PUSH);
		itemAction.setImage(IconConstants.ICON_ACTION);
		String messageAction = testEditView.translate("%TestEditViewContext_addAction");
		itemAction.setText(messageAction);
		itemAction.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				testEditView.getEventBroker().send(TestEditorUIEventConstants.EDIT_CONTEXTMENU_F8, "");
				// openDialogForF8();
			}
		});
	}

	/**
	 * creates the menu for cut, copy and paste operation.
	 * 
	 * @param menu
	 *            the contextmenu
	 */
	private void createMenuCutCopyPaste(Menu menu) {
		MenuItem itemCut = new MenuItem(menu, SWT.PUSH);
		itemCut.setImage(IconConstants.ICON_CUT);
		String messageCut = testEditView.translate("%TestEditViewContext_cut");
		itemCut.setText(messageCut);

		itemCut.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				testEditView.getEventBroker().send(TestEditorUIEventConstants.EDIT_CONTEXTMENU_CNTRL_X, "");
			}
		});
		itemCut.setEnabled(testEditView.canExecuteCutCopy());

		MenuItem itemCopy = new MenuItem(menu, SWT.PUSH);
		itemCopy.setImage(IconConstants.ICON_COPY);
		String messageCopy = testEditView.translate("%TestEditViewContext_copy");
		itemCopy.setText(messageCopy);

		itemCopy.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				testEditView.getEventBroker().send(TestEditorUIEventConstants.EDIT_CONTEXTMENU_CNTRL_C, "");
			}
		});
		itemCopy.setEnabled(testEditView.canExecuteCutCopy());

		MenuItem itemPaste = new MenuItem(menu, SWT.PUSH);
		itemPaste.setImage(IconConstants.ICON_PASTE);
		String messagePaste = testEditView.translate("%TestEditViewContext_paste");
		itemPaste.setText(messagePaste);

		itemPaste.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				testEditView.getEventBroker().send(TestEditorUIEventConstants.EDIT_CONTEXTMENU_CNTRL_V, "");
			}
		});
		itemPaste.setEnabled(testEditView.isPastePossible());
	}
}
