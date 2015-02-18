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
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

/**
 * 
 * Modultests for TestCompositeStructure.
 * 
 */
public class TestCompositeStructureTest {

	/**
	 * Test that TestComposite calls the getTestStructreByFullName Methos of the
	 * TestProject.
	 */
	@Test
	public void testGetTestStructureByNameCallsTestProject() {
		final TestCase testCase = new TestCase();
		TestProject tp = new TestProject() {
			@Override
			public TestStructure getTestChildByFullName(String fullName) {
				return testCase;
			}
		};
		TestSuite suite = new TestSuite();
		tp.addChild(suite);
		TestCompositeStructure tcs = getOUT();
		suite.addChild(tcs);
		assertSame(testCase, tcs.getTestChildByFullName(null));
	}

	/**
	 * Tast has children based on the property childCOunt and not the list
	 * children.
	 */
	@Test
	public void testHasChildren() {
		TestCompositeStructure tcs = getOUT();
		tcs.setChildCountInBackend(0);
		assertFalse(tcs.hasChildren());
		tcs.setChildCountInBackend(10);
		assertTrue(tcs.hasChildren());
	}

	/**
	 * Tests the lazy loading behavior.
	 * 
	 * @throws Exception
	 *             on Test Error
	 */
	public void testLazyLoading() throws Exception {
		final TestCompositeStructure out = getOUT();
		out.setChildCountInBackend(3);
		assertEquals("Expecting lazy doesn't run if no runnable is set.", 0, out.getTestChildren().size());

		out.setLazyLoader(new Runnable() {

			@Override
			public void run() {
				out.addChild(new TestCase());
			}
		});
		assertEquals("Expecting lazy is one run", 1, out.getTestChildren().size());
		assertEquals("Expecting lazy is second run", 2, out.getTestChildren().size());
		assertEquals("Expecting lazy is last time runs", 3, out.getTestChildren().size());
		assertEquals("Expecting lazy is not running", 3, out.getTestChildren().size());
	}

	/**
	 * 
	 * Test the access of all complete TestStructures like TestSuite and
	 * TestCases vs. get all test objects (TestSuite, TestCase, ScenarioSuite
	 * and TestScenario).
	 * 
	 * @throws Exception
	 *             on Test Error
	 */
	@Test
	public void testAllTestsWithScenarien() throws Exception {
		TestProject procject = new TestProject();
		TestSuite testSuite = new TestSuite();
		procject.addChild(testSuite);
		TestCase tc = new TestCase();
		tc.setName("foo");
		procject.addChild(tc);
		tc = new TestCase();
		tc.setName("foo1");
		testSuite.addChild(tc);
		tc = new TestCase();
		tc.setName("foo3");
		testSuite.addChild(tc);
		ScenarioSuite scenarioSuite = new ScenarioSuite();
		procject.addChild(scenarioSuite);
		scenarioSuite.addChild(new TestScenario());
		scenarioSuite.addChild(new TestScenario());
		assertEquals(4, procject.getAllTestChildren().size());
		assertEquals(6, procject.getAllTestChildrenWithScenarios().size());
	}

	/**
	 * Tests the Handling of the ChildCount of the Backend.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testManagmentOfChildCountInBackend() {
		final TestCompositeStructure out = getOUT();
		out.setChildCountInBackend(3);
		assertTrue(out.hasChildren());
		out.addChild(new TestCase());
		assertTrue(out.hasChildren());
		final Map<String, String> monitor = new HashMap<String, String>();
		monitor.put("seen", "false");
		out.setLazyLoader(new Runnable() {

			@Override
			public void run() {
				List<TestStructure> testChildren = new ArrayList<TestStructure>();
				testChildren.add(new TestCase());
				testChildren.add(new TestCase());
				testChildren.add(new TestCase());
				out.setTestChildren(testChildren);
				monitor.put("seen", "true");
			}
		});
		assertEquals(3, out.getTestChildren().size());
		assertTrue("Lazy Loader executed.", Boolean.parseBoolean(monitor.get("seen")));
		monitor.put("seen", "false");
		assertEquals(3, out.getTestChildren().size());
		assertFalse("Lazy Loader is not a second time executed.", Boolean.parseBoolean(monitor.get("seen")));
		TestCase testCase = new TestCase();
		testCase.setName("foo");
		out.addChild(testCase);
		assertEquals(4, out.getTestChildren().size());
		assertFalse("Lazy Loader is not a second time executed.", Boolean.parseBoolean(monitor.get("seen")));
		out.addChild(testCase);
		assertEquals("Don't add an object a second time.", 4, out.getTestChildren().size());
		out.removeChild(testCase);
		assertEquals(3, out.getTestChildren().size());
		out.removeChild(testCase);
		assertEquals("Don't remove Object that is not in list", 3, out.getTestChildren().size());
		assertFalse("Lazy Loader is not a second time executed.", Boolean.parseBoolean(monitor.get("seen")));
	}

	/**
	 * 
	 * @return the Object under Test.
	 */
	private TestCompositeStructure getOUT() {
		return new TestCompositeStructure() {

			@Override
			public String getTypeName() {
				return null;
			}

			@Override
			public String getSourceCode() {
				return null;
			}

			@Override
			public String getPageType() {
				return null;
			}
		};
	}

}
