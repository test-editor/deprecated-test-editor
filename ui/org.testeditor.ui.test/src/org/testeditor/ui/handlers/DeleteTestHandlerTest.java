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
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.model.teststructure.TestCase;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.model.teststructure.TestSuite;
import org.testeditor.core.services.interfaces.ServiceLookUpForTest;
import org.testeditor.core.services.interfaces.TestProjectService;
import org.testeditor.core.services.interfaces.TestScenarioService;
import org.testeditor.core.services.interfaces.TestStructureService;
import org.testeditor.ui.adapter.TestScenarioServiceAdapter;
import org.testeditor.ui.adapter.TestStructureServiceAdapter;
import org.testeditor.ui.constants.TestEditorConstants;
import org.testeditor.ui.parts.testExplorer.TestExplorer;
import org.testeditor.ui.utilities.TestEditorTranslationService;

/**
 * 
 * Integrationtests for DeleteTestHandler.
 * 
 */
public class DeleteTestHandlerTest {

	private boolean refreshDone;
	private String removedTestCaseName;
	private IEclipseContext context;
	private DeleteTestHandler testHandler;

	/**
	 * Tests the execute method with a Testcase.
	 * 
	 * @throws Exception
	 *             for test
	 */
	@Test
	@Ignore
	public void testExecuteDeleteTestCase() throws Exception {
		context.set(TestEditorConstants.TEST_EXPLORER_VIEW, getTestExplorerWithTestCaseMock(this));
		testHandler.execute(context, getTranslationServiceMock(), context.get(TestProjectService.class),
				context.get(TestScenarioService.class), context.get(TestStructureService.class));
		assertTrue("Delete Done with Refresh", refreshDone);
		assertEquals("forDel", removedTestCaseName);
	}

	/**
	 * Tests the execute method with a Testsuite.
	 * 
	 * @throws Exception
	 *             for Test
	 */
	@Test
	@Ignore
	public void testExecuteDeleteTestSuite() throws Exception {
		DeleteTestHandler testHandler = getOUT();
		context.set(TestEditorConstants.TEST_EXPLORER_VIEW, getTestExplorerWithTestSuiteMock(this));
		testHandler.execute(context, getTranslationServiceMock(), context.get(TestProjectService.class),
				context.get(TestScenarioService.class), context.get(TestStructureService.class));
		assertTrue("Delete Done", refreshDone);
		assertEquals("forDel", removedTestCaseName);
	}

	/**
	 * Tests that the Handler can not execute on the Root Element.
	 * 
	 * @throws Exception
	 *             for Test
	 */
	@Test
	public void testCanNotExecuteOnRoot() throws Exception {
		context.set(TestEditorConstants.TEST_EXPLORER_VIEW, getTestExplorerWithTestSuiteMock(this));
		assertFalse(testHandler.canExecute(context));
	}

	/**
	 * Tests that the Handler can not execute on the Root Element.
	 * 
	 * @throws Exception
	 *             for Test
	 */

	@Test
	public void testCanExecuteOnTestSuiteOrTestCase() throws Exception {
		List<TestStructure> list = new ArrayList<TestStructure>();
		TestCase testCase = new TestCase();
		TestSuite testSuite = new TestSuite();
		testSuite.addChild(testCase);
		list.add(testCase);
		new TestProject().addChild(testSuite);
		context.set(TestEditorConstants.SELECTED_TEST_COMPONENTS, new TestExplorerMock(list).getSelection());
		assertTrue(testHandler.canExecute(context));
		list = new ArrayList<TestStructure>();
		TestSuite suite = new TestSuite();
		suite.addChild(testSuite);
		list.add(suite);
		new TestProject().addChild(suite);
		context.set(TestEditorConstants.SELECTED_TEST_COMPONENTS, new TestExplorerMock(list).getSelection());
		assertTrue(testHandler.canExecute(context));
	}

