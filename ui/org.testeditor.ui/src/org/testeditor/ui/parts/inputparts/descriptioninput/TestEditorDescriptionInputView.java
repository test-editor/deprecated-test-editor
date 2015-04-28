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
package org.testeditor.ui.parts.inputparts.descriptioninput;

import java.util.ArrayList;
import java.util.Scanner;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.testeditor.core.model.teststructure.TestCase;
import org.testeditor.core.model.teststructure.TestComponent;
import org.testeditor.core.model.teststructure.TestDescriptionTestCase;
import org.testeditor.core.model.teststructure.TestDescriptionTestScenario;
import org.testeditor.core.model.teststructure.TestFlow;
import org.testeditor.core.model.teststructure.TestScenario;
import org.testeditor.ui.constants.CustomWidgetIdConstants;
import org.testeditor.ui.constants.TestEditorEventConstants;
import org.testeditor.ui.parts.editor.view.handler.TestEditorInputObject;
import org.testeditor.ui.parts.inputparts.TestEditorInputView;

/**
 * Description Area is the UI of a TestCase Description used in the Testeditor.
 * 
 * 
 */
public class TestEditorDescriptionInputView extends TestEditorInputView {

	private StyledText descriptionText;
	private int selectedLine;
	private int releasedLine;
	private ScrolledComposite sc;
	private Composite subContainer;

	/**
	 * whit this method the ui is created.
	 * 
	 * @param compositeContent
	 *            {@link Composite}
	 * @wbp.parser.entryPoint
	 */
	@Override
	public void createUI(Composite compositeContent) {
		if (compositeContent != null) {
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

			createDescriptionInputArea(getEditComposite());
			descriptionText.setVisible(false);
			setCommitButtonVisible(false);
			compositeContent.layout();
			getEditComposite().layout();
			getEditComposite().setVisible(false);
		}
	}

