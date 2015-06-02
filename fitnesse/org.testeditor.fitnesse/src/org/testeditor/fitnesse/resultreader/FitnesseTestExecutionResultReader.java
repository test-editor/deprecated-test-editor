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
 * Reads the Result of a test execution process and creates test result bean.
 *
 */
public class FitnesseTestExecutionResultReader extends FitnesseMultiResultReader implements FitNesseResultReader {

	@Override
	public TestResult readTestResult(InputStream resultStream) {
		TestResult readTestResult = super.readTestResult(resultStream, "result");
		if (readTestResult != null) {
			if (readTestResult.getChildren().size() == 1) {
				return readTestResult.getChildren().get(0);
			}
		}
		return readTestResult;
	}
}