	/**
	 * Tests that the Handler can not execute on the Root Element.
	 * 
	 * @throws Exception
	 *             for Test
	 */
	@Test
	public void testCanExecuteOnTestSuiteAndTestCase() throws Exception {
		List<TestStructure> list = new ArrayList<TestStructure>();
		TestCase testCase = new TestCase();
		TestSuite testSuite = new TestSuite();
		testSuite.addChild(testCase);
		list.add(testCase);
		TestSuite suite = new TestSuite();
		suite.addChild(testSuite);
		TestProject testProject = new TestProject();
		testProject.addChild(suite);
		list.add(suite);
		context.set(TestEditorConstants.SELECTED_TEST_COMPONENTS, new TestExplorerMock(list).getSelection());
		assertTrue(testHandler.canExecute(context));
	}

	/**
	 * Test the CommaList of the Children of a Testsuite.
	 * 
	 * @throws Exception
	 *             for Test
	 */
	@Test
	public void testCommaListOfTestStructuresNamesWithTestSuite() throws Exception {
		ArrayList<TestStructure> list = new ArrayList<TestStructure>();
		TestSuite suite = new TestSuite();
		suite.setName("Suite");
		TestCase testCase = new TestCase();
		testCase.setName("Foo");
		suite.addChild(testCase);
		testCase = new TestCase();
		testCase.setName("Bar");
		suite.addChild(testCase);
		list.add(suite);

		assertEquals(
				System.getProperty("line.separator") + "Suite" + System.getProperty("line.separator") + "   Foo, Bar",
				testHandler.getCommaListOfTestStructuresNames(list.iterator(), getTranslationServiceMock(), 1,
						context.get(TestScenarioService.class)).toString());

	}

	/**
	 * Test the CommaList of the Children of an empty Testsuite.
	 * 
	 * @throws Exception
	 *             for Test
	 */
	@Test
	public void testCommaListOfTestStructuresNamesWithEmptyTestSuite() throws Exception {
		ArrayList<TestStructure> list = new ArrayList<TestStructure>();
		TestSuite suite = new TestSuite();
		suite.setName("Suite");
		list.add(suite);
		assertEquals(
				System.getProperty("line.separator") + "Suite",
				testHandler.getCommaListOfTestStructuresNames(list.iterator(), getTranslationServiceMock(), 1,
						context.get(TestScenarioService.class)).toString());
	}

	/**
	 * Test that there is no comma in a names list containing only one name.
	 * 
	 * @throws Exception
	 *             for Test
	 */
	@Test
	public void testCommaListOfTestStructuresNamesWithOneName() throws Exception {
		ArrayList<TestStructure> list = new ArrayList<TestStructure>();
		TestCase testCase = new TestCase();
		testCase.setName("Foo");
		list.add(testCase);
		assertEquals(
				"Foo",
				testHandler.getCommaListOfTestStructuresNames(list.iterator(), getTranslationServiceMock(), 1,
						getTestScenrioServiceMock()).toString());
	}

	/**
	 * Tests that between all names is a comma.
	 * 
	 * @throws Exception
	 *             for Test
	 */
	@Test
	public void testCommaListOfTestStructuresNamesWithManyNames() throws Exception {
		ArrayList<TestStructure> list = new ArrayList<TestStructure>();
		TestCase testCase = new TestCase();
		testCase.setName("Foo");
		list.add(testCase);
		testCase = new TestCase();
		testCase.setName("Bar");
		list.add(testCase);
		testCase = new TestCase();
		testCase.setName("Hello");
		list.add(testCase);
		testCase = new TestCase();
		testCase.setName("World");
		list.add(testCase);
		assertEquals(
				"Foo, Bar, Hello, World",
				testHandler.getCommaListOfTestStructuresNames(list.iterator(), getTranslationServiceMock(), 1,
						getTestScenrioServiceMock()).toString());
	}

