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
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.testeditor.core.exceptions.SystemException;

/**
 * 
 * test for the TestData.class.
 * 
 */
public class TestDataTest {
	/**
	 * test the addRow-Method.
	 */
	@Test
	public void createNewTestData() {

		TestData testData = new TestData();

		TestDataRow row1 = new TestDataRow(new String[] { "A1", "B1", "C1" });
		row1.add("D1");
		testData.addRow(row1);

		TestDataRow row2 = new TestDataRow(new String[] { "A2", "B2", "C2" });
		row2.add("D2");
		testData.addRow(row2);

		assertTrue(testData.getRowCounts() == 2);

	}

	/**
	 * test the addRow-Method and the getRows-Method.
	 */

	@Test
	public void getTestData() {

		TestData testData = new TestData();

		TestDataRow row1 = new TestDataRow(new String[] { "A1", "B1", "C1" });
		row1.add("D1");
		testData.addRow(row1);

		TestDataRow row2 = new TestDataRow(new String[] { "A2", "B2", "C2" });
		row2.add("D2");
		testData.addRow(row2);

		List<TestDataRow> testDataRows = testData.getRows();

		assertTrue(testDataRows.get(0).getColumnCount() == 4);

		assertEquals("A1", testDataRows.get(0).getColumn(0));
		assertEquals("D2", testDataRows.get(1).getColumn(3));
	}

	/**
	 * test the addEmptyRow method.
	 */
	@Test
	public void addEmptyRow() {
		TestData testData = new TestData();

		TestDataRow row1 = new TestDataRow(new String[] { "A1", "B1", "C1" });
		row1.add("D1");
		testData.addRow(row1);
		TestDataRow row2 = new TestDataRow(new String[] { "A2", "B2", "C2", "D2" });
		testData.addRow(row2);

		testData.addEmptyRow(2);
		testData.addEmptyRow(5);
		List<TestDataRow> testDataRows = testData.getDataRows();

		assertEquals("", testDataRows.get(1).getColumn(0));

	}

	/**
	 * test the pasteRows-method.
	 */
	@Test
	public void pasteRows() {
		TestData testData = new TestData();

		TestDataRow header = new TestDataRow(new String[] { "A1", "B1", "C1" });
		header.add("D1");
		testData.addRow(header);
		TestDataRow row2 = new TestDataRow(new String[] { "A2", "B2", "C2", "D2" });
		testData.addRow(row2);
		TestDataRow row3 = new TestDataRow(new String[] { "A3", "B3", "C3", "D3" });
		testData.addRow(row3);
		testData.addRow(row3);
		TestDataRow row4 = new TestDataRow(new String[] { "A4", "B4", "C4", "D4" });
		testData.addRow(row4);

		List<String> datas = new ArrayList<String>();
		datas.add(header.toString() + "\n");
		datas.add(row4.toString() + "\n");

		testData.pasteRows(datas, new int[] { 3, 4 });
		assertEquals(6, testData.getDataRows().size());
		assertEquals("A1", testData.getDataRows().get(2).getColumn(0));
	}

	/**
	 * test the removeRow-method.
	 */
	@Test
	public void removeRow() {
		TestData testData = new TestData();

		TestDataRow row1 = new TestDataRow(new String[] { "A1", "B1", "C1" });
		row1.add("D1");
		testData.addRow(row1);
		TestDataRow row2 = new TestDataRow(new String[] { "A2", "B2", "C2", "D2" });
		testData.addRow(row2);
		TestDataRow row3 = new TestDataRow(new String[] { "A3", "B3", "C3", "D3" });
		testData.addRow(row3);
		testData.removeRow(0);
		assertEquals(1, testData.getDataRows().size());
		assertEquals("A3", testData.getDataRows().get(0).getColumn(0));
	}

