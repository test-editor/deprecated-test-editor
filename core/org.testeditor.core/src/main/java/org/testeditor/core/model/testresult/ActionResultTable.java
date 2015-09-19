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

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * Model Object of the Execution Report of an Action Group in a Testflow.
 *
 */
public class ActionResultTable {

	private List<List<String>> rows = new ArrayList<List<String>>();
	private String name;

	/**
	 * Creates a New List as a row of the ActionResult Table. The row is stored
	 * locally.
	 * 
	 * @return new created List representing the new Row of the table.
	 */
	public List<String> createNewRow() {
		ArrayList<String> row = new ArrayList<String>();
		rows.add(row);
		return row;
	}

	/**
	 * 
	 * @return all rows in this table.
	 */
	public List<List<String>> getRows() {
		return rows;
	}

	/**
	 * 
	 * @return the Name of the Table
	 */
	public String getName() {
		return name;
	}

	/**
	 * 
	 * @param name
	 *            of the Table
	 */
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return getName();
	}
}
