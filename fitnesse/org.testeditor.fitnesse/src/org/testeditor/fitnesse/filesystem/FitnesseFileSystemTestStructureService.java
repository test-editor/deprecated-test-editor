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
package org.testeditor.fitnesse.filesystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.model.testresult.TestResult;
import org.testeditor.core.model.teststructure.ScenarioSuite;
import org.testeditor.core.model.teststructure.TestCase;
import org.testeditor.core.model.teststructure.TestCompositeStructure;
import org.testeditor.core.model.teststructure.TestScenario;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.model.teststructure.TestSuite;
import org.testeditor.core.model.teststructure.TestType;
import org.testeditor.core.services.interfaces.TestStructureService;
import org.testeditor.fitnesse.resultreader.FitNesseResultReader;
import org.testeditor.fitnesse.resultreader.FitNesseResultReaderFactory;
import org.testeditor.fitnesse.util.FitNesseRestClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * 
 * Fitnesse Filesystem based implementation of the TestStructureService.
 * 
 */
public class FitnesseFileSystemTestStructureService implements TestStructureService {

	private static final Logger LOGGER = Logger.getLogger(FitnesseFileSystemTestStructureService.class);
	private HashSet<String> specialPages;

	@Override
	public void loadChildrenInto(TestCompositeStructure testCompositeStructure) throws SystemException {
		Path path = Paths.get(FitnesseFileSystemUtility.getPathToTestStructureDirectory(testCompositeStructure));

		if (Files.exists(path)) {

			try (DirectoryStream<Path> newDirectoryStream = Files.newDirectoryStream(path)) {
				for (Path file : newDirectoryStream) {
					if (file.toFile().isDirectory()) {
						String name = file.toFile().getName();
						if (!(name.startsWith(".") || isReservedName(name))) {
							File[] listFiles = file.toFile().listFiles(FitnesseFileSystemUtility.getPropertyFiler());
							if (listFiles.length > 0) {
								TestStructure structure = createTestStructureFrom(listFiles[0]);
								if (structure != null) {
									testCompositeStructure.addChild(structure);
								}
							}
						}
					}
				}

			} catch (IOException e) {
				LOGGER.error("Unable to scan directory", e);
				throw new SystemException("Unable to scan directory", e);
			}
		} else {
			LOGGER.trace("Path " + path.toAbsolutePath() + " does not exists");
		}
	}

	/**
	 * Creates the Path to the Directory of the TestResults of the given
	 * TestStructure in the FileSystem as a string.
	 * 
	 * @param testStructure
	 *            to be used for lookup.
	 * @return the path as string to the TestResults.
	 */
	public String getPathToTestResults(TestStructure testStructure) {
		StringBuilder sb = new StringBuilder();
		sb.append(FitnesseFileSystemUtility.getPathToProject(testStructure));
		sb.append(File.separator).append("FitNesseRoot").append(File.separator).append("files").append(File.separator)
				.append("testResults").append(File.separator).append(testStructure.getFullName());
		return sb.toString();
	}

	/**
	 * Creates an instance of a subclass of TestStructure based on the
	 * informations in the property file. if the property file contains the
	 * property: test it creates a TestCase. if the property file contains the
	 * property: suite it creates a TestSuite. if the property file contains the
	 * property: suites it creates a ScenarioSuite.
	 * 
	 * @param propertyFile
	 *            FitNesse xml file with the properties of a TestPage.
	 * @return a instance of subclass of TestStructure.
	 * @throws SystemException
	 *             on IOExcpetion or XML processing.
	 */
	protected TestStructure createTestStructureFrom(File propertyFile) throws SystemException {
		TestStructure result = null;
		try {
			DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document document = documentBuilder.parse(propertyFile);
			boolean isSuites = false;
			if (document.getFirstChild().getNodeName().equals("properties")) {
				NodeList nodeList = document.getFirstChild().getChildNodes();
				for (int i = 0; i < nodeList.getLength(); i++) {

					String nodeName = nodeList.item(i).getNodeName();

					switch (nodeName) {
					case "Test":
						result = new TestCase();
						break;
					case "Suite":
						if (FitnesseFileSystemUtility.isComponentNode(propertyFile.getAbsolutePath())) {
							result = new ScenarioSuite();
						} else {
							result = new TestSuite();
						}
						break;
					case "Suites":
						isSuites = true;
						break;
					case "Static":
						isSuites = true;
						break;
					}
				}

				if (result == null) {
					if (isSuites) {
						result = new ScenarioSuite();
					} else {
						result = new TestScenario();
					}
				}

				String testStructureName = propertyFile.getParentFile().getName();
				result.setName(testStructureName.trim());
				if (result instanceof TestCompositeStructure) {
					initLazyLoader((TestCompositeStructure) result, propertyFile);
				}
			}
		} catch (ParserConfigurationException | SAXException | IOException e) {
			LOGGER.error("Error lodaingg properties of teststructrue", e);
			throw new SystemException(e.getMessage(), e);
		}
		return result;
	}

