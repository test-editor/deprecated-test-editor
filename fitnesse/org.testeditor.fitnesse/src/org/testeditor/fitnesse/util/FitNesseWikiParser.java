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
package org.testeditor.fitnesse.util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.osgi.framework.FrameworkUtil;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.model.teststructure.BrokenTestStructure;
import org.testeditor.core.model.teststructure.TestActionGroup;
import org.testeditor.core.model.teststructure.TestActionGroupTestCase;
import org.testeditor.core.model.teststructure.TestActionGroupTestScenario;
import org.testeditor.core.model.teststructure.TestComponent;
import org.testeditor.core.model.teststructure.TestDataEvaluationReturnList;
import org.testeditor.core.model.teststructure.TestDataRow;
import org.testeditor.core.model.teststructure.TestDescription;
import org.testeditor.core.model.teststructure.TestFlow;
import org.testeditor.core.model.teststructure.TestInvisibleContent;
import org.testeditor.core.model.teststructure.TestScenario;
import org.testeditor.core.model.teststructure.TestScenarioParameterTable;
import org.testeditor.core.model.teststructure.TestScenarioParameters;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.model.teststructure.TestSuite;
import org.testeditor.core.services.interfaces.ActionGroupService;
import org.testeditor.core.services.interfaces.TestEditorPlugInService;
import org.testeditor.core.services.interfaces.TestScenarioService;

/**
 * Parser for FitNesse Wiki pages (with | notation).
 */
public class FitNesseWikiParser {

	private static final Logger LOGGER = Logger.getLogger(FitNesseWikiParser.class);

	private static final String SCENARIO_INCLUDE = "!include <";

	private static final String SCENARIO_PRAEAMBLE = "|note|scenario|";

	private static final String SCENARIO_FINAL = "'''End Scenario Include: ";

	@Inject
	private TestEditorPlugInService testEditorPluginService;

	@Inject
	private ActionGroupService actionGroupService;

	/**
	 * Parses a wiki page in raw format with | to the internal object structure.
	 * 
	 * @param testFlow
	 *            {@link TestFlow}
	 * @param content
	 *            wiki page content
	 * @return test component of this page
	 * @throws SystemException
	 *             is thrown in case of invalid input or parsing errors
	 */
	public LinkedList<TestComponent> parse(TestFlow testFlow, String content) throws SystemException {
		LinkedList<TestComponent> testComponents = new LinkedList<TestComponent>();
		StringTokenizer stringTokenizer = new StringTokenizer(content, "\n");
		Boolean firstRow = true;
		IEclipseContext context = EclipseContextFactory.getServiceContext(FrameworkUtil.getBundle(getClass())
				.getBundleContext());

		while (stringTokenizer.hasMoreTokens()) {
			String line = stringTokenizer.nextToken();

			// if first row sets the meta data to a invisible content object
			if (firstRow && line.equals("!contents")) {
				TestInvisibleContent testInvisibleContent = new TestInvisibleContent();
				testInvisibleContent.setSourceCode(line);
				testComponents.add(testInvisibleContent);
			} else if (line.equalsIgnoreCase(SCENARIO_PRAEAMBLE) && testFlow instanceof TestScenario) {
				addScenario(testFlow, testComponents, stringTokenizer);

			}
			// if line is part of a script table
			else if (lineIsElementOfActionGroup(line)) {
				addTestActionGroup(testFlow, testComponents, context, line);
			} else if (line.startsWith("!|scenario")) {
				addScenarioParameter(testComponents, line);
			}
			// this include may be reference to a scenario.
			else if (line.startsWith(SCENARIO_INCLUDE)) {
				parseScenarioInclude(testFlow, testComponents, stringTokenizer, line);
				// if line is a plain comment or an unknown action (e.g. import)
			} else if (line.startsWith("!|")) {
				addScenarioInScenario(testFlow, testComponents, stringTokenizer, line);
			} else {
				addDescriptionLine(testFlow, testComponents, line);
			}
		}
		return testComponents;
	}

