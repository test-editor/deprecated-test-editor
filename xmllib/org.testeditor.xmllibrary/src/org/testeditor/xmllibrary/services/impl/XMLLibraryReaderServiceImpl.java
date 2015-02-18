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

import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.Platform;
import org.testeditor.core.exceptions.CorrruptLibraryException;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.model.action.Action;
import org.testeditor.core.model.action.ActionGroup;
import org.testeditor.core.model.action.ProjectActionGroups;
import org.testeditor.core.model.action.ProjectLibraryConfig;
import org.testeditor.core.model.teststructure.LibraryLoadingStatus;
import org.testeditor.core.services.interfaces.LibraryReadException;
import org.testeditor.core.services.interfaces.LibraryReaderService;
import org.testeditor.core.services.interfaces.ObjectTreeConstructionException;
import org.testeditor.core.util.FileLocatorService;
import org.testeditor.xmllibrary.domain.binding.TechnicalBindingTypes;
import org.testeditor.xmllibrary.model.XMLProjectLibraryConfig;
import org.testeditor.xmllibrary.utils.ActionGroupMapper;
import org.testeditor.xmllibrary.utils.JaxbMarshaller;
import org.xml.sax.SAXException;

/**
 * 
 * this class provides the libraryReaderService for the reading of the
 * XML-Library.
 * 
 * @author llipinski
 */
public class XMLLibraryReaderServiceImpl implements LibraryReaderService {

	private static final Logger LOGGER = Logger.getLogger(XMLLibraryReaderServiceImpl.class);

	private org.testeditor.xmllibrary.domain.action.ActionGroups allActionGroupsDomain;
	private TechnicalBindingTypes technicalBindingTypes;
	private FileLocatorService fileLocatorService;

	/**
	 * {@inheritDoc}
	 * 
	 * @throws SystemException
	 */
	@Override
	public ProjectActionGroups readBasisLibrary(ProjectLibraryConfig libraryConfig) throws LibraryReadException,
			ObjectTreeConstructionException {
		File wsDir = Platform.getLocation().toFile();
		String userDir = wsDir.getAbsolutePath();
		String pathXmlActionGroup;
		String pathXmlBindings;
		XMLProjectLibraryConfig xmlConfig = (XMLProjectLibraryConfig) libraryConfig;
		pathXmlActionGroup = xmlConfig.getPathToXmlActionGroup();
		pathXmlBindings = xmlConfig.getPathToXmlTechnicalBindings();
		if (!new File(pathXmlActionGroup).exists()) {
			pathXmlActionGroup = new StringBuffer(userDir).append(File.separator)
					.append(xmlConfig.getPathToXmlActionGroup()).toString();
		}
		if (!new File(pathXmlBindings).exists()) {
			pathXmlBindings = new StringBuffer(userDir).append(File.separator)
					.append(xmlConfig.getPathToXmlTechnicalBindings()).toString();
		}
		try {
			String pathActionGroupsXSD = getPathToActionGroupsXSD();
			String pathTechnicalBindingsXSD = getPathToTechnicalBindingsXSD();
			if (!testFilesExists(libraryConfig.getLibraryLoadingStatus(), pathActionGroupsXSD, pathXmlActionGroup,
					pathTechnicalBindingsXSD, pathXmlBindings)) {
				LOGGER.error("XSD Files not found.");
				return null;
			}

			allActionGroupsDomain = JaxbMarshaller.unmarshal(pathActionGroupsXSD, pathXmlActionGroup,
					org.testeditor.xmllibrary.domain.action.ActionGroups.class);

			technicalBindingTypes = JaxbMarshaller.unmarshal(pathTechnicalBindingsXSD, pathXmlBindings,
					TechnicalBindingTypes.class);
		} catch (JAXBException e) {
			throw new LibraryReadException(e);
		} catch (SAXException e) {
			throw new LibraryReadException(e);
		} catch (IOException e) {
			throw new LibraryReadException(e);
		}
		return createCoreObjects();
	}

	/**
	 * 
	 * @return the Path to the technical binding xsd.
	 * @throws IOException
	 *             on accessing files.
	 */
	protected String getPathToTechnicalBindingsXSD() throws IOException {
		return new StringBuffer(fileLocatorService.getBundleLocationFor(this.getClass())).append(File.separator)
				.append("resources").append(File.separator).append("TechnicalBindingTypeCollection.xsd").toString();
	}

	/**
	 * 
	 * @return the Path to the action group xsd.
	 * @throws IOException
	 *             on accessing files.
	 */
	protected String getPathToActionGroupsXSD() throws IOException {
		return new StringBuffer(fileLocatorService.getBundleLocationFor(this.getClass())).append(File.separator)
				.append("resources").append(File.separator).append("AllActionGroups.xsd").toString();
	}

