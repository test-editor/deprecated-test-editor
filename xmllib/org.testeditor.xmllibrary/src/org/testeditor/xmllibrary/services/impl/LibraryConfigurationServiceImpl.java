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
package org.testeditor.xmllibrary.services.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.eclipse.e4.core.services.translation.TranslationService;
import org.testeditor.core.model.action.ProjectLibraryConfig;
import org.testeditor.core.services.interfaces.FieldMappingExtension;
import org.testeditor.core.services.interfaces.LibraryConfigurationService;
import org.testeditor.core.services.interfaces.TestProjectService;
import org.testeditor.xmllibrary.model.XMLProjectLibraryConfig;

/**
 * 
 * This Implementation manages the LibraryConfiguration of the XML
 * implementation of the <code>LibraryReaderService</code>.
 * 
 */
public class LibraryConfigurationServiceImpl implements LibraryConfigurationService {

	private static final String PATH_XML_ACTIONGROUP = "library.xmllibrary.actiongroup";
	private static final String PATH_XML_TECHNICALBINDINGS = "library.xmllibrary.technicalbindings";

	private static final String PATH_XML_ACTIONGROUP1DOT1 = "pathXmlActionGroup";
	private static final String PATH_XML_TECHNICALBINDINGS1DOT1 = "pathXmlTechnicalBindings";

	@Override
	public ProjectLibraryConfig createProjectLibraryConfigFrom(Properties properties) {
		// TODO Refactor this method to comunicate the migrate stratagy
		String version = properties.getProperty(TestProjectService.VERSION_TAG);
		if (TestProjectService.VERSION1_1.equals(version) | !TestProjectService.SUPPORTED_VERSIONS.contains(version)) {
			return migrateConfigVersion(properties);
		}
		XMLProjectLibraryConfig xmlProjectLibraryConfig = new XMLProjectLibraryConfig();
		xmlProjectLibraryConfig.setPathToActionGroupXml(properties.getProperty(PATH_XML_ACTIONGROUP));
		xmlProjectLibraryConfig.setPathToXmlTechnicalBindings(properties.getProperty(PATH_XML_TECHNICALBINDINGS));
		return xmlProjectLibraryConfig;
	}

	/**
	 * gets the configuration from a older, but supported version, than the
	 * actual version.
	 * 
	 * @param properties
	 *            Properties
	 * @return the ProjectLibraryConfig
	 */
	private ProjectLibraryConfig migrateConfigVersion(Properties properties) {
		XMLProjectLibraryConfig xmlProjectLibraryConfig = new XMLProjectLibraryConfig();
		if (properties.getProperty(TestProjectService.VERSION_TAG).equals(TestProjectService.VERSION1_1)) {
			xmlProjectLibraryConfig.setPathToActionGroupXml(properties.getProperty(PATH_XML_ACTIONGROUP1DOT1));
			xmlProjectLibraryConfig.setPathToXmlTechnicalBindings(properties
					.getProperty(PATH_XML_TECHNICALBINDINGS1DOT1));
		}
		return xmlProjectLibraryConfig;
	}

	@Override
	public String getId() {
		return XMLProjectLibraryConfig.ID;
	}

	@Override
	public Map<String, String> getAsProperties(ProjectLibraryConfig projectLibraryConfig) {
		XMLProjectLibraryConfig xmlLibConfig = (XMLProjectLibraryConfig) projectLibraryConfig;
		Map<String, String> properties = new HashMap<String, String>();
		properties.put(PATH_XML_ACTIONGROUP, xmlLibConfig.getPathToXmlActionGroup());
		properties.put(PATH_XML_TECHNICALBINDINGS, xmlLibConfig.getPathToXmlTechnicalBindings());
		return properties;
	}

	@Override
	public String getTranslatedHumanReadableLibraryPlugInName(TranslationService translationService) {
		return translationService.translate("%testprojecteditor.xmlplugin", "platform:/plugin/org.testeditor.ui");
	}

	@Override
	public List<FieldMappingExtension> getConfigUIExtensions() {
		List<FieldMappingExtension> result = new ArrayList<FieldMappingExtension>();
		result.add(new PathToActionGroupFieldDeclaration());
		result.add(new PathToTechnicalBindingsXMLFieldDeclaration());
		return result;
	}

	@Override
	public ProjectLibraryConfig createEmptyProjectLibraryConfig() {
		return new XMLProjectLibraryConfig();
	}

	@Override
	public String getTemplateForConfiguration() {
		StringBuilder sb = new StringBuilder();
		sb.append("library.xmllibrary.actiongroup=$ACTION_SOURCE$").append("\n");
		sb.append("library.xmllibrary.technicalbindings=$TECHNICAL_BINDING_SOURCE$").append("\n");
		return sb.toString();
	}

}
