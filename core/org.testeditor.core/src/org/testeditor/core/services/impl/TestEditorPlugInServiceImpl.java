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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.testeditor.core.model.action.ProjectLibraryConfig;
import org.testeditor.core.model.team.TeamShareConfig;
import org.testeditor.core.services.interfaces.LibraryConfigurationService;
import org.testeditor.core.services.interfaces.LibraryReaderService;
import org.testeditor.core.services.interfaces.TeamShareConfigurationService;
import org.testeditor.core.services.interfaces.TeamShareService;
import org.testeditor.core.services.interfaces.TestEditorPlugInService;
import org.testeditor.core.services.interfaces.TestScenarioService;
import org.testeditor.core.services.interfaces.TestStructureContentService;
import org.testeditor.core.services.interfaces.TestStructureService;

/**
 * 
 * Implementation of the Plug-In Service of the TestEditor.
 * 
 */
public class TestEditorPlugInServiceImpl implements TestEditorPlugInService {

	private static final Logger LOGGER = Logger.getLogger(TestEditorPlugInServiceImpl.class);
	private Map<String, LibraryReaderService> libraryReaderServices = new HashMap<String, LibraryReaderService>();
	private Map<String, LibraryConfigurationService> libraryConfigurationServices = new HashMap<String, LibraryConfigurationService>();
	private Map<String, TeamShareConfigurationService> teamShareConfigurationServices = new HashMap<String, TeamShareConfigurationService>();
	private Map<String, TeamShareService> teamShareServices = new HashMap<String, TeamShareService>();
	private Map<String, TestStructureService> testStructureServices = new HashMap<String, TestStructureService>();
	private Map<String, TestStructureContentService> testStructureContentServices = new HashMap<String, TestStructureContentService>();
	private Map<String, TestScenarioService> testScenarioServices = new HashMap<String, TestScenarioService>();

	@Override
	public ProjectLibraryConfig createProjectLibraryConfigFrom(Properties properties) {
		String plugInID = properties.getProperty(TestEditorPlugInService.LIBRARY_ID);
		LibraryConfigurationService libraryConfigurationService = libraryConfigurationServices.get(plugInID);
		if (libraryConfigurationService != null) {
			return libraryConfigurationService.createProjectLibraryConfigFrom(properties);
		} else {
			return null;
		}
	}

	/**
	 * 
	 * @param teamShareConfigurationService
	 *            to be bind to this service.
	 */
	public void bind(TeamShareConfigurationService teamShareConfigurationService) {
		teamShareConfigurationServices.put(teamShareConfigurationService.getId(), teamShareConfigurationService);
		LOGGER.info("Binding TeamShareConfigurationService Plug-In "
				+ teamShareConfigurationService.getClass().getName());
	}

	/**
	 * 
	 * @param teamShareConfigurationService
	 *            to be removed.
	 */
	public void unBind(TeamShareConfigurationService teamShareConfigurationService) {
		teamShareConfigurationServices.remove(teamShareConfigurationService.getId());
		LOGGER.info("Removing TeamShareConfigurationService Plug-In "
				+ teamShareConfigurationService.getClass().getName());
	}

	/**
	 * 
	 * @param teamShareService
	 *            to be bind to this service.
	 */
	public void bind(TeamShareService teamShareService) {
		teamShareServices.put(teamShareService.getId(), teamShareService);
		LOGGER.info("Binding TeamShareService Plug-In " + teamShareService.getClass().getName());
	}

	/**
	 * 
	 * @param teamShareService
	 *            to be removed.
	 */
	public void unBind(TeamShareService teamShareService) {
		teamShareServices.remove(teamShareService.getId());
		LOGGER.info("Removing TeamShareService Plug-In " + teamShareService.getClass().getName());
	}

	/**
	 * 
	 * @param libraryConfigurationService
	 *            to be bind to this service.
	 */
	public void bind(LibraryConfigurationService libraryConfigurationService) {
		libraryConfigurationServices.put(libraryConfigurationService.getId(), libraryConfigurationService);
		LOGGER.info("Binding LibraryConfigurationService Plug-In " + libraryConfigurationService.getClass().getName());
	}

	/**
	 * 
	 * @param libraryConfigurationService
	 *            to be removed.
	 */
	public void unBind(LibraryConfigurationService libraryConfigurationService) {
		libraryConfigurationServices.remove(libraryConfigurationService.getId());
		LOGGER.info("Removing LibraryConfigurationService Plug-In " + libraryConfigurationService.getClass().getName());
	}

	/**
	 * 
	 * @param readerService
	 *            to be bind to this service.
	 */
	public void bind(LibraryReaderService readerService) {
		libraryReaderServices.put(readerService.getId(), readerService);
		LOGGER.info("Binding LibraryReaderService Plug-In " + readerService.getClass().getName());
	}

