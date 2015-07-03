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

import org.apache.log4j.Logger;
import org.testeditor.core.services.plugins.LibraryConfigurationServicePlugIn;
import org.testeditor.core.services.plugins.TeamShareConfigurationServicePlugIn;
import org.testeditor.core.services.plugins.TestEditorPlugInService;

/**
 * 
 * Implementation of the Plug-In Service of the TestEditor.
 * 
 */
public class TestEditorPlugInServiceImpl implements TestEditorPlugInService {

	private static final Logger LOGGER = Logger.getLogger(TestEditorPlugInServiceImpl.class);
	private Map<String, LibraryConfigurationServicePlugIn> libraryConfigurationServices = new HashMap<String, LibraryConfigurationServicePlugIn>();
	private Map<String, TeamShareConfigurationServicePlugIn> teamShareConfigurationServices = new HashMap<String, TeamShareConfigurationServicePlugIn>();

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
	public TeamShareConfigurationServicePlugIn getTeamShareConfigurationServiceFor(String id) {
		return teamShareConfigurationServices.get(id);
	}

}
