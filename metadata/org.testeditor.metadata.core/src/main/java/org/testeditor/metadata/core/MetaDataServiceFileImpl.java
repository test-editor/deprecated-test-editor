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
package org.testeditor.metadata.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.services.interfaces.TeamShareService;
import org.testeditor.metadata.core.model.MetaData;
import org.testeditor.metadata.core.model.MetaDataTag;
import org.testeditor.metadata.core.model.MetaDataValue;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * The MetaDataServiceFileImpl realizes the persistence of the metadata
 * information on file basis. For every testcase is a metadata.xml stored in the
 * fitnesse directory of the testcase. The metadata.properties contains the list
 * of all existing metadata.<br/>
 * The metadata.properties could be edited by the user, the metadata.xml files
 * are written automatically by the program.
 *
 */
public class MetaDataServiceFileImpl extends MetaDataServiceAbstractBase {
	private static final String META_DATA_XML = "metadata.xml";
	private static final String META_DATA_PROPERTIES = "metadata.properties";
	private XStream xStream;

	private TeamShareService teamShareService;
	private static final Logger LOGGER = Logger.getLogger(MetaDataServiceFileImpl.class);

	/**
	 * The contstructor will prepare the XSTREAM for the metadata.xml - files.
	 */
	public MetaDataServiceFileImpl() {
		super();
		xStream = new XStream(new DomDriver("UTF-8"));
		xStream.alias("metaDataTagList", MetaData.class);
		xStream.alias("metaDataValueList", MetaData.class);
		xStream.alias("metaDataValue", MetaDataValue.class);
		xStream.alias("metaDataTag", MetaDataTag.class);
		xStream.alias("metaDataStoreObject", MetaDataStoreObject.class);
		xStream.autodetectAnnotations(true);
	}

	@Override
	public void store(TestStructure testStructure) throws SystemException {

		String fileName = "";
		TestProject testProject = testStructure.getRootElement();
		String projectPath = testProject.getTestProjectConfig().getProjectPath();
		try {

			String testCaseName = testStructure.getFullName();
			String testCasePath = "FitNesseRoot" + File.separator + testCaseName.replace(".", File.separator);
			fileName = projectPath + File.separator + testCasePath;
			if (new File(fileName).exists()) {
				fileName = projectPath + File.separator + testCasePath + File.separator + META_DATA_XML;
				File xmlFile = new File(fileName);
				List<MetaDataTag> metaDataTags = getMetaDataStore(testProject.getName()).get(testCaseName);
				if (metaDataTags != null) {
					Writer writer = new FileWriter(xmlFile);
					List<MetaDataStoreObject> metaDataStoreObjects = new ArrayList<MetaDataStoreObject>();
					metaDataStoreObjects.add(new MetaDataStoreObject(testCaseName, metaDataTags));
					xStream.toXML(metaDataStoreObjects, writer);
					writer.close();
					if (teamShareService != null) {
						teamShareService.addAdditonalFile(testStructure, META_DATA_XML);
					} else {
						LOGGER.warn("Teamshare serive not configured for MetaDataService");
					}
				} else {
					if (xmlFile.exists()) {
						if (teamShareService != null) {
							teamShareService.removeAdditonalFile(testStructure, META_DATA_XML);
						} else {
							LOGGER.warn("Teamshare serive not configured for MetaDataService");
						}
						if (!xmlFile.delete()) {
							throw new RuntimeException("could not delete MetaDataFile " + fileName);
						}
					}
				}
			}

		} catch (IOException e) {
			throw new RuntimeException("the metadata.xml " + fileName + " could not be written. Reason: " + e, e);
		}
	}

	protected void store(String projectName, String projectPath) {

		String fileName = "";
		try {
			List<String> testCases = new ArrayList<String>();
			testCases.addAll(getMetaDataStore(projectName).keySet());
			Collections.sort(testCases);

			for (String testCase : testCases) {
				String testCasePath = "FitNesseRoot" + File.separator + testCase.replace(".", File.separator);
				fileName = projectPath + File.separator + testCasePath + File.separator + META_DATA_XML;
				File xmlFile = new File(fileName);
				Writer writer = new FileWriter(xmlFile);
				List<MetaDataTag> metaDataTags = getMetaDataStore(projectName).get(testCase);
				List<MetaDataStoreObject> metaDataStoreObjects = new ArrayList<MetaDataStoreObject>();
				metaDataStoreObjects.add(new MetaDataStoreObject(testCase, metaDataTags));
				xStream.toXML(metaDataStoreObjects, writer);
				writer.close();
			}

		} catch (IOException e) {
			throw new RuntimeException("the metadata.xml " + fileName + " could not be written. Reason: " + e, e);
		}
	}

