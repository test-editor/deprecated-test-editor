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

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.TableColumn;

/**
 * Listener for handling column sort order.
 * 
 */
public class TableSortSelectionListener implements SelectionListener {
	private final TableViewer viewer;
	private final TableColumn column;
	private final InvertableSorter sorter;
	private final boolean keepDirection;
	private InvertableSorter currentSorter;

	/**
	 * The constructor of this listener.
	 * 
	 * @param viewer
	 *            the tableviewer this listener belongs to
	 * @param column
	 *            the column this listener is responsible for
	 * @param sorter
	 *            the sorter this listener uses
	 * @param defaultDirection
	 *            the default sorting direction of this Listener. Possible
	 *            values are {@link SWT.UP} and {@link SWT.DOWN}
	 * @param keepDirection
	 *            if true, the listener will remember the last sorting direction
	 *            of the associated column and restore it when the column is
	 *            reselected. If false, the listener will use the default soting
	 *            direction
	 */
	public TableSortSelectionListener(TableViewer viewer, TableColumn column, AbstractInvertableTableSorter sorter,
			int defaultDirection, boolean keepDirection) {
		this.viewer = viewer;
		this.column = column;
		this.keepDirection = keepDirection;

		if (defaultDirection == SWT.UP) {
			this.sorter = sorter;
		} else {
			this.sorter = sorter.getInverseSorter();
		}

		this.currentSorter = this.sorter;

		this.column.addSelectionListener(this);
	}

	/**
	 * Chooses the colum of this listener for sorting of the table. Mainly used
	 * when first initialising the table.
	 */
	public void chooseColumnForSorting() {
		viewer.getTable().setSortColumn(column);
		viewer.getTable().setSortDirection(currentSorter.getSortDirection());
		viewer.setSorter(currentSorter);
	}

	/**
	 * Inverts the sort order by click on column header.
	 * 
	 * @param e
	 *            SelectionEvent
	 * 
	 */
	public void widgetSelected(SelectionEvent e) {
		InvertableSorter newSorter;
		if (viewer.getTable().getSortColumn() == column) {
			newSorter = ((InvertableSorter) viewer.getSorter()).getInverseSorter();
		} else {
			if (keepDirection) {
				newSorter = currentSorter;
			} else {
				newSorter = sorter;
			}
		}

		currentSorter = newSorter;
		chooseColumnForSorting();
	}

	/**
	 * Inverts the sort order by click on column header.
	 * 
	 * @param e
	 *            SelectionEvent
	 * 
	 */
	public void widgetDefaultSelected(SelectionEvent e) {
		widgetSelected(e);
	}
}