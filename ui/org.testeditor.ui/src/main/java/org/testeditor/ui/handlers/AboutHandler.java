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

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.Version;
import org.testeditor.ui.utilities.TestEditorTranslationService;

/**
 * 
 * Shows About Information about the TestEditor to the User.
 * 
 */
public class AboutHandler {

	@Inject
	private static TestEditorTranslationService translationService;

	private String copyright;
	private String informationlabel;
	private final StringBuilder information = new StringBuilder();

	/**
	 * Shows the About Information of the Testeditor.
	 * 
	 * @param shell
	 *            Active UI Shell to create the MessageDialog.
	 */
	@Execute
	public void execute(@Named(IServiceConstants.ACTIVE_SHELL) Shell shell) {
		MessageDialog.openInformation(shell, informationlabel, getInformationString());
	}

	/**
	 * 
	 * @return the Version Informations of the Testeditor Bundles as a String.
	 */
	String getInformationString() {
		return information.toString();
	}

	/**
	 * Fills the translated Strings for the MessageDialog.
	 */
	@Inject
	private void fillStrings() {
		copyright = translationService.translate("%copyright.symbol");

		informationlabel = translationService.translate("%about.information.label");

		BundleContext context = FrameworkUtil.getBundle(AboutHandler.class).getBundleContext();

		StringBuilder versionInfo = new StringBuilder();
		StringBuilder svnVersionInfo = new StringBuilder();
		Version versionOfTestEditor = null;
		Bundle[] bundles = context.getBundles();
		for (Bundle bundle : bundles) {

			if (bundle.getSymbolicName().startsWith("org.testeditor")) {
				versionInfo.append("\n" + bundle.getSymbolicName() + ": "
						+ bundle.getVersion().toString().replace(".qualifier", "-SNAPSHOT"));
				if (bundle.getSymbolicName().equals("org.testeditor.ui")) {
					versionOfTestEditor = bundle.getVersion();
				}
			}
			if (bundle.getSymbolicName().startsWith("org.tmatesoft.svnkit")) {
				svnVersionInfo.append("\n\nSVN:");
				svnVersionInfo.append("\n*** " + bundle.getSymbolicName() + ": " + bundle.getVersion().getMajor() + "."
						+ bundle.getVersion().getMinor() + "." + bundle.getVersion().getMicro());
				svnVersionInfo.append(" works with Subversion: " + bundle.getVersion().getMajor() + "."
						+ bundle.getVersion().getMinor());

			}

		}

		information.append(String.format(translationService.translate("%about.information.text"), versionOfTestEditor
				.toString().replace(".qualifier", "-SNAPSHOT"), copyright));
		information.append(versionInfo);
		information.append(svnVersionInfo);
	}
}
