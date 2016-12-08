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
package org.testeditor.fitnesse.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.model.teststructure.BrokenTestStructure;
import org.testeditor.core.model.teststructure.TestActionGroup;
import org.testeditor.core.model.teststructure.TestCase;
import org.testeditor.core.model.teststructure.TestComponent;
import org.testeditor.core.model.teststructure.TestDescription;
import org.testeditor.core.model.teststructure.TestFlow;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestProjectConfig;
import org.testeditor.core.model.teststructure.TestScenario;
import org.testeditor.core.model.teststructure.TestScenarioParameterTable;
import org.testeditor.core.model.teststructure.TestScenarioParameters;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.model.teststructure.TestSuite;
import org.testeditor.core.services.interfaces.ActionGroupService;
import org.testeditor.core.services.interfaces.LibraryReaderService;
import org.testeditor.core.services.interfaces.ServiceLookUpForTest;
import org.testeditor.core.services.interfaces.TestProjectService;
import org.testeditor.core.services.interfaces.TestScenarioService;
import org.testeditor.core.services.interfaces.TestStructureContentService;
import org.testeditor.fitnesse.TestProjectDataFactory;

/**
 * Tests the WikiParser.
 */
public class FitNesseWikiParserTest {

	private TestFlow firstTestCase;
	private static final String SCENARIO_FINAL = "'''End Scenario Include: ";

	/**
	 * initialization before the tests.
	 */
	@Before
	public void initializeTestProjekt() {
		firstTestCase = (TestFlow) TestProjectDataFactory.createTestProjectForFitnesseTests().getTestChildren().get(0);
	}

	/**
	 * Tests the parsing of a simple description-string content.
	 * "''Starte Browser''' --------" parser should define this as a description
	 * 
	 * @throws SystemException
	 *             is thrown in case of IO or connection exceptions
	 */
	@Test
	public void parseSimpleDiscriptionString() throws SystemException {
		final String content = "''Starte Browser''' --------";
		final String exptectedContent = "''Starte Browser\n";

		FitNesseWikiParser parser = getOUT();
		List<TestComponent> components = parser.parse(firstTestCase, content);

		TestComponent comp = components.get(0);
		assertTrue(comp instanceof TestDescription);
		assertTrue(((TestDescription) comp).getDescription().contentEquals("''Starte Browser"));

		compareComponentsToSource(components, "TestFall", exptectedContent);
	}

	/**
	 * Tests the parsing of a simple testAktionHeader-string content.
	 * "-!|script|" parser should define this as a TestActionGroup without a
	 * header
	 * 
	 * @throws SystemException
	 *             is thrown in case of IO or connection exceptions
	 */
	@Test
	public void parseSimpleTestaktionStringTwo() throws SystemException {
		final String content = "# Maske: Willkommen\n-!|script|\n|Start|\n";

		FitNesseWikiParser parser = getOUT();
		List<TestComponent> components = parser.parse(firstTestCase, content);

		TestComponent comp = components.get(0);
		assertTrue(comp instanceof TestActionGroup);
		assertTrue(((TestActionGroup) comp).getActionGroupName().contentEquals("Willkommen"));

		compareComponentsToSource(components, "TestFall", content);
	}

	/**
	 * Tests the parsing of a simple testAktionHeader-string content.
	 * -!|script|org.testeditor.fitnesse.fixture.WebFixture| |open
	 * browser|firefox|" parser should define this as a TestActionGroup with the
	 * header "org.testeditor.fitnesse.fixture.WebFixture"
	 * 
	 * @throws SystemException
	 *             is thrown in case of IO or connection exceptions
	 */
	@Test
	public void parseSimpleTestaktionStringThree() throws SystemException {
		final String content = "# Maske: Browser\n-!|script|\n|starte Browser|Firefox|\n";

		FitNesseWikiParser parser = getOUT();
		List<TestComponent> components = parser.parse(firstTestCase, content);

		TestComponent comp = components.get(0);
		assertTrue(comp instanceof TestActionGroup);
		TestActionGroup testAcGr = (TestActionGroup) comp;
		assertTrue(testAcGr.getActionGroupName().equalsIgnoreCase("Browser"));
		List<String> texts = testAcGr.getTexts();
		assertTrue(texts.get(0).contains("starte Browser"));

		compareComponentsToSource(components, "TestFall", content);
	}

