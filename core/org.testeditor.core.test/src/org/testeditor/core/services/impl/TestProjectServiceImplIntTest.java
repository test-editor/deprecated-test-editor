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
package org.testeditor.core.services.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.services.internal.events.EventBroker;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.FrameworkUtil;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.model.team.TeamShareConfig;
import org.testeditor.core.model.teststructure.TestCase;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestProjectConfig;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.services.interfaces.FieldMappingExtension;
import org.testeditor.core.services.interfaces.ServiceLookUpForTest;
import org.testeditor.core.services.interfaces.TestProjectService;
import org.testeditor.core.services.plugins.TeamShareConfigurationServicePlugIn;
import org.testeditor.core.services.plugins.TestEditorPlugInService;

/**
 * 
 * Integration tests for TestProjectServiceImpl.
 * 
 */
public class TestProjectServiceImplIntTest {

	private static final String PROJECT_NAME = "MyProject";

	/**
	 * Test the lookup for required bundles for copy Jobs.
	 * 
	 * @throws Exception
	 *             for Test
	 */
	@Test
	public void testLookUpForRequiredBundels() throws Exception {
		TestProjectServiceImpl service = (TestProjectServiceImpl) ServiceLookUpForTest
				.getService(TestProjectService.class);
		assertTrue("Demo Bundle exists", new File(service.findBundleFile("org.testeditor.demo")).exists());
	}

	/**
	 * Tests the Handle with null and not not nulled TeamShare Property.
	 */
	@Test
	public void testGetPropertiesFromConfigWithTeamShareLocalAndRemote() {
		FrameworkUtil.getBundle(getClass()).getBundleContext()
				.registerService(TeamShareConfigurationServicePlugIn.class, getTeamServiceConfigurationMock(), null);
		TestProjectConfig projectConfig = new TestProjectConfig();
		TestProjectServiceImpl service = (TestProjectServiceImpl) ServiceLookUpForTest
				.getService(TestProjectService.class);
		assertNotNull("Expect Properties on null value by teamshare", service.getPropertiesFrom(projectConfig));
		projectConfig.setTeamShareConfig(new TeamShareConfig() {

			@Override
			public String getId() {
				return "myTeamId";
			}
		});
		assertEquals("Expect Team ID in Properties", "myTeamId",
				service.getPropertiesFrom(projectConfig).getProperty(TestEditorPlugInService.TEAMSHARE_ID));
	}

	/**
	 * Tests reading Demo Projects from Filesystem without the Template
	 * DemoEmpty.
	 * 
	 * @throws Exception
	 *             on test failure.
	 */
	@Test
	public void testGetDemoProjects() throws Exception {
		TestProjectServiceImpl service = (TestProjectServiceImpl) ServiceLookUpForTest
				.getService(TestProjectService.class);
		Set<String> demoProjectNames = new HashSet<String>();
		File[] demoProjects = service.getDemoProjects();
		for (File file : demoProjects) {
			demoProjectNames.add(file.getName());
		}
		assertTrue("Expecting a demo project:", demoProjectNames.contains("DemoWebTests"));
		assertTrue("Expecting a demo project:", demoProjectNames.contains("DemoWebRapTests"));
		assertTrue("Expecting a demo project:", demoProjectNames.contains("DemoSwingTests"));
		assertFalse("Expecting DemoEmpty not in the List of DemoProjects.", demoProjectNames.contains("DemoEmpty"));
	}

	/**
	 * 
	 * @return TeamShareConfigurationServiceMock.
	 */
	private TeamShareConfigurationServicePlugIn getTeamServiceConfigurationMock() {
		return new TeamShareConfigurationServicePlugIn() {

			@Override
			public String getTranslatedHumanReadablePlugInName(TranslationService translationService) {
				return null;
			}

			@Override
			public String getId() {
				return "myTeamId";
			}

			@Override
			public List<FieldMappingExtension> getFieldMappingExtensions() {
				return null;
			}

			@Override
			public Map<String, String> getAsProperties(TeamShareConfig teamShareConfig) {
				return new HashMap<String, String>();
			}

			@Override
			public TeamShareConfig createAnEmptyTeamShareConfig() {
				return null;
			}

			@Override
			public TeamShareConfig createTeamShareConfigFrom(Properties properties) {
				return new TeamShareConfig() {

					@Override
					public String getId() {
						return "myTeamId";
					}
				};
			}

			@Override
			public String getTemplateForConfiguration() {
				return null;
			}

		};
	}

