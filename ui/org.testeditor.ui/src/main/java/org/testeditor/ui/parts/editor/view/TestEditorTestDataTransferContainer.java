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

import java.io.Serializable;

/**
 * 
 * this class represents the transferobject for the selected TestComponents.
 * 
 * @author llipinski
 */
public class TestEditorTestDataTransferContainer implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2L;
	private String storedTestComponents;
	private String testProjectName;

	/**
	 * gets the stored TestComponents as a String.
	 * 
	 * @return the String representing the stored TestComponents
	 */
	public String getStoredTestComponents() {
		return storedTestComponents;
	}

	/**
	 * stores the testComponents as a String.
	 * 
	 * @param storedTestComponents
	 *            as a String
	 */
	public void setStoredTestComponents(String storedTestComponents) {
		this.storedTestComponents = storedTestComponents;
	}

	/**
	 * 
	 * @return the name of the TestProject
	 */
	public String getTestProjectName() {
		return testProjectName;
	}

	/**
	 * sets the name of the TestProject.
	 * 
	 * @param testProjectName
	 *            the name of the TestProject
	 */
	public void setTestProjectName(String testProjectName) {
		this.testProjectName = testProjectName;
	}

	/**
	 * 
	 * @return true, if there are TestComponents, else false.
	 */
	public boolean isEmpty() {
		return storedTestComponents.length() == 0;
	}
}
