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
package org.testeditor.ui.utilities;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.services.interfaces.LibraryReadException;
import org.testeditor.core.services.interfaces.ObjectTreeConstructionException;

/**
 * 
 * central object to catch the special exceptions while reading the library.
 * 
 * @author llipinski
 */
public final class TestEditorCatchReadingExceptions {
	/**
	 * Don't create objects of this constants class.
	 */
	private TestEditorCatchReadingExceptions() {
	}

	/**
	 * this method handles the exception.
	 * 
	 * @param shell
	 *            Shell for the dialog
	 * @param translationService
	 *            to translate the message
	 * @param log
	 *            the specific logger
	 * @param except
	 *            the exception
	 */
	public static void catchException(Shell shell, TestEditorTranslationService translationService, Logger log,
			SystemException except) {
		String dialogTitle = translationService.translate("%editController.LibraryNotLoaded");
		String errorReading = translationService.translate("%editController.ErrorReadingLibrary");

		if (except instanceof LibraryReadException) {
			MessageDialog.openError(Display.getCurrent().getActiveShell(), dialogTitle, errorReading + ": "
					+ except.getCause().getMessage());
			log.error("Error reading library :: FAILED", except);
		} else if (except instanceof ObjectTreeConstructionException) {
			String mappingErrorPartOne = translationService.translate("%editController.ErrorObjectMappingPartOne");
			String mappingErrorPartTow = translationService.translate("%editController.ErrorObjectMappingPartTow");
			MessageDialog.openError(Display.getCurrent().getActiveShell(), dialogTitle, errorReading
					+ mappingErrorPartOne + " " + except.getMessage() + " " + mappingErrorPartTow);
			log.error("Error reading library :: FAILED" + mappingErrorPartOne + " " + except.getMessage() + " "
					+ mappingErrorPartTow, except);
		} else {
			MessageDialog.openError(Display.getCurrent().getActiveShell(), dialogTitle, errorReading + ": "
					+ except.getCause().getCause().getLocalizedMessage());
			log.error("Error reading library :: FAILED", except);
		}

	}
}
