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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.junit.Test;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.services.interfaces.TestExceutionEnvironmentService;
import org.testeditor.core.services.interfaces.TestScenarioService;
import org.testeditor.ui.adapter.StructuredSelectionAdapter;
import org.testeditor.ui.adapter.TestExceutionEnvironmentServiceAdapter;
import org.testeditor.ui.constants.TestEditorConstants;

/**
 * Modultest for TearDownTestExecutionEnvironmentHandler and
 * ResetTestExecutionEnvironmentHandler.
 *
 */
public class ResetOrTearDownTestExecutionEnvironmentHandlerTest {

	/**
	 * Test the can execute on testprojects with an launched test envorinment.
	 */
	@Test
	public void testCanExecute() {
		IEclipseContext context = EclipseContextFactory.create();
		final TestProject myTp = new TestProject();
		context.set(TestEditorConstants.SELECTED_TEST_COMPONENTS, new StructuredSelectionAdapter() {
			@Override
			public Object getFirstElement() {
				return new TestProject();
			}
		});
		context.set(TestScenarioService.class, null);
		TestExceutionEnvironmentService serviceAdapter = new TestExceutionEnvironmentServiceAdapter() {
			public boolean isTestEnvironmentLaunchedFor(TestProject testProject) {
				return testProject == myTp;
			};
		};
		context.set(TestExceutionEnvironmentService.class, serviceAdapter);
		ShutDownTestExecutionEnvironmentHandler tearDownHandler = new ShutDownTestExecutionEnvironmentHandler();
		ResetTestExecutionEnvironmentHandler resetHandler = new ResetTestExecutionEnvironmentHandler();
		assertFalse(tearDownHandler.canExecute(context, serviceAdapter));
		assertFalse(resetHandler.canExecute(context, serviceAdapter));
		context.set(TestEditorConstants.SELECTED_TEST_COMPONENTS, new StructuredSelectionAdapter() {
			@Override
			public Object getFirstElement() {
				return myTp;
			}
		});
		assertTrue(tearDownHandler.canExecute(context, serviceAdapter));
		assertTrue(resetHandler.canExecute(context, serviceAdapter));
	}

	/**
	 * Tests the execute method.
	 */
	@Test
	public void testExecute() {
		IEclipseContext context = EclipseContextFactory.create();
		final Set<String> monitorSet = new HashSet<String>();
		context.set(TestEditorConstants.SELECTED_TEST_COMPONENTS, new StructuredSelectionAdapter() {
			@Override
			public Object getFirstElement() {
				return new TestProject();
			}
		});
		TestExceutionEnvironmentService serviceAdapter = new TestExceutionEnvironmentServiceAdapter() {
			@Override
			public void resetEnvironment(TestProject testProject, IProgressMonitor monitor)
					throws IOException, InterruptedException {
				monitorSet.add("reset");
			}

			@Override
			public void shutDownEnvironment(TestProject testProject, IProgressMonitor monitor)
					throws IOException, InterruptedException {
				monitorSet.add("teardown");
			}
		};
		context.set(TestExceutionEnvironmentService.class, serviceAdapter);
		ShutDownTestExecutionEnvironmentHandler tearDownHandler = new ShutDownTestExecutionEnvironmentHandler();
		ResetTestExecutionEnvironmentHandler resetHandler = new ResetTestExecutionEnvironmentHandler();
		tearDownHandler.execute(context, serviceAdapter);
		assertTrue(monitorSet.contains("teardown"));
		monitorSet.clear();
		resetHandler.execute(context, serviceAdapter);
		assertTrue(monitorSet.contains("reset"));
	}

}
