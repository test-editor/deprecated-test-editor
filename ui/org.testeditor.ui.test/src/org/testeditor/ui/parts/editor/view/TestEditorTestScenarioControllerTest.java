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
import static org.junit.Assert.assertTrue;

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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.FrameworkUtil;
import org.testeditor.core.model.action.Action;
import org.testeditor.core.model.action.Argument;
import org.testeditor.core.model.teststructure.TestActionGroup;
import org.testeditor.core.model.teststructure.TestActionGroupTestScenario;
import org.testeditor.core.model.teststructure.TestDescription;
import org.testeditor.core.model.teststructure.TestDescriptionTestCase;
import org.testeditor.core.model.teststructure.TestDescriptionTestScenario;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestScenario;
import org.testeditor.core.services.interfaces.ActionGroupService;
import org.testeditor.core.services.interfaces.TestScenarioService;
import org.testeditor.core.services.interfaces.TestStructureContentService;
import org.testeditor.ui.adapter.MPartAdapter;
import org.testeditor.ui.adapter.PartServiceAdapter;
import org.testeditor.ui.adapter.TestStructureContentServiceAdapter;
import org.testeditor.ui.mocks.TestScenarioServiceMock;
import org.testeditor.ui.parts.editor.view.Adapter.ActionGroupServiceAdapter;
import org.testeditor.ui.parts.editor.view.Adapter.TestEditorTestScenarioControllerMock;
import org.testeditor.ui.utilities.TestEditorTranslationService;

/**
 * test for the TestEditorTestScenarioController.
 * 
 * @author llipinski
 * 
 */
public class TestEditorTestScenarioControllerTest {

	private TestEditorTestScenarioController testEditorController;
	private MPart mPartAdapter;
	private Shell shell;
	private TestScenario testFlowForTest;
	private TestProject testProject;
	private TestDescriptionTestCase descOne = new TestDescriptionTestCase("New description one");
	private TestDescriptionTestCase descTwo = new TestDescriptionTestCase("New description two");
	private TestDescriptionTestCase descThree = new TestDescriptionTestCase("New description three");
	private TestDescriptionTestCase descFour = new TestDescriptionTestCase("New description four");

	/**
	 * test create DescriptionArray.
	 */
	@Test
	public void createDesriptionArrayTest() {
		List<String> list = new ArrayList<String>();
		list.add("first");
		list.add("second");
		list.add("third");
		String fourth = "fourth";
		list.add(fourth);
		List<TestDescription> descriptionsArray = testEditorController.createDescriptionsArray(list);
		assertEquals(new TestDescriptionTestScenario(fourth).getSourceCode(), descriptionsArray.get(3).getSourceCode());
	}

	/**
	 * test isInputValid.
	 */
	@Test
	public void isInputValidTest() {
		assertFalse(testEditorController.isInputValid(""));
		assertFalse(testEditorController.isInputValid("@"));
		assertTrue(testEditorController.isInputValid("@a"));
	}

	/**
	 * runs the method setActionGroup. No explicit test.
	 */
	@Test
	public void setActionGroupTest() {
		initializeSpecialTestFlowAndProject();
		ArrayList<String> inputLineParts = new ArrayList<String>();
		inputLineParts.add("Öffne Seite:");
		inputLineParts.add("http://www.testeditor.org");
		ArrayList<Argument> arguments = new ArrayList<Argument>();
		arguments.add(new Argument(null, "http://www.testeditor.org"));

		testEditorController.setActionGroup("Browser", "navigate to page", inputLineParts, arguments, 0, true);
	}

	/**
	 * test the method scanForScenarioParameters only descriptions.
	 */
	@Test
	public void scanForScenarioParametersOnlydescriptions() {
		initializeSpecialTestFlowAndProject();
		assertFalse(testEditorController.scanForScenarioParameters());

	}