	/**
	 * test the validation of the testdata and the paramtable with valid
	 * testdata.
	 * 
	 * @throws SystemException
	 *             SystemException
	 */
	@Test
	public void testValidImportData() throws SystemException {
		TestData testData = new TestData();

		TestDataRow row1 = new TestDataRow(new String[] { "A1", "B1", "C1" });
		testData.addRow(row1);
		TestScenarioParameterTable testScenarioParameterTable = new TestScenarioParameterTable();
		testScenarioParameterTable.addParameterLine("|A1|B1|C1|");
		assertTrue(testData.validateTableAgainstTable(testScenarioParameterTable).getColumnHeadersOnlyInSourceTable()
				.isEmpty());
	}

	/**
	 * test the validation of the testdata and the paramtable with valid
	 * testdata.
	 * 
	 * @throws SystemException
	 *             SystemException
	 */
	@Test
	public void testValidImportDataWithIgnorCase() throws SystemException {
		TestData testData = new TestData();

		TestDataRow row1 = new TestDataRow(new String[] { "a1", "B1", "C1" });
		testData.addRow(row1);
		TestScenarioParameterTable testScenarioParameterTable = new TestScenarioParameterTable();
		testScenarioParameterTable.addParameterLine("|A1|B1|C1|");

		assertTrue(testData.validateTableAgainstTable(testScenarioParameterTable).getColumnHeadersOnlyInSourceTable()
				.isEmpty());
		assertTrue(testData.validateTableAgainstTable(testScenarioParameterTable).getColumnHeadersOnlyInTargetTable()
				.isEmpty());

		TestScenarioParameterTable testScenarioParameterTableValid = new TestScenarioParameterTable();
		testScenarioParameterTableValid.addParameterLine("|a1|B1|C1|");
		assertTrue(testData.validateTableAgainstTable(testScenarioParameterTableValid)
				.getColumnHeadersOnlyInSourceTable().isEmpty());
		assertTrue(testData.validateTableAgainstTable(testScenarioParameterTableValid)
				.getColumnHeadersOnlyInTargetTable().isEmpty());
	}

	/**
	 * test the validation of the testdata and the paramtable with testdata.
	 * 
	 * @throws SystemException
	 *             SystemException
	 */
	@Test
	public void testImportDataWithMoreDataInSource() throws SystemException {
		TestData testData = new TestData();

		TestDataRow row1 = new TestDataRow(new String[] { "a1", "B1", "C1", "d1" });
		testData.addRow(row1);
		TestScenarioParameterTable testScenarioParameterTable = new TestScenarioParameterTable();
		testScenarioParameterTable.addParameterLine("|A1|B1|C1|");

		assertFalse(testData.validateTableAgainstTable(testScenarioParameterTable).getColumnHeadersOnlyInSourceTable()
				.isEmpty());
		assertTrue(testData.validateTableAgainstTable(testScenarioParameterTable).getColumnHeadersOnlyInTargetTable()
				.isEmpty());
	}

	/**
	 * test the validation of the testdata and the paramtable with testdata.
	 * 
	 * @throws SystemException
	 *             SystemException
	 */
	@Test
	public void testImportDataWithMoreDataInTarget() throws SystemException {
		TestData testData = new TestData();

		TestDataRow row1 = new TestDataRow(new String[] { "a1", "B1", "C1" });
		testData.addRow(row1);
		TestScenarioParameterTable testScenarioParameterTable = new TestScenarioParameterTable();
		testScenarioParameterTable.addParameterLine("|A1|B1|C1|D1|");

		assertTrue(testData.validateTableAgainstTable(testScenarioParameterTable).getColumnHeadersOnlyInSourceTable()
				.isEmpty());
		assertFalse(testData.validateTableAgainstTable(testScenarioParameterTable).getColumnHeadersOnlyInTargetTable()
				.isEmpty());
	}

