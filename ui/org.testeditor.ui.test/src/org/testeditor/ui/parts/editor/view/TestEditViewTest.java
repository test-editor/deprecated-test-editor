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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.FrameworkUtil;
import org.testeditor.core.model.action.TextType;
import org.testeditor.core.model.teststructure.TestActionGroup;
import org.testeditor.core.model.teststructure.TestCase;
import org.testeditor.core.model.teststructure.TestComponent;
import org.testeditor.core.model.teststructure.TestDescriptionTestCase;
import org.testeditor.core.model.teststructure.TestFlow;
import org.testeditor.ui.adapter.PartServiceAdapter;
import org.testeditor.ui.adapter.TestEditorControllerAdapter;
import org.testeditor.ui.constants.TestEditorUIEventConstants;
import org.testeditor.ui.parts.editor.ITestEditorController;
import org.testeditor.ui.utilities.TestEditorTranslationService;

/**
 * 
 * Instance tests for TestEditView.
 * 
 */
public class TestEditViewTest {

	private TestEditView testEditView;
	private Shell shell;

	/**
	 * Test for predestroy().
	 * 
	 * @throws Exception
	 *             Injection Exception if make fails
	 */
	@Test
	public void testPredestroy() throws Exception {

		ITestEditorController testCaseController = new TestEditorControllerAdapter() {
			@Override
			public void removeTestEditView(TestEditView aTestEditView) {
				assertSame("Expecting remove of the TestEditView Reference of this test.", testEditView, aTestEditView);
			}
		};

		testEditView.setTestCaseController(testCaseController);

		testEditView.createUI(shell);

		testEditView.preDestroy();
	}

	/**
	 * Test if KeyHandler is created.
	 */
	@Test
	public void testInitKeyHandler() {
		testEditView.createUI(shell);
		assertNotNull(testEditView.getKeyHandler());
	}

	/**
	 * Tests if KeyEvent arrives in TestEditView.
	 * 
	 * @throws Exception
	 *             if test fails
	 */
	@Test
	public void testCreateNewTestComponentKeyEventHandling() throws Exception {

		testEditView.createUI(shell);

		IEventBroker eventBroker = testEditView.getEventBroker();
		assertNull(testEditView.getPopupDialogDescription());
		// In The PDEJunit Context the Event message works.
		// In the Maven tycho build context the event doesn't work, for that
		// case the method is called directly.
		eventBroker.send(TestEditorUIEventConstants.EDIT_CONTEXTMENU_F7, "");
		testEditView.showDescriptionPopupDialog();
		assertNotNull(testEditView.getPopupDialogDescription());

		eventBroker = testEditView.getEventBroker();
		assertNull(testEditView.getActionGroupPopupDialog());
		// In The PDEJunit Context the Event message works.
		// In the Maven tycho build context the event doesn't work, for that
		// case the method is called directly.
		eventBroker.send(TestEditorUIEventConstants.EDIT_CONTEXTMENU_F8, "");
		testEditView.showActionGroupPopupDialog();
		assertNotNull(testEditView.getActionGroupPopupDialog());
	}

	/**
	 * Testing selecting lines in popup.
	 * 
	 * @throws Exception
	 *             if test fails
	 */
	@Test
	public void testF6KeyToOpenExistingDesc() throws Exception {
		testEditView.setTestCaseController(new TestEditorControllerAdapter() {
			@Override
			public boolean isLineEditable(int lineNumber) {
				return true;
			}

			@Override
			public TestComponent getTestComponentAt(int i) {
				return new TestDescriptionTestCase();
			}
		});

		testEditView.createUI(shell);
		testEditView.getStyledText().setText("|wait|3|");
		testEditView.getStyledText().setSelection(1);

		IEventBroker eventBroker = testEditView.getEventBroker();
		assertNull(testEditView.getPopupDialogDescription());
		// In The PDEJunit Context the Event message works.
		// In the Maven tycho build context the event doesn't work, for that
		// case the method is called directly.
		testEditView.editLineInPopupDialog("");
		eventBroker.send(TestEditorUIEventConstants.EDIT_CONTEXTMENU_F6, "");
		assertNull(testEditView.getPopupDialogDescription());
	}

