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

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.junit.Test;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.model.teststructure.TestCase;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestProjectConfig;
import org.testeditor.core.model.teststructure.TestScenario;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.model.teststructure.TestSuite;
import org.testeditor.core.services.interfaces.TestEditorPlugInService;
import org.testeditor.core.services.interfaces.TestProjectService;
import org.testeditor.core.services.interfaces.TestStructureContentService;
import org.testeditor.ui.adapter.TestProjectServiceAdapter;
import org.testeditor.ui.adapter.TestStructureContentServiceAdapter;
import org.testeditor.ui.adapter.TranslationServiceAdapter;
import org.testeditor.ui.constants.TestEditorConstants;
import org.testeditor.ui.mocks.TestEditorPluginServiceMock;
import org.testeditor.ui.utilities.TestEditorTranslationService;

/**
 *
 * Modul/Inregration- Tests for CloneTestStructure.
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

	/**
	 * Tests the clone operation.
	 */
	@Test
	public void testCloneTestCase() {
		IEclipseContext context = EclipseContextFactory.create();
		List<TestStructure> list = new ArrayList<TestStructure>();
		TestCase testCase = new TestCase();
		list.add(testCase);
		context.set(TestEditorConstants.TEST_EXPLORER_VIEW, new TestExplorerMock(list));
		context.set(TestEditorPlugInService.class, new TestEditorPluginServiceMock() {
			@Override
			public TestStructureContentService getTestStructureContentServiceFor(String testServerID) {
				return new TestStructureContentServiceAdapter();
			}
		});
		context.set(TestProjectService.class, new TestProjectServiceAdapter() {
			@Override
			public TestStructure findTestStructureByFullName(String testStructureFullName) throws SystemException {
				TestCase testCase = new TestCase();
				testCase.setName("MyTestCase");
				TestProject tp = new TestProject();
				tp.setName("MyPrj");
				tp.addChild(testCase);
				TestProjectConfig cfg = new TestProjectConfig();
				tp.setTestProjectConfig(cfg);
				return testCase;
			}
		});
		context.set(TestEditorTranslationService.class, new TestEditorTranslationService());
		context.set(TranslationService.class, new TranslationServiceAdapter().getTranslationService());
		CloneTestStructureHandler handler = ContextInjectionFactory.make(CloneTestStructureHandler.class, context);
		handler.execute(context);
		handler.updateNewTestStructureWithLastSelection("MyPrj.MyTestCase");
	}

}
