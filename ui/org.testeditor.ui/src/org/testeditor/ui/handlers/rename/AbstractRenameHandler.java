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

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.model.teststructure.TestCase;
import org.testeditor.core.model.teststructure.TestFlow;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestScenario;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.model.teststructure.TestSuite;
import org.testeditor.core.services.interfaces.TestStructureService;
import org.testeditor.metadata.core.MetaDataService;
import org.testeditor.ui.ITestStructureEditor;
import org.testeditor.ui.constants.TestEditorConstants;
import org.testeditor.ui.handlers.CanExecuteTestExplorerHandlerRules;
import org.testeditor.ui.handlers.OpenTestStructureHandler;
import org.testeditor.ui.parts.editor.view.TestEditorTestCaseController;
import org.testeditor.ui.parts.editor.view.TestEditorTestScenarioController;
import org.testeditor.ui.parts.projecteditor.TestProjectEditor;
import org.testeditor.ui.parts.testExplorer.TestExplorer;
import org.testeditor.ui.parts.testsuite.TestSuiteEditor;
import org.testeditor.ui.utilities.TestEditorTranslationService;
import org.testeditor.ui.wizardpages.AbstractRenameTestStructureWizardPage;
import org.testeditor.ui.wizardpages.AbstractTestStructureWizardPage;

/**
 * Abstract rename-handler.
 * 
 * 
 */
public abstract class AbstractRenameHandler {

	@Inject
	private TestStructureService testStructureService;
	@Inject
	private EPartService partService;
	@Inject
	private MetaDataService metaDataService;

	private boolean selectedStructureWasOpen = false;

	/**
	 * execute method.
	 * 
	 * @param context
	 *            the {@link IEclipseContext}
	 * 
	 * @param translationService
	 *            the {@link TestEditorTranslationService}
	 * 
	 * @param shell
	 *            the active shell
	 */
	@Execute
	public void execute(IEclipseContext context, TestEditorTranslationService translationService,
			@Named(IServiceConstants.ACTIVE_SHELL) Shell shell) {

		// Buffer holding the new name
		final StringBuffer sbname = new StringBuffer("");

		// Receive TreeViewer object via eclipse-context
		TestExplorer explorer = (TestExplorer) context.get(TestEditorConstants.TEST_EXPLORER_VIEW);
		TestStructure selected = (TestStructure) explorer.getSelection().getFirstElement();

		// New Wizard
		Wizard nwiz = new Wizard() {
			/**
			 * Called before wizard is closed
			 */
			@Override
			public boolean performFinish() {
				// Receive the new name
				String str = ((AbstractTestStructureWizardPage) this.getPages()[0]).getTextInNameText();
				// Copy the name into the buffer
				sbname.replace(0, str.length(), str);
				return true;
			}
		};

		// // Set the wizard title
		// nwiz.setWindowTitle(translationService.translate("%popupmenu.label.rename.item"));
		// Add the new-page to the wizard
		AbstractTestStructureWizardPage renameTestPage = getRenameTestStructureWizardPage(selected);
		nwiz.addPage(renameTestPage);

		renameTestPage.setSelectedTestStructure(selected);

		WizardDialog wizardDialog = new WizardDialog(shell, nwiz);
		// ...and if it wasn't canceled
		if (wizardDialog.open() == Window.OK) {

			saveAndCloseTestStructureBeforeRename(selected, true);

			try {
				executeRenaming(selected, sbname.toString());
				executeSpecials(selected, sbname.toString());

			} catch (SystemException e) {
				MessageDialog.openError(shell, "System-Exception", e.getLocalizedMessage());
			}

			if (!(selected instanceof TestProject)) {
				refreshTestStructureInEditor(selected, sbname.toString(), context);
			}

			explorer.setSelectionOn(selected);
		}
	}

	/**
	 * executes the renaming of the teststructure.
	 * 
	 * @param selectedTestStructure
	 *            the teststructure to rename
	 * @param sbname
	 *            the new name
	 * @throws SystemException
	 *             while file-operations
	 */
	protected void executeRenaming(TestStructure selectedTestStructure, String sbname) throws SystemException {
		metaDataService.rename(selectedTestStructure, sbname);
		testStructureService.renameTestStructure(selectedTestStructure, sbname);
	}

	/**
	 * after the renaming of the test-structure there mid needed some special
	 * operations for the specific class.
	 * 
	 * @param selected
	 *            {@link TestStructure}
	 * @param sbname
	 *            {@link String}
	 * @throws SystemException
	 *             if the special operation failed.
	 */
	protected abstract void executeSpecials(TestStructure selected, String sbname) throws SystemException;

