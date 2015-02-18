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
package org.testeditor.ui.parts.commons;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.FrameworkUtil;
import org.testeditor.ui.parts.commons.tree.TestStructureTree;

/**
 * 
 * Integration Test for the TestStructureTree.
 * 
 */
public class TestStructureTreeTest {

	private Shell shell;

	/**
	 * Tests the Setup of the Filter.
	 * 
	 * @throws Exception
	 *             for Test
	 */
	@Test
	public void testShowOnlyParentStructures() throws Exception {
		TestStructureTree tree = ContextInjectionFactory.make(TestStructureTree.class, getContext());
		tree.createUI(shell, null);
		assertEquals(1, tree.getTreeViewer().getFilters().length);
		tree.showOnlyParentStructures();
		assertEquals(4, tree.getTreeViewer().getFilters().length);
		tree.showOnlyParentStructuresOfSuites();
		assertEquals(4, tree.getTreeViewer().getFilters().length);
		tree.showParentTestStructesAndChildren();
		assertEquals(1, tree.getTreeViewer().getFilters().length);
	}

	/**
	 * Tests that the default style is a MultiSelection.
	 */
	@Test
	public void testSingleAndMultiSelectionStyle() {
		TestStructureTree tree = ContextInjectionFactory.make(TestStructureTree.class, getContext());
		tree.createUI(shell, null);
		assertTrue("Multi is slected", (tree.getTreeViewer().getTree().getStyle() & SWT.MULTI) == SWT.MULTI);
		tree.createUI(shell, null, SWT.SINGLE);
		assertTrue("Single is slected", (tree.getTreeViewer().getTree().getStyle() & SWT.SINGLE) == SWT.SINGLE);
	}

	/**
	 * Tests the Setup of the Filter for Test Components.
	 * 
	 */
	@Test
	public void testShowOnlyTestKomponentsSuite() {
		TestStructureTree tree = ContextInjectionFactory.make(TestStructureTree.class, getContext());
		tree.createUI(shell, null);
		assertEquals(1, tree.getTreeViewer().getFilters().length);
		tree.showOnlyTestScenarioSuites();
		assertEquals(4, tree.getTreeViewer().getFilters().length);
	}

	/**
	 * Init UI Element for Test.
	 */
	@Before
	public void setUp() {
		shell = new Shell();
	}

	/**
	 * Dispose UI.
	 */
	@After
	public void tearDown() {
		shell.dispose();
	}

	/**
	 * 
	 * @return a mock for IEClipseContext
	 */
	private IEclipseContext getContext() {
		IEclipseContext context = EclipseContextFactory.getServiceContext(FrameworkUtil.getBundle(getClass())
				.getBundleContext());
		return context;
	}

}
