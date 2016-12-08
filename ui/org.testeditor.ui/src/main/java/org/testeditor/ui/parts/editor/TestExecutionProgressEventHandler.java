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
package org.testeditor.ui.parts.editor;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.testeditor.core.services.interfaces.TechnicalBindingsDSLMappingService;

/**
 * 
 * Event Handler to Handle Messages from the EventBroker. The Topic for this
 * Handler are TestExecutionProgress Messages.
 * 
 */
public class TestExecutionProgressEventHandler implements EventHandler {

	@Inject
	private TechnicalBindingsDSLMappingService tecBinDSLMappingService;

	private static final Logger LOGGER = Logger.getLogger(TestExecutionProgressEventHandler.class);

	@Override
	public void handleEvent(Event event) {
		String progressLine = (String) event.getProperty("org.eclipse.e4.data");
		String technicalBindingStatement = extractTechnicalBindingStatementFromLine(progressLine);
		String dslOfTestFlow = tecBinDSLMappingService.mapTechnicalBindingToTestDSL(technicalBindingStatement);
		if (dslOfTestFlow != null) {
			LOGGER.info(dslOfTestFlow);
		}
	}

	/**
	 * 
	 * @param progressLine
	 *            with the technical Binding
	 * @return the Technical Bindign execution String.
	 */
	protected String extractTechnicalBindingStatementFromLine(String progressLine) {
		String result = progressLine.substring(progressLine.indexOf("TestEditorLoggingInteraction  Methode :") + 40,
				progressLine.length());
		return result;
	}

}
