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
package org.testeditor.teamshare.svn;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.model.team.TeamChangeType;
import org.testeditor.core.model.teststructure.TestCase;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestProjectConfig;
import org.testeditor.core.model.teststructure.TestSuite;
import org.testeditor.core.services.interfaces.TeamShareService;
import org.testeditor.teamshare.svn.util.SvnHelper;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;

/**
 * Test the TeamShareStatus class.
 * 
 */
public class TeamShareStatusTest {

	private static final String SOURCE_WORKSPACE_PATH = "./testProject";
	private static final String REPOSITORY_PATH = "./testrepo";

	private static final String PROJEKT_NAME = "DemoWebTests";

	private static String targetWorkspacePath;

	private static String projectpath;
	private TeamShareService teamService;

	private TranslationService translationService = new TranslationServiceAdapter().getTranslationService();

	private static final Logger LOGGER = Logger.getLogger(TeamShareStatusTest.class);

	/**
	 * Setup for testing.
	 * 
	 */
	@BeforeClass
	public static void setUpForClass() {
		if (!System.getProperty("java.io.tmpdir").endsWith(File.separator)) {
			targetWorkspacePath = System.getProperty("java.io.tmpdir") + File.separator + "testProjectJUnit";
		} else {
			targetWorkspacePath = System.getProperty("java.io.tmpdir") + "testProjectJUnit";
		}
		projectpath = targetWorkspacePath + File.separator + PROJEKT_NAME;

	}

	/**
	 * Setup for testing.
	 * 
	 * @throws Exception
	 *             if file access fails
	 */
	@Before
	public void setUp() throws Exception {
		LOGGER.setLevel(Level.ERROR);

		System.setProperty("svn.default.comment", "xyz");

		teamService = new SVNTeamShareService();

		SVNRepositoryFactoryImpl.setup();

		FileUtils.copyDirectory(new File(SOURCE_WORKSPACE_PATH), new File(targetWorkspacePath));

		FileUtils.deleteDirectory(new File(REPOSITORY_PATH));
		SVNRepositoryFactory.createLocalRepository(new File(REPOSITORY_PATH), true, false);

	}

	/**
	 * Removes temporary created SVN.
	 * 
	 * @throws IOException
	 *             on File cleanups.
	 */
	@After
	public void cleanUpLocalProject() throws IOException {
		if (new File(projectpath).exists()) {
			FileUtils.deleteDirectory(new File(projectpath));
		}
	}

	/**
	 * this method creates a new Testproject.
	 * 
	 * @param repositoryPath
	 *            can be local (on file system usage: "file:///c:/tmp/testrepo") <br>
	 *            or remote (on a remote svn system)
	 * @param userName
	 *            can be empty for local share
	 * @param password
	 *            can be empty for local share
	 * @return test project
	 */
	private TestProject createTestProject(String repositoryPath, String userName, String password) {

		TestProject testProject = new TestProject();
		testProject.setName(PROJEKT_NAME);
		TestProjectConfig testProjectConfig = new TestProjectConfig();

		testProjectConfig.setProjectPath(projectpath);

		SVNTeamShareConfig svnTeamShareConfig = new SVNTeamShareConfig();

		svnTeamShareConfig.setUrl(repositoryPath);
		svnTeamShareConfig.setUserName(userName);
		svnTeamShareConfig.setPassword(password);

		testProjectConfig.setTeamShareConfig(svnTeamShareConfig);

		testProject.setTestProjectConfig(testProjectConfig);

		return testProject;

	}

	/**
	 * This test creates a {@link TestProject} and shares it on a local
	 * repository. Changes were made to the TestProject configuration files, no
	 * {@link org.testeditor.core.model.teststructure.TestStructure} are
	 * involved e.g AllActioGroup.xml will be changed. Checks if the SVN state
	 * has changed and has been set in the TestProject correctly.
	 * 
	 * @throws IOException
	 *             IO failure
	 * @throws SVNException
	 *             SVN failure
	 * @throws SystemException
	 *             System failure
	 * @throws InterruptedException
	 *             InterruptedException
	 */
	@Test
	public void testStatusOnProjectChanges() throws IOException, SVNException, SystemException, InterruptedException {

		final TestProject testProject = createTestProject(REPOSITORY_PATH, "", "");

		teamService.share(testProject, translationService, "");

		assertTrue(TeamChangeType.NONE == testProject.getTeamChangeType());

		String appendString = "do Something";
		File updateFile = new File(projectpath + "/AllActionGroups.xml");
		SvnHelper.updateFile(updateFile, appendString);

		TeamShareStatus teamShareStatus = new TeamShareStatus(null);
		teamShareStatus.setSVNStatusForProject(testProject);
		while (!teamShareStatus.isFinish()) {
			LOGGER.debug("Wait for thread ends");
		}
		assertTrue(TeamChangeType.NONE != testProject.getTeamChangeType());
	}

