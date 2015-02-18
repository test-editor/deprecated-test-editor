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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;

import org.junit.Before;
import org.junit.Test;

/**
 * 
 * Test for the TestEditorTestFlowDataTransferContainer class.
 * 
 * @author llipinski
 */
public class TestEditorTestFlowDataTransferContainerTest {
	private TestEditorTestDataTransferContainer testEditorTestFlowDataTransferContainer;

	/**
	 * before the tests.
	 */
	@Before
	public void beforeTest() {
		testEditorTestFlowDataTransferContainer = new TestEditorTestDataTransferContainer();
		testEditorTestFlowDataTransferContainer.setStoredTestComponents("Dies sind die TestDaten");
		testEditorTestFlowDataTransferContainer.setTestProjectName("Project_one");
	}

	/**
	 * gets the stored TestComponents as a String.
	 * 
	 */
	@Test
	public void getStoredTestComponents() {
		assertEquals("Dies sind die TestDaten", testEditorTestFlowDataTransferContainer.getStoredTestComponents());
	}

	/**
	 * test the setter of the TestComponents.
	 * 
	 */
	@Test
	public void setStoredTestComponents() {
		testEditorTestFlowDataTransferContainer.setStoredTestComponents("Dies sind neue TestDaten");
		assertNotEquals("Dies sind die TestDaten", testEditorTestFlowDataTransferContainer.getStoredTestComponents());
		assertEquals("Dies sind neue TestDaten", testEditorTestFlowDataTransferContainer.getStoredTestComponents());
	}

	/**
	 * 
	 * test the getter of the ProjectName.
	 */
	@Test
	public void getTestProjectName() {
		assertEquals("Project_one", testEditorTestFlowDataTransferContainer.getTestProjectName());
	}

	/**
	 * tests the setter of the ProjectName.
	 * 
	 */
	@Test
	public void setTestProjectName() {
		testEditorTestFlowDataTransferContainer.setTestProjectName("Second_Project");
		assertNotEquals("Project_one", testEditorTestFlowDataTransferContainer.getTestProjectName());
		assertEquals("Second_Project", testEditorTestFlowDataTransferContainer.getTestProjectName());
	}

	/**
	 * 
	 * test the isEmpty-method.
	 */
	@Test
	public void isEmpty() {
		assertFalse(testEditorTestFlowDataTransferContainer.isEmpty());
	}

}
