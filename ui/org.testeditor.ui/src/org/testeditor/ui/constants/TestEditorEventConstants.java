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
 * Class for defining final variables for evenrt broker.
 * 
 * @deprecated @see TestEditorUIConstants.
 * 
 */
@Deprecated
public final class TestEditorEventConstants {

	/**
	 * Privat Constructor.
	 */
	private TestEditorEventConstants() {
	}

	// TODO Ticket to check the need of using this event. Sounds like a command.
	public static final String TEST_GET_FOCUS_IN_TABLE = "test_get_focus_in_table";

	public static final String REFRESH_FILTER_FOR_SCENARIOS_IN_TREE = "resfresh_filter_for_scenarios_in_tree";
	public static final String CACHE_TEST_COMPONENT_TEMPORARY = "cache_test_component_temporary";
	public static final String CACHE_TEST_COMPONENT_OF_PART_TEMPORARY = "cache_test_component_of_part_temporary"; // initialize
	public static final String GET_FOCUS_ON_INPUT_PART = "get_focus_on_input_part";

	public static final String REFRESH_TEST_FLOW_VIEW = "refresh_test_flow_view";

	public static final String REFRESH_TESTFLOW_VIEWS_TO_PROJECT = "refresh_all_testflow_views_of_project";

}
