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

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
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
import org.testeditor.metadata.core.model.MetaData;
import org.testeditor.metadata.core.model.MetaDataValue;
import org.testeditor.metadata.core.model.MetaData;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class MetaDataServiceTest {

	TestProject project;
	MetaDataStore testStore;
	TestCase testCase;
	MetaData testMetaDataTags;

	@Before
	public void setup() throws Exception {

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

	private void createMetaDataStore() throws Exception {

		testStore = new MetaDataStore();
		testStore.setProject(project);

		MetaData testList = new MetaData();
		List<MetaDataValue> testValues = new ArrayList<>();
		MetaDataValue value = new MetaDataValue();
		value.setId(1);
		value.setKey("2");
		value.setValue("Antrag speichern.");
		value.setParent(testList);
		testValues.add(value);
		value = new MetaDataValue();
		value.setId(2);
		value.setKey("3");
		value.setValue("Antrag bearbeiten.");
		value.setParent(testList);
		testValues.add(value);
		value = new MetaDataValue();
		value.setId(3);
		value.setKey("4");
		value.setValue("Antrag ablehnen.");
		value.setParent(testList);
		testValues.add(value);

		testList.setName("Gevo");
		testList.setDescription("Geschäftsvorfälle");
		testList.setMetaDataValues(testValues);

		MetaData testList2 = new MetaData();
		List<MetaDataValue> testValues2 = new ArrayList<>();
		MetaDataValue value2 = new MetaDataValue();
		value2.setId(4);
		value2.setKey("R1.1");
		value2.setValue("Release 1.1");
		value2.setParent(testList2);
		testValues2.add(value2);

		testList2.setName("Release");
		testList2.setDescription("Release 1.1");
		testList2.setMetaDataValues(testValues2);

		testStore.getList().add(testList);
		testStore.getList().add(testList2);
		testStore.setId(5);

		File root = new File(project.getTestProjectConfig().getProjectPath(), "metaData");
		if (!root.exists()) {
			root.mkdir();
		}
		File testMetaDataFile = new File(root, "metadata.xml");
		XStream xStream = new XStream(new DomDriver("UTF-8"));
		xStream.alias("metaDataStore", MetaDataStore.class);
		xStream.alias("metaDataTagList", MetaData.class);
		xStream.alias("metaDataValueList", MetaData.class);
		xStream.alias("metaDataValue", MetaDataValue.class);
		xStream.alias("metaDataTag", MetaDataTag.class);
		xStream.autodetectAnnotations(true);

		try {
			xStream.toXML(testStore, new FileWriter(testMetaDataFile));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private MetaDataService getService() {
		IEclipseContext context = EclipseContextFactory.create();
		// context.set(LibraryDataStoreService.class,
		// ServiceLookUpForTest.getService(LibraryDataStoreService.class));
		return ContextInjectionFactory.make(MetaDataServiceImpl.class, context);
	}

	private void createMetaDataTagList() throws Exception {
		testMetaDataTags = new MetaData();
		testMetaDataTags.setTestcase(testCase);

		MetaDataTag testMetaDataTag = new MetaDataTag();
		MetaDataValue value = new MetaDataValue();
		value.setId(1);
		value.setKey("2");
		value.setValue("Antrag speichern.");
		value.setParent(testStore.getList().get(0));
		testMetaDataTag.setMetaDataValue(value);

		MetaDataTag testMetaDataTag2 = new MetaDataTag();
		MetaDataValue value2 = new MetaDataValue();
		value2.setId(2);
		value2.setKey("3");
		value2.setValue("Antrag bearbeiten.");
		value2.setParent(testStore.getList().get(0));
		testMetaDataTag2.setMetaDataValue(value2);

		testMetaDataTags.getValues().add(testMetaDataTag);
		testMetaDataTags.getValues().add(testMetaDataTag2);

		File root = new File(project.getTestProjectConfig().getProjectPath(), "metaData");
		if (!root.exists()) {
			root.mkdir();
		}
		File testTestCaseFile = new File(root, testCase.getFullName() + ".xml");
		XStream xStream = new XStream(new DomDriver("UTF-8"));
		xStream.alias("metaDataStore", MetaDataStore.class);
		xStream.alias("metaDataTagList", MetaData.class);
		xStream.alias("metaDataValueList", MetaData.class);
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
	public void testSaveCreateNewValue() throws Exception {
		MetaDataService service = getService();
		// TODO: project = null
		MetaDataStore store = service.getMetaDataStore(project);
		store.setProject(project);
		MetaData toBeSavedMetaDataList = new MetaData();
		List<MetaDataValue> newMetaDataValues = new ArrayList<>();
		MetaDataValue newValue = store.createNewValue("1", "Hallo", store.getList().get(0));
		newMetaDataValues.add(newValue);
		toBeSavedMetaDataList.setDescription("TEST");
		toBeSavedMetaDataList.setMetaDataValues(newMetaDataValues);
		toBeSavedMetaDataList.setName("Hallo");

		store.getList().add(toBeSavedMetaDataList);
		service.saveLists(store);

	}

	// @Test
	// public void testCreateDuplicateValue() {
	//
	// }

	@Test
	public void testGetAllMetaDataList() throws IOException {
		MetaDataService service = getService();
		MetaDataStore storeFromXml = service.getMetaDataStore(project);

		assertEquals(testStore.getId(), storeFromXml.getId());
		assertEquals(testStore.getList().size(), storeFromXml.getList().size());
		assertTrue(testStore.getList().containsAll(storeFromXml.getList()));
	}

	@Test
	public void testSaveAddMetaDataList() throws Exception {

		MetaDataService service = getService();

		MetaData toBeSavedMetaDataList = new MetaData();
		List<MetaDataValue> newMetaDataValues = new ArrayList<>();
		MetaDataValue value = new MetaDataValue();
		value.setId(5);
		value.setKey("TEST key");
		value.setValue("TEST value");
		value.setParent(toBeSavedMetaDataList);
		newMetaDataValues.add(value);
		toBeSavedMetaDataList.setName("TEST name");
		toBeSavedMetaDataList.setDescription("TEST description");
		toBeSavedMetaDataList.setMetaDataValues(newMetaDataValues);

		// TODO: Auslagern
		MetaDataStore toBeUpdatedstore = service.getMetaDataStore(project);
		List<MetaData> toBeUpdatedList = toBeUpdatedstore.getList();

		for (MetaData metaDataValueList : toBeUpdatedList) {
			if (toBeSavedMetaDataList.getName().equals(metaDataValueList.getName())) {
				throw new Exception("Eine MetaDataList mit dem ShortName '" + toBeSavedMetaDataList.getName()
						+ "' ist bereits vorhanden.");
			}
		}

		toBeUpdatedstore.getList().add(toBeSavedMetaDataList);
		service.saveLists(toBeUpdatedstore);

		MetaDataStore updatedStore = service.getMetaDataStore(toBeUpdatedstore.getProject());
		List<MetaData> updatedListWithAddedElement = updatedStore.getList();
		assertEquals(updatedListWithAddedElement.size(), toBeUpdatedstore.getList().size());
		assertTrue(updatedListWithAddedElement.contains(toBeSavedMetaDataList));

	}

	@Test
	public void testSaveRemoveMetaDataList() throws Exception {

		MetaData toBeSavedMetaDataList = new MetaData();
		List<MetaDataValue> newMetaDataValues = new ArrayList<>();
		MetaDataValue value = new MetaDataValue();
		value.setId(5);
		value.setKey("TEST key");
		value.setValue("TEST value");
		value.setParent(toBeSavedMetaDataList);
		newMetaDataValues.add(value);
		toBeSavedMetaDataList.setName("TEST name");
		toBeSavedMetaDataList.setDescription("TEST description");
		toBeSavedMetaDataList.setMetaDataValues(newMetaDataValues);

		MetaDataService service = new MetaDataServiceImpl();
		MetaDataStore toBeUpdatedstore = service.getMetaDataStore(project);

		toBeUpdatedstore.getList().remove(toBeSavedMetaDataList);
		service.saveLists(toBeUpdatedstore);

		MetaDataStore updatedStore = service.getMetaDataStore(toBeUpdatedstore.getProject());
		List<MetaData> updatedListWithAddedElement = updatedStore.getList();
		assertEquals(updatedListWithAddedElement.size(), toBeUpdatedstore.getList().size());
		assertFalse(updatedListWithAddedElement.contains(toBeSavedMetaDataList));

	}

	@Test
	// TODO?
	public void testSaveListWithExistingElement() throws Exception {
		testMetaDataTags = new MetaData();
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
		MetaData metaDataTagListFromXml = service.getTags(testCase);

		assertEquals(testMetaDataTags.getValues().size(), metaDataTagListFromXml.getValues().size());
		assertTrue(testMetaDataTags.getValues().containsAll(metaDataTagListFromXml.getValues()));
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

		MetaData toBeUpdatedList = service.getTags(testCase);
		toBeUpdatedList.setTestcase(testCase);
		List<MetaDataTag> toBeUpdatedTags = toBeUpdatedList.getValues();

		// TODO: Auslagern
		for (MetaDataTag metaDataTag : toBeUpdatedTags) {
			if (toBeSavedMetaDataTag.getMetaDataValue().getValue().equals(metaDataTag.getMetaDataValue().getValue())) {
				throw new Exception("Es existiert bereits ein MetaDataTag mit folgenden MetaDataValues: '"
						+ toBeSavedMetaDataTag.getMetaDataValue().getKey() + "' & '"
						+ toBeSavedMetaDataTag.getMetaDataValue().getValue() + "'.");
			}
		}

		toBeUpdatedList.getValues().addAll(toBeSavedMetaDataTags);
		service.saveTags(toBeUpdatedList);

		MetaData updatedListWithAddedTags = service.getTags(testCase);
		assertEquals(toBeUpdatedList.getValues().size(), updatedListWithAddedTags.getValues().size());
		assertTrue(updatedListWithAddedTags.getValues().containsAll(toBeUpdatedList.getValues()));

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

		MetaData toBeUpdatedList = service.getTags(testCase);
		toBeUpdatedList.setTestcase(testCase);
		toBeUpdatedList.getValues().remove(toBeSavedMetaDataTags);
		service.saveTags(toBeUpdatedList);

		MetaData updatedListWithAddedTags = service.getTags(testCase);
		assertEquals(toBeUpdatedList.getValues().size(), updatedListWithAddedTags.getValues().size());
		assertFalse(updatedListWithAddedTags.getValues().containsAll(toBeSavedMetaDataTags));
	}

	// TODO: Funktioniert nicht (mehr) - MetaDataOrdner und Technical Bindings
	// löschen...
	// @After
	// public void deleteProject() {
	//
	// TestProjectService projectService = (TestProjectService)
	// ServiceLookUpForTest
	// .getService(TestProjectService.class);
	// try {
	// projectService.deleteProject(project);
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// }

}
