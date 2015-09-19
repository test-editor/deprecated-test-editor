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

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.testeditor.core.model.action.TextType;

/**
 * 
 * module-test for the {@link TestScenarioParameters}.
 * 
 * @author llipinski
 */
public class TestScenarioParametersTest {

	private TestScenarioParameters parameters = new TestScenarioParameters();

	/**
	 * initialize before each test.
	 */
	@Before
	public void beforeTest() {

		ArrayList<String> initParameters = new ArrayList<String>();
		initParameters.add("page");
		initParameters.add("subPage");
		parameters.setTexts(initParameters);
	}

	/**
	 * tests the getSourceCode method.
	 */
	@Test
	public void testGetSourceCode() {
		assertEquals("page, subPage", parameters.getSourceCode());
	}

	/**
	 * tests the getTexts method.
	 */
	@Test
	public void testGetTexts() {
		assertEquals("Scenario Parameter: ", parameters.getTexts().get(0));
		assertEquals("page", parameters.getTexts().get(1));
		assertEquals(", subPage", parameters.getTexts().get(2));
	}

	/**
	 * tests the getTextTypes method.
	 */
	@Test
	public void testGetTextTypes() {
		assertEquals(TextType.DESCRIPTION, parameters.getTextTypes().get(0));
		assertEquals(TextType.ARGUMENT, parameters.getTextTypes().get(1));
		assertEquals(TextType.ARGUMENT, parameters.getTextTypes().get(2));
	}

	/**
	 * test the setTexts method.
	 * 
	 */
	@Test
	public void setTexts() {
		ArrayList<String> localParameters = new ArrayList<String>();
		localParameters.add("name");
		localParameters.add("vorname");
		parameters.setTexts(localParameters);
		assertEquals("Scenario Parameter: ", parameters.getTexts().get(0));
		assertEquals("name", parameters.getTexts().get(1));
		assertEquals(", vorname", parameters.getTexts().get(2));
	}
}
