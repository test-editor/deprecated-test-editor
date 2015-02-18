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
package org.testeditor.ui.parts.systemconfiguration;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.nebula.widgets.grid.GridCellRenderer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.FrameworkUtil;
import org.testeditor.ui.adapter.MPartAdapter;
import org.testeditor.ui.utilities.TestEditorTranslationService;

/**
 * 
 * Integrationstests for SystemConfigurationEditor.
 * 
 */
public class SystemConfigurationEditorTest {

	private Shell shell;

	/**
	 * Test the creation and ui creation of the editor.
	 * 
	 * @throws Exception
	 *             on problems accessing the backend.
	 */
	@Test
	public void testCreatoinandInitOfTheEditor() throws Exception {
		SystemConfigurationEditor editor = ContextInjectionFactory.make(SystemConfigurationEditor.class,
				getTestContext());
		assertNotNull(editor);
	}

	/**
	 * Test that the hiding cell renderer is set.
	 * 
	 */
	@Test
	public void testHidingCellRenderer() {
		IEclipseContext context = getTestContext();
		SystemConfigurationEditor editor = ContextInjectionFactory.make(SystemConfigurationEditor.class, context);
		GridCellRenderer hidingRenderer = editor.getEmptyCellHidingRenderer();
		assertNull(hidingRenderer.computeSize(null, 1, 1, null));
	}

	/**
	 * 
	 * @return context used to create object under test.
	 */
	private IEclipseContext getTestContext() {
		IEclipseContext context = EclipseContextFactory.getServiceContext(FrameworkUtil.getBundle(getClass())
				.getBundleContext());
		context.set(MPart.class, new MPartAdapter());
		context.set(Logger.class, null);
		context.set(TestEditorTranslationService.class, new TestEditorTranslationService() {
			@Override
			public String translate(String key, Object... params) {
				return "";
			}
		});
		context.set(Composite.class, shell);
		return context;
	}

	/**
	 * Creates a shell to Test SWT components.
	 */
	@Before
	public void setUp() {
		shell = new Shell();
	}

	/**
	 * Disposes the SWT components.
	 */
	@After
	public void tearDown() {
		shell.dispose();
	}

}
