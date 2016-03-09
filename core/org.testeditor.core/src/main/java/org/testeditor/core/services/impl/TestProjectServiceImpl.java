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
package org.testeditor.core.services.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.contexts.IContextFunction;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.osgi.service.component.ComponentContext;
import org.testeditor.core.constants.TestEditorCoreConstants;
import org.testeditor.core.constants.TestEditorCoreEventConstants;
import org.testeditor.core.constants.TestEditorGlobalConstans;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.model.action.ProjectLibraryConfig;
import org.testeditor.core.model.team.TeamShareConfig;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestProjectConfig;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.services.interfaces.FileWatchService;
import org.testeditor.core.services.interfaces.TeamShareService;
import org.testeditor.core.services.interfaces.TestExecutionEnvironmentService;
import org.testeditor.core.services.interfaces.TestProjectService;
import org.testeditor.core.services.interfaces.TestServerService;
import org.testeditor.core.services.interfaces.TestStructureService;
import org.testeditor.core.services.plugins.LibraryConfigurationServicePlugIn;
import org.testeditor.core.services.plugins.TeamShareConfigurationServicePlugIn;
import org.testeditor.core.services.plugins.TestEditorPlugInService;
import org.testeditor.core.util.ConfigurationTemplateWriter;
import org.testeditor.core.util.FileLocatorService;
import org.testeditor.core.util.FileUtils;

/**
 * 
 * Default TestProjectService Implementation. It uses the Workspace location to
 * find the Project Configurations.
 * 
 */
public class TestProjectServiceImpl implements TestProjectService, IContextFunction {

	private static final Logger logger = Logger.getLogger(TestProjectServiceImpl.class);
	private TestEditorPlugInService plugInservice;
	private FileLocatorService fileLocatorService;
	private String preferncesFileName;
	private List<TestProject> testProjects;
	private IEventBroker eventBroker;
	private Map<String, String> renamedProjects = new HashMap<String, String>();

	private List<TestProject> oldTestProjects = new ArrayList<TestProject>();
	private TestServerService testServerService;
	private TestStructureService testStructureService;
	private TeamShareService teamShareService;

	private FileWatchService fileWatchService;

	/**
	 * 
	 * @param testStructureService
	 *            used in this service
	 * 
	 */
	public void bind(TestStructureService testStructureService) {
		this.testStructureService = testStructureService;
		logger.info("Bind testStructureService");
	}

	/**
	 * 
	 * @param testStructureService
	 *            removed from system
	 */
	public void unBind(TestStructureService testStructureService) {
		this.testStructureService = null;
		logger.info("Unbind testStructureService");
	}

	/**
	 * 
	 * @param teamShareService
	 *            used in this service
	 * 
	 */
	public void bind(TeamShareService teamShareService) {
		this.teamShareService = teamShareService;
		logger.info("Bind teamShareService");
	}

	/**
	 * 
	 * @param teamShareService
	 *            removed from system
	 */
	public void unBind(TeamShareService teamShareService) {
		this.teamShareService = null;
		logger.info("Unbind teamShareService");
	}

	/**
	 * 
	 * @param plugInservice
	 *            used in this service
	 * 
	 */
	public void bind(TestEditorPlugInService plugInservice) {
		this.plugInservice = plugInservice;
		logger.info("Bind TestEditorPlugInService");
	}

	/**
	 * 
	 * @param plugInservice
	 *            removed from system
	 */
	public void unBind(TestEditorPlugInService plugInservice) {
		this.plugInservice = null;
		logger.info("Unbind TestEditorPlugInService");
	}

	/**
	 * 
	 * @param fileLocatorService
	 *            used in this service
	 * 
	 */
	public void bind(FileLocatorService fileLocatorService) {
		this.fileLocatorService = fileLocatorService;
		logger.info("Bind FileLocatorService");
	}

	/**
	 * 
	 * @param fileWatchService
	 *            used in this service
	 * 
	 */
	public void bind(FileWatchService fileWatchService) {
		this.fileWatchService = fileWatchService;
		logger.info("Bind FileWatchService");
	}

	/**
	 * 
	 * @param fileWatchService
	 *            used in this service
	 * 
	 */
	public void unBind(FileWatchService fileWatchService) {
		this.fileWatchService = null;
		logger.info("Unbind FileWatchService");
	}

	/**
	 * 
	 * @param fileLocatorService
	 *            removed from system
	 */
	public void unBind(FileLocatorService fileLocatorService) {
		this.fileLocatorService = null;
		logger.info("Unbind FileLocatorService");
	}

	/**
	 * 
	 * @param testServerService
	 *            used in this service
	 * 
	 */
	public void bind(TestServerService testServerService) {
		this.testServerService = testServerService;
		logger.info("Bind TestServerService");
	}

	/**
	 * 
	 * @param testServerService
	 *            removed from system
	 */
	public void unBind(TestServerService testServerService) {
		this.testServerService = null;
		logger.info("Unbind TestServerService");
	}

	@Override
	public List<TestProject> getProjects() {
		return testProjects;
	}