	/**
	 * Test the creation of a TestProject with a Team Share Property.
	 * 
	 * @throws IOException
	 *             on storing the configuration after a migration
	 */
	@Test
	public void testGetProjectConfigWithTeamShareOptionFromProperties() throws IOException {
		FrameworkUtil.getBundle(getClass()).getBundleContext()
				.registerService(TeamShareConfigurationServicePlugIn.class, getTeamServiceConfigurationMock(), null);
		Properties props = new Properties();
		props.put(TestEditorPlugInService.TEAMSHARE_ID, "myTeamId");
		props.put(TestProjectService.VERSION_TAG, TestProjectService.VERSION1_2);
		TestProjectServiceImpl service = (TestProjectServiceImpl) ServiceLookUpForTest
				.getService(TestProjectService.class);
		TestProjectConfig testProjectConfig = service.getTestProjectConfigFrom(props, PROJECT_NAME);
		assertTrue("Expect Testproject has team support", testProjectConfig.isTeamSharedProject());
		assertNotNull("Expect a TeamShare Config in the Testproject", testProjectConfig.getTeamShareConfig());
	}

	/**
	 * Tests the creation of the Demo projects.
	 * 
	 * @throws Exception
	 *             on IO Error.
	 */
	@Test
	public void testCreateAndConfigureDemoProjects() throws Exception {
		TestProjectService service = ServiceLookUpForTest.getService(TestProjectService.class);
		service.reloadProjectList();
		List<File> demoProjectsDirs = new ArrayList<File>();
		for (File file : service.getDemoProjects()) {
			if (file.getName().equals("DemoWebTests")) {
				demoProjectsDirs.add(file);
			}
		}
		service.createAndConfigureDemoProjects(demoProjectsDirs);
		assertTrue("Expecting DemoWebtests",
				new File(Platform.getLocation().toFile() + File.separator + "DemoWebTests").exists());
		assertTrue("Expecting DemoWebtests with config.tpr.", new File(
				Platform.getLocation().toFile() + File.separator + "DemoWebTests" + File.separator + "config.tpr")
						.exists());
	}

	/**
	 * Tests the creation of a new Project and the correct registration in the
	 * project list.
	 * 
	 * @throws Exception
	 *             on IO Error.
	 */
	@Test
	public void testCreateNewProject() throws Exception {
		TestProjectService service = ServiceLookUpForTest.getService(TestProjectService.class);
		service.reloadProjectList();
		assertTrue("Empty Project list expected.", service.getProjects().isEmpty());
		TestProject testProject = service.createNewProject("MyDemo");
		assertTrue("Expecting Project in List.", service.getProjects().contains(testProject));
		assertNotNull("Expecting Project with config.", testProject.getTestProjectConfig());
	}

	/**
	 * Test the Rename and lookup with old name.
	 * 
	 * @throws Exception
	 *             on IO Error
	 */
	@Test
	public void testRenameProject() throws Exception {
		TestProjectService service = ServiceLookUpForTest.getService(TestProjectService.class);
		service.reloadProjectList();
		TestProject testProject = service.createNewProject("MyDemo");
		assertTrue("Expecting Project in List.", service.getProjects().contains(testProject));
		service.renameTestproject(testProject, "RenamedProject");
		assertEquals("Expect Lookup with renamed name.", testProject, service.getProjectWithName("RenamedProject"));
	}

	/**
	 * Tests the registration of an existing project to the list.
	 * 
	 * @throws Exception
	 *             on IO Error.
	 */
	@Test
	public void testReloadTestProjectWithNameOnEmptyList() throws Exception {
		List<TestProject> list = new ArrayList<TestProject>();
		TestProjectServiceImpl service = getTestProjectImplMockWithProjects(list);
		createProjectInFileSystem();
		TestProject tp = new TestProject();
		tp.setName("MyPrj");
		service.reloadTestProjectFromFileSystem(tp);
		assertEquals(1, service.getProjects().size());
	}

