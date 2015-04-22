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
package org.testeditor.ui.parts.inputparts.scenarioselection;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.testeditor.core.model.teststructure.ScenarioSuite;
import org.testeditor.core.model.teststructure.TestFlow;
import org.testeditor.core.model.teststructure.TestScenario;
import org.testeditor.core.model.teststructure.TestScenarioParameterTable;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.services.interfaces.TestStructureTreeModelService;
import org.testeditor.ui.constants.CustomWidgetIdConstants;
import org.testeditor.ui.constants.IconConstants;
import org.testeditor.ui.constants.TestEditorEventConstants;
import org.testeditor.ui.parts.commons.tree.TestStructureTree;
import org.testeditor.ui.parts.commons.tree.filter.TestScenarioRecursiveFilter;
import org.testeditor.ui.parts.editor.view.handler.TestEditorInputObject;
import org.testeditor.ui.parts.inputparts.TestEditorInputView;
import org.testeditor.ui.utilities.TestEditorTranslationService;

/**
 * Description Area is the UI of a TestCase Description used in the Testeditor.
 * 
 * @author Lothar Lipinski
 * 
 */
public class TestEditorScenarioSelectionView extends TestEditorInputView {

	@Inject
	private TestEditorTranslationService translationService;

	@Inject
	private IEclipseContext context;

	@Inject
	private IEventBroker eventBroker;

	private TestStructureTree testScenarioTree;
	private int selectedLine;
	private ScrolledComposite sc;
	private Composite subContainer;
	private Composite buttonComposite;
	private SashForm sash;
	private Button scenarioLinkButton;
	private TestStructureTreeModelService treeInputService;

	private ITestScenarioSelectionController scenarioController;

	/**
	 * whit this method the ui is created.
	 * 
	 * @param compositeContent
	 *            {@link Composite}
	 * @wbp.parser.entryPoint
	 */
	@Override
	public void createUI(Composite compositeContent) {
		if (compositeContent != null && sc == null) {
			compositeContent.setLayout(new FillLayout());
			// Create the ScrolledComposite to scroll horizontally and
			// vertically
			sc = new ScrolledComposite(compositeContent, SWT.H_SCROLL | SWT.V_SCROLL);

			// Expand both horizontally and vertically
			sc.setExpandHorizontal(true);
			sc.setExpandVertical(true);

			subContainer = new Composite(sc, SWT.NONE);
			sc.setContent(subContainer);

			super.createUI(subContainer);

			createScenarioSelectionArea(getEditComposite());
			testScenarioTree.getTreeViewer().getTree().setVisible(false);
			getEditComposite().setVisible(false);
			setCommitButtonVisible(false);
			compositeContent.layout();
			getEditComposite().layout();
		}
	}

