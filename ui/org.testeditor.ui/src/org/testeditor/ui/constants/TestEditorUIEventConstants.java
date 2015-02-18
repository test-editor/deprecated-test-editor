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
 * 
 * Definition of UI Events.
 *
 */
public final class TestEditorUIEventConstants {

	/**
	 * TestFlow State is changed to a dirty TestFlow which contains not
	 * persisted changes.
	 */
	public static final String TEST_FLOW_STATE_CHANGED_TO_DIRTY = "testflow/state/changed/dirty";

	private static final String TESTSTRUCTURE = "teststructure";

	/**
	 * TestStructure is reverted, when it the TestSturctre is open it should be
	 * refreshed.
	 */
	public static final String TESTSTRUCTURE_REVERTED = TESTSTRUCTURE + "/team/reverted";

	/**
	 * TestStructure was executed.
	 */
	public static final String TESTSTRUCTURE_EXECUTED = TESTSTRUCTURE + "/executed";

	/**
	 * Context menu shortcut events.
	 */
	public static final String EDIT_CONTEXTMENU_F6 = "Edit/ContextMenu_F6";
	public static final String EDIT_CONTEXTMENU_F7 = "Edit/ContextMenu_F7";
	public static final String EDIT_CONTEXTMENU_F8 = "Edit/ContextMenu_F8";
	public static final String EDIT_CONTEXTMENU_DEL = "Edit/ContextMenu_DEL";
	public static final String EDIT_CONTEXTMENU_CNTRL_C = "Edit/ContextMenu_CNTR_C";
	public static final String EDIT_CONTEXTMENU_CNTRL_X = "Edit/ContextMenu_CNTR_X";
	public static final String EDIT_CONTEXTMENU_CNTRL_V = "Edit/ContextMenu_CNTR_V";
	public static final String EDIT_CONTEXTMENU_CNTRL_A = "Edit/ContextMenu_CNTR_A";
	public static final String EDIT_CONTEXTMENU_HOME_OR_END = "Edit/ContextMenu_HOME_or_END";
	public static final String EDIT_CONTEXTMENU_CNTRL_INSERT = "Edit/ContextMenu_CNTRL_INSERT";

	/**
	 * Testlibrary was new loaded form the hard disk.
	 */
	public static final String LIBRARY_LOADED_FOR_PROJECT = "library_loaded_for_project";

	/**
	 * Event to identify a removed key from the UI Table of the
	 * SystemConfiguration.
	 */
	public static final String SYSTEMCONFIGURATION_TABLE_KEY_DELETED = "systemconfiguration/tableview/changed/key_deleted";

	/**
	 * Event to identify a changed value in the UI Table of the
	 * SystemConfiguration.
	 */
	public static final String SYSTEMCONFIGURATION_TABLE_UPDATE_ELEMENT = "systemconfiguration/tableview/changed/update_element";

	/**
	 * Event hierarchy for changes of tables in a teststructure editor view.
	 */
	private static final String TESTEDITOR_VIEW_CHNAGED_TABLE_PREF = "testeditor_view/changed/table";
	public static final String TESTEDITOR_VIEW_CHNAGED_TABLE = TESTEDITOR_VIEW_CHNAGED_TABLE_PREF + "/*";
	public static final String TESTEDITOR_VIEW_CHNAGED_TABLE_EXPANDED = TESTEDITOR_VIEW_CHNAGED_TABLE_PREF
			+ "/expanded";
	public static final String TESTEDITOR_VIEW_CHNAGED_TABLE_COLLAPSED = TESTEDITOR_VIEW_CHNAGED_TABLE_PREF
			+ "/collapsed";

	/**
	 * event that a projkect is added to a team share.
	 */
	public static final String PROJECT_TEAM_SHARED = "project_team_shared";

	/**
	 * Private constructor to avoid instances of this class.
	 */
	private TestEditorUIEventConstants() {
	}

}
