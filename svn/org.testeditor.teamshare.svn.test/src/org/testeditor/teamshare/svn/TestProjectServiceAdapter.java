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
package org.testeditor.teamshare.svn;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestProjectConfig;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.services.interfaces.TestProjectService;

/**
 * Adpater for Tests with svn. This class will be removed after adding mockito
 * to the infrastructure.
 *
 */
public class TestProjectServiceAdapter implements TestProjectService {

	@Override
	public List<? extends TestStructure> getElements() throws SystemException {
		return null;
	}

	@Override
	public List<TestProject> getProjects() {
		return null;
	}

	@Override
	public void storeProjectConfig(TestProject testProject, TestProjectConfig config) throws SystemException {

	}

	@Override
	public TestProject createNewProject(String projectName) throws IOException, SystemException {
		return null;
	}

	@Override
	public void deleteProject(TestProject testProject) throws IOException {

	}

	@Override
	public void createAndConfigureDemoProjects(List<File> list) throws SystemException {

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
