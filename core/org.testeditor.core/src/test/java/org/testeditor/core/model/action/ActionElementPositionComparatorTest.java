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
 * Test for ActionElementPositionComparator.
 *
 */
public class ActionElementPositionComparatorTest {

	/**
	 * test the compareTo method.
	 */
	@Test
	public void testCompareTo() {
		ActionElementPositionComparator comparator = new ActionElementPositionComparator();
		ActionElement actionElement = new ActionElement(27, ActionElementType.ARGUMENT, "Test", "");
		ActionElement actionElementCompare = new ActionElement();
		assertEquals(-1, comparator.compare(actionElement, actionElementCompare));
		assertEquals(1, comparator.compare(actionElementCompare, actionElement));
		ActionElement actionElementCompareNull = new ActionElement();
		assertEquals(0, comparator.compare(actionElementCompare, actionElementCompareNull));
		ActionElement actionElement3 = new ActionElement(3, ActionElementType.ARGUMENT, "Test_3", "");
		assertEquals(1, comparator.compare(actionElement, actionElement3));
		assertEquals(-1, comparator.compare(actionElement3, actionElement));
		assertEquals(0, comparator.compare(actionElement, actionElement));

	}
}