	/**
	 * Tests the parsing of a ScenarioParameterString content. !|scenario
	 * |LoginValidationSzenario _|Passwort, TextVorhanden, Name,
	 * TextNichtVorhanden| shpuld give the parameters Passwort, TextVorhanden,
	 * Name, TextNichtVorhanden
	 * 
	 * @throws SystemException
	 *             is thrown in case of IO or connection exceptions
	 */
	@Test
	public void parseScenario() throws SystemException {
		final String content = "!|scenario |LoginValidationSzenario _||\n|note|Description: ﻿Dieses Szenario überprüft den Login auf der Starseite.|\n";

		FitNesseWikiParser parser = getOUT();
		TestScenario testScenario = new TestScenario();
		LinkedList<TestComponent> components = parser.parse(testScenario, content);
		testScenario.setTestComponents(components);
		assertTrue(components.get(0) instanceof TestScenarioParameters);

		compareComponentsToSource(components, "LoginValidationSzenario", content);
	}

	/**
	 * Tests the parsing of a ScenarioParameterString content. !|scenario
	 * |LoginValidationSzenario _|Passwort, TextVorhanden, Name,
	 * TextNichtVorhanden| shpuld give the parameters Passwort, TextVorhanden,
	 * Name, TextNichtVorhanden
	 * 
	 * @throws SystemException
	 *             is thrown in case of IO or connection exceptions
	 */
	@Test
	public void parseScenarioParameterString() throws SystemException {
		final String content = "!|scenario |LoginValidationSzenario _|Passwort, TextVorhanden, Name, TextNichtVorhanden|\n|note|Description: ﻿Dieses Szenario überprüft den Login auf der Starseite.''' --------|";
		final String expectedContent = "!|scenario |LoginValidationSzenario _|Passwort, TextVorhanden, Name, TextNichtVorhanden|\n|note|Description: ﻿Dieses Szenario überprüft den Login auf der Starseite.|\n";

		FitNesseWikiParser parser = getOUT();
		TestScenario testScenario = new TestScenario();
		LinkedList<TestComponent> components = parser.parse(testScenario, content);
		testScenario.setTestComponents(components);
		assertTrue(components.get(0) instanceof TestScenarioParameters);
		assertEquals("TextVorhanden", testScenario.getTestParameters().get(1));

		compareComponentsToSource(components, "LoginValidationSzenario", expectedContent);
	}

	/**
	 * Tests the parsing of a simple Scenario Statement.
	 * 
	 * @throws SystemException
	 *             is thrown in case of IO or connection exceptions
	 */
	@Test
	public void parseSimpleScenarioStatement() throws SystemException {
		final String content = "!include <DemoWebTests.TestKomponenten.OeffneSeiteTesteditor\n!|script|\n|Oeffne Seite Testeditor|\n"
				+ SCENARIO_FINAL + "Oeffne Seite Testeditor''' --------";
		final String contentExpected = "!include <DemoWebTests.TestKomponenten.OeffneSeiteTesteditor\n!|script|\n|Oeffne Seite Testeditor|\n#\n";

		FitNesseWikiParser parser = getOUT();
		List<TestComponent> components = parser.parse(firstTestCase, content);

		TestComponent comp = components.get(0);
		assertTrue(comp instanceof TestScenarioParameterTable);
		TestScenarioParameterTable testScenarioParamTable = (TestScenarioParameterTable) comp;
		assertEquals("Oeffne Seite Testeditor", testScenarioParamTable.getTitle());
		assertTrue(testScenarioParamTable.isSimpleScriptStatement());

		compareComponentsToSource(components, "TestFall", contentExpected);
	}

