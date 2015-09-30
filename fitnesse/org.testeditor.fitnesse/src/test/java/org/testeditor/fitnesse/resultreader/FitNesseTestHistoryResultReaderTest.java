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
package org.testeditor.fitnesse.resultreader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Test;
import org.testeditor.core.model.testresult.ActionResultTable;
import org.testeditor.core.model.testresult.InstructionExpectation;
import org.testeditor.core.model.testresult.InstructionsResult;
import org.testeditor.core.model.testresult.TestResult;
import org.testeditor.core.model.teststructure.TestType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * 
 * Test for the FitNesseTestResultReader.
 *
 */
public class FitNesseTestHistoryResultReaderTest {

	/**
	 * Test the reading from null object.
	 * 
	 * @throws Exception
	 *             on Test execution.
	 */
	@Test
	public void testCreateTestActionReportWithInvalidXML() throws Exception {
		FitNesseTestHistoryResultReader resultReader = new FitNesseTestHistoryResultReader();
		assertNotNull("Expecting a Result list. Min an empty one.", resultReader.createActionResultTable(null));
	}

	/**
	 * Test the reading from an XML Structure of the TestTables.
	 * 
	 * @throws Exception
	 *             on Test execution.
	 */
	@Test
	public void testCreateTestActionReportWithValidXML() throws Exception {
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		Element element = doc.createElement("tables");
		Element table = doc.createElement("table");
		element.appendChild(table);
		FitNesseTestHistoryResultReader resultReader = new FitNesseTestHistoryResultReader();
		List<ActionResultTable> list = resultReader.createActionResultTable(element);
		assertNotNull("Expecting a Result list. Min an empty one.", list);
		ActionResultTable actionResultTable = list.get(0);
		assertNotNull(actionResultTable);
	}

	/**
	 * Test the reading from null object.
	 * 
	 * @throws Exception
	 *             on Test execution.
	 */
	@Test
	public void testCreateActionResultTableFromWithNull() throws Exception {
		FitNesseTestHistoryResultReader resultReader = new FitNesseTestHistoryResultReader();
		ActionResultTable resultTable = resultReader.createActionResultTableFrom(null);
		assertNotNull(resultTable);
		assertTrue(resultTable.toString().contains("Invalid"));
	}

	/**
	 * Test the reading from XML object.
	 * 
	 * @throws Exception
	 *             on Test execution.
	 */
	@Test
	public void testCreateActionResultTableFromWithValidXML() throws Exception {
		FitNesseTestHistoryResultReader resultReader = new FitNesseTestHistoryResultReader();
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		String tableName = "MyTable";
		Element table = doc.createElement("table");
		Element nameTag = doc.createElement("name");
		nameTag.setTextContent(tableName);
		table.appendChild(nameTag);
		Element rowTag = doc.createElement("row");
		table.appendChild(rowTag);
		Element colTag = doc.createElement("col");
		colTag.setTextContent("action step");
		rowTag.appendChild(colTag);
		ActionResultTable resultTable = resultReader.createActionResultTableFrom(table);
		assertNotNull(resultTable);
		assertEquals(tableName, resultTable.getName());
		assertEquals("Expecting One Row in Table", 1, resultTable.getRows().size());
		assertEquals("Col Value expected.", "action step", resultTable.getRows().get(0).get(0));
	}

	/**
	 * Test the reading from null object.
	 * 
	 * @throws Exception
	 *             on Test execution.
	 */
	@Test
	public void testCreateInstructionsResultTableFromWithInvalidXML() throws Exception {
		FitNesseTestHistoryResultReader resultReader = new FitNesseTestHistoryResultReader();
		assertNotNull("Expecting a Result list. Min an empty one.", resultReader.createInstructionsResult(null));
	}