	/**
	 * Adds a scenario inside a scenario. The fitnesse syntax of an scenario in
	 * scenario must be handled in special way.
	 * 
	 * @param testFlow
	 *            TestFlow
	 * @param testComponents
	 *            LinkedList<TestComponent>
	 * @param stringTokenizer
	 *            the StringTokenizer of the hole contents.
	 * @param line
	 *            this line must be the scenario name e.g. !|Test Scenario|
	 * @throws SystemException
	 *             if, scenario is not found.
	 */
	private void addScenarioInScenario(TestFlow testFlow, LinkedList<TestComponent> testComponents,
			StringTokenizer stringTokenizer, String line) throws SystemException {
		StringBuilder scenarioStringBuider = new StringBuilder();
		String nextElement = stringTokenizer.nextToken();
		String scenarioName;
		boolean isSimpleScenarioStatment = false;

		// this is a scenario without a parameter
		if (line.equals("!|script|")) {
			isSimpleScenarioStatment = true;
			scenarioStringBuider.append(line).append("\n");
			line = "!" + nextElement;
			nextElement = stringTokenizer.nextToken();
		}

		scenarioName = line.substring(2, line.length() - 1).replace(" ", "");
		String includeOfScenario = ((TestScenario) testFlow).getIncludeOfScenario(scenarioName);

		try {
			scenarioStringBuider.append(line).append("\n");
			scenarioStringBuider.append(nextElement).append("\n");

			// now we add the headers of the paramtertable and the first
			// parameter-line
			if (!isSimpleScenarioStatment) {
				String paramLine = stringTokenizer.nextToken();
				if (!paramLine.equals("#")) {
					scenarioStringBuider.append(paramLine);
					String temp = stringTokenizer.nextToken();
					while (!temp.equals("#")) {
						temp = stringTokenizer.nextToken();
					}
				} else {
					String[] lineTemp = nextElement.split("\\|");
					for (int count = 0; count < lineTemp.length; count++) {
						scenarioStringBuider.append("|");
					}
					scenarioStringBuider.append("\n");
				}
			}

			StringTokenizer inputTokenizer = new StringTokenizer(scenarioStringBuider.toString(), "\n");
			addTestScenarioParameterTable(testFlow, testComponents, inputTokenizer, includeOfScenario,
					isAScenarioInclude(testFlow, includeOfScenario.substring(10)));

		} catch (StringIndexOutOfBoundsException e) {

			if (includeOfScenario.isEmpty()) {
				LOGGER.error(testFlow.getFullName() + ": Include for the scenario with the name: " + scenarioName
						+ " does not exist !", e);
			} else {
				LOGGER.error(e);
			}
		}
	}

