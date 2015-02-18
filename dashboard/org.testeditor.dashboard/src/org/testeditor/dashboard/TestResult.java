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
package org.testeditor.dashboard;

import java.util.ArrayList;
import java.util.List;

/**
 * @author alebedev
 * 
 */
public class TestResult extends AllRunsResult {

	/**
	 * All child test results.
	 */
	private List<TestResult> childs = new ArrayList<TestResult>();

	/**
	 * The test result.
	 */
	private String result;

	/**
	 * @return all child test results
	 */
	public List<TestResult> getChilds() {
		return childs;
	}

	/**
	 * total result of test case or suite run failed, successful or warning.
	 * 
	 * @return result
	 */
	public String getResult() {
		return result;
	}

	/**
	 * @param result
	 *            set ok, failed, warning
	 */
	public void setResult(String result) {
		this.result = result;
	}

	/**
	 * adding tree elements (test cases, suite).
	 * 
	 * @param test
	 *            a child test result
	 */
	public void add(TestResult test) {
		childs.add(test);
	}
}
