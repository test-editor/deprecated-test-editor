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
import static org.junit.Assert.assertTrue;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Test;

/**
 * 
 * test for the ActionLineInputLabel.
 * 
 * @author llipinski
 */
public class ActionLineInputLabelTest {

	private Shell shell = new Shell();
	private ActionLineInputLabel wrappedLabel = new ActionLineInputLabel(shell, SWT.NONE);

	/**
	 * test the showText-method.
	 */
	@Test
	public void showText() {
		String text = "Test";
		wrappedLabel.showText(text);
		assertEquals(null, wrappedLabel.getText());
		assertEquals("", wrappedLabel.getInputText());
	}

	/**
	 * test the setText-, getText- and getInputText-method.
	 */
	@Test
	public void setText() {
		String text = "Test";
		wrappedLabel.setText(text);
		assertEquals(null, wrappedLabel.getText());
		assertEquals(text, wrappedLabel.getInputText());
	}

	/**
	 * test the isInputValid-method.
	 */
	@Test
	public void isInputValid() {
		String text = "Test";
		wrappedLabel.setText(text);
		assertTrue(wrappedLabel.isInputValid());
	}

	/**
	 * test the getArgument-method.
	 */
	@Test
	public void getArgument() {
		assertEquals(null, wrappedLabel.getArgument());
	}

	/**
	 * dispose the shell after the tests.
	 */
	@After
	public void dispose() {
		shell.dispose();
	}

}