	/**
	 * adds a scenario.
	 * 
	 * @param testFlow
	 *            TestFlow
	 * @param testComponents
	 *            LinkedList<TestComponent>
	 * @param stringTokenizer
	 *            the StringTokenizer of the hole contents.
	 * @throws SystemException
	 *             if, scenario is not found.
	 */
	private void addScenario(TestFlow testFlow, LinkedList<TestComponent> testComponents,
			StringTokenizer stringTokenizer) throws SystemException {
		String nextElement = stringTokenizer.nextToken();
		StringTokenizer scenarioTokenizer = new StringTokenizer(nextElement, "|");
		if (scenarioTokenizer.hasMoreTokens()) {
			String scenarioName = scenarioTokenizer.nextToken();
			String includeOfScenario = ((TestScenario) testFlow).getIncludeOfScenario(scenarioName.split(";")[0]);
			StringBuilder scenarioStringBuider = new StringBuilder();
			String[] scenarioNameSplittedByCapitals = scenarioName.split("(?=[A-Z])");
			StringBuilder scenarioNameWithSpaces = buildScenarioNameWithSpaces(scenarioNameSplittedByCapitals);

			try {
				TestScenarioService testScenarioService = testEditorPluginService.getTestScenarioService(testFlow
						.getRootElement().getTestProjectConfig().getTestServerID());

				TestScenario scenarioByFullName = testScenarioService.getScenarioByFullName(testFlow.getRootElement(),
						includeOfScenario.substring(10));

				List<String> params = new ArrayList<String>();
				// scenarioByFullName can be null if scenario was not found
				if (scenarioByFullName != null) {
					params = scenarioByFullName.getTestParameters();
				}

				if (!params.isEmpty()) {
					scenarioStringBuider.append("!|")
							.append(scenarioNameWithSpaces.substring(1, scenarioNameWithSpaces.length() - 1))
							.append("|\n");
					for (String param : params) {
						scenarioStringBuider.append("|").append(param);
					}
					scenarioStringBuider.append("|\n");
					scenarioStringBuider.append(nextElement.substring(scenarioName.length() + 1)).append("\n");

				} else {
					scenarioStringBuider.append("!|script|\n|");
					scenarioStringBuider.append(
							scenarioNameWithSpaces.substring(1, scenarioNameWithSpaces.length() - 1)).append("|\n");
				}

				StringTokenizer inputTokenizer = new StringTokenizer(scenarioStringBuider.toString(), "\n");

				addTestScenarioParameterTable(testFlow, testComponents, inputTokenizer, includeOfScenario,
						isAScenarioInclude(testFlow, includeOfScenario.substring(10)));

			} catch (StringIndexOutOfBoundsException e) {

				if (includeOfScenario.isEmpty()) {
					LOGGER.error(testFlow.getFullName() + ": Include for the scenario with the name: " + scenarioName
							+ " does not exist !", e);
				} else {
					LOGGER.error(e);
				}
			}
		}
	}

	/**
	 * @param scenarioNameSplittedByCapitals
	 *            array of scenario names.
	 * @return stringBuilder with the scenario names separated by spaces.
	 */
	private StringBuilder buildScenarioNameWithSpaces(String[] scenarioNameSplittedByCapitals) {
		StringBuilder scenarioNameWithSpaces = new StringBuilder();
		for (String part : scenarioNameSplittedByCapitals) {
			scenarioNameWithSpaces.append(part.trim()).append(" ");
		}
		return scenarioNameWithSpaces;
	}

	/**
	 * generates the TestScenarioParameterTable.
	 * 
	 * @param testFlow
	 *            TestFlow
	 * @param testComponents
	 *            LinkedList<TestComponent>
	 * @param stringTokenizer
	 *            StringTokenizer
	 * @param line
	 *            actual line
	 * @throws SystemException
	 *             is thrown in case of invalid input or parsing errors
	 */
	private void parseScenarioInclude(TestFlow testFlow, LinkedList<TestComponent> testComponents,
			StringTokenizer stringTokenizer, String line) throws SystemException {
		if (testFlow instanceof TestScenario) {
			((TestScenario) testFlow).addInclude(line);
		} else {
			addTestScenarioParameterTable(testFlow, testComponents, stringTokenizer, line,
					isAScenarioInclude(testFlow, line.substring(10)));
		}
	}

	/**
	 * test the line, if is an element of an {@link ActionGroup} the return
	 * true, else false.
	 * 
	 * @param line
	 *            the line of the {@link TestFlow}
	 * @return true if the line an actionGroup, else false
	 */
	private boolean lineIsElementOfActionGroup(String line) {
		return (line.startsWith("-!|") || line.startsWith("|") || line.startsWith("# Maske") || line.startsWith("\n"))
				&& !line.startsWith("|note|Description:");
	}

