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
package org.testeditor.ui.parts.commons.tree.filter;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.testeditor.core.model.teststructure.TestProject;

/**
 * 
 * Filter to reduce the tree only to a given testProject.
 * 
 */
public class SkipProjectFilter extends ViewerFilter {

	private TestProject project;

	/**
	 * Creates the filter for a given project.
	 * 
	 * @param project
	 *            - the project.
	 */
	public SkipProjectFilter(TestProject project) {
		this.project = project;
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		return !(element instanceof TestProject) || ((TestProject) element).getName().equals(project.getName());
	}

}
