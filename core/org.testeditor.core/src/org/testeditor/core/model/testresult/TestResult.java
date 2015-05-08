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
package org.testeditor.core.model.testresult;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 
 * Result from FitNesse Test.
 * 
 */
public class TestResult {

	private int right = -1;
	private int wrong = -1;
	private int ignored = -1;
	private int exception = -1;

	// in case of suite execution children exists
	private List<TestResult> children = new ArrayList<TestResult>();;

	/**
	 * Execution timestamp of test.
	 */
	private Date resultDate;
	private String fullName;
	private String resultLink;
	private int runTimeMillis;
	private List<InstructionsResult> instructionResultTables;
	private List<ActionResultTable> actionResultTables;
	private URI resultFileUri;

	/**
	 * 
	 * @return count of rights.
	 */
	public int getRight() {
		return right;
	}

	/**
	 * Sets the right count of test.
	 * 
	 * @param right
	 *            count of test.
	 */
	public void setRight(int right) {
		this.right = right;
	}

	/**
	 * 
	 * @return returns the count of wrong.
	 */
	public int getWrong() {
		return wrong;
	}

	/**
	 * Sets the wrong count of test.
	 * 
	 * @param wrong
	 *            wrong count
	 */
	public void setWrong(int wrong) {
		this.wrong = wrong;
	}

	/**
	 * @return returns the count of ignored.
	 */
	public int getIgnored() {
		return ignored;
	}

	/**
	 * 
	 * @param ignored
	 *            ignored value
	 */
	public void setIgnored(int ignored) {
		this.ignored = ignored;
	}

	/**
	 * 
	 * 
	 * @return exception count
	 */
	public int getException() {
		return exception;
	}

	/**
	 * Sets the exception count of test.
	 * 
	 * @param exception
	 *            given exception count.
	 */
	public void setException(int exception) {
		this.exception = exception;
	}

	/**
	 * In case of suite chidren exists.
	 * 
	 * @return test cases of suite
	 */
	public List<TestResult> getChildren() {
		return children;
	}

	/**
	 * .
	 * 
	 * @return result date
	 */
	public Date getResultDate() {
		return new Date(resultDate.getTime());
	}

	/**
	 * @param resultDate
	 *            resultDate
	 */
	public void setResultDate(Date resultDate) {
		this.resultDate = new Date(resultDate.getTime());
	}

	/**
	 * Absolute path of test. e.g.
	 * DemoWebTests.LocalDemoSuite.LoginSuite.LoginInvalidTest
	 * 
	 * @return fullname of test
	 */
	public String getFullName() {
		return fullName;
	}

	/**
	 * 
	 * @param fullName
	 *            fullname
	 */
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	/**
	 * Returns true if the test was running successfully: no wrong or exception
	 * was thrown.
	 * 
	 * @return boolean
	 */
	public boolean isSuccessfully() {
		return getWrong() + getException() == 0;
	}

	@Override
	public String toString() {
		return "TestResult [right=" + right + ", wrong=" + wrong + ", ignored=" + ignored + ", exception=" + exception
				+ ", children=" + children + ", resultDate=" + resultDate + ", fullName=" + fullName + "]";
	}

	/**
	 * Returns true if the test wasn't ran.
	 * 
	 * @return boolean
	 */
	public boolean isNotRun() {
		if (getWrong() == -1 && getException() == -1) {
			return true;
		}
		return false;
	}

	/**
	 * @param testResult
	 *            in case of suite the test case
	 */
	public void add(TestResult testResult) {
		children.add(testResult);
	}

	/**
	 * If the {@link TestResult} contains children, then it is an suite
	 * {@link TestResult}.
	 * 
	 * @return true if it was execeuted a suite
	 */
	public boolean isSuite() {
		return children.size() > 0;
	}

	/**
	 * 
	 * 
	 * @return link to the testresult at the wikipage.
	 */
	public String getResultLink() {
		return resultLink;
	}

	/**
	 * adds the link to the testresult at the wikipage.
	 * 
	 * @param resultLink
	 *            the link to the test-result-wikipage of FitNesse
	 */
	public void setResultLink(String resultLink) {
		this.resultLink = resultLink;
	}

	/**
	 * sets the runTimeMillis.
	 * 
	 * @param runTimeMillis
	 *            int
	 */
	public void setRunTimeMillis(int runTimeMillis) {
		this.runTimeMillis = runTimeMillis;
	}

	/**
	 * 
	 * @return runTimeMillis
	 */
	public int getRunTimeMillis() {
		return runTimeMillis;
	}

	/**
	 * 
	 * @return the runtime in seconds.
	 */
	public int getRunTimesSec() {
		return getRunTimeMillis() / 1000;
	}

	/**
	 * 
	 * @return a List with ActionResultTables
	 */
	public List<ActionResultTable> getActionResultTables() {
		return actionResultTables;
	}

	/**
	 * 
	 * @return a List with InstructionsResultTables
	 */
	public List<InstructionsResult> getInstructionResultTables() {
		return instructionResultTables;
	}

	/**
	 * 
	 * @param instructionResultTables
	 *            stored in this test result.
	 */
	public void setInstructionResultTables(List<InstructionsResult> instructionResultTables) {
		this.instructionResultTables = instructionResultTables;
	}

	/**
	 * 
	 * @param actionResultTables
	 *            stored in this test result.
	 */
	public void setActionResultTables(List<ActionResultTable> actionResultTables) {
		this.actionResultTables = actionResultTables;
	}

	/**
	 * 
	 * @param uri
	 *            of the file that represents the test result.
	 */
	public void setUriToTestResultFile(URI uri) {
		resultFileUri = uri;
	}

	/**
	 * Clients that know the Testserver can use this information for further
	 * reporting.
	 * 
	 * @return uri to a file that represents the test result.
	 */
	public URI getUriToTestResultFile() {
		return resultFileUri;
	}

}
