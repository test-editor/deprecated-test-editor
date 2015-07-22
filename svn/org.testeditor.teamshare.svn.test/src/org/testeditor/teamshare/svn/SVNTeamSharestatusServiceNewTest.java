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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IContextFunction;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.osgi.framework.FrameworkUtil;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.model.teststructure.TestCase;
import org.testeditor.core.model.teststructure.TestCompositeStructure;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestProjectConfig;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.model.teststructure.TestSuite;
import org.testeditor.core.services.interfaces.TeamShareService;
import org.testeditor.core.services.interfaces.TeamShareStatusServiceNew;
import org.testeditor.teamshare.svn.util.SvnHelper;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;

public class SVNTeamSharestatusServiceNewTest {

	List<TestStructure> testStructures;
	TeamShareStatusServiceNew teamShareStatusServiceNew;

	private static final String SOURCE_WORKSPACE_PATH = "./testProject";
	private static final String REPOSITORY_PATH = "./testrepo";

	private static final String PROJEKT_NAME = "DemoWebTests";

	private static String targetWorkspacePath;

	private static String projectpath;
	private static final Logger LOGGER = Logger.getLogger(SVNTeamShareService.class);

	private TeamShareService teamService;
	private TeamShareStatusServiceNew statusService;

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
		statusService = new SVNTeamShareStatusServiceNew();

		SVNRepositoryFactoryImpl.setup();

		FileUtils.copyDirectory(new File(SOURCE_WORKSPACE_PATH), new File(targetWorkspacePath));

		FileUtils.deleteDirectory(new File(REPOSITORY_PATH));
		SVNRepositoryFactory.createLocalRepository(new File(REPOSITORY_PATH), true, false);

