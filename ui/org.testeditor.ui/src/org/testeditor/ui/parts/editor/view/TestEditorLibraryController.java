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
package org.testeditor.ui.parts.editor.view;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Shell;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.model.action.ProjectActionGroups;
import org.testeditor.core.model.teststructure.LibraryLoadingStatus;
import org.testeditor.core.model.teststructure.TestFlow;
import org.testeditor.core.services.interfaces.LibraryDataStoreService;
import org.testeditor.core.services.interfaces.LibraryReaderService;
import org.testeditor.ui.utilities.TestEditorCatchReadingExceptions;
import org.testeditor.ui.utilities.TestEditorTranslationService;

/**
 * 
 * Controller for reading the Library.
 * 
 * @author llipinski
 */
public class TestEditorLibraryController {

	@Inject
	private LibraryReaderService libraryReaderService;
	@Inject
	private LibraryDataStoreService libraryDataStoreService;

	private static final Logger LOGGER = Logger.getLogger(TestEditorLibraryController.class);

	/**
	 * reading the library.
	 * 
	 * @param translationService
	 *            TranslationService
	 * @param logger
	 *            Logger
	 * @param testStructure
	 *            the {@link TestFlow}
	 * @param libraryStatus
	 *            {@link LibraryLoadingStatus}
	 * @param shell
	 *            Shell
	 */
	protected void readingLibrary(TestEditorTranslationService translationService, Logger logger,
			TestFlow testStructure, LibraryLoadingStatus libraryStatus, Shell shell) {
		try {
			ProjectActionGroups projectActionGroups = libraryReaderService.readBasisLibrary(testStructure
					.getRootElement().getTestProjectConfig().getProjectLibraryConfig());
			projectActionGroups.setProjectName(testStructure.getRootElement().getName());
			LOGGER.debug("ProjectGroups read for: " + projectActionGroups.getProjectName());
			libraryDataStoreService.addProjectActionGroups(projectActionGroups);
		} catch (SystemException except) {
			TestEditorCatchReadingExceptions.catchException(shell, translationService, logger, except);
			libraryStatus.setErrorLessLoaded(false);
		}
	}
}