	/**
	 * this method adds a {@link TestScenarioParameterTable} to the
	 * testcomponents.
	 * 
	 * @param testFlow
	 *            TestFlow
	 * @param testComponents
	 *            LinkedList<TestComponent>
	 * @param stringTokenizer
	 *            StringTokenizer
	 * @param includeLine
	 *            String
	 * @param isIncluedProjectScenario
	 *            boolean
	 * @throws SystemException
	 *             is thrown in case of invalid input or parsing errors
	 */
	private void addTestScenarioParameterTable(TestFlow testFlow, LinkedList<TestComponent> testComponents,
			StringTokenizer stringTokenizer, String includeLine, boolean isIncluedProjectScenario)
			throws SystemException {
		if (stringTokenizer.hasMoreTokens()) {
			String nextLine = stringTokenizer.nextToken();
			TestScenarioParameterTable parameterTable = new TestScenarioParameterTable();
			parameterTable.setIncludeLong(includeLine);
			parameterTable.setScenarioOfProject(isIncluedProjectScenario);
			testComponents.add(parameterTable);
			if (nextLine.startsWith("!|script|")) {
				parameterTable.setSimpleScriptStatement(true);
				if (stringTokenizer.hasMoreTokens()) {
					nextLine = stringTokenizer.nextToken().replaceAll(";", "");
					if (nextLine.startsWith("|" + parameterTable.getTitleOutOfInclude() + "|")
							|| nextLine.startsWith("|" + parameterTable.getName() + "|")) {
						parameterTable.setTitle(parameterTable.getTitleOutOfInclude());
					}
				} else {
					return;
				}
			}
			nextLine = nextLine.replaceAll(";", "");
			if (nextLine.startsWith("!|") && nextLine.equals("!|" + parameterTable.getTitleOutOfInclude() + "|")
					|| nextLine.startsWith("!|" + parameterTable.getName() + "|")) {
				parameterTable.setTitle(parameterTable.getTitleOutOfInclude());
			}
			while (stringTokenizer.hasMoreElements()) {
				nextLine = stringTokenizer.nextToken();
				if (nextLine.startsWith("|") && !nextLine.startsWith("|note")) {
					parameterTable.addParameterLine(nextLine);
					if (parameterTable.getDataTable().getRowCounts() == 1) {
						checkIsParamtableCorrect(testFlow, parameterTable, includeLine);
					}
				} else if (nextLineIsEndOfParameterTable(testFlow, testComponents, nextLine, parameterTable.getTitle())) {
					return;
				}
			}
		}
	}

	/**
	 * checking the columns in the table against the parameters of the scenario.
	 * 
	 * @param testFlow
	 *            the TestFlow
	 * @param parameterTable
	 *            the actual TestScenarioParameterTable
	 * @param include
	 *            the include-String
	 * @throws SystemException
	 *             on checking the columns in the table against the parameters
	 *             of the scenario
	 * 
	 */
	private void checkIsParamtableCorrect(TestFlow testFlow, TestScenarioParameterTable parameterTable, String include)
			throws SystemException {
		TestScenarioService testScenarioService = testEditorPluginService.getTestScenarioService(testFlow
				.getRootElement().getTestProjectConfig().getTestServerID());

		TestScenario scenarioFromProject = testScenarioService.getScenarioByFullName(testFlow.getRootElement(),
				include.substring(10));
		TestDataRow parametersOfScenario = new TestDataRow();
		if (scenarioFromProject != null) {
			for (String param : scenarioFromProject.getTestParameters()) {
				parametersOfScenario.add(param);
			}
			TestScenarioParameterTable parameterTabelCreatedOutOfScenario = new TestScenarioParameterTable();
			parameterTabelCreatedOutOfScenario.getDataTable().addRow(parametersOfScenario);
			TestDataEvaluationReturnList testDataEvaluationReturnList = parameterTable.getDataTable()
					.validateTableAgainstTable(parameterTabelCreatedOutOfScenario);
			parameterTable.setTestDataEvaluationReturnList(testDataEvaluationReturnList);
		}
	}

