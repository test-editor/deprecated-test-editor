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
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.services.interfaces.TestProjectService;
import org.xml.sax.InputSource;

/**
 * @author alebedev
 * 
 *         get data from XML files for LastRunTable
 */
public class GetDataTableLastRuns {

	@Inject
	private static TranslationService translationService;

	@Inject
	private TestProjectService testProjectService;

	/**
	 * The contributor URI.
	 */
	public static final String CONTRIBUTOR_URI = "platform:/plugin/org.testeditor.dashboard";

	/**
	 * creates a tree of test results for LastRunTable contains only last runs.
	 * of suites contains only last runs of test cases which were separately
	 * from suite runs
	 * 
	 * 
	 * @param projectname
	 *            to retrieve test results (DemoWebTest)
	 * @param modelService
	 *            to set part label
	 * @param window
	 *            trimmed window
	 * @param context
	 *            IEclipseContext context
	 * @param app
	 *            org.eclipse.e4.ide.application
	 * @return root with test results
	 * @throws JDOMException
	 *             if one of the arguments is invalid
	 * @throws IOException
	 *             Signals that an I/O exception of some sort has occurred
	 * @throws ParseException
	 *             Signals that an error has been reached unexpectedly while
	 *             parsing.
	 */
	public TestResult getData(String projectname, EModelService modelService, MWindow window, IEclipseContext context,
			MApplication app) throws JDOMException, IOException, ParseException {

		TestResult root = new TestResult();
		List<TestProject> projects = testProjectService.getProjects();

		int size = projects.size();

		if (size == 0) {
			ErrorMessage error = ContextInjectionFactory.make(ErrorMessage.class, context);
			error.errorProjectEmpty();
		}
		if (size > 0) {
			MPart mPart = (MPart) modelService.find("org.testeditor.ui.part.0", app);
			File testResultsDir = null;

			if (projectname != null) {
				testResultsDir = new File(Platform.getLocation().toFile() + "\\" + projectname
						+ "\\FitNesseRoot\\files\\testResults");
				mPart.setLabel(translationService.translate("%dashboard.table.label.lastrun", CONTRIBUTOR_URI) + " "
						+ projectname);
			} else {
				testResultsDir = new File(Platform.getLocation().toFile() + "\\" + projects.get(0)
						+ "\\FitNesseRoot\\files\\testResults");
				mPart.setLabel(translationService.translate("%dashboard.table.label.lastrun", CONTRIBUTOR_URI) + " "
						+ projects.get(0).getName());
			}

			if (checkFile(testResultsDir)) {
				root = getDataFromXMLResultFiles(root, testResultsDir);
			} else {
				ErrorMessage error = ContextInjectionFactory.make(ErrorMessage.class, context);
				error.errorProjectEmpty();
			}
		}
		return root;
	}

	/**
	 * gets data from XML file.
	 * 
	 * @param root
	 *            with test results
	 * @param testResultDir
	 *            test results directory (for example
	 *            ...\.testeditor\DemoSwingTests
	 *            \FitNesseRoot\files\testResults)
	 * @param list
	 *            of XML files
	 * @param app
	 * @return root with test results
	 * @throws JDOMException
	 *             if one of the arguments is invalid
	 * @throws IOException
	 *             Signals that an I/O exception of some sort has occurred
	 * @throws ParseException
	 *             Signals that an error has been reached unexpectedly while
	 *             parsing.
	 */

	private TestResult getDataFromXMLResultFiles(TestResult root, File testResultsDir) throws JDOMException,
			IOException, ParseException {

		File[] allTestResults = testResultsDir.listFiles();
		Arrays.sort(allTestResults);

		for (int i = 0; i < allTestResults.length; i++) {
			String fullTestCaseName = allTestResults[i].getName();
			String testCaseName = TableAllRuns.getLastName(fullTestCaseName);
			File testCaseFolder = new File(testResultsDir + "\\" + fullTestCaseName);
			if (!"SuiteSetUp".equals(testCaseName) && !"SuiteTearDown".equals(testCaseName)
					&& checkFile(testCaseFolder)) {
				// folder exists and has files
				int runCount = getRunsCount(testCaseFolder);
				File lastModFile = getLastModified(testCaseFolder);
				if (checkSuite(lastModFile)) {
					// it is a suite
					root = addSuiteToResults(lastModFile, root, fullTestCaseName, runCount);
				} else {
					// it is test case
					root = retrieveTestCaseData(root, i, fullTestCaseName, testCaseFolder, runCount, lastModFile);
				}
			}
		}
		return root;
	}

