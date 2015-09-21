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

import java.io.File;

import org.testeditor.core.exceptions.SystemException;

/**
 * Depends on file extension factory creates an specific implementation class of
 * file importer.
 * 
 * 
 * @author orhan
 */
public final class FileImporterFactory {

	private static final String XLS = "xls";
	private static final String CSV = "csv";

	/**
	 * 
	 */
	private FileImporterFactory() {

	}

	/**
	 * Returns {@link FileImporter} depends on file type.
	 * 
	 * @param file
	 *            input file
	 * @return specific file importer
	 * @throws SystemException
	 *             exception will be thrown if unknown file extension
	 */
	public static FileImporter getInstance(File file) throws SystemException {

		String[] split = file.getName().split("\\.");
		String ext = split[split.length - 1];

		if (ext.equalsIgnoreCase(CSV)) {
			return new CsvFileImporter();
		} else if (ext.equalsIgnoreCase(XLS)) {
			return new ExcelFileImporter();
		}

		throw new SystemException("Unknown file extension: " + ext);

	}
}
