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
package org.testeditor.dashboard;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.di.AboutToShow;
import org.eclipse.e4.ui.model.application.ui.menu.MDirectMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuFactory;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.services.interfaces.TestProjectService;

/**
 * @author alebedev
 * 
 *         retrieves project names for dropdown menu in in LastRunsTable-toolbar
 */
public class ProjectComboBox {

	@Inject
	private TestProjectService testProjectService;

	/**
	 * creating projects names in dropdown menu in LastRunsTable-toolbar.
	 * 
	 * @param context
	 *            IEclipseContext context
	 * @param items
	 *            to add project-items to drop down menu
	 */
	@AboutToShow
	public void aboutToShow(List<MMenuElement> items, IEclipseContext context) {

		List<TestProject> projects = testProjectService.getProjects();
		for (int i = 0; i < projects.size(); i++) {
			MDirectMenuItem element = MMenuFactory.INSTANCE.createDirectMenuItem();
			String projectName = projects.get(i).getName();
			element.setLabel(projectName);
			element.setElementId(projectName);
			element.setContributionURI("bundleclass://org.testeditor.dashboard/org.testeditor.dashboard.DirectMenuItem");
			items.add(i, element);
		}
	}

}