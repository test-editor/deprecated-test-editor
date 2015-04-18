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
package org.testeditor.ui.parts.inputparts.actioninput;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.services.internal.events.EventBroker;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testeditor.core.model.action.ActionGroup;
import org.testeditor.core.model.action.TechnicalBindingType;
import org.testeditor.core.model.teststructure.TestCase;
import org.testeditor.core.model.teststructure.TestFlow;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.services.interfaces.ActionGroupService;
import org.testeditor.ui.adapter.TestEditorControllerAdapter;
import org.testeditor.ui.parts.editor.ITestEditorController;
import org.testeditor.ui.parts.editor.view.Adapter.ActionGroupServiceAdapter;
import org.testeditor.ui.utilities.TestEditorTranslationService;

/**
 * Modul Tests for TestEditorActionInputController.
 *
 */
public class TestEditorActionInputControllerTest {

	private IEclipseContext context;
	private TestEditorActionInputController inputController;
	private Shell shell;

	/**
	 * Tests event processing.
	 * 
	 * @throws Exception
	 *             on test failure
	 */
	@Test
	public void testEventActionGroupComboModfied() throws Exception {
		String bindingName = "Allgemein Brwoser";
		context.set(ActionGroupService.class, new ActionGroupServiceAdapter() {
			@Override
			public List<TechnicalBindingType> getTechnicalBindingTypes(TestProject testProject, String name) {
				return new ArrayList<TechnicalBindingType>();
			}
		});
		ContextInjectionFactory.inject(inputController, context);
		inputController.createComboboxActionSelection(bindingName);
	}

	/**
	 * Tests that an IlligalStateException is thrown on empty mask.
	 * 
	 * @throws Exception
	 *             on test success.
	 */
	@Test(expected = IllegalStateException.class)
	public void testEventActionsComboModfiedWithEmptyMaskSelection() throws Exception {
		inputController.createActionLineInputArea("Allgemein Brwoser");
	}

	/**
	 * Tests the work throw creating an ActionInputArea.
	 */
	@Test
	public void testCreateActionLineInputArea() {
		final Set<String> tecBindings = new HashSet<String>();
		context.set(ActionGroupService.class, new ActionGroupServiceAdapter() {
			@Override
			public ActionGroup getActionGroup(TestProject testProject, String name) {
				return new ActionGroup();
			}

			@Override
			public TechnicalBindingType getTechnicalBindingByName(TestProject testProject, String actionGroupName,
					String technicalBindingType) {
				tecBindings.add(technicalBindingType);
				return new TechnicalBindingType();
			}
		});
		ContextInjectionFactory.inject(inputController, context);
		String bindingName = "Allgemein Brwoser";
		inputController.createActionLineInputArea("Allgemein", bindingName);
		assertTrue(tecBindings.contains(bindingName));
	}

	/**
	 * Creates th out with dependency injection.
	 */
	@Before
	public void setUp() {
		context = EclipseContextFactory.create();
		context.set(IEventBroker.class, new EventBroker());
		context.set(ActionGroupService.class, new ActionGroupServiceAdapter());
		context.set(TranslationService.class, new TranslationService() {
		});
		context.set(ITestEditorController.class, new TestEditorControllerAdapter() {
			@Override
			public TestFlow getTestFlow() {
				TestProject tp = new TestProject();
				TestCase testCase = new TestCase();
				tp.addChild(testCase);
				return testCase;
			}
		});
		context.set(TestEditorTranslationService.class,
				ContextInjectionFactory.make(TestEditorTranslationService.class, context));
		shell = new Shell();
		context.set(Composite.class, shell);
		inputController = ContextInjectionFactory.make(TestEditorActionInputController.class, context);
	}

	/**
	 * Cleanup UI handles.
	 */
	@After
	public void tearDown() {
		shell.dispose();
	}

}
