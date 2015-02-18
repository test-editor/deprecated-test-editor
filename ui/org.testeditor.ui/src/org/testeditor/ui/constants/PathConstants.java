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
package org.testeditor.ui.constants;

/**
 * This Class provides the directory where to find the Log-Files of the
 * Application. The Logfile-Directory is in the user home directory
 * 
 * directory: [USERHOME]/.testeditor/.metadata
 * 
 */
public final class PathConstants {
	/**
	 * Don't create objects of this constants class.
	 */
	private PathConstants() {
	}

	/**
	 * 
	 */
	private static final String FILE_SEPARATOR = System.getProperty("file.separator");
	public static final String USER_HOME_DIRECTORY = System.getProperty("user.home");
	public static final String TEST_EDITOR_LOG_DIRECTORY = ".testeditor" + FILE_SEPARATOR + ".metadata";
	public static final String METADA_DIRECTORY = ".metadata";

	/**
	 * 
	 * @return the logDirectory of the user-home.
	 */
	public static String getLogDirectoryOfUserHome() {
		StringBuilder sb = new StringBuilder();
		sb.append(USER_HOME_DIRECTORY);
		sb.append(FILE_SEPARATOR);
		sb.append(TEST_EDITOR_LOG_DIRECTORY);
		return sb.toString();
	}

	/**
	 * @param pathToWorkspaceDirectory
	 *            path to the workspace
	 * @return the logDirectory to the path in pathToWorkspaceDirectory
	 */
	public static String getLogDirectory(String pathToWorkspaceDirectory) {
		StringBuilder sb = new StringBuilder();
		sb.append(pathToWorkspaceDirectory);
		sb.append(FILE_SEPARATOR);
		sb.append(METADA_DIRECTORY);
		return sb.toString();
	}

}
