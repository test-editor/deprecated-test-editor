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
package org.testeditor.ui;

import org.testeditor.core.model.teststructure.TestStructure;

/**
 * 
 * TestStructure Editor which contains a TestStructure. Known implementations
 * for TestFlow, TestSuites and TestProjects.
 */
public interface ITestStructureEditor {

	/**
	 * 
	 * @return TestStructure of the Editor.
	 */
	TestStructure getTestStructure();

	/**
	 * closes the part.
	 */
	void closePart();

	/**
	 * 
	 * @param testStructure
	 *            to be set to the editor.
	 * 
	 */
	void setTestStructure(TestStructure testStructure);

}
