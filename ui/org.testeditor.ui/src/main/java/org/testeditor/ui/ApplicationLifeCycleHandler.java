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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.e4.ui.workbench.lifecycle.PostContextCreate;
import org.eclipse.e4.ui.workbench.lifecycle.PreSave;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;
import org.osgi.service.prefs.BackingStoreException;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.jobs.TeamModificationCheckJob;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestProjectConfig;
import org.testeditor.core.services.interfaces.TeamShareStatusServiceNew;
import org.testeditor.core.services.interfaces.TestEditorConfigurationService;
import org.testeditor.core.services.interfaces.TestExecutionEnvironmentService;
import org.testeditor.core.services.interfaces.TestProjectService;
import org.testeditor.ui.constants.TestEditorConstants;
import org.testeditor.ui.utilities.TestEditorTranslationService;

/**
 * 
 * Handle Events on the Application Lifecycle.
 * 
 * This class starts and stops the TestServer.
 * 
 */
@SuppressWarnings("restriction")
public class ApplicationLifeCycleHandler {

	private static final Logger LOGGER = Logger.getLogger(ApplicationLifeCycleHandler.class);

	@Inject
	private IEclipseContext context;

	@Inject
	@Named(IServiceConstants.ACTIVE_SHELL)
	private Shell shell;

	@Inject
	private TestEditorConfigurationService testEditorConfigService;

	@Inject
	private TranslationService translationService;

	@Inject
	private TestProjectService testProjectService;

	private Collection<Thread> jobs = new ArrayList<Thread>();

	/**
	 * Inititalization of the Application.
	 * 
	 * Update of the context.
	 * 
	 * Starting Backend Server.
	 * 
	 */
	@PostContextCreate
	public void initApplication() {
		if (testEditorConfigService.isResetApplicationState()) {
			System.getProperties().put(IWorkbench.CLEAR_PERSISTED_STATE, "true");
			try {
				testEditorConfigService.setResetApplicationState(false);
			} catch (BackingStoreException e) {
				LOGGER.error("Error stroring restart state", e);
			}
		}
		try {
			testProjectService.reloadProjectList();
		} catch (SystemException e1) {
			LOGGER.error("Error loading projects", e1);
		}
		context.set(TestEditorTranslationService.class,
				ContextInjectionFactory.make(TestEditorTranslationService.class, context));
		initTestEditorCronJobs();
		initTeamStatusInformation();
		try {
			testEditorConfigService.exportGlobalVariablesToSystemProperties(true);
			testEditorConfigService.initializeSystemProperties();
		} catch (BackingStoreException e) {
			LOGGER.error("Error setting SystemVariables", e);
		} catch (IOException e) {

			LOGGER.error("Error setting SystemVariables", e);
		}
		startBackendServers();
	}

	/**
	 * for all given projects the svn update will be invoked.
	 */
	private void initTeamStatusInformation() {

		TestProjectService testProjectService = context.get(TestProjectService.class);
		TeamShareStatusServiceNew teamShareStatusService = context.get(TeamShareStatusServiceNew.class);

		List<TestProject> projects = testProjectService.getProjects();
		for (TestProject testProject : projects) {
			try {
				teamShareStatusService.update(testProject);
			} catch (FileNotFoundException e) {
				LOGGER.error(e);
			}
		}

	}

	/**
	 * Initializes Cron Jobs of the Test-Editor. This jobs run in the background
	 * and can send events to update the ui.
	 * 
	 */
	protected void initTestEditorCronJobs() {
		TeamModificationCheckJob job = ContextInjectionFactory.make(TeamModificationCheckJob.class, context);
		Thread jobRunner = new Thread(job, "Team sever observer");
		jobRunner.start();
		jobs.add(jobRunner);
		LOGGER.info("Team server observer started.");
	}

	/**
	 * Starts the TestServer to initialize the Backendsystems.
	 * 
	 */
	public void startBackendServers() {
		try {
			TestServerStarter starter = ContextInjectionFactory.make(TestServerStarter.class, context);
			TestProjectService testProjectService = context.get(TestProjectService.class);
			List<TestProject> projects = testProjectService.getProjects();
			for (TestProject testProject : projects) {
				startBackendServer(starter, testProject);
			}
		} catch (IOException | URISyntaxException e) {
			LOGGER.trace("Error starting Test Server", e);
			MessageDialog.openError(shell, "Error", e.getMessage());
		}
	}

