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

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IContextFunction;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.osgi.framework.FrameworkUtil;
import org.testeditor.core.constants.TestEditorCoreEventConstants;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.model.testresult.TestResult;
import org.testeditor.core.model.teststructure.TestComponent;
import org.testeditor.core.model.teststructure.TestCompositeStructure;
import org.testeditor.core.model.teststructure.TestFlow;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestScenario;
import org.testeditor.core.model.teststructure.TestScenarioParameterTable;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.model.teststructure.TestSuite;
import org.testeditor.core.services.interfaces.TeamShareService;
import org.testeditor.core.services.interfaces.TestExecutionEnvironmentService;
import org.testeditor.core.services.interfaces.TestScenarioService;
import org.testeditor.core.services.interfaces.TestStructureContentService;
import org.testeditor.core.services.plugins.TeamShareServicePlugIn;
import org.testeditor.core.services.plugins.TestStructureServicePlugIn;
import org.testeditor.fitnesse.filesystem.FitnesseFileSystemTestStructureService;
import org.testeditor.fitnesse.filesystem.FitnesseFileSystemUtility;
import org.testeditor.fitnesse.util.FitNesseRestClient;
import org.testeditor.fitnesse.util.FitNesseWikiParser;

/**
 * FitNesse implementation of CRUD operation referring to the internal test
 * structure.
 */
public class TestStructureServiceImpl implements TestStructureServicePlugIn, IContextFunction {

	private static final Logger logger = Logger.getLogger(TestStructureServiceImpl.class);
	private IEventBroker eventBroker;
	private Map<String, TeamShareService> teamShareServices = new HashMap<String, TeamShareService>();
	private IEclipseContext context;
	private TestExecutionEnvironmentService environmentService;
	private TestStructureContentService testStructureContentService;
	private TestScenarioService testScenarioService;

	/**
	 * 
	 * @param testStructureContentService
	 *            to be bind to this service.
	 */
	public void bind(TestStructureContentService testStructureContentService) {
		this.testStructureContentService = testStructureContentService;
		logger.info("binding testStructureContentService " + testStructureContentService.getClass().getName());
	}

	/**
	 * 
	 * @param testStructureContentService
	 *            to be unbind to this service.
	 */
	public void unbind(TestStructureContentService testStructureContentService) {
		this.testStructureContentService = null;
		logger.info("unbinding testStructureContentService " + testStructureContentService.getClass().getName());
	}

	/**
	 * 
	 * @param testScenarioService
	 *            to be bind to this service.
	 */
	public void bind(TestScenarioService testScenarioService) {
		this.testScenarioService = testScenarioService;
		logger.info("binding testScenarioService " + testScenarioService.getClass().getName());
	}

	/**
	 * 
	 * @param testScenarioService
	 *            to be unbind to this service.
	 */
	public void unbind(TestScenarioService testScenarioService) {
		this.testScenarioService = null;
		logger.info("unbinding testScenarioService " + testScenarioService.getClass().getName());
	}

	/**
	 * 
	 * @param teamShareService
	 *            to be bind to this service.
	 */
	public void bind(TeamShareServicePlugIn teamShareService) {
		teamShareServices.put(teamShareService.getId(), teamShareService);
		logger.info("Binding TeamShareService Plug-In " + teamShareService.getClass().getName());
	}

	/**
	 * 
	 * @param teamShareService
	 *            to be removed.
	 */
	public void unBind(TeamShareServicePlugIn teamShareService) {
		teamShareServices.remove(teamShareService.getId());
		logger.info("Removing TeamShareService Plug-In " + teamShareService.getClass().getName());
	}

	/**
	 * 
	 * @param environmentService
	 *            to be bind to this service.
	 */
	public void bind(TestExecutionEnvironmentService environmentService) {
		this.environmentService = environmentService;
		logger.info("Binding Plug-In " + environmentService.getClass().getName());
	}

