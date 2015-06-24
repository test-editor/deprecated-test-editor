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

import static org.junit.Assert.assertNotNull;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.services.interfaces.TestEditorPlugInService;
import org.testeditor.core.services.interfaces.TestEditorReservedNamesService;
import org.testeditor.core.services.interfaces.TestProjectService;
import org.testeditor.core.services.interfaces.TestStructureService;
import org.testeditor.ui.adapter.TestProjectServiceAdapter;
import org.testeditor.ui.adapter.TestStructureServiceAdapter;
import org.testeditor.ui.mocks.TestEditorPluginServiceMock;
import org.testeditor.ui.utilities.TestEditorTranslationService;

/**
 * Integrationtests for NewProjectHandler.
 * 
 */
public class NewProjectHandlerTest {

	private Shell shell;
	private IEclipseContext context;

	/**
	 * Creation of Wizard.
	 */
	@Test
	public void testGetWizard() {
		NewProjectHandler projectHandler = ContextInjectionFactory.make(NewProjectHandler.class, context);
		assertNotNull(projectHandler.getWizard(context));
	}

	/**
	 * Creation of Wizard.
	 */
	@Test
	public void testGetWizardPage() {
		NewProjectHandler projectHandler = ContextInjectionFactory.make(NewProjectHandler.class, context);
		assertNotNull(projectHandler.getNewTestStructureWizardPage(new TestProject(), context));
	}

	/**
	 * Setups resources for test.
	 */
	@Before
	public void setup() {
		shell = new Shell(Display.getDefault());
		context = EclipseContextFactory.create();
		context.set(TestStructureService.class, new TestStructureServiceAdapter());
		context.set(TestProjectService.class, new TestProjectServiceAdapter());
		context.set(TestEditorPlugInService.class, new TestEditorPluginServiceMock());
		context.set("activeShell", shell);
		context.set(EPartService.class, null);
		context.set(TestEditorTranslationService.class, null);
		context.set(TestEditorReservedNamesService.class, null);
		context.set(TranslationService.class, new TranslationService() {
		});
	}

	/**
	 * dispose the os handles.
	 */
	@After
	public void dispose() {
		shell.dispose();
	}

}
