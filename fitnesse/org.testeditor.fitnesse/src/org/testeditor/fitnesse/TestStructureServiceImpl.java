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
import org.testeditor.core.model.testresult.TestResult;
import org.testeditor.core.model.teststructure.TestCompositeStructure;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.services.interfaces.TeamShareService;
import org.testeditor.core.services.plugins.TeamShareServicePlugIn;
import org.testeditor.core.services.plugins.TestStructureServicePlugIn;
import org.testeditor.fitnesse.filesystem.FitnesseFileSystemTestStructureService;
import org.testeditor.fitnesse.util.FitNesseRestClient;

/**
 * FitNesse implementation of CRUD operation referring to the internal test
 * structure.
 */
public class TestStructureServiceImpl implements TestStructureServicePlugIn, IContextFunction {

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
	public void bind(TeamShareServicePlugIn teamShareService) {
		teamShareServices.put(teamShareService.getId(), teamShareService);
		LOGGER.info("Binding TeamShareService Plug-In " + teamShareService.getClass().getName());
	}

	/**
	 * 
	 * @param teamShareService
	 *            to be removed.
	 */
	public void unBind(TeamShareServicePlugIn teamShareService) {
		teamShareServices.remove(teamShareService.getId());
		LOGGER.info("Removing TeamShareService Plug-In " + teamShareService.getClass().getName());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void create(TestStructure testStructure) throws SystemException {
		new FitnesseFileSystemTestStructureService().create(testStructure);
		eventBroker.post(TestEditorCoreEventConstants.TESTSTRUCTURE_MODEL_CHANGED_UPDATE_BY_ADD,
				testStructure.getFullName());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void delete(TestStructure testStructure) throws SystemException {
		String testStructureFullName = testStructure.getFullName();
		if (testStructure.getParent() instanceof TestCompositeStructure) {
			((TestCompositeStructure) testStructure.getParent()).removeChild(testStructure);
		}
		if (testStructure.getRootElement().getTestProjectConfig().getTeamShareConfig() != null) {
			String id = testStructure.getRootElement().getTestProjectConfig().getTeamShareConfig().getId();
			LOGGER.trace("Looking up for team share service with id: " + id);
			TeamShareService teamShareService = teamShareServices.get(id);
			teamShareService.delete(testStructure, context.get(TranslationService.class));
			LOGGER.trace("Used " + teamShareService);
		}
		new FitnesseFileSystemTestStructureService().delete(testStructure);
		clearTestHistory(testStructure);
		if (eventBroker != null) {
			eventBroker.post(TestEditorCoreEventConstants.TESTSTRUCTURE_MODEL_CHANGED_DELETED, testStructureFullName);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void rename(TestStructure testStructure, String newName) throws SystemException {
		storeOldNameOnTheFullNewNameAsKey(testStructure, newName);
		clearTestHistory(testStructure);
		if (testStructure.getRootElement().getTestProjectConfig().getTeamShareConfig() != null) {
			String id = testStructure.getRootElement().getTestProjectConfig().getTeamShareConfig().getId();
			LOGGER.trace("Looking up for team share service with id: " + id);
			TeamShareService teamShareService = teamShareServices.get(id);
			teamShareService.rename(testStructure, newName, context.get(TranslationService.class));
		} else {
			new FitnesseFileSystemTestStructureService().rename(testStructure, newName);
		}
		if (eventBroker != null) {
			eventBroker.post(TestEditorCoreEventConstants.TESTSTRUCTURE_MODEL_CHANGED_UPDATE_BY_MODIFY,
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

	@Override
	public TestResult executeTestStructure(TestStructure testStructure, IProgressMonitor monitor)
			throws SystemException, InterruptedException {
		return FitNesseRestClient.execute(testStructure, monitor);
	}

	@Override
	public String getTestExecutionLog(TestStructure testStructure) throws SystemException {
		return new FitnesseFileSystemTestStructureService().getTestExecutionLog(testStructure);
	}

	@Override
	public void loadChildrenInto(TestCompositeStructure testCompositeStructure) throws SystemException {
		new FitnesseFileSystemTestStructureService().loadChildrenInto(testCompositeStructure);
	}

	@Override
	public List<TestResult> getTestHistory(TestStructure testStructure) throws SystemException {
		if (!testStructure.isExecutableTestStructure()) {
			return new ArrayList<TestResult>();
		}

		return new FitnesseFileSystemTestStructureService().getTestHistory(testStructure);
	}

	@Override
	public boolean isReservedName(TestProject testProject, String name) {
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
	public void clearTestHistory(TestStructure testStructure) throws SystemException {
		new FitnesseFileSystemTestStructureService().clearTestHistory(testStructure);
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
	public boolean hasTestExecutionLog(TestStructure testStructure) throws SystemException {
		return new FitnesseFileSystemTestStructureService().hasTestExecutionLog(testStructure);
	}

	@Override
	public String lookUpTestStructureFullNameMatchedToPath(TestProject testProject, String path) {
		return new FitnesseFileSystemTestStructureService().lookUpTestStructureFullNameMatchedToPath(testProject, path);
	}

}
