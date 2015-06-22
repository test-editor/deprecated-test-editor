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
import org.testeditor.core.services.interfaces.TeamShareConfigurationService;
import org.testeditor.core.services.interfaces.TeamShareService;
import org.testeditor.core.services.interfaces.TestEditorPlugInService;
import org.testeditor.core.services.interfaces.TestScenarioService;
import org.testeditor.core.services.interfaces.TestStructureContentService;
import org.testeditor.core.services.interfaces.TestStructureService;
import org.testeditor.core.services.plugins.LibraryConfigurationServicePlugIn;
import org.testeditor.core.services.plugins.LibraryReaderServicePlugIn;
import org.testeditor.core.services.plugins.TeamShareConfigurationServicePlugIn;
import org.testeditor.core.services.plugins.TeamShareServicePlugIn;
import org.testeditor.core.services.plugins.TestScenarioServicePlugIn;
import org.testeditor.core.services.plugins.TestStructureContentServicePlugIn;
import org.testeditor.core.services.plugins.TestStructureServicePlugIn;

/**
 * 
 * Implementation of the Plug-In Service of the TestEditor.
 * 
 */
public class TestEditorPlugInServiceImpl implements TestEditorPlugInService {

	private static final Logger LOGGER = Logger.getLogger(TestEditorPlugInServiceImpl.class);
	private Map<String, LibraryReaderServicePlugIn> libraryReaderServices = new HashMap<String, LibraryReaderServicePlugIn>();
	private Map<String, LibraryConfigurationServicePlugIn> libraryConfigurationServices = new HashMap<String, LibraryConfigurationServicePlugIn>();
	private Map<String, TeamShareConfigurationServicePlugIn> teamShareConfigurationServices = new HashMap<String, TeamShareConfigurationServicePlugIn>();
	private Map<String, TeamShareServicePlugIn> teamShareServices = new HashMap<String, TeamShareServicePlugIn>();
	private Map<String, TestStructureServicePlugIn> testStructureServices = new HashMap<String, TestStructureServicePlugIn>();
	private Map<String, TestStructureContentServicePlugIn> testStructureContentServices = new HashMap<String, TestStructureContentServicePlugIn>();
	private Map<String, TestScenarioServicePlugIn> testScenarioServices = new HashMap<String, TestScenarioServicePlugIn>();

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
	public void bind(TeamShareConfigurationServicePlugIn teamShareConfigurationService) {
		teamShareConfigurationServices.put(teamShareConfigurationService.getId(), teamShareConfigurationService);
		LOGGER.info("Binding TeamShareConfigurationService Plug-In "
				+ teamShareConfigurationService.getClass().getName());
	}

	/**
	 * 
	 * @param teamShareConfigurationService
	 *            to be removed.
	 */
	public void unBind(TeamShareConfigurationServicePlugIn teamShareConfigurationService) {
		teamShareConfigurationServices.remove(teamShareConfigurationService.getId());
		LOGGER.info("Removing TeamShareConfigurationService Plug-In "
				+ teamShareConfigurationService.getClass().getName());
	}

	/**
	 * 
	 * @param teamShareService
	 *            to be bind to this service.
	 */
	public void bind(TeamShareServicePlugIn teamShareService) {
		teamShareServices.put(teamShareService.getId(), teamShareService);
		LOGGER.info("Binding TeamShareService Plug-In " + teamShareService.getClass().getName());
	}

	/**
	 * 
	 * @param teamShareService
	 *            to be removed.
	 */
	public void unBind(TeamShareServicePlugIn teamShareService) {
		teamShareServices.remove(teamShareService.getId());
		LOGGER.info("Removing TeamShareService Plug-In " + teamShareService.getClass().getName());
	}

