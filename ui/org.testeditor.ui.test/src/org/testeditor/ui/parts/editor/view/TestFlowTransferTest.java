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
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

/**
 * 
 * This class tests the TestFlowTransfer-Class.
 * 
 */
public class TestFlowTransferTest {

	private TestEditorTestDataTransferContainer testEditorTestFlowDataTransferContainer;
	private static final String MYTYPENAME = "test_flow_data_transfer"; //$NON-NLS-1$

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
	 * test the getInstance method.
	 */
	@Test
	public void getInstance() {
		assertTrue(TestEditorTestFlowTransfer.getInstance() != null);
	}

	/**
	 * test the getTypeNames-method.
	 */
	@Test
	public void getTypeNames() {
		assertEquals(MYTYPENAME, TestEditorTestFlowTransfer.getInstance().getTypeNames()[0]);
	}

	/**
	 * checks the object.
	 */
	@Test
	public void checkMyType() {
		TestEditorTransfer instance = TestEditorTestFlowTransfer.getInstance();
		assertTrue(instance.checkMyType(testEditorTestFlowDataTransferContainer));
		assertFalse(instance.checkMyType(new Object()));

	}

	/**
	 * test the checkMyType-method with a null-value for the TestProject. The
	 * result should be false.
	 */
	@Test
	public void checkMyTypeNullProjectName() {
		TestEditorTestFlowTransfer instance = TestEditorTestFlowTransfer.getInstance();
		testEditorTestFlowDataTransferContainer.setTestProjectName(null);
		assertFalse(instance.checkMyType(testEditorTestFlowDataTransferContainer));
	}

	/**
	 * test the checkMyType-method with a null-value for the
	 * StoredTestComponents. The result should be false.
	 */
	@Test
	public void checkMyTypeNullStoredTestComponents() {
		TestEditorTestFlowTransfer instance = TestEditorTestFlowTransfer.getInstance();
		testEditorTestFlowDataTransferContainer.setStoredTestComponents(null);
		assertFalse(instance.checkMyType(testEditorTestFlowDataTransferContainer));
	}

	/**
	 * test the checkMyType-method with a zero-length for the
	 * StoredTestComponents. The result should be false.
	 */
	@Test
	public void checkMyTypeEmptyStoredTestComponents() {
		TestEditorTestFlowTransfer instance = TestEditorTestFlowTransfer.getInstance();
		testEditorTestFlowDataTransferContainer.setStoredTestComponents("");
		assertFalse(instance.checkMyType(testEditorTestFlowDataTransferContainer));
	}

	/**
	 * validates the object.
	 * 
	 */
	@Test
	public void validate() {
		TestEditorTestFlowTransfer instance = TestEditorTestFlowTransfer.getInstance();
		testEditorTestFlowDataTransferContainer.setStoredTestComponents("");
		assertFalse(instance.validate(testEditorTestFlowDataTransferContainer));
	}

}
