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
package org.testeditor.core.model.teststructure;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

/**
 * 
 * Modultest for TestProject.
 * 
 */
public class TestProjectTest {

	/**
	 * Check that a Testproject has no parent all the time.
	 * 
	 * @throws Exception
	 *             for Test
	 */
	@Test
	public void testEnsureParentIsEveryTimeNull() throws Exception {
		TestProject project = new TestProject();
		assertNull("Parent is null after Initialize", project.getParent());
		project.setParent(new TestSuite());
		assertNull("SetParent doesn't work", project.getParent());
		try {
			new TestSuite().addChild(project);
		} catch (Exception e) {
			assertNull("Adding to a Suite doesn't set the parent", project.getParent());
		}
		assertNull("Adding to a Suite doesn't set the parent", project.getParent());
	}

	/**
	 * Tests the access to the special TestSuite to collect all Test Scenrarios.
	 */
	@Test
	public void testGetScenarioRoot() {
		TestProject testProject = new TestProject();
		testProject.addChild(new TestCase());
		testProject.addChild(new TestSuite());
		testProject.addChild(new ScenarioSuite());
		testProject.addChild(new TestCase());
		TestStructure scenarioRoot = testProject.getScenarioRoot();
		assertNotNull("Expecting ScenarioRoot", scenarioRoot);
		assertTrue("Expecting ScenarioRoot Type", scenarioRoot instanceof ScenarioSuite);
	}

	/**
	 * Tests the search for a teststructure in the Tree by the full name.
	 */
	@Test
	public void testGetTestChildByFullName() {
		TestProject project = new TestProject();
		project.setName("A");
		TestSuite b = new TestSuite();
		b.setName("B");
		project.addChild(b);
		TestCase c = new TestCase();
		c.setName("C");
		b.addChild(c);
		TestSuite h = new TestSuite();
		h.setName("H");
		project.addChild(h);
		TestCase i = new TestCase();
		i.setName("I");
		assertEquals(c, project.getTestChildByFullName("A.B.C"));
		assertEquals(b, project.getTestChildByFullName("A.B"));
		assertEquals(c, b.getTestChildByFullName("A.B.C"));
		assertEquals(c, h.getTestChildByFullName("A.B.C"));
	}

	/**
	 * Test the equals method.
	 * 
	 * @throws Exception
	 *             for test
	 */
	@Test
	public void testEquals() throws Exception {
		TestProject testProject = new TestProject();
		testProject.setName("TP");
		TestSuite testSuite = new TestSuite();
		testSuite.setName("TP");
		assertFalse(testProject.equals(testSuite));
		TestProject testProject2 = new TestProject();
		assertFalse(testProject.equals(testProject2));
		testProject2.setName("foo");
		assertFalse(testProject.equals(testProject2));
		testProject2.setName("TP");
		assertTrue(testProject.equals(testProject2));
	}

	/**
	 * Test the creation of an Empty Parent List on TestProject.
	 */
	@Test
	public void testGetAllParents() {
		TestProject testProject = new TestProject();
		List<TestStructure> allParents = testProject.getAllParents();
		assertNotNull(allParents);
		assertTrue(allParents.isEmpty());
	}

}
