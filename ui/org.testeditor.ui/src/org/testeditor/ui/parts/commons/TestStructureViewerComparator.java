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
package org.testeditor.ui.parts.commons;

import org.eclipse.jface.viewers.ViewerComparator;
import org.testeditor.core.model.teststructure.TestCase;
import org.testeditor.core.model.teststructure.TestScenario;
import org.testeditor.core.model.teststructure.TestSuite;
import org.testeditor.core.services.interfaces.TestEditorGlobalConstans;

/**
 * 
 * Comaparator to Sort Teststructures.
 * 
 */
public class TestStructureViewerComparator extends ViewerComparator {

	@Override
	public int category(Object element) {
		if (element.toString().equalsIgnoreCase(TestEditorGlobalConstans.TEST_KOMPONENTS)
				|| element.toString().equalsIgnoreCase(TestEditorGlobalConstans.TEST_SCENARIO_SUITE)) {
			return 3;
		} else if (element.getClass() == TestSuite.class) {
			return 1;
		} else if (element.getClass() == TestCase.class || element.getClass() == TestScenario.class) {
			return 2;
		}
		return super.category(element);
	}
}
