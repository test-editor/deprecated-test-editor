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
import org.testeditor.core.model.teststructure.TestCase;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.model.teststructure.TestSuite;

/**
 * Service for save test result for running tests.
 * 
 */
public class TestStateProtocolService implements IContextFunction {

	private final Map<TestStructure, TestResult> protocolMap = new HashMap<TestStructure, TestResult>();
	private final Map<String, TestResult> protocolUnLoadedTestStructures = new HashMap<String, TestResult>();
	private final Map<TestProject, Integer> updateProjectMap = new HashMap<TestProject, Integer>();
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

		if (test instanceof TestCase) {
			protocolMap.put(test, testResult);
		} else if (test instanceof TestSuite) {

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
	 *            test to be removed from state storage.
	 */
	public void remove(TestStructure test) {
		if (protocolMap.containsKey(test)) {
			protocolMap.remove(test);
		}
		if (protocolUnLoadedTestStructures.containsKey(test.getFullName())) {
			protocolUnLoadedTestStructures.remove(test.getFullName());
		}
		if (updateProjectMap.containsKey(test)) {
			updateProjectMap.remove(test);
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
			eventBroker.subscribe(TestEditorCoreEventConstants.TESTSTRUCTURE_MODEL_CHANGED_UPDATE_BY_MODIFY,
					getDeletedTestStructureEventHandler());
			eventBroker.subscribe(TestEditorCoreEventConstants.TESTSTRUCTURE_STATE_RESET,
					getResetStateTestStructureEventHandler());
		}
		return this;
	}

	/**
	 * 
	 * @return eventHanlder to handle the reset state event of an element.
	 */
	private EventHandler getResetStateTestStructureEventHandler() {
		return new EventHandler() {

			@Override
			public void handleEvent(Event event) {
				String testStructureName = (String) event.getProperty("org.eclipse.e4.data");
				TestProject toBeRemoved = null;
				for (TestProject tp : updateProjectMap.keySet()) {
					if (tp.getFullName().equals(testStructureName)) {
						toBeRemoved = tp;
					}
				}
				if (toBeRemoved != null) {
					updateProjectMap.remove(toBeRemoved);
					eventBroker.post(TestEditorCoreEventConstants.TESTSTRUCTURE_STATE_UPDATED_BY_TESTNAME,
							toBeRemoved.getFullName());
				}
			}
		};
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

	/**
	 * Gets the number of available updates for a testproject.
	 * 
	 * @param testProject
	 *            check for updates
	 * @return the number of updates.
	 */
	public int getAvailableUpdatesFor(TestProject testProject) {
		if (updateProjectMap.containsKey(testProject)) {
			return updateProjectMap.get(testProject);
		}
		return 0;
	}

	/**
	 * Sets the possible updates for a project.
	 * 
	 * @param testProject
	 *            used as key
	 * @param updatesCount
	 *            number of updates.
	 */
	public void setUpdateCountForProject(TestProject testProject, int updatesCount) {
		if (updatesCount == 0) {
			if (updateProjectMap.containsKey(testProject)) {
				updateProjectMap.remove(testProject);
				fireUpdateCountSateCHangeEvent(testProject);
			}
		} else {
			updateProjectMap.put(testProject, updatesCount);
			fireUpdateCountSateCHangeEvent(testProject);
		}
	}

	/**
	 * fires an event to update the ui with new update change informations.
	 * 
	 * @param testProject
	 *            that has state changes.
	 */
	private void fireUpdateCountSateCHangeEvent(TestProject testProject) {
		if (eventBroker != null) {
			eventBroker.post(TestEditorCoreEventConstants.TESTSTRUCTURE_MODEL_CHANGED_UPDATE_BY_TEAMSHARESTATUS,
					testProject.getName());
		}
	}

}
