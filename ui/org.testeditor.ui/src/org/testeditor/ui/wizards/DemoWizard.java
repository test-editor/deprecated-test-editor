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
package org.testeditor.ui.wizards;

import java.io.File;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.jface.wizard.Wizard;
import org.testeditor.core.services.interfaces.TestProjectService;
import org.testeditor.ui.utilities.TestEditorTranslationService;
import org.testeditor.ui.wizardpages.DemoWizardPage;

/**
 * 
 * Wizaard to create the demo projects for the Test-Editor.
 * 
 */
public class DemoWizard extends Wizard {

	@Inject
	private TestProjectService testProjectService;

	@Inject
	private TestEditorTranslationService translationService;

	private List<File> output;

	/**
	 * Initilisation after injection.
	 */
	@PostConstruct
	public void postConstruct() {
		this.setWindowTitle(translationService.translate("%wizard.demo.titel"));
		DemoWizardPage demoWizardPage = new DemoWizardPage(translationService.translate("%wizard.demo.titel"),
				translationService, testProjectService);
		this.addPage(demoWizardPage);
	}

	@Override
	public boolean performFinish() {
		output = ((DemoWizardPage) this.getPage(translationService.translate("%wizard.demo.titel")))
				.getDemoProjectsDirs();
		return true;
	}

	/**
	 * 
	 * @return File list of demo projects to be build
	 */
	public List<File> getDemoProjectsDirs() {
		return output;
	}
}
