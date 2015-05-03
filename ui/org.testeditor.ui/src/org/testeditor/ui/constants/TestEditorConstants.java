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
package org.testeditor.ui.constants;

/**
 * Constants for the TestEditor.
 * 
 */
public abstract class TestEditorConstants {

	public static final String OPEN_TEST_STRUCTURE_COMMAND_ID = "org.testeditor.ui.command.OpenTestStructureCommand";
	public static final String TEST_CASE_CONTROLER = "org.testeditor.ui.commandparameter.testCaseControler";
	public static final String SELECTED_TEST_COMPONENT = "org.testeditor.ui.commandparameter.selectedTestComponent";
	public static final String TEST_EXPLORER_VIEW = "org.testeditor.ui.part.testexplorer";
	public static final String TEST_HISTORY_VIEW = "org.testeditor.ui.part.testhistory";
	public static final String SYSTEM_CONFIGUTRATION_VIEW = "org.testeditor.ui.part.systemconfiguration";
	public static final String CONTRIBUTOR_URI = "platform:/plugin/org.testeditor.ui";
	public static final String LOGGING_INTERACTION = "TestEditorLoggingInteraction";
	public static final String LOG4J_RELATIVE_PATH = ".metadata/testEditorLog4j/log4j.xml";
	public static final String BROWSER_PATH = "BROWSER_INSTALLATION_PATH";

	public static final String REGEX_DEFINE_VARIABLE_LOGGING = ".*LOGGING.*\\{(.*)\\}";
	// TODO hier noch ändern.
	public static final String REGEX_DEFINE_VARIABLE_IEWEBDRIVERSERVER = ".*IEWEBDRIVERSERVER.*\\{(.*)\\}";

	public static final int TABLE_DEFAULT_HEIGTH = 70;

}
