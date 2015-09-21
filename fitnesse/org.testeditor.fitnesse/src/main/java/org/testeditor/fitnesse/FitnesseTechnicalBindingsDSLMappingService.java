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
package org.testeditor.fitnesse;

import java.util.HashMap;
import java.util.Map;

import org.testeditor.core.services.interfaces.TechnicalBindingsDSLMappingService;

/**
 * 
 * Fitnesse Implementation of <code>TechnicalBindingsDSLMappingService</code>.
 * 
 */
public class FitnesseTechnicalBindingsDSLMappingService implements TechnicalBindingsDSLMappingService {

	private Map<String, TecBindDslParameteMapper> map;

	/**
	 * Creates the Service with static Mapping Rules. This Map is a Workaround,
	 * until the element list is parsed for mapping.
	 */
	public FitnesseTechnicalBindingsDSLMappingService() {
		map = new HashMap<String, TecBindDslParameteMapper>();
		map.put("click ()", new TecBindDslParameteMapper("|klicke auf|guiElement|", "guiElement"));
		map.put("navigateToUrl ()", new TecBindDslParameteMapper("|navigiere auf die Seite|url|", "url"));
		map.put("insertIntoField ()", new TecBindDslParameteMapper("|gebe in das Feld|guiid|den Wert|text|ein|",
				"text, guiid"));
	}

	@Override
	public String mapTechnicalBindingToTestDSL(String technicalBinding) {
		String methodName = extractMethodNameFromTechnicalBinding(technicalBinding);
		TecBindDslParameteMapper fitnesseDSLLine = map.get(methodName);
		if (fitnesseDSLLine == null) {
			return null;
		}
		return replaceFitnesseDSLParametersWithTechnicalBindingParameters(fitnesseDSLLine, technicalBinding);
	}

	/**
	 * 
	 * @param fitnesseDSLLine
	 *            to replace the parameters in.
	 * @param technicalBinding
	 *            to extract the parameters from.
	 * @return FitNesse DSL with parameters from the technical bindings.
	 */
	protected String replaceFitnesseDSLParametersWithTechnicalBindingParameters(
			TecBindDslParameteMapper fitnesseDSLLine, String technicalBinding) {
		String parameterString = technicalBinding.substring(technicalBinding.indexOf('(') + 1,
				technicalBinding.lastIndexOf(')'));
		String[] parameterStrings = new String[] { parameterString };
		if (parameterString.indexOf(',') > -1) {
			parameterStrings = parameterString.split(",");
		}
		StringBuilder result = new StringBuilder();
		String[] fitnesseDSLParts = fitnesseDSLLine.getDsl().split("\\|");
		boolean isParameter = false;
		for (String dslPart : fitnesseDSLParts) {
			if (dslPart.length() > 0) {
				if (isParameter) {
					int parameterIndex = fitnesseDSLLine.getIndexFor(dslPart);
					String parameter = fixPossibleQuotMarks(parameterStrings[parameterIndex]);
					result.append("|").append(parameter);
					isParameter = false;
				} else {
					result.append("|").append(dslPart.trim());
					isParameter = true;
				}
			}
		}
		return result.append("|").toString();
	}

	/**
	 * Removes blanks and quotation from String. <br>
	 * Examples: "login" -> login <br>
	 * login -> login
	 * 
	 * @param string
	 *            to work with
	 * @return string without quotations.
	 */
	protected String fixPossibleQuotMarks(String string) {
		String result = string;
		while (result.indexOf("\"") > -1) {
			result = result.substring(0, result.indexOf("\""))
					+ result.substring(result.indexOf("\"") + 1, result.length());
		}
		return result.trim();
	}

	/**
	 * 
	 * @param technicalBinding
	 *            as a java method call with parameter
	 * @return the method name with an empty parameterlist.
	 */
	protected String extractMethodNameFromTechnicalBinding(String technicalBinding) {
		return technicalBinding.substring(0, technicalBinding.indexOf('(')) + "()";
	}

}
