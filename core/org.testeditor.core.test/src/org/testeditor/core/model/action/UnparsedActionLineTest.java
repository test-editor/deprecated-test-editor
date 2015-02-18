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
package org.testeditor.core.model.action;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;

/**
 * this test the action.UnparsedActionLine class.
 * 
 * 
 * @author dkuhlmann
 */
public class UnparsedActionLineTest {

	/**
	 * tests the Set and get Arguments.
	 */
	@Test
	public void testArguments() {
		UnparsedActionLine unparsedActionLine = new UnparsedActionLine("Dies ist ein Test");
		ArrayList<Argument> arguments = new ArrayList<Argument>();
		Argument arg0 = new Argument();
		arg0.setValue("1");
		arguments.add(arg0);
		unparsedActionLine.setArguments(arguments);
		assertEquals(arg0, unparsedActionLine.getArguments().get(0));
	}

	/**
	 * tests the getText methode.
	 */
	@Test
	public void testGetText() {
		UnparsedActionLine unparsedActionLine = new UnparsedActionLine("Test");
		assertEquals("Test", unparsedActionLine.getTexts().get(0).toString());
	}

	/**
	 * tests the getTextTypes methode.
	 */
	@Test
	public void testGetTextTypes() {
		UnparsedActionLine unparsedActionLine = new UnparsedActionLine("Test");
		assertEquals(TextType.UNPARSED_ACTION_lINE, unparsedActionLine.getTextTypes().get(0));
	}

	/**
	 * tests the getSourceCode methode.
	 */
	@Test
	public void testGetSourceCode() {
		UnparsedActionLine unparsedActionLine = new UnparsedActionLine("Test");
		assertEquals("Test", unparsedActionLine.getSourceCode().get(0).toString());
	}

	/**
	 * tests the getTechnicalBindingType methode.
	 */
	@Test
	public void testGetTechnicalBindingType() {
		UnparsedActionLine unparsedActionLine = new UnparsedActionLine("Test");
		TechnicalBindingType technicalBindingType = new TechnicalBindingType();
		assertEquals(technicalBindingType.getSorting(), unparsedActionLine.getTechnicalBindingType().getSorting());
	}
}
