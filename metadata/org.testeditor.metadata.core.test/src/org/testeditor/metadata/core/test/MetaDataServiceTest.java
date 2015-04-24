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
package org.testeditor.metadata.core.test;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testeditor.core.model.teststructure.TestCase;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestProjectConfig;
import org.testeditor.metadata.core.MetaDataService;
import org.testeditor.metadata.core.MetaDataServiceSimpleImpl;
import org.testeditor.metadata.core.model.MetaData;
import org.testeditor.metadata.core.model.MetaDataTag;
import org.testeditor.metadata.core.model.MetaDataValue;

/**
 * Tests the metadataservice.
 * 
 * @author Georg Portwich
 *
 */
public class MetaDataServiceTest {

	private List<String> projects = new ArrayList<String>();

	@Before
	public void setup() throws Exception {
		projects.add("testProject1");
		projects.add("testProject2");
		projects.add("testProject3");
	}

	/**
	 * Checks that the metadata are read correctly.
	 */
	@Test
	public void testReadMetaDataForProject() {
		TestProject project = createProject("testProject1");
		List<MetaData> metaDataList = getNewServiceObject().getAllMetaData(project);

		assertTrue("" + metaDataList.size() + " Metadata was read. Expected was 3", metaDataList.size() == 3);

		for (MetaData metaData : metaDataList) {
			if ("MetaData1".equals(metaData.getKey())) {
				assertTrue("MetaData1 with wrong label " + metaData.getLabel(),
						"MetaData1Label".equals(metaData.getLabel()));

				assertTrue("MetaData1 with " + metaData.getValues().size() + ". 2 was expected", metaData.getValues()
						.size() == 2);
				for (MetaDataValue metaDataValue : metaData.getValues()) {
					if ("MetaData1".equals(metaDataValue.getKey())) {
						assertTrue("MetaDataValue1 with wrong label " + metaDataValue.getLabel(),
								"MetaData1-1Label".equals(metaDataValue.getLabel()));
					} else if ("MetaData2".equals(metaDataValue.getKey())) {
						assertTrue("MetaDataValue2 with wrong label " + metaDataValue.getLabel(),
								"MetaData1-2Label".equals(metaDataValue.getLabel()));
						assertTrue("wrong globalKey " + metaDataValue.getGlobalKey()
								+ " expected was MetaData1-MetaData2",
								"MetaData1-MetaData2".equals(metaDataValue.getGlobalKey()));
					} else {
						assertTrue("Illegal MetadataValuekey " + metaDataValue.getKey(), false);
					}
					assertTrue("wrong parentKey " + metaDataValue.getMetaData().getKey() + " expected was MetaData1",
							"MetaData1".equals(metaDataValue.getMetaData().getKey()));
				}
			} else if ("MetaData2".equals(metaData.getKey())) {
				assertTrue("MetaData2 with wrong label " + metaData.getLabel(),
						"MetaData2Label".equals(metaData.getLabel()));

				assertTrue("MetaData1 with " + metaData.getValues().size() + ". 3 was expected", metaData.getValues()
						.size() == 3);
			} else if ("MetaData3".equals(metaData.getKey())) {
				assertTrue("MetaData3 with wrong label " + metaData.getLabel(),
						"MetaData3Label".equals(metaData.getLabel()));
			} else {
				assertTrue("Illegal Metadatakey " + metaData.getKey(), false);
			}
		}
	}

	/**
	 * Creates the reading of the metadata for an other project
	 */
	@Test
	public void testReadMetaDataForOtherProject() {
		TestProject project = createProject("testProject2");
		List<MetaData> metaDataList = getNewServiceObject().getAllMetaData(project);

		assertTrue("" + metaDataList.size() + " Metadata was read. Expected was 2", metaDataList.size() == 2);
	}

	/**
	 * Tests the start of the metaDataService if no metaData are created for the
	 * project
	 */
	@Test
	public void testReadMetaDataForEmptyProject() {
		TestProject project = createProject("testProject3");
		List<MetaData> metaDataList = getNewServiceObject().getAllMetaData(project);

		assertTrue("" + metaDataList.size() + " Metadata was read. Expected was 0", metaDataList.size() == 0);
	}

