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
package org.testeditor.core.importer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import org.apache.log4j.Logger;
import org.testeditor.core.model.teststructure.TestData;
import org.testeditor.core.model.teststructure.TestDataRow;

/**
 * Generates testdata depends on csv file.
 * 
 * 
 * @author orhan
 */
public class CsvFileImporter implements FileImporter {

	private static final Logger LOGGER = Logger.getLogger(CsvFileImporter.class);
	private static final String CSV_FILE_ENCODING = "csv.file.encoding";

	/**
	 * if all cells of a row are empty then return true.
	 * 
	 * @param rowData
	 *            rowdata to be tested
	 * @return true if row has no data
	 */
	private boolean allCellsAreEmpty(String[] rowData) {

		for (String string : rowData) {

			if (!string.equals("")) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns generated TestData object.
	 * 
	 * @param file
	 *            file for generating TestData.
	 * @return TestData
	 */
	@Override
	public TestData getTestData(File file) {

		TestData testData = new TestData();

		BufferedReader reader = null;

		Charset charset = null;
		String encoding = System.getProperty(CSV_FILE_ENCODING);
		if (encoding != null) {
			try {
				charset = Charset.forName(encoding);
			} catch (Exception e) {
				LOGGER.error("CSV file encoding '" + System.getProperty(CSV_FILE_ENCODING) + "' not valid.");
				charset = Charset.defaultCharset();
			}
		} else {
			charset = Charset.defaultCharset();
		}

		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), charset));

			String dataRow = null;
			while ((dataRow = reader.readLine()) != null) {

				String[] splittedDataRow = dataRow.split(";");

				if (!allCellsAreEmpty(splittedDataRow)) {

					TestDataRow testDataRow = new TestDataRow(splittedDataRow);
					testData.addRow(testDataRow);
				}
			}

			reader.close();

		} catch (FileNotFoundException e) {
			LOGGER.error(e);
		} catch (Exception e) {
			LOGGER.error(e);
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				LOGGER.error(e);
			}
		}

		return testData;

	}
}
