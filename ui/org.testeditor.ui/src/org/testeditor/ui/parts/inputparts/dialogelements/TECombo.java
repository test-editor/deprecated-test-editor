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
package org.testeditor.ui.parts.inputparts.dialogelements;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.fieldassist.ComboContentAdapter;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.IControlContentAdapter;
import org.eclipse.jface.fieldassist.SimpleContentProposalProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.testeditor.core.model.action.Argument;
import org.testeditor.ui.parts.inputparts.actioninput.ActionLineTextContainsInvalidText;
import org.testeditor.ui.parts.inputparts.actioninput.IActionLineInputWidget;

/**
 * 
 * This class is a wrapper around the SWT.Combo it implements
 * IActionLineInputWidget. So there are less instanceof operations necessary.
 * 
 */

public class TECombo extends ActionLineTextContainsInvalidText implements IActionLineInputWidget {

	private Combo wrappedCombo;
	private Map<String, Argument> arguments = new HashMap<String, Argument>();

	private static final String LCL = "abcdefghijklmnopqrstuvwxyz";
	private static final String UCL = LCL.toUpperCase();
	private static final String NUMS = "0123456789";
	private SimpleContentProposalProvider proposalProvider;
	private ContentProposalAdapter proposalAdapter;

	private String eventTopic = "";

	private IEventBroker eventBroker;

	// will be set on true if TECombo contains ACTION_NAMES
	private boolean containsActionNames = false;

	/**
	 * constructor.
	 * 
	 * @param parent
	 *            composite
	 * @param style
	 *            STW.Style
	 * @param eventTopic
	 *            topic for the change-event
	 * @param eventBroker
	 *            the eventBroker
	 */
	public TECombo(Composite parent, int style, String eventTopic, IEventBroker eventBroker) {
		wrappedCombo = new Combo(parent, style);
		this.eventTopic = eventTopic;
		this.eventBroker = eventBroker;
	}

