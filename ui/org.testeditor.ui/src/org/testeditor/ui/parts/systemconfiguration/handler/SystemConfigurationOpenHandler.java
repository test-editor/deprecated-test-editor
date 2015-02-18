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
package org.testeditor.ui.parts.systemconfiguration.handler;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.eclipse.swt.widgets.Shell;
import org.testeditor.ui.constants.TestEditorConstants;
import org.testeditor.ui.parts.systemconfiguration.SystemConfigurationEditor;

/**
 * opens the SystemConfigurationEditor.
 * 
 * @author llipinski
 * 
 */
public class SystemConfigurationOpenHandler {

	@Inject
	private EPartService partService;

	/**
	 * Executes the handler to show system-configuration in a dialog.
	 * 
	 * @param shell
	 *            The active shell
	 */
	@Execute
	public void execute(@Named(IServiceConstants.ACTIVE_SHELL) Shell shell) {
		MPart mPart = partService.findPart(TestEditorConstants.SYSTEM_CONFIGUTRATION_VIEW);
		if (mPart == null) {
			mPart = partService.createPart(SystemConfigurationEditor.ID);
			partService.showPart(mPart, PartState.ACTIVATE);
		}
		partService.activate(mPart);

	}
}
