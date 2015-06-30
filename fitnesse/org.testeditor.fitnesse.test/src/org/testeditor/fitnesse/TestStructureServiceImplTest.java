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
package org.testeditor.fitnesse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.junit.Test;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.exceptions.TeamAuthentificationException;
import org.testeditor.core.model.team.TeamChange;
import org.testeditor.core.model.team.TeamChangeType;
import org.testeditor.core.model.team.TeamShareConfig;
import org.testeditor.core.model.teststructure.ScenarioSuite;
import org.testeditor.core.model.teststructure.TestCase;
import org.testeditor.core.model.teststructure.TestCompositeStructure;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestProjectConfig;
import org.testeditor.core.model.teststructure.TestScenario;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.model.teststructure.TestSuite;
import org.testeditor.core.services.interfaces.ProgressListener;
import org.testeditor.core.services.interfaces.ServiceLookUpForTest;
import org.testeditor.core.services.interfaces.TestStructureService;
import org.testeditor.core.services.plugins.TeamShareServicePlugIn;
import org.testeditor.core.services.plugins.TestStructureServicePlugIn;

/**
 * 
 * Integration Test for TestStructureServiceImpl.
 * 
 */
public class TestStructureServiceImplTest {

	/**
	 * Tests the registration of the Service.
	 * 
	 * @throws Exception
	 *             for Test.
	 */
	@Test
	public void testServiceRegistration() throws Exception {
		TestStructureService service = ServiceLookUpForTest.getService(TestStructureServicePlugIn.class);
		assertNotNull("Expecting an implementation of the TestStructureService", service);
		assertTrue("Service implementation is not an instance of TestStructureServiceImpl",
				service instanceof TestStructureServiceImpl);
	}

	/**
	 * Test for Magic FitnessePages as reserved Words.
	 * 
	 * @throws Exception
	 *             for test.
	 */
	@Test
	public void testIsReservedName() throws Exception {
		TestStructureService service = new TestStructureServiceImpl();
		assertTrue(service.isReservedName(null, "SetUp"));
		assertTrue(service.isReservedName(null, "PageHeader"));
		assertFalse(service.isReservedName(null, "MyTest"));
	}

	/**
	 * Test the Handling of the Lazy Loader.
	 */
	@Test
	public void testGetLazyLoader() {
		final Set<String> monitor = new HashSet<String>();
		TestStructureService service = new TestStructureServiceImpl() {
			@Override
			public Runnable getTestProjectLazyLoader(TestCompositeStructure toBeLoadedLazy) {
				return new Runnable() {

					@Override
					public void run() {
						monitor.add("seen");
					}
				};
			}

		};
		TestProject project = new TestProject();
		project.setChildCountInBackend(-1);
		project.setLazyLoader(service.getTestProjectLazyLoader(project));
		assertFalse(monitor.contains("seen"));
		project.getTestChildren();
		assertTrue(monitor.contains("seen"));
	}

	/**
	 * Tests the Storage of a old name under the new fullname as key.
	 */
	@Test
	public void testStoreOldNameOnTheFullNewNameAsKey() {
		TestStructureServiceImpl service = new TestStructureServiceImpl();
		TestProject tp = new TestProject();
		tp.setName("TestPrj");
		TestSuite suite = new TestSuite();
		suite.setName("MySuite");
		tp.addChild(suite);
		service.storeOldNameOnTheFullNewNameAsKey(suite, "NewSuite");
		assertTrue(service.getRenamedTestStructures().containsKey("TestPrj.NewSuite"));
		assertEquals("MySuite", service.getRenamedTestStructures().get("TestPrj.NewSuite"));
	}

	/**
	 * Tests the building of a fitnesse url.
	 */
	@Test
	public void testGetFitnesseURL() {
		TestStructureServiceImpl service = new TestStructureServiceImpl();
		TestProject tp = new TestProject();
		TestProjectConfig testProjectConfig = new TestProjectConfig();
		testProjectConfig.setPort("8080");
		tp.setTestProjectConfig(testProjectConfig);
		TestCase testStructure = new TestCase();
		tp.addChild(testStructure);
		assertEquals("http://localhost:8080/", service.getFitnesseUrl(testStructure));
	}

