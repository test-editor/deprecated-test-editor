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
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.testeditor.core.model.teststructure.TestData;
import org.testeditor.core.model.teststructure.TestDataRow;
import org.testeditor.ui.constants.TestEditorEventConstants;

/**
 * special class extending the @link {@link EditingSupport}.
 * 
 * @author llipinski
 * 
 */
public class TestEditorViewEditingSupport extends EditingSupport {

	private GridTableViewer tableViewer;
	private int colNo;
	private TextCellEditor textCellEditor;
	private MPart mPart;
	private IEventBroker eventBroker;
	private TestData dataTable;

	/**
	 * constructor with parameters.
	 * 
	 * @param tableViewer
	 *            the GridTableViewer
	 * @param colNo
	 *            the number of the column as an int.
	 * @param textCellEditor
	 *            the TextCellEditor
	 * @param mPart
	 *            the parent part
	 * @param dataTable
	 *            TestData
	 * @param eventBroker
	 *            IEventBroker
	 */
	public TestEditorViewEditingSupport(GridTableViewer tableViewer, int colNo, TextCellEditor textCellEditor,
			MPart mPart, IEventBroker eventBroker, TestData dataTable) {
		super(tableViewer);
		this.tableViewer = tableViewer;
		this.colNo = colNo;
		this.textCellEditor = textCellEditor;
		this.mPart = mPart;
		this.eventBroker = eventBroker;
		this.dataTable = dataTable;
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		return textCellEditor;
	}

	@Override
	protected boolean canEdit(Object element) {
		return true;
	}

	@Override
	protected Object getValue(Object element) {
		return ((TestDataRow) element).getColumn(colNo);
	}

	@Override
	protected void setValue(Object element, Object value) {
		String oldValue = ((TestDataRow) element).getColumn(colNo);
		if (!value.equals(oldValue)) {
			((TestDataRow) element).setColumn(colNo, (String) value);
			tableViewer.update(element, null);
			eventBroker.send(TestEditorEventConstants.REFRESH_TEST_FLOW_VIEW, dataTable);
			mPart.setDirty(true);
		}
	}
}
