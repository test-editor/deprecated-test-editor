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

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.ToolItem;

/**
 * @author alebedev
 * 
 *         Dropdown toolbar item disabled bug.
 */
public class MenuToolbarItemHandler {
	/**
	 * @return true
	 */
	@CanExecute
	public boolean canExecute() {
		return true;
	}

	/**
	 * A workaround can be custom handler, and corresponding command:.
	 * 
	 * @param application
	 *            org.eclipse.e4.ide.application
	 * @param modelService
	 *            to find item
	 * @param itemid
	 *            item id
	 */
	@Execute
	public void execute(final MApplication application, final EModelService modelService,
			@Named("com.at.dropdowntoolbaritem.itemid") final String itemid) {
		MUIElement element = modelService.find(itemid, application);
		if (element != null) {
			ToolItem toolItem = (ToolItem) element.getWidget();
			Event event = new Event();
			event.type = SWT.Selection;
			event.detail = SWT.ARROW;
			toolItem.notifyListeners(SWT.Selection, event);
		}
	}
}