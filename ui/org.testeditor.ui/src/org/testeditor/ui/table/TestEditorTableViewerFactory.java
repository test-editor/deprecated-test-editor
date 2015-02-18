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
package org.testeditor.ui.table;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.nebula.jface.gridviewer.GridColumnLayout;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerEditor;
import org.eclipse.swt.widgets.Composite;

/**
 * Wrapper for the GridTableViewer.
 * 
 * @author llipinski
 * 
 */
public final class TestEditorTableViewerFactory {
	/**
	 * private constructor.
	 */
	private TestEditorTableViewerFactory() {
	}

	/**
	 * creates the table-viewer and the table with editing support.
	 * 
	 * @param parent
	 *            the parent composite.
	 * @param inputModel
	 *            the data-model
	 * @param columCreater
	 *            implements the ITestEditorTableColumnCreater
	 * @return the created GridTableViewer
	 */
	public static GridTableViewer createTableViewer(Composite parent, Object inputModel,
			ITestEditorTableColumnCreater columCreater) {
		GridTableViewer tableViewer = new GridTableViewer(parent);
		tableViewer.getGrid().setHeaderVisible(true);
		tableViewer.getGrid().setCellSelectionEnabled(true);

		final TextCellEditor textCellEditor = new TestEditorTextCellEditor(tableViewer.getGrid());
		ColumnViewerEditorActivationStrategy strat = new TestEditorColumnViewerEditorActivationStrategy(tableViewer);

		GridViewerEditor.create(tableViewer, strat, GridViewerEditor.TABBING_HORIZONTAL
				| GridViewerEditor.TABBING_VERTICAL | GridViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR
				| GridViewerEditor.KEYBOARD_ACTIVATION);
		GridColumnLayout tcl = new GridColumnLayout();
		parent.setLayout(tcl);
		columCreater.createColumns(tableViewer, textCellEditor, tcl);
		tableViewer.setContentProvider(ArrayContentProvider.getInstance());
		tableViewer.setInput(inputModel);
		return tableViewer;
	}
}
