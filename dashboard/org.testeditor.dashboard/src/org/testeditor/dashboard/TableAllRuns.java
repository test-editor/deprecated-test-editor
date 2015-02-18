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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.jdom2.JDOMException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author alebedev
 * 
 *         shows all runs for selected Test case or Suite in LastRunsTable by
 *         double click on test result cell sends data to ErrorTable to update
 *         it
 */
@SuppressWarnings("restriction")
public class TableAllRuns {

	@Inject
	private TranslationService translationService;

	@Inject
	private IEventBroker eventBroker;

	/**
	 * The Logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(TableAllRuns.class);
	/**
	 * The contributor URI.
	 */
	public static final String CONTRIBUTOR_URI = "platform:/plugin/org.testeditor.dashboard";
	/**
	 * Table with all test runs.
	 */
	private Table table;

	/**
	 * Constructor.
	 */
	@Inject
	public TableAllRuns() {

	}

	/**
	 * disposes AllRunsResultTable by project, test suite, test case change.
	 * 
	 * @param tableToRefresh
	 *            project name
	 * @param modelService
	 *            to find table part
	 * @param window
	 *            TrimmedWindow
	 * @param app
	 *            org.eclipse.e4.ide.application
	 */
	@Inject
	@Optional
	public void refreshEvent(@UIEventTopic("DisposeAllRunsResultTable") String tableToRefresh,
			EModelService modelService, MWindow window, MApplication app) {
		if (table != null) {
			table.dispose();
			MPart mPart = (MPart) modelService.find("org.testeditor.ui.part.1", app);
			mPart.setLabel(translationService.translate("%dashboard.table.label.allruns", CONTRIBUTOR_URI));
			mPart.setTooltip(translationService.translate("%dashboard.table.label.allruns.tooltip.no.test.selected",
					CONTRIBUTOR_URI));
		}
	}

