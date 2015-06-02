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
import org.testeditor.core.services.interfaces.TestScenarioService;
import org.testeditor.ui.parts.testExplorer.TestExplorer;

/**
 * 
 * Rule Class to be used in Handler of the Test Treeview. It checks that only
 * one Element is selected in the Treeview.
 * 
 */
public class CanExecuteTestExplorerHandlerRules {

	@Inject
	private TestScenarioService testScenarioService;

	/**
	 * Check if this Handler is enabled on the selection. Only one Teststructure
	 * is valid as a selection.
	 * 
	 * @param testExplorer
	 *            to check selection
	 * @return true if only one element is selected.
	 */
	public boolean canExecuteOnlyOneElementRule(TestExplorer testExplorer) {
		IStructuredSelection sel = testExplorer.getSelection();
		if (sel.size() == 1
				&& !(sel.getFirstElement() instanceof TestProject && ((TestProject) sel.getFirstElement())
						.getTestProjectConfig() == null)) {
			return true;
		}
		return false;
	}

	/**
	 * Check if this Handler is enabled on the selection. One or more
	 * Teststructure is valid as a selection.
	 * 
	 * @param testExplorer
	 *            to check selection
	 * @return true if one or more elements are selected.
	 */
	public boolean canExecuteOnOneOrManyElementRule(TestExplorer testExplorer) {
		if (testExplorer != null) {
			IStructuredSelection sel = testExplorer.getSelection();
			if (sel.size() > 0) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Rule to check that the first element of the selection is'nt the root.
	 * 
	 * @param explorer
	 *            TestExplorer
	 * @return true the root is not in the selection
	 */
	public boolean canExecuteOnNoneRootRule(TestExplorer explorer) {
		boolean isNoProjectTestStructure = true;

		Iterator<?> iterator = explorer.getSelection().iterator();
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
	 * @param explorer
	 *            TestExplorer
	 * @return true if the selection in the TextExplorer is a TestSuite
	 */
	public boolean canExecuteOnTestSuiteRule(TestExplorer explorer) {
		TestStructure ts = (TestStructure) explorer.getSelection().getFirstElement();
		return ts instanceof TestSuite;
	}

	/**
	 * 
	 * @param testExplorer
	 *            TestExplorer
	 * @return true if the selection in the TextExplorer is a TestProject or the
	 *         TestExplorer is null.
	 */
	public boolean canExecuteOnTestProjectRule(TestExplorer testExplorer) {
		IStructuredSelection selection = testExplorer.getSelection();
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
	 * @param testExplorer
	 *            TestExplorer
	 * @return true, if the selection in the TextExplorer is the suite named
	 *         TestEditorGlobalConstans.TEST_SCENARIO_SUITE
	 */
	public boolean canExecuteOnTestScenarienSuiteRule(TestExplorer testExplorer) {
		IStructuredSelection selection = testExplorer.getSelection();
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
	 * @param testExplorer
	 *            TestExplorer
	 * 
	 * @return true, if the selected element is a descendant of the
	 *         test-scenario-suite.
	 */
	public boolean canExecuteOnDescendantFromTestScenarioSuite(TestExplorer testExplorer) {
		IStructuredSelection selection = testExplorer.getSelection();
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
	 * @param testExplorer
	 *            TestExplorer
	 * @return true, if a selected element is the main-ScenarioSuite of the
	 *         project, else false
	 */
	public boolean canExecuteOnProjectMainScenarioSuite(TestExplorer testExplorer) {
		IStructuredSelection selection = testExplorer.getSelection();
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
	 * @param explorer
	 *            testExplorer with selection to be checked.
	 * @return checks if the selection is a testscenario. if not it returns
	 *         true. In the other case it checks the usage of the scenario. If
	 *         the usage is 0 it returns true. false in the other case.
	 */
	public boolean canExecuteOnUnusedScenario(TestExplorer explorer) {
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
	 * @param testExplorer
	 *            TestExplorer
	 * @return true if the selection in the TextExplorer is the suite named
	 *         TestKomponenten
	 */
	public boolean canExecuteOnTestScenarioRule(TestExplorer testExplorer) {
		IStructuredSelection selection = testExplorer.getSelection();
		boolean onlyTestKomponents = true;
		Iterator iter = selection.iterator();
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
	 * @param testExplorer
	 *            TestExplorer
	 * @return true, if every TestProject of the selected TestStructures are
	 *         under version-control.
	 */
	public boolean canExecuteTeamShareApproveOrUpdate(TestExplorer testExplorer) {
		IStructuredSelection selection = testExplorer.getSelection();
		Iterator iter = selection.iterator();
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
	 * @param explorer
	 *            TestExplorer
	 * @return if, no element of the selection is a ScenarioSuite with children,
	 *         else false.
	 */
	public boolean canExecuteOnNonScenarioSuiteParents(TestExplorer explorer) {
		IStructuredSelection selection = explorer.getSelection();
		Iterator iter = selection.iterator();
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
	 * @param explorer
	 *            TestExplorer with selected elements
	 * @return true on instance of TestFlow otherwise false.
	 */
	public boolean canExecuteOnTestFlowRule(TestExplorer explorer) {
		IStructuredSelection selection = explorer.getSelection();
		Iterator iter = selection.iterator();
		while (iter.hasNext()) {
			TestStructure ts = (TestStructure) iter.next();
			if (!(ts instanceof TestFlow)) {
				return false;
			}
		}
		return true;
	}
}
