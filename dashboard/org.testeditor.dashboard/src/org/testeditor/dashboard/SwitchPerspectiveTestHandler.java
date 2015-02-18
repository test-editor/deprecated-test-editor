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

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

/**
 * @author alebedev
 * 
 *         switches perspective from Dashboard back to Test-Editor
 */
public class SwitchPerspectiveTestHandler {

	/**
	 * @param app
	 *            org.eclipse.e4.ide.application
	 * @param partService
	 *            to switch perspective
	 * @param modelService
	 *            to find test-editor perspective
	 */
	@Execute
	public void execute(MApplication app, EPartService partService, EModelService modelService) {
		MPerspective element = (MPerspective) modelService.find("org.testeditor.perspective.testeditor", app);
		partService.switchPerspective(element);
	}
}