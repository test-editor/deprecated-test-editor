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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.osgi.service.event.EventHandler;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.model.teststructure.TestCase;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.model.teststructure.TestSuite;
import org.testeditor.core.services.interfaces.TestProjectService;
import org.testeditor.core.services.interfaces.TestScenarioService;
import org.testeditor.core.services.interfaces.TestStructureContentService;
import org.testeditor.core.services.interfaces.TestStructureService;
import org.testeditor.core.util.TestProtocolService;
import org.testeditor.ui.adapter.MPartAdapter;
import org.testeditor.ui.adapter.PartServiceAdapter;
import org.testeditor.ui.adapter.TestProjectServiceAdapter;
import org.testeditor.ui.adapter.TestStructureContentServiceAdapter;
import org.testeditor.ui.constants.TestEditorUIEventConstants;
import org.testeditor.ui.mocks.EventBrokerMock;
import org.testeditor.ui.mocks.TestExplorerMock;
import org.testeditor.ui.utilities.TestEditorTranslationService;

/**
 * 
 * Integration Tests for TestSuiteEditor.
 * 
 */
public class TestSuiteEditorTest {

	private Shell shell;
	private TestSuiteEditor out;
	private String lastSavedContent;
	private EventBrokerMock eventBroker;

	/**
	 * Test the created UI.
	 */
	@Test
	public void testCreateComposite() {
		int childsBefore = shell.getChildren().length;
		out.createControls(shell, eventBroker);
		assertTrue("Expecting new childs on the ui.", shell.getChildren().length > childsBefore);
	}

	/**
	 * Test the setting the TestSuite remote.
	 */
	@Test
	public void testSetTestSuite() {
		out.createControls(shell, eventBroker);
		TestSuite testSuite = new TestSuite();
		testSuite.setName("MyTestSuite");
		out.setTestSuite(testSuite);
		assertSame("Expecting the same object", testSuite, out.getTestSuite());
		assertFalse("Expecting a non dorty UI after setting a testsuite.", out.getMPart().isDirty());
		assertEquals("Expecting Testsuite name of the UI Part.", testSuite.getName(), out.getMPart().getLabel());
		assertEquals("Mpart persoited state stores the fullname", testSuite.getFullName(), out.getMPart()
				.getPersistedState().get(TestSuiteEditor.EDITOR_OBJECT_ID_FOR_RESTORE));
	}

	/**
	 * Tests the Save operation of the Editor.
	 */
	@Test
	public void testSave() {
		out.getMPart().setDirty(true);
		TestSuite testSuite = new TestSuite();
		TestCase testCase = new TestCase();
		testSuite.addReferredTestStructure(testCase);
		out.setTestSuite(testSuite);
		out.save();
		assertFalse("Expecting that dirty is false after save.", out.getMPart().isDirty());
		assertNotNull("Content stored", lastSavedContent);
		assertEquals("Expecting TestSuite content is equals to the stored.", testSuite.getSourceCode(),
				lastSavedContent);
	}

	/**
	 * Tests the creation of TestCaseSelectionDialog with the e4 and osgi
	 * context.
	 */
	@Test
	public void testGetTestCaseSelectionDialog() {
		assertNotNull("Expecting the creation of the TestCaseSelectionDialog based on the eclipse context.",
				out.getTestCaseSelectionDialog());
	}

	/**
	 * Tests that no null value is added to the TestSuite by the
	 * SelectionListener.
	 */
	@Test
	public void testAddButtonSelectionListenerOnNullSelection() {
		TestSuiteEditorForTest editorForTest = new TestSuiteEditorForTest(new MPartAdapter(), null);
		ContextInjectionFactory.inject(editorForTest, getEclipseContextForTest());
		editorForTest.createControls(shell, eventBroker);
		SelectionListener listener = editorForTest.getAddButtonSelectionListener();
		TestSuite testSuite = new TestSuite();
		editorForTest.setTestSuite(testSuite);
		listener.widgetSelected(null);
		assertTrue("Expecting an empty selection isn't added to the TestSuite.", testSuite.getReferredTestStrcutures()
				.isEmpty());
	}

