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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Composite;
import org.testeditor.core.model.teststructure.TestFlow;
import org.testeditor.core.model.teststructure.TestScenario;
import org.testeditor.core.model.teststructure.TestScenarioParameterTable;
import org.testeditor.ui.utilities.TestEditorTranslationService;

/**
 * 
 * special TestEditorViewTableViewer with clipboard-functioniality.
 * 
 */
public class TestEditorViewTableViewerClipboard extends TestEditorViewTableViewer {

	private Clipboard clipboard;
	private TestEditorScenarioParameterTableMouseAdapter tableMouseAdapter;
	private StyleRange style;

	/**
	 * constructor.
	 * 
	 * @param parent
	 *            Composite
	 * @param paramTable
	 *            {@link TestScenarioParameterTable}
	 * @param translationService
	 *            the TranslationService
	 * @param testFlow
	 *            TestFlow
	 * @param mPart
	 *            MPart
	 * @param eventBroker
	 *            IEventBroker
	 */
	public TestEditorViewTableViewerClipboard(Composite parent, TestScenarioParameterTable paramTable,
			TestEditorTranslationService translationService, TestFlow testFlow, MPart mPart, IEventBroker eventBroker) {
		super(parent, paramTable, translationService, testFlow, mPart, eventBroker);
		clipboard = new Clipboard(parent.getDisplay());
	}

	/**
	 * copies the checked lines into the clipboard.
	 */
	public void copySelectedLinesFromTable() {
		cutOrCopyTestDataRow();
	}

	/**
	 * cuts the checked lines from the table into the clipboard.
	 */
	public void cutSelectedLinesFromTable() {
		cutOrCopyTestDataRow();
		removeLineFromTable();
	}

	/**
	 * sets the StyleRange used to show the table in the StyledText.
	 * 
	 * @param style
	 *            StyleRange
	 */
	public void setStyleRange(StyleRange style) {
		this.style = style;
		addListener();
	}

	/**
	 * cut or copy the selected testDataRow and store the text in the clipboard.
	 * 
	 */
	public void cutOrCopyTestDataRow() {
		StringBuilder sb = getSelectedRowsAsStringBuilder();

		TestEditorTestRowTransferContainer dataContainer = createTestDataRowTransferContainer(sb);

		TextTransfer textTransfer = TextTransfer.getInstance();
		TestEditorTestDataRowTransfer testDataTransfer = TestEditorTestDataRowTransfer.getInstance();

		clipboard.setContents(new Object[] { sb.toString(), dataContainer }, new Transfer[] { textTransfer,
				testDataTransfer });
	}

	/**
	 * 
	 * @param sb
	 *            data of the selected rows
	 * @return the TestEditorTestRowTransferContainer out of the data in the
	 *         parameter sb and with the name of the project.
	 */
	private TestEditorTestRowTransferContainer createTestDataRowTransferContainer(StringBuilder sb) {
		TestEditorTestRowTransferContainer dataContainer = new TestEditorTestRowTransferContainer();
		dataContainer.setStoredTestComponents(sb.toString());
		dataContainer.setTestProjectName(getProjectName());
		return dataContainer;
	}

	/**
	 * paste the rows from the clipboard into the table.
	 */
	public void getSelectedRowsFromClipboardAndPasteIntoTable() {
		int[] selectedRows = getSelectionIndices();
		selectedRows = shiftSelectedRows(selectedRows);
		TestEditorTestDataRowTransfer testDataRowTransfer = TestEditorTestDataRowTransfer.getInstance();
		Object dataTransferObject = clipboard.getContents(testDataRowTransfer);
		List<String> datas = new ArrayList<String>();
		for (String str : ((TestEditorTestRowTransferContainer) dataTransferObject).getStoredTestComponents().split(
				"\n")) {
			datas.add(str);
		}
		pasteSelectedRowsIntoTable(selectedRows, datas);
	}

	/**
	 * shifts the selected rows. Add 1 to the numbers. The Table returns the
	 * number of the rows without counting the header. But the header is
	 * included in the model.
	 * 
	 * @param selectedRows
	 *            int[]
	 * @return int[]
	 */
	private int[] shiftSelectedRows(int[] selectedRows) {

		int[] selectedRowsShifted = new int[selectedRows.length];
		int i = 0;
		for (int row : selectedRows) {
			selectedRowsShifted[i] = row + 1;
			i++;
		}
		return selectedRowsShifted;
	}

	/**
	 * 
	 * @return true, if the transfer is instanceof
	 *         TestEditorTestRowTransferContainer and has the same project.
	 */
	public boolean canExecutePasteTestDataRow() {
		TestEditorTestDataTransferContainer transfer = getTransfer();
		if (transfer != null && transfer instanceof TestEditorTestRowTransferContainer) {
			return transferContainerProjectEquals(transfer);
		}
		return false;
	}

	/**
	 * 
	 * @return the TestEditorTestDataTransferContainer from the testEditViewArea
	 */
	private TestEditorTestDataTransferContainer getTransfer() {
		TestEditorTestFlowTransfer testFlowTransfer = TestEditorTestFlowTransfer.getInstance();
		Object dataTransferObject = clipboard.getContents(testFlowTransfer);
		return (TestEditorTestDataTransferContainer) dataTransferObject;
	}

	/**
	 * 
	 * @param transfer
	 *            TestEditorTestDataTransferContainer
	 * @return true, if the project is equal, else false
	 */
	private boolean transferContainerProjectEquals(TestEditorTestDataTransferContainer transfer) {
		return !transfer.isEmpty() && transfer.getTestProjectName().equalsIgnoreCase(getProjectName());
	}

	/**
	 * adds a selectionListener to the table.
	 */
	private void addListener() {
		if (!(getTestFlow() instanceof TestScenario)) {
			tableMouseAdapter = new TestEditorScenarioParameterTableMouseAdapter(getShell(), getTranslationService(),
					this, style, getEventBroker());
			getTable().addMouseListener(tableMouseAdapter);
		}
	}
}