	/**
	 * Scans the workspace and creates a TestProject for every directory. These
	 * are stored in the local TestProject List.
	 * 
	 * @throws SystemException
	 *             on io error while creating project
	 * 
	 */
	private void loadProjectListFromFileSystem() throws SystemException {
		testProjects = new ArrayList<TestProject>();
		File[] files = getWorkspaceDirectories();
		logger.info("Scanning for Projects in: " + Platform.getLocation().toOSString());
		for (File file : files) {
			if (isTestProjectDirectory(file)) {
				TestProject testProject = createProjectFrom(file);
				try {
					if (teamShareService != null && teamShareService.isCleanupNeeded(testProject)) {
						teamShareService.cleanup(testProject);
					}
				} catch (SystemException e) {
					logger.error("Error during lockcheck in the local subversion database. Message " + e.getMessage(),
							e);
				}

				testProjects.add(testProject);

				if (eventBroker != null) {
					eventBroker.post(TestEditorCoreEventConstants.TESTSTRUCTURE_STATE_UPDATED, testProject);
				}

			}
		}

		if (eventBroker != null) {
			eventBroker.send(TestEditorCoreEventConstants.TESTSTRUCTURE_MODEL_CHANGED_RELOADED, "");
		}
	}

	/**
	 * Checks if a directory is the root of a TestProject.
	 * 
	 * @param file
	 *            to be checked.
	 * @return true if the directory is an testproject otherwise false.
	 */
	private boolean isTestProjectDirectory(File file) {
		if (file.isDirectory() && !file.getName().startsWith(".") && file.list() != null) {
			return Arrays.asList(file.list()).contains("config.tpr");
		}
		return false;
	}

	/**
	 * Updates a TestProject from a ProjectDirectory.
	 * 
	 * @param testProject
	 *            to be filled with the configuration informations in the
	 *            filesystem.
	 * @param projectDirectory
	 *            which contains the project
	 * @throws SystemException
	 *             exception on loading project.
	 */
	private void loadProjectConfigFromFileSystem(TestProject testProject, File projectDirectory)
			throws SystemException {
		testProject.setName(projectDirectory.getName());
		testProject.setTestProjectConfig(getProjectConfigFor(testProject));
		testProject.setUrl(projectDirectory);

		setPortFromOldProjectObjectTo(testProject);
		if (plugInservice != null) {
			testProject.setChildCountInBackend(-1);
			testProject.setLazyLoader(testStructureService.getTestProjectLazyLoader(testProject));
		}
		logger.trace("Building Project " + testProject.getName());
	}

	/**
	 * Creates a TestProject from a ProjectDirectory.
	 * 
	 * @param projectDirectory
	 *            which contains the project
	 * @return TestProject based on the configuration in the projectDirectory.
	 * @throws SystemException
	 *             exception on loading project.
	 */
	private TestProject createProjectFrom(File projectDirectory) throws SystemException {
		TestProject testProject = new TestProject();
		loadProjectConfigFromFileSystem(testProject, projectDirectory);
		try {
			testProject.setUrl(projectDirectory.toURI().toURL());
		} catch (MalformedURLException e) {
			throw new SystemException("Unable to convert project file to url.", e);
		}
		fileWatchService.watch(testProject);
		return testProject;
	}

	/**
	 * Set the port from archived {@link TestProject} Object to new generated
	 * {@link TestProject} object.
	 * 
	 * @param testProject
	 *            new created {@link TestProject}
	 */
	private void setPortFromOldProjectObjectTo(TestProject testProject) {
		for (TestProject oldTestProject : oldTestProjects) {
			if (oldTestProject.getName().equals(testProject.getName())) {
				testProject.getTestProjectConfig().setPort(oldTestProject.getTestProjectConfig().getPort());
			}
		}
	}

	@Override
	public void createAndConfigureDemoProjects(List<File> demoProjectsDirs) throws SystemException {
		try {
			List<TestProject> projects = createDemoProject(demoProjectsDirs);
			testProjects.addAll(projects);
			if (eventBroker != null) {
				eventBroker.send(TestEditorCoreEventConstants.TESTSTRUCTURE_MODEL_CHANGED_UPDATE_BY_MODIFY, "");
			}
		} catch (IOException e) {
			throw new SystemException("Error creating Demo Projects", e);
		}
	}

	/**
	 * 
	 * @return Array with Files of the Workspace.
	 */
	protected File[] getWorkspaceDirectories() {
		return fileLocatorService.getWorkspace().listFiles();
	}

	/**
	 * Returns the demo projects directory. Name of Directory of release version
	 * has a timestamp at the end of name.
	 * 
	 * e.g.: org.testeditor.demo_1.0.0.20130529-1142
	 * 
	 * @param dir
	 *            the filename
	 * @return directory.
	 * @throws IOException
	 *             on file-operations
	 */
	private File getDirectoryOfDemoProjects(String dir) throws IOException {
		logger.info("Searching for " + dir);
		logger.debug("Find Bundle File: " + findBundleFile("org.testeditor.demo"));
		String pathToDemo = findBundleFile("org.testeditor.demo");
		if (pathToDemo == null) {
			logger.error("The path for the demoprojects does not exist");
			return null;
		}
		return new File(pathToDemo + File.separator + dir);
	}

