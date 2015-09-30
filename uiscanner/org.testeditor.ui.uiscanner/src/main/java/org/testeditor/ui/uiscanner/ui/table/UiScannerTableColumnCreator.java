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
package org.testeditor.ui.uiscanner.ui.table;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.swt.SWT;
import org.testeditor.ui.uiscanner.ui.UiScannerTranslationService;
import org.testeditor.ui.uiscanner.webscanner.UiScannerConstants;
import org.testeditor.ui.uiscanner.webscanner.UiScannerWebElement;

/**
 * ColmnCreator for the UiScanner Nebula GridTable.
 * 
 * @author dkuhlmann
 * 
 */
public class UiScannerTableColumnCreator {

	@Inject
	private UiScannerTranslationService translate;

	/**
	 * Create every column for the UiScanner.
	 * 
	 * @param tableViewer
	 *            GridTableViewer: where the columns should be added.
	 * @param textEditor
	 *            TextCellEditor: texteditor for the cells.
	 * @param style
	 *            the style used to create the column for style bits see
	 *            GridColumn.
	 */
	public void createCoulmns(final GridTableViewer tableViewer, final TextCellEditor textEditor, int style) {

		final GridViewerColumn c = new GridViewerColumn(tableViewer, style | SWT.CHECK);
		c.getColumn().setWidth(20);
		c.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				return "";
			}
		});
		createTypCoulmn(tableViewer, textEditor, style);
		createNameCoulmn(tableViewer, textEditor, style);
		createLocatorCoulmn(tableViewer, textEditor, style);
		createTechnicalIdCoulmn(tableViewer, textEditor, style);
		createValueCoulmn(tableViewer, textEditor, style);
	}

	/**
	 * Create the Typ column for the UiScanner.
	 * 
	 * @param tableViewer
	 *            GridTableViewer: where the columns should be added.
	 * @param textEditor
	 *            TextCellEditor: texteditor for the cells.
	 * @param style
	 *            the style used to create the column for style bits see
	 *            GridColumn.
	 */
	private void createTypCoulmn(final GridTableViewer tableViewer, final TextCellEditor textEditor, int style) {

		GridViewerColumn c = new GridViewerColumn(tableViewer, style);
		c.getColumn().setText(translate.translate("%COLUMN_TABLE_LABEL_TYP"));
		c.getColumn().setWidth(85);
		c.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				return ((UiScannerWebElement) element).getTyp();
			}
		});

		final ComboBoxCellEditor comboCellEditor = new ComboBoxCellEditor();
		comboCellEditor.create(tableViewer.getGrid());
		comboCellEditor.setItems((String[]) UiScannerConstants.ALL_TYPES.toArray());
		c.setEditingSupport(new EditingSupport(tableViewer) {

			@Override
			protected void setValue(Object element, Object value) {
				if (((int) value) != -1) {
					((UiScannerWebElement) element).setTyp(UiScannerConstants.ALL_TYPES.get((int) value));
					tableViewer.update(element, null);
				}
			}

			@Override
			protected Object getValue(Object element) {
				return UiScannerConstants.ALL_TYPES.indexOf(((UiScannerWebElement) element).getTyp());
			}

			@Override
			protected CellEditor getCellEditor(Object element) {
				return comboCellEditor;
			}

			@Override
			protected boolean canEdit(Object element) {
				return true;
			}
		});
	}

	/**
	 * Create the Name column for the UiScanner.
	 * 
	 * @param tableViewer
	 *            GridTableViewer: where the columns should be added.
	 * @param textEditor
	 *            TextCellEditor: texteditor for the cells.
	 * @param style
	 *            the style used to create the column for style bits see
	 *            GridColumn.
	 */
	private void createNameCoulmn(final GridTableViewer tableViewer, final TextCellEditor textEditor, int style) {

		GridViewerColumn c = new GridViewerColumn(tableViewer, style);
		c.getColumn().setText(translate.translate("%COLUMN_TABLE_LABEL_NAME"));
		c.getColumn().setWidth(105);
		c.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((UiScannerWebElement) element).getName();
			}
		});

		c.setEditingSupport(new EditingSupport(tableViewer) {

			@Override
			protected void setValue(Object element, Object value) {
				((UiScannerWebElement) element).setName((String) value);
				tableViewer.update(element, null);
			}

			@Override
			protected Object getValue(Object element) {
				return ((UiScannerWebElement) element).getName();
			}

			@Override
			protected CellEditor getCellEditor(Object element) {
				return textEditor;
			}

			@Override
			protected boolean canEdit(Object element) {
				return true;
			}
		});
	}

	/**
	 * Create the Technical ID column for the UiScanner.
	 * 
	 * @param tableViewer
	 *            GridTableViewer: where the columns should be added.
	 * @param textEditor
	 *            TextCellEditor: texteditor for the cells.
	 * @param style
	 *            the style used to create the column for style bits see
	 *            GridColumn.
	 */
	private void createTechnicalIdCoulmn(final GridTableViewer tableViewer, final TextCellEditor textEditor, int style) {

		GridViewerColumn c = new GridViewerColumn(tableViewer, style);
		c.getColumn().setText(translate.translate("%COLUMN_TABLE_LABEL_TECHNICAL_ID"));
		c.getColumn().setWidth(200);
		c.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((UiScannerWebElement) element).getTechnicalID();
			}
		});

		c.setEditingSupport(new EditingSupport(tableViewer) {

			@Override
			protected void setValue(Object element, Object value) {
				((UiScannerWebElement) element).setTechnicalID((String) value);
				tableViewer.update(element, null);
			}

			@Override
			protected Object getValue(Object element) {
				return ((UiScannerWebElement) element).getTechnicalID();
			}

			@Override
			protected CellEditor getCellEditor(Object element) {
				return textEditor;
			}

			@Override
			protected boolean canEdit(Object element) {
				return true;
			}
		});

	}

	/**
	 * Create the Locator column for the UiScanner.
	 * 
	 * @param tableViewer
	 *            GridTableViewer: where the columns should be added.
	 * @param textEditor
	 *            TextCellEditor: texteditor for the cells.
	 * @param style
	 *            the style used to create the column for style bits see
	 *            GridColumn.
	 */
	private void createLocatorCoulmn(final GridTableViewer tableViewer, final TextCellEditor textEditor, int style) {

		GridViewerColumn c = new GridViewerColumn(tableViewer, style);
		c.getColumn().setText(translate.translate("%COLUMN_TABLE_LABEL_LOCATOR"));
		c.getColumn().setWidth(200);
		c.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((UiScannerWebElement) element).getLocator();
			}
		});

		c.setEditingSupport(new EditingSupport(tableViewer) {

			@Override
			protected void setValue(Object element, Object value) {
				((UiScannerWebElement) element).setLocator((String) value);
				tableViewer.update(element, null);
			}

			@Override
			protected Object getValue(Object element) {
				return ((UiScannerWebElement) element).getLocator();
			}

			@Override
			protected CellEditor getCellEditor(Object element) {
				return textEditor;
			}

			@Override
			protected boolean canEdit(Object element) {
				return true;
			}
		});
	}

	/**
	 * Create the Value column for the UiScanner.
	 * 
	 * @param tableViewer
	 *            GridTableViewer: where the columns should be added.
	 * @param textEditor
	 *            TextCellEditor: texteditor for the cells.
	 * @param style
	 *            the style used to create the column for style bits see
	 *            GridColumn.
	 */
	private void createValueCoulmn(final GridTableViewer tableViewer, final TextCellEditor textEditor, int style) {

		GridViewerColumn c = new GridViewerColumn(tableViewer, style);
		c.getColumn().setText(translate.translate("%COLUMN_TABLE_LABEL_VALUE"));
		c.getColumn().setWidth(300);
		c.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (((UiScannerWebElement) element).getValue() != null) {
					return ((UiScannerWebElement) element).getValue().toString()
							.substring(1, ((UiScannerWebElement) element).getValue().toString().length() - 1);
				}
				return "";
			}
		});

		c.setEditingSupport(new EditingSupport(tableViewer) {

			@Override
			protected void setValue(Object element, Object value) {
				if (!((String) value).isEmpty()) {
					List<String> valueList = new ArrayList<>();
					for (String str : ((String) value).split(",")) {
						valueList.add(str.trim());
					}
					((UiScannerWebElement) element).setValue(valueList);
					tableViewer.update(element, null);
				}
			}

			@Override
			protected Object getValue(Object element) {
				if (((UiScannerWebElement) element).getValue() != null) {
					return ((UiScannerWebElement) element).getValue().toString()
							.substring(1, ((UiScannerWebElement) element).getValue().toString().length() - 1);
				}
				return "";
			}

			@Override
			protected CellEditor getCellEditor(Object element) {
				return textEditor;
			}

			@Override
			protected boolean canEdit(Object element) {
				return true;
			}
		});
	}
}
