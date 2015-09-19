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
import java.net.URISyntaxException;
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
	 * 
	 * @throws URISyntaxException
	 *             on test failure
	 */
	@Test
	public void readExcelFileWithFullRows() throws URISyntaxException {

		ExcelFileImporter importer = new ExcelFileImporter();

		File dataFile = new File(getClass().getResource("/testdata.xls").toURI());

		try {
			TestData testData = importer.getTestData(dataFile);
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
	 * 
	 * @throws URISyntaxException
	 *             on test failure
	 */
	@Test
	public void readExcelFileWithNotFullRows() throws URISyntaxException {

		ExcelFileImporter importer = new ExcelFileImporter();

		File dataFile = new File(getClass().getResource("/testdata2.xls").toURI());

		try {
			TestData testData = importer.getTestData(dataFile);
			List<TestDataRow> rows = testData.getRows();

			TestDataRow testDataRow = rows.get(1);
			assertEquals("http://www.orhan-polat.de", testDataRow.getColumn(0));

		} catch (ExcelFileImportException e) {
			fail("Excel Datei zu alt");
		}

	}

	/**
	 * Tests reading of empty file.
	 * 
	 * @throws URISyntaxException
	 *             on test failure
	 */
	@Test
	public void readExcelFileWithNoData() throws URISyntaxException {

		CsvFileImporter importer = new CsvFileImporter();

		File dataFile = new File(getClass().getResource("/testdataWithoutContent.csv").toURI());

		TestData testData = importer.getTestData(dataFile);

		List<TestDataRow> rows = testData.getRows();

		assertTrue(rows.size() == 0);

	}

	/**
	 * Tests if reading of xls file works correct.
	 * 
	 * @throws URISyntaxException
	 *             on test failure
	 */
	@Test
	public void readExcelFileWitDifferentColumnTypes() throws URISyntaxException {

		ExcelFileImporter importer = new ExcelFileImporter();

		File dataFile = new File(getClass().getResource("/testdata_different_types.xls").toURI());

		try {
			TestData testData = importer.getTestData(dataFile);
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
