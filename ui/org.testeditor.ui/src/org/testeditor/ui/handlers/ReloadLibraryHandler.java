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

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.model.action.ProjectActionGroups;
import org.testeditor.core.model.teststructure.LibraryLoadingStatus;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.services.interfaces.LibraryDataStoreService;
import org.testeditor.core.services.interfaces.LibraryReaderService;
import org.testeditor.ui.constants.TestEditorUIEventConstants;
import org.testeditor.ui.parts.projecteditor.TestProjectEditor;
import org.testeditor.ui.utilities.TestEditorCatchReadingExceptions;
import org.testeditor.ui.utilities.TestEditorTranslationService;

/**
 * 
 * Handler for the reload of the library.
 * 
 */
public class ReloadLibraryHandler {

	@Inject
	private TestEditorTranslationService translationService;

	private static final Logger LOGGER = Logger.getLogger(ReloadLibraryHandler.class);

	/**
	 * Executes the reload of the library.
	 * 
	 * @param context
	 *            EclipseContext used to create the save action.
	 * @param partService
	 *            {@link EPartService}
	 * @param libraryReaderService
	 *            LibraryReaderService
	 * @param libraryDataStoreService
	 *            LibraryDataStoreService
	 */
	@Execute
	public void execute(final IEclipseContext context, EPartService partService,
			final LibraryReaderService libraryReaderService, final LibraryDataStoreService libraryDataStoreService) {

		if (!partService.saveAll(true)) {
			return;
		}
		Collection<MPart> parts = partService.getParts();
		MPart mpart = null;
		for (MPart mPart2 : parts) {
			if (mPart2.getElementId().equals(TestProjectEditor.ID) && (mPart2.getObject()) != null && mPart2.isOnTop()) {
				mpart = mPart2;
				break;
			}
		}
		if (mpart != null) {

			final TestProject testProject = ((TestProjectEditor) mpart.getObject()).getTestProject();
			ProgressMonitorDialog dialog = new ProgressMonitorDialog(Display.getCurrent().getActiveShell());

			try {
				dialog.run(true, false, new IRunnableWithProgress() {

					@Override
					public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
						monitor.beginTask(translationService.translate("%testprojecteditor.reloadLibrary"),
								IProgressMonitor.UNKNOWN);
						Display.getDefault().asyncExec(new Runnable() {
							@Override
							public void run() {
								try {
									relaodLibrary(testProject, context.get(IEventBroker.class),
											libraryDataStoreService, libraryReaderService);
								} catch (SystemException e) {
									LOGGER.error("Error geting children of project", e);
									MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error",
											e.getLocalizedMessage());
								}
							}
						});
					}
				});
			} catch (InvocationTargetException e1) {
				LOGGER.trace("Error starting Test Server", e1);
				MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error", e1.getLocalizedMessage());
			} catch (InterruptedException e1) {
				LOGGER.trace("Error starting Test Server", e1);
				MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error", e1.getLocalizedMessage());
			}
		}
	}

	/**
	 * reading the library.
	 * 
	 * @param testProject
	 *            the {@link TestProject}
	 * @param libraryDataStoreService
	 *            service to store library
	 * @param libraryReaderService
	 *            service to read library
	 */
	private void readingLibrary(TestProject testProject, LibraryDataStoreService libraryDataStoreService,
			LibraryReaderService libraryReaderService) {

		LibraryLoadingStatus libraryStatus = testProject.getTestProjectConfig().getLibraryLoadingStatus();
		try {

			ProjectActionGroups projectActionGroups = libraryReaderService.readBasisLibrary(testProject
					.getTestProjectConfig().getProjectLibraryConfig());
			projectActionGroups.setProjectName(testProject.getName());
			libraryDataStoreService.addProjectActionGroups(projectActionGroups);
		} catch (SystemException except) {
			Shell shell = Display.getCurrent().getActiveShell();
			TestEditorCatchReadingExceptions.catchException(shell, translationService, LOGGER, except);
			libraryStatus.setErrorLessLoaded(false);
		}
	}

	/**
	 * reloads the library for the testProject and reloads opened the test of
	 * the project.
	 * 
	 * @param testProject
	 *            the TestProject
	 * @param eventBroker
	 *            to fire reload Event.
	 * @param libraryDataStoreService
	 *            service to store library
	 * @param libraryReaderService
	 *            service to read library
	 * @throws SystemException
	 *             on loading of children
	 */
	private void relaodLibrary(TestProject testProject, IEventBroker eventBroker,
			LibraryDataStoreService libraryDataStoreService, LibraryReaderService libraryReaderService)
			throws SystemException {
		testProject.getTestProjectConfig().getLibraryLoadingStatus().setLoaded(false);
		readingLibrary(testProject, libraryDataStoreService, libraryReaderService);
		eventBroker.send(TestEditorUIEventConstants.LIBRARY_LOADED_FOR_PROJECT, testProject);
	}
}