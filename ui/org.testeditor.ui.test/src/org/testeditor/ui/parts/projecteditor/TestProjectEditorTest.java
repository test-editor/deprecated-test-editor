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
package org.testeditor.ui.parts.projecteditor;

import static org.junit.Assert.assertEquals;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.internal.events.EventBroker;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.osgi.service.prefs.PreferencesService;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.model.action.ProjectActionGroups;
import org.testeditor.core.model.action.ProjectLibraryConfig;
import org.testeditor.core.services.interfaces.LibraryReaderService;
import org.testeditor.core.services.interfaces.ServiceLookUpForTest;
import org.testeditor.core.services.interfaces.TestExecutionEnvironmentService;
import org.testeditor.core.services.interfaces.TestProjectService;
import org.testeditor.core.services.interfaces.TestServerService;
import org.testeditor.core.services.plugins.LibraryReaderServicePlugIn;
import org.testeditor.core.services.plugins.TestEditorPlugInService;
import org.testeditor.ui.adapter.MPartAdapter;
import org.testeditor.ui.adapter.TestProjectServiceAdapter;
import org.testeditor.ui.mocks.PreferencesServiceMock;
import org.testeditor.ui.mocks.TestEditorPluginServiceMock;
import org.testeditor.ui.utilities.TestEditorTranslationService;

/**
 * 
 * Integration Tests for the TestProject Editor.
 * 
 */
public class TestProjectEditorTest {

	private Shell shell;

	/**
	 * Set UP UI.
	 */
	@Before
	public void setUp() {
		shell = new Shell();
	}

	/**
	 * free ui ressources.
	 */
	@After
	public void tearDown() {
		shell.dispose();
	}

	/**
	 * 
	 * Tests that the Combo Box for Library Types is filled by the
	 * TestEditorPluginService.
	 * 
	 * @throws Exception
	 *             for test
	 */
	@Test
	public void testLibraryComboBoxIsFilledWithServices() throws Exception {
		IEclipseContext context = createContextForIntTest();
		TestProjectEditor projectEditor = ContextInjectionFactory.make(TestProjectEditor.class, context);
		String[] items = projectEditor.getLibraryTypeCombo().getItems();
		assertEquals("Expect first dbservice", "DB", items[0]);
		assertEquals("Expect second xmlservice", "XML", items[1]);
	}

	/**
	 * Test that the String in the combobox retrives the correct Plug-In ID.
	 * 
	 * @throws Exception
	 *             for test
	 */
	@Test
	public void testLibraryServiceLookUpFromName() throws Exception {
		IEclipseContext context = createContextForIntTest();
		TestProjectEditor projectEditor = ContextInjectionFactory.make(TestProjectEditor.class, context);
		assertEquals("dbservice", projectEditor.getLibraryIDFor("DB"));
		assertEquals("xmlservice", projectEditor.getLibraryIDFor("XML"));
	}

	/**
	 * 
	 * @throws Exception
	 *             for Test.
	 */
	@Test
	public void testUpdateLibraryTypeComboSelection() throws Exception {
		IEclipseContext context = createContextForIntTest();

		TestProjectEditor projectEditor = ContextInjectionFactory.make(TestProjectEditor.class, context);
		assertEquals("Empty Selection in Combo", "", projectEditor.getLibraryTypeCombo().getText());
		projectEditor.updateLibraryTypeComboSelection("xmlservice");
		assertEquals("XML Type selected in Combo", "XML", projectEditor.getLibraryTypeCombo().getText());
	}

	/**
	 * 
	 * @return Mock for TestEditorPluginService
	 */
	private TestEditorPlugInService getTestEditorPluginServiceMock() {
		return new TestEditorPluginServiceMock();
	}

	/**
	 * 
	 * @return a Context with Mocks and Services to create the
	 *         TestProjectEditor.
	 */
	protected IEclipseContext createContextForIntTest() {
		IEclipseContext context = EclipseContextFactory.create();
		context.set(MPart.class, new MPartAdapter());
		context.set(PreferencesService.class, new PreferencesServiceMock());
		context.set(TestProjectService.class, getTestProjectServiceMock());
		context.set(TranslationService.class, getTranslationServiceMock());
		context.set(EPartService.class, null);
		context.set(TestServerService.class, ServiceLookUpForTest.getService(TestServerService.class));
		context.set(TestEditorPlugInService.class, ServiceLookUpForTest.getService(TestEditorPlugInService.class));
		context.set(LibraryReaderService.class, getLibraryReaderServiceMock());
		context.set(Composite.class, shell);
		context.set(IEventBroker.class, new EventBroker());
		context.set(TestEditorTranslationService.class,
				ContextInjectionFactory.make(TestEditorTranslationService.class, context));
		context.set(TestEditorPlugInService.class, getTestEditorPluginServiceMock());
		context.set(UISynchronize.class, getUISynchronizeMock());
		context.set(TestExecutionEnvironmentService.class, null);
		return context;
	}

	/**
	 * 
	 * @return a mock for UISynchronize.
	 */
	private UISynchronize getUISynchronizeMock() {
		return new UISynchronize() {

			@Override
			public void syncExec(Runnable runnable) {

			}

			@Override
			public void asyncExec(Runnable runnable) {

			}
		};
	}

	/**
	 * 
	 * @return Mock for LibraryReaderService
	 */
	private LibraryReaderService getLibraryReaderServiceMock() {
		return new LibraryReaderServicePlugIn() {

			@Override
			public ProjectActionGroups readBasisLibrary(ProjectLibraryConfig cfg) throws SystemException {
				return new ProjectActionGroups();
			}

			@Override
			public String getId() {
				return null;
			}
		};
	}

	/**
	 * 
	 * @return Mock for TranslationService
	 */
	private TranslationService getTranslationServiceMock() {
		return new TranslationService() {
			@Override
			public String translate(String key, String contributorURI) {
				return "";
			}
		};
	}

	/**
	 * 
	 * @return mock
	 */
	private TestProjectService getTestProjectServiceMock() {
		return new TestProjectServiceAdapter();
	}

}
