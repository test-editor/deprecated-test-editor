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
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.core.contexts.ContextFunction;
import org.eclipse.e4.core.contexts.IContextFunction;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.model.testresult.TestResult;
import org.testeditor.core.model.teststructure.TestCompositeStructure;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.services.interfaces.TestStructureService;
import org.testeditor.core.services.plugins.TestStructureServicePlugIn;

/**
 * Dispatcher class of the TestStructureService. It looks up the corresponding
 * plug-in for the test structure to work on.
 *
 */
public class TestStructureServiceDispatcher extends ContextFunction implements TestStructureService, IContextFunction {

	private static final Logger LOGGER = Logger.getLogger(TestStructureServiceDispatcher.class);
	private Map<String, TestStructureServicePlugIn> testStructureServices = new HashMap<String, TestStructureServicePlugIn>();

	/**
	 * 
	 * @param testStructureService
	 *            used in this service
	 * 
	 */
	public void bind(TestStructureServicePlugIn testStructureService) {
		this.testStructureServices.put(testStructureService.getId(), testStructureService);
		LOGGER.info("Bind TestStructureService Plug-In" + testStructureService.getClass().getName());
	}

	/**
	 * 
	 * @param testStructureService
	 *            removed from system
	 */
	public void unBind(TestStructureServicePlugIn testStructureService) {
		this.testStructureServices.remove(testStructureService.getId());
		LOGGER.info("UnBind TestStructureService Plug-In" + testStructureService.getClass().getName());
	}

	@Override
	public void loadChildrenInto(TestCompositeStructure testCompositeStructure) throws SystemException {
		testStructureServices.get(testCompositeStructure.getRootElement().getTestProjectConfig().getTestServerID())
				.loadChildrenInto(testCompositeStructure);
	}

	@Override
	public void create(TestStructure testStructure) throws SystemException {
		testStructureServices.get(testStructure.getRootElement().getTestProjectConfig().getTestServerID()).create(
				testStructure);
	}

	@Override
	public void delete(TestStructure testStructure) throws SystemException {
		testStructureServices.get(testStructure.getRootElement().getTestProjectConfig().getTestServerID()).delete(
				testStructure);
	}

	@Override
	public void rename(TestStructure testStructure, String newName) throws SystemException {
		testStructureServices.get(testStructure.getRootElement().getTestProjectConfig().getTestServerID()).rename(
				testStructure, newName);
	}

	@Override
	public TestResult executeTestStructure(TestStructure testStructure, IProgressMonitor monitor)
			throws SystemException, InterruptedException {
		return testStructureServices.get(testStructure.getRootElement().getTestProjectConfig().getTestServerID())
				.executeTestStructure(testStructure, monitor);
	}

	@Override
	public String getTestExecutionLog(TestStructure testStructure) throws SystemException {
		return testStructureServices.get(testStructure.getRootElement().getTestProjectConfig().getTestServerID())
				.getTestExecutionLog(testStructure);
	}

	@Override
	public boolean hasTestExecutionLog(TestStructure testStructure) throws SystemException {
		return testStructureServices.get(testStructure.getRootElement().getTestProjectConfig().getTestServerID())
				.hasTestExecutionLog(testStructure);
	}

	@Override
	public List<TestResult> getTestHistory(TestStructure testStructure) throws SystemException {
		return testStructureServices.get(testStructure.getRootElement().getTestProjectConfig().getTestServerID())
				.getTestHistory(testStructure);
	}

	@Override
	public void clearTestHistory(TestStructure testStructure) throws SystemException {
		testStructureServices.get(testStructure.getRootElement().getTestProjectConfig().getTestServerID())
				.clearTestHistory(testStructure);
	}

	@Override
	public boolean isReservedName(TestProject testProject, String name) {
		if (testProject != null) {
			return testStructureServices.get(testProject.getTestProjectConfig().getTestServerID()).isReservedName(
					testProject, name);
		}
		return true;
	}

	@Override
	public Runnable getTestProjectLazyLoader(TestCompositeStructure toBeLoadedLazy) {
		if (testStructureServices.get(toBeLoadedLazy.getRootElement().getTestProjectConfig().getTestServerID()) != null) {
			return testStructureServices.get(toBeLoadedLazy.getRootElement().getTestProjectConfig().getTestServerID())
					.getTestProjectLazyLoader(toBeLoadedLazy);
		}
		return null;
	}

	@Override
	public Object compute(IEclipseContext context, String contextKey) {
		Collection<TestStructureServicePlugIn> plugins = testStructureServices.values();
		for (TestStructureServicePlugIn plugin : plugins) {
			if (plugin instanceof IContextFunction) {
				((IContextFunction) plugin).compute(context, contextKey);
			}
		}
		return this;
	}

}
