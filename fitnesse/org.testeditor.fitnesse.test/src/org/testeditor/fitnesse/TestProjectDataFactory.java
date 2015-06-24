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
package org.testeditor.fitnesse;

import java.io.File;
import java.util.Properties;

import org.testeditor.core.model.teststructure.TestCase;
import org.testeditor.core.model.teststructure.TestFlow;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestProjectConfig;
import org.testeditor.core.services.interfaces.ServiceLookUpForTest;
import org.testeditor.core.services.interfaces.TestEditorPlugInService;
import org.testeditor.core.services.interfaces.TestProjectService;
import org.testeditor.core.services.plugins.LibraryConfigurationServicePlugIn;

/**
 * 
 * Utility Class to build TestProjects.
 * 
 */
public final class TestProjectDataFactory {

	/**
	 * Utility Class.
	 */
	private TestProjectDataFactory() {

	}

	/**
	 * 
	 * @return TestProeject filled with Data for Tests.
	 */
	public static TestProject createTestProjectForFitnesseTests() {
		TestProject firstTestProject = new TestProject();
		TestFlow firstTestCase = new TestCase();
		firstTestProject.setName("firstTestProject");

		TestProjectConfig firstTestprojectConfig = new TestProjectConfig();
		String sourceFile = "AllActionGroups.xml";
		String sourceFileBindings = "TechnicalBindingTypeCollection.xml";
		String tstLib = "testLibrary";

		String testActionGroupXml = new StringBuffer(new File("").getAbsolutePath()).append(File.separatorChar)
				.append(tstLib).append(File.separatorChar).append(sourceFile).toString();
		String pathTechnicalBindings = new StringBuffer(new File("").getAbsolutePath()).append(File.separatorChar)
				.append(tstLib).append(File.separatorChar).append(sourceFileBindings).toString();
		TestEditorPlugInService plugInService = ServiceLookUpForTest.getService(TestEditorPlugInService.class);
		Properties properties = new Properties();
		properties.put(TestEditorPlugInService.LIBRARY_ID, "org.testeditor.xmllibrary");
		properties.put("library.xmllibrary.actiongroup", testActionGroupXml);
		properties.put("library.xmllibrary.technicalbindings", pathTechnicalBindings);
		properties.put(TestProjectService.VERSION_TAG, TestProjectService.VERSION1_2);
		LibraryConfigurationServicePlugIn libraryConfigurationService = plugInService
				.getLibraryConfigurationServiceFor("org.testeditor.xmllibrary");
		firstTestprojectConfig.setProjectLibraryConfig(libraryConfigurationService
				.createProjectLibraryConfigFrom(properties));
		firstTestProject.setTestProjectConfig(firstTestprojectConfig);
		firstTestProject.addChild(firstTestCase);
		return firstTestProject;
	}

}
