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

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.grid.GridCellRenderer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.importer.ExcelFileImportException;
import org.testeditor.core.importer.FileImporter;
import org.testeditor.core.importer.FileImporterFactory;
import org.testeditor.core.model.teststructure.TestData;
import org.testeditor.core.model.teststructure.TestDataEvaluationReturnList;
import org.testeditor.core.model.teststructure.TestFlow;
import org.testeditor.core.model.teststructure.TestScenarioParameterTable;
import org.testeditor.ui.constants.TestEditorEventConstants;
import org.testeditor.ui.constants.TestEditorUIEventConstants;
import org.testeditor.ui.table.TestEditorTableViewerFactory;
import org.testeditor.ui.utilities.TestEditorTranslationService;

/**
 * 
 * special ui-component uses the NatTable.
 * 
 */
public class TestEditorViewTableViewer {

	private TestEditorTranslationService translationService;

	private TestScenarioParameterTable paramTable;

	private IEventBroker eventBroker;

	private TestFlow testFlow;

	private static final Logger LOGGER = Logger.getLogger(TestEditorViewTableViewer.class);

	private Composite parent;

	private GridTableViewer createdTableViewer;

	private MPart mPart;

	/**
	 * constructor for test purpose.
	 * 
	 */
	public TestEditorViewTableViewer() {
	}

	/**
	 * constructor.
	 * 
	 * @param <IEventBrkoer>
	 * 
	 * @param parent
	 *            Composite
	 * @param paramTable
	 *            {@link TestScenarioParameterTable}
	 * @param translationService
	 *            the TranslationService
	 * @param mPart
	 *            MPart
	 * @param testFlow
	 *            TestFlow
	 * @param eventBroker
	 *            IEventBroker
	 */
	public TestEditorViewTableViewer(Composite parent, TestScenarioParameterTable paramTable,
			TestEditorTranslationService translationService, TestFlow testFlow, MPart mPart, IEventBroker eventBroker) {
		this.mPart = mPart;
		this.eventBroker = eventBroker;
		Composite tableParent = new ScrolledComposite(parent, SWT.NORMAL);
		tableParent.setLayoutData(new GridData(GridData.FILL_BOTH));
		this.parent = tableParent;
		this.paramTable = paramTable;
		this.translationService = translationService;
		this.eventBroker = eventBroker;
		this.testFlow = testFlow;
		TestEditorViewTableColumnCreater columnCreater = new TestEditorViewTableColumnCreater(
				paramTable.getDataTable(), mPart, eventBroker);
		createdTableViewer = TestEditorTableViewerFactory.createTableViewer(tableParent, paramTable.getDataTable()
				.getDataRows(), columnCreater);
		createdTableViewer.getGrid().setEmptyCellRenderer(getEmptyCellHidingRenderer());
	}

	/**
	 * 
	 * @return the associated TestScenarioParameterTable of this object.
	 */
	public TestScenarioParameterTable getTestComp() {
		return paramTable;
	}

	/**
	 * adds a line in the table given by the parameter.
	 * 
	 */
	public void addLineInTable() {
		int[] selectionIndices = getSelectionIndices();
		int selectedLine = 1;
		if (selectionIndices.length > 0) {
			selectedLine = selectionIndices[0] + 2;
			if (selectedLine < 1) {
				selectedLine = 1;
			}
		}
		paramTable.getDataTable().addEmptyRow(selectedLine);
		refreshTable();
		selectAllItems(false);
		createdTableViewer.editElement(paramTable.getDataTable().getRows().get(selectedLine), 0);
		setDirty();
	}

	/**
	 * removes the selected line from the table.
	 * 
	 */
	public void removeLineFromTable() {
		int[] selectionIndices = getSelectionIndices();
		int removedLines = 0;
		for (int selectedIndex : selectionIndices) {
			paramTable.getDataTable().removeRow(selectedIndex + 1 - removedLines);
			removedLines++;
		}
		refreshTable();
		if (paramTable.getDataTable().getRowCounts() == 1) {
			addLineInTable();
		}

		setDirty();
	}

