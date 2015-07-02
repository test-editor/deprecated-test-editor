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

import java.text.MessageFormat;
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
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MToolBarElement;
import org.eclipse.e4.ui.services.EMenuService;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;
import org.testeditor.core.constants.TestEditorCoreEventConstants;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.model.teststructure.TestFlow;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.model.teststructure.TestSuite;
import org.testeditor.core.services.interfaces.TeamShareStatusService;
import org.testeditor.core.services.interfaces.TestProjectService;
import org.testeditor.ui.ITestStructureEditor;
import org.testeditor.ui.constants.CustomWidgetIdConstants;
import org.testeditor.ui.constants.TestEditorConstants;
import org.testeditor.ui.constants.TestEditorUIEventConstants;
import org.testeditor.ui.handlers.OpenTestStructureHandler;
import org.testeditor.ui.handlers.ReloadLibraryHandler;
import org.testeditor.ui.parts.commons.tree.TestStructureTree;
import org.testeditor.ui.parts.editor.view.TestEditorTestCaseController;
import org.testeditor.ui.parts.editor.view.TestEditorTestScenarioController;
import org.testeditor.ui.parts.projecteditor.TestProjectEditor;
import org.testeditor.ui.parts.testsuite.TestSuiteEditor;
import org.testeditor.ui.utilities.TestEditorTranslationService;

/**
 * The Test-Explorer allows to browse the hierarchy of the test projects
 * containing test suites and test cases. Run tests and CRUD operations on the
 * test structures are available in the UI.
 */
public class TestExplorer {

	private static final Logger LOGGER = Logger.getLogger(TestExplorer.class);

	@Inject
	private IEclipseContext context;

	@Inject
	private TestProjectService testProjectService;

	@Inject
	private EPartService partService;

	@Inject
	private TeamShareStatusService teamShareStatusService;

	@Inject
	private TestEditorTranslationService translationService;

	private TestStructureTree testStructureTree;

	private MPart part;

	/**
	 * Constructor for the e4 framework to inject the model element of the
	 * explorer.
	 * 
	 * @param part
	 *            model part that represents the explorer view part.
	 */
	@Inject
	public TestExplorer(MPart part) {
		this.part = part;
	}

