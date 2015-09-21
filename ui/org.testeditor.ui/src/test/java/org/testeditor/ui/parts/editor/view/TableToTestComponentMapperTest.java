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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;
import org.testeditor.core.model.teststructure.TestScenarioParameterTable;

/**
 * tests the TableToTestComponentMapper-class.
 * 
 * 
 */
public class TableToTestComponentMapperTest {

	private TestScenarioParameterTable testScenarioParameterTableOne = new TestScenarioParameterTable();
	private TestScenarioParameterTable testScenarioParameterTableTwo = new TestScenarioParameterTable();
	private TestScenarioParameterTable testScenarioParameterTableThree = new TestScenarioParameterTable();
	private TestScenarioParameterTable testScenarioParameterTableFour = new TestScenarioParameterTable();

	private TableToTestComponentMapper tableToTestComponentMapper = new TableToTestComponentMapper();

	private TestEditorViewTableViewer testEditorViewTableViewerOne = new TestEditorViewTableViewer();
	private TestEditorViewTableViewer testEditorViewTableViewerTwo = new TestEditorViewTableViewer();
	private TestEditorViewTableViewer testEditorViewTableViewerThree = new TestEditorViewTableViewer();
	private TestEditorViewTableViewer testEditorViewTableViewerFour = new TestEditorViewTableViewer();

	/**
	 * setup.
	 */
	@Before
	public void setup() {
		testScenarioParameterTableOne.setTitle("one");
		testScenarioParameterTableTwo.setTitle("two");
		testScenarioParameterTableThree.setTitle("three");
		testScenarioParameterTableFour.setTitle("four");
		tableToTestComponentMapper.addTableToTableStore(testEditorViewTableViewerOne, testScenarioParameterTableOne);
		tableToTestComponentMapper.addTableToTableStore(testEditorViewTableViewerTwo, testScenarioParameterTableTwo);
		tableToTestComponentMapper.addTableToTableStore(testEditorViewTableViewerThree,
				testScenarioParameterTableThree);
		tableToTestComponentMapper.addTableToTableStore(testEditorViewTableViewerFour, testScenarioParameterTableFour);

	}

	/**
	 * test the getTestScenarioTableToTableViewer.
	 */
	@Test
	public void testGetTestComponent() {
		assertEquals("three", tableToTestComponentMapper
				.getTestScenarioTableToTableViewer(testEditorViewTableViewerThree).getTitle());
	}

	/**
	 * test the clearing.
	 */
	@Test
	public void testTheClearing() {
		assertNotNull(tableToTestComponentMapper.getTestScenarioTableToTableViewer(testEditorViewTableViewerFour));
		tableToTestComponentMapper.clear();
		assertNull(tableToTestComponentMapper.getTestScenarioTableToTableViewer(testEditorViewTableViewerFour));
	}
}