	/**
	 * by selecting test case or suite in LastRuntable the AllRunsTable is
	 * getting updated with selected data. It gets Test case or Test suite name,
	 * and retrieves all runs for it
	 * 
	 * @param context
	 *            IEclipseContext context
	 * @param app
	 *            org.eclipse.e4.ide.application
	 * @param fileName
	 *            file name to display all runs
	 *            DemoWebTests.GoogleSucheSuite.XxxSuiteinSuite.SuiteInsuite2
	 * @param parent
	 *            composite parent
	 * @param modelService
	 *            to find table part
	 * @throws ParserConfigurationException
	 *             if the virtual machine can not be mapped
	 * @throws SAXException
	 *             parsing exception if xml element not found
	 * @throws NumberFormatException
	 *             exception number is in wrong format
	 * @throws DOMException
	 *             operation is impossible to perform
	 * @throws JDOMException
	 *             if one of the arguments is invalid
	 * @throws IOException
	 *             Signals that an I/O exception of some sort has occurred
	 */
	@Inject
	@Optional
	public void getEvent(@UIEventTopic("FileName") String fileName, Composite parent, EModelService modelService,
			MApplication app, IEclipseContext context) throws ParserConfigurationException, SAXException, IOException,
			NumberFormatException, DOMException, JDOMException {

		String filenameSplitted = getLastName(fileName);
		String[] arr = fileName.split("\\.");
		String projectName = arr[0];

		File dir = new File(Platform.getLocation().toFile() + "\\" + projectName + "\\FitNesseRoot\\files\\testResults"
				+ "\\" + fileName);
		File[] fileList = null;
		if (dir.exists()) {
			fileList = dir.listFiles();
		} else {
			ErrorMessage error = ContextInjectionFactory.make(ErrorMessage.class, context);
			error.errorPath();
		}

		// path has no files
		if (fileList != null && fileList.length == 0) {
			ErrorMessage error = ContextInjectionFactory.make(ErrorMessage.class, context);
			error.errorFile();
		}

		MPart mPart = (MPart) modelService.find("org.testeditor.ui.part.1", app);
		if (fileList != null
				&& fileList.length != 0
				&& !(mPart.getLabel().equals(translationService.translate("%dashboard.table.label.allruns",
						CONTRIBUTOR_URI) + " " + filenameSplitted))) {

			mPart.setLabel(translationService.translate("%dashboard.table.label.allruns", CONTRIBUTOR_URI) + " "
					+ filenameSplitted);
			mPart.setTooltip(translationService.translate("%dashboard.table.label.allruns", CONTRIBUTOR_URI) + " "
					+ fileName);

			if (table != null) {
				table.dispose();
			}
			Arrays.sort(fileList);
			ArrayList<String> titelsList = new ArrayList<String>();

			final List<AllRunsResult> objektList = new ArrayList<AllRunsResult>();
			final ArrayList<List<AllRunsResult>> objektListSuiteTests = new ArrayList<List<AllRunsResult>>();
			titelsList = setAllRunResultData(objektListSuiteTests, objektList, fileName, dir, fileList, titelsList);

			parent.setLayout(new FillLayout());
			table = new Table(parent, SWT.BORDER | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);
			table.setHeaderVisible(true);
			table.setLinesVisible(true);
			TableItem item = new TableItem(table, SWT.NONE);
			int columnsToLoad = titelsList.size();
			if (columnsToLoad > 30) {
				columnsToLoad = 30;
			}
			if (objektListSuiteTests.isEmpty()) {
				// create table for test case
				for (int j = 0; j < columnsToLoad; j++) {
					TableColumn column = new TableColumn(table, SWT.CENTER);
					column.setText(titelsList.get(j));
					column.setWidth(150);
					column.setAlignment(SWT.CENTER);
					column.setToolTipText(translationService.translate(
							"%dashboard.table.label.allruns.tooltip.testcase", CONTRIBUTOR_URI));
				}

				// set test cases results in table
				for (int j = 0; j < table.getColumnCount(); j++) {
					// set 1st row results
					setFirstRowResults(objektList, item, j, 0);
				}
			} else {
				// create table for suite
				TableColumn suiteColumn = new TableColumn(table, SWT.CENTER);
				suiteColumn.setText(translationService.translate("%dashboard.table.label.allruns.suite",
						CONTRIBUTOR_URI));
				suiteColumn.setWidth(150);
				suiteColumn.setAlignment(SWT.CENTER);
				suiteColumn.setToolTipText(translationService.translate("%dashboard.table.label.allruns.tooltip.suite",
						CONTRIBUTOR_URI));
				for (int j = 0; j < columnsToLoad; j++) {
					TableColumn tableColumn = new TableColumn(table, SWT.CENTER);
					tableColumn.setText(titelsList.get(j));
					tableColumn.setWidth(150);
					tableColumn.setAlignment(SWT.CENTER);
					tableColumn.setToolTipText(translationService.translate(
							"%dashboard.table.label.allruns.tooltip.suite", CONTRIBUTOR_URI));
				}

				// set suite results in table
				setSuiteResultsInTable(filenameSplitted, objektList, objektListSuiteTests, item);
			}

			eventBroker.send("DisposeChartTable", null);// delete chart table
			eventBroker.send("Testobjektlist", objektList);// create chart table
			eventBroker.send("DisposeErrorTable", fileName);
			sendDataToErrorTable(objektList, objektListSuiteTests);
			parent.layout();
		}
	}

