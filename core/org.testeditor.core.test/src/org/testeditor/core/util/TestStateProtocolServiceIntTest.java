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
package org.testeditor.core.util;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.junit.Test;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.testeditor.core.constants.TestEditorCoreEventConstants;
import org.testeditor.core.model.testresult.TestResult;
import org.testeditor.core.model.teststructure.TestCase;
import org.testeditor.core.services.interfaces.ServiceLookUpForTest;

/**
 * Integration Test for TestProtocolService.
 * 
 */
public class TestStateProtocolServiceIntTest {

	/**
	 * Tests that the Service is registered in the Osgi context.
	 * 
	 * @throws Exception
	 *             for test.
	 */
	@Test
	public void testServiceIsRegistered() throws Exception {
		assertNotNull("Service retriving from osgi is not null.",
				ServiceLookUpForTest.getService(TestStateProtocolService.class));
	}

	/**
	 * Tests the Registration of an Event Hanlder in the event broker of the
	 * eclpse context.
	 */
	@Test
	public void testEventBrokerEventHanlderRegistration() {
		TestStateProtocolService protocolService = new TestStateProtocolService();
		IEclipseContext context = EclipseContextFactory.create();
		final Map<String, EventHandler> topics = new HashMap<String, EventHandler>();
		context.set(IEventBroker.class, new IEventBroker() {

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
				topics.put(topic, eventHandler);
				return true;
			}

			@Override
			public boolean subscribe(String topic, String filter, EventHandler eventHandler, boolean headless) {
				return false;
			}

			@Override
			public boolean unsubscribe(EventHandler eventHandler) {
				return false;
			}
		});
		protocolService.compute(context, null);
		EventHandler handler = topics.get(TestEditorCoreEventConstants.TESTSTRUCTURE_MODEL_CHANGED_DELETED);
		assertNotNull("Hanlder should be registered", handler);
		TestCase testCase = new TestCase();
		testCase.setName("TestCase1");
		protocolService.set(testCase, new TestResult());
		assertNotNull(protocolService.get(testCase));
		Map<String, String> properties = new HashMap<String, String>();
		properties.put("org.eclipse.e4.data", testCase.getFullName());
		Event event = new Event(TestEditorCoreEventConstants.TESTSTRUCTURE_MODEL_CHANGED_DELETED, properties);
		handler.handleEvent(event);
		assertNull(protocolService.get(testCase));
	}

}
