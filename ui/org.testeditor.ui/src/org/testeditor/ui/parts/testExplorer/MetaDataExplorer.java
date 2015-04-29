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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.EMenuService;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.testeditor.core.constants.TestEditorCoreEventConstants;
import org.testeditor.core.model.teststructure.TestFlow;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.model.teststructure.TestSuite;
import org.testeditor.core.services.interfaces.TestProjectService;
import org.testeditor.metadata.core.MetaDataService;
import org.testeditor.teamshare.svn.TeamShareStatus;
import org.testeditor.ui.ITestStructureEditor;
import org.testeditor.ui.constants.TestEditorConstants;
import org.testeditor.ui.constants.TestEditorUIEventConstants;
import org.testeditor.ui.handlers.OpenTestStructureHandler;
import org.testeditor.ui.parts.commons.tree.MetaDataStructureTree;
import org.testeditor.ui.parts.editor.view.TestEditorTestCaseController;
import org.testeditor.ui.parts.editor.view.TestEditorTestScenarioController;
import org.testeditor.ui.parts.projecteditor.TestProjectEditor;
import org.testeditor.ui.parts.testsuite.TestSuiteEditor;

/**
 * The Test-Explorer allows to browse the hierarchy of the test projects
 * containing test suites and test cases. Run tests and CRUD operations on the
 * test structures are available in the UI.
 */
public class MetaDataExplorer {

	private static final Logger LOGGER = Logger.getLogger(MetaDataExplorer.class);

	@Inject
	private IEclipseContext context;

	@Inject
	private TestProjectService testProjectService;

	@Inject
	private MetaDataService metaDataService;

	@Inject
	private EPartService partService;

	@Inject
	private IEventBroker eventBroker;

	private MetaDataStructureTree metaDataStructureTree;

	/**
	 * Creates the tree-structure to manage testsuite and testcases.
	 * 
	 * @param parent
	 *            UI-Parent
	 * @param service
	 *            service object used to register the popup-menu
	 */
	@PostConstruct
	public void createUi(Composite parent, EMenuService service) {
		metaDataStructureTree = ContextInjectionFactory.make(MetaDataStructureTree.class, context);
		metaDataStructureTree.createUI(parent, metaDataService);
		// testStructureTree.showOnlyTestKomponentsSuite();
		TreeViewer treeViewer = metaDataStructureTree.getTreeViewer();
		List<TestProject> projects;
		projects = testProjectService.getProjects();
		if (projects.size() > 0) {
			setSelectionOn(projects.get(0));
		}
		reloadSvnStatusForProjects();
		if (service != null) {
			service.registerContextMenu(treeViewer.getControl(), "org.testeditor.ui.popupmenu");
		}
		// Make the tree viewer accessible in the popup-menu handlers and
		// content-provider
		context.set(TestEditorConstants.META_DATA_EXPLORER_VIEW, this);
		treeViewer.addOpenListener(new IOpenListener() {
			@Override
			public void open(OpenEvent arg0) {
				OpenTestStructureHandler handler = ContextInjectionFactory
						.make(OpenTestStructureHandler.class, context);
				handler.execute(context);
			}
		});
	}

	/**
	 * Refresh the Content of the Treeviewer. The Treeviewer Input is replaced
	 * with a new Object. Every open Instance will be saved. The old state of
	 * the tree is restored. This operation is expensive and should be used with
	 * care.
	 */
	public void refreshTreeInput() {
		Object[] expandedElements = metaDataStructureTree.getTreeViewer().getVisibleExpandedElements();
		TestStructure selectedElement = metaDataStructureTree.getSelectedTestStrucuture();
		getTreeViewer().getControl().setRedraw(false);
		List<TestStructure> openedTestStructures = getEveryOpenTestStructure();
		if (partService != null && !partService.saveAll(true)) {
			return;
		}
		metaDataStructureTree.getTreeViewer().setInput(testProjectService);
		reloadSvnStatusForProjects();
		// Restore after refresh the previous ui state as much as possible
		for (Object expElement : expandedElements) {
			metaDataStructureTree.getTreeViewer().expandToLevel(expElement, 1);
		}
		metaDataStructureTree.selectTestStructure(selectedElement);
		getTreeViewer().getControl().setRedraw(true);
		OpenTestStructureHandler openHandler = ContextInjectionFactory.make(OpenTestStructureHandler.class, context);
		for (TestStructure ts : openedTestStructures) {
			if (ts instanceof TestFlow) {
				openHandler.execute((TestFlow) ts, context);
			} else if (ts instanceof TestProject) {
				openHandler.execute((TestProject) ts, context);
			} else if (ts instanceof TestSuite) {
				openHandler.execute((TestSuite) ts, context);
			}
		}
	}

