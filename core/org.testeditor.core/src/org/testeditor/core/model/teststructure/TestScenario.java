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
package org.testeditor.core.model.teststructure;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 
 * The TestScenario is a TestFlow with some parameters instead of some values in
 * some of the ActionGroup.
 * 
 */
public class TestScenario extends TestFlow implements Comparable<TestScenario> {

	private Set<String> includes = new HashSet<String>();

	@Override
	public String getPageType() {
		return "TESTSCENARIO";
	}

	@Override
	public String getTypeName() {
		return TestType.TESTSCENARIO.getName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getSourceCode() {
		StringBuilder includeLines = new StringBuilder("");
		StringBuilder sourceCode = new StringBuilder("");
		TestComponent previousComp = null;
		if (getTestComponents().isEmpty()) {
			return super.getSourceCode();
		}
		for (TestComponent testComponent : getTestComponents()) {
			if (testComponent instanceof TestScenarioParameters) {
				sourceCode.append("!|scenario |").append(getName()).append(" _|").append(testComponent.getSourceCode())
						.append("|");
			} else if (testComponent instanceof TestActionGroup && previousComp != null
					&& previousComp instanceof TestActionGroup
					&& ((TestActionGroup) testComponent).isStartOfSourceCodeEqual((TestActionGroup) previousComp)) {
				sourceCode.append(((TestActionGroup) testComponent).getTableSourcecode());
			} else if (testComponent instanceof TestScenarioParameterTable) {
				addComponentParameterTable(includeLines, sourceCode, testComponent);
			} else {
				sourceCode.append(testComponent.getSourceCode());

			}
			sourceCode.append("\n");
			previousComp = testComponent;
		}
		if (!includeLines.toString().isEmpty()) {
			return includeLines.append("\n").append(sourceCode).toString();

		}
		return sourceCode.toString();
	}

	/**
	 * adds a sourceCode for the TestScenarioParameterTable.
	 * 
	 * @param includeLines
	 *            StringBuilder
	 * @param sourceCode
	 *            StringBuilder
	 * @param testComponent
	 *            TestComponent
	 */
	private void addComponentParameterTable(StringBuilder includeLines, StringBuilder sourceCode,
			TestComponent testComponent) {
		if (!((TestScenarioParameterTable) testComponent).getInclude().isEmpty()) {
			includeLines.append(((TestScenarioParameterTable) testComponent).getInclude()).append("\n");
		}

		List<TestDataRow> dataRows = ((TestScenarioParameterTable) testComponent).getDataTable().getDataRows();

		sourceCode.append("|note|scenario|\n");

		String endsWithSemicolon = "";
		if (!dataRows.isEmpty()) {
			endsWithSemicolon = ";";
		}
		sourceCode.append("|").append(((TestScenarioParameterTable) testComponent).getTitle().replace(" ", ""))
				.append(endsWithSemicolon).append("|");

		if (!dataRows.isEmpty()) {
			String parameterString = dataRows.get(0).toString();
			if (parameterString.length() >= 1) {
				sourceCode.append(parameterString.substring(0, parameterString.length() - 1)).append("|");
			}
		}
	}

	/**
	 * adds a new include to the includes.
	 * 
	 * @param include
	 *            String
	 */
	public void addInclude(String include) {
		includes.add(include);
	}

	/**
	 * 
	 * @param include
	 *            String
	 * @return true, if the includes contains the include.
	 */
	public boolean cointainsInclude(String include) {
		return includes.contains(include);
	}

	/**
	 * 
	 * @param scenarioName
	 *            the name of the scenario called in a scenario.
	 * @return true, if the scenario is included, else false.
	 */
	public boolean isScenarioIncluded(String scenarioName) {
		for (String include : includes) {
			if (include.endsWith("." + scenarioName)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * @param scenarioName
	 *            the name of the scenario
	 * @return the include of the scenario
	 */
	public String getIncludeOfScenario(String scenarioName) {

		String scenarioNameWithoutSpace = scenarioName.replaceAll(" ", "");

		for (String include : includes) {
			if (include.endsWith("." + scenarioNameWithoutSpace)) {
				return include;
			}
		}
		return "";
	}

	/**
	 * returns the parameters.
	 * 
	 * @return ArrayList<String> including the parameters
	 */
	public ArrayList<String> getTestParameters() {
		List<TestComponent> components = getTestComponents();
		if (components.size() > 0) {
			for (TestComponent comp : components) {
				if (comp != null && comp instanceof TestScenarioParameters) {
					return ((TestScenarioParameters) comp).getParameters();
				}
			}
		}
		return new ArrayList<String>();
	}

	/**
	 * set the parameters.
	 * 
	 * @param testParameters
	 *            ArrayList<String>
	 */
	public void setTestParameters(ArrayList<String> testParameters) {
		List<TestComponent> components = getTestComponents();
		if (testScenarioParametersExists()) {
			((TestScenarioParameters) components.get(0)).setTexts(testParameters);
		} else {
			TestScenarioParameters testScenarioParameters = new TestScenarioParameters();
			testScenarioParameters.setTexts(testParameters);
			components.add(0, testScenarioParameters);
		}
	}

	/**
	 * checks the exist of the {@link TestComponent} of the type
	 * {@link TestScenarioParameters}.
	 * 
	 * @return true, if this component in the {@link TestScenario}
	 */
	public boolean testScenarioParametersExists() {
		List<TestComponent> components = getTestComponents();
		return components.size() > 0 && components.get(0) != null
				&& components.get(0) instanceof TestScenarioParameters;
	}

	/**
	 * adds a test parameter.
	 * 
	 * @param parameter
	 *            String testparameter
	 */
	public void addTestparameter(String parameter) {
		ArrayList<String> parameters = getTestParameters();
		if (!parameters.contains(parameter)) {
			parameters.add(parameter);
			setTestParameters(parameters);
		}
	}

	@Override
	public int compareTo(TestScenario testScenario) {
		return this.getFullName().compareTo(testScenario.getFullName());
	}

	@Override
	public TestDescription getNewTestDescription() {
		return new TestDescriptionTestScenario();
	}
}
