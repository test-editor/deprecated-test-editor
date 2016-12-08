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

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.model.testresult.TestResult;
import org.testeditor.core.model.teststructure.TestCompositeStructure;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.model.teststructure.TestSuite;
import org.testeditor.core.services.interfaces.TestStructureService;

/**
 * Adapter for the mock-objects in the test of teststructureservice.
 * 
 */
public class TestStructureServiceAdapter implements TestStructureService {

	private boolean empty = false;

	/**
	 * sets the empty variable.
	 * 
	 * @param empty
	 *            boolean
	 */
	public void setEmptyVariable(boolean empty) {
		this.empty = empty;
	}

	@Override
	public List<String> rename(TestStructure testStructure, String newName) throws SystemException {
		return null;
	}

	@Override
	public void delete(TestStructure testStructure) throws SystemException {
	}

	@Override
	public void loadChildrenInto(TestCompositeStructure project) throws SystemException {
		if (!empty) {
			TestSuite suite = new TestSuite();
			suite.setName("root");
			project.addChild(suite);
		} else {
			throw new SystemException("");
		}
	}

	@Override
	public TestResult executeTestStructure(TestStructure testStructure, IProgressMonitor monitor)
			throws SystemException {
		return null;
	}

	@Override
	public void create(TestStructure testStructure) throws SystemException {

	}

	@Override
	public String getTestExecutionLog(TestStructure testStructure) throws SystemException {
		return null;
	}

	@Override
	public List<TestResult> getTestHistory(TestStructure testStructure) throws SystemException {
		return null;
	}

	@Override
	public boolean isReservedName(TestProject testProject, String name) {
		return false;
	}

	@Override
	public void clearTestHistory(TestStructure testStructure) throws SystemException {
	}

	@Override
	public Runnable getTestProjectLazyLoader(TestCompositeStructure toBeLoadedLazy) {
		return null;
	}

	@Override
	public boolean hasTestExecutionLog(TestStructure testStructure) throws SystemException {
		return false;
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
