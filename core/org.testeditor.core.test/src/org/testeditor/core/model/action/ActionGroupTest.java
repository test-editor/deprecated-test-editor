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
 * 
 * this class test the functionality of the ActionGroup.
 * 
 * @author llipinski
 */
public class ActionGroupTest {
	/**
	 * this test tests the sorting of the actions.
	 */
	@Test
	public void testSortActionsTest() {
		ActionGroup actionGr = new ActionGroup();
		Action firstAction = new Action();
		firstAction.setSorting(1);
		ArrayList<Argument> firstArguments = new ArrayList<Argument>();
		firstArguments.add(new Argument("first", "first"));
		firstAction.setArguments(firstArguments);
		actionGr.addAction(firstAction);
		Action secondAction = new Action();
		secondAction.setSorting(3);
		ArrayList<Argument> secondArguments = new ArrayList<Argument>();
		secondArguments.add(new Argument("second", "second"));
		secondAction.setArguments(secondArguments);
		actionGr.addAction(secondAction);
		Action thirdAction = new Action();
		thirdAction.setSorting(2);
		ArrayList<Argument> thirdArguments = new ArrayList<Argument>();
		thirdArguments.add(new Argument("third", "third"));
		thirdAction.setArguments(thirdArguments);
		actionGr.addAction(thirdAction);
		assertEquals("second", actionGr.getActions().get(1).getArguments().get(0).getValue());

		actionGr.sortActions();

		assertEquals("third", actionGr.getActions().get(1).getArguments().get(0).getValue());

	}

	/**
	 * this test tests the sorting of the actions.
	 */
	@Test
	public void testSortActionsWithoutSortingValueTest() {
		ActionGroup actionGr = new ActionGroup();
		Action firstAction = new Action();
		ArrayList<Argument> firstArguments = new ArrayList<Argument>();
		firstArguments.add(new Argument("first", "first"));
		firstAction.setArguments(firstArguments);
		actionGr.addAction(firstAction);
		Action secondAction = new Action();
		secondAction.setSorting(3);
		ArrayList<Argument> secondArguments = new ArrayList<Argument>();
		secondArguments.add(new Argument("second", "second"));
		secondAction.setArguments(secondArguments);
		actionGr.addAction(secondAction);
		Action thirdAction = new Action();
		thirdAction.setSorting(2);
		ArrayList<Argument> thirdArguments = new ArrayList<Argument>();
		thirdArguments.add(new Argument("third", "third"));
		thirdAction.setArguments(thirdArguments);
		actionGr.addAction(thirdAction);
		Action fourthAction = new Action();
		ArrayList<Argument> fourthArguments = new ArrayList<Argument>();
		thirdArguments.add(new Argument("fourth", "fourth"));
		fourthAction.setArguments(fourthArguments);
		actionGr.addAction(fourthAction);

		assertEquals("second", actionGr.getActions().get(1).getArguments().get(0).getValue());

		actionGr.sortActions();

		assertEquals("third", actionGr.getActions().get(0).getArguments().get(0).getValue());
		assertEquals("second", actionGr.getActions().get(1).getArguments().get(0).getValue());
		assertEquals("first", actionGr.getActions().get(2).getArguments().get(0).getValue());
	}

	/**
	 * this test tests the sorting of the actions.
	 */
	@Test
	public void testCompareTo() {
		ActionGroup actionGr = new ActionGroup();
		ActionGroup compareActionGr = new ActionGroup();
		actionGr.setName("group");
		compareActionGr.setName("copare");

		actionGr.setSorting(null);
		compareActionGr.setSorting(1);
		assertEquals(actionGr.compareTo(compareActionGr), 1);
		actionGr.setSorting(null);
		compareActionGr.setSorting(null);
		assertEquals(actionGr.compareTo(compareActionGr), 4);
		actionGr.setSorting(1);
		compareActionGr.setSorting(null);
		assertEquals(actionGr.compareTo(compareActionGr), -1);
		actionGr.setSorting(1);
		compareActionGr.setSorting(1);
		assertEquals(actionGr.compareTo(compareActionGr), 4);
		actionGr.setSorting(3);
		compareActionGr.setSorting(1);
		assertEquals(actionGr.compareTo(compareActionGr), 1);
		actionGr.setSorting(1);
		compareActionGr.setSorting(3);
		assertEquals(actionGr.compareTo(compareActionGr), -1);
	}
}
