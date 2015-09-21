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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.Platform;
import org.testeditor.core.services.interfaces.TestProjectService;

/**
 * CanExecuteion for NewProject or CreatDeamoProjects.
 * 
 * @author llipinski
 * 
 */
public final class CanExecuteNewProjectOrDemoProjectsRules {

	/**
	 * private constructor.
	 */
	private CanExecuteNewProjectOrDemoProjectsRules() {
	}

	/**
	 * 
	 * @return true, if the demoporjects are existing.
	 * @param testProjectService
	 *            TestProjectService
	 * @throws IOException
	 *             , on file operation
	 */
	public static boolean canExecute(TestProjectService testProjectService) throws IOException {
		boolean demoPorjectsExisting = false;
		// existing demo projects
		File[] demoProjectsDirs = testProjectService.getDemoProjects();

		// workspace directories (existing TestProjects)
		File wsDir = Platform.getLocation().toFile();

		Set<String> wsDirectoryNames = new HashSet<String>(Arrays.asList(wsDir.list()));

		if (demoProjectsDirs != null) {
			for (File demoProjectDir : demoProjectsDirs) {
				if (!wsDirectoryNames.contains(demoProjectDir.getName())) {
					// not existing demo project found
					demoPorjectsExisting = true;
					break;
				}
			}
		}
		return demoPorjectsExisting;
	}
}