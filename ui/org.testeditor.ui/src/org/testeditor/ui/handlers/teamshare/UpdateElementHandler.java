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

import java.util.List;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.testeditor.core.constants.TestEditorCoreEventConstants;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.model.team.TeamChange;
import org.testeditor.core.model.team.TeamChangeType;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.ui.utilities.TestEditorTranslationService;

/**
 * executes the update-element event.
 * 
 * 
 */
@SuppressWarnings("restriction")
public class UpdateElementHandler extends AbstractUpdateOrApproveHandler {

	@Inject
	private IEventBroker eventBroker;

	private static final Logger LOGGER = Logger.getLogger(UpdateElementHandler.class);

	@Inject
	private TestEditorTranslationService translationService;

	@Inject
	private TranslationService translate;

	@Override
	boolean executeSpecials(TestStructure testStructure) {
		try {
			List<TeamChange> changes = getTeamService(testStructure).update(testStructure, translate);
			String eventTopic = TestEditorCoreEventConstants.TESTSTRUCTURE_MODEL_CHANGED_UPDATE;
			for (TeamChange teamChange : changes) {
				if (teamChange.getTeamChangeType() == TeamChangeType.DELETE) {
					TestStructure structure = lookUpTestStructureFrom(teamChange);
					LOGGER.trace("SVN deleted Object found: " + structure.getFullName() + " with type: "
							+ structure.getClass());
					if (structure != null && !(structure instanceof TestProject)) {
						eventTopic = TestEditorCoreEventConstants.TESTSTRUCTURE_MODEL_CHANGED_DELETED;
					}
				}
			}
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
				translationService.translate("%update.completed"));
	}

}
