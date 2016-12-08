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
package org.testeditor.core.services.plugins;

import java.util.Collection;

/**
 * 
 * This Service manages the Communication of the Plug-Ins of the Testeditor.
 * 
 */
public interface TestEditorPlugInService {

	String LIBRARY_ID = "library_id";
	String TEAMSHARE_ID = "teamshare_plugin_id";

	/**
	 * Library Plug-Ins has to implement the interface
	 * <code>LibraryConfigurationService</code>. This method is used to get
	 * access of all available plug-ins for libraries.
	 * 
	 * @return a Collection of all registered
	 *         <code>LibraryConfigurationService</code> Service objects.
	 */
	Collection<LibraryConfigurationServicePlugIn> getAllLibraryConfigurationServices();

	/**
	 * Lookup the LibraryConfigurationService with for the id.
	 * 
	 * @param id
	 *            used to identify the library plug-in.
	 * 
	 * @return LibraryConfigurationService that has the same id.
	 */
	LibraryConfigurationServicePlugIn getLibraryConfigurationServiceFor(String id);

	/**
	 * TeamShare Plug-Ins have to implement the interface
	 * <code>TeamShareConfigurationService</code>. This method is used to get
	 * access of all available plug-ins for libraries.
	 * 
	 * @return a Collection of all registered
	 *         <code>TeamShareConfigurationService</code> Service objects.
	 */
	Collection<TeamShareConfigurationServicePlugIn> getAllTeamShareConfigurationServices();

	/**
	 * Lookup the TeamShareConfigurationService with for the id.
	 * 
	 * @param id
	 *            used to identify the library plug-in.
	 * 
	 * @return TeamShareConfigurationService that has the same id.
	 */
	TeamShareConfigurationServicePlugIn getTeamShareConfigurationServiceFor(String id);

}
