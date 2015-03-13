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
package org.testeditor.metadata.ui.parts;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.services.interfaces.TestStructureService;

public class MetaDataTaggingView {

	public static final String ID = "org.testeditor.metadata.ui.parts.MetaDataTaggingView";

	private static final Logger LOGGER = Logger.getLogger(MetaDataTaggingView.class);

	private MPart part;

	private Label label;

	@Inject
	private TestStructureService testStructureService;

	@Inject
	private IEclipseContext context;

	@Inject
	private TranslationService translate;

	/**
	 * Default Constructor of the MetaDataTaggingView.
	 * 
	 * @param part
	 *            to be used to communicate with the application model.
	 */
	@Inject
	public MetaDataTaggingView(MPart part) {
		this.part = part;
	}

	/**
	 * Constructs the UI after creating and building this object.
	 * 
	 * @param parent
	 *            composite to build the ui on.
	 */
	@PostConstruct
	public void postConstruct(Composite parent) {
		parent.setLayout(new GridLayout(1, false));

		label = new Label(parent, SWT.LEFT);
		label.setText("Tagging View says 'Hello!'");
	}

	/**
	 * Sets the Focus in the text widget of this view.
	 */
	@Focus
	public void setFocus() {

	}

	/**
	 * 
	 * @param testStructure
	 *            which last log should be displayed.
	 */
	public void setTestStructure(TestStructure testStructure) {
	}

}
