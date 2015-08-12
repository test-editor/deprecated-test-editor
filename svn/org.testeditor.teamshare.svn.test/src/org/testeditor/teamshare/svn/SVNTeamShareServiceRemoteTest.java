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

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.exceptions.TeamAuthentificationException;
import org.testeditor.core.model.teststructure.TestCase;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestProjectConfig;
import org.testeditor.core.model.teststructure.TestSuite;
import org.testeditor.core.services.interfaces.TeamShareService;
import org.testeditor.teamshare.svn.util.SvnHelper;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;

/**
 * Tests the {@link SVNTeamShareService} on remote svn repository. All tests
 * have to be ignored because the test ar designed for running manuell.
 * 
 * Please checkin this junit class only with ignored methods !!!
 * 
 * 
 * 
 */
@Ignore("only for debugging")
public class SVNTeamShareServiceRemoteTest {

	private static final String USERNAME = "***";
	private static final String PASSWORD = "***";

	private static final String SOURCE_WORKSPACE_PATH = "./testProject";
	private static final String REPOSITORY_PATH = "***";

	private static final String PROJEKT_NAME = "DemoWebTests";

	private static final String TARGET_WORKSPACE_PATH = System.getProperty("java.io.tmpdir") + "/testProjectJUnit";

	private static final String PROJEKTPATH = TARGET_WORKSPACE_PATH + "/" + PROJEKT_NAME;

	private TeamShareService teamService;

	private TranslationService translationService = new TranslationServiceAdapter().getTranslationService();

	/**
	 * Setup for testing.
	 * 
	 * @throws Exception
	 *             if file access fails
	 */
	@Before
	public void setUp() throws Exception {

		System.setProperty("default.svn.comment", "[TE-1] default comment.");

		teamService = new SVNTeamShareService();

		SVNRepositoryFactoryImpl.setup();

		FileUtils.deleteDirectory(new File(TARGET_WORKSPACE_PATH));
		FileUtils.copyDirectory(new File(SOURCE_WORKSPACE_PATH), new File(TARGET_WORKSPACE_PATH));

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

		testProjectConfig.setProjectPath(PROJEKTPATH);

		SVNTeamShareConfig svnTeamShareConfig = new SVNTeamShareConfig();

		svnTeamShareConfig.setUrl(repositoryPath);
		svnTeamShareConfig.setUserName(userName);
		svnTeamShareConfig.setPassword(password);

		testProjectConfig.setTeamShareConfig(svnTeamShareConfig);

		testProject.setTestProjectConfig(testProjectConfig);

		return testProject;

	}

	/**
	 * Test for sharing Project on remote repository.
	 * 
	 * @throws SystemException
	 *             System failure
	 * 
	 */
	@Ignore("only for debugging")
	public void testShareProject() throws SystemException {

		TestProject testProject = createTestProject(REPOSITORY_PATH, USERNAME, PASSWORD);
		teamService.share(testProject, translationService, "");

	}