	/**
	 * 
	 * @param testFlow
	 *            TestFlow
	 * @param testComponents
	 *            LinkedList<TestComponent>
	 * @param nextLine
	 *            next line of the testflow
	 * @param pageTitle
	 *            titel title of the scenario.
	 * @return true, if nextLine starts with ',*,||
	 * @throws SystemException
	 *             is thrown in case of invalid input or parsing errors
	 */
	private boolean nextLineIsEndOfParameterTable(TestFlow testFlow, LinkedList<TestComponent> testComponents,
			String nextLine, String pageTitle) throws SystemException {
		if (isADescriptionLine(nextLine)) {
			addDescriptionLine(testFlow, testComponents, nextLine);
			return true;
		}
		if (lineIsElementOfActionGroup(nextLine)) {
			addTestActionGroup(testFlow, testComponents,
					EclipseContextFactory.getServiceContext(FrameworkUtil.getBundle(getClass()).getBundleContext()),
					nextLine);
			return true;
		}
		if (nextLine.startsWith(SCENARIO_FINAL + pageTitle) || nextLine.startsWith("#") || nextLine.startsWith("*")
				|| nextLine.startsWith("||") || nextLine.startsWith("\n") || isADescriptionLine(nextLine)) {
			return true;
		}
		return false;

	}

	/**
	 * this method checks by using the {@link TestScenarioService} the
	 * parameter, if its a link to a scenario it returns true.
	 * 
	 * @param testFlow
	 *            {@link TestFlow}
	 * @param linkString
	 *            the link to a file
	 * @return true, if its a link to a scenario, else false
	 * @throws SystemException
	 *             by reading the scenario
	 */
	private boolean isAScenarioInclude(TestFlow testFlow, String linkString) throws SystemException {
		TestScenarioService testScenarioService = testEditorPluginService.getTestScenarioService(testFlow
				.getRootElement().getTestProjectConfig().getTestServerID());
		return testScenarioService.isLinkToScenario(testFlow.getRootElement(), linkString);
	}

	/**
	 * adds the scenario parameters.
	 * 
	 * @param testComponents
	 *            testComponents
	 * @param line
	 *            the line after the tag !|scenario ..
	 */
	private void addScenarioParameter(LinkedList<TestComponent> testComponents, String line) {
		TestScenarioParameters scenarioParameters = new TestScenarioParameters();
		String[] partsOfLine = line.split("\\|");
		if (partsOfLine.length > 3) {
			String parametersOfLine = partsOfLine[3];
			String[] parameters = parametersOfLine.split(",");
			ArrayList<String> parameterArrayList = new ArrayList<String>();

			for (int i = 0; i < parameters.length; i++) {
				parameterArrayList.add(parameters[i].trim());
			}

			scenarioParameters.setTexts(parameterArrayList);
			testComponents.add(scenarioParameters);
		}

	}

	/**
	 * this method adds a actionLine.
	 * 
	 * @param testFlow
	 *            {@link TestFlow}
	 * @param testComponents
	 *            testComponents
	 * @param context
	 *            IEclipseContext
	 * @param line
	 *            line to be parsed
	 * @throws SystemException
	 *             SystemException
	 */
	private void addTestActionGroup(TestFlow testFlow, LinkedList<TestComponent> testComponents,
			IEclipseContext context, String line) throws SystemException {
		if (line.startsWith("-!|script|") && testComponents.get(testComponents.size() - 1) instanceof TestActionGroup) {
			// the scriptstatment shut not been shown to the user. Put
			// this line to the invisible lines
			((TestActionGroup) testComponents.get(testComponents.size() - 1)).addInvisibleActionLine(line);
		} else if (line.startsWith("|note| Maske:")) {
			addTestActionGroup(testFlow, context, testComponents, line.substring(14, line.length() - 1));
		} else if (line.startsWith("# Maske:")) {
			addTestActionGroup(testFlow, context, testComponents, line.substring(9));
		} else if (line.startsWith("|") && testComponents.size() > 0
				&& testComponents.get(testComponents.size() - 1) instanceof TestActionGroup) {
			if (!((TestActionGroup) testComponents.get(testComponents.size() - 1)).getActionLines().isEmpty()) {
				addTestActionGroup(testFlow, context, testComponents,
						((TestActionGroup) (testComponents.get(testComponents.size() - 1))).getActionGroupName());
			}
			TestActionGroup testActionGroup = (TestActionGroup) testComponents.get(testComponents.size() - 1);
			actionGroupService.addActionLine(testActionGroup, testFlow, line);
		} else if (line.startsWith("\n")) {
			TestInvisibleContent testInvisibleContent = new TestInvisibleContent();
			testInvisibleContent.setSourceCode(line);
			testComponents.add(testInvisibleContent);
		} else {

			if (testComponents.size() < 1
					|| !(testComponents.get(testComponents.size() - 1) instanceof TestActionGroup)) {

				TestActionGroup corruptActionGroup = ContextInjectionFactory.make(TestActionGroup.class, context);
				corruptActionGroup.setActionGroupName("Unknown");
				corruptActionGroup.setParsedActionGroup(false);
				testComponents.add(corruptActionGroup);

			} else {
				SystemException systemException = new SystemException(
						"An error occurred while parsing the wiki page to the internal object structure" + " Line: "
								+ line.toString());
				LOGGER.error("parseWikiPageToTestComponents - FAILED", systemException);
				throw systemException;
			}

		}
	}

