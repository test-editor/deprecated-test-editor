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

/**
 * 
 * Model Object to store the details of the result of Instruction part
 * execution. This contains: Expected Value comparing, Exceptions...
 *
 */
public class InstructionExpectation {

	private int actionPartPosition;
	private String status;

	/**
	 * 
	 * @return Status of the Instruction execution.
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * a test instruction an Action is composed by parts. For Example
	 * "start application" "myExecutable" has to parts. This method returns the
	 * part order od of this InstructionExpectation.
	 * 
	 * @return the position in the test sentence.
	 */
	public int getActionPartPosition() {
		return actionPartPosition;
	}

	/**
	 * @see getActionPartPosition
	 * 
	 * @param actionPartPosition
	 *            in the Action part.
	 */
	public void setActionPartPosition(int actionPartPosition) {
		this.actionPartPosition = actionPartPosition;
	}

	/**
	 * 
	 * @param status
	 *            of this Expectation
	 */
	public void setStatus(String status) {
		this.status = status;
	}

}
