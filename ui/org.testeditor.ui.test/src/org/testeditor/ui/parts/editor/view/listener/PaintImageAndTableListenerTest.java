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

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.junit.Before;

/**
 * 
 * test for the {@link PaintImageAndTableListener}.
 * 
 * @author llipinski
 */
public class PaintImageAndTableListenerTest {

	private Shell shell;
	private Composite composite;
	private PaintImageAndTableListener paintImageAndTableListener;

	/**
	 * initialize before the tests.
	 */
	@Before
	public void init() {
		shell = new Shell();
		composite = new Composite(shell, SWT.NORMAL);

		paintImageAndTableListener = new PaintImageAndTableListener(new StyledText(composite, SWT.NONE));

	}

	/**
	 * tests the addTable method.
	 * 
	 */
	// public void addTable() {
	// int tableOffset = 400;
	// paintImageAndTableListener.addTable(new Table(composite, SWT.BORDER),
	// tableOffset);
	// paintImageAndTableListener.disposeAllTables();
	// }
}