	/**
	 * Creates the tree-structure to manage test suites and test cases.
	 * 
	 * @param parent
	 *            UI-Parent
	 * @param service
	 *            service object used to register the popup-menu
	 */
	@PostConstruct
	public void createUi(Composite parent, EMenuService service) {
		testStructureTree = ContextInjectionFactory.make(TestStructureTree.class, context);
		testStructureTree.createUI(parent, testProjectService);

		TreeViewer treeViewer = testStructureTree.getTreeViewer();
		List<TestProject> projects;
		projects = testProjectService.getProjects();
		if (projects.size() > 0) {
			setSelectionOn(projects.get(0));
		}
		reloadTeamShareStatusForProjects();
		if (service != null) {
			service.registerContextMenu(treeViewer.getControl(), "org.testeditor.ui.popupmenu");
		}
		// Make the tree viewer accessible in the popup-menu handlers and
		// content-provider
		context.set(TestEditorConstants.TEST_EXPLORER_VIEW, this);
		treeViewer.addOpenListener(new IOpenListener() {
			@Override
			public void open(OpenEvent arg0) {
				OpenTestStructureHandler handler = ContextInjectionFactory
						.make(OpenTestStructureHandler.class, context);
				handler.execute(context);
			}
		});
		final IEventBroker eventBroker = context.get(IEventBroker.class);
		treeViewer.addPostSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				context.get(MApplication.class).getContext()
						.set(TestEditorConstants.SELECTED_TEST_COMPONENTS, getSelection());
				eventBroker.send(UIEvents.REQUEST_ENABLEMENT_UPDATE_TOPIC, UIEvents.ALL_ELEMENT_ID);
			}
		});
	}

	/**
	 * Refresh the Content of the TreeViewer. The TreeViewer Input is replaced
	 * with a new Object. Every open Instance will be saved. The old state of
	 * the tree is restored. This operation is expensive and should be used with
	 * care.
	 */
	public void refreshTreeInput() {
		Object[] expandedElements = testStructureTree.getTreeViewer().getVisibleExpandedElements();
		TestStructure selectedElement = testStructureTree.getSelectedTestStrucuture();
		getTreeViewer().getControl().setRedraw(false);
		List<TestStructure> openedTestStructures = getEveryOpenTestStructure();
		if (partService != null && !partService.saveAll(true)) {
			return;
		}
		testStructureTree.getTreeViewer().setInput(testProjectService);
		reloadTeamShareStatusForProjects();
		// Restore after refresh the previous ui state as much as possible
		for (Object expElement : expandedElements) {
			testStructureTree.getTreeViewer().expandToLevel(expElement, 1);
		}
		testStructureTree.selectTestStructure(selectedElement);
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
	 * Loads the Team-share status info for all projects.
	 */
	public void reloadTeamShareStatusForProjects() {
		for (TestProject project : testProjectService.getProjects()) {
			if (project.getTestProjectConfig().isTeamSharedProject()) {
				teamShareStatusService.setTeamStatusForProject(project);
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
		if (testStructureTree != null) {
			testStructureTree.selectTestStructure(testStructure);
		}
	}

	/**
	 * In case of modifications of library files this will be reloaded.
	 * 
	 * @param testProject
	 *            TestProject
	 */
	@Inject
	@Optional
	public void reloadLibrary(

	@UIEventTopic(TestEditorCoreEventConstants.LIBRARY_FILES_CHANGED_MODIFIED) TestProject testProject) {
		MessageDialog.openInformation(
				Display.getCurrent().getActiveShell(),
				translationService.translate("%reload.library.message.dialog.title"),
				MessageFormat.format(translationService.translate("%reload.library.message.dialog.data"),
						testProject.getName()));

		ReloadLibraryHandler reloadLibraryHandler = ContextInjectionFactory.make(ReloadLibraryHandler.class, context);
		reloadLibraryHandler.execute(testProject);

	}

	/**
	 * Set the Focus on the Tree with the Teststructures.
	 * 
	 * @param shell
	 *            Shell
	 */
	@Focus
	public void setFocusOnTree(@Named(IServiceConstants.ACTIVE_SHELL) Shell shell) {
		testStructureTree.getTreeViewer().getTree().setFocus();
		List<MToolBarElement> children = part.getToolbar().getChildren();
		for (MToolBarElement mToolBarElement : children) {
			Object widget = mToolBarElement.getWidget();
			if (widget != null && widget instanceof Widget) {
				((Widget) widget).setData(CustomWidgetIdConstants.TEST_EDITOR_WIDGET_ID_SWT_BOT_KEY,
						mToolBarElement.getElementId());
			}
		}
	}

	/**
	 * 
	 * @return used TreeViewer
	 */
	public TreeViewer getTreeViewer() {
		return testStructureTree.getTreeViewer();
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
		testStructureTree.getTreeViewer().refresh(testStructure);
	}

	/**
	 * Refresh the Content of the using the method {@link #refreshTreeInput()}.
	 * Will be Called by the event
	 * {@link TestEditorCoreEventConstants#TESTSTRUCTURE_MODEL_CHANGED}.
	 * 
	 * @param data
	 *            send from the sender.
	 */
	@Inject
	@Optional
	protected void refresh(@UIEventTopic(TestEditorCoreEventConstants.TESTSTRUCTURE_MODEL_CHANGED) String data) {
		reloadTeamShareStatusForProjects();
		getTreeViewer().refresh();
	}

	/**
	 * refreshes a single item of the given TestStructure in the tree by using
	 * the method {@link #refreshTreeViewerOnTestStrucutre(TestStructure)}. Will
	 * be Called by the event:
	 * {@link TestEditorCoreEventConstants#TEAM_STATE_LOADED}
	 * 
	 * @param testStructureName
	 *            TestStructure send from the sender.
	 */
	@Inject
	@Optional
	protected void refreshTreeByLoadedSVnState(
			@UIEventTopic(TestEditorCoreEventConstants.TESTSTRUCTURE_STATE_UPDATED) String testStructureName) {
		try {
			refreshTreeViewerOnTestStrucutre(testProjectService.findTestStructureByFullName(testStructureName));
		} catch (SystemException e) {
			LOGGER.error("Error reading teststructure by name", e);
		}
	}
}