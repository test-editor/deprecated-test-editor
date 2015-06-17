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
package org.testeditor.ui.analyzer;

import java.util.ArrayList;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.ui.analyzer.errormodel.ErrorContainer;

/**
 * 
 * Integration test for ValidateResultsView.
 * 
 */
public class ValidateResultsViewInTest {

	private Shell shell;

	/**
	 * Test that the View is createable.
	 * 
	 * @throws Exception
	 *             on creation problem.
	 */
	@Test
	public void testViewCreation() throws Exception {
		BundleContext bundleContext = FrameworkUtil.getBundle(getClass()).getBundleContext();
		IEclipseContext context = EclipseContextFactory.getServiceContext(bundleContext);
		context.set(Composite.class, shell);
		context.set(TranslationService.class, new TranslationService() {
		});
		ValidateResultsView view = ContextInjectionFactory.make(ValidateResultsView.class, context);
		Assert.assertNotNull(view);
		view.setErrorContainers(new ArrayList<ErrorContainer>(), new TestProject());
	}

	/**
	 * Setups a shell for integration tests.
	 */
	@Before
	public void setup() {
		shell = new Shell(Display.getDefault());
	}

	/**
	 * Cleans up UI handles.
	 */
	@After
	public void tearDown() {
		shell.dispose();
	}

}
