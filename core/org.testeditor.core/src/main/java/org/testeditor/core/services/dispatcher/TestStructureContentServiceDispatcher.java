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

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.contexts.ContextFunction;
import org.eclipse.e4.core.contexts.IContextFunction;
import org.eclipse.e4.core.contexts.IEclipseContext;
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
public class TestStructureContentServiceDispatcher extends ContextFunction implements TestStructureContentService,
		IContextFunction {

	private static final Logger LOGGER = Logger.getLogger(TestStructureContentServiceDispatcher.class);
	private Map<String, TestStructureContentServicePlugIn> testStructureContentServices = new HashMap<String, TestStructureContentServicePlugIn>();

	@Override
	public void refreshTestCaseComponents(TestStructure testStructure) throws SystemException, TestCycleDetectException {
		testStructureContentServices.get(testStructure.getRootElement().getTestProjectConfig().getTestServerID())
				.refreshTestCaseComponents(testStructure);
	}

	@Override
	public void saveTestStructureData(TestStructure testStructure) throws SystemException {
		testStructureContentServices.get(testStructure.getRootElement().getTestProjectConfig().getTestServerID())
				.saveTestStructureData(testStructure);
	}

	@Override
	public void reparseChangedTestFlow(TestFlow testFlow) throws SystemException {
		testStructureContentServices.get(testFlow.getRootElement().getTestProjectConfig().getTestServerID())
				.reparseChangedTestFlow(testFlow);
	}

	@Override
	public List<TestComponent> parseFromString(TestFlow testFlow, String storedTestComponents) throws SystemException {
		return testStructureContentServices.get(testFlow.getRootElement().getTestProjectConfig().getTestServerID())
				.parseFromString(testFlow, storedTestComponents);
	}

	@Override
	public String getTestStructureAsSourceText(TestStructure testStructure) throws SystemException {
		return testStructureContentServices
				.get(testStructure.getRootElement().getTestProjectConfig().getTestServerID())
				.getTestStructureAsSourceText(testStructure);
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
	 * @param testStructureContentService
	 *            removed from system
	 */
	public void unBind(TestStructureContentServicePlugIn testStructureContentService) {
		this.testStructureContentServices.remove(testStructureContentService.getId());
		LOGGER.info("UnBind TestStructureContentService Plug-In" + testStructureContentService.getClass().getName());
	}

	@Override
	public Object compute(IEclipseContext context, String contextKey) {
		Collection<TestStructureContentServicePlugIn> plugins = testStructureContentServices.values();
		for (TestStructureContentServicePlugIn plugin : plugins) {
			if (plugin instanceof IContextFunction) {
				((IContextFunction) plugin).compute(context, contextKey);
			}
		}
		return this;
	}
}
