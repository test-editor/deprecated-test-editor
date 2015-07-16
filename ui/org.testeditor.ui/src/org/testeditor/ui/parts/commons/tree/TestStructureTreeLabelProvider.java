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
package org.testeditor.ui.parts.commons.tree;

import javax.inject.Inject;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.testeditor.core.model.team.TeamChangeType;
import org.testeditor.core.model.testresult.TestResult;
import org.testeditor.core.model.teststructure.BrokenTestStructure;
import org.testeditor.core.model.teststructure.ScenarioSuite;
import org.testeditor.core.model.teststructure.TestCase;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestProjectConfig;
import org.testeditor.core.model.teststructure.TestScenario;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.model.teststructure.TestSuite;
import org.testeditor.core.services.interfaces.TeamShareStatusServiceNew;
import org.testeditor.core.services.interfaces.TestProjectService;
import org.testeditor.core.util.TestStateProtocolService;
import org.testeditor.ui.constants.IconConstants;

/**
 * 
 * This class provides the labels for the testEditortree.
 * 
 */
public class TestStructureTreeLabelProvider extends LabelProvider implements ILabelProvider {

	@Inject
	private TestStateProtocolService testProtocolService;

	@Inject
	private TeamShareStatusServiceNew teamShareStatusService;

	private boolean showFullName = false;

	/**
	 * Sets the state of the label provider, to show the name or the full name
	 * of a teststructure. setting this property to true, will show the full
	 * name, otherwise the name only is shown.
	 * 
	 * Examples:
	 * <ul>
	 * <li>name: TestCase</li>
	 * <li>full name: TestProject.TestSuite.TestCase</li>
	 * </ul>
	 * 
	 * Default is the name.
	 * 
	 * @param showFullName
	 *            Property
	 */
	public void setShowFullName(boolean showFullName) {
		this.showFullName = showFullName;
	}

	@Override
	public Image getImage(Object element) {
		if (element instanceof TestProject) {
			return getTestProjectImage((TestProject) element);
		} else if (element instanceof TestCase) {
			return getTestCaseImage((TestCase) element);
		} else if (element instanceof TestScenario) {
			return getTestScenarioImage((TestScenario) element);
		} else if (element instanceof ScenarioSuite) {
			return getScnearioSuiteImage((ScenarioSuite) element);
		} else if (element instanceof BrokenTestStructure) {
			return IconConstants.ICON_UNPARSED_LINE;
		}
		// Return default icon
		return getTestSuiteImage((TestSuite) element);
	}

	/**
	 * @param testSuite
	 *            TestSuite
	 * @return Image
	 */
	private Image getTestSuiteImage(TestSuite testSuite) {

		if (teamShareStatusService.isModified(testSuite)) {
			return IconConstants.ICON_TESTSUITE_MODIFIED;

		}

		return IconConstants.ICON_TESTSUITE;
	}

	/**
	 * @param scenarioSuite
	 *            ScenarioSuite
	 * @return Image
	 */
	private Image getScnearioSuiteImage(ScenarioSuite scenarioSuite) {

		if (teamShareStatusService.isModified(scenarioSuite)) {
			return IconConstants.ICON_SCENARIOSUITE_MODIFIED;

		}
		return IconConstants.ICON_SCENARIOSUITE;
	}

	/**
	 * @param testScenario
	 *            TestScenario
	 * @return image
	 */
	private Image getTestScenarioImage(TestScenario testScenario) {
		if (teamShareStatusService.isModified(testScenario)) {
			return IconConstants.ICON_SCENARIO_MODIFIED;

		}
		return IconConstants.ICON_SCENARIO;

	}

	/**
	 * return the Image of the TestCase.
	 * 
	 * @param testCase
	 *            TestCase
	 * @return Image
	 */
	private Image getTestCaseImage(TestCase testCase) {
		if (testProtocolService == null) {
			return null;
		}
		TestResult testResult = testProtocolService.get(testCase);

		TestProjectConfig testProjectConfig = testCase.getRootElement().getTestProjectConfig();
		if (testProjectConfig.isTeamSharedProject()) {

			if (testResult != null) {
				if (teamShareStatusService.isModified(testCase)) {
					if (testResult.isSuccessfully()) {
						return IconConstants.ICON_TESTCASE_SUCCESSED_MODIFIED;
					} else {
						return IconConstants.ICON_TESTCASE_FAILED_MODIFIED;
					}
				}

			} else {
				if (teamShareStatusService.isModified(testCase)) {
					return IconConstants.ICON_TESTCASE_MODIFIED;

				} else {
					return IconConstants.ICON_TESTCASE;
				}
			}

		} else {
			if (testResult != null) {
				if (testResult.isSuccessfully()) {
					return IconConstants.ICON_TESTCASE_SUCCESSED;
				} else {
					return IconConstants.ICON_TESTCASE_FAILED;
				}
			}
		}

		return IconConstants.ICON_TESTCASE;

	}

	/**
	 * return the Image for the TestProject.
	 * 
	 * @param testProject
	 *            TestProject
	 * @return Image
	 */
	private Image getTestProjectImage(TestProject testProject) {
		if (testProject.getTestProjectConfig() == null
				|| testProject.getTestProjectConfig().getProjectConfigVersion()
						.equals(TestProjectService.UNSUPPORTED_CONFIG_VERSION)) {
			return IconConstants.ICON_WARNING_SMALL;
		}
		TestProjectConfig testProjectConfig = testProject.getTestProjectConfig();
		if (testProjectConfig.isTeamSharedProject()) {

			if (teamShareStatusService.isModified(testProject)) {
				return IconConstants.ICON_SHARED_PROJECT_MODIFIED;
			} else {
				return IconConstants.ICON_SHARED_PROJECT;
			}

		} else {
			return IconConstants.ICON_PROJECT;
		}
	}

	@Override
	public String getText(Object element) {
		if (showFullName) {
			return ((TestStructure) element).getFullName();
		}
		if (element instanceof TestProject) {
			TestProject tp = (TestProject) element;
			int updates = testProtocolService.getAvailableUpdatesFor(tp);
			if (updates > 0) {
				return tp.toString() + " ↓ " + updates;
			}
		}
		return super.getText(element);
	}

}
