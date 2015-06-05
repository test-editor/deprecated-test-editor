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

import java.util.HashSet;
import java.util.Set;

/**
 * list with the result of the testdata-evaluation.
 */
public class TestDataEvaluationReturnList {
	private Set<String> columnHeadersOnlyInTargetTable = new HashSet<String>();
	private Set<String> columnHeadersOnlyInSourceTable = new HashSet<String>();
	private boolean dataRowColumnCountEqualsHeaderRowColumnCount = true;

	/**
	 * @return the HashSet with the Columns only in the target table.
	 */
	public Set<String> getColumnHeadersOnlyInTargetTable() {
		return columnHeadersOnlyInTargetTable;
	}

	/**
	 * adds an entry to the columnHeadersOnlyInTargetTable.
	 * 
	 * @param entry
	 *            a column of the columnHeadersOnlyInTargetTable
	 * @return the result of the operation
	 */
	protected boolean addEntryInColumnHeadersOnlyInTargetTable(String entry) {
		return this.columnHeadersOnlyInTargetTable.add(entry);
	}

	/**
	 * 
	 * @return the content of the columnHeadersOnlyInSourceTable.
	 */
	public Set<String> getColumnHeadersOnlyInSourceTable() {
		return columnHeadersOnlyInSourceTable;
	}

	/**
	 * adds an entry to the columnHeadersOnlyInSourceTable.
	 * 
	 * @param entry
	 *            a column of the columnHeadersOnlyInSourceTable
	 * @return the result of the operation
	 */
	protected boolean addEntryToColumnHeadersOnlyInSourceTable(String entry) {
		return this.columnHeadersOnlyInSourceTable.add(entry);
	}

	/**
	 * removes the entry from the columnHeadersOnlyInSourceTable.
	 * 
	 * @param entry
	 *            String
	 * @return result of the operation
	 */
	protected boolean removeEntryFromColumnHeadersOnlyInSourceTable(String entry) {
		return columnHeadersOnlyInSourceTable.remove(entry);
	}

	/**
	 * removes the entry from the columnHeadersOnlyInTargetTable.
	 * 
	 * @param entry
	 *            String
	 * @return result of the operation
	 */
	protected boolean removeEntryFromColumnHeadersOnlyInTargetTable(String entry) {
		return columnHeadersOnlyInTargetTable.remove(entry);
	}

	/**
	 * @return the result of the toString-operation.
	 */
	public String toString() {
		return columnHeadersOnlyInSourceTable.toString() + "\n" + columnHeadersOnlyInTargetTable.toString();
	}

	/**
	 * 
	 * @return true if the column count of the header row is equals with the
	 *         column count of the data row, false if not.
	 */
	public boolean isDataRowColumnCountEqualsHeaderRowColumnCount() {
		return dataRowColumnCountEqualsHeaderRowColumnCount;
	}

	/**
	 * 
	 * @param dataRowColumnCountEqualsHeaderRowColumnCount
	 *            true if the column count of the header row is equals with the
	 *            column count of the data row, false if not.
	 */
	public void setDataRowColumnCountEqualsHeaderRowColumnCount(boolean dataRowColumnCountEqualsHeaderRowColumnCount) {
		this.dataRowColumnCountEqualsHeaderRowColumnCount = dataRowColumnCountEqualsHeaderRowColumnCount;
	}
}