		buildContext();
	}

	/**
	 * Removes temporary created SVN.
	 * 
	 * @throws IOException
	 *             on File cleanups.
	 */
	@After
	public void cleanUpLocalSVN() throws IOException {
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

	private TestStructure createTestStructure(String... strings) {

		TestStructure testStructure = null;
		TestProject testProject = new TestProject();

		TestStructure lastStructure = null;

		for (int i = 0; i < strings.length; i++) {
			if (i == 0) {
				testProject.setName(strings[i]);
				lastStructure = testProject;
			} else if (i == strings.length - 1) {
				testStructure = new TestCase();
				testStructure.setName(strings[i]);
				((TestCompositeStructure) lastStructure).addChild(testStructure);
				lastStructure = testStructure;

			} else {
				testStructure = new TestSuite();
				testStructure.setName(strings[i]);
				((TestCompositeStructure) lastStructure).addChild(testStructure);
				lastStructure = testStructure;
			}

		}

		return testStructure;
	}

	private void update(String filePath) throws IOException {
		File updateFile = new File(projectpath + filePath);
		String appendString = "do Something";
		SvnHelper.updateFile(updateFile, appendString);
	}

	/**
	 * Returns the list of modificated teststructures for given project
	 * 
	 * @throws SystemException
	 * @throws IOException
	 */
	@Test
	public void testGetModifiedEmpty() throws SystemException, IOException {

		// given no modifications

		// when
		List<String> teststructures = statusService.getModified(new TestProject());

		// then
		assertEquals(null, teststructures);

	}

	/**
	 * Update the internal map of modifications, list must be in synch with SVN
	 * state
	 * 
	 * @throws SystemException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@Test
	public void testUpdateModified() throws SystemException, IOException, InterruptedException {

		// given
		TestProject testProject = createTestProject(REPOSITORY_PATH, "", "");
		teamService.share(testProject, translationService, "");
		update("/FitNesseRoot/DemoWebTests/LocalDemoSuite/LoginSuite/LoginValidTest/content.txt");

		// when
		statusService.update(testProject);
		// because update method runs in a thread, waits here until thread has
		// ended.
		getThreadByName("threadStatusService").join();

		// then
		List<String> teststructures = statusService.getModified(testProject);
		assertEquals(1, teststructures.size());
		assertTrue(teststructures.get(0).contains(
				"DemoWebTests.LocalDemoSuite.LoginSuite.LoginValidTest".replace('.', File.separatorChar)));

	}

	/**
	 * Checks if given teststructure is modificated
	 * 
	 * @throws SystemException
	 * @throws IOException
	 * @throws InterruptedException
	 * 
	 */
	@Test
	public void testIsModified() throws SystemException, IOException, InterruptedException {

		// given
		TestProject testProject = createTestProject(REPOSITORY_PATH, "", "");
		teamService.share(testProject, translationService, "");
		update("/FitNesseRoot/DemoWebTests/LocalDemoSuite/LoginSuite/LoginValidTest/content.txt");
		update("/FitNesseRoot/DemoWebTests/LocalDemoSuite/LoginSuite/content.txt");

		// when
		statusService.update(testProject);
		// because update method runs in a thread, waits here until thread has
		// ended.
		getThreadByName("threadStatusService").join();

		// then
		TestStructure testStructure = createTestStructure(new String[] { "DemoWebTests", "LocalDemoSuite",
				"LoginSuite", "LoginValidTest" });
		assertTrue(statusService.isModified(testStructure));

	}

	/**
	 * 
	 */
	private void buildContext() {
		IEclipseContext context = EclipseContextFactory.getServiceContext(FrameworkUtil.getBundle(getClass())
				.getBundleContext());
		context.set(IEventBroker.class, null);
		context.set(TeamShareService.class, new SVNTeamShareService());
		((IContextFunction) statusService).compute(context, null);
	}

	/**
	 * 
	 * @param threadName
	 * @return
	 */
	public Thread getThreadByName(String threadName) {
		for (Thread t : Thread.getAllStackTraces().keySet()) {
			if (t.getName().equals(threadName))
				return t;
		}
		return null;
	}

	/**
	 * Tests that given teststructure is not modified.
	 * 
	 * @throws SystemException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@Test
	public void testIsModifiedWithoutResult() throws SystemException, IOException, InterruptedException {

		// given
		TestProject testProject = createTestProject(REPOSITORY_PATH, "", "");
		teamService.share(testProject, translationService, "");
		update("\\FitNesseRoot\\DemoWebTests\\LocalDemoSuite\\LoginSuite\\LoginValidTest\\content.txt");
		update("\\FitNesseRoot\\DemoWebTests\\LocalDemoSuite\\LoginSuite\\content.txt");

		// when
		statusService.update(testProject);
		// because update method runs in a thread, waits here until thread has
		// ended.
		getThreadByName("threadStatusService").join();

		// then
		TestStructure testStructure = createTestStructure("DemoWebTests", "LocalDemoSuite", "LoginSuite",
				"LoginValidTestNotExists");
		assertFalse(statusService.isModified(testStructure));

	}

	/**
	 * Remove given project from internal state of service
	 * 
	 * @throws SystemException
	 * @throws IOException
	 * @throws InterruptedException
	 * 
	 */
	@Test
	public void testRemove() throws SystemException, IOException, InterruptedException {

		// given
		TestProject testProject = createTestProject(REPOSITORY_PATH, "", "");
		teamService.share(testProject, translationService, "");
		update("\\FitNesseRoot\\DemoWebTests\\LocalDemoSuite\\LoginSuite\\LoginValidTest\\content.txt");

		// when
		statusService.update(testProject);
		// because update method runs in a thread, waits here until thread has
		// ended.
		getThreadByName("threadStatusService").join();

		// then
		assertTrue(statusService.remove(testProject));

	}

	/**
	 * Remove given project from internal state of service. Removing will be not
	 * perform because no project found.
	 * 
	 * @throws SystemException
	 * @throws IOException
	 * @throws InterruptedException
	 * 
	 */
	@Test
	public void testRemoveNoFound() throws SystemException, IOException, InterruptedException {

		// given
		TestProject testProject = new TestProject();
		testProject.setName(PROJEKT_NAME);
		TestProjectConfig testProjectConfig = new TestProjectConfig();
		testProjectConfig.setProjectPath(projectpath);
		testProject.setTestProjectConfig(testProjectConfig);

		// when
		statusService.update(testProject);
		// because update method runs in a thread, waits here until thread has
		// ended.
		getThreadByName("threadStatusService").join();

		// then
		testProject = new TestProject();
		testProject.setName("dummyProject");
		assertFalse(statusService.remove(testProject));

	}

	/**
	 * Tests that given teststructure is not modified.
	 * 
	 * @throws SystemException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@Test(expected = FileNotFoundException.class)
	public void testUpdateIfProjectNoExists() throws SystemException, IOException, InterruptedException {

		// given
		TestProject testProject = new TestProject();
		String projectName = "noProject";
		testProject.setName(projectName);
		TestProjectConfig testProjectConfig = new TestProjectConfig();
		testProjectConfig.setProjectPath(targetWorkspacePath + File.separator + projectName);
		testProject.setTestProjectConfig(testProjectConfig);

		// when
		statusService.update(testProject);

	}

	/**
	 * Tests the state after approve a project.
	 * 
	 * 
	 * @throws SystemException
	 * @throws IOException
	 * @throws InterruptedException
	 * 
	 */
	@Test
	public void testStateAfterApprove() throws SystemException, IOException, InterruptedException {

		// given
		TestProject testProject = createTestProject(REPOSITORY_PATH, "", "");
		teamService.share(testProject, translationService, "");
		update("/FitNesseRoot/DemoWebTests/LocalDemoSuite/LoginSuite/LoginValidTest/content.txt");

		// when
		statusService.update(testProject);
		// because update method runs in a thread, waits here until thread has
		// ended.
		getThreadByName("threadStatusService").join();

		assertTrue(statusService.isModified(testProject));

		teamService.approve(testProject, translationService, "comment");

		statusService.update(testProject);
		// because update method runs in a thread, waits here until thread has
		// ended.
		getThreadByName("threadStatusService").join();

		// then
		assertFalse(statusService.isModified(testProject));

	}

}
