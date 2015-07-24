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

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.testeditor.core.model.teststructure.TestProject;

/**
 * The FileWatcherService checks the files of a testProject for modifications
 * and informs the test-editor about changed from another player. This allows
 * the editor to keep the filesystem and momory model in sync.
 *
 */
public interface FileWatchService {

	/**
	 * Activates a watching thread for the testProject.
	 * 
	 * @param testProject
	 *            to be observed.
	 */
	void watch(TestProject testProject);

	/**
	 * Sets the EclipseContext to the Service.
	 * 
	 * @param context
	 *            used to retrieve the EventBroker.
	 */
	void setContext(IEclipseContext context);

	/**
	 * Drops all Filewatchers.
	 * 
	 * Calling this method will terminate all watching threads and removes them
	 * from the service.
	 */
	void dropWatchers();

	/**
	 * Remove a Project from the service.
	 * 
	 * The Thread watching the file system will be stopped.
	 * 
	 * @param testProject
	 *            to be removed from the watch list.
	 */
	void remove(TestProject testProject);
}
