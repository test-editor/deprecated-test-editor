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
package org.testeditor.ui.handlers.teamshare;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Iterator;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.testeditor.core.constants.TestEditorCoreEventConstants;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.services.interfaces.ProgressListener;
import org.testeditor.core.services.interfaces.TeamShareService;
import org.testeditor.ui.constants.TestEditorConstants;
import org.testeditor.ui.handlers.CanExecuteTestExplorerHandlerRules;
import org.testeditor.ui.parts.testExplorer.TestExplorer;
import org.testeditor.ui.utilities.TestEditorTranslationService;

/**
 * parent-class for the common parts of the update- or approve-element-handler.
 * 
 */
public abstract class AbstractUpdateOrApproveHandler {

	@Inject
	private TeamShareService teamShareService;
	@Inject
	protected TestEditorTranslationService translationService;

	private static final Logger LOGGER = Logger.getLogger(AbstractUpdateOrApproveHandler.class);

	private HashSet<TestProject> testProjectSet = new HashSet<TestProject>();

	/**
	 * 
	 * @param context
	 *            current Context of the Application.
	 * 
	 * @return true, if the project of every testStructure is under
	 *         version-control.
	 */
	@CanExecute
	public boolean canExecute(IEclipseContext context) {

		TestExplorer testExplorer = (TestExplorer) context.get(TestEditorConstants.TEST_EXPLORER_VIEW);

		IStructuredSelection selection = testExplorer.getSelection();
		CanExecuteTestExplorerHandlerRules canExecuteTestExplorerHandlerRules = new CanExecuteTestExplorerHandlerRules();
		return canExecuteTestExplorerHandlerRules.canExecuteOnOneOrManyElementRule(selection)
				&& canExecuteTestExplorerHandlerRules.canExecuteOnTeamShareProject(selection);
	}

	/**
	 * executes the event for the selected-elements.
	 * 
	 * @param eventBroker
	 *            used to send an update message.
	 * @param context
	 *            Eclipse context to look up the selection to work on.
	 */
	@Execute
	public void execute(IEventBroker eventBroker, IEclipseContext context) {
		testProjectSet = new HashSet<TestProject>();
		TestExplorer testExplorer = (TestExplorer) context.get(TestEditorConstants.TEST_EXPLORER_VIEW);

		final Iterator<TestStructure> iter = testExplorer.getSelection().iterator();
		final Shell activeShell = Display.getCurrent().getActiveShell();
		final ProgressMonitorDialog dialog = new ProgressMonitorDialog(activeShell);
		try {
			dialog.run(true, false, new IRunnableWithProgress() {

				@Override
				public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					monitor.beginTask(getMessage(), IProgressMonitor.UNKNOWN);

					try {
						boolean noError = true;
						while (iter.hasNext()) {
							Object next = iter.next();
							if (next instanceof TestStructure) {
								TestStructure testStructure = (TestStructure) next;
								teamShareService.addProgressListener(testStructure, new ProgressListener() {
									@Override
									public void log(String progressInfo) {
										monitor.subTask(progressInfo);
									}

									@Override
									public boolean isCanceled() {
										return monitor.isCanceled();
									}
								});

								if (executeSpecials(testStructure)) {
									addToProjectSet(testStructure.getRootElement());
								} else {
									noError = false;
								}
								if (noError) {
									getDisplay().syncExec(new Runnable() {

										@Override
										public void run() {
											showCompletedMessage();
										}
									});

								}
							}
						}
					} catch (Exception e) {
						LOGGER.error(e.getMessage(), e);
						MessageDialog.openError(activeShell, translationService.translate("%error"), e.getMessage());
					}
					monitor.done();
				}
			});

			if (!testExplorer.getSelection().isEmpty()) {
				eventBroker.post(TestEditorCoreEventConstants.TESTSTRUCTURE_MODEL_CHANGED_RELOADED,
						((TestStructure) testExplorer.getSelection().getFirstElement()).getFullName());
			} else {
				LOGGER.warn("selection does not exist in testexplorer !");
			}

		} catch (InvocationTargetException | InterruptedException e) {
			LOGGER.error(e.getMessage());
			MessageDialog.openError(activeShell, translationService.translate("%error"), e.getMessage());
		}
	}

	/**
	 * abstract method should be implemented by the children.
	 * 
	 * @return the message for the monitor-dialog
	 */
	abstract String getMessage();

	/**
	 * executes the special operations in the children. abstract method should
	 * be implemented by the children.
	 * 
	 * @param testStructure
	 *            TestStructure
	 * @return true, if every thing is ok.
	 */
	abstract boolean executeSpecials(TestStructure testStructure);

	/**
	 * shows the completed-message. abstract method should be implemented by the
	 * children.
	 */
	abstract void showCompletedMessage();

	/**
	 * adds an element to the list of testProjects. this TestProjects will be
	 * later restarted.
	 * 
	 * @param testProject
	 *            TestProject
	 */
	protected void addToProjectSet(TestProject testProject) {
		testProjectSet.add(testProject);

	}

	/**
	 * gets the display.
	 * 
	 * @return the display
	 */
	protected static Display getDisplay() {
		Display display = Display.getCurrent();
		// may be null if outside the UI thread
		if (display == null) {
			display = Display.getDefault();
		}
		return display;
	}

	/**
	 * 
	 * @return the injected TeamShareService.
	 */
	protected TeamShareService getTeamService() {
		return teamShareService;
	}

}
