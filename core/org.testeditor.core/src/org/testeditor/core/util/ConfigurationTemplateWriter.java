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
package org.testeditor.core.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;

import org.testeditor.core.constants.TestEditorGlobalConstans;
import org.testeditor.core.services.interfaces.TestExecutionEnvironmentService;

/**
 * this class writes a configuration by replacing the place-holders in the
 * template-file with the given values in the parameters.
 * 
 * @author llipinski
 * 
 */
public class ConfigurationTemplateWriter {

	/**
	 * 
	 * @param fileLocatorService
	 *            the fileLocatorService
	 * @param configurationFile
	 *            the configuration-file
	 * @param properties
	 *            the configuration-properties
	 * @param templateLibConfig
	 *            the template of the library-configuration
	 * @param templateTeamshareConfig
	 *            the template of the teamshare-configuration
	 * @throws IOException
	 *             while file-io
	 */
	public void writeConfiguration(FileLocatorService fileLocatorService, File configurationFile, Properties properties,
			String templateLibConfig, String templateTeamshareConfig) throws IOException {
		String pathToTemplate = new StringBuffer(fileLocatorService.getBundleLocationFor(this.getClass()))
				.append(File.separator).append("resources").append(File.separator).append("project.config.tmpl")
				.toString();
		File templateFile = new File(pathToTemplate);
		writeConfiguration(configurationFile, templateFile, properties, templateLibConfig, templateTeamshareConfig);
	}

