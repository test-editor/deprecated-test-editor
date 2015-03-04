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
package org.testeditor.ui.parts.inputparts.actioninput;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.testeditor.core.model.action.Argument;
import org.testeditor.ui.constants.ColorConstants;
import org.testeditor.ui.constants.CustomWidgetIdConstants;
import org.testeditor.ui.parts.editor.ITestEditorController;
import org.testeditor.ui.parts.inputparts.TestEditorInputView;
import org.testeditor.ui.parts.inputparts.dialogelements.TECombo;
import org.testeditor.ui.utilities.InputValidator;
import org.testeditor.ui.utilities.TestEditorTranslationService;

/**
 * 
 * this class represented the editor for the input and change of test-actions
 * and -descriptions.
 * 
 */
@Creatable
public class TestEditorActionInputView extends TestEditorInputView {

	@Inject
	private TestEditorTranslationService translationService;
	@Inject
	private IEventBroker eventBroker;

	private Composite actionLineInputComposite;
	private Composite actionCompositeEditArea;

	private TECombo comboActionGroup;
	private TECombo comboActions;
	private Label lblAction;

	private Color colorBlue;
	private Color colorDarkGreen;
	private Color colorYellow;

	private LinkedList<IActionLineInputWidget> actionLineWidgets;

	private GridLayout glContainerACLine;
	private int selectedLineInTestCase;

	private ScrolledComposite sc;
	private Composite subContainer;

	private boolean unparsedAction;

	/**
	 * with this method the ui is created.
	 * 
	 * @param compositeContent
	 *            {@link Composite}
	 * @wbp.parser.entryPoint
	 */
	@Override
	public void createUI(Composite compositeContent) {
		getSystemResources();
		compositeContent.setLayout(new FillLayout());

		// Create the ScrolledComposite to scroll horizontally and vertically
		sc = new ScrolledComposite(compositeContent, SWT.H_SCROLL | SWT.V_SCROLL);
		// Expand both horizontally and vertically
		sc.setExpandHorizontal(true);
		sc.setExpandVertical(true);

		subContainer = new Composite(sc, SWT.NONE);
		sc.setContent(subContainer);

		super.createUI(subContainer);

		actionLineWidgets = new LinkedList<IActionLineInputWidget>();
		createInputAreaAction(subContainer);
		compositeContent.layout(true);
	}

	/**
	 * creation of the colors.
	 * 
	 */
	protected void getSystemResources() {
		colorBlue = ColorConstants.COLOR_BLUE;
		colorDarkGreen = ColorConstants.COLOR_DARK_GREEN;
		colorYellow = ColorConstants.COLOR_YELLOW;
	}

	/**
	 * creation of the action input area.
	 * 
	 * @param editComposite
	 *            {@link Composite}
	 */
	private void createInputAreaAction(Composite editComposite) {
		// Input for an action
		editComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		actionCompositeEditArea = new Composite(editComposite, SWT.NORMAL);
		GridLayout gridLayoutEditArea = new GridLayout();
		GridData gd = new GridData(SWT.NORMAL, SWT.CENTER, true, false);
		actionCompositeEditArea.setLayoutData(gd);
		gridLayoutEditArea.numColumns = 1;
		actionCompositeEditArea.setLayout(gridLayoutEditArea);
		createActionInputArea(actionCompositeEditArea);
		editComposite.setVisible(false);

	}

	/**
	 * setter for the values in the action input line.
	 * 
	 * @param texts
	 *            array of {@link String}
	 * @param cursorPosInLine
	 *            position of the cursor in the line.
	 */
	public void setValuesInActionLine(ArrayList<String> texts, int cursorPosInLine) {
		unparsedAction = false;
		int totalTextLength = 0;
		boolean cursorIsSet = false;
		for (int i = 0; i < actionLineWidgets.size(); i++) {
			IActionLineInputWidget w = actionLineWidgets.get(i);
			String textForActWidged = texts.get(i);
			w.showText(textForActWidged);
			int lengthOfWidget = textForActWidged.length();
			if ((totalTextLength + lengthOfWidget >= cursorPosInLine || w.isInputField() && !anOtherInputFieldFolows(i))
					&& !cursorIsSet) {
				int posInWidget = cursorPosInLine - totalTextLength;
				cursorIsSet = w.setCursor(posInWidget);
				if (!cursorIsSet) {
					cursorPosInLine = totalTextLength + lengthOfWidget;
				}
				w.setFocus();

			}
			totalTextLength = totalTextLength + lengthOfWidget;
		}

		showActionLineInputComposite();
	}

