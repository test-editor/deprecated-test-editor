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

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Shell;
import org.testeditor.core.util.FileLocatorService;

/**
 * 
 * This class provides and opens the pdf-manual.
 * 
 */
public class UserManualHandler {

	@Inject
	private FileLocatorService fileLocatorService;

	private static final Logger LOGGER = Logger.getLogger(UserManualHandler.class);

	/**
	 * Shows the About Information of the Testeditor.
	 * 
	 * @param shell
	 *            Active UI Shell to create the MessageDialog.
	 */
	@Execute
	public void execute(@Named(IServiceConstants.ACTIVE_SHELL) Shell shell) {
		String absolutPathToBundle;
		try {
			absolutPathToBundle = findBundleFile("org.testeditor.demo");
			Program.launch(absolutPathToBundle + File.separator + "TestEditorUserManualDe.pdf");
		} catch (IOException e) {
			LOGGER.info("Error loading Manual", e);
		}
	}

	/**
	 * this method search for a file with a filename beginning with the
	 * parameter.
	 * 
	 * @param fileName
	 *            first part of the name of the file
	 * @return the absolutpath
	 * @throws IOException
	 *             on file access.
	 */
	protected String findBundleFile(String fileName) throws IOException {
		return fileLocatorService.findBundleFileLocationAsString(fileName);
	}

}
