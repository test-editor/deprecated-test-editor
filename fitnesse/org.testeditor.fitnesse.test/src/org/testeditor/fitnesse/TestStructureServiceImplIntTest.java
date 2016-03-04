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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Map;

import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.junit.Test;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.exceptions.TeamAuthentificationException;
import org.testeditor.core.model.team.TeamShareConfig;
import org.testeditor.core.model.teststructure.TestCase;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestProjectConfig;
import org.testeditor.core.model.teststructure.TestStructure;
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
public class TestStructureServiceImplIntTest {

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
			public void revert(TestStructure testStructure, TranslationService translationService)
					throws SystemException {

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

			@Override
			public void removeAdditonalFile(TestStructure testStructure, String fileName) throws SystemException {
			}

			@Override
			public boolean isCleanupNeeded(TestProject testProject) throws SystemException {
				return false;
			}

			@Override
			public void cleanup(TestProject testProject) throws SystemException {
			}

			@Override
			public Map<String, String> getAvailableReleases(TestProject testProject) {
				return null;
			}

			@Override
			public void switchToBranch(TestProject testproject, String url) throws SystemException {
			}

			@Override
			public String getCurrentBranch(TestProject testProject) {
				return null;
			}

			@Override
			public boolean isDirty(TestProject project) {
				return false;
			}
		};
	}

}
