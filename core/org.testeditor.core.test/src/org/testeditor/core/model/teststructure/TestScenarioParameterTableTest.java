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

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.testeditor.core.model.action.TextType;

/**
 * 
 * test the {@link TestScenarioParameterTable} class.
 * 
 * @author llipinski
 */
public class TestScenarioParameterTableTest {

	private TestScenarioParameterTable testScenarioParameterTable = new TestScenarioParameterTable();

	/**
	 * before any test this method initialize the member
	 * testScenarioParameterTable.
	 */
	@Before
	public void beforeTests() {
		testScenarioParameterTable.setInclude("DemoWebTests.TestKomponenten.LoginSzenario");
		testScenarioParameterTable.setTitle("Login Szenario");
		TestDataRow tableHeader = new TestDataRow(new String[] { "page", "searchString" });
		testScenarioParameterTable.getDataTable().addRow(tableHeader);
		TestDataRow tableLineOne = new TestDataRow(new String[] { "www.SignalIduna.de" });
		tableLineOne.add("Signal");
		TestDataRow tableLineTo = new TestDataRow(new String[] { "www.SignalIduna.de" });
		tableLineTo.add("Iduna");
		testScenarioParameterTable.getDataTable().addRow(tableLineOne);
		testScenarioParameterTable.getDataTable().addRow(tableLineTo);

	}

	/**
	 * this method tests the sourcecode of the testScenarioParameterTable.
	 */
	@Test
	public void testSourceCode() {
		assertEquals(
				"!include <DemoWebTests.TestKomponenten.LoginSzenario\n!|Login Szenario|\n|page|searchString|\n|www.SignalIduna.de|Signal|\n|www.SignalIduna.de|Iduna|\n#",
				testScenarioParameterTable.getSourceCode());
	}

	/**
	 * this method tests the getTexts-method.
	 */
	@Test
	public void testGetTexts() {
		assertEquals("Login Szenario", testScenarioParameterTable.getTexts().get(0));
	}

	/**
	 * this method tests the getTextTypes-method.
	 */
	@Test
	public void testGetTextYpes() {
		assertEquals(TextType.ACTION_NAME, testScenarioParameterTable.getTextTypes().get(0));
	}

	/**
	 * this method test the extraction of the title out of the include.
	 */
	@Test
	public void testGetTitleOutOfInclude() {
		assertEquals(testScenarioParameterTable.getTitle(), testScenarioParameterTable.getTitleOutOfInclude());
	}

}
