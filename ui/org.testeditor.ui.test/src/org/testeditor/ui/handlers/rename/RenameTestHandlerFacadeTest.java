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
package org.testeditor.ui.handlers.rename;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.junit.Test;
import org.testeditor.core.model.teststructure.TestCase;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestProjectConfig;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.model.teststructure.TestSuite;
import org.testeditor.core.services.interfaces.TestExceutionEnvironmentService;
import org.testeditor.core.services.interfaces.TestProjectService;
import org.testeditor.core.services.interfaces.TestScenarioService;
import org.testeditor.core.services.interfaces.TestStructureContentService;
import org.testeditor.core.services.interfaces.TestStructureService;
import org.testeditor.core.services.plugins.TestEditorPlugInService;
import org.testeditor.ui.constants.TestEditorConstants;
import org.testeditor.ui.handlers.TestExplorerMock;
import org.testeditor.ui.mocks.EventBrokerMock;
import org.testeditor.ui.mocks.TestEditorPluginServiceMock;
import org.testeditor.ui.mocks.TestScenarioServiceMock;
import org.testeditor.ui.utilities.TestEditorTranslationService;

/**
 * 
 * Integrationtests for the RenameTestHandlerFacade.
 * 
 */
public class RenameTestHandlerFacadeTest {

	/**
	 * Test that Rename works on single selection of <code>TestCase</code> and
	 * <code>TestSuite</code>.
	 * 
	 * @throws Exception
	 *             for Test
	 */
	@Test
	public void testCanExecuteOnSingleSelection() throws Exception {
		IEclipseContext context = EclipseContextFactory.create();
		List<TestStructure> list = new ArrayList<TestStructure>();
		TestSuite suite = new TestSuite();
		suite.addChild(suite);
		new TestProject().addChild(suite);
		list.add(suite);
		setServices(context);

		context.set(TestEditorConstants.TEST_EXPLORER_VIEW, new TestExplorerMock(list));

		RenameTestHandlerFacade handler = ContextInjectionFactory.make(RenameTestHandlerFacade.class, context);
		assertTrue(handler.canExecute(context));
		list = new ArrayList<TestStructure>();
		TestCase tc = new TestCase();
		TestSuite ts = new TestSuite();
		ts.addChild(tc);
		new TestProject().addChild(ts);
		list.add(tc);
		context.set(TestEditorConstants.TEST_EXPLORER_VIEW, new TestExplorerMock(list));
		// context.set(IEventBroker.class, getEventBrokerMock());
		assertTrue(handler.canExecute(context));
	}

	/**
	 * set the needed services into the context.
	 * 
	 * @param context
	 *            {@link IEclipseContext}
	 */
	private void setServices(IEclipseContext context) {

		context.set(TestScenarioService.class, getTestScenarioServiceMock());
		context.set(TestProjectService.class, null);
		context.set(TestExceutionEnvironmentService.class, null);
		context.set(TestStructureService.class, null);
		context.set(EPartService.class, null);
		context.set(TestStructureContentService.class, null);
		context.set(TestEditorTranslationService.class, new TestEditorTranslationService());
		context.set(IEventBroker.class, new EventBrokerMock());
		context.set(TestEditorPlugInService.class, new TestEditorPluginServiceMock());
	}

	/**
	 * No Excecute on more than one Selection.
	 * 
	 * @throws Exception
	 *             for Test
	 */
	@Test
	public void testCanNotExecuteOnMultipleSelection() throws Exception {
		IEclipseContext context = EclipseContextFactory.create();
		setServices(context);
		List<TestStructure> list = new ArrayList<TestStructure>();
		TestSuite suite = new TestSuite();
		suite.addChild(suite);
		list.add(suite);
		TestCase tc = new TestCase();
		new TestSuite().addChild(tc);
		list.add(tc);
		context.set(TestEditorConstants.TEST_EXPLORER_VIEW, new TestExplorerMock(list));
		RenameTestHandlerFacade handler = ContextInjectionFactory.make(RenameTestHandlerFacade.class, context);
		assertFalse(handler.canExecute(context));
	}

	/**
	 * Test that there is no execute on the Root Element.
	 * 
	 * @throws Exception
	 *             for Test
	 */
	@Test
	public void testCanNotExecuteOnRootSelection() throws Exception {
		IEclipseContext context = EclipseContextFactory.create();
		setServices(context);
		List<TestStructure> list = new ArrayList<TestStructure>();
		TestSuite suite = new TestSuite();
		new TestProject().addChild(suite);
		list.add(suite);
		context.set(TestEditorConstants.TEST_EXPLORER_VIEW, new TestExplorerMock(list));
		RenameTestHandlerFacade handler = ContextInjectionFactory.make(RenameTestHandlerFacade.class, context);
		assertTrue(handler.canExecute(context));
	}

	/**
	 * 
	 * @return TestScenarioServiceMock to check the TestScenarioMocks
	 */
	private TestScenarioService getTestScenarioServiceMock() {
		return new TestScenarioServiceMock();
	}

	/**
	 * Test that Rename works on single selection of <code>TestProject</code>.
	 * 
	 * @throws Exception
	 *             for Test
	 */
	@Test
	public void testCanExecuteOnTestProject() throws Exception {

		IEclipseContext context = EclipseContextFactory.create();
		List<TestStructure> list = new ArrayList<TestStructure>();
		TestSuite suite = new TestSuite();
		suite.addChild(suite);
		TestProject testProject = new TestProject();
		testProject.setTestProjectConfig(new TestProjectConfig());
		testProject.addChild(suite);
		list.add(testProject);
		setServices(context);

		context.set(TestEditorConstants.TEST_EXPLORER_VIEW, new TestExplorerMock(list));
		RenameTestHandlerFacade handler = ContextInjectionFactory.make(RenameTestHandlerFacade.class, context);
		assertTrue(handler.canExecute(context));
	}
}
