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
package org.testeditor.fitnesse;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.exceptions.TeamAuthentificationException;
import org.testeditor.core.model.team.TeamShareConfig;
import org.testeditor.core.model.teststructure.TestCase;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestProjectConfig;
import org.testeditor.core.model.teststructure.TestScenario;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.model.teststructure.TestSuite;
import org.testeditor.core.services.interfaces.ProgressListener;
import org.testeditor.core.services.interfaces.ServiceLookUpForTest;
import org.testeditor.core.services.interfaces.TestScenarioService;
import org.testeditor.core.services.interfaces.TestStructureContentService;
import org.testeditor.core.services.interfaces.TestStructureService;
import org.testeditor.core.services.plugins.LibraryConfigurationServicePlugIn;
import org.testeditor.core.services.plugins.TeamShareServicePlugIn;
import org.testeditor.core.services.plugins.TestEditorPlugInService;
import org.testeditor.core.services.plugins.TestStructureServicePlugIn;
import org.testeditor.fitnesse.filesystem.FitnesseFileSystemTestStructureService;
import org.testeditor.fitnesse.filesystem.FitnesseFileSystemUtility;
import org.testeditor.fitnesse.util.FitnesseServerController;

/**
 * 
 * Integration Test for TestStructureServiceImpl.
 * 
 */
public class TestStructureServiceImplIntTest {

	private static final String SOURCE_WORKSPACE_PATH = "./DemoWebTests";
	private static String projectpath;
	private static final String PROJEKT_NAME = "DemoWebTests";
	private TestStructureService testStructureService;
	private TestScenarioService testScenarioService;
	private TestStructureContentService testStructureContentService;

	/**
	 * set the log-level to error for the tests.
	 */
	@BeforeClass
	public static void initialize() {
		projectpath = Platform.getLocation() + File.separator + PROJEKT_NAME;
		(new File(projectpath)).mkdirs();

	}

	/**
	 * Creates a new local svn-repository after deleting. Copies the example
	 * project in a new directory.
	 * 
	 * @throws Exception
	 *             IOException
	 */
	@Before
	public void setUp() throws Exception {
		FileUtils.deleteDirectory(new File(projectpath));
		FileUtils.copyDirectory(new File(SOURCE_WORKSPACE_PATH), new File(projectpath));

		testStructureService = ServiceLookUpForTest.getService(TestStructureService.class);
		testScenarioService = ServiceLookUpForTest.getService(TestScenarioService.class);
		testStructureContentService = ServiceLookUpForTest.getService(TestStructureContentService.class);
	}

	/**
	 * Tests the registration of the Service.
	 * 
	 * @throws Exception
	 *             for Test.
	 */
	@Test
	public void testServiceRegistration() throws Exception {
		TestStructureService service = ServiceLookUpForTest.getService(TestStructureServicePlugIn.class);
		assertNotNull("Expecting an implementation of the TestStructureService", service);
		assertTrue("Service implementation is not an instance of TestStructureServiceImpl",
				service instanceof TestStructureServiceImpl);
	}

	/**
	 * Tests that the wire up works and a TeamService is used for delete test
	 * structure.
	 * 
	 * @throws Exception
	 *             on test failure.
	 */
	@Test
	public void testDeleteWithTeamService() throws Exception {
		TestStructureServiceImpl service = new TestStructureServiceImpl();
		TestStructure testStructure = getTeamSharedTestStructure(service);
		service.delete(testStructure);
	}

	/**
	 * Test the rename of an Teststructure is delegated to the team service.
	 * 
	 * @throws Exception
	 *             on Testfailue
	 */
	@Test
	public void testRenameWithTeamService() throws Exception {
		TestStructureServiceImpl service = new TestStructureServiceImpl();
		TestStructure testStructure = getTeamSharedTestStructure(service);
		service.rename(testStructure, "foo");
	}

	/**
	 * Test the rename of a Teststructure.
	 * 
	 * @throws Exception
	 *             on Testfailue
	 */
	@Test
	public void testRenameTestCase() throws Exception {
		TestProject project = createTestProject();

		Platform.getLocation();
		FitnesseFileSystemTestStructureService fitnesseFileSystemTestStructureService = new FitnesseFileSystemTestStructureService();
		fitnesseFileSystemTestStructureService.loadChildrenInto(project);

		TestStructure testStructure = project.getTestChildByFullName("DemoWebTests.GoogleSucheSuite.SucheAkquinetTest");

		TestStructureServiceImpl service = new TestStructureServiceImpl();
		service.rename(testStructure, "newName");

		Path path = Paths.get(FitnesseFileSystemUtility.getPathToTestStructureDirectory(testStructure));
		assertTrue(path.toAbsolutePath() + " was not found.", Files.exists(path));
	}

