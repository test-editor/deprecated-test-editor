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
package org.testeditor.core.model.team;

/**
 * 
 * Definition of the Configuration of a TeamShareOptions. 
 *
 */
public interface TeamShareConfig {

	/**
	 * This id is used to identify the team share plug-in. It must the same ID in
	 * the <code>TeamShareConfigurationService</code> and in the
	 * <code>TeamShareService</code>
	 * 
	 * @return ID to identify the Service Implementation.
	 */
	String getId();

}
