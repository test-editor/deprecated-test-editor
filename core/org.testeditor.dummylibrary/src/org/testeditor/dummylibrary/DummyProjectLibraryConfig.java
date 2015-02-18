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
package org.testeditor.dummylibrary;

import org.testeditor.core.model.action.ProjectLibraryConfig;
import org.testeditor.core.model.teststructure.LibraryLoadingStatus;

/**
 * 
 * Example Implementation of the <code>ProjectLibraryConfig</code>. This class
 * is only a dummy, to show the usage of the framework.
 * 
 */
public class DummyProjectLibraryConfig implements ProjectLibraryConfig {

	public static final String ID = "org.testeditor.dummylibrary";
	private LibraryLoadingStatus libraryLoadingStatus = new LibraryLoadingStatus();

	@Override
	public String getId() {
		return ID;
	}

	/**
	 * 
	 * @return a Dummy Name used in the Service.
	 */
	public String getDummyName() {
		return "Dummy";
	}

	@Override
	public LibraryLoadingStatus getLibraryLoadingStatus() {
		return libraryLoadingStatus;
	}

	@Override
	public void setLibraryLoadingStatus(LibraryLoadingStatus libraryLoadingStatus) {
		this.libraryLoadingStatus = libraryLoadingStatus;
	}

	@Override
	public ProjectLibraryConfig copyConfigurationToDestination(String nameOfTamplateProject, String destination) {
		return null;
	}

	@Override
	public void renameConfigurationToDestination(String oldName, String newName) {

	}

}