	/**
	 * Test the rename of a Teststructure.
	 * 
	 * @throws Exception
	 *             on Testfailue
	 */
	@Test
	public void testMoveTestCase() throws Exception {
		TestProject project = createTestProject();

		Platform.getLocation();
		FitnesseFileSystemTestStructureService fitnesseFileSystemTestStructureService = new FitnesseFileSystemTestStructureService();
		fitnesseFileSystemTestStructureService.loadChildrenInto(project);

		TestStructure testStructure = project.getTestChildByFullName("DemoWebTests.GoogleSucheSuite.SucheAkquinetTest");

		TestSuite testSuite = (TestSuite) project.getTestChildByFullName("DemoWebTests.LocalDemoSuite");
		assertNotNull(testSuite);

		testStructureService.move(testStructure, testSuite);

		fitnesseFileSystemTestStructureService.loadChildrenInto(project);
		assertNull(project.getTestChildByFullName("DemoWebTests.GoogleSucheSuite.SucheAkquinetTest"));
		TestStructure newTestStructure = project
				.getTestChildByFullName("DemoWebTests.LocalDemoSuite.SucheAkquinetTest");
		assertNotNull(newTestStructure);
		assertFalse(getFitnessCode(newTestStructure).equals("!contents"));

		Path path = Paths.get(FitnesseFileSystemUtility.getPathToTestStructureDirectory(testStructure));
		assertTrue(path.toAbsolutePath() + " was not found.", Files.exists(path));
	}

	/**
	 * Test the rename of a Scenario.
	 * 
	 * @throws Exception
	 *             on Testfailue
	 */
	@Test
	public void testRenameScenario() throws Exception {
		TestProject project = createTestProject();
		Platform.getLocation();
		FitnesseFileSystemTestStructureService fitnesseFileSystemTestStructureService = new FitnesseFileSystemTestStructureService();
		fitnesseFileSystemTestStructureService.loadChildrenInto(project);

		TestStructure testStructure = project
				.getTestChildByFullName("DemoWebTests.TestSzenarien.ApplikationStartSzenario");
		assertTrue(getFitnessCode(testStructure).startsWith("!|scenario |ApplikationStartSzenario"));
		TestStructureService service = ServiceLookUpForTest.getService(TestStructureService.class);
		TestScenario scenario = testScenarioService.getScenarioByFullName(testStructure.getRootElement(),
				testStructure.getFullName());
		boolean usageFound = false;
		for (String usage : testScenarioService.getUsedOfTestSceneario(scenario)) {
			usageFound = true;
			TestStructure usageStructure = scenario.getRootElement().getTestChildByFullName(usage);
			String usageCode = testStructureContentService.getTestStructureAsSourceText(usageStructure);
			assertTrue(usageCode.indexOf("!include <DemoWebTests.TestSzenarien.ApplikationStartSzenario") > 0);
			assertTrue(usageCode.indexOf("!|script|\n|Applikation Start Szenario|") > 0);
			assertTrue(usageCode.indexOf("!include <DemoWebTests.TestSzenarien.NewName") == -1);
			assertTrue(usageCode.indexOf("!|script|\n|New Name|") == -1);
		}
		assertTrue(usageFound);

		service.rename(testStructure, "NewName");

		Path path = Paths.get(FitnesseFileSystemUtility.getPathToTestStructureDirectory(testStructure));
		assertTrue(path.toAbsolutePath() + " was not found.", Files.exists(path));
		assertTrue(getFitnessCode(testStructure).startsWith("!|scenario |NewName"));

		usageFound = false;
		for (String usage : testScenarioService.getUsedOfTestSceneario(scenario)) {
			usageFound = true;
			TestStructure usageStructure = scenario.getRootElement().getTestChildByFullName(usage);
			String usageCode = testStructureContentService.getTestStructureAsSourceText(usageStructure);
			assertTrue(usageCode.indexOf("!include <DemoWebTests.TestSzenarien.ApplikationStartSzenario") == -1);
			assertTrue(usageCode.indexOf("!|script|\n|Applikation Start Szenario|") == -1);
			assertTrue(usageCode.indexOf("!include <DemoWebTests.TestSzenarien.NewName") > 0);
			assertTrue(usageCode.indexOf("!|script|\n|New Name|") > 0);
		}
		assertTrue(usageFound);
	}

