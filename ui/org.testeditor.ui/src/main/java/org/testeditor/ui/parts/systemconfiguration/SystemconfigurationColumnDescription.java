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
package org.testeditor.ui.parts.systemconfiguration;

/**
 * special description of the columns.
 * 
 * @author llipinski
 * 
 */
public class SystemconfigurationColumnDescription {

	private String columnHeaderText;
	private int intColumnWeightData;
	private boolean isKey;
	private String columnDataModelField;

	/**
	 * constructor.
	 * 
	 * @param columnHeaderText
	 *            the title of the column
	 * @param columnDataModelField
	 *            the name of the field in the dataModel. There should be a
	 *            getter with the name "get" + columnDataModelField and a setter
	 *            with "set" + columnDataModelField
	 * @param intColumnWeightData
	 *            the weight of the column in the grid as a int
	 * @param isKey
	 *            boolean, should be set true, for special function, fi the key
	 *            of a key-value-pair is changed.
	 */
	public SystemconfigurationColumnDescription(String columnHeaderText, String columnDataModelField, int intColumnWeightData,
			boolean isKey) {
		this.columnHeaderText = columnHeaderText;
		this.columnDataModelField = columnDataModelField;
		this.intColumnWeightData = intColumnWeightData;
		this.isKey = isKey;
	}

	/**
	 * 
	 * @return columnHeaderText
	 */
	public String getcolumnHeaderText() {
		return columnHeaderText;
	}

	/**
	 * 
	 * @return columnDataModelField
	 */
	public String getColumnDataModelField() {
		return columnDataModelField;
	}

	/**
	 * 
	 * @return intColumnWeightData
	 */
	public int getIntColumnWeightData() {
		return intColumnWeightData;
	}

	/**
	 * 
	 * @return isKey
	 */
	public boolean isKey() {
		return isKey;
	}

}
