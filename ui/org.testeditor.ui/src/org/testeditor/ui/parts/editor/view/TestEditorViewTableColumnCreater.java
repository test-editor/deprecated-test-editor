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
package org.testeditor.ui.parts.editor.view;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.nebula.jface.gridviewer.GridColumnLayout;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.swt.SWT;
import org.testeditor.core.model.teststructure.TestData;
import org.testeditor.ui.table.ITestEditorTableColumnCreater;

/**
 * this class creates the column.
 * 
 * @author llipinski
 * 
 */
public class TestEditorViewTableColumnCreater implements ITestEditorTableColumnCreater {

	private TestData dataTable;
	private MPart mpart;
	private IEventBroker eventBroker;

	/**
	 * constructor.
	 * 
	 * @param dataTable
	 *            TestData
	 * @param mpart
	 *            the parent part
	 * @param eventBroker
	 *            IEventBroker
	 */
	public TestEditorViewTableColumnCreater(TestData dataTable, MPart mpart, IEventBroker eventBroker) {
		this.dataTable = dataTable;
		this.mpart = mpart;
		this.eventBroker = eventBroker;

	}

	@Override
	public void createColumns(GridTableViewer tableViewer, TextCellEditor textCellEditor, GridColumnLayout tcl) {
		for (int colNo = 0; colNo < dataTable.getTitleRow().getList().size(); colNo++) {
			creatColumn(tableViewer, textCellEditor, colNo);
		}
	}

	/**
	 * private method to create a column.
	 * 
	 * @param tableViewer
	 *            GridTableViewer
	 * @param textCellEditor
	 *            TextCellEditor
	 * @param colNo
	 *            the number of the column as an int.
	 */
	private void creatColumn(GridTableViewer tableViewer, TextCellEditor textCellEditor, int colNo) {
		GridViewerColumn viewerColumn = new GridViewerColumn(tableViewer, SWT.NONE);
		viewerColumn.getColumn().setText(dataTable.getTitleRow().getColumn(colNo));
		viewerColumn.setLabelProvider(new TestEditorViewColumnLableProvider(colNo));
		viewerColumn.setEditingSupport(new TestEditorViewEditingSupport(tableViewer, colNo, textCellEditor, mpart,
				eventBroker, dataTable));

	}

}
