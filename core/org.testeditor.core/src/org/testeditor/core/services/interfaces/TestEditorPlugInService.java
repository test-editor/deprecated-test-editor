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

import java.util.Collection;
import java.util.Map;
import java.util.Properties;

import org.testeditor.core.model.action.ProjectLibraryConfig;
import org.testeditor.core.model.team.TeamShareConfig;

/**
 * 
 * This Service manages the Communication of the Plug-Ins of the Testeditor.
 * 
 */
public interface TestEditorPlugInService {

	String LIBRARY_ID = "library_id";
	String TEAMSHARE_ID = "teamshare_plugin_id";

	/**
	 * Creates a <code>ProjectLibraryConfig</code> with the values of the
	 * properties.
	 * 
	 * @param properties
	 *            to be passed to the ProjectLibraryConfig
	 * @return ProjectLibraryConfig
	 */
	ProjectLibraryConfig createProjectLibraryConfigFrom(Properties properties);

	/**
	 * Looks the correct ConfigurationService up and delegates the work to the
	 * service. Stores the ID of the ProjectLibraryConfig in the properties.
	 * 
	 * @param projectLibraryConfig
	 *            to be transformed in a map
	 * @return ProjectLibraryConfig as Map to be stored as properties.
	 */
	Map<String, String> getAsProperties(ProjectLibraryConfig projectLibraryConfig);

	/**
	 * Library Plug-Ins has to implement the interface
	 * <code>LibraryConfigurationService</code>. This method is used to get
	 * access of all available plug-ins for libraries.
	 * 
	 * @return a Collection of all registered
	 *         <code>LibraryConfigurationService</code> Service objects.
	 */
	Collection<LibraryConfigurationService> getAllLibraryConfigurationService();

	/**
	 * Lookup the LibraryConfigurationService with for the id.
	 * 
	 * @param id
	 *            used to identify the library plug-in.
	 * 
	 * @return LibraryConfigurationService that has the same id.
	 */
	LibraryConfigurationService getLibraryConfigurationServiceFor(String id);

	/**
	 * TeamShare Plug-Ins have to implement the interface
	 * <code>TeamShareConfigurationService</code>. This method is used to get
	 * access of all available plug-ins for libraries.
	 * 
	 * @return a Collection of all registered
	 *         <code>TeamShareConfigurationService</code> Service objects.
	 */
	Collection<TeamShareConfigurationService> getAllTeamShareConfigurationServices();

	/**
	 * This Method returns all registered implementations of the
	 * <code>TeamShareService</code>. TeamShare Plug-Ins have to implement the
	 * interface <code>TeamShareService</code> for synchronize a
	 * <code>TestProject</code>.
	 * 
	 * @return a Collection of all registered <code>TeamShareService</code>
	 *         Service objects.
	 */
	Collection<TeamShareService> getAllTeamShareServices();

	/**
	 * Lookup the TeamShareConfigurationService with for the id.
	 * 
	 * @param id
	 *            used to identify the library plug-in.
	 * 
	 * @return TeamShareConfigurationService that has the same id.
	 */
	TeamShareConfigurationService getTeamShareConfigurationServiceFor(String id);

	/**
	 * Lookup the TeamShareService with for the id.
	 * 
	 * @param id
	 *            used to identify the teamshare plug-in.
	 * 
	 * @return TeamShareService that has the same id.
	 */
	TeamShareService getTeamShareServiceFor(String id);

	/**
	 * Looks the correct ConfigurationService up and delegates the work to the
	 * service. Stores the ID of the Plug-In Config in the properties.
	 * 
	 * @param teamShareConfig
	 *            to be transformed in a map
	 * @return ProjectLibraryConfig as Map to be stored as properties.
	 */
	Map<String, String> getAsProperties(TeamShareConfig teamShareConfig);

	/**
	 * Creates a <code>teamShareConfig</code> with the values of the properties.
	 * 
	 * @param properties
	 *            to be passed to the TeamShareConfig
	 * @return TeamShareConfig
	 */
	TeamShareConfig createTeamShareConfigFrom(Properties properties);

	/**
	 * Lookup the TestStructureService with for the id.
	 * 
	 * @param testServerID
	 *            used to identify the TestServer plug-in.
	 * 
	 * @return TestStructureService that has the same id.
	 */
	TestStructureService getTestStructureServiceFor(String testServerID);

	/**
	 * Lookup the TestStructureContentService with for the id.
	 * 
	 * @param testServerID
	 *            used to identify the TestServer plug-in.
	 * 
	 * @return TestStructureContentService that has the same id.
	 */
	TestStructureContentService getTestStructureContentServiceFor(String testServerID);

	/**
	 * Lookup the TestScenarioService with for the id.
	 * 
	 * @param testServerID
	 *            used to identify the TestServer plug-in.
	 * 
	 * @return TestScenarioService that has the same id.
	 */
	TestScenarioService getTestScenarioService(String testServerID);

}
