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
import java.util.Date;
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
 * Abstract class for common behavior of result reader of multiple test result
 * xmls.
 *
 */
public abstract class FitnesseMultiResultReader {

	private static final Logger LOGGER = Logger.getLogger(FitnesseMultiResultReader.class);

	/**
	 * Returns a list of {@link TestResult} after parsing.
	 * 
	 * @param multiResultTag
	 *            tag of the result elements which are more than one.
	 * @param resultStream
	 *            given stream of xml file.
	 * @return list of {@link TestResult}
	 */
	public TestResult readTestResult(InputStream resultStream, String multiResultTag) {

		TestResult suiteResult = new TestResult();

		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(new InputSource(new InputStreamReader(resultStream, "UTF-8")));

			doc.getDocumentElement().normalize();

			// create testresult for summary of suite
			String rootPath = doc.getElementsByTagName("rootPath").item(0).getTextContent();
			Element finalCounts = (Element) doc.getElementsByTagName("finalCounts").item(0);
			String right = finalCounts.getElementsByTagName("right").item(0).getTextContent();
			String wrong = finalCounts.getElementsByTagName("wrong").item(0).getTextContent();
			String ignores = finalCounts.getElementsByTagName("ignores").item(0).getTextContent();
			String exceptions = finalCounts.getElementsByTagName("exceptions").item(0).getTextContent();
			suiteResult.setFullName(rootPath);
			suiteResult.setRight(Integer.parseInt(right));
			suiteResult.setWrong(Integer.parseInt(wrong));
			suiteResult.setIgnored(Integer.parseInt(ignores));
			suiteResult.setException(Integer.parseInt(exceptions));
			int suiteRunTimeMillis = 0;

			// create testresult for each test case
			NodeList nodeLst = doc.getElementsByTagName(multiResultTag);
			for (int s = 0; s < nodeLst.getLength(); s++) {

				TestResult testResult = new TestResult();

				Node fstNode = nodeLst.item(s);

				if (fstNode.getNodeType() == Node.ELEMENT_NODE) {

					Element fstElmnt = (Element) fstNode;
					String name = getNameFrom(fstElmnt);
					NodeList tagList = fstElmnt.getElementsByTagName("date");
					if (tagList.getLength() > 0) {
						String date = tagList.item(0).getTextContent();
						testResult.setResultDate(parseDateString(date));
					}
					// read counts information
					Element countsElement = (Element) fstElmnt.getElementsByTagName("counts").item(0);
					right = countsElement.getElementsByTagName("right").item(0).getTextContent();
					wrong = countsElement.getElementsByTagName("wrong").item(0).getTextContent();
					ignores = countsElement.getElementsByTagName("ignores").item(0).getTextContent();
					exceptions = countsElement.getElementsByTagName("exceptions").item(0).getTextContent();
					String runTimeInMillis = fstElmnt.getElementsByTagName("runTimeInMillis").item(0).getTextContent();
					// set values to testresult
					testResult.setFullName(name);
					testResult.setRight(Integer.parseInt(right));
					testResult.setWrong(Integer.parseInt(wrong));
					testResult.setIgnored(Integer.parseInt(ignores));
					testResult.setException(Integer.parseInt(exceptions));
					testResult.setRunTimeMillis(Integer.parseInt(runTimeInMillis));

					suiteRunTimeMillis = suiteRunTimeMillis + Integer.parseInt(runTimeInMillis);
					suiteResult.add(testResult);

				}
			}
			suiteResult.setRunTimeMillis(suiteRunTimeMillis);

		} catch (ParserConfigurationException | SAXException | IOException e) {
			LOGGER.error(e.getMessage(), e);
			return null;
		} catch (ParseException e) {
			LOGGER.error(e.getMessage(), e);
			return null;
		}
		return suiteResult;
	}

	/**
	 * Extracts test structure name from xml node.
	 * 
	 * @param fstElmnt
	 *            node that represents the teststructure.
	 * @return name of the teststructure.
	 */
	protected String getNameFrom(Element fstElmnt) {
		String name = null;
		NodeList elementsByTagName = fstElmnt.getElementsByTagName("name");
		if (elementsByTagName.getLength() > 0) {
			name = elementsByTagName.item(0).getTextContent().trim();
		}
		elementsByTagName = fstElmnt.getElementsByTagName("pageHistoryLink");
		if (elementsByTagName.getLength() > 0) {
			String link = elementsByTagName.item(0).getTextContent().trim();
			name = link.substring(0, link.indexOf("?"));
		}

		return name;
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

		if (dateString.contains("/")) {
			df = new SimpleDateFormat("MM/dd/YY kk:mm:ss", Locale.ENGLISH);

		}
		return df.parse(dateString);
	}

}
