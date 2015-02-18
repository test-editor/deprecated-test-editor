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
package org.testeditor.core.model.teststructure;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Test suite includes a hierarchical structure of sub test suites or/and test
 * cases.
 */
public class TestSuite extends TestCompositeStructure {

	private List<TestStructure> referredTestStructures = new ArrayList<TestStructure>();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getSourceCode() {
		StringBuffer sb = new StringBuffer();
		sb.append("!contents");
		for (TestStructure testStructure : referredTestStructures) {
			sb.append("\n!see .").append(testStructure.getFullName());
		}
		return sb.toString();
	}

	@Override
	public String getPageType() {
		return "Suite";
	}

	@Override
	public String getTypeName() {
		return TestType.SUITE.getName();
	}

	/**
	 * 
	 * @return list with all referred TestStructure.
	 */
	public List<TestStructure> getReferredTestStrcutures() {
		return referredTestStructures;
	}

	/**
	 * This Method returns all Tests reachable from this TestSuite. This
	 * contains: All TestChildren. All Refered TestStructures.
	 * 
	 * @return a Set of all Tests.
	 */
	public Set<TestStructure> getAllTestChildrensAndReferedTestcases() {
		HashSet<TestStructure> result = new HashSet<TestStructure>();
		result.addAll(getAllTestChildren());
		result.addAll(getReferredTestStrcutures());
		return result;
	}

	/**
	 * 
	 * @param testStructure
	 *            to be referred in this TestSuite.
	 */
	public void addReferredTestStructure(TestStructure testStructure) {
		if (testStructure == null) {
			throw new AssertionError("Null Value not allowed");
		}
		referredTestStructures.add(testStructure);
	}

	/**
	 * Sets the refferedTestStructures.
	 * 
	 * @param referredTestCases
	 *            to be set.
	 */
	public void setReferredTestStructures(List<TestStructure> referredTestCases) {
		referredTestStructures = referredTestCases;
	}

	/**
	 * 
	 * @param testCase
	 *            to be removed from the referredTestcases list.
	 */
	public void removeReferredTestStructure(TestStructure testCase) {
		referredTestStructures.remove(testCase);
	}

	@Override
	public boolean isExecutableTestStructure() {
		return true;
	}
	
}
