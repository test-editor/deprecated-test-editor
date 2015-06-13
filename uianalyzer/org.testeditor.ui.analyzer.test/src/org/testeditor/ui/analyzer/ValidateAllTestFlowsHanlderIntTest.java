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
package org.testeditor.ui.analyzer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testeditor.core.model.teststructure.TestCase;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.model.teststructure.TestSuite;
import org.testeditor.ui.constants.TestEditorConstants;
import org.testeditor.ui.parts.testExplorer.TestExplorer;

/**
 * 
 * Integration Tests for ValidateAllTestCasesHanlder.
 * 
 * 
 */
public class ValidateAllTestFlowsHanlderIntTest {

	private Shell shell;

	/**
	 * Test the canExecute logic. Can execute is only possible on seletion in
	 * the testexplorer.
	 * 
	 */
	@Test
	public void testCanExecute() {
		ValidateAllTestFlowsHandler handler = new ValidateAllTestFlowsHandler();
		IEclipseContext context = EclipseContextFactory.create();
		context.set(TestEditorConstants.TEST_EXPLORER_VIEW, getTestExplorerMock(null));
		assertFalse(handler.canExecute(context));
		context.set(TestEditorConstants.TEST_EXPLORER_VIEW, getTestExplorerMock(new TestCase()));
		assertFalse(handler.canExecute(context));
		context.set(TestEditorConstants.TEST_EXPLORER_VIEW, getTestExplorerMock(new TestSuite()));
		assertTrue(handler.canExecute(context));
		context.set(TestEditorConstants.TEST_EXPLORER_VIEW, getTestExplorerMock(new TestProject()));
		assertTrue(handler.canExecute(context));
	}

	/**
	 * 
	 */
	@Test
	public void testGetTestStructuresToWorkOn() {
		IEclipseContext context = EclipseContextFactory.create();
		context.set(TranslationService.class, new TranslationService() {
			@Override
			public String translate(String key, String contributorURI) {
				return "";
			}
		});
		ValidateAllTestFlowsHandler handler = ContextInjectionFactory.make(ValidateAllTestFlowsHandler.class, context);
		TestProject tp = new TestProject();
		TestSuite suite = new TestSuite();
		tp.addChild(suite);
		suite.addChild(new TestCase());
		tp.addChild(new TestCase());
		List<TestStructure> list = handler.getTestStructuresToWorkOn(tp, shell);
		assertEquals(3, list.size());
	}

	/**
	 * Creates a shell for ui tests.
	 */
	@Before
	public void setup() {
		shell = new Shell(Display.getDefault());
	}

	/**
	 * Disposes the shell after test.
	 */
	@After
	public void tearDown() {
		shell.dispose();
	}

	/**
	 * 
	 * @param selection
	 *            the firstElement of the selection in the TestExplorerMockk
	 * @return TestExplorerMock.
	 */
	private TestExplorer getTestExplorerMock(final Object selection) {
		return new TestExplorer(null) {
			@Override
			public IStructuredSelection getSelection() {
				return new StructuredSelection() {

					@Override
					public boolean isEmpty() {
						return selection == null;
					}

					@Override
					public Object getFirstElement() {
						return selection;
					}

				};
			}
		};
	}

}
