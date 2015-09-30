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

import java.util.ArrayList;
import java.util.List;

/**
 * Entity represents testdata.
 * 
 * @author orhan
 */
public class TestData {

	private final List<TestDataRow> testDataRows = new ArrayList<TestDataRow>();

	/**
	 * 
	 * @param testDataRow
	 *            TestDataRow
	 */
	public void addRow(TestDataRow testDataRow) {
		testDataRows.add(testDataRow);

	}

	/**
	 * 
	 * adds the testDataRow to the row toRow.
	 * 
	 * @param toRow
	 *            int
	 * 
	 * @param testDataRow
	 *            TestDataRow
	 */
	public void addRow(int toRow, TestDataRow testDataRow) {
		testDataRows.add(toRow, testDataRow);

	}

	/**
	 * Returns the count of rows.
	 * 
	 * @return count of rows
	 */
	public int getRowCounts() {
		return testDataRows.size();
	}

	/**
	 * 
	 * @return list of @see {@link TestDataRow}
	 */
	public List<TestDataRow> getRows() {
		return testDataRows;
	}

	/**
	 * 
	 * @return the titleline.
	 */
	public TestDataRow getTitleRow() {
		return testDataRows.get(0);
	}

	/**
	 * Returns true if title row exists.
	 * 
	 * @return the titleline.
	 */
	public boolean hasTitleRow() {
		return testDataRows.size() > 0;
	}

	/**
	 * gets only the data-rows of the table without the columnheaders.
	 * 
	 * @return List<TestDataRow>
	 */
	public List<TestDataRow> getDataRows() {
		if (getRowCounts() > 1) {
			return testDataRows.subList(1, getRowCounts());
		}
		return new ArrayList<TestDataRow>();
	}

	/**
	 * 
	 * @return a row with empty string-values in the cells.
	 */
	private TestDataRow getEmptyRow() {
		TestDataRow emptyRow = new TestDataRow();
		for (int i = 0; i < getRows().get(0).getList().size(); i++) {
			emptyRow.add("");
		}
		return emptyRow;
	}

	/**
	 * adds a row with empty strings in the cells.
	 * 
	 * @param line
	 *            the position.
	 */
	public void addEmptyRow(int line) {
		if (testDataRows.size() < line) {
			line = testDataRows.size();
		}
		testDataRows.add(line, getEmptyRow());

	}

	/**
	 * removes the row from the testDataRows.
	 * 
	 * @param selctedLine
	 *            the row to be removed
	 */
	public void removeRow(int selctedLine) {
		testDataRows.remove(selctedLine);

	}

	/**
	 * paste the pasteContents into the testDataRows.
	 * 
	 * @param pasteContens
	 *            List<String>
	 * @param selectedRows
	 *            int[]
	 */
	public void pasteRows(List<String> pasteContens, int[] selectedRows) {
		int selectedLine = selectedRows[0];

		for (int i = 0; i < pasteContens.size(); i++) {
			addRow(selectedLine, new TestDataRow(pasteContens.get(i)));
			selectedLine++;
		}
	}

	/**
	 * create and return a empty testDatarow with col splits.
	 * 
	 * @param col
	 *            number of splits
	 * @return empty testDataRow with col splits
	 * 
	 */
	public TestDataRow getEmptyRow(int col) {
		TestDataRow testDataRow = new TestDataRow();
		for (int sortTestDataCol = 0; sortTestDataCol < col; sortTestDataCol++) {
			testDataRow.add("");
		}
		return testDataRow;
	}

	/**
	 * create and return a empty TestData with col splits and row lines.
	 * 
	 * @param col
	 *            number of splits
	 * @param row
	 *            number of lines
	 * 
	 */
	public void addEmptyTestData(int row, int col) {
		for (int i = 0; i < row; i++) {
			this.addRow(getEmptyRow(col));
		}
	}

	/**
	 * Removes all empty rows in the testData.
	 */
	public void removeEmptyRows() {
		for (int row = 0; row < this.getRowCounts(); row++) {
			TestDataRow testDataRow = this.getRows().get(row);
			if (testDataRow.isRowEmpty()) {
				this.removeRow(row);
			}
		}
	}

	/**
	 * returns the sorted TestData.
	 * 
	 * @param parmaTable
	 *            : TestData to sort
	 * 
	 * @return testData
	 */
	public TestData sortTestData(TestScenarioParameterTable parmaTable) {
		TestData sortData = new TestData();
		sortData.addEmptyTestData(this.getRowCounts(), parmaTable.getDataTable().getRows().get(0).getColumnCount());

		for (int sortthisCol = 0; sortthisCol < parmaTable.getDataTable().getRows().get(0).getColumnCount(); sortthisCol++) {
			for (int thisCol = 0; thisCol < this.getRows().get(0).getColumnCount(); thisCol++) {
				if (parmaTable.getDataTable().getRows().get(0).getColumn(sortthisCol).toString()
						.equals(this.getRows().get(0).getColumn(thisCol).toString())) {
					for (int row = 0; row < this.getRowCounts(); row++) {
						sortData.getRows().get(row)
								.setColumn(sortthisCol, this.getRows().get(row).getColumn(thisCol).toString());
					}
				}
			}
		}
		sortData.removeEmptyRows();
		return sortData;
	}

	/**
	 * Validate the columns of this.parameterTable against the paramTable.
	 * 
	 * @param paramTable
	 *            : parmaTable to match
	 * 
	 * 
	 * @return true by validate TestData
	 */
	public TestDataEvaluationReturnList validateTableAgainstTable(TestScenarioParameterTable paramTable) {
		TestDataEvaluationReturnList testDataEvaluationReturnList;
		if (paramTable.getTestDataEvaluationReturnList() == null) {
			testDataEvaluationReturnList = new TestDataEvaluationReturnList();
		} else {
			testDataEvaluationReturnList = paramTable.getTestDataEvaluationReturnList();
		}
		for (int i = 0; i < paramTable.getDataTable().getRows().get(0).getColumnCount(); i++) {
			testDataEvaluationReturnList.addEntryInColumnHeadersOnlyInTargetTable(paramTable.getDataTable().getRows()
					.get(0).getColumn(i).toString().toLowerCase());
		}
		if (this.getRows().size() == 0) {
			return testDataEvaluationReturnList;
		}
		for (int i = 0; i < this.getRows().get(0).getColumnCount(); i++) {
			String columnOfSourceTable = this.getRows().get(0).getColumn(i).toString().toLowerCase();
			if (!testDataEvaluationReturnList.removeEntryFromColumnHeadersOnlyInTargetTable(columnOfSourceTable)) {
				testDataEvaluationReturnList.addEntryToColumnHeadersOnlyInSourceTable(columnOfSourceTable);
			}
		}
		return testDataEvaluationReturnList;
	}

	/**
	 * own toString-method.
	 * 
	 * @return the String
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (TestDataRow dataRow : testDataRows) {
			sb.append(dataRow.toString()).append("\n");
		}
		return sb.toString();
	}
}
