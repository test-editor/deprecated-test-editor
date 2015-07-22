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
package org.testeditor.dashboard;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.inject.Inject;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * @author alebedev
 * 
 *         Error Table lists errors find in sent test case file from
 *         AllRunsTAble identification as error is performed, when slimresult is
 *         not OK or TRUE
 */
public class TableErrorIdent {

	@Inject
	private TranslationService translationService;

	/**
	 * The Logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(TableErrorIdent.class);

	/**
	 * The contributor URI.
	 */
	public static final String CONTRIBUTOR_URI = "platform:/plugin/org.testeditor.dashboard";
	/**
	 * Error table.
	 */
	private Table table;

	/**
	 * Constructor.
	 */
	@Inject
	public TableErrorIdent() {
	}

	/**
	 * disposes ErrorTable by project change.
	 * 
	 * @param fileName
	 *            file name
	 * @param modelService
	 *            to find part label
	 * @param window
	 *            trimmed window
	 * @param app
	 *            org.eclipse.e4.ide.application
	 */
	@Inject
	@Optional
	public void disposeOnProjectChangeEvent(@UIEventTopic("DisposeErrorTable1") String fileName,
			EModelService modelService, MWindow window, MApplication app) {
		if (table != null) {
			table.dispose();
			MPart mPart = (MPart) modelService.find("org.testeditor.ui.part.3", app);
			mPart.setLabel(translationService.translate("%dashboard.table.label.error", CONTRIBUTOR_URI));
			mPart.setTooltip(translationService.translate("%dashboard.table.label.error.tooltip.no.test.selected",
					CONTRIBUTOR_URI));
		}
	}

	/**
	 * disposes ErrorTable by test case or suite change.
	 * 
	 * @param fileName
	 *            file name
	 * @param modelService
	 *            to find part label
	 * @param window
	 *            trimmed window
	 * @param app
	 *            org.eclipse.e4.ide.application
	 */
	@Inject
	@Optional
	public void disposeOnTestCaseChangeEvent(@UIEventTopic("DisposeErrorTable") String fileName,
			EModelService modelService, MWindow window, MApplication app) {
		String[] arr = fileName.split("\\.");
		String projectName = arr[0];
		File resultFolder = new File(Platform.getLocation().toFile() + "\\" + projectName
				+ "\\FitNesseRoot\\files\\testResults" + "\\" + fileName);

		if (resultFolder.exists()) {
			File[] fileList = resultFolder.listFiles();
			if (fileList != null && fileList.length != 0) {
				if (table != null) {
					table.dispose();
					MPart mPart = (MPart) modelService.find("org.testeditor.ui.part.3", app);
					mPart.setLabel(translationService.translate("%dashboard.table.label.error", CONTRIBUTOR_URI));
					mPart.setTooltip(translationService.translate(
							"%dashboard.table.label.error.tooltip.no.test.selected", CONTRIBUTOR_URI));
				}
			}
		}
	}

	/**
	 * Reloads Error from new test case file.
	 * 
	 * @param allRunsResult
	 *            AllRunsResult object
	 * @param parent
	 *            composite parent
	 * @param modelService
	 *            to find part label
	 * @param window
	 *            trimmed window
	 * @param app
	 *            org.eclipse.e4.ide.application
	 * @param context
	 *            IEclipseContext context
	 * @throws ParseException
	 *             Signals that an error has been reached unexpectedly while
	 *             parsing a date.
	 * @throws IOException
	 *             Signals that an I/O exception of some sort has occurred
	 */
	@Inject
	@Optional
	public void getEvent(@UIEventTopic("Testobject") AllRunsResult allRunsResult, Composite parent,
			EModelService modelService, MWindow window, MApplication app, IEclipseContext context)
			throws ParseException, IOException {
		// file does not exist
		if (!allRunsResult.getTestResultFilePath().exists()) {
			ErrorMessage error = ContextInjectionFactory.make(ErrorMessage.class, context);
			error.errorFile();
		} else {
			if (table != null) {
				table.dispose();
			}

			// Set Tab label and tooltip
			MPart mPart = (MPart) modelService.find("org.testeditor.ui.part.3", app);
			Date date = new SimpleDateFormat("yyyyMMddHHmmss").parse(allRunsResult.getTestResultFilePath().getName()
					.substring(0, 14));
			String formattedDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(date);
			String[] arr = allRunsResult.getTestResultFilePath().getParentFile().getName().split("\\.");
			String filenameSplitted = arr[arr.length - 1];
			mPart.setLabel(translationService.translate("%dashboard.table.label.error", CONTRIBUTOR_URI) + " "
					+ filenameSplitted + " " + formattedDate);
			mPart.setTooltip(translationService.translate("%dashboard.table.label.error", CONTRIBUTOR_URI) + " "
					+ allRunsResult.getTestResultFilePath().getParentFile().getName() + " " + formattedDate);

			// Add table titles
			ArrayList<String> titelsList = new ArrayList<String>();
			titelsList.add(translationService.translate("%dashboard.table.label.error.column.action", CONTRIBUTOR_URI));
			titelsList.add(translationService.translate("%dashboard.table.label.error.column.result", CONTRIBUTOR_URI));

			parent.setLayout(new FillLayout());
			table = new Table(parent, SWT.BORDER | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);
			table.setHeaderVisible(true);
			table.setLinesVisible(true);

			for (int j = 0; j < titelsList.size(); j++) {
				TableColumn column = new TableColumn(table, SWT.CENTER);
				column.setText(titelsList.get(j));
				if (j == 0) {
					column.setWidth(250);
				}
				if (j == 1) {
					column.setWidth(400);
				}
				column.setAlignment(SWT.LEFT);
			}
			if (!allRunsResult.isSuccessfully()) {
				searchAndAddErrorsFromXML(allRunsResult);
			}

			parent.layout();
		}
	}

