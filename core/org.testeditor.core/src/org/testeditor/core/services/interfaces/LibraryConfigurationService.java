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
import org.testeditor.core.model.action.ProjectLibraryConfig;

/**
 * 
 * Implementation of an <code>LibraryReaderService</code> needs a Configuration
 * Service to Load, Store and show the UI Config.
 * 
 */
public interface LibraryConfigurationService {

	/**
	 * 
	 * Create a new ProjectLibraryConfig and fills this bean with values from
	 * the properties.
	 * 
	 * @param properties
	 *            with the values for the config.
	 * @return new ProjectLibraryConfig with the values of the properties.
	 */
	ProjectLibraryConfig createProjectLibraryConfigFrom(Properties properties);

	/**
	 * This id is used to identify the library plug-in. It must the same ID in
	 * the <code>ProjectLibraryConfig</code> and in the
	 * <code>LibraryReaderService</code>
	 * 
	 * @return the id of the implementation
	 */
	String getId();

	/**
	 * This method is called for to store the config of the library in the
	 * project properties.
	 * 
	 * @param projectLibraryConfig
	 *            to be transformed in a map
	 * @return ProjectLibraryConfig as Map to be stored as properties.
	 */
	Map<String, String> getAsProperties(ProjectLibraryConfig projectLibraryConfig);

	/**
	 * The Name of the Library-Plug-In shown in the UI.
	 * 
	 * @param translationService
	 *            used to translate the Library Plug-In name.
	 * 
	 * @return the Human readable Name of the Plug-In
	 */
	String getTranslatedHumanReadableLibraryPlugInName(TranslationService translationService);

	/**
	 * The UI uses this method to get a List of <code>FieldDeclaration</code>.
	 * This FieldDeclaration are used by the UI to display and update the
	 * Plug-In Configuration.
	 * 
	 * @return List with FieldDeclaration for the UI od the Plug-In
	 *         configuration.
	 */
	List<FieldDeclaration> getFieldDeclarations();

	/**
	 * 
	 * @return an empty Plug-In specific implementation of
	 *         <code>ProjectLibraryConfig</code>.
	 */
	ProjectLibraryConfig createEmptyProjectLibraryConfig();

	/**
	 * 
	 * @return the template for the configuration
	 */
	String getTemplateForConfiguration();

}
