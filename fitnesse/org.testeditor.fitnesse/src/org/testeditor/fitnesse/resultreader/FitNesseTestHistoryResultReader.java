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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.testeditor.core.model.testresult.ActionResultTable;
import org.testeditor.core.model.testresult.InstructionExpectation;
import org.testeditor.core.model.testresult.InstructionsResult;
import org.testeditor.core.model.testresult.TestResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Reads from result file of an test case end returns a {@link TestResult}
 * object.
 * 
 * 
 */
public class FitNesseTestHistoryResultReader implements FitNesseResultReader {

	private static final Logger LOGGER = Logger.getLogger(FitNesseTestHistoryResultReader.class);

	@Override
	public TestResult readTestResult(InputStream resultStream) {

		TestResult testResult = new TestResult();

		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(new InputSource(new InputStreamReader(resultStream, "UTF-8")));

			doc.getDocumentElement().normalize();

			// create testresult for summary of suite
			Element finalCounts = (Element) doc.getElementsByTagName("counts").item(0);
			String right = finalCounts.getElementsByTagName("right").item(0).getTextContent();
			String wrong = finalCounts.getElementsByTagName("wrong").item(0).getTextContent();
			String ignores = finalCounts.getElementsByTagName("ignores").item(0).getTextContent();
			String exceptions = finalCounts.getElementsByTagName("exceptions").item(0).getTextContent();
			String runTimeInMillis = doc.getElementsByTagName("runTimeInMillis").item(0).getTextContent();

			String errorLog = "";
			Node item = doc.getElementsByTagName("stdOut").item(0);
			if (item != null) {
				errorLog = item.getTextContent();
			}

			if (right.isEmpty() && wrong.isEmpty() && ignores.isEmpty()) {
				// this case will be exists if test were canceled.
				return testResult;
			}

			testResult.setTestExecutionLog(errorLog);
			testResult.setRight(Integer.parseInt(right));
			testResult.setWrong(Integer.parseInt(wrong));
			testResult.setIgnored(Integer.parseInt(ignores));
			testResult.setException(Integer.parseInt(exceptions));
			testResult.setRunTimeMillis(Integer.parseInt(runTimeInMillis));

			testResult.setActionResultTables(createActionResultTable(doc.getElementsByTagName("tables").item(0)));
			testResult.setInstructionResultTables(createInstructionsResult(doc.getElementsByTagName("instructions")
					.item(0)));

		} catch (ParserConfigurationException | SAXException | IOException e) {
			LOGGER.error(e.getMessage());
		}

		return testResult;

	}

	/**
	 * Reads the DOM Tree from the Node "tables".
	 * 
	 * @param item
	 *            Node named tables
	 * @return Object model of the XML as List with the ActionResultTable's.
	 */
	protected List<InstructionsResult> createInstructionsResult(Node item) {
		List<InstructionsResult> result = new ArrayList<InstructionsResult>();
		if (item != null) {
			NodeList tableList = item.getChildNodes();
			for (int i = 0; i < tableList.getLength(); i++) {
				InstructionsResult resultTable = createInstructionsResultFrom(tableList.item(i));
				result.add(resultTable);
			}
		}
		return result;
	}

	/**
	 * Creates an InstructionsResultTable based on the given node.
	 * 
	 * @param item
	 *            used to create an InstructionsResultTable.
	 * @return new InstructionsResultTable.
	 */
	protected InstructionsResult createInstructionsResultFrom(Node item) {
		InstructionsResult result = new InstructionsResult();
		if (item != null && item.getNodeName().equals("instructionResult")) {
			NodeList childNodes = item.getChildNodes();
			for (int i = 0; i < childNodes.getLength(); i++) {
				Node node = childNodes.item(i);
				switch (node.getNodeName()) {
				case "instruction":
					result.setInstruction(node.getTextContent());
					break;
				case "slimResult":
					result.setResult(node.getTextContent());
					break;
				case "expectation":
					result.addExpectation(createExpectationFrom(node));
				default:
					break;
				}
			}
		} else {
			result.setInstruction("Invalid Instruction");
		}
		return result;
	}

	/**
	 * 
	 * @param node
	 *            expectation part of the testresult xml document.
	 * @return InstructionExpectation with the data of the xml node.
	 */
	protected InstructionExpectation createExpectationFrom(Node node) {
		InstructionExpectation exp = new InstructionExpectation();
		NodeList nodeList = node.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node item = nodeList.item(i);
			switch (item.getNodeName()) {
			case "status":
				exp.setStatus(item.getTextContent());
				break;
			case "row":
				exp.setActionPartPosition(Integer.parseInt(item.getTextContent()));
				break;

			default:
				break;
			}
		}
		return exp;
	}

	/**
	 * Reads the DOM Tree from the Node "tables".
	 * 
	 * @param item
	 *            Node named tables
	 * @return Object model of the XML as List with the ActionResultTable's.
	 */
	protected List<ActionResultTable> createActionResultTable(Node item) {
		ArrayList<ActionResultTable> result = new ArrayList<ActionResultTable>();
		if (item != null) {
			NodeList tableList = item.getChildNodes();
			for (int i = 0; i < tableList.getLength(); i++) {
				if (tableList.item(i).getNodeName().equals("table")) {
					ActionResultTable resultTable = createActionResultTableFrom(tableList.item(i));
					result.add(resultTable);
				}
			}
		}
		return result;
	}

	/**
	 * Creates an ActionResultTable based on the given node.
	 * 
	 * @param item
	 *            used to create an ActionResultTable.
	 * @return new ActionResultTable.
	 */
	protected ActionResultTable createActionResultTableFrom(Node item) {
		ActionResultTable resultTable = new ActionResultTable();
		if (item != null) {
			NodeList childNodes = item.getChildNodes();
			for (int i = 0; i < childNodes.getLength(); i++) {
				Node node = childNodes.item(i);
				switch (node.getNodeName()) {
				case "name":
					resultTable.setName(node.getTextContent());
					break;
				case "row":
					List<String> newRow = resultTable.createNewRow();
					NodeList colNodes = node.getChildNodes();
					for (int j = 0; j < colNodes.getLength(); j++) {
						if (colNodes.item(j).getNodeName().equals("col")) {
							newRow.add(colNodes.item(j).getTextContent());
						}
					}
					break;
				default:
					break;
				}
			}
		} else {
			resultTable.setName("Invalid ResultTable");
		}
		return resultTable;
	}

}