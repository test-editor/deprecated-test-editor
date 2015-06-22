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

import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.exceptions.TestCycleDetectException;
import org.testeditor.core.model.teststructure.TestComponent;
import org.testeditor.core.model.teststructure.TestFlow;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.services.interfaces.TestStructureContentService;

public class TestStructureContentServiceDispatcher implements TestStructureContentService {

	@Override
	public void refreshTestCaseComponents(TestStructure testStructure) throws SystemException, TestCycleDetectException {
		// TODO Auto-generated method stub

	}

	@Override
	public void saveTestStructureData(TestStructure testStructure) throws SystemException {
		// TODO Auto-generated method stub

	}

	@Override
	public void reparseChangedTestFlow(TestFlow testFlow) throws SystemException {
		// TODO Auto-generated method stub

	}

	@Override
	public List<TestComponent> parseFromString(TestFlow testFlow, String storedTestComponents) throws SystemException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTestStructureAsSourceText(TestStructure testStructure) throws SystemException {
		// TODO Auto-generated method stub
		return null;
	}

}
