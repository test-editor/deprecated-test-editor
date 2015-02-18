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
package org.testeditor.core.model.testresult;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

/**
 * 
 * Module test of ActionResultTable.
 *
 */
public class ActionResultTableTest {

	/**
	 * 
	 * Tests the adding reading of rows to the ActionResultTable.
	 * 
	 * @throws Exception
	 *             on Test abort.
	 */
	@Test
	public void testBasicAddAndReadingOfRows() throws Exception {
		ActionResultTable table = new ActionResultTable();
		List<String> row = table.createNewRow();
		assertNotNull(row);
		assertTrue(table.getRows().contains(row));
	}

	/**
	 * 
	 * Test the Handling of the Name of ActionResultTable.
	 * 
	 * @throws Exception
	 *             on Test abort.
	 */
	@Test
	public void testNameHandling() throws Exception {
		ActionResultTable table = new ActionResultTable();
		table.setName("MyName");
		assertEquals("MyName", table.getName());
		assertEquals(table.getName(), table.toString());
	}

}