	/**
	 * this method writing the configurationFile by replacing the place-holders
	 * in the template-file with the given values in the parameters.
	 * 
	 * @param configurationFile
	 *            the Configuration-File
	 * @param templateFile
	 *            the Template-File
	 * @param configurationProperties
	 *            Properties
	 * @param templateLibConfig
	 *            the template of the library-configuration
	 * @param templateTeamshareConfig
	 *            the template of the teamshare-configuration
	 * @throws IOException
	 *             while file-io
	 */
	private void writeConfiguration(File configurationFile, File templateFile, Properties configurationProperties,
			String templateLibConfig, String templateTeamshareConfig) throws IOException {

		Properties keyValuesTemplate = loadKeyValuesFromTemplateFile(templateFile);

		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(templateFile), "UTF-8"));
		PrintWriter writer = new PrintWriter(configurationFile, "UTF-8");
		String line = null;
		while ((line = reader.readLine()) != null) {
			String replacedVariableInLineByValue = replaceVariableInLineByValue(line, configurationProperties,
					keyValuesTemplate, templateLibConfig, templateTeamshareConfig);
			writer.println(replacedVariableInLineByValue);
		}

		// Close the input stream and writer
		reader.close();
		writer.close();
	}

	/**
	 * load the properties from the config-template-file.
	 * 
	 * @param templateFile
	 *            template-file
	 * @return the properties from the template
	 * @throws IOException
	 *             while reading the file
	 */
	private Properties loadKeyValuesFromTemplateFile(File templateFile) throws IOException {
		Properties properties = new Properties();
		Properties propertiesTmp = new Properties();
		InputStreamReader inputStream = new InputStreamReader(new FileInputStream(templateFile), "UTF-8");
		propertiesTmp.load(inputStream);
		inputStream.close();
		for (Object key : propertiesTmp.keySet()) {
			properties.put(propertiesTmp.get(key), key);
		}

		return properties;
	}

	/**
	 * replaces the Variable (like $VAR$) with the value of variables.If the
	 * variable is not include in the variables, then only the rest of the input
	 * will be returned.
	 * 
	 * @param inputLine
	 *            as the string
	 * @param properties
	 *            Properties
	 * @param keyValueTemplate
	 *            Properties in configTemplate-file
	 * @param templateLibConfig
	 *            the template of the library-configuration
	 * @param templateTeamshareConfig
	 *            the template of the teamshare-configuration
	 * @return the inputLine with replaced variable.
	 * 
	 */
	protected String replaceVariableInLineByValue(String inputLine, Properties properties, Properties keyValueTemplate,
			String templateLibConfig, String templateTeamshareConfig) {
		String variableInLine = getVariableIncludedInLine(inputLine);
		if (variableInLine.isEmpty()) {
			return inputLine;
		}
		if (variableInLine.equals("$VARIABLES$")) {
			return addOptionallVariablesToConfiguration(properties);
		}
		String keyInProperties = keyValueTemplate.getProperty(variableInLine);
		String value = "";
		if (keyInProperties != null) {
			value = properties.getProperty(keyInProperties);
		}
		if (value == null) {
			value = "";
		}
		if (!variableInLine.isEmpty() && value != null) {
			inputLine = inputLine.replace(variableInLine, value);
		}
		if (variableInLine.equals("$LIBRARY_VERSION$")) {
			return appendPluginConfig(inputLine, properties, templateLibConfig);
		} else if (variableInLine.equals("$SYNC_TYPE$")) {
			return appendPluginConfig(inputLine, properties, templateTeamshareConfig);
		}

		if (variableInLine.equals("$TEST_EXEC_ENV$")) {
			return "test.execution.environment.config="
					+ properties.getProperty(TestExecutionEnvironmentService.CONFIG);
		}

		return inputLine;
	}

	/**
	 * 
	 * @param inputLine
	 *            as String
	 * @param properties
	 *            the properties of the configuration
	 * @param templateConfig
	 *            configurationTemplate for a pluginConfiguration
	 * @return the modified line with added properties for the configuration of
	 *         the plugin
	 */
	protected String appendPluginConfig(String inputLine, Properties properties, String templateConfig) {
		StringBuilder sb = new StringBuilder(inputLine);
		String lf = "\n";
		String[] templatLines = templateConfig.split(lf);
		for (String line : templatLines) {
			String[] lineParts = line.split("=");
			String variableInLine = getVariableIncludedInLine(line);
			if (variableInLine != null && !variableInLine.isEmpty() && !lineParts[0].isEmpty()
					&& properties.getProperty(lineParts[0]) != null) {
				sb.append(lf).append(line.replace(variableInLine, properties.getProperty(lineParts[0])));
			}
		}

		return sb.toString();
	}

	/**
	 * adds the optional variables.
	 * 
	 * @param properties
	 *            Properties with the optional-variables. The key is beginning
	 *            with 'variable.'
	 * @return a String including the variables.
	 */
	private String addOptionallVariablesToConfiguration(Properties properties) {
		StringBuilder returnVariables = new StringBuilder();
		Enumeration<Object> keys = properties.keys();
		ArrayList<String> keyList = new ArrayList<String>();
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			if (key.startsWith(TestEditorGlobalConstans.VARIABLE_PRAEFIX)) {
				keyList.add((String) key);
			}
		}

		Collections.sort(keyList);
		for (Object key : keyList) {
			returnVariables.append(key).append("=").append(properties.get(key)).append("\n");
		}

		return returnVariables.toString().replace("\\", "/");
	}

	/**
	 * gets the variable in the inputLine like '$VAR$'. If no variable is in the
	 * input, then an empty-String will be returned. If there are more, then two
	 * '$' in the input, then an empty-string will be returned.
	 * 
	 * @param inputLine
	 *            as a String.
	 * @return the variable
	 */
	protected String getVariableIncludedInLine(String inputLine) {
		if (countChars('$', inputLine) == 2) {
			int startIndex = inputLine.indexOf("$");
			int lastIndex = inputLine.substring(startIndex + 1).indexOf('$');
			return inputLine.substring(startIndex, startIndex + lastIndex + 2);
		}
		return "";
	}

	/**
	 * counts the char c in the string s.
	 * 
	 * @param c
	 *            char
	 * @param s
	 *            the string
	 * @return the count of the char c in the string s.
	 */
	private int countChars(char c, String s) {
		int result = 0;
		for (int i = 0, n = s.length(); i < n; i++) {
			if (s.charAt(i) == c) {
				result++;
			}
		}
		return result;
	}

}
