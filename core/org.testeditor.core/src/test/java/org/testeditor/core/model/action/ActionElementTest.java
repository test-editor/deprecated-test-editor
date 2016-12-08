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
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * 
 * JunitTest for the actionElement.
 * 
 */
public class ActionElementTest {

	/**
	 * test the type of the actionElement.
	 */
	@Test
	public void testGetType() {
		ActionElement actionElement = new ActionElement(0, ActionElementType.ARGUMENT, "Test", "");
		assertEquals(actionElement.getType(), ActionElementType.ARGUMENT);
	}

	/**
	 * test the value of an actionElement.
	 */
	@Test
	public void testGetValue() {
		ActionElement actionElement = new ActionElement(0, ActionElementType.ARGUMENT, "Test", "");
		assertEquals(actionElement.getValue(), "Test");

	}

	/**
	 * test the setType method.
	 * 
	 */
	@Test
	public void testSetType() {
		ActionElement actionElement = new ActionElement(0, ActionElementType.ARGUMENT, "Test", "");
		assertEquals(actionElement.getType(), ActionElementType.ARGUMENT);
		actionElement.setType(ActionElementType.ACTION_NAME);
		assertEquals(actionElement.getType(), ActionElementType.ACTION_NAME);

	}

	/**
	 * test the setValue method.
	 */
	@Test
	public void testSetValue() {
		ActionElement actionElement = new ActionElement(0, ActionElementType.ARGUMENT, "Test", "");
		assertEquals(actionElement.getValue(), "Test");
		actionElement.setValue("anderer Test");
		assertEquals(actionElement.getValue(), "anderer Test");
	}

	/**
	 * test the getPosition method.
	 */
	@Test
	public void testGetPosition() {
		ActionElement actionElement = new ActionElement(27, ActionElementType.ARGUMENT, "Test", "");
		assertTrue(actionElement.getPosition() == 27);
	}

	/**
	 * test the getPosition method.
	 */
	@Test
	public void testGetid() {
		ActionElement actionElement = new ActionElement(27, ActionElementType.ARGUMENT, "Test", "4");
		assertTrue(actionElement.getId() == "4");
	}

}
