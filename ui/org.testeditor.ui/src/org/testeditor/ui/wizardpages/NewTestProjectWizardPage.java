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
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.services.interfaces.TestProjectService;
import org.testeditor.ui.parts.commons.tree.TestStructureTree;
import org.testeditor.ui.utilities.TestEditorTranslationService;
import org.testeditor.ui.wizardpages.nameinspector.INameInspector;
import org.testeditor.ui.wizardpages.nameinspector.TestProjectNameInspector;

/**
 * This class creates new Test Project.
 * 
 */
public class NewTestProjectWizardPage extends AbstractNewTestStructureWizardPage {

	@Inject
	private TestEditorTranslationService translationService;
	@Inject
	private TestProjectService testProjectService;
	@Inject
	private IEclipseContext context;
	private INameInspector nameInspector;
	private Text portText;

	private static final String INTEGER_PATTERN_REGEX = "\\d*$";
	private static final Logger LOGGER = Logger.getLogger(NewTestProjectWizardPage.class);
	private List<TestProject> projects;
	private Font hintFont;

	/**
	 * Access to the TeststructureTree Component.
	 * 
	 * @return the TeststructureTree of the Wizard
	 */
	protected TestStructureTree getTestStructureTree() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getTitleValue() {
		return translationService.translate("%popupmenu.label.new.test.project");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDescriptionValue() {
		return translationService.translate("%new.wizard.whitemsg.project");
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @return the name-inspector for the test project.
	 */
	@Override
	protected INameInspector getNameInspector() {
		if (nameInspector == null) {
			nameInspector = ContextInjectionFactory.make(TestProjectNameInspector.class, context);
		}
		return nameInspector;
	}

	@Override
	protected void setTreeFilter() {

	}

	/**
	 * create location tree.
	 */
	protected void createLocationTree() {
	}

	/**
	 * creates the control.
	 * 
	 * @param parent
	 *            the parent composite.
	 */
	public void createControl(Composite parent) {
		projects = testProjectService.getProjects();

		super.createControl(parent);
		createLocationTree();

	}

	/**
	 * validates if the all page entries are set correctly set the PageComplete
	 * switch.
	 */
	protected void validatePageAndSetComplete() {
		String nameText = getNameText().getText();
		if (!doesNameAlreadyExist(nameText) && super.isNameValid(nameText)) {
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
	protected boolean doesNameAlreadyExist(String projectName) {

		if (existProjectName(projectName)) {
			this.setErrorMessage(translationService.translate("%wizard.error.double"));
			return true;
		}
		this.setErrorMessage(null);
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

	@Override
	public void dispose() {
		if (hintFont != null && !hintFont.isDisposed()) {
			hintFont.dispose();
			hintFont = null;
		}
		super.dispose();
	}

}
