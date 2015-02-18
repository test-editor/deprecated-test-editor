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
package org.testeditor.core.services.impl;

import java.util.HashMap;

import org.testeditor.core.model.action.ProjectActionGroups;
import org.testeditor.core.services.interfaces.LibraryDataStoreService;

/**
 * 
 * this class implements the {@link LibraryDataStoreService}.
 * 
 * @author llipinski
 */
public class LibraryDataStoreServiceImpl implements LibraryDataStoreService {

	private HashMap<String, ProjectActionGroups> projectsActionGroups = new HashMap<String, ProjectActionGroups>();

	@Override
	public void addProjectActionGroups(ProjectActionGroups projectActionGroups) {

		this.projectsActionGroups.put(projectActionGroups.getProjectName(), projectActionGroups);

	}

	@Override
	public HashMap<String, ProjectActionGroups> getProjectsActionGroups() {
		return projectsActionGroups;
	}

}
