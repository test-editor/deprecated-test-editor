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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.osgi.framework.FrameworkUtil;
import org.testeditor.core.constants.TestEditorGlobalConstans;
import org.testeditor.core.model.teststructure.ScenarioSuite;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestProjectConfig;
import org.testeditor.core.model.teststructure.TestSuite;
import org.testeditor.core.services.interfaces.ServiceLookUpForTest;
import org.testeditor.core.services.interfaces.TestEditorReservedNamesService;
import org.testeditor.core.services.interfaces.TestProjectService;
import org.testeditor.core.services.interfaces.TestScenarioService;
import org.testeditor.core.services.interfaces.TestServerService;
import org.testeditor.core.services.interfaces.TestStructureService;
import org.testeditor.ui.mocks.TestScenarioServiceMock;
import org.testeditor.ui.utilities.TestEditorTranslationService;

/**
 * 
 * Integrationtests for the first Page of a new TestScenario Wizard.
 * 
 */
public class NewScenarioPageTest {

	private Shell shell;
	private Composite composite;

	/**
	 * Setup for all tests.
	 * 
	 * @throws Exception
	 *             for test
	 */
	@BeforeClass
	public static void beforeClass() throws Exception {
		TestServerService service = ServiceLookUpForTest.getService(TestServerService.class);
		service.startTestServer(getTestProject());
	}

	/**
	 * Shutdown Server.
	 * 
	 * @throws Exception
	 *             for Test
	 */
	@AfterClass
	public static void shutDown() throws Exception {
		ServiceLookUpForTest.getService(TestServerService.class).stopTestServer(getTestProject());
	}

	/**
	 * 
	 * @return TestProject With Config for Test.
	 */
	private static TestProject getTestProject() {
		TestProject tp = new TestProject();
		TestProjectConfig testProjectConfig = new TestProjectConfig();
		tp.setTestProjectConfig(testProjectConfig);
		return tp;
	}

	/**
	 * Creating UI Elements to be used in the Tests.
	 */
	@Before
	public void setUpSWT() {
		shell = new Shell();
		composite = new Composite(shell, SWT.NORMAL);
	}

	/**
	 * Disposing the UI Elements after the Tests.
	 */
	@After
	public void dispose() {
		composite.dispose();
		shell.dispose();
	}

	/**
	 * Tests that an new wizard page for a test-structure with an empty name can
	 * not complete.
	 * 
	 * @throws Exception
	 *             for Test
	 */
	@Test
	public void testCheckPageIncompleteOnNew() throws Exception {
		AbstractTestStructureWizardPage testPage = ContextInjectionFactory.make(NewTestScenarioWizardPage.class,
				getContext());
		testPage.setSelectedTestStructure(new ScenarioSuite());
		testPage.createControl(composite);
		assertFalse(testPage.isPageComplete());
	}

	/**
	 * Only for Tests.
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
	 * @return the IEclipseContext for the test.
	 */
	private IEclipseContext getContext() {
		IEclipseContext context = EclipseContextFactory.getServiceContext(FrameworkUtil.getBundle(getClass())
				.getBundleContext());
		context.set(Shell.class, null);
		context.set(Logger.class, null);
		context.set(TestStructureService.class, ServiceLookUpForTest.getService(TestStructureService.class));
		context.set(TestEditorTranslationService.class, getTranslationServiceMock());
		context.set(TestProjectService.class, ServiceLookUpForTest.getService(TestProjectService.class));
		context.set(TestEditorReservedNamesService.class, getTestEditorReservedNamesServiceMock());
		context.set(TestScenarioService.class, getTestScenarioServiceMock());
		return context;
	}

	/**
	 * 
	 * @return TestScenarioServiceMock to check the TestScenarioMocks
	 */
	private TestScenarioService getTestScenarioServiceMock() {
		return new TestScenarioServiceMock();
	}

	/**
	 * 
	 * @return a Mock for TestEditorReservedNamesService
	 */
	private TestEditorReservedNamesService getTestEditorReservedNamesServiceMock() {
		return new TestEditorReservedNamesService() {

			@Override
			public Set<String> getReservedTestStructureNames() {
				Set<String> result = new HashSet<String>();
				result.add(TestEditorGlobalConstans.TEST_SCENARIO_SUITE);
				result.add(TestEditorGlobalConstans.TEST_KOMPONENTS);
				return result;
			}

			@Override
			public boolean isReservedName(String name) {
				return getReservedTestStructureNames().contains(name);
			}
		};
	}

	/**
	 * Tests that there is a tree on the wizard in state of new structure.
	 * 
	 * @throws Exception
	 *             for Test
	 */
	@Test
	public void testPageContainsTree() throws Exception {
		AbstractTestStructureWizardPage testPage = ContextInjectionFactory.make(NewTestCaseWizardPage.class,
				getContext());
		testPage.setSelectedTestStructure(new TestSuite());
		testPage.createControl(composite);
		assertNotNull(testPage.getTestStructureTree());
	}

	/**
	 * Tests that an Wizard Page in state for rename for a test-structure with
	 * an existend Name can complete.
	 * 
	 * @throws Exception
	 *             for Test
	 */
	@Test
	public void testCheckPageCompleteOnRename() throws Exception {
		AbstractTestStructureWizardPage testPage = ContextInjectionFactory.make(RenameTestCaseWizardPage.class,
				getContext());
		testPage.setSelectedTestStructure(new ScenarioSuite());
		testPage.createControl(composite);
		assertTrue(testPage.isPageComplete());
	}

}
