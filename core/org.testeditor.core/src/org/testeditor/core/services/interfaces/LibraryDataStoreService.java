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

import java.util.Map;

import org.testeditor.core.model.action.ProjectActionGroups;

/**
 * 
 * this is the interface for a module, that keeps the information of the library
 * in a cache.
 * 
 */
// TODO Can this class be combined with the ActionGroupService
public interface LibraryDataStoreService {
	/**
	 * this method adds the given projectActionGroup to the cache.
	 * 
	 * @param projectsActionGroups
	 *            the {@link ProjectActionGroups} to be added
	 */
	void addProjectActionGroups(ProjectActionGroups projectsActionGroups);

	/**
	 * this method returns a Hash of all ProjectNames an their
	 * ProjectActionGroups.
	 * 
	 * @return HashMap<String, ProjectActionGroups>
	 */
	Map<String, ProjectActionGroups> getProjectsActionGroups();
}
