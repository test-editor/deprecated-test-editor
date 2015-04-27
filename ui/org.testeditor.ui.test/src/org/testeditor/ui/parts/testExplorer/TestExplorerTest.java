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
package org.testeditor.ui.parts.testExplorer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.Category;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.IParameter;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.services.EMenuService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.services.interfaces.TeamShareStatusService;
import org.testeditor.core.services.interfaces.TestProjectService;
import org.testeditor.ui.adapter.MPartAdapter;
import org.testeditor.ui.adapter.PartServiceAdapter;
import org.testeditor.ui.adapter.TestProjectServiceAdapter;
import org.testeditor.ui.mocks.EventBrokerMock;
import org.testeditor.ui.utilities.TestEditorTranslationService;

/**
 * 
 * Integrationstest for the TestExplorer.
 * 
 */
public class TestExplorerTest {

	private TestExplorer testExplorer;
	private static int retriveTestStructureCounts;
	private Shell shell;
	private Composite composite;
	private IEclipseContext context;

	/**
	 * Init Test Object TestExplorer with the Eclipse Context.
	 */
	@Before
	public void initOUT() {
		context = EclipseContextFactory.create();
		context.set(MPart.class, null);
		context.set(ECommandService.class, new ECommandService() {

			@Override
			public Command getCommand(String commandId) {
				return null;
			}

			@Override
			public Category getCategory(String categoryId) {
				return null;
			}

			@Override
			public Command defineCommand(String id, String name, String description, Category category,
					IParameter[] parameters) {
				return null;
			}

			@Override
			public Category defineCategory(String id, String name, String description) {
				return null;
			}

			@Override
			public ParameterizedCommand createCommand(String id, Map parameters) {
				return null;
			}
		});
		context.set(IEventBroker.class, new EventBrokerMock() {
			@Override
			public boolean send(String topic, Object data) {
				return true;
			}

			@Override
			public boolean post(String topic, Object data) {
				return true;
			}
		});
		context.set(EPartService.class, getPartServiceMock());
		context.set(EHandlerService.class, null);
		context.set(TranslationService.class, null);
		context.set(EMenuService.class, null);
		context.set(TestEditorTranslationService.class, null);
		context.set(MWindow.class, null);
		context.set(Logger.class, null);
		context.set(MApplication.class, null);
		context.set(TeamShareStatusService.class, null);

		shell = new Shell();
		composite = new Composite(shell, SWT.NORMAL);
		context.set(Composite.class, composite);
		context.set(TestProjectService.class, getTestProjectMock());
		setRetriveTestStructureCounts(0);
		testExplorer = ContextInjectionFactory.make(TestExplorer.class, context);
	}

	/**
	 * Extends mock for PartService.
	 * 
	 * @return new MPartAdapter
	 */
	private EPartService getPartServiceMock() {
		return new PartServiceAdapter() {
			@Override
			public MPart getActivePart() {
				return new MPartAdapter();
			}
		};
	}

	/**
	 * set count variable for test.
	 * 
	 * @param retriveTestStructureCounts
	 *            variable
	 */
	protected static void setRetriveTestStructureCounts(int retriveTestStructureCounts) {
		TestExplorerTest.retriveTestStructureCounts = retriveTestStructureCounts;
	}

	/**
	 * 
	 * @return Mock for TestProjectService.
	 */
	private TestProjectService getTestProjectMock() {
		return new TestProjectServiceAdapter() {

			@Override
			public List<TestProject> getProjects() {
				setRetriveTestStructureCounts(retriveTestStructureCounts + 1);
				return new ArrayList<TestProject>();
			}

		};
	}

	/**
	 * Dispose used UI Elements.
	 */
	@After
	public void dispose() {
		composite.dispose();
		shell.dispose();
	}

	/**
	 * Test the refresh Action on the Tree.
	 * 
	 * @throws Exception
	 *             from Test.
	 */
	@Test
	public void testRefresh() throws Exception {
		testExplorer.refreshTreeInput();
		assertSame("Two Times Teststructure Service is called", 6, retriveTestStructureCounts);
	}

	/**
	 * Test that a Contextmenu is registered.
	 * 
	 * @throws Exception
	 *             for Test
	 */
	@Test
	public void testCreatePopupMenu() throws Exception {
		final ArrayList<Object> checkup = new ArrayList<Object>();
		EMenuService service = new EMenuService() {

			@Override
			public boolean registerContextMenu(Object parent, String menuId) {
				assertEquals("org.testeditor.ui.popupmenu", menuId);
				checkup.add(parent);
				return true;
			}
		};
		testExplorer.createUi(shell, service);
		assertNotNull(testExplorer.getTreeViewer());
		assertSame(checkup.get(0), testExplorer.getTreeViewer().getTree());
	}

}
