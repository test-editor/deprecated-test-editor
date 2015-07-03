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
package org.testeditor.ui.handlers;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.junit.Test;
import org.testeditor.core.model.teststructure.TestCase;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.model.teststructure.TestSuite;
import org.testeditor.ui.constants.TestEditorConstants;

/**
 * 
 * Integration Test for the OpenFileHandler.
 * 
 */
public class OpenFileHandlerTest {

	/**
	 * Test that there is no execute on the Root Element.
	 * 
	 * @throws Exception
	 *             for Test
	 */
	@Test
	public void testCanNotExecuteOnRootSelection() throws Exception {
		OpenSourceCodeHandler handler = new OpenSourceCodeHandler();
		IEclipseContext context = EclipseContextFactory.create();
		List<TestStructure> list = new ArrayList<TestStructure>();
		TestSuite suite = new TestSuite();
		list.add(suite);
		context.set(TestEditorConstants.SELECTED_TEST_COMPONENTS, new TestExplorerMock(list).getSelection());
		assertFalse(handler.canExecute(context));
	}

	/**
	 * Test that Open works on single selection of <code>TestCase</code> and
	 * <code>TestSuite</code>.
	 * 
	 * @throws Exception
	 *             for Test
	 */
	@Test
	public void testCanExecuteOnSingleSelection() throws Exception {
		OpenSourceCodeHandler handler = new OpenSourceCodeHandler();
		IEclipseContext context = EclipseContextFactory.create();
		List<TestStructure> list = new ArrayList<TestStructure>();
		TestSuite suite = new TestSuite();
		suite.addChild(suite);
		list.add(suite);
		context.set(TestEditorConstants.SELECTED_TEST_COMPONENTS, new TestExplorerMock(list).getSelection());
		assertTrue(handler.canExecute(context));
		list = new ArrayList<TestStructure>();
		TestCase tc = new TestCase();
		new TestSuite().addChild(tc);
		list.add(tc);
		context.set(TestEditorConstants.SELECTED_TEST_COMPONENTS, new TestExplorerMock(list).getSelection());
		assertTrue(handler.canExecute(context));
	}

	/**
	 * No Excecute on more than one Selection.
	 * 
	 * @throws Exception
	 *             for Test
	 */
	@Test
	public void testCanNotExecuteOnMultipleSelection() throws Exception {
		OpenSourceCodeHandler handler = new OpenSourceCodeHandler();
		IEclipseContext context = EclipseContextFactory.create();
		List<TestStructure> list = new ArrayList<TestStructure>();
		TestSuite suite = new TestSuite();
		suite.addChild(suite);
		list.add(suite);
		TestCase tc = new TestCase();
		new TestSuite().addChild(tc);
		list.add(tc);
		context.set(TestEditorConstants.SELECTED_TEST_COMPONENTS, new TestExplorerMock(list).getSelection());
		assertFalse(handler.canExecute(context));
	}

}
