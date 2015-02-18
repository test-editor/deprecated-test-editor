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

import org.eclipse.e4.core.services.events.IEventBroker;
import org.osgi.service.event.EventHandler;

/**
 * 
 * Mock of the IEventBroker for testing.
 * 
 */
public class EventBrokerMock implements IEventBroker {

	@Override
	public boolean send(String topic, Object data) {
		return false;
	}

	@Override
	public boolean post(String topic, Object data) {
		return false;
	}

	@Override
	public boolean subscribe(String topic, EventHandler eventHandler) {
		return false;
	}

	@Override
	public boolean subscribe(String topic, String filter, EventHandler eventHandler, boolean headless) {
		return false;
	}

	@Override
	public boolean unsubscribe(EventHandler eventHandler) {
		return false;
	}

}