	/**
	 * Tests the storing and reading of the data of multiple testcases
	 */
	@Test
	public void testStoreAndReadMulitpleMetaDataList() {
		TestProject project = createProject("testProject1");
		setupMetaDatatestCaseStore(project);
		for (int index = 1; index <= 3; index++) {
			TestCase testCase = new TestCase();
			testCase.setName("testCase" + index);
			project.addChild(testCase);
			List<MetaDataTag> metaDataTagListResult = getNewServiceObject().getMetaDataTags(testCase);
			assertTrue("" + metaDataTagListResult.size() + " found for Testcase. Expected was " + index,
					metaDataTagListResult.size() == index);
		}

	}

	/*
	 * Tests the removing of metadata - this is used during deleting a testcase
	 */
	@Test
	public void testDeleteTestStructure() {
		TestProject project = createProject("testProject1");
		setupMetaDatatestCaseStore(project);

		TestCase testCase = new TestCase();
		testCase.setName("testCase3");
		project.addChild(testCase);
		assertTrue("Testcase not setup. Entries " + getNewServiceObject().getMetaDataTags(testCase).size()
				+ " instead of 3", getNewServiceObject().getMetaDataTags(testCase).size() == 3);

		getNewServiceObject().delete(testCase);

		assertTrue(
				"Testcase not deleted. Still " + getNewServiceObject().getMetaDataTags(testCase).size() + " entries",
				getNewServiceObject().getMetaDataTags(testCase).size() == 0);
	}

	/**
	 * Tests that changes the name of a testcase.
	 */
	@Test
	public void testRenameTestStructure() {
		TestProject project = createProject("testProject1");
		setupMetaDatatestCaseStore(project);

		TestCase testCase = new TestCase();
		testCase.setName("testCase3");
		project.addChild(testCase);
		assertTrue("Testcase not setup. Entries " + getNewServiceObject().getMetaDataTags(testCase).size()
				+ " instead of 3", getNewServiceObject().getMetaDataTags(testCase).size() == 3);

		getNewServiceObject().rename(testCase, "testCase4");

		assertTrue("Testcase not renamed. Old Entry has still "
				+ getNewServiceObject().getMetaDataTags(testCase).size() + " entries", getNewServiceObject()
				.getMetaDataTags(testCase).size() == 0);

		testCase.setName("testCase4");
		assertTrue("Testcase not deleted. New entry has " + getNewServiceObject().getMetaDataTags(testCase).size()
				+ " instead of 3", getNewServiceObject().getMetaDataTags(testCase).size() == 3);
	}

	@Test
	public void testGetMetaDataFromMetaDataTag() {
		TestProject project = createProject("testProject1");
		setupMetaDatatestCaseStore(project);

		TestCase testCase = new TestCase();
		testCase.setName("testCase3");
		project.addChild(testCase);

		List<MetaDataTag> metaDataTagListResult = getNewServiceObject().getMetaDataTags(testCase);

		for (MetaDataTag metaDataTag : metaDataTagListResult) {
			MetaDataValue metaDataValue = getNewServiceObject().getMetaDataValue(metaDataTag, project);
			assertTrue(metaDataTag.getGlobalKey() + " not found in MetaDataService", metaDataValue.getGlobalKey()
					.equals(metaDataTag.getGlobalKey()));
		}

	}

	@Test
	public void testGetTestCasesForMetaData() {
		TestProject project = createProject("testProject1");
		setupMetaDatatestCaseStore(project);
		MetaDataService metaDataService = getNewServiceObject();

		for (int index = 1; index <= 3; index++) {
			MetaDataValue metaDataValue = metaDataService.getAllMetaData(project).get(3 - index).getValues()
					.get(3 - index);
			List<String> testCases = metaDataService.getTestCases(project, metaDataValue);
			assertTrue("no testcases found for " + metaDataValue, testCases.size() == index);
		}
	}

