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
package org.testeditor.ui.analyzer.errormodel;

import java.util.ArrayList;
import java.util.List;

import org.testeditor.core.model.teststructure.TestFlow;

/**
 * 
 * Container class to collect errors of a testflow.
 * 
 */
public class ErrorContainer {

	private TestFlow testFlow;
	private List<Error> errorList;

	/**
	 * Creates an ErrorContainer to collect errors in the testflow structure.
	 * 
	 * @param testFlow
	 *            which contains the errors.
	 */
	public ErrorContainer(TestFlow testFlow) {
		this.testFlow = testFlow;
		errorList = new ArrayList<Error>();
	}

	/**
	 * 
	 * @return testflow with errors.
	 */
	public TestFlow getTestFlow() {
		return testFlow;
	}

	/**
	 * 
	 * @param error
	 *            of the testflow.
	 */
	public void add(Error error) {
		errorList.add(error);
	}

	/**
	 * 
	 * @return list with all errors in the testflow.
	 */
	public List<Error> getErrorList() {
		return errorList;
	}

	@Override
	public String toString() {
		return getTestFlow().getFullName() + " (" + getErrorList().size() + ")";
	}

}
