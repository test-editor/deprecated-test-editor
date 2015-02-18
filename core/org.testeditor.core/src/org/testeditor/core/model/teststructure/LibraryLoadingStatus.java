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
package org.testeditor.core.model.teststructure;

import java.util.ArrayList;

/**
 * 
 * this class contains after the loading of the library the loading-status.
 * 
 * @author llipinski
 */
public class LibraryLoadingStatus {

	private boolean errorLessLoaded = false;
	private boolean loaded = false;
	// in this map there will be the filenames and the errors stored.
	private ArrayList<String> errorWhileLoadingList = new ArrayList<String>();

	/**
	 * isErrorLessLoaded gets the status of the loading of the library.
	 * 
	 * @return the loading-status
	 */
	public boolean isErrorLessLoaded() {
		return errorLessLoaded;
	}

	/**
	 * sets the loading status.
	 * 
	 * @param loadingStatus
	 *            the loading-status
	 */
	public void setErrorLessLoaded(boolean loadingStatus) {
		this.errorLessLoaded = loadingStatus;
	}

	/**
	 * gets the errors from the last loading.
	 * 
	 * @return the error-list from the last loading
	 */
	public ArrayList<String> getErrorWhileLoadingList() {
		return errorWhileLoadingList;
	}

	/**
	 * get the loaded status. This status is true after a first loading try.
	 * Also if the loading fails.
	 * 
	 * @return loaded
	 */

	public boolean isLoaded() {
		return loaded;
	}

	/**
	 * this status should be set = true after a loading. Also if the loading
	 * fails.
	 * 
	 * @param loaded
	 *            loaded
	 */
	public void setLoaded(boolean loaded) {
		this.loaded = loaded;
	}
}