	/**
	 * searching and identifying errors in XML result file error if slim result
	 * is not OK or TRUE.
	 * 
	 * @param allRunsResult
	 *            XML file with all run results
	 * @throws IOException
	 *             Signals that an I/O exception of some sort has occurred
	 */
	private void searchAndAddErrorsFromXML(AllRunsResult allRunsResult) throws IOException {

		InputStream is = new FileInputStream(allRunsResult.getTestResultFilePath().getPath());
		InputStreamReader inputReader = new InputStreamReader(is, "ISO-8859-1");
		InputSource inputSource = new InputSource(inputReader);
		inputSource.setEncoding("UTF-8");

		// XPath to get all wrong test steps and exceptions
		XPath xPath = XPathFactory.newInstance().newXPath();
		String expression = "//instructionResult/expectation[status='wrong'] | //instructionResult/expectation[actual[text()[contains(.,'Exception:')]]]";

		try {
			NodeList expectations = (NodeList) xPath.compile(expression).evaluate(inputSource, XPathConstants.NODESET);
			if (expectations == null || expectations.getLength() == 0) {
				return;
			}

			for (int i = 0; i < expectations.getLength(); i++) {
				Element expectation = (Element) expectations.item(i);

				String actual = expectation.getElementsByTagName("actual").item(0).getTextContent();
				actual = actual.startsWith("Exception:") ? "Exception" : actual;
				actual = actual.startsWith("!fail:") ? actual.substring(6) : actual;

				// called method
				String method = expectation.getElementsByTagName("expected").item(0).getTextContent();
				method = method.trim().replaceAll("\\;", "");

				// retrieve instruction content
				Element parent = (Element) expectation.getParentNode();
				String instruction = parent.getElementsByTagName("instruction").item(0).getTextContent().trim();

				StringBuffer action = new StringBuffer();
				action.append(method);
				action.append(" ");
				appendParameters(instruction, action);

				TableItem item = new TableItem(table, SWT.NONE);
				item.setText(0, action.toString());
				item.setText(1, actual != null ? actual.trim() : "");
			}
		} catch (XPathExpressionException e) {
			LOGGER.warn("XPath leads to an error: ", e);
		}

	}

	/**
	 * Adds the parameter from the instruction string to the string buffer,
	 * separated by ", ".
	 * 
	 * @param instruction
	 *            content of the instruction in the result XML
	 * @param action
	 *            a string buffer representing the fitNesse action
	 */
	private void appendParameters(String instruction, StringBuffer action) {
		// remove all [ at the begin of instruction content
		while (instruction.startsWith("[")) {
			instruction = instruction.substring(1);
		}
		// remove all ] at the end of instruction content
		while (instruction.endsWith("]")) {
			instruction = instruction.substring(0, instruction.length() - 1);
		}

		action.append("( ");
		// split by ","
		String[] callArguments = instruction.split("\\,");

		boolean paramsFound = false;
		for (int k = 0; k < callArguments.length; k++) {
			if (paramsFound) {
				action.append(callArguments[k].trim());
				if (k < callArguments.length - 1) {
					// add separator if param isn't the last param
					action.append(", ");
				}
			} else if ("scriptTableActor".equals(callArguments[k].trim())) {
				// the next but one are all parameters
				paramsFound = true;
				++k; // skip called method
			}
		}
		action.append(" )");
	}
}