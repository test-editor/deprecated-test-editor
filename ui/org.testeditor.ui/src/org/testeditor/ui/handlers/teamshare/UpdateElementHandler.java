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
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.model.team.TeamChange;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.metadata.core.MetaDataService;

/**
 * executes the update-element event.
 * 
 * 
 */
public class UpdateElementHandler extends AbstractUpdateOrApproveHandler {

	private static final Logger LOGGER = Logger.getLogger(UpdateElementHandler.class);

	@Inject
	private TranslationService translate;

	@Inject
	@Optional
	private MetaDataService metaDataService;
	private String teamChangeState;

	@Override
	boolean executeSpecials(TestStructure testStructure) {
		try {
			teamChangeState = getTeamService().update(testStructure, translate);
			if (getMetaDataService() != null) {
				getMetaDataService().refresh(testStructure.getRootElement());
			}
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

	/**
	 * Get the metadata services. Writes a info message if the metadata service
	 * is not there.
	 * 
	 * @return metaDataService
	 */
	private MetaDataService getMetaDataService() {
		if (metaDataService == null) {
			LOGGER.info("MetaDataTabService is not there. Probably the plugin 'org.testeditor.metadata.core' is not activated");
		}
		return metaDataService;
	}
}
