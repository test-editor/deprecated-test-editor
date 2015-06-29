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
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IContextFunction;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.testeditor.core.constants.TestEditorCoreEventConstants;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.exceptions.TestCycleDetectException;
import org.testeditor.core.model.teststructure.TestComponent;
import org.testeditor.core.model.teststructure.TestFlow;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.services.plugins.TestStructureContentServicePlugIn;
import org.testeditor.fitnesse.filesystem.FitnesseFileSystemTestStructureContentService;
import org.testeditor.fitnesse.util.FitNesseWikiParser;

/**
 * FitNesse implementation regarding the read and write service of a test flow.
 * A test flow could be a simple test case or a test scenario (this data is used
 * in the middle view of the Test-Editor).
 */
public class TestStructureContentServiceImpl implements TestStructureContentServicePlugIn, IContextFunction {

	private static final Logger LOGGER = Logger.getLogger(TestStructureContentServiceImpl.class);

	private List<TestFlow> seenflows;

	private boolean cycleFound;

	private IEventBroker eventBroker;

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void refreshTestCaseComponents(TestStructure testStructure) throws SystemException, TestCycleDetectException {
		seenflows = new ArrayList<TestFlow>();
		cycleFound = false;
		new FitnesseFileSystemTestStructureContentService().refreshTestCaseComponents(testStructure);
		if (cycleFound) {
			List<String> seenFlowNames = new ArrayList<String>();
			for (TestFlow testFlow : seenflows) {
				seenFlowNames.add(testFlow.getFullName());
			}
			throw new TestCycleDetectException(seenFlowNames);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void saveTestStructureData(TestStructure testStructure) throws SystemException {
		new FitnesseFileSystemTestStructureContentService().saveTestStructureData(testStructure);
		if (eventBroker != null) {
			eventBroker.post(TestEditorCoreEventConstants.TESTSTRUCTURE_MODEL_CHANGED_UPDATE_BY_MODIFY,
					testStructure.getFullName());
			eventBroker.send(TestEditorCoreEventConstants.TESTSTRUCTURE_STATE_CNAGED, testStructure.getFullName());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void reparseChangedTestFlow(TestFlow testFlow) throws SystemException {
		testFlow.setTestComponents(createNewWikiParser().parse(testFlow, testFlow.getSourceCode()));
	}

	@Override
	public List<TestComponent> parseFromString(TestFlow testFlow, String storedTestComponents) throws SystemException {
		if (seenflows == null) {
			seenflows = new ArrayList<TestFlow>();
		}
		if (seenflows.contains(testFlow)) {
			cycleFound = true;
		}
		seenflows.add(testFlow);
		return createNewWikiParser().parse(testFlow, storedTestComponents);
	}

	/**
	 * creates a wikiParser with the EclipseContextFactory.
	 * 
	 * @return a FitNesseWikiParser
	 */
	protected FitNesseWikiParser createNewWikiParser() {
		IEclipseContext context = EclipseContextFactory.getServiceContext(FrameworkUtil.getBundle(getClass())
				.getBundleContext());
		FitNesseWikiParser fitNesseWikiParser = ContextInjectionFactory.make(FitNesseWikiParser.class, context);
		return fitNesseWikiParser;
	}

	@Override
	public String getId() {
		return "fitnesse_based_1.2";
	}

	@Override
	public String getTestStructureAsSourceText(TestStructure testStructure) throws SystemException {
		return new FitnesseFileSystemTestStructureContentService().getTestStructureAsSourceText(testStructure);
	}

	@Override
	public Object compute(IEclipseContext context, String contextKey) {
		if (eventBroker == null) {
			eventBroker = context.get(IEventBroker.class);
			eventBroker.subscribe(TestEditorCoreEventConstants.TESTSTRUCTURE_MODEL_CHANGED_RELOADED,
					new EventHandler() {

						@Override
						public void handleEvent(Event arg0) {
							seenflows = new ArrayList<TestFlow>();
							LOGGER.trace("Data reload event: dropping loaded TestFlows.");
						}
					});
		}
		return this;
	}

	/**
	 * 
	 * @return the collection of already loaded TestFlows in this session.
	 */
	protected List<TestFlow> getSeenTestFlows() {
		return seenflows;
	}

}