	/**
	 * Tests the replace of an existing project to the list.
	 * 
	 * @throws Exception
	 *             on IO Error.
	 */
	@Test
	public void testReplaceInReloadTestProjectWithNameOnExistingList() throws Exception {
		List<TestProject> list = new ArrayList<TestProject>();
		TestProject tp = new TestProject();
		tp.setName("MyPrj");
		list.add(tp);
		TestProjectServiceImpl service = getTestProjectImplMockWithProjects(list);
		createProjectInFileSystem();
		service.reloadTestProjectFromFileSystem(tp);
		assertEquals(1, service.getProjects().size());
	}

	/**
	 * Test lookup of TestProject with old name after renaming TestProjects.
	 * 
	 * @throws Exception
	 *             if renaming on file system fails
	 */
	@Test
	public void testLookUpForRenamedTestProjects() throws Exception {
		List<TestProject> list = new ArrayList<TestProject>();
		TestProject tp = new TestProject();
		tp.setName("FirstName");
		TestCase testCase = new TestCase();
		testCase.setName("TestCase");
		tp.addChild(testCase);
		list.add(tp);
		TestProjectServiceImpl service = getTestProjectImplMockWithProjects(list);
		assertNotNull(service.findTestStructureByFullName("FirstName.TestCase"));
		service.renameTestproject(tp, "SecondName");
		TestStructure firstNameTP = service.findTestStructureByFullName("FirstName.TestCase");
		TestStructure secondNameTP = service.findTestStructureByFullName("SecondName.TestCase");
		assertNotNull(firstNameTP);
		assertNotNull(secondNameTP);
		assertSame(firstNameTP, secondNameTP);
	}

	/**
	 * Tests the check of an renamed element in the path.
	 * 
	 * @throws Exception
	 *             if renaming on file system fails
	 */
	@Test
	public void testContainsFullNameRenamedElements() throws Exception {
		List<TestProject> list = new ArrayList<TestProject>();
		TestProject tp = new TestProject();
		list.add(tp);
		TestProjectServiceImpl service = getTestProjectImplMockWithProjects(list);
		tp.setName("FirstName");
		service.renameTestproject(tp, "myname");
		tp.setName("TestPrj");
		service.renameTestproject(tp, "myname");
		assertNull(service.containsFullNameRenamedElements("SecondProject.TestPath"));
		assertNull(service.containsFullNameRenamedElements("myname.TestPath"));
		assertEquals("FirstName", service.containsFullNameRenamedElements("FirstName.TestPath"));
		assertEquals("TestPrj", service.containsFullNameRenamedElements("TestPrj.TestPath"));
	}

	/**
	 * 
	 * @throws Exception
	 *             on IO Error
	 */
	@Test
	public void testDeleteProject() throws Exception {
		TestProjectService service = ServiceLookUpForTest.getService(TestProjectService.class);
		service.reloadProjectList();
		TestProject testProject = service.createNewProject("MyDemo");
		assertTrue("Expecting Project in List.", service.getProjects().contains(testProject));
		service.deleteProject(testProject);
		assertTrue("Empty Project list expected.", service.getProjects().isEmpty());
	}

	/**
	 * the lookup of the event broker on compute context.
	 */
	@Test
	public void testCompute() {
		TestProjectServiceImpl testProjectServiceImpl = new TestProjectServiceImpl();
		testProjectServiceImpl.bind(new FileWatchServiceImpl());
		IEclipseContext context = EclipseContextFactory.create();
		assertNull(testProjectServiceImpl.getEventBroker());
		context.set(IEventBroker.class, new EventBroker());
		testProjectServiceImpl.compute(context, "");
		assertNotNull(testProjectServiceImpl.getEventBroker());
		IEventBroker second = new EventBroker();
		context.set(IEventBroker.class, second);
		assertNotSame(testProjectServiceImpl.getEventBroker(), second);
		testProjectServiceImpl.compute(context, "");
		assertNotSame(testProjectServiceImpl.getEventBroker(), second);
	}