	/**
	 * 
	 * This test creates a {@link TestProject}, creates and add a new
	 * {@link TestSuite} to the TestProject and shares the TestProject on a
	 * local repository. Changes the context.txt in the TestSuite. Checks if the
	 * SVN state has changed and has been set in the TestProject and TestSuite
	 * correctly.
	 * 
	 * @throws IOException
	 *             IO failure
	 * @throws SVNException
	 *             SVN failure
	 * @throws SystemException
	 *             System failure
	 * @throws InterruptedException
	 *             InterruptedException
	 */
	@Test
	public void testStatusOnSuiteChanges() throws IOException, SVNException, SystemException, InterruptedException {
		String testSuiteName = "SuiteSvn";
		final TestProject testProject = createTestProject(REPOSITORY_PATH, "", "");

		// Add new Testpage
		SvnHelper.createNewTestPage(projectpath + "/FitNesseRoot/" + PROJEKT_NAME, testSuiteName);

		TestSuite testSuite = new TestSuite();
		testSuite.setName(testSuiteName);

		testProject.addChild(testSuite);

		teamService.share(testProject, translationService, "");

		assertTrue(TeamChangeType.NONE == testProject.getTeamChangeType());
		assertTrue(TeamChangeType.NONE == testSuite.getTeamChangeType());

		String appendString = "do Something";
		File updateFile = new File(projectpath + "/FitNesseRoot/" + PROJEKT_NAME + "/" + testSuite + "/content.txt");
		SvnHelper.updateFile(updateFile, appendString);

		TeamShareStatus teamShareStatus = new TeamShareStatus(null);
		teamShareStatus.setSVNStatusForProject(testProject);
		while (!teamShareStatus.isFinish()) {
			LOGGER.debug("Wait for thread ends");
		}
		assertTrue(TeamChangeType.NONE != testProject.getTeamChangeType());
		assertTrue(TeamChangeType.NONE != testSuite.getTeamChangeType());
	}

	/**
	 * 
	 * This test creates a {@link TestProject}, creates and add a new
	 * {@link TestSuite} and {@link TestCase} to the TestProject and shares the
	 * TestProject on a local repository. Changes the context.txt in the
	 * TestCase. Checks if the SVN state has changed and has been set in the
	 * TestProject, TestSuite and TestCase correctly.
	 * 
	 * @throws IOException
	 *             IO failure
	 * @throws SVNException
	 *             SVN failure
	 * @throws SystemException
	 *             System failure
	 * @throws InterruptedException
	 *             InterruptedException
	 */
	@Test
	public void testStatusOnCaseChanges() throws IOException, SVNException, SystemException, InterruptedException {
		String testSuiteName = "SuiteSvn";
		String testCaseName = "CaseSvn";
		final TestProject testProject = createTestProject(REPOSITORY_PATH, "", "");

		// Add new Testpage
		SvnHelper.createNewTestPage(projectpath + "/FitNesseRoot/" + PROJEKT_NAME, testSuiteName);
		TestSuite testSuite = new TestSuite();
		testSuite.setName(testSuiteName);
		testProject.addChild(testSuite);

		// Add new Testpage
		SvnHelper.createNewTestPage(projectpath + "/FitNesseRoot/" + PROJEKT_NAME + "/" + testSuiteName, testCaseName);
		TestCase testCase = new TestCase();
		testCase.setName(testCaseName);
		testSuite.addChild(testCase);

		teamService.share(testProject, translationService, "");

		assertTrue(TeamChangeType.NONE == testProject.getTeamChangeType());
		assertTrue(TeamChangeType.NONE == testSuite.getTeamChangeType());
		assertTrue(TeamChangeType.NONE == testCase.getTeamChangeType());

		String appendString = "do Something";
		File updateFile = new File(projectpath + "/FitNesseRoot/" + PROJEKT_NAME + "/" + testSuite + "/" + testCase
				+ "/content.txt");
		SvnHelper.updateFile(updateFile, appendString);

		TeamShareStatus teamShareStatus = new TeamShareStatus(null);
		teamShareStatus.setSVNStatusForProject(testProject);
		while (!teamShareStatus.isFinish()) {
			LOGGER.debug("Wait for thread ends");
		}
		assertTrue(TeamChangeType.NONE != testProject.getTeamChangeType());
		assertTrue(TeamChangeType.NONE != testSuite.getTeamChangeType());
		assertTrue(TeamChangeType.NONE != testCase.getTeamChangeType());
	}

