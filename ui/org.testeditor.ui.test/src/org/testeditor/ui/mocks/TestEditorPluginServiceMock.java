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
package org.testeditor.ui.mocks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.eclipse.e4.core.services.translation.TranslationService;
import org.testeditor.core.model.action.ProjectLibraryConfig;
import org.testeditor.core.model.team.TeamShareConfig;
import org.testeditor.core.services.interfaces.FieldMappingExtension;
import org.testeditor.core.services.interfaces.TestEditorPlugInService;
import org.testeditor.core.services.plugins.LibraryConfigurationServicePlugIn;
import org.testeditor.core.services.plugins.TeamShareConfigurationServicePlugIn;

/**
 * 
 * Mock of the TestEditorPluginService. It contains mocks for library and
 * teamshare.
 * 
 */
public class TestEditorPluginServiceMock implements TestEditorPlugInService {
	@Override
	public Map<String, String> getAsProperties(ProjectLibraryConfig projectLibraryConfig) {
		return null;
	}

	@Override
	public Collection<LibraryConfigurationServicePlugIn> getAllLibraryConfigurationServices() {
		ArrayList<LibraryConfigurationServicePlugIn> result = new ArrayList<LibraryConfigurationServicePlugIn>();
		result.add(getLibraryConfigurationServiceMock("xmlservice", "XML"));
		result.add(getLibraryConfigurationServiceMock("dbservice", "DB"));
		return result;
	}

	/**
	 * 
	 * @param id
	 *            of the library plugin Mock
	 * @param pluginName
	 *            of the library plugin Mock
	 * @return library plugin Mock
	 */
	private LibraryConfigurationServicePlugIn getLibraryConfigurationServiceMock(final String id,
			final String pluginName) {
		return new LibraryConfigurationServicePlugIn() {

			@Override
			public String getId() {
				return id;
			}

			@Override
			public Map<String, String> getAsProperties(ProjectLibraryConfig projectLibraryConfig) {
				return null;
			}

			@Override
			public ProjectLibraryConfig createProjectLibraryConfigFrom(Properties properties) {
				return null;
			}

			@Override
			public String getTranslatedHumanReadableLibraryPlugInName(TranslationService service) {
				return pluginName;
			}

			@Override
			public List<FieldMappingExtension> getConfigUIExtensions() {
				return new ArrayList<FieldMappingExtension>();
			}

			@Override
			public ProjectLibraryConfig createEmptyProjectLibraryConfig() {
				return null;
			}

			@Override
			public String getTemplateForConfiguration() {
				return null;
			}
		};
	}

	@Override
	public ProjectLibraryConfig createProjectLibraryConfigFrom(Properties properties) {
		return null;
	}

	@Override
	public LibraryConfigurationServicePlugIn getLibraryConfigurationServiceFor(String id) {
		for (LibraryConfigurationServicePlugIn service : getAllLibraryConfigurationServices()) {
			if (service.getId().equals(id)) {
				return service;
			}
		}
		return null;
	}

	@Override
	public Collection<TeamShareConfigurationServicePlugIn> getAllTeamShareConfigurationServices() {
		List<TeamShareConfigurationServicePlugIn> result = new ArrayList<TeamShareConfigurationServicePlugIn>();
		result.add(new TeamShareConfigurationServicePlugIn() {

			@Override
			public String getTranslatedHumanReadablePlugInName(TranslationService translationService) {
				return "SVN";
			}

			@Override
			public String getId() {
				return "svn";
			}

			@Override
			public TeamShareConfig createAnEmptyTeamShareConfig() {
				return null;
			}

			@Override
			public Map<String, String> getAsProperties(TeamShareConfig teamShareConfig) {
				return null;
			}

			@Override
			public TeamShareConfig createTeamShareConfigFrom(Properties properties) {
				return null;
			}

			@Override
			public String getTemplateForConfiguration() {
				return null;
			}

			@Override
			public List<FieldMappingExtension> getFieldMappingExtensions() {
				return null;
			}
		});
		return result;
	}

	@Override
	public TeamShareConfigurationServicePlugIn getTeamShareConfigurationServiceFor(String id) {
		return null;
	}

	@Override
	public Map<String, String> getAsProperties(TeamShareConfig teamShareConfig) {
		return null;
	}

	@Override
	public TeamShareConfig createTeamShareConfigFrom(Properties properties) {
		return null;
	}

}