	/**
	 * this method creates and adds an {@link TestActionGroup} to the
	 * testComponents.
	 * 
	 * @param testFlow
	 *            {@link TestFlow}
	 * @param context
	 *            IEclipseContext
	 * @param testComponents
	 *            LinkedList<TestComponent>
	 * @param actionGroupName
	 *            name of the {@link TestActionGroup}
	 */
	private void addTestActionGroup(TestFlow testFlow, IEclipseContext context,
			LinkedList<TestComponent> testComponents, String actionGroupName) {
		TestActionGroup testActionGroup;
		if (testFlow instanceof TestScenario) {
			testActionGroup = ContextInjectionFactory.make(TestActionGroupTestScenario.class, context);
		} else {
			testActionGroup = ContextInjectionFactory.make(TestActionGroupTestCase.class, context);
		}
		if (testActionGroup != null) {
			testActionGroup.setActionGroupName(actionGroupName);
			testComponents.add(testActionGroup);
		}
	}

	/**
	 * creates an new descriptionLine out of the line an add this test-component
	 * to the testcomponents.
	 * 
	 * @param testFlow
	 *            {@link TestFlow}
	 * @param testComponents
	 *            testComponents
	 * @param line
	 *            line to be parsed
	 */

	private void addDescriptionLine(TestFlow testFlow, LinkedList<TestComponent> testComponents, String line) {
		TestDescription testDescription = testFlow.getNewTestDescription();
		if (line.startsWith("'''")) {
			line = line.substring(3);
		} else if (line.startsWith("|note|Description: ")) {
			line = line.substring("|note|Description: ".length());
		}
		if (line.endsWith("|")) {
			line = line.substring(0, line.length() - 1);
		}
		if (line.endsWith("''' --------")) {
			line = line.substring(0, line.length() - 12);
		}

		testDescription.setDescription(line);
		testComponents.add(testDescription);
	}

	/**
	 * 
	 * @param line
	 *            of the TestFlwo-Source as a String
	 * @return true, if the line a descriptionLine
	 */
	private boolean isADescriptionLine(String line) {
		if ((line.startsWith("'''") && line.endsWith("''' --------|"))
				|| (line.startsWith("|note|Description: ") && line.endsWith("|"))) {
			return true;
		}
		return false;
	}

	/**
	 * Parses the content of a TestSuite FitNesse page and returns the referred
	 * Tetscases.
	 * 
	 * @param testSuite
	 *            ts
	 * @param content
	 *            of the testsuite
	 * @return list of referred TestStructures
	 */
	public List<TestStructure> parseReferredTestCases(TestSuite testSuite, String content) {
		List<TestStructure> result = new ArrayList<TestStructure>();
		String[] strings = content.split("\n");
		for (String line : strings) {
			if (line.startsWith("!see")) {
				String fullName = line.substring(6);
				TestStructure testStructure = testSuite.getRootElement().getTestChildByFullName(fullName);
				if (testStructure != null) {
					result.add(testStructure);
				} else {
					BrokenTestStructure structure = new BrokenTestStructure();
					structure.setName(fullName);
					structure.setSourceCode(line);
					result.add(structure);
				}
			}
		}
		return result;
	}

}