	/**
	 * 
	 * sets Suite's retrieved data in AllRunsTable.
	 * 
	 * @param filenameSplitted
	 *            last name of suite GoogleSucheSuite
	 * @param objektList
	 *            list of all suite GoogleSucheSuite runs <AllRunsResult>
	 * @param objektListSuiteTests
	 *            list of all test cases runs in suite, for each suite run
	 *            <List<AllRunsResult>>
	 * @param item
	 *            table row
	 */
	private void setSuiteResultsInTable(String filenameSplitted, final List<AllRunsResult> objektList,
			final ArrayList<List<AllRunsResult>> objektListSuiteTests, TableItem item) {
		// set suite run results
		boolean blankCell = false;
		for (int j = 0; j < table.getColumnCount() - 1; j++) {
			if (!blankCell) {
				TableItem itemBlank = new TableItem(table, SWT.NONE);
				itemBlank
						.setText(0, translationService.translate("%dashboard.table.label.allruns.suite.testcases",
								CONTRIBUTOR_URI));
				itemBlank.setBackground(new Color(Display.getDefault(), 199, 199, 199)); // gray
				blankCell = true;
			}
			item.setText(0, filenameSplitted);
			// set 1st row results
			setFirstRowResults(objektList, item, j, 1);
			// set suite-test cases results
			for (int i = 0; i < objektListSuiteTests.get(j).size(); i++) {

				// set data for 1st name-column and 2nd result-column
				if (j == 0) {
					TableItem subitem = new TableItem(table, SWT.NONE);
					subitem.setText(j, "  -" + getLastName(objektListSuiteTests.get(j).get(i).getName()));
					setResultsSuiteTestCases(objektListSuiteTests, j, i, subitem);
				}
				// need to compare if test case name already in 1st
				// column
				if (j > 0) {
					int index = 0;
					boolean testnameExists = false;
					String testname = getLastName(objektListSuiteTests.get(j).get(i).getName());
					for (int k = 0; k < table.getItemCount(); k++) {
						if (table.getItem(k).getText().substring(3).equals(testname)) {
							testnameExists = true;
							index = k;
							break;
						}
					}
					if (testnameExists) {
						// if test case name is already in 1st column, take
						// existing item and enter data in corresponding column.
						if (objektListSuiteTests.get(j).get(i).isFailed()) {
							table.getItem(index).setBackground(j + 1, new Color(Display.getDefault(), 255, 182, 153));
							table.getItem(index).setImage(j + 1, MyLabelProvider.getImage("/failed.png"));
							table.getItem(index).setText(j + 1, objektListSuiteTests.get(j).get(i).getResultSummary());
						}
						if (objektListSuiteTests.get(j).get(i).isSuccessfully()) {
							table.getItem(index).setBackground(j + 1, new Color(Display.getDefault(), 161, 223, 101));
							table.getItem(index).setText(j + 1, objektListSuiteTests.get(j).get(i).getResultSummary());
							table.getItem(index).setImage(j + 1, MyLabelProvider.getImage("/ok.png"));
						}
						if (objektListSuiteTests.get(j).get(i).isWarning()) {
							table.getItem(index).setBackground(j + 1, new Color(Display.getDefault(), 248, 231, 112));
							table.getItem(index).setText(j + 1, objektListSuiteTests.get(j).get(i).getResultSummary());
							table.getItem(index).setImage(j + 1, MyLabelProvider.getImage("/warning.png"));
						}
					} else {
						// if test case name is not in 1st column, new item
						// will be created and enter data in corresponding
						// column.
						TableItem subitem = new TableItem(table, SWT.NONE);
						subitem.setText(0, "  -" + getLastName(objektListSuiteTests.get(j).get(i).getName()));
						setResultsSuiteTestCases(objektListSuiteTests, j, i, subitem);
					}
				}
			}
		}
	}

	/**
	 * 
	 * sends selected data to ErrorTable. testcasePath of selected test case for
	 * updating errors in a ErrorTable
	 * 
	 * @param objektList
	 *            list of all suite GoogleSucheSuite runs <AllRunsResult>
	 * @param objektListSuiteTests
	 *            list of all test cases runs in suite, for each suite run
	 *            <List<AllRunsResult>>
	 */

	private void sendDataToErrorTable(final List<AllRunsResult> objektList,
			final ArrayList<List<AllRunsResult>> objektListSuiteTests) {
		table.addListener(SWT.MouseDoubleClick, new Listener() {
			private int columnCount = table.getColumnCount();

			public void handleEvent(Event event) {
				Point pt = new Point(event.x, event.y);
				TableItem item = table.getItem(pt);
				if (item == null) {
					return;
				}
				boolean objectSend = false;

				for (int i = 0; i < columnCount; i++) {
					Rectangle rect = item.getBounds(i);
					if (rect.contains(pt)) {

						// suite table
						if (i > 0 && !(objektListSuiteTests.isEmpty())) {
							String testname = item.getText(0).substring(3);// 1st
																			// column
							// testname
							// search testname in
							// objektListSuiteTests.get(i)

							for (int o = 0; o < objektListSuiteTests.get(i - 1).size(); o++) {
								if (testname.equals(getLastName(objektListSuiteTests.get(i - 1).get(o).getName()))) {
									File testcasePath = new File(objektList.get(i - 1).getTestResultFilePath()
											.getParentFile().getParentFile()
											+ "\\"
											+ objektListSuiteTests.get(i - 1).get(o).getName()
											+ "\\"
											+ objektListSuiteTests.get(i - 1).get(o).getDate()
											+ "_"
											+ objektListSuiteTests.get(i - 1).get(o).getQuantityRight()
											+ "_"
											+ objektListSuiteTests.get(i - 1).get(o).getQuantityWrong()
											+ "_"
											+ objektListSuiteTests.get(i - 1).get(o).getQuantityIgnores()
											+ "_"
											+ objektListSuiteTests.get(i - 1).get(o).getQuantityExceptions() + ".xml");
									objektListSuiteTests.get(i - 1).get(o).setTestResultFilePath(testcasePath);
									eventBroker.send("Testobject", objektListSuiteTests.get(i - 1).get(o));
									objectSend = true;
									break;
								}
							}
						}
						if (objectSend) {
							break;
						}
						// test case table
						if (i >= 0 && objektListSuiteTests.isEmpty()) {
							if (objektList.get(i).getTestResultFilePath().exists()) {
								eventBroker.send("Testobject", objektList.get(i));
								objectSend = true;
								break;
							}
						}
					}
				}
			}
		});
	}

