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

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.exceptions.TeamAuthentificationException;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestProjectConfig;
import org.testeditor.core.services.interfaces.TeamShareService;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;

// Dieser Test ist nur manuell unter Windows XP auszuführen.
// Darf nicht im CI Build laufen.
@Ignore
public class SVNTeamShareCredentialsTest {

	// private static final String PROJEKT_NAME = "SVNCredentialss";
	private static final String PROJEKT_NAME = "DemoWebTests";

	private TeamShareService teamService;

	private static final String SVN_CREDENTIALS_DIRECTORY = System.getProperty("user.home") + File.separator
			+ "Anwendungsdaten" + File.separator + "Subversion";

	private static final String TARGET_WORKSPACE_PATH = System.getProperty("java.io.tmpdir") + "/testProjectJUnit";

	private static final String PROJEKTPATH = TARGET_WORKSPACE_PATH + "/" + PROJEKT_NAME;

	// private static final String REPOSITORY_PATH =
	private static final String REPOSITORY_PATH = "http://svn.system/pathToRepositoryArtefact/";

	private TranslationService translationService = new TranslationServiceAdapter().getTranslationService();

	// hier koennen die Credentials fuer das jeweilige verwendete SVN
	// eingetragen.
	private String USERNAME = "XXX";
	private String PASSWORD = "XXX";

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

		FileUtils.deleteDirectory(new File(SVN_CREDENTIALS_DIRECTORY));

	}

	@Test(expected = TeamAuthentificationException.class)
	public void testCheckoutWithoutCredentials() throws SystemException, TeamAuthentificationException {
		// Vorbedingung 1 Credentials sind nicht auf Testsystem vorhanden
		System.out.println(System.getProperty("user.home"));
		Assert.assertTrue(svnCredentialsShouldNotExits());
		// Vorbedingung 2 SVN Projekt ist im Repository vorhanden
		TestProject testProject = createTestProject(REPOSITORY_PATH);
		// teamService.SVN_CREDENTIALS_PATH = SVN_CREDENTIALS_DIRECTORY;

		teamService.checkout(testProject, translationService);
	}

	@Test
	public void testSettingCredentialsAfterTeamauthentificationException() throws SystemException,
			TeamAuthentificationException {
		TestProject testProject = null;

		try {
			// Vorbedingung 1 Credentials sind nicht auf Testsystem vorhanden
			System.out.println(System.getProperty("user.home"));
			Assert.assertTrue(svnCredentialsShouldNotExits());
			// Vorbedingung 2 SVN Projekt ist im Repository vorhanden
			testProject = createTestProject(REPOSITORY_PATH);
			// teamService.SVN_CREDENTIALS_PATH = SVN_CREDENTIALS_DIRECTORY;

			teamService.checkout(testProject, translationService);
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TeamAuthentificationException e) {
			SVNTeamShareConfig teamShareConfig = (SVNTeamShareConfig) testProject.getTestProjectConfig()
					.getTeamShareConfig();
			teamShareConfig.setPassword(PASSWORD);
			teamShareConfig.setUserName(USERNAME);
			teamService.checkout(testProject, translationService);
		}
	}

	private boolean svnCredentialsShouldNotExits() {
		return !Files.exists(Paths.get(SVN_CREDENTIALS_DIRECTORY + File.pathSeparator + "auth"));
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
	private TestProject createTestProject(String repositoryPath) {

		TestProject testProject = new TestProject();
		testProject.setName(PROJEKT_NAME);
		TestProjectConfig testProjectConfig = new TestProjectConfig();

		testProjectConfig.setProjectPath(PROJEKTPATH);

		SVNTeamShareConfig svnTeamShareConfig = new SVNTeamShareConfig();

		svnTeamShareConfig.setUrl(repositoryPath);

		testProjectConfig.setTeamShareConfig(svnTeamShareConfig);

		testProject.setTestProjectConfig(testProjectConfig);

		return testProject;

	}

}