	/**
	 * creates the input text-area for the description.
	 * 
	 * @param parent
	 *            {@link Composite}
	 */
	public void createDescriptionInputArea(Composite parent) {

		descriptionText = new StyledText(parent, SWT.WRAP | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);

		descriptionText.setData(CustomWidgetIdConstants.TEST_EDITOR_WIDGET_ID_SWT_BOT_KEY,
				CustomWidgetIdConstants.CREATE_DESCRIPTION_TEXT);

		GridData gridData = new GridData(GridData.FILL_BOTH);

		descriptionText.setLayoutData(gridData);
		descriptionText.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				if (!((e.stateMask & SWT.ALT) == SWT.ALT)) {
					if ((e.keyCode == 13) || e.keyCode == 16777296) {
						changeInputInView();
						cleanInput();
						setAddMode(true);
					}

					else if ((e.stateMask & SWT.CTRL) == SWT.CTRL || (e.stateMask & SWT.COMMAND) == SWT.COMMAND) {
						// CTRL (Windows + Linux ) COMMAND (APPLE)?
						if (e.keyCode == 'a') {
							descriptionText.selectAll();
						}
					}
				}
			}
		});
		createCommitButtons(parent);

		getCommitButton().setData(CustomWidgetIdConstants.TEST_EDITOR_WIDGET_ID_SWT_BOT_KEY,
				CustomWidgetIdConstants.ADD_NEW_DESCRIPTION);

		addSelectionListernToCommitButtons();

		addValidateDescriptionText();

	}

	/**
	 * adds the Validation of the commitButton for the descriptionText.
	 */
	private void addValidateDescriptionText() {
		descriptionText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				if (descriptionText.getText().isEmpty()) {
					setEnabledCommitButtons(false);
				} else {
					setEnabledCommitButtons(true);
				}
			}
		});
	}

	/**
	 * sets the description active.
	 */
	protected void setDescriptionActive() {
		// the actionInputLine should be clean up

		getEditComposite().layout();
		getEditComposite().setVisible(true);
		getEditComposite().setEnabled(true);
		descriptionText.setVisible(true);
		descriptionText.setEditable(true);
		setCommitButtonVisible(true);
		setCommitToDefaultButton();
		getEditComposite().redraw();
	}

	/**
	 * sets a description to the input area.
	 * 
	 * @param description
	 *            text of the description
	 * @param lineNumber
	 *            number of the line
	 * @param releasedLine
	 *            the line-number of the end of the selection
	 * @param cursorPosInLine
	 *            position of the cursor.
	 * 
	 */
	public void setDescriptonTextToChangeable(String description, int lineNumber, int releasedLine, int cursorPosInLine) {
		descriptionText.setText(description);

		setSelectedLine(lineNumber);
		setReleasedLine(releasedLine);
		descriptionText.setCaretOffset(cursorPosInLine);
		getEditComposite().layout(true);
		getEditComposite().getParent().layout(true);
		descriptionText.setFocus();

	}

	/**
	 * this method changes a description in the test case.
	 */
	protected void setDescriptionLineInTestCase() {
		String inputText = descriptionText.getText();
		String lineSep = System.getProperty("line.separator");
		ArrayList<String> newLines = new ArrayList<String>();

		if (inputText.contains(lineSep)) {
			@SuppressWarnings("resource")
			Scanner scanner = new Scanner(inputText).useDelimiter(lineSep);
			while (scanner.hasNext()) {

				newLines.add(scanner.next());
			}
		} else {
			newLines.add(inputText);
		}
		getTestCaseController().setDescription(selectedLine, newLines, false);
		refreshStyledText();

		int newSelectedLine = getTestCaseController().getChangePosition(getAddMode());
		if (newSelectedLine > selectedLine) {
			selectedLine = newSelectedLine;
		}

		markSelectedLineInView(selectedLine + newLines.size() - 1);
	}

	/**
	 * this method add (addMode) or changes a description in the test case.
	 */
	@Override
	public void changeInputInView() {
		if (!getAddMode()) {
			getTestCaseController().removeLines(selectedLine, releasedLine);
		}
		selectedLine = getTestCaseController().getChangePosition(getAddMode());
		setDescriptionLineInTestCase();
		setAddMode(true);
	}

	/**
	 * cleans the text of the description-line.
	 */
	@Override
	public void cleanInput() {
		if (!getEditComposite().isDisposed()) {
			descriptionText.setText("");
		}
	}

	/**
	 * sets the releasedLine.
	 * 
	 * @param lineNo
	 *            int number of the line
	 */

	protected void setReleasedLine(int lineNo) {
		releasedLine = lineNo;

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
		if (!descriptionText.isDisposed()) {
			descriptionText.setVisible(false);
		}
	}

	/**
	 * enables the views.
	 */
	@Override
	public void enableViews() {
		super.enableViews();
		if (!descriptionText.isDisposed()) {
			descriptionText.setVisible(true);
		}
	}

	/**
	 * collects the actual input and sends it via event to the actual
	 * TestFlowController to cache the input temporary.
	 *
	 * 
	 * @param eventBroker
	 *            the EventBroker
	 */
	// TODO Check why is the eventBroker a param and not injected.
	public void cacheInput(IEventBroker eventBroker) {
		if (descriptionText.isDisposed() || descriptionText.getText().isEmpty()) {
			return;
		}
		TestComponent testComponent = null;
		TestFlow testFlow = null;
		if (getTestCaseController() != null) {
			testFlow = getTestCaseController().getTestFlow();
			if (testFlow != null) {
				if (testFlow instanceof TestCase) {
					testComponent = new TestDescriptionTestCase(descriptionText.getText());
				} else if (testFlow instanceof TestScenario) {
					testComponent = new TestDescriptionTestScenario(descriptionText.getText());

				}
				if (testComponent != null) {
					int selectedLine = getTestCaseController().getChangePosition(getAddMode());
					int cursorPos = descriptionText.getSelection().x;
					eventBroker.post(TestEditorEventConstants.CACHE_TEST_COMPONENT_TEMPORARY,
							new TestEditorInputObject(testFlow, testComponent, selectedLine, cursorPos, getAddMode()));
				}
			}
		}
	}
}