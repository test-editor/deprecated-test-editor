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
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.ui.utilities.TestEditorTranslationService;
import org.testeditor.ui.wizardpages.teamshare.TeamShareApproveWizardPage;

/**
 * executes the approveElement-event.
 * 
 */
public class ApproveElementHandler extends AbstractUpdateOrApproveHandler {

	private static final Logger LOGGER = Logger.getLogger(ApproveElementHandler.class);

	@Inject
	private TestEditorTranslationService translationService;

	@Inject
	private TranslationService translate;

	@Inject
	@Named(IServiceConstants.ACTIVE_SHELL)
	private Shell shell;

	private TeamShareApproveWizardPage approveProjectPage;

	/**
	 * executes the event for the selected-elements.
	 * 
	 * @param context
	 *            IEclipseContext
	 */
	@Execute
	public void execute(IEclipseContext context) {
		EPartService partService = context.get(EPartService.class);
		if (partService.saveAll(true)) {
			// New Wizard
			Wizard nwiz = new Wizard() {

				@Override
				public boolean performFinish() {
					return true;
				}
			};

			// Add the new-page to the wizard
			approveProjectPage = ContextInjectionFactory.make(TeamShareApproveWizardPage.class, context);

			nwiz.addPage(approveProjectPage);

			// Show the wizard...
			WizardDialog wizardDialog = new WizardDialog(shell, nwiz);

			if (wizardDialog.open() == Window.OK) {
				super.execute(context.get(IEventBroker.class));
			}
		}
	}

	@Override
	boolean executeSpecials(TestStructure testStructure) {
		try {
			getTeamService(testStructure).approve(testStructure, translate, approveProjectPage.getSvnComment());
		} catch (final SystemException e) {
			Display.getDefault().syncExec(new Runnable() {

				@Override
				public void run() {
					Shell activeShell = Display.getDefault().getActiveShell();
					MessageDialog.openError(activeShell, translationService.translate("%error"), e.getMessage());
				}
			});
			LOGGER.error(e.getMessage(), e);
			return false;
		}
		return true;
	}

	@Override
	String getMessage() {
		return translationService.translate("%popupmenu.label.approveElement");
	}

	@Override
	void showCompletedMessage() {
		MessageDialog.openInformation(getDisplay().getActiveShell(), translationService.translate("%info"),
				translationService.translate("%approve.completed"));

	}

}
