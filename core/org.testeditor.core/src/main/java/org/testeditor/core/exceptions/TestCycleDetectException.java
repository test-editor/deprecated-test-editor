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
package org.testeditor.core.exceptions;

import java.util.List;

/**
 * 
 * Exception for Cycle in TestStructure Call hierarchies.
 * 
 *
 */
public class TestCycleDetectException extends Exception {

	/**
	 * Default serialUID.
	 */
	private static final long serialVersionUID = 1L;

	private List<String> testFlowStack;

	/**
	 * Creates the TestCycleDetectException.
	 * 
	 * @param testFlowStack
	 *            stack of testflows containing the cycle.
	 */
	public TestCycleDetectException(List<String> testFlowStack) {
		this.testFlowStack = testFlowStack;
	}

	/**
	 * 
	 * @return stack of testflows with the cycle as string.
	 */
	public String getCycleString() {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (String testFlow : testFlowStack) {
			if (first) {
				first = false;
			} else {
				sb.append(", \n");
			}
			sb.append(testFlow);
		}
		return sb.toString();
	}

}
