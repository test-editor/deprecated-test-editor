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

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.testeditor.ui.parts.systemconfiguration.SystemConfigurationEditor;

/**
 * Handling remove event on system configuration table.
 */
public class SystemConfigurationRemoveElementHandler implements EventHandler {

	/**
	 * Checks if the handler is enabled (always true for adding).
	 * 
	 * @return true if button is active.
	 */
	@CanExecute
	public boolean canExecute() {
		return true;
	}

	/**
	 * Executing remove on grid table.
	 * 
	 * @param partService
	 * 				EPartService
	 *            
	 */
	@Execute
	public void execute(EPartService partService) {
		MPart activePart = partService.getActivePart();
		if(activePart.getElementId().equals(SystemConfigurationEditor.ID)){
			((SystemConfigurationEditor)activePart.getObject()).removeRow();
		}
	}

	@Override
	public void handleEvent(Event arg0) {
	}

}