	/**
	 * Tests the search of testcases for a metadatavalue
	 */
	@Test
	public void testGetTestCasesForMetaDataList() {
		TestProject project = createProject("testProject1");
		setupMetaDatatestCaseStore(project);
		MetaDataService metaDataService = getNewServiceObject();

		List<MetaDataValue> metaDataValueList = new ArrayList<MetaDataValue>();
		for (int index = 0; index < 3; index++) {
			MetaDataValue metaDataValue = metaDataService.getAllMetaData(project).get(index).getValues().get(index);
			metaDataValueList.add(metaDataValue);
			List<String> testCases = metaDataService.getTestCases(project, metaDataValueList);
			assertTrue("" + testCases.size() + " testcaes found. Expected was " + (3 - index),
					testCases.size() == (3 - index));
		}
	}

	/**
	 * Tests the storing and reading of a metadatalist
	 */
	@Test
	public void testStoreAndReadMetaDataList() {

		TestProject project = createProject("testProject1");
		TestCase testCase = new TestCase();
		testCase.setName("testCase1");
		List<MetaDataTag> metaDataTagList = getMetTagList(testCase, project, 2);

		getNewServiceObject().storeMetaDataTags(metaDataTagList, testCase);

		List<MetaDataTag> metaDataTagListResult = getNewServiceObject().getMetaDataTags(testCase);
		assertTrue("" + metaDataTagListResult.size() + " found for Testcase. Expected was 2",
				metaDataTagListResult.size() == 2);

		assertTrue(metaDataTagList.get(0).getGlobalKey() + " not found in stored data", metaDataTagList.get(0)
				.getGlobalKey().equals(metaDataTagListResult.get(0).getGlobalKey()));
		assertTrue(metaDataTagList.get(1).getGlobalKey() + " not found in stored data", metaDataTagList.get(1)
				.getGlobalKey().equals(metaDataTagListResult.get(1).getGlobalKey()));
	}

	/**
	 * Creates a store containing three testcases with different metatags for
	 * project
	 * 
	 * @param project
	 */
	private void setupMetaDatatestCaseStore(TestProject project) {
		for (int index = 1; index <= 3; index++) {
			TestCase testCase = new TestCase();
			testCase.setName("testCase" + index);
			getNewServiceObject().storeMetaDataTags(getMetTagList(testCase, project, index), testCase);
		}
	}

	/**
	 * Creates a list of metatags for a testcase with count number of elements
	 * 
	 * @param testCase
	 * @param project
	 * @param count
	 * @return
	 */
	private List<MetaDataTag> getMetTagList(TestCase testCase, TestProject project, int count) {
		List<MetaDataTag> metaDataTagList = new ArrayList<MetaDataTag>();

		project.addChild(testCase);
		for (int index = 0; index < count; index++) {
			MetaDataTag metaDataTag = new MetaDataTag(getNewServiceObject().getAllMetaData(project).get(index)
					.getValues().get(index));
			metaDataTagList.add(metaDataTag);
		}

		return metaDataTagList;
	}

	/**
	 * Gets the path to the testprojects - the testprojects are stored in the
	 * resources directors of the testproject
	 * 
	 * @param projectName
	 * @return
	 */
	private String getProjectPath(String projectName) {
		URL url = this.getClass().getClassLoader().getResource(projectName);
		assertTrue("Project " + projectName + " not found in classpath of test", url != null);

		return url.toString().substring("file:/".length());

	}

	/**
	 * Creates an object of type TestProject whith the given name.
	 * 
	 * @param projectName
	 * @return
	 */
	private TestProject createProject(String projectName) {
		TestProjectConfig testProjectConfig = new TestProjectConfig();

		testProjectConfig.setProjectPath(getProjectPath(projectName));

		TestProject project = new TestProject();
		project.setName(projectName);
		project.setTestProjectConfig(testProjectConfig);
		return project;
	}

	/**
	 * creates a new object of the serviceobject. This garanties that all data
	 * have to be read from the disk and no data is read from the internal state
	 * of the service
	 * 
	 * @return
	 */
	private MetaDataService getNewServiceObject() {
		return new MetaDataServiceSimpleImpl();
	}

	/**
	 * removes all Files containing testprojectdata after the test.
	 */
	@After
	public void removeCreatedMetaDataFiles() {
		for (String project : projects) {
			File file = new File(getProjectPath(project) + File.separator + "MetaData.xml");
			if (file.exists()) {
				file.delete();
			}
		}
	}

}