	/**
	 * Testing selecting lines in popup.
	 * 
	 * @throws Exception
	 *             if test fails
	 */
	@Test
	public void testF6KeyToOpenExistingAction() throws Exception {
		testEditView.setTestCaseController(new TestEditorControllerAdapter() {
			@Override
			public boolean isLineEditable(int lineNumber) {
				return true;
			}

			@Override
			public TestComponent getTestComponentAt(int i) {
				return new TestActionGroup();
			}
		});

		testEditView.createUI(shell);
		testEditView.getStyledText().setText("|wait|3|");
		testEditView.getStyledText().setSelection(1);

		IEventBroker eventBroker = testEditView.getEventBroker();
		assertNull(testEditView.getActionGroupPopupDialog());
		// In The PDEJunit Context the Event message works.
		// In the Maven tycho build context the event doesn't work, for that
		// case the method is called directly.
		testEditView.editLineInPopupDialog("");
		eventBroker.send(TestEditorUIEventConstants.EDIT_CONTEXTMENU_F6, "");
		assertNull(testEditView.getActionGroupPopupDialog());
	}

	/**
	 * Test the Refresh of the StyledText of the EditView without any data.
	 */
	@Test
	public void testRefreshStyledTextOnEmptyData() {
		ITestEditorController testCaseController = new TestEditorControllerAdapter();
		testEditView.setTestCaseController(testCaseController);
		testEditView.createUI(shell);
		testEditView.refreshStyledText();
	}

	/**
	 * Test proceeds refreshStyledText(), branches to
	 * refreshUnEmptyText(sizeTestCase) which uses
	 * addTextsToStyledText(sizeTestCase, lineInTestCase) to refresh the
	 * styledText.
	 * 
	 * @throws Exception
	 *             if test fails
	 */
	@Test
	public void testRefreshUnEmptyText() throws Exception {
		ITestEditorController testCaseController = new TestEditorControllerAdapter() {
			@Override
			public int getTestFlowSize() {
				return 1;
			}

			@Override
			public ArrayList<String> getLine(int i) {
				ArrayList<String> lines = new ArrayList<String>();
				lines.add("|wait|3|");
				return lines;
			}

			@Override
			public ArrayList<TextType> getTextTypes(int i) {
				ArrayList<TextType> types = new ArrayList<TextType>();
				types.add(TextType.UNPARSED_ACTION_lINE);
				return types;
			}

			@Override
			public boolean isLibraryErrorLessLoaded() {
				return true;
			}
		};

		testEditView.setTestCaseController(testCaseController);
		testEditView.createUI(shell);
		testEditView.getStyledText().setText("test");

		assertEquals("Set text should be test: ", "test", testEditView.getStyledText().getText());
		testEditView.refreshStyledText();
		assertEquals("Set text should the restored original text: ", "|wait|3|", testEditView.getStyledText().getText());
	}

	/**
	 * Test the Refresh of the StyledText of the EditView with a TestCase.
	 */
	@Test
	public void testRefreshStyledTextOnTestCase() {
		ITestEditorController testCaseController = new TestEditorControllerAdapter() {
			@Override
			public TestFlow getTestFlow() {
				TestCase testCase = new TestCase();
				return testCase;
			}
		};
		testEditView.setTestCaseController(testCaseController);
		testEditView.createUI(shell);
		testEditView.refreshStyledText();
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
			public String translate(String key, Object... params) {
				return key;
			};
		});
		context.set(EPartService.class, new PartServiceAdapter());

		testEditView = ContextInjectionFactory.make(TestEditView.class, context);
		testEditView.setTestCaseController(new TestEditorControllerAdapter());
	}

	/**
	 * Destroying Shell.
	 */
	@After
	public void tearDown() {
		shell.dispose();
	}
}