	/**
	 * if the teststructure is changed, the user will be ask to save before run
	 * the teststructure.
	 * 
	 * @param selected
	 *            selected teststructure in the tree
	 * @param closeChild
	 *            if true, the child should be closed.
	 * @return the saved teststructure
	 */
	private TestStructure saveAndCloseTestStructureBeforeRename(TestStructure selected, boolean closeChild) {
		Collection<MPart> parts = partService.getParts();

		if (isSelectedtTestProjectCaseOrScenario(selected)) {
			for (MPart mPart : parts) {

				if (isTestStructure(mPart) && (mPart.getObject()) != null) {

					if (mPart.getObject() instanceof ITestStructureEditor) {
						if (((ITestStructureEditor) mPart.getObject()).getTestStructure().getFullName()
								.startsWith(selected.getFullName())) {
							if (mPart.isDirty()) {
								partService.savePart(mPart, true);
							}
							if (closeChild) {
								partService.hidePart(mPart, true);
							}
						}
					}

				}
			}

		}
		return selected;
	}

	/**
	 * 
	 * @param selected
	 *            element of the tree
	 * @return true, if the selected is a TestProject, TestCase or TestScenario
	 */
	private boolean isSelectedtTestProjectCaseOrScenario(TestStructure selected) {
		return selected instanceof TestCase || selected instanceof TestScenario || selected instanceof TestProject
				|| selected instanceof TestSuite;
	}

	/**
	 * 
	 * @param mPart
	 *            MPart
	 * @return true if the part shows a TestCase or TestScenario
	 */
	private boolean isTestStructure(MPart mPart) {
		return mPart.getElementId().equals(TestEditorTestCaseController.ID)
				|| mPart.getElementId().equals(TestEditorTestScenarioController.ID)
				|| mPart.getElementId().equals(TestSuiteEditor.ID) || mPart.getElementId().equals(TestProjectEditor.ID);
	}

	/**
	 * Check if this Handler is enabled on the selection. Only one Teststrucutre
	 * is valid as a selection.
	 * 
	 * @param context
	 *            Eclipse Context to retrive the Viewer
	 * @return true if only one element is selected.
	 */
	@CanExecute
	public boolean canExecute(IEclipseContext context) {
		TestExplorer explorer = (TestExplorer) context.get(TestEditorConstants.TEST_EXPLORER_VIEW);
		CanExecuteTestExplorerHandlerRules rules = ContextInjectionFactory.make(
				CanExecuteTestExplorerHandlerRules.class, context);
		return rules.canExecuteOnlyOneElementRule(explorer) && !rules.canExecuteOnProjectMainScenarioSuite(explorer)
				&& rules.canExecuteOnUnusedScenario(explorer) && rules.canExecuteOnNonScenarioSuiteParents(explorer);
	}

	/**
	 * Refresh Tree with renamed element.
	 * 
	 * @param selectedTeststructure
	 *            Element
	 * @param newName
	 *            the new Name
	 * @param context
	 *            the Eclipse Context
	 */
	private void refreshTestStructureInEditor(TestStructure selectedTeststructure, String newName,
			IEclipseContext context) {
		MPart mpart = searchEditorFor(selectedTeststructure);
		if (mpart != null) {
			partService.hidePart(mpart, true);
			selectedStructureWasOpen = true;
		}
		selectedTeststructure.setName(newName);
		if (selectedStructureWasOpen) {
			if (selectedTeststructure instanceof TestFlow) {
				ContextInjectionFactory.make(OpenTestStructureHandler.class, context).execute(
						(TestFlow) selectedTeststructure, context);
			} else if (selectedTeststructure instanceof TestSuite) {
				ContextInjectionFactory.make(OpenTestStructureHandler.class, context).execute(
						(TestSuite) selectedTeststructure, context);
			} else if (selectedTeststructure instanceof TestProject) {
				ContextInjectionFactory.make(OpenTestStructureHandler.class, context).execute(
						(TestProject) selectedTeststructure, context);
			}
		}
	}

	/**
	 * Searches all open Editor to find one wich is open for the
	 * selectedTestStructure.
	 * 
	 * @param selectedTeststructure
	 *            to be opened in an Editor.
	 * @return the Mpart of the Editor or null if no one is opned.
	 */
	private MPart searchEditorFor(TestStructure selectedTeststructure) {
		List<String> editorIDList = Arrays.asList(new String[] { TestEditorTestCaseController.ID,
				TestEditorTestScenarioController.ID, TestProjectEditor.ID, TestSuiteEditor.ID });
		Collection<MPart> parts = partService.getParts();
		for (MPart mPart2 : parts) {
			if (editorIDList.contains(mPart2.getElementId())) {
				ITestStructureEditor tsEditor = (ITestStructureEditor) mPart2.getObject();
				if (tsEditor.getTestStructure().equals(selectedTeststructure)) {
					return mPart2;
				}
			}
		}
		return null;
	}

	/**
	 * this abstract method should be implemented by the child. In the
	 * implementation it should get the right child of the
	 * {@link AbstractRenameTestStructureWizardPage}
	 * 
	 * @param selectedTS
	 *            TestStructure
	 * @return the NewTestStructureWizardPage.
	 */
	protected abstract AbstractRenameTestStructureWizardPage getRenameTestStructureWizardPage(TestStructure selectedTS);

}
