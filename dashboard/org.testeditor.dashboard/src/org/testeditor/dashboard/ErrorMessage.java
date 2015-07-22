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
package org.testeditor.dashboard;

import javax.inject.Inject;

import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * @author alebedev
 * 
 *         errors for file access
 */
public final class ErrorMessage {

	@Inject
	private static TranslationService translationService;

	/**
	 * The contributor URI.
	 */
	public static final String CONTRIBUTOR_URI = "platform:/plugin/org.testeditor.dashboard";

	/**
	 * File path is not available.
	 */
	public void errorPath() {
		Shell shell = new Shell();
		MessageDialog.openInformation(shell, translationService.translate("%dashboard.errorlabel", CONTRIBUTOR_URI),
				translationService.translate("%dashboard.errorPath", CONTRIBUTOR_URI));
	}

	/**
	 * No test result files.
	 */
	public void errorFile() {
		Shell shell = new Shell();
		MessageDialog.openInformation(shell, translationService.translate("%dashboard.errorlabel", CONTRIBUTOR_URI),
				translationService.translate("%dashboard.errorFile", CONTRIBUTOR_URI));
	}

	/**
	 * No projects.
	 */
	public void errorProject() {
		Shell shell = new Shell();
		MessageDialog.openInformation(shell, translationService.translate("%dashboard.errorlabel", CONTRIBUTOR_URI),
				translationService.translate("%dashboard.errorProject", CONTRIBUTOR_URI));
	}

	/**
	 * Project has no test results.
	 */
	public void errorProjectEmpty() {
		Shell shell = new Shell();
		MessageDialog.openInformation(shell, translationService.translate("%dashboard.errorlabel", CONTRIBUTOR_URI),
				translationService.translate("%dashboard.errorProjectEmpty", CONTRIBUTOR_URI));
	}
}
