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
/**
 * 
 */
package org.testeditor.teamshare.svn;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.exceptions.TeamAuthentificationException;
import org.testeditor.core.model.teststructure.TestCase;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestProjectConfig;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.model.teststructure.TestSuite;
import org.testeditor.core.services.interfaces.TeamShareService;
import org.testeditor.core.services.plugins.TeamShareStatusServicePlugIn;
import org.testeditor.teamshare.svn.util.SvnHelper;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNErrorCode;
import org.tmatesoft.svn.core.SVNErrorMessage;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.ISVNStatusHandler;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNStatus;
import org.tmatesoft.svn.core.wc.SVNStatusClient;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

/**
 * Tests the {@link SVNTeamShareService} on local repository.
 * 
 */
public class SVNTeamShareServiceLocalTest {

	private static final String SOURCE_WORKSPACE_PATH = "./testProject";
	private static final String REPOSITORY_PATH = "./testrepo";

	private static final String PROJEKT_NAME = "DemoWebTests";

	private static String targetWorkspacePath;

	private static String projectpath;
	private static final Logger LOGGER = Logger.getLogger(SVNTeamShareService.class);

	private TeamShareService teamService;

	private TranslationService translationService = new TranslationServiceAdapter().getTranslationService();

	/**
	 * set the log-level to error for the tests.
	 */
	@BeforeClass
	public static void initialize() {
		if (!System.getProperty("java.io.tmpdir").endsWith(File.separator)) {
			targetWorkspacePath = System.getProperty("java.io.tmpdir") + File.separator + "testProjectJUnit";
		} else {
			targetWorkspacePath = System.getProperty("java.io.tmpdir") + "testProjectJUnit";
		}
		projectpath = targetWorkspacePath + File.separator + PROJEKT_NAME;

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
	public void cleanUpdLocalSVN() throws IOException {
		if (new File(targetWorkspacePath).exists()) {
			FileUtils.deleteDirectory(new File(targetWorkspacePath));
		}
	}

	/**
	 * This method creates a new Testproject.
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
	 * Test for sharing project on local file system.
	 * 
	 * @throws IOException
	 *             IO failure
	 * @throws SVNException
	 *             SVN failure
	 * @throws SystemException
	 *             System failure
	 */
	@Test
	public void testShareProject() throws IOException, SVNException, SystemException {

		TestProject testProject = createTestProject(REPOSITORY_PATH, "", "");
		teamService.share(testProject, translationService, "");

	}

	/**
	 * Test for
	 * {@link SVNTeamShareService#approve(org.testeditor.core.model.teststructure.TestStructure)}
	 * Method.
	 * 
	 * @throws IOException
	 *             IO failure
	 * @throws SVNException
	 *             SVN failure
	 * @throws SystemException
	 *             System failure
	 */
	@Test
	public void testCommitTest() throws IOException, SVNException, SystemException {

		String testPageName = "MyFirstTest";

		TestProject testProject = createTestProject(REPOSITORY_PATH, "", "");
		teamService.share(testProject, translationService, "");

		// Add new Testpage
		SvnHelper.createNewTestPage(projectpath + "/FitNesseRoot/" + PROJEKT_NAME, testPageName);

		TestCase testCase = new TestCase();
		testCase.setName(testPageName);

		testProject.addChild(testCase);

		teamService.approve(testCase, translationService, "");
	}

	/**
	 * Test for
	 * {@link SVNTeamShareService#approve(org.testeditor.core.model.teststructure.TestStructure)}
	 * Method.
	 * 
	 * By committing an project e.g. xml files will be also committed.
	 * 
	 * @throws IOException
	 *             IO failure
	 * @throws SVNException
	 *             SVN failure
	 * @throws SystemException
	 *             System failure
	 * @throws TeamAuthentificationException
	 */
	@Test
	public void testCommitProject() throws IOException, SVNException, SystemException, TeamAuthentificationException {

		TestProject testProject = createTestProject(REPOSITORY_PATH, "", "");
		teamService.share(testProject, translationService, "");

		File updateFile = new File(projectpath + "/AllActionGroups.xml");
		String appendString = "do Something";
		SvnHelper.updateFile(updateFile, appendString);

		teamService.approve(testProject, translationService, "");

		// delete working copy
		FileUtils.deleteDirectory(new File(targetWorkspacePath));

		// checkout the project
		teamService.checkout(testProject, translationService);

		assertTrue(FileUtils.readFileToString(updateFile).endsWith(appendString));

	}

	/**
	 * Test for
	 * {@link SVNTeamShareService#approve(org.testeditor.core.model.teststructure.TestStructure)}
	 * Method for adding a new suite with one Test as child.
	 * 
	 * <ul>
	 * <li>Suite1 (commit at this point)</li>
	 * </ul>
	 * <ul>
	 * <li>Test1</li>
	 * </ul>
	 * 
	 * @throws IOException
	 *             IO failure
	 * @throws SVNException
	 *             SVN failure
	 * @throws SystemException
	 *             System failure
	 * @throws TeamAuthentificationException
	 *             authorization failed
	 */
	@Test
	public void testCommitSuiteWithOneTest() throws IOException, SVNException, SystemException,
			TeamAuthentificationException {

		TestProject testProject = createTestProject(REPOSITORY_PATH, "", "");
		teamService.share(testProject, translationService, "");

		String suiteName = "Suite1";
		String testName = "Test1";

		// Add new Suite
		Path suitePath = SvnHelper.createNewTestPage(projectpath + "/FitNesseRoot/" + PROJEKT_NAME, suiteName);
		// Add new Test
		SvnHelper.createNewTestPage(suitePath.toString(), testName.toString());

		TestSuite testSuite = new TestSuite();
		testSuite.setName(suiteName);
		testProject.addChild(testSuite);
		teamService.addChild(testSuite, translationService);

		TestCase testCase = new TestCase();
		testCase.setName(testName);

		testSuite.addChild(testCase);
		teamService.addChild(testCase, translationService);

		teamService.update(testProject, translationService);

		String statusInfo = teamService.getStatus(testProject, translationService);
		String[] statusLines = statusInfo.split("\n");
		for (String line : statusLines) {
			String[] split = line.split(";");
			if (split[0].contains(suiteName) || split[0].contains(testName)) {
				assertEquals("added 3", split[1]);
			}
		}
		teamService.approve(testProject, translationService, "");

		statusInfo = teamService.getStatus(testProject, translationService);
		statusLines = statusInfo.split("\n");
		for (String line : statusLines) {
			String[] split = line.split(";");
			if (split[0].contains(suiteName) || split[0].contains(testName)) {
				assertEquals("normal 1", split[1]);
			}
		}

	}

	/**
	 * Test for
	 * {@link SVNTeamShareService#update(org.testeditor.core.model.teststructure.TestStructure)}
	 * Method.
	 * 
	 * @throws IOException
	 *             IO failure
	 * @throws SVNException
	 *             SVN failure
	 * @throws SystemException
	 *             System failure
	 * @throws TeamAuthentificationException
	 *             authorization failed
	 */
	@Test
	public void testUpdateSuite() throws IOException, SVNException, SystemException, TeamAuthentificationException {

		// share new repository
		TestProject testProject = createTestProject(REPOSITORY_PATH, "", "");
		teamService.share(testProject, translationService, "");

		// delete some tests locally
		File loginSuite = new File(projectpath + "/FitNesseRoot/" + PROJEKT_NAME + "/LocalDemoSuite/LoginSuite");
		FileUtils.deleteDirectory(loginSuite);

		// deletion was successfull
		assertTrue(!loginSuite.exists());

		TestSuite localDemoSuite = new TestSuite();
		final HashSet<String> set = new HashSet<String>();
		localDemoSuite.setLazyLoader(new Runnable() {

			@Override
			public void run() {
				set.add("run");
			}
		});
		localDemoSuite.setChildCountInBackend(1);
		localDemoSuite.setName("LocalDemoSuite");
		testProject.addChild(localDemoSuite);

		// update at suite node
		teamService.update(localDemoSuite, translationService);
		assertFalse(set.contains("run"));
		localDemoSuite.getTestChildren();
		assertTrue(set.contains("run"));
		// update was successfull
		assertTrue(loginSuite.exists());

	}

	/**
	 * Test for
	 * {@link SVNTeamShareService#update(org.testeditor.core.model.teststructure.TestStructure)}
	 * Method.
	 * 
	 * @throws IOException
	 *             IO failure
	 * @throws SVNException
	 *             SVN failure
	 * @throws SystemException
	 *             System failure
	 * @throws TeamAuthentificationException
	 *             authorization failed
	 */
	@Test
	public void testUpdateProject() throws IOException, SVNException, SystemException, TeamAuthentificationException {

		// share new repository
		TestProject testProject = createTestProject(REPOSITORY_PATH, "", "");
		teamService.share(testProject, translationService, "");

		// delete some tests locally
		File deleteFile = new File(projectpath + "/AllActionGroups.xml");
		deleteFile.delete();

		// deletion was successfull
		assertTrue(!deleteFile.exists());

		// update at suite node
		teamService.update(testProject, translationService);

		// update was successfull
		assertTrue(deleteFile.exists());

	}

	/**
	 * Test for
	 * {@link SVNTeamShareService#update(org.testeditor.core.model.teststructure.TestStructure)}
	 * Method.
	 * 
	 * @throws IOException
	 *             IO failure
	 * @throws SVNException
	 *             SVN failure
	 * @throws SystemException
	 *             System failure
	 * @throws TeamAuthentificationException
	 */
	@Test
	public void testCheckoutTest() throws IOException, SVNException, SystemException, TeamAuthentificationException {

		TestProject testProject = createTestProject(REPOSITORY_PATH, "", "");
		teamService.share(testProject, translationService, "");

		String projectPath = testProject.getRootElement().getTestProjectConfig().getProjectPath();

		FileUtils.deleteDirectory(new File(projectPath).getParentFile());

		// deletion was successfull
		assertTrue(!new File(targetWorkspacePath + "/" + PROJEKT_NAME).exists());

		teamService.checkout(testProject, translationService);

		// checkout was successfull
		assertTrue(FileUtils.directoryContains(new File(targetWorkspacePath), new File(targetWorkspacePath + "/"
				+ PROJEKT_NAME)));

	}

	/**
	 * Test for
	 * {@link SVNTeamShareService#doDelete(org.testeditor.core.model.teststructure.TestStructure)}
	 * Method.
	 * 
	 * @throws IOException
	 *             IO failure
	 * @throws SVNException
	 *             SVN failure
	 * @throws SystemException
	 *             System failure
	 * @throws TeamAuthentificationException
	 *             authorization failed
	 */
	@Test
	public void testDeleteTest() throws IOException, SVNException, SystemException, TeamAuthentificationException {

		String testPageName = "MyFirstTest";

		TestProject testProject = createTestProject(REPOSITORY_PATH, "", "");
		teamService.share(testProject, translationService, "");

		// Add new Testpage
		Path createdNewTestPage = SvnHelper.createNewTestPage(projectpath + "/FitNesseRoot/" + PROJEKT_NAME,
				testPageName);

		TestCase testCase = new TestCase();
		testCase.setName(testPageName);

		testProject.addChild(testCase);
		TestStructure parent = testCase.getParent();
		teamService.approve(testCase, translationService, "");

		teamService.delete(testCase, translationService);
		// delete directory for checkout from repository
		FileUtils.deleteDirectory(createdNewTestPage.toFile());

		testStatus(testPageName, testProject, testCase.getName(), "deleted 4");

		teamService.approve(parent, translationService, "");
		testStatus(testPageName, testProject, testCase.getName(), "missing 6");

		teamService.update(parent, translationService);
		assertFalse(createdNewTestPage.toFile().exists());

	}

	/**
	 * Test for
	 * {@link SVNTeamShareService#doDelete(org.testeditor.core.model.teststructure.TestSuite)}
	 * Method.
	 * 
	 * @throws IOException
	 *             IO failure
	 * @throws SVNException
	 *             SVN failure
	 * @throws SystemException
	 *             System failure
	 * @throws TeamAuthentificationException
	 *             authorization failed
	 */
	@Test
	public void testDeleteTestSuite() throws IOException, SVNException, SystemException, TeamAuthentificationException {

		String testPageName = "MyFirstSuite";

		TestProject testProject = createTestProject(REPOSITORY_PATH, "", "");
		teamService.share(testProject, translationService, "");

		// Add new TestSuite
		Path createdNewSuite = SvnHelper.createNewTestPage(projectpath + "/FitNesseRoot/" + PROJEKT_NAME, testPageName);

		TestSuite testSuite = new TestSuite();
		testSuite.setName(testPageName);

		testProject.addChild(testSuite);
		TestStructure parent = testSuite.getParent();

		String testCaseName = "MyFirstTestCase";
		Path createdNewTestPage = SvnHelper.createNewTestPage(projectpath + "/FitNesseRoot/" + PROJEKT_NAME + "/"
				+ testPageName, testCaseName);

		TestCase testPage = new TestCase();
		testPage.setName(testCaseName);

		testSuite.addChild(testPage);

		teamService.approve(testSuite, translationService, "");

		testStatus(testPageName, testProject, testCaseName, "normal 1");

		teamService.delete(testSuite, translationService);
		testStatus(testPageName, testProject, testCaseName, "deleted 4");

		teamService.approve(testProject, translationService, "");
		testStatus(testPageName, testProject, testCaseName, "unversioned 5");

		FileUtils.deleteDirectory(createdNewSuite.toFile());
		testStatus(testPageName, testProject, testCaseName, "deleted 4");
		teamService.update(parent, translationService);
		assertFalse(createdNewTestPage.toFile().exists());
		assertFalse(createdNewSuite.toFile().exists());

	}

	/**
	 * test the svn-status of a teststructure.
	 * 
	 * @param testPageName
	 *            the name of the parentPage
	 * @param testProject
	 *            the TestProject
	 * @param testCaseName
	 *            the name of the testcase
	 * @param status
	 *            the status as string
	 * @throws SystemException
	 *             on operation
	 */
	private void testStatus(String testPageName, TestProject testProject, String testCaseName, String status)
			throws SystemException {
		String statusInfo;
		String[] statusLines;
		statusInfo = teamService.getStatus(testProject, translationService);
		statusLines = statusInfo.split("\n");
		for (String line : statusLines) {
			if (line.contains(testPageName) || line.contains(testCaseName)) {
				assertTrue(line.contains(status));
			}
		}
	}

	/**
	 * Test for
	 * {@link SVNTeamShareService#doDelete(org.testeditor.core.model.teststructure.TestSuite)}
	 * Method and reverts to the previous status.
	 * 
	 * @throws IOException
	 *             IO failure
	 * @throws SVNException
	 *             SVN failure
	 * @throws SystemException
	 *             System failure
	 * @throws TeamAuthentificationException
	 *             authorization failed
	 */
	@Test
	public void testDeleteTestSuiteAndRevert() throws IOException, SVNException, SystemException,
			TeamAuthentificationException {

		String testPageName = "MyFirstSuite";

		TestProject testProject = createTestProject(REPOSITORY_PATH, "", "");
		teamService.share(testProject, translationService, "");

		// Add new TestSuite
		Path createdNewSuite = SvnHelper.createNewTestPage(projectpath + "/FitNesseRoot/" + PROJEKT_NAME, testPageName);

		TestSuite testSuite = new TestSuite();
		testSuite.setName(testPageName);

		testProject.addChild(testSuite);

		String testCaseName = "MyFirstTestCase";
		Path createdNewTestPage = SvnHelper.createNewTestPage(projectpath + "/FitNesseRoot/" + PROJEKT_NAME + "/"
				+ testPageName, testCaseName);

		TestCase testPage = new TestCase();
		testPage.setName(testCaseName);

		testSuite.addChild(testPage);

		teamService.approve(testSuite, translationService, "");

		String statusInfo = teamService.getStatus(testProject, translationService);
		String[] statusLines = statusInfo.split("\n");
		for (String line : statusLines) {
			if (line.contains(testPageName) || line.contains(testCaseName)) {
				assertTrue(line.contains("normal 1"));
			}
		}

		teamService.delete(testPage, translationService);
		statusInfo = teamService.getStatus(testProject, translationService);
		statusLines = statusInfo.split("\n");
		for (String line : statusLines) {
			if (line.contains(testCaseName)) {
				assertTrue(line.contains("deleted 4"));
			}
		}

		FileUtils.deleteDirectory(createdNewSuite.toFile());
		statusInfo = teamService.getStatus(testProject, translationService);
		statusLines = statusInfo.split("\n");
		for (String line : statusLines) {
			if (line.contains(testCaseName)) {
				assertTrue(line.contains("deleted 4"));
			}
		}
		teamService.update(testSuite, translationService);
		teamService.update(testPage, translationService);
		statusInfo = teamService.getStatus(testProject, translationService);
		assertTrue(createdNewSuite.toFile().exists());

		statusLines = statusInfo.split("\n");
		for (String line : statusLines) {
			if (line.contains(testCaseName)) {
				assertTrue(line.contains("deleted 4"));
			}
		}
		assertFalse(createdNewTestPage.toFile().exists());
		assertTrue(createdNewSuite.toFile().exists());

	}

	/**
	 * Tests the ignore list.
	 * 
	 * @throws SystemException
	 *             System failure
	 */
	@Test
	public void testIgnoreListPositive() throws SystemException {
		TestProject testProject = createTestProject(REPOSITORY_PATH, "", "");
		teamService.share(testProject, translationService, "");

		String[] ignoreList = SVNTeamShareService.IGNORE_LIST;

		Collection<File> listFiles = FileUtils.listFilesAndDirs(new File(targetWorkspacePath), TrueFileFilter.INSTANCE,
				TrueFileFilter.INSTANCE);

		for (File file : listFiles) {
			for (String ignoreItem : ignoreList) {
				assertNotEquals("File/Directory: " + file.getName() + " must not be shared !", file.getName(),
						ignoreItem);
			}
		}
	}

	/**
	 * Negative test for ignore list. Directory "DemoWebTests" must not be
	 * found.
	 * 
	 * @throws SystemException
	 *             System failure
	 */
	@Test
	public void testIgnoreListNegative() throws SystemException {
		TestProject testProject = createTestProject(REPOSITORY_PATH, "", "");
		teamService.share(testProject, translationService, "");

		String[] ignoreList = { "DemoWebTests" };

		Collection<File> listFiles = FileUtils.listFilesAndDirs(new File(targetWorkspacePath), TrueFileFilter.INSTANCE,
				TrueFileFilter.INSTANCE);

		boolean found = false;
		// traverse Filelist and check if ignoreList matches.
		for (File file : listFiles) {
			for (String ignoreItem : ignoreList) {

				if (file.getName().equals(ignoreItem)) {
					found = true;
					break;
				}
			}
		}

		assertTrue("Directory 'DemoWebTests' was not found", found);

	}

	/**
	 * Test for sharing project twice on local file system.
	 * 
	 * @throws IOException
	 *             IO failure
	 * @throws SVNException
	 *             SVN failure
	 * @throws SystemException
	 *             System failure
	 */
	@Test
	public void testShareProjectTwice() throws IOException, SVNException, SystemException {

		TestProject testProject = createTestProject(REPOSITORY_PATH, "", "");
		teamService.share(testProject, translationService, "");
		try {
			teamService.share(testProject, translationService, "");
		} catch (Exception e) {
			assertTrue(e.getMessage().startsWith("translated key %svnE160020"));
		}
	}

	/**
	 * Test for sharing project without repos_path on local file system.
	 * 
	 * @throws IOException
	 *             IO failure
	 * @throws SVNException
	 *             SVN failure
	 * @throws SystemException
	 *             System failure
	 */
	@Test
	public void testShareProjectWithoutReposPath() throws IOException, SVNException, SystemException {

		TestProject testProject = createTestProject("", "", "");
		try {
			teamService.share(testProject, translationService, "");
		} catch (Exception e) {
			assertTrue(e.getMessage().startsWith("translated key %svnE125002" + " /DemoWebTests"));
		}
	}

	/**
	 * Tests that a Config with an empty url is invalid.
	 */
	@Test
	public void testConfigurationIsInValidOnEmptyURL() {
		TestProject testProject = createTestProject("moreStuff", "", "");
		try {
			teamService.validateConfiguration(testProject, translationService);
			fail("ValidateConfiguration should throw Exception on empty url.");
		} catch (SystemException e) {
			LOGGER.equals(e);
		}
	}

	/**
	 * Tests that a Config with a not existing TestProject in the SVN Repo is
	 * invalid.
	 * 
	 * @throws SVNException
	 *             on wrong test setup
	 */
	@Test
	public void testConfigurationIsInValidOnNotExistingProjectName() throws SVNException {
		TestProject testProject = createTestProject(SVNURL.fromFile(new File(REPOSITORY_PATH)).toDecodedString(), "",
				"");
		testProject.setName("Not Existing Project");
		try {
			teamService.validateConfiguration(testProject, translationService);
			fail("ValidateConfiguration should throw Exception on not existing Project.");
		} catch (SystemException e) {
			LOGGER.equals(e);
		}
	}

	/**
	 * Tests that a config with URL and an existing folder name is valid. For an
	 * URL is en empty name an exiting name.
	 * 
	 * @throws Exception
	 *             on SVN error.
	 */
	@Test
	public void testConfigurationIsValid() throws Exception {
		TestProject testProject = createTestProject(SVNURL.fromFile(new File(REPOSITORY_PATH)).toDecodedString(), "",
				"");
		testProject.setName("");
		assertTrue("Expecting Config is valid.", teamService.validateConfiguration(testProject, translationService));
	}

	/**
	 * Tests if the SVN exception E175005 is substituted.
	 */
	@Test
	public void substitudeSVNExceptionE175005Test() {
		SVNException exception = new SVNException(SVNErrorMessage.create(SVNErrorCode.RA_DAV_ALREADY_EXISTS,
				"Path 'myLocation/myFile' already exists"));

		assertTrue(substitudeSVNException(exception, translationService).startsWith("translated key %svnE175005"));
	}

	/**
	 * Tests if the SVN exception E170001 is substituted.
	 */
	@Test
	public void substitudeSVNExceptionE170001Test() {
		String message = "Authentication required for '<http://localhost:80> Subversion Repo'";
		SVNException exception = new SVNException(SVNErrorMessage.create(SVNErrorCode.RA_NOT_AUTHORIZED, message));
		assertTrue(substitudeSVNException(exception, translationService).startsWith(
				"translated key %svnE170001 " + message.split("'")[1]));
	}

	/**
	 * Tests if the SVN exception E160020 is substituted.
	 */
	@Test
	public void substitudeSVNExceptionE160020Test() {
		String message = "File already exists: filesystem 'org.testeditor.teamshare.svn.test/testrepo/db', transaction '1-1', path '/DemoWebTests/AllActionGroups.xml'";
		SVNException exception = new SVNException(SVNErrorMessage.create(SVNErrorCode.FS_ALREADY_EXISTS, message));
		assertEquals(
				"translated key %svnE160020 org.testeditor.teamshare.svn.test/testrepo/db /DemoWebTests/AllActionGroups.xml",
				substitudeSVNException(exception, translationService));
	}

	/**
	 * Tests if the SVN exception E175002 is substituted.
	 */
	@Test
	public void substitudeSVNExceptionE175002Test() {
		String message = "OPTIONS of '/svnrep/qrx/rtz/XXX/fitnessedemo/test/TestEditorTests': 500 Internal Server Error (http://anyhost)";
		SVNException exception = new SVNException(SVNErrorMessage.create(SVNErrorCode.RA_DAV_REQUEST_FAILED, message));
		assertEquals("translated key %svnE175002 /svnrep/qrx/rtz/XXX/fitnessedemo/test/TestEditorTests {1}",
				substitudeSVNException(exception, translationService));
	}

	/**
	 * Tests if the SVN exception E160024 is substituted.
	 */
	@Test
	public void substitudeSVNExceptionE160024Test() {
		SVNException exception = new SVNException(
				SVNErrorMessage
						.create(SVNErrorCode.FS_CONFLICT,
								"Approval is not possible because another user changed and approved test \n\n 'myLocation/myFile' \n\n Please contact your test manager or project administrator."));

		assertTrue(substitudeSVNException(exception, translationService).contains("E160024"));
	}

	/**
	 * Tests if the SVN exception E155015 is substituted.
	 */
	@Test
	public void substitudeSVNExceptionE155015Test() {
		SVNException exception = new SVNException(
				SVNErrorMessage
						.create(SVNErrorCode.WC_FOUND_CONFLICT,
								"Approval is not possible because another user changed and approved test \n\n 'myLocation/myFile' \n\n Please contact your test manager or project administrator."));

		assertTrue(substitudeSVNException(exception, translationService).contains("E155015"));
	}

	/**
	 * Tests if the conflict message is created correctly.
	 */
	@Test
	public void createConflictErrorMessageTest() {
		List<String> conflicts = new ArrayList<>();
		String expectedResult = "translated key %svn.conflict.message " + "\n" + "/test/test/conflict" + "\n\n"
				+ " {1}";
		conflicts.add("/test/test/conflict");
		String createConflictErrorMessage = ((SVNTeamShareService) teamService).createConflictErrorMessage(conflicts,
				translationService);
		assertEquals("messages should be equals", expectedResult, createConflictErrorMessage);
	}

	/**
	 * Checks WC state.
	 * 
	 * @throws SVNException
	 *             on operation.
	 */
	@Test
	public void checkWcStateTest() throws SVNException {

		List<String> expectedResult = new ArrayList<>();
		expectedResult.add("/test/conflictTest-0".replace('/', File.separatorChar));
		expectedResult.add("/test/conflictTest-1".replace('/', File.separatorChar));
		List<String> checkWcState = ((SVNTeamShareService) teamService).checkWcState(createSVNStatusClientMock(), null,
				0L);

		Assert.assertArrayEquals(expectedResult.toArray(), checkWcState.toArray());
	}

	/**
	 * Mock for SVN status client.
	 * 
	 * @return SVN status
	 */
	private SVNStatusClient createSVNStatusClientMock() {
		return new SVNStatusClient(SVNWCUtil.createDefaultAuthenticationManager("", ""), null) {

			@Override
			public long doStatus(File path, SVNRevision revision, SVNDepth depth, boolean remote, boolean reportAll,
					boolean includeIgnored, boolean collectParentExternals, ISVNStatusHandler handler,
					Collection<String> changeLists) throws SVNException {
				SVNStatus svnStatus = new SVNStatus();
				svnStatus.setIsConflicted(true);
				svnStatus.setFile(new File("/test/conflictTest-0"));
				handler.handleStatus(svnStatus);

				SVNStatus svnStatus2 = new SVNStatus();
				svnStatus2.setIsConflicted(true);
				svnStatus2.setFile(new File("/test/conflictTest-1"));
				handler.handleStatus(svnStatus2);

				SVNStatus svnStatusNonConfilct = new SVNStatus();
				svnStatusNonConfilct.setFile(new File("/test/conflictTest- Non conflict"));
				handler.handleStatus(svnStatusNonConfilct);
				return 0L;
			}

		};
	}

	/**
	 * delegates the translation to the SVNTeamShareTranslateExceptions.class.
	 * 
	 * @param e
	 *            Exception
	 * @param translationService
	 *            TranslationService
	 * @return the translated message
	 */
	private String substitudeSVNException(SVNException e, TranslationService translationService) {
		return new SVNTeamShareTranslateExceptions().substitudeSVNException(e, translationService);
	}

	/**
	 * Test for {@link
	 * SVNTeamShareServiceRevert(org.testeditor.core.model.teststructure.
	 * TestStructure)} Method.
	 * 
	 * @throws IOException
	 *             IO failure
	 * @throws SVNException
	 *             SVN failure
	 * @throws SystemException
	 *             System failure
	 */
	@Test
	public void testRevertTest() throws IOException, SVNException, SystemException {

		String testPageName = "MyFirstTestRevertTest";

		TestProject testProject = createTestProject(REPOSITORY_PATH, "", "");
		teamService.share(testProject, translationService, "");

		// Add new Testpage
		SvnHelper.createNewTestPage(projectpath + "/FitNesseRoot/" + PROJEKT_NAME, testPageName);

		TestCase testCase = new TestCase();
		testCase.setName(testPageName);

		TestSuite testSuite = new TestSuite();
		testSuite.addChild(testCase);

		testProject.addChild(testSuite);

		teamService.approve(testSuite, translationService, "");

		TestCase secondTestCase = new TestCase();
		secondTestCase.setName("SecondTest");

		String status = teamService.getStatus(testProject, translationService);

		teamService.approve(testProject, translationService, "RevertTest");

		status = teamService.getStatus(testProject, translationService);

		File updateFile = new File(projectpath + "/TechnicalBindingTypeCollection.xml");
		String appendedString = "This is a SVN-reverting Test";
		SvnHelper.updateFile(updateFile, appendedString);

		String readFileToString = FileUtils.readFileToString(updateFile);
		Assert.assertTrue(readFileToString.endsWith(appendedString));

		status = teamService.getStatus(testProject, translationService);
		Assert.assertTrue(status.contains("TechnicalBindingTypeCollection.xml;modified 2"));

		teamService.revert(testProject, translationService);

		String newstatus = teamService.getStatus(testProject, translationService);
		Assert.assertTrue(newstatus.contains("TechnicalBindingTypeCollection.xml;normal 1"));

		readFileToString = FileUtils.readFileToString(updateFile);
		Assert.assertFalse(readFileToString.contains(appendedString));

	}

	/**
	 * Tests the rename of an teststructure over svn team share.
	 * 
	 * @throws Exception
	 *             on Backend Error.
	 */
	@Test
	public void testRenameTestStructure() throws Exception {
		String testPageName = "MyTestStructure";

		TestProject testProject = createTestProject(REPOSITORY_PATH, "", "");
		teamService.share(testProject, translationService, "");

		// Add new TestSuite
		Path testStructurePath = SvnHelper.createNewTestPage(projectpath + "/FitNesseRoot/" + PROJEKT_NAME,
				testPageName);
		assertTrue(testStructurePath.toFile().exists());
		TestCase testCase = new TestCase();
		testCase.setName(testPageName);
		testProject.addChild(testCase);
		teamService.addChild(testCase, translationService);
		teamService.approve(testCase, translationService, "");
		teamService.rename(testCase, "myNewName", translationService);
		assertFalse(testStructurePath.toFile().exists());
		assertTrue(new File(projectpath + "/FitNesseRoot/" + PROJEKT_NAME + "/" + "myNewName").exists());
	}

	/**
	 * Test for sharing project twice on local file system.
	 * 
	 * @throws IOException
	 *             IO failure
	 * @throws SVNException
	 *             SVN failure
	 * @throws SystemException
	 *             System failure
	 */
	@Test
	public void testShareProjectDisconnect() throws IOException, SVNException, SystemException {
		TeamShareStatusServicePlugIn statusPlugin = new SVNTeamShareStatusService();
		((SVNTeamShareService) teamService).bind(statusPlugin);

		TestProject testProject = createTestProject(REPOSITORY_PATH, "", "");
		teamService.share(testProject, translationService, "");

		assertTrue(testProject.getTestProjectConfig().isTeamSharedProject());

		teamService.disconnect(testProject, translationService);

		assertFalse(testProject.getTestProjectConfig().isTeamSharedProject());
	}

	/**
	 * Test that the revert operation also removes added and not commited files
	 * from the workspace.
	 * 
	 * @throws SystemException
	 *             on test failure.
	 * @throws IOException
	 *             on test failure.
	 */
	@Test
	public void testRevertWithAddedTestcase() throws SystemException, IOException {
		String testPageName = "MyFirstTestRevertTest";

		TestProject testProject = createTestProject(REPOSITORY_PATH, "", "");
		teamService.share(testProject, translationService, "");

		String pathToTestFiles = testProject.getTestProjectConfig().getProjectPath() + File.separatorChar
				+ "FitNesseRoot" + File.separatorChar + testProject.getName();
		// Add new Testpage
		SvnHelper.createNewTestPage(pathToTestFiles, testPageName);

		TestSuite testSuite = new TestSuite();
		String suiteName = "SuiteName";
		testSuite.setName(suiteName);
		testProject.addChild(testSuite);
		SvnHelper.createNewTestPage(pathToTestFiles, suiteName);
		teamService.addChild(testSuite, translationService);

		TestCase testCase = new TestCase();
		SvnHelper
				.createNewTestPage(pathToTestFiles + File.separatorChar + suiteName + File.separatorChar, testPageName);
		testCase.setName(testPageName);
		testSuite.addChild(testCase);
		teamService.addChild(testCase, translationService);

		testSuite.addChild(testCase);

		testProject.addChild(testSuite);
		teamService.addChild(testSuite, translationService);
		teamService.approve(testSuite, translationService, "");

		String secPageName = "SecondTest";
		TestCase secondTestCase = new TestCase();
		secondTestCase.setName(secPageName);
		// Add new Testpage

		SvnHelper.createNewTestPage(pathToTestFiles, secPageName);

		String status = teamService.getStatus(testProject, translationService);

		teamService.approve(testProject, translationService, "RevertTest");

		status = teamService.getStatus(testProject, translationService);

		File updateFile = new File(testProject.getTestProjectConfig().getProjectPath()
				+ "/TechnicalBindingTypeCollection.xml");
		String appendedString = "This is a SVN-reverting Test";
		SvnHelper.updateFile(updateFile, appendedString);

		String readFileToString = FileUtils.readFileToString(updateFile);
		Assert.assertTrue(readFileToString.endsWith(appendedString));

		status = teamService.getStatus(testProject, translationService);
		Assert.assertTrue(status.contains("TechnicalBindingTypeCollection.xml;modified 2"));

		TestCase sctestCase = new TestCase();
		String scTestPageName = "secondTestCase";
		SvnHelper.createNewTestPage(pathToTestFiles + File.separatorChar + suiteName + File.separatorChar,
				scTestPageName);
		sctestCase.setName(scTestPageName);
		testSuite.addChild(sctestCase);
		teamService.addChild(sctestCase, translationService);

		String secStatus = teamService.getStatus(testProject, translationService);
		secStatus.contains(scTestPageName);

		teamService.revert(testProject, translationService);

		String newstatus = teamService.getStatus(testProject, translationService);
		Assert.assertTrue(newstatus.contains("TechnicalBindingTypeCollection.xml;normal 1"));

		Assert.assertFalse(SvnHelper.existsTestCase(pathToTestFiles + File.separatorChar + suiteName
				+ File.separatorChar, scTestPageName));

		readFileToString = FileUtils.readFileToString(updateFile);
		Assert.assertFalse(readFileToString.contains(appendedString));
	}

	/**
	 * Tests that a revert of an teststructure ensures, that the parents lazy
	 * loader will run next time again and reload the subtree of the
	 * teststructure.
	 */
	@Test
	public void testRevertMemoryModel() {
		SVNTeamShareService svnService = (SVNTeamShareService) teamService;
		TestSuite testSuite = new TestSuite();
		final HashSet<String> set = new HashSet<String>();
		testSuite.setLazyLoader(new Runnable() {

			@Override
			public void run() {
				set.add("run");
			}
		});
		TestCase testCase = new TestCase();
		testSuite.addChild(testCase);
		assertFalse(set.contains("run"));
		svnService.revertMemoryModel(testCase);
		testSuite.getTestChildren();
		assertTrue(set.contains("run"));
	}

	/**
	 * Test the disconnect operation of a shared project.
	 * 
	 * @throws Exception
	 *             on SVN error.
	 */
	@Test
	public void testDisconnectProject() throws Exception {
		TestProject testProject = createTestProject(System.getProperty("java.io.tmpdir") + File.separator
				+ "disconnectPrj", "", "");
		TestSuite testSuite = new TestSuite();
		testProject.addChild(testSuite);
		TestCase testCase = new TestCase();
		testSuite.addChild(testCase);
		TeamShareStatusServicePlugIn statusPlugin = new SVNTeamShareStatusService();
		((SVNTeamShareService) teamService).bind(statusPlugin);
		teamService.disconnect(testProject, translationService);
		assertFalse(statusPlugin.isModified(testProject));
		assertNull("Expecting test project is removed from statsus service.", statusPlugin.getModified(testProject));
	}

}
