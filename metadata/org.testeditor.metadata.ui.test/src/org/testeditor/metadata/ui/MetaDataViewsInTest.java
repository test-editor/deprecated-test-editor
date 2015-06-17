package org.testeditor.metadata.ui;

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

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.testeditor.metadata.ui.parts.MetaDataAdminView;
import org.testeditor.metadata.ui.parts.MetaDataTaggingView;

/**
 * 
 * Integration test for AdministrationView.
 * 
 */
public class MetaDataViewsInTest {

	private Shell shell;

	/**
	 * Test that the View is createable.
	 * 
	 * @throws Exception
	 *             on creation problem.
	 */
	@Test
	public void testAdminViewCreation() throws Exception {
		BundleContext bundleContext = FrameworkUtil.getBundle(getClass()).getBundleContext();
		IEclipseContext context = EclipseContextFactory.getServiceContext(bundleContext);
		context.set(Composite.class, shell);
		context.set(TranslationService.class, new TranslationService() {
		});
		MetaDataAdminView view = ContextInjectionFactory.make(MetaDataAdminView.class, context);
		Assert.assertNotNull(view);

	}

	/**
	 * Test that the View is createable.
	 * 
	 * @throws Exception
	 *             on creation problem.
	 */
	@Test
	public void testTaggingViewCreation() throws Exception {
		BundleContext bundleContext = FrameworkUtil.getBundle(getClass()).getBundleContext();
		IEclipseContext context = EclipseContextFactory.getServiceContext(bundleContext);
		context.set(Composite.class, shell);
		context.set(TranslationService.class, new TranslationService() {
		});
		MetaDataTaggingView view = ContextInjectionFactory.make(MetaDataTaggingView.class, context);
		Assert.assertNotNull(view);

	}

	/**
	 * Setups a shell for integration tests.
	 */
	@Before
	public void setup() {
		shell = new Shell();
	}

	/**
	 * Cleans up UI handles.
	 */
	@After
	public void tearDown() {
		shell.dispose();
	}

}