	/**
	 * Creates the demo Project in an empty Workspace.
	 * 
	 * @param demoProjectsToBeBuildDirs
	 *            List of demo projects user wants to be build
	 * 
	 * @throws IOException
	 *             on file-operations
	 * @return list of created demo projects.
	 * @throws SystemException
	 *             on io error while file access.
	 */
	private List<TestProject> createDemoProject(List<File> demoProjectsToBeBuildDirs)
			throws IOException, SystemException {
		List<TestProject> result = new ArrayList<TestProject>();
		if (demoProjectsToBeBuildDirs != null) {
			File wsDir = Platform.getLocation().toFile();

			Set<String> wsDirectoryNames = new HashSet<String>(Arrays.asList(wsDir.list()));

			for (File demoProjectDir : demoProjectsToBeBuildDirs) {

				if (!wsDirectoryNames.contains(demoProjectDir.getName())) {
					// copy of the demo project files
					copyDemoProject(demoProjectDir, wsDir);
					if (demoProjectDir.isDirectory()) {
						// copy of the project-configuration
						String demoProjectName = demoProjectDir.getName();
						TestProjectConfig config = getProjectConfigFor(demoProjectName);
						internalStoreProjectConfig(demoProjectName, config, true);
					}
					TestProject project = createProjectFrom(
							new File(wsDir.getAbsolutePath() + File.separator + demoProjectDir.getName()));
					if (project != null) {
						result.add(project);
					}
				}
			}
		}
		return result;
	}

