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
package org.testeditor.xmllibrary.services.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.Properties;

import org.eclipse.e4.core.services.translation.TranslationService;
import org.junit.Test;
import org.testeditor.core.services.interfaces.TestProjectService;
import org.testeditor.xmllibrary.model.XMLProjectLibraryConfig;

/**
 * 
 * Integration Test for LibraryConfigurationServiceImpl.
 * 
 */
public class LibraryConfigurationServiceImplTest {

	/**
	 * Test the usage of the Translationservice.
	 * 
	 * @throws Exception
	 *             for test
	 */
	@Test
	public void testGetTranslatedHumanReadableLibraryPlugInName() throws Exception {
		LibraryConfigurationServiceImpl serviceImpl = new LibraryConfigurationServiceImpl();
		String name = serviceImpl.getTranslatedHumanReadableLibraryPlugInName(new TranslationService() {
			@Override
			public String translate(String key, String contributorURI) {
				return "Test using service";
			}
		});
		assertEquals(name, "Test using service");
	}

	/**
	 * Tests the creation and mapping from properties to object model.
	 * 
	 * @throws Exception
	 *             for test
	 */
	@Test
	public void testCreateProjectLibraryConfigFrom() throws Exception {
		LibraryConfigurationServiceImpl serviceImpl = new LibraryConfigurationServiceImpl();
		Properties properties = new Properties();
		properties.setProperty("library.xmllibrary.actiongroup", "/aPath/ToAction.xml");
		properties.setProperty("library.xmllibrary.technicalbindings", "/aPath/ToTecBindings.xml");
		properties.setProperty(TestProjectService.VERSION_TAG, TestProjectService.VERSION1_2);
		XMLProjectLibraryConfig config = (XMLProjectLibraryConfig) serviceImpl
				.createProjectLibraryConfigFrom(properties);
		assertEquals("/aPath/ToAction.xml", config.getPathToXmlActionGroup());
		assertEquals("/aPath/ToTecBindings.xml", config.getPathToXmlTechnicalBindings());
	}

	/**
	 * tests the rename-method.
	 */
	@Test
	public void renameProjectLibraryConfigTest() {
		LibraryConfigurationServiceImpl serviceImpl = new LibraryConfigurationServiceImpl();
		Properties properties = new Properties();
		properties.setProperty("library.xmllibrary.actiongroup", "/aPath/ToAction.xml");
		properties.setProperty("library.xmllibrary.technicalbindings", "/aPath/ToTecBindings.xml");
		properties.setProperty(TestProjectService.VERSION_TAG, TestProjectService.VERSION1_2);
		XMLProjectLibraryConfig config = (XMLProjectLibraryConfig) serviceImpl
				.createProjectLibraryConfigFrom(properties);
		config.renameConfigurationToDestination("aPath", "newName");
		assertNotEquals("/aPath/ToTecBindings.xml", config.getPathToXmlTechnicalBindings());
		assertNotEquals("/aPath/ToAction.xml", config.getPathToXmlActionGroup());
		assertEquals("/newName/ToTecBindings.xml", config.getPathToXmlTechnicalBindings());
		assertEquals("/newName/ToAction.xml", config.getPathToXmlActionGroup());
	}

	/**
	 * tests the copy-method.
	 */
	@Test
	public void copyProjectLibraryConfigTest() {
		LibraryConfigurationServiceImpl serviceImpl = new LibraryConfigurationServiceImpl();
		Properties properties = new Properties();
		properties.setProperty("library.xmllibrary.actiongroup", "/aPath/ToAction.xml");
		properties.setProperty("library.xmllibrary.technicalbindings", "/aPath/ToTecBindings.xml");
		properties.setProperty(TestProjectService.VERSION_TAG, TestProjectService.VERSION1_2);
		XMLProjectLibraryConfig config = (XMLProjectLibraryConfig) serviceImpl
				.createProjectLibraryConfigFrom(properties);
		XMLProjectLibraryConfig newConfiguration = (XMLProjectLibraryConfig) config.copyConfigurationToDestination(
				"aPath", "DemoWebTests");
		assertEquals("/DemoWebTests/ToTecBindings.xml", newConfiguration.getPathToXmlTechnicalBindings());
		assertEquals("/DemoWebTests/ToAction.xml", newConfiguration.getPathToXmlActionGroup());

	}
}
