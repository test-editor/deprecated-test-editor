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

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testeditor.core.model.teststructure.TestCase;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.services.interfaces.TestProjectService;
import org.testeditor.ui.adapter.TestProjectServiceAdapter;

/**
 * Tests for the SearchTestStructureDialog.
 *
 */
public class SearchTestStructureDialogTest {

	private Shell shell;

	/**
	 * Tests the building of the dialog.
	 * 
	 * @throws Exception
	 *             on test failure
	 */
	@Test
	public void testCreateDialogAndFillResultView() throws Exception {
		IEclipseContext context = EclipseContextFactory.create();

		context.set(TestProjectService.class, new TestProjectServiceAdapter() {
			@Override
			public List<TestProject> getProjects() {
				ArrayList<TestProject> list = new ArrayList<TestProject>();
				TestCase testCase = new TestCase();
				TestProject testProject = new TestProject();
				testCase.setName("MyTest");
				testProject.addChild(testCase);
				list.add(testProject);
				return list;
			}
		});
		SearchTestStructureDialog dialog = new SearchTestStructureDialog(shell) {
			@Override
			public Shell getShell() {
				return shell;
			}
		};
		ContextInjectionFactory.inject(dialog, context);
		assertNull(dialog.getSelectedTestStructure());
		dialog.createDialogArea(shell);
		dialog.createTestStructureNamesLoader().run();
		assertSame(1, dialog.getResultViewer().getTable().getItemCount());
	}

	/**
	 * Creates UI handle for test.
	 */
	@Before
	public void setup() {
		shell = new Shell();
	}

	/**
	 * Cleanup ui handles.
	 */
	@After
	public void teardown() {
		shell.dispose();
	}
}