	/**
	 * checks if there is a need to find parent suite and compare time stamps.
	 * for 1st file there is no need
	 * 
	 * @param root
	 *            with test results
	 * @param list
	 *            of XML files
	 * @param i
	 *            file counter
	 * @param testCaseName
	 *            test case name file name
	 *            DemoSwingTests.GeburtstagsVerwaltungsSuite.
	 *            AnlegenUndPruefenTest
	 * @param testCaseFolder
	 *            C:\Users\alebedev\.testeditor\DemoSwingTests\FitNesseRoot\
	 *            files\testResults\DemoSwingTests.GeburtstagsVerwaltungsSuite.
	 *            AnlegenUndPruefenTest
	 * @param runCount
	 *            counter for all runs
	 * @param lastModFile
	 *            last modified file
	 *            C:\Users\alebedev\.testeditor\DemoSwingTests\FitNesseRoot\
	 *            files\testResults\DemoSwingTests.GeburtstagsVerwaltungsSuite.
	 *            AnlegenUndPruefenTest\20141016110428_16_0_0_0.xml
	 * @return root with test results
	 * @throws JDOMException
	 *             if one of the arguments is invalid
	 * @throws IOException
	 *             Signals that an I/O exception of some sort has occurred
	 * @throws ParseException
	 *             Signals that an error has been reached unexpectedly while
	 *             parsing.
	 */

	private TestResult retrieveTestCaseData(TestResult root, int i, String testCaseName, File testCaseFolder,
			int runCount, File lastModFile) throws JDOMException, IOException, ParseException {

		// it is test case
		if (i == 0) {// 1st test case
			root = addTestCaseToResults(lastModFile, testCaseName, root, runCount);
		}
		if (i > 0) {// need to compare last run Parentsuite
			// and last run test case
			root = compareLastRunParentSuitesWithTestCaseRuns(root, testCaseName, testCaseFolder, runCount);
		}

		return root;
	}

	/**
	 * searches for parent suites of test case and compares the timestamps. In
	 * case of no matching, add test case to the test result.
	 * 
	 * @param root
	 *            with test results
	 * @param testCaseName
	 *            name of test case
	 *            DemoSwingTests.GeburtstagsVerwaltungsSuite.MassenAnlageTest
	 * @param resultDirectory
	 *            directory
	 *            C:\Users\alebedev\.testeditor\DemoSwingTests\FitNesseRoot
	 *            \files\testResults\DemoSwingTests.GeburtstagsVerwaltungsSuite.
	 *            MassenAnlageTest
	 * @param runCount
	 *            all runs counter
	 * @param lastModFile
	 *            last modified file
	 *            C:\Users\alebedev\.testeditor\DemoSwingTests
	 *            \FitNesseRoot\files
	 *            \testResults\DemoSwingTests.GeburtstagsVerwaltungsSuite
	 *            .MassenAnlageTest\20141023205106_20_0_0_0.xml
	 * @return root with test results
	 * @throws JDOMException
	 *             if one of the arguments is invalid
	 * @throws IOException
	 *             Signals that an I/O exception of some sort has occurred
	 * @throws ParseException
	 *             Signals that an error has been reached unexpectedly while
	 *             parsing.
	 */

	private TestResult compareLastRunParentSuitesWithTestCaseRuns(TestResult root, String testCaseName,
			File testCaseFolder, int runCount) throws JDOMException, IOException, ParseException {

		// Sort files by file name (timestamp)
		List<File> sortedTestCaseRuns = sortFilesByDate(testCaseFolder);
		String suiteName = testCaseName;

		while (suiteName.contains(".")) {
			suiteName = testCaseName.substring(0, suiteName.lastIndexOf("."));
			File suiteDir = new File(testCaseFolder.getParentFile().getAbsolutePath() + File.separator + suiteName);
			if (suiteDir.exists()) {
				sortedTestCaseRuns = removeTestCaseByMatchingTimestamp(sortedTestCaseRuns, suiteDir, testCaseFolder);
			}

			if (sortedTestCaseRuns.isEmpty()) {
				// all test cases are part of a suite, nothing to add
				return root;
			}
		}

		if (!sortedTestCaseRuns.isEmpty()) {
			// some test cases are left, add last modified (created)
			root = addTestCaseToResults(sortedTestCaseRuns.get(sortedTestCaseRuns.size() - 1), testCaseName, root,
					runCount);
		}

		return root;
	}

