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
import java.util.List;

import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestStructure;

public interface TeamShareStatusServiceNew {

	/**
	 * Returns the list of modificated teststructures
	 * 
	 * @param testProject
	 *            {@link TestProject}
	 * 
	 * @throws SystemException
	 * @throws IOException
	 */
	List<String> getModified(TestProject testProject);

	/**
	 * Update the internal map of modifications, list must be in synch with SVN
	 * state
	 * 
	 * @param testProject
	 *            {@link TestProject}
	 * 
	 */
	void update(TestProject testProject);

	/**
	 * Checks if given teststructure is midificated
	 * 
	 * @param testStructure
	 *            {@link TestStructure}
	 * 
	 * @throws SystemException
	 * @throws IOException
	 * 
	 */
	boolean isModified(TestStructure testStructure);

	/***
	 * Project will be removed from internal map.
	 * 
	 * @param testProject
	 *            {@link TestProject}
	 * 
	 * @return true if deleted
	 */
	boolean remove(TestProject testProject);

}