	/**
	 * refreshes the table-contents.
	 */
	protected void refreshTable() {
		createdTableViewer.setInput(paramTable.getDataTable().getDataRows());
		eventBroker.send(TestEditorEventConstants.REFRESH_TEST_FLOW_VIEW, paramTable.getDataTable());
	}

	/**
	 * 
	 * @return true, if at least one row is selected, else false.
	 */
	public boolean isTableRowSelected() {
		return createdTableViewer.getGrid().getSelection().length > 0;
	}

	/**
	 * 
	 * @return the selected rows of the table.
	 */
	protected int[] getSelectionIndices() {
		return createdTableViewer.getGrid().getSelectionIndices();
	}

	/**
	 * send a setDirty signal.
	 */
	private void setDirty() {
		eventBroker.post(TestEditorUIEventConstants.TEST_FLOW_STATE_CHANGED_TO_DIRTY, testFlow);
	}

	/**
	 * send GetFocus-signal to TestEditView.
	 */
	public void sendGetFocusSignal() {
		TestEditorParameterTableFocusEventObject testEditorParameterTableFocusEventObject = new TestEditorParameterTableFocusEventObject(
				testFlow, this);
		eventBroker.post(TestEditorEventConstants.TEST_GET_FOCUS_IN_TABLE, testEditorParameterTableFocusEventObject);
	}

	/**
	 * adds the testData to the testcomponent.
	 * 
	 * @param testData
	 *            the testData
	 */
	protected void addTestDataToTable(TestData testData) {
		if (paramTable.getDataTable().getRowCounts() == 2 && paramTable.getDataTable().getRows().get(1).isRowEmpty()) {
			paramTable.getDataTable().removeEmptyRows();
		}
		paramTable.addTestData(testData);
		refreshTable();
		setDirty();
	}

	/**
	 * select or deselect all items in the table.
	 * 
	 * @param selectAll
	 *            true, if allItems should be selected, else false
	 */
	public void selectAllItems(boolean selectAll) {
		if (selectAll) {
			createdTableViewer.getGrid().selectAll();
		} else {
			createdTableViewer.getGrid().deselectAll();
		}
	}

	/**
	 * Returns the selected File object with testdata content.
	 * 
	 * 
	 * @return null if no file selected.
	 */
	private File getImportFile() {

		// File standard dialog
		FileDialog fileDialog = new FileDialog(getShell());
		// Set the text

		fileDialog.setText(translate("%testprojecteditor.importSelectFile"));
		// Set filter on .txt files
		fileDialog.setFilterExtensions(new String[] { "*.csv", "*.xls" });
		// Put in a readable name for the filter
		fileDialog.setFilterNames(new String[] { translate("%testprojecteditor.importCSV"), "Excel" });
		// Open Dialog and save result of selection
		String selected = fileDialog.open();

		if (selected != null) {
			return new File(selected);
		}

		return null;

	}

	/**
	 * Handles the file import.
	 * 
	 */
	public void handleFileImport() {
		File importFile = getImportFile();
		handleFileImport(importFile);
	}

	/**
	 * Handles the file import.
	 * 
	 * @param importFile
	 *            to be imported and the values are appended to the table.
	 * 
	 */
	public void handleFileImport(File importFile) {
		if (importFile != null) {
			FileImporter importer;
			try {
				importer = FileImporterFactory.getInstance(importFile);
				TestData testData = importer.getTestData(importFile);
				TestDataEvaluationReturnList dataEvaluationReturnList = testData.validateTableAgainstTable(paramTable);
				if (dataEvaluationReturnList.getColumnHeadersOnlyInTargetTable().isEmpty()) {
					testData = testData.sortTestData(paramTable);
					testData.removeRow(0);
					addTestDataToTable(testData);
					if (!dataEvaluationReturnList.getColumnHeadersOnlyInSourceTable().isEmpty()) {
						String columnsOnlyInSource = dataEvaluationReturnList.getColumnHeadersOnlyInSourceTable()
								.toString();
						MessageDialog
								.openError(Display.getCurrent().getActiveShell(), translate("%importInfoValidTitle"),
										translate("%importInfoValid ", columnsOnlyInSource));
					}
				} else {
					String columnsOnlyInSource = dataEvaluationReturnList.getColumnHeadersOnlyInSourceTable()
							.toString();
					String columnsOnlyInTarget = dataEvaluationReturnList.getColumnHeadersOnlyInTargetTable()
							.toString();
					MessageDialog.openError(Display.getCurrent().getActiveShell(),
							translate("%importErrorNotValidTitle"),
							translate("%importErrorNotValid", columnsOnlyInTarget, columnsOnlyInSource));
					LOGGER.info("handleFileImport: File not valid");
				}
			} catch (ExcelFileImportException e) {
				LOGGER.info("handleFileImport OldExcel" + e.getMessage());
				MessageDialog.openError(Display.getCurrent().getActiveShell(), translate("%importErrorNotValidTitle"),
						translate("%importErrorNotValidToOldFile"));
			} catch (SystemException e1) {
				LOGGER.error("handleFileImport" + e1.getMessage());
			}
		}
	}