	/**
	 * 
	 * sets data for 1st row in a table. if test case in LastRunsTable selected
	 * 
	 * @param objektList
	 *            list of all suite GoogleSucheSuite runs <AllRunsResult>
	 * @param item
	 *            table row
	 * @param j
	 *            object list index
	 * @param indexShift
	 *            divergence between object list index and table column
	 */
	private void setFirstRowResults(final List<AllRunsResult> objektList, TableItem item, int j, int indexShift) {
		// SUITE Failed
		if (objektList.get(j).isFailed()) {
			item.setBackground(j + indexShift, new Color(Display.getDefault(), 255, 182, 153));
			item.setText(j + indexShift, objektList.get(j).getResultSummary());
			item.setImage(j + indexShift, MyLabelProvider.getImage("/failed.png"));
		}// SUITE Successfully
		if (objektList.get(j).isSuccessfully()) {
			item.setBackground(j + indexShift, new Color(Display.getDefault(), 161, 223, 101));
			item.setText(j + indexShift, objektList.get(j).getResultSummary());
			item.setImage(j + indexShift, MyLabelProvider.getImage("/ok.png"));
		}
		// SUITE Warning
		if (objektList.get(j).isWarning()) {
			item.setBackground(j + indexShift, new Color(Display.getDefault(), 248, 231, 112));
			item.setText(j + indexShift, objektList.get(j).getResultSummary());
			item.setImage(j + indexShift, MyLabelProvider.getImage("/warning.png"));
		}
	}

	/**
	 * 
	 * sets data for suite. if suite in LastRunsTable selected
	 * 
	 * @param objektListSuiteTests
	 *            list of all test cases runs in suite, for each suite run
	 *            <List<AllRunsResult>>
	 * @param subitem
	 *            test cases rows
	 * @param j
	 *            table column and object (suite file)
	 * @param i
	 *            object (test cases in suite)
	 */
	private void setResultsSuiteTestCases(final ArrayList<List<AllRunsResult>> objektListSuiteTests, int j, int i,
			TableItem subitem) {
		// SubTest Failed
		if (objektListSuiteTests.get(j).get(i).isFailed()) {
			subitem.setBackground(j + 1, new Color(Display.getDefault(), 255, 182, 153));
			subitem.setText(j + 1, objektListSuiteTests.get(j).get(i).getResultSummary());
			subitem.setImage(j + 1, MyLabelProvider.getImage("/failed.png"));
		}
		// SubTest Successfully
		if (objektListSuiteTests.get(j).get(i).isSuccessfully()) {
			subitem.setBackground(j + 1, new Color(Display.getDefault(), 161, 223, 101));
			subitem.setText(j + 1, objektListSuiteTests.get(j).get(i).getResultSummary());
			subitem.setImage(j + 1, MyLabelProvider.getImage("/ok.png"));
		}
		// SubTest Warning
		if (objektListSuiteTests.get(j).get(i).isWarning()) {
			subitem.setBackground(j + 1, new Color(Display.getDefault(), 248, 231, 112));
			subitem.setText(j + 1, objektListSuiteTests.get(j).get(i).getResultSummary());
			subitem.setImage(j + 1, MyLabelProvider.getImage("/warning.png"));
		}
	}

	/**
	 * 
	 * real name of test case or suite DemoWebTests.GoogleSucheSuite- name with.
	 * project name GoogleSucheSuite- real name
	 * 
	 * @param fileName
	 *            DemoWebTests.GoogleSucheSuite.NewNew2
	 * @return filenameSplitted last file name
	 */
	static String getLastName(String fileName) {
		String[] arr = fileName.split("\\.");
		return arr[arr.length - 1];
	}

