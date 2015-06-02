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
package org.testeditor.dummylibrary;

import java.util.ArrayList;

import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.model.action.Action;
import org.testeditor.core.model.action.ActionGroup;
import org.testeditor.core.model.action.Argument;
import org.testeditor.core.model.action.ProjectActionGroups;
import org.testeditor.core.model.action.ProjectLibraryConfig;
import org.testeditor.core.services.interfaces.LibraryReaderService;

/**
 * 
 * Example Implementation of the <code>LibraryReaderService</code>. This class
 * is only a dummy, to show the usage of the framework.
 * 
 */
public class DummyLibraryReaderService implements LibraryReaderService {

	@Override
	public ProjectActionGroups readBasisLibrary(ProjectLibraryConfig libraryConfig) throws SystemException {
		DummyProjectLibraryConfig projectLibraryConfig = (DummyProjectLibraryConfig) libraryConfig;
		ProjectActionGroups actionGroups = new ProjectActionGroups();
		ActionGroup actionGroup = new ActionGroup();
		Action action = new Action();
		action.setArguments(new ArrayList<Argument>());
		actionGroup.addAction(action);
		actionGroup.setName(projectLibraryConfig.getDummyName());
		actionGroups.addActionGroup(actionGroup);
		return actionGroups;
	}

	@Override
	public String getId() {
		return DummyProjectLibraryConfig.ID;
	}

}