	/**
	 * Tests the parsing of a simple Scenario Statement.
	 * 
	 * @throws SystemException
	 *             is thrown in case of IO or connection exceptions
	 */
	@Test
	@Ignore
	public void parseScenarioWithScenarioCallWithParameters() throws SystemException {
		final StringBuffer content = new StringBuffer(
				"!include <DemoWebTests.TestSzenarien.TomatenSaft.ApplikationStopSzenario\n");
		content.append("\n");
		content.append("!|scenario |SzenarioSzenario _||\n");
		content.append("|note|scenario|\n");
		content.append("|ApplikationStopSzenario;|test|\n");

		FitNesseWikiParser parser = getOUT();
		TestScenario testScenario = new TestScenario();
		testScenario.setParent(firstTestCase.getParent());
		List<TestComponent> components = parser.parse(testScenario, content.toString());

		TestComponent comp = components.get(0);
		assertTrue(comp instanceof TestScenarioParameterTable);
		TestScenarioParameterTable testScenarioParamTable = (TestScenarioParameterTable) comp;
		assertEquals("SzenarioSzenario", testScenarioParamTable.getTitle());
		assertTrue(testScenarioParamTable.isSimpleScriptStatement());

	}

	/**
	 * 
	 * @return the object under test created with eclipse context.
	 */
	private FitNesseWikiParser getOUT() {
		IEclipseContext context = getEclipseContext();
		return ContextInjectionFactory.make(FitNesseWikiParser.class, context);
	}

	/**
	 * 
	 * @return EclipseContext to build the out.
	 */
	private IEclipseContext getEclipseContext() {
		IEclipseContext context = EclipseContextFactory.create();
		context.set(LibraryReaderService.class, ServiceLookUpForTest.getService(LibraryReaderService.class));
		context.set(TestScenarioService.class, getScenarioServiceMock());
		context.set(TestProjectService.class, ServiceLookUpForTest.getService(TestProjectService.class));
		context.set(ActionGroupService.class, ServiceLookUpForTest.getService(ActionGroupService.class));
		context.set(TestStructureContentService.class,
				ServiceLookUpForTest.getService(TestStructureContentService.class));
		// context.set(TestStructureContentService.class, new
		// TestStructureContentServiceAdapter());
		return context;
	}

	/**
	 * 
	 * @return a special TestScenarioServiceMock
	 */
	private TestScenarioService getScenarioServiceMock() {
		return new TestScenarioService() {

			@Override
			public void readTestScenario(TestScenario testScenario, String testStructureText) throws SystemException {

			}

			@Override
			public boolean isSuiteForScenarios(TestStructure element) {
				return false;
			}

			@Override
			public boolean isLinkToScenario(TestProject testProject, String linkToFile) throws SystemException {
				return false;
			}

			@Override
			public boolean isDescendantFromTestScenariosSuite(TestStructure testStructure) {
				return false;
			}

			@Override
			public List<String> getUsedOfTestSceneario(TestScenario testScenario) {
				return null;
			}

			@Override
			public TestScenario getScenarioByFullName(TestProject testProject, String includeOfScenario)
					throws SystemException {
				TestScenario testScenario = new TestScenario();
				testScenario.setName("MeinSzenario");
				testScenario.addTestparameter("key");
				if (includeOfScenario.contains("DemoWebTests.TestSzenarien.MeinSzenario")) {
					return testScenario;
				}
				return new TestScenario();
			}

		};
	}

