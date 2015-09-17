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

/**
 * 
 * This abstract class provides methods for getting the sort direction, to
 * compare entries and getting an InvertableSorter.
 */
public abstract class AbstractInvertableTableSorter extends InvertableSorter {

	private final InvertableSorter inverse = new InvertableSorter() {

		public int compare(Viewer viewer, Object e1, Object e2) {
			return (-1) * AbstractInvertableTableSorter.this.compare(viewer, e1, e2);
		}

		InvertableSorter getInverseSorter() {
			return AbstractInvertableTableSorter.this;
		}

		public int getSortDirection() {
			return SWT.DOWN;
		}
	};

	/**
	 * @return an InvertableSorter.
	 */
	InvertableSorter getInverseSorter() {
		return inverse;
	}

	/**
	 * @return a sortDirection here the sortdirection up.
	 */
	public int getSortDirection() {
		return SWT.UP;
	}
}