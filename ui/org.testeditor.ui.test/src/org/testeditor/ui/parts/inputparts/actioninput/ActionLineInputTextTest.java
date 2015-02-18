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
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Test;

/**
 * 
 * tests the ActionLineInputText.
 * 
 * @author llipinski
 */
public class ActionLineInputTextTest {

	private Shell shell = new Shell();
	private ActionLineInputText wrappedText = new ActionLineInputText(shell, SWT.NONE);

	/**
	 * test the showText method.
	 */
	@Test
	public void showText() {
		String text = "TEST ";
		wrappedText.showText(text);
		assertEquals(text.substring(0, text.length() - 1), wrappedText.getInputText());
	}

	/**
	 * test the setText-method.
	 */
	@Test
	public void setText() {
		String text = "TEST ";
		wrappedText.setText(text);
		assertEquals(text, wrappedText.getInputText());
		assertEquals(text, wrappedText.getText());
	}

	/**
	 * test the dispose-method.
	 */
	@Test
	public void dispose() {

		assertFalse(wrappedText.isDisposed());
		wrappedText.dispose();
		assertTrue(wrappedText.isDisposed());
		wrappedText = new ActionLineInputText(shell, SWT.NONE);
	}

	/**
	 * test the setBounds-method.
	 */
	@Test
	public void setBounds() {
		Rectangle rectangle = new Rectangle(1, 2, 10, 20);

		wrappedText.setBounds(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
		assertEquals(rectangle.x, wrappedText.getBounds().x);
		assertEquals(rectangle.y, wrappedText.getBounds().y);
		assertEquals(rectangle.width, wrappedText.getBounds().width);
		assertEquals(rectangle.height, wrappedText.getBounds().height);

	}

	/**
	 * test the isInputValid-method.
	 */
	@Test
	public void isInputValid() {
		assertFalse(wrappedText.isInputValid());
		wrappedText.setText("Test");
		assertTrue(wrappedText.isInputValid());
	}

	/**
	 * test the getArgument-method.
	 */
	@Test
	public void getArgument() {
		String text = "§786%&";
		wrappedText.setText(text);
		assertEquals(text, wrappedText.getArgument().getValue());
	}

	/**
	 * dispose the shell after the tests.
	 */
	@After
	public void disposeShell() {
		shell.dispose();
	}

}
