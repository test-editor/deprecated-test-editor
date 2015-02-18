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
package org.testeditor.fitnesse.resultreader;

import java.io.InputStream;
import java.util.List;

import org.testeditor.core.model.testresult.TestResult;

/**
 * Liest das Resultfile des Tests und erzeugt ein FitNesseResult.
 * 
 */
public interface FitNesseResultReader {

	/**
	 * Reads the generated result file as stream.
	 * 
	 * @param resultStream
	 *            stream of test result after test execution.
	 * @return TestResult
	 */
	TestResult readTestResult(InputStream resultStream);

	/**
	 * gets a list of testresults of a test.
	 * 
	 * @param fileInputStream
	 *            {@link InputStream}
	 * @return List<TestResult>
	 */
	List<TestResult> readResultHistory(InputStream fileInputStream);
}