	/**
	 * test the method scanForScenarioParameters one parameter.
	 */
	@Test
	public void scanForScenarioParametersOneParameter() {
		initializeSpecialTestFlowAndProject();
		TestActionGroupTestScenario testActionGroupTestScenario = new TestActionGroupTestScenario();
		Action action = new Action();
		List<Argument> arguments = new ArrayList<Argument>();
		arguments.add(new Argument(null, "@value"));
		action.setArguments(arguments);
		testActionGroupTestScenario.addActionLine(action);
		testFlowForTest.addTestComponent(testActionGroupTestScenario);
		assertTrue(testEditorController.scanForScenarioParameters());
		assertEquals(1, testFlowForTest.getTestParameters().size());
		assertEquals("value", testFlowForTest.getTestParameters().get(0));
		assertTrue(testEditorController.scanForScenarioParameters());
		assertEquals(1, testFlowForTest.getTestParameters().size());
		assertEquals("value", testFlowForTest.getTestParameters().get(0));
	}

	/**
	 * test the method scanForScenarioParameters one parameter.
	 */
	@Test
	public void scanForScenarioParametersNoParameter() {
		initializeSpecialTestFlowAndProject();
		TestActionGroupTestScenario testActionGroupTestScenario = new TestActionGroupTestScenario();
		Action action = new Action();
		List<Argument> arguments = new ArrayList<Argument>();
		arguments.add(new Argument(null, "value"));
		action.setArguments(arguments);
		testActionGroupTestScenario.addActionLine(action);
		testFlowForTest.addTestComponent(testActionGroupTestScenario);
		assertFalse(testEditorController.scanForScenarioParameters());
		assertEquals(0, testFlowForTest.getTestParameters().size());
		assertFalse(testEditorController.scanForScenarioParameters());
		assertEquals(0, testFlowForTest.getTestParameters().size());
	}

	/**
	 * test the setUnparsedActionGroup method.
	 */
	@Test
	public void setUnparsedActionGroupTest() {
		initializeSpecialTestFlowAndProject();
		List<String> inputTexts = new ArrayList<String>();
		String input = "|123|abc|";
		inputTexts.add(input);
		testEditorController.setUnparsedActionGroup("Browser", "Navigation", inputTexts, 2);
		String[] splits = ((TestActionGroup) testFlowForTest.getTestComponents().get(2)).getActionLines().get(0)
				.getTexts().get(0).split("\n");
		String compareString = splits[1];
		assertEquals(input, compareString);

	}

	/**
	 * setup.
	 */
	@Before
	public void setup() {
		shell = new Shell();
		IEclipseContext context = EclipseContextFactory.getServiceContext(FrameworkUtil.getBundle(
				TestEditorViewKeyHandler.class).getBundleContext());
		context.set(EPartService.class, new PartServiceAdapter());
		context.set(TestEditorTranslationService.class, null);
		context.set(TestStructureContentService.class, new TestStructureContentServiceAdapter());
		context.set(ActionGroupService.class, getActionGroupServiceMock());
		context.set(TestScenarioService.class, new TestScenarioServiceMock());
		context.set(IEventBroker.class, new EventBroker());
		context.set(Composite.class, new Composite(shell, SWT.NONE));
		context.set(Shell.class, shell);
		mPartAdapter = new MPartAdapter();
		context.set(MPart.class, mPartAdapter);
		testEditorController = ContextInjectionFactory.make(TestEditorTestScenarioControllerMock.class, context);
	}

	/**
	 * 
	 * @return a mockup ActionGroupService.
	 */
	private ActionGroupService getActionGroupServiceMock() {
		return new ActionGroupServiceAdapter();
	}

	/**
	 * initialize for the tests.
	 * 
	 * @return a testDescription
	 */
	private TestDescription initalizeForTest() {
		testFlowForTest = new TestScenario();
		testFlowForTest.addTestComponent(descOne);
		testFlowForTest.addTestComponent(descTwo);
		testFlowForTest.addTestComponent(descThree);
		testFlowForTest.addTestComponent(descFour);
		testEditorController.setTestStructure(testFlowForTest);
		return descThree;
	}

	/**
	 * initialize a testFlow and add it to a new TestProject.
	 */
	private void initializeSpecialTestFlowAndProject() {
		initalizeForTest();
		testProject = new TestProject();
		testProject.setName("Project_one");
		testProject.addChild(testFlowForTest);
	}

	/**
	 * Destroying Shell.
	 */
	@After
	public void tearDown() {
		shell.dispose();
	}
}