	/**
	 * enable the content-proposal.
	 * 
	 */
	public void enableContentProposal() {
		if (wrappedCombo == null) {
			return;
		}
		if (proposalAdapter == null) {
			proposalProvider = new SimpleContentProposalProvider(wrappedCombo.getItems());
			proposalProvider.setFiltering(true);
			proposalAdapter = new ContentProposalAdapter(wrappedCombo,
					getComoboContentAdapterWithFireSelectionChangeEvenet(), proposalProvider, getActivationKeystroke(),
					getAutoactivationChars());
			proposalAdapter.setPropagateKeys(true);
			proposalAdapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);
		} else {
			proposalProvider.setProposals(wrappedCombo.getItems());
		}
	}

	/**
	 * 
	 * @return special ComboContentAdapter that calls the
	 *         fireSelectionChangeEvnt method.
	 */
	private IControlContentAdapter getComoboContentAdapterWithFireSelectionChangeEvenet() {
		return new ComboContentAdapter() {
			@Override
			public void setControlContents(Control control, String text, int cursorPosition) {
				super.setControlContents(control, text, cursorPosition);
				fireSelectionChangeEvent();
			}
		};
	}

	/**
	 * 
	 * @return the autoactivation-characters.
	 */
	private static char[] getAutoactivationChars() {

		// To enable content proposal on deleting a char

		String delete = new String(new char[] { 8 });
		String allChars = LCL + UCL + NUMS + delete;
		return allChars.toCharArray();
	}

	/**
	 * 
	 * @return the activationKeystroke.
	 */

	private static KeyStroke getActivationKeystroke() {
		return KeyStroke.getInstance(SWT.CTRL, Integer.valueOf(' ').intValue());
	}

	@Override
	public void showText(String text) {

		for (int j = 0; j < wrappedCombo.getItemCount(); j++) {
			if ((wrappedCombo.getItem(j).toString() + " ").equalsIgnoreCase(text)) {
				wrappedCombo.select(j);
				return;
			}
		}
		if (text.length() > 0) {
			wrappedCombo.setText(text.substring(0, text.length() - 1));
		}
	}

	@Override
	public String getInputText() {
		String comboText = wrappedCombo.getText();
		if (arguments.containsKey(comboText)) {
			if (arguments.get(comboText).getLocator() != null) {
				return arguments.get(comboText).getLocator();
			}
		}

		return comboText;
	}

	@Override
	public String getText() {
		if (!wrappedCombo.isDisposed()) {
			return wrappedCombo.getText();
		}
		return "";
	}

	/**
	 * 
	 * @return the selected argument
	 */
	private Argument getSelectedArgument() {
		return arguments.get(getText());
	}

	@Override
	public void dispose() {
		wrappedCombo.dispose();
	}

	/**
	 * wrapper around the method add(Argument) from the private SWT.Combo.
	 * 
	 * @param argument
	 *            Argument
	 */
	public void add(Argument argument) {
		wrappedCombo.add(argument.getValue());
		arguments.put(argument.getValue(), argument);
	}

	/**
	 * wrapper around the method add(String) from the private SWT.Combo.
	 * 
	 * @param text
	 *            String
	 */
	public void add(String text) {
		wrappedCombo.add(text);
	}

	/**
	 * wrapper around the method getItemCount() from the private SWT.Combo.
	 * 
	 * @return int the count
	 * 
	 */
	public int getItemCount() {
		return wrappedCombo.getItemCount();
	}

	/**
	 * wrapper around the method select(int) from the private SWT.Combo.
	 *
	 * @param sel
	 *            the selection
	 *
	 */
	public void select(int sel) {
		wrappedCombo.select(sel);
	}

	/**
	 * 
	 * wrapper around the method setVisible(boolean) from the private SWT.Combo.
	 * 
	 * @param vis
	 *            boolean visible
	 */
	public void setVisible(boolean vis) {
		wrappedCombo.setVisible(vis);
		if (vis) {
			wrappedCombo.getParent().layout(true);
		}
	}

	/**
	 * 
	 * wrapper around the method setForeground(Color) from the private
	 * SWT.Combo.
	 * 
	 * @param color
	 *            Color of the foreground
	 */
	public void setForeground(Color color) {
		wrappedCombo.setForeground(color);
	}

	/**
	 * wrapper around the method setFocus() from the private SWT.Combo.
	 */
	@Override
	public void setFocus() {
		wrappedCombo.setFocus();
	}

	/**
	 * wrapper around the method addFocusListener(FocusListener) from the
	 * private SWT.Combo.
	 * 
	 * @param listener
	 *            the FocusListener
	 */
	public void addFocusListener(FocusListener listener) {
		wrappedCombo.addFocusListener(listener);
	}

	/**
	 * Adds the listener to the collection of listeners who will be notified
	 * when the receiver's text is modified, by sending it one of the messages
	 * defined in the ModifyListener interface.
	 * 
	 * @param listener
	 *            the ModifyListener
	 */
	public void addModifyListener(ModifyListener listener) {
		wrappedCombo.addModifyListener(listener);
	}

	/**
	 * Adds the listener to the collection of listeners who will be notified
	 * when the receiver's text is modified, by sending it one of the messages
	 * defined in the ModifyListener interface.
	 * 
	 * @param listener
	 *            the ModifyListener
	 */
	@Override
	public void addKeyListener(KeyListener listener) {
		wrappedCombo.addKeyListener(listener);
	}

	/**
	 * Returns the zero-relative index of the item which is currently selected
	 * in the receiver's list, or -1 if no item is selected.
	 * 
	 * @return the index of the selected item
	 */
	public int getSelectionIndex() {
		return wrappedCombo.getSelectionIndex();
	}

	/**
	 * Returns true if the widget has been disposed, and false otherwise.
	 * 
	 * This method gets the dispose state for the widget. When a widget has been
	 * disposed, it is an error to invoke any other method (except dispose())
	 * using the widget.
	 * 
	 * @return true when the widget is disposed and false otherwise
	 */
	@Override
	public boolean isDisposed() {
		return wrappedCombo.isDisposed();
	}

	@Override
	public boolean isInputValid() {
		return !getText().isEmpty() && (indexOf(getText()) != -1) && !containsTextInvalidChar(getText());
	}

	/**
	 * Removes all of the items from the receiver's list and clear the contents
	 * of receiver's text field.
	 */
	public void removeAll() {

		if (wrappedCombo != null && !wrappedCombo.isDisposed() && wrappedCombo.getItemCount() > 0) {
			wrappedCombo.removeAll();
		}
	}

	/**
	 * Adds the listener to the collection of listeners who will be notified
	 * when the user changes the receiver's selection, by sending it one of the
	 * messages defined in the SelectionListener interface.
	 * 
	 * widgetSelected is called when the user changes the combo's list
	 * selection. widgetDefaultSelected is typically called when ENTER is
	 * pressed the combo's text area.
	 * 
	 * 
	 * @param selectionListener
	 *            the listener which should be notified
	 */
	public void addSelectionListener(SelectionListener selectionListener) {
		wrappedCombo.addSelectionListener(selectionListener);
	}

	/**
	 * Searches the receiver's list starting at the first item (index 0) until
	 * an item is found that is equal to the argument, and returns the index of
	 * that item. If no item is found, returns -1.
	 * 
	 * 
	 * @param text
	 *            the search item
	 * 
	 * @return the search item
	 */
	public int indexOf(String text) {
		return wrappedCombo.indexOf(text);
	}

	/**
	 * Returns the item at the given, zero-relative index in the receiver's
	 * list. Throws an exception if the index is out of range.
	 * 
	 * 
	 * @param index
	 *            the index of the item to return
	 * 
	 * @return the index of the item to return
	 */
	public String getItem(int index) {
		return wrappedCombo.getItem(index);
	}

	/**
	 * clear the selection in the combobox.
	 */
	public void clearSelection() {
		wrappedCombo.deselectAll();
		wrappedCombo.clearSelection();
		wrappedCombo.setText("");
		wrappedCombo.redraw();
	}

	@Override
	public void setData(Object data) {
		wrappedCombo.setData(data);
	}

	@Override
	public void setData(String key, Object value) {
		wrappedCombo.setData(key, value);
	}

	/**
	 * @param key
	 *            the key for the data
	 * @return the data-Object from the widget
	 */
	public Object getData(String key) {
		return wrappedCombo.getData(key);
	}

	@Override
	public Argument getArgument() {
		return getSelectedArgument();
	}

	@Override
	public boolean setCursor(int posInWidget) {
		return false;
	}

	@Override
	public boolean isInputField() {
		return false;
	}

	@Override
	public int getCursorPos() {
		return -1;
	}

	/**
	 * sends a selectionChangeEvent for the combobox.
	 */
	public void fireSelectionChangeEvent() {
		if (!eventTopic.equalsIgnoreCase("")) {
			eventBroker.send(eventTopic, this.getText());
		}
	}

	// CHECKSTYLE:OFF
	public void setContainsActionNames(boolean containsActionNames) {
		this.containsActionNames = containsActionNames;

	}

	public boolean containsActionNames() {
		return this.containsActionNames;
	}

	// CHECKSTYLE:ON

}