	/**
	 * Test the rename of a Scenario.
	 * 
	 * @throws Exception
	 *             on Testfailue
	 */
	@Test
	public void testRenameScenarioWithParameter() throws Exception {
		TestProject project = createTestProject();

		Platform.getLocation();
		FitnesseFileSystemTestStructureService fitnesseFileSystemTestStructureService = new FitnesseFileSystemTestStructureService();
		fitnesseFileSystemTestStructureService.loadChildrenInto(project);

		TestStructure testStructure = project
				.getTestChildByFullName("DemoWebTests.TestSzenarien.LoginValidationSzenario");
		assertTrue(getFitnessCode(testStructure).startsWith("!|scenario |LoginValidationSzenario _|Name, Passwort"));

		TestScenario scenario = testScenarioService.getScenarioByFullName(testStructure.getRootElement(),
				testStructure.getFullName());
		boolean usageFound = false;
		for (String usage : testScenarioService.getUsedOfTestSceneario(scenario)) {
			usageFound = true;
			TestStructure usageStructure = scenario.getRootElement().getTestChildByFullName(usage);
			String usageCode = testStructureContentService.getTestStructureAsSourceText(usageStructure);
			assertTrue(usageCode.indexOf("!include <DemoWebTests.TestSzenarien.LoginValidationSzenario") > 0);
			assertTrue(usageCode.indexOf("!|Login Validation Szenario|") > 0);
			assertTrue(usageCode.indexOf("!include <DemoWebTests.TestSzenarien.NewName") == -1);
			assertTrue(usageCode.indexOf("!|New Name|") == -1);

		}
		assertTrue(usageFound);

		testStructureService.rename(testStructure, "NewName");

		Path path = Paths.get(FitnesseFileSystemUtility.getPathToTestStructureDirectory(testStructure));
		assertTrue(path.toAbsolutePath() + " was not found.", Files.exists(path));
		assertTrue(getFitnessCode(testStructure).startsWith("!|scenario |NewName _|Name, Passwort"));

		scenario = testScenarioService.getScenarioByFullName(testStructure.getRootElement(),
				testStructure.getFullName());
		usageFound = false;
		for (String usage : testScenarioService.getUsedOfTestSceneario(scenario)) {
			usageFound = true;
			TestStructure usageStructure = scenario.getRootElement().getTestChildByFullName(usage);
			String usageCode = testStructureContentService.getTestStructureAsSourceText(usageStructure);
			assertTrue(usageCode.indexOf("!include <DemoWebTests.TestSzenarien.LoginValidationSzenario") == -1);
			assertTrue(usageCode.indexOf("!|Login Validation Szenario|") == -1);
			assertTrue(usageCode.indexOf("!include <DemoWebTests.TestSzenarien.NewName") > 0);
			assertTrue(usageCode.indexOf("!|New Name|") > 0);
		}
		assertTrue(usageFound);
	}

	/**
	 * 
	 * @param service
	 *            used to inject the team service mock.
	 * @return TestStruture in team shared Project.
	 */
	private TestStructure getTeamSharedTestStructure(TestStructureServiceImpl service) {
		IEclipseContext context = EclipseContextFactory.create();
		service.compute(context, null);
		HashSet<String> set = new HashSet<String>();
		TeamShareServicePlugIn serviceMock = getTeamShareServiceMock(set);
		service.bind(serviceMock);
		TestProject tp = new TestProject();
		TestProjectConfig testProjectConfig = new TestProjectConfig();
		TeamShareConfig aTeamShareConfig = new TeamShareConfig() {

			@Override
			public String getId() {
				return "myDummy";
			}
		};
		testProjectConfig.setTeamShareConfig(aTeamShareConfig);
		tp.setTestProjectConfig(testProjectConfig);
		TestStructure testStructure = new TestCase();
		tp.addChild(testStructure);
		return testStructure;
	}

