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
package org.testeditor.ui.parts.commons;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.services.internal.events.EventBroker;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.FrameworkUtil;
import org.testeditor.core.model.testresult.TestResult;
import org.testeditor.core.model.teststructure.BrokenTestStructure;
import org.testeditor.core.model.teststructure.TestCase;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestProjectConfig;
import org.testeditor.core.model.teststructure.TestSuite;
import org.testeditor.core.services.interfaces.ServiceLookUpForTest;
import org.testeditor.core.services.interfaces.TestProjectService;
import org.testeditor.core.util.TestStateProtocolService;
import org.testeditor.ui.constants.IconConstants;
import org.testeditor.ui.parts.commons.tree.TestStructureTree;
import org.testeditor.ui.parts.commons.tree.TestStructureTreeLabelProvider;

/**
 * 
 * Test the LabelProvider of TestStructureTree.
 * 
 */
public class TestStructureTreeLabelProviderTest {

	private TestStructureTreeLabelProvider labelProvider;

	/**
	 * Setup a new LabelProvider as OUT.
	 */
	@Before
	public void setUp() {
		IEclipseContext context = EclipseContextFactory.getServiceContext(FrameworkUtil.getBundle(
				TestStructureTree.class).getBundleContext());
		context.set(Logger.class, null);
		labelProvider = ContextInjectionFactory.make(TestStructureTreeLabelProvider.class, context);
	}

	/**
	 * Check the correct Image for the Root Element in the Tree.
	 * 
	 * @throws Exception
	 *             from Test.
	 */
	@Test
	public void testGetImageForRootElement() throws Exception {
		TestProject testProject = new TestProject();
		testProject.setTestProjectConfig(new TestProjectConfig());
		assertSame(labelProvider.getImage(testProject), IconConstants.ICON_PROJECT);
	}

	/**
	 * Check the correct Image for a Testsuite in the Tree.
	 * 
	 * @throws Exception
	 *             from Test.
	 */
	@Test
	public void testGetImageForTestSuite() throws Exception {
		TestSuite ts = new TestSuite();
		new TestSuite().addChild(ts);
		assertSame(labelProvider.getImage(ts), IconConstants.ICON_TESTSUITE);
	}

	/**
	 * Test the TestResult Icons.
	 * 
	 * @throws Exception
	 *             for Test
	 */
	@Test
	public void testGetImageForTestCase() throws Exception {
		assertSame(labelProvider.getImage(new TestCase()), IconConstants.ICON_TESTCASE);
		TestCase testCase = new TestCase();
		TestResult testResult = new TestResult();
		ServiceLookUpForTest.getService(TestStateProtocolService.class).set(testCase, testResult);
		testResult.setWrong(0);
		testResult.setException(0);
		testResult.setIgnored(0);
		assertSame(labelProvider.getImage(testCase), IconConstants.ICON_TESTCASE_SUCCESSED);
		testResult.setWrong(1);
		assertSame(labelProvider.getImage(testCase), IconConstants.ICON_TESTCASE_FAILED);
		assertSame(labelProvider.getImage(new BrokenTestStructure()), IconConstants.ICON_UNPARSED_LINE);
	}

	/**
	 * Tests the labeling of the teststructure depending on the fullname switch.
	 */
	@Test
	public void testToStringByContext() {
		TestProject tp = new TestProject();
		TestSuite ts = new TestSuite();
		tp.setName("TestProject");
		tp.addChild(ts);
		TestCase tc = new TestCase();
		ts.setName("MySuite");
		tc.setName("TheTestCase");
		ts.addChild(tc);
		assertEquals("TheTestCase", labelProvider.getText(tc));
		labelProvider.setShowFullName(true);
		assertEquals("TestProject.MySuite.TheTestCase", labelProvider.getText(tc));
		labelProvider.setShowFullName(false);
		assertEquals("TheTestCase", labelProvider.getText(tc));
	}

	/**
	 * Tests the label of a testcase with and without incoming changes.
	 */
	@Test
	public void testStrinInformationAboutIncommingTeamChanges() {
		IEclipseContext context = EclipseContextFactory.create();
		context.set(TestProjectService.class, null);
		final TestProject tpWithChanges = new TestProject();
		tpWithChanges.setName("ChangedTp");
		context.set(TestStateProtocolService.class, new TestStateProtocolService() {
			@Override
			public int getAvailableUpdatesFor(TestProject testProject) {
				if (testProject == tpWithChanges) {
					return 3;
				}
				return 0;
			}
		});
		context.set(IEventBroker.class, new EventBroker());
		TestStructureTreeLabelProvider provider = ContextInjectionFactory.make(TestStructureTreeLabelProvider.class,
				context);
		TestProject testProject = new TestProject();
		testProject.setName("MyPrj");
		assertEquals("MyPrj", provider.getText(testProject));
		assertEquals("ChangedTp ↓ 3", provider.getText(tpWithChanges));
	}

}