	/**
	 * Test for all files the existing. If one not exist, then it returns false.
	 * 
	 * @param libraryLoadingStatus
	 *            the {@link LibraryLoadingStatus}
	 * @param pathXsdActionGroup
	 *            fileName with path of the actionGroupXsd
	 * @param pathXmlActionGroup
	 *            fileName with path of the actionGroupXml
	 * @param pathXsdBindings
	 *            fileName with path of the technicalBindingXsd
	 * @param pathXmlBindings
	 *            fileName with path of the technicalBindingXml
	 * @return boolean false if one file not exists, else true
	 * @throws LibraryReadException
	 *             if a file is not founded
	 */
	private boolean testFilesExists(LibraryLoadingStatus libraryLoadingStatus, String pathXsdActionGroup,
			String pathXmlActionGroup, String pathXsdBindings, String pathXmlBindings) throws LibraryReadException {
		libraryLoadingStatus.getErrorWhileLoadingList().clear();
		libraryLoadingStatus.setErrorLessLoaded(true);
		libraryLoadingStatus.setLoaded(true);
		testFileExist(libraryLoadingStatus, pathXsdActionGroup);
		testFileExist(libraryLoadingStatus, pathXmlActionGroup);
		testFileExist(libraryLoadingStatus, pathXsdBindings);
		testFileExist(libraryLoadingStatus, pathXmlBindings);
		libraryLoadingStatus.setErrorLessLoaded(libraryLoadingStatus.getErrorWhileLoadingList().isEmpty());
		if (!libraryLoadingStatus.isErrorLessLoaded()) {
			StringBuilder messageBuilder = new StringBuilder();
			for (String fileName : libraryLoadingStatus.getErrorWhileLoadingList()) {
				messageBuilder.append("File not found: ").append(fileName).append("\n");
			}
			throw new LibraryReadException(new SystemException(messageBuilder.toString()));
		}
		return libraryLoadingStatus.isErrorLessLoaded();
	}

	/**
	 * Checks the exist of one file.
	 * 
	 * @param libraryLoadingStatus
	 *            the {@link LibraryLoadingStatus}
	 * @param fileName
	 *            fileName with path
	 * @return boolean false if file not exists, else true
	 * @throws LibraryReadException
	 */
	private boolean testFileExist(LibraryLoadingStatus libraryLoadingStatus, String fileName) {
		File f = new File(fileName);
		if (f.exists()) {
			return true;
		} else {
			if (!libraryLoadingStatus.getErrorWhileLoadingList().contains(fileName)) {
				libraryLoadingStatus.getErrorWhileLoadingList().add(fileName);
			}
			return false;
		}
	}

	/**
	 * Creates the core-objects.
	 * 
	 * @return the ProjectActionGroups
	 * @throws ObjectTreeConstructionException
	 *             if the tree couln't constructed
	 */
	private ProjectActionGroups createCoreObjects() throws ObjectTreeConstructionException {
		ProjectActionGroups projectActionGroups = new ProjectActionGroups();
		for (org.testeditor.xmllibrary.domain.action.ActionGroup actionGroupDomain : allActionGroupsDomain
				.getActionGroup()) {
			ActionGroup actionGroup = new ActionGroup();
			actionGroup.setName(actionGroupDomain.getName());
			actionGroup.setSorting(actionGroupDomain.getSort());
			for (org.testeditor.xmllibrary.domain.action.Action actionDomain : actionGroupDomain.getAction()) {

				try {
					Action action = ActionGroupMapper.mapAction(actionDomain, technicalBindingTypes);
					actionGroup.addAction(action);
				} catch (CorrruptLibraryException e) {
					LOGGER.error("Error in ActionGroup: " + actionGroup.getName(), e);
				}
			}
			actionGroup.sortActions();
			projectActionGroups.addActionGroup(actionGroup);
		}
		projectActionGroups.sortActionGroups();
		for (org.testeditor.xmllibrary.domain.binding.TechnicalBindingType technicalBindingType : technicalBindingTypes
				.getTechnicalBindingType()) {
			projectActionGroups
					.addTechnicalBindingType(ActionGroupMapper.mapTechnicalBindingType(technicalBindingType));
		}
		return projectActionGroups;
	}

	@Override
	public String getId() {
		return XMLProjectLibraryConfig.ID;
	}

	/**
	 * 
	 * @param fileLocatorService
	 *            used in this service
	 * 
	 */
	public void bind(FileLocatorService fileLocatorService) {
		this.fileLocatorService = fileLocatorService;
		LOGGER.info("Bind FileLocatorService");
	}

	/**
	 * 
	 * @param fileLocatorService
	 *            removed from system
	 */
	public void unBind(FileLocatorService fileLocatorService) {
		this.fileLocatorService = null;
		LOGGER.info("Unbind FileLocatorService");
	}

}
