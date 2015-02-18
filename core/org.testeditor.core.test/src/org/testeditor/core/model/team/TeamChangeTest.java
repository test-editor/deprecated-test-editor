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
package org.testeditor.core.model.team;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.model.teststructure.TestCase;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.model.teststructure.TestSuite;

/**
 * 
 * Integration Tests for TeamChange.
 * 
 */
public class TeamChangeTest {

	/**
	 * 
	 * Tests the lookup of a TestStructure based on a relative path name. Path
	 * Name like: /DemoWebRapTests/DialogWidgetsSuite/ConfirmMessageTest
	 * 
	 * @throws SystemException
	 *             for test
	 */
	@Test
	public void testLookUpOfTestStructure() throws SystemException {
		TestProject project = new TestProject();
		project.setName("DemoWebRapTests");
		TestSuite suite = new TestSuite();
		project.addChild(suite);
		suite.setName("DialogWidgetsSuite");
		TestCase testCase = new TestCase();
		testCase.setName("ConfirmMessageTest");
		suite.addChild(testCase);
		TeamChange change = new TeamChange(TeamChangeType.DELETE,
				"DemoWebRapTests.DialogWidgetsSuite.ConfirmMessageTest", project);
		TestStructure testStructure = change.getReleatedTestStructure();
		assertNotNull("TestStructure expected.", testStructure);
		assertEquals("Expecting MyTestCase as teststructure", "ConfirmMessageTest", testStructure.getName());
	}

}
