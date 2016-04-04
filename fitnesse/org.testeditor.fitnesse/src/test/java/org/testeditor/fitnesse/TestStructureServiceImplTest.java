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
package org.testeditor.fitnesse;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.testeditor.core.model.teststructure.ScenarioSuite;
import org.testeditor.core.model.teststructure.TestCompositeStructure;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestScenario;
import org.testeditor.core.services.interfaces.TestStructureService;

/**
 * Modul tests for TestStructureServiceImpl.
 *
 */
public class TestStructureServiceImplTest {

	/**
	 * Test for Magic FitnessePages as reserved Words.
	 * 
	 * @throws Exception
	 *             for test.
	 */
	@Test
	public void testIsReservedName() throws Exception {
		TestStructureService service = new TestStructureServiceImpl();
		assertTrue(service.isReservedName(null, "SetUp"));
		assertTrue(service.isReservedName(null, "PageHeader"));
		assertFalse(service.isReservedName(null, "MyTest"));
	}

	/**
	 * Test the Handling of the Lazy Loader.
	 */
	@Test
	public void testGetLazyLoader() {
		final Set<String> monitor = new HashSet<String>();
		TestStructureService service = new TestStructureServiceImpl() {
			@Override
			public Runnable getTestProjectLazyLoader(TestCompositeStructure toBeLoadedLazy) {
				return new Runnable() {

					@Override
					public void run() {
						monitor.add("seen");
					}
				};
			}

		};
		TestProject project = new TestProject();
		project.setChildCountInBackend(-1);
		project.setLazyLoader(service.getTestProjectLazyLoader(project));
		assertFalse(monitor.contains("seen"));
		project.getTestChildren();
		assertTrue(monitor.contains("seen"));
	}

	/**
	 * Test the Creation of an empty History List for non executable
	 * TestStructures.
	 * 
	 * @throws Exception
	 *             on Test failure.
	 */
	@Test
	public void testGetEmptyHistoryOnNotExecutableTestStructures() throws Exception {
		TestStructureServiceImpl service = new TestStructureServiceImpl();
		assertTrue(service.getTestHistory(new TestProject()).isEmpty());
		assertTrue(service.getTestHistory(new TestScenario()).isEmpty());
		assertTrue(service.getTestHistory(new ScenarioSuite()).isEmpty());
	}

}
