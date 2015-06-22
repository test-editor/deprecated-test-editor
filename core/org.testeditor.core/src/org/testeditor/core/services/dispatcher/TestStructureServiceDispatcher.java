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
package org.testeditor.core.services.dispatcher;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.model.testresult.TestResult;
import org.testeditor.core.model.teststructure.TestCompositeStructure;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.services.interfaces.TestStructureService;

public class TestStructureServiceDispatcher implements TestStructureService {

	@Override
	public void loadChildrenInto(TestCompositeStructure testCompositeStructure) throws SystemException {
		// TODO Auto-generated method stub

	}

	@Override
	public void create(TestStructure testStructure) throws SystemException {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete(TestStructure testStructure) throws SystemException {
		// TODO Auto-generated method stub

	}

	@Override
	public void rename(TestStructure testStructure, String newName) throws SystemException {
		// TODO Auto-generated method stub

	}

	@Override
	public TestResult executeTestStructure(TestStructure testStructure, IProgressMonitor monitor)
			throws SystemException, InterruptedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTestExecutionLog(TestStructure testStructure) throws SystemException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasTestExecutionLog(TestStructure testStructure) throws SystemException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<TestResult> getTestHistory(TestStructure testStructure) throws SystemException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void clearTestHistory(TestStructure testStructure) throws SystemException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isReservedName(String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Runnable getTestProjectLazyLoader(TestCompositeStructure toBeLoadedLazy) {
		// TODO Auto-generated method stub
		return null;
	}

}
