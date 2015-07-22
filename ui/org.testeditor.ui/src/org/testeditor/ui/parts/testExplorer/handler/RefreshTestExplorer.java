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
package org.testeditor.ui.parts.testExplorer.handler;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.services.interfaces.TestProjectService;
import org.testeditor.metadata.core.MetaDataService;

/**
 * 
 * Handler to Refresh the TestExplorer.
 * 
 */
public class RefreshTestExplorer {

	private static final Logger LOGGER = Logger.getLogger(RefreshTestExplorer.class);

	@Inject
	private TestProjectService testProjectService;

	@Inject
	@Optional
	private MetaDataService metaDataService;

	/**
	 * Looks up for the TestExporerView and refresh's it.
	 */
	@Execute
	public void refreshTestExplorer() {
		try {
			if (getMetaDataService() != null) {
				for (TestProject project : testProjectService.getProjects()) {
					getMetaDataService().refresh(project);
				}
			}
			testProjectService.reloadProjectList();
		} catch (SystemException e) {
			LOGGER.error("Error reloading Testprojects", e);
			MessageDialog.openError(Display.getCurrent().getActiveShell(), "System-Exception", e.getLocalizedMessage());
		}
	}

	/**
	 * Getter for the metaData Service. Checks if the service is set. If the
	 * service is not there, an info-message is displayed and null will be
	 * returned
	 * 
	 * @return the service
	 */
	private MetaDataService getMetaDataService() {
		if (metaDataService == null) {
			LOGGER.info("MetaDataTabService is not there. Probably the plugin 'org.testeditor.metadata.core' is not activated");
		}
		return metaDataService;

	}

}
