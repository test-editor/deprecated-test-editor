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
package org.testeditor.core.services.dispatcher;

import java.util.List;

import org.eclipse.e4.core.services.translation.TranslationService;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.exceptions.TeamAuthentificationException;
import org.testeditor.core.model.team.TeamChange;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.services.interfaces.ProgressListener;
import org.testeditor.core.services.interfaces.TeamShareService;

public class TeamShareServiceDispatcher implements TeamShareService {

	@Override
	public void disconnect(TestProject testProject, TranslationService translationService) throws SystemException {
		// TODO Auto-generated method stub

	}

	@Override
	public void share(TestProject testProject, TranslationService translationService, String comment)
			throws SystemException {
		// TODO Auto-generated method stub

	}

	@Override
	public String approve(TestStructure testStructure, TranslationService translationService, String comment)
			throws SystemException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String update(TestStructure testStructure, TranslationService translationService) throws SystemException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void checkout(TestProject testProject, TranslationService translationService) throws SystemException,
			TeamAuthentificationException {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete(TestStructure testStructure, TranslationService translationService) throws SystemException {
		// TODO Auto-generated method stub

	}

	@Override
	public String getStatus(TestStructure testStructure, TranslationService translationService) throws SystemException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addProgressListener(ProgressListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addChild(TestStructure testStructureChild, TranslationService translationService)
			throws SystemException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean validateConfiguration(TestProject testProject, TranslationService translationService)
			throws SystemException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<TeamChange> revert(TestStructure testStructure, TranslationService translationService)
			throws SystemException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void rename(TestStructure testStructure, String newName, TranslationService translationService)
			throws SystemException {
		// TODO Auto-generated method stub

	}

}
