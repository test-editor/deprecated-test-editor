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
package org.testeditor.ui.mocks;

import java.util.Map;

import javax.inject.Inject;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.ui.constants.TestEditorUIEventConstants;
import org.testeditor.ui.parts.testExplorer.TestExplorer;

/**
 * special TestExplorer for the junitTest.
 * 
 */
public class TestExplorerMock extends TestExplorer implements EventHandler {

	@Inject
	private IEventBroker eventBroker;
	private Map<String, Boolean> monitor;

	@Override
	public void handleEvent(Event arg0) {
		monitor.put("seen", true);
	}

	@Override
	public void setSelectionOn(TestStructure testStructure) {
		monitor.put("seen", true);
	}

	/**
	 * shares the MonitorMap.
	 * 
	 * @param monitor
	 *            the monitorMap
	 */
	public void shareMonitorMap(Map<String, Boolean> monitor) {
		eventBroker.subscribe(TestEditorUIEventConstants.ACTIVE_TESTFLOW_EDITOR_CHANGED, this);
		this.monitor = monitor;
	}

	/**
	 * sets the eventBroker.
	 * 
	 * @param specialEventBroker
	 *            IEventBroker
	 */
	public void setEventBroker(IEventBroker specialEventBroker) {
		eventBroker = specialEventBroker;

	}
}
