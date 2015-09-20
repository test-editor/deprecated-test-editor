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
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.testeditor.ui.parts.editor.ITestEditorController;

/**
 * 
 * parent-class for the TestEditorViewCopyHandler and
 * TestEditorCutHandler-class.
 * 
 * @author llipinski
 */
public class TestEditorViewCCHandler extends TestEditorViewHandler {
	/**
	 * @param partService
	 *            {@link EPartService}
	 * @return true, if their is at least one TestComponent selected.
	 */
	@CanExecute
	public boolean canExecute(EPartService partService) {
		MPart partOnTop = getPartOnTop(partService);
		return ((ITestEditorController) partOnTop.getObject()).canExecuteCutCopy();
	}
}
