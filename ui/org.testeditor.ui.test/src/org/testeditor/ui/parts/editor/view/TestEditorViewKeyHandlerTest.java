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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.event.EventHandler;
import org.testeditor.ui.constants.TestEditorUIEventConstants;

/**
 * 
 * Testing the handling of key events.
 * 
 */
public class TestEditorViewKeyHandlerTest {

	private Composite shell;
	private Event event;
	private TestEditorViewKeyHandler keyHandler;
	private IEclipseContext context;
	private final Set<String> events = new HashSet<String>();
	private IEventBroker eventBroker;

	/**
	 * Tests the key events.
	 * 
	 * @throws Exception
	 *             for failing test
	 */
	@Test
	public void testDoHandleKeyEvent() throws Exception {

		KeyEvent e = getEventForTest(SWT.None, SWT.DEL);
		checkKeyValue(TestEditorUIEventConstants.EDIT_CONTEXTMENU_DEL, e);

		e = getEventForTest(SWT.None, SWT.F6);
		checkKeyValue(TestEditorUIEventConstants.EDIT_CONTEXTMENU_F6, e);

		e = getEventForTest(SWT.None, SWT.F7);
		checkKeyValue(TestEditorUIEventConstants.EDIT_CONTEXTMENU_F7, e);

		e = getEventForTest(SWT.None, SWT.F8);
		checkKeyValue(TestEditorUIEventConstants.EDIT_CONTEXTMENU_F8, e);

		e = getEventForTest(SWT.CTRL, 'c');
		checkKeyValue(TestEditorUIEventConstants.EDIT_CONTEXTMENU_CNTRL_C, e);

		e = getEventForTest(SWT.CTRL, 'v');
		checkKeyValue(TestEditorUIEventConstants.EDIT_CONTEXTMENU_CNTRL_V, e);

		e = getEventForTest(SWT.CTRL, 'a');
		checkKeyValue(TestEditorUIEventConstants.EDIT_CONTEXTMENU_CNTRL_A, e);

		e = getEventForTest(SWT.CTRL, SWT.HOME);
		checkKeyValue(TestEditorUIEventConstants.EDIT_CONTEXTMENU_HOME_OR_END, e);

		e = getEventForTest(SWT.CTRL, SWT.INSERT);
		checkKeyValue(TestEditorUIEventConstants.EDIT_CONTEXTMENU_CNTRL_INSERT, e);
	}

	/**
	 * Test if short cut combination.
	 * 
	 * @throws Exception
	 *             if test fails
	 */
	@Test
	public void testShortCutWithOutKeyMask() throws Exception {
		KeyEvent e = getEventForTest(SWT.CTRL, SWT.DEL);
		keyHandler.doHandleKeyEvent(e);
		assertFalse(events.contains(TestEditorUIEventConstants.EDIT_CONTEXTMENU_DEL));
		keyHandler.doHandleKeyEvent(e);
		e = getEventForTest(SWT.None, SWT.DEL);
		checkKeyValue(TestEditorUIEventConstants.EDIT_CONTEXTMENU_DEL, e);

		e = getEventForTest(SWT.ALT, SWT.F6);
		keyHandler.doHandleKeyEvent(e);
		assertFalse(events.contains(TestEditorUIEventConstants.EDIT_CONTEXTMENU_F6));
		keyHandler.doHandleKeyEvent(e);
		e = getEventForTest(SWT.None, SWT.F6);
		checkKeyValue(TestEditorUIEventConstants.EDIT_CONTEXTMENU_F6, e);

		e = getEventForTest(SWT.SHIFT, SWT.F7);
		keyHandler.doHandleKeyEvent(e);
		assertFalse(events.contains(TestEditorUIEventConstants.EDIT_CONTEXTMENU_F7));
		keyHandler.doHandleKeyEvent(e);
		e = getEventForTest(SWT.None, SWT.F7);
		checkKeyValue(TestEditorUIEventConstants.EDIT_CONTEXTMENU_F7, e);

		e = getEventForTest(SWT.ALT, 'c');
		keyHandler.doHandleKeyEvent(e);
		assertFalse(events.contains(TestEditorUIEventConstants.EDIT_CONTEXTMENU_CNTRL_C));
		keyHandler.doHandleKeyEvent(e);
		e = getEventForTest(SWT.CTRL, 'c');
		checkKeyValue(TestEditorUIEventConstants.EDIT_CONTEXTMENU_CNTRL_C, e);

		e = getEventForTest(SWT.SHIFT, 'x');
		keyHandler.doHandleKeyEvent(e);
		assertFalse(events.contains(TestEditorUIEventConstants.EDIT_CONTEXTMENU_CNTRL_X));
		keyHandler.doHandleKeyEvent(e);
		e = getEventForTest(SWT.CTRL, 'x');
		checkKeyValue(TestEditorUIEventConstants.EDIT_CONTEXTMENU_CNTRL_X, e);

		e = getEventForTest(SWT.None, 'a');
		keyHandler.doHandleKeyEvent(e);
		assertFalse(events.contains(TestEditorUIEventConstants.EDIT_CONTEXTMENU_CNTRL_A));
		keyHandler.doHandleKeyEvent(e);
		e = getEventForTest(SWT.CTRL, 'a');
		checkKeyValue(TestEditorUIEventConstants.EDIT_CONTEXTMENU_CNTRL_A, e);
	}

	/**
	 * Creates KeyEvent for test.
	 * 
	 * @param mask
	 *            KeyEvent Mask
	 * @param keyCode
	 *            KeyEvent KeyCode
	 * @return KeyEvent
	 */
	public KeyEvent getEventForTest(int mask, int keyCode) {
		KeyEvent e = new KeyEvent(event);
		e.stateMask = mask;
		e.keyCode = keyCode;
		return e;
	}

	/**
	 * Checks if first processed KeyEvent is not created and second if it is
	 * created.
	 * 
	 * @param eventConstant
	 *            constant for KeyEvent
	 * @param e
	 *            KeyEvent
	 */
	public void checkKeyValue(String eventConstant, KeyEvent e) {
		assertFalse(events.contains(eventConstant));
		keyHandler.doHandleKeyEvent(e);
		assertTrue(events.contains(eventConstant));
	}

	/**
	 * Creating new Shell.
	 */
	@Before
	public void setUP() {
		shell = new Shell();
		event = new Event();

		context = EclipseContextFactory.getServiceContext(FrameworkUtil.getBundle(TestEditorViewKeyHandler.class)
				.getBundleContext());
		context.set(Logger.class, null);
		eventBroker = context.get(IEventBroker.class);
		eventBroker.subscribe("Edit/*", new EventHandler() {

			@Override
			public void handleEvent(org.osgi.service.event.Event arg0) {
				events.add(arg0.getTopic());
			}
		});
		keyHandler = ContextInjectionFactory.make(TestEditorViewKeyHandler.class, context);

		event.widget = new Button(shell, SWT.NORMAL);
	}

	/**
	 * Destroying Shell.
	 */
	@After
	public void tearDown() {
		shell.dispose();
	}
}
