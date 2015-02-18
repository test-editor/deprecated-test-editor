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
package org.testeditor.ui.parts.systemconfiguration;

import java.util.List;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.nebula.jface.gridviewer.GridColumnLayout;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.swt.SWT;
import org.testeditor.ui.table.ITestEditorTableColumnCreater;

/**
 * this class creates the columns for the system-configuration-table.
 * 
 * @author llipinski
 * 
 */
public class SystemConfigurationTableColumnCreater implements ITestEditorTableColumnCreater {

	private List<SystemconfigurationColumnDescription> columnDescriptions;
	private IEventBroker eventBroker;

	/**
	 * constructor.
	 * 
	 * @param columnDescriptions
	 *            List<SystemconfigurationColumnDescription>
	 * @param eventBroker
	 *            IEventBroker
	 */
	public SystemConfigurationTableColumnCreater(List<SystemconfigurationColumnDescription> columnDescriptions,
			IEventBroker eventBroker) {
		this.columnDescriptions = columnDescriptions;
		this.eventBroker = eventBroker;

	}

	@Override
	public void createColumns(final GridTableViewer tableViewer, TextCellEditor textCellEditor, GridColumnLayout tcl) {
		for (SystemconfigurationColumnDescription columnDescription : columnDescriptions) {
			createColumn(tableViewer, textCellEditor, tcl, columnDescription);
		}
	}

	/**
	 * Creates a column for the table.
	 * 
	 * @param tableViewer
	 *            to manage the columns
	 * @param textCellEditor
	 *            for editing the properties.
	 * @param tcl
	 *            layout to be used for the column.
	 * @param columnDescription
	 *            the description of the column.
	 */
	private void createColumn(final GridTableViewer tableViewer, final TextCellEditor textCellEditor,
			GridColumnLayout tcl, SystemconfigurationColumnDescription columnDescription) {
		GridViewerColumn viewerColumn = new GridViewerColumn(tableViewer, SWT.NONE);
		viewerColumn.getColumn().setText(columnDescription.getcolumnHeaderText());
		tcl.setColumnData(viewerColumn.getColumn(), new ColumnWeightData(columnDescription.getIntColumnWeightData()));
		viewerColumn.setLabelProvider(new SystemConfigurationColumnLableProvider(columnDescription
				.getColumnDataModelField()));
		viewerColumn.setEditingSupport(new SystemConfigurationEditingSupport(tableViewer, columnDescription
				.getColumnDataModelField(), textCellEditor, eventBroker, columnDescription.isKey()));
	}

}
