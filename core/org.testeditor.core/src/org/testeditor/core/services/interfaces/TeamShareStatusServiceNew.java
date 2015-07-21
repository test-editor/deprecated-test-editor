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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestStructure;

/**
 * Service for handling team share state.
 *
 */
public interface TeamShareStatusServiceNew {

	/**
	 * 
	 * @param testProject
	 *            {@link TestProject}
	 * 
	 * @throws IOException
	 * 
	 * @return the list of modificated teststructures.
	 */
	List<String> getModified(TestProject testProject);

	/**
	 * Update the internal map of modifications, list must be in synch with SVN
	 * state.
	 * 
	 * @param testProject
	 *            {@link TestProject}
	 * 
	 * @throws FileNotFoundException
	 *             if given project not exists
	 * 
	 */
	void update(TestProject testProject) throws FileNotFoundException;

	/**
	 * Checks if given teststructure is modificated.
	 * 
	 * @param testStructure
	 *            {@link TestStructure}
	 * 
	 * @throws SystemException
	 * @throws IOException
	 * 
	 * @return true if given file is modificated.
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
