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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testeditor.core.model.teststructure.TestCase;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestSuite;
import org.testeditor.core.services.interfaces.ServiceLookUpForTest;
import org.testeditor.core.services.interfaces.TestProjectService;
import org.testeditor.metadata.core.MetaDataService;
import org.testeditor.metadata.core.MetaDataServiceImpl;
import org.testeditor.metadata.core.model.MetaDataStore;
import org.testeditor.metadata.core.model.MetaDataTag;
import org.testeditor.metadata.core.model.MetaDataTagList;
import org.testeditor.metadata.core.model.MetaDataValue;
import org.testeditor.metadata.core.model.MetaDataValueList;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class MetaDataServiceTest {

	TestProject project;
	MetaDataStore testStore;
	TestCase testCase;
	MetaDataTagList testMetaDataTags;

	@Before
	public void setup() {

		if (project == null) {
			project = createProject();

			createMetaDataStore();
			createMetaDataTagList();
		}

	}

	private TestProject createProject() {
		TestProjectService projectService = (TestProjectService) ServiceLookUpForTest
				.getService(TestProjectService.class);
		TestProject project;
		project = projectService.getProjectWithName("testproject");
		if (project == null) {
			try {
				project = projectService.createNewProject("testproject");
				TestSuite suite = new TestSuite();
				suite.setName("testsuite");
				project.addChild(suite);
				testCase = new TestCase();
				testCase.setName("fall");
				suite.addChild(testCase);
			} catch (Exception e) {
				e.printStackTrace();
				fail(e.getMessage());
			}
		} else {
			testCase = (TestCase) project.getTestChildByName("fall");
		}
		return project;
	}

	private void createMetaDataStore() {

		testStore = new MetaDataStore();
		testStore.setProject(project);

		MetaDataValueList testList = new MetaDataValueList();
		List<MetaDataValue> testValues = new ArrayList<>();
		MetaDataValue value = new MetaDataValue();
		value.setKey("2");
		value.setValue("Antrag speichern.");
		testValues.add(value);
		value = new MetaDataValue();
		value.setKey("3");
		value.setValue("Antrag bearbeiten.");
		testValues.add(value);
		value = new MetaDataValue();
		value.setKey("4");
		value.setValue("Antrag ablehnen.");
		testValues.add(value);

		testList.setName("Gevo");
		testList.setDescription("Geschäftsvorfälle");
		testList.setMetaDataValues(testValues);

		MetaDataValueList testList2 = new MetaDataValueList();
		List<MetaDataValue> testValues2 = new ArrayList<>();
		MetaDataValue value2 = new MetaDataValue();
		value2.setKey("1");
		value2.setValue("Release 2.1");
		testValues2.add(value2);

		testList2.setDescription("Release 1.2");
		testList2.setName("R1.2");
		testList2.setMetaDataValues(testValues2);

		testStore.getList().add(testList);
		testStore.getList().add(testList2);

		File root = new File(project.getTestProjectConfig().getProjectPath(), "metaData");
		if (!root.exists()) {
			root.mkdir();
		}
		File testMetaDataFile = new File(root, "metadata.xml");
		XStream xStream = new XStream(new DomDriver("UTF-8"));
		xStream.alias("metaDataStore", MetaDataStore.class);
		xStream.alias("metaDataTagList", MetaDataTagList.class);
		xStream.alias("metaDataValueList", MetaDataValueList.class);
		xStream.alias("metaDataValue", MetaDataValue.class);
		xStream.alias("metaDataTag", MetaDataTag.class);
		xStream.autodetectAnnotations(true);

		try {
			xStream.toXML(testStore, new FileWriter(testMetaDataFile));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void createMetaDataTagList() {
		testMetaDataTags = new MetaDataTagList();
		testMetaDataTags.setTestcase(testCase);

		MetaDataTag testMetaDataTag = new MetaDataTag();
		MetaDataValue value = new MetaDataValue();
		value.setKey("2");
		value.setValue("Antrag speichern.");
		testMetaDataTag.setMetaDataValue(value);
		MetaDataTag testMetaDataTag2 = new MetaDataTag();
		MetaDataValue value2 = new MetaDataValue();
		value2.setKey("3");
		value2.setValue("Antrag bearbeiten.");
		testMetaDataTag2.setMetaDataValue(value2);

		testMetaDataTags.getTags().add(testMetaDataTag);
		testMetaDataTags.getTags().add(testMetaDataTag2);

		File root = new File(project.getTestProjectConfig().getProjectPath(), "metaData");
		if (!root.exists()) {
			root.mkdir();
		}
		File testTestCaseFile = new File(root, testCase.getFullName() + ".xml");
		XStream xStream = new XStream(new DomDriver("UTF-8"));
		xStream.alias("metaDataStore", MetaDataStore.class);
		xStream.alias("metaDataTagList", MetaDataTagList.class);
		xStream.alias("metaDataValueList", MetaDataValueList.class);
		xStream.alias("metaDataValue", MetaDataValue.class);
		xStream.alias("metaDataTag", MetaDataTag.class);
		xStream.autodetectAnnotations(true);

		try {
			xStream.toXML(testMetaDataTags, new FileWriter(testTestCaseFile));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Test
	public void testGetAllMetaDataList() throws IOException {

		MetaDataService service = new MetaDataServiceImpl();
		MetaDataStore storeFromXml = service.getAllMetaDataLists(project);

		assertEquals(testStore.getList().size(), storeFromXml.getList().size());
		assertTrue(testStore.getList().containsAll(storeFromXml.getList()));

	}

	@Test
	public void testSaveAddMetaDataList() throws Exception {

		MetaDataService service = new MetaDataServiceImpl();

		MetaDataValueList toBeSavedMetaDataList = new MetaDataValueList();
		List<MetaDataValue> newMetaDataValues = new ArrayList<>();
		MetaDataValue value = new MetaDataValue();
		value.setKey("TEST key");
		value.setValue("TEST value");
		newMetaDataValues.add(value);
		toBeSavedMetaDataList.setName("TEST name");
		toBeSavedMetaDataList.setDescription("TEST description");
		toBeSavedMetaDataList.setMetaDataValues(newMetaDataValues);

		// TODO: Auslagern
		MetaDataStore toBeUpdatedstore = service.getAllMetaDataLists(project);
		List<MetaDataValueList> toBeUpdatedList = toBeUpdatedstore.getList();

		for (MetaDataValueList metaDataValueList : toBeUpdatedList) {
			if (toBeSavedMetaDataList.getName().equals(metaDataValueList.getName())) {
				throw new Exception("Eine MetaDataList mit dem ShortName '" + toBeSavedMetaDataList.getName()
						+ "' ist bereits vorhanden.");
			}
		}

		toBeUpdatedstore.getList().add(toBeSavedMetaDataList);
		service.saveLists(toBeUpdatedstore);

		MetaDataStore updatedStore = service.getAllMetaDataLists(toBeUpdatedstore.getProject());
		List<MetaDataValueList> updatedListWithAddedElement = updatedStore.getList();
		assertEquals(updatedListWithAddedElement.size(), toBeUpdatedstore.getList().size());
		assertTrue(updatedListWithAddedElement.contains(toBeSavedMetaDataList));

	}

	@Test
	public void testSaveRemoveMetaDataList() throws Exception {

		MetaDataValueList toBeSavedMetaDataList = new MetaDataValueList();
		List<MetaDataValue> newMetaDataValues = new ArrayList<>();
		MetaDataValue value = new MetaDataValue();
		value.setKey("TEST key");
		value.setValue("TEST value");
		newMetaDataValues.add(value);
		toBeSavedMetaDataList.setName("TEST name");
		toBeSavedMetaDataList.setDescription("TEST description");
		toBeSavedMetaDataList.setMetaDataValues(newMetaDataValues);

		MetaDataService service = new MetaDataServiceImpl();
		MetaDataStore toBeUpdatedstore = service.getAllMetaDataLists(project);

		toBeUpdatedstore.getList().remove(toBeSavedMetaDataList);
		service.saveLists(toBeUpdatedstore);

		MetaDataStore updatedStore = service.getAllMetaDataLists(toBeUpdatedstore.getProject());
		List<MetaDataValueList> updatedListWithAddedElement = updatedStore.getList();
		assertEquals(updatedListWithAddedElement.size(), toBeUpdatedstore.getList().size());
		assertFalse(updatedListWithAddedElement.contains(toBeSavedMetaDataList));

	}

	@Test
	public void testSaveListWithExistingElement() {
		testMetaDataTags = new MetaDataTagList();
		testMetaDataTags.setTestcase(testCase);

		MetaDataTag metaDataTag = new MetaDataTag();
		MetaDataValue value = new MetaDataValue();
		value.setKey("2");
		value.setValue("Antrag speichern.");
		metaDataTag.setMetaDataValue(value);
	}

	@Test
	public void testGetMetaDataTags() {

		MetaDataService service = new MetaDataServiceImpl();
		MetaDataTagList metaDataTagListFromXml = service.getTags(testCase);

		assertEquals(testMetaDataTags.getTags().size(), metaDataTagListFromXml.getTags().size());
		assertTrue(testMetaDataTags.getTags().containsAll(metaDataTagListFromXml.getTags()));
	}

	/*
	 * TODO
	 * 
	 * @Test public void testGetEmptyMetaDataTagList() throws IOException,
	 * SystemException { TestProjectService projectService =
	 * (TestProjectService) ServiceLookUpForTest
	 * .getService(TestProjectService.class); TestProject testProject =
	 * projectService.createNewProject("projectWithoutFile");
	 * org.testeditor.core.model.teststructure.TestSuite testSuite = new
	 * org.testeditor.core.model.teststructure.TestSuite();
	 * testSuite.setName("testSuiteWithoutFile");
	 * testProject.addChild(testSuite); TestCase testCaseWithoutFile = new
	 * TestCase(); testCaseWithoutFile.setName("testCaseWithoutFile");
	 * testSuite.addChild(testCase);
	 * 
	 * MetaDataService service = new MetaDataServiceImpl(); MetaDataTagList
	 * metaDataTagListFromXml = service.getTags(testCaseWithoutFile);
	 * metaDataTagListFromXml.setTestcase(testCaseWithoutFile);
	 * 
	 * assertTrue(metaDataTagListFromXml.getTags().isEmpty());
	 * 
	 * }
	 */

	@Test
	public void testSaveAddMetaDataTag() throws Exception {

		MetaDataService service = new MetaDataServiceImpl();

		List<MetaDataTag> toBeSavedMetaDataTags = new ArrayList<>();
		MetaDataTag toBeSavedMetaDataTag = new MetaDataTag();
		MetaDataValue newValue = new MetaDataValue();
		newValue.setKey("new key");
		newValue.setValue("new value");
		toBeSavedMetaDataTag.setMetaDataValue(newValue);
		toBeSavedMetaDataTags.add(toBeSavedMetaDataTag);

		MetaDataTagList toBeUpdatedList = service.getTags(testCase);
		toBeUpdatedList.setTestcase(testCase);
		List<MetaDataTag> toBeUpdatedTags = toBeUpdatedList.getTags();

		// TODO: Auslagern
		for (MetaDataTag metaDataTag : toBeUpdatedTags) {
			if (toBeSavedMetaDataTag.getMetaDataValue().getValue().equals(metaDataTag.getMetaDataValue().getValue())) {
				throw new Exception("Es existiert bereits ein MetaDataTag mit folgenden MetaDataValues: '"
						+ toBeSavedMetaDataTag.getMetaDataValue().getKey() + "' & '"
						+ toBeSavedMetaDataTag.getMetaDataValue().getValue() + "'.");
			}
		}

		toBeUpdatedList.getTags().addAll(toBeSavedMetaDataTags);
		service.saveTags(toBeUpdatedList);

		MetaDataTagList updatedListWithAddedTags = service.getTags(testCase);
		assertEquals(toBeUpdatedList.getTags().size(), updatedListWithAddedTags.getTags().size());
		assertTrue(updatedListWithAddedTags.getTags().containsAll(toBeUpdatedList.getTags()));

	}

	@Test
	public void testSaveRemoveMetaDataTag() throws Exception {

		MetaDataService service = new MetaDataServiceImpl();

		List<MetaDataTag> toBeSavedMetaDataTags = new ArrayList<>();
		MetaDataTag toBeSavedMetaDataTag = new MetaDataTag();
		MetaDataValue newValue = new MetaDataValue();
		newValue.setKey("new key");
		newValue.setValue("new value");
		toBeSavedMetaDataTag.setMetaDataValue(newValue);
		toBeSavedMetaDataTags.add(toBeSavedMetaDataTag);

		MetaDataTagList toBeUpdatedList = service.getTags(testCase);
		toBeUpdatedList.setTestcase(testCase);
		toBeUpdatedList.getTags().remove(toBeSavedMetaDataTags);
		service.saveTags(toBeUpdatedList);

		MetaDataTagList updatedListWithAddedTags = service.getTags(testCase);
		assertEquals(toBeUpdatedList.getTags().size(), updatedListWithAddedTags.getTags().size());
		assertFalse(updatedListWithAddedTags.getTags().containsAll(toBeSavedMetaDataTags));
	}

	@After
	public void deleteProject() {

		TestProjectService projectService = (TestProjectService) ServiceLookUpForTest
				.getService(TestProjectService.class);
		try {
			projectService.deleteProject(project);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
