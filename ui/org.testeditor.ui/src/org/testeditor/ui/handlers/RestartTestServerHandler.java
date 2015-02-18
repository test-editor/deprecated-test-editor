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

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.ui.TestServerStarter;
import org.testeditor.ui.parts.projecteditor.TestProjectEditor;
import org.testeditor.ui.utilities.TestEditorTranslationService;

/**
 * 
 * handler for the restart of the testserver.
 * 
 */
public class RestartTestServerHandler {

	private static final Logger LOGGER = Logger.getLogger(TestProjectEditor.class);

	/**
	 * executs the rstart of the testserver.
	 * 
	 * @param context
	 *            IEclipseContext
	 * @param translationService
	 *            used to translate messages.
	 */
	@Execute
	public void execute(final IEclipseContext context, final TestEditorTranslationService translationService) {
		EPartService partService = context.get(EPartService.class);
		Collection<MPart> parts = partService.getParts();
		MPart mpart = null;
		for (MPart mPart2 : parts) {
			if (mPart2.getElementId().equals(TestProjectEditor.ID) && (mPart2.getObject()) != null && mPart2.isOnTop()) {
				mpart = mPart2;
				break;
			}
		}
		if (mpart != null) {
			if (mpart.isDirty()) {
				partService.savePart(mpart, true);
			}

			final TestProjectEditor testProjectEditor = (TestProjectEditor) mpart.getObject();
			final TestProject testProject = testProjectEditor.getTestProject();
			ProgressMonitorDialog dialog = new ProgressMonitorDialog(Display.getCurrent().getActiveShell());
			try {
				dialog.run(true, false, new IRunnableWithProgress() {

					@Override
					public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
						try {
							monitor.beginTask(translationService.translate("%testprojecteditor.restart"),
									IProgressMonitor.UNKNOWN);

							TestServerStarter serverStarter = ContextInjectionFactory.make(TestServerStarter.class,
									context);

							serverStarter.stopTestServer(testProject);
							serverStarter.startTestServer(testProject);

							Display.getDefault().asyncExec(new Runnable() {
								@Override
								public void run() {
									testProjectEditor.updatePort();
								}
							});
						} catch (IOException | URISyntaxException e1) {
							LOGGER.trace("Error starting Test Server", e1);
							MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error",
									e1.getLocalizedMessage());
						}
					}
				});
			} catch (InvocationTargetException | InterruptedException e1) {
				LOGGER.error("Error starting Test Server", e1);
				MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error", e1.getLocalizedMessage());
			}
			LOGGER.trace("tt24t24");
		}
	}
}
