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

import javax.inject.Inject;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.testeditor.core.model.teststructure.ScenarioSuite;
import org.testeditor.core.services.interfaces.TestScenarioService;

/**
 * 
 * Filter for the creation of children of the TestScenariosSuite.
 * 
 */
public class TestScenarioFilter extends ViewerFilter {

	@Inject
	private TestScenarioService testScenarioService;

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		return !(element instanceof ScenarioSuite && testScenarioService.isSuiteForScenarios((ScenarioSuite) element));
	}

}