	/**
	 * check if directory exists and has files.
	 * 
	 * @param dir
	 *            test results directory
	 *            C:\Users\alebedev\.testeditor\DemoWebTests\FitNesseRoot
	 *            \files\testResults
	 * @return directory exists and has files. true or false
	 */
	static boolean checkFile(File dir) {
		return dir.exists() && dir.listFiles().length != 0;
	}

	/**
	 * counts all runs of test case or suite.
	 * 
	 * @param dir
	 *            suite or test case directroy
	 *            C:\Users\alebedev\.testeditor\DemoWebTests\FitNesseRoot
	 *            \files\testResults\DemoWebTests.GoogleSucheSuite
	 * @return runs count
	 */
	static int getRunsCount(File dir) {
		File[] files = dir.listFiles();
		int runsCount = files.length;
		return runsCount;
	}

	/**
	 * searches for last modified file in directory.
	 * 
	 * @param dir
	 *            directory suite ot test case
	 *            C:\Users\alebedev\.testeditor\DemoWebTests\FitNesseRoot
	 *            \files\testResults\DemoWebTests.GoogleSucheSuite
	 * @return last modified file or null if directory is empty
	 */
	static File getLastModified(File dir) {
		File[] files = dir.listFiles();

		if (files.length == 0) {
			return null;
		}

		File lastModifiedFile = files[0];
		for (int i = 1; i < files.length; i++) {
			if (lastModifiedFile.lastModified() < files[i].lastModified()) {
				lastModifiedFile = files[i];
			}
		}
		return lastModifiedFile;
	}

	/**
	 * sorts files by date. Works because the filename is a timestamp.
	 * 
	 * @param dir
	 *            directory of suite or test case
	 *            C:\Users\alebedev\.testeditor\DemoWebTests\FitNesseRoot
	 *            \files\testResults\DemoWebTests.GoogleSucheSuite.NewNew2
	 * @return root with test results
	 */
	static List<File> sortFilesByDate(File dir) {
		List<File> filesByDate = new ArrayList<File>(Arrays.asList(dir.listFiles()));
		Collections.sort(filesByDate);
		return filesByDate;
	}

	/**
	 * checks if file is a suite.
	 * 
	 * @param file
	 *            file C:\Users\alebedev\.testeditor\DemoWebTests\FitNesseRoot\
	 *            files
	 *            \testResults\DemoWebTests.GoogleSucheSuite.XxxSuiteinSuite
	 *            .SuiteInsuite2\20141010111805_0_1_0_0.xml
	 * 
	 * @return true or false
	 * @throws JDOMException
	 *             if one of the arguments is invalid
	 * @throws IOException
	 *             Signals that an I/O exception of some sort has occurred
	 */
	static boolean checkSuite(File file) throws JDOMException, IOException {
		InputStream is = new FileInputStream(file.getPath());
		InputStreamReader inputReader = new InputStreamReader(is, "ISO-8859-1");
		InputSource inputSource = new InputSource(inputReader);
		inputSource.setEncoding("UTF-8");

		Document doc = new SAXBuilder().build(inputSource);
		Element docRoot = doc.getRootElement();

		return "suiteResults".equals(docRoot.getName());
	}

