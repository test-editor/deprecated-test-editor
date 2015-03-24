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
import org.testeditor.metadata.core.model.MetaDataTagList;
import org.testeditor.metadata.core.model.MetaDataValue;
import org.testeditor.metadata.core.model.MetaDataValueList;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class NewMetaDataServiceTest {

	TestProject project;
	MetaDataStore testStore;
	TestCase testCase;
	MetaDataTagList testMetaDataTags;

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

		MetaDataValueList testList = new MetaDataValueList();
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

		MetaDataValueList testList2 = new MetaDataValueList();
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

	private MetaDataService getService() {
		IEclipseContext context = EclipseContextFactory.create();
		// context.set(LibraryDataStoreService.class,
		// ServiceLookUpForTest.getService(LibraryDataStoreService.class));
		return ContextInjectionFactory.make(MetaDataServiceImpl.class, context);
	}

	private void createMetaDataTagList() throws Exception {
		testMetaDataTags = new MetaDataTagList();
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
		MetaDataService service = getService();
		MetaDataStore storeFromXml = service.getMetaDataStore(project);

		assertEquals(testStore.getId(), storeFromXml.getId());
		assertEquals(testStore.getList().size(), storeFromXml.getList().size());
		assertTrue(testStore.getList().containsAll(storeFromXml.getList()));
	}

	// TODO: Folgende Methode und removeValue()
	@Test
	public void testSaveAddNewMetaDataValue() throws Exception {
		MetaDataService service = getService();
		MetaDataStore store = service.getMetaDataStore(project);

		MetaDataValueList toBeSavedMetaDataList = new MetaDataValueList();
		toBeSavedMetaDataList.setName("Gevo");

		List<MetaDataValueList> listFromXML = store.getList();
		for (MetaDataValueList metaDataValueList : listFromXML) {
			if (metaDataValueList.getName().equals(toBeSavedMetaDataList.getName())) {
				MetaDataValue newValue = store.createNewValue("1", "Hallo", metaDataValueList);
				metaDataValueList.getMetaDataValues().add(newValue);
			}
		}

		service.saveLists(store);
		MetaDataStore storeFromXml = service.getMetaDataStore(project);

		assertEquals(storeFromXml.getList().size(), store.getList().size());
		assertEquals(storeFromXml.getId(), store.getId());
	}

	@Test
	public void testSaveAddNewMetaDataValueList() throws Exception {
		MetaDataService service = getService();
		MetaDataStore store = service.getMetaDataStore(project);

		MetaDataValueList toBeSavedMetaDataList = new MetaDataValueList();
		toBeSavedMetaDataList.setDescription("TEST");
		toBeSavedMetaDataList.setName("Hallo");
		List<MetaDataValue> newMetaDataValues = new ArrayList<>();
		MetaDataValue newValue = store.createNewValue("1", "Hallo", toBeSavedMetaDataList);
		newMetaDataValues.add(newValue);
		toBeSavedMetaDataList.setMetaDataValues(newMetaDataValues);

		store.getList().add(toBeSavedMetaDataList);
		service.saveLists(store);

		MetaDataStore storeFromXml = service.getMetaDataStore(project);

		assertEquals(storeFromXml.getList().size(), store.getList().size());
		assertEquals(storeFromXml.getId(), store.getId());
		assertTrue(storeFromXml.getList().contains(toBeSavedMetaDataList));

	}

	@Test
	public void testSaveRemoveMetaDataValueList() throws Exception {
		MetaDataValueList toBeSavedMetaDataList = new MetaDataValueList();
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

		MetaDataService service = getService();
		MetaDataStore toBeUpdatedstore = service.getMetaDataStore(project);
		toBeUpdatedstore.getList().remove(toBeSavedMetaDataList);
		service.saveLists(toBeUpdatedstore);

		MetaDataStore updatedStore = service.getMetaDataStore(toBeUpdatedstore.getProject());
		List<MetaDataValueList> updatedListWithAddedElement = updatedStore.getList();
		assertEquals(updatedListWithAddedElement.size(), toBeUpdatedstore.getList().size());
		assertEquals(updatedStore.getId(), toBeUpdatedstore.getId());
		assertFalse(updatedListWithAddedElement.contains(toBeSavedMetaDataList));
	}

	@Test(expected = RuntimeException.class)
	public void testCreateDuplicateValue() throws Exception {
		MetaDataService service = getService();
		MetaDataStore store = service.getMetaDataStore(project);
		store.setProject(project);

		MetaDataValueList parentList = store.getList().get(2);
		List<MetaDataValue> alreadyExsistingMetaDataValues = new ArrayList<>();
		MetaDataValue alreadyExsistingValue = store.createNewValue("1", "Antrag speichern.", parentList);
		alreadyExsistingMetaDataValues.add(alreadyExsistingValue);

		service.saveLists(store);
	}

	// TODO: public void testCreateValueWithSameId()
	// TODO: Testen, ob Liste bereits vorhanden (name muss eindeutig sein)
	// TODO: Testen, ob Parent mit der angegebenen Liste identisch ist

	@Test
	public void testGetMetaDataTags() {
		// TODO: MetaDataValue wird nicht übergeben aus der xml..
		// Grund: Diese werden nicht in XML erzeugt. Daher mit Hilfe des Keys
		// passend aus dem Store suchen und zurückgeben?
		MetaDataService service = getService();
		MetaDataTagList metaDataTagListFromXml = service.getTags(testCase);

		assertEquals(testMetaDataTags.getTags().size(), metaDataTagListFromXml.getTags().size());
		assertTrue(testMetaDataTags.getTags().containsAll(metaDataTagListFromXml.getTags()));
	}

	// TODO: Add & Remove Tags

	// TODO: Funktioniert nicht (mehr) -> MetaDataOrdner und Technical Bindings
	// löschen..?
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
