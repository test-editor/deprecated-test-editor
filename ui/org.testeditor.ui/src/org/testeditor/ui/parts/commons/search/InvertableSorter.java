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
import org.eclipse.jface.viewers.ViewerSorter;

/**
 * This abstract class provides methods for getting the sort direction, to
 * compare entries and getting an InvertableSorter
 *
 */
abstract class InvertableSorter extends ViewerSorter {

	/**
	 * Compares the given 2 objects.
	 * 
	 * @param viewer
	 *            tableViewer
	 * @param e1
	 *            first entry
	 * @param e2
	 *            second entry
	 * 
	 * @return default compare values.
	 * 
	 */
	public abstract int compare(Viewer viewer, Object e1, Object e2);

	/**
	 * 
	 * @return the InvertableSorter
	 */
	abstract InvertableSorter getInverseSorter();

	/**
	 * 
	 * @return sort direction as SWT.UP/SWT.DOWN
	 */
	public abstract int getSortDirection();
}