	/**
	 * returns the translation for a given string form the language-resource.
	 * 
	 * @param translateKey
	 *            the key for the translation
	 * @param params
	 *            params in translated text given as placeholder example: this
	 * @return the translation
	 */
	protected String translate(String translateKey, Object... params) {
		return translationService.translate(translateKey, params);
	}

	/**
	 * 
	 * @return the selected rows of the table as a stringbuilder.
	 */
	protected StringBuilder getSelectedRowsAsStringBuilder() {
		StringBuilder sb = new StringBuilder();
		int[] fullySelectedRowPositions = getSelectionIndices();
		for (int selected : fullySelectedRowPositions) {
			sb.append(paramTable.getDataTable().getDataRows().get(selected).toString()).append("\n");
		}
		return sb;
	}

	/**
	 * getter.
	 * 
	 * @return project name
	 */
	protected String getProjectName() {
		return testFlow.getRootElement().getName();
	}

	/**
	 * Pastes the selected row content into table.
	 * 
	 * @param selectedRows
	 *            numbers of selected rows
	 * @param datas
	 *            input content
	 */
	protected void pasteSelectedRowsIntoTable(int[] selectedRows, List<String> datas) {
		paramTable.getDataTable().pasteRows(datas, selectedRows);
		refreshTable();
		setDirty();
	}

	/**
	 * getter.
	 * 
	 * @return TestEditorTranslationService
	 */
	protected TestEditorTranslationService getTranslationService() {
		return translationService;
	}

	/**
	 * getter.
	 * 
	 * @return shell
	 */
	protected Shell getShell() {
		return getParent().getShell();
	}

	/**
	 * getter.
	 * 
	 * @return testFlow
	 */
	protected TestFlow getTestFlow() {
		return testFlow;
	}

	/**
	 * 
	 * @return the Table Widget to allow subclasses to add event listeners.
	 */
	public Grid getTable() {
		return createdTableViewer.getGrid();
	}

	/**
	 * 
	 * @param paramTable
	 *            to be used as datamodel in this TableViewer
	 */
	public void setParamTable(TestScenarioParameterTable paramTable) {
		this.paramTable = paramTable;
	}

	/**
	 * 
	 * @return the IEventBroker
	 */
	protected IEventBroker getEventBroker() {
		return eventBroker;
	}

	/**
	 * 
	 * @return the paramTable.
	 */
	protected TestScenarioParameterTable getParamTable() {
		return paramTable;
	}

	/**
	 * 
	 * @return the parent of the nattable.
	 */
	protected Composite getParent() {
		return parent;
	}

	/**
	 * 
	 * @return the MPart
	 */
	protected MPart getPart() {
		return mPart;
	}

	/**
	 * 
	 * @return GridCellRenderer to hide empty cells.
	 */
	protected GridCellRenderer getEmptyCellHidingRenderer() {
		return new GridCellRenderer() {

			@Override
			public void paint(GC gc, Object value) {

			}

			@Override
			public Point computeSize(GC gc, int wHint, int hHint, Object value) {
				return null;
			}

			@Override
			public boolean notify(int event, Point point, Object value) {
				return false;
			}
		};
	}

	/**
	 * 
	 * @return the GridTableViewer
	 */
	protected GridTableViewer getGridTableViewer() {
		return createdTableViewer;
	}
}
