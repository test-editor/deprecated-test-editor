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
 * the paste-handler.
 * 
 * @author llipinski
 */
public class TestEditorViewPasteHandler extends TestEditorViewHandler {
	/**
	 * @param partService
	 *            EPartService
	 * @return true, if their are one or more testcomponents on the clipboard
	 *         from the same project.
	 */
	@CanExecute
	public boolean canExecute(EPartService partService) {
		MPart partOnTop = getPartOnTop(partService);
		return ((ITestEditorController) partOnTop.getObject()).canExecutePasteTestFlow();
	}

	/**
	 * executes the paste-operation.
	 * 
	 * @param eventBroker
	 *            IEventBroker
	 */
	@Execute
	public void execute(IEventBroker eventBroker) {
		eventBroker.send(TestEditorUIEventConstants.EDIT_CONTEXTMENU_CNTRL_V, "");
	}
}
