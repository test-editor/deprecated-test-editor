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
package org.testeditor.core.services.interfaces;

import java.io.IOException;
import java.util.Map;

import org.osgi.service.prefs.BackingStoreException;

/**
 * 
 * This Services manages the configuration of the TestEditor. The TestEditor has
 * some properties for all projects like:
 * <ul>
 * <li>Browser Path</li>
 * <li>Time to wait after step</li>
 * </ul>
 * 
 */
public interface TestEditorConfigurationService {

	/**
	 * Loads the globalVaraiables from the workspace and sets them as
	 * SystemProperties.
	 * 
	 * If there are no globalVaraiables in the workspace default values are
	 * created and stored to the preference store.
	 * 
	 * @throws BackingStoreException
	 *             on problems accessing the preference store
	 * 
	 */
	void loadGlobalVariablesAsSystemProperties() throws BackingStoreException;

	/**
	 * 
	 * Updates the Preference Store with this key value pair and sets them as VM
	 * Properties.
	 * 
	 * @param key
	 *            used as property identifier.
	 * @param value
	 *            of the property.
	 */
	void updatePair(String key, String value);

	/**
	 * 
	 * Removes the key from the Preference Store and removes it from VM
	 * Properties.
	 * 
	 * @param key
	 *            used as property identifier.
	 * @throws BackingStoreException
	 *             while removing the property.
	 */
	void clearKey(String key) throws BackingStoreException;

	/**
	 * Makes the last Changes to the Configuration persistent.
	 * 
	 * @throws BackingStoreException
	 *             on problems accessing the preference store
	 */
	void storeChanges() throws BackingStoreException;

	/**
	 * Loads the globalVaraiables from the workspace and returns them.
	 * 
	 * @return global Variables of the TestEditor Workspace.
	 * @throws BackingStoreException
	 *             on problems accessing the preference store
	 */
	Map<String, String> getGlobalVariables() throws BackingStoreException;

	/**
	 * Sets a Property to indicate, that the Application state should be reset
	 * on the next application launch.
	 * 
	 * @param resetState
	 *            value to be stored.
	 * @throws BackingStoreException
	 *             on problems accessing the preference store
	 */
	void setResetApplicationState(boolean resetState) throws BackingStoreException;

	/**
	 * Check if the Application State should be reseted on restart.
	 * 
	 * @return state of the Application reset.
	 */
	boolean isResetApplicationState();

	/**
	 * Initializes the System Properties of the Applications. They are used by
	 * the test engine in a new jvm. It sets the path to the system fixtures and
	 * the workspace location.
	 * 
	 * @throws IOException
	 *             on file access.
	 */
	void initializeSystemProperties() throws IOException;

}
