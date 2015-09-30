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
package org.testeditor.core.model.action;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * 
 * Modultest for TechnicalBindingTypeComperator.
 * 
 */
public class TechnicalBindingTypeComperatorTest {

	/**
	 * Test the simple Compare with two TechnicalBindingType.
	 */
	@Test
	public void testCompare() {
		TechnicalBindingType technicalBindingType = new TechnicalBindingType();
		TechnicalBindingType technicalBindingTypeComp = new TechnicalBindingType();
		technicalBindingType.setSorting(1);

		int compare = technicalBindingType.compareWith(technicalBindingTypeComp);
		assertEquals(-1, compare);
		compare = technicalBindingTypeComp.compareWith(technicalBindingType);
		assertEquals(1, compare);

	}

	/**
	 * Test Comparing with two TechnicalBindingType with different Sorting.
	 */
	@Test
	public void testCompareBothNotNull() {
		TechnicalBindingType technicalBindingType = new TechnicalBindingType();
		technicalBindingType.setSorting(1);
		TechnicalBindingType technicalBindingTypeComp = new TechnicalBindingType();
		technicalBindingTypeComp.setSorting(2);
		int compare = technicalBindingType.compareWith(technicalBindingTypeComp);
		assertEquals(-1, compare);
		compare = technicalBindingTypeComp.compareWith(technicalBindingType);
		assertEquals(1, compare);
	}

	/**
	 * Test Comparing with two TechnicalBindingType with null Sorting.
	 */
	@Test
	public void testCompareBothNull() {
		TechnicalBindingType technicalBindingType = new TechnicalBindingType();
		TechnicalBindingType technicalBindingTypeComp = new TechnicalBindingType();

		int compare = technicalBindingType.compareWith(technicalBindingTypeComp);
		assertEquals(0, compare);
	}

	/**
	 * Test Comparing with two TechnicalBindingType by names.
	 */
	@Test
	public void testCompareBothNullButIds() {
		TechnicalBindingType technicalBindingType = new TechnicalBindingType();
		technicalBindingType.setId("A");
		TechnicalBindingType technicalBindingTypeComp = new TechnicalBindingType();
		technicalBindingTypeComp.setId("B");

		int compare = technicalBindingType.compareWith(technicalBindingTypeComp);
		assertEquals(-1, compare);
		compare = technicalBindingTypeComp.compareWith(technicalBindingType);
		assertEquals(1, compare);
		technicalBindingTypeComp.setId("A");
		compare = technicalBindingType.compareWith(technicalBindingType);
		assertEquals(0, compare);
	}

}
