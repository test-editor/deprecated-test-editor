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
package org.testeditor.ui.analyzer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.FrameworkUtil;
import org.testeditor.core.model.action.Action;
import org.testeditor.core.model.action.UnparsedActionLine;
import org.testeditor.core.model.teststructure.TestActionGroupTestCase;
import org.testeditor.core.model.teststructure.TestCase;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.services.interfaces.TestStructureContentService;
import org.testeditor.ui.analyzer.errormodel.ErrorContainer;

/**
 * 
 * IntegrationTests for ValidadateTestFlowRunnable.
 * 
 */
public class ValidadateTestFlowRunnableIntTest {

	private ValidadateTestFlowRunnable runnable;

	/**
	 * Tests the creation of the runnable over the eclipse Context.
	 */
	@Test
	public void testCreateOverEclipseContext() {
		assertNotNull("Expecting object is created", runnable);
	}

	/**
	 * Tests the Validation of a collection of TestStructures.
	 * 
	 * @throws Exception
	 *             on Test error.
	 */
	@Test
	public void testGetValidationResults() throws Exception {
		List<TestStructure> testflows = new ArrayList<TestStructure>();
		TestCase tc = new TestCase();
		tc.setName("TC1");
		TestActionGroupTestCase group = new TestActionGroupTestCase();
		group.addActionLine(new Action());
		group.addActionLine(new Action());
		tc.addTestComponent(group);
		testflows.add(tc);
		runnable.setTestFlowsToBeValidated(testflows);
		runnable.run(new NullProgressMonitor());
		assertEquals("Expecting 1 Error", 0, runnable.getValidationResult().size());
		TestCase tc2 = new TestCase();
		tc2.setName("TC2");
		group = new TestActionGroupTestCase();
		group.addActionLine(new UnparsedActionLine("MyLine"));
		group.addActionLine(new Action());
		tc2.addTestComponent(group);
		testflows.add(tc2);
		runnable.setTestFlowsToBeValidated(testflows);
		runnable.run(new NullProgressMonitor());
		assertEquals("Expecting 1 Error", 1, runnable.getValidationResult().size());
		ErrorContainer container = runnable.getValidationResult().iterator().next();
		assertEquals("Error in TestCase 2 expected", tc2, container.getTestFlow());
		assertEquals("Error in TestCase 2 expected", tc2, container.getErrorList().get(0).getTestFlow());
		assertEquals("MyLine", container.getErrorList().get(0).toString());
	}

	/**
	 * Init the object under tests.
	 */
	@Before
	public void setUp() {
		IEclipseContext context = EclipseContextFactory.getServiceContext(FrameworkUtil.getBundle(getClass())
				.getBundleContext());
		context.set(TranslationService.class, new TranslationService() {
		});
		context.set(TestStructureContentService.class, null);
		runnable = ContextInjectionFactory.make(ValidadateTestFlowRunnable.class, context);
	}
}