	/**
	 * Test extraction of the common parent in a selection. if there is no
	 * common parent. And if there is a common parent.
	 */
	@Test
	public void testExtractParentElementsFromSlectionSimplePath() {
		TestProject tp = new TestProject();
		TestSuite testSuite = new TestSuite();
		tp.addChild(testSuite);
		TestCase leftTc = new TestCase();
		leftTc.setName("left");
		TestCase rightTc = new TestCase();
		rightTc.setName("right");
		testSuite.addChild(rightTc);
		testSuite.addChild(leftTc);
		Set<TestStructure> selectedTestStructures = new HashSet<TestStructure>();
		selectedTestStructures.add(leftTc);
		selectedTestStructures.add(rightTc);
		Set<TestStructure> set = testHandler.extractParentElementsFromSelection(selectedTestStructures);
		assertEquals("Two elements in the set", 2, set.size());
		assertTrue(set.contains(rightTc));
		assertTrue(set.contains(leftTc));
		assertFalse(set.contains(testSuite));
		assertFalse(set.contains(tp));

		selectedTestStructures.add(testSuite);
		set = testHandler.extractParentElementsFromSelection(selectedTestStructures);
		assertEquals("One elements in the set", 1, set.size());
		assertFalse(set.contains(rightTc));
		assertFalse(set.contains(leftTc));
		assertTrue(set.contains(testSuite));
		assertFalse(set.contains(tp));

		selectedTestStructures.add(tp);
		set = testHandler.extractParentElementsFromSelection(selectedTestStructures);
		assertEquals("One elements in the set", 1, set.size());
		assertFalse(set.contains(rightTc));
		assertFalse(set.contains(leftTc));
		assertFalse(set.contains(testSuite));
		assertTrue(set.contains(tp));
	}

	/**
	 * Test extraction of the common parent in a selection and also adding
	 * another structure in another selection.
	 */
	@Test
	public void testExtractParentElementsFromSlectionTwoPath() {
		TestProject tp = new TestProject();
		TestSuite testSuite = new TestSuite();
		tp.addChild(testSuite);
		TestCase childTc = new TestCase();
		TestCase nonChildTc = new TestCase();
		testSuite.addChild(childTc);
		tp.addChild(nonChildTc);
		Set<TestStructure> selectedTestStructures = new HashSet<TestStructure>();
		selectedTestStructures.add(childTc);
		selectedTestStructures.add(nonChildTc);
		Set<TestStructure> selection = testHandler.extractParentElementsFromSelection(selectedTestStructures);
		assertEquals("Two elements in the set", 2, selection.size());
		assertTrue(selection.contains(childTc));
		assertTrue(selection.contains(nonChildTc));
		assertFalse(selection.contains(testSuite));
		assertFalse(selection.contains(tp));

		selectedTestStructures.add(testSuite);
		selection = testHandler.extractParentElementsFromSelection(selectedTestStructures);
		assertEquals("Two elements in the set", 2, selection.size());
		assertFalse(selection.contains(childTc));
		assertTrue(selection.contains(nonChildTc));
		assertTrue(selection.contains(testSuite));
		assertFalse(selection.contains(tp));

		selectedTestStructures.add(tp);
		selection = testHandler.extractParentElementsFromSelection(selectedTestStructures);
		assertEquals("One elements in the set", 1, selection.size());
		assertFalse(selection.contains(childTc));
		assertFalse(selection.contains(nonChildTc));
		assertFalse(selection.contains(testSuite));
		assertTrue(selection.contains(tp));
	}

	/**
	 * Inits for Test execution.
	 */
	@Before
	public void init() {
		refreshDone = false;
		removedTestCaseName = "";
		testHandler = getOUT();
	}

