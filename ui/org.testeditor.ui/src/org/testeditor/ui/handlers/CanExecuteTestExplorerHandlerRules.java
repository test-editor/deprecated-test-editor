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
package org.testeditor.ui.handlers;

import java.util.Iterator;

import javax.inject.Inject;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.testeditor.core.model.teststructure.ScenarioSuite;
import org.testeditor.core.model.teststructure.TestFlow;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestScenario;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.model.teststructure.TestSuite;
import org.testeditor.core.services.interfaces.TestExceutionEnvironmentService;
import org.testeditor.core.services.interfaces.TestScenarioService;

/**
 * 
 * Rule Class to be used in Handler of the Test Treeview. It checks that only
 * one Element is selected in the Treeview.
 * 
 */
public class CanExecuteTestExplorerHandlerRules {

	@Inject
	private TestScenarioService testScenarioService;

	@Inject
	private TestExceutionEnvironmentService testExecService;

	/**
	 * Check if this Handler is enabled on the selection. Only one Teststructure
	 * is valid as a selection.
	 * 
	 * @param selection
	 *            to check selection
	 * @return true if only one element is selected.
	 */
	public boolean canExecuteOnlyOneElementRule(IStructuredSelection selection) {
		if (selection.size() == 1 && !(selection.getFirstElement() instanceof TestProject
				&& ((TestProject) selection.getFirstElement()).getTestProjectConfig() == null)) {
			return true;
		}
		return false;
	}

	/**
	 * Check if this Handler is enabled on the selection. One or more
	 * Teststructure is valid as a selection.
	 * 
	 * @param selection
	 *            to check selection
	 * @return true if one or more elements are selected.
	 */
	public boolean canExecuteOnOneOrManyElementRule(IStructuredSelection selection) {
		if (selection != null && selection.size() > 0) {
			return true;
		}
		return false;
	}

	/**
	 * Rule to check that the first element of the selection is'nt the root.
	 * 
	 * @param selection
	 *            selection in testexplorer
	 * @return true the root is not in the selection
	 */
	public boolean canExecuteOnNoneRootRule(IStructuredSelection selection) {
		boolean isNoProjectTestStructure = true;

		Iterator<?> iterator = selection.iterator();
		while (iterator.hasNext()) {
			TestStructure ts = (TestStructure) iterator.next();
			if (ts.getParent() == null) {
				isNoProjectTestStructure = false;
				break;
			}
		}
		return isNoProjectTestStructure;
	}

	/**
	 * 
	 * @param selection
	 *            of the TestExplorer
	 * @return true if the selection in the TextExplorer is a TestSuite
	 */
	public boolean canExecuteOnTestSuiteRule(IStructuredSelection selection) {
		TestStructure ts = (TestStructure) selection.getFirstElement();
		return ts instanceof TestSuite;
	}

	/**
	 * 
	 * @param selection
	 *            selection of the TestExplorer
	 * @return true if the selection in the TextExplorer is a TestProject or the
	 *         TestExplorer is null.
	 */
	public boolean canExecuteOnTestProjectRule(IStructuredSelection selection) {
		boolean onlyTestProjects = true;
		if (selection.isEmpty()) {
			return false;
		}
		Iterator<?> iter = selection.iterator();
		while (iter.hasNext()) {
			TestStructure ts = (TestStructure) iter.next();
			if (!(ts instanceof TestProject)) {
				onlyTestProjects = false;
				break;
			}
			if (((TestProject) ts).getTestProjectConfig() == null) {
				return false;
			}
		}
		return onlyTestProjects;
	}

