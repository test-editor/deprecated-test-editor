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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.model.teststructure.TestCase;
import org.testeditor.core.model.teststructure.TestData;
import org.testeditor.core.model.teststructure.TestDataRow;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestScenarioParameterTable;
import org.testeditor.core.model.teststructure.TestSuite;
import org.testeditor.core.services.interfaces.ServiceLookUpForTest;
import org.testeditor.core.util.FileLocatorService;
import org.testeditor.ui.adapter.MPartAdapter;
import org.testeditor.ui.constants.TestEditorEventConstants;
import org.testeditor.ui.constants.TestEditorUIEventConstants;
import org.testeditor.ui.mocks.EventBrokerMock;
import org.testeditor.ui.utilities.TestEditorTranslationService;

/**
 * 
 * Integration Tests for TestEditorViewTableViewer.
 * 
 */
public class TestEditorViewTableViewerTest {

	private Shell shell;
	private EventBrokerMock eventBroker;
	private Set<String> monitor;
	private TestCase testFlow;

	/**
	 * 
	 * Test the creation and initialization of the Viewer.
	 * 
	 * @throws SystemException
	 *             SystemException
	 */
	@Test
	public void testObjectCreation() throws SystemException {
		TestScenarioParameterTable paramTable = new TestScenarioParameterTable();
		paramTable.addParameterLine("|Name|FirstName|City");
		TestEditorTranslationService translationService = null;
		IEventBroker eventBroker = new EventBrokerMock();
		TestEditorViewTableViewer testEditorViewTableViewer = new TestEditorViewTableViewer(shell, paramTable,
				translationService, testFlow, new MPartAdapter(), eventBroker);
		assertNotNull("Expecting a table.", testEditorViewTableViewer.getTable());
	}

	/**
	 * 
	 * Tests the filling and modifying of a NatTable.
	 * 
	 * @throws SystemException
	 *             SystemException
	 */
	@Test
	public void testModifyObjectData() throws SystemException {
		TestScenarioParameterTable paramTable = new TestScenarioParameterTable();
		TestEditorTranslationService translationService = null;
		paramTable.addParameterLine("|Name|FirstName|City");
		TestEditorViewTableViewer tableViewer = new TestEditorViewTableViewer(shell, paramTable, translationService,
				testFlow, new MPartAdapter(), eventBroker);
		tableViewer.setParamTable(paramTable);

		TestData testData = new TestData();
		TestDataRow testDataRow = new TestDataRow("Hemmingway|ernest|lego the movie");
		testData.addRow(testDataRow);
		tableViewer.addTestDataToTable(testData);
		paramTable.addTestData(testData);

		assertEquals("Hemmingway", tableViewer.getTestComp().getDataTable().getDataRows().get(0).getColumn(0));
		assertTrue("Expecting dirty editor.",
				monitor.contains(TestEditorUIEventConstants.TEST_FLOW_STATE_CHANGED_TO_DIRTY));
	}

	/**
	 * Test that a line is added to the table and modell.
	 * 
	 * @throws SystemException
	 *             SystemException
	 */
	@Test
	public void testAddLine() throws SystemException {
		TestScenarioParameterTable paramTable = new TestScenarioParameterTable();
		TestEditorTranslationService translationService = null;
		paramTable.addParameterLine("|Name|FirstName|City");
		TestEditorViewTableViewer tableViewer = new TestEditorViewTableViewer(shell, paramTable, translationService,
				testFlow, new MPartAdapter(), eventBroker);
		assertEquals("Expecting only headline", 1, paramTable.getDataTable().getRowCounts());
		tableViewer.addLineInTable();
		assertTrue("Expecting dirty editor.",
				monitor.contains(TestEditorUIEventConstants.TEST_FLOW_STATE_CHANGED_TO_DIRTY));
		assertEquals("Expecting new line", 2, paramTable.getDataTable().getRowCounts());
	}

