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
package org.testeditor.ui.parts.inputparts;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.testeditor.ui.constants.IconConstants;
import org.testeditor.ui.parts.editor.ITestEditorController;
import org.testeditor.ui.utilities.TestEditorTranslationService;

/**
 * this class is a parent of the description and the actioninputview-class.
 */
public abstract class TestEditorInputView {

	@Inject
	@Optional
	private ITestEditorController testCaseController;
	private Composite editComposite;
	private Button commitButton;
	private Button commitAndCloseButton;

	private boolean addMode;

	@Inject
	private TestEditorTranslationService translationService;

	/**
	 * whit this method the ui is created.
	 * 
	 * @param compositeContent
	 *            {@link Composite}
	 * @wbp.parser.entryPoint
	 */
	public void createUI(Composite compositeContent) {

		if (compositeContent != null) {
			editComposite = compositeContent;
			GridLayout gridLayout = new GridLayout();
			gridLayout.numColumns = 1;
			editComposite.setLayout(gridLayout);
			editComposite.setEnabled(false);
		}
	}

	/**
	 * 
	 * @return the commit Button of the InputView.
	 */
	public Button getCommitButton() {
		return commitButton;
	}

	/**
	 * StyledText will be synchronized with current Document.
	 */
	protected void refreshStyledText() {

		testCaseController.refreshStyledText();
	}

	/**
	 * this method sets the testCaseController.
	 * 
	 * @param testCaseController
	 *            {@link ITestEditorController}
	 */
	public void setTestCaseControler(ITestEditorController testCaseController) {
		this.testCaseController = testCaseController;
		if (editComposite != null && !editComposite.isDisposed() && testCaseController != null) {
			editComposite.setEnabled(true);
			enableViews();
		} else if (testCaseController == null && editComposite != null && !editComposite.isDisposed()) {
			editComposite.setEnabled(false);
			editComposite.setVisible(false);
			commitButton.setVisible(false);
		}
	}

	/**
	 * sets the selectedLine in the View of the Testcase.
	 * 
	 * @param selectedLine
	 *            SelectedtLine
	 */
	protected void markSelectedLineInView(int selectedLine) {
		testCaseController.markCorrespondingLine(selectedLine);

	}

	/**
	 * this method compares the member testCaseController with the parameter
	 * newTestEditorController.
	 * 
	 * @param newTestEditorController
	 *            TestEditorController
	 * @return the result of comparison
	 */
	public boolean isNewTestEditorController(ITestEditorController newTestEditorController) {
		return testCaseController == newTestEditorController;
	}

	/**
	 * removes the testEditorController, if its equal to the stored in this
	 * object.
	 * 
	 * @param oldTestEditorController
	 *            ITestEditorController
	 */
	public void removeTestEditorController(ITestEditorController oldTestEditorController) {
		if (testCaseController == oldTestEditorController) {
			setTestCaseControler(null);
		}
	}

	/**
	 * set the add mode.
	 * 
	 * @param b
	 *            boolean
	 */
	public void setAddMode(boolean b) {
		addMode = b;
		String messageCommit = "";
		String messageCommitAndClose = "";
		if (addMode) {
			messageCommit = translationService.translate("%TestEditViewContext_insert");
			messageCommitAndClose = translationService.translate("%TestEditViewContext_insertAndClose");
		} else {
			messageCommit = translationService.translate("%TestEditViewContext_commit");
			messageCommitAndClose = translationService.translate("%TestEditViewContext_commitAndClose");
		}
		if (commitButton != null && !commitButton.isDisposed()) {
			commitButton.setText(messageCommit);
			commitButton.getParent().layout(true);
		}
		if (commitAndCloseButton != null && !commitAndCloseButton.isDisposed()) {
			commitAndCloseButton.setText(messageCommitAndClose);
			commitAndCloseButton.getParent().layout(true);
		}
	}

	/**
	 * add the commitButtons.
	 * 
	 * @param parent
	 *            the composite for the composite of the buttons
	 */
	protected void createCommitButtons(Composite parent) {
		Composite buttonCompo = new Composite(parent, SWT.NONE);
		GridLayout gd = new GridLayout(2, true);
		buttonCompo.setLayout(gd);

		commitButton = new Button(buttonCompo, SWT.PUSH);

		commitButton.setImage(IconConstants.ICON_ADD);
		String messageCommit = translationService.translate("%TestEditViewContext_commit");

		commitButton.setText(messageCommit);

		commitAndCloseButton = new Button(buttonCompo, SWT.PUSH);
		commitAndCloseButton.setImage(IconConstants.ICON_ADD);
		String messageCommitAndClose = translationService.translate("%TestEditViewContext_commitAndClose");

		commitAndCloseButton.setText(messageCommitAndClose);

		commitAndCloseButton.setVisible(false);
	}

	/**
	 * set the parameter enable-close-after-commit.
	 * 
	 * @param status
	 *            boolean
	 */
	public void setPopupmode(boolean status) {
		commitAndCloseButton.setVisible(status);
	}

	/**
	 * this method returns the addmode.
	 * 
	 * @return boolean addmode
	 */
	public boolean getAddMode() {
		return addMode;
	}

	/**
	 * returns the private variable testCaseController.
	 * 
	 * @return ITestEditorController testCaseController
	 */
	public ITestEditorController getTestCaseController() {
		return testCaseController;
	}

	/**
	 * returns the private variable editComposite.
	 * 
	 * @return composite editComposite
	 */
	protected Composite getEditComposite() {
		return editComposite;
	}

	/**
	 * sets the enabled status at the commitButtons to the paramter enable.
	 * 
	 * @param enable
	 *            boolean
	 */

	protected void setEnabledCommitButtons(boolean enable) {
		if (commitButton != null && !commitButton.isDisposed()) {
			commitButton.setEnabled(enable);
		}
		if (commitAndCloseButton != null && !commitAndCloseButton.isDisposed()) {
			commitAndCloseButton.setEnabled(enable);
		}
	}

	/**
	 * sets the commit-button as the defaultbutton.
	 */
	public void setCommitToDefaultButton() {
		if (commitButton != null && !commitButton.isDisposed()) {
			getEditComposite().getShell().setDefaultButton(commitButton);
		}
	}

	/**
	 * add selectionListener to the Commit-Buttons.
	 */
	protected void addSelectionListernToCommitButtons() {
		commitButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				changeInputInView();
				cleanInput();
				setAddMode(true);
			}
		});
		commitAndCloseButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				changeInputInView();
				cleanInput();
				getEditComposite().setEnabled(false);
				getEditComposite().setVisible(false);
				getTestCaseController().closePopupDialog();
				setAddMode(true);
			}
		});
	}

	/**
	 * cleans the input.
	 */
	protected abstract void cleanInput();

	/**
	 * puts the input to the editor-view.
	 */
	protected abstract void changeInputInView();

	/**
	 * disables the views.
	 */
	public void disabelViews() {
		if (getEditComposite() != null && !getEditComposite().isDisposed()) {
			getEditComposite().setEnabled(false);
			getEditComposite().setVisible(false);
		}

	}

	/**
	 * enables the views.
	 */
	public void enableViews() {
		if (getEditComposite() != null && !getEditComposite().isDisposed()) {
			getEditComposite().setEnabled(true);
			getEditComposite().setVisible(true);
			setCommitButtonVisible(true);
		}
	}

	/**
	 * sets the visibility of the commitButton.
	 * 
	 * @param visible
	 *            boolean
	 */
	protected void setCommitButtonVisible(boolean visible) {
		commitButton.setVisible(visible);
	}
}
