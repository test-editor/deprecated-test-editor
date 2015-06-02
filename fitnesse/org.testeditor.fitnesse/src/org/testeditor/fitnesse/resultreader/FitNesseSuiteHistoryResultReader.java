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

import org.testeditor.core.model.testresult.TestResult;

/**
 * <pre>
 * Creates List of TestResult after parsing the result xml file of test
 * execution.
 * 
 * example: 
 * name = DemoWebTests.LocalDemoSuite.LoginSuite.LoginInvalidTest 
 * resultDate = 10/11/2013 15:12:19
 * 
 * </pre>
 */
public class FitNesseSuiteHistoryResultReader extends FitnesseMultiResultReader implements FitNesseResultReader {

	/**
	 * Returns a list of {@link TestResult} after parsing.
	 * 
	 * @param resultStream
	 *            given stream of xml file.
	 * @return list of {@link TestResult}
	 */
	@Override
	public TestResult readTestResult(InputStream resultStream) {
		return super.readTestResult(resultStream, "pageHistoryReference");
	}

}