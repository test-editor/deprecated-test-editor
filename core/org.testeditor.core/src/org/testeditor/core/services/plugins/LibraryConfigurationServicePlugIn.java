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
package org.testeditor.core.services.plugins;

import org.testeditor.core.model.action.ProjectLibraryConfig;
import org.testeditor.core.services.interfaces.LibraryConfigurationService;

public interface LibraryConfigurationServicePlugIn extends LibraryConfigurationService {

	/**
	 * This id is used to identify the library plug-in. It must the same ID in
	 * the <code>ProjectLibraryConfig</code> and in the
	 * <code>LibraryReaderService</code>
	 * 
	 * @return the id of the implementation
	 */
	String getId();

	/**
	 * 
	 * @return an empty Plug-In specific implementation of
	 *         <code>ProjectLibraryConfig</code>.
	 */
	ProjectLibraryConfig createEmptyProjectLibraryConfig();

	/**
	 * Template for the config.tpr to allow this file to be well formed and
	 * formatted.
	 * 
	 * @return the template for the configuration
	 */
	String getTemplateForConfiguration();

}
