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

import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.metadata.core.model.MetaData;
import org.testeditor.metadata.core.model.MetaData.MetaDataTagListComparator;
import org.testeditor.metadata.core.model.MetaDataTag;
import org.testeditor.metadata.core.model.MetaDataValue;

/**
 * The MetaDataServiceAbstractBase implements the MetaDataService without
 * implementing the persistence.<br/>
 * The MetaDataServiceAbstractBase contains three different maps where the data
 * is stored:
 * <ul>
 * <li>metaDataMap - the repository of all MetaData information of the project.
 * (A Meta data constains of a name and a list of MetDataValues). It does not
 * contains the mapping of testCases to the MetaData.
 * <li>metaDataStore - a map of the testcases and the associated metaDataValues.
 * the metaDataValues are stored as a list MetaDataTags. A metaDataTag refers to
 * a MetaDataValue by a key.
 * <li>allMetaDataValues - a map of all metaDataValues identified by there
 * key.This is the connection between the metaDataTag and the metaDataValue.
 * </ul>
 * Every project has its own metaData. So all three datastructures exist for
 * every project. They are stored in maps and are identified by the projectname.
 *
 */
public abstract class MetaDataServiceAbstractBase implements MetaDataService {

	private Map<String, Map<String, MetaData>> metaDataMap = new TreeMap<String, Map<String, MetaData>>();
	private Map<String, Map<String, List<MetaDataTag>>> metaDataStore = new TreeMap<String, Map<String, List<MetaDataTag>>>();
	private Map<String, Map<String, MetaDataValue>> allMetaDataValues = new TreeMap<String, Map<String, MetaDataValue>>();

	/**
	 * Get the repository of all MetaData information of a project. (A Meta data
	 * contains of a name and a list of MetDataValues). It does not contains the
	 * mapping of testCases to the MetaData.
	 * 
	 * @see MetaDataServiceAbstractBase.getMetaDataStore
	 * @param key
	 *            - the name of the project
	 * @return the MetaDataMap for the project.
	 */
	protected Map<String, MetaData> getMetaDataMap(String key) {
		if (!metaDataMap.containsKey(key)) {
			throw new RuntimeException("no data for  " + key + " in metaDataMap");
		}
		return metaDataMap.get(key);
	}

	/**
	 * A Map used for the lookup of the MetaDataValue and the MetaDataTag from a
	 * testCase.
	 * 
	 * @see MetaDataServiceAbstractBase.getMetaDataStore
	 * @param key
	 *            - the name of the project
	 * @return the AllMetaDataValues for the project.
	 */
	protected Map<String, MetaDataValue> getAllMetaDataValues(String key) {
		if (!allMetaDataValues.containsKey(key)) {
			throw new RuntimeException("no data for  " + key + " in allMetaDataValues");
		}
		return allMetaDataValues.get(key);
	}

	/**
	 * The repository of the testcases and the associated metaDataValues. The
	 * metaDataValues of a testcase are stored as a list of MetaDataTags. A
	 * metaDataTag refers to a MetaDataValue by a key.
	 * 
	 * @param key
	 *            - the name of the project
	 * @return the MetaDataStore for the project.
	 */
	protected Map<String, List<MetaDataTag>> getMetaDataStore(String key) {
		if (!metaDataStore.containsKey(key)) {
			metaDataStore.put(key, new TreeMap<String, List<MetaDataTag>>());
		}
		return metaDataStore.get(key);
	}

	/**
	 * Constructor for the MetaDataServiceAbstractBase. Setup all internal
	 * datastructures but without data.
	 */
	public MetaDataServiceAbstractBase() {
	}

	/**
	 * Initializes the data for a project. Data are only read if no data for the
	 * project is stored in the interal structures. To force the reload of the
	 * data, the method refresh has to be called. <br>
	 * The method will only prepare the datastructures for the project. The
	 * access to the persistence is done in the abstact method doInit.
	 * 
	 * @param testProject
	 *            the project
	 */
	protected void init(TestProject testProject) {
		String projectName = testProject.getFullName();
		if (!metaDataMap.containsKey(projectName)) {
			metaDataMap.put(projectName, new TreeMap<String, MetaData>());
			allMetaDataValues.put(projectName, new TreeMap<String, MetaDataValue>());
			metaDataStore.put(projectName, new TreeMap<String, List<MetaDataTag>>());
			doInit(testProject);
		}
	}

	/**
	 * abstract method ot read metadata from the persistence.
	 * 
	 * @param testProject
	 *            - the testproject
	 */
	abstract protected void doInit(TestProject testProject);

	@Override
	public List<MetaData> getAllMetaData(TestProject project) {
		init(project);
		ArrayList<MetaData> list = new ArrayList<MetaData>();
		list.addAll(getMetaDataMap(project.getFullName()).values());
		Collections.sort(list, new MetaDataTagListComparator());
		return list;
	}

	@Override
	public MetaDataValue getMetaDataValue(MetaDataTag metaDataTag, TestProject project) {
		init(project);
		if (getAllMetaDataValues(project.getFullName()).containsKey(metaDataTag.getGlobalKey())) {
			return getAllMetaDataValues(project.getFullName()).get(metaDataTag.getGlobalKey());
		}
		return null;
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
	public void storeMetaDataTags(List<MetaDataTag> metaDataTags, TestStructure testStructure) throws SystemException {
		init(testStructure.getRootElement());
		String projectName = testStructure.getRootElement().getFullName();
		if (!getMetaDataStore(projectName).containsKey(testStructure.getFullName())) {
			getMetaDataStore(projectName).put(testStructure.getFullName(), new ArrayList<MetaDataTag>());
		}
		getMetaDataStore(projectName).get(testStructure.getFullName()).clear();
		getMetaDataStore(projectName).get(testStructure.getFullName()).addAll(metaDataTags);
		store(testStructure);

	}

	/**
	 * abstract method to store metadata for a teststructure. The implementation
	 * is done in the persistence.
	 * 
	 * @param testStructure
	 *            - the teststructure
	 * @throws SystemException
	 *             - a systemexception
	 */
	abstract protected void store(TestStructure testStructure) throws SystemException;

	@Override
	public void rename(TestStructure testStructure, String newName) throws SystemException {
		if (metaDataStore.containsKey(testStructure.getFullName())) {
			init(testStructure.getRootElement());
			String projectName = testStructure.getRootElement().getFullName();
			String newFullName = testStructure.getParent().getFullName() + "." + newName;
			getMetaDataStore(projectName).put(newFullName, getMetaDataTags(testStructure));
			getMetaDataStore(projectName).get(testStructure.getFullName()).clear();
			store(testStructure);
			String oldName = testStructure.getName();
			testStructure.setName(newName);
			store(testStructure);
			testStructure.setName(oldName);
		}
	}

	@Override
	public void delete(TestStructure testStructure) throws SystemException {
		init(testStructure.getRootElement());
		String projectName = testStructure.getRootElement().getFullName();
		getMetaDataStore(projectName).get(testStructure.getFullName()).clear();
		store(testStructure);
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

	@Override
	public void refresh(TestProject testProject) {
		if (metaDataMap.containsKey(testProject.getFullName())) {
			metaDataMap.remove(testProject.getFullName());
			allMetaDataValues.remove(testProject.getFullName());
			metaDataStore.remove(testProject.getFullName());
		}

	}

}
