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
package org.testeditor.ui.parts.testExplorer;

import static org.junit.Assert.assertTrue;

import java.util.HashSet;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.junit.Test;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.services.interfaces.TestProjectService;
import org.testeditor.metadata.core.MetaDataService;
import org.testeditor.ui.adapter.TestProjectServiceAdapter;
import org.testeditor.ui.parts.testExplorer.handler.RefreshTestExplorer;

/**
 * IntegrationTest for RefreshTestExplorer.
 * 
 */
public class RefreshTestExplorerTest {

	/**
	 * Tests that the handler drops the current loaded projects and refreshes
	 * the tree.
	 */
	@Test
	public void testRefreshAndDropProjectActionsOfTheHandler() {
		final HashSet<String> monitor = new HashSet<String>();
		IEclipseContext context = EclipseContextFactory.create();
		context.set(TestProjectService.class, new TestProjectServiceAdapter() {
			@Override
			public void reloadProjectList() throws SystemException {
				monitor.add("reloaded");
				super.reloadProjectList();
			}
		});
		context.set(MetaDataService.class, null);
		RefreshTestExplorer refreshTestExplorer = ContextInjectionFactory.make(RefreshTestExplorer.class, context);
		refreshTestExplorer.refreshTestExplorer();
		assertTrue("Refresh Event was send to the event bus.", monitor.contains("reloaded"));
	}
}