	/**
	 * Tests setting a url to a loaded testproject.
	 * 
	 * @throws Exception
	 *             on test failue
	 */
	@Test
	public void testGetURLFromProject() throws Exception {
		TestProjectService service = ServiceLookUpForTest.getService(TestProjectService.class);
		createProjectInFileSystem();
		service.reloadProjectList();
		TestProject testProject = service.getProjects().get(0);
		assertNotNull(testProject.getUrl());
		assertTrue(new File(testProject.getUrl().toURI()).exists());
	}

	/**
	 * Tests setting a url to a renamed testproject.
	 * 
	 * @throws Exception
	 *             on test failue
	 */
	@Test
	public void testGetURLFroRenamedProject() throws Exception {
		TestProjectService service = ServiceLookUpForTest.getService(TestProjectService.class);
		createProjectInFileSystem();
		service.reloadProjectList();
		TestProject testProject = service.getProjects().get(0);
		service.renameTestproject(testProject, "NewPrjName");
		assertTrue(new File(testProject.getUrl().toURI()).exists());
	}

	/**
	 * 
	 * @return TestProjectServiceMock to work with a virtual FileSystem.
	 */
	private TestProjectServiceImpl getTestProjectServiceImplMock() {
		return new TestProjectServiceImpl() {
			@Override
			protected File[] getWorkspaceDirectories() {
				return new File[] { new File(".meta"), getTestProjectMockFile(), new File("NoTestProject") };
			}

			@Override
			public TestProjectConfig getProjectConfigFor(TestProject testProject) throws SystemException {
				return new TestProjectConfig();
			}
		};
	}

	/**
	 * 
	 * @return File Mock to be used as a Project Folder.
	 */
	protected File getTestProjectMockFile() {
		return new File("AkzeptanzTests") {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isDirectory() {
				return true;
			}

			@Override
			public String[] list() {
				return new String[] { "config.tpr" };
			}
		};
	}

	/**
	 * Cleans existing projects in the Workspace.
	 * 
	 * @throws Exception
	 *             on IO Error
	 */
	@Before
	public void setUp() throws Exception {
		File wsDir = Platform.getLocation().toFile();
		for (File file : wsDir.listFiles()) {
			if (file.isDirectory() && !file.getName().startsWith(".")) {
				Files.walkFileTree(file.toPath(), new SimpleFileVisitor<Path>() {
					@Override
					public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
						Files.delete(file);
						return FileVisitResult.CONTINUE;
					}

					@Override
					public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
						Files.delete(dir);
						return FileVisitResult.CONTINUE;
					}

				});

			}
		}
	}

	/**
	 * helper method to create a simple demo structure.
	 * 
	 * @throws IOException
	 *             on io problems.
	 */
	private void createProjectInFileSystem() throws IOException {
		File prjDir = new File(Platform.getLocation().toFile() + File.separator + "MyPrj");
		if (prjDir.mkdir()) {
			Properties props = new Properties();
			props.put(TestEditorPlugInService.TEAMSHARE_ID, "myTeamId");
			props.put(TestProjectService.VERSION_TAG, TestProjectService.VERSION1_2);
			FileOutputStream stream = new FileOutputStream(new File(prjDir + File.separator + "config.tpr"));
			try {
				props.store(stream, "Test");
			} finally {
				stream.close();
			}
			File internalDir = new File(new File(prjDir, "FitNesseRoot"), "MyPrj");
			if (!internalDir.mkdirs()) {
				fail("Error creating demo project fitnesse directory structure for renaming tests.");
			}
		}
	}

	/**
	 * Get TestProjectImplMock with projects.
	 * 
	 * @param projectList
	 *            list of projects
	 * @return TestProjectSvericeImpl
	 */
	private TestProjectServiceImpl getTestProjectImplMockWithProjects(final List<TestProject> projectList) {
		return new TestProjectServiceImpl() {
			@Override
			public List<TestProject> getProjects() {
				return projectList;
			}

			@Override
			protected void renameProjectInFileSystem(TestProject testProject, String newName)
					throws SystemException, IOException {
				getProject(testProject.getName()).setName(newName);
			}
		};
	}

}
