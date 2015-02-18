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

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * Model class to store the Instruction results of an action execution.
 *
 */
public class InstructionsResult {

	private String instruction;
	private String result;
	private List<InstructionExpectation> instructionExpectations = new ArrayList<InstructionExpectation>();

	/**
	 * 
	 * @param instruction
	 *            instruction of the test.
	 */
	public void setInstruction(String instruction) {
		this.instruction = instruction;
	}

	/**
	 * 
	 * @return instruction of the test.
	 */
	public String getInstruction() {
		return instruction;
	}

	@Override
	public String toString() {
		return getInstruction() + " results: " + getResult();
	}

	/**
	 * 
	 * @return result of the instruction
	 */
	public String getResult() {
		return result;
	}

	/**
	 * 
	 * @param result
	 *            to be stroed in this object.
	 */
	public void setResult(String result) {
		this.result = result;
	}

	/**
	 * 
	 * @param instructionExpectation
	 *            to be added to this InstructionResult
	 */
	public void addExpectation(InstructionExpectation instructionExpectation) {
		instructionExpectations.add(instructionExpectation);
	}

	/**
	 * 
	 * @return List with all InstructionExpectation matching to this
	 *         InstructionResult.
	 */
	public List<InstructionExpectation> getInstructionExpectations() {
		return instructionExpectations;
	}

}
