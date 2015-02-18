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
package org.testeditor.core.services.interfaces;

import java.util.List;

import org.testeditor.core.model.action.ActionGroup;
import org.testeditor.core.model.action.Argument;
import org.testeditor.core.model.action.IAction;
import org.testeditor.core.model.action.TechnicalBindingType;
import org.testeditor.core.model.teststructure.TestActionGroup;
import org.testeditor.core.model.teststructure.TestActionGroupTestCase;
import org.testeditor.core.model.teststructure.TestActionGroupTestScenario;
import org.testeditor.core.model.teststructure.TestFlow;
import org.testeditor.core.model.teststructure.TestProject;

/**
 * Provides read services regarding the project specific XML library. This
 * contains the meta information for available masks and available test steps.
 * For each project the XML configuration file is located at the local workspace
 * of the Test-Editor (e.g. at $userHome/.testeditor).
 */
public interface ActionGroupService {

	/**
	 * Returns the whole structure of available action groups (e.g. UI masks)
	 * for the given project.
	 * 
	 * @param testProject
	 *            the current project (e.g. object for "DemoWebTests")
	 * @return list of action groups (e.g. objects for "Common Browser, "Login")
	 */
	List<ActionGroup> getActionGroups(TestProject testProject);

	/**
	 * Returns a specific action group (e.g. UI mask) for a given name (e.g. the
	 * mask name) and project.
	 * 
	 * @param testProject
	 *            the current project (e.g. object for "DemoWebTests")
	 * @param name
	 *            name of the group (e.g. "Login")
	 * @return action group if found, otherwise <code>null</code>
	 */
	ActionGroup getActionGroup(TestProject testProject, String name);

	/**
	 * Returns a list of technical binding types for a given name (e.g. the mask
	 * name) and project.
	 * 
	 * @param testProject
	 *            the current project (e.g. object for "DemoWebTests")
	 * @param name
	 *            name of the group (e.g. "Login")
	 * @return technical binding types (e.g. "TYPE_INPUT", "CLICK_BUTTON") if
	 *         found, otherwise <code>null</code>
	 */
	List<TechnicalBindingType> getTechnicalBindingTypes(TestProject testProject, String name);

	/**
	 * Returns the action by a given uncoded String line (e.g. a FitNesse table
	 * column separated by pipes) and project.
	 * 
	 * @param testProject
	 *            the current project (e.g. object for "DemoWebTests")
	 * @param actionGroup
	 *            related group (e.g. "Login" mask)
	 * @param line
	 *            line in clear code (e.g. |insert|123|into|user name|field|)
	 * @param arguments
	 *            List<Arguments> arguments
	 * @param isTestActionGroupTestScenario
	 *            true, if it's a scenario, else false
	 * @return action (e.g. action for the insert into field)
	 */
	IAction getActionByLine(TestProject testProject, ActionGroup actionGroup, String line, List<Argument> arguments,
			boolean isTestActionGroupTestScenario);

	/**
	 * creates a testactiongroup out of the input in the ui.
	 * 
	 * @param testProject
	 *            TestProject
	 * @param actionGroupName
	 *            name of the actiongroup
	 * @param inputTexts
	 *            parts of the input line
	 * @param arguments
	 *            arguments of the input line
	 * @return TestActionGroup
	 */
	TestActionGroup createTestActionGroup(TestProject testProject, String actionGroupName, List<String> inputTexts,
			List<Argument> arguments);

	/**
	 * creates a {@link TestActionGroupTestCase} out of the input in the ui.
	 * 
	 * @param testProject
	 *            TestProject
	 * @param actionGroupName
	 *            name of the actiongroup
	 * @param inputLineParts
	 *            parts of the input line
	 * @param arguments
	 *            arguments of the input line
	 * @return TestActionGroupTestCase
	 * 
	 */
	TestActionGroupTestCase createTestActionGroupTestCase(TestProject testProject, String actionGroupName,
			List<String> inputLineParts, List<Argument> arguments);

	/**
	 * creates a {@link TestActionGroupTestCase} out of the input in the ui.
	 * 
	 * @param testProject
	 *            TestProject
	 * @param actionGroupName
	 *            name of the actiongroup
	 * @param inputLineParts
	 *            parts of the input line
	 * @param arguments
	 *            arguments of the input line
	 * @return TestActionGroupTestScenario
	 * 
	 */
	TestActionGroupTestScenario createTestActionGroupTestScenario(TestProject testProject, String actionGroupName,
			List<String> inputLineParts, List<Argument> arguments);

	/**
	 * @param testProject
	 *            the current project (e.g. object for "DemoWebTests")
	 * @param actionGroupName
	 *            name of the group (e.g. "Login")
	 * @param technicalBindingType
	 *            String name of the technichalBindingType
	 * @return the technicalBindingType with the name of the parameter
	 */
	TechnicalBindingType getTechnicalBindingByName(TestProject testProject, String actionGroupName,
			String technicalBindingType);

	/**
	 * adds a actionLine to the TestActionGroup.
	 * 
	 * @param testActionGroup
	 *            TestActionGroup
	 * @param testFlow
	 *            TestFlow
	 * @param line
	 *            ActionLine as String
	 */
	void addActionLine(TestActionGroup testActionGroup, TestFlow testFlow, String line);

}
