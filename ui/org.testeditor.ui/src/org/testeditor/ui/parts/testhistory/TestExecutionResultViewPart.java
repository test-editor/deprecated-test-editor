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
package org.testeditor.ui.parts.testhistory;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * 
 * View part to Display the Content of a TestExecution from the History View.
 *
 */
public class TestExecutionResultViewPart {

	private static final Logger LOGGER = Logger.getLogger(TestExecutionResultViewPart.class);

	private Browser browser;

	/**
	 * Constructs the UI.
	 * 
	 * @param parent
	 *            of the UI WIndow.
	 */
	@PostConstruct
	public void createUi(Composite parent) {
		parent.setLayout(new GridLayout(1, true));
		createHeadline(parent);
		browser = new Browser(parent, SWT.None);
		browser.setLayoutData(new GridData(GridData.FILL_BOTH));
	}

	/**
	 * Creates a Head UI Area to display Test execution result summary.
	 * 
	 * @param parent
	 *            UI Composite to build the Headline on.
	 */
	private void createHeadline(Composite parent) {
		Composite cmp = new Composite(parent, SWT.NORMAL);
		new Label(cmp, SWT.NORMAL).setText("Test Results:");
	}

	/**
	 * 
	 * @param url
	 *            URL of the backendserver to load the TestResult as html in the
	 *            browser.
	 */
	public void setTestResultURL(String url) {
		LOGGER.trace("Opening URL: " + url);
		browser.setUrl(url);
	}
}
