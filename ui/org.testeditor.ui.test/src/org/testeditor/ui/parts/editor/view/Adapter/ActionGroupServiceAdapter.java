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
package org.testeditor.ui.parts.editor.view.Adapter;

import java.util.List;

import org.testeditor.core.model.action.Action;
import org.testeditor.core.model.action.ActionGroup;
import org.testeditor.core.model.action.Argument;
import org.testeditor.core.model.action.ProjectActionGroups;
import org.testeditor.core.model.action.TechnicalBindingType;
import org.testeditor.core.model.teststructure.TestActionGroup;
import org.testeditor.core.model.teststructure.TestActionGroupTestCase;
import org.testeditor.core.model.teststructure.TestActionGroupTestScenario;
import org.testeditor.core.model.teststructure.TestFlow;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.services.interfaces.ActionGroupService;

/**
 * a mockup for the ActionGroupService.
 * 
 */
public class ActionGroupServiceAdapter implements ActionGroupService {

	@Override
	public List<ActionGroup> getActionGroups(TestProject testProject) {
		return null;
	}

	@Override
	public ActionGroup getActionGroup(TestProject testProject, String name) {
		return null;
	}

	@Override
	public List<TechnicalBindingType> getTechnicalBindingTypes(TestProject testProject, String name) {
		return null;
	}

	@Override
	public TestActionGroup createTestActionGroup(TestProject testProject, String actionGroupName,
			List<String> inputLineParts, List<Argument> arguments) {
		return null;
	}

	@Override
	public TestActionGroupTestCase createTestActionGroupTestCase(TestProject testProject, String actionGroupName,
			List<String> inputLineParts, List<Argument> arguments) {
		TestActionGroupTestCase testActionGroupTestCase = new TestActionGroupTestCase();
		testActionGroupTestCase.addActionLine(new Action());
		return testActionGroupTestCase;
	}

	@Override
	public TestActionGroupTestScenario createTestActionGroupTestScenario(TestProject testProject,
			String actionGroupName, List<String> inputLineParts, List<Argument> arguments) {
		TestActionGroupTestScenario testActionGroupTestScenario = new TestActionGroupTestScenario();
		testActionGroupTestScenario.addActionLine(new Action());
		return testActionGroupTestScenario;
	}

	@Override
	public TechnicalBindingType getTechnicalBindingByName(TestProject testProject, String actionGroupName,
			String technicalBindingType) {
		return null;
	}

	@Override
	public void addActionLine(TestActionGroup testActionGroup, TestFlow testFlow, String line) {

	}

	@Override
	public void addProjectActionGroups(ProjectActionGroups projectsActionGroups) {

	}

}
