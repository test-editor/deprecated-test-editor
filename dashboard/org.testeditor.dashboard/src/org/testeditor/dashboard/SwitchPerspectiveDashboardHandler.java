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
package org.testeditor.dashboard;

import java.io.IOException;
import java.text.ParseException;

import javax.inject.Named;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.widgets.Shell;
import org.jdom2.JDOMException;
import org.testeditor.core.exceptions.SystemException;

/**
 * @author alebedev
 * 
 *         handler to switch perspective to dashboard view
 */
public class SwitchPerspectiveDashboardHandler {

	/**
	 * checks if perspective is Test-Editor and switches to Dashboard.
	 * 
	 * @param app
	 *            org.eclipse.e4.ide.application
	 * @param partService
	 *            to switch perspective
	 * @param modelService
	 *            to find dashboard perspective
	 * @param window
	 *            trimmed window
	 * @param context
	 *            IEclipseContext context
	 * @param shell
	 *            Shell {Test-Editor}
	 * @throws JDOMException
	 *             if one of the arguments is invalid
	 * @throws IOException
	 *             Signals that an I/O exception of some sort has occurred
	 * @throws ParseException
	 *             Signals that an error has been reached unexpectedly while
	 *             parsing.
	 * @throws SystemException
	 *             If the transaction service fails in an unexpected way
	 */
	@Execute
	public void execute(MApplication app, EPartService partService, EModelService modelService, MWindow window,
			IEclipseContext context, @Named(IServiceConstants.ACTIVE_SHELL) final Shell shell) throws JDOMException,
			IOException, ParseException, SystemException {
		MPerspective dashboard = (MPerspective) modelService.find("org.testeditor.ui.perspective.dasboard", app);
		if (!modelService.getActivePerspective(window).getLabel().equals("DASHBOARD")) {
			partService.switchPerspective(dashboard);
			TableLastRuns tab = (TableLastRuns) partService.findPart("org.testeditor.ui.part.0").getObject();
			tab.refresh(null, modelService, window, app, context);
		}
	}
}