	/**
	 * sets data from xml SUITE result file in TestResult objects sets data
	 * from. in suite contained test cases
	 * 
	 * @param lastModFile
	 *            last modified file C:\Users\alebedev\.testeditor\DemoWebTests
	 *            \FitNesseRoot\files\testResults
	 *            \DemoWebTests.GoogleSucheSuite.XxxSuiteinSuite
	 *            .SuiteInsuite2\20141010111805_0_1_0_0.xml
	 * @param root
	 *            with test results
	 * @param fileName
	 *            name of test suite
	 *            DemoWebTests.GoogleSucheSuite.XxxSuiteinSuite.SuiteInsuite2
	 * @param runCount
	 *            all runs counter
	 * @return root
	 * @throws JDOMException
	 *             if one of the arguments is invalid
	 * @throws IOException
	 *             Signals that an I/O exception of some sort has occurred
	 * @throws ParseException
	 *             Signals that an error has been reached unexpectedly while
	 *             parsing.
	 */
	public static TestResult addSuiteToResults(File lastModFile, TestResult root, String fileName, int runCount)
			throws JDOMException, IOException, ParseException {

		InputStream is = new FileInputStream(lastModFile.getPath());
		InputStreamReader inputReader = new InputStreamReader(is, "UTF-8");
		InputSource inputSource = new InputSource(inputReader);
		inputSource.setEncoding("UTF-8");
		Document doc = new SAXBuilder().build(inputSource);
		Element docRoot = doc.getRootElement();
		List<Element> docPageHistoryReference = docRoot.getChildren("pageHistoryReference");
		List<Element> docChildren = docRoot.getChildren();

		TestResult suite = new TestResult();
		suite.setSuite(true);
		String suiteDate = lastModFile.getName().substring(0, 14); // 20140922142345
																	// yyyyMMddHHmmss
		Date dateSuite = new SimpleDateFormat("yyyyMMddHHmmss").parse(suiteDate);
		String formattedDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(dateSuite);
		suite.setDate(formattedDate);
		suite.setRunCount(runCount);
		suite.setName(fileName);
		Element finalCounts = docRoot.getChild("finalCounts");

		String suiteWrong = finalCounts.getChild("wrong").getValue();
		int suiteWrongInt = Integer.parseInt(suiteWrong);
		suite.setQuantityWrong(suiteWrongInt);

		String suiteRight = finalCounts.getChild("right").getValue();
		int suiteRightInt = Integer.parseInt(suiteRight);
		suite.setQuantityRight(suiteRightInt);

		String suiteIgnores = finalCounts.getChild("ignores").getValue();
		int suiteIgnoresInt = Integer.parseInt(suiteIgnores);
		suite.setQuantityIgnores(suiteIgnoresInt);

		String suiteExceptions = finalCounts.getChild("exceptions").getValue();
		int suiteExceptionsInt = Integer.parseInt(suiteExceptions);
		suite.setQuantityExceptions(suiteExceptionsInt);

		suite.setSubSetRightOf(subSetRightOfAsString(suite));
		suite.setSubSetWrongOf(subSetWrongOfAsString(suite));

		if (suite.isSuccessfully()) {
			suite.setResult("ok");
		}
		if (suite.isFailed()) {
			suite.setResult("failed");
		}
		if (suite.isWarning()) {
			suite.setResult("warning");
		}

		int e = 0;
		int runTimeInMillisTotal = 0;
		// get in suite included test cases results
		for (int m = 0; m < docChildren.size() && e < docPageHistoryReference.size(); m++) {

			Element pageHistoryReference = docChildren.get(m);
			if (pageHistoryReference == docPageHistoryReference.get(e)) {

				String name = pageHistoryReference.getChild("name").getValue();
				String date = pageHistoryReference.getChild("date").getValue();
				// 09/23/2014 MM/dd/yyyy HH:mm:ss
				Date dateTestfall = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").parse(date);
				String formattedDateTestfall = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(dateTestfall);

				Element countsElement = pageHistoryReference.getChild("counts");
				String right = countsElement.getChild("right").getValue();
				int rightint = Integer.parseInt(right);
				String wrong = countsElement.getChild("wrong").getValue();
				int wrongInt = Integer.parseInt(wrong);
				String ignores = countsElement.getChild("ignores").getValue();
				int ignoresInt = Integer.parseInt(ignores);
				String exceptions = countsElement.getChild("exceptions").getValue();
				int exceptionsInt = Integer.parseInt(exceptions);
				String runTimeInMillis = pageHistoryReference.getChild("runTimeInMillis").getValue();
				int runTimeInMillisInt = Integer.parseInt(runTimeInMillis);

				TestResult testResult = new TestResult();
				testResult.setTestcase(true);
				testResult.setName(name);
				testResult.setDate(formattedDateTestfall);
				testResult.setDuration(runTimeInMillisInt);
				testResult.setQuantityRight(rightint);
				testResult.setQuantityWrong(wrongInt);
				testResult.setRunCount(runCount);
				testResult.setQuantityIgnores(ignoresInt);
				testResult.setQuantityExceptions(exceptionsInt);
				testResult.setSubSetRightOf(subSetRightOfAsString(testResult));
				testResult.setSubSetWrongOf(subSetWrongOfAsString(testResult));

				if (testResult.isSuccessfully()) {
					testResult.setResult("ok");
				}
				if (testResult.isFailed()) {
					testResult.setResult("failed");
				}
				if (testResult.isWarning()) {
					testResult.setResult("warning");
				}

				suite.add(testResult);
				runTimeInMillisTotal += runTimeInMillisInt;
				e++;
			}

		}
		suite.setDuration(runTimeInMillisTotal);
		root.add(suite);
		return root;
	}

