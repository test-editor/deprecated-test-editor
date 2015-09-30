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

import org.junit.Test;

/**
 * Tests for ActionGroupPositionComparator.
 *
 */
public class ActionGroupPositionComparatorTest {

	/**
	 * this test tests the sorting of the actions.
	 */
	@Test
	public void testCompareTo() {
		ActionGroupPositionComparator comparator = new ActionGroupPositionComparator();
		ActionGroup actionGr = new ActionGroup();
		ActionGroup compareActionGr = new ActionGroup();
		actionGr.setName("group");
		compareActionGr.setName("copare");

		actionGr.setSorting(null);
		compareActionGr.setSorting(1);
		assertEquals(comparator.compare(actionGr, compareActionGr), 1);
		actionGr.setSorting(null);
		compareActionGr.setSorting(null);
		assertEquals(comparator.compare(actionGr, compareActionGr), 4);
		actionGr.setSorting(1);
		compareActionGr.setSorting(null);
		assertEquals(comparator.compare(actionGr, compareActionGr), -1);
		actionGr.setSorting(1);
		compareActionGr.setSorting(1);
		assertEquals(comparator.compare(actionGr, compareActionGr), 4);
		actionGr.setSorting(3);
		compareActionGr.setSorting(1);
		assertEquals(comparator.compare(actionGr, compareActionGr), 1);
		actionGr.setSorting(1);
		compareActionGr.setSorting(3);
		assertEquals(comparator.compare(actionGr, compareActionGr), -1);
	}

}
