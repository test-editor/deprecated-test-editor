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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.testeditor.core.constants.TestEditorGlobalConstans;

/**
 * tests the methods of the ConfigurationTemplateWriter.
 * 
 * @author llipinski
 * 
 */
public class ConfigurationTemplateWriterTest {

	private Properties keyValuesTemplate = new Properties();

	/**
	 * startup method runs before the tests.
	 */
	@Before
	public void beforeTest() {
		keyValuesTemplate.put("$world$", "location.world");
	}

	/**
	 * test return of an empty-string from a line without any variable included.
	 */
	@Test
	public void getVariableIncludedInLineWithNoVariableInLineTest() {
		ConfigurationTemplateWriter configurationTemplateWriter = new ConfigurationTemplateWriter();

		String inputLine = "hello world";
		assertEquals("", configurationTemplateWriter.getVariableIncludedInLine(inputLine));
	}

	/**
	 * test the return of the variable included in the input.
	 */
	@Test
	public void getVariableIncludedInLineTest() {
		ConfigurationTemplateWriter configurationTemplateWriter = new ConfigurationTemplateWriter();

		String inputLine = "hello $world$";
		assertEquals("$world$", configurationTemplateWriter.getVariableIncludedInLine(inputLine));
	}

	/**
	 * test the replacing of a variable in the input line with the value of the
	 * variables of the key-value-pairs.
	 */
	@Test
	public void repalceVariabelInLineTest() {
		Properties properties = new Properties();
		properties.put("location.world", "every buddy");
		ConfigurationTemplateWriter configurationTemplateWriter = new ConfigurationTemplateWriter();

		String inputLine = "hello $world$!";

		assertEquals("hello every buddy!", configurationTemplateWriter.replaceVariableInLineByValue(inputLine,
				properties, keyValuesTemplate, "", ""));
	}

	/**
	 * test the replacing without a value. The variable is not a key in the
	 * key-value-pairs.
	 */
	@Test
	public void repalceVariabelInLineTestWithNoVariable() {
		Properties properties = new Properties();
		properties.put("ort.welt", "jedermann");
		ConfigurationTemplateWriter configurationTemplateWriter = new ConfigurationTemplateWriter();

		String inputLine = "hello $world$!";
		assertNotEquals("hello jedermann!", configurationTemplateWriter.replaceVariableInLineByValue(inputLine,
				properties, keyValuesTemplate, "", ""));
		assertEquals("hello !", configurationTemplateWriter.replaceVariableInLineByValue(inputLine, properties,
				keyValuesTemplate, "", ""));
	}

	/**
	 * test the appending of the optional-variables.
	 */
	@Test
	public void addOptionalVariablesTest() {
		Properties properties = new Properties();
		ConfigurationTemplateWriter configurationTemplateWriter = new ConfigurationTemplateWriter();
		properties.put(TestEditorGlobalConstans.VARIABLE_PRAEFIX + "1", "one");
		properties.put(TestEditorGlobalConstans.VARIABLE_PRAEFIX + "2", "two");
		properties.put(TestEditorGlobalConstans.VARIABLE_PRAEFIX + "3", "three");
		properties.put(TestEditorGlobalConstans.VARIABLE_PRAEFIX + "4", "four");
		String inputLine = "$VARIABLES$";
		assertEquals(TestEditorGlobalConstans.VARIABLE_PRAEFIX + "1=one\n" + TestEditorGlobalConstans.VARIABLE_PRAEFIX
				+ "2=two\n" + TestEditorGlobalConstans.VARIABLE_PRAEFIX + "3=three\n"
				+ TestEditorGlobalConstans.VARIABLE_PRAEFIX + "4=four\n",
				configurationTemplateWriter.replaceVariableInLineByValue(inputLine, properties, keyValuesTemplate, "",
						""));

	}

	/**
	 * test the replacing in a template of a plugin.
	 */
	@Test
	public void pluginConfigurationTemplateTest() {
		Properties properties = new Properties();
		ConfigurationTemplateWriter configurationTemplateWriter = new ConfigurationTemplateWriter();
		properties.put("synchronisation", "one");
		properties.put("synchronisation.local_path", "xyz");
		String inputLine = "synchronisation=$SYNC_TYPE$";
		String teamsyncTemplate = "synchronisation.local_path=$SYNC_LOCAL_PATH$";
		Properties variabelToKey = new Properties();
		variabelToKey.put("$SYNC_TYPE$", "synchronisation");
		String replaceVariableInLineByValue = configurationTemplateWriter.replaceVariableInLineByValue(inputLine,
				properties, variabelToKey, "", teamsyncTemplate);
		assertEquals("synchronisation=one\nsynchronisation.local_path=xyz", replaceVariableInLineByValue);

	}
}
