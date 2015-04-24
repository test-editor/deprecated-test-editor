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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.metadata.core.model.MetaData;
import org.testeditor.metadata.core.model.MetaData.MetaDataTagListComparator;
import org.testeditor.metadata.core.model.MetaDataTag;
import org.testeditor.metadata.core.model.MetaDataValue;

public abstract class MetaDataServiceAbstractBase implements MetaDataService {

	private Map<String, Map<String, MetaData>> metaDataMap = new TreeMap<String, Map<String, MetaData>>();
	private Map<String, Map<String, MetaDataValue>> allMetaDataValues = new TreeMap<String, Map<String, MetaDataValue>>();
	private Map<String, Map<String, List<MetaDataTag>>> metaDataStore = new TreeMap<String, Map<String, List<MetaDataTag>>>();

	protected Map<String, MetaData> getMetaDataMap(String key) {
		if (!metaDataMap.containsKey(key)) {
			throw new RuntimeException("no data for  " + key + " in metaDataMap");
		}
		return metaDataMap.get(key);
	}

	protected Map<String, MetaDataValue> getAllMetaDataValues(String key) {
		if (!allMetaDataValues.containsKey(key)) {
			throw new RuntimeException("no data for  " + key + " in allMetaDataValues");
		}
		return allMetaDataValues.get(key);
	}

	protected Map<String, List<MetaDataTag>> getMetaDataStore(String key) {
		if (!metaDataStore.containsKey(key)) {
			throw new RuntimeException("no data for  " + key + " in metaDataStore");
		}
		return metaDataStore.get(key);
	}

	public MetaDataServiceAbstractBase() {
	}

	protected void init(String projectName, String rootPath) {
		if (!metaDataMap.containsKey(projectName)) {
			metaDataMap.put(projectName, new TreeMap<String, MetaData>());
			allMetaDataValues.put(projectName, new TreeMap<String, MetaDataValue>());
			metaDataStore.put(projectName, new TreeMap<String, List<MetaDataTag>>());
			doInit(projectName, rootPath);
		}
	}

	protected void init(TestProject testProject) {
		String projectName = testProject.getFullName();
		if (!metaDataMap.containsKey(projectName)) {
			metaDataMap.put(projectName, new TreeMap<String, MetaData>());
			allMetaDataValues.put(projectName, new TreeMap<String, MetaDataValue>());
			metaDataStore.put(projectName, new TreeMap<String, List<MetaDataTag>>());
			doInit(testProject.getName(), testProject.getTestProjectConfig().getProjectPath());
		}
	}

	abstract protected void doInit(String projectName, String rootPath);

	@Override
	public List<MetaData> getAllMetaData(TestProject project) {
		init(project);
		ArrayList<MetaData> list = new ArrayList<MetaData>();
		list.addAll(getMetaDataMap(project.getFullName()).values());
		Collections.sort(list, new MetaDataTagListComparator());
		return list;
	}

	public List<MetaData> getAllMetaData(String projectName, String rootPath) {
		init(projectName, rootPath);
		ArrayList<MetaData> list = new ArrayList<MetaData>();
		list.addAll(getMetaDataMap(projectName).values());
		Collections.sort(list, new MetaDataTagListComparator());
		return list;
	}

	@Override
	public MetaDataValue getMetaDataValue(MetaDataTag metaDataTag, TestProject project) {
		init(project);
		if (getAllMetaDataValues(project.getFullName()).containsKey(metaDataTag.getGlobalKey())) {
			return getAllMetaDataValues(project.getFullName()).get(metaDataTag.getGlobalKey());
		}
		throw new IllegalArgumentException("No values found for metaDataTage with global key "
				+ metaDataTag.getGlobalKey());
	}

	@Override
	public List<MetaDataTag> getMetaDataTags(TestStructure testStructure) {
		init(testStructure.getRootElement());
		String projectName = testStructure.getRootElement().getFullName();
		List<MetaDataTag> value = new ArrayList<MetaDataTag>();
		if (getMetaDataStore(projectName).containsKey(testStructure.getFullName())) {
			value.addAll(getMetaDataStore(projectName).get(testStructure.getFullName()));
		}
		return value;
	}

	@Override
	public void storeMetaDataTags(List<MetaDataTag> metaDataTags, TestStructure testStructure) {
		init(testStructure.getRootElement());
		String projectName = testStructure.getRootElement().getFullName();
		if (!getMetaDataStore(projectName).containsKey(testStructure.getFullName())) {
			getMetaDataStore(projectName).put(testStructure.getFullName(), new ArrayList<MetaDataTag>());
		}
		getMetaDataStore(projectName).get(testStructure.getFullName()).clear();
		getMetaDataStore(projectName).get(testStructure.getFullName()).addAll(metaDataTags);
		store(testStructure.getRootElement());

	}

	abstract public void store(TestProject testProject);

	@Override
	public void rename(TestStructure testStructure, String newName) {
		init(testStructure.getRootElement());
		String projectName = testStructure.getRootElement().getFullName();
		String newFullName = testStructure.getParent().getFullName() + "." + newName;
		getMetaDataStore(projectName).put(newFullName, getMetaDataTags(testStructure));
		getMetaDataStore(projectName).remove(testStructure.getFullName());
		store(testStructure.getRootElement());
	}

	@Override
	public void delete(TestStructure testStructure) {
		init(testStructure.getRootElement());
		String projectName = testStructure.getRootElement().getFullName();
		getMetaDataStore(projectName).remove(testStructure.getFullName());
		store(testStructure.getRootElement());
	}

	@Override
	public List<String> getTestCases(TestProject testProject, List<MetaDataValue> metaDataValueList) {
		if (metaDataValueList.size() == 0) {
			return new ArrayList<String>();
		}
		if (metaDataValueList.size() == 1) {
			return getTestCases(testProject, metaDataValueList.get(0));
		}
		List<MetaDataTag> metaDataTags = new ArrayList<MetaDataTag>();
		for (MetaDataValue metaDataValue : metaDataValueList) {
			metaDataTags.add(new MetaDataTag(metaDataValue));
		}
		List<String> testCases = new ArrayList<String>();
		String projectName = testProject.getName();

		for (String testCase : getMetaDataStore(projectName).keySet()) {
			List<MetaDataTag> testCaseMetaDataTags = getMetaDataStore(projectName).get(testCase);
			int foundElemets = metaDataTags.size();
			for (MetaDataTag testCaseMetaDataTag : testCaseMetaDataTags) {
				for (MetaDataTag metaDataTag : metaDataTags) {
					if (metaDataTag.equals(testCaseMetaDataTag)) {
						foundElemets--;
						break;
					}
				}
			}
			if (foundElemets == 0) {
				testCases.add(testCase);
			}
		}
		return testCases;
	}

	@Override
	public List<String> getTestCases(TestProject testProject, MetaDataValue metaDataValue) {
		List<String> testCases = new ArrayList<String>();
		String projectName = testProject.getName();
		MetaDataTag metaDataTag = new MetaDataTag(metaDataValue);
		for (String testCase : getMetaDataStore(projectName).keySet()) {
			for (MetaDataTag indexMetaDataTag : getMetaDataStore(projectName).get(testCase)) {
				if (metaDataTag.equals(indexMetaDataTag)) {
					testCases.add(testCase);
				}
			}
		}
		return testCases;
	}

}
