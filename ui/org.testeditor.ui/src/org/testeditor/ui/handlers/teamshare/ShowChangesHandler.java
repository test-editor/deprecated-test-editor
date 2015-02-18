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

import java.util.Iterator;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.teamshare.svn.SVNTeamShareService;
import org.testeditor.teamshare.svn.TeamShareStatus;
import org.testeditor.ui.constants.TestEditorConstants;
import org.testeditor.ui.parts.testExplorer.TestExplorer;
import org.testeditor.ui.wizardpages.teamshare.TeamShareShowChangesWizardPage;

/**
 * handler to show the changed files.
 * 
 * @author dkuhlmann
 *
 */
public class ShowChangesHandler extends AbstractUpdateOrApproveHandler {
	@Inject
	private MApplication application;

	// @Inject
	// private IEventBroker eventBroker;
	//
	// private static final Logger LOGGER =
	// Logger.getLogger(RevertElementHandler.class);
	//
	// @Inject
	// private TestEditorTranslationService translationService;
	//
	// @Inject
	// private TranslationService translate;
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
		TeamShareShowChangesWizardPage showChangesPage = ContextInjectionFactory.make(
				TeamShareShowChangesWizardPage.class, context);

		EPartService partService = application.getSelectedElement().getContext().get(EPartService.class);
		final TestExplorer explorer = (TestExplorer) partService.findPart(TestEditorConstants.TEST_EXPLORER_VIEW)
				.getObject();
		final IStructuredSelection selection = explorer.getSelection();
		@SuppressWarnings("rawtypes")
		final Iterator iter = selection.iterator();
		TestStructure testStructure = (TestStructure) iter.next();
		showChangesPage.setTestStructure(testStructure);
		nwiz.addPage(showChangesPage);

		// Show the wizard...
		WizardDialog wizardDialog = new WizardDialog(shell, nwiz);

		wizardDialog.open();
	}

	@Override
	String getMessage() {
		return null;
	}

	@Override
	boolean executeSpecials(TestStructure testStructure) {
		return true;
	}

	@Override
	void showCompletedMessage() {
	}
}
