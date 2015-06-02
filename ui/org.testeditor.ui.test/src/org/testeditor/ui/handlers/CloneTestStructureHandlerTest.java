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
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestScenario;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.model.teststructure.TestSuite;
import org.testeditor.ui.constants.TestEditorConstants;

/**
 *
 * Modul Tests for CloneTestStructure.
 *
 */
public class CloneTestStructureHandlerTest {

	/**
	 * Test the can execute on instances of testflow returns true. Also it can
	 * only execute on single elements.
	 */
	@Test
	public void testCanExecute() {
		CloneTestStructureHandler handler = new CloneTestStructureHandler();
		IEclipseContext context = EclipseContextFactory.create();
		List<TestStructure> list = new ArrayList<TestStructure>();
		list.add(new TestCase());
		context.set(TestEditorConstants.TEST_EXPLORER_VIEW, new TestExplorerMock(list));
		assertTrue(handler.canExecute(context));
		list.clear();
		list.add(new TestProject());
		assertFalse(handler.canExecute(context));
		list.clear();
		list.add(new TestScenario());
		assertTrue(handler.canExecute(context));
		list.clear();
		list.add(new TestSuite());
		assertFalse(handler.canExecute(context));
		list.clear();
		list.add(new TestCase());
		assertTrue(handler.canExecute(context));
		list.add(new TestScenario());
		assertFalse(handler.canExecute(context));
	}

}