	/**
	 * This method starts the backend server.
	 * 
	 * @param starter
	 *            TestServerStarter
	 * @param testProject
	 *            the project to be started
	 * @throws IOException
	 *             io Exception
	 * @throws URISyntaxException
	 *             {@link URISyntaxException}
	 */
	public void startBackendServer(TestServerStarter starter, TestProject testProject)
			throws IOException, URISyntaxException {
		TestProjectConfig projectConfig = testProject.getTestProjectConfig();
		if (projectConfig != null) {
			if (projectConfig.getProjectConfigVersion().equals(TestProjectService.UNSUPPORTED_CONFIG_VERSION)) {
				MessageDialog.openInformation(shell,
						translate("%unsupportedversionOfConfigurationForProject.title", testProject.getName()),
						translate("%unsupportedversionOfConfigurationForProject.message", testProject.getName()));
			} else {
				starter.startTestServer(testProject);
			}
		}
	}

	/**
	 * Shuts the Application down and clears ressources.
	 */
	@PreSave
	public void shutDownApplication() {
		for (Thread thread : jobs) {
			LOGGER.info("Shutdown the job " + thread.getName());
			thread.interrupt();
		}
		final ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);
		try {
			dialog.run(true, false, new IRunnableWithProgress() {

				@Override
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					stopBackendServers(monitor);
				}
			});
			TestExecutionEnvironmentService testExecService = context.get(TestExecutionEnvironmentService.class);
			testExecService.tearDownAllEnvironments();
		} catch (InvocationTargetException | InterruptedException | IOException e) {
			LOGGER.error("Error shutdown application.", e);
			MessageDialog.openError(shell, "Error", e.getLocalizedMessage());
		}
	}

	/**
	 * Terminates all running Backend Server.
	 * 
	 * @param monitor
	 *            ProgressMonitor to indicate progress of stopping TestServer.
	 */
	public void stopBackendServers(IProgressMonitor monitor) {
		try {
			TestServerStarter starter = ContextInjectionFactory.make(TestServerStarter.class, context);
			TestProjectService testProjectService = context.get(TestProjectService.class);
			List<TestProject> projects = testProjectService.getProjects();
			monitor.beginTask("Exit Test-Editor", projects.size());
			for (TestProject testProject : projects) {
				if (testProject.getTestProjectConfig() != null) {
					monitor.subTask("Stop test engine: " + testProject.getName());
					starter.stopTestServer(testProject);
					monitor.worked(1);
				}
			}
			monitor.done();
		} catch (Exception e) {
			LOGGER.trace("Error stopping Test Server", e);
		}
	}

	/**
	 * <pre>
	 * Translates the given key into the local language.
	 * 
	 * You can invoke this method with additionaly params depends on translated
	 * text. The params will be replace the placeholder in text.
	 * 
	 * e.g: 
	 * given text with placeholder: "{0} value and {1} value"
	 * params are: "first","second"
	 * result text: first value and second value
	 * 
	 * </pre>
	 * 
	 * @param key
	 *            key
	 * @param params
	 *            params in translated text given as placeholder example: this
	 * 
	 * @return local language value
	 */
	private String translate(String key, Object... params) {
		String translatedText = translationService.translate(key, TestEditorConstants.CONTRIBUTOR_URI);
		return MessageFormat.format(translatedText, params);

	}

	/**
	 * Starts test server for given project.
	 * 
	 * @param testProject
	 *            to be started test project
	 */
	public void startBackendServer(TestProject testProject) {
		try {
			TestServerStarter starter = ContextInjectionFactory.make(TestServerStarter.class, context);
			startBackendServer(starter, testProject);
		} catch (IOException | URISyntaxException e) {
			LOGGER.trace("Error starting Test Server", e);
			MessageDialog.openError(shell, "Error", e.getLocalizedMessage());
		}
	}

}
