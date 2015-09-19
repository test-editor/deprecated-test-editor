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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.Test;

/**
 * 
 * Module Tests for the TestSuite.
 * 
 */
public class TestSuiteTest {

	/**
	 * Tests that a Suite returns all TestCases.
	 * 
	 * @throws Exception
	 *             for Test
	 */
	@Test
	public void testGetAllChildrenSimple() throws Exception {
		TestSuite suite = new TestSuite();
		suite.addChild(new TestCase());
		suite.addChild(new TestScenario());
		assertNotNull(suite.getAllTestChildren());
		assertArrayEquals(suite.getTestChildren().toArray(), suite.getAllTestChildren().toArray());
	}

	/**
	 * Test that a Suite with suite children returns all TestStructures.
	 * 
	 * @throws Exception
	 *             to Test
	 */
	@Test
	public void testGetAllChildrenWithTreeSearch() throws Exception {
		TestSuite suite = new TestSuite();
		TestFlow tc = new TestCase();
		tc.setName("TestCase");
		suite.addChild(tc);
		tc = new TestScenario();
		tc.setName("TestScenario");
		suite.addChild(tc);
		TestSuite childSuite = new TestSuite();
		childSuite.setName("ChildSuite");
		tc = new TestCase();
		tc.setName("TestCaseUnderChild");
		childSuite.addChild(tc);
		suite.addChild(childSuite);
		assertEquals(4, suite.getAllTestChildren().size());
	}

	/**
	 * Test that addChildren sets the set Parent correct.
	 */
	@Test
	public void testSetParentWorksOnAddChildren() {
		TestSuite suite = new TestSuite();
		TestFlow tc = new TestCase();
		assertNull("parent is null", tc.getParent());
		suite.addChild(tc);
		assertSame("suite is now parent", suite, tc.getParent());
	}

	/**
	 * Test that setChildren sets the set Parent correct.
	 */
	@Test
	public void testSetParentWorksOnSetChildren() {
		TestSuite suite = new TestSuite();
		TestFlow tc = new TestCase();
		List<TestStructure> testChildren = new ArrayList<TestStructure>();
		testChildren.add(tc);
		testChildren.add(new TestCase());
		testChildren.add(new TestSuite());
		suite.setTestChildren(testChildren);
		List<TestStructure> children = suite.getTestChildren();
		for (TestStructure testStructure : children) {
			assertSame("suite is now parent", suite, testStructure.getParent());
		}
	}

	/**
	 * TestProjects are not a valid Children for a TestSuite.
	 */
	@Test
	public void testRejectTestProjectAsChildren() {
		TestSuite testSuite = new TestSuite();
		try {
			testSuite.addChild(new TestProject());
			fail("Exception expected");
		} catch (Exception e) {
			assertTrue("IllegalArgumentException expected", e instanceof IllegalArgumentException);
		}
	}

	/**
	 * Test the SourceCode extraction from a TestSuite with referred Test Cases.
	 */
	@Test
	public void testGetSourceCodeForTestSuiteWithReferredTestCases() {
		TestSuite testSuite = new TestSuite();
		TestCase tc = new TestCase();
		tc.setName("Foo");
		TestProject tp = new TestProject();
		tp.setName("Bar");
		tc.setParent(tp);
		testSuite.addReferredTestStructure(tc);
		assertEquals("Expecting Testsuite Source Code", "!contents\n!see .Bar.Foo", testSuite.getSourceCode());
	}

	/**
	 * Test the adding of referred Tests.
	 */
	@Test
	public void testAddingRemovingReferredTestCases() {
		TestSuite testSuite = new TestSuite();
		assertNotNull("Expecting a list", testSuite.getReferredTestStrcutures());
		assertTrue("Expecting an empty list", testSuite.getReferredTestStrcutures().isEmpty());
		TestCase tc = new TestCase();
		tc.setName("Foo");
		TestProject tp = new TestProject();
		tp.setName("Bar");
		tc.setParent(tp);
		testSuite.addReferredTestStructure(tc);
		assertFalse("Expecting a list with referred teststructure.", testSuite.getReferredTestStrcutures().isEmpty());
		testSuite.removeReferredTestStructure(tc);
		assertTrue("Expecting an empty list with referred teststructure.", testSuite.getReferredTestStrcutures()
				.isEmpty());
	}

	/**
	 * Tests that null values are refused.
	 * 
	 */
	@Test
	public void testRefusingNullReferredTestStructures() {
		TestSuite testSuite = new TestSuite();
		try {
			testSuite.addReferredTestStructure(null);
			fail();
		} catch (AssertionError e) {
			testSuite.addReferredTestStructure(new TestCase());
		}
	}

	/**
	 * Test the collection of all Tests.
	 */
	@Test
	public void testGetAllTests() {
		TestSuite suite = new TestSuite();
		TestCase directChild = new TestCase();
		suite.addChild(directChild);
		TestSuite childSuite = new TestSuite();
		suite.addChild(childSuite);
		TestCase indirectChild = new TestCase();
		suite.addChild(indirectChild);
		TestCase referredTC = new TestCase();
		suite.addChild(referredTC);
		Set<TestStructure> allTests = suite.getAllTestChildrensAndReferedTestcases();
		assertTrue(allTests.contains(directChild));
		assertTrue(allTests.contains(indirectChild));
		assertTrue(allTests.contains(referredTC));
	}
	
}
