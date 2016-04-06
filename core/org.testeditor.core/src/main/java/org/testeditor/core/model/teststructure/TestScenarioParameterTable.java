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
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.model.action.TextType;

/**
 * 
 * this class represents the parameters and the include-information of a
 * {@link TestScenario} using in a {@link TestFlow}.
 * 
 * @author llipinski
 */
public class TestScenarioParameterTable implements TestComponent {

	private TestData dataTable = new TestData();
	private String include = "";
	private String title = "";
	private boolean isSimpleScriptStatement = false;
	private boolean isScenarioOfProject = false;

	private TestDataEvaluationReturnList testDataEvaluationReturnList;

	private static final Logger LOGGER = Logger.getLogger(TestScenarioParameterTable.class);

	@Override
	public List<String> getTexts() {
		ArrayList<String> texts = new ArrayList<String>();
		texts.add(getSplitedTitle().trim());
		return texts;
	}

	@Override
	public String getSourceCode() {
		StringBuilder sourceCodeBuilder = new StringBuilder();
		sourceCodeBuilder.append(getInclude()).append("\n");
		if (isSimpleScriptStatement) {
			sourceCodeBuilder.append("!|script|\n");
			sourceCodeBuilder.append("|").append(getTitle()).append("|\n");
		} else {
			sourceCodeBuilder.append("!|").append(getTitle()).append("|\n");
		}
		sourceCodeBuilder.append(getParamTableToString());
		sourceCodeBuilder.append("#");
		return sourceCodeBuilder.toString();
	}

	/**
	 * converts the dataTable to a string.
	 * 
	 * @return the dataTable as a string for as part of the Sourcecode
	 */
	private String getParamTableToString() {
		if (dataTable != null && dataTable.getRows().size() > 0 && dataTable.getTitleRow() != null) {
			StringBuilder paramlistSourceCode = new StringBuilder();
			int columnCount = dataTable.getTitleRow().getColumnCount();
			for (TestDataRow paramLine : dataTable.getRows()) {
				paramlistSourceCode.append("|");
				for (int i = 0; i < columnCount; i++) {
					String paramElement = paramLine.getColumn(i);
					paramlistSourceCode.append(paramElement).append("|");
				}
				paramlistSourceCode.append("\n");
			}
			return paramlistSourceCode.toString();
		}
		return "";
	}

	@Override
	public List<TextType> getTextTypes() {
		ArrayList<TextType> types = new ArrayList<TextType>();
		types.add(TextType.ACTION_NAME);
		return types;
	}

	/**
	 * returns the include-line.
	 * 
	 * @return the include-line
	 */
	public String getInclude() {
		return include;
	}

	/**
	 * setter for the include.
	 * 
	 * @param include
	 *            include-line
	 */
	public void setInclude(String include) {
		this.include = "!include <" + include;
	}

	/**
	 * setter for the include.
	 * 
	 * @param include
	 *            include-line
	 */
	public void setIncludeLong(String include) {
		this.include = include;
	}

	/**
	 * the getter for the title above the dataTable.
	 * 
	 * @return the title above the dataTable
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @return the on the capital-chars splited title
	 */
	public String getSplitedTitle() {
		return splitOnCapitalsWithWhiteSpaces(getTitle(), 0);
	}

	/**
	 * set the Title.
	 * 
	 * @param title
	 *            title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * the parameterTable.
	 * 
	 * @return the dataTable as a {@link ArrayList} of ArrayLists
	 */
	public TestData getDataTable() {
		return dataTable;
	}

	/**
	 * creates a title line out of includeLine. i.e. includeLine
	 * 
	 * @return a titelLine, that fits to the given parameter includeLine
	 */
	public String getTitleOutOfInclude() {
		String name = getName();
		int fromPos = 1;
		/*
		 * convert from !include <DemoWebTests.TestSzenarien.LoginSzenario to
		 * !|Login Szenario|
		 */
		return splitOnCapitalsWithWhiteSpaces(name, fromPos);
	}

