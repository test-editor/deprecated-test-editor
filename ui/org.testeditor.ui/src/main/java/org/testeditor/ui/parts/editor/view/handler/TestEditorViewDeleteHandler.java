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
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.testeditor.ui.constants.TestEditorUIEventConstants;
import org.testeditor.ui.parts.editor.ITestEditorController;

/**
 * 
 * Delete-Handler.
 * 
 * @author llipinski
 */
public class TestEditorViewDeleteHandler extends TestEditorViewHandler {

	/**
	 * @param partService
	 *            {@link EPartService}
	 * @return true, if the cursor is on a editable line or if at last one
	 *         editable line is selected, else false.
	 */
	@CanExecute
	public boolean canExecute(EPartService partService) {
		MPart partOnTop = getPartOnTop(partService);
		return ((ITestEditorController) partOnTop.getObject()).canExecuteDelete();
	}

	/**
	 * delete the line under the cursor or the selected lines.
	 * 
	 * @param eventBroker
	 *            IEventBroker
	 */
	@Execute
	public void execute(IEventBroker eventBroker) {
		eventBroker.send(TestEditorUIEventConstants.EDIT_CONTEXTMENU_DEL, "");
	}
}
