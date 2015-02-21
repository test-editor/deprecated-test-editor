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
package org.testeditor.ui.handlers.rename;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.services.interfaces.TestProjectService;
import org.testeditor.ui.TestServerStarter;
import org.testeditor.ui.constants.TestEditorConstants;
import org.testeditor.ui.handlers.CanExecuteTestExplorerHandlerRules;
import org.testeditor.ui.parts.testExplorer.TestExplorer;
import org.testeditor.ui.utilities.TestEditorTranslationService;
import org.testeditor.ui.wizardpages.AbstractRenameTestStructureWizardPage;
import org.testeditor.ui.wizardpages.RenameTestProjectWizardPage;

/**
 * This is a handler for renaming an existing test Project.
 */
public class RenameTestProjectHandler extends AbstractRenameHandler {

	@Inject
	private IEclipseContext context;

	@Inject
	private TestProjectService testProjectService;
	@Inject
	private TestEditorTranslationService translationService;

	private static final Logger LOGGER = Logger.getLogger(RenameTestProjectHandler.class);

	@Override
	protected void executeSpecials(TestStructure selected, String sbname) {

		if (selected instanceof TestProject) {
			Shell shell = Display.getCurrent().getActiveShell();

			try {
				// now stop the fitness-server for the project
				TestServerStarter serverStarter = ContextInjectionFactory.make(TestServerStarter.class, context);
				// close the project
				serverStarter.stopTestServer((TestProject) selected);

				TestProject renamedTestproject = testProjectService.renameTestproject((TestProject) selected, sbname);
				if (renamedTestproject != null) {
					serverStarter.startTestServer(renamedTestproject);

				} else {

					LOGGER.error(translationService.translate("%wizard.error.msgRenameProjectFailed"));
					MessageDialog.openError(shell,
							translationService.translate("%wizard.error.msgRenameProjectFailed"),
							translationService.translate("%wizard.error.msgRenameProjectFailed"));
				}

			} catch (IOException e) {
				LOGGER.error(translationService.translate("%wizard.error.msgRenameProjectFailed"), e);
				MessageDialog.openError(shell, translationService.translate("%wizard.error.msgRenameProjectFailed"),
						translationService.translate("%wizard.error.msgRenameProjectFailed") + "\n" + e.getMessage());
			} catch (SystemException e) {
				LOGGER.error(translationService.translate("%wizard.error.msgRenameProjectFailed"), e);
				MessageDialog.openError(shell, translationService.translate("%wizard.error.msgRenameProjectFailed"),
						translationService.translate("%wizard.error.msgRenameProjectFailed") + "\n" + e.getMessage());
			} catch (URISyntaxException e) {
				LOGGER.error(translationService.translate("%wizard.error.msgRenameProjectFails"), e);
				MessageDialog.openError(shell, translationService.translate("%wizard.error.msgRenameProjectFails"),
						translationService.translate("%wizard.error.msgRenameProjectFails") + "\n" + e.getMessage());
			}
		}
	}

	@Override
	protected AbstractRenameTestStructureWizardPage getRenameTestStructureWizardPage(TestStructure selectedTS) {
		RenameTestProjectWizardPage renameTestProjectWizardPage = ContextInjectionFactory.make(
				RenameTestProjectWizardPage.class, context);
		renameTestProjectWizardPage.setSelectedTestStructure(selectedTS);
		return renameTestProjectWizardPage;
	}

	/**
	 * overriding the method of the parent class, without an operation. The
	 * renaming of the testproject will be done later.
	 * 
	 * @param selectedTestStructure
	 *            the selected testttructure.
	 * @param sbname
	 *            the new name
	 * @throws SystemException
	 *             on restOperations
	 */
	@Override
	protected void executeRenaming(TestStructure selectedTestStructure, String sbname) throws SystemException {
	}

	@Override
	@Execute
	public boolean canExecute(IEclipseContext context) {
		TestExplorer explorer = (TestExplorer) context.get(TestEditorConstants.TEST_EXPLORER_VIEW);
		CanExecuteTestExplorerHandlerRules rules = ContextInjectionFactory.make(
				CanExecuteTestExplorerHandlerRules.class, context);
		return super.canExecute(context)
				& (rules.canExecuteOnTestProjectRule(explorer) & ((TestStructure) explorer.getSelection()
						.getFirstElement()).getRootElement().getTestProjectConfig().getTeamShareConfig() == null);
	}
}
