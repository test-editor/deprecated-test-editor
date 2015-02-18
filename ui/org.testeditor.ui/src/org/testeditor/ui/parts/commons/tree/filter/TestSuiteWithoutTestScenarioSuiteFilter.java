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
package org.testeditor.ui.parts.commons.tree.filter;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.testeditor.core.model.teststructure.ScenarioSuite;
import org.testeditor.core.model.teststructure.TestSuite;

/**
 * 
 * Filter to ignore all TestCases, but keep all
 * CustomWidgetIdConstants.TEST_KOMPONENTEN in a Tree.
 * 
 * @author llipinski
 */
public class TestSuiteWithoutTestScenarioSuiteFilter extends ViewerFilter {

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		return !(element instanceof TestSuite) || (element instanceof ScenarioSuite);

	}
}
