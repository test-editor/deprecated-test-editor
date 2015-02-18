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
package org.testeditor.ui.wizardpages;

import java.util.ArrayList;
import java.util.List;

import org.testeditor.core.model.teststructure.TestCompositeStructure;
import org.testeditor.core.model.teststructure.TestStructure;

/**
 * this class is a cacheing-container for a parent TestStructure and their
 * children.
 * 
 * @author llipinski
 * 
 */
public class TestEditorChildrenOfParentContainer {
	private TestCompositeStructure testCompStructure;
	private List<TestStructure> children = new ArrayList<TestStructure>();

	/**
	 * constructor.
	 * 
	 * @param testCompStructure
	 *            TestCompositeStructure
	 * @param children
	 *            List<TestStructure>
	 */
	public TestEditorChildrenOfParentContainer(TestCompositeStructure testCompStructure, List<TestStructure> children) {
		this.testCompStructure = testCompStructure;
		this.children = children;
	}

	/**
	 * 
	 * @return the parent-TestStructure.
	 */
	public TestCompositeStructure getTestCompStructure() {
		return testCompStructure;
	}

	/**
	 * 
	 * @return the List of the children.
	 */
	public List<TestStructure> getChildren() {
		return children;
	}

}
