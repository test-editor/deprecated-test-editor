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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.services.internal.events.EventBroker;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testeditor.core.model.teststructure.TestCase;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestProjectConfig;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.model.teststructure.TestSuite;
import org.testeditor.core.services.interfaces.TeamShareService;
import org.testeditor.core.services.interfaces.TestProjectService;
import org.testeditor.core.services.interfaces.TestStructureService;
import org.testeditor.ui.adapter.TestProjectServiceAdapter;
import org.testeditor.ui.adapter.TestStructureServiceAdapter;
import org.testeditor.ui.adapter.TranslationServiceAdapter;
import org.testeditor.ui.constants.TestEditorConstants;

/**
 * 
 * Integrationtest for the abstract NewTestStructureHandler.
 * 
 */
public class NewTestStructureHandlerTest {

	private Shell shell;

	/**
	 * Test that the Root of the Service is used. If no testExplorer is
	 * available.
	 * 
	 * @throws Exception
	 *             for Test
	 */
	@Test
	public void testFindSelectedParentWithOutTestExplorer() throws Exception {
		NewTestStructureHandler handler = getOUT(false);
		TestStructure testStructure = handler.findSelectedParent(null);
		assertNotNull("No Null Value for the Parent of a Teststructure.", testStructure);
		assertEquals("Expecting Root Element", "root", testStructure.getName());
	}

	/**
	 * Test that the Root of the Service is used. If no element in the
	 * testExplorer is selected.
	 * 
	 * @throws Exception
	 *             for Test
	 */
	@Test
	public void testFindSelectedParentWithOutSelectionInTheTestExplorer() throws Exception {
		NewTestStructureHandler handler = getOUT(false);
		TestStructure testStructure = handler.findSelectedParent((IStructuredSelection) new TreeViewer(shell)
				.getSelection());
		assertNotNull("No Null Value for the Parent of a Teststructure.", testStructure);
		assertEquals("Expecting Root Element", "root", testStructure.getName());
	}

	/**
	 * Test that the Root of the Service is used. If no element in the
	 * testExplorer is selected.
	 * 
	 * @throws Exception
	 *             for Test
	 */
	@Test
	public void testFindSelectedParentBasedOnTheSelectionInTheTestExplorer() throws Exception {
		NewTestStructureHandler handler = getOUT(false);
		TestStructure testStructure = handler.findSelectedParent((IStructuredSelection) getMyTreeViewerMock()
				.getSelection());
		assertNotNull("No Null Value for the Parent of a Teststructure.", testStructure);
		assertEquals("Expecting Root Element", "TestCaseInTree", testStructure.getName());
	}

	/**
	 * Test Null if service has no elments in other cases the root.
	 * 
	 * @throws Exception
	 *             fot Test
	 */
	@Test
	public void testGetRootElementFromService() throws Exception {
		NewTestStructureHandler handler = getOUT(true);
		assertNull(handler.getRootElementFromTestStructureService());
		handler = getOUT(false);
		assertNotNull(handler.getRootElementFromTestStructureService());
		assertEquals("Expecting Root Element", "root", handler.getRootElementFromTestStructureService().getName());
	}

	/**
	 * Handler can execute on Selection of a Root Element.
	 * 
	 * @throws Exception
	 *             for Test
	 */
	@Test
	public void testCanExecuteOnRootElement() throws Exception {
		NewTestStructureHandler handler = new NewTestSuiteHandler();
		IEclipseContext context = EclipseContextFactory.create();
		List<TestStructure> list = new ArrayList<TestStructure>();
		TestProject project = new TestProject();
		project.setTestProjectConfig(new TestProjectConfig());
		list.add(project);
		context.set(TestEditorConstants.SELECTED_TEST_COMPONENTS, new TestExplorerMock(list).getSelection());
		assertTrue(handler.canExecute(context));
	}

