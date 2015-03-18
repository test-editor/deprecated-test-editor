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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.e4.core.contexts.IContextFunction;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.testeditor.core.constants.TestEditorCoreEventConstants;
import org.testeditor.core.model.testresult.TestResult;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.model.teststructure.TestSuite;
import org.testeditor.core.model.teststructure.TestType;

/**
 * Service for save test result for running tests.
 * 
 */
public class TestProtocolService implements IContextFunction {

	private final Map<TestStructure, TestResult> protocolMap = new HashMap<TestStructure, TestResult>();
	private final Map<String, TestResult> protocolUnLoadedTestStructures = new HashMap<String, TestResult>();
	private IEventBroker eventBroker;

	/**
	 * 
	 * 
	 * @param test
	 *            run test
	 * @param testResult
	 *            test result
	 */
	public void set(TestStructure test, TestResult testResult) {

		TestType testType = TestType.valueOf(test.getPageType().toUpperCase());

		if (testType == TestType.TEST) {
			protocolMap.put(test, testResult);
		} else if (testType == TestType.SUITE) {

			// suite counts
			protocolMap.put(test, testResult);

			// iterate test counts
			TestSuite suite = (TestSuite) test;

			// this map stores the fullname of teststructure as key and the
			// teststructure objects as value
			// suite1.test1 = testructureObj1
			Map<String, TestStructure> mapForSearch = new HashMap<String, TestStructure>();

			// create a temp map to search in it
			Set<TestStructure> testChildren = suite.getAllTestChildrensAndReferedTestcases();
			for (TestStructure testStructure : testChildren) {
				mapForSearch.put(testStructure.getFullName(), testStructure);
			}

			// and now iterate the result list and find by fullname in the
			// mapFORSEARCH
			// the concrete teststructure object
			List<TestResult> testResultChildren = testResult.getChildren();
			for (TestResult testChild : testResultChildren) {
				if (mapForSearch.containsKey(testChild.getFullName())) {
					protocolMap.put(mapForSearch.get(testChild.getFullName()), testChild);
				} else {
					protocolUnLoadedTestStructures.put(testChild.getFullName(), testChild);
				}
			}

		}

	}

	/**
	 * Remove TestResult from Protocol for init tree node with default icon.
	 * 
	 * @param test
	 *            run test
	 */
	public void remove(TestStructure test) {
		if (protocolMap.containsKey(test)) {
			protocolMap.remove(test);
		}
		if (protocolUnLoadedTestStructures.containsKey(test.getFullName())) {
			protocolUnLoadedTestStructures.remove(test.getFullName());
		}
	}

	/**
	 * Looksup the TestStructure in the TestProtocol. First it searches for the
	 * TestStructúre. In the case it is executed, but not loaded before, it well
	 * be looked for the Fullname.
	 * 
	 * @param test
	 *            the TestStructure to be checked for last execution state.
	 * @return TestResult as the result of the last test execution.
	 */
	public TestResult get(TestStructure test) {
		TestResult testResult = protocolMap.get(test);
		if (testResult == null) {
			testResult = protocolUnLoadedTestStructures.get(test.getFullName());
		}
		return testResult;
	}

	@Override
	public Object compute(IEclipseContext context, String contextKey) {
		if (eventBroker == null) {
			eventBroker = context.get(IEventBroker.class);
			eventBroker.subscribe(TestEditorCoreEventConstants.TESTSTRUCTURE_MODEL_CHANGED_DELETED,
					getDeletedTestStructureEventHandler());
		}
		return this;
	}

	/**
	 * 
	 * @return eventHanlder to handle the remove of an element.
	 */
	protected EventHandler getDeletedTestStructureEventHandler() {
		return new EventHandler() {

			@Override
			public void handleEvent(Event event) {
				String testStructureName = (String) event.getProperty("org.eclipse.e4.data");
				TestStructure toBeRemoved = null;
				for (TestStructure ts : protocolMap.keySet()) {
					if (ts.getFullName().startsWith(testStructureName)) {
						toBeRemoved = ts;
					}
				}
				if (toBeRemoved != null) {
					remove(toBeRemoved);
				}
			}
		};
	}
}
