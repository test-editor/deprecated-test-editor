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

import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.nebula.jface.gridviewer.GridColumnLayout;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;

/**
 * interface for the creation of the columns in the nebula-grid.
 * 
 * @author llipinski
 * 
 */
public interface ITestEditorTableColumnCreater {
	/**
	 * this method should create the columns for the table.
	 * 
	 * @param tableViewer
	 *            GridTableViewer
	 * @param textCellEditor
	 *            the TextCellEditor
	 * @param tcl
	 *            GridColumnLayout
	 */
	void createColumns(final GridTableViewer tableViewer, TextCellEditor textCellEditor, GridColumnLayout tcl);

}
