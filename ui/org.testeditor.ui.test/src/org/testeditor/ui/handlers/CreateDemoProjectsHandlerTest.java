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

import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.services.internal.events.EventBroker;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.Test;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestProjectConfig;
import org.testeditor.core.services.interfaces.TestProjectService;
import org.testeditor.ui.adapter.TestProjectServiceAdapter;

/**
 * 
 * Integrationtests for CreateDemoProjectsHandler.
 * 
 */
public class CreateDemoProjectsHandlerTest {

	/**
	 * Test that it only can execute on an Empty Workspace.
	 * 
	 * @throws Exception
	 *             for Test
	 */
	@Test
	public void testCanExecute() throws Exception {
		IEclipseContext context = EclipseContextFactory.create();
		context.set(IEventBroker.class, new EventBroker());
		context.set(TestProjectService.class, getTestProjectServiceMock());
		context.set(IServiceConstants.ACTIVE_SHELL, new Shell(Display.getDefault()));
		ContextInjectionFactory.make(CreateDemoProjectsHandler.class, context);
	}

	/**
	 * 
	 * @return TestProjectService Mock for Test.
	 */
	private TestProjectService getTestProjectServiceMock() {
		return new TestProjectServiceAdapter() {

			@Override
			public List<TestProject> getProjects() {
				return new ArrayList<TestProject>();
			}

			public TestProjectConfig getProjectConfigFor(TestProject testProject) throws SystemException {
				fail("test should not call this method");
				return null;
			}

			@Override
			public void storeProjectConfig(TestProject testProject, TestProjectConfig config) throws SystemException {
				fail("test should not call this method");
			}

			@Override
			public TestProject createNewProject(String projectName) throws IOException, SystemException {
				fail("test should not call this method");
				return null;
			}

			@Override
			public TestProject renameTestproject(TestProject testProject, String newName) throws IOException,
					SystemException {
				fail("test should not call this method");
				return null;
			}

		};
	}
}