	/**
	 * 
	 * @param actWidgetNo
	 *            number of the actual widget.
	 * @return true, if an other input-field is in the line, else false
	 */
	private boolean anOtherInputFieldFolows(int actWidgetNo) {
		for (int i = actWidgetNo + 1; i < actionLineWidgets.size(); i++) {
			if (actionLineWidgets.get(i).isInputField()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * creates the ActionInputArea.
	 * 
	 * @param parent
	 *            {@link Composite}
	 */
	private void createActionInputArea(Composite parent) {

		// dropdownbox for the actiongroups i.e masks

		Composite maskComposite = new Composite(actionCompositeEditArea, SWT.NORMAL);
		maskComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		GridLayout glContainer = new GridLayout(4, false);

		maskComposite.setLayout(glContainer);

		Label lblMaske = new Label(maskComposite, SWT.NONE);
		String messageMakse = translationService.translate("%TestEditView_Mask");
		lblMaske.setText(messageMakse);

		comboActionGroup = new TECombo(maskComposite, SWT.NONE, null, eventBroker);
		comboActionGroup.setData(CustomWidgetIdConstants.TEST_EDITOR_WIDGET_ID_SWT_BOT_KEY,
				CustomWidgetIdConstants.CHOSE_MASKE);
		comboActionGroup.setVisible(true);

		// SelectionListener should be set in the controller

		lblAction = new Label(maskComposite, SWT.NONE);
		String messageAktion = translationService.translate("%TestEditView_Action");
		lblAction.setText(messageAktion);
		lblAction.setVisible(false);

		comboActions = new TECombo(maskComposite, SWT.NONE, null, eventBroker);
		comboActions.setData(CustomWidgetIdConstants.TEST_EDITOR_WIDGET_ID_SWT_BOT_KEY,
				CustomWidgetIdConstants.CHOSE_ACTION);
		comboActions.setVisible(false); // later on the combobox is set to
		// visible
		// SelectionListener should be set in the controller

		actionLineInputComposite = new Composite(actionCompositeEditArea, SWT.NORMAL);
		actionLineInputComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));

		glContainerACLine = new GridLayout(0, false);
		glContainerACLine.horizontalSpacing = 0;
		actionLineInputComposite.setLayout(glContainerACLine);

		addActionCommitButton();
		addValidateComboActionGroup();
	}

	/**
	 * adds the Validation of the commitButtonAction for the
	 * ComboboxActionGroup.
	 */
	private void addValidateComboActionGroup() {
		setEnabledCommitButtons();
		comboActionGroup.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				setEnabledCommitButtons();
			}
		});
	}

	/**
	 * this method transfers the inputs of the action input in the TestCase.
	 * 
	 */
	@Override
	protected void changeInputInView() {
		if (unparsedAction) {
			changeUnparsedActionInTestCase();
		} else {

			ArrayList<String> inputTexts = getActionTextsFromInputLine();
			ArrayList<Argument> arguments = getActionArgumentsFromInputLine();

			selectedLineInTestCase = getTestCaseController().getChangePosition(getAddMode());

			String mask = comboActionGroup.getText();
			String actionName = comboActions.getText();
			getTestCaseController().setActionGroup(mask, actionName, inputTexts, arguments, selectedLineInTestCase,
					!getAddMode());
			setActionActive();
			refreshStyledText();
			int newSelectedLine = getTestCaseController().getChangePosition(getAddMode());
			if (newSelectedLine > selectedLineInTestCase) {
				selectedLineInTestCase = newSelectedLine;
			}

			markSelectedLineInView(selectedLineInTestCase);
			setAddMode(true);
		}
	}

	/**
	 * sets the action active.
	 */
	protected void setActionActive() {
		// the description should be cleanup
		if (!getEditComposite().isDisposed()) {
			getEditComposite().layout();
		}
		if (!getEditComposite().isDisposed()) {
			getEditComposite().layout();
			sc.layout();
			actionLineInputComposite.layout();
			getEditComposite().setVisible(true);
		}
		setCommitToDefaultButton();
	}

	/**
	 * this method gets the texts of the action-line-input-widgets.
	 * 
	 * 
	 * @return array {@link String}
	 */
	protected ArrayList<Argument> getActionArgumentsFromInputLine() {
		ArrayList<Argument> arguments = new ArrayList<Argument>();
		for (IActionLineInputWidget w : actionLineWidgets) {

			if (w.getArgument() != null) {
				arguments.add(w.getArgument());
			}
		}
		return arguments;
	}

	/**
	 * this method gets the inputs of the action-line-input-widgets.
	 * 
	 * @return array {@link String}
	 */
	protected ArrayList<String> getActionTextsFromInputLine() {
		ArrayList<String> inputTexts = new ArrayList<String>();
		for (IActionLineInputWidget w : actionLineWidgets) {
			if (!w.isDisposed() && w.getInputText() != null) {
				inputTexts.add(w.getInputText());
			}
		}

		return inputTexts;
	}

	/**
	 * this method returns the actionGroupCombobox.
	 * 
	 * @return ActionLineInputCombo the actionGroupCombobox
	 */
	protected TECombo getComboActionGroup() {
		return comboActionGroup;
	}

	/**
	 * this method adds the combo for the actions and puts the contents into the
	 * combobox.
	 * 
	 * @param actions
	 *            names of the actions
	 */

	public void addComboboxAction(ArrayList<String> actions) {
		comboActions.removeAll();
		for (String action : actions) {
			comboActions.add(action);
		}
		comboActions.enableContentProposal();
		lblAction.setVisible(true);
		comboActions.setVisible(true);
		actionLineInputComposite.setVisible(false);
		updateScrollBars();
		// SelectionListener should be set in the Controller

		addValidateComboboxAction();
	}

	/**
	 * adds the Validation of the commitButtonAction for the ComboboxAction.
	 */
	private void addValidateComboboxAction() {
		setEnabledCommitButtons();
		comboActions.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				setEnabledCommitButtons();
			}
		});
	}

	/**
	 * getter for the comboAction. So it can be filled whit values from outside
	 * 
	 * @return ActionLineInputCombo
	 */
	protected TECombo getComboActions() {
		return comboActions;
	}

	/**
	 * this method cleans the input-area for the action.
	 */
	@Override
	protected void cleanInput() {
		for (IActionLineInputWidget w : actionLineWidgets) {
			w.dispose();
		}
		actionLineWidgets.clear();
		setEnabledCommitButtons(false);
	}

	/**
	 * adds the actionElementCombobox.
	 * 
	 * @param params
	 *            the values in the combobox
	 * 
	 * @param position
	 *            position in the action-line
	 * @param arguments
	 *            Map<String, Argument>
	 * @return ActionLineInputCombo
	 */
	protected TECombo addActionElementCombobox(ArrayList<String> params, Map<String, Argument> arguments, int position) {

		TECombo actionElementKey = addEmptyActionElementCombobox(position);

		for (String param : params) {
			actionElementKey.add(arguments.get(param));
		}
		actionElementKey.enableContentProposal();
		if (actionElementKey.getItemCount() == 1) {
			actionElementKey.select(0);
		}

		// TODO is called a second time. First in addEmptyActionElementCombobox.
		// Is that correct?
		addValidActionElementCombobox(actionElementKey);
		return actionElementKey;
	}

	/**
	 * adds the actionElementCombobox.
	 * 
	 * 
	 * @param position
	 *            position in the action-line
	 * @return ActionLineInputCombo
	 * 
	 */
	protected TECombo addEmptyActionElementCombobox(int position) {

		TECombo actionElementKey = new TECombo(actionLineInputComposite, SWT.NONE, "", eventBroker);
		actionElementKey.setVisible(true);
		// set default color for combo boxes
		actionElementKey.setForeground(colorDarkGreen);
		actionElementKey.setData(CustomWidgetIdConstants.TEST_EDITOR_WIDGET_ID_SWT_BOT_KEY,
				CustomWidgetIdConstants.ACTION_COMBO + position);
		actionElementKey.setAllINvalidChars(getTestCaseController().getInvalidChars());
		actionLineWidgets.add(actionElementKey);

		addValidActionElementCombobox(actionElementKey);
		return actionElementKey;
	}

	/**
	 * adds the Validation of the commitButtonAction for the
	 * ActionElementCombobox.
	 * 
	 * @param actionElementKey
	 *            ActionLineInputCombo
	 */
	private void addValidActionElementCombobox(final TECombo actionElementKey) {
		setEnabledCommitButtons();
		actionElementKey.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {

				setEnabledCommitButtons();
			}
		});
	}

	/**
	 * this method validate the inputs to an action. If every input is done, it
	 * returns true.
	 * 
	 * @return boolean valid-input
	 */
	private boolean isActionLineInputValid() {
		boolean totalResult = true;
		// test the input of the mask
		totalResult = totalResult && comboActionGroup.isInputValid();
		// test the input of the action
		totalResult = totalResult && comboActions.isInputValid();
		// test the input of the actionLineWidgets
		if (actionLineWidgets.isEmpty()) {
			return false;
		} else {
			for (IActionLineInputWidget widget : actionLineWidgets) {
				totalResult = totalResult
						&& (widget.isInputValid() || getTestCaseController().isInputValid(widget.getText()) || InputValidator
								.isInputValidSystemProperty(widget.getText()))
						&& widgetContainsActionNamesWithValidValue(widget);
			}
		}
		return totalResult;
	}

	/**
	 * Returns true if Combo contains ACTION_NAMES and not starting with '@'.
	 * 
	 * @param widget
	 *            containing action names
	 * @return boolean
	 */
	private boolean widgetContainsActionNamesWithValidValue(IActionLineInputWidget widget) {
		if (widget instanceof TECombo) {
			if (((TECombo) widget).containsActionNames()) {
				if (widget.getText().startsWith("@")) {
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * this method adds a {@link ActionLineInputText}.
	 * 
	 * @param position
	 *            position in the action-line
	 */
	protected void addActionElementInputText(int position) {
		ActionLineInputText actionElementInputValue = new ActionLineInputText(actionLineInputComposite, SWT.NONE);
		actionElementInputValue.setForeground(colorBlue);
		actionElementInputValue.setText("");
		actionElementInputValue.setData(CustomWidgetIdConstants.TEST_EDITOR_WIDGET_ID_SWT_BOT_KEY,
				CustomWidgetIdConstants.ACTION_TEXT + position);
		actionElementInputValue.setAllINvalidChars(getTestCaseController().getInvalidChars());
		actionLineWidgets.add(actionElementInputValue);

		addValidateActionElementInputValue(actionElementInputValue);
	}

	/**
	 * adds the Validation of the commitButtonAction for the
	 * ActionElementInputValue.
	 * 
	 * @param actionElementInputValue
	 *            the text-input-widget
	 */
	private void addValidateActionElementInputValue(ActionLineInputText actionElementInputValue) {
		setEnabledCommitButtons();
		actionElementInputValue.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				setEnabledCommitButtons();
			}
		});
	}

	/**
	 * this method adds a {@link ActionLineInputLabel}.
	 * 
	 * @param string
	 *            the text of the label
	 * @param position
	 *            position in the action-line
	 */
	protected void addActionElementOutputText(String string, int position) {
		ActionLineInputLabel actionelementLabel = new ActionLineInputLabel(actionLineInputComposite, SWT.NONE);
		actionLineWidgets.add(actionelementLabel);
		actionelementLabel.setData(CustomWidgetIdConstants.TEST_EDITOR_WIDGET_ID_SWT_BOT_KEY,
				CustomWidgetIdConstants.ACTION_LABEL + position);
		actionelementLabel.setText(string);
	}

	/**
	 * this method shows the action-input-line.
	 */
	protected void showActionLineInputComposite() {
		actionLineInputComposite.layout(true);
		actionLineInputComposite.getParent().layout(true);
		actionLineInputComposite.getParent().getParent().layout(true);
		actionLineInputComposite.getParent().getParent().getParent().layout(true);
		actionLineInputComposite.getParent().getParent().getParent().getParent().layout(true);
		actionLineInputComposite.setVisible(true);
		actionLineInputComposite.getParent().setVisible(true);
		actionLineInputComposite.getParent().getParent().setVisible(true);
		actionLineInputComposite.getParent().getParent().getParent().setVisible(true);
		updateScrollBars();
	}

	/**
	 * Updates the Scrollbars of the Composite. If the size of a widget changes,
	 * this method should be called to layout the composite and update the
	 * scrollbars.
	 */
	protected void updateScrollBars() {
		subContainer.layout(true);
		sc.setMinSize(subContainer.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}

	/**
	 * this method sets the {@link ITestEditorController}.
	 * 
	 * @param editorController
	 *            TestEditorController
	 */
	public void setTestEditorController(ITestEditorController editorController) {
		setTestCaseControler(editorController);
	}

	/**
	 * sets the selected line.
	 * 
	 * @param lineNumber
	 *            int number of the line
	 */
	public void setSelectedLine(int lineNumber) {
		selectedLineInTestCase = lineNumber;
	}

	/**
	 * set an unparsed action line to the edit area.
	 * 
	 * @param texts
	 *            array of {@link String}
	 */
	public void setUnparsedActionLineToEdit(ArrayList<String> texts) {
		unparsedAction = true;
		StringBuilder output = new StringBuilder();
		for (String text : texts) {
			output = output.append(text);
		}
		ActionLineInputText actionElementInputValue = new ActionLineInputText(actionLineInputComposite, SWT.NONE);
		actionElementInputValue.setForeground(colorBlue);
		actionElementInputValue.setBackground(colorYellow);
		actionElementInputValue.setText(output.toString());
		actionLineWidgets.add(actionElementInputValue);
		setColumsCount(1);
		showActionLineInputComposite();

		setEnabledCommitButtons(true);
	}

	/**
	 * this method transfers the inputs of the unparsedAction in the TestCase.
	 * 
	 */
	protected void changeUnparsedActionInTestCase() {
		ArrayList<String> inputTexts = getActionTextsFromInputLine();

		String mask = comboActionGroup.getText();
		String actionName = comboActions.getText();
		// TODO hier den Text parsen und ggf. als Action einfügen.
		getTestCaseController().setUnparsedActionGroup(mask, actionName, inputTexts, selectedLineInTestCase);
		setActionActive();

		markSelectedLineInView(selectedLineInTestCase);
		refreshStyledText();
	}

	/**
	 * set the number of columns in the actionLineContainer.
	 * 
	 * @param columns
	 *            int
	 */
	protected void setColumsCount(int columns) {
		glContainerACLine.numColumns = columns;
	}

	/**
	 * this method adds a commitButton to the actionInputArea.
	 */
	protected void addActionCommitButton() {
		createCommitButtons(actionCompositeEditArea);

		getCommitButton().setData(CustomWidgetIdConstants.TEST_EDITOR_WIDGET_ID_SWT_BOT_KEY,
				CustomWidgetIdConstants.ADD_NEW_ACTION);

		addSelectionListernToCommitButtons();
	}

	/**
	 * sets the switch unparsedAction.
	 * 
	 * @param b
	 *            boolean
	 */
	protected void setEditUnparsedAction(boolean b) {
		unparsedAction = b;
	}

	/**
	 * cleans the ActionCombobox.
	 */
	public void cleanActionComboboxSelection() {
		ArrayList<String> actions = new ArrayList<String>();
		if (!comboActions.isDisposed()) {
			for (int i = 0; i < comboActions.getItemCount(); i++) {
				actions.add(comboActions.getItem(i));
			}
			addComboboxAction(actions);
		}

	}

	/**
	 * sets the enabled status at the commitButtons.
	 */
	protected void setEnabledCommitButtons() {
		setEnabledCommitButtons(isActionLineInputValid());
	}

	/**
	 * cleans the values in the comboActions.
	 */
	public void cleanActionCombobox() {
		comboActions.removeAll();
	}

	/**
	 * cleans the values in the comboActionGroup.
	 */
	public void cleanActionGroupCombobox() {
		comboActionGroup.removeAll();
	}

	/**
	 * sets the values into the choice-list.
	 * 
	 * @param choicePos
	 *            position in the action-line.
	 * @param choises
	 *            List<String>
	 * @param locator
	 *            String in the application.
	 */
	protected void setChoiceListWidget(Integer choicePos, List<String> choises, String locator) {
		int pos = 0;

		for (IActionLineInputWidget w : actionLineWidgets) {
			if (pos == choicePos - 1) {
				if (w instanceof TECombo) {
					TECombo widgetChoiceList = (TECombo) w;
					widgetChoiceList.removeAll();
					for (String value : choises) {
						widgetChoiceList.add(new Argument(locator, value));
					}
					widgetChoiceList.enableContentProposal();
					// Reset color in combo boxes for argument values
					widgetChoiceList.setForeground(colorBlue);
				}
				break;
			}
			pos++;
		}
	}

	/**
	 * 
	 * @return the actual position of the cursor in the inputLine
	 */
	protected int getCursorPos() {
		int cursorPos = 0;
		for (IActionLineInputWidget w : actionLineWidgets) {
			if (w.getCursorPos() != -1) {
				cursorPos = cursorPos + w.getCursorPos();
				break;
			}

			cursorPos = cursorPos + w.getInputText().length() + 1;
		}
		return cursorPos;
	}

	/**
	 * sets the visibility of the lblAction and comboActions.
	 * 
	 * @param visible
	 *            boolean
	 */
	public void setActionSelectionVisible(boolean visible) {
		lblAction.setVisible(visible);
		comboActions.setVisible(visible);

	}

}
