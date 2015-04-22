/*******************************************************************************
 * Copyright (c) 2012, 2014 Signal Iduna Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Signal Iduna Corporation - initial API and implementation
 * akquinet AG
 *******************************************************************************/
package org.testeditor.teamshare.svn;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Properties;

import org.eclipse.e4.core.services.translation.TranslationService;
import org.junit.Test;
import org.testeditor.core.services.interfaces.FieldMappingExtension;

/**
 * 
 * ModulTests for SVNTeamShareConfigurationService.
 * 
 */
@SuppressWarnings("restriction")
public class SVNTeamShareConfigurationServiceTest {

	/**
	 * Test that there are Field Declarations for SVN.
	 * 
	 * @throws Exception
	 *             for Test
	 */
	@Test
	public void testGettingFieldDeclarations() throws Exception {
		SVNTeamShareConfigurationService teamShareConfigurationService = new SVNTeamShareConfigurationService();
		List<FieldMappingExtension> fieldDeclarations = teamShareConfigurationService.getFieldDeclarations();
		assertNotNull("Expecting a List with Declaring Fields.", fieldDeclarations);
		assertTrue("Expect one or more Fields.", fieldDeclarations.size() > 0);
		for (FieldMappingExtension fieldDeclaration : fieldDeclarations) {
			assertNotNull("Expect a value for the label.",
					fieldDeclaration.getTranslatedLabel(getTranslationServiceMock()));
			assertNotNull("Expect a value for the tooltip.",
					fieldDeclaration.getTranslatedToolTip(getTranslationServiceMock()));
		}
	}

	/**
	 * Check that the Service writes and reads the URL correct.
	 * 
	 * @throws Exception
	 *             for Test
	 */
	@Test
	public void testReadAndUpdateOfURL() throws Exception {
		SVNTeamShareConfigurationService teamShareConfigurationService = new SVNTeamShareConfigurationService();
		URLFieldDeclaration url = (URLFieldDeclaration) teamShareConfigurationService.getFieldDeclarations().get(0);
		SVNTeamShareConfig cfgBean = new SVNTeamShareConfig();
		cfgBean.setUrl("http://localhost");
		assertEquals("Expecting correct url from config.", "http://localhost", url.getStringValue(cfgBean));
		url.updatePlugInConfig(cfgBean, "https://foo");
		assertEquals("Expecting correct url from config.", "https://foo", cfgBean.getUrl());
	}

	/**
	 * Check that the Service writes and reads the Username correct.
	 * 
	 * @throws Exception
	 *             for Test
	 */
	@Test
	public void testReadAndUpdateOfUserName() throws Exception {
		SVNTeamShareConfigurationService teamShareConfigurationService = new SVNTeamShareConfigurationService();
		UserNameFieldDeclaration url = (UserNameFieldDeclaration) teamShareConfigurationService.getFieldDeclarations()
				.get(1);
		SVNTeamShareConfig cfgBean = new SVNTeamShareConfig();
		cfgBean.setUserName("hugo");
		assertEquals("Expecting correct Username from config.", "hugo", url.getStringValue(cfgBean));
		url.updatePlugInConfig(cfgBean, "bar");
		assertEquals("Expecting correct Username from config.", "bar", cfgBean.getUserName());
	}

	/**
	 * Check that the Service writes and reads the password correct.
	 * 
	 * @throws Exception
	 *             for Test
	 */
	@Test
	public void testReadAndUpdateOfPassword() throws Exception {
		SVNTeamShareConfigurationService teamShareConfigurationService = new SVNTeamShareConfigurationService();
		PasswordFieldDeclaration url = (PasswordFieldDeclaration) teamShareConfigurationService.getFieldDeclarations()
				.get(2);
		SVNTeamShareConfig cfgBean = new SVNTeamShareConfig();
		cfgBean.setPassword("hugo");
		assertEquals("Expecting correct Password from config.", "hugo", url.getStringValue(cfgBean));
		url.updatePlugInConfig(cfgBean, "bar");
		assertEquals("Expecting correct Password from config.", "bar", cfgBean.getPassword());
	}

	/**
	 * Test the Creation from Properties of the SVNTeamShareConfig.
	 * 
	 * @throws Exception
	 *             for Test
	 */
	@Test
	public void testCreateSVNTeamShareConfigFromProperties() throws Exception {
		SVNTeamShareConfigurationService teamShareConfigurationService = new SVNTeamShareConfigurationService();
		Properties properties = new Properties();
		properties.setProperty(SVNTeamShareConfigurationService.URL_PROPERTY, "http://host");
		SVNTeamShareConfig teamShareConfig = (SVNTeamShareConfig) teamShareConfigurationService
				.createTeamShareConfigFrom(properties);
		assertNotNull(teamShareConfig);
		assertEquals("Expect an URL", "http://host", teamShareConfig.getUrl());
	}

	/**
	 * Test that the Service can handle null values in a SVNConfig.
	 */
	@Test
	public void testNullSave() {
		SVNTeamShareConfigurationService teamShareConfigurationService = new SVNTeamShareConfigurationService();
		SVNTeamShareConfig teamShareConfig = new SVNTeamShareConfig();
		try {
			teamShareConfigurationService.getAsProperties(teamShareConfig);
		} catch (Exception e) {
			fail("Configuration Service should not fail.");
		}
	}

	/**
	 * 
	 * @return Translation Mock that returns in every case the Key.
	 */
	private TranslationService getTranslationServiceMock() {
		return new TranslationService() {

			@Override
			public String translate(String key, String contributorURI) {
				return key;
			}

		};
	}

}
