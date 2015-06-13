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
package org.testeditor.ui.parts.testhistory;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.Test;

/**
 * 
 * Integration Tests for TestExecutionResultViewPart.
 *
 */
public class TestExecutionResultViewPartTest {

	/**
	 * Tests the creation of the TestExecutionViewPart with initial UI widgets.
	 * 
	 * @throws Exception
	 *             on Testfailure
	 */
	@Test
	public void testCreateInit() throws Exception {
		TestExecutionResultViewPart testExecutionResultViewPart = new TestExecutionResultViewPart();
		Shell shell = new Shell(Display.getDefault());
		testExecutionResultViewPart.createUi(shell);
		testExecutionResultViewPart.setTestResultURL("foo");
		shell.dispose();
	}

}
