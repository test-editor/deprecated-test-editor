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
package org.testeditor.metadata.ui;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;

/**
 * 
 * Viewpart to manage meta data value lists
 * 
 */
public class AdministrationView {

	private Label testLabel;

	@Inject
	private IEclipseContext context;

	@Inject
	private TranslationService translate;

	/**
	 * 
	 * Creates a TreeViewer to display the validations.
	 * 
	 * @param parent
	 *            used to create UI Elements in.
	 */
	@PostConstruct
	public void createControls(org.eclipse.swt.widgets.Composite parent) {
		parent.setLayout(new GridLayout(1, false));
		testLabel = new Label(parent, SWT.NORMAL);
		// testLabel.setText(translate.translate("%viewpart.teststructurevalidation",
		// "platform:/plugin/org.testeditor.ui.analyzer"));
		testLabel.setText("Hello World");

	}

}
