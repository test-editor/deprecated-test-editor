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

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import org.testeditor.core.model.teststructure.TestData;
import org.testeditor.core.model.teststructure.TestScenarioParameterTable;

/**
 * this class is a mapper between the tablwViewers and the TestComponent.
 * 
 * @author llipinski
 * 
 */
public class TableToTestComponentMapper {

	private HashMap<TestEditorViewTableViewer, TestScenarioParameterTable> tableViewerToTestScenarioTable = new HashMap<TestEditorViewTableViewer, TestScenarioParameterTable>();

	private HashMap<TestData, TestEditorViewTableViewer> testDataToTableViewer = new HashMap<TestData, TestEditorViewTableViewer>();

	/**
	 * Returns the selected testdata table.
	 * 
	 * @return null if no table selected otherwise selected table
	 */
	protected TestEditorViewTableViewer getSelectedTestDataTable() {

		Set<Entry<TestEditorViewTableViewer, TestScenarioParameterTable>> entrySet = tableViewerToTestScenarioTable
				.entrySet();

		for (Entry<TestEditorViewTableViewer, TestScenarioParameterTable> entry : entrySet) {

			TestEditorViewTableViewer tableViewer = entry.getKey();

			if (tableViewer.getSelectionIndices().length > 0) {
				return tableViewer;
			}
		}
		return null;
	}

	/**
	 * adds a table to the controlArray to show it in the styledText.
	 * 
	 * @param tableViewer
	 *            TestEditorViewTableViewer
	 * @param testComp
	 *            the testComponent
	 */
	protected void addTableToTableStore(TestEditorViewTableViewer tableViewer, TestScenarioParameterTable testComp) {
		tableViewerToTestScenarioTable.put(tableViewer, testComp);
		testDataToTableViewer.put(testComp.getDataTable(), tableViewer);
	}

	/**
	 * 
	 * @param tableViewer
	 *            the TestEditorViewTableViewer
	 * @return the TestComponent to the TableViewer.
	 */
	protected TestScenarioParameterTable getTestScenarioTableToTableViewer(TestEditorViewTableViewer tableViewer) {
		return tableViewerToTestScenarioTable.get(tableViewer);
	}

	/**
	 * 
	 * @param testData
	 *            the TestData
	 * @return the TableViewer to the TestComponent.
	 */
	protected TestEditorViewTableViewer getTestEditorViewTableViewerToTestData(TestData testData) {
		return testDataToTableViewer.get(testData);
	}

	/**
	 * resets the hashmap of the mapper.
	 */
	protected void clear() {
		tableViewerToTestScenarioTable.clear();
		testDataToTableViewer.clear();
	}
}
