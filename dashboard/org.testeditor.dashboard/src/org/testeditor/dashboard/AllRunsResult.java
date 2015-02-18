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

import java.io.File;

/**
 * Model Object to Collect all test results.
 * 
 */
public class AllRunsResult {

	private File filePath;
	private File testResultFilePath;
	private String date;
	private String name;
	private int duration;
	private int runCount;
	private int quantityRight;
	private int quantityWrong;
	private int quantityExceptions;
	private int quantityIgnores;
	private String subSetRightOf;
	private String subSetWrongOf;
	private boolean suite = false;
	private boolean testcase = false;

	/**
	 * 
	 * @return testcase
	 */
	public boolean isTestcase() {
		return testcase;
	}

	/**
	 * @param testcase
	 *            if testcase set true
	 */
	public void setTestcase(boolean testcase) {
		this.testcase = testcase;
	}

	/**
	 * 
	 * @return suite
	 */
	public boolean isSuite() {
		return suite;
	}

	/**
	 * @param suite
	 *            if suite set true
	 */
	public void setSuite(boolean suite) {
		this.suite = suite;
	}

	/**
	 * 
	 * @return subSetRightOf
	 */
	public String getSubSetRightOf() {
		return subSetRightOf;
	}

	/**
	 * @param subSetRightOf
	 *            test case right steps of all steps or suite right test cases
	 *            of all test cases
	 */
	public void setSubSetRightOf(String subSetRightOf) {
		this.subSetRightOf = subSetRightOf;
	}

	/**
	 * 
	 * @return subSetWrongOf
	 */
	public String getSubSetWrongOf() {
		return subSetWrongOf;
	}

	/**
	 * @param subSetWrongOf
	 *            test case failed steps of all steps or suite failed test cases
	 *            of all test cases
	 */
	public void setSubSetWrongOf(String subSetWrongOf) {
		this.subSetWrongOf = subSetWrongOf;
	}

	/**
	 * 
	 * @return resultSummary
	 */
	public String getResultSummary() {
		return "r: " + getQuantityRight() + ", w: " + getQuantityWrong() + ", i: " + getQuantityIgnores() + ", e: "
				+ getQuantityExceptions();
	}

	/**
	 * result file of test suite.
	 * 
	 * @return absolute path to the result XML
	 */
	public File getTestResultFilePath() {
		return testResultFilePath;
	}

	/**
	 * @param testResultFilePath
	 *            path of suite result file
	 */
	public void setTestResultFilePath(File testResultFilePath) {
		this.testResultFilePath = testResultFilePath;
	}

	/**
	 * @return filePath absolute path to suite or test case folder
	 */
	public File getFilePath() {
		return filePath;
	}

	/**
	 * @param filePath
	 *            sets path to suite or test case folder
	 */
	public void setFilePath(File filePath) {
		this.filePath = filePath;
	}

	/**
	 * @return quantityExceptions amount of exceptions in test case run
	 */
	public int getQuantityExceptions() {
		return quantityExceptions;
	}

	/**
	 * @param quantityExceptions
	 *            sets exceptions amount from test run
	 */
	public void setQuantityExceptions(int quantityExceptions) {
		this.quantityExceptions = quantityExceptions;
	}

	/**
	 * @return quantityIgnores amount of ignored steps from test run
	 */
	public int getQuantityIgnores() {
		return quantityIgnores;
	}

	/**
	 * @param quntityIgnores
	 *            sets amount of ignored steps from test run
	 */
	public void setQuantityIgnores(int quntityIgnores) {
		this.quantityIgnores = quntityIgnores;
	}

	/**
	 * @return date of test run
	 */
	public String getDate() {
		return date;
	}

	/**
	 * @param date
	 *            sets date of test run
	 */
	public void setDate(String date) {
		this.date = date;
	}

	/**
	 * @return name of test case or suite DemoWebTests.GoogleSucheSuite
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            sets name of test case or suite (for example
	 *            DemoWebTests.GoogleSucheSuite)
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return duration of test run
	 */
	public int getDuration() {
		return duration;
	}

	/**
	 * @param duration
	 *            sets test run duration
	 */
	public void setDuration(int duration) {
		this.duration = duration;
	}

	/**
	 * @return runCount of all runs
	 */
	public int getRunCount() {
		return runCount;
	}

	/**
	 * @param runCount
	 *            sets amount of all runs for one test case or suite
	 */
	public void setRunCount(int runCount) {
		this.runCount = runCount;
	}

	/**
	 * @return quantityRight right steps count of test case or suite
	 */
	public int getQuantityRight() {
		return quantityRight;
	}

	/**
	 * @param quantityRight
	 *            sets right steps amount of test case or sutie
	 */
	public void setQuantityRight(int quantityRight) {
		this.quantityRight = quantityRight;
	}

	/**
	 * @return quantityWrong wrong steps count of test case or suite
	 */
	public int getQuantityWrong() {
		return quantityWrong;
	}

	/**
	 * @param quantityWrong
	 *            sets wrong steps amount of test case or suite
	 */
	public void setQuantityWrong(int quantityWrong) {
		this.quantityWrong = quantityWrong;
	}

	/**
	 * @return calculates if test totally was successfully
	 * 
	 */
	public boolean isSuccessfully() {
		return getQuantityWrong() + getQuantityExceptions() + getQuantityIgnores() == 0 && getQuantityRight() > 0;
	}

	/**
	 * @return calculates if test totally failed
	 * 
	 */
	public boolean isFailed() {
		return getQuantityWrong() > 0 || getQuantityExceptions() > 0 || getQuantityIgnores() > 0;
	}

	/**
	 * @return calculates if test totally is a warning
	 * 
	 */
	public boolean isWarning() {
		return getQuantityRight() + getQuantityWrong() + getQuantityIgnores() + getQuantityExceptions() == 0
				|| getQuantityWrong() + getQuantityExceptions() + getQuantityRight() == 0 && getQuantityIgnores() > 0;
	}
}
