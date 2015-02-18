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
import static org.junit.Assert.assertTrue;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.testeditor.ui.utilities.TestEditorTranslationService;

/**
 * 
 * Integration Tests for AboutHandler.
 * 
 */
public class AboutHandlerTest {

	/**
	 * Test the Version Information of the TestEditor Bundles of the about
	 * Dialog.
	 * 
	 * @throws Exception
	 *             for Test
	 */
	@Test
	public void testVerisonInformations() throws Exception {
		assertEquals("Empty String", "", new AboutHandler().getInformationString());
		Bundle bundle = FrameworkUtil.getBundle(this.getClass());
		IEclipseContext context = EclipseContextFactory.getServiceContext(bundle.getBundleContext());
		context.set(TestEditorTranslationService.class, new TestEditorTranslationService() {
			@Override
			public String translate(String key, Object... params) {
				return "";
			}
		});
		AboutHandler handler = ContextInjectionFactory.make(AboutHandler.class, context);
		assertTrue("Bundle is listed", handler.getInformationString().indexOf(bundle.getSymbolicName()) > -1);
	}

}
