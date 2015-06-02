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
package org.testeditor.ui.handlers.teamshare;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.testeditor.core.constants.TestEditorCoreEventConstants;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.model.team.TeamChange;
import org.testeditor.core.model.teststructure.TestStructure;

/**
 * executes the update-element event.
 * 
 * 
 */
public class UpdateElementHandler extends AbstractUpdateOrApproveHandler {

	@Inject
	private IEventBroker eventBroker;

	private static final Logger LOGGER = Logger.getLogger(UpdateElementHandler.class);

	@Inject
	private TranslationService translate;

	private String teamChangeState;

	@Override
	boolean executeSpecials(TestStructure testStructure) {
		try {
			teamChangeState = getTeamService(testStructure).update(testStructure, translate);
			String eventTopic = TestEditorCoreEventConstants.TESTSTRUCTURE_MODEL_CHANGED_UPDATE_BY_MODIFY;
			eventBroker.post(eventTopic, testStructure.getFullName());
		} catch (final SystemException e) {
			LOGGER.error(e.getMessage(), e);
			getDisplay().syncExec(new Runnable() {

				@Override
				public void run() {
					MessageDialog.openError(getDisplay().getActiveShell(), translationService.translate("%error"),
							e.getMessage());
				}
			});
			return false;
		}
		return true;
	}

	/**
	 * Searches for the teststructure with the path.
	 * 
	 * @param change
	 *            used to find the TestStructure.
	 * @return teststructure found by the path.
	 * @throws SystemException
	 *             on failure of the backend.
	 */
	protected TestStructure lookUpTestStructureFrom(TeamChange change) throws SystemException {
		return change.getReleatedTestStructure();
	}

	@Override
	String getMessage() {
		return translationService.translate("%popupmenu.label.updateElement");
	}

	@Override
	void showCompletedMessage() {
		MessageDialog.openInformation(getDisplay().getActiveShell(), translationService.translate("%info"),
				translationService.translate("%update.completed") + "\n" + teamChangeState);
	}
}
