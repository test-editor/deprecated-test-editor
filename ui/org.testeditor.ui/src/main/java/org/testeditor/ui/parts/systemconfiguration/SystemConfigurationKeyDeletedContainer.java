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
package org.testeditor.ui.parts.systemconfiguration;

import org.eclipse.jface.viewers.ColumnViewer;

/**
 * data-container for the delete-event.
 * 
 * @author llipinski
 * 
 */
public class SystemConfigurationKeyDeletedContainer {

	private ColumnViewer viewer;
	private String oldKey;

	/**
	 * special constructor with parameters.
	 * 
	 * @param viewer
	 *            ColumnViewer
	 * @param oldKey
	 *            the old key to be removed.
	 */
	public SystemConfigurationKeyDeletedContainer(ColumnViewer viewer, String oldKey) {
		this.viewer = viewer;
		this.oldKey = oldKey;
	}

	/**
	 * 
	 * @return viewer.
	 */
	public ColumnViewer getViewer() {
		return viewer;
	}

	/**
	 * 
	 * @return oldKey
	 */
	public String getOldKey() {
		return oldKey;
	}
}