	/**
	 * 
	 * @param name
	 *            the name as String
	 * @param fromPos
	 *            as int
	 * @return the splited name started at the fromPos
	 */
	public static String splitOnCapitalsWithWhiteSpaces(String name, int fromPos) {
		StringBuilder title = new StringBuilder();
		String[] titleStrings = splitOnCapitals(name);
		int i = titleStrings.length;
		for (String titlePart : titleStrings) {
			title.append(titlePart);
			if (i > fromPos) {
				title.append(" ");
			}
			i--;
		}
		return title.toString();
	}

	/**
	 * this method splits a string with camel-case into separated words.
	 * 
	 * @param str
	 *            the input
	 * @return String[] the separated words
	 */
	private static String[] splitOnCapitals(String str) {
		ArrayList<String> array = new ArrayList<String>();
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < str.length(); i++) {
			if (Character.isUpperCase(str.charAt(i))) {
				String line = builder.toString().trim();
				if (line.length() > 0) {
					array.add(line);
				}
				builder = new StringBuilder();
			}
			builder.append(str.charAt(i));
		}
		array.add(builder.toString().trim()); // get the last little bit too
		return array.toArray(new String[0]);
	}

	/**
	 * adds a parameterLine into the dataTable.
	 * 
	 * @param paramLine
	 *            String. Parameters are separated by |
	 * @throws SystemException
	 *             throws if data row count higher than title roe count
	 */
	public void addParameterLine(String paramLine) throws SystemException {

		String[] elementListTemp = paramLine.substring(1).split("\\|", -1);

		// remove the last element
		String[] elementList = Arrays.copyOf(elementListTemp, elementListTemp.length - 1);

		TestDataRow actParams = new TestDataRow(elementList);

		if (getDataTable().hasTitleRow()) {
			int headerColumnCount = getDataTable().getTitleRow().getColumnCount();
			if (actParams.getColumnCount() != headerColumnCount) {
				LOGGER.info("count of datarow [" + actParams.toString() + "] is higher then row header column count ["
						+ headerColumnCount + "]");
				TestDataEvaluationReturnList testDataEvaluationReturnList = new TestDataEvaluationReturnList();
				testDataEvaluationReturnList.setDataRowColumnCountEqualsHeaderRowColumnCount(false);
				setTestDataEvaluationReturnList(testDataEvaluationReturnList);
			}

		}

		getDataTable().addRow(actParams);
	}

	/**
	 * returns true, if its a simple scriptStatment without parameters.
	 * 
	 * @return true, if its a simple scriptStatment without parameters.
	 */
	public boolean isSimpleScriptStatement() {
		return isSimpleScriptStatement;
	}

	/**
	 * set s the varibale isSimpleScriptStatement with the parameter.
	 * 
	 * @param isSimpleScriptStatement
	 *            boolean
	 */
	public void setSimpleScriptStatement(boolean isSimpleScriptStatement) {
		this.isSimpleScriptStatement = isSimpleScriptStatement;
	}

	/**
	 * adds testData to the parameters.
	 * 
	 * @param testData
	 *            {@link TestData}
	 */
	public void addTestData(TestData testData) {
		for (TestDataRow testRow : testData.getRows()) {
			dataTable.addRow(testRow);
		}
	}

	/**
	 * 
	 * @return the name of the TestScenario
	 */
	public String getName() {
		return getInclude().substring(include.lastIndexOf(".") + 1);

	}

	/**
	 * 
	 * @return the value of the isScenarioOfProject member-parameter.
	 */
	public boolean isScenarioOfProject() {
		return isScenarioOfProject;
	}

	/**
	 * sets the member-parameter isScenarioOfProject.
	 * 
	 * @param isScenarioOfProject
	 *            boolean
	 */
	public void setScenarioOfProject(boolean isScenarioOfProject) {
		this.isScenarioOfProject = isScenarioOfProject;
	}

	/**
	 * sets the TestDataEvaluationReturnList.
	 * 
	 * @param testDataEvaluationReturnList
	 *            TestDataEvaluationReturnList
	 */
	public void setTestDataEvaluationReturnList(TestDataEvaluationReturnList testDataEvaluationReturnList) {
		this.testDataEvaluationReturnList = testDataEvaluationReturnList;
	}

	/**
	 * 
	 * @return TestDataEvaluationReturnList
	 */
	public TestDataEvaluationReturnList getTestDataEvaluationReturnList() {
		return testDataEvaluationReturnList;
	}
}
