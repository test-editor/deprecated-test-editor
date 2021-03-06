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

import java.io.File;
import java.util.List;

import org.junit.Test;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.importer.FileImporter;
import org.testeditor.core.importer.FileImporterFactory;
import org.testeditor.core.model.teststructure.TestData;
import org.testeditor.core.model.teststructure.TestDataRow;

/**
 * Tests the class @see {@link FileImporterFactory}.
 * 
 */
public class FileImporterFactoryTest {

	/**
	 * Tests if reading of csv file works correct.
	 * 
	 * @throws Exception
	 *             on test failure
	 */
	@Test
	public void readExcelFile() throws Exception {

		File dataFile = new File(getClass().getResource("/testdata.xls").toURI());

		FileImporter importer = FileImporterFactory.getInstance(dataFile);

		TestData testData = importer.getTestData(dataFile);

		List<TestDataRow> rows = testData.getRows();

		TestDataRow testDataRow = rows.get(1);
		assertEquals("http://www.orhan-polat.de", testDataRow.getColumn(0));

		testDataRow = rows.get(5);
		assertEquals("kpanier", testDataRow.getColumn(2));

	}

	/**
	 * Tests if reading of csv file works correct.
	 * 
	 * @throws Exception
	 *             on test failure
	 */
	@Test
	public void readCsvFile() throws Exception {

		File dataFile = new File(getClass().getResource("/testdata.csv").toURI());

		FileImporter importer = FileImporterFactory.getInstance(dataFile);

		TestData testData = importer.getTestData(dataFile);

		List<TestDataRow> rows = testData.getRows();

		TestDataRow testDataRow = rows.get(1);
		assertEquals("http://www.orhan-polat.de", testDataRow.getColumn(0));

		testDataRow = rows.get(5);
		assertEquals("kpanier", testDataRow.getColumn(2));

	}

	/**
	 * Reads an unknown file format.
	 * 
	 * @throws Exception
	 *             on test failure
	 */
	@Test
	public void readUnknownFileFormat() throws Exception {

		try {

			File dataFile = new File(getClass().getResource("/unknownFileExtension.abc").toURI());

			FileImporterFactory.getInstance(dataFile);
			assertFalse("File " + dataFile.getName() + " has unknown extension ", true);

		} catch (SystemException e) {
			assertTrue(true);
		}

	}

}
