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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.junit.Test;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.model.testresult.TestResult;
import org.testeditor.core.model.teststructure.ScenarioSuite;
import org.testeditor.core.model.teststructure.TestCase;
import org.testeditor.core.model.teststructure.TestCompositeStructure;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestScenario;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.model.teststructure.TestSuite;

/**
 * 
 * Tests for the Fit and Slim based Implementation of the TestStructureService.
 * 
 */
public class FitnesseFileSystemTestStructureServiceTest extends FitnesseFileSystemAbstractTest {

	/**
	 * Tests the correct reading of the teststructure type testcase.
	 * 
	 * @throws Exception
	 *             on test failure
	 */
	@Test
	public void testCreateTestStructureFromTestCaseProperties() throws Exception {
		FitnesseFileSystemTestStructureService service = new FitnesseFileSystemTestStructureService();
		URL url = FileLocator.toFileURL(this.getClass().getResource("/history/tc_properties.xml"));
		assertNotNull(url);
		File file = new File(url.toURI());
		assertNotNull(file);
		assertTrue(file.exists());
		TestStructure testStructure = service.createTestStructureFrom(file);
		assertTrue(testStructure instanceof TestCase);
	}

	/**
	 * Tests the correct reading of the teststructure type testsuite.
	 * 
	 * @throws Exception
	 *             on test failure
	 */
	@Test
	public void testCreateTestStructureFromTestSuiteProperties() throws Exception {
		FitnesseFileSystemTestStructureService service = new FitnesseFileSystemTestStructureService();
		URL url = FileLocator.toFileURL(this.getClass().getResource("/history/ts_properties.xml"));
		TestStructure testStructure = service.createTestStructureFrom(new File(url.toURI()));
		assertTrue(testStructure instanceof TestSuite);
	}

	/**
	 * Tests the correct reading of the teststructure type testscenariosuite.
	 * 
	 * @throws Exception
	 *             on test failure
	 */
	@Test
	public void testCreateTestStructureFromTestScenarioSuiteProperties() throws Exception {
		FitnesseFileSystemTestStructureService service = new FitnesseFileSystemTestStructureService();
		URL url = FileLocator.toFileURL(this.getClass().getResource("/history/tsecsuite_properties.xml"));
		TestStructure testStructure = service.createTestStructureFrom(new File(url.toURI()));
		assertTrue(testStructure instanceof ScenarioSuite);
	}

	/**
	 * Tests the correct reading of the teststructure type testscenario.
	 * 
	 * @throws Exception
	 *             on test failure
	 */
	@Test
	public void testCreateTestStructureFromTestScenarioProperties() throws Exception {
		FitnesseFileSystemTestStructureService service = new FitnesseFileSystemTestStructureService();
		URL url = FileLocator.toFileURL(this.getClass().getResource("/history/tsec_properties.xml"));
		TestStructure testStructure = service.createTestStructureFrom(new File(url.toURI()));
		assertTrue(testStructure instanceof TestScenario);
	}

	/**
	 * Tests the lookup of the Path to the TestResults of a given TestStructure.
	 */
	@Test
	public void testGetPathToTestResults() {
		FitnesseFileSystemTestStructureService service = new FitnesseFileSystemTestStructureService();
		TestProject testProject = new TestProject();
		testProject.setName("TestPrj");
		TestCase tc = new TestCase();
		tc.setName("TesCa");
		testProject.addChild(tc);
		String pathToTestResults = service.getPathToTestResults(tc);
		String pathPart = File.separator + "FitNesseRoot" + File.separator + "files" + File.separator + "testResults"
				+ File.separator + "TestPrj.TesCa";
		assertTrue("Path ends with", pathToTestResults.endsWith(pathPart));
	}

	/**
	 * Test the loading of the sub testcases of a teststructurecomposite.
	 * 
	 * @throws Exception
	 *             on Test failure.
	 */
	@Test
	public void testLoadTestStructuresChildrenFor() throws Exception {
		TestProject testProject = createTestProjectsInWS();
		FitnesseFileSystemTestStructureService service = new FitnesseFileSystemTestStructureService();
		service.loadChildrenInto(testProject);
		assertEquals(2, testProject.getTestChildren().size());
	}

