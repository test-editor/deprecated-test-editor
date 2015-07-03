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
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.services.interfaces.TeamShareService;
import org.testeditor.core.services.interfaces.TestProjectService;
import org.testeditor.ui.constants.TestEditorConstants;
import org.testeditor.ui.handlers.CanExecuteTestExplorerHandlerRules;
import org.testeditor.ui.parts.testExplorer.TestExplorer;
import org.testeditor.ui.wizardpages.teamshare.TeamShareDisconnectProjectWizardPage;

/**
 * Handler to open a Wizard Dialog to select a Sharing Service to share a
 * Project.
 * 
 * This Handler is enabled an <Code>TestProject</code> entries without a team
 * configuration in the TestExplorer tree.
 * 
 */
public class DisconnectProjectHandler {

	@Inject
	private TeamShareService teamShareService;
	@Inject
	private TestProjectService testProjectService;
	@Inject
	private TranslationService translate;

	@Inject
	@Named(IServiceConstants.ACTIVE_SHELL)
	private Shell shell;

	private TeamShareDisconnectProjectWizardPage teamShareDisconnectProjectPage;

	private static final Logger LOGGER = Logger.getLogger(DisconnectProjectHandler.class);

	/**
	 * Checks if the handler can execute on the current selection in the
	 * TestExplorer.
	 * 
	 * @param context
	 *            current Context of the Application.
	 * @return true if the selection is only one TestProject with a Team Share
	 *         configuration in other cases false.
	 */
	@CanExecute
	public boolean canExecute(IEclipseContext context) {
		TestExplorer testExplorer = (TestExplorer) context.get(TestEditorConstants.TEST_EXPLORER_VIEW);
		CanExecuteTestExplorerHandlerRules handlerRules = new CanExecuteTestExplorerHandlerRules();
		if (handlerRules.canExecuteOnTestProjectRule(testExplorer.getSelection())
				&& handlerRules.canExecuteOnlyOneElementRule(testExplorer.getSelection())) {
			TestProject selection = (TestProject) testExplorer.getSelection().getFirstElement();
			return selection.getTestProjectConfig().isTeamSharedProject();
		}
		return false;
	}

	/**
	 * executes the disconnecting of the project.
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
		teamShareDisconnectProjectPage = ContextInjectionFactory.make(TeamShareDisconnectProjectWizardPage.class,
				context);

		nwiz.addPage(teamShareDisconnectProjectPage);

		// Show the wizard...
		WizardDialog wizardDialog = new WizardDialog(shell, nwiz);

		if (wizardDialog.open() == Window.OK) {
			TestExplorer testExplorer = (TestExplorer) context.get(TestEditorConstants.TEST_EXPLORER_VIEW);
			final TestProject testProject = (TestProject) testExplorer.getSelection().getFirstElement();

			try {
				disconnectProject(testProject);
			} catch (SystemException e) {
				LOGGER.error(e.getMessage(), e);
			}
			testExplorer.refreshTreeInput();
		}
	}

	/**
	 * disconnect the testProject.
	 * 
	 * @param testProject
	 *            TestProject
	 * @throws SystemException
	 *             if the storing of the configuration or the teamsharing fails.
	 */
	private void disconnectProject(TestProject testProject) throws SystemException {
		try {
			teamShareService.disconnect(testProject, translate);
			testProjectService.storeProjectConfig(testProject, testProject.getTestProjectConfig());
		} catch (Exception e) {
			throw new SystemException(e.getLocalizedMessage(), e);
		}
	}

}
