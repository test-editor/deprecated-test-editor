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
 * Data-container for the element, that should be updated.
 * 
 * @author llipinski
 * 
 */
public class SystemConfigurationUpdateElementContainer {

	private ColumnViewer viewer;
	private Object element;

	/**
	 * constructor with parameters.
	 * 
	 * @param viewer
	 *            the viewer
	 * @param element
	 *            the element, that should be updated.
	 */
	public SystemConfigurationUpdateElementContainer(ColumnViewer viewer, Object element) {
		this.viewer = viewer;
		this.element = element;
	}

	/**
	 * 
	 * @return viewer
	 */
	public ColumnViewer getViewer() {
		return viewer;
	}

	/**
	 * 
	 * @return element
	 */
	public Object getElement() {
		return element;
	}
}
