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
package org.testeditor.ui.adapter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestProjectConfig;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.services.interfaces.TestProjectService;

/**
 * TestProjectServiceAdapter is a general dummy implementation of the
 * TestProjectService interface. Classes that extends this class can override
 * its methods according.
 * 
 */
public class TestProjectServiceAdapter implements TestProjectService {

	private List<TestProject> testProjects = new ArrayList<TestProject>();

	/**
	 * sets the list of the testProjects.
	 * 
	 * @param testProjects
	 *            List<TestProject>
	 */
	public void setProjects(List<TestProject> testProjects) {
		this.testProjects = testProjects;

	}

	@Override
	public List<TestProject> getProjects() {
		return testProjects;
	}

	@Override
	public void storeProjectConfig(TestProject testProject, TestProjectConfig config) throws SystemException {
	}

	@Override
	public TestProject createNewProject(String projectName) throws IOException, SystemException {
		return null;
	}

	@Override
	public void createAndConfigureDemoProjects(List<File> demoProjectsDirs) throws SystemException {

	}

	@Override
	public TestProject renameTestproject(TestProject testProject, String newName) throws IOException, SystemException {
		return null;
	}

	@Override
	public File[] getDemoProjects() throws IOException {
		return null;
	}

	@Override
	public void deleteProject(TestProject testProject) {

	}

	@Override
	public List<TestStructure> getElements() throws SystemException {
		List<TestStructure> testComposites = new ArrayList<TestStructure>();

		for (TestProject testProject : getProjects()) {
			testComposites.add(testProject);
		}
		return testComposites;
	}

	@Override
	public TestProject getProjectWithName(String testProjectName) {
		return null;
	}

	@Override
	public TestStructure findTestStructureByFullName(String testStructureFullName) throws SystemException {
		return null;
	}

	@Override
	public void reloadProjectList() throws SystemException {
	}

	@Override
	public void reloadTestProjectFromFileSystem(TestProject testProject) throws SystemException {

	}

	@Override
	public boolean existsProjectWithName(String projectName) {
		return false;
	}

}
