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

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.testeditor.core.model.teststructure.TestCase;

/**
 * 
 * Modultests for ErrorContainer.
 * 
 */
public class ErrorContainerTest {

	/**
	 * Test the String representation of the Error object.
	 */
	@Test
	public void testToString() {
		TestCase testCase = new TestCase();
		testCase.setName("MyTest");
		ErrorContainer container = new ErrorContainer(testCase);
		container.add(new Error(testCase));
		container.add(new Error(testCase));
		assertEquals("MyTest (2)", container.toString());
	}

}
