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
package org.testeditor.fitnesse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.junit.Before;
import org.junit.Test;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.testeditor.core.model.teststructure.TestCase;
import org.testeditor.core.model.teststructure.TestComponent;
import org.testeditor.core.model.teststructure.TestFlow;

/**
 * Integration Tests the test structure service.
 */
public class TestStructureContentServiceImplTest {

	private TestFlow firstTestCase;

	/**
	 * initialization before the tests.
	 */
	@Before
	public void initializeTestProjekt() {
		firstTestCase = (TestFlow) TestProjectDataFactory.createTestProjectForFitnesseTests().getTestChildren().get(0);
	}

	/**
	 * Tests the Init Process of the seen TestFlows.
	 */
	@Test
	public void testInitSeenTestsFlows() {
		TestStructureContentServiceImpl service = new TestStructureContentServiceImpl();
		assertNull("Expecting not init list.", new TestStructureContentServiceImpl().getSeenTestFlows());
		IEclipseContext context = EclipseContextFactory.create();
		context.set(IEventBroker.class, getEventBrokerMock());
		service.compute(context, "");
		assertNotNull("Expecting init list reciving service from osgi container.", service.getSeenTestFlows());
	}

	/**
	 * Tests the storage of parsed testflows.
	 * 
	 * @throws Exception
	 *             Parsing failure.
	 */
	@Test
	public void testParsingOfStringToTestComponent() throws Exception {
		TestStructureContentServiceImpl service = new TestStructureContentServiceImpl();
		IEclipseContext context = EclipseContextFactory.create();
		IEventBroker brokerMock = getEventBrokerMock();
		context.set(IEventBroker.class, brokerMock);
		service.compute(context, "");
		TestFlow testFlow = new TestCase();

		testFlow.setName("maFlow");
		List<TestComponent> testComponentList = service.parseFromString(firstTestCase,
				"# Maske: Browser\n-!|script|\n|starte Browser|firefox|\n");
		List<TestComponent> testComponentList2 = service.parseFromString(firstTestCase,
				"# Maske: Browser\n-!|script|\n|starte Browser|firefox|\n");
		assertEquals(testComponentList.get(0).getSourceCode(), testComponentList2.get(0).getSourceCode());
		List<TestComponent> testComponentList3 = service.parseFromString(firstTestCase,
				"# Maske: Browser\n-!|script|\n|starte Browser|firefox|\n");
		assertEquals(testComponentList.get(0).getSourceCode(), testComponentList3.get(0).getSourceCode());
	}

	/**
	 * Tests the storage of parsed testflows.
	 * 
	 * @throws Exception
	 *             Parsing failure.
	 */
	@Test
	public void testOfSeenFlowsInParseProcess() throws Exception {
		TestStructureContentServiceImpl service = new TestStructureContentServiceImpl();
		IEclipseContext context = EclipseContextFactory.create();
		IEventBroker brokerMock = getEventBrokerMock();
		context.set(IEventBroker.class, brokerMock);
		service.compute(context, "");
		TestFlow testFlow = new TestCase();
		testFlow.setName("maFlow");
		service.parseFromString(testFlow, "|name|lastname|");
		assertTrue(service.getSeenTestFlows().contains(testFlow));
		assertEquals(1, service.getSeenTestFlows().size());
		service.parseFromString(testFlow, "|name|lastname|");
		assertEquals(2, service.getSeenTestFlows().size());
		service.parseFromString(new TestCase(), "|name|lastname|");
		assertEquals(3, service.getSeenTestFlows().size());
		brokerMock.send("", null);
		assertTrue(service.getSeenTestFlows().isEmpty());
	}

	/**
	 * 
	 * @return IEventBroker Mock for test.
	 */
	private IEventBroker getEventBrokerMock() {
		return new IEventBroker() {

			private List<EventHandler> handlers = new ArrayList<EventHandler>();

			@Override
			public boolean unsubscribe(EventHandler eventHandler) {
				return false;
			}

			@Override
			public boolean subscribe(String topic, String filter, EventHandler eventHandler, boolean headless) {
				return false;
			}

			@Override
			public boolean subscribe(String topic, EventHandler eventHandler) {
				handlers.add(eventHandler);
				eventHandler.handleEvent(new Event("foo", new HashMap<String, Object>()));
				return false;
			}

			@Override
			public boolean send(String topic, Object data) {
				for (EventHandler eventHandler : handlers) {
					eventHandler.handleEvent(null);
				}
				return false;
			}

			@Override
			public boolean post(String topic, Object data) {
				return false;
			}
		};
	}
}
