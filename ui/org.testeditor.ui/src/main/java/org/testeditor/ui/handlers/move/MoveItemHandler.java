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
package org.testeditor.ui.handlers.move;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.model.teststructure.TestCase;
import org.testeditor.core.model.teststructure.TestCompositeStructure;
import org.testeditor.core.model.teststructure.TestFlow;
import org.testeditor.core.model.teststructure.TestScenario;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.services.interfaces.TestEditorConfigurationService;
import org.testeditor.core.services.interfaces.TestStructureService;
import org.testeditor.metadata.core.MetaDataService;
import org.testeditor.metadata.core.model.MetaDataTag;
import org.testeditor.ui.constants.TestEditorConstants;
import org.testeditor.ui.handlers.CanExecuteTestExplorerHandlerRules;
import org.testeditor.ui.parts.testExplorer.TestExplorer;
import org.testeditor.ui.utilities.TestEditorTranslationService;
import org.testeditor.ui.wizardpages.AbstractNewTestStructureWizardPage;
import org.testeditor.ui.wizardpages.AbstractTestStructureWizardPage;
import org.testeditor.ui.wizardpages.MoveScenarioWizardPage;
import org.testeditor.ui.wizardpages.MoveTestCaseWizardPage;
import org.testeditor.ui.wizards.MoveItemWizard;

/**
 * rename-handler for the testcases.
 * 
 */
public class MoveItemHandler {

	private static final Logger logger = Logger.getLogger(MoveItemHandler.class);

	@Inject
	private TestStructureService testStructureService;

	@Inject
	@Optional
	private MetaDataService metaDataService;

	@Inject
	private TestEditorTranslationService translationService;

	@Inject
	private TestEditorConfigurationService testEditorConfigService;

	/**
	 * Enables the complete button only if a folder is selected.
	 * 
	 * @param context
	 *            - the context
	 * @return true if a folder is selected
	 */
	@CanExecute
	public boolean canExecute(IEclipseContext context) {
		TestExplorer explorer = (TestExplorer) context.get(TestEditorConstants.TEST_EXPLORER_VIEW);
		CanExecuteTestExplorerHandlerRules rules = ContextInjectionFactory
				.make(CanExecuteTestExplorerHandlerRules.class, context);
		if (rules.canExecuteOnTeamShareProject(explorer.getSelection()) && !testEditorConfigService.isAdminUser()) {
			return false;
		}
		IStructuredSelection selection = (IStructuredSelection) context
				.get(TestEditorConstants.SELECTED_TEST_COMPONENTS);
		TestFlow lastSelection = (TestFlow) selection.getFirstElement();
		return lastSelection instanceof TestCase || lastSelection instanceof TestScenario;
	}

	/**
	 * executes the disconnecting of the project.
	 * 
	 * @param context
	 *            IEclipseContext
	 * @param shell
	 *            - the shell
	 */
	@Execute
	public void execute(IEclipseContext context, @Named(IServiceConstants.ACTIVE_SHELL) Shell shell) {

		IStructuredSelection selection = (IStructuredSelection) context
				.get(TestEditorConstants.SELECTED_TEST_COMPONENTS);
		final TestStructure testStructure = (TestStructure) selection.getFirstElement();

		final MoveItemWizard nwiz = new MoveItemWizard();
		nwiz.setWindowTitle(translationService.translate("%popupmenu.label.move.item"));

		// Add the new-page to the wizard
		AbstractTestStructureWizardPage newTestPage = getNewTestStructureWizardPage(testStructure, context);
		nwiz.addPage(newTestPage);

		// Show the wizard...
		WizardDialog wizardDialog = new WizardDialog(shell, nwiz);
		final List<String> changedItems = new ArrayList<String>();
		if (wizardDialog.open() == Window.OK) {
			try {
				new ProgressMonitorDialog(shell).run(true, false, new IRunnableWithProgress() {

					@Override
					public void run(final IProgressMonitor monitor)
							throws InvocationTargetException, InterruptedException {
						try {

							monitor.beginTask(translationService.translate("%progressmonitor.label.move.item")
									+ testStructure.getFullName(), IProgressMonitor.UNKNOWN);
							List<MetaDataTag> metaDataTags = null;
							if (getMetaDataService() != null) {
								metaDataTags = getMetaDataService().getMetaDataTags(testStructure);
							}
							changedItems.addAll(testStructureService.move(testStructure,
									(TestCompositeStructure) nwiz.getNewTestStructureParent()));
							if (metaDataTags != null) {
								getMetaDataService().storeMetaDataTags(metaDataTags, new ArrayList<MetaDataTag>(),
										testStructure);
							}
							monitor.done();
						} catch (SystemException e) {
							throw new InvocationTargetException(e);
						}
					}
				});
			} catch (InvocationTargetException e) {
				logger.error(e.getMessage(), e);
				MessageDialog.openError(shell, "System-Exception", e.getLocalizedMessage());
			} catch (InterruptedException e) {
				logger.error(e.getMessage(), e);
			}
			String msg = "";
			if (testStructure.getRootElement().getTestProjectConfig().getTeamShareConfig() != null) {
				if (changedItems.size() > 0) {
					msg = translationService.translate("%move.success.msg");
					for (String changedItem : changedItems) {
						msg += changedItem + "\n";
					}
				}
				showWarningMessage(msg);
			}
		}

		return;

	}

	/**
	 * create a new wizard page based on the type of the selection.
	 * 
	 * @param selectedTS
	 *            - the selected testStructure
	 * @param context
	 *            - eclipse context (it is needed to access beans created in the
	 *            context).
	 * @return - the new page
	 */
	protected AbstractNewTestStructureWizardPage getNewTestStructureWizardPage(TestStructure selectedTS,
			IEclipseContext context) {
		AbstractNewTestStructureWizardPage moveItemWizardPage = null;

		if (selectedTS instanceof TestCase) {
			moveItemWizardPage = ContextInjectionFactory.make(MoveTestCaseWizardPage.class, context);
		} else if (selectedTS instanceof TestScenario) {
			moveItemWizardPage = ContextInjectionFactory.make(MoveScenarioWizardPage.class, context);
		} else {
			throw new IllegalArgumentException("Illegal type " + selectedTS.getClass().getName());
		}

		moveItemWizardPage.setRenderNameField(false);
		moveItemWizardPage.setSelectedTestStructure(selectedTS);
		return moveItemWizardPage;
	}

	/**
	 * Getter for the metaData Service. Checks if the service is set. If the
	 * service is not there, an info-message is displayed and null will be
	 * returned
	 * 
	 * @return the service
	 */
	private MetaDataService getMetaDataService() {
		if (metaDataService == null) {
			logger.info(
					"MetaDataTabService is not there. Probably the plugin 'org.testeditor.metadata.core' is not activated");
		}
		return metaDataService;

	}

	/**
	 * Shows a general warning message to the user to reduce problems with
	 * subversion and renaming.
	 * 
	 * @param msg
	 *            - a message containing all changed items
	 */
	private void showWarningMessage(String msg) {
		MessageDialog.openWarning(getDisplay().getActiveShell(), translationService.translate("%warn"),
				translationService.translate("%move.teamshare.warning") + "\n\n" + msg);
	}

	/**
	 * gets the display.
	 * 
	 * @return the display
	 */
	private static Display getDisplay() {
		Display display = Display.getCurrent();
		// may be null if outside the UI thread
		if (display == null) {
			display = Display.getDefault();
		}
		return display;
	}

}
