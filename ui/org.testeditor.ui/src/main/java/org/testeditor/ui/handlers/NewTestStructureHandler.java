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
package org.testeditor.ui.handlers;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.testeditor.core.constants.TestEditorCoreEventConstants;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.model.teststructure.TestCompositeStructure;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.services.interfaces.TeamShareService;
import org.testeditor.core.services.interfaces.TestProjectService;
import org.testeditor.core.services.interfaces.TestStructureService;
import org.testeditor.ui.constants.TestEditorConstants;
import org.testeditor.ui.constants.TestEditorUIEventConstants;
import org.testeditor.ui.utilities.TestEditorTranslationService;
import org.testeditor.ui.wizardpages.AbstractNewTestStructureWizardPage;
import org.testeditor.ui.wizardpages.AbstractTestStructureWizardPage;
import org.testeditor.ui.wizards.NewTestStructureWizard;

/**
 * 
 * Abstract Class to Create new TestStructures. Subclasses have to provide the
 * concrete teststructure.
 * 
 */
public abstract class NewTestStructureHandler {

	@Inject
	private TestProjectService testProjectService;

	@Inject
	private TestStructureService testStructureService;

	@Inject
	private TeamShareService teamService;

	@Inject
	private TranslationService translationService;

	private TestStructure selectedTestStrucutureElement;

	private String newTestStructureName;

	private static final Logger LOGGER = Logger.getLogger(NewTestStructureHandler.class);

	/**
	 * Executes the new TestStructure actions. Known implementations for
	 * TestCase, TestSuite, TestScenario and Testproject.
	 * 
	 * @param teTranslationService
	 *            internationalization Service
	 * @param workbench
	 *            Workbench-window
	 * @param eventBroker
	 *            {@link IEventBroker}
	 * @param shell
	 *            active shell
	 * @param context
	 *            the actual Eclipse Context.
	 * @return new created TestStructure.
	 */
	@Execute
	public TestStructure execute(TestEditorTranslationService teTranslationService, IWorkbench workbench,
			IEventBroker eventBroker, @Named(IServiceConstants.ACTIVE_SHELL) Shell shell, IEclipseContext context) {
		IStructuredSelection selection = (IStructuredSelection) context
				.get(TestEditorConstants.SELECTED_TEST_COMPONENTS);
		try {
			selectedTestStrucutureElement = findSelectedParent(selection);
		} catch (SystemException exp) {
			MessageDialog.openError(shell, "System-Exception", exp.getLocalizedMessage());
		}

		// New Wizard
		NewTestStructureWizard nwiz = getWizard(context);

		// Set the wizard title
		nwiz.setWindowTitle(teTranslationService.translate(getWindowTitle()));

		// Add the new-page to the wizard
		AbstractTestStructureWizardPage newTestPage = getNewTestStructureWizardPage(selectedTestStrucutureElement,
				context);
		nwiz.addPage(newTestPage);

		// Show the wizard...
		WizardDialog wizardDialog = new WizardDialog(shell, nwiz);
		TestStructure testStructure = null;
		// ...and if it wasn't canceled
		if (wizardDialog.open() == Window.OK) {
			newTestStructureName = nwiz.getNewTestStructureName();
			selectedTestStrucutureElement = nwiz.getNewTestStructureParent();
			testStructure = createNewTestStructure(context);
			if (testStructure != null) {
				testStructure.setName(newTestStructureName);
				addChild(testStructure);
				try {
					createAndOpenTestStructure(testStructure, context);
					eventBroker.send(TestEditorCoreEventConstants.TESTSTRUCTURE_MODEL_CHANGED_UPDATE_BY_ADD,
							testStructure.getFullName());
					eventBroker.send(TestEditorUIEventConstants.ACTIVE_TESTFLOW_EDITOR_CHANGED, testStructure);
				} catch (SystemException e) {
					LOGGER.error(e.getMessage(), e);
					MessageDialog.openError(shell, "System-Exception", e.getMessage());
				}
			}
		}
		return testStructure;
	}

	/**
	 * adds a testStructure local to the teamshare. After a commit its shared in
	 * the repository.
	 * 
	 * @param testStructure
	 *            the TestStructure to add
	 * @param shell
	 *            active Shell.
	 */
	private void addTestStructureLocalToRepository(TestStructure testStructure, Shell shell) {
		try {
			teamService.addChild(testStructure, translationService);
		} catch (SystemException e) {
			LOGGER.error(e);
			MessageDialog.openError(shell, "System-Exception", e.getMessage());
		}
	}