	/**
	 * creates the input text-area for the description.
	 * 
	 * @param parent
	 *            {@link Composite}
	 */
	public void createScenarioSelectionArea(Composite parent) {
		parent.setLayout(new FillLayout());
		sash = new SashForm(parent, SWT.HORIZONTAL);
		if (testScenarioTree == null) {
			testScenarioTree = ContextInjectionFactory.make(TestStructureTree.class, context);

			testScenarioTree.createUI(sash, treeInputService, SWT.SINGLE | SWT.BORDER);
			testScenarioTree.getTreeViewer().getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
			testScenarioTree
					.getTreeViewer()
					.getTree()
					.setData(CustomWidgetIdConstants.TEST_EDITOR_WIDGET_ID_SWT_BOT_KEY,
							CustomWidgetIdConstants.CHOSE_SCENARIO);
			testScenarioTree.getTreeViewer().getTree().setVisible(true);

			buttonComposite = new Composite(sash, SWT.NORMAL | SWT.BORDER);
			GridLayout gridLayoutButtonComposite = new GridLayout(1, false);
			GridData gdButtonComposite = new GridData(SWT.NORMAL, SWT.CENTER, true, false);
			buttonComposite.setLayoutData(gdButtonComposite);
			buttonComposite.setLayout(gridLayoutButtonComposite);
			scenarioLinkButton = new Button(buttonComposite, SWT.PUSH);
			scenarioLinkButton.setImage(IconConstants.ICON_SCENARIO);
			String messageCommit = translationService.translate("%TestEditView_ScenarioOpen");

			scenarioLinkButton.setText(messageCommit);
			scenarioLinkButton.setEnabled(false);
			scenarioLinkButton.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					openScenarioLink();
				}
			});
			createCommitButtons(buttonComposite);

			getCommitButton().setData(CustomWidgetIdConstants.TEST_EDITOR_WIDGET_ID_SWT_BOT_KEY,
					CustomWidgetIdConstants.ADD_NEW_SCENARIO);

			addSelectionListernToCommitButtons();

			addValidateScenarioSelectionText();
			sash.setWeights(new int[] { 1, 1 });

		}
	}

	/**
	 * adds the Validation of the commitButton for the descriptionText.
	 */
	private void addValidateScenarioSelectionText() {
		testScenarioTree.getTreeViewer().getTree().addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (testScenarioTree.getSelectedTestStrucuture() != null
						&& !(testScenarioTree.getSelectedTestStrucuture() instanceof ScenarioSuite)) {
					setEnabledCommitButtons(true);
					scenarioLinkButton.setEnabled(true);
				} else {
					setEnabledCommitButtons(false);
					scenarioLinkButton.setEnabled(false);
				}

			}

		});

	}

	/**
	 * opens the selected scenario.
	 */
	private void openScenarioLink() {
		scenarioController.openSelectedScenario(testScenarioTree.getSelectedTestStrucuture().getFullName());
	}

	/**
	 * sets the scenarioSelection active.
	 */
	protected void setScenarioSelectionActive() {
		// the actionInputLine should be clean up
		if (getEditComposite() != null) {
			getEditComposite().layout();
			getEditComposite().setVisible(true);
			getEditComposite().setEnabled(true);
			testScenarioTree.getTreeViewer().getTree().setVisible(true);
			setCommitButtonVisible(true);
			setCommitToDefaultButton();
		}
	}

	/**
	 * sets a scenario to the input area.
	 * 
	 * @param scenario
	 *            the TestScenario
	 * @param lineNumber
	 *            number of the line
	 * 
	 */
	public void setScenarioSelectionToChangeable(TestScenario scenario, int lineNumber) {
		testScenarioTree.selectTestStructure(scenario);
		setSelectedLine(lineNumber);
		getEditComposite().layout(true);
		getEditComposite().getParent().layout(true);
	}

	/**
	 * this method add (addMode) or changes a description in the test case.
	 */
	@Override
	public void changeInputInView() {
		selectedLine = getTestCaseController().getChangePosition(getAddMode());
		scenarioController.setScenarioIntoTestFlow(testScenarioTree.getSelectedTestStrucuture(), selectedLine,
				getAddMode());
		setAddMode(true);

		int newSelectedLine = getTestCaseController().getChangePosition(getAddMode());
		if (newSelectedLine > selectedLine) {
			selectedLine = newSelectedLine;
		}

		markSelectedLineInView(selectedLine);
	}

	/**
	 * cleans the text of the description-line.
	 */
	@Override
	public void cleanInput() {
		if (getEditComposite() != null && !getEditComposite().isDisposed()) {
			testScenarioTree.getTreeViewer().getTree().deselectAll();
		}
		setEnabledCommitButtons(false);
		if (scenarioLinkButton != null && !scenarioLinkButton.isDisposed()) {
			scenarioLinkButton.setEnabled(false);
		}
	}

	/**
	 * sets the selected line.
	 * 
	 * @param lineNumber
	 *            int number of the line
	 */
	public void setSelectedLine(int lineNumber) {
		selectedLine = lineNumber;
	}

	/**
	 * disables the views.
	 */
	@Override
	public void disabelViews() {
		super.disabelViews();
		if (testScenarioTree != null && !testScenarioTree.getTreeViewer().getTree().isDisposed()) {
			testScenarioTree.getTreeViewer().getTree().setVisible(false);
			scenarioLinkButton.setEnabled(false);
		}
	}

	/**
	 * enables the views.
	 */
	@Override
	public void enableViews() {
		super.enableViews();
		if (testScenarioTree != null && !testScenarioTree.getTreeViewer().getTree().isDisposed()) {
			testScenarioTree.getTreeViewer().getTree().setVisible(true);
			scenarioLinkButton.setEnabled(false);
		}
	}

	/**
	 * setter for the scenarioController.
	 * 
	 * @param testEditorScenarioSelectionController
	 *            ITestScenarioSelectionController
	 */
	public void setTestScenarioSelectionController(
			ITestScenarioSelectionController testEditorScenarioSelectionController) {
		this.scenarioController = testEditorScenarioSelectionController;

	}

	/**
	 * removes every entry in the scenarioCombobox.
	 */
	protected void cleanScenarioTree() {
		if (testScenarioTree != null) {
			testScenarioTree.getTreeViewer().getTree().removeAll();
		}
	}

	/**
	 * collects the input and post it as a {@link ScenarioParameterTable} with a
	 * CACHE_TEST_COMPONENT_TEMPORARY-event.
	 * 
	 */
	public void cacheInput() {
		if (getTestCaseController() != null) {
			selectedLine = getTestCaseController().getChangePosition(getAddMode());
			TestScenarioParameterTable testComponent = new TestScenarioParameterTable();
			if (testScenarioTree != null && testScenarioTree.getSelectedTestStrucuture() != null) {
				testComponent.setTitle(testScenarioTree.getSelectedTestStrucuture().getName());
				// ToDo hier ungefähr anderes Element abspeichern.
			}

			TestFlow testFlow = getTestCaseController().getTestFlow();
			int selectedLine = getTestCaseController().getChangePosition(getAddMode());
			eventBroker.post(TestEditorEventConstants.CACHE_TEST_COMPONENT_TEMPORARY, new TestEditorInputObject(
					testFlow, testComponent, selectedLine, 0, getAddMode()));
		}
	}

	/**
	 * changes the treeinput.
	 * 
	 * @param testScenarioTreeInput
	 *            TestScenarioTreeInput
	 */
	protected void changeTreeInput(TestScenarioTreeInput testScenarioTreeInput) {
		if (testScenarioTree != null) {
			// Restore after refresh the previous ui state as much as possible
			testScenarioTree.getTreeViewer().setInput(testScenarioTreeInput);
		}
		this.treeInputService = testScenarioTreeInput;

	}

	/**
	 * sets the filter to avoid a simple recursive scenario-call (i.e.
	 * scenario-a is calling scenario-a) .
	 * 
	 * @param testScenarioRecursiveFilter
	 *            TestScenarioRecursiveFilter
	 */
	protected void setRecursiveFilter(TestScenarioRecursiveFilter testScenarioRecursiveFilter) {
		if (testScenarioRecursiveFilter != null && testScenarioTree != null && testScenarioTree.getTreeViewer() != null) {
			// Restore after refresh the previous ui state as much as possible
			testScenarioTree.getTreeViewer().addFilter(testScenarioRecursiveFilter);
		}
	}

	/**
	 * removes every ViewerFilters.
	 */
	protected void removeTreeFilter() {
		// Restore after refresh the previous ui state as much as possible
		if (testScenarioTree != null) {
			ViewerFilter[] filters = testScenarioTree.getTreeViewer().getFilters();
			for (ViewerFilter filter : filters) {
				testScenarioTree.getTreeViewer().removeFilter(filter);
			}
		}
	}

	/**
	 * 
	 * @return the visible expanded elements of the tree.
	 */
	protected Object[] getVisibleExpandedTreeElements() {
		if (testScenarioTree != null && testScenarioTree.getTreeViewer() != null
				&& testScenarioTree.getTreeViewer().getVisibleExpandedElements() != null) {
			return testScenarioTree.getTreeViewer().getVisibleExpandedElements();
		}
		return null;
	}

	/**
	 * 
	 * @return the selected elements of the tree.
	 */
	protected TestStructure getSelectedTreeElement() {
		if (testScenarioTree != null) {
			return testScenarioTree.getSelectedTestStrucuture();
		}
		return null;
	}

	/**
	 * select element in the tree given by the parameter selectedElement.
	 * 
	 * @param selectedElement
	 *            the TestStructure to select in the tree.
	 */
	protected void selectTreeElement(TestStructure selectedElement) {
		testScenarioTree.selectTestStructure(selectedElement);
	}

	/**
	 * expands the elements in the tree given by the parameter expandedElements.
	 * 
	 * @param expandedElements
	 *            the elements to expand.
	 */
	protected void expandTreeElements(Object[] expandedElements) {
		for (Object expElement : expandedElements) {
			testScenarioTree.getTreeViewer().expandToLevel(expElement, 1);
		}
	}

	/**
	 * cleans the scenario-selection in the tree.
	 */
	public void cleanSceanrioSelectionInTree() {
		if (testScenarioTree != null) {
			if (testScenarioTree.getTreeViewer() != null && testScenarioTree.getTreeViewer().getTree() != null
					&& !testScenarioTree.getTreeViewer().getTree().isDisposed()) {
				testScenarioTree.getTreeViewer().setSelection(null);
			}
			if (getCommitButton() != null && !getCommitButton().isDisposed()) {
				getCommitButton().setEnabled(false);
			}
			if (scenarioLinkButton != null && !scenarioLinkButton.isDisposed()) {
				scenarioLinkButton.setEnabled(false);
			}
		}
	}

}