	/**
	 * Test the Remove of a Testcase from the filesystem. It checks fist that
	 * there is a testcase in the file system. After removing the file entry is
	 * also away.
	 * 
	 * @throws Exception
	 *             on Test failure.
	 */
	@Test
	public void testRemoveTestStructure() throws Exception {
		TestProject testProject = createTestProjectsInWS();
		FitnesseFileSystemTestStructureService service = new FitnesseFileSystemTestStructureService();
		assertTrue("Directory of Testcase exists.",
				Files.exists(Paths.get(Platform.getLocation().toFile().toPath().toString() + "/tp/FitNesseRoot/tp/tc")));
		service.delete(testProject.getTestChildByFullName("tp.tc"));
		assertFalse("Directory of Testcase is removed.",
				Files.exists(Paths.get(Platform.getLocation().toFile().toPath().toString() + "/tp/FitNesseRoot/tp/tc")));
	}

	/**
	 * Test the Renaming of teststructure.
	 * 
	 * @throws Exception
	 *             on Test failure.
	 */
	@Test
	public void testRenameTestStructure() throws Exception {
		TestProject testProject = createTestProjectsInWS();
		FitnesseFileSystemTestStructureService service = new FitnesseFileSystemTestStructureService();
		assertTrue("Testcase exists.",
				Files.exists(Paths.get(Platform.getLocation().toFile().toPath().toString() + "/tp/FitNesseRoot/tp/tc")));
		service.rename(testProject.getTestChildByFullName("tp.tc"), "tcChanged");

		assertFalse("Testcase with oldname does not exists.",
				Files.exists(Paths.get(Platform.getLocation().toFile().toPath().toString() + "/tp/FitNesseRoot/tp/tc")));

		assertTrue(
				"Testcase with changed name exists.",
				Files.exists(Paths.get(Platform.getLocation().toFile().toPath().toString()
						+ "/tp/FitNesseRoot/tp/tcChanged")));

	}

	/**
	 * Tests the Adding of a TestCase to an existing TestProject.
	 * 
	 * @throws Exception
	 *             on Test failure.
	 */
	@Test
	public void testAddTestCaseToTestProject() throws Exception {
		TestProject testProject = createTestProjectsInWS();
		FitnesseFileSystemTestStructureService service = new FitnesseFileSystemTestStructureService();
		TestCase tc = new TestCase();
		tc.setName("MyTestCase");
		testProject.addChild(tc);
		service.create(tc);
		assertTrue(
				"Directory of Testcase exists.",
				Files.exists(Paths.get(Platform.getLocation().toFile().toPath().toString()
						+ "/tp/FitNesseRoot/tp/MyTestCase")));
		assertTrue(
				"Content of Testcase exists.",
				Files.exists(Paths.get(Platform.getLocation().toFile().toPath().toString()
						+ "/tp/FitNesseRoot/tp/MyTestCase/content.txt")));
		assertTrue(
				"Properties of Testcase exists.",
				Files.exists(Paths.get(Platform.getLocation().toFile().toPath().toString()
						+ "/tp/FitNesseRoot/tp/MyTestCase/properties.xml")));
		assertTrue(new String(Files.readAllBytes(Paths.get(Platform.getLocation().toFile().toPath().toString()
				+ "/tp/FitNesseRoot/tp/MyTestCase/properties.xml")), StandardCharsets.UTF_8).contains("<Test/>"));
	}

	/**
	 * Tests Exception on adding of a allready existing TestCase to an existing
	 * TestProject.
	 * 
	 * @throws Exception
	 *             on Test failure.
	 */
	@Test
	public void testAddDuplicateTestCaseToTestProject() throws Exception {
		TestProject testProject = createTestProjectsInWS();
		FitnesseFileSystemTestStructureService service = new FitnesseFileSystemTestStructureService();
		TestCase tc = new TestCase();
		tc.setName("tc");
		testProject.addChild(tc);
		try {
			service.create(tc);
			fail("Exception expected.");
		} catch (SystemException e) {
			assertTrue(e.getMessage().contains("TestStructure allready exits"));
		}
	}

	/**
	 * Tests the Adding of a TestCase to an existing TestProject.
	 * 
	 * @throws Exception
	 *             on Test failure.
	 */
	@Test
	public void testAddTestCaseToTestSuite() throws Exception {
		TestProject testProject = createTestProjectsInWS();
		FitnesseFileSystemTestStructureService service = new FitnesseFileSystemTestStructureService();
		TestCase tc = new TestCase();
		tc.setName("MyTestCase");
		TestStructure structure = testProject.getTestChildByFullName("tp.ts");
		((TestCompositeStructure) structure).addChild(tc);
		service.create(tc);
		assertTrue(
				"Directory of Testcase exists.",
				Files.exists(Paths.get(Platform.getLocation().toFile().toPath().toString()
						+ "/tp/FitNesseRoot/tp/ts/MyTestCase")));
		assertTrue(
				"Properties of Testcase exists.",
				Files.exists(Paths.get(Platform.getLocation().toFile().toPath().toString()
						+ "/tp/FitNesseRoot/tp/ts/MyTestCase/properties.xml")));
		assertTrue(new String(Files.readAllBytes(Paths.get(Platform.getLocation().toFile().toPath().toString()
				+ "/tp/FitNesseRoot/tp/ts/MyTestCase/properties.xml")), StandardCharsets.UTF_8).contains("<Test/>"));
	}

	/**
	 * Tests the Adding of a TestSuite to an existing TestProject.
	 * 
	 * @throws Exception
	 *             on Test failure.
	 */
	@Test
	public void testAddTestStuiteToTestProject() throws Exception {
		TestProject testProject = createTestProjectsInWS();
		FitnesseFileSystemTestStructureService service = new FitnesseFileSystemTestStructureService();
		TestSuite ts = new TestSuite();
		ts.setName("CiSuite");
		testProject.addChild(ts);
		service.create(ts);
		assertTrue(
				"Directory of TestSuite exists.",
				Files.exists(Paths.get(Platform.getLocation().toFile().toPath().toString()
						+ "/tp/FitNesseRoot/tp/CiSuite")));
		assertTrue(
				"Content of TestSuite exists.",
				Files.exists(Paths.get(Platform.getLocation().toFile().toPath().toString()
						+ "/tp/FitNesseRoot/tp/CiSuite/content.txt")));
		assertTrue(
				"Properties of TestSuite exists.",
				Files.exists(Paths.get(Platform.getLocation().toFile().toPath().toString()
						+ "/tp/FitNesseRoot/tp/CiSuite/properties.xml")));
		assertTrue(
				"Property Suite exists.",
				new String(Files.readAllBytes(Paths.get(Platform.getLocation().toFile().toPath().toString()
						+ "/tp/FitNesseRoot/tp/CiSuite/properties.xml")), StandardCharsets.UTF_8).contains("<Suite/>"));
	}

	/**
	 * Tests the Adding of a TestScenario to an existing TestProject.
	 * 
	 * @throws Exception
	 *             on Test failure.
	 */
	@Test
	public void testAddTestScenarioToTestProject() throws Exception {
		TestProject testProject = createTestProjectsInWS();
		FitnesseFileSystemTestStructureService service = new FitnesseFileSystemTestStructureService();
		TestScenario tsc = new TestScenario();
		tsc.setName("Scenario");
		testProject.addChild(tsc);
		service.create(tsc);
		assertTrue(
				"Directory of TestScenario exists.",
				Files.exists(Paths.get(Platform.getLocation().toFile().toPath().toString()
						+ "/tp/FitNesseRoot/tp/Scenario")));
		assertTrue(
				"Content of TestScenario exists.",
				Files.exists(Paths.get(Platform.getLocation().toFile().toPath().toString()
						+ "/tp/FitNesseRoot/tp/Scenario/content.txt")));
		assertTrue(
				"Properties of TestScenario exists.",
				Files.exists(Paths.get(Platform.getLocation().toFile().toPath().toString()
						+ "/tp/FitNesseRoot/tp/Scenario/properties.xml")));
		assertTrue(
				"Peroperty is Testscenario",
				new String(Files.readAllBytes(Paths.get(Platform.getLocation().toFile().toPath().toString()
						+ "/tp/FitNesseRoot/tp/Scenario/properties.xml")), StandardCharsets.UTF_8)
						.contains("<TESTSCENARIO/>"));
	}

	/**
	 * Tests the Adding of a ScenarioSuite to an existing TestProject.
	 * 
	 * @throws Exception
	 *             on Test failure.
	 */
	@Test
	public void testAddScenarioSuiteToTestProject() throws Exception {
		TestProject testProject = createTestProjectsInWS();
		FitnesseFileSystemTestStructureService service = new FitnesseFileSystemTestStructureService();
		ScenarioSuite scs = new ScenarioSuite();
		scs.setName("ScenarioSuite");
		testProject.addChild(scs);
		service.create(scs);
		assertTrue(
				"Directory of ScenarioSuite exists.",
				Files.exists(Paths.get(Platform.getLocation().toFile().toPath().toString()
						+ "/tp/FitNesseRoot/tp/ScenarioSuite")));
		assertTrue(
				"Content of ScenarioSuite exists.",
				Files.exists(Paths.get(Platform.getLocation().toFile().toPath().toString()
						+ "/tp/FitNesseRoot/tp/ScenarioSuite/content.txt")));
		assertTrue(
				"Properties of ScenarioSuite exists.",
				Files.exists(Paths.get(Platform.getLocation().toFile().toPath().toString()
						+ "/tp/FitNesseRoot/tp/ScenarioSuite/properties.xml")));
		assertTrue(
				"Property Suites exitsts.",
				new String(Files.readAllBytes(Paths.get(Platform.getLocation().toFile().toPath().toString()
						+ "/tp/FitNesseRoot/tp/ScenarioSuite/properties.xml")), StandardCharsets.UTF_8)
						.contains("<Suites/>"));
	}

	/**
	 * Tests the Adding of a TestScenario to an existing ScenarioSuite.
	 * 
	 * @throws Exception
	 *             on Test failure.
	 */
	@Test
	public void testAddTestScenarioToScenarioSuite() throws Exception {
		TestProject testProject = createTestProjectsInWS();
		FitnesseFileSystemTestStructureService service = new FitnesseFileSystemTestStructureService();
		ScenarioSuite scs = new ScenarioSuite();
		scs.setName("ScenarioSuite");
		testProject.addChild(scs);
		service.create(scs);
		TestScenario tsc = new TestScenario();
		tsc.setName("Scenario");
		scs.addChild(tsc);
		service.create(tsc);
		assertTrue(
				"Directory of Testcase exists.",
				Files.exists(Paths.get(Platform.getLocation().toFile().toPath().toString()
						+ "/tp/FitNesseRoot/tp/ScenarioSuite/Scenario")));
		assertTrue(
				"Properties of Testcase exists.",
				Files.exists(Paths.get(Platform.getLocation().toFile().toPath().toString()
						+ "/tp/FitNesseRoot/tp/ScenarioSuite/Scenario/properties.xml")));
		assertTrue(new String(Files.readAllBytes(Paths.get(Platform.getLocation().toFile().toPath().toString()
				+ "/tp/FitNesseRoot/tp/ScenarioSuite/Scenario/properties.xml")), StandardCharsets.UTF_8)
				.contains("<TESTSCENARIO/>"));
	}

	/**
	 * Integrationtest to add and load a TestTRee.
	 * 
	 * @throws Exception
	 *             on Test failure.
	 */
	@Test
	public void testIntegrationOfAddAndReloadOfTestStructures() throws Exception {
		TestProject testProject = createTestProjectsInWS();
		FitnesseFileSystemTestStructureService service = new FitnesseFileSystemTestStructureService();
		TestSuite suite = new TestSuite();
		suite.setName("MySuite");
		TestCase tc1 = new TestCase();
		tc1.setName("MyTest");
		suite.addChild(tc1);
		testProject.addChild(suite);
		TestCase tc2 = new TestCase();
		tc2.setName("SecondTest");
		testProject.addChild(tc2);
		service.create(tc2);
		service.create(suite);
		service.create(tc1);
		TestProject tp = new TestProject();
		tp.setName("tp");
		service.loadChildrenInto(tp);
		assertTrue(tp.getAllTestChildren().contains(tc2));
		assertTrue(tp.getAllTestChildren().contains(tc1));
		assertTrue(tp.getAllTestChildren().contains(suite));
	}

