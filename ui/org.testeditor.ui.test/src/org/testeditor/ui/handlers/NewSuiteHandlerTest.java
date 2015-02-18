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

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.junit.Test;
import org.testeditor.core.services.interfaces.TestEditorPlugInService;
import org.testeditor.core.services.interfaces.TestProjectService;
import org.testeditor.core.services.interfaces.TestStructureService;
import org.testeditor.ui.adapter.TranslationServiceAdapter;
import org.testeditor.ui.mocks.TestEditorPluginServiceMock;

/**
 * 
 * Integrationtest for NewSuiteHandler.
 * 
 */
public class NewSuiteHandlerTest {

	/**
	 * Test if the Handler is enabled or not by executing it from the Main Menu.
	 * The Handler is enabled only if there is any project.
	 * 
	 * @throws Exception
	 *             for Test
	 */
	@Test
	public void testCanExecuteFromMenu() throws Exception {
		IEclipseContext context = EclipseContextFactory.create();
		context.set(TestStructureService.class, null);
		context.set(TranslationService.class, new TranslationServiceAdapter().getTranslationService());
		context.set(TestProjectService.class, HandlerMockFactory.getEmptyTestProjectService());
		context.set(IServiceConstants.ACTIVE_SHELL, null);
		context.set(EPartService.class, null);
		context.set(TestEditorPlugInService.class, new TestEditorPluginServiceMock());
		NewTestSuiteHandler newSuiteHandler = ContextInjectionFactory.make(NewTestSuiteHandler.class, context);
		assertFalse("Expecting can not Exceute on non projects in the workspace.",
				newSuiteHandler.canExecute(context, "true"));
		context.set(TestProjectService.class, HandlerMockFactory.getNonEmptyTestProjectService());
		newSuiteHandler = ContextInjectionFactory.make(NewTestSuiteHandler.class, context);
		assertTrue("Expecting can execute on workspace with projects.", newSuiteHandler.canExecute(context, "true"));
	}

}
