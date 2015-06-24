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
package org.testeditor.ui.parts.editor.view;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.internal.events.EventBroker;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.FrameworkUtil;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.model.teststructure.TestCase;
import org.testeditor.core.model.teststructure.TestComponent;
import org.testeditor.core.model.teststructure.TestDescription;
import org.testeditor.core.model.teststructure.TestDescriptionTestCase;
import org.testeditor.core.model.teststructure.TestFlow;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestProjectConfig;
import org.testeditor.core.services.interfaces.ActionGroupService;
import org.testeditor.core.services.interfaces.TestScenarioService;
import org.testeditor.core.services.plugins.TestStructureContentServicePlugIn;
import org.testeditor.ui.adapter.MPartAdapter;
import org.testeditor.ui.adapter.PartServiceAdapter;
import org.testeditor.ui.adapter.TestStructureContentServiceAdapter;
import org.testeditor.ui.parts.editor.view.Adapter.TestEditorControllerMock;
import org.testeditor.ui.utilities.TestEditorTranslationService;

/**
 * test of testEditorController.
 * 
 * 
 */
public class TestEditorControllerTestForPastTestComponents {

	private TestEditorControllerMock testEditorController;
	private MPart mPartAdapter;
	private Shell shell;
	private TestCase testFlowForTest;
	private TestProject testProject;
	private TestDescriptionTestCase testDescToAdd = new TestDescriptionTestCase("This is a new description!");

	/**
	 * initialize for the tests.
	 * 
	 * @return a testDescription
	 */
	private TestDescription initalizeForTest() {
		testFlowForTest = new TestCase();
		testFlowForTest.addTestComponent(new TestDescriptionTestCase("New description one"));
		testFlowForTest.addTestComponent(new TestDescriptionTestCase("New description two"));
		TestDescription testDescription = new TestDescriptionTestCase("New description three");
		testFlowForTest.addTestComponent(testDescription);
		testFlowForTest.addTestComponent(new TestDescriptionTestCase("New description four"));
		testEditorController.setTestStructure(testFlowForTest);
		return testDescription;
	}

	/**
	 * initialize a testFlow and add it to a new TestProject.
	 */
	private void initializeSpecialTestFlowAndProject() {
		initalizeForTest();
		testProject = new TestProject();
		TestProjectConfig config = new TestProjectConfig();
		config.setTestServerID("test_adapter");
		testProject.setTestProjectConfig(config);
		testProject.setName("Project_one");
		testProject.addChild(testFlowForTest);
	}

	/**
	 * test the the pasteStoredTestComponents method.
	 */
	@Test
	public void pasteStoredTestComponentsTest() {
		initializeSpecialTestFlowAndProject();
		int sizeOfTestFlow = testFlowForTest.getSize();
		testEditorController.pasteStoredTestComponents(1, false, getTransferContainer());
		assertEquals(sizeOfTestFlow + 1, testFlowForTest.getSize());
	}

	/**
	 * test the the pasteStoredTestComponents method.
	 */
	@Test
	public void pasteStoredTestComponentsTestPositionNegativ() {
		initializeSpecialTestFlowAndProject();
		int sizeOfTestFlow = testFlowForTest.getSize();
		testEditorController.pasteStoredTestComponents(-2, false, getTransferContainer());
		assertEquals(sizeOfTestFlow, testFlowForTest.getSize());
		List<TestComponent> testComponents = testFlowForTest.getTestComponents();
		for (TestComponent comp : testComponents) {
			assertFalse(testDescToAdd.equals(comp));
		}
	}

	/**
	 * test the the pasteStoredTestComponents method.
	 */
	@Test
	public void pasteStoredTestComponentsTestLineToNegativAndInserBeforeTrue() {
		initializeSpecialTestFlowAndProject();
		int sizeOfTestFlow = testFlowForTest.getSize();
		testEditorController.pasteStoredTestComponents(-1, true, getTransferContainer());
		assertEquals(sizeOfTestFlow, testFlowForTest.getSize());
		List<TestComponent> testComponents = testFlowForTest.getTestComponents();
		for (TestComponent comp : testComponents) {
			assertFalse(testDescToAdd.equals(comp));
		}
	}

	/**
	 * test the pasteStoredTestComponents method. past the testcomponent at the
	 * end.
	 */
	@Test
	public void pasteStoredTestComponentsTestAtTheEnd() {
		initializeSpecialTestFlowAndProject();
		int sizeOfTestFlow = testFlowForTest.getSize();
		testEditorController.pasteStoredTestComponents(sizeOfTestFlow, false, getTransferContainer());
		assertEquals(sizeOfTestFlow + 1, testFlowForTest.getSize());
	}

	/**
	 * setup.
	 */
	@Before
	public void setup() {
		shell = new Shell();
		TestStructureContentServiceAdapter contentServiceAdapter = getSpecialTestStructureContentServiceAdapter();
		FrameworkUtil.getBundle(getClass()).getBundleContext()
				.registerService(TestStructureContentServicePlugIn.class.getName(), contentServiceAdapter, null);
		IEclipseContext context = EclipseContextFactory.getServiceContext(FrameworkUtil.getBundle(
				TestEditorViewKeyHandler.class).getBundleContext());
		context.set(EPartService.class, new PartServiceAdapter());
		context.set(TestEditorTranslationService.class, null);
		context.set(ActionGroupService.class, null);
		context.set(TestScenarioService.class, null);
		context.set(IEventBroker.class, new EventBroker());
		context.set(Composite.class, new Composite(shell, SWT.NONE));
		context.set(Shell.class, shell);
		mPartAdapter = new MPartAdapter();
		context.set(MPart.class, mPartAdapter);
		testEditorController = ContextInjectionFactory.make(TestEditorControllerMock.class, context);
	}

	/**
	 * 
	 * @return a mockup for TestEditorTestFlowTransferContainer.
	 */
	private TestEditorTestFlowTransferContainer getTransferContainer() {
		TestEditorTestFlowTransferContainer testEditorTestFlowDataTransferContainer = new TestEditorTestFlowTransferContainer();
		testEditorTestFlowDataTransferContainer.setStoredTestComponents("Dies sind die TestDaten");
		testEditorTestFlowDataTransferContainer.setTestProjectName("Project_one");
		return testEditorTestFlowDataTransferContainer;
	}

	/**
	 * 
	 * @return a special TestStructureContentServiceAdapter
	 */
	private TestStructureContentServiceAdapter getSpecialTestStructureContentServiceAdapter() {
		return new TestStructureContentServiceAdapter() {
			@Override
			public List<TestComponent> parseFromString(TestFlow testFlow, String storedTestComponents)
					throws SystemException {
				ArrayList<TestComponent> list = new ArrayList<TestComponent>();
				list.add(testDescToAdd);
				return list;
			}
		};
	}
}
