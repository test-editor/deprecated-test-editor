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

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.model.action.ProjectActionGroups;
import org.testeditor.core.model.teststructure.LibraryLoadingStatus;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.services.interfaces.ActionGroupService;
import org.testeditor.core.services.interfaces.LibraryConstructionException;
import org.testeditor.core.services.interfaces.LibraryReaderService;
import org.testeditor.ui.constants.TestEditorUIEventConstants;
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

	@Inject
	private IEclipseContext context;

	@Inject
	private EPartService partService;

	@Inject
	private LibraryReaderService libraryReaderService;

	@Inject
	private ActionGroupService actionGroupService;

	/**
	 * Executes the reload of the library.
	 * 
	 * @param testProject
	 *            TestProject
	 * 
	 */
	@Execute
	public void execute(TestProject testProject) {

		if (!partService.saveAll(true)) {
			return;
		}

		try {
			relaodLibrary(testProject, context.get(IEventBroker.class), actionGroupService, libraryReaderService);
		} catch (SystemException e) {
			LOGGER.error("Error geting children of project", e);
			MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error", e.getLocalizedMessage());
		}

	}

	/**
	 * reading the library.
	 * 
	 * @param testProject
	 *            the {@link TestProject}
	 * @param actionGroupService
	 *            service to store library
	 * @param libraryReaderService
	 *            service to read library
	 */
	private void readingLibrary(TestProject testProject, ActionGroupService actionGroupService,
			LibraryReaderService libraryReaderService) {

		LibraryLoadingStatus libraryStatus = testProject.getTestProjectConfig().getLibraryLoadingStatus();
		try {

			ProjectActionGroups projectActionGroups = libraryReaderService.readBasisLibrary(testProject
					.getTestProjectConfig().getProjectLibraryConfig());
			projectActionGroups.setProjectName(testProject.getName());
			actionGroupService.addProjectActionGroups(projectActionGroups);
		} catch (LibraryConstructionException e) {
			String mappingErrorPartOne = translationService.translate("%editController.ErrorObjectMappingPartOne");
			String mappingErrorPartTow = translationService.translate("%editController.ErrorObjectMappingPartTow");
			MessageDialog.openError(
					Display.getCurrent().getActiveShell(),
					translationService.translate("%editController.LibraryNotLoaded"),
					translationService.translate("%editController.ErrorReadingLibrary") + mappingErrorPartOne + " "
							+ e.getMessage() + " " + mappingErrorPartTow);
			LOGGER.error("Error reading library :: FAILED" + mappingErrorPartOne + " " + e.getMessage() + " "
					+ mappingErrorPartTow, e);
			libraryStatus.setErrorLessLoaded(false);
		} catch (SystemException e) {
			MessageDialog.openError(
					Display.getCurrent().getActiveShell(),
					translationService.translate("%editController.LibraryNotLoaded"),
					translationService.translate("%editController.ErrorReadingLibrary") + ": "
							+ e.getLocalizedMessage());
			LOGGER.error("Error reading library :: FAILED", e);
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
	 * @param actionGroupService
	 *            service to store library
	 * @param libraryReaderService
	 *            service to read library
	 * @throws SystemException
	 *             on loading of children
	 */
	private void relaodLibrary(TestProject testProject, IEventBroker eventBroker,
			ActionGroupService actionGroupService, LibraryReaderService libraryReaderService) throws SystemException {
		testProject.getTestProjectConfig().getLibraryLoadingStatus().setLoaded(false);
		readingLibrary(testProject, actionGroupService, libraryReaderService);
		eventBroker.send(TestEditorUIEventConstants.LIBRARY_LOADED_FOR_PROJECT, testProject);
	}
}