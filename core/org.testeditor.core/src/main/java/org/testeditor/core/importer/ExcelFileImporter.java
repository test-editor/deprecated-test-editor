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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.OldExcelFormatException;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.testeditor.core.model.teststructure.TestData;
import org.testeditor.core.model.teststructure.TestDataRow;

/**
 * Generates testdata depends on xsl file.
 * 
 * 
 * @author orhan
 */
public class ExcelFileImporter implements FileImporter {

	private static final Logger LOGGER = Logger.getLogger(ExcelFileImporter.class);
	private static final String JAVA_TOSTRING = "EEE MMM dd HH:mm:ss zzz yyyy";

	/**
	 * Returns generated TestData object.
	 * 
	 * @param file
	 *            excel file for generating TestData.
	 * @return TestData
	 * @throws ExcelFileImportException
	 *             catch oldFileexceptions
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public TestData getTestData(File file) throws ExcelFileImportException {

		TestData testData = new TestData();

		InputStream input = null;

		try {
			input = new BufferedInputStream(new FileInputStream(file));

			POIFSFileSystem fs = new POIFSFileSystem(input);
			HSSFWorkbook wb = new HSSFWorkbook(fs);

			HSSFSheet sheet = wb.getSheetAt(0);

			Iterator rows = sheet.rowIterator();

			while (rows.hasNext()) {

				HSSFRow row = (HSSFRow) rows.next();

				TestDataRow testDataRow = getTestDataRow(row);

				if (testDataRow.getList().size() > 0 && !dataRowIsEmpty(testDataRow)) {
					testData.addRow(testDataRow);
				} else {
					break;
				}
			}
		} catch (OldExcelFormatException e) {
			throw new ExcelFileImportException(e);
		} catch (FileNotFoundException e) {
			LOGGER.error("getTestData :: " + e.getMessage());
		} catch (IOException e) {
			LOGGER.error("getTestData" + e.getMessage());
		} finally {
			try {
				if (input != null) {
					input.close();
				}
			} catch (IOException e) {
				LOGGER.error(e);
			}
		}

		return testData;

	}

	/**
	 * Checks if datarow is empty.
	 * 
	 * @param testDataRow
	 *            datarow
	 * @return true if all cells in row has no data.
	 */
	private boolean dataRowIsEmpty(TestDataRow testDataRow) {

		int emptyColumns = 0;

		for (int i = 0; i < testDataRow.getColumnCount(); i++) {

			if (testDataRow.getColumn(i).equals("")) {
				emptyColumns++;
			}
		}

		if (emptyColumns == testDataRow.getColumnCount()) {
			return true;
		}

		return false;

	}

	/**
	 * Iterates through the cells in a row an creates a {@link TestDataRow}
	 * Object.
	 * 
	 * @param row
	 *            row in excel sheet
	 * @return TestDataRow
	 */
	@SuppressWarnings("rawtypes")
	private TestDataRow getTestDataRow(HSSFRow row) {
		int id = 0;
		Iterator cells = row.cellIterator();

		TestDataRow testDataRow = new TestDataRow();

		while (cells.hasNext()) {

			HSSFCell cell = (HSSFCell) cells.next();

			for (int i = id; i < cell.getColumnIndex(); i++) {
				testDataRow.add("");
			}
			id = cell.getColumnIndex() + 1;

			if (HSSFCell.CELL_TYPE_NUMERIC == cell.getCellType()) {
				getTestDataNumericCell(testDataRow, cell);
			} else if (HSSFCell.CELL_TYPE_STRING == cell.getCellType()) {
				testDataRow.add(cell.getStringCellValue());
			} else if (HSSFCell.CELL_TYPE_BOOLEAN == cell.getCellType()) {
				testDataRow.add(String.valueOf(cell.getBooleanCellValue()));
			} else if (HSSFCell.CELL_TYPE_FORMULA == cell.getCellType()) {

				HSSFFormulaEvaluator fe = new HSSFFormulaEvaluator(row.getSheet().getWorkbook());
				CellValue cv = fe.evaluate(cell);
				testDataRow.add(cv.formatAsString());

			} else if (HSSFCell.CELL_TYPE_BLANK == cell.getCellType()) {
				testDataRow.add("");
			} else {
				if (LOGGER.isInfoEnabled()) {
					LOGGER.info("getTestData :: Unknown cell type");
				}
			}
		}
		return testDataRow;
	}

	/**
	 * gets the testData from a numeric cell.
	 * 
	 * @param testDataRow
	 *            TestDataRow
	 * @param cell
	 *            HSSFCell
	 */
	protected void getTestDataNumericCell(TestDataRow testDataRow, HSSFCell cell) {
		if (HSSFDateUtil.isCellDateFormatted(cell)) {
			double value = cell.getNumericCellValue();
			if (HSSFDateUtil.isValidExcelDate(value)) {
				Date date = HSSFDateUtil.getJavaDate(cell.getNumericCellValue());
				DateFormat format = new SimpleDateFormat(JAVA_TOSTRING);
				testDataRow.add(format.format(date));
			}
		} else {
			DataFormatter df = new DataFormatter();
			Format cellFormat = df.createFormat(cell);
			if (cellFormat instanceof DecimalFormat) {
				String pattern = ((DecimalFormat) cellFormat).toPattern();
				DecimalFormat dFormatter = new DecimalFormat(pattern);
				testDataRow.add(dFormatter.format(cell.getNumericCellValue()));
			} else {
				testDataRow.add(String.valueOf(cell.getNumericCellValue()));
			}
		}
	}
}