	/**
	 * 
	 * @param set
	 *            used to inform the test about operations calls.
	 * @return team share service mock.
	 */
	private TeamShareServicePlugIn getTeamShareServiceMock(final HashSet<String> set) {
		return new TeamShareServicePlugIn() {

			@Override
			public void disconnect(TestProject testProject, TranslationService translationService)
					throws SystemException {
			}

			@Override
			public void share(TestProject testProject, TranslationService translationService, String svnComment)
					throws SystemException {
			}

			@Override
			public String approve(TestStructure testStructure, TranslationService translationService, String svnComment)
					throws SystemException {
				return "";
			}

			@Override
			public String update(TestStructure testStructure, TranslationService translationService)
					throws SystemException {
				return "";
			}

			@Override
			public void checkout(TestProject testProject, TranslationService translationService)
					throws SystemException, TeamAuthentificationException {

			}

			@Override
			public String getId() {
				return "myDummy";
			}

			@Override
			public void delete(TestStructure testStructure, TranslationService translationService)
					throws SystemException {
				set.add("deleted");
			}

			@Override
			public String getStatus(TestStructure testStructure, TranslationService translationService)
					throws SystemException {
				return null;
			}

			@Override
			public void addProgressListener(TestStructure testStructure, ProgressListener listener) {
			}

			@Override
			public void addChild(TestStructure testStructureChild, TranslationService translationService)
					throws SystemException {
			}

			@Override
			public boolean validateConfiguration(TestProject testProject, TranslationService translationService)
					throws SystemException {
				return false;
			}

			@Override
			public void revert(TestStructure testStructure, TranslationService translationService)
					throws SystemException {

			}

			@Override
			public void rename(TestStructure testStructure, String newName, TranslationService translationService)
					throws SystemException {
				set.add("renamed");
			}

			@Override
			public void addAdditonalFile(TestStructure testStructur, String fileName) throws SystemException {
			}

			@Override
			public int availableUpdatesCount(TestProject testProject) {
				return 0;
			}

			@Override
			public void removeAdditonalFile(TestStructure testStructure, String fileName) throws SystemException {
			}

			@Override
			public boolean isCleanupNeeded(TestProject testProject) throws SystemException {
				return false;
			}

			@Override
			public void cleanup(TestProject testProject) throws SystemException {
			}

			@Override
			public Map<String, String> getAvailableReleases(TestProject testProject) {
				return null;
			}

			@Override
			public void switchToBranch(TestProject testproject, String url) throws SystemException {
			}

			@Override
			public String getCurrentBranch(TestProject testProject) {
				return null;
			}

			@Override
			public boolean isDirty(TestProject project) {
				return false;
			}
		};
	}

	/**
	 * Creates a testproject based on the projectstructure in the project and
	 * starts a fitness-server for the project.
	 * 
	 * @return - the testProject.
	 * @throws Exception
	 *             - all exception during the testrun
	 */
	private TestProject createTestProject() throws Exception {

		TestProject testProject = new TestProject();
		testProject.setName(PROJEKT_NAME);

		TestProjectConfig testProjectConfig = new TestProjectConfig();
		testProjectConfig.setPort("8082");

		testProjectConfig.setProjectPath(projectpath);
		File configFile = new File(projectpath + File.separator + "config.tpr");
		InputStream input = new FileInputStream(configFile);
		Properties properties = new Properties();
		properties.load(input);

		input.close();

		TestEditorPlugInService plugInService = ServiceLookUpForTest.getService(TestEditorPlugInService.class);
		LibraryConfigurationServicePlugIn libraryConfigurationService = plugInService
				.getLibraryConfigurationServiceFor("org.testeditor.xmllibrary");
		testProjectConfig
				.setProjectLibraryConfig(libraryConfigurationService.createProjectLibraryConfigFrom(properties));

		testProject.setUrl(new File(projectpath));

		testProject.setTestProjectConfig(testProjectConfig);

		FitnesseServerController controller = new FitnesseServerController();
		controller.startFitnesse(testProject);

		return testProject;

	}

	private String getFitnessCode(TestStructure testStructure) throws IOException {
		Path path = Paths.get(FitnesseFileSystemUtility.getPathToTestStructureDirectory(testStructure) + File.separator
				+ "content.txt");
		return new String(Files.readAllBytes(path));
	}

}
