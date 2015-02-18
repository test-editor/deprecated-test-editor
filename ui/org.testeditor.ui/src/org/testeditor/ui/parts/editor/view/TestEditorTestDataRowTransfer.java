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

/**
 * 
 * special DataTransfer for the DataRow of a TestScenarioPrameterTable.
 * 
 * @author llipinski
 */
public final class TestEditorTestDataRowTransfer extends TestEditorTransfer {
	private static TestEditorTestDataRowTransfer instance = new TestEditorTestDataRowTransfer();

	private static final String MYTYPENAME = "test_flow_data_transfer"; //$NON-NLS-1$
	private static final int MYTYPEID = registerType(MYTYPENAME);

	/**
	 * private constructor used by the own factory-method getInstance().
	 */
	private TestEditorTestDataRowTransfer() {
	}

	/**
	 * Returns the singleton instance of the TestFlowTransfer class.
	 * 
	 * @return the singleton instance of the TestFlowTransfer class
	 */
	public static TestEditorTestDataRowTransfer getInstance() {
		return instance;
	}

	@Override
	protected int[] getTypeIds() {
		return new int[] { MYTYPEID };
	}

	@Override
	protected String[] getTypeNames() {
		return new String[] { MYTYPENAME };
	}
}