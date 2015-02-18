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

import java.io.IOException;
import java.net.URISyntaxException;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Display;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.services.interfaces.TestProjectService;
import org.testeditor.ui.ApplicationLifeCycleHandler;
import org.testeditor.ui.TestServerStarter;
import org.testeditor.ui.wizardpages.AbstractNewTestStructureWizardPage;
import org.testeditor.ui.wizardpages.NewTestProjectWizardPage;
import org.testeditor.ui.wizards.NewTestProjectWizard;

/**
 * NewProjectHandler creates new Project.
 * 
 */
public class NewProjectHandler extends NewTestStructureHandler {

	private static final Logger LOGGER = Logger.getLogger(NewProjectHandler.class);

	@Inject
	private TestProjectService testProjectService;

	private NewTestProjectWizard wizard;

	@Override
	protected AbstractNewTestStructureWizardPage getNewTestStructureWizardPage(TestStructure selectedTS,
			IEclipseContext context) {
		NewTestProjectWizardPage testProjectWizardPage = ContextInjectionFactory.make(NewTestProjectWizardPage.class,
				context);
		testProjectWizardPage.setSelectedTestStructure(selectedTS);
		return testProjectWizardPage;
	}

	@Override
	protected boolean canOpenObject() {
		return true;
	}

	@Override
	protected String getWindowTitle() {
		return "%popupmenu.label.new.test.project";
	}

	@Override
	protected TestStructure createNewTestStructure(IEclipseContext context) {
		TestProject createdProject = null;
		try {
			createdProject = getTestProjectService().createNewProject(wizard.getNameText());
			if (createdProject != null) {
				TestServerStarter starter = ContextInjectionFactory.make(TestServerStarter.class, context);
				ContextInjectionFactory.make(ApplicationLifeCycleHandler.class, context).startBackendServer(starter,
						createdProject);
				setNewTestStructureName(createdProject.getName());
			}
		} catch (IOException e) {
			LOGGER.error("IOException while creating project", e);
		} catch (SystemException e) {
			LOGGER.error("SystemException while creating project", e);
		} catch (URISyntaxException e) {
			LOGGER.error("URISyntaxException while creating project", e);
		}
		return createdProject;
	}

	/**
	 * special canExecute method for TestProject.
	 * 
	 * @param context
	 *            Eclipse Context
	 * @param ignoreCanExecute
	 *            ignore this Method in File menu
	 * @return false, if the selection is the TestKomponenten-Suite
	 */
	@CanExecute
	public boolean canExecute(IEclipseContext context,
			@Named("org.testeditor.ui.newproject.command.parameter.ignoreCanExecute") @Optional String ignoreCanExecute) {
		try {
			return CanExecuteNewProjectOrDemoProjectsRules.canExecute(testProjectService);
		} catch (IOException e) {
			LOGGER.error(e);
			MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error", e.getLocalizedMessage());
		}
		return false;
	}

	@Override
	protected Wizard getWizard(IEclipseContext context) {
		wizard = ContextInjectionFactory.make(NewTestProjectWizard.class, context);
		return wizard;
	}

	/**
	 * This addChild method is not implemented here because the TestProject has
	 * no parent.
	 * 
	 * @param testStructure
	 *            TestStructure of the TestProject
	 */
	@Override
	protected void addChild(TestStructure testStructure) {
	}

	@Override
	protected void createAndOpenTestStructure(TestStructure testStructure, IEclipseContext context) {
		try {
			OpenTestStructureHandler openHandler = ContextInjectionFactory
					.make(OpenTestStructureHandler.class, context);
			openHandler.execute((TestProject) testStructure, context);
		} catch (Exception e) {
			LOGGER.error("Opening Tetsproject", e);
		}
	}

}
