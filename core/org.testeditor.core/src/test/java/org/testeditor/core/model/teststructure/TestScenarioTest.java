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
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Test;

/**
 * 
 * modultest for {@link TestScenario}.
 * 
 * @author llipinski
 */
public class TestScenarioTest {

	private TestScenario testScenario = new TestScenario();

	/**
	 * tests adding of a parameter.
	 */
	@Test
	public void testAddTestparameterString() {
		testScenario.addTestparameter("page");
		assertEquals("page", testScenario.getTestParameters().get(0));
	}

	/**
	 * tests the getPageType-method.
	 */
	@Test
	public void testGetPageType() {
		assertEquals("TESTSCENARIO", testScenario.getPageType());
	}

	/**
	 * tests the getSoureCode method.
	 */
	@Test
	public void testGetSourceCode() {
		testScenario.setName("TestScenario");
		testScenario.addTestparameter("page");
		assertEquals("!|scenario |TestScenario _|page|\n", testScenario.getSourceCode());
	}

	/**
	 * tests the getParameters method.
	 */
	@Test
	public void testGetParameters() {
		testScenario.addTestparameter("page");
		testScenario.addTestparameter("subPage");
		assertEquals("page", testScenario.getTestParameters().get(0));
		assertEquals("subPage", testScenario.getTestParameters().get(1));
	}

	/**
	 * tests the getTypeName method.
	 */
	@Test
	public void testGetTypeName() {
		assertEquals("scenario", testScenario.getTypeName());
	}

	/**
	 * tests the setTestParameters method.
	 */
	@Test
	public void testSetTestParameters() {
		ArrayList<String> testParameters = new ArrayList<String>();
		testParameters.add("page");
		testParameters.add("subPage");
		testScenario.setTestParameters(testParameters);
		assertEquals("page", testScenario.getTestParameters().get(0));
		assertEquals("subPage", testScenario.getTestParameters().get(1));
	}

	/**
	 * test the compareTo method.
	 */
	@Test
	public void compareToTest() {
		TestScenario testScenarioComp = new TestScenario();
		assertEquals(0, testScenario.compareTo(testScenarioComp));
		assertEquals(0, testScenarioComp.compareTo(testScenario));
		testScenario.setName("A");

		testScenarioComp.setName("");
		assertEquals(1, testScenario.compareTo(testScenarioComp));
		assertEquals(-1, testScenarioComp.compareTo(testScenario));

		testScenarioComp.setName("A");
		assertEquals(0, testScenario.compareTo(testScenarioComp));
		assertEquals(0, testScenarioComp.compareTo(testScenario));

		testScenarioComp.setName("B");
		assertEquals(-1, testScenario.compareTo(testScenarioComp));
		assertEquals(1, testScenarioComp.compareTo(testScenario));

	}

	/**
	 * test the isScenarioIncluded method.
	 */
	@Test
	public void testIsScenarioIncluded() {
		TestScenario testScenario = new TestScenario();
		testScenario.addInclude("DemoWebTests.TestSzenarien.TestSzenario");
		assertTrue(testScenario.isScenarioIncluded("TestSzenario"));
	}
	
	
}