	/**
	 * Loads the SVN Status for all projects.
	 */
	public void reloadSvnStatusForProjects() {
		for (TestProject project : testProjectService.getProjects()) {
			if (project.getTestProjectConfig().isTeamSharedProject()) {
				TeamShareStatus shareState = new TeamShareStatus(eventBroker);
				shareState.setSVNStatusForProject(project);
			}
		}
	}

	/**
	 * Gives all TestStructure back, which are opened in the testExplorer.
	 * 
	 * @return testStructureList
	 */
	private List<TestStructure> getEveryOpenTestStructure() {
		List<TestStructure> testStructureList = new ArrayList<TestStructure>();
		if (partService == null) {
			return testStructureList;
		}
		Collection<MPart> parts = partService.getParts();
		if (parts == null) {
			return testStructureList;
		}
		for (MPart p : parts) {
			if ((p.getElementId().equals(TestEditorTestCaseController.ID)
					|| p.getElementId().equals(TestProjectEditor.ID)
					|| p.getElementId().equals(TestEditorTestScenarioController.ID) || p.getElementId().equals(
					TestSuiteEditor.ID))
					&& (p.getObject() != null) && (((ITestStructureEditor) p.getObject()).getTestStructure()) != null) {
				testStructureList.add(((ITestStructureEditor) p.getObject()).getTestStructure());
			}
		}
		return testStructureList;
	}

	/**
	 * Changes the Selection of the TestCase Tree.
	 * 
	 * @param testStructure
	 *            to be selected in the Tree.
	 */
	@Inject
	@Optional
	public void setSelectionOn(
			@UIEventTopic(TestEditorUIEventConstants.ACTIVE_TESTFLOW_EDITOR_CHANGED) TestStructure testStructure) {
		if (metaDataStructureTree != null) {
			metaDataStructureTree.selectTestStructure(testStructure);
		}
	}

	/**
	 * Set the Focus on the Tree with the Teststructures.
	 * 
	 * @param shell
	 *            Shell
	 */
	@Focus
	public void setFocusOnTree(@Named(IServiceConstants.ACTIVE_SHELL) Shell shell) {
		metaDataStructureTree.getTreeViewer().getTree().setFocus();
		shell.setDefaultButton(null);
	}

	/**
	 * 
	 * @return used TreeViewer
	 */
	public TreeViewer getTreeViewer() {
		return metaDataStructureTree.getTreeViewer();
	}

	/**
	 * 
	 * @return selected Teststructures in the TestExplorer tree.
	 */
	public IStructuredSelection getSelection() {
		return (IStructuredSelection) getTreeViewer().getSelection();
	}

	/**
	 * refreshes a single item of the given TestStructure in the tree.
	 * 
	 * @param testStructure
	 *            the TestStructure of the item to refresh
	 */
	public void refreshTreeViewerOnTestStrucutre(TestStructure testStructure) {
		metaDataStructureTree.getTreeViewer().refresh(testStructure);
	}

	/**
	 * Refresh the Content of the using the methode {@link #refreshTreeInput()}.
	 * Will be Called by the event
	 * {@link TestEditorCoreEventConstants#TESTSTRUCTURE_MODEL_CHANGED}.
	 * 
	 * @param data
	 *            send from the sender.
	 */
	@Inject
	@Optional
	protected void refresh(@UIEventTopic(TestEditorCoreEventConstants.TESTSTRUCTURE_MODEL_CHANGED) String data) {
		reloadSvnStatusForProjects();
		getTreeViewer().refresh();
	}

	/**
	 * refreshes a single item of the given TestStructure in the tree by using
	 * the methode {@link #refreshTreeViewerOnTestStrucutre(TestStructure)}.
	 * Will be Called by the event:
	 * {@link TestEditorCoreEventConstants#TEAM_STATE_LOADED}
	 * 
	 * @param testStructure
	 *            TestStructure send from the sender.
	 */
	@Inject
	@Optional
	protected void refreshTreeByLoadedSVnState(
			@UIEventTopic(TestEditorCoreEventConstants.TEAM_STATE_LOADED) TestStructure testStructure) {
		refreshTreeViewerOnTestStrucutre(testStructure);
	}

}