	/**
	 * Test the check for reserved names.
	 */
	@Test
	public void testIsReservedName() {
		FitnesseFileSystemTestStructureService service = new FitnesseFileSystemTestStructureService();
		assertTrue(service.isReservedName("SetUp"));
		assertTrue(service.isReservedName("TearDown"));
		assertFalse(service.isReservedName("MyTestCase"));
	}

	/**
	 * Tests the building of the Result link of a teststructure.
	 */
	@Test
	public void testGetResultLink() {
		FitnesseFileSystemTestStructureService service = new FitnesseFileSystemTestStructureService();
		String link = service.getResultLink("MyTestProject.TestSuite.TestCase", "20141010122334");
		assertEquals("Expecting link suffix to the test server.",
				"MyTestProject.TestSuite.TestCase?pageHistory&resultDate=20141010122334", link);
	}

	/**
	 * Test the loading of the test execution history of a teststructure.
	 * 
	 * @throws Exception
	 *             on loading tests.
	 */
	@Test
	public void testGetTestHistory() throws Exception {
		FitnesseFileSystemTestStructureService service = new FitnesseFileSystemTestStructureService();
		TestProject testProject = createTestProjectsInWS();
		List<TestResult> testHistory = service.getTestHistory(testProject.getTestChildByFullName("tp.tc"));
		assertNotNull(testHistory);
		assertEquals(3, testHistory.size());
	}

	/**
	 * compares the given order in List.
	 * 
	 * @param testResults
	 *            List<TestResult>
	 * @param strs
	 *            referenz list
	 */
	private void compare(List<TestResult> testResults, String[] strs) {

		assertTrue("Arrays not the same length", testResults.size() == strs.length);

		int i = 0;

		for (TestResult testResult : testResults) {
			assertEquals(testResult.getResultDate().toString(), strs[i++]);
		}

	}

	/**
	 * Checks the correct sort order test history.
	 * 
	 * @throws Exception
	 *             on loading tests.
	 */
	@Test
	public void testCheckSortOrderFromTestHistory() throws Exception {
		FitnesseFileSystemTestStructureService service = new FitnesseFileSystemTestStructureService();
		TestProject testProject = createTestProjectsInWS();
		List<TestResult> testHistory = service.getTestHistory(testProject.getTestChildByFullName("tp.tc"));
		assertNotNull(testHistory);
		assertEquals(3, testHistory.size());

		compare(testHistory, new String[] { "Sun Oct 19 13:55:03 CEST 2014", "Sat Oct 18 13:55:03 CEST 2014",
				"Fri Oct 17 22:13:15 CEST 2014" });

	}

	/**
	 * Test deleting the history of a testcase.
	 * 
	 * @throws Exception
	 *             on io error.
	 */
	@Test
	public void testClearTestHistory() throws Exception {
		FitnesseFileSystemTestStructureService service = new FitnesseFileSystemTestStructureService();
		TestProject testProject = createTestProjectsInWS();
		TestStructure testStructure = testProject.getTestChildByFullName("tp.tc");
		assertTrue(new File(service.getPathToTestResults(testStructure)).exists());
		service.clearTestHistory(testStructure);
		assertFalse(new File(service.getPathToTestResults(testStructure)).exists());
	}

	/**
	 * Tests receiving a runnable.
	 * 
	 * @throws Exception
	 *             on loading tests.
	 */
	@Test
	public void testGetRunnableForLazyLoading() throws Exception {
		FitnesseFileSystemTestStructureService service = new FitnesseFileSystemTestStructureService();
		URL url = FileLocator.toFileURL(this.getClass().getResource("/history/ts_properties.xml"));
		TestCompositeStructure testStructure = (TestCompositeStructure) service.createTestStructureFrom(new File(url
				.toURI()));
		Runnable runnable = service.getTestProjectLazyLoader(testStructure);
		assertNotNull(runnable);
	}

}