	/**
	 * tests the parsing of a complex ScenarioInclude.
	 * 
	 * @throws SystemException
	 *             is thrown in case of IO or connection exceptions
	 */
	@Test
	public void parseComplexScenarioIncludeShortestVersion() throws SystemException {
		StringBuilder content = new StringBuilder("!include <DemoWebTests.TestKomponenten.LoginSzenario\n");
		content.append("!|Login Szenario|\n");
		content.append("|page|name|pwd|prueftext|prueftext_zwei_nicht_vorhanden|\n");
		content.append(SCENARIO_FINAL + "Login Szenario''' --------\n");

		FitNesseWikiParser parser = getOUT();
		List<TestComponent> components = parser.parse(firstTestCase, content.toString());

		TestComponent comp = components.get(0);
		assertTrue(comp instanceof TestScenarioParameterTable);
		TestScenarioParameterTable testScenarioParamTable = (TestScenarioParameterTable) comp;
		assertEquals("Login Szenario", testScenarioParamTable.getTitle());
		assertFalse(testScenarioParamTable.isSimpleScriptStatement());
	}

	/**
	 * tests the parsing of a complex ScenarioInclude.
	 * 
	 * @throws SystemException
	 *             is thrown in case of IO or connection exceptions
	 */
	@Test
	public void parseComplexScenarioInclude() throws SystemException {
		StringBuilder content = getScenarioContentMock();
		content.append(SCENARIO_FINAL + "Login Szenario''' --------\n");

		FitNesseWikiParser parser = getOUT();
		List<TestComponent> components = parser.parse(firstTestCase, content.toString());

		TestComponent comp = components.get(0);
		assertTrue(comp instanceof TestScenarioParameterTable);
		TestScenarioParameterTable testScenarioParamTable = (TestScenarioParameterTable) comp;
		assertEquals("Login Szenario", testScenarioParamTable.getTitle());
		assertFalse(testScenarioParamTable.isSimpleScriptStatement());

		assertEquals(" Max Mustermann", testScenarioParamTable.getDataTable().getRows().get(1).getColumn(1));
	}

	/**
	 * tests the parsing of a complex ScenarioInclude with a new mask as the end
	 * of the scenario include.
	 * 
	 * @throws SystemException
	 *             is thrown in case of IO or connection exceptions
	 */
	@Test
	public void parseComplexScenarioIncludeWithANewMaskAsEndOfScenario() throws SystemException {
		StringBuilder content = getScenarioContentMock();
		content.append("# Maske: Browser\n-!|script|\n|starte Browser|firefox|\n");

		FitNesseWikiParser parser = getOUT();
		List<TestComponent> components = parser.parse(firstTestCase, content.toString());

		TestComponent comp = components.get(0);
		assertTrue(comp instanceof TestScenarioParameterTable);
		TestScenarioParameterTable testScenarioParamTable = (TestScenarioParameterTable) comp;
		assertEquals("Login Szenario", testScenarioParamTable.getTitle());
		assertFalse(testScenarioParamTable.isSimpleScriptStatement());

		assertEquals(" Max Mustermann", testScenarioParamTable.getDataTable().getRows().get(1).getColumn(1));
	}

	/**
	 * test the parsing of a complex ScenarioInclude with a new description as
	 * the end of the scenario include.
	 * 
	 * @throws SystemException
	 *             is thrown in case of IO or connection exceptions
	 */
	@Test
	public void parseComplexScenarioIncludeANewDescriptiomAsEndOfScenario() throws SystemException {
		StringBuilder content = getScenarioContentMock();
		content.append("''' eine Beschreibung ''' --------|\n");

		FitNesseWikiParser parser = getOUT();
		List<TestComponent> components = parser.parse(firstTestCase, content.toString());

		TestComponent comp = components.get(0);
		assertTrue(comp instanceof TestScenarioParameterTable);
		TestScenarioParameterTable testScenarioParamTable = (TestScenarioParameterTable) comp;
		assertEquals("Login Szenario", testScenarioParamTable.getTitle());
		assertFalse(testScenarioParamTable.isSimpleScriptStatement());

		assertEquals(" Max Mustermann", testScenarioParamTable.getDataTable().getRows().get(1).getColumn(1));
	}

