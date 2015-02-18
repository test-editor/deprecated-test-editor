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
package org.testeditor.core.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.junit.Test;
import org.testeditor.core.importer.CsvFileImporter;
import org.testeditor.core.model.teststructure.TestData;
import org.testeditor.core.model.teststructure.TestDataRow;

/**
 * Tests the class @see {@link CsvFileImporter}.
 * 
 * @author orhan
 */
public class CsvFileImporterTest {

	/**
	 * Tests if reading of csv file works correct.
	 */
	@Test
	public void readCsvFile() {

		CsvFileImporter importer = new CsvFileImporter();

		String csvFile = new StringBuffer(new File("").getAbsolutePath()).append(File.separatorChar)
				.append("ressources").append(File.separatorChar).append("testdata.csv").toString();

		TestData testData = importer.getTestData(new File(csvFile));

		List<TestDataRow> rows = testData.getRows();

		TestDataRow testDataRow = rows.get(1);
		assertEquals("http://www.orhan-polat.de", testDataRow.getColumn(0));

		testDataRow = rows.get(5);
		assertEquals("kpanier", testDataRow.getColumn(2));

	}

	/**
	 * Tests reading of empty file.
	 */
	@Test
	public void readCsvFileWithNoData() {

		CsvFileImporter importer = new CsvFileImporter();

		String csvFile = new StringBuffer(new File("").getAbsolutePath()).append(File.separatorChar)
				.append("ressources").append(File.separatorChar).append("testdataWithoutContent.csv").toString();

		TestData testData = importer.getTestData(new File(csvFile));

		List<TestDataRow> rows = testData.getRows();

		assertTrue(rows.size() == 0);

	}

}