	/**
	 * Test the Creation of an empty History List for non executable
	 * TestStructures.
	 * 
	 * @throws Exception
	 *             on Test failure.
	 */
	@Test
	public void testGetEmptyHistoryOnNotExecutableTestStructures() throws Exception {
		TestStructureServiceImpl service = new TestStructureServiceImpl();
		assertTrue(service.getTestHistory(new TestProject()).isEmpty());
		assertTrue(service.getTestHistory(new TestScenario()).isEmpty());
		assertTrue(service.getTestHistory(new ScenarioSuite()).isEmpty());
	}

	/**
	 * Tests that the wire up works and a TeamService is used for delete test
	 * structure.
	 * 
	 * @throws Exception
	 *             on test failure.
	 */
	@Test
	public void testDeleteWithTeamService() throws Exception {
		TestStructureServiceImpl service = new TestStructureServiceImpl();
		TestStructure testStructure = getTeamSharedTestStructure(service);
		service.delete(testStructure);
	}

	/**
	 * Test the rename of an Teststructure is delegated to the team service.
	 * 
	 * @throws Exception
	 *             on Testfailue
	 */
	@Test
	public void testRenameWithTeamService() throws Exception {
		TestStructureServiceImpl service = new TestStructureServiceImpl();
		TestStructure testStructure = getTeamSharedTestStructure(service);
		service.rename(testStructure, "foo");
		assertTrue(testStructure.getTeamChangeType().equals(TeamChangeType.MOVED));
	}

	/**
	 * 
	 * @param service
	 *            used to inject the team service mock.
	 * @return TestStruture in team shared Project.
	 */
	private TestStructure getTeamSharedTestStructure(TestStructureServiceImpl service) {
		IEclipseContext context = EclipseContextFactory.create();
		service.compute(context, null);
		HashSet<String> set = new HashSet<String>();
		TeamShareServicePlugIn serviceMock = getTeamShareServiceMock(set);
		service.bind(serviceMock);
		TestProject tp = new TestProject();
		TestProjectConfig testProjectConfig = new TestProjectConfig();
		TeamShareConfig aTeamShareConfig = new TeamShareConfig() {

			@Override
			public String getId() {
				return "myDummy";
			}
		};
		testProjectConfig.setTeamShareConfig(aTeamShareConfig);
		tp.setTestProjectConfig(testProjectConfig);
		TestStructure testStructure = new TestCase();
		tp.addChild(testStructure);
		return testStructure;
	}

	/**
	 * 
	 * @param set
	 *            used to inform the test about operations calls.
	 * @return team share service mock.
	 */
	private TeamShareServicePlugIn getTeamShareServiceMock(final HashSet<String> set) {
		return new TeamShareServicePlugIn() {

			@Override
			public void disconnect(TestProject testProject, TranslationService translationService)
					throws SystemException {
			}

			@Override
			public void share(TestProject testProject, TranslationService translationService, String svnComment)
					throws SystemException {
			}

			@Override
			public String approve(TestStructure testStructure, TranslationService translationService, String svnComment)
					throws SystemException {
				return "";
			}

			@Override
			public String update(TestStructure testStructure, TranslationService translationService)
					throws SystemException {
				return "";
			}

			@Override
			public void checkout(TestProject testProject, TranslationService translationService)
					throws SystemException, TeamAuthentificationException {

			}

			@Override
			public String getId() {
				return "myDummy";
			}

			@Override
			public void delete(TestStructure testStructure, TranslationService translationService)
					throws SystemException {
				set.add("deleted");
			}

			@Override
			public String getStatus(TestStructure testStructure, TranslationService translationService)
					throws SystemException {
				return null;
			}

			@Override
			public void addProgressListener(TestStructure testStructure, ProgressListener listener) {
			}

			@Override
			public void addChild(TestStructure testStructureChild, TranslationService translationService)
					throws SystemException {
			}

			@Override
			public boolean validateConfiguration(TestProject testProject, TranslationService translationService)
					throws SystemException {
				return false;
			}

			@Override
			public List<TeamChange> revert(TestStructure testStructure, TranslationService translationService)
					throws SystemException {
				return null;
			}

			@Override
			public void rename(TestStructure testStructure, String newName, TranslationService translationService)
					throws SystemException {
				set.add("renamed");
			}

			@Override
			public void addAdditonalFile(TestStructure testStructur, String fileName) throws SystemException {
			}

			@Override
			public int availableUpdatesCount(TestProject testProject) {
				return 0;
			}
		};
	}
}
