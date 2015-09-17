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
package org.testeditor.ui;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.services.internal.events.EventBroker;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.Test;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.prefs.PreferencesService;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestProjectConfig;
import org.testeditor.core.services.interfaces.TestEditorConfigurationService;
import org.testeditor.core.services.interfaces.TestExecutionEnvironmentService;
import org.testeditor.core.services.interfaces.TestProjectService;
import org.testeditor.core.services.interfaces.TestServerService;
import org.testeditor.core.services.interfaces.TestStructureService;
import org.testeditor.ui.adapter.TestExceutionEnvironmentServiceAdapter;
import org.testeditor.ui.adapter.TestProjectServiceAdapter;
import org.testeditor.ui.mocks.PreferencesServiceMock;
import org.testeditor.ui.utilities.TestEditorTranslationService;

/**
 * 
 * Integrationtests for the ApplicationLifecycleHandler.
 * 
 */
@SuppressWarnings("restriction")
public class ApplicationLifeCycleHandlerTest {

	/**
	 * Test to start the Backendserver.
	 * 
	 * @throws Exception
	 *             for Test
	 */
	@Test
	public void testStartStopBackendServer() throws Exception {

		IEclipseContext context = getContext();

		TestProject tp1 = new TestProject();
		tp1.setName("tp1");
		TestProjectConfig config = new TestProjectConfig();
		tp1.setTestProjectConfig(config);
		TestProject tp2 = new TestProject();
		tp2.setName("tp2");
		config = new TestProjectConfig();
		tp2.setTestProjectConfig(config);
		TestProjectServiceAdapter testProjectServiceAdapter = new TestProjectServiceAdapter();
		testProjectServiceAdapter.getProjects().add(tp1);
		testProjectServiceAdapter.getProjects().add(tp2);
		context.set(TestProjectService.class, testProjectServiceAdapter);
		TestServerService serverService = context.get(TestServerService.class);
		context.set(TestEditorTranslationService.class,
				ContextInjectionFactory.make(TestEditorTranslationService.class, context));
		ApplicationLifeCycleHandler handler = ContextInjectionFactory.make(ApplicationLifeCycleHandler.class, context);
		handler.startBackendServers();
		assertTrue("Server 1 is running after Launch", serverService.isRunning(tp1));
		assertTrue("Server 2 is running after Launch", serverService.isRunning(tp2));
		handler.stopBackendServers(new NullProgressMonitor());
	}

	/**
	 * Tests the Init Process of the Application.
	 * 
	 */
	@Test
	public void testInit() {
		IEclipseContext context = getContext();

		ApplicationLifeCycleHandler handler = ContextInjectionFactory.make(ApplicationLifeCycleHandler.class, context);
		handler.initApplication();
		assertNotNull(context.get(TestEditorTranslationService.class));
		TestEditorConfigurationService configurationService = context.get(TestEditorConfigurationService.class);
		assertFalse("Expecting no reset on default", configurationService.isResetApplicationState());
	}

	/**
	 * Test the Restart and Reset Logic of the ApplicationLifeCycleHanlder.
	 * 
	 * @throws Exception
	 *             on problems accessing the preference store.
	 */
	@Test
	public void testRestartAndResetBehaviorOnStart() throws Exception {

		IEclipseContext context = getContext();

		TestEditorConfigurationService configurationService = context.get(TestEditorConfigurationService.class);

		assertFalse("Expecting no reset on default", configurationService.isResetApplicationState());
		configurationService.setResetApplicationState(true);
		assertTrue("Expecting reset is set.", configurationService.isResetApplicationState());
		ApplicationLifeCycleHandler handler = ContextInjectionFactory.make(ApplicationLifeCycleHandler.class, context);
		handler.initApplication();
		String property = System.getProperty(IWorkbench.CLEAR_PERSISTED_STATE);
		assertTrue("Expecting clear persisted state is set.", Boolean.valueOf(property));
	}

	/**
	 * Tests the shutdown method to call Stop Servers.
	 */
	@Test
	public void testShutDownApplication() {
		final HashSet<String> set = new HashSet<String>();
		ApplicationLifeCycleHandler handler = new ApplicationLifeCycleHandler() {
			@Override
			public void stopBackendServers(org.eclipse.core.runtime.IProgressMonitor monitor) {
				set.add("stop");
			}
		};
		IEclipseContext context = EclipseContextFactory
				.getServiceContext(FrameworkUtil.getBundle(getClass()).getBundleContext());
		context.set(IServiceConstants.ACTIVE_SHELL, null);
		context.set(TestExecutionEnvironmentService.class, new TestExceutionEnvironmentServiceAdapter());
		context.set(TranslationService.class, getTranslationServiceMock());

		ContextInjectionFactory.inject(handler, context);

		handler.shutDownApplication();
		assertTrue(set.contains("stop"));
	}

	/**
	 * Prepare for test and returns the context. Set Mock-Objects in context.
	 * 
	 * @return IEclipseContext
	 */
	private IEclipseContext getContext() {
		IEclipseContext context = EclipseContextFactory
				.getServiceContext(FrameworkUtil.getBundle(ApplicationLifeCycleHandlerTest.class).getBundleContext());
		context.set(TranslationService.class, getTranslationServiceMock());
		context.set(TestStructureService.class, null);
		context.set(PreferencesService.class, new PreferencesServiceMock());
		IEventBroker eventBroker = new EventBroker();
		context.set(IEventBroker.class, eventBroker);
		context.set(IServiceConstants.ACTIVE_SHELL, new Shell(Display.getDefault()));
		TestProjectServiceAdapter testProjectServiceAdapter = new TestProjectServiceAdapter();
		context.set(TestProjectService.class, testProjectServiceAdapter);
		return context;
	}

	/**
	 * 
	 * @return Mock for TranslationService
	 */
	private TranslationService getTranslationServiceMock() {
		return new TranslationService() {
			@Override
			public String translate(String key, String contributorURI) {
				return "";
			}
		};
	}

}
