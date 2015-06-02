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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.junit.Test;
import org.testeditor.core.model.team.TeamShareConfig;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestProjectConfig;
import org.testeditor.ui.constants.TestEditorConstants;
import org.testeditor.ui.parts.testExplorer.TestExplorer;

/**
 * 
 * IntegrationTests for ShareProjectHandler.
 * 
 */
public class ShareProjectHandlerTest {

	/**
	 * Tests the CanExecute Behavior of the handler.
	 * 
	 * @throws Exception
	 *             for Test
	 */
	@Test
	public void testCanExecute() throws Exception {
		ShareProjectHandler shareProjectHandler = new ShareProjectHandler();
		IEclipseContext context = EclipseContextFactory.create();
		TestProject testProject = new TestProject();
		testProject.setTestProjectConfig(new TestProjectConfig());
		context.set(TestEditorConstants.TEST_EXPLORER_VIEW, getTestExplorerWith(testProject));
		assertTrue("Expect can execute handler on config without share config", shareProjectHandler.canExecute(context));
		testProject.getTestProjectConfig().setTeamShareConfig(new TeamShareConfig() {

			@Override
			public String getId() {
				return null;
			}
		});
		assertFalse("Expect can not execute with a share config.", shareProjectHandler.canExecute(context));
	}

	/**
	 * 
	 * @param testProject
	 *            used in this Mock to be returned in the selection.
	 * @return StructuredSelection with the testProject.
	 */
	private TestExplorer getTestExplorerWith(final TestProject testProject) {
		return new TestExplorer(null) {

			@Override
			public IStructuredSelection getSelection() {
				return new StructuredSelection() {
					@Override
					public Object getFirstElement() {
						return testProject;
					}

					@Override
					public int size() {
						return 1;
					}

					@Override
					public boolean isEmpty() {
						return false;
					}
				};
			}

		};
	}

}
