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
package org.testeditor.ui.parts.testhistory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.model.team.TeamShareConfig;
import org.testeditor.core.model.testresult.TestResult;
import org.testeditor.core.model.teststructure.TestCase;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestProjectConfig;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.services.interfaces.TestStructureService;
import org.testeditor.ui.adapter.TestStructureServiceAdapter;
import org.testeditor.ui.utilities.TestEditorTranslationService;

/**
 * 
 * Test for TestHistoryPart.
 *
 */
public class TestHistoryPartTest {

	private TestHistoryPart historyPart;
	private Shell shell;
	private IEclipseContext context;

	/**
	 * Test the check of containing a test structure with a testhistory in this
	 * view.
	 */
	@Test
	public void testContainsTestHistory() {
		assertFalse(historyPart.containsTestHistory());
		TestProject tp = new TestProject();
		TestProjectConfig testProjectConfig = new TestProjectConfig();
		tp.setTestProjectConfig(testProjectConfig);
		testProjectConfig.setTeamShareConfig(new TeamShareConfig() {

			@Override
			public String getId() {
				return null;
			}
		});
		TestCase testCase = new TestCase();
		tp.addChild(testCase);
		historyPart.showTestHistory(testCase);
		assertFalse(historyPart.containsTestHistory());
		final List<TestResult> mockdata = new ArrayList<TestResult>();
		TestResult testResult = new TestResult();
		testResult.setResultDate(new Date());
		mockdata.add(testResult);
		context.set(TestStructureService.class, new TestStructureServiceAdapter() {
			@Override
			public List<TestResult> getTestHistory(TestStructure testStructure) throws SystemException {
				return mockdata;
			}
		});
		ContextInjectionFactory.inject(historyPart, context);
		historyPart.showTestHistory(testCase);
		assertTrue(historyPart.containsTestHistory());
	}

	/**
	 * Test the extraction of a String represantation of the TestResult in the
	 * History table.
	 * 
	 * @throws ParseException
	 *             on test error.
	 */
	@Test
	public void testExtractTestResultString() throws ParseException {
		TestResult testResult = new TestResult();
		testResult.setException(0);
		testResult.setRight(3);
		testResult.setWrong(1);
		SimpleDateFormat sdf = new SimpleDateFormat("dd.mm.yyyy");
		testResult.setResultDate(sdf.parse("12.10.1970"));
		String[] resultSummaryRowFrom = historyPart.getResultSummaryRowFrom(testResult);
		assertEquals(":3; :1; :-1; :0", resultSummaryRowFrom[2]);
	}

	/**
	 * Creates OUT with SWT Widget.
	 */
	@Before
	public void setUp() {
		shell = new Shell();
		context = EclipseContextFactory.create();
		context.set(TestStructureService.class, new TestStructureServiceAdapter() {
			@Override
			public List<TestResult> getTestHistory(TestStructure testStructure) throws SystemException {
				return new ArrayList<TestResult>();
			}
		});
		context.set(TestEditorTranslationService.class, new TestEditorTranslationService() {
			@Override
			public String translate(String key, Object... params) {
				return "";
			}
		});
		context.set(Composite.class, shell);
		historyPart = ContextInjectionFactory.make(TestHistoryPart.class, context);
	}

	/**
	 * Releases SWT handles.
	 */
	@After
	public void tearDown() {
		shell.dispose();
	}

}
