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
package org.testeditor.ui.parts.editor;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * 
 * Module Tests for TestExecutionProgressEventHandler.
 * 
 * @author karsten
 */
public class TestExecutionProgressEventHandlerTest {

	private static final String OPEN_BROWSER = "openBrowser ( \"Firefox\",\"/home/user/tools/browser\" )";
	private static final String NAVIGATE = "navigateToUrl ( \"http://localhost:8060/files/demo/ExampleApplication/WebApplicationDe/index.html\" )";
	private static final String SRC_FOR_OPEN_BROWSER = "2013-07-08 18:20:33,914 [DEBUG]   TestEditorLoggingInteraction  Methode : openBrowser ( \"Firefox\",\"/home/user/tools/browser\" )";
	private static final String SRC_FOR_NAVIGATE = "2013-07-08 18:20:35,573 [DEBUG]   TestEditorLoggingInteraction  Methode : navigateToUrl ( \"http://localhost:8060/files/demo/ExampleApplication/WebApplicationDe/index.html\" )";

	/**
	 * Test a Simple Extraction of the Technical Binding Statement from a Log
	 * Message.
	 * 
	 * @throws Exception
	 *             for test
	 */
	@Test
	public void testExtractTechnicalBindingSimpleFromLogMessage() throws Exception {
		TestExecutionProgressEventHandler progressEventHandler = new TestExecutionProgressEventHandler();
		assertEquals(OPEN_BROWSER, progressEventHandler.extractTechnicalBindingStatementFromLine(SRC_FOR_OPEN_BROWSER));
		assertEquals(NAVIGATE, progressEventHandler.extractTechnicalBindingStatementFromLine(SRC_FOR_NAVIGATE));
	}
}