	@Override
	public File[] getDemoProjects() throws IOException {
		File demoDir = getDirectoryOfDemoProjects("demoProjects");

		File[] demoProjectsDirs = demoDir.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return !name.startsWith(".") && !name.equalsIgnoreCase("DemoEmpty");
			}
		});

		if (demoProjectsDirs == null) {
			logger.error("No demoprojects found in directory " + demoDir.getAbsolutePath());
		}
		return demoProjectsDirs;
	}

	/**
	 * this method search for a file with a filename beginning with the
	 * parameter.
	 * 
	 * @param fileName
	 *            first part of the name of the file
	 * @return the absolutpath
	 * @throws IOException
	 *             on file-operations
	 */
	protected String findBundleFile(String fileName) throws IOException {
		return fileLocatorService.findBundleFileLocationAsString(fileName);
	}

	/**
	 * copies the demo projects. and root files parallel to the project
	 * directories.
	 * 
	 * @param demoProjectDir
	 *            the source-projectDir of the demoProject
	 * @param wsDir
	 *            the workingDirectory
	 */
	private void copyDemoProject(File demoProjectDir, File wsDir) {

		try {
			File destFile = new File(wsDir.getAbsolutePath() + File.separator + demoProjectDir.getName());
			FileUtils.copyFolder(demoProjectDir, destFile);
			copyWebDemoAUT(demoProjectDir, wsDir, destFile);
		} catch (IOException e) {
			logger.trace("Error while copy the Project " + demoProjectDir.getName(), e);
		}
	}

	/**
	 * 
	 * Copy the Demo Web Application in to the workscpace of the user. This Demo
	 * Application is used as an AUT in the WebDemo Testproject.
	 * 
	 * @param demoProjectDir
	 *            demo Project
	 * @param wsDir
	 *            path to the local workspace
	 * @param destFile
	 *            in the user workspace
	 * @throws IOException
	 *             on io error
	 */
	protected void copyWebDemoAUT(File demoProjectDir, File wsDir, File destFile) throws IOException {
		File fitNesseFilesDir = getDirectoryOfDemoProjects("fitNesseFiles" + File.separator + "files");

		if (destFile.isDirectory()) {
			String projectPath = wsDir.getAbsolutePath() + File.separator + demoProjectDir.getName() + File.separator
					+ "FitNesseRoot" + File.separator + "files";
			logger.info("Creating Projectfiles in " + projectPath);

			FileUtils.copyFolder(fitNesseFilesDir, new File(projectPath));
		}
	}

	/**
	 * 
	 * @param testProject
	 *            to get the config for.
	 * @return Config for the given TestProject
	 * @throws SystemException
	 *             on Read Action
	 */
	public TestProjectConfig getProjectConfigFor(TestProject testProject) throws SystemException {
		String projectName = testProject.getName();
		try {
			return getProjectConfigFor(projectName);
		} catch (IOException e) {
			logger.trace("Error reading config", e);
			throw new SystemException("Error Reading config", e);
		}
	}

	/**
	 * Get the projectconfiguration for a project by the name.
	 * 
	 * @param projectName
	 *            name of the project
	 * @return the {@link TestProjectConfig}
	 * @throws IOException
	 *             on reading config of a project.
	 */
	private TestProjectConfig getProjectConfigFor(String projectName) throws IOException {
		File[] directories = getWorkspaceDirectories(projectName);
		TestProjectConfig result = null;
		if (directories != null) {
			for (File file : directories) {
				Properties properties = new Properties();
				File configFile = new File(file.getAbsolutePath() + File.separator + "config.tpr");
				InputStreamReader inputStream = new InputStreamReader(new FileInputStream(configFile), "UTF-8");
				properties.load(inputStream);
				inputStream.close();
				TestProjectConfig projectConfig = getTestProjectConfigFrom(properties, projectName);
				projectConfig.setProjectPath(file.getAbsolutePath());
				result = projectConfig;
			}
		}
		return result;
	}

	/**
	 * 
	 * @param properties
	 *            with BeanInformations for the Config.
	 * @param projectName
	 *            the name of the project
	 * @return TestProjectConfig
	 * @throws IOException
	 *             on storing config.
	 */
	protected TestProjectConfig getTestProjectConfigFrom(Properties properties, String projectName) throws IOException {
		boolean configMigrated = false;
		TestProjectConfig testProjectConfig = new TestProjectConfig();
		addGlobalVariablesToProjectConfig(testProjectConfig, properties);
		setConfigValues(testProjectConfig, properties);
		testProjectConfig.setPathToTestFiles(properties.getProperty("pathToTestFiles"));
		if (properties.containsKey(TestExecutionEnvironmentService.CONFIG)) {
			testProjectConfig
					.setTestEnvironmentConfiguration(properties.getProperty(TestExecutionEnvironmentService.CONFIG));
		}
		if (!properties.containsKey(TestProjectService.VERSION_TAG)) {
			fixNonVersionProperties(properties);
		} else {
			String cfgVersion = properties.getProperty(TestProjectService.VERSION_TAG);
			testProjectConfig.setProjectConfigVersion(cfgVersion);
			if (!cfgVersion.equals(TestProjectService.VERSION)) {
				if (isConfigVersionSupported(cfgVersion)) {
					testProjectConfig = getFixedTestProjectConfigVersion(testProjectConfig, properties);
					configMigrated = true;
				} else {
					testProjectConfig.setProjectConfigVersion(TestProjectService.UNSUPPORTED_CONFIG_VERSION);
				}
			}

		}
		setupPluginConfig(testProjectConfig, properties);
		if (configMigrated) {
			internalStoreProjectConfig(projectName, testProjectConfig, true);
		}

		return testProjectConfig;
	}

	/**
	 * Init values from properties or set defaults.
	 * 
	 * @param testProjectConfig
	 *            to be configured.
	 * @param properties
	 *            used to get the values.
	 */
	protected void setConfigValues(TestProjectConfig testProjectConfig, Properties properties) {
		String serverId = properties.getProperty("testautomat.serverid");
		if (serverId != null) {
			testProjectConfig.setTestServerID(serverId);
		}
		String testEnv = properties.getProperty("test.execution.environment.config");
		if (testEnv == null) {
			testProjectConfig.setTestEnvironmentConfiguration(TestEditorCoreConstants.NONE_TEST_AGENT);
		} else {
			testProjectConfig.setTestEnvironmentConfiguration(testEnv);
		}
	}

	/**
	 * Setup and if necessary migrate configuration of the plug-ins.
	 * 
	 * @param testProjectConfig
	 *            to be builded.
	 * @param properties
	 *            wit base information to build it.
	 */
	private void setupPluginConfig(TestProjectConfig testProjectConfig, Properties properties) {
		if (plugInservice != null) {
			testProjectConfig.setProjectLibraryConfig(createProjectLibraryConfigFrom(properties));
			if (properties.containsKey(TestEditorPlugInService.TEAMSHARE_ID)
					&& !properties.getProperty(TestEditorPlugInService.TEAMSHARE_ID).isEmpty()) {
				TeamShareConfigurationServicePlugIn teamCfgService = plugInservice.getTeamShareConfigurationServiceFor(
						properties.getProperty(TestEditorPlugInService.TEAMSHARE_ID));
				if (teamCfgService != null) {
					testProjectConfig.setTeamShareConfig(teamCfgService.createTeamShareConfigFrom(properties));
				}
			}
		}

	}

	/**
	 * Creates a <code>ProjectLibraryConfig</code> with the values of the
	 * properties.
	 * 
	 * @param properties
	 *            to be passed to the ProjectLibraryConfig
	 * @return ProjectLibraryConfig
	 */
	public ProjectLibraryConfig createProjectLibraryConfigFrom(Properties properties) {
		String plugInID = properties.getProperty(TestEditorPlugInService.LIBRARY_ID);
		LibraryConfigurationServicePlugIn libraryConfigurationService = plugInservice
				.getLibraryConfigurationServiceFor(plugInID);
		if (libraryConfigurationService != null) {
			return libraryConfigurationService.createProjectLibraryConfigFrom(properties);
		} else {
			return null;
		}
	}

	/**
	 * adds globalVaraibales into the projectConfiguration.
	 * 
	 * @param testProjectConfig
	 *            the TestProjectConfig
	 * @param properties
	 *            properties out of the configuration-file.
	 */
	private void addGlobalVariablesToProjectConfig(TestProjectConfig testProjectConfig, Properties properties) {
		for (Object key : properties.keySet()) {
			if (key instanceof String && ((String) key).startsWith(TestEditorGlobalConstans.VARIABLE_PRAEFIX)) {
				testProjectConfig.putGlobalProjectVariable(
						((String) key).substring(TestEditorGlobalConstans.VARIABLE_PRAEFIX.length() + 1),
						properties.getProperty((String) key));
			}
		}

	}

	/**
	 * Fixing the ProjectConfig for the current TestEditor instance.
	 * 
	 * @param testProjectConfig
	 *            to be fixed
	 * @param properties
	 *            base for the config.
	 * @return the fixes Project config.
	 */
	protected TestProjectConfig getFixedTestProjectConfigVersion(TestProjectConfig testProjectConfig,
			Properties properties) {
		return migrateProjectConfigVersion(testProjectConfig, properties);
	}

	/**
	 * gets the config-properties from older versions.
	 * 
	 * @param testProjectConfig
	 *            actual created Config.
	 * 
	 * @param properties
	 *            base for the config.
	 * 
	 * @return the fixes Project config
	 */
	private TestProjectConfig migrateProjectConfigVersion(TestProjectConfig testProjectConfig, Properties properties) {
		if (properties.getProperty(TestProjectService.VERSION_TAG).equals(TestProjectService.VERSION1_1)) {
			return getTestProjectConfigFromVersion1dot1(properties);
		}
		if (properties.getProperty(TestProjectService.VERSION_TAG).equals(TestProjectService.VERSION1_2)) {
			return getTestProjectConfigFromVersion1dot2(testProjectConfig, properties);
		}
		if (properties.getProperty(TestProjectService.VERSION_TAG).equals(TestProjectService.VERSION1_3)) {
			return getTestProjectConfigFromVersion1dot3(testProjectConfig, properties);
		}
		return new TestProjectConfig();
	}

	/**
	 * gets the config-properties from the version 1.3 and initializes with new
	 * .
	 * 
	 * @param testProjectConfig
	 *            actual created configuration.
	 * 
	 * @param properties
	 *            with BeanInformations for the configuration.
	 * @return TestProjectConfig
	 */
	protected TestProjectConfig getTestProjectConfigFromVersion1dot3(TestProjectConfig testProjectConfig,
			Properties properties) {
		testProjectConfig.setTestEnvironmentConfiguration(TestEditorCoreConstants.NONE_TEST_AGENT);
		testProjectConfig.setProjectConfigVersion(VERSION);
		return testProjectConfig;
	}

	/**
	 * gets the config-properties from the version 1.2 and initializes with new
	 * .
	 * 
	 * @param testProjectConfig
	 *            actual created Config.
	 * 
	 * @param properties
	 *            with BeanInformations for the Config.
	 * @return TestProjectConfig
	 */
	protected TestProjectConfig getTestProjectConfigFromVersion1dot2(TestProjectConfig testProjectConfig,
			Properties properties) {
		testProjectConfig.setTestServerID("fitnesse_based_1.2");
		testProjectConfig.setProjectConfigVersion(VERSION1_2);
		return testProjectConfig;
	}

	/**
	 * gets the config-properties from the version 1.1.
	 * 
	 * @param properties
	 *            with BeanInformations for the Config.
	 * @return TestProjectConfig
	 */
	private TestProjectConfig getTestProjectConfigFromVersion1dot1(Properties properties) {
		TestProjectConfig testProjectConfig = new TestProjectConfig();
		testProjectConfig.setPathToTestFiles(properties.getProperty("pathToTestFiles"));
		testProjectConfig.setFixtureClass(properties.getProperty("classFixtures"));
		testProjectConfig.setProjectConfigVersion(VERSION1_1);
		return testProjectConfig;
	}

	/**
	 * Updates Configurations from the 1.0 release.
	 * 
	 * @param properties
	 *            to be upgraded.
	 */
	private void fixNonVersionProperties(Properties properties) {
		properties.put(TestEditorPlugInService.LIBRARY_ID, "org.testeditor.xmllibrary");
	}

	/**
	 * 
	 * @param config
	 *            TestProjectConfig
	 * @return properties based on the config
	 */
	protected Properties getPropertiesFrom(TestProjectConfig config) {
		Properties properties = new Properties();
		properties.put("testautomat.serverid", config.getTestServerID());
		if (config.getPathToTestFiles() != null) {
			properties.put("pathToTestFiles", config.getPathToTestFiles());
		}
		properties.put(TestProjectService.VERSION_TAG, TestProjectService.VERSION);
		properties.put(TestExecutionEnvironmentService.CONFIG, config.getTestEnvironmentConfiguration());
		if (plugInservice != null) {
			if (config.getProjectLibraryConfig() != null) {
				LibraryConfigurationServicePlugIn configurationService = plugInservice
						.getLibraryConfigurationServiceFor(config.getProjectLibraryConfig().getId());
				properties.putAll(configurationService.getAsProperties(config.getProjectLibraryConfig()));
				properties.put(TestEditorPlugInService.LIBRARY_ID, config.getProjectLibraryConfig().getId());
			}
			if (config.isTeamSharedProject()) {
				TeamShareConfigurationServicePlugIn teamCfgService = plugInservice
						.getTeamShareConfigurationServiceFor(config.getTeamShareConfig().getId());
				properties.putAll(teamCfgService.getAsProperties(config.getTeamShareConfig()));
				properties.put(TestEditorPlugInService.TEAMSHARE_ID, config.getTeamShareConfig().getId());
			}
		}
		addPopertiesForGlobalVariables(config, properties);
		return properties;
	}

	/**
	 * adds the globalVariables into the properties.
	 * 
	 * @param config
	 *            the TestProjectConfig
	 * @param properties
	 *            the Properties
	 */
	private void addPopertiesForGlobalVariables(TestProjectConfig config, Properties properties) {

		for (String key : config.getGlobalProjectVariables().keySet()) {
			properties.put(TestEditorGlobalConstans.VARIABLE_PRAEFIX + "." + key,
					config.getGlobalProjectVariables().get(key));
		}

	}

	@Override
	public void storeProjectConfig(TestProject testProject, TestProjectConfig config) throws SystemException {
		String testProjectName = testProject.getName();
		try {
			internalStoreProjectConfig(testProjectName, config, true);
		} catch (IOException e) {
			logger.trace("Error writing config", e);
			throw new SystemException("Error writing config", e);
		}
	}

	/**
	 * this method stores the configuration of a project.
	 * 
	 * @param testProjectName
	 *            name of the project
	 * @param config
	 *            the configuration of the project
	 * @param storeConfigTpr
	 *            boolean true: save the config.tpr
	 * 
	 * 
	 * @throws IOException
	 *             on file access.
	 */
	private void internalStoreProjectConfig(String testProjectName, TestProjectConfig config, boolean storeConfigTpr)
			throws IOException {
		File[] directories = getWorkspaceDirectories(testProjectName);
		for (File file : directories) {
			Properties props = getPropertiesFrom(config);
			File configFile = new File(file.getAbsolutePath() + File.separator + "config.tpr");
			if (logger.isInfoEnabled()) {
				logger.info("Storing Configuration for " + testProjectName + " in " + configFile.getAbsolutePath());
			}
			TeamShareConfig teamShareConfig = config.getTeamShareConfig();
			String templateForTeamshareConfiguration = "";
			if (teamShareConfig != null) {
				TeamShareConfigurationServicePlugIn teamShareConfigurationServiceFor = plugInservice
						.getTeamShareConfigurationServiceFor(config.getTeamShareConfig().getId());
				if (teamShareConfigurationServiceFor != null) {
					templateForTeamshareConfiguration = teamShareConfigurationServiceFor.getTemplateForConfiguration();
				}
			}

			if (storeConfigTpr) {
				ConfigurationTemplateWriter configurationTemplateWriter = new ConfigurationTemplateWriter();
				if (config.getProjectLibraryConfig() != null) {
					configurationTemplateWriter.writeConfiguration(fileLocatorService, configFile, props,
							plugInservice.getLibraryConfigurationServiceFor(config.getProjectLibraryConfig().getId())
									.getTemplateForConfiguration(),
							templateForTeamshareConfiguration);
				}
			}
		}
	}

	/**
	 * get only the directories with the name equal to the testProjectName.
	 * 
	 * @param testProjectName
	 *            name of the TestProject
	 * @return File[]
	 */
	private File[] getWorkspaceDirectories(String testProjectName) {
		final String fileDemoName = testProjectName;
		FilenameFilter filter = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String fileName) {
				return fileName.equalsIgnoreCase(fileDemoName);
			}
		};

		return Platform.getLocation().toFile().listFiles(filter);
	}

	@Override
	public TestProject createNewProject(String nameNewProject) throws IOException, SystemException {

		final String nameDemoProject = "DemoEmpty";
		File directoryOfDemoProjects = getDirectoryOfDemoProjects("demoProjects");

		if (directoryOfDemoProjects != null) {
			File sourceFile = new File(directoryOfDemoProjects + File.separator + nameDemoProject);

			if (!sourceFile.exists()) {
				logger.error("sourceFiles in " + directoryOfDemoProjects.getAbsolutePath() + " doesn't exist.");
				return null;
			}
			File wsDir = Platform.getLocation().toFile();
			File destDir = new File(wsDir.getAbsoluteFile() + File.separator + nameNewProject);
			// copy of the DemoEmpty-project to a new project
			FileUtils.copyFolder(sourceFile, destDir);

			// rename the directory
			// */<projectName>/FitNesseRoot/DemoWebTests to
			// */<projectName>/FitNesseRoot/<projectName>
			String rootPartRenameDir = destDir.getAbsolutePath() + File.separator + "FitNesseRoot" + File.separator;
			File renameDir = new File(rootPartRenameDir + nameDemoProject);
			if (!renameDir.renameTo(new File(rootPartRenameDir + nameNewProject))) {
				String message = "Rename failed from " + renameDir.getName() + " to " + rootPartRenameDir
						+ nameNewProject;
				logger.error(message);
				throw new SystemException(message);
			}

			// change the configuration of the new project

			TestProjectConfig config = getProjectConfigFor(nameNewProject);
			config.setProjectLibraryConfig(
					replacePathInXMLProperties(config.getProjectLibraryConfig(), nameDemoProject, nameNewProject));
			internalStoreProjectConfig(nameNewProject, config, true);
			File configFile = new File(rootPartRenameDir + nameNewProject + File.separator + "content.txt");
			changePathOfTheElementListe(nameDemoProject, nameNewProject, configFile);
			replacePathStringsInContentFiles(new File(rootPartRenameDir), nameDemoProject, nameNewProject);
			TestProject testProject = createProjectFrom(destDir);
			getProjects().add(testProject);
			if (eventBroker != null) {
				eventBroker.send(TestEditorCoreEventConstants.TESTSTRUCTURE_MODEL_CHANGED_UPDATE_BY_MODIFY, "");
			}
			return getProject(nameNewProject);
		}
		return null;
	}

	/**
	 * replaces the paths in the XML-properties.
	 * 
	 * @param projectLibraryConfig
	 *            the libraryConfig
	 * @param nameDemoProject
	 *            the name of the demo-project
	 * @param nameNewProject
	 *            name of the new project
	 * @return the modified ProjectLibraryConfig
	 */
	private ProjectLibraryConfig replacePathInXMLProperties(ProjectLibraryConfig projectLibraryConfig,
			String nameDemoProject, String nameNewProject) {
		if (projectLibraryConfig != null) {
			return projectLibraryConfig.copyConfigurationToDestination(nameDemoProject, nameNewProject);
		}
		return null;
	}

	/**
	 * gets the {@link TestProject} by the given name.
	 * 
	 * @param projectName
	 *            name of the project
	 * @return {@link TestProject} or null
	 */
	protected TestProject getProject(String projectName) {
		List<TestProject> projects = getProjects();
		for (TestProject project : projects) {
			if (project.getName().equals(projectName)) {
				return project;
			}
		}

		return null;
	}

	/**
	 * this method sets the path of the elementList to a default-path.
	 * 
	 * @param nameDemoProject
	 *            name of the demo-project
	 * @param projectName
	 *            name of the new project
	 * @param configFile
	 *            the configuration-file
	 * @throws IOException
	 *             on file-operations
	 */
	private void changePathOfTheElementListe(String nameDemoProject, String projectName, File configFile)
			throws IOException {

		File outFile = new File(configFile + ".tmp");
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(configFile), "UTF-8"));
		PrintWriter writer = new PrintWriter(outFile, "UTF-8");
		String line = null;

		while ((line = reader.readLine()) != null) {
			if (line.startsWith("!define ELEMENT_LISTE")) {
				line = line.replace(nameDemoProject, projectName);
			}
			writer.println(line);
		}
		reader.close();
		writer.close();
		if (configFile.exists() && !configFile.delete()) {
			logger.error("Delete failed for " + configFile);
		}
		if (!outFile.renameTo(configFile)) {
			logger.error("Rename failed from " + outFile.getName() + " to " + configFile.getName());
		}

	}

	@Override
	public TestProject renameTestproject(TestProject testProject, String newName) throws SystemException, IOException {
		String oldName = testProject.getName();
		renamedProjects.put(oldName, newName);
		renameProjectInFileSystem(testProject, newName);
		testProject.setName(newName);
		testProject.setTestProjectConfig(getProjectConfigFor(newName));
		if (eventBroker != null) {
			eventBroker.send(TestEditorCoreEventConstants.TESTSTRUCTURE_MODEL_CHANGED_UPDATE_BY_MODIFY, "");
		}
		return getProject(newName);

	}

	/**
	 * Renames the TestProject in the file system.
	 * 
	 * @param testProject
	 *            the project
	 * @param newName
	 *            of the project
	 * 
	 * @throws IOException
	 *             , while file handling
	 * @throws SystemException
	 *             if replacing of patterns failed
	 * 
	 */
	protected void renameProjectInFileSystem(TestProject testProject, String newName)
			throws SystemException, IOException {
		File wsDir = Platform.getLocation().toFile();
		File oldRootDir = new File(wsDir.getAbsoluteFile() + File.separator + testProject.getName());

		File destRootDir = new File(wsDir.getAbsoluteFile() + File.separator + newName);

		if (!oldRootDir.renameTo(destRootDir)) {
			String message = "Rename from " + oldRootDir.getName() + " to " + destRootDir + " failed.";
			logger.error(message);
			throw new SystemException(message);
		}

		// rename the directory */<projectName>/FitNesseRoot/DemoWebTests to
		// */<projectName>/FitNesseRoot/<projectName>
		String fitnesseRootPartRenameDir = destRootDir.getAbsolutePath() + File.separator + "FitNesseRoot"
				+ File.separator;
		File renameDir = new File(fitnesseRootPartRenameDir + testProject.getName());
		if (!renameDir.renameTo(new File(fitnesseRootPartRenameDir + newName))) {
			String message = "Rename from " + renameDir.getName() + " to " + fitnesseRootPartRenameDir + newName
					+ " failed.";
			logger.error(message);
			throw new SystemException(message);
		}

		// change the configuration of the new project

		replacePathStringsInContentFiles(new File(fitnesseRootPartRenameDir), testProject.getName(), newName);

		TestProjectConfig config = getProjectConfigFor(newName);
		config.setProjectLibraryConfig(
				replacePathInXMLProperties(config.getProjectLibraryConfig(), testProject.getName(), newName));
		internalStoreProjectConfig(newName, config, true);
		testProject.setUrl(destRootDir.toURI().toURL());
	}

	/**
	 * this method replaces all strings equals to the oldName with the newName
	 * in the content.txt-files.
	 * 
	 * @param rootDirOfNewProject
	 *            root directory of the new project.
	 * @param oldName
	 *            old name
	 * @param newName
	 *            new name
	 * @return true, if every oldName in the content.txt-files is replaced by
	 *         the newName , else false.
	 * @throws IOException
	 *             , while file handling
	 * @throws SystemException
	 *             if replacing of patterns failed
	 */
	private boolean replacePathStringsInContentFiles(File rootDirOfNewProject, String oldName, String newName)
			throws IOException, SystemException {
		File[] listFiles = rootDirOfNewProject.listFiles();
		for (File file : listFiles) {
			if (file.isDirectory()) {
				boolean replaced = replacePathStringsInContentFiles(file, oldName, newName);
				if (!replaced) {
					String message = "Replace old pattern " + oldName + " with new pattern " + newName + " failed.";
					logger.error(message);
					throw new SystemException(message);
				}
			} else {
				File outFile = new File(file.getAbsolutePath() + ".tmp");
				if (!file.getAbsolutePath().equalsIgnoreCase(preferncesFileName)) {
					replace(oldName, newName, file, outFile);
					if (file.exists() && !file.delete()) {
						logger.error("Delete failed form " + file);
					}
					if (!outFile.renameTo(file)) {
						String message = "Rename from " + outFile.getName() + " to " + file.getAbsoluteFile()
								+ " failed.";
						logger.error(message);
						throw new SystemException(message);
					}
				}
			}
		}
		return true;
	}

	/**
	 * replaces the old string whit the new string in the in-file and returns
	 * the out-file.
	 * 
	 * @param oldstring
	 *            old string should be replaced
	 * @param newstring
	 *            new string for the replace
	 * @param in
	 *            file
	 * @param out
	 *            file
	 * @throws IOException
	 *             while file-handling operations
	 */
	private void replace(String oldstring, String newstring, File in, File out) throws IOException {

		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(in), "UTF-8"));
		PrintWriter writer = new PrintWriter(out, "UTF-8");
		String line = null;
		while ((line = reader.readLine()) != null) {
			writer.println(line.replace(oldstring, newstring));
		}

		reader.close();
		writer.close();
	}

	@Override
	public void deleteProject(TestProject testProject) throws IOException {
		// remove the directory from filesystem
		if (testServerService != null) {
			testServerService.stopTestServer(testProject);
		}
		File ws = Platform.getLocation().toFile();
		String projectPath = ws.getAbsolutePath() + File.separator + testProject.getName();
		deleteDirectory(new File(projectPath));
		fileWatchService.remove(testProject);
		testProjects.remove(testProject);
		if (eventBroker != null) {
			eventBroker.post(TestEditorCoreEventConstants.TESTSTRUCTURE_MODEL_CHANGED_DELETED,
					testProject.getFullName());
		}
	}

	/**
	 * Deletes directory and its content.
	 * 
	 * @param file
	 *            directory or file to be deleted
	 * @return true if delete was successful else false
	 */
	private boolean deleteDirectory(File file) {
		if (file.isDirectory()) {
			String[] children = file.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDirectory(new File(file, children[i]));
				if (!success) {
					return false;
				}
			}
		}
		return file.delete();
	}

	@Override
	public List<? extends TestStructure> getElements() {
		return getProjects();
	}

	@Override
	public TestProject getProjectWithName(String testProjectName) {
		List<TestProject> projects = getProjects();
		for (TestProject testProject : projects) {
			if (testProject.getName().equals(testProjectName)) {
				return testProject;
			}
		}
		return null;
	}

	@Override
	public TestStructure findTestStructureByFullName(String testStructureFullName) throws SystemException {
		String oldNamePath = containsFullNameRenamedElements(testStructureFullName);
		if (oldNamePath != null) {
			testStructureFullName = testStructureFullName.replace(oldNamePath, renamedProjects.remove(oldNamePath));
		}
		String[] testStructurePath = testStructureFullName.split("\\.");
		TestProject testProject = getProjectWithName(testStructurePath[0]);
		if (testProject != null) {
			return testProject.getTestChildByFullName(testStructureFullName);
		} else {
			return null;
		}
	}

	/**
	 * Checks if the renamed Elements contains a subpath to the teststructure.
	 * 
	 * @param testStructureFullName
	 *            to be searched for.
	 * @return the old name or null if there is no one.
	 */
	protected String containsFullNameRenamedElements(String testStructureFullName) {
		Set<String> oldnames = renamedProjects.keySet();
		for (String oldName : oldnames) {
			if (testStructureFullName.indexOf(oldName) == 0) {
				return oldName;
			}
		}
		return null;
	}

	@Override
	public Object compute(IEclipseContext context, String contextKey) {
		if (eventBroker == null) {
			eventBroker = context.getActive(IEventBroker.class);
			fileWatchService.setContext(context);
			registerAllProjectInFileWatcher();
		}
		return this;
	}

	/**
	 * Drops all existing FileWatchers and create new ones for the testproject
	 * list.
	 */
	private void registerAllProjectInFileWatcher() {
		if (testProjects != null) {
			fileWatchService.dropWatchers();
			for (TestProject testProject : testProjects) {
				fileWatchService.watch(testProject);
			}
		}
	}

	/**
	 * 
	 * @param cfgVersion
	 *            the config version as a string
	 * @return true, if config version is supported, else false
	 */
	private boolean isConfigVersionSupported(String cfgVersion) {
		return TestProjectService.SUPPORTED_VERSIONS.contains(cfgVersion);
	}

	@Override
	public void reloadProjectList() throws SystemException {
		if (testProjects != null) {
			oldTestProjects = new ArrayList<TestProject>();
			oldTestProjects.addAll(testProjects);
		}
		loadProjectListFromFileSystem();
	}

	@Override
	public void reloadTestProjectFromFileSystem(TestProject testProject) throws SystemException {
		File dir = new File(Platform.getLocation().toFile() + File.separator + testProject.getName());
		loadProjectConfigFromFileSystem(testProject, dir);
		registerProject(testProject);
		if (eventBroker != null) {
			eventBroker.post(TestEditorCoreEventConstants.TESTSTRUCTURE_MODEL_CHANGED_UPDATE_BY_MODIFY,
					testProject.getName());
		}
	}

	/**
	 * 
	 * @param testProject
	 *            is registered to the list of Testprojects. If the project is
	 *            allready in the list, it will be removed and added new.
	 */
	private void registerProject(TestProject testProject) {
		if (getProjects().contains(testProject)) {
			getProjects().remove(testProject);
		}
		getProjects().add(testProject);
	}

	/**
	 * Activates the Service after the osgi system has created it. This method
	 * loads the Projects in the Workspace.
	 * 
	 * @param componentContext
	 *            of the osgi framework.
	 */
	public void activate(ComponentContext componentContext) {
		try {
			loadProjectListFromFileSystem();
		} catch (SystemException e) {
			logger.error("Error loadin projects on startup.", e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean existsProjectWithName(String projectName) {
		return getProjectWithName(projectName) != null;
	}

	/**
	 * 
	 * @return eventbroker of this service.
	 */
	protected IEventBroker getEventBroker() {
		return eventBroker;
	}

}
