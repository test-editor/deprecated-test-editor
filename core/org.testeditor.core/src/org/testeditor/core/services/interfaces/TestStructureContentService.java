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
package org.testeditor.core.services.interfaces;

import java.util.List;

import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.exceptions.TestCycleDetectException;
import org.testeditor.core.model.teststructure.TestComponent;
import org.testeditor.core.model.teststructure.TestFlow;
import org.testeditor.core.model.teststructure.TestStructure;

/**
 * Provides read and write services regarding the content of a test flow. A test
 * flow could be a simple test case or a test scenario (this data is used in the
 * middle view of the Test-Editor).
 */
public interface TestStructureContentService {

	/**
	 * Reads the content of a test structure and refreshes the internal
	 * components.
	 * 
	 * @param testStructure
	 *            test suite, test case or test scenario
	 * @throws SystemException
	 *             is thrown if a system exception occurred
	 * 
	 * @throws TestCycleDetectException
	 *             on cycle in testcall hierarchies
	 */
	// TODO Rename Method to make intension clear
	void refreshTestCaseComponents(TestStructure testStructure) throws SystemException, TestCycleDetectException;

	/**
	 * Saves the content of the given test flow by the internal components.
	 * 
	 * @param testStructure
	 *            test case, test scenario or test suite
	 * @throws SystemException
	 *             is thrown if a system exception occurred
	 */
	void saveTestStructureData(TestStructure testStructure) throws SystemException;

	/**
	 * Parses the test flow after changes.
	 * 
	 * @param testFlow
	 *            test case or test scenario
	 * @throws SystemException
	 *             is thrown if a system exception occurred (e.g. third party
	 *             system unavailable)
	 */
	// TODO Method name and doc is not clear. why using here is testflow?. Which
	// text is parsed?
	void reparseChangedTestFlow(TestFlow testFlow) throws SystemException;

	/**
	 * this method parses the string with the stored TestComponents after a
	 * paste call. The TransferObject stores only Strings not Testcomponents. So
	 * their is a method needed, that parse a String-Obbject to a TestComponent.
	 * The TestComponents are stored by a copy or cut action.
	 * 
	 * @param testFlow
	 *            the TestFlow
	 * @param storedTestComponents
	 *            string whit the storedTestComponents
	 * @return a LinkedListe with the TestComponents
	 * @throws SystemException
	 *             while parsing
	 */
	// TODO Method name and doc is not clear. why using here is testflow?

	List<TestComponent> parseFromString(TestFlow testFlow, String storedTestComponents) throws SystemException;

	/**
	 * Returns a given test structure as source text.
	 * 
	 * @param testStructure
	 *            text of structure which should be shown
	 * @return String
	 * @throws SystemException
	 *             is thrown if a system exception occurred
	 */
	String getTestStructureAsSourceText(TestStructure testStructure) throws SystemException;

	/**
	 * This id is used to identify the TeestStructureServer plug-in. It must the
	 * same ID in the <code>TestProjectConfig</code>.
	 * 
	 * @return ID to Identify the Plug-In.
	 */
	String getId();

}
