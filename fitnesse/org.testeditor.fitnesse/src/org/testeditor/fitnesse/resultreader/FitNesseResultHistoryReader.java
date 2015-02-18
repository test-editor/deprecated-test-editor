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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.testeditor.core.model.testresult.TestResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * parse the result-history of a test or a suite.
 * 
 * @author llipinski
 * 
 */
// TODO Check to remove this class, when using Fitnesse File System backend.
public class FitNesseResultHistoryReader {

	private static final Logger LOGGER = Logger.getLogger(FitNesseResultHistoryReader.class);

	/**
	 * parse the result-history.
	 * 
	 * @param resultStream
	 *            the result-xml as a stream.
	 * @return List<{@link TestResult}>
	 */
	public List<TestResult> readResultHistory(InputStream resultStream) {
		List<TestResult> testResults = new ArrayList<TestResult>();
		String name;

		try {
			InputSource inputSource = new InputSource(new InputStreamReader(resultStream, "UTF-8"));
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(inputSource);
			// TODO here correct the method
			try {
				String longName = doc.getElementsByTagName("Name").item(0).getTextContent().trim();
				name = longName;
				String[] splits = longName.split("\\.");
				if (splits.length > 0) {
					name = splits[splits.length - 1];
				}
			} catch (NullPointerException e) {
				return testResults;
			}

			doc.getDocumentElement().normalize();
			NodeList nodeLst = doc.getElementsByTagName("TestResult");
			for (int s = 0; s < nodeLst.getLength(); s++) {

				TestResult testResult = new TestResult();

				Node node = nodeLst.item(s);

				if (node.getNodeType() == Node.ELEMENT_NODE) {

					Element element = (Element) node;

					String date = element.getElementsByTagName("Date").item(0).getTextContent();
					int endPoint = element.getElementsByTagName("ResultLink").item(0).getTextContent().length()
							- "&format=xml".length();
					String resultLink = element.getElementsByTagName("ResultLink").item(0).getTextContent()
							.substring(0, endPoint);
					// read counts information
					String right = element.getElementsByTagName("Pass").item(0).getTextContent();
					String wrong = element.getElementsByTagName("Fail").item(0).getTextContent();
					String ignores = "0";
					String exceptions = "0";

					// set values to testresult
					testResult.setFullName(name);

					testResult.setResultDate(parseDateString(date));
					testResult.setResultLink(resultLink);
					testResult.setRight(Integer.parseInt(right));
					testResult.setWrong(Integer.parseInt(wrong));
					testResult.setIgnored(Integer.parseInt(ignores));
					testResult.setException(Integer.parseInt(exceptions));

					testResults.add(testResult);

				}
			}
		} catch (ParserConfigurationException | SAXException | IOException e) {
			LOGGER.error(e.getMessage());
		} catch (ParseException e) {
			LOGGER.error(e.getMessage());
		}
		return testResults;
	}

	/**
	 * parse the date-input-string into a date.
	 * 
	 * @param dateString
	 *            the dateString
	 * @return the date
	 * @throws ParseException
	 *             if the format is not correct.
	 */
	private Date parseDateString(String dateString) throws ParseException {
		DateFormat df = new SimpleDateFormat("EEE MMM dd kk:mm:ss z yyyy", Locale.ENGLISH);
		return df.parse(dateString);
	}
}
