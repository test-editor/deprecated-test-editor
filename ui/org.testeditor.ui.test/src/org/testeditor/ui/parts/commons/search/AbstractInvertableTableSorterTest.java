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
package org.testeditor.ui.parts.commons.search;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.junit.Assert;
import org.junit.Test;

public class AbstractInvertableTableSorterTest {

	@Test
	public void testInvertableSorter() {

		InvertableSorter sorter = new AbstractInvertableTableSorter() {

			@Override
			public int compare(Viewer viewer, Object e1, Object e2) {
				return 0;
			}
		};

		InvertableSorter invertableSorter = sorter.getInverseSorter();
		Assert.assertTrue(SWT.DOWN == invertableSorter.getSortDirection());
		Assert.assertTrue(invertableSorter != null);

	}

	@Test
	public void testInvertableSorter2() {

		InvertableSorter sorter = new AbstractInvertableTableSorter() {

			@Override
			public int compare(Viewer viewer, Object e1, Object e2) {
				return 0;
			}
		};

		InvertableSorter invertableSorter = sorter.getInverseSorter().getInverseSorter();
		Assert.assertTrue(invertableSorter != null);

	}

	@Test
	public void testSortDirection() {

		InvertableSorter sorter = new AbstractInvertableTableSorter() {

			@Override
			public int compare(Viewer viewer, Object e1, Object e2) {
				return 0;
			}
		};

		Assert.assertTrue(SWT.UP == sorter.getSortDirection());
	}

}