	/**
	 * test the parsing of a complex ScenarioInclude with a new description as
	 * the end of the scenario include.
	 * 
	 * @throws SystemException
	 *             is thrown in case of IO or connection exceptions
	 */
	@Test
	public void parseComplexScenarioIncludeANewDescriptionBeginninWithNoteAsEndOfScenario() throws SystemException {
		StringBuilder content = getScenarioContentMock();
		content.append("|note|Description: |\n");

		FitNesseWikiParser parser = getOUT();
		List<TestComponent> components = parser.parse(firstTestCase, content.toString());

		TestComponent comp = components.get(0);
		assertTrue(comp instanceof TestScenarioParameterTable);
		TestScenarioParameterTable testScenarioParamTable = (TestScenarioParameterTable) comp;
		assertEquals("Login Szenario", testScenarioParamTable.getTitle());
		assertFalse(testScenarioParamTable.isSimpleScriptStatement());

		assertEquals(" Max Mustermann", testScenarioParamTable.getDataTable().getRows().get(1).getColumn(1));
	}

	/**
	 * 
	 * @return a content for the scenario for the different tests.
	 */
	private StringBuilder getScenarioContentMock() {
		StringBuilder content = new StringBuilder("!include <DemoWebTests.TestKomponenten.LoginSzenario\n");
		content.append("!|Login Szenario|\n");
		content.append("|page|name|pwd|prueftext|prueftext_zwei_nicht_vorhanden|\n");
		content.append(
				"|http://localhost:8060/files/demo/ExampleApplication/WebApplicationDe/index.html| Max Mustermann| test| Anmeldung war erfolgreich| Login|\n");
		content.append(
				"|http://localhost:8060/files/demo/ExampleApplication/WebApplicationDe/index.html| Emil Mustermann| test| Login| Anmeldung war erfolgreich|\n");
		return content;
	}

	/**
	 * test the method nextLineIsEndOfParameterTable.
	 * 
	 * @throws SystemException
	 *             is thrown in case of IO or connection exceptions
	 */
	@Test
	public void nextLineIsEndOfParameterTableTest() throws SystemException {
		StringBuilder content = getScenarioContentMock();
		content.append("\n");
		content.append("# Maske: Browser\n-!|script|\n|starte Browser|firefox|\n");

		FitNesseWikiParser parser = getOUT();
		List<TestComponent> components = parser.parse(firstTestCase, content.toString());

		TestComponent comp = components.get(0);
		assertTrue(comp instanceof TestScenarioParameterTable);
		TestScenarioParameterTable testScenarioParamTable = (TestScenarioParameterTable) comp;
		assertEquals("Login Szenario", testScenarioParamTable.getTitle());
		assertEquals(3, testScenarioParamTable.getDataTable().getRows().size());

	}

	/**
	 * test the method nextLineIsEndOfParameterTable. end is a new description
	 * 
	 * @throws SystemException
	 *             is thrown in case of IO or connection exceptions
	 */
	@Test
	public void nextLineIsEndOfParameterTableTestNewDescription() throws SystemException {
		StringBuilder content = getScenarioContentMock();
		content.append("'''Beschreibung''' --------|");

		FitNesseWikiParser parser = getOUT();
		List<TestComponent> components = parser.parse(firstTestCase, content.toString());

		TestComponent comp = components.get(0);
		assertTrue(comp instanceof TestScenarioParameterTable);
		TestScenarioParameterTable testScenarioParamTable = (TestScenarioParameterTable) comp;
		assertEquals("Login Szenario", testScenarioParamTable.getTitle());
		assertEquals(3, testScenarioParamTable.getDataTable().getRows().size());

	}

	/**
	 * test the method nextLineIsEndOfParameterTable. end is a new description
	 * 
	 * @throws SystemException
	 *             is thrown in case of IO or connection exceptions
	 */
	@Test
	public void nextLineIsEndOfParameterTableTestOtherDescription() throws SystemException {
		StringBuilder content = getScenarioContentMock();
		content.append("|note|Description: beschreibung|");

		FitNesseWikiParser parser = getOUT();
		List<TestComponent> components = parser.parse(firstTestCase, content.toString());

		TestComponent comp = components.get(0);
		assertTrue(comp instanceof TestScenarioParameterTable);
		TestScenarioParameterTable testScenarioParamTable = (TestScenarioParameterTable) comp;
		assertEquals("Login Szenario", testScenarioParamTable.getTitle());
		assertEquals(3, testScenarioParamTable.getDataTable().getRows().size());

	}