	/**
	 * Test that a line is removed to the table and modell.
	 * 
	 * @throws SystemException
	 *             SystemException
	 */
	@Test
	public void testRemoveLine() throws SystemException {
		TestScenarioParameterTable paramTable = new TestScenarioParameterTable();
		TestEditorTranslationService translationService = null;
		paramTable.addParameterLine("|Name|FirstName|City");
		TestEditorViewTableViewer tableViewer = new TestEditorViewTableViewer(shell, paramTable, translationService,
				testFlow, new MPartAdapter(), eventBroker);
		assertEquals("Expecting headline and data", 1, paramTable.getDataTable().getRowCounts());
		tableViewer.addLineInTable();
		assertEquals("Expecting new line", 2, paramTable.getDataTable().getRowCounts());
		tableViewer.addLineInTable();
		assertEquals("Expecting new line", 3, paramTable.getDataTable().getRowCounts());
		tableViewer.getGridTableViewer().getGrid().setSelection(0);
		tableViewer.removeLineFromTable();
		assertEquals("Expecting new line", 2, paramTable.getDataTable().getRowCounts());
		assertTrue("Expecting dirty editor.",
				monitor.contains(TestEditorUIEventConstants.TEST_FLOW_STATE_CHANGED_TO_DIRTY));
	}

	/**
	 * try to remove the last line of the table and modell.
	 * 
	 * @throws SystemException
	 *             SystemException
	 */
	@Test
	public void testRemoveLastLine() throws SystemException {
		TestScenarioParameterTable paramTable = new TestScenarioParameterTable();
		TestEditorTranslationService translationService = null;
		paramTable.addParameterLine("|Name|FirstName|City");
		TestEditorViewTableViewer tableViewer = new TestEditorViewTableViewer(shell, paramTable, translationService,
				testFlow, new MPartAdapter(), eventBroker);
		assertEquals("Expecting headline and data", 1, paramTable.getDataTable().getRowCounts());
		tableViewer.addLineInTable();
		assertEquals("Expecting new line", 2, paramTable.getDataTable().getRowCounts());
		tableViewer.getGridTableViewer().getGrid().setSelection(0);
		tableViewer.removeLineFromTable();
		assertEquals("Expecting new line", 2, paramTable.getDataTable().getRowCounts());
		assertTrue("Expecting dirty editor.",
				monitor.contains(TestEditorUIEventConstants.TEST_FLOW_STATE_CHANGED_TO_DIRTY));
	}

	/**
	 * Test that an get focus event is send to the bus.
	 * 
	 * @throws SystemException
	 *             SystemException
	 */
	@Test
	public void testSendGetFocusSignal() throws SystemException {
		TestScenarioParameterTable paramTable = new TestScenarioParameterTable();
		TestEditorTranslationService translationService = null;
		paramTable.addParameterLine("|Name|FirstName|City");
		TestEditorViewTableViewer tableViewer = new TestEditorViewTableViewer(shell, paramTable, translationService,
				testFlow, new MPartAdapter(), eventBroker);
		tableViewer.sendGetFocusSignal();
		assertFalse("Expecting clean editor.",
				monitor.contains(TestEditorUIEventConstants.TEST_FLOW_STATE_CHANGED_TO_DIRTY));
		assertTrue("Expecting Focus Event.", monitor.contains(TestEditorEventConstants.TEST_GET_FOCUS_IN_TABLE));
	}

	/**
	 * Test that the OUT is stable and works with null values.
	 * 
	 * @throws SystemException
	 *             SystemException
	 */
	@Test
	public void testHandleFileImportWithNullFile() throws SystemException {
		TestScenarioParameterTable paramTable = new TestScenarioParameterTable();
		TestEditorTranslationService translationService = null;
		paramTable.addParameterLine("|Name|FirstName|City");
		TestEditorViewTableViewer tableViewer = new TestEditorViewTableViewer(shell, paramTable, translationService,
				testFlow, new MPartAdapter(), eventBroker);
		try {
			tableViewer.handleFileImport(null);
		} catch (Exception e) {
			fail("Expecting no exception on null file");
		}
	}