	@Override
	protected void doInit(TestProject testProject) {

		String projectName = testProject.getName();
		String projectPath = testProject.getTestProjectConfig().getProjectPath();

		Properties prop = readMetaDataProperties(projectPath);

		if (prop == null) {
			return;
		}

		for (Object object : prop.keySet()) {
			String key = (String) object;
			if (key.indexOf('.') == -1) {
				MetaData metaData = new MetaData(key, (String) prop.get(key), testProject);
				getMetaDataMap(projectName).put(metaData.getKey(), metaData);
			}
		}
		for (Object object : prop.keySet()) {
			String key = (String) object;
			if (key.indexOf('.') != -1) {
				String key1 = key.substring(0, key.indexOf('.'));
				String key2 = key.substring(key.indexOf('.') + 1);
				if (getMetaDataMap(projectName).containsKey(key1)) {
					MetaDataValue metaDataValue = new MetaDataValue(getMetaDataMap(projectName).get(key1), key2,
							(String) prop.get(key));
					getAllMetaDataValues(projectName).put(metaDataValue.getGlobalKey(), metaDataValue);
				}
			}

		}

		getMetaDataStore(projectName).clear();

		String projectRootPath = projectPath + File.separator + "FitNesseRoot" + File.separator + projectName;
		File projectRoot = new File(projectRootPath);
		readMetaDataForFolder(testProject, projectRoot);

	}

	/**
	 * Read the properties-file containing the metadata.properties. If no file
	 * was found, a null is returned.
	 * 
	 * @param projectPath
	 *            - the path of the project where the properties have to be
	 *            read.
	 * @return an object containg all properties of the project or null.
	 */
	private Properties readMetaDataProperties(String projectPath) {
		Properties prop = new Properties();

		String propertiesFileName = projectPath + File.separator + META_DATA_PROPERTIES;
		File propertiesFile = null;
		InputStreamReader inputStream = null;
		try {
			propertiesFile = new File(propertiesFileName);
			if (!propertiesFile.exists()) {
				return null;
			}
			inputStream = new InputStreamReader(new FileInputStream(propertiesFile), "UTF-8");
			prop.load(inputStream);
		} catch (FileNotFoundException e) {
			throw new RuntimeException("the propertiesfile " + propertiesFileName + " was not found");
		} catch (IOException e) {
			throw new RuntimeException("the propertiesfile " + propertiesFileName + " could not be read. Reason: " + e,
					e);
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (Throwable e) {
				LOGGER.error("could not close inputStream. Message " + e.getMessage(), e);
			}
		}
		return prop;
	}

	private void readMetaDataForFolder(TestProject testProject, File directory) {

		File[] listOfFiles = directory.listFiles();
		for (int i = 0; listOfFiles != null && i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile() && listOfFiles[i].getName().equals(META_DATA_XML)) {
				String xmlTagFile = listOfFiles[i].getAbsolutePath();
				Object fromXML = null;
				try {
					fromXML = xStream.fromXML(new File(xmlTagFile));
				} catch (Exception e) {
					LOGGER.error("could not read xml in file '" + xmlTagFile + "'");
				}
				if (fromXML != null && fromXML instanceof List<?>) {
					List<MetaDataStoreObject> metaDataStoreObjects = (List<MetaDataStoreObject>) fromXML;
					if (metaDataStoreObjects != null) {
						for (MetaDataStoreObject metaDataStoreObject : metaDataStoreObjects) {
							getMetaDataStore(testProject.getName()).put(metaDataStoreObject.getTestCase(),
									metaDataStoreObject.getMetaDataTags());

							for (MetaDataTag metaDataTag : metaDataStoreObject.getMetaDataTags()) {
								MetaDataValue metaDataValue = getMetaDataValue(metaDataTag, testProject);
								if (metaDataValue != null) {
									metaDataValue.getTestCases().add(metaDataStoreObject.getTestCase());
								} else {
									LOGGER.error("Unknown metaDataTag '" + metaDataTag.getGlobalKey() + "'");
								}
							}
						}
					}
				}

			} else if (listOfFiles[i].isDirectory()) {
				readMetaDataForFolder(testProject, listOfFiles[i]);
			}
		}
	}

	public void unBindTeamShareService(TeamShareService teamShareService) {
		this.teamShareService = null;
	}

	public void bindTeamShareService(TeamShareService teamShareService) {
		this.teamShareService = teamShareService;
	}

	/**
	 * The MetaDataStoreObject is used during the reading and writing of the
	 * metadata.xml of a testcase. Its task is to order the entries in the
	 * metadata.xml file to ensure the same order of the entries.
	 */
	private static class MetaDataStoreObject {
		public String getTestCase() {
			return testCase;
		}

		public List<MetaDataTag> getMetaDataTags() {
			return metaDataTags;
		}

		private String testCase;
		private List<MetaDataTag> metaDataTags;

		public MetaDataStoreObject(String testCase, List<MetaDataTag> metaDataTags) {
			this.testCase = testCase;
			this.metaDataTags = metaDataTags;
		}

	}

}
