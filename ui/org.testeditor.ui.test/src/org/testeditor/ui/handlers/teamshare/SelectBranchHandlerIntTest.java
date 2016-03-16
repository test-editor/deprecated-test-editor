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
package org.testeditor.ui.handlers.teamshare;

import static org.junit.Assert.assertTrue;

import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.junit.Test;
import org.testeditor.core.model.team.TeamShareConfig;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestProjectConfig;
import org.testeditor.ui.constants.TestEditorConstants;

/**
 * Integration tests for the SelectBranchHandler.
 *
 */
public class SelectBranchHandlerIntTest {

	/**
	 * Tests the CanExecute Behavior of the handler.
	 * 
	 * @throws Exception
	 *             for Test
	 */
	@Test
	public void testCanExecute() throws Exception {
		SelectBranchHandler handler = new SelectBranchHandler();
		IEclipseContext context = EclipseContextFactory.create();
		TestProject testProject = new TestProject();
		TestProjectConfig projectConfig = new TestProjectConfig();
		projectConfig.setTeamShareConfig(new TeamShareConfig() {

			@Override
			public String getId() {
				return null;
			}
		});
		testProject.setTestProjectConfig(projectConfig);
		context.set(TestEditorConstants.TEST_EXPLORER_VIEW,
				new TeamShareHandlerMockFactory().getTestExplorerWith(testProject));
		assertTrue("Expect can execute handler on config with share config", handler.canExecute(context));
	}

}
