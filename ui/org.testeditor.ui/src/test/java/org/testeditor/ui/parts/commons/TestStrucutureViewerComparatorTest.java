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

import org.junit.Test;
import org.testeditor.core.model.teststructure.TestCase;
import org.testeditor.core.model.teststructure.TestSuite;

/**
 * 
 * Modultest for TestStrucutureViewerComparator.
 * 
 */
public class TestStrucutureViewerComparatorTest {

	/**
	 * Test the order of Teststructures. Display first Testsuite and second
	 * Testcases.
	 * 
	 * @throws Exception
	 *             for Test
	 */
	@Test
	public void testComparatorForTestSuiteBeforTestCase() throws Exception {
		TestStructureViewerComparator comparator = new TestStructureViewerComparator();
		assertEquals(-1, comparator.compare(null, new TestSuite(), new TestCase()));
	}

	/**
	 * Test the alphabetic order of a teststrucutre name on equal type.
	 * 
	 * @throws Exception
	 *             for Test
	 */
	@Test
	public void testComparatorForTestcasesStringbased() throws Exception {
		TestCase tc1 = new TestCase();
		tc1.setName("abc");
		TestCase tc2 = new TestCase();
		tc2.setName("xyz");
		TestStructureViewerComparator comparator = new TestStructureViewerComparator();
		assertTrue(0 > comparator.compare(null, tc1, tc2));
	}

	/**
	 * Tests that the Test Component is at the End of the List.
	 * 
	 * @throws Exception
	 *             for Test
	 */
	@Test
	public void testTestComponentIsTheLast() throws Exception {
		TestStructureViewerComparator comparator = new TestStructureViewerComparator();
		TestCase tc = new TestCase();
		TestSuite ts = new TestSuite();
		TestSuite tComponent = new TestSuite();
		tComponent.setName("TestKomponenten");
		assertTrue(0 > comparator.compare(null, ts, tc));
		assertTrue(0 > comparator.compare(null, tc, tComponent));
	}

}
