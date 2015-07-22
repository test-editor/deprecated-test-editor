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

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

import javax.inject.Inject;

import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuItem;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.jdom2.JDOMException;
import org.testeditor.core.exceptions.SystemException;

/**
 * @author alebedev
 * 
 *         project drop down menu for LastRunTable
 */
public class DirectMenuItem {

	@Inject
	private TranslationService translationService;

	/**
	 * The contributor URI.
	 */
	public static final String CONTRIBUTOR_URI = "platform:/plugin/org.testeditor.dashboard";

	/**
	 * takes selected project from dropdown menu and refreshes LastRunTable.
	 * 
	 * @param menuItem
	 *            project from drop down menu (DemoWebTests)
	 * @param partService
	 *            to find part containing table
	 * @param modelService
	 *            to check part label
	 * @param window
	 *            trimmed window
	 * @param app
	 *            org.eclipse.e4.ide.application
	 * @param context
	 *            IEclipseContext context
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
	public void execute(@Optional MMenuItem menuItem, EPartService partService, IEclipseContext context,
			EModelService modelService, MWindow window, MApplication app) throws IOException, ParseException,
			SystemException, JDOMException {

		String projectName = menuItem.getLabel();
		TableLastRuns tab = (TableLastRuns) partService.findPart("org.testeditor.ui.part.0").getObject();
		MPart mPart = (MPart) modelService.find("org.testeditor.ui.part.0", app);
		File dir = new File(Platform.getLocation().toFile() + "\\" + projectName + "\\FitNesseRoot\\files\\testResults");
		if (GetDataTableLastRuns.checkFile(dir)) {
			if (!mPart.getLabel()
					.equals(translationService.translate("%dashboard.table.label.lastrun", CONTRIBUTOR_URI) + " "
							+ projectName)) {
				tab.refresh(projectName, modelService, window, app, context);
			}
		} else {
			ErrorMessage error = ContextInjectionFactory.make(ErrorMessage.class, context);
			error.errorProjectEmpty();
		}
	}

}