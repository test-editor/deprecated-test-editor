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
package org.testeditor.xmllibrary.model;

import org.testeditor.core.model.action.ProjectLibraryConfig;
import org.testeditor.core.model.teststructure.LibraryLoadingStatus;

/**
 * 
 * ProjectActionGroupsConfig for XML Based fixture scanner.
 * 
 */
public class XMLProjectLibraryConfig implements ProjectLibraryConfig {

	public static final String ID = "org.testeditor.xmllibrary";
	private String pathToXmlActionGroup = "";
	private String pathToXmlTechnicalBindings = "";
	private LibraryLoadingStatus libraryLoadingStatus = new LibraryLoadingStatus();

	/**
	 * default-constructor.
	 */
	public XMLProjectLibraryConfig() {
	};

	/**
	 * copy-constructor.
	 * 
	 * @param projectLibraryConfig
	 *            XMLProjectLibraryConfig
	 */
	public XMLProjectLibraryConfig(XMLProjectLibraryConfig projectLibraryConfig) {
		setPathToActionGroupXml(projectLibraryConfig.getPathToXmlActionGroup());
		setPathToXmlTechnicalBindings(projectLibraryConfig.getPathToXmlTechnicalBindings());
	}

	/**
	 * returns the path to the project ActionGroup-XML-File.
	 * 
	 * @return pathXmlActionGroup
	 */
	public String getPathToXmlActionGroup() {
		return pathToXmlActionGroup;
	}

	/**
	 * sets the path to the XmlActionGroup.
	 * 
	 * @param pathXmlActionGroup
	 *            the path to the XML-File of the ActionGroup
	 */
	public void setPathToActionGroupXml(String pathXmlActionGroup) {
		this.pathToXmlActionGroup = pathXmlActionGroup;
	}

	/**
	 * gets the relativ path to the TechnicalBindings-xml.
	 * 
	 * @return pathTechnicalBindings
	 */
	public String getPathToXmlTechnicalBindings() {
		return pathToXmlTechnicalBindings;
	}

	/**
	 * sets the path to the XmlActionGroup.
	 * 
	 * @param pathTechnicalBindings
	 *            the path to the XML-File of the TechnicalBindings
	 */
	public void setPathToXmlTechnicalBindings(String pathTechnicalBindings) {
		this.pathToXmlTechnicalBindings = pathTechnicalBindings;
	}

	@Override
	public String getId() {
		return ID;
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
		XMLProjectLibraryConfig copyOfProjectLibraryConfig = new XMLProjectLibraryConfig(this);
		copyOfProjectLibraryConfig.renameConfigurationToDestination(nameOfTamplateProject, destination);
		return copyOfProjectLibraryConfig;
	}

	@Override
	public void renameConfigurationToDestination(String oldName, String newName) {
		setPathToActionGroupXml(this.getPathToXmlActionGroup().replace(oldName, newName));
		setPathToXmlTechnicalBindings(pathToXmlTechnicalBindings.replace(oldName, newName));
	}

}
