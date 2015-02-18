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
package org.testeditor.ui.wizardpages;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.swt.widgets.Shell;
import org.junit.Test;
import org.testeditor.core.model.teststructure.TestCase;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestScenario;
import org.testeditor.core.model.teststructure.TestSuite;
import org.testeditor.core.services.interfaces.ServiceLookUpForTest;
import org.testeditor.core.services.interfaces.TestEditorReservedNamesService;
import org.testeditor.core.services.interfaces.TestProjectService;
import org.testeditor.core.services.interfaces.TestStructureService;
import org.testeditor.ui.adapter.TestStructureServiceAdapter;
import org.testeditor.ui.utilities.TestEditorTranslationService;

/**
 * Test for the renaming wizard of testcase, testsuite and testscenario.
 * 
 * @author llipinski
 * 
 */
public class RenameTestStructureWizardTest {

	/**
	 * Test the IsNameValid Method in the renaming state. Testing just the rule
	 * with a least to capital characters and between them at least a single
	 * small character.
	 * 
	 * @throws Exception
	 *             for Test
	 */
	@Test
	public void testIsNameValidRenameWithNotSameNameInTree() throws Exception {
		TestSuite testSuite = new TestSuite();
		testSuite.setName("FooBar");
		TestCase testCase = new TestCase();
		testCase.setName("HelloWorld");
		TestCase testCase2 = new TestCase();
		testCase2.setName("HelloBigWorld");
		testSuite.addChild(testCase);
		testSuite.addChild(testCase2);
		AbstractTestStructureWizardPage page = ContextInjectionFactory.make(RenameTestCaseWizardPage.class,
				getContextMock());
		page.setSelectedTestStructure(testCase);
		assertFalse(page.isNameValid("HelloBigWorld"));
		assertTrue(page.isNameValid("FooBarSo"));
	}

	/**
	 * Test the IsNameValid Method in the renaming state. For the testcases
	 * there should not the word "Suite" in the name.
	 * 
	 * @throws Exception
	 *             for Test
	 */
	@Test
	public void testIsNameValidRenameTestCaseWithNotSuiteInTheName() throws Exception {
		TestSuite testSuite = new TestSuite();
		testSuite.setName("FooBar");
		TestCase testCase = new TestCase();
		testCase.setName("HelloWorld");
		TestCase testCase2 = new TestCase();
		testCase2.setName("HelloBigWorld");
		testSuite.addChild(testCase);
		testSuite.addChild(testCase2);
		AbstractTestStructureWizardPage page = ContextInjectionFactory.make(RenameTestCaseWizardPage.class,
				getContextMock());
		page.setSelectedTestStructure(testCase);
		assertFalse(page.isNameValid("SuiteFortesting"));
		assertTrue(page.isNameValid("FooBarSo"));
	}

	/**
	 * Test the IsNameValid Method in the renaming state. For testsuites there
	 * should not the word 'Test' in the name.
	 * 
	 * @throws Exception
	 *             for Test
	 */
	@Test
	public void testIsNameValidRenameTestSuiteWithNotTestInTheName() throws Exception {
		TestProject testProject = new TestProject();
		TestSuite testSuite = new TestSuite();
		testSuite.setName("MySuite");
		testProject.addChild(testSuite);
		TestCase testCase = new TestCase();
		testCase.setName("HelloWorld");
		TestCase testCase2 = new TestCase();
		testCase2.setName("HelloBigWorld");
		testSuite.addChild(testCase);
		testSuite.addChild(testCase2);
		AbstractTestStructureWizardPage page = ContextInjectionFactory.make(RenameTestSuiteWizardPage.class,
				getContextMock());
		page.setSelectedTestStructure(testSuite);
		assertFalse(page.isNameValid("TestFortestting"));
		assertTrue(page.isNameValid("FooBarSo"));
	}

	/**
	 * Test the IsNameValid Method in the renaming state. For testscenarios
	 * there should neither the word 'Test' nor the word 'Suite' in the name.
	 * 
	 * @throws Exception
	 *             for Test
	 */
	@Test
	public void testIsNameValidRenameTestScenarioWithNotTestInTheName() throws Exception {
		TestSuite testSuite = new TestSuite();
		testSuite.setName("TestKomponent");
		TestScenario testScenario = new TestScenario();
		testScenario.setName("HelloWorld");
		testSuite.addChild(testScenario);
		AbstractTestStructureWizardPage page = ContextInjectionFactory.make(RenameTestScenarioWizardPage.class,
				getContextMock());
		page.setSelectedTestStructure(testScenario);
		assertFalse(page.isNameValid("TestFortestting"));
		assertTrue(page.isNameValid("FooBarSo"));
	}

	/**
	 * Test the IsNameValid Method in the renaming state. For testscenarios
	 * there should neither the word 'Test' nor the word 'Suite' in the name.
	 * 
	 * @throws Exception
	 *             for Test
	 */
	@Test
	public void testIsNameValidRenameTestScenarioWithNotSuiteInTheName() throws Exception {
		TestSuite testSuite = new TestSuite();
		testSuite.setName("TestKomponent");
		TestScenario testScenario = new TestScenario();
		testScenario.setName("HelloWorld");
		testSuite.addChild(testScenario);
		AbstractTestStructureWizardPage page = ContextInjectionFactory.make(RenameTestScenarioWizardPage.class,
				getContextMock());
		page.setSelectedTestStructure(testScenario);
		assertFalse(page.isNameValid("SuiteFortestting"));
		assertTrue(page.isNameValid("FooBarSo"));
	}

	/**
	 * 
	 * @return the IEclipseContext for the test.
	 */
	private IEclipseContext getContextMock() {
		IEclipseContext context = EclipseContextFactory.create();
		context.set(Shell.class, null);
		context.set(TestStructureService.class, getTestStructureServiceMock());
		context.set(TestEditorTranslationService.class, getTranslationServiceMock());
		context.set(TestProjectService.class, ServiceLookUpForTest.getService(TestProjectService.class));
		context.set(TestEditorReservedNamesService.class,
				ServiceLookUpForTest.getService(TestEditorReservedNamesService.class));
		return context;
	}

	/**
	 * 
	 * @return TranslationService Mock Object
	 */
	private TestEditorTranslationService getTranslationServiceMock() {
		return new TestEditorTranslationService() {
			@Override
			public String translate(String key, Object... params) {
				return "fooBar";
			}
		};
	}

	/**
	 * 
	 * @return TestStrucutreService Mock
	 */
	private TestStructureService getTestStructureServiceMock() {
		TestStructureServiceAdapter testStructureServiceAdapter = new TestStructureServiceAdapter();
		testStructureServiceAdapter.setEmptyVariable(false);
		return testStructureServiceAdapter;
	}
}
