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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * 
 * Modultests for InstructionsResult.
 *
 */
public class InstructionsResultTest {

	/**
	 * Tests the toString behavior.
	 */
	@Test
	public void testToString() {
		InstructionsResult resultTable = new InstructionsResult();
		resultTable.setInstruction("MyInst");
		assertEquals("MyInst results: null", resultTable.toString());
		resultTable.setResult("ok");
		assertEquals("MyInst results: ok", resultTable.toString());
	}

	/**
	 * Tests the adding and accessing of InstructionExpectation.
	 */
	@Test
	public void testInstructionExpectionManagment() {
		InstructionsResult result = new InstructionsResult();
		assertNotNull("Expecting min an empty List of InstructionExpectation", result.getInstructionExpectations());
		assertTrue("Expecting an empty list as init value ", result.getInstructionExpectations().isEmpty());
		result.addExpectation(new InstructionExpectation());
		assertFalse("Expecting an empty list as init value ", result.getInstructionExpectations().isEmpty());
	}

}
