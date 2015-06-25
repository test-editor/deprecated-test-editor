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
package org.testeditor.ui.parts.editor.view.Adapter;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.testeditor.ui.constants.TestEditorEventConstants;
import org.testeditor.ui.parts.editor.view.ActionLineToTestCaseLineMapper;
import org.testeditor.ui.parts.editor.view.TestEditView;
import org.testeditor.ui.parts.editor.view.TestEditorController;
import org.testeditor.ui.parts.editor.view.TestEditorTestDataTransferContainer;
import org.testeditor.ui.parts.editor.view.TestEditorTestScenarioController;
import org.testeditor.ui.parts.inputparts.actioninput.TestEditorActionInputController;
import org.testeditor.ui.parts.inputparts.descriptioninput.TestEditorDescriptionInputController;
import org.testeditor.ui.parts.inputparts.scenarioselection.TestEditorScenarioSelectionController;

/**
 * a mockup for the TestEditorTestScenarioController.
 * 
 * 
 */
public class TestEditorTestScenarioControllerMock extends TestEditorTestScenarioController {
	@Inject
	private Shell shell;

	/**
	 * constructor.
	 * 
	 * @param part
	 *            MPart
	 */
	@Inject
	public TestEditorTestScenarioControllerMock(MPart part) {
		super(part);
	}

	/**
	 * 
	 * @param parent
	 *            composite
	 */
	@Override
	@PostConstruct
	public void createControls(Composite parent) {
		setCompositeForView(new Composite(shell, SWT.NONE));
		createTestCaseView();
		setTestEditorActionInputController(getActionInputControllerMock());
		setTestEditorDescriptionInputController(getDescriptionControllerMock());
		setScenarioController(getScenarioControllerMock());
	}

	/**
	 * this method creates the TestCaseView.
	 */
	@Override
	public void createTestCaseView() {
		setTestEditViewArea(getTestEditViewAreaMock());
	}

	@Override
	protected void initializeControllerForToolboxes() {
	}

	@Override
	protected void wireUpToolBoxControllerWithEditorController() {
	}

	/**
	 * 
	 * @return a mockup for TestEditorDescriptionInputController.
	 */
	private TestEditorDescriptionInputController getDescriptionControllerMock() {
		return new TestEditorDescriptionInputController() {
			@Override
			public void cleanViewsSynchron() {
			}

			@Override
			public void setAddMode(boolean b) {
			}

			@Override
			public void setDescriptionActive(int selectedLine, int releasedLine) {
			}

			@Override
			public void removeTestCaseController(TestEditorController testEditorController) {
			}
		};
	}

	/**
	 * 
	 * @return a mockup for TestEditorActionInputController.
	 */
	private TestEditorActionInputController getActionInputControllerMock() {
		return new TestEditorActionInputController() {
			@Override
			public void cleanViewsSynchron() {
			}

			@Override
			public void setAddMode(boolean b) {
			}

			@Override
			public void setActionActive() {
			}

			@Override
			public void removeTestCaseController(TestEditorController testEditorController) {
			}
		};
	}

	/**
	 * operations, after the testflow is set.
	 */
	@Override
	protected void afterSetTestFlow() {
	}

	/**
	 * 
	 * @return a mockup for the TestEditView
	 */
	private TestEditView getTestEditViewAreaMock() {
		return new TestEditView() {
			private boolean insertBefore = true;

			@Override
			public int getSelectionStartInTestCase() {
				return 2;
			}

			@Override
			public int getSelectionEndInTestCase() {
				return 5;
			}

			@Override
			protected void markSelectedLine(int lineNumber) {
			}

			@Override
			public void setCursor(int newCursorPosition, boolean endLinePos) {
			}

			@Override
			public int getCorrespondingLine(int selectedLineInTestCase) {
				return 1;
			}

			@Override
			public TestEditorTestDataTransferContainer getTestFlowTranfer() {
				return null;
			}

			@Override
			public ActionLineToTestCaseLineMapper getCodeLineMapper() {
				return new ActionLineToTestCaseLineMapper() {
					@Override
					public int getContentOfOffsetViewToTestListAt(int lineIndex) {
						return 2;
					}
				};
			}

			@Override
			protected boolean canExecuteDelete() {
				return true;
			}

			@Override
			public boolean isInsertBefore() {
				return insertBefore;
			}

			@Override
			public void setInsertBefore(boolean insertBefore) {
				this.insertBefore = insertBefore;
			}

			@Override
			protected int getKlickedLineInTestCase() {
				return 1;
			}

			@Override
			protected boolean canExecuteCutCopy() {
				return true;
			}

			@Override
			protected boolean isTestDataTableSelected() {
				return true;
			}
		};
	}

	/**
	 * 
	 * @return a mockup for the TestEditorScenarioSelectionController.
	 */
	private TestEditorScenarioSelectionController getScenarioControllerMock() {
		return new TestEditorScenarioSelectionController() {

			@Override
			public void cleanScenarioSelectionInTree() {
			}

			@Override
			public void refreshFilterForScenarioTree(
					@UIEventTopic(TestEditorEventConstants.REFRESH_FILTER_FOR_SCENARIOS_IN_TREE) String param) {

			}

			@Override
			public void setScenarioSelectionActive(int selectedLine, int releasedLine) {

			}

			@Override
			public void removeTestCaseController(TestEditorController testEditorController) {
			}
		};
	}

	/**
	 * disposing the Composite for the view.
	 */
	@PreDestroy
	public void tearDown() {
		getCompositeContent().dispose();
	}
}
