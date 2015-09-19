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

import org.junit.Test;

/**
 * 
 * test for {@link TestDescriptionTestScenario}.
 * 
 * @author llipinski
 */
public class TestDescriptionTestScenarioTest {
	/**
	 * constructor.
	 */
	@Test
	public void testDescriptionTestScenario() {
		TestDescriptionTestScenario testDescriptionTestScenario = new TestDescriptionTestScenario();
		assertEquals("|note|Description: |", testDescriptionTestScenario.getSourceCode());
	}

	/**
	 * constructor.
	 * 
	 */
	@Test
	public void testDescriptionTestScenarioParam() {
		String beschreibung = "Beschreibung";
		TestDescriptionTestScenario testDescriptionTestScenario = new TestDescriptionTestScenario(beschreibung);
		assertEquals("|note|Description: " + beschreibung + "|", testDescriptionTestScenario.getSourceCode());
	}
}