	/**
	 * Tests the update of a TestSuite Memory object by the addButton. The
	 * TestSuite should contain a referred TestCase and the Editor state should
	 * be dirty.
	 */
	@Test
	public void testAddButtonSelectionListenerOnValidSelection() {
		TestSuiteEditorForTest editorForTest = new TestSuiteEditorForTest(new MPartAdapter(), new TestCase());
		ContextInjectionFactory.inject(editorForTest, getEclipseContextForTest());
		editorForTest.createControls(shell, eventBroker);
		SelectionListener listener = editorForTest.getAddButtonSelectionListener();
		TestSuite testSuite = new TestSuite();
		editorForTest.setTestSuite(testSuite);
		listener.widgetSelected(null);
		assertFalse("Expecting that there is an TestCase added to the testsuite", testSuite.getReferredTestStrcutures()
				.isEmpty());
		assertTrue("Editor is dirty", editorForTest.getMPart().isDirty());
	}

	/**
	 * Tests the Update Listener of the remove Button.
	 */
	@Test
	public void testGetTabeleSelectionListener() {
		TestSuiteEditorForTest editorForTest = new TestSuiteEditorForTest(new MPartAdapter(), new TestCase());
		ContextInjectionFactory.inject(editorForTest, getEclipseContextForTest());
		editorForTest.createControls(shell, eventBroker);
		SelectionListener listener = editorForTest.getTabeleSelectionListener();
		listener.widgetSelected(null);
		assertFalse("Expecting disabled remove button", editorForTest.getRemoveButton().isEnabled());
	}

	/**
	 * Tests the Update Listener of the remove Button.
	 */
	@Test
	public void testGetRemoveButtonSelectionListener() {
		TestSuite testSuite = new TestSuite();
		TestCase testCase = new TestCase();
		testSuite.addReferredTestStructure(testCase);
		TestSuiteEditorForTest editorForTest = new TestSuiteEditorForTest(new MPartAdapter(), testCase);
		ContextInjectionFactory.inject(editorForTest, getEclipseContextForTest());
		editorForTest.setTestSuite(testSuite);
		editorForTest.createControls(shell, eventBroker);
		SelectionListener listener = editorForTest.getRemoveButtonSelectionListener();
		listener.widgetSelected(null);
		assertTrue("Expecting Testcase is removed.", testSuite.getReferredTestStrcutures().isEmpty());
		assertTrue("Expecting editor is dirty.", editorForTest.getMPart().isDirty());
	}

	/**
	 * Test the Set Focus in the TestExplorer triggered from the
	 * TestSuiteEditor.
	 */
	@Test
	public void testOnFocus() {
		final Map<String, Boolean> monitor = new HashMap<String, Boolean>();
		TestSuiteEditorForTest editorForTest = new TestSuiteEditorForTest(new MPartAdapter(), new TestCase());
		IEclipseContext context = getEclipseContextForTest();
		TestExplorerMock testExplorerMock = new TestExplorerMock();
		IEventBroker specialEventBroker = getSpecialEventBroker();
		testExplorerMock.setEventBroker(specialEventBroker);
		testExplorerMock.shareMonitorMap(monitor);
		// ContextInjectionFactory.inject(editorForTest, context);
		editorForTest.onFocus(shell, specialEventBroker);
		assertTrue("Selection is selected", monitor.get("seen") != null);
	}

	/**
	 * 
	 * Sorting Referred TestCases in TestSuite Editor by name.
	 * 
	 */
	@Test
	public void testSortingOfReferredTestCasesInView() {
		out.createControls(shell, eventBroker);
		TestSuite testSuite = new TestSuite();
		testSuite.setName("MyTestSuite");
		TestStructure testStructure = new TestCase();
		testStructure.setName("Foo");
		testSuite.addReferredTestStructure(testStructure);
		testStructure = new TestCase();
		testStructure.setName("Bar");
		testSuite.addReferredTestStructure(testStructure);
		testStructure = new TestCase();
		testStructure.setName("Zzz");
		testSuite.addReferredTestStructure(testStructure);
		testStructure = new TestCase();
		testStructure.setName("Abc");
		testSuite.addReferredTestStructure(testStructure);
		out.setTestSuite(testSuite);
		TableItem[] items = out.getReferredTestCasesViewer().getTable().getItems();
		assertEquals("Abc", items[0].getText());
		assertEquals("Bar", items[1].getText());
		assertEquals("Foo", items[2].getText());
		assertEquals("Zzz", items[3].getText());
	}

