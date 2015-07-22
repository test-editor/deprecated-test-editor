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

import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity represents row for the testdata.
 * 
 * @author orhan
 */
public class TestDataRow {

	private final List<String> testDataRowParts = new ArrayList<String>();
	private PropertyChangeSupport changes = new PropertyChangeSupport(this);

	/**
	 * Default Constructor.
	 * 
	 */
	public TestDataRow() {
	}

	/**
	 * Constructor from rowAsString parts are separated by |.
	 * 
	 * @param rowAsString
	 *            String
	 * 
	 */
	public TestDataRow(String rowAsString) {
		for (String str : rowAsString.split("\\|")) {
			testDataRowParts.add(str);
		}
	}

	/**
	 * 
	 * @param datas
	 *            array of Strings
	 */
	public TestDataRow(String[] datas) {
		for (String data : datas) {
			this.add(data);
		}
	}

	/**
	 * Adds a new testdatarow.
	 * 
	 * @param data
	 *            data of row
	 * @return TestDataRow
	 */
	public TestDataRow add(String data) {
		this.testDataRowParts.add(data);
		return this;
	}

	/**
	 * 
	 * @return count of columns
	 */
	public int getColumnCount() {
		return testDataRowParts.size();
	}

	/**
	 * 
	 * @param i
	 *            position of data in column
	 * @return data in column
	 */
	public String getColumn(int i) {
		if (getColumnCount() - 1 >= i) {
			return testDataRowParts.get(i);
		}
		return "";

	}

	/**
	 * 
	 * @return list of string.
	 */
	public List<String> getList() {
		return testDataRowParts;
	}

	/**
	 * sets the data in a column.
	 * 
	 * @param columnNo
	 *            int
	 * @param value
	 *            String
	 */
	public void setColumn(int columnNo, String value) {
		if (getColumnCount() - 1 >= columnNo) {
			String oldValue = testDataRowParts.get(columnNo);
			testDataRowParts.set(columnNo, value);
			changes.firePropertyChange("Colnum: " + columnNo, oldValue, value);
			return;
		}
		for (int i = getColumnCount() - 1; i < columnNo; i++) {
			testDataRowParts.add("");
		}
		testDataRowParts.add(columnNo, value);

	}

	/**
	 * converts the testDataRow to a string.
	 * 
	 * @return the TestDataRow as a string parts are separated by \t
	 */
	@Override
	public String toString() {
		StringBuffer bs = new StringBuffer();
		for (String str : testDataRowParts) {
			bs.append(str).append("|");
		}
		return bs.toString();
	}

	/**
	 * extract the method from the removeEmptyRow method from the table.
	 * 
	 * @return true if row is empty, else false
	 */
	public boolean isRowEmpty() {
		for (int col = 0; col < getColumnCount(); col++) {
			// if at least one cell in the row is not empty, than the line is
			// not empty.
			if (getColumn(col) != null && !getColumn(col).toString().equals("")) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 
	 * @return a list of strings of the TestDataRow.
	 */
	public List<String> getValue() {
		return testDataRowParts;
	}
}
