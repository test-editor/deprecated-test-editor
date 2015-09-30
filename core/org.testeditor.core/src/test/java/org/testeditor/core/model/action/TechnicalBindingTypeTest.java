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
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

/**
 * 
 * test for the TechnicalBindingTypeTest.class.
 * 
 * @author dkuhlmann
 */
public class TechnicalBindingTypeTest {

	/**
	 * test the setId-method.
	 */
	@Test
	public void testSetId() {
		TechnicalBindingType technicalBindingType = new TechnicalBindingType();
		technicalBindingType.setId("1");
		assertEquals("1", technicalBindingType.getId());
	}

	/**
	 * test the setShortName-method.
	 */
	@Test
	public void testSetShortName() {
		TechnicalBindingType technicalBindingType = new TechnicalBindingType();
		technicalBindingType.setShortName("1");
		assertEquals("1", technicalBindingType.getShortName());
	}

	/**
	 * test the setSorting-method.
	 */
	@Test
	public void testSetSorting() {
		TechnicalBindingType technicalBindingType = new TechnicalBindingType();
		technicalBindingType.setSorting(1);
		assertTrue(1 == technicalBindingType.getSorting());
	}

	/**
	 * test the setActionParts-method.
	 */
	@Test
	public void testSetActionParts() {
		TechnicalBindingType technicalBindingType = new TechnicalBindingType();
		List<ActionElement> actionParts = new ArrayList<ActionElement>();
		ActionElement actionElement = new ActionElement();
		actionElement.setId("1");
		actionParts.add(actionElement);
		assertNotEquals(actionParts, technicalBindingType.getActionParts());
		technicalBindingType.setActionParts(actionParts);
		assertEquals(actionParts, technicalBindingType.getActionParts());
	}

	/**
	 * test the getArgPosChoices-method.
	 */
	@Test
	public void testGetArgPosChoices() {
		TechnicalBindingType technicalBindingType = new TechnicalBindingType();
		List<ActionElement> actionParts = new ArrayList<ActionElement>();
		ActionElement actionElement = new ActionElement(0, null, "", "1");
		actionParts.add(actionElement);
		ActionElement actionElement2 = new ActionElement(1, null, "", "2");
		actionParts.add(actionElement2);
		technicalBindingType.setActionParts(actionParts);
		List<Integer> argPos = technicalBindingType.getArgPosChoices();
		assertTrue(argPos.get(0) == 0);
		assertTrue(argPos.get(1) == 1);

	}
}
