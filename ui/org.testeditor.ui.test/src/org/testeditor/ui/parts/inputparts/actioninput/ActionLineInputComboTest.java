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
package org.testeditor.ui.parts.inputparts.actioninput;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testeditor.core.model.action.Argument;
import org.testeditor.ui.constants.CustomWidgetIdConstants;
import org.testeditor.ui.mocks.EventBrokerMock;
import org.testeditor.ui.parts.inputparts.dialogelements.TECombo;

/**
 * This test the actionLineInputCombo class.
 * 
 */
public class ActionLineInputComboTest {

	private Shell shell = new Shell();
	private TECombo actionLineInputCombo;

	/**
	 * 
	 */
	@Before
	public void testInit() {
		actionLineInputCombo = new TECombo(shell, 1, "", new EventBrokerMock());
	}

	/**
	 * This test the Add(String) method.
	 */
	@Test
	public void testAddString() {
		actionLineInputCombo.add("String");
		assertEquals(actionLineInputCombo.getItemCount(), 1);
		assertEquals("String", actionLineInputCombo.getItem(0));
	}

	/**
	 * This test the Add(Argument) method.
	 */
	@Test
	public void testAddArgument() {
		Argument argument = new Argument();
		argument.setValue("Argument");
		actionLineInputCombo.add(argument);
		assertEquals(actionLineInputCombo.getItemCount(), 1);
	}

	/**
	 * This test the getArgument() methode.
	 */
	@Test
	public void testGetArgument() {
		Argument argument = new Argument();
		argument.setValue("Argument");
		actionLineInputCombo.add(argument);
		actionLineInputCombo.select(0);
		assertEquals(actionLineInputCombo.getArgument(), argument);
	}

	/**
	 * This test the getItemCount() method.
	 */
	@Test
	public void testGetItemCountt() {
		Argument argument = new Argument();
		argument.setValue("Argument");
		actionLineInputCombo.add(argument);
		actionLineInputCombo.add("String");
		assertEquals(actionLineInputCombo.getItemCount(), 2);
	}

	/**
	 * This test the removeAll() method.
	 */
	@Test
	public void testRemoveAll() {
		Argument argument = new Argument();
		argument.setValue("Argument");
		actionLineInputCombo.add(argument);
		actionLineInputCombo.add("String");
		actionLineInputCombo.removeAll();
		assertEquals(actionLineInputCombo.getItemCount(), 0);
	}

	/**
	 * This test the getSelectionIndex() method.
	 */
	@Test
	public void testGetSelectionIndex() {
		actionLineInputCombo.add("String");
		actionLineInputCombo.add("String_line_1");
		actionLineInputCombo.add("String_line_2");
		actionLineInputCombo.select(1);
		assertEquals(1, actionLineInputCombo.getSelectionIndex());
	}

	/**
	 * This test the indexOf() method.
	 */
	@Test
	public void testIndexOf() {
		actionLineInputCombo.add("String");
		actionLineInputCombo.add("test");
		actionLineInputCombo.add("test2");
		assertEquals(1, actionLineInputCombo.indexOf("test"));
	}

	/**
	 * This test the showText() method.
	 */
	@Test
	public void testShowText() {
		actionLineInputCombo.add("String");
		actionLineInputCombo.add("test");
		actionLineInputCombo.add("test2");
		actionLineInputCombo.showText("test ");
		assertEquals(1, actionLineInputCombo.getSelectionIndex());
	}

	/**
	 * This test the testClearSelection() method.
	 */
	@Test
	public void testClearSelection() {
		actionLineInputCombo.add("String");
		actionLineInputCombo.add("test");
		actionLineInputCombo.add("test2");
		actionLineInputCombo.select(2);
		assertEquals(2, actionLineInputCombo.getSelectionIndex());
		actionLineInputCombo.clearSelection();
		assertEquals("", actionLineInputCombo.getText());
	}

	/**
	 * test getInputText().
	 */
	@Test
	public void testGetInputText() {
		actionLineInputCombo.add("String");
		actionLineInputCombo.add("test");
		actionLineInputCombo.add("test2");
		actionLineInputCombo.select(2);
		assertEquals("test2", actionLineInputCombo.getInputText());
	}

	/**
	 * test setData().
	 */
	@Test
	public void testSetData() {
		int position = 1;
		String key = CustomWidgetIdConstants.TEST_EDITOR_WIDGET_ID_SWT_BOT_KEY;
		String data = CustomWidgetIdConstants.ACTION_COMBO + position;
		actionLineInputCombo.setData(key, data);
		assertEquals(data, actionLineInputCombo.getData(key));
	}

	/**
	 * tests the method isInoputValide(). no items no selection
	 * 
	 */
	@Test
	public void testIsInputValide() {
		assertFalse(actionLineInputCombo.isInputValid());

	}

	/**
	 * tests the method isInoputValide(). no selection
	 * 
	 */

	@Test
	public void testIsInputValideWithItems() {
		actionLineInputCombo.add("String");
		actionLineInputCombo.add("test");
		actionLineInputCombo.add("test2");
		assertFalse(actionLineInputCombo.isInputValid());

	}

	/**
	 * tests the method isInoputValide().
	 * 
	 */

	@Test
	public void testIsInputValideWithItemsAndSelection() {
		actionLineInputCombo.add("String");
		actionLineInputCombo.add("test");
		actionLineInputCombo.add("test2");
		actionLineInputCombo.select(1);
		assertTrue(actionLineInputCombo.isInputValid());

	}

	/**
	 * test dispose and isDisposed methods.
	 * 
	 * @return
	 */
	@Test
	public void testDisposed() {
		assertFalse(actionLineInputCombo.isDisposed());
		actionLineInputCombo.dispose();
		assertTrue(actionLineInputCombo.isDisposed());
		actionLineInputCombo = new TECombo(shell, SWT.NONE, "", new EventBrokerMock());
	}

	/**
	 * dispose the shell after the tests.
	 */
	@After
	public void dispose() {
		shell.dispose();
	}

}
