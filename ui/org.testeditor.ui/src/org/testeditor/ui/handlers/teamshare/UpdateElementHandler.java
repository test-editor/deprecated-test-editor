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
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.exceptions.TeamAuthentificationException;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.metadata.core.MetaDataService;
import org.testeditor.ui.dialogs.TeamShareAuthentificationDialog;

/**
 * executes the update-element event.
 * 
 * 
 */
public class UpdateElementHandler extends AbstractUpdateOrApproveHandler {

	private static final Logger logger = Logger.getLogger(UpdateElementHandler.class);

	@Inject
	private TranslationService translate;

	@Inject
	@Named(IServiceConstants.ACTIVE_SHELL)
	private Shell shell;

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
			logger.error(e.getMessage(), e);
			getDisplay().syncExec(new Runnable() {

				@Override
				public void run() {
					MessageDialog.openError(getDisplay().getActiveShell(), translationService.translate("%error"),
							e.getMessage());
				}
			});
			return false;
		} catch (TeamAuthentificationException e) {
			logger.warn(e.getMessage(), e);
			TeamShareAuthentificationDialog dialog = new TeamShareAuthentificationDialog(shell);
			dialog.create();
			dialog.setTitle("adasd");

			dialog.open();

		}
		return true;
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
			logger.info("MetaDataTabService is not there. Probably the plugin 'org.testeditor.metadata.core' is not activated");
		}
		return metaDataService;
	}
}
