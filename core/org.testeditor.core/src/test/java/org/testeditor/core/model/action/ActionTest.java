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
import org.testeditor.core.exceptions.CorrruptLibraryException;

/**
 * 
 * test for the action.class.
 * 
 * @author llipinski
 */
public class ActionTest {
	/**
	 * test the compareTo-method by the sorting-member.
	 */
	@Test
	public void testCompareTo() {
		Action action = new Action();
		Action actionCompare = new Action();
		action.setSorting(1);
		actionCompare.setSorting(2);
		assertEquals(-1, action.compareTo(actionCompare));

	}

	/**
	 * compare sorting-member with a null-value in the sorting.
	 */
	@Test
	public void testCompareToNull() {
		Action action = new Action();
		Action actionCompare = new Action();
		action.setSorting(1);
		assertEquals(-1, action.compareTo(actionCompare));

	}

	/**
	 * compare null-value in the sorting with the sorting member.
	 */
	@Test
	public void testCompareNullToSorting() {
		Action action = new Action();
		Action actionCompare = new Action();
		actionCompare.setSorting(1);
		assertEquals(1, action.compareTo(actionCompare));
	}

	/**
	 * compares to equal-sorting member-variables.
	 * 
	 * @throws CorrruptLibraryException
	 *             exception will be thrown if error in library was found
	 */
	@Test
	public void testCompareSortEqual() throws CorrruptLibraryException {
		ArrayList<Argument> argumentList = new ArrayList<Argument>();
		TechnicalBindingType technicalBindingType = new TechnicalBindingType();
		ArrayList<ChoiceList> choiceLists = new ArrayList<ChoiceList>();
		Action action = new Action("A", argumentList, 1, technicalBindingType, choiceLists);
		Action actionCompare = new Action("A", argumentList, 1, technicalBindingType, choiceLists);
		assertEquals(0, action.compareTo(actionCompare));
	}

	/**
	 * compares by the name of the action.
	 * 
	 * @throws CorrruptLibraryException
	 *             exception will be thrown if error in library was found
	 */
	@Test
	public void testCompareByName() throws CorrruptLibraryException {
		ArrayList<Argument> argumentList = new ArrayList<Argument>();
		TechnicalBindingType technicalBindingType = new TechnicalBindingType();
		ArrayList<ChoiceList> choiceLists = new ArrayList<ChoiceList>();
		Action action = new Action("B", argumentList, 1, technicalBindingType, choiceLists);
		Action actionCompare = new Action("A", argumentList, 1, technicalBindingType, choiceLists);
		assertEquals(1, action.compareTo(actionCompare));
	}

	/**
	 * sorting-parameters in opposite direction.
	 */
	@Test
	public void testCompareToOpposite() {
		Action action = new Action();
		Action actionCompare = new Action();
		action.setSorting(2);
		actionCompare.setSorting(1);
		assertEquals(1, action.compareTo(actionCompare));

	}

	/**
	 * the setTechnicalBindingType.
	 * 
	 * @throws CorrruptLibraryException
	 *             exception will be thrown if error in library was found
	 */
	@Test
	public void testSetTechnicalBindingType() throws CorrruptLibraryException {
		ArrayList<Argument> argumentList = new ArrayList<Argument>();
		TechnicalBindingType technicalBindingType = new TechnicalBindingType();
		technicalBindingType.setId("3");
		TechnicalBindingType technicalBindingTypeNew = new TechnicalBindingType();
		technicalBindingType.setId("5");
		ArrayList<ChoiceList> choiceLists = new ArrayList<ChoiceList>();
		Action action = new Action("", argumentList, 1, technicalBindingType, choiceLists);
		action.setTechnicalBindingType(technicalBindingTypeNew);
		assertEquals(action.getTechnicalBindingType().getId(), technicalBindingTypeNew.getId());
	}

	/**
	 * compares to actions with different arguments.
	 */
	@Test
	public void testCompareByTechnicalBindings() {
		Action action = new Action();
		Action actionCompare = new Action();
		Argument argument = new Argument("1", "A1");
		Argument compareArgument = new Argument("1", "A12");
		TechnicalBindingType technicalBindingType = new TechnicalBindingType();
		technicalBindingType.setId("3");
		technicalBindingType.getActionParts().add(new ActionElement(0, ActionElementType.ACTION_NAME, "", ""));
		action.getArguments().add(argument);
		action.setTechnicalBindingType(technicalBindingType);
		actionCompare.getArguments().add(compareArgument);
		actionCompare.setTechnicalBindingType(technicalBindingType);
		assertEquals(-1, action.compareTo(actionCompare));
	}
}
