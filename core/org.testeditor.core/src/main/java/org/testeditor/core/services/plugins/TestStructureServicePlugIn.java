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

import org.testeditor.core.services.interfaces.TestStructureService;

/**
 * 
 * Plug-In extension of the TestStructureService. Plug-In Provider must
 * implement this interface and not the TestStructureService.
 *
 */
public interface TestStructureServicePlugIn extends TestStructureService {

	/**
	 * This id is used to identify the TestStructureService plug-in. It must
	 * match the ID in the <code>TestProjectConfig</code>.
	 * 
	 * @return ID to Identify the Plug-In.
	 */
	String getId();

}
