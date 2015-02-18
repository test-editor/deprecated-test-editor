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

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.junit.Test;
import org.testeditor.core.services.interfaces.ServiceLookUpForTest;
import org.testeditor.core.util.FileLocatorService;

/**
 * 
 * Integrationtest for the ManualHandler.
 * 
 */
public class ManualHandlerTest {

	/**
	 * Test the lookup for the Manual in the TE Bundles.
	 * 
	 * @throws Exception
	 *             for test
	 */
	@Test
	public void testFindBundleFile() throws Exception {
		IEclipseContext context = EclipseContextFactory.create();
		context.set(FileLocatorService.class, ServiceLookUpForTest.getService(FileLocatorService.class));
		UserManualHandler handler = ContextInjectionFactory.make(UserManualHandler.class, context);
		String bundleFile = handler.findBundleFile("org.testeditor.demo");
		assertTrue("manual found", new File(bundleFile + File.separator + "TestEditorUserManualDe.pdf").exists());
	}

}
