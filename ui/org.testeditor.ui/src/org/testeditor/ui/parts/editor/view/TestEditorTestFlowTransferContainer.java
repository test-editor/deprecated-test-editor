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
package org.testeditor.ui.parts.editor.view;

import java.util.List;

import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.model.teststructure.TestComponent;
import org.testeditor.core.model.teststructure.TestFlow;
import org.testeditor.core.services.interfaces.TestStructureContentService;

/**
 * 
 * Special transfercontainer for the TestFlow.
 * 
 */
public class TestEditorTestFlowTransferContainer extends TestEditorTestDataTransferContainer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8112225684808313964L;

	/**
	 * setter to set the list of TestStructures for the transfer.
	 * 
	 * @param transfList
	 *            List<TestStructure>
	 */
	public void setTransferStructure(List<TestComponent> transfList) {
		StringBuilder transferComponents = new StringBuilder();
		for (TestComponent testComp : transfList) {
			transferComponents.append(testComp.getSourceCode()).append("\n");
		}
		setStoredTestComponents(transferComponents.toString());
	}

	/**
	 * returns the transfered TestComponents.
	 * 
	 * @param testFlow
	 *            the actual TestFlow
	 * @param testStructureContentService
	 *            TestStructureContentService
	 * @return List<TestComponents>
	 * @throws SystemException
	 *             on parsing the stored string
	 */
	public List<TestComponent> getStoredTestComponents(TestFlow testFlow,
			TestStructureContentService testStructureContentService) throws SystemException {

		return testStructureContentService.parseFromString(testFlow, getStoredTestComponents());
	}
}
