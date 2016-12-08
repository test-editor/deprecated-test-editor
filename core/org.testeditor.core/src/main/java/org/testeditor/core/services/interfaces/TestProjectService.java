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
package org.testeditor.core.services.interfaces;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestProjectConfig;
import org.testeditor.core.model.teststructure.TestStructure;

/**
 * 
 * Service to manage the Projects in the workspace.
 * 
 */
public interface TestProjectService extends TestStructureTreeModel {

	String VERSION_TAG = "tpr-version";

	String VERSION1_1 = "1.1";
	String VERSION1_2 = "1.2";
	String VERSION = "1.3";

	String SERVER_ID = "testautomat.serverid";

	Set<String> SUPPORTED_VERSIONS = new HashSet<String>(Arrays.asList(VERSION, VERSION1_2));
	String UNSUPPORTED_CONFIG_VERSION = "unsupported";

	/**
	 * Scans the workspace and creates a TestProject for every directory.
	 * 
	 * @return a List of all Projects
	 */
	List<TestProject> getProjects();

	/**
	 * Stores the TestProjectConfig. This includes the Config.tpr,
	 * TeamShareConfig prefs. The Config.tpr will be created from the template.
	 * 
	 * @param testProject
	 *            the Location to store the configuration
	 * @param config
	 *            to store
	 * @throws SystemException
	 *             on Write Action
	 */
	void storeProjectConfig(TestProject testProject, TestProjectConfig config) throws SystemException;

	/**
	 * this method creates a new {@link TestProject}.
	 * 
	 * @param projectName
	 *            name of the project
	 * @return TestProject the new {@link TestProject}
	 * @throws IOException
	 *             on the file operation
	 * @throws SystemException
	 *             on reading the {@link TestProjectConfig}
	 * 
	 */
	TestProject createNewProject(String projectName) throws IOException, SystemException;

	/**
	 * Deletes the TestProject and stops the testserver.
	 * 
	 * @param testProject
	 *            {@link TestProject}
	 * @throws IOException
	 *             on error shuting down testserver or deleting project.
	 */
	void deleteProject(TestProject testProject) throws IOException;

	/**
	 * creates and configures the demoProjects.
	 * 
	 * @param list
	 *            of files
	 * 
	 * @throws SystemException
	 *             , if the creation fails
	 */
	void createAndConfigureDemoProjects(List<File> list) throws SystemException;

	/**
	 * @param testProject
	 *            {@link TestProject}
	 * @param newName
	 *            the new name if the project
	 * @return the renamed project
	 * @throws IOException
	 *             on the file operation
	 * @throws SystemException
	 *             on reading the {@link TestProjectConfig}
	 */
	TestProject renameTestproject(TestProject testProject, String newName) throws IOException, SystemException;

	/**
	 * Scans the test editor for DemoProjects and returns the matching
	 * directories.
	 * 
	 * @return an array of all DemoProject directories
	 * @throws IOException
	 *             on the scan for DemoProjects
	 */
	File[] getDemoProjects() throws IOException;

	/**
	 * Searches the Project List and returns the Project with the
	 * testProjectName.
	 * 
	 * @param testProjectName
	 *            of the Project searched for.
	 * 
	 * @return TestProject with the given name in the project list. If there is
	 *         no project with the name the value is null.
	 */
	TestProject getProjectWithName(String testProjectName);

	/**
	 * Searches for a TestStructure by the FullName.
	 * 
	 * @param testStructureFullName
	 *            Full Name of the TestStructure builded with:
	 *            ProjectName.TestSuiteName.TestStructureName
	 * @return TestFlow found by that name.
	 * @throws SystemException
	 *             on loading TestProjects from filesystem.
	 * 
	 */
	TestStructure findTestStructureByFullName(String testStructureFullName) throws SystemException;

	/**
	 * Reloads the Project List. Drops the Reference to the loaded Projects. It
	 * will force a reload of the ProjectList and throws an event for the ui.
	 * 
	 * @throws SystemException
	 *             on error reading project list.
	 */
	void reloadProjectList() throws SystemException;

	/**
	 * Reload a testProject in the List with the given name. If the project
	 * wasn't in the list it is added in other case it will be replaced. It
	 * throws an event to inform the UI about the need for refresh.
	 * 
	 * @param testProject
	 *            the project to be reloaded from filesystem, and added to the
	 *            project list.
	 * @throws SystemException
	 *             on io error loading project configuration.
	 */
	void reloadTestProjectFromFileSystem(TestProject testProject) throws SystemException;

	/**
	 * Checks if an project with the given name already exists.
	 * 
	 * @param projectName
	 *            name to to check.
	 * @return true if a project with that name already exists other wise false.
	 */
	boolean existsProjectWithName(String projectName);

}