	/**
	 * 
	 * @param readerService
	 *            to be removed.
	 */
	public void unBind(LibraryReaderService readerService) {
		libraryReaderServices.remove(readerService.getId());
		LOGGER.info("Removing LibraryReaderService Plug-In " + readerService.getClass().getName());
	}

	/**
	 * 
	 * @param testStructureService
	 *            used in this service
	 * 
	 */
	public void bind(TestStructureService testStructureService) {
		this.testStructureServices.put(testStructureService.getId(), testStructureService);
		LOGGER.info("Bind TestStructureService Plug-In" + testStructureService.getClass().getName());
	}

	/**
	 * 
	 * @param testStructureContentService
	 *            removed from system
	 */
	public void unBind(TestStructureContentService testStructureContentService) {
		this.testStructureContentServices.remove(testStructureContentService.getId());
		LOGGER.info("UnBind TestStructureContentService Plug-In" + testStructureContentService.getClass().getName());
	}

	/**
	 * 
	 * @param testStructureContentService
	 *            used in this service
	 * 
	 */
	public void bind(TestStructureContentService testStructureContentService) {
		this.testStructureContentServices.put(testStructureContentService.getId(), testStructureContentService);
		LOGGER.info("Bind TestStructureContentService Plug-In" + testStructureContentService.getClass().getName());
	}

	/**
	 * 
	 * @param testStructureService
	 *            removed from system
	 */
	public void unBind(TestStructureService testStructureService) {
		this.testStructureServices.remove(testStructureService.getId());
		LOGGER.info("UnBind TestStructureService Plug-In" + testStructureService.getClass().getName());
	}

	/**
	 * 
	 * @param testScenarioService
	 *            used in this service
	 * 
	 */
	public void bind(TestScenarioService testScenarioService) {
		this.testScenarioServices.put(testScenarioService.getId(), testScenarioService);
		LOGGER.info("Bind TestScenarioService Plug-In" + testScenarioService.getClass().getName());
	}

	/**
	 * 
	 * @param testScenarioService
	 *            removed from system
	 */
	public void unBind(TestScenarioService testScenarioService) {
		this.testStructureServices.remove(testScenarioService.getId());
		LOGGER.info("UnBind TestScenarioService Plug-In" + testScenarioService.getClass().getName());
	}

	@Override
	public Map<String, String> getAsProperties(ProjectLibraryConfig projectLibraryConfig) {
		LibraryConfigurationService libraryConfigurationService = libraryConfigurationServices.get(projectLibraryConfig
				.getId());
		Map<String, String> properties = libraryConfigurationService.getAsProperties(projectLibraryConfig);
		properties.put(TestEditorPlugInService.LIBRARY_ID, projectLibraryConfig.getId());
		return properties;
	}

	@Override
	public Collection<LibraryConfigurationService> getAllLibraryConfigurationServices() {
		return libraryConfigurationServices.values();
	}

	@Override
	public LibraryConfigurationService getLibraryConfigurationServiceFor(String id) {
		return libraryConfigurationServices.get(id);
	}

	@Override
	public Collection<TeamShareConfigurationService> getAllTeamShareConfigurationServices() {
		return teamShareConfigurationServices.values();
	}

	@Override
	public Collection<TeamShareService> getAllTeamShareServices() {
		return teamShareServices.values();
	}

	@Override
	public TeamShareConfigurationService getTeamShareConfigurationServiceFor(String id) {
		return teamShareConfigurationServices.get(id);
	}

	@Override
	public Map<String, String> getAsProperties(TeamShareConfig teamShareConfig) {
		TeamShareConfigurationService teamShareConfigurationService = teamShareConfigurationServices
				.get(teamShareConfig.getId());
		Map<String, String> properties = teamShareConfigurationService.getAsProperties(teamShareConfig);
		properties.put(TestEditorPlugInService.TEAMSHARE_ID, teamShareConfig.getId());
		return properties;
	}

	@Override
	public TeamShareConfig createTeamShareConfigFrom(Properties properties) {
		String plugInID = properties.getProperty(TestEditorPlugInService.TEAMSHARE_ID);
		TeamShareConfigurationService teamShareConfigurationService = teamShareConfigurationServices.get(plugInID);
		if (teamShareConfigurationService == null) {
			return null;
		}
		return teamShareConfigurationService.createTeamShareConfigFrom(properties);
	}

	@Override
	public TeamShareService getTeamShareServiceFor(String id) {
		return teamShareServices.get(id);
	}

	@Override
	public TestStructureService getTestStructureServiceFor(String testServerID) {
		return testStructureServices.get(testServerID);
	}

	@Override
	public TestStructureContentService getTestStructureContentServiceFor(String testServerID) {
		return testStructureContentServices.get(testServerID);
	}

	@Override
	public TestScenarioService getTestScenarioService(String testServerID) {
		return testScenarioServices.get(testServerID);
	}

}