	/**
	 * This test creates a {@link TestProject} and DONT shares the TestProject.
	 * Changes the context.txt in the TestCase. Checks if the SVN state has
	 * changed and has been set in the TestProject, TestSuite and TestCase
	 * correctly.
	 * 
	 * IGnore the test, because of failure at the integration. The project is
	 * always shared at the server.
	 * 
	 * @throws IOException
	 *             IO failure
	 * @throws SVNException
	 *             SVN failure
	 * @throws SystemException
	 *             System failure
	 * @throws InterruptedException
	 *             InterruptedException
	 */
	@Ignore
	@Test
	public void testStatusOnProjectChangesNotShared() throws IOException, SVNException, SystemException,
			InterruptedException {

		final TestProject testProject = createTestProject(REPOSITORY_PATH, "", "");

		assertTrue(TeamChangeType.NONE == testProject.getTeamChangeType());

		String appendString = "do Something";
		File updateFile = new File(projectpath + "/AllActionGroups.xml");
		SvnHelper.updateFile(updateFile, appendString);

		TeamShareStatus teamShareStatus = new TeamShareStatus(null);
		teamShareStatus.setSVNStatusForProject(testProject);
		while (!teamShareStatus.isFinish()) {
			LOGGER.debug("Wait for thread ends");
		}
		assertTrue(TeamChangeType.NONE == testProject.getTeamChangeType());
	}

	/**
	 * This test creates a {@link TestProject}, Changes the context.txt in one
	 * TestScenario. Checks if the SVN state has changed and has been set in the
	 * TestProject correctly.
	 * 
	 * @throws IOException
	 *             IO failure
	 * @throws SVNException
	 *             SVN failure
	 * @throws SystemException
	 *             System failure
	 * @throws InterruptedException
	 *             InterruptedException
	 */
	@Test
	public void testStatusOnSzenarioChange() throws IOException, SVNException, SystemException, InterruptedException {

		final TestProject testProject = createTestProject(REPOSITORY_PATH, "", "");

		teamService.share(testProject, translationService, "");

		assertTrue(TeamChangeType.NONE == testProject.getTeamChangeType());

		String appendString = "do Something";
		File updateFile = new File(projectpath + "/FitNesseRoot/" + PROJEKT_NAME
				+ "/TestKomponenten/SucheGoogleSzenario/content.txt");
		SvnHelper.updateFile(updateFile, appendString);

		TeamShareStatus teamShareStatus = new TeamShareStatus(null);
		teamShareStatus.setSVNStatusForProject(testProject);
		while (!teamShareStatus.isFinish()) {
			LOGGER.debug("Wait for thread ends");
		}
		assertTrue(TeamChangeType.NONE != testProject.getTeamChangeType());
	}

