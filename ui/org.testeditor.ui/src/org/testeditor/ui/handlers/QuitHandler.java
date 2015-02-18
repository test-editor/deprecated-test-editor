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

import javax.inject.Named;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.testeditor.ui.utilities.TestEditorTranslationService;

/**
 * 
 * Ask User to quit the application.
 * 
 */
public class QuitHandler {

	/**
	 * 
	 * @param workbench
	 *            Workbench
	 * @param context
	 *            EclipseContext
	 * @param shell
	 *            Active Shell
	 * @param translation
	 *            Service to translate the Confirm message
	 * @param partService
	 *            Service to lookup for dirty Parts for Save Question.
	 * @throws InvocationTargetException
	 *             on Error
	 * @throws InterruptedException
	 *             on interrupting the progress monitor.
	 */
	@Execute
	public void execute(IWorkbench workbench, IEclipseContext context,
			@Named(IServiceConstants.ACTIVE_SHELL) Shell shell, TestEditorTranslationService translation,
			EPartService partService) throws InvocationTargetException, InterruptedException {
		if (MessageDialog.openConfirm(shell, translation.translate("%handler.quit.confirm"),
				translation.translate("%handler.quit.question"))) {
			partService.saveAll(true);
			workbench.close();
		}
	}

}
