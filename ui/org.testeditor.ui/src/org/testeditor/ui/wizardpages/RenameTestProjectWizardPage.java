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
package org.testeditor.ui.wizardpages;

import java.util.List;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.swt.widgets.Composite;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.services.interfaces.TestProjectService;
import org.testeditor.ui.utilities.TestEditorTranslationService;
import org.testeditor.ui.wizardpages.nameinspector.INameInspector;
import org.testeditor.ui.wizardpages.nameinspector.TestProjectNameInspector;

/**
 * This is a Wizard for renaming an existing test-project.
 * 
 * @author fokoh
 * 
 */
public class RenameTestProjectWizardPage extends AbstractRenameTestStructureWizardPage {

	@Inject
	private TestEditorTranslationService translationService;
	@Inject
	private TestProjectService testProjectService;
	@Inject
	private IEclipseContext context;
	private INameInspector nameInspector = new TestProjectNameInspector();
	private List<TestProject> projects;
	private static final Logger LOGGER = Logger.getLogger(RenameTestProjectWizardPage.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDescriptionValue() {
		return translationService.translate("%rename.wizard.whitemsg.project");

	}

	@Override
	public void createControl(Composite parent) {
		projects = testProjectService.getProjects();
		super.createControl(parent);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected INameInspector getNameInspector() {
		if (nameInspector == null) {
			nameInspector = ContextInjectionFactory.make(TestProjectNameInspector.class, context);
		}
		return this.nameInspector;
	}

	/**
	 * validates if the all page entries are set correctly set the PageComplete
	 * switch.
	 */
	protected void validatePageAndSetComplete() {
		String nameText = getNameText().getText();
		if (!isNameAlreadyExist(nameText) && super.isNameValid(nameText)) {
			setPageComplete(true);
		} else {
			setPageComplete(false);
		}
	}

	/**
	 * Checks if name of a project already exist. It also sends an error message
	 * if the project name already exit.
	 * 
	 * @param projectName
	 *            name of the project
	 * @return <code>true</code> if project name already exist else
	 *         <code>false</code>
	 */
	private boolean isNameAlreadyExist(String projectName) {

		if (existProjectName(projectName)) {
			this.setErrorMessage(translationService.translate("%wizard.error.double"));
			return true;
		}
		setErrorMessage(null);
		return false;
	}

	/**
	 * ask for the existence of a project with the same name.
	 * 
	 * @param projectName
	 *            the name of the project
	 * @return true, if an other project with the same name as the parameter
	 *         projectName exists, else false
	 */
	private boolean existProjectName(String projectName) {
		for (TestProject project : projects) {
			if (project.getName().equalsIgnoreCase(projectName)) {
				return true;
			}
		}
		return false;
	}
}