	/**
	 * creates a new TestStructure i.e. TestSuite, TestCase, TestScenario. For
	 * TestProjects there is a special method.
	 * 
	 * @param testStructure
	 *            the TestStructure
	 * @param context
	 *            the actual Eclipse Context.
	 * 
	 * @throws SystemException
	 *             , if the creation failed
	 */
	protected void createAndOpenTestStructure(TestStructure testStructure, IEclipseContext context)
			throws SystemException {
		testStructureService.create(testStructure);
	}

	/**
	 * adds a child to the selected TestStructure.
	 * 
	 * @param testStructure
	 *            TestStructure
	 */
	protected void addChild(TestStructure testStructure) {
		((TestCompositeStructure) selectedTestStrucutureElement).addChild(testStructure);
	}

	/**
	 * Sets the name of the new teststructure.
	 * 
	 * @param name
	 *            to be set as new name.
	 */
	protected void setNewTestStructureName(String name) {
		newTestStructureName = name;
	}

	/**
	 * New Operations only on TestSuites, TestProjects or TestKomponenten
	 * possible.
	 * 
	 * @param context
	 *            Eclipse Context
	 * @return if the selection is a TestSuite
	 */
	@CanExecute
	public boolean canExecute(IEclipseContext context) {
		IStructuredSelection selection = (IStructuredSelection) context
				.get(TestEditorConstants.SELECTED_TEST_COMPONENTS);
		CanExecuteTestExplorerHandlerRules handlerRules = new CanExecuteTestExplorerHandlerRules();
		return handlerRules.canExecuteOnTestSuiteRule(selection) || handlerRules.canExecuteOnTestProjectRule(selection);
	}

	/**
	 * Find the selected Element in the Tree. If no tree is available or no
	 * Element is selected then return the root Element from the
	 * TeststructureService.
	 * 
	 * @param selection
	 *            TestExplorer View from the Workbench.
	 * @return the possible Parent for the new teststructure.
	 * @throws SystemException
	 *             thrown by the TeststructureService.
	 */
	protected TestStructure findSelectedParent(IStructuredSelection selection) throws SystemException {
		if (selection == null) {
			return getRootElementFromTestStructureService();
		}
		TreePath[] treePaths = ((ITreeSelection) selection).getPaths();
		if (treePaths.length == 0) {
			return getRootElementFromTestStructureService();
		}
		return (TestStructure) treePaths[0].getLastSegment();
	}

	/**
	 * 
	 * @return Root Element of the TestStructure Service or null if the service
	 *         has no elements.
	 */
	protected TestStructure getRootElementFromTestStructureService() {
		if (testProjectService.getProjects().isEmpty()) {
			return null;
		}
		return testProjectService.getProjects().get(0);
	}

	/**
	 * this abstract method should be implemented by the child. In the
	 * implementation it should get the right child of the
	 * {@link AbstractNewTestStructureWizardPage}
	 * 
	 * @param selectedTS
	 *            TestStructure
	 * @param context
	 *            the actual Eclipse Context.
	 * @return the NewTestStructureWizardPage.
	 */
	protected abstract AbstractNewTestStructureWizardPage getNewTestStructureWizardPage(TestStructure selectedTS,
			IEclipseContext context);

	/**
	 * 
	 * @return Depending in the teststructure type should it be opend in the
	 *         editor.
	 */
	protected abstract boolean canOpenObject();

	/**
	 * Subclasses must implements this Method.
	 * 
	 * @return the Window Title of the New Wizard.
	 */
	protected abstract String getWindowTitle();

	/**
	 * Subclasses implements this method to determine the concrete
	 * Teststructure.
	 * 
	 * @param context
	 *            the acutal Eclipse Context
	 * 
	 * @return a New TestStructure object for the new Operation.
	 */
	protected abstract TestStructure createNewTestStructure(IEclipseContext context);

	/**
	 * @param context
	 *            the actual EclipseContext.
	 * 
	 * @return the Wizard for the special handler. Should be implemented in the
	 *         subclass.
	 */
	protected NewTestStructureWizard getWizard(IEclipseContext context) {
		return new NewTestStructureWizard();
	}

	/**
	 * 
	 * @return TestProjectService used by subclasses.
	 */
	protected TestProjectService getTestProjectService() {
		return testProjectService;
	}

}
