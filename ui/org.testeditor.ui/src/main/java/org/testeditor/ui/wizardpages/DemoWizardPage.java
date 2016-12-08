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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.testeditor.core.services.interfaces.TestProjectService;
import org.testeditor.ui.constants.CustomWidgetIdConstants;
import org.testeditor.ui.utilities.TestEditorTranslationService;

/**
 * Wizard for creating demo projects.
 * 
 * 
 */
public class DemoWizardPage extends WizardPage {

	private TestProjectService testProjectService;

	private static final Logger LOGGER = Logger.getLogger(DemoWizardPage.class);

	private File[] demoProjectsDirs;
	private List<Button> buttonList = new ArrayList<Button>();
	private List<File> selectedDemosToBuild;

	/**
	 * 
	 * @param pageName
	 *            Name of the wizard page
	 * @param translationService
	 *            software localisation service
	 * @param testProjectService
	 *            Service
	 */
	public DemoWizardPage(String pageName, TestEditorTranslationService translationService,
			TestProjectService testProjectService) {
		super(pageName);
		this.setTitle(pageName);
		this.setDescription(translationService.translate("%wizard.demo.description"));
		this.testProjectService = testProjectService;
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(1, false));

		try {
			demoProjectsDirs = testProjectService.getDemoProjects();

			// workspace directories (existing TestProjects)
			File wsDir = Platform.getLocation().toFile();
			Set<String> wsDirectoryNames = new HashSet<String>(Arrays.asList(wsDir.list()));

			for (int i = 0; i < demoProjectsDirs.length; i++) {
				if (!wsDirectoryNames.contains(demoProjectsDirs[i].getName())) {
					// not existing demo project found
					buttonList.add(createButtons(parent, container, i, true));
				} else {
					buttonList.add(createButtons(parent, container, i, false));
				}
			}

			setControl(container);
			setPageComplete(true);
		} catch (IOException e) {
			LOGGER.error(e);
			MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error", e.getLocalizedMessage());
		}
	}

	/**
	 * Creates Buttons for demo selection on wizard page.
	 * 
	 * @param parent
	 *            Context of wizard
	 * @param container
	 *            Container for widgets - in this case only checkboxes
	 * @param i
	 *            Counter for list
	 * @param enable
	 *            Enables the receiver if the argument is true, and disables it
	 *            otherwise. A disabled control is typically not selectable from
	 *            the user interface and draws with an inactive or "grayed"
	 *            look.
	 * @return Button
	 */
	public Button createButtons(Composite parent, Composite container, int i, boolean enable) {
		Button button = new Button(container, SWT.CHECK);
		button.setText(demoProjectsDirs[i].getName());
		button.setSelection(false);
		button.setEnabled(enable);
		button.setData(CustomWidgetIdConstants.TEST_EDITOR_WIDGET_ID_SWT_BOT_KEY, demoProjectsDirs[i].getName());

		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				selectedDemosToBuild = new ArrayList<File>();

				for (int i = 0; i < buttonList.size(); i++) {
					if (buttonList.get(i).getSelection()) {
						selectedDemosToBuild.add(demoProjectsDirs[i]);
					}
				}
			}
		});
		return button;
	}

	/**
	 * 
	 * @return File list of demo projects to be build
	 */
	public List<File> getDemoProjectsDirs() {
		return selectedDemosToBuild;
	}

}