	/**
	 * 
	 * @param testCmp
	 *            TestComposite to be initialized with a lazy loader.
	 * @param propertyFile
	 *            of the TestComposite.
	 */
	private void initLazyLoader(TestCompositeStructure testCmp, File propertyFile) {
		testCmp.setChildCountInBackend(propertyFile.getParentFile().listFiles(
				FitnesseFileSystemUtility.getDirectoryFilter()).length);
		testCmp.setLazyLoader(getTestProjectLazyLoader(testCmp));
	}

	@Override
	public void create(TestStructure testStructure) throws SystemException {
		Path pathToTestStructure = Paths.get(FitnesseFileSystemUtility.getPathToTestStructureDirectory(testStructure));
		if (Files.exists(pathToTestStructure)) {
			throw new SystemException("TestStructure allready exits");
		}
		try {
			Files.createDirectories(pathToTestStructure);
			Files.write(Paths.get(pathToTestStructure.toString() + File.separator + "content.txt"), testStructure
					.getSourceCode().getBytes(StandardCharsets.UTF_8));

			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.newDocument();
			Element props = doc.createElement("properties");
			doc.appendChild(props);
			props.appendChild(createTrueElement(doc, "Edit"));
			props.appendChild(createTrueElement(doc, "Files"));
			props.appendChild(createTrueElement(doc, "Properties"));
			props.appendChild(createTrueElement(doc, "RecentChanges"));
			props.appendChild(createTrueElement(doc, "Refactor"));
			props.appendChild(createTrueElement(doc, "Search"));
			props.appendChild(createTrueElement(doc, "Versions"));
			props.appendChild(createTrueElement(doc, "WhereUsed"));

			String type = testStructure.getPageType();
			if (type.equals(new ScenarioSuite().getPageType())) {
				type = "Suites";
			}
			props.appendChild(doc.createElement(type));

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(pathToTestStructure + File.separator + "properties.xml"));

			transformer.transform(source, result);

		} catch (IOException | ParserConfigurationException | TransformerException e) {
			LOGGER.error("Error creating teststructrue in filesystem", e);
			throw new SystemException(e.getMessage(), e);
		}
	}

	/**
	 * Creates XML Nodes with a True TextNode as child of the node.
	 * 
	 * @param doc
	 *            used for creation.
	 * @param name
	 *            of the new node
	 * @return a new node.
	 */
	private Node createTrueElement(Document doc, String name) {
		Element element = doc.createElement(name);
		element.appendChild(doc.createTextNode("true"));
		return element;
	}

	@Override
	public void delete(TestStructure testStructure) throws SystemException {

		if (testStructureExists(testStructure)) {

			try {
				Files.walkFileTree(Paths.get(FitnesseFileSystemUtility.getPathToTestStructureDirectory(testStructure)),
						FitnesseFileSystemUtility.getDeleteRecursiveVisitor());
				LOGGER.trace("Deleted teststructrue: " + testStructure);
			} catch (IOException e) {
				LOGGER.error("Error deleting teststructrue: " + testStructure, e);
				throw new SystemException("Error deleting teststructrue: " + testStructure + "\n" + e.getMessage(), e);
			}
		}
	}

	/**
	 * 
	 * @param testStructure
	 *            TestStructure
	 * @return true if {@link TestStructure} exists.
	 */
	private boolean testStructureExists(TestStructure testStructure) {
		return Files.exists(Paths.get(FitnesseFileSystemUtility.getPathToTestStructureDirectory(testStructure)),
				java.nio.file.LinkOption.NOFOLLOW_LINKS);
	}

	@Override
	public void rename(TestStructure testStructure, String newName) throws SystemException {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("oldFullName: " + testStructure.getFullName() + " newName: " + newName);
		}

		Path oldName = Paths.get(FitnesseFileSystemUtility.getPathToTestStructureDirectory(testStructure));

		try {
			Files.move(oldName, oldName.resolveSibling(newName));
		} catch (IOException e) {
			String message = "Error renaming teststructrue: from= " + oldName + " to: " + newName;
			LOGGER.error(message, e);
			throw new SystemException(message + "\n" + e.getMessage(), e);
		}

	}

	@Override
	public TestResult executeTestStructure(TestStructure testStructure, IProgressMonitor monitor)
			throws SystemException, InterruptedException {
		TestResult testResult = null;
		testResult = FitNesseRestClient.execute(testStructure, monitor);
		return testResult;
	}

	@Override
	public String getTestExecutionLog(TestStructure testStructure) throws SystemException {
		Path pathToTestStructure = Paths.get(FitnesseFileSystemUtility
				.getPathToTestStructureErrorDirectory(testStructure));
		return FitnesseFileSystemUtility.getContentOfFitnesseFileForTestStructure(testStructure,
				pathToTestStructure.toString() + File.separator + "content.txt");
	}

	@Override
	public List<TestResult> getTestHistory(TestStructure testStructure) throws SystemException {

		List<TestResult> result = new ArrayList<TestResult>();

		if (!FitnesseFileSystemUtility.existsPathToTestStructureDirectory(testStructure)) {
			return result;
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		Path testResultsDirOfTestStructure = Paths.get(getPathToTestResults(testStructure));

		// no historie data exists
		if (!Files.exists(testResultsDirOfTestStructure)) {
			return result;
		}

		LOGGER.trace("Reading Testhistory from " + testResultsDirOfTestStructure);
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(testResultsDirOfTestStructure)) {

			for (Path path : stream) {
				FitNesseResultReader reader = FitNesseResultReaderFactory.getReader(TestType.valueOf(testStructure
						.getPageType().toUpperCase()));
				FileInputStream fileInputStream = new FileInputStream(path.toFile());
				TestResult testResult = reader.readTestResult(fileInputStream);
				String timestampString = path.getFileName().toString().substring(0, 14);
				LOGGER.trace("Reading Testhistory with Timestamp " + timestampString);
				testResult.setResultDate(sdf.parse(timestampString));
				testResult.setFullName(testStructure.getFullName());
				testResult.setResultLink(getResultLink(testStructure.getFullName(), timestampString));
				result.add(testResult);

				fileInputStream.close();
			}

		} catch (IOException | ParseException e) {
			LOGGER.error("Error reading testresults of teststructrue: " + testStructure, e);
			throw new SystemException("Error reading testresults of teststructrue: " + testStructure + "\n"
					+ e.getMessage(), e);
		}

		Collections.sort(result, new Comparator<TestResult>() {
			@Override
			public int compare(TestResult o1, TestResult o2) {
				return o2.getResultDate().compareTo(o1.getResultDate());
			}

		});

		return result;
	}

	/**
	 * Creates URL part to the test history. Example:
	 * DemoWebTests.LocalDemoSuite
	 * .LoginSuite.LoginInvalidClearTest?pageHistory&amp
	 * ;resultDate=20131121112026
	 * 
	 * @param fullName
	 *            of the TestStructure
	 * @param timestampString
	 *            of the Test execution.
	 * @return the Fitnesse URL part to the test history.
	 */
	protected String getResultLink(String fullName, String timestampString) {
		StringBuilder sb = new StringBuilder();
		sb.append(fullName).append("?pageHistory&resultDate=").append(timestampString);
		return sb.toString();
	}

	@Override
	public boolean isReservedName(String name) {
		return getSpecialPages().contains(name);
	}

	/**
	 * FitNesse has some reserved Words for special pages. This pages are used
	 * for example as test preparation. See:
	 * http://fitnesse.org/FitNesse.UserGuide.SpecialPages
	 * 
	 * @return a Set of reserved Names in FitNesse.
	 */
	private Set<String> getSpecialPages() {
		if (specialPages == null) {
			specialPages = new HashSet<String>();
			specialPages.add("PageHeader");
			specialPages.add("PageFooter");
			specialPages.add("SetUp");
			specialPages.add("TearDown");
			specialPages.add("SuiteSetUp");
			specialPages.add("SuiteTearDown");
			specialPages.add("ScenarioLibrary");
			specialPages.add("TemplateLibrary");
			specialPages.add("Suites");
		}
		return specialPages;
	}

	@Override
	public void clearTestHistory(TestStructure testStructure) throws SystemException {
		String pathToTestResults = getPathToTestResults(testStructure);
		try {
			if (new File(pathToTestResults).exists()) {
				Files.walkFileTree(Paths.get(pathToTestResults), FitnesseFileSystemUtility.getDeleteRecursiveVisitor());
			}
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
			throw new SystemException(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public Runnable getTestProjectLazyLoader(final TestCompositeStructure toBeLoadedLazy) {
		return new Runnable() {

			@Override
			public void run() {
				try {
					loadChildrenInto(toBeLoadedLazy);
				} catch (SystemException e) {
					LOGGER.error(e.getMessage(), e);
					throw new RuntimeException(e);
				}
			}

		};
	}

	@Override
	public String getId() {
		return FitnesseFileSystemConstants.PLUGIN_ID;
	}

	@Override
	public boolean hasTestExecutionLog(TestStructure testStructure) throws SystemException {
		return FitnesseFileSystemUtility.existsContentTxtInPathOfTestStructureInErrorDirectory(testStructure);
	}

}
