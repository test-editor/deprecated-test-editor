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

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.testeditor.core.model.action.Action;
import org.testeditor.core.model.teststructure.TestCase;

/**
 * 
 * Modultests for Error.
 * 
 */
public class ErrorTest {

	/**
	 * Test the String representation of the Error object.
	 */
	@Test
	public void testToString() {
		Error error = new Error(new TestCase());
		Action action = new Action() {
			@Override
			public List<String> getSourceCode() {
				List<String> result = new ArrayList<String>();
				result.add("|myAction|");
				result.add("|Hello World|");
				return result;
			}
		};
		error.setAction(action);
		assertEquals("|myAction|\n|Hello World|", error.toString());
	}

}
