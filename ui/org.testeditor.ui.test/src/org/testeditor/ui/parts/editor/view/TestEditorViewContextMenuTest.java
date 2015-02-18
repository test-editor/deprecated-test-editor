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

import static org.junit.Assert.assertNotNull;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Decorations;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.osgi.framework.FrameworkUtil;
import org.testeditor.ui.adapter.TestEditorControllerAdapter;
import org.testeditor.ui.parts.editor.ITestEditorController;
import org.testeditor.ui.utilities.TestEditorTranslationService;

/**
 * JUnit tests for class TestEditorViewContextMenu.
 */
public class TestEditorViewContextMenuTest {
	private Decorations shell;
	private TestEditView testEditView;

	/**
	 * Tests if context menu is build.
	 * 
	 * @throws Exception
	 *             if test fails
	 */
	@Ignore
	@Test
	public void testGetContextMenu() throws Exception {
		ITestEditorController testCaseController = new TestEditorControllerAdapter() {
			@Override
			public boolean isLineEditable(int lineNumber) {
				return true;
			}
		};

		testEditView.setTestCaseController(testCaseController);
		testEditView.createUI(shell);

		Event e = new Event();
		e.widget = new Button(shell, SWT.NORMAL);
		MenuDetectEvent parentEvent = new MenuDetectEvent(e);
		TestEditorViewContextMenu contextMenu = new TestEditorViewContextMenu();
		Menu contextMenu2 = contextMenu.getContextMenu(shell, SWT.POP_UP, testEditView, parentEvent);
		assertNotNull(contextMenu2);
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