	/**
	 * Handler can execute on Selection of a TestSuite.
	 * 
	 * @throws Exception
	 *             for Test
	 */
	@Test
	public void testCanExecuteOnTestSuite() throws Exception {
		NewTestStructureHandler handler = new NewTestSuiteHandler();
		IEclipseContext context = EclipseContextFactory.create();
		List<TestStructure> list = new ArrayList<TestStructure>();
		TestSuite suite = new TestSuite();
		suite.addChild(suite);
		list.add(suite);
		context.set(TestEditorConstants.SELECTED_TEST_COMPONENTS, new TestExplorerMock(list).getSelection());
		assertTrue(handler.canExecute(context));
	}

	/**
	 * Handler can not execute on Selection of a TestCase.
	 * 
	 * @throws Exception
	 *             for Test
	 */
	@Test
	public void testCanNotExecuteOnTestCase() throws Exception {
		NewTestStructureHandler handler = new NewTestSuiteHandler();
		IEclipseContext context = EclipseContextFactory.create();
		List<TestStructure> list = new ArrayList<TestStructure>();
		TestCase tc = new TestCase();
		new TestSuite().addChild(tc);
		list.add(tc);
		context.set(TestEditorConstants.SELECTED_TEST_COMPONENTS, new TestExplorerMock(list).getSelection());
		assertFalse(handler.canExecute(context));
	}

	/**
	 * 
	 * @return Mock for the TreeViewer
	 */
	private TreeViewer getMyTreeViewerMock() {
		return new TreeViewer(shell) {
			@Override
			public ISelection getSelection() {
				return new TreeSelection() {

					@Override
					public int size() {
						return 1;
					}

					@Override
					public TreePath[] getPaths() {
						TestCase testCase = new TestCase();
						testCase.setName("TestCaseInTree");
						Object[] segments = new Object[] { testCase };
						return new TreePath[] { new TreePath(segments) };
					}
				};
			}
		};
	}

	/**
	 * 
	 * @param empty
	 *            controlling the mock to return an empty or not empty list of
	 *            teststructures.
	 * @return Object Under Test
	 */
	private NewTestStructureHandler getOUT(boolean empty) {
		IEclipseContext context = EclipseContextFactory.create();
		context.set(TranslationService.class, new TranslationServiceAdapter().getTranslationService());
		context.set(TestStructureService.class, getTestStructureServiceMock(empty));
		context.set(EPartService.class, null);
		context.set(TestProjectService.class, getTestProjectServiceMock(empty));
		context.set(IServiceConstants.ACTIVE_SHELL, shell);
		context.set(TeamShareService.class, null);
		context.set(IEventBroker.class, new EventBroker());
		return ContextInjectionFactory.make(NewScenarioHandler.class, context);
	}

	/**
	 * 
	 * @param empty
	 *            return an empty projectlist or not
	 * 
	 * @return TestProjectService Mock for Test
	 */
	private TestProjectService getTestProjectServiceMock(final boolean empty) {
		return new TestProjectServiceAdapter() {

			@Override
			public List<TestProject> getProjects() {
				ArrayList<TestProject> list = new ArrayList<TestProject>();
				TestProject tp = new TestProject();
				tp.setName("root");
				if (!empty) {
					list.add(tp);
				}
				return list;
			}

		};
	}

	/**
	 * @param empty
	 *            controlling the mock to return an empty or not empty list of
	 *            teststructures.
	 * @return Mock for the TestStructureService
	 */
	private TestStructureService getTestStructureServiceMock(final boolean empty) {
		TestStructureServiceAdapter testStructureServiceAdapter = new TestStructureServiceAdapter();
		testStructureServiceAdapter.setEmptyVariable(empty);
		return testStructureServiceAdapter;
	}

	/**
	 * Init the UI.
	 */
	@Before
	public void setUp() {
		shell = new Shell(Display.getDefault());
	}

	/**
	 * Disposes UI.
	 */
	@After
	public void tearDown() {
		shell.dispose();
	}

}
