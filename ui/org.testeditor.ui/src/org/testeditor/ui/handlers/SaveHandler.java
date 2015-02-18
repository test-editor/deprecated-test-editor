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

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.util.TestProtocolService;
import org.testeditor.teamshare.svn.TeamShareStatus;
import org.testeditor.ui.ITestStructureEditor;
import org.testeditor.ui.constants.TestEditorConstants;
import org.testeditor.ui.parts.testExplorer.TestExplorer;

/**
 * 
 * Handler to Execute save Operation on the active Editorpart.
 * 
 */
public class SaveHandler {

	@Inject
	private TestProtocolService testProtocolService;

	@Inject
	private IEventBroker eventBroker;

	/**
	 * Checks that the dirty Editor is on Top.
	 * 
	 * @param partService
	 *            to check the ui state.
	 * @return true id can excuete is valid on the active editor.
	 */
	@CanExecute
	public boolean canExecute(EPartService partService) {
		return partService.getActivePart().isDirty();
	}

	/**
	 * Get the Editor which is open, and on top of the editors and dirty.
	 * 
	 * @param partService
	 *            to lookup the dirty part.
	 * 
	 * @return the dirty part which is on top of the editors.
	 */
	private MPart getActiveDirtyPart(EPartService partService) {
		MPart activePart = partService.getActivePart();
		if (activePart.isDirty()) {
			return activePart;
		}
		return null;
	}

	/**
	 * execute the save operation.
	 * 
	 * @param context
	 *            Eclipse Context
	 * @param shell
	 *            active Shell
	 * @param partService
	 *            used to refresh the ui.
	 * @throws InvocationTargetException
	 *             on error
	 * @throws InterruptedException
	 *             of the Progress Monitor
	 */
	@Execute
	public void execute(IEclipseContext context, @Named(IServiceConstants.ACTIVE_SHELL) Shell shell,
			EPartService partService) throws InvocationTargetException, InterruptedException {
		final IEclipseContext pmContext = context.createChild();
		final MPart activeDirtyPart = getActiveDirtyPart(partService);
		if (activeDirtyPart != null) {

			final Object clientObject = activeDirtyPart.getObject();

			ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);
			dialog.open();
			dialog.run(true, true, new IRunnableWithProgress() {
				@Override
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

					pmContext.set(IProgressMonitor.class.getName(), monitor);

					ContextInjectionFactory.invoke(clientObject, Persist.class, pmContext, null);

				}

			});
			if (activeDirtyPart.getObject() instanceof ITestStructureEditor) {
				TestProject testProject = ((ITestStructureEditor) activeDirtyPart.getObject()).getTestStructure()
						.getRootElement();
				if (testProject.getTestProjectConfig().isTeamSharedProject()) {

					TeamShareStatus shareState = new TeamShareStatus(eventBroker);
					shareState.setSVNStatusForProject(testProject);

				}
			}
			refreshNodeIcon(partService);

		}

		pmContext.dispose();

	}

	/**
	 * After change test, reset test state for rendering icon for default.
	 * 
	 * @param partService
	 *            used to refresh the ui.
	 * 
	 */
	private void refreshNodeIcon(EPartService partService) {
		TestExplorer testExplorer = (TestExplorer) partService.findPart(TestEditorConstants.TEST_EXPLORER_VIEW)
				.getObject();
		TestStructure selected = (TestStructure) testExplorer.getSelection().getFirstElement();
		// only, if we have a selection
		if (selected != null) {
			// refresh the icon depends on test result
			testProtocolService.remove(selected);
			testExplorer.refreshTreeViewerOnTestStrucutre(selected);
		}
	}

}
