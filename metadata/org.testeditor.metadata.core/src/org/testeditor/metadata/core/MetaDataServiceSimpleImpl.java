/*******************************************************************************
a * Copyright (c) 2012 - 2015 Signal Iduna Corporation and others.
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
import java.io.InputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.metadata.core.model.MetaData;
import org.testeditor.metadata.core.model.MetaDataTag;
import org.testeditor.metadata.core.model.MetaDataValue;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class MetaDataServiceSimpleImpl extends MetaDataServiceAbstractBase {
	private static final String META_DATA_XML = "metadata.xml";
	private static final String META_DATA_PROPERTIES = "metadata.properties";
	private XStream xStream;

	public MetaDataServiceSimpleImpl() {
		xStream = new XStream(new DomDriver("UTF-8"));
		xStream.alias("metaDataTagList", MetaData.class);
		xStream.alias("metaDataValueList", MetaData.class);
		xStream.alias("metaDataValue", MetaDataValue.class);
		xStream.alias("metaDataTag", MetaDataTag.class);
		xStream.alias("metaDataStoreObject", MetaDataStoreObject.class);
		xStream.autodetectAnnotations(true);
	}

	@Override
	public void store(TestProject testProject) {

		String rootPath = testProject.getTestProjectConfig().getProjectPath();
		store(testProject.getFullName(), rootPath);
	}

	protected void store(String projectName, String projectPath) {

		Writer writer;

		String fileName = projectPath + File.separator + META_DATA_XML;
		try {
			writer = new FileWriter(projectPath + File.separator + META_DATA_XML);
			List<String> testCases = new ArrayList<String>();
			testCases.addAll(getMetaDataStore(projectName).keySet());
			Collections.sort(testCases);

			List<MetaDataStoreObject> metaDataStoreObjects = new ArrayList<MetaDataStoreObject>();

			for (String testCase : testCases) {
				List<MetaDataTag> metaDataTags = getMetaDataStore(projectName).get(testCase);
				metaDataStoreObjects.add(new MetaDataStoreObject(testCase, metaDataTags));
			}

			xStream.toXML(metaDataStoreObjects, writer);
			writer.close();
		} catch (IOException e) {
			throw new RuntimeException("the metadata.xml " + fileName + " could not be written. Reason: " + e, e);
		}
	}

	@Override
	protected void doInit(TestProject testProject) {

		String projectName = testProject.getName();
		String projectPath = testProject.getTestProjectConfig().getProjectPath();

		Properties prop = new Properties();

		String propertiesFileName = projectPath + File.separator + META_DATA_PROPERTIES;
		try {
			File propertiesFile = new File(propertiesFileName);
			if (!propertiesFile.exists()) {
				return;
			}
			InputStream inputStream = new FileInputStream(propertiesFile);
			prop.load(inputStream);
		} catch (FileNotFoundException e) {
			throw new RuntimeException("the propertiesfile " + propertiesFileName + " was not found");
		} catch (IOException e) {
			throw new RuntimeException("the propertiesfile " + propertiesFileName + " could not be read. Reason: " + e,
					e);
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
				MetaDataValue metaDataValue = new MetaDataValue(getMetaDataMap(projectName).get(key1), key2,
						(String) prop.get(key));
				getAllMetaDataValues(projectName).put(metaDataValue.getGlobalKey(), metaDataValue);
			}

		}
		String xmlFileName = projectPath + File.separator + META_DATA_XML;
		File xmlTagFile = new File(xmlFileName);

		getMetaDataStore(projectName).clear();
		if (xmlTagFile.exists()) {
			Object fromXML = xStream.fromXML(xmlTagFile);
			if (fromXML instanceof List<?>) {
				List<MetaDataStoreObject> metaDataStoreObjects = (List<MetaDataStoreObject>) fromXML;
				for (MetaDataStoreObject metaDataStoreObject : metaDataStoreObjects) {
					getMetaDataStore(projectName).put(metaDataStoreObject.getTestCase(),
							metaDataStoreObject.getMetaDataTags());
				}
			} else {
				throw new RuntimeException("illegal class of type " + fromXML.getClass().getName() + " found in "
						+ xmlFileName);
			}
		}

	}

	static private class MetaDataStoreObject {
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
