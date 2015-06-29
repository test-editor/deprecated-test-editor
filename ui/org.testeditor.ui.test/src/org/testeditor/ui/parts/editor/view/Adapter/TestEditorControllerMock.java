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
import org.testeditor.ui.parts.editor.ITestEditorController;
import org.testeditor.ui.parts.editor.view.ActionLineToTestCaseLineMapper;
import org.testeditor.ui.parts.editor.view.TestEditView;
import org.testeditor.ui.parts.editor.view.TestEditorController;
import org.testeditor.ui.parts.editor.view.TestEditorTestCaseController;
import org.testeditor.ui.parts.editor.view.TestEditorTestDataTransferContainer;
import org.testeditor.ui.parts.inputparts.actioninput.TestEditorActionInputController;
import org.testeditor.ui.parts.inputparts.descriptioninput.TestEditorDescriptionInputController;
import org.testeditor.ui.parts.inputparts.scenarioselection.TestEditorScenarioSelectionController;

/**
 * Mock to test the save-method of the TestEditorController.
 * 
 * @author llipinski
 * 
 */
public class TestEditorControllerMock extends TestEditorTestCaseController {

	@Inject
	private Shell shell;
	private boolean scenarioTreeCleaned;
	private int selectedLineInView = -1;

	private TestEditorTestDataTransferContainer testFlowTransfer;
	private boolean popupClosed;
	private boolean refreshed = false;

	/**
	 * constructor.
	 * 
	 * @param part
	 *            MPart
	 */
	@Inject
	public TestEditorControllerMock(MPart part) {
		super(part);
	}

	/**
	 * specialized method without contents for the mock.
	 */
	@Override
	protected void afterSetTestFlow() {

	}

	@Override
	protected void initializeControllerForToolboxes() {
	}

	@Override
	protected void wireUpToolBoxControllerWithEditorController() {
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
	 * 
	 * @return a mockup for the TestEditorScenarioSelectionController.
	 */
	private TestEditorScenarioSelectionController getScenarioControllerMock() {
		return new TestEditorScenarioSelectionController() {

			@Override
			public void cleanScenarioSelectionInTree() {
				scenarioTreeCleaned = true;
			}

			@Override
			public void refreshFilterForScenarioTree(
					@UIEventTopic(TestEditorEventConstants.REFRESH_FILTER_FOR_SCENARIOS_IN_TREE) String param) {

			}

			@Override
			public void setScenarioSelectionActive(int selectedLine, int releasedLine) {

			}

			@Override
			public void removeTestCaseController(ITestEditorController testEditorController) {
			}
		};
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
	 * this method creates the TestCaseView.
	 */
	@Override
	protected void createTestCaseView() {
		setTestEditViewArea(getTestEditViewAreaMock());
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
				selectedLineInView = lineNumber;
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
				return testFlowTransfer;
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
			public void refreshStyledText() {
				refreshed = true;
			}

			@Override
			public boolean isLineEmpty(int klickedLine) {
				return true;
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

			@Override
			public void closePopupDialog() {
				popupClosed = true;
			}
		};
	}

	/**
	 * setter for the variable testFlowTransfer.
	 * 
	 * @param transferContainer
	 *            TestEditorTestDataTransferContainer
	 */
	public void setTestFlowTranfer(TestEditorTestDataTransferContainer transferContainer) {
		this.testFlowTransfer = transferContainer;

	}

	/**
	 * 
	 * @return the value of scenarioTreeCleaned.
	 */
	public boolean isScenarioTreeCleaned() {
		return scenarioTreeCleaned;
	}

	/**
	 * sets the value of scenarioTreeCleaned.
	 * 
	 * @param scenarioTreeCleaned
	 *            boolean
	 */
	public void setScenarioTreeCleaned(boolean scenarioTreeCleaned) {
		this.scenarioTreeCleaned = scenarioTreeCleaned;
	}

	/**
	 * 
	 * @return the selectedLineInView.
	 */
	public int getSelectedLineInView() {
		return selectedLineInView;
	}

	/**
	 * sets the value to popupClosed = false.
	 */
	public void setPopupDialogOpend() {
		popupClosed = false;
	}

	/**
	 * 
	 * @return the value of popupClosed
	 */
	public boolean isPopupClosed() {
		return popupClosed;
	}

	/**
     * 
     */
	@Override
	protected void connectActionInputController() {
	}

	/**
     * 
     */
	@Override
	protected void connectDescriptionController() {
	}

	/**
	 * 
	 */
	@Override
	protected void connectScenarioController() {

	}

	/**
	 * 
	 * @return the value of refreshed
	 */
	public boolean getRefreshed() {
		return refreshed;
	}

	/**
	 * disposing the Composite for the view.
	 */
	@PreDestroy
	public void tearDown() {
		getCompositeContent().dispose();
	}

}
