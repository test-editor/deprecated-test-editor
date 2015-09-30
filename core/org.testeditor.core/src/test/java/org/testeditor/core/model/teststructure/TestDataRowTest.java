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

import org.junit.Test;

/**
 * 
 * test-class for the testDataRow-class.
 * 
 * @author llipinski
 */
public class TestDataRowTest {
	/**
	 * tests the method getColumn.
	 */
	@Test
	public void getColumn() {
		TestDataRow testDataRow = new TestDataRow(new String[] { "A1", "B1", "C1" });
		assertEquals("B1", testDataRow.getColumn(1));
		assertEquals("", testDataRow.getColumn(3));
	}
/**
 *  tests the method setColumn.
 */
	@Test
	public void setColumn() {
		TestDataRow testDataRow = new TestDataRow(new String[] { "A1", "B1", "C1" });
		assertEquals("B1", testDataRow.getColumn(1));
		testDataRow.setColumn(1, "Egon");
		assertEquals("Egon", testDataRow.getColumn(1));

	}
}