	/**
	 * Test the import of a dummy File.
	 * 
	 * @throws Exception
	 *             on file access.
	 */
	@Test
	@Ignore
	public void testHandleFileImportWithFile() throws Exception {
		TestScenarioParameterTable paramTable = new TestScenarioParameterTable();
		TestEditorTranslationService translationService = new TestEditorTranslationService() {
			@Override
			public String translate(String key, Object... params) {
				return "";
			}
		};
		paramTable.addParameterLine("|TextVorhanden|Name|TextNichtVorhanden|Land|Passwort");
		TestEditorViewTableViewer tableViewer = new TestEditorViewTableViewer(shell, paramTable, translationService,
				testFlow, new MPartAdapter(), eventBroker);
		String demoBundlePath = ServiceLookUpForTest.getService(FileLocatorService.class)
				.findBundleFileLocationAsString("org.testeditor.demo");
		File importFile = new File(demoBundlePath + File.separator + "demoProjects" + File.separator + "DemoWebTests"
				+ File.separator + "ExampleImportFiles" + File.separator + "CSVImport_Valid.csv");
		assertTrue("Demo File doesn't exists", importFile.exists());
		tableViewer.handleFileImport(importFile);
		assertEquals("Moxen", tableViewer.getTestComp().getDataTable().getDataRows().get(3).getList().get(1));
	}

	/**
	 * Tests that the Test Object access of the Object.
	 * 
	 * @throws SystemException
	 *             SystemException
	 */
	@Test
	public void testGetProjectName() throws SystemException {
		TestProject testProject = new TestProject();
		testProject.setName("MyTestProject");
		TestSuite ts = new TestSuite();
		ts.setName("MyTestSuite");
		ts.addChild(testFlow);
		testProject.addChild(ts);
		TestScenarioParameterTable paramTable = new TestScenarioParameterTable();
		TestEditorTranslationService translationService = null;
		paramTable.addParameterLine("|Name|FirstName|City");
		TestEditorViewTableViewer tableViewer = new TestEditorViewTableViewer(shell, paramTable, translationService,
				testFlow, new MPartAdapter(), eventBroker);
		assertEquals(testFlow, tableViewer.getTestFlow());
		assertEquals(paramTable, tableViewer.getTestComp());
		assertEquals("MyTestProject", tableViewer.getProjectName());
	}

	/**
	 * Tests the selection in the table.
	 * 
	 * @throws SystemException
	 *             SystemException
	 */
	@Test
	public void testSelectAll() throws SystemException {
		TestScenarioParameterTable paramTable = new TestScenarioParameterTable();
		TestEditorTranslationService translationService = null;
		paramTable.addParameterLine("|Name|FirstName|City");
		TestEditorViewTableViewer tableViewer = new TestEditorViewTableViewer(shell, paramTable, translationService,
				testFlow, new MPartAdapter(), eventBroker);
		tableViewer.selectAllItems(true);
		assertEquals(0, tableViewer.getSelectionIndices().length);

		TestData testData = new TestData();
		TestDataRow testDataRow = new TestDataRow("HamWay|ernest|lego the movie");
		testData.addRow(testDataRow);
		paramTable.addTestData(testData);
		tableViewer.refreshTable();

		tableViewer.selectAllItems(true);
		assertTrue(tableViewer.getSelectionIndices().length == 1);
		tableViewer.selectAllItems(false);
		assertEquals(0, tableViewer.getSelectionIndices().length);
	}

	/**
	 * Tests the Paste in the table.
	 * 
	 * @throws SystemException
	 *             SystemException
	 */
	@Test
	public void testPasteSelectedRows() throws SystemException {
		TestScenarioParameterTable paramTable = new TestScenarioParameterTable();
		TestEditorTranslationService translationService = null;
		paramTable.addParameterLine("|Name|FirstName|City");
		TestEditorViewTableViewer tableViewer = new TestEditorViewTableViewer(shell, paramTable, translationService,
				testFlow, new MPartAdapter(), eventBroker);
		TestData testData = new TestData();
		TestDataRow testDataRow = new TestDataRow("HamWay|ernest|lego the movie");
		testData.addRow(testDataRow);
		paramTable.addTestData(testData);
		List<String> data = new ArrayList<String>();
		data.add("foo");
		data.add("bar");
		tableViewer.pasteSelectedRowsIntoTable(new int[] { 1, 2 }, data);
		assertEquals("foo", paramTable.getDataTable().getDataRows().get(0).getColumn(0));
		assertEquals("bar", paramTable.getDataTable().getDataRows().get(1).getColumn(0));
	}

