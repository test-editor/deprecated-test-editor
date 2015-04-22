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

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.model.action.ProjectLibraryConfig;
import org.testeditor.core.model.team.TeamShareConfig;
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
import org.testeditor.core.services.interfaces.LibraryConfigurationService;
import org.testeditor.core.services.interfaces.LibraryDataStoreService;
import org.testeditor.core.services.interfaces.LibraryReaderService;
import org.testeditor.core.services.interfaces.ServiceLookUpForTest;
import org.testeditor.core.services.interfaces.TeamShareConfigurationService;
import org.testeditor.core.services.interfaces.TeamShareService;
import org.testeditor.core.services.interfaces.TestEditorPlugInService;
import org.testeditor.core.services.interfaces.TestProjectService;
import org.testeditor.core.services.interfaces.TestScenarioService;
import org.testeditor.core.services.interfaces.TestStructureContentService;
import org.testeditor.core.services.interfaces.TestStructureService;
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

		FitNesseWikiParser parser = getOUT();
		List<TestComponent> components = parser.parse(firstTestCase, content);

		TestComponent comp = components.get(0);
		assertTrue(comp instanceof TestDescription);
		assertTrue(((TestDescription) comp).getDescription().contentEquals("''Starte Browser"));
	}

	/**
	 * Tests the parsing of a simple testAktionHeader-string content.
	 * "-!|script|org.testeditor.fitnesse.fixture.WebFixture|" parser should
	 * define this as a TestActionGroup with the header
	 * "org.testeditor.fitnesse.fixture.WebFixture"
	 * 
	 * @throws SystemException
	 *             is thrown in case of IO or connection exceptions
	 */
	@Test
	public void parseSimpleTestaktionString() throws SystemException {
		final String content = "# Maske: Willkommen\n-!|script|org.testeditor.fitnesse.fixture.WebFixture|";

		FitNesseWikiParser parser = getOUT();
		List<TestComponent> components = parser.parse(firstTestCase, content);

		TestComponent comp = components.get(0);
		assertTrue(comp instanceof TestActionGroup);
		assertTrue(((TestActionGroup) comp).getInvisibelActionLines().get(0)
				.contentEquals("-!|script|org.testeditor.fitnesse.fixture.WebFixture|"));
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
		final String content = "# Maske: Willkommen\n-!|script|";

		FitNesseWikiParser parser = getOUT();
		List<TestComponent> components = parser.parse(firstTestCase, content);

		TestComponent comp = components.get(0);
		assertTrue(comp instanceof TestActionGroup);
		assertTrue(((TestActionGroup) comp).getActionGroupName().contentEquals("Willkommen"));
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
	@Ignore
	@Test
	public void parseSimpleTestaktionStringThree() throws SystemException {
		final String content = "# Maske: Browser\n-!|script|\n|starte Browser|firefox|\n";

		FitNesseWikiParser parser = getOUT();
		List<TestComponent> components = parser.parse(firstTestCase, content);

		TestComponent comp = components.get(0);
		assertTrue(comp instanceof TestActionGroup);
		TestActionGroup testAcGr = (TestActionGroup) comp;
		assertTrue(testAcGr.getActionGroupName().equalsIgnoreCase("Browser"));
		ArrayList<String> texts = testAcGr.getTexts();
		// System.err.println(texts.size());
		// System.err.println(texts.get(0));
		assertEquals("starte Browser ", texts.get(0));

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

		FitNesseWikiParser parser = getOUT();
		TestScenario testScenario = new TestScenario();
		LinkedList<TestComponent> components = parser.parse(testScenario, content);
		testScenario.setTestComponents(components);
		assertTrue(components.get(0) instanceof TestScenarioParameters);
		assertEquals("TextVorhanden", testScenario.getTestParameters().get(1));

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

		FitNesseWikiParser parser = getOUT();
		List<TestComponent> components = parser.parse(firstTestCase, content);

		TestComponent comp = components.get(0);
		assertTrue(comp instanceof TestScenarioParameterTable);
		TestScenarioParameterTable testScenarioParamTable = (TestScenarioParameterTable) comp;
		assertEquals("Oeffne Seite Testeditor", testScenarioParamTable.getTitle());
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
		context.set(LibraryDataStoreService.class, ServiceLookUpForTest.getService(LibraryDataStoreService.class));
		context.set(LibraryReaderService.class, ServiceLookUpForTest.getService(LibraryReaderService.class));
		context.set(TestEditorPlugInService.class, getTestEditorPluginService());
		context.set(TestProjectService.class, ServiceLookUpForTest.getService(TestProjectService.class));
		context.set(ActionGroupService.class, ServiceLookUpForTest.getService(ActionGroupService.class));
		return context;
	}

	/**
	 * Mock object of the Plug-In Service.
	 * 
	 * @return the Mock of the Plugin-In service.
	 */
	private TestEditorPlugInService getTestEditorPluginService() {
		return new TestEditorPlugInService() {

			@Override
			public TestStructureService getTestStructureServiceFor(String testServerID) {
				return null;
			}

			@Override
			public TestStructureContentService getTestStructureContentServiceFor(String testServerID) {
				return null;
			}

			@Override
			public TestScenarioService getTestScenarioService(String testServerID) {
				return getScenarioServiceMock();
			}

			@Override
			public TeamShareService getTeamShareServiceFor(String id) {
				return null;
			}

			@Override
			public TeamShareConfigurationService getTeamShareConfigurationServiceFor(String id) {
				return null;
			}

			@Override
			public LibraryConfigurationService getLibraryConfigurationServiceFor(String id) {
				return null;
			}

			@Override
			public Map<String, String> getAsProperties(TeamShareConfig teamShareConfig) {
				return null;
			}

			@Override
			public Map<String, String> getAsProperties(ProjectLibraryConfig projectLibraryConfig) {
				return null;
			}

			@Override
			public Collection<TeamShareService> getAllTeamShareServices() {
				return null;
			}

			@Override
			public Collection<TeamShareConfigurationService> getAllTeamShareConfigurationServices() {
				return null;
			}

			@Override
			public Collection<LibraryConfigurationService> getAllLibraryConfigurationServices() {
				return null;
			}

			@Override
			public TeamShareConfig createTeamShareConfigFrom(Properties properties) {
				return null;
			}

			@Override
			public ProjectLibraryConfig createProjectLibraryConfigFrom(Properties properties) {
				return null;
			}
		};
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
			public boolean isReservedNameForRootSceanrioSuite(String pageName) {
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

			@Override
			public String getId() {
				return null;
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
		content.append("|http://localhost:8060/files/demo/ExampleApplication/WebApplicationDe/index.html| Max Mustermann| test| Anmeldung war erfolgreich| Login|\n");
		content.append("|http://localhost:8060/files/demo/ExampleApplication/WebApplicationDe/index.html| Emil Mustermann| test| Login| Anmeldung war erfolgreich|\n");
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

		assertEquals("Expect 5 TestComponent in List", 5, testComponents.size());
		TestComponent testComponent = testComponents.get(0);
		assertTrue(testComponent instanceof TestScenarioParameterTable);
		String title = ((TestScenarioParameterTable) testComponent).getTitle();
		if (title.length() > 0) {
			assertEquals("Mein Szenario", title);
		}
		assertEquals(1, ((TestScenarioParameterTable) testComponent).getDataTable().getDataRows().size());
		assertTrue(((TestScenarioParameterTable) testComponent).getDataTable().getDataRows().get(0).toString()
				.startsWith("X fuer' n U|"));
		testComponent = testComponents.get(1);
		assertTrue(testComponent instanceof TestDescription);
		assertEquals("|note|Description: Hier noch schnell eine Beschreibung|", testComponent.getSourceCode()
				.toString());
		testComponent = testComponents.get(4);
		assertTrue(((TestActionGroup) testComponent).getActionGroupName().contentEquals("Willkommen"));
	}
}
