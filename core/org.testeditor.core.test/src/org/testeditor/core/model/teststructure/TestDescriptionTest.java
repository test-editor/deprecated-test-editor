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
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.testeditor.core.model.action.TextType;

/**
 * 
 * test for the testdescription.
 * 
 * @author llipinski
 */
public class TestDescriptionTest {

	/**
	 * testInitialization.
	 * 
	 * @throws Exception
	 *             Exception
	 */
	@Test
	public void testInitialization() throws Exception {
		TestDescription testDescription = new TestDescriptionTestCase();
		assertNotNull("Null Description not allowed", testDescription.getSourceCode());
		assertNotNull("Null Description not allowed", testDescription.getDescription());
	}

	/**
	 * test with a content in the description.
	 */
	@Test
	public void testSetDescription() {
		TestDescription testDescription = new TestDescriptionTestCase();
		String besch = "Meine Beschreibung";
		testDescription.setDescription(besch);
		assertEquals(testDescription.getDescription(), besch);
	}

	/**
	 * test gettextTypes().
	 */
	@Test
	public void testGetTextTypes() {
		TestDescription testDescription = new TestDescriptionTestCase();
		String besch = "Meine Beschreibung";
		testDescription.setDescription(besch);
		assertEquals(TextType.DESCRIPTION, testDescription.getTextTypes().get(0));
	}
}
