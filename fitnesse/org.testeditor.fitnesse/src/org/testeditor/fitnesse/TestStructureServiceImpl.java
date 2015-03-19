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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.core.contexts.IContextFunction;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.testeditor.core.constants.TestEditorCoreEventConstants;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.model.team.TeamChangeType;
import org.testeditor.core.model.testresult.TestResult;
import org.testeditor.core.model.teststructure.TestCompositeStructure;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.services.interfaces.TeamShareService;
import org.testeditor.core.services.interfaces.TestStructureService;
import org.testeditor.fitnesse.filesystem.FitnesseFileSystemTestStructureService;
import org.testeditor.fitnesse.filesystem.FitnesseFileSystemUtility;
import org.testeditor.fitnesse.util.FitNesseRestClient;

/**
 * FitNesse implementation of CRUD operation referring to the internal test
 * structure.
 */
public class TestStructureServiceImpl implements TestStructureService, IContextFunction {

	private static final Logger LOGGER = Logger.getLogger(TestStructureServiceImpl.class);
	private Map<String, String> renamedTestStructures = new HashMap<String, String>();
	private IEventBroker eventBroker;
	private Map<String, TeamShareService> teamShareServices = new HashMap<String, TeamShareService>();
	private IEclipseContext context;

	/**
	 * 
	 * @param teamShareService
	 *            to be bind to this service.
	 */
	public void bind(TeamShareService teamShareService) {
		teamShareServices.put(teamShareService.getId(), teamShareService);
		LOGGER.info("Binding TeamShareService Plug-In " + teamShareService.getClass().getName());
	}