	/**
	 * 
	 * @param environmentService
	 *            to be removed.
	 */
	public void unbind(TestExecutionEnvironmentService environmentService) {
		this.environmentService = null;
		logger.info("Removing Plug-In " + environmentService.getClass().getName());
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
		if (testStructure.getRootElement().getTestProjectConfig().getTeamShareConfig() != null) {
			String id = testStructure.getRootElement().getTestProjectConfig().getTeamShareConfig().getId();
			logger.trace("Looking up for team share service with id: " + id);
			TeamShareService teamShareService = teamShareServices.get(id);
			teamShareService.delete(testStructure, context.get(TranslationService.class));
			logger.trace("Used " + teamShareService);
		}
		if (testStructure.getParent() instanceof TestCompositeStructure) {
			((TestCompositeStructure) testStructure.getParent()).removeChild(testStructure);
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
	public List<String> rename(TestStructure testStructure, String newName) throws SystemException {

		List<String> changedItems = null;
		if (testStructure instanceof TestScenario) {
			changedItems = renameScenario((TestScenario) testStructure, newName);
		} else if (testStructure instanceof TestSuite) {
			changedItems = renameTestSuite((TestSuite) testStructure, newName);
		} else {
			changedItems = renameTestCase(testStructure, newName);
		}
		if (eventBroker != null) {
			eventBroker.post(TestEditorCoreEventConstants.TESTSTRUCTURE_MODEL_CHANGED_UPDATE_BY_MODIFY,
					testStructure.getFullName());
		}
		return changedItems;
	}

	/**
	 * Renames a suite. All childrens of the node are updated.
	 * 
	 * @param testSuite
	 *            -the suite to be renamed.
	 * @param newName
	 *            -the new name of the suite
	 * @return - the list of all changed items
	 * @throws SystemException
	 *             - any excpetion during renaming.
	 */
	private List<String> renameTestSuite(TestSuite testSuite, String newName) throws SystemException {
		renameFiles(testSuite, newName);
		setName(testSuite, newName);
		return updateAllChildren(testSuite);
	}

	/**
	 * Update the URL for all children of a testsuite.
	 * 
	 * @param testSuite
	 *            - the testsuite to replace the children
	 * @throws SystemException
	 *             - the exception
	 * @return - list of changed items
	 */
	private List<String> updateAllChildren(TestSuite testSuite) throws SystemException {
		List<String> changedItems = new ArrayList<String>();
		for (TestStructure testStructure : testSuite.getAllTestChildren()) {
			Path path = Paths.get(FitnesseFileSystemUtility.getPathToTestStructureDirectory(testStructure));
			try {
				testStructure.setUrl(path.toUri().toURL());
				changedItems.add(testStructure.getFullName());
			} catch (MalformedURLException e) {
				throw new SystemException(e.getMessage(), e);
			}
			if (testStructure instanceof TestSuite) {
				changedItems.addAll(updateAllChildren((TestSuite) testStructure));
			}
		}
		return changedItems;
	}

	/**
	 * Renames a testcase. This is just a change of filenames.
	 * 
	 * @param testStructure
	 *            - the testStructure to be renamed
	 * @param newName
	 *            - the newName of the tescase
	 * @return - list of changed items
	 * @throws SystemException
	 *             - any excpetion during renaming
	 */
	private List<String> renameTestCase(TestStructure testStructure, String newName) throws SystemException {
		clearTestHistory(testStructure);
		renameFiles(testStructure, newName);
		testStructure.setName(newName);
		Path path = Paths.get(FitnesseFileSystemUtility.getPathToTestStructureDirectory(testStructure));
		try {
			testStructure.setUrl(path.toUri().toURL());
		} catch (MalformedURLException e) {
			throw new SystemException(e.getMessage(), e);
		}
		return new ArrayList<String>();

	}

	/**
	 * updates the calls of a scenario: Changes the name and the path of the
	 * scenario and changes all testcases and scenarios that make use of the
	 * scenario.
	 * 
	 * @param testProject
	 *            - the testProject
	 * @param newName
	 *            - the new name of the scenario
	 * @param newPath
	 *            - the new path of the scenario
	 * @param oldPath
	 *            - the old path of the scenario
	 * @param usages
	 *            - a list of testcases and scenarios that use the scenario
	 * @throws SystemException
	 *             - any exception during renaming
	 * @return the list of updated testFlows.
	 */
	private List<TestFlow> updateScenarioCalls(TestProject testProject, String newName, String newPath, String oldPath,
			List<String> usages) throws SystemException {

		List<TestFlow> changedFlows = new ArrayList<TestFlow>();

		// Search and update all calls to the scenario to be renamed.
		for (String usage : usages) {
			TestFlow testFlow = (TestFlow) testProject.getTestChildByFullName(usage);
			FitNesseWikiParser fitNesseWikiParser = createNewWikiParser();

			String oldUsageCode = testStructureContentService.getTestStructureAsSourceText(testFlow);
			String oldInclude = "!include <" + oldPath;
			LinkedList<TestComponent> testComponents = fitNesseWikiParser.parse((TestFlow) testFlow,
					oldUsageCode.toString());
			for (TestComponent testComponent : testComponents) {
				if (testComponent instanceof TestScenarioParameterTable) {
					TestScenarioParameterTable paramCall = (TestScenarioParameterTable) testComponent;
					if (paramCall.getInclude().equals(oldInclude)) {
						paramCall.setInclude(newPath);
						paramCall.setTitle(TestScenarioParameterTable.splitOnCapitalsWithWhiteSpaces(newName, 1));
					}
				}
			}
			testFlow.setTestComponents(testComponents);
			changedFlows.add(testFlow);
		}
		return changedFlows;
	}

	@Override
	public List<String> move(TestStructure testStructure, TestCompositeStructure newParent) throws SystemException {
		List<String> changedItems = new ArrayList<String>();
		testStructureContentService.refreshTestCaseComponents(testStructure);

		List<TestFlow> changedFlows = null;

		if (testStructure instanceof TestScenario) {
			List<String> usages = testScenarioService.getUsedOfTestSceneario((TestScenario) testStructure);

			changedFlows = updateScenarioCalls(testStructure.getRootElement(), testStructure.getName(),
					newParent.getFullName() + "." + testStructure.getName(), testStructure.getFullName(), usages);
		} else {
			clearTestHistory(testStructure);
		}
		if (testStructure.getRootElement().getTestProjectConfig().getTeamShareConfig() != null) {
			moveStructureWithTeamShare(testStructure, newParent);
		} else {
			new FitnesseFileSystemTestStructureService().move(testStructure, newParent);
		}
		if (changedFlows != null) {
			updateChangedFlows(changedFlows);
			for (TestFlow testFlow : changedFlows) {
				changedItems.add(testFlow.getFullName());
			}
		}

		if (eventBroker != null) {
			eventBroker.send(TestEditorCoreEventConstants.TESTSTRUCTURE_MODEL_CHANGED_UPDATE_BY_MODIFY,
					testStructure.getRootElement().getName());
		}
		return changedItems;
	}

	/**
	 * renames a scenario.
	 * 
	 * @param scenario
	 *            -the scenario to be renamed
	 * @param newName
	 *            - the new name of the scenario
	 * @throws SystemException
	 *             - any exception during execution.
	 */
	private List<String> renameScenario(TestScenario scenario, String newName) throws SystemException {
		List<String> usages = testScenarioService.getUsedOfTestSceneario(scenario);

		TestProject testProject = scenario.getRootElement();

		String oldName = scenario.getFullName();
		// execute the rename of the scenario itself.

		List<TestFlow> changedFlows = updateScenarioCalls(testProject, newName,
				scenario.getParent().getFullName() + "." + newName, oldName, usages);

		renameFiles(scenario, newName);
		setName(scenario, newName);
		testStructureContentService.refreshTestCaseComponents(scenario);
		testStructureContentService.saveTestStructureData(scenario);
		updateChangedFlows(changedFlows);
		List<String> changedItems = new ArrayList<String>();
		for (TestFlow flow : changedFlows) {
			changedItems.add(flow.getFullName());
		}
		return changedItems;
	}

	/**
	 * Sets the name of the testcase. It is used to set the name and the path in
	 * one place.
	 * 
	 * @param testStructure
	 *            - the structure to be renamed
	 * @param newName
	 *            - the new name
	 * @throws SystemException
	 *             - any exception setting the name.
	 */
	private void setName(TestStructure testStructure, String newName) throws SystemException {
		testStructure.setName(newName);
		Path path = Paths.get(FitnesseFileSystemUtility.getPathToTestStructureDirectory(testStructure));
		try {
			testStructure.setUrl(path.toUri().toURL());
		} catch (MalformedURLException e) {
			throw new SystemException(e.getMessage(), e);
		}
	}

	/**
	 * Rename the files of the of the teststructure. This has to be done in two
	 * steps.
	 * <ol>
	 * <li/>Change the name of the files.
	 * <li/>Change the name of the teststructure.
	 * </ol>
	 * 
	 * @param testStructure
	 *            - the teststructure to be renamed.
	 * @param newName
	 *            - the new name of the teststructure
	 * @throws SystemException
	 *             - any exception during renaming.
	 */
	private void renameFiles(TestStructure testStructure, String newName) throws SystemException {
		if (testStructure.getRootElement().getTestProjectConfig().getTeamShareConfig() != null) {
			String id = testStructure.getRootElement().getTestProjectConfig().getTeamShareConfig().getId();
			logger.trace("Looking up for team share service with id: " + id);
			TeamShareService teamShareService = teamShareServices.get(id);
			teamShareService.rename(testStructure, newName, context.get(TranslationService.class));
		} else {
			new FitnesseFileSystemTestStructureService().rename(testStructure, newName);
		}
	}

	@Override
	public TestResult executeTestStructure(TestStructure testStructure, IProgressMonitor monitor)
			throws SystemException, InterruptedException {
		TestResult result = new TestResult();
		if (testStructure.getRootElement().getTestProjectConfig().usesTestAgent()
				&& !System.getProperties().keySet().contains("execInVagrant")) {
			result = executeInVagrant(testStructure, monitor);
		} else {
			result = FitNesseRestClient.execute(testStructure, monitor);
		}
		boolean isTestSystemExecuted = result.getRight() > 0 | result.getWrong() > 0 | result.getException() > 0;
		if (isTestSystemExecuted) {
			return result;
		} else {
			return new TestResult();
		}
	}

	/**
	 * Executes the test in an vagrant environment.
	 * 
	 * @param testStructure
	 *            to be executed
	 * @param monitor
	 *            used to report progress.
	 * @return test result of the test execution
	 * @throws SystemException
	 *             on failure
	 * @throws InterruptedException
	 *             on user interrupt
	 */
	private TestResult executeInVagrant(TestStructure testStructure, IProgressMonitor monitor)
			throws SystemException, InterruptedException {
		int workToDo = 3;
		if (testStructure instanceof TestSuite) {
			workToDo = workToDo + ((TestSuite) testStructure).getAllTestChildrensAndReferedTestcases().size();
		}
		monitor.beginTask("Starting test execution environment...", workToDo);
		logger.info("Start test execution environment");
		try {
			environmentService.setUpEnvironment(testStructure.getRootElement(), monitor);
			monitor.worked(1);
			TestResult result = environmentService.executeTests(testStructure, monitor);
			monitor.worked(1);
			return result;
		} catch (IOException e) {
			logger.error(
					"Error executing testenvironment: "
							+ testStructure.getRootElement().getTestProjectConfig().getTestEnvironmentConfiguration(),
					e);
			throw new SystemException("Error executing testenvironment", e);
		}
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

	@Override
	public Object compute(IEclipseContext context, String contextKey) {
		if (eventBroker == null) {
			eventBroker = context.get(IEventBroker.class);
			logger.trace("EventBroker registered.");
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

	@Override
	public void pauseTest(TestStructure testStructure) throws SystemException {
		FitNesseRestClient.pauseTest(testStructure);
	}

	@Override
	public void resumeTest(TestStructure testStructure) throws SystemException {
		FitNesseRestClient.resumeTest(testStructure);
	}

	@Override
	public void stepwiseTest(TestStructure testStructure) throws SystemException {
		FitNesseRestClient.stepwiseTest(testStructure);
	}

	/**
	 * creates a wikiParser with the EclipseContextFactory.
	 * 
	 * @return a FitNesseWikiParser
	 */
	private FitNesseWikiParser createNewWikiParser() {
		IEclipseContext context = EclipseContextFactory
				.getServiceContext(FrameworkUtil.getBundle(getClass()).getBundleContext());
		FitNesseWikiParser fitNesseWikiParser = ContextInjectionFactory.make(FitNesseWikiParser.class, context);
		return fitNesseWikiParser;
	}

	/**
	 * Saves a list of testFlows to the disk.
	 * 
	 * @param changedFlows
	 *            - the list of changed testFlows
	 * @throws SystemException
	 *             - any exception during renaming
	 */
	private void updateChangedFlows(List<TestFlow> changedFlows) throws SystemException {
		for (TestFlow testFlow : changedFlows) {
			testStructureContentService.saveTestStructureData(testFlow);
			if (eventBroker != null) {
				eventBroker.post(TestEditorCoreEventConstants.TESTSTRUCTURE_MODEL_CHANGED_UPDATE_BY_MODIFY,
						testFlow.getFullName());
			}

		}
	}

	/**
	 * moves a testcase or a scenario that is under version control.
	 * 
	 * @param testStructure
	 *            - the teststructure to be moved
	 * @param newParent
	 *            - the new parent
	 * @throws SystemException
	 *             - any exception during renaming
	 */
	private void moveStructureWithTeamShare(TestStructure testStructure, TestCompositeStructure newParent)
			throws SystemException {
		String id = testStructure.getRootElement().getTestProjectConfig().getTeamShareConfig().getId();
		logger.trace("Looking up for team share service with id: " + id);
		TeamShareService teamShareService = teamShareServices.get(id);
		delete(testStructure);
		newParent.addChild(testStructure);
		create(testStructure);
		teamShareService.addChild(testStructure, null);
	}

}