	/**
	 * test the method nextLineIsEndOfParameterTable.
	 * 
	 * @throws SystemException
	 *             is thrown in case of IO or connection exceptions
	 */
	@Test
	public void nextLineIsEndOfParameterTableTestNewAction() throws SystemException {
		StringBuilder content = getScenarioContentMock();
		content.append("# Maske: Browser\n-!|script|\n|starte Browser|firefox|\n");

		FitNesseWikiParser parser = getOUT();
		List<TestComponent> components = parser.parse(firstTestCase, content.toString());

		TestComponent comp = components.get(0);
		assertTrue(comp instanceof TestScenarioParameterTable);
		TestScenarioParameterTable testScenarioParamTable = (TestScenarioParameterTable) comp;
		assertEquals("Login Szenario", testScenarioParamTable.getTitle());
		assertEquals(3, testScenarioParamTable.getDataTable().getRows().size());

	}

	/**
	 * test the method nextLineIsEndOfParameterTable.
	 * 
	 * @throws SystemException
	 *             is thrown in case of IO or connection exceptions
	 */
	@Test
	public void nextLineIsEndOfParameterTableTestStar() throws SystemException {
		StringBuilder content = getScenarioContentMock();
		content.append("*");

		FitNesseWikiParser parser = getOUT();
		List<TestComponent> components = parser.parse(firstTestCase, content.toString());

		TestComponent comp = components.get(0);
		assertTrue(comp instanceof TestScenarioParameterTable);
		TestScenarioParameterTable testScenarioParamTable = (TestScenarioParameterTable) comp;
		assertEquals("Login Szenario", testScenarioParamTable.getTitle());
		assertEquals(3, testScenarioParamTable.getDataTable().getRows().size());

	}

	/**
	 * test the method nextLineIsEndOfParameterTable.
	 * 
	 * @throws SystemException
	 *             is thrown in case of IO or connection exceptions
	 */
	@Test
	public void nextLineIsEndOfParameterTableTestHash() throws SystemException {
		StringBuilder content = getScenarioContentMock();
		content.append("#");

		FitNesseWikiParser parser = getOUT();
		List<TestComponent> components = parser.parse(firstTestCase, content.toString());

		TestComponent comp = components.get(0);
		assertTrue(comp instanceof TestScenarioParameterTable);
		TestScenarioParameterTable testScenarioParamTable = (TestScenarioParameterTable) comp;
		assertEquals("Login Szenario", testScenarioParamTable.getTitle());
		assertEquals(3, testScenarioParamTable.getDataTable().getRows().size());

	}

	/**
	 * Tests that referred TestCases are extracted form the FitNesse TestSuite
	 * Content.
	 */
	@Test
	public void testParseReferredTestCaesInTestSuite() {
		FitNesseWikiParser parser = getOUT();
		String content = "!content\n" + "Some Text\n" + "!see .MyPrj.aTS.MyTestCase\n" + "Some Other text\n";
		TestSuite testSuite = new TestSuite();
		TestProject testProject = new TestProject();
		testProject.addChild(testSuite);
		TestSuite suite = new TestSuite();
		suite.setName("aTS");
		testProject.addChild(suite);
		testProject.setName("MyPrj");
		TestCase testCase = new TestCase();
		testCase.setName("MyTestCase");
		suite.addChild(testCase);
		List<TestStructure> testCases = parser.parseReferredTestCases(testSuite, content);
		assertEquals("Expect 1 TestStructures in List", 1, testCases.size());
		assertSame("Expecting same Project", testProject, testCases.get(0).getRootElement());
		assertEquals("Expecting name", "MyTestCase", testCases.get(0).getName());
	}