	/**
	 * Test the reading from an XML Structure of the InstructionsTables.
	 * 
	 * @throws Exception
	 *             on Test execution.
	 */
	@Test
	public void testCreateTestInstructionsReportWithValidXML() throws Exception {
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		Element element = doc.createElement("instructions");
		Element instruction = doc.createElement("instructionResult");
		element.appendChild(instruction);
		FitNesseTestHistoryResultReader resultReader = new FitNesseTestHistoryResultReader();
		List<InstructionsResult> list = resultReader.createInstructionsResult(element);
		assertNotNull("Expecting a Result list. Min an empty one.", list);
		InstructionsResult resultTable = list.get(0);
		assertNotNull(resultTable);
	}

	/**
	 * Test the reading from null object.
	 * 
	 * @throws Exception
	 *             on Test execution.
	 */
	@Test
	public void testCreateInstructionsResultTableFromWithNull() throws Exception {
		FitNesseTestHistoryResultReader resultReader = new FitNesseTestHistoryResultReader();
		InstructionsResult resultTable = resultReader.createInstructionsResultFrom(null);
		assertNotNull(resultTable);
		assertTrue(resultTable.toString().contains("Invalid"));
	}

	/**
	 * Test the reading from XML object without expectation.
	 * 
	 * @throws Exception
	 *             on Test execution.
	 */
	@Test
	public void testCreateInstructionsResultTableFromWithValidXMLWithoutExpectation() throws Exception {
		FitNesseTestHistoryResultReader resultReader = new FitNesseTestHistoryResultReader();
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		Element instructionResult = doc.createElement("instructionResult");
		Element inst = doc.createElement("instruction");
		inst.setTextContent("script");
		Element slimResult = doc.createElement("slimResult");
		slimResult.setTextContent("OK");
		instructionResult.appendChild(inst);
		instructionResult.appendChild(slimResult);
		InstructionsResult instructionsResult = resultReader.createInstructionsResultFrom(instructionResult);
		assertEquals("script", instructionsResult.getInstruction());
		assertEquals("OK", instructionsResult.getResult());
	}

	/**
	 * Test the reading from XML object with expectation.
	 * 
	 * @throws Exception
	 *             on Test execution.
	 */
	@Test
	public void testCreateInstructionsResultTableFromWithValidXMLWithExpectation() throws Exception {
		FitNesseTestHistoryResultReader resultReader = new FitNesseTestHistoryResultReader();
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		Element instructionResult = doc.createElement("instructionResult");
		Element exp = doc.createElement("expectation");
		instructionResult.appendChild(exp);
		exp = doc.createElement("expectation");
		instructionResult.appendChild(exp);
		InstructionsResult instructionsResult = resultReader.createInstructionsResultFrom(instructionResult);
		assertEquals(2, instructionsResult.getInstructionExpectations().size());
	}

	/**
	 * Tests creating an InstructionExpectation based on an XML Structure.
	 * 
	 * @throws Exception
	 *             on Test execution.
	 */
	@Test
	public void testCreateExpectationFrom() throws Exception {
		FitNesseTestHistoryResultReader resultReader = new FitNesseTestHistoryResultReader();
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		Element expNode = doc.createElement("expectation");
		Element status = doc.createElement("status");
		status.setTextContent("ignored");
		expNode.appendChild(status);
		Element row = doc.createElement("row");
		row.setTextContent("1");
		expNode.appendChild(row);
		InstructionExpectation expectation = resultReader.createExpectationFrom(expNode);
		assertNotNull(expectation);
		assertEquals("ignored", expectation.getStatus());
		assertEquals(1, expectation.getActionPartPosition());
	}

	/**
	 * Integration Test of the methods. Reading tables and instructions from a
	 * result xml of a single test case.
	 * 
	 * @throws IOException
	 *             while file-operation
	 */
	@Test
	public void testTestResultWithExecutionDetailsReadedFromXml() throws IOException {
		InputStream fileInputStream = FitNesseTestHistoryResultReader.class.getResourceAsStream("/test_result.xml");
		FitNesseResultReader reader = FitNesseResultReaderFactory.getHistoryReader(TestType.TEST);
		TestResult testResult = reader.readTestResult(fileInputStream);
		fileInputStream.close();
		assertNotNull("ActionTables is set", testResult.getActionResultTables());
		assertNotNull("Instructions is set", testResult.getInstructionResultTables());
	}

}
