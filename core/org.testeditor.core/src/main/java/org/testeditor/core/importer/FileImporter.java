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

import org.testeditor.core.model.teststructure.TestData;

/**
 * Interface for import data from any file.
 * 
 * 
 * @author orhan
 */
public interface FileImporter {

	/**
	 * Returns generated TestData object depends on input data type.
	 * 
	 * @param file
	 *            file for import.
	 * @return TestData
	 * @throws ExcelFileImportException
	 *             catch oldFileexceptions
	 */
	TestData getTestData(File file) throws ExcelFileImportException;
}
