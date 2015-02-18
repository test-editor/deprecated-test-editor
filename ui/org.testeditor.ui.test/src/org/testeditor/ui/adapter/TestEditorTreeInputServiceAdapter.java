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
package org.testeditor.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.services.interfaces.TestStructureTreeInputService;

/**
 * Mockup for a TestEditorTreeInputService.
 * 
 * @author llipinski
 * 
 */
public class TestEditorTreeInputServiceAdapter implements TestStructureTreeInputService {

	private List<TestStructure> testStructures = new ArrayList<TestStructure>();

	@Override
	public List<TestStructure> getElements() throws SystemException {
		return testStructures;
	}

	/**
	 * setter for the testCompoositeStructures.
	 * 
	 * @param testStructures
	 *            List<TestStructure>
	 */
	public void setTestStructures(List<TestStructure> testStructures) {
		this.testStructures = testStructures;
	}

}
