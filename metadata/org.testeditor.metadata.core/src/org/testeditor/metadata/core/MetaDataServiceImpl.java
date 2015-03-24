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
import java.io.FileWriter;
import java.io.Writer;
import java.util.List;

import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.metadata.core.model.MetaDataStore;
import org.testeditor.metadata.core.model.MetaDataTag;
import org.testeditor.metadata.core.model.MetaDataTagList;
import org.testeditor.metadata.core.model.MetaDataValue;
import org.testeditor.metadata.core.model.MetaDataValueList;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class MetaDataServiceImpl implements MetaDataService {

	private static final String META_DATA_FILENAME = "metadata.xml";

	private XStream xStream;

	public MetaDataServiceImpl() {
		xStream = new XStream(new DomDriver("UTF-8"));
		xStream.alias("metaDataStore", MetaDataStore.class);
		xStream.alias("metaDataTagList", MetaDataTagList.class);
		xStream.alias("metaDataValueList", MetaDataValueList.class);
		xStream.alias("metaDataValue", MetaDataValue.class);
		xStream.alias("metaDataTag", MetaDataTag.class);
		xStream.autodetectAnnotations(true);

	}

	@Override
	public MetaDataStore getMetaDataStore(TestProject testProject) {

		File metaDataFile = getMetaDataFile(testProject);
		MetaDataStore store = new MetaDataStore();
		if (metaDataFile.exists()) {
			Object fromXML = xStream.fromXML(metaDataFile);

			if (fromXML instanceof MetaDataStore) {
				store = ((MetaDataStore) fromXML);
			}
		}
		store.setProject(testProject);
		return store;
	}

	@Override
	public void saveLists(MetaDataStore store) throws Exception {

		Writer writer = new FileWriter(getMetaDataFile(store.getProject()));
		xStream.toXML(store, writer);
		writer.close();

	}

	@Override
	public MetaDataTagList getTags(TestStructure testcase) {

		File xmlTagFile = getTagFile(testcase);
		MetaDataTagList tags = new MetaDataTagList();

		if (xmlTagFile.exists()) {
			Object fromXML = xStream.fromXML(xmlTagFile);
			if (fromXML instanceof MetaDataTagList) {
				tags = (MetaDataTagList) fromXML;
			}
		}

		tags.setTestcase(testcase);
		return tags;

	}

	@Override
	public void saveTags(MetaDataTagList metaDataTagList) throws Exception {

		Writer writer = new FileWriter(getTagFile(metaDataTagList.getTestcase()));
		xStream.toXML(metaDataTagList, writer);
		writer.close();
	}

	@Override
	public List<TestStructure> findDirtyTestcases(TestProject testProject) {
		// TODO
		return null;
	}

	private File getProjectRoot(TestProject testProject) {
		return new File(testProject.getTestProjectConfig().getProjectPath());
	}

	private File getMetaDataFile(TestProject testProject) {

		File metaDataFolder = new File(getProjectRoot(testProject), "metaData");
		if (!metaDataFolder.exists()) {
			metaDataFolder.mkdirs();
		}
		return new File(metaDataFolder, "/" + META_DATA_FILENAME);

	}

	private File getTagFile(TestStructure testcase) {
		File metaDataFolder = new File(getProjectRoot(testcase.getRootElement()), "metaData");
		if (!metaDataFolder.exists()) {
			metaDataFolder.mkdirs();
		}
		return new File(metaDataFolder, "/" + testcase.getFullName() + ".xml");
	}

}
