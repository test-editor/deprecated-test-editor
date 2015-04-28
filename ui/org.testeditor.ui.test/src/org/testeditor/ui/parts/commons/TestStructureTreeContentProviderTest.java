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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.junit.Test;
import org.testeditor.core.model.teststructure.TestCase;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestSuite;
import org.testeditor.core.services.interfaces.TestProjectService;
import org.testeditor.core.services.interfaces.TestStructureTreeModel;
import org.testeditor.ui.adapter.TestProjectServiceAdapter;
import org.testeditor.ui.handlers.HandlerMockFactory;
import org.testeditor.ui.parts.commons.tree.TestStructureTreeContentProvider;

/**
 * 
 * Integrationtest for the TestStructureTreeContentProvider.
 * 
 */
public class TestStructureTreeContentProviderTest {

	/**
	 * Tests that get Elements returns Projects.
	 */
	@Test
	public void testGetElements() {
		TestStructureTreeContentProvider testStructureTreeContentProvider = new TestStructureTreeContentProvider();
		assertNotNull(testStructureTreeContentProvider.getElements(getEmptyTestStructureTreeInputService()));
	}

	/**
	 * Tests that get Elements returns Projects.
	 */
	@Test
	public void testHasChildren() {
		TestStructureTreeContentProvider testStructureTreeContentProvider = new TestStructureTreeContentProvider();
		assertFalse(testStructureTreeContentProvider.hasChildren(testStructureTreeContentProvider
				.getElements(getEmptyTestStructureTreeInputService())));
	}

	/**
	 * 
	 * @return an Empty Service
	 */
	private TestStructureTreeModel getEmptyTestStructureTreeInputService() {
		return HandlerMockFactory.getEmptyTestProjectService();
	}

	/**
	 * Tests that get Elements returns Projects with an invalid ProjectConfig.
	 */
	@Test
	public void testGetElementsWithInvalidProjctConfig() {
		IEclipseContext context = EclipseContextFactory.create();
		TestStructureTreeContentProvider testStructureTreeContentProvider = ContextInjectionFactory.make(
				TestStructureTreeContentProvider.class, context);
		Object[] elements = testStructureTreeContentProvider.getElements(getTestProjectServiceWithInvalidConfig());
		assertNotNull(elements);
		assertEquals(1, elements.length);
	}

	/**
	 * Tests that get Elements returns Projects with an invalid ProjectConfig.
	 */
	@Test
	public void testGetChildred() {
		IEclipseContext context = EclipseContextFactory.create();
		TestStructureTreeContentProvider testStructureTreeContentProvider = ContextInjectionFactory.make(
				TestStructureTreeContentProvider.class, context);
		TestSuite suite = new TestSuite();
		suite.addChild(new TestCase());
		Object[] children = testStructureTreeContentProvider.getChildren(suite);
		assertNotNull(children);
		assertEquals(1, children.length);
	}

	/**
	 * 
	 * @return TestProjectService with invalid configuration.
	 */
	private TestProjectService getTestProjectServiceWithInvalidConfig() {
		return new TestProjectServiceAdapter() {

			@Override
			public List<TestProject> getProjects() {
				ArrayList<TestProject> result = new ArrayList<TestProject>();
				result.add(new TestProject());
				return result;
			}
		};
	}

}