	private static String subSetWrongOfAsString(TestResult testResult) {
		String subSetWrongOf = Integer.toString(testResult.getQuantityWrong() + testResult.getQuantityExceptions())
				+ " "
				+ translationService.translate("%dashboard.table.label.lastrun.column.subset", CONTRIBUTOR_URI)
				+ " "
				+ Integer.toString(testResult.getQuantityRight() + testResult.getQuantityWrong()
						+ testResult.getQuantityIgnores() + testResult.getQuantityExceptions());
		return subSetWrongOf;
	}

	private static String subSetRightOfAsString(TestResult testResult) {
		String subSetRightOf = testResult.getQuantityRight()
				+ " "
				+ translationService.translate("%dashboard.table.label.lastrun.column.subset", CONTRIBUTOR_URI)
				+ " "
				+ Integer.toString(testResult.getQuantityRight() + testResult.getQuantityWrong()
						+ testResult.getQuantityIgnores() + testResult.getQuantityExceptions());
		return subSetRightOf;
	}

	/**
	 * sets data from xml Test Case result file in TestResult objects.
	 * 
	 * @param lastModFile
	 *            last modified file
	 *            C:\Users\alebedev\.testeditor\DemoWebTests\FitNesseRoot
	 *            \files\testResults
	 *            \DemoWebTests.GoogleSucheSuite.XxxSuiteinSuite
	 *            .SuiteInsuite2.TestfallMitFehler\20141016142432_3_1_0_0.xml
	 * @param fileName
	 *            name of test case
	 *            DemoWebTests.GoogleSucheSuite.XxxSuiteinSuite.SuiteInsuite2
	 *            .TestfallMitFehler
	 * @param root
	 *            with test results
	 * @param runCount
	 *            all test runs counter
	 * @return root
	 * @throws JDOMException
	 *             if one of the arguments is invalid
	 * @throws IOException
	 *             Signals that an I/O exception of some sort has occurred
	 * @throws ParseException
	 *             Signals that an error has been reached unexpectedly while
	 *             parsing.
	 */
	public static TestResult addTestCaseToResults(File lastModFile, String fileName, TestResult root, int runCount)
			throws JDOMException, IOException, ParseException {
		InputStream is = new FileInputStream(lastModFile.getPath());
		InputStreamReader inputReader = new InputStreamReader(is, "UTF-8");
		InputSource inputSource = new InputSource(inputReader);
		inputSource.setEncoding("UTF-8");
		Document doc = new SAXBuilder().build(inputSource);
		Element docRoot = doc.getRootElement();
		Element docChildResult = docRoot.getChild("result");
		String testfallDate = lastModFile.getName().substring(0, 14);
		Date date = new SimpleDateFormat("yyyyMMddHHmmss").parse(testfallDate);
		String formattedDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(date);

		Element countsElement = docChildResult.getChild("counts");
		String right = countsElement.getChild("right").getValue();
		int rightint = Integer.parseInt(right);
		String wrong = countsElement.getChild("wrong").getValue();
		int wrongInt = Integer.parseInt(wrong);
		String ignores = countsElement.getChild("ignores").getValue();
		int ignoresInt = Integer.parseInt(ignores);
		String exceptions = countsElement.getChild("exceptions").getValue();
		int exceptionsInt = Integer.parseInt(exceptions);
		String runTimeInMillis = docChildResult.getChild("runTimeInMillis").getValue();
		int runTimeInMillisInt = Integer.parseInt(runTimeInMillis);

		TestResult testResult = new TestResult();
		testResult.setTestcase(true);
		testResult.setDate(formattedDate);
		testResult.setDuration(runTimeInMillisInt);
		testResult.setQuantityRight(rightint);
		testResult.setQuantityWrong(wrongInt);
		testResult.setName(fileName);
		testResult.setRunCount(runCount);
		testResult.setQuantityIgnores(ignoresInt);
		testResult.setQuantityExceptions(exceptionsInt);
		testResult.setSubSetRightOf(subSetRightOfAsString(testResult));
		testResult.setSubSetWrongOf(subSetWrongOfAsString(testResult));

		if (wrongInt + exceptionsInt + ignoresInt == 0 && rightint > 0) {
			testResult.setResult("ok");
		}
		if (wrongInt > 0 || exceptionsInt > 0 || ignoresInt > 0) {
			testResult.setResult("failed");
		}
		if (rightint + wrongInt + ignoresInt + exceptionsInt == 0 || wrongInt + exceptionsInt + rightint == 0
				&& ignoresInt > 0) {
			testResult.setResult("warning");
		}
		root.add(testResult);
		return root;

	}