	/**
	 * 
	 * sets retrieved result data into objects.
	 * 
	 * @param objektList
	 *            list of all suite GoogleSucheSuite runs <AllRunsResult>
	 * @param objektListAllSuiteTests
	 *            list of all test cases runs in suite, for each suite run
	 *            <List<AllRunsResult>>
	 * @param fileName
	 *            DemoWebTests.NurTestafal
	 * @param dir
	 *            directory of test case or suite
	 *            C:\Users\alebedev\.testeditor\DemoWebTests\FitNesseRoot\files\
	 *            testResults\DemoWebTests.NurTestafal
	 * @param fileList
	 *            list of all test result files in test case or sutie directory
	 *            C:\Users\alebedev
	 *            \.testeditor\DemoWebTests\FitNesseRoot\files\testResults
	 *            \DemoWebTests.NurTestafal\
	 * @param fileCount
	 *            test result file counter
	 * @param titelsList
	 *            list of dates of result files runs for column labels
	 * @throws DOMException
	 *             operation is impossible to perform
	 * @throws ParserConfigurationException
	 *             if the virtual machine can not be mapped
	 * @throws SAXException
	 *             parsing exception if xml element not found
	 * @throws NumberFormatException
	 *             exception number is in wrong format
	 * @throws JDOMException
	 *             if one of the arguments is invalid
	 * @throws IOException
	 *             Signals that an I/O exception of some sort has occurred
	 * @return titelsList
	 */
	private ArrayList<String> setAllRunResultData(ArrayList<List<AllRunsResult>> objektListAllSuiteTests,
			List<AllRunsResult> objektList, String fileName, File dir, File[] fileList, ArrayList<String> titelsList)
			throws ParserConfigurationException, SAXException, IOException, NumberFormatException, DOMException,
			JDOMException {

		// Inverse processing, because newest column comes first
		for (int k = fileList.length - 1; k >= 0; k--) {
			AllRunsResult test = new AllRunsResult();

			File file = fileList[k];
			String suiteDate = file.getName().substring(0, 14);
			test.setTestResultFilePath(file);
			test.setFilePath(dir);
			test.setName(fileName);

			try {
				// Date-columns labels
				Date dateSuite = new SimpleDateFormat("yyyyMMddHHmmss").parse(suiteDate);
				titelsList.add(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(dateSuite));
				test.setDate(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(dateSuite));
			} catch (ParseException e) {
				LOGGER.warn("Date parsing error" + suiteDate);
			}

			if (GetDataTableLastRuns.checkSuite(test.getTestResultFilePath())) {
				// suite
				List<AllRunsResult> objektListSuiteTestCases = new ArrayList<AllRunsResult>();
				retrieveSuiteData(fileList, k, test, objektListSuiteTestCases);
				objektListAllSuiteTests.add(objektListSuiteTestCases);
			} else {
				// test case
				retrieveTestCaseData(fileList, k, test);
			}

			objektList.add(test);
		}
		return titelsList;
	}

