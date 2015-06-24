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

import org.testeditor.core.services.interfaces.TeamShareConfigurationService;

public interface TeamShareConfigurationServicePlugIn extends TeamShareConfigurationService {

	/**
	 * The Plug-In needs the same ID for their services to identity the Plug-In.
	 * 
	 * @return Plug-In ID
	 */
	String getId();

	/**
	 * Template for the config.tpr to allow this file to be well formed and
	 * formatted.
	 * 
	 * @return the template for the configuration
	 */
	String getTemplateForConfiguration();

}
