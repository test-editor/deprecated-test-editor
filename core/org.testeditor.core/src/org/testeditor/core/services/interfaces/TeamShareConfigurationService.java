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

import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.eclipse.e4.core.services.translation.TranslationService;
import org.testeditor.core.model.team.TeamShareConfig;

/**
 * Configuration Service for the Team Share Option. This Service manges the
 * Configuration of a TeamShareService.
 * 
 */
public interface TeamShareConfigurationService {

	/**
	 * 
	 * @param translationService
	 *            used for Translation.
	 * @return the language specific Name of the Plug-In
	 */
	String getTranslatedHumanReadablePlugInName(TranslationService translationService);

	/**
	 * The Plug-In needs the same ID for their services to identity the Plug-In.
	 * 
	 * @return Plug-In ID
	 */
	String getId();

	/**
	 * 
	 * @return a new and empty <code>TeamShareConfig</code> of the specific
	 *         Plug-In.
	 */
	TeamShareConfig createAnEmptyTeamShareConfig();

	/**
	 * The UI uses this method to get a List of <code>FieldDeclaration</code>.
	 * This FieldDeclaration are used by the UI to display and update the
	 * Plug-In Configuration.
	 * 
	 * @return List with FieldDeclaration for the UI od the Plug-In
	 *         configuration.
	 */
	List<FieldMappingExtension> getFieldDeclarations();

	/**
	 * This method is called for to store the config of the team share in the
	 * project properties.
	 * 
	 * @param teamShareConfig
	 *            to be transformed in a map
	 * @return TeamShareConfig as Map to be stored as properties.
	 */
	Map<String, String> getAsProperties(TeamShareConfig teamShareConfig);

	/**
	 * Creates a <code>TeamShareConfig</code> based on the values in the
	 * Properties.
	 * 
	 * @param properties
	 *            to be used as values for the TeamShareConfig
	 * @return TeamShareConfig
	 */
	TeamShareConfig createTeamShareConfigFrom(Properties properties);

	/**
	 * 
	 * @return the template for the configuration
	 */
	String getTemplateForConfiguration();

}
