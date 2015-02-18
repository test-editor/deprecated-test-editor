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
package org.testeditor.ui.parts.testsuite;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.List;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testeditor.core.model.teststructure.TestCase;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestScenario;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.services.interfaces.TestProjectService;
import org.testeditor.ui.parts.commons.tree.TestStructureTree;
import org.testeditor.ui.utilities.TestEditorTranslationService;

/**
 * 
 * IntegrationTests for TestStructureSelectionDialog.
 * 
 */
public class TestStructureSelectionDialogTest {

	private Shell shell;

	/**
	 * Tests the Usage of the selected TestStructure.
	 */
	@Test
	public void testGetSelectedTestStructure() {
		TestCase tc = new TestCase();
		tc.setName("FooBar");
		TestStructureSelectionDialog out = getOUTWith(tc);
		out.createContents(shell);
		out.okPressed();
		assertEquals("Expecting FooBar as TestCase", "FooBar",
				((TestStructure) out.getSelection().getFirstElement()).getName());
	}

	/**
	 * Ok Button should be enabled if the selection in the Tree is a TestCase.
	 */
	@Test
	public void testOkIsEnabledOnTestCases() {
		TestCase tc = new TestCase();
		tc.setName("FooBar");
		TestStructureSelectionDialog out = getOUTWith(tc);
		out.createContents(shell);
		out.getTreeSelectionListener().widgetSelected(null);
		assertTrue("Expecting enabled ok button on TestCase selection.", out.isOk());
	}

	/**
	 * Ok Button is disabled on all other elements than a TestCase.
	 */
	@Test
	public void testOkIsDisabledOnOtherthanTestCases() {
		TestScenario ts = new TestScenario();
		ts.setName("TestScenrario");
		TestStructureSelectionDialog out = getOUTWith(ts);
		out.createContents(shell);
		out.getTreeSelectionListener().widgetSelected(null);
		assertFalse("Expecting disabled ok button on TestScenrario selection.", out.isOk());
		TestProject tp = new TestProject();
		tp.setName("MyTestProject");
		out = getOUTWith(tp);
		out.createContents(shell);
		out.getTreeSelectionListener().widgetSelected(null);
		assertFalse("Expecting disabled ok button on TestScenrario selection.", out.isOk());
	}

	/**
	 * Creates the OUT with a dummy selection.
	 * 
	 * @param testStructure
	 *            to be returned for test purpose.
	 * @return OUT
	 */
	private TestStructureSelectionDialog getOUTWith(final TestStructure testStructure) {
		TestStructureSelectionDialog dialog = new TestStructureSelectionDialog(shell) {
			@Override
			protected TestStructureTree createTestStructureTree() {
				return new TestStructureTree() {
					@Override
					public IStructuredSelection getSelection() {
						return new IStructuredSelection() {

							@Override
							public boolean isEmpty() {
								return false;
							}

							@Override
							public List toList() {
								return null;
							}

							@Override
							public Object[] toArray() {
								return null;
							}

							@Override
							public int size() {
								return 1;
							}

							@Override
							public Iterator iterator() {
								return null;
							}

							@Override
							public Object getFirstElement() {
								return testStructure;
							}
						};
					}

					@Override
					public TestStructure getSelectedTestStrucuture() {
						return testStructure;
					}

				};
			}
		};
		IEclipseContext context = EclipseContextFactory.create();
		context.set(TestEditorTranslationService.class, new TestEditorTranslationService() {
			@Override
			public String translate(String key, Object... params) {
				return key;
			}
		});
		context.set(TestProjectService.class, null);
		ContextInjectionFactory.inject(dialog, context);
		return dialog;
	}

	/**
	 * Setup UI Elements to be used in the Tests..
	 */
	@Before
	public void setUp() {
		shell = new Shell();
	}

	/**
	 * Disposes ui and os handles.
	 */
	@After
	public void tearDown() {
		shell.dispose();
	}

}
