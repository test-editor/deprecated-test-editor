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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.services.interfaces.ServiceLookUpForTest;
import org.testeditor.core.services.interfaces.TestEditorReservedNamesService;
import org.testeditor.core.services.interfaces.TestProjectService;
import org.testeditor.core.services.interfaces.TestStructureService;
import org.testeditor.ui.adapter.TestStructureServiceAdapter;
import org.testeditor.ui.utilities.TestEditorTranslationService;

/**
 * test class to test the NewProjectWizard.
 * 
 * 
 */
public class NewTestProjectTest {

	private Shell shell;
	private Composite composite;

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
	 * test invalid name without capital letters.
	 */
	@Test
	public void testNewTestProjectinvalidNameNoCapitalLetter() {
		NewTestProjectWizardPage page = ContextInjectionFactory.make(NewTestProjectWizardPage.class, getContextMock());
		assertFalse(page.isNameValid("otto"));
	}

	/**
	 * test invalid name with only one capital letter.
	 */
	@Test
	public void testNewTestProjectinvalidNameOnlyOneCapitalLetter() {
		NewTestProjectWizardPage page = ContextInjectionFactory.make(NewTestProjectWizardPage.class, getContextMock());
		assertFalse(page.isNameValid("Otto"));
	}

	/**
	 * test invalid name with double capital letter.
	 */
	@Test
	public void testNewTestProjectinvalidNameDoubleCapitalLetter() {
		NewTestProjectWizardPage page = ContextInjectionFactory.make(NewTestProjectWizardPage.class, getContextMock());
		assertFalse(page.isNameValid("OTto"));
	}

	/**
	 * test invalid name with correct writing.
	 */
	@Test
	public void testNewTestProjectCorrectWrintingOfTheName() {
		NewTestProjectWizardPage page = ContextInjectionFactory.make(NewTestProjectWizardPage.class, getContextMock());
		assertTrue(page.isNameValid("OttO"));
	}

	/**
	 * test the createControl-method.
	 */
	@Test
	public void testCreateControl() {
		NewTestProjectWizardPage page = ContextInjectionFactory.make(NewTestProjectWizardPage.class, getContextMock());
		page.createControl(composite);
		assertEquals(page.getTitle(), "fooBar");
	}

	/**
	 * test the doesNameAlreadyExist method.
	 * 
	 * @throws SystemException
	 *             at testProjectService.createNewProject("EmiL", "8379");
	 * @throws IOException
	 *             at testProjectService.createNewProject("EmiL", "8379");
	 * 
	 */
	@Test
	public void testDoesNameAlreadyExist() throws IOException, SystemException {
		NewTestProjectWizardPage page = ContextInjectionFactory.make(NewTestProjectWizardPage.class, getContextMock());
		TestProjectService testProjectService = getContextMock().get(TestProjectService.class);
		TestProject createdNewProject = testProjectService.createNewProject("EmiL");
		page.createControl(composite);
		assertFalse(page.doesNameAlreadyExist("HugO"));
		assertTrue(page.doesNameAlreadyExist("EmiL"));
		testProjectService.deleteProject(createdNewProject);
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
