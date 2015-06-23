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
package org.testeditor.core.services.dispatcher;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.exceptions.TestCycleDetectException;
import org.testeditor.core.model.teststructure.TestComponent;
import org.testeditor.core.model.teststructure.TestFlow;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.services.interfaces.TestStructureContentService;
import org.testeditor.core.services.plugins.TestStructureContentServicePlugIn;

/**
 * Dispatcher to look up the right plug-on of TestStructureContentService.
 *
 */
public class TestStructureContentServiceDispatcher implements TestStructureContentService {

	private static final Logger LOGGER = Logger.getLogger(TestStructureContentServiceDispatcher.class);
	private Map<String, TestStructureContentServicePlugIn> testStructureContentServices = new HashMap<String, TestStructureContentServicePlugIn>();

	@Override
	public void refreshTestCaseComponents(TestStructure testStructure) throws SystemException, TestCycleDetectException {
		// TODO Auto-generated method stub

	}

	@Override
	public void saveTestStructureData(TestStructure testStructure) throws SystemException {
		// TODO Auto-generated method stub

	}

	@Override
	public void reparseChangedTestFlow(TestFlow testFlow) throws SystemException {
		// TODO Auto-generated method stub

	}

	@Override
	public List<TestComponent> parseFromString(TestFlow testFlow, String storedTestComponents) throws SystemException {
		return testStructureContentServices.get(testFlow.getRootElement().getTestProjectConfig().getTestServerID())
				.parseFromString(testFlow, storedTestComponents);
	}

	@Override
	public String getTestStructureAsSourceText(TestStructure testStructure) throws SystemException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 
	 * @param testStructureContentService
	 *            used in this service
	 * 
	 */
	public void bind(TestStructureContentServicePlugIn testStructureContentService) {
		this.testStructureContentServices.put(testStructureContentService.getId(), testStructureContentService);
		LOGGER.info("Bind TestStructureContentService Plug-In" + testStructureContentService.getClass().getName());
	}

	/**
	 * 
	 * @param testStructureService
	 *            removed from system
	 */
	public void unBind(TestStructureContentServicePlugIn testStructureContentService) {
		this.testStructureContentServices.remove(testStructureContentService.getId());
		LOGGER.info("UnBind TestStructureContentService Plug-In" + testStructureContentService.getClass().getName());
	}

}
