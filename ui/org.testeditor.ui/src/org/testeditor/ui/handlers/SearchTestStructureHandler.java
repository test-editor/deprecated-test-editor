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

import javax.inject.Named;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Shell;
import org.testeditor.ui.parts.commons.SearchTestStructureDialog;

/**
 * Handler to Execute a TestStructure Search.
 *
 */
public class SearchTestStructureHandler {

	/**
	 * Executes the Search for TestStructures.
	 * 
	 * @param shell
	 *            active shell used to create the SearchDialog.
	 * @param context
	 *            used to create the search dialog.
	 */
	@Execute
	public void execute(@Named(IServiceConstants.ACTIVE_SHELL) Shell shell, IEclipseContext context) {
		SearchTestStructureDialog dialog = new SearchTestStructureDialog(shell);
		ContextInjectionFactory.inject(dialog, context);
		if (dialog.open() == Dialog.OK && dialog.getSelectedTestStructure() != null) {
			OpenTestStructureHandler structureHandler = ContextInjectionFactory.make(OpenTestStructureHandler.class,
					context);
			structureHandler.execute(dialog.getSelectedTestStructure(), context);
		}
	}
}