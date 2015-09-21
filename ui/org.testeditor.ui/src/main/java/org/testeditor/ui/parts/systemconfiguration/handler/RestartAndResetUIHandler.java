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
package org.testeditor.ui.parts.systemconfiguration.handler;

import javax.inject.Named;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.osgi.service.prefs.BackingStoreException;
import org.testeditor.core.services.interfaces.TestEditorConfigurationService;
import org.testeditor.ui.utilities.TestEditorTranslationService;

/**
 * 
 * Handler to send a restart command to the framework with a clear persistence
 * command.
 * 
 */
public class RestartAndResetUIHandler {

	private static final Logger LOGGER = Logger.getLogger(RestartAndResetUIHandler.class);

	/**
	 * Restarts the Application.
	 * 
	 * @param workbench
	 *            to be restarted.
	 * @param shell
	 *            activeShell to open a confirm Dialog.
	 * 
	 * @param translate
	 *            TestEditor Translation Service to translate the confirmation
	 *            dialog.
	 * @param partService
	 *            to store opened editor documents.
	 * 
	 * @param testEditorConfigService
	 *            service to store the reset application state on restart.
	 */
	@Execute
	public void resetAndRestart(@Optional IWorkbench workbench,
			@Optional @Named(IServiceConstants.ACTIVE_SHELL) Shell shell, TestEditorTranslationService translate,
			TestEditorConfigurationService testEditorConfigService, EPartService partService) {
		if (shell != null) {
			if (!MessageDialog.openConfirm(shell, translate.translate("%reset_restart_title"),
					translate.translate("%reset_restart_messagebox"))) {
				return;
			}
		}
		try {
			testEditorConfigService.setResetApplicationState(true);
			partService.saveAll(true);
			workbench.restart();
		} catch (BackingStoreException e) {
			LOGGER.error("Error storing the restart command. Restart aborted.", e);
			MessageDialog.openError(shell, translate.translate("%error"), translate.translate("%error.restart"));
		}
	}

}