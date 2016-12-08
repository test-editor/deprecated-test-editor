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
package org.testeditor.ui.parts.editor.view.handler;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.testeditor.ui.parts.editor.ITestEditorController;

/**
 * 
 * The Testdata import-handler for Excel or csv files.
 * 
 * @author llipinski
 */
public class TestEditorViewImportHandler extends TestEditorViewHandler {
	/**
	 * @param partService
	 *            EPartService
	 * @return true, if a table is selected, else false.
	 */
	@CanExecute
	public boolean canExecute(EPartService partService) {
		MPart partOnTop = getPartOnTop(partService);
		return partOnTop != null && ((ITestEditorController) partOnTop.getObject()).isTestDataTableSelected();
	}

	/**
	 * executes the import-operation.
	 * 
	 * @param partService
	 *            EPartService
	 * 
	 */
	@Execute
	public void execute(EPartService partService) {
		((ITestEditorController) getPartOnTop(partService).getObject()).executeFileImportToTable();
	}

}
