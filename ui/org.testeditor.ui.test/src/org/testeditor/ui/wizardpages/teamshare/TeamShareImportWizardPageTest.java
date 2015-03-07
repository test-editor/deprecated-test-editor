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
package org.testeditor.ui.wizardpages.teamshare;

import static org.junit.Assert.assertFalse;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testeditor.core.services.interfaces.TestEditorPlugInService;
import org.testeditor.core.services.interfaces.TestProjectService;
import org.testeditor.ui.adapter.TestProjectServiceAdapter;
import org.testeditor.ui.mocks.TestEditorPluginServiceMock;
import org.testeditor.ui.utilities.TestEditorTranslationService;

/**
 * tests the TeamShareImportWizardPage.
 * 
 */
public class TeamShareImportWizardPageTest {

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
	 * tests the creation of the control and the incompletion of the wizardpage
	 * after creation.
	 * 
	 */
	@Test
	public void testCheckPageCompleteOnImportProject() {
		TeamShareImportProjectWizardPage testPage = ContextInjectionFactory.make(
				TeamShareImportProjectWizardPage.class, getContext());
		testPage.createControl(composite);
		testPage.validatePageAndSetComplete();
		assertFalse(testPage.isPageComplete());
	}

	/**
	 * 
	 * @return the IEclipseContext for the test.
	 */
	private IEclipseContext getContext() {
		IEclipseContext context = EclipseContextFactory.create();
		context.set(Shell.class, null);
		context.set(TestEditorTranslationService.class, getTestEditorTranslationServiceMock());
		context.set(TestEditorPlugInService.class, getTestEditorPluginServiceMock());
		context.set(TestProjectService.class, new TestProjectServiceAdapter());
		context.set(TranslationService.class, getTranslationService());
		return context;
	}

	/**
	 * Only for test.
	 * 
	 * @return a mockup for the TranslationService
	 */
	private TranslationService getTranslationService() {
		return new TranslationService() {
			@Override
			public String translate(String key, String contributorURI) {
				return "fooBar";
			}
		};
	}

	/**
	 * Only for Tests.
	 * 
	 * @return TranslationService Mock Object
	 */
	private TestEditorTranslationService getTestEditorTranslationServiceMock() {
		return new TestEditorTranslationService() {
			@Override
			public String translate(String key, Object... params) {
				return "fooBar";
			}
		};
	}

	/**
	 * 
	 * @return Mock for TestEditorPluginService
	 */
	private TestEditorPlugInService getTestEditorPluginServiceMock() {
		return new TestEditorPluginServiceMock();
	}

}