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
package org.testeditor.ui.reporting;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.model.testresult.TestResult;
import org.testeditor.core.model.teststructure.TestCase;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.services.interfaces.TestStructureService;
import org.testeditor.ui.adapter.TestStructureServiceAdapter;
import org.testeditor.ui.utilities.TestEditorTranslationService;

/**
 * 
 * Integrationtests for the TestExecutionProgressDialog.
 * 
 */
public class TestExecutionProgressDialogTest {

	private Shell shell;
	private TestExecutionProgressDialog testExecutionDialog;

	/**
	 * Test the Execution of a Teststructure.
	 * 
	 * @throws Exception
	 *             for test
	 */
	@Test
	@Ignore
	public void testExecuteTest() throws Exception {
		TestStructure test = new TestCase();
		TestResult testResult = testExecutionDialog.executeTest(test);
		assertNotNull("Notnull for testresult", testResult);
	}

	/**
	 * Test the detail switcher selection listener.
	 * 
	 */
	@Test
	public void testSwitchDetailSelectionListener() {
		testExecutionDialog.createDialogArea(shell);
		Composite composite = new Composite(shell, SWT.NORMAL);
		composite.setLayout(new GridLayout());
		testExecutionDialog.createButtonsForButtonBar(composite);
		Point originalSize = shell.getSize();
		SelectionListener listener = testExecutionDialog.getSwitchDetailSelectionListener();
		listener.widgetSelected(null);
		assertNotEquals(originalSize, shell.getSize());
		listener.widgetSelected(null);
		assertEquals(originalSize, shell.getSize());
	}

	/**
	 * Creates the UI Ressources for the test.
	 */
	@Before
	public void setUp() {
		shell = new Shell(Display.getDefault());
		IEclipseContext context = EclipseContextFactory.create();
		context.set(Shell.class, shell);
		context.set("ActualTCService", getTestStructureServiceMock());
		context.set(IEventBroker.class, null);
		context.set(TestStructureService.class, null);
		context.set(TestEditorTranslationService.class, new TestEditorTranslationService() {
			@Override
			public String translate(String key, Object... params) {
				return "";
			}
		});
		testExecutionDialog = ContextInjectionFactory.make(TestExecutionProgressDialog.class, context);
	}

	/**
	 * Disposes UI Resources after test.
	 */
	@After
	public void tearDown() {
		shell.dispose();
	}

	/**
	 * Creates a TestStructureService Mock Object. CHECKSTYLE:OFF
	 * 
	 * @return Mock for the Test
	 */
	private TestStructureService getTestStructureServiceMock() {
		TestStructureServiceAdapter testStructureServiceAdapter = new TestStructureServiceAdapter() {
			@Override
			public TestResult executeTestStructure(TestStructure testStructure, IProgressMonitor monitor)
					throws SystemException {

				return new TestResult();
			}
		};
		testStructureServiceAdapter.setEmptyVariable(true);
		return testStructureServiceAdapter;
	}

}
