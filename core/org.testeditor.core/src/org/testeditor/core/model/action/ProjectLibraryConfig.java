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
package org.testeditor.core.model.action;

import org.testeditor.core.model.teststructure.LibraryLoadingStatus;

/**
 * 
 * Definition of the Config of a ProjectActionGroups. Known Implementation is
 * the Configuration for XML based ProjectActionGroups. This Backend Service
 * needs the location of the xml files in the configuration.
 * 
 */
public interface ProjectLibraryConfig {

	/**
	 * This id is used to identify the library plug-in. It must the same ID in
	 * the <code>LibraryConfigurationService</code> and in the
	 * <code>LibraryReaderService</code>
	 * 
	 * @return ID to identify the Service Implementation.
	 */
	String getId();

	/**
	 * get the laodingStatus of the last loading.
	 * 
	 * @return the loading Status as {@link LibraryLoadingStatus}
	 */
	LibraryLoadingStatus getLibraryLoadingStatus();

	/**
	 * sets the libraryloadingstatus.
	 * 
	 * @param libraryLoadingStatus
	 *            LibraryLoadingStatus
	 */
	void setLibraryLoadingStatus(LibraryLoadingStatus libraryLoadingStatus);

	/**
	 * copies the projectLibrary to the new destination and returns the new
	 * configuration.
	 * 
	 * @param nameOfTamplateProject
	 *            name of the template-project
	 * @param destination
	 *            as a String
	 * @return the new configuration.
	 * 
	 */
	ProjectLibraryConfig copyConfigurationToDestination(String nameOfTamplateProject, String destination);

	/**
	 * renames the projectLibrary to the new destination.
	 * 
	 * @param oldName
	 *            the old name
	 * @param newName
	 *            the new name of the project
	 */
	void renameConfigurationToDestination(String oldName, String newName);
}