	/**
	 * compares timestamps of test case and suite to remove suite runs from file
	 * list.
	 * 
	 * @param lastRunFileList
	 *            : all test case results files except of suite runs
	 * @param suiteDir
	 *            : all test case result files including suite runs
	 * @param testCaseFolder
	 *            directory of test case
	 *            C:\Users\alebedev\.testeditor\DemoWebTests\FitNesseRoot\files\
	 *            testResults
	 *            \DemoWebTests.GoogleSucheSuite.XxxSuiteinSuite.SuiteInsuite2
	 *            .Ts2Tesfall
	 * @return filtered list of test case result files,without suite runs
	 * @throws JDOMException
	 *             if one of the arguments is invalid
	 * @throws IOException
	 *             Signals that an I/O exception of some sort has occurred
	 */
	public static List<File> removeTestCaseByMatchingTimestamp(List<File> lastRunFileList, File suiteDir,
			File testCaseFolder) throws JDOMException, IOException {
		List<File> filesByDateParentSuite = sortFilesByDate(suiteDir);
		int filesByDateParentSuiteLength = filesByDateParentSuite.size();

		// find test run files equals to suite runs and delete them from list
		Iterator<File> testResultsIterator = lastRunFileList.iterator();
		while (testResultsIterator.hasNext()) {
			for (int s = 0; s < filesByDateParentSuiteLength; s++) {
				String testfallDate = testResultsIterator.next().getName().substring(0, 14);

				InputStream is = new FileInputStream(filesByDateParentSuite.get(s).getPath());
				InputStreamReader inputReader = new InputStreamReader(is, "UTF-8");
				InputSource inputSource = new InputSource(inputReader);
				inputSource.setEncoding("UTF-8");
				Document doc = new SAXBuilder().build(inputSource);
				Element docRoot = doc.getRootElement();
				// nur testfall elemente in suitexml
				List<Element> docpageHistoryReference = docRoot.getChildren("pageHistoryReference");
				// alle elemente Suitexml
				List<Element> docChildren = docRoot.getChildren();

				int e = 0;
				for (int m = 0; m < docChildren.size() && e < docpageHistoryReference.size(); m++) {

					Element pageHistoryReference = docChildren.get(m);
					// testfall in suite mit dem gesuchten vergleichen
					if (pageHistoryReference == docpageHistoryReference.get(e)) {
						String nameinxml = pageHistoryReference.getChild("name").getValue();
						String nametestfall = testCaseFolder.getName();
						// test case in suite found
						if (nameinxml.equals(nametestfall)) {
							int timestampXMLlength = pageHistoryReference.getChild("pageHistoryLink").getValue()
									.length();
							String timestampXML = pageHistoryReference.getChild("pageHistoryLink").getValue()
									.substring(timestampXMLlength - 14, timestampXMLlength);
							// test case time stamp equals suite time stamp,
							// delete it from list
							if (timestampXML.equals(testfallDate)) {
								testResultsIterator.remove();
								s = filesByDateParentSuiteLength;
								break;
							}
						}
						e++;
					}
				}
			}
		}
		return lastRunFileList;
	}
}