	/**
	 * 
	 * @param teamShareService
	 *            to be removed.
	 */
	public void unBind(TeamShareService teamShareService) {
		teamShareServices.remove(teamShareService.getId());
		LOGGER.info("Removing TeamShareService Plug-In " + teamShareService.getClass().getName());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createTestStructure(TestStructure testStructure) throws SystemException {
		new FitnesseFileSystemTestStructureService().createTestStructure(testStructure);
		eventBroker.post(TestEditorCoreEventConstants.TESTSTRUCTURE_MODEL_CHANGED_UPDATE, testStructure.getFullName());

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeTestStructure(TestStructure testStructure) throws SystemException {
		String testStructureFullName = testStructure.getFullName();
		if (testStructure.getParent() instanceof TestCompositeStructure) {
			((TestCompositeStructure) testStructure.getParent()).removeChild(testStructure);
		}
		if (testStructure.getRootElement().getTestProjectConfig().getTeamShareConfig() != null) {
			String id = testStructure.getRootElement().getTestProjectConfig().getTeamShareConfig().getId();
			LOGGER.trace("Looking up for team share service with id: " + id);
			TeamShareService teamShareService = teamShareServices.get(id);
			teamShareService.doDelete(testStructure, context.get(TranslationService.class));
			LOGGER.trace("Used " + teamShareService);
		}
		new FitnesseFileSystemTestStructureService().removeTestStructure(testStructure);
		clearHistory(testStructure);
		if (eventBroker != null) {
			eventBroker.post(TestEditorCoreEventConstants.TESTSTRUCTURE_MODEL_CHANGED_DELETED, testStructureFullName);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void renameTestStructure(TestStructure testStructure, String newName) throws SystemException {
		storeOldNameOnTheFullNewNameAsKey(testStructure, newName);
		clearHistory(testStructure);
		if (testStructure.getRootElement().getTestProjectConfig().getTeamShareConfig() != null) {
			String id = testStructure.getRootElement().getTestProjectConfig().getTeamShareConfig().getId();
			LOGGER.trace("Looking up for team share service with id: " + id);
			TeamShareService teamShareService = teamShareServices.get(id);
			teamShareService.rename(testStructure, newName, context.get(TranslationService.class));
			testStructure.setTeamChangeType(TeamChangeType.MOVED);
		} else {
			new FitnesseFileSystemTestStructureService().renameTestStructure(testStructure, newName);
		}
		if (eventBroker != null) {
			eventBroker.post(TestEditorCoreEventConstants.TESTSTRUCTURE_MODEL_CHANGED_UPDATE,
					testStructure.getFullName());
		}
	}

	/**
	 * Stores the old name of the TestStructure under the key of the new
	 * fullname.
	 * 
	 * @param testStructure
	 *            that should be renamed.
	 * @param newName
	 *            the new name.
	 */
	protected void storeOldNameOnTheFullNewNameAsKey(TestStructure testStructure, String newName) {
		String oldName = testStructure.getName();
		String path = testStructure.getFullName().substring(0, testStructure.getFullName().lastIndexOf(".") + 1);
		renamedTestStructures.put(path + newName, oldName);
	}

	/**
	 * Looks up the TestProjectConfiguration to build the Fitnesse URL.
	 * 
	 * @param testStructure
	 *            to retrive the TestProjectConfiguration
	 * @return the url to the fitnesse server.
	 */
	protected String getFitnesseUrl(TestStructure testStructure) {
		StringBuilder sb = new StringBuilder();
		TestProject tp = testStructure.getRootElement();
		sb.append("http://localhost:").append(tp.getTestProjectConfig().getPort()).append("/");
		return sb.toString();
	}

	@Override
	public TestResult executeTestStructure(TestStructure testStructure, IProgressMonitor monitor)
			throws SystemException, InterruptedException {
		return FitNesseRestClient.execute(testStructure, monitor);
	}

	@Override
	public String getLogData(TestStructure testStructure) throws SystemException {
		return new FitnesseFileSystemTestStructureService().getLogData(testStructure);
	}

	@Override
	public void loadTestStructuresChildrenFor(TestCompositeStructure testCompositeStructure) throws SystemException {
		new FitnesseFileSystemTestStructureService().loadTestStructuresChildrenFor(testCompositeStructure);
	}

	@Override
	public List<TestResult> getTestHistory(TestStructure testStructure) throws SystemException {
		if (!testStructure.isExecutableTestStructure()) {
			return new ArrayList<TestResult>();
		}

		return new FitnesseFileSystemTestStructureService().getTestHistory(testStructure);
	}

	@Override
	public boolean isReservedName(String name) {
		return getSpecialPages().contains(name);
	}

	/**
	 * FitNesse has some reserved Words for special pages. This pages are used
	 * for example as test preparation. See:
	 * http://fitnesse.org/FitNesse.UserGuide.SpecialPages
	 * 
	 * @return a Set of reserved Names in FitNesse.
	 */
	Set<String> getSpecialPages() {
		Set<String> specialPages = new HashSet<String>();
		specialPages.add("PageHeader");
		specialPages.add("PageFooter");
		specialPages.add("SetUp");
		specialPages.add("TearDown");
		specialPages.add("SuiteSetUp");
		specialPages.add("SuiteTearDown");
		specialPages.add("ScenarioLibrary");
		specialPages.add("TemplateLibrary");
		specialPages.add("Suites");
		return specialPages;
	}

	@Override
	public void clearHistory(TestStructure testStructure) throws SystemException {
		new FitnesseFileSystemTestStructureService().clearHistory(testStructure);
		if (eventBroker != null) {
			eventBroker.post(TestEditorCoreEventConstants.TESTSTRUCTURE_HISTORY_DELETED, testStructure.getFullName());
		}
	}

	@Override
	public Runnable getTestProjectLazyLoader(final TestCompositeStructure toBeLoadedLazy) {
		return new FitnesseFileSystemTestStructureService().getTestProjectLazyLoader(toBeLoadedLazy);
	}

	@Override
	public String getId() {
		return "fitnesse_based_1.2";
	}

	/**
	 * For Test purpose only.
	 * 
	 * @return renamedTestStructures.
	 */
	Map<String, String> getRenamedTestStructures() {
		return renamedTestStructures;
	}

	@Override
	public Object compute(IEclipseContext context, String contextKey) {
		if (eventBroker == null) {
			eventBroker = context.get(IEventBroker.class);
			LOGGER.trace("EventBroker registered.");
			this.context = context;
		}
		return this;
	}

	@Override
	public boolean hasLogData(TestStructure testStructure) throws SystemException {
		return FitnesseFileSystemUtility.existsContentTxtInPathOfTestStructureInErrorDirectory(testStructure);
	}

}
