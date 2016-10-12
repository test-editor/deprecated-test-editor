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

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.model.testresult.TestResult;
import org.testeditor.core.model.teststructure.TestCompositeStructure;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.services.interfaces.TestStructureService;

/**
 * Adpater for Tests with svn. This class will be removed after adding mockito
 * to the infrastructure.
 *
 */
public class TestStructureServiceAdpater implements TestStructureService {

	@Override
	public void loadChildrenInto(TestCompositeStructure testCompositeStructure) throws SystemException {

	}

	@Override
	public void create(TestStructure testStructure) throws SystemException {

	}

	@Override
	public void delete(TestStructure testStructure) throws SystemException {

	}

	@Override
	public List<String> rename(TestStructure testStructure, String newName) throws SystemException {
		return null;
	}

	@Override
	public TestResult executeTestStructure(TestStructure testStructure, IProgressMonitor monitor)
			throws SystemException, InterruptedException {
		return null;
	}

	@Override
	public String getTestExecutionLog(TestStructure testStructure) throws SystemException {
		return null;
	}

	@Override
	public boolean hasTestExecutionLog(TestStructure testStructure) throws SystemException {
		return false;
	}

	@Override
	public List<TestResult> getTestHistory(TestStructure testStructure) throws SystemException {
		return null;
	}

	@Override
	public void clearTestHistory(TestStructure testStructure) throws SystemException {

	}

	@Override
	public boolean isReservedName(TestProject testProtect, String name) {
		return false;
	}

	@Override
	public Runnable getTestProjectLazyLoader(TestCompositeStructure toBeLoadedLazy) {
		return null;
	}

	@Override
	public String lookUpTestStructureFullNameMatchedToPath(TestProject testProject, String path) {
		return null;
	}

	@Override
	public void pauseTest(TestStructure testStructure) throws SystemException {
	}

	@Override
	public void resumeTest(TestStructure testStructure) throws SystemException {
	}

	@Override
	public void stepwiseTest(TestStructure testStructure) throws SystemException {
	}

	@Override
	public List<String> move(TestStructure testStructure, TestCompositeStructure newParent) throws SystemException {
		return null;
	}

}
