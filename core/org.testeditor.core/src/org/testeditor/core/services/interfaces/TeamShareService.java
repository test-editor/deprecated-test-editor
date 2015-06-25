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

import org.eclipse.e4.core.services.translation.TranslationService;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.exceptions.TeamAuthentificationException;
import org.testeditor.core.model.team.TeamChange;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestStructure;

/**
 * The Team Share Service is used to synchronize TestProjects in a Team.
 * 
 * A TestProject needs a <code>TeamShareConfig</code> with the Configuration of
 * the TeamShare Plug-In that provides an implementation of this interface.
 * 
 * This Service allows to approve Tests to the team and update Tests from the
 * Team.
 * 
 * This interface is intended to be used by clients and not to be implemented by
 * plug-ins. Plug-In developer should implement the interface:
 * TeamShareServicePlugIn.
 *
 */
public interface TeamShareService {

	/**
	 * Disconnect a Testproject from the Team Share Backendsystem.
	 * 
	 * @param testProject
	 *            to be disconnect.
	 * @param translationService
	 *            {@link TranslationService}
	 * 
	 * @throws SystemException
	 *             if the sharing of the project fails
	 */
	void disconnect(TestProject testProject, TranslationService translationService) throws SystemException;

	/**
	 * Shares a TestProject with a Team Share Backendsystem.
	 * 
	 * @param testProject
	 *            to be shared.
	 * @param comment
	 *            String comment for share.
	 * @param translationService
	 *            {@link TranslationService}
	 * @throws SystemException
	 *             if the sharing of the project fails
	 */
	void share(TestProject testProject, TranslationService translationService, String comment) throws SystemException;

	/**
	 * Approve the Changes of a <code>TestStructure</code> and all it children's
	 * to the Team Share System.
	 * 
	 * @param testStructure
	 *            to be approved.
	 * @param comment
	 *            String comment for the Team Share system.
	 * @param translationService
	 *            {@link TranslationService}
	 * @throws SystemException
	 *             if the sharing of the project fails
	 * @return state Information of the approve operation.
	 */
	String approve(TestStructure testStructure, TranslationService translationService, String comment)
			throws SystemException;

	/**
	 * Update the Changes of a <code>TestStructure</code> and all it children's
	 * in the local Workspace with the actual state of the Team Share System.
	 * 
	 * @param testStructure
	 *            to be updated.
	 * @param translationService
	 *            {@link TranslationService}
	 * @throws SystemException
	 *             if the sharing of the project fails
	 * @return state Information of the update operation.
	 */
	String update(TestStructure testStructure, TranslationService translationService) throws SystemException;

	/**
	 * This method imports a new project from the teamshare-repository and put
	 * it into the local workspace.
	 * 
	 * @param testProject
	 *            project to be import in workspace
	 * @param translationService
	 *            {@link TranslationService}
	 * 
	 * @throws SystemException
	 *             if the import doesn't works
	 * @throws TeamAuthentificationException
	 *             will be thrown when authentification failed
	 */
	void checkout(TestProject testProject, TranslationService translationService) throws SystemException,
			TeamAuthentificationException;

	/**
	 * deletes the {@link TestStructure} given by the parameter.
	 * 
	 * @param testStructure
	 *            {@link TestStructure}
	 * @param translationService
	 *            {@link TranslationService}
	 * @throws SystemException
	 *             if the deletion fails
	 */
	void delete(TestStructure testStructure, TranslationService translationService) throws SystemException;

	/**
	 * gets the status of the {@link TestStructure}.
	 * 
	 * @param testStructure
	 *            {@link TestStructure}
	 * @param translationService
	 *            {@link TranslationService}
	 * @throws SystemException
	 *             if the reading of the status fails
	 * @return the status of the {@link TestStructure} and its children
	 */
	String getStatus(TestStructure testStructure, TranslationService translationService) throws SystemException;

	/**
	 * 
	 * @param testStructure
	 *            used to work on with the team share service.
	 * @param listener
	 *            ProgressListener
	 */
	void addProgressListener(TestStructure testStructure, ProgressListener listener);

	/**
	 * adds a child to the parent in the local structure.
	 * 
	 * @param testStructureChild
	 *            child TestStructure
	 * @param translationService
	 *            {@link TranslationService}
	 * @throws SystemException
	 *             if the adding of the child fails
	 */
	void addChild(TestStructure testStructureChild, TranslationService translationService) throws SystemException;

	/**
	 * Validates a TeamShareConfig. It checks that it is possible to connect a
	 * TeamShare Server with the given teamShareConfig.
	 * 
	 * @param testProject
	 *            used to connect to a TeamShare Server.
	 * 
	 * @param translationService
	 *            used to translate team server error messages.
	 * @throws SystemException
	 *             on error connecting to the TeamShare Server.
	 * 
	 * @return true if the connect was successfully.
	 */
	boolean validateConfiguration(TestProject testProject, TranslationService translationService)
			throws SystemException;

	/**
	 * reverts the local changes recursive from the teststructure.
	 * 
	 * @param testStructure
	 *            to revert
	 * @param translationService
	 *            used to translate team server error messages.
	 * @return List of Team Changes created by this revert operation.
	 * @throws SystemException
	 *             on error reverting the local changes
	 */
	List<TeamChange> revert(TestStructure testStructure, TranslationService translationService) throws SystemException;

	/**
	 * Informs the Team server about a renaming operation and renames the
	 * teststrutcure.
	 * 
	 * @param testStructure
	 *            to be renamed
	 * @param newName
	 *            for the teststructure
	 * @param translationService
	 *            used to translate team server error messages.
	 * @throws SystemException
	 *             on error renaming teststructure.
	 */
	void rename(TestStructure testStructure, String newName, TranslationService translationService)
			throws SystemException;

}
