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
import org.testeditor.core.services.interfaces.LibraryConfigurationService;
import org.testeditor.core.services.interfaces.TeamShareConfigurationService;
import org.testeditor.core.services.interfaces.TeamShareService;
import org.testeditor.core.services.interfaces.TestEditorPlugInService;
import org.testeditor.core.services.interfaces.TestScenarioService;
import org.testeditor.core.services.interfaces.TestStructureContentService;
import org.testeditor.core.services.interfaces.TestStructureService;

/**
 * 
 * Mock of the TestEditorPluginService. It contains mocks for library and
 * teamshare.
 * 
 */
@SuppressWarnings("restriction")
public class TestEditorPluginServiceMock implements TestEditorPlugInService {
	@Override
	public Map<String, String> getAsProperties(ProjectLibraryConfig projectLibraryConfig) {
		return null;
	}

	@Override
	public Collection<LibraryConfigurationService> getAllLibraryConfigurationService() {
		ArrayList<LibraryConfigurationService> result = new ArrayList<LibraryConfigurationService>();
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
	private LibraryConfigurationService getLibraryConfigurationServiceMock(final String id, final String pluginName) {
		return new LibraryConfigurationService() {

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
	public LibraryConfigurationService getLibraryConfigurationServiceFor(String id) {
		for (LibraryConfigurationService service : getAllLibraryConfigurationService()) {
			if (service.getId().equals(id)) {
				return service;
			}
		}
		return null;
	}

	@Override
	public Collection<TeamShareConfigurationService> getAllTeamShareConfigurationServices() {
		List<TeamShareConfigurationService> result = new ArrayList<TeamShareConfigurationService>();
		result.add(new TeamShareConfigurationService() {

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
			public List<FieldMappingExtension> getConfigUIExtensions() {
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
		});
		return result;
	}

	@Override
	public Collection<TeamShareService> getAllTeamShareServices() {
		return null;
	}

	@Override
	public TeamShareConfigurationService getTeamShareConfigurationServiceFor(String id) {
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
	public TeamShareService getTeamShareServiceFor(String id) {
		return null;
	}

	@Override
	public TestStructureService getTestStructureServiceFor(String testServerID) {
		return null;
	}

	@Override
	public TestStructureContentService getTestStructureContentServiceFor(String testServerID) {
		return null;
	}

	@Override
	public TestScenarioService getTestScenarioService(String testServerID) {
		return null;
	}

}