	/**
	 * This test creates a {@link TestProject}, creates and add a new
	 * {@link TestSuite} and {@link TestCase} to the TestProject and shares the
	 * TestProject on a local repository. Makes different changes and approve
	 * them. Checks if the SVN state has changed and has been set in the
	 * TestProject, TestSuite and TestCase correctly after every change and
	 * approve.
	 * 
	 * @throws IOException
	 *             IO failure
	 * @throws SVNException
	 *             SVN failure
	 * @throws SystemException
	 *             System failure
	 * @throws InterruptedException
	 *             InterruptedException
	 */
	@Test
	public void testStatusOnCaseChangeAndApprove() throws IOException, SVNException, SystemException,
			InterruptedException {
		String testSuiteName = "SuiteSvn";
		String testCaseName = "CaseSvn";
		final TestProject testProject = createTestProject(REPOSITORY_PATH, "", "");

		// Add new Testpage
		SvnHelper.createNewTestPage(projectpath + "/FitNesseRoot/" + PROJEKT_NAME, testSuiteName);
		TestSuite testSuite = new TestSuite();
		testSuite.setName(testSuiteName);
		testProject.addChild(testSuite);

		// Add new Testpage
		SvnHelper.createNewTestPage(projectpath + "/FitNesseRoot/" + PROJEKT_NAME + "/" + testSuiteName, testCaseName);
		TestCase testCase = new TestCase();
		testCase.setName(testCaseName);
		testSuite.addChild(testCase);

		teamService.share(testProject, translationService, "");

		assertTrue(TeamChangeType.NONE == testProject.getTeamChangeType());
		assertTrue(TeamChangeType.NONE == testSuite.getTeamChangeType());
		assertTrue(TeamChangeType.NONE == testCase.getTeamChangeType());

		String appendString = "do Something";
		File updateFile = new File(projectpath + "/FitNesseRoot/" + PROJEKT_NAME + "/" + testSuite + "/" + testCase
				+ "/content.txt");
		SvnHelper.updateFile(updateFile, appendString);

		TeamShareStatus teamShareStatus = new TeamShareStatus(null);
		teamShareStatus.setSVNStatusForProject(testProject);
		while (!teamShareStatus.isFinish()) {
			LOGGER.debug("Wait for thread ends");
		}
		assertTrue(TeamChangeType.NONE != testProject.getTeamChangeType());
		assertTrue(TeamChangeType.NONE != testSuite.getTeamChangeType());
		assertTrue(TeamChangeType.NONE != testCase.getTeamChangeType());

		teamService.approve(testProject, translationService, "");

		teamShareStatus = new TeamShareStatus(null);
		teamShareStatus.setSVNStatusForProject(testProject);
		while (!teamShareStatus.isFinish()) {
			LOGGER.debug("Wait for thread ends");
		}
		assertTrue(TeamChangeType.NONE == testProject.getTeamChangeType());
		assertTrue(TeamChangeType.NONE == testSuite.getTeamChangeType());
		assertTrue(TeamChangeType.NONE == testCase.getTeamChangeType());
	}

	/**
	 * 
	 * This test creates a {@link TestProject}, creates and add a new
	 * {@link TestSuite} and {@link TestCase} to the TestProject and shares the
	 * TestProject on a local repository. Deletes the TestCase. Checks if the
	 * SVN state has changed and has been set in the TestProject and TestSuite
	 * correctly.
	 * 
	 * @throws IOException
	 *             IO failure
	 * @throws SVNException
	 *             SVN failure
	 * @throws SystemException
	 *             System failure
	 * @throws InterruptedException
	 *             InterruptedException
	 */
	@Test
	public void testStatusOnTestCaseDeleted() throws IOException, SVNException, SystemException, InterruptedException {
		String testSuiteName = "SuiteSvn";
		String testCaseName = "CaseSvn";
		final TestProject testProject = createTestProject(REPOSITORY_PATH, "", "");

		// Add new Testpage
		SvnHelper.createNewTestPage(projectpath + "/FitNesseRoot/" + PROJEKT_NAME, testSuiteName);
		TestSuite testSuite = new TestSuite();
		testSuite.setName(testSuiteName);
		testProject.addChild(testSuite);

		// Add new Testpage
		SvnHelper.createNewTestPage(projectpath + "/FitNesseRoot/" + PROJEKT_NAME + "/" + testSuiteName, testCaseName);
		TestCase testCase = new TestCase();
		testCase.setName(testCaseName);
		testSuite.addChild(testCase);

		teamService.share(testProject, translationService, "");

		assertEquals(TeamChangeType.NONE, testProject.getTeamChangeType());
		assertEquals(TeamChangeType.NONE, testSuite.getTeamChangeType());
		assertEquals(TeamChangeType.NONE, testCase.getTeamChangeType());

		teamService.delete(testCase, translationService);
		testSuite.removeChild(testCase);

		TeamShareStatus teamShareStatus = new TeamShareStatus(null);
		teamShareStatus.setSVNStatusForProject(testProject);
		while (!teamShareStatus.isFinish()) {
			LOGGER.debug("Wait for thread ends");
		}
		assertEquals(TeamChangeType.MODIFY, testProject.getTeamChangeType());
		assertEquals(TeamChangeType.MODIFY, testSuite.getTeamChangeType());

	}

}
