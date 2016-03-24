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

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.testeditor.core.model.teststructure.TestProject;

/**
 * Modultests for SVNTeamShareService.
 *
 */
public class SVNTeamShareServiceTest {

	/**
	 * Tests the extraction of the project url from an url of svn structure.
	 * 
	 * @throws Exception
	 *             on test failure
	 */
	@Test
	public void testGetProjectURL() throws Exception {
		SVNTeamShareService service = new SVNTeamShareService();
		assertEquals("Extract from trunk", "http://srv/repo/prj/", service.getProjectURL("http://srv/repo/prj/trunk"));
		assertEquals("Extract from file in the trunk path", "http://srv/repo/prj/",
				service.getProjectURL("http://srv/repo/prj/trunk/src/Foo.txt"));
		assertEquals("Extract from tags", "http://srv/repo/prj/", service.getProjectURL("http://srv/repo/prj/tags"));
		assertEquals("Extract from branches", "http://srv/repo/prj/",
				service.getProjectURL("http://srv/repo/prj/branches/v1.1"));
		assertEquals("Extract from no svn layout", "http://srv/repo/prj/no/svn/layout",
				service.getProjectURL("http://srv/repo/prj/no/svn/layout"));
	}

	/**
	 * Tests the build of an svn url for the project.
	 * 
	 * @throws Exception
	 *             on test failure
	 */
	@Test
	public void testgetTargetUrl() throws Exception {
		SVNTeamShareService service = new SVNTeamShareService();
		TestProject testProject = new TestProject();
		testProject.setName("Prj");
		assertEquals("http://host/repo/prj/trunk/Prj",
				service.getTargetUrl("http://host/repo/prj/trunk", testProject).toString());
	}

}
