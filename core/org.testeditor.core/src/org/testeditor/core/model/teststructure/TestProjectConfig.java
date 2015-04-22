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
package org.testeditor.core.model.teststructure;

import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeSet;

import org.testeditor.core.model.action.ProjectLibraryConfig;
import org.testeditor.core.model.team.TeamShareConfig;
import org.testeditor.core.services.interfaces.TestEditorGlobalConstans;

/**
 * Configuration of a TestProject. Beans of this class contains informations
 * about: <li>Fixtures <li>Testmanagement Server
 * 
 */
public class TestProjectConfig {

	private String port = "";
	private String pathToTestFiles = "";
	private ProjectLibraryConfig projectLibraryConfig;
	private String fixtureClass = "";
	private String projectPath = "";
	private TeamShareConfig teamShareConfig;
	private HashMap<String, String> globalProjectVariables = new HashMap<String, String>();
	private String testServerID = "fitnesse_based_1.2";
	private String projectConfigVersion = "";

	/***
	 * 
	 * @param projectPath
	 *            Path in file-system to the folder of the project.
	 */
	public void setProjectPath(String projectPath) {
		this.projectPath = projectPath;
	}

	/**
	 * 
	 * @return the Path to the Test files.
	 */
	public String getPathToTestFiles() {
		return pathToTestFiles;
	}

	/**
	 * 
	 * @param pathToTestFiles
	 *            to the TestFiles
	 */
	public void setPathToTestFiles(String pathToTestFiles) {
		this.pathToTestFiles = pathToTestFiles;
	}

	/**
	 * 
	 * @param port
	 *            of the Testserver
	 */
	public void setPort(String port) {
		this.port = port;
	}

	/**
	 * 
	 * @return the port of the testserver.
	 */
	public String getPort() {
		return port;
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		return toString().equals(obj.toString());
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("test project config ");
		sb.append("port: ").append(port);
		sb.append(" path to TestFiles: ").append(pathToTestFiles);
		sb.append(" ProjectActionGroup: ").append(projectLibraryConfig);
		sb.append(" fixture bundle: ").append(fixtureClass);
		sb.append(" projectpath: ").append(projectPath);
		sb.append(" testServerId: ").append(testServerID);
		sb.append(getGlobalVariablesAsString());
		return sb.toString();
	}

	/**
	 * 
	 * @return the content of the globalProjectVariables as a string with the
	 *         key-value-pairs.
	 */
	private String getGlobalVariablesAsString() {
		StringBuilder sb = new StringBuilder();
		SortedSet<String> keys = new TreeSet<String>(globalProjectVariables.keySet());
		for (String key : keys) {
			if (!key.equalsIgnoreCase(TestEditorGlobalConstans.FIXTURE_JAR_PATH)) {
				sb.append(key).append(": ").append(globalProjectVariables.get(key));
			}
		}
		return sb.toString();
	}

	/**
	 * 
	 * @return Path in Filesystem to the Project folder.
	 */
	public String getProjectPath() {
		return projectPath;
	}

	/**
	 * 
	 * @param fixtureClass
	 *            the fixtureClass.
	 */
	public void setFixtureClass(String fixtureClass) {
		this.fixtureClass = fixtureClass;
	}

	/**
	 * 
	 * @return the fixtureClass.
	 */
	public String getFixtureClass() {
		return fixtureClass;
	}

	/**
	 * this method set the values from the given configuration into the
	 * configuration (this).
	 * 
	 * @param testProjectConfig
	 *            ProjectConfiguration
	 */
	public void setConfiguration(TestProjectConfig testProjectConfig) {
		this.setPathToTestFiles(testProjectConfig.getPathToTestFiles());
		this.setProjectLibraryConfig(testProjectConfig.getProjectLibraryConfig());
		this.setFixtureClass(testProjectConfig.getFixtureClass());
		this.setProjectPath(testProjectConfig.getProjectPath());
		this.setGlobalProjectVariables(testProjectConfig.getGlobalProjectVariables());
		this.setTeamShareConfig(testProjectConfig.getTeamShareConfig());
		this.setTestServerID(testProjectConfig.getTestServerID());
		this.setProjectConfigVersion(testProjectConfig.getProjectConfigVersion());
	}

	/**
	 * get the laodingStatus of the last loading.
	 * 
	 * @return the loading Status as {@link LibraryLoadingStatus}
	 */
	public LibraryLoadingStatus getLibraryLoadingStatus() {
		return projectLibraryConfig.getLibraryLoadingStatus();
	}

	/**
	 * sets the libraryloadingstatus.
	 * 
	 * @param libraryLoadingStatus
	 *            LibraryLoadingStatus
	 */
	public void setLibraryLoadingStatus(LibraryLoadingStatus libraryLoadingStatus) {
		projectLibraryConfig.setLibraryLoadingStatus(libraryLoadingStatus);
	}

	/**
	 * 
	 * @return the Configuration of the ProjectLibraryService
	 */
	public ProjectLibraryConfig getProjectLibraryConfig() {
		return projectLibraryConfig;
	}

	/**
	 * sets the Configuration of the ProjectActionGroupsService.
	 * 
	 * @param projectLibraryConfig
	 *            projectLibraryConfig
	 */
	public void setProjectLibraryConfig(ProjectLibraryConfig projectLibraryConfig) {
		this.projectLibraryConfig = projectLibraryConfig;
	}

	/**
	 * 
	 * @return the Configuration of the TeamShareConfig.
	 */
	public TeamShareConfig getTeamShareConfig() {
		return teamShareConfig;
	}

	/**
	 * 
	 * @return true if project is shared with a team, otherwise false
	 */
	public boolean isTeamSharedProject() {
		return getTeamShareConfig() != null;
	}

	/**
	 * sets the Configuration for Team Sharing.
	 * 
	 * @param aTeamShareConfig
	 *            teamShareConfig
	 */
	public void setTeamShareConfig(TeamShareConfig aTeamShareConfig) {
		teamShareConfig = aTeamShareConfig;
	}

	/**
	 * 
	 * @return the globalProjectvariables as a HashMap
	 */
	public HashMap<String, String> getGlobalProjectVariables() {
		return globalProjectVariables;
	}

	/**
	 * adds a globalProjectVariable into the HashMap. If the key exists, this
	 * method replaces the value.
	 * 
	 * @param key
	 *            the key of the variable
	 * @param value
	 *            the value of the variable
	 */
	public void putGlobalProjectVariable(String key, String value) {
		globalProjectVariables.put(key, value);
	}

	/**
	 * sets the memberVariable globalProjectVariables whit the content of the
	 * parameter globalProjectVariables.
	 * 
	 * @param globalProjectVariables
	 *            as a HashMap
	 */
	public void setGlobalProjectVariables(HashMap<String, String> globalProjectVariables) {
		this.globalProjectVariables = globalProjectVariables;
	}

	/**
	 * Sets the ID of the Test Server used in this TestProject Config.
	 * 
	 * @param testServerID
	 *            to identify the TestServerImplementation.
	 * 
	 */
	public void setTestServerID(String testServerID) {
		this.testServerID = testServerID;
	}

	/**
	 * 
	 * @return testServerID to identify the TestServerImplementation.
	 */
	public String getTestServerID() {
		return testServerID;
	}

	/**
	 * 
	 * @return the config version as a string.
	 */
	public String getProjectConfigVersion() {
		return projectConfigVersion;
	}

	/**
	 * sets the version of the config.
	 * 
	 * @param projectConfigVersion
	 *            String
	 */
	public void setProjectConfigVersion(String projectConfigVersion) {
		this.projectConfigVersion = projectConfigVersion;
	}
}