	/**
	 * 
	 * Tests the Extraction of the Header from the PramTable Object.
	 * 
	 * @throws SystemException
	 *             SystemException
	 * 
	 */
	@Test
	public void testTableHeaderExtraktion() throws SystemException {
		TestEditorTranslationService translationService = null;
		TestScenarioParameterTable paramTable = new TestScenarioParameterTable();
		paramTable.addParameterLine("|Name|FirstName|City|");
		TestEditorViewTableViewer tableViewer = new TestEditorViewTableViewer(shell, paramTable, translationService,
				testFlow, new MPartAdapter(), eventBroker);

		tableViewer.setParamTable(paramTable);
		tableViewer.refreshTable();
		assertEquals(3, tableViewer.getTable().getColumns().length);
		assertEquals("Name", tableViewer.getTable().getColumns()[0].getText());
		assertEquals("FirstName", tableViewer.getTable().getColumns()[1].getText());
		assertEquals("City", tableViewer.getTable().getColumns()[2].getText());
	}

	/**
	 * Tests the filling of the body layer.
	 * 
	 * @throws SystemException
	 *             SystemException
	 */
	@Test
	public void testTableDataExtraktion() throws SystemException {
		TestEditorTranslationService translationService = null;
		TestScenarioParameterTable paramTable = new TestScenarioParameterTable();
		paramTable.addParameterLine("|Name|FirstName|City|");
		TestData testData = new TestData();
		TestDataRow testDataRow = new TestDataRow("Foo|Bar|legoland|");
		testData.addRow(testDataRow);
		testDataRow = new TestDataRow("HamWay|ernest|lego the movie|");
		testData.addRow(testDataRow);
		paramTable.addTestData(testData);
		TestEditorViewTableViewer tableViewer = new TestEditorViewTableViewer(shell, paramTable, translationService,
				testFlow, new MPartAdapter(), eventBroker);
		tableViewer.refreshTable();

		assertEquals("Foo", tableViewer.getTable().getItems()[0].getText());
		assertEquals("Bar", tableViewer.getTable().getItems()[0].getText(1));
		assertEquals("legoland", tableViewer.getTable().getItems()[0].getText(2));

		assertEquals("HamWay", tableViewer.getTable().getItems()[1].getText(0));
		assertEquals("ernest", tableViewer.getTable().getItems()[1].getText(1));
		assertEquals("lego the movie", tableViewer.getTable().getItems()[1].getText(2));
	}

	/**
	 * Test the Extraction from the model based on the selection in the table as
	 * a String.
	 * 
	 * @throws SystemException
	 *             SystemException
	 */
	@Test
	public void testGetSelectedRowsAsStringBuilder() throws SystemException {
		TestEditorTranslationService translationService = null;
		TestScenarioParameterTable paramTable = new TestScenarioParameterTable();
		paramTable.addParameterLine("|Name|FirstName|City");
		TestData testData = new TestData();
		TestDataRow testDataRow = new TestDataRow("Foo|Bar|legoland");
		testData.addRow(testDataRow);
		testDataRow = new TestDataRow("HamWay|ernest|lego the movie");
		testData.addRow(testDataRow);
		paramTable.addTestData(testData);
		TestEditorViewTableViewer tableViewer = new TestEditorViewTableViewer(shell, paramTable, translationService,
				testFlow, new MPartAdapter(), eventBroker);
		tableViewer.refreshTable();
		tableViewer.selectAllItems(true);

		String selectedRows = tableViewer.getSelectedRowsAsStringBuilder().toString();
		assertEquals("Foo|Bar|legoland|\nHamWay|ernest|lego the movie|\n", selectedRows);
	}

	/**
	 * Setup common objects used in the AUT like: - Shell object. - EventBroker.
	 */
	@Before
	public void setup() {
		shell = new Shell();
		testFlow = new TestCase();
		eventBroker = new EventBrokerMock() {
			@Override
			public boolean send(String topic, Object data) {
				return monitor.add(topic);
			}

			@Override
			public boolean post(String topic, Object data) {
				return send(topic, data);
			}
		};
		monitor = new HashSet<String>();
	}

	/**
	 * Dispose the UI Widget after test.
	 */
	@After
	public void tearDown() {
		shell.dispose();
	}

}