	/**
	 * Commit a new test page on remote repository.
	 * 
	 * @throws IOException
	 *             IO failure
	 * @throws SVNException
	 *             SVN failure
	 * @throws SystemException
	 *             System failure
	 */
	@Ignore("only for debugging")
	public void testCommitTest() throws IOException, SVNException, SystemException {

		String testPageName = "MyFirstTest";

		TestProject testProject = createTestProject(REPOSITORY_PATH, USERNAME, PASSWORD);
		teamService.share(testProject, translationService, "");

		SvnHelper.createNewTestPage(PROJEKTPATH + "/FitNesseRoot/" + PROJEKT_NAME, testPageName);

		TestCase testCase = new TestCase();
		testCase.setName(testPageName);

		testProject.addChild(testCase);

		teamService.approve(testCase, translationService, "");

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
	 */
	@Ignore("only for debugging")
	public void testCommitSuiteWithOneTest() throws IOException, SVNException, SystemException {

		TestProject testProject = createTestProject(REPOSITORY_PATH, USERNAME, PASSWORD);
		teamService.share(testProject, translationService, "");

		String suiteName = "Suite1";
		String testName = "Test1";

		// Add new Suite
		Path suitePath = SvnHelper.createNewTestPage(PROJEKTPATH + "/FitNesseRoot/" + PROJEKT_NAME, suiteName);
		// Add new Test
		SvnHelper.createNewTestPage(suitePath.toString(), testName.toString());

		TestSuite testSuite = new TestSuite();
		testSuite.setName(suiteName);

		TestCase testCase = new TestCase();
		testCase.setName(testName);

		testSuite.addChild(testCase);

		testProject.addChild(testSuite);

		teamService.approve(testSuite, translationService, "");
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
	@Ignore("only for debugging")
	public void testUpdateSuite() throws IOException, SVNException, SystemException, TeamAuthentificationException {

		TestProject testProject = createTestProject(REPOSITORY_PATH, USERNAME, PASSWORD);
		teamService.share(testProject, translationService, "");

		// delete some tests locally
		File loginSuite = new File(PROJEKTPATH + "/FitNesseRoot/" + PROJEKT_NAME + "/LocalDemoSuite/LoginSuite");
		FileUtils.deleteDirectory(loginSuite);

		// deletion was successfull
		assertTrue(!loginSuite.exists());

		TestSuite localDemoSuite = new TestSuite();
		localDemoSuite.setName("LocalDemoSuite");
		testProject.addChild(localDemoSuite);

		// update local Project
		teamService.update(localDemoSuite, translationService);

		// update was successfull
		assertTrue(!loginSuite.exists());

	}

	/**
	 * Tests the ignore list.
	 * 
	 * @throws SystemException
	 *             System failure
	 */
	@Ignore("only for debugging")
	public void testIgnoreListPositive() throws SystemException {

		TestProject testProject = createTestProject(REPOSITORY_PATH, "", "");
		teamService.share(testProject, translationService, "");

		String[] ignoreList = SVNTeamShareService.IGNORE_LIST;

		Collection<File> listFiles = FileUtils.listFilesAndDirs(new File(TARGET_WORKSPACE_PATH),
				TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);

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
	@Ignore("only for debugging")
	public void testIgnoreListNegative() throws SystemException {
		String[] ignoreList = { "DemoWebTests" };

		Collection<File> listFiles = FileUtils.listFilesAndDirs(new File(TARGET_WORKSPACE_PATH),
				TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);

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
	 * try to revert the localChanges.
	 * 
	 * HINT: the function is not implemented yet!
	 * 
	 * @throws SystemException
	 *             System failure
	 * @throws IOException
	 *             IO failure
	 */
	@Ignore("only for debugging")
	public void testRevertWithAddedTestcase() throws SystemException, IOException {
		String testPageName = "MyFirstTestRevertTest";

		TestProject testProject = createTestProject(REPOSITORY_PATH, USERNAME, PASSWORD);
		teamService.share(testProject, translationService, "");

		String pathToTestFiles = testProject.getTestProjectConfig().getProjectPath() + File.separatorChar
				+ "FitNesseRoot" + File.separatorChar + testProject.getName();
		// Add new Testpage
		SvnHelper.createNewTestPage(pathToTestFiles, testPageName);

		TestCase testCase = new TestCase();
		testCase.setName(testPageName);

		TestSuite testSuite = new TestSuite();
		testSuite.addChild(testCase);

		testProject.addChild(testSuite);

		teamService.approve(testSuite, translationService, "");

		String secPageName = "SecondTest";
		TestCase secondTestCase = new TestCase();
		secondTestCase.setName(secPageName);
		// Add new Testpage

		SvnHelper.createNewTestPage(pathToTestFiles, secPageName);

		String status = teamService.getStatus(testProject, translationService);

		teamService.approve(testProject, translationService, "RevertTest");

		status = teamService.getStatus(testProject, translationService);

		File updateFile = new File(PROJEKTPATH + "/TechnicalBindingTypeCollection.xml");
		String appendedString = "This is a SVN-reverting Test";
		SvnHelper.updateFile(updateFile, appendedString);

		String readFileToString = FileUtils.readFileToString(updateFile);
		Assert.assertTrue(readFileToString.endsWith(appendedString));

		status = teamService.getStatus(testProject, translationService);
		Assert.assertTrue(status.contains("TechnicalBindingTypeCollection.xml;modified 2"));

		TestCase sctestCase = new TestCase();
		String scTestPageName = "secondTestCase";
		SvnHelper.createNewTestPage(pathToTestFiles, scTestPageName);
		sctestCase.setName(scTestPageName);
		testSuite.addChild(sctestCase);
		teamService.addChild(sctestCase, translationService);

		String secStatus = teamService.getStatus(testProject, translationService);
		secStatus.contains(scTestPageName);

		teamService.revert(testProject, translationService);

		String newstatus = teamService.getStatus(testProject, translationService);
		Assert.assertTrue(newstatus.contains("TechnicalBindingTypeCollection.xml;normal 1"));

		Assert.assertFalse(SvnHelper.existsTestCase(pathToTestFiles, scTestPageName));

		readFileToString = FileUtils.readFileToString(updateFile);
		Assert.assertFalse(readFileToString.contains(appendedString));

	}

}