	/**
	 * gets test data for a test case from xml files.
	 * 
	 * @param fileList
	 *            list of files
	 * @param fileCount
	 *            amount of file in a list
	 * @param i
	 *            file in a list counter
	 * @param test
	 *            test object
	 * @throws SAXException
	 *             parsing exception if xml element not found
	 * @throws IOException
	 *             Signals that an I/O exception of some sort has occurred
	 * @throws ParserConfigurationException
	 *             if the virtual machine can not be mapped
	 * @throws FileNotFoundException
	 *             file not found
	 * @throws UnsupportedEncodingException
	 *             wrong encoding
	 */
	private void retrieveTestCaseData(File[] fileList, int k, AllRunsResult test) throws ParserConfigurationException,
			FileNotFoundException, UnsupportedEncodingException, SAXException, IOException {
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
		InputStream is = new FileInputStream(fileList[k].getPath());
		InputStreamReader inputReader = new InputStreamReader(is, "UTF-8");
		InputSource inputSource = new InputSource(inputReader);
		inputSource.setEncoding("UTF-8");
		Document document = docBuilder.parse(inputSource);
		NodeList nodeListTestCase = document.getElementsByTagName("*");
		boolean runTimeInMillis = false, right = false, wrong = false, ignores = false, exceptions = false;
		for (int n = 0; n < nodeListTestCase.getLength(); n++) {
			Node node = nodeListTestCase.item(n);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				if (node.getNodeName().equals("result")) {
					NodeList childresult = node.getChildNodes();
					for (int m = 0; m < childresult.getLength(); m++) {
						Node noderesult = childresult.item(m);
						if (noderesult.getNodeType() == Node.ELEMENT_NODE
								&& noderesult.getNodeName().equals("runTimeInMillis")) {
							test.setDuration(Integer.parseInt(noderesult.getTextContent()));
							runTimeInMillis = true;
							break;
						}
					}
				}
				if (node.getNodeName().equals("counts")) {
					NodeList childcounts = node.getChildNodes();
					for (int m = 0, lenchildcounts = childcounts.getLength(); m < lenchildcounts; m++) {
						Node nodeCounts = childcounts.item(m);
						if (nodeCounts.getNodeType() == Node.ELEMENT_NODE) {
							if (nodeCounts.getNodeName().equals("right")) {
								right = true;
								test.setQuantityRight(Integer.parseInt(nodeCounts.getTextContent()));
							}
							if (nodeCounts.getNodeName().equals("wrong")) {
								wrong = true;
								test.setQuantityWrong(Integer.parseInt(nodeCounts.getTextContent()));
							}
							if (nodeCounts.getNodeName().equals("ignores")) {
								ignores = true;
								test.setQuantityIgnores(Integer.parseInt(nodeCounts.getTextContent()));
							}
							if (nodeCounts.getNodeName().equals("exceptions")) {
								exceptions = true;
								test.setQuantityExceptions(Integer.parseInt(nodeCounts.getTextContent()));
							}
						}
						if (exceptions && ignores && wrong && right) {
							break;
						}
					}
				}

			}
			if (exceptions && ignores && wrong && right && runTimeInMillis) {
				break;
			}
		}
	}

	/**
	 * gets test data for a suite and contained test cases from xml files.
	 * 
	 * @param fileList
	 *            list of files
	 * @param fileCount
	 *            amount of file in a list
	 * @param i
	 *            file in a list counter
	 * @param test
	 *            test object
	 * @param objektListSuiteTestCases
	 *            list of all test cases runs in suite, for each suite run
	 *            <List<AllRunsResult>>
	 * @throws SAXException
	 *             parsing exception if xml element not found
	 * @throws IOException
	 *             Signals that an I/O exception of some sort has occurred
	 * @throws ParserConfigurationException
	 *             if the virtual machine can not be mapped
	 * @throws FileNotFoundException
	 *             file not found
	 * @throws UnsupportedEncodingException
	 *             wrong encoding
	 */
	private void retrieveSuiteData(File[] fileList, int k, AllRunsResult test,
			List<AllRunsResult> objektListSuiteTestCases) throws ParserConfigurationException, SAXException,
			IOException, FileNotFoundException, UnsupportedEncodingException {
		int suiteDurationInt = calculateSuiteDuration(test.getTestResultFilePath());
		test.setDuration(suiteDurationInt);

		// retrieve data for suite and test cases in suite
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
		InputStream is = new FileInputStream(fileList[k].getPath());
		InputStreamReader inputReader = new InputStreamReader(is, "UTF-8");
		InputSource inputSource = new InputSource(inputReader);
		inputSource.setEncoding("UTF-8");
		Document document = docBuilder.parse(inputSource);
		NodeList nodeList = document.getElementsByTagName("*");
		boolean suiteRight = false, suiteWrong = false, suiteIgnores = false, suiteExceptions = false;
		for (int n = 0, len = nodeList.getLength(); n < len; n++) {
			Node node = nodeList.item(n);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				if (node.getNodeName().equals("finalCounts")) {
					NodeList childFinalCounts = node.getChildNodes();
					for (int m = 0; m < childFinalCounts.getLength(); m++) {
						Node nodeFinalCounts = childFinalCounts.item(m);
						if (nodeFinalCounts.getNodeType() == Node.ELEMENT_NODE) {
							if (nodeFinalCounts.getNodeName().equals("right")) {
								suiteRight = true;
								test.setQuantityRight(Integer.parseInt(nodeFinalCounts.getTextContent()));
							}
							if (nodeFinalCounts.getNodeName().equals("wrong")) {
								suiteWrong = true;
								test.setQuantityWrong(Integer.parseInt(nodeFinalCounts.getTextContent()));
							}
							if (nodeFinalCounts.getNodeName().equals("ignores")) {
								suiteIgnores = true;
								test.setQuantityIgnores(Integer.parseInt(nodeFinalCounts.getTextContent()));
							}
							if (nodeFinalCounts.getNodeName().equals("exceptions")) {
								suiteExceptions = true;
								test.setQuantityExceptions(Integer.parseInt(nodeFinalCounts.getTextContent()));
							}
						}
						if (suiteRight && suiteWrong && suiteIgnores && suiteExceptions) {
							break;
						}
					}
				}
				if (node.getNodeName().equals("pageHistoryReference")) {
					// find test cases in suite
					AllRunsResult suiteTests = new AllRunsResult();
					NodeList childPageHistoryReference = node.getChildNodes();
					boolean testcaseDataSet = false;
					boolean suiteSetupTearSuite = true;
					for (int m = 0, lenchildPageHistoryReference = childPageHistoryReference.getLength(); m < lenchildPageHistoryReference; m++) {
						Node nodepageHistoryReference = childPageHistoryReference.item(m);
						if (nodepageHistoryReference.getNodeType() == Node.ELEMENT_NODE) {
							if (nodepageHistoryReference.getNodeName().equals("name")) {
								suiteTests.setName(nodepageHistoryReference.getTextContent());
								suiteSetupTearSuite = false;
							}
							if (nodepageHistoryReference.getNodeName().equals("pageHistoryLink")
									&& !suiteSetupTearSuite) {
								suiteTests.setDate(nodepageHistoryReference.getTextContent().substring(
										nodepageHistoryReference.getTextContent().length() - 14,
										nodepageHistoryReference.getTextContent().length()));
							}
							if (nodepageHistoryReference.getNodeName().equals("counts") && !suiteSetupTearSuite) {
								NodeList childcounts = nodepageHistoryReference.getChildNodes();
								boolean exceptions = false, ignores = false, wrong = false, right = false;
								for (int c = 0, lenchildcounts = childcounts.getLength(); c < lenchildcounts; c++) {
									Node nodeCounts = childcounts.item(c);
									if (nodeCounts.getNodeType() == Node.ELEMENT_NODE) {
										if (nodeCounts.getNodeName().equals("right")) {
											suiteTests.setQuantityRight(Integer.parseInt(nodeCounts.getTextContent()));
											right = true;
										}
										if (nodeCounts.getNodeName().equals("wrong")) {
											suiteTests.setQuantityWrong(Integer.parseInt(nodeCounts.getTextContent()));
											wrong = true;
										}
										if (nodeCounts.getNodeName().equals("ignores")) {
											suiteTests
													.setQuantityIgnores(Integer.parseInt(nodeCounts.getTextContent()));
											ignores = true;
										}
										if (nodeCounts.getNodeName().equals("exceptions")) {
											suiteTests.setQuantityExceptions(Integer.parseInt(nodeCounts
													.getTextContent()));
											exceptions = true;
										}
										if (exceptions && ignores && wrong && right) {
											objektListSuiteTestCases.add(suiteTests);
											testcaseDataSet = true;
											suiteSetupTearSuite = true;
											break;
										}
									}
								}
							}
						}
						if (testcaseDataSet) {
							break;
						}
					}
				}
			}
		}
	}

	/**
	 * calculating suite duration as sum of contained test cases durations.
	 * 
	 * @param filePath
	 *            suite test result path
	 *            C:\Users\alebedev\.testeditor\DemoWebTests\FitNesseRoot\files\
	 *            testResults
	 *            \DemoWebTests.GoogleSucheSuite\20141028213851_1_2_1_0.xml
	 * @throws SAXException
	 *             parsing exception if xml element not found
	 * @throws IOException
	 *             Signals that an I/O exception of some sort has occurred
	 * @throws ParserConfigurationException
	 *             if the virtual machine can not be mapped
	 * @return suiteDuration duration of suite run
	 */
	private int calculateSuiteDuration(File filePath) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
		InputStream is = new FileInputStream(filePath.getPath());
		InputStreamReader inputReader = new InputStreamReader(is, "UTF-8");
		InputSource inputSource = new InputSource(inputReader);
		inputSource.setEncoding("UTF-8");
		Document document = docBuilder.parse(inputSource);
		int suiteDuration = 0;
		NodeList nodeList = document.getElementsByTagName("*");
		for (int n = 0, len = nodeList.getLength(); n < len; n++) {
			Node node = nodeList.item(n);
			if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals("runTimeInMillis")) {
				suiteDuration = suiteDuration + Integer.parseInt(node.getTextContent());
			}
		}
		return suiteDuration;
	}
}