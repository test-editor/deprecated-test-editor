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
/**
 * 
 */
package org.testeditor.core.constants;

/**
 * Class for defining constants for event broker.
 * 
 */
public final class TestEditorCoreEventConstants {

	private static final String TESTSTRUCTURE_MODEL_CHANGED_PREFIX = "TESTPROJECTS/CHANGED";

	/**
	 * Model of loaded TestStructures is chnaged. UI should refresh the
	 * Informations.
	 */
	public static final String TESTSTRUCTURE_MODEL_CHANGED = TESTSTRUCTURE_MODEL_CHANGED_PREFIX + "/*";

	/**
	 * Model of TestStructures is updated.
	 */
	public static final String TESTSTRUCTURE_MODEL_CHANGED_UPDATE = TESTSTRUCTURE_MODEL_CHANGED_PREFIX + "/UPDATE/*";

	public static final String TESTSTRUCTURE_MODEL_CHANGED_UPDATE_BY_ADD = TESTSTRUCTURE_MODEL_CHANGED_PREFIX
			+ "/UPDATE/ADD";

	public static final String TESTSTRUCTURE_MODEL_CHANGED_UPDATE_BY_MODIFY = TESTSTRUCTURE_MODEL_CHANGED_PREFIX
			+ "/UPDATE/SAVE";

	/**
	 * List of TestProjects is dropped and fresh loaded from the backend.
	 */
	public static final String TESTSTRUCTURE_MODEL_CHANGED_RELOADED = TESTSTRUCTURE_MODEL_CHANGED_PREFIX + "/RELOAD";

	/**
	 * Test structure was deleted.
	 */
	public static final String TESTSTRUCTURE_MODEL_CHANGED_DELETED = TESTSTRUCTURE_MODEL_CHANGED_PREFIX + "/deleted";

	/**
	 * This event will be send when a team shared project loaded from all files
	 * the SVN state and set them in the child`s. It is Asynchrony because it
	 * will be throw from a Thread and the User should not be Interrupted.
	 */
	public static final String TESTSTRUCTURE_STATE_UPDATED = "TESTSTRUCTURE/STATE/CHNAGED";

	public static final String TESTSTRUCTURE_HISTORY_DELETED = "TESTHISTORY/DELETED";

	/**
	 * Defined Library files were changed.
	 */
	public static final String LIBRARY_FILES_CHANGED_MODIFIED = "LIBRARY/CHANGED/modified";

	/**
	 * Event to show that a team state is rest to default.
	 */
	public static final String TESTSTRUCTURE_STATE_RESET = "TESTSTRUCTURE/STATE/RESET";

	/**
	 * Private Constructor.
	 */
	private TestEditorCoreEventConstants() {
	}

}
