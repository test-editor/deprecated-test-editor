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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.FrameworkUtil;
import org.testeditor.core.model.teststructure.TestCase;
import org.testeditor.core.model.teststructure.TestDescriptionTestCase;
import org.testeditor.core.model.teststructure.TestFlow;
import org.testeditor.ui.adapter.PartServiceAdapter;
import org.testeditor.ui.adapter.TestEditorControllerAdapter;
import org.testeditor.ui.parts.editor.ITestEditorController;
import org.testeditor.ui.utilities.TestEditorTranslationService;

/**
 * Integration tests for TestEditViewBasis.
 * 
 */
public class TestEditViewBasisTest {

	private TestEditorViewBasis testEditViewBasis;
	private Shell shell;

	/**
	 * Tests the setting of a focus in a sub component.
	 * 
	 * @throws Exception
	 *             if test fails
	 */
	@Test
	public void testSetFocusInSubComponent() throws Exception {

		ITestEditorController testCaseController = new TestEditorControllerAdapter() {
			@Override
			public TestFlow getTestFlow() {
				TestCase testCase = new TestCase();
				return testCase;
			}
		};

		testEditViewBasis.setTestCaseController(testCaseController);
		testEditViewBasis.createUI(shell);
		TestEditorViewTableViewer tableViewer = new TestEditorViewTableViewer();
		testEditViewBasis.setFocusInSubComponent(tableViewer);

		assertTrue(testEditViewBasis.isFocusInSubComponent());
	}

	/**
	 * Creates a styled text to test if parts of it would be deletable.
	 * 
	 * @throws Exception
	 *             if test fails
	 */
	@Test
	public void testCanExecuteDelete() throws Exception {
		ITestEditorController testCaseController = new TestEditorControllerAdapter() {
			@Override
			public boolean isSelectionEditable(int firstLine, int lastLine) {
				return true;
			}
		};

		testEditViewBasis.setTestCaseController(testCaseController);
		testEditViewBasis.createUI(shell);

		testEditViewBasis.getStyledText().setText("Dies ist ein CanExecuteDelete test.");
		testEditViewBasis.getStyledText().setSelection(0, 5);

		assertTrue(testEditViewBasis.canExecuteDelete());
	}

	/**
	 * Creates a styled text to test if parts of it would be cutable.
	 * 
	 * @throws Exception
	 *             if test fails
	 */
	@Test
	public void testCanExecuteCutCopy() throws Exception {
		ITestEditorController testCaseController = new TestEditorControllerAdapter() {
			@Override
			public boolean isSelectionEditable(int firstLine, int lastLine) {
				return true;
			}
		};

		testEditViewBasis.setTestCaseController(testCaseController);
		testEditViewBasis.createUI(shell);

		testEditViewBasis.getStyledText().setText("Dies ist ein CanExecuteCutCopy test.");
		testEditViewBasis.getStyledText().setSelection(0, 5);

		assertTrue(testEditViewBasis.canExecuteCutCopy());
	}

	/**
	 * Runs pasteTestComponents().
	 * 
	 * @throws Exception
	 *             if test fails
	 */
	@Test
	public void testPasteTestComponents() throws Exception {
		testEditViewBasis.createUI(shell);

		testEditViewBasis.getStyledText().setText("Dies ist ein PasteTestComponents test.");
		testEditViewBasis.pasteTestComponents("");
	}

	/**
	 * Test the logic of isLineEmpty.
	 * 
	 */
	@Test
	public void testIsLineEmpty() {
		testEditViewBasis.createUI(shell);
		testEditViewBasis.getStyledText().setText("");
		assertTrue(testEditViewBasis.isLineEmpty(0));
		testEditViewBasis.getStyledText().setText("first Line\n\nThridLine");
		assertFalse(testEditViewBasis.isLineEmpty(0));
		assertTrue(testEditViewBasis.isLineEmpty(1));
		assertFalse(testEditViewBasis.isLineEmpty(2));
	}

	/**
	 * a test for put and get the TestEditorTestDataTransferContainer.
	 */
	@Test
	public void putDataToClipboardTest() {
		TestEditorTestDataTransferContainer testEditorTestDataTransferContainer = new TestEditorTestDataTransferContainer();
		String projectName = "MyProject";
		testEditorTestDataTransferContainer.setTestProjectName(projectName);
		TestDescriptionTestCase testDescriptionTestCase = new TestDescriptionTestCase("One Description");
		testEditorTestDataTransferContainer.setStoredTestComponents(testDescriptionTestCase.getSourceCode());
		Display display = Display.getDefault();
		if (display == null) {
			fail();
		}
		testEditViewBasis.setClipboard(new Clipboard(display));
		testEditViewBasis.putDataToClipboard(testEditorTestDataTransferContainer);
		assertEquals(projectName, testEditViewBasis.getTestFlowTranfer().getTestProjectName());
		assertEquals(testDescriptionTestCase.getSourceCode(), testEditViewBasis.getTestFlowTranfer()
				.getStoredTestComponents());
	}

	/**
	 * Creating new Shell.
	 */
	@Before
	public void setUP() {
		shell = new Shell();
		IEclipseContext context = EclipseContextFactory.getServiceContext(FrameworkUtil.getBundle(
				TestEditorViewKeyHandler.class).getBundleContext());
		context.set(Logger.class, null);
		context.set(TestEditorTranslationService.class, new TestEditorTranslationService() {
			@Override
			public String translate(String key, Object... params) {
				return key;
			};
		});
		context.set(EPartService.class, new PartServiceAdapter());

		testEditViewBasis = ContextInjectionFactory.make(TestEditView.class, context);
		testEditViewBasis.setTestCaseController(new TestEditorControllerAdapter());
	}

	/**
	 * Destroying Shell.
	 */
	@After
	public void tearDown() {
		shell.dispose();
	}
}