	/**
	 * Tests that referred TestCases are extracted form the FitNesse TestSuite
	 * Content.
	 */
	@Test
	public void testParseDeletedReferredTestCaesInTestSuite() {
		FitNesseWikiParser parser = getOUT();
		String content = "!content\n" + "Some Text\n" + "!see .MyPrj1.aTS1.MyTestCase\n" + "Some Other text\n";
		TestSuite testSuite = new TestSuite();
		TestProject testProject = new TestProject();
		testProject.addChild(testSuite);
		TestSuite suite = new TestSuite();
		suite.setName("aTS1");
		testProject.addChild(suite);
		testProject.setName("MyPrj1");
		TestCase testCase = new TestCase();
		testCase.setName("MyTestCase2");
		suite.addChild(testCase);
		List<TestStructure> testCases = parser.parseReferredTestCases(testSuite, content);
		assertEquals("Expect 1 TestStructures in List", 1, testCases.size());
		assertTrue("Expecting Broken Testcase", testCases.get(0) instanceof BrokenTestStructure);
	}

	/**
	 * this test checks the parsing of a scenario with a linked scenario inside.
	 * 
	 * @throws SystemException
	 *             while parsing
	 */
	@Test
	@Ignore
	public void testParseScenarioInScenario() throws SystemException {
		FitNesseWikiParser parser = getOUT();
		StringBuilder content = new StringBuilder("!include <DemoWebTests.TestSzenarien.MeinSzenario\n");
		content.append("!include <DemoWebTests.TestSzenarien.ApplikationStartSzenario\n");
		content.append("!|scenario |MeinScenarioSzenario _||\n");
		content.append("|note|scenario|\n");
		content.append("|MeinSzenario|X fuer' n U|\n");
		content.append("|note|Description: Hier noch schnell eine Beschreibung|\n");
		content.append("|note|scenario|\n");
		content.append("|ApplikationStartSzenario|\n");
		content.append("|note|Description: Hier noch eine zweite Beschreibung|\n");
		content.append("|note| Maske: Willkommen|\n");
		TestSuite testSuite = new TestSuite();
		TestProject testProject = new TestProject();
		TestProjectConfig testProjectConfig = new TestProjectConfig();
		testProjectConfig.setTestServerID("foo");
		testProject.setTestProjectConfig(testProjectConfig);
		testProject.addChild(testSuite);
		TestSuite suite = new TestSuite();
		suite.setName("aTS1");
		testProject.addChild(suite);
		testProject.setName("MyPrj1");
		TestScenario testScenario = new TestScenario();
		testScenario.setName("MyTestScenario");
		suite.addChild(testScenario);

		List<TestComponent> testComponents = parser.parse(testScenario, content.toString());

		assertEquals("Expect 6 TestComponent in List", 6, testComponents.size());
		TestComponent testComponent = testComponents.get(1);
		assertTrue(testComponent instanceof TestScenarioParameterTable);
		String title = ((TestScenarioParameterTable) testComponent).getTitle();
		if (title.length() > 0) {
			assertEquals("Mein Szenario", title);
		}
		assertEquals(1, ((TestScenarioParameterTable) testComponent).getDataTable().getDataRows().size());
		assertTrue(((TestScenarioParameterTable) testComponent).getDataTable().getDataRows().get(0).toString()
				.startsWith("X fuer' n U|"));
		testComponent = testComponents.get(2);
		assertTrue(testComponent instanceof TestDescription);
		assertEquals("|note|Description: Hier noch schnell eine Beschreibung|",
				testComponent.getSourceCode().toString());
		testComponent = testComponents.get(5);
		assertTrue(((TestActionGroup) testComponent).getActionGroupName().contentEquals("Willkommen"));
	}

	private void compareComponentsToSource(List<TestComponent> components, String name, String content) {
		TestCase testCase = new TestCase();
		testCase.setName(name);
		testCase.setTestComponents(components);

		assertEquals(content, testCase.getSourceCode());

	}
}
