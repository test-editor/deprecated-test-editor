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

import java.util.List;

import org.testeditor.core.constants.TestEditorCoreEventConstants;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestStructure;

/**
 * Service to retrieve the state of a team shared test structures.
 *
 */
public interface TeamShareStatusService {

	/**
	 * Starts the thread to read the SVN status from the given TestProject the
	 * event ({@link TestEditorCoreEventConstants#TEAM_STATE_LOADED}).
	 * 
	 * @param testProject
	 *            TestProject
	 */
	void setTeamStatusForProject(TestProject testProject);

	/**
	 * 
	 * 
	 * @param testStructure
	 *            TestStructure
	 */
	List<String> getModifiedFilesFromTestStructure(TestStructure testStructure);

	/**
	 * checks if the thread is not anymore alive.
	 * 
	 * @return true when the thread is not Alive.
	 */
	boolean isFinish();
}
