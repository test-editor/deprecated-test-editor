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
 * Provides unique widget IDs which could be used in context of a SWTBot test.
 * 
 */
public final class CustomWidgetIdConstants {

	/**
	 *   
	 */
	private CustomWidgetIdConstants() {

	}

	public static final String NEW_TEST_PAGE_NAME = "new.test.page.name";
	public static final String NEW_TEST_STRUCTUR_TREE = "new.test.structure.wizard.tree";
	public static final String CREATE_DESCRIPTION_TEXT = "create.description.text";
	public static final String ADD_NEW_DESCRIPTION = "add.new.description";
	public static final String ADD_NEW_ACTION = "add.new.action";
	public static final String ADD_NEW_SCENARIO = "add.new.scenario";

	public static final String ADD_REFERRED_TESTCASE = "add.referred.testcase";
	public static final String REMOVE_REFERRED_TESTCASE = "remove.referred.testcase";

	public static final String SELECTION_DIALOG_TESTCASE_OK = "dialog.referred.testcase.ok";
	public static final String SELECTION_DIALOG_TESTCASE_CANCEL = "dialog.referred.testcase.cancel";

	public static final String CHOSE_MASKE = "chose.maske";
	public static final String CHOSE_ACTION = "chose.action";
	public static final String CHOSE_SCENARIO = "chose.scenario";

	public static final String ACTION_COMBO = "action.line.combo";
	public static final String ACTION_TEXT = "action.line.text";
	public static final String ACTION_LABLE = "action.line.lable";

	public static final String TEST_CASE_VIEW_TEXT = "test.case.view.text";
	public static final String TEST_CASE_VIEW = "test.case.view";
	public static final String TESTCASE_TOOLITEM_SAVE = "testcase.toolitem.save";

	public static final String TEST_FLOW_VIEW_DELETE_LINE = "test.case.context.menu.delete.line";

	public static final String TEST_PROJECT_CONFIGURATION_PORT = "test.project.configuration.port";
	public static final String TEST_PROJECT_CONFIGURATION_BROWSER = "test.project.configuration.browser";
	public static final String TEST_PROJECT_CONFIGURATION_VARIABLE_KEY = "test.project.configuration.variable.key";
	public static final String TEST_PROJECT_CONFIGURATION_VARIABLE_VALUE = "test.project.configuration.variable.value";

	public static final String INFO_DIALOG_STYLED_TEXT = "info.dialog.styled.text";
	public static final String TESTCASE_TOOLBAR_DELETE = "testcase.toolbar.delete";

	public static final String TEST_HISTORY_LABEL = "TestHistoryLabel";

	public static final String NAME_ERROR_MESSAGE_LABEL = "name.error.message.label";

	// key for the setData method at the widgets
	public static final String TEST_EDITOR_WIDGET_ID_SWT_BOT_KEY = "org.eclipse.swtbot.widget.key";
	public static final Object TEAM_SHARE_IMPORT_PROJECTNAME = "wizard.shareProject.projectname";

}