	/**
	 * 
	 * @param selection
	 *            TestExplorer
	 * @return true, if the selection in the TextExplorer is the suite named
	 *         TestEditorGlobalConstans.TEST_SCENARIO_SUITE
	 */
	public boolean canExecuteOnTestScenarienSuiteRule(IStructuredSelection selection) {
		Iterator<?> iter = selection.iterator();
		while (iter.hasNext()) {
			TestStructure ts = (TestStructure) iter.next();
			if (!(ts instanceof ScenarioSuite)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 
	 * @param selection
	 *            TestExplorer
	 * 
	 * @return true, if the selected element is a descendant of the
	 *         test-scenario-suite.
	 */
	public boolean canExecuteOnDescendantFromTestScenarioSuite(IStructuredSelection selection) {
		Iterator<?> iter = selection.iterator();
		while (iter.hasNext()) {
			TestStructure ts = (TestStructure) iter.next();
			if (testScenarioService.isDescendantFromTestScenariosSuite(ts)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * @param selection
	 *            TestExplorer
	 * @return true, if a selected element is the main-ScenarioSuite of the
	 *         project, else false
	 */
	public boolean canExecuteOnProjectMainScenarioSuite(IStructuredSelection selection) {
		Iterator<?> iter = selection.iterator();
		while (iter.hasNext()) {
			TestStructure ts = (TestStructure) iter.next();
			if (ts.getParent() != null && ts.getParent().equals(ts.getRootElement()) && ts instanceof ScenarioSuite) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * @param selection
	 *            testExplorer with selection to be checked.
	 * @return checks if the selection is a testscenario. if not it returns
	 *         true. In the other case it checks the usage of the scenario. If
	 *         the usage is 0 it returns true. false in the other case.
	 */
	public boolean canExecuteOnUnusedScenario(IStructuredSelection selection) {
		// boolean useageOFAllScenarios = false;
		// Iterator iterator = explorer.getSelection().iterator();
		// while (iterator.hasNext()) {
		// Object selElem = iterator.next();
		// if (selElem instanceof TestScenario) {
		// List<String> usageOfTestScenario =
		// testScenarioService.getUsedOfTestSceneario((TestScenario) selElem);
		// if (usageOfTestScenario.size() > 0) {
		// useageOFAllScenarios = true;
		// break;
		// }
		// }
		// }
		// return !useageOFAllScenarios;

		return true;
	}

	/**
	 * 
	 * @param selection
	 *            TestExplorer
	 * @return true if the selection in the TextExplorer is the suite named
	 *         TestKomponenten
	 */
	public boolean canExecuteOnTestScenarioRule(IStructuredSelection selection) {
		boolean onlyTestKomponents = true;
		Iterator<?> iter = selection.iterator();
		while (iter.hasNext()) {
			TestStructure ts = (TestStructure) iter.next();
			if (!(ts instanceof TestScenario)) {
				return false;
			}
		}
		return onlyTestKomponents;
	}

	/**
	 * 
	 * @param selection
	 *            of the TestExplorer
	 * @return true, if every TestProject of the selected TestStructures are
	 *         under version-control.
	 */
	public boolean canExecuteTeamShareApproveOrUpdate(IStructuredSelection selection) {
		Iterator<?> iter = selection.iterator();
		while (iter.hasNext()) {
			TestStructure ts = (TestStructure) iter.next();
			if (ts.getRootElement().getTestProjectConfig().getTeamShareConfig() == null) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 
	 * @param selection
	 *            of the TestExplorer
	 * @return if, no element of the selection is a ScenarioSuite with children,
	 *         else false.
	 */
	public boolean canExecuteOnNonScenarioSuiteParents(IStructuredSelection selection) {
		Iterator<?> iter = selection.iterator();
		while (iter.hasNext()) {
			TestStructure ts = (TestStructure) iter.next();
			if (ts instanceof ScenarioSuite && !((ScenarioSuite) ts).getTestChildren().isEmpty()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 
	 * @param selection
	 *            TestExplorer with selected elements
	 * @return true on instance of TestFlow otherwise false.
	 */
	public boolean canExecuteOnTestFlowRule(IStructuredSelection selection) {
		Iterator<?> iter = selection.iterator();
		while (iter.hasNext()) {
			TestStructure ts = (TestStructure) iter.next();
			if (!(ts instanceof TestFlow)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Can execute on this selection is determined. if the selection belongs to
	 * a test project with test environment it will return true.
	 * 
	 * @param selection
	 *            of the test explorer.
	 * @return true on selection with test agent.
	 */
	public boolean canExecuteOnTestStructureWithLaunchedTestExecutionEnvironment(IStructuredSelection selection) {
		if (selection.getFirstElement() instanceof TestStructure) {
			TestStructure ts = (TestStructure) selection.getFirstElement();
			return testExecService.isTestEnvironmentLaunchedFor(ts.getRootElement());
		}
		return false;
	}

}
