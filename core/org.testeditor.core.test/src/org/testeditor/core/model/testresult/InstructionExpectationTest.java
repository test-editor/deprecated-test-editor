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

import org.junit.Test;

/**
 * 
 * Modultest for InstructionExpectation.
 *
 */
public class InstructionExpectationTest {

	/**
	 * very simple test to verify object creation and getter setter.
	 */
	@Test
	public void testInit() {
		InstructionExpectation ie = new InstructionExpectation();
		ie.setStatus("foo");
		ie.setActionPartPosition(2);
		assertEquals("foo", ie.getStatus());
		assertEquals(2, ie.getActionPartPosition());
	}

}