	/**
	 * 
	 * @param test
	 *            for Test Result Callbacks.
	 * @return TestExplorer Mock Object
	 */
	private TestExplorer getTestExplorerWithTestCaseMock(final DeleteTestHandlerTest test) {
		return new TestExplorer(null) {
			@Override
			public void refreshTreeInput() {
				test.setRefreshDone();
			}

			@Override
			public IStructuredSelection getSelection() {
				return new TreeSelection() {
					@Override
					public Iterator iterator() {
						List<TestStructure> list = new ArrayList<TestStructure>();
						TestCase testCase = new TestCase();
						testCase.setName("forDel");
						TestProject testProject = new TestProject();
						testProject.setName("myProject");
						testProject.addChild(testCase);
						list.add(testCase);
						return list.iterator();
					}

					@Override
					public Object getFirstElement() {
						TestSuite suite = new TestSuite();
						TestCase testCase = new TestCase();
						suite.addChild(testCase);
						return testCase;
					}

				};
			}
		};
	}

	/**
	 * 
	 * @param test
	 *            for Test Result Callbacks.
	 * @return TestExplorer with testsuite Mock Object
	 */
	private TestExplorer getTestExplorerWithTestSuiteMock(final DeleteTestHandlerTest test) {
		return new TestExplorer(null) {
			@Override
			public void refreshTreeInput() {
				test.setRefreshDone();
			}

			@Override
			public IStructuredSelection getSelection() {
				return new TreeSelection() {
					@Override
					public Iterator iterator() {
						List<TestStructure> list = new ArrayList<TestStructure>();
						TestSuite testSuite = new TestSuite();
						testSuite.setName("forDel");
						testSuite.addChild(new TestCase());
						testSuite.addChild(new TestCase());
						new TestProject().addChild(testSuite);
						list.add(testSuite);
						return list.iterator();
					}

					@Override
					public Object getFirstElement() {
						TestSuite suite = new TestSuite();
						TestCase testCase = new TestCase();
						suite.addChild(testCase);
						return testCase;
					}

				};
			}
		};
	}

	/**
	 * Notify Test that a refresh is done.
	 */
	protected void setRefreshDone() {
		refreshDone = true;
	}

	/**
	 * Initialize the OUT with the Eclipse Context.
	 * 
	 * @return the Object under Test.
	 */
	private DeleteTestHandler getOUT() {
		context = EclipseContextFactory.create();
		context.set(TestEditorConstants.TEST_EXPLORER_VIEW, getTestExplorerWithTestCaseMock(this));
		context.set(EPartService.class, null);
		context.set(TestStructureService.class, getTestStructureServiceMock(this));
		context.set(TestProjectService.class, ServiceLookUpForTest.getService(TestProjectService.class));
		context.set(TestScenarioService.class, getTestScenrioServiceMock());
		return ContextInjectionFactory.make(DeleteTestHandler.class, context);
	}

	/**
	 * 
	 * @return a mock for the TestScenarioService.
	 */
	private TestScenarioService getTestScenrioServiceMock() {
		return new TestScenarioServiceAdapter();
	}

	/**
	 * 
	 * @param test
	 *            for callbacks of the testresult.
	 * @return TestStrucutreService Mock
	 */
	private TestStructureService getTestStructureServiceMock(final DeleteTestHandlerTest test) {
		TestStructureServiceAdapter testStructureServiceAdapter = new TestStructureServiceAdapter() {
			@Override
			public void delete(TestStructure testStructure) throws SystemException {
				test.setRemovedTestCaseName(testStructure.getName());
			}
		};
		testStructureServiceAdapter.setEmptyVariable(true);
		return testStructureServiceAdapter;
	}

	/**
	 * 
	 * @return Mock for TranslationService
	 */
	private TestEditorTranslationService getTranslationServiceMock() {
		return new TestEditorTranslationService() {
			@Override
			public String translate(String key, Object... params) {
				return "";
			}
		};
	}

	/**
	 * 
	 * @param name
	 *            of the removed TestStrucutre
	 */
	protected void setRemovedTestCaseName(String name) {
		this.removedTestCaseName = name;
	}

}
