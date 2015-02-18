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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.List;

import org.junit.Test;
import org.testeditor.core.importer.CsvFileImporter;
import org.testeditor.core.importer.ExcelFileImportException;
import org.testeditor.core.importer.ExcelFileImporter;
import org.testeditor.core.model.teststructure.TestData;
import org.testeditor.core.model.teststructure.TestDataRow;

/**
 * Tests the class @see {@link CsvFileImporter}.
 * 
 */
public class ExcelFileImporterTest {

	/**
	 * Tests if reading of xls file works correct.
	 */
	@Test
	public void readExcelFileWithFullRows() {

		ExcelFileImporter importer = new ExcelFileImporter();

		String file = new StringBuffer(new File("").getAbsolutePath()).append(File.separatorChar).append("ressources")
				.append(File.separatorChar).append("testdata.xls").toString();

		try {
			TestData testData = importer.getTestData(new File(file));
			List<TestDataRow> rows = testData.getRows();
			TestDataRow testDataRow = rows.get(1);
			assertEquals("http://www.orhan-polat.de", testDataRow.getColumn(0));
			testDataRow = rows.get(5);
			assertEquals("kpanier", testDataRow.getColumn(2));

		} catch (ExcelFileImportException e) {
			fail("Excel Datei zu alt");
		}

	}

	/**
	 * Tests if reading of xls file works correct.
	 */
	@Test
	public void readExcelFileWithNotFullRows() {

		ExcelFileImporter importer = new ExcelFileImporter();

		String file = new StringBuffer(new File("").getAbsolutePath()).append(File.separatorChar).append("ressources")
				.append(File.separatorChar).append("testdata2.xls").toString();

		try {
			TestData testData = importer.getTestData(new File(file));
			List<TestDataRow> rows = testData.getRows();

			TestDataRow testDataRow = rows.get(1);
			assertEquals("http://www.orhan-polat.de", testDataRow.getColumn(0));

		} catch (ExcelFileImportException e) {
			fail("Excel Datei zu alt");
		}

	}

	/**
	 * Tests reading of empty file.
	 */
	@Test
	public void readExcelFileWithNoData() {

		CsvFileImporter importer = new CsvFileImporter();

		String csvFile = new StringBuffer(new File("").getAbsolutePath()).append(File.separatorChar)
				.append("ressources").append(File.separatorChar).append("testdataWithoutContent.csv").toString();

		TestData testData = importer.getTestData(new File(csvFile));

		List<TestDataRow> rows = testData.getRows();

		assertTrue(rows.size() == 0);

	}

	/**
	 * Tests if reading of xls file works correct.
	 */
	@Test
	public void readExcelFileWitDifferentColumnTypes() {

		ExcelFileImporter importer = new ExcelFileImporter();

		String file = new StringBuffer(new File("").getAbsolutePath()).append(File.separatorChar).append("ressources")
				.append(File.separatorChar).append("testdata_different_types.xls").toString();

		try {
			TestData testData = importer.getTestData(new File(file));
			List<TestDataRow> rows = testData.getRows();
			TestDataRow testDataRow = rows.get(1);
			assertTrue(testDataRow.getColumn(5).toString().endsWith("2001"));
			assertTrue(testDataRow.getColumn(5).toString().startsWith("Mo"));

			testDataRow = rows.get(5);
			assertTrue(testDataRow.getColumn(5).toString().endsWith("2056"));
			assertTrue(testDataRow.getColumn(5).toString().startsWith("Fr"));
			assertFalse(Boolean.valueOf(testDataRow.getColumn(8)));

		} catch (ExcelFileImportException e) {
			fail("Excel Datei zu alt");
		}

	}
}