	/**
	 * test the validation of the testdata and the paramtable with invalid
	 * testdata.
	 * 
	 * @throws SystemException
	 *             SystemException
	 */
	@Test
	public void testInValidImportData() throws SystemException {
		TestData testData = new TestData();

		TestDataRow row1 = new TestDataRow(new String[] { "a1", "B1", "C1" });
		testData.addRow(row1);
		TestScenarioParameterTable testScenarioParameterTable = new TestScenarioParameterTable();
		testScenarioParameterTable.addParameterLine("|D1|B1|C1|");
		testScenarioParameterTable.addParameterLine("|d1|B1|C1|");
		testScenarioParameterTable.addParameterLine("|d1|b1|C1|");
		testScenarioParameterTable.addParameterLine("|d1|b1|c1|");

		assertFalse(testData.validateTableAgainstTable(testScenarioParameterTable).getColumnHeadersOnlyInSourceTable()
				.isEmpty());
		assertFalse(testData.validateTableAgainstTable(testScenarioParameterTable).getColumnHeadersOnlyInTargetTable()
				.isEmpty());

		TestScenarioParameterTable testScenarioParameterTableValid = new TestScenarioParameterTable();
		testScenarioParameterTableValid.addParameterLine("|a1|B1|C1|");
		assertTrue(testData.validateTableAgainstTable(testScenarioParameterTableValid)
				.getColumnHeadersOnlyInSourceTable().isEmpty());
		assertTrue(testData.validateTableAgainstTable(testScenarioParameterTableValid)
				.getColumnHeadersOnlyInTargetTable().isEmpty());
	}

	/**
	 * test the sorting of the TestData.
	 * 
	 * @throws SystemException
	 *             SystemException
	 * 
	 */
	@Test
	public void sortTestData() throws SystemException {

		TestData testData = new TestData();

		TestDataRow row1 = new TestDataRow(new String[] { "B1", "C1", "A1" });
		testData.addRow(row1);
		TestDataRow row2 = new TestDataRow(new String[] { "A2", "B2", "C2" });
		testData.addRow(row2);
		TestDataRow row3 = new TestDataRow(new String[] { "A3", "B3", "C3" });
		testData.addRow(row3);
		testData.addEmptyRow(3);
		testData.addRow(row1);
		TestScenarioParameterTable testScenarioParameterTable = new TestScenarioParameterTable();
		testScenarioParameterTable.addParameterLine("|A1|B1|C1|");

		TestData sortTestData = testData.sortTestData(testScenarioParameterTable);
		assertEquals(3, sortTestData.getDataRows().size());
		assertEquals(4, sortTestData.getRowCounts());
		assertEquals("A1", sortTestData.getRows().get(0).getColumn(0));
		assertEquals("C2", sortTestData.getRows().get(1).getColumn(0));
		assertEquals("C3", sortTestData.getRows().get(2).getColumn(0));
		assertEquals("A1", sortTestData.getRows().get(3).getColumn(0));

		assertEquals("B1", sortTestData.getRows().get(0).getColumn(1));
		assertEquals("A2", sortTestData.getRows().get(1).getColumn(1));
		assertEquals("A3", sortTestData.getRows().get(2).getColumn(1));
		assertEquals("B1", sortTestData.getRows().get(3).getColumn(1));

		assertEquals("C1", sortTestData.getRows().get(0).getColumn(2));
		assertEquals("B2", sortTestData.getRows().get(1).getColumn(2));
		assertEquals("B3", sortTestData.getRows().get(2).getColumn(2));
		assertEquals("C1", sortTestData.getRows().get(3).getColumn(2));
	}

	/**
	 * test the getDataRows of the TestData.
	 * 
	 */
	@Test
	public void getDataRows() {
		TestData testData = new TestData();
		TestDataRow header = new TestDataRow(new String[] { "B", "C", "A" });
		TestDataRow row1 = new TestDataRow(new String[] { "B1", "C1", "A1" });
		testData.addRow(header);
		testData.addRow(row1);
		assertEquals(testData.getDataRows().get(0).getColumn(0), row1.getColumn(0));
		assertEquals(testData.getDataRows().get(0).getColumn(1), row1.getColumn(1));
		assertEquals(testData.getDataRows().get(0).getColumn(2), row1.getColumn(2));
	}

}