	/**
	 * Creates the OUT and a ui shell.
	 */
	@Before
	public void setUp() {
		shell = new Shell();
		lastSavedContent = null;
		IEclipseContext context = getEclipseContextForTest();
		out = ContextInjectionFactory.make(TestSuiteEditor.class, context);
	}

	/**
	 * 
	 * @return EclipseContext to create the OUT.
	 */
	private IEclipseContext getEclipseContextForTest() {
		IEclipseContext context = EclipseContextFactory.create();
		context.set(MPart.class, new MPartAdapter());
		context.set(EPartService.class, getParServiceMock());
		context.set(Composite.class, shell);
		context.set(Shell.class, shell);
		context.set(TestProjectService.class, new TestProjectServiceAdapter());
		context.set(TestScenarioService.class, null);
		context.set(TestProtocolService.class, null);
		context.set(TestStructureService.class, null);
		eventBroker = new EventBrokerMock();
		context.set(IEventBroker.class, eventBroker);
		context.set(TestStructureContentService.class, new TestStructureContentServiceAdapter() {
			@Override
			public void saveTestStructureData(TestStructure testStructure) throws SystemException {
				lastSavedContent = testStructure.getSourceCode();
			}
		});
		context.set(TestEditorTranslationService.class, new TestEditorTranslationService() {
			@Override
			public String translate(String key, Object... params) {
				return key;
			};
		});
		return context;
	}

	/**
	 * TestSuiteEditor with extensions for Test.
	 * 
	 */
	class TestSuiteEditorForTest extends TestSuiteEditor {

		private TestStructure selectedTestStructure;

		/**
		 * 
		 * @param part
		 *            the UI model
		 * @param selectedTestStructure
		 *            to be used as selection of this editor.
		 */
		@Inject
		public TestSuiteEditorForTest(MPart part, TestStructure selectedTestStructure) {
			super(part);
			this.selectedTestStructure = selectedTestStructure;
		}

		@Override
		protected TestStructureSelectionDialog getTestCaseSelectionDialog() {
			return new TestStructureSelectionDialog(shell) {
				@Override
				public int open() {
					return Dialog.OK;
				}

				@Override
				public IStructuredSelection getSelection() {
					return new IStructuredSelection() {

						@Override
						public boolean isEmpty() {
							return false;
						}

						@Override
						public List toList() {
							ArrayList list = new ArrayList();
							list.add(getFirstElement());
							return list;
						}

						@Override
						public Object[] toArray() {
							return toList().toArray();
						}

						@Override
						public int size() {
							return toList().size();
						}

						@Override
						public Iterator iterator() {
							return toList().iterator();
						}

						@Override
						public Object getFirstElement() {
							return selectedTestStructure;
						}
					};
				}
			};
		}

		@Override
		protected Object[] getSelectedTestStructures() {
			return new Object[] { selectedTestStructure };
		}
	}

	/**
	 * 
	 * @return IEventBroker
	 */
	private IEventBroker getSpecialEventBroker() {
		return new IEventBroker() {

			private EventHandler myEventHandler;

			@Override
			public boolean send(String topic, Object data) {
				if (topic.equals(TestEditorUIEventConstants.ACTIVE_TESTFLOW_EDITOR_CHANGED)) {
					myEventHandler.handleEvent(null);
				}
				return false;
			}

			@Override
			public boolean post(String topic, Object data) {
				return false;
			}

			@Override
			public boolean subscribe(String topic, EventHandler eventHandler) {
				myEventHandler = eventHandler;
				return true;
			}

			@Override
			public boolean subscribe(String topic, String filter, EventHandler eventHandler, boolean headless) {
				return false;
			}

			@Override
			public boolean unsubscribe(EventHandler eventHandler) {
				return false;
			}
		};
	}

	/**
	 * 
	 * @return PartServiceMock for tests.
	 */
	private EPartService getParServiceMock() {
		return new PartServiceAdapter();
	}

	/**
	 * Disposes ui elements and drops os handles.
	 */
	@After
	public void tearDown() {
		shell.dispose();
	}

}