	/**
	 * 
	 * @param libraryConfigurationService
	 *            to be bind to this service.
	 */
	public void bind(LibraryConfigurationServicePlugIn libraryConfigurationService) {
		libraryConfigurationServices.put(libraryConfigurationService.getId(), libraryConfigurationService);
		LOGGER.info("Binding LibraryConfigurationService Plug-In " + libraryConfigurationService.getClass().getName());
	}

	/**
	 * 
	 * @param libraryConfigurationService
	 *            to be removed.
	 */
	public void unBind(LibraryConfigurationServicePlugIn libraryConfigurationService) {
		libraryConfigurationServices.remove(libraryConfigurationService.getId());
		LOGGER.info("Removing LibraryConfigurationService Plug-In " + libraryConfigurationService.getClass().getName());
	}

	/**
	 * 
	 * @param readerService
	 *            to be bind to this service.
	 */
	public void bind(LibraryReaderServicePlugIn readerService) {
		libraryReaderServices.put(readerService.getId(), readerService);
		LOGGER.info("Binding LibraryReaderService Plug-In " + readerService.getClass().getName());
	}

	/**
	 * 
	 * @param readerService
	 *            to be removed.
	 */
	public void unBind(LibraryReaderServicePlugIn readerService) {
		libraryReaderServices.remove(readerService.getId());
		LOGGER.info("Removing LibraryReaderService Plug-In " + readerService.getClass().getName());
	}

	/**
	 * 
	 * @param testStructureService
	 *            used in this service
	 * 
	 */
	public void bind(TestStructureServicePlugIn testStructureService) {
		this.testStructureServices.put(testStructureService.getId(), testStructureService);
		LOGGER.info("Bind TestStructureService Plug-In" + testStructureService.getClass().getName());
	}

	/**
	 * 
	 * @param testStructureContentService
	 *            removed from system
	 */
	public void unBind(TestStructureContentServicePlugIn testStructureContentService) {
		this.testStructureContentServices.remove(testStructureContentService.getId());
		LOGGER.info("UnBind TestStructureContentService Plug-In" + testStructureContentService.getClass().getName());
	}

	/**
	 * 
	 * @param testStructureContentService
	 *            used in this service
	 * 
	 */
	public void bind(TestStructureContentServicePlugIn testStructureContentService) {
		this.testStructureContentServices.put(testStructureContentService.getId(), testStructureContentService);
		LOGGER.info("Bind TestStructureContentService Plug-In" + testStructureContentService.getClass().getName());
	}

	/**
	 * 
	 * @param testStructureService
	 *            removed from system
	 */
	public void unBind(TestStructureServicePlugIn testStructureService) {
		this.testStructureServices.remove(testStructureService.getId());
		LOGGER.info("UnBind TestStructureService Plug-In" + testStructureService.getClass().getName());
	}

	/**
	 * 
	 * @param testScenarioService
	 *            used in this service
	 * 
	 */
	public void bind(TestScenarioServicePlugIn testScenarioService) {
		this.testScenarioServices.put(testScenarioService.getId(), testScenarioService);
		LOGGER.info("Bind TestScenarioService Plug-In" + testScenarioService.getClass().getName());
	}

	/**
	 * 
	 * @param testScenarioService
	 *            removed from system
	 */
	public void unBind(TestScenarioServicePlugIn testScenarioService) {
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
	public Collection<LibraryConfigurationServicePlugIn> getAllLibraryConfigurationServices() {
		return libraryConfigurationServices.values();
	}

	@Override
	public LibraryConfigurationServicePlugIn getLibraryConfigurationServiceFor(String id) {
		return libraryConfigurationServices.get(id);
	}

	@Override
	public Collection<TeamShareConfigurationServicePlugIn> getAllTeamShareConfigurationServices() {
		return teamShareConfigurationServices.values();
	}

	@Override
	public Collection<TeamShareServicePlugIn> getAllTeamShareServices() {
		return teamShareServices.values();
	}

	@Override
	public TeamShareConfigurationServicePlugIn getTeamShareConfigurationServiceFor(String id) {
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
