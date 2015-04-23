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
import java.io.InputStream;
import java.io.Writer;
import java.util.List;
import java.util.Map;
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
		xStream.autodetectAnnotations(true);
	}

	@Override
	public void store(TestProject testProject) {

		String rootPath = testProject.getTestProjectConfig().getProjectPath();
		store(testProject.getFullName(), rootPath);
	}

	protected void store(String projectName, String projectPath) {

		Writer writer;

		try {
			writer = new FileWriter(projectPath + File.separator + META_DATA_XML);
			xStream.toXML(getMetaDataStore(projectName), writer);
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void doInit(String projectName, String projectPath) {

		Properties prop = new Properties();

		String fileName = projectPath + File.separator + META_DATA_PROPERTIES;
		try {
			File propertiesFile = new File(fileName);
			if (!propertiesFile.exists()) {
				return;
			}
			InputStream inputStream = new FileInputStream(propertiesFile);
			prop.load(inputStream);
		} catch (FileNotFoundException e) {
			throw new RuntimeException("the propertiesfile " + fileName + " was not found");
		} catch (IOException e) {
			throw new RuntimeException("the propertiesfile " + fileName + " could not be read. Reason: " + e, e);
		}
		for (Object object : prop.keySet()) {
			String key = (String) object;
			if (key.indexOf('.') == -1) {
				MetaData metaData = new MetaData(key, (String) prop.get(key));
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
		File xmlTagFile = new File(projectPath + File.separator + META_DATA_XML);

		getMetaDataStore(projectName).clear();
		if (xmlTagFile.exists()) {
			Object fromXML = xStream.fromXML(xmlTagFile);
			if (fromXML instanceof Map<?, ?>) {
				Map<String, List<MetaDataTag>> map = (Map<String, List<MetaDataTag>>) fromXML;
				getMetaDataStore(projectName).putAll(map);
			}
		}

	}

}
