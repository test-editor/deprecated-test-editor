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
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.model.team.TeamChange;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.teamshare.svn.TeamShareStatus;
import org.testeditor.ui.constants.TestEditorUIEventConstants;
import org.testeditor.ui.utilities.TestEditorTranslationService;
import org.testeditor.ui.wizardpages.teamshare.TeamShareRevertWizardPage;

/**
 * executes the revert-element event.
 * 
 * 
 */
@SuppressWarnings("restriction")
public class RevertElementHandler extends AbstractUpdateOrApproveHandler {

	@Inject
	private IEventBroker eventBroker;

	private static final Logger LOGGER = Logger.getLogger(RevertElementHandler.class);

	@Inject
	private TestEditorTranslationService translationService;

	@Inject
	private TranslationService translate;

	@Inject
	@Named(IServiceConstants.ACTIVE_SHELL)
	private Shell shell;

	/**
	 * executes the event for the selected-elements.
	 * 
	 * @param context
	 *            IEclipseContext
	 */
	@Execute
	public void execute(IEclipseContext context) {

		// New Wizard
		Wizard nwiz = new Wizard() {

			@Override
			public boolean performFinish() {
				return true;
			}
		};

		// Add the new-page to the wizard
		TeamShareRevertWizardPage revertProjectPage = ContextInjectionFactory.make(TeamShareRevertWizardPage.class,
				context);

		nwiz.addPage(revertProjectPage);

		// Show the wizard...
		WizardDialog wizardDialog = new WizardDialog(shell, nwiz);

		if (wizardDialog.open() == Window.OK) {
			super.execute(context.get(IEventBroker.class));
		}
	}

	@Override
	boolean executeSpecials(TestStructure testStructure) {

		try {
			List<TeamChange> revertedTestStructurs = getTeamService(testStructure).revert(testStructure, translate);
			for (TeamChange teamChange : revertedTestStructurs) {
				eventBroker.post(TestEditorUIEventConstants.TESTSTRUCTURE_REVERTED,
						teamChange.getRelativeTestStructureFullName());
			}
			TeamShareStatus teamShareStatus = new TeamShareStatus(eventBroker);
			teamShareStatus.setSVNStatusForProject(testStructure.getRootElement());

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
				translationService.translate("%revert.completed"));
	}
}
