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
package org.testeditor.teamshare.svn;

import static org.junit.Assert.*;

import java.util.Collection;

import org.junit.Test;
import org.testeditor.core.services.interfaces.ServiceLookUpForTest;
import org.testeditor.core.services.interfaces.TeamShareConfigurationService;
import org.testeditor.core.services.interfaces.TeamShareService;
import org.testeditor.core.services.interfaces.TestEditorPlugInService;

/**
 * Integration Tests to check the Registration of the Services of the SVN Team
 * Share Bundle.
 * 
 */
public class ServiceRegistrationTest {

	/**
	 * 
	 * Tests that the SVNTeamShareConfigurationService is registered as an
	 * OSGi-Service.
	 * 
	 * @throws Exception
	 *             for Test
	 */
	@Test
	public void testTeamShareConfigurationServiceRegistrationTest() throws Exception {
		TestEditorPlugInService plugInService = ServiceLookUpForTest.getService(TestEditorPlugInService.class);
		boolean found = false;
		Collection<TeamShareConfigurationService> allTeamShareConfigurationServices = plugInService
				.getAllTeamShareConfigurationServices();
		for (TeamShareConfigurationService teamShareConfigurationService : allTeamShareConfigurationServices) {
			if (teamShareConfigurationService instanceof SVNTeamShareConfigurationService) {
				found = true;
			}
		}
		assertTrue("Expect SVNTeamShareConfigurationService as an OSGi-Service", found);
	}

	/**
	 * 
	 * Tests that the SVNTeamSharenService is registered as an OSGi-Service.
	 * 
	 * @throws Exception
	 *             for Test
	 */
	@Test
	public void testTeamShareServiceRegistrationTest() throws Exception {
		TestEditorPlugInService plugInService = ServiceLookUpForTest.getService(TestEditorPlugInService.class);
		boolean found = false;
		Collection<TeamShareService> allTeamShareServices = plugInService.getAllTeamShareServices();
		for (TeamShareService teamShareService : allTeamShareServices) {
			if (teamShareService instanceof SVNTeamShareService) {
				found = true;
			}
		}
		assertTrue("Expect SVNTeamShareService as an OSGi-Service", found);
	}

	/**
	 * Checks that all SVN Services and the config bean have the same id.
	 * 
	 * @throws Exception
	 *             for Test
	 */
	@Test
	public void testIDManagement() throws Exception {
		assertEquals("Expect for Config and Configuration Service the same id.", new SVNTeamShareConfig().getId(),
				new SVNTeamShareConfigurationService().getId());
		assertEquals("Expect for Config and Service the same id.", new SVNTeamShareConfig().getId(),
				new SVNTeamShareService().getId());
	}
}
