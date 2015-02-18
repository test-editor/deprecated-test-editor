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

import java.util.ArrayList;
import java.util.List;

import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.model.action.UnparsedActionLine;
import org.testeditor.core.model.teststructure.TestActionGroup;
import org.testeditor.core.model.teststructure.TestCase;
import org.testeditor.core.model.teststructure.TestComponent;
import org.testeditor.core.model.teststructure.TestDescriptionTestCase;
import org.testeditor.core.model.teststructure.TestFlow;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.services.interfaces.TestStructureContentService;

/**
 * Adpater of the TestFlowService to be used in Tests.
 * 
 */
public class TestStructureContentServiceAdapter implements TestStructureContentService {

	private static String newDescription = "New Description after refresh of the refreshTestCaseComponents";

	@Override
	public void refreshTestCaseComponents(TestStructure testStructure) throws SystemException {
		if (testStructure instanceof TestCase) {
			((TestCase) testStructure).getTestComponents().add(0, new TestDescriptionTestCase(newDescription));
		}

	}

	@Override
	public void saveTestStructureData(TestStructure testStructure) throws SystemException {
		testStructure.setName(testStructure.getName() + "_saved");

	}

	@Override
	public void reparseChangedTestFlow(TestFlow testFlow) throws SystemException {

	}

	@Override
	public List<TestComponent> parseFromString(TestFlow testFlow, String storedTestComponents) throws SystemException {
		List<TestComponent> compList = new ArrayList<TestComponent>();
		TestActionGroup testActionGroup = new TestActionGroup();
		testActionGroup.addActionLine(new UnparsedActionLine(storedTestComponents));
		compList.add((TestComponent) testActionGroup);
		return compList;
	}

	/**
	 * 
	 * @return the newDescription
	 */
	public String getNewDescription() {
		return newDescription;
	}

	@Override
	public String getId() {
		return "test_adapter";
	}

	@Override
	public String getTestStructureAsSourceText(TestStructure testStructure) throws SystemException {
		return null;
	}

}
