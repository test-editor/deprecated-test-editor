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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.testeditor.core.model.teststructure.TestCase;
import org.testeditor.core.model.teststructure.TestSuite;
import org.testeditor.ui.parts.commons.tree.filter.TestCaseFilter;

/**
 * 
 * Modultest for the TestCaseFilter.
 * 
 */
public class TestCaseFilterTest {

	/**
	 * Test that Testcases are deselected.
	 * 
	 * @throws Exception
	 *             for Test
	 */
	@Test
	public void testSelectNoTestCase() throws Exception {
		TestCaseFilter filter = new TestCaseFilter();
		assertFalse(filter.select(null, null, new TestCase()));
		assertTrue(filter.select(null, null, new TestSuite()));
		assertTrue(filter.select(null, null, new Object()));
	}

}
