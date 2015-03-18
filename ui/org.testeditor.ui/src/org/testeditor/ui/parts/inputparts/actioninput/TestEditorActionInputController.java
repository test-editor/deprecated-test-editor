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
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.testeditor.core.model.action.AbstractAction;
import org.testeditor.core.model.action.Action;
import org.testeditor.core.model.action.ActionElement;
import org.testeditor.core.model.action.ActionElementType;
import org.testeditor.core.model.action.ActionGroup;
import org.testeditor.core.model.action.Argument;
import org.testeditor.core.model.action.ChoiceList;
import org.testeditor.core.model.action.IAction;
import org.testeditor.core.model.action.TechnicalBindingType;
import org.testeditor.core.model.action.UnparsedActionLine;
import org.testeditor.core.model.teststructure.TestActionGroup;
import org.testeditor.core.model.teststructure.TestFlow;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.services.interfaces.ActionGroupService;
import org.testeditor.ui.constants.TestEditorEventConstants;
import org.testeditor.ui.parts.editor.ITestEditorController;
import org.testeditor.ui.parts.editor.view.TestEditorController;
import org.testeditor.ui.parts.editor.view.handler.TestEditorInputObject;
import org.testeditor.ui.parts.inputparts.AbstractTestEditorInputPartController;
import org.testeditor.ui.parts.inputparts.dialogelements.TECombo;

/**
 * 
 * this class is the controller for the edit view part.
 * 
 */
public class TestEditorActionInputController extends AbstractTestEditorInputPartController {

	public static final String ID = "org.testeditor.ui.partdescriptor.testStructureEditor";
	private static final Logger LOGGER = Logger.getLogger(TestEditorActionInputController.class);
	@Inject
	private IEclipseContext context;
	@Inject
	private ActionGroupService actionGroupService;

	private TestEditorActionInputView editArea;

	private String lastSelectedMask = "";

	private SelectionListener comboboxActionSelectionListener;

	/**
	 * this method is called when this part gets the focus. This method is
	 * necessary to handle the CTRL+Enter-Event. Don't delete the empty method.
	 */
	@Focus
	public void onFocus() {
		editArea.setCommitToDefaultButton();
	}

	/**
	 * 
	 * @param parent
	 *            composite
	 */
	@PostConstruct
	public void createControls(Composite parent) {
		editArea = ContextInjectionFactory.make(TestEditorActionInputView.class, context);
		editArea.createUI(parent);

		editArea.getComboActionGroup().addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				String actionGroupName = editArea.getComboActionGroup().getText();
				createComboboxActionSelection(actionGroupName);
			}
		});
	}

	/**
	 * this method puts the names of the actiongroups in the combobox.
	 */
	private void putActionGroupNamesInCombobox() {
		editArea.getComboActionGroup().clearSelection();
		editArea.getComboActionGroup().removeAll();
		editArea.cleanActionCombobox();
		TestStructure testCase = editArea.getTestCaseController().getTestFlow();
		if (editArea.getTestCaseController() != null && editArea.getTestCaseController().getTestFlow() != null
				&& testCase.getRootElement().getTestProjectConfig().getLibraryLoadingStatus().isErrorLessLoaded()) {
			for (ActionGroup actionGroup : actionGroupService.getActionGroups(testCase.getRootElement())) {
				editArea.getComboActionGroup().add(actionGroup.getName());
			}
			editArea.getComboActionGroup().enableContentProposal();
		}
	}

	/**
	 * creates the combo for the selection of the action.
	 * 
	 * @param actionGroupName
	 *            name of the actionGroup
	 */
	void createComboboxActionSelection(String actionGroupName) {
		if (comboboxActionSelectionListener == null) {
			addSelectionListenerToActionCombobox();
		}
		ArrayList<String> actions = new ArrayList<String>();
		if (editArea != null && editArea.getTestCaseController() != null
				&& editArea.getTestCaseController().getTestFlow() != null) {
			for (TechnicalBindingType action : actionGroupService.getTechnicalBindingTypes(editArea
					.getTestCaseController().getTestFlow().getRootElement(), actionGroupName)) {
				actions.add(action.getShortName());
			}

			for (int i = 0; i < actions.size(); i++) {
				for (int j = 0; j < actions.size(); j++) {
					if (i != j && actions.get(j).equals(actions.get(i))) {
						actions.remove(j);
					}
				}
			}
			editArea.cleanInput();
			editArea.addComboboxAction(actions);
		}
	}

	/**
	 * add a selectionListener to the actionCombobox.
	 */
	private void addSelectionListenerToActionCombobox() {
		comboboxActionSelectionListener = new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				String action = editArea.getComboActions().getText();
				String maske = editArea.getComboActionGroup().getText();
				createActionLineInputArea(maske, action);

			}
		};
		editArea.getComboActions().addSelectionListener(comboboxActionSelectionListener);
	}

	/**
	 * creates the input area for a action line.
	 * 
	 * @param technicalBindingType
	 *            name of the {@link TechnicalBindingType}
	 */
	public void createActionLineInputArea(String technicalBindingType) {
		String maske = editArea.getComboActionGroup().getText();
		createActionLineInputArea(maske, technicalBindingType);
	}

	/**
	 * creates the input area for a action line.
	 * 
	 * @param maske
	 *            name of the {@link ActionGroup}
	 * @param technicalBindingType
	 *            name of the {@link TechnicalBindingType}
	 */
	private void createActionLineInputArea(String maske, String technicalBindingType) {
		ActionGroup actionGroup = actionGroupService.getActionGroup(editArea.getTestCaseController().getTestFlow()
				.getRootElement(), maske);
		LinkedList<ArrayList<String>> parameterList = new LinkedList<ArrayList<String>>();
		ArrayList<String> keys = new ArrayList<String>();
		Map<String, Argument> arguments = new HashMap<String, Argument>();
		if (actionGroup == null) {
			return;
		}
		List<AbstractAction> actions = actionGroup.getActions();
		int keyPosition = -1;
		TechnicalBindingType technicalBinding = actionGroupService.getTechnicalBindingByName(editArea
				.getTestCaseController().getTestFlow().getRootElement(), actionGroup.getName(), technicalBindingType);
		if (technicalBinding == null || technicalBinding.getActionParts() == null) {
			return;
		}
		int argPosOfKey = getArgPosOfKey(technicalBinding);
		List<Integer> argPosChoices = technicalBinding.getArgPosChoices();
		for (AbstractAction action : actions) {
			if (action.getTechnicalBindingType().getShortName().equalsIgnoreCase(technicalBindingType)) {

				if (argPosOfKey < action.getArguments().size()) {
					keys.add(action.getArguments().get(argPosOfKey).getValue());
					arguments.put(action.getArguments().get(argPosOfKey).getValue(),
							action.getArguments().get(argPosOfKey));
				}
				List<ActionElement> actionElements = action.getTechnicalBindingType().getActionParts();
				Collections.sort(actionElements);
				keyPosition = createElementsOfActionLineInput(parameterList, keyPosition, actionElements);
			}
		}
		if (keyPosition >= 0) {
			parameterList.set(keyPosition, keys);
		}
		editArea.cleanInput();
		addActionLineWidgetElements(parameterList, arguments, keyPosition, argPosChoices);
		editArea.setEditUnparsedAction(false);
		editArea.showActionLineInputComposite();
		editArea.setEnabledCommitButtons();

	}

	/**
	 * adds the actionLine-widget-elements.
	 * 
	 * @param parameterList
	 *            LinkedList<ArrayList<String>>
	 * @param arguments
	 *            Map<String, Argument>
	 * @param keyPosition
	 *            position of the actionKey or name
	 * @param argPosChoices
	 *            List<Integer>
	 */
	protected void addActionLineWidgetElements(LinkedList<ArrayList<String>> parameterList,
			Map<String, Argument> arguments, int keyPosition, List<Integer> argPosChoices) {
		int i = 0;
		int cells = parameterList.size();
		editArea.setColumsCount(cells + 1);
		boolean onlyOneActionName = false;
		TECombo actionNameCombobox = null;
		for (ArrayList<String> params : parameterList) {
			if (i == keyPosition) {
				actionNameCombobox = editArea.addActionElementCombobox(params, arguments, i);
				actionNameCombobox.setContainsActionNames(true);
				if (params.size() == 1) {
					onlyOneActionName = true;
				}
				addEventForChangeInCombobox(actionNameCombobox);
			} else if (params.isEmpty()) {
				addInputTextOrChoiceList(argPosChoices, i);
			} else {
				editArea.addActionElementOutputText(params.get(0), i);
			}
			i++;
		}
		if (onlyOneActionName && actionNameCombobox != null && actionNameCombobox.getSelectionIndex() > -1) {
			actionKeySelectionModified(actionNameCombobox);
		}
	}

	/**
	 * adds the Validation of the commitButtonAction for the
	 * ActionElementCombobox.
	 * 
	 * @param actionNameCombobox
	 *            ActionLineInputCombo
	 */
	private void addEventForChangeInCombobox(TECombo actionNameCombobox) {
		final TECombo lcActionElement = actionNameCombobox;
		actionNameCombobox.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {

				actionKeySelectionModified(lcActionElement);

			}
		});
	}

	/**
	 * adds an inoutText or a choiceList.
	 * 
	 * @param argPosChoices
	 *            List<Integer> argPosChoices positions of the ChoiceLists
	 * @param i
	 *            position
	 */
	private void addInputTextOrChoiceList(List<Integer> argPosChoices, int i) {
		boolean choiceListAdded = false;
		for (Integer choiceListPos : argPosChoices) {
			if (choiceListPos - 1 == i) {
				editArea.addEmptyActionElementCombobox(i);
				choiceListAdded = true;
			}
		}
		if (!choiceListAdded) {
			editArea.addActionElementInputText(i);
		}
	}

	/**
	 * get the position of the argument.
	 * 
	 * @param technicalBindingType
	 *            {@link TechnicalBindingType}
	 * @return int the position of the argument
	 */
	private int getArgPosOfKey(TechnicalBindingType technicalBindingType) {
		int argPos = 0;
		if (technicalBindingType == null) {
			return -1;
		}
		for (ActionElement actionElement : technicalBindingType.getActionParts()) {
			if (actionElement.getType() == ActionElementType.ACTION_NAME
					|| actionElement.getType() == ActionElementType.ARGUMENT) {
				if (actionElement.getType() == ActionElementType.ACTION_NAME) {
					return argPos;
				}
				argPos++;
			}
		}

		return 0;
	}

	/**
	 * creates the actionInputElements.
	 * 
	 * @param parameterList
	 *            ParamListe
	 * @param keyPosition
	 *            position of the key
	 * 
	 * @param actionElements
	 *            actionElements
	 * @return int the position of the key
	 */
	private int createElementsOfActionLineInput(LinkedList<ArrayList<String>> parameterList, int keyPosition,
			List<ActionElement> actionElements) {
		for (ActionElement actionElement : actionElements) {
			ArrayList<String> params;
			if (parameterList.size() < actionElement.getPosition()
					|| parameterList.get(actionElement.getPosition() - 1) == null) {
				params = new ArrayList<String>();
				parameterList.add(actionElement.getPosition() - 1, params);
			} else {
				params = parameterList.get(actionElement.getPosition() - 1);
			}
			if (actionElement.getType().equals(ActionElementType.TEXT)) {
				params.add(actionElement.getValue());
			} else if (actionElement.getType().equals(ActionElementType.ACTION_NAME)) {
				keyPosition = actionElement.getPosition() - 1;
			}
		}
		return keyPosition;
	}

	/**
	 * sets the action to the edit area.
	 * 
	 * @param lineNumber
	 *            number of the selected line
	 * @param texts
	 *            the different parts of the text in the selected line
	 * @param testActionGr
	 *            the {@link TestActionGroup }
	 * @param cursorPosInLine
	 *            the position of the cursor in the line
	 */
	public void setActionToEditArea(int lineNumber, ArrayList<String> texts, TestActionGroup testActionGr,
			int cursorPosInLine) {
		setAddMode(false);
		// chose the actionGroup
		editArea.setSelectedLine(lineNumber);

		String testActionGroupName = testActionGr.getActionGroupName();
		int choseItem = -1;
		TECombo comboActionGroup = editArea.getComboActionGroup();
		if (comboActionGroup.getItemCount() > 0) {
			for (int i = 0; i < comboActionGroup.getItemCount(); i++) {
				if (testActionGroupName.equalsIgnoreCase(comboActionGroup.getItem(i).toString())) {
					choseItem = i;
				}
			}
			if (choseItem == -1) {
				choseItem = 0;
				testActionGroupName = comboActionGroup.getItem(choseItem).toString();
			}
			comboActionGroup.select(choseItem);
			createComboboxActionSelection(testActionGroupName);

			// chose the action
			for (IAction action : testActionGr.getActionLines()) {
				if (!(action instanceof UnparsedActionLine)) {
					String actionName = action.getTechnicalBindingType().getShortName();
					int choseActionItem = -1;
					TECombo comboActions = editArea.getComboActions();
					for (int i = 0; i < comboActions.getItemCount(); i++) {
						if (actionName.equalsIgnoreCase(comboActions.getItem(i).toString())) {
							choseActionItem = i;
						}
					}
					if (choseActionItem == -1) {
						choseActionItem = 0;
						actionName = comboActions.getItem(choseActionItem).toString();
					}
					comboActions.select(choseActionItem);
					createActionLineInputArea(testActionGroupName, actionName);
					editArea.setValuesInActionLine(texts, cursorPosInLine);
				} else {
					editArea.setUnparsedActionLineToEdit(texts);
				}

			}
		}
	}

	/**
	 * this method is called, when the TestCaseView-part is changed.
	 * 
	 * @param testEditorController
	 *            TestEditorController
	 */
	public void setTestCaseController(ITestEditorController testEditorController) {
		if (testEditorController == null) {
			editArea.cleanActionCombobox();
			editArea.cleanActionGroupCombobox();
		} else {
			LOGGER.trace("Drop and rebuild masks and steps of Teststep view");
			cleanViewsSynchron();
			editArea.setTestEditorController(testEditorController);
			putActionGroupNamesInCombobox();
			editArea.enableViews();
		}
		editArea.enableViews();
		editArea.setTestCaseControler(testEditorController);

	}

	/**
	 * adds the input, in the input area, to the test case.
	 */
	public void addInputLine() {
		editArea.changeInputInView();
	}

	/**
	 * set the action input active.
	 */
	public void setActionActive() {
		editArea.cleanActionComboboxSelection();
		if (!lastSelectedMask.equalsIgnoreCase("")) {
			int maskToSelect = editArea.getComboActionGroup().indexOf(lastSelectedMask);
			if (maskToSelect == -1) {
				maskToSelect = 0;
			}
			editArea.getComboActionGroup().select(maskToSelect);
			createComboboxActionSelection(editArea.getComboActionGroup().getText());
			editArea.setActionSelectionVisible(true);
		} else {
			editArea.cleanActionCombobox();
			editArea.setActionSelectionVisible(false);
		}
		editArea.cleanInput();
		editArea.setActionActive();
	}

	/**
	 * Set the Last used Mask. Which will be selected by the next new Action.
	 * 
	 * @param lastSelectedMask
	 *            The Name of the Mask
	 */
	public void setLastSelectedMask(String lastSelectedMask) {
		this.lastSelectedMask = lastSelectedMask;
	}

	/**
	 * set the add mode in the editArea.
	 * 
	 * @param b
	 *            boolean
	 */
	public void setAddMode(boolean b) {
		editArea.setAddMode(b);

	}

	/**
	 * clean the ActionInputArea and close them.
	 */
	@Override
	public void cleanViewsSynchron() {
		cleanViews();
	}

	/**
	 * cleans the views.
	 */
	private void cleanViews() {
		if (editArea.getActionTextsFromInputLine().size() > 0) {
			editArea.cleanActionComboboxSelection();
			editArea.cleanInput();
			editArea.setAddMode(true);
		}
	}

	/**
	 * set the parameter enable-close-after-commit.
	 * 
	 * @param status
	 *            boolean
	 */
	public void setPopupmode(boolean status) {
		editArea.setPopupmode(status);
	}

	/**
	 * reload the names of the actions in the action-combobox.
	 */
	public void reloadActionCombobox() {
		putActionGroupNamesInCombobox();
	}

	/**
	 * cleans the stored value lastSelectedMask.
	 */
	public void cleanLastMaskValue() {
		lastSelectedMask = "";

	}

	/**
	 * disables the views.
	 */
	@Override
	public void disableViews() {
		editArea.disabelViews();

	}

	/**
	 * removes the testEditorController, if its equal to the stored in this
	 * object.
	 * 
	 * @param testEditorController
	 *            ITestEditorController
	 */
	public void removeTestCaseController(TestEditorController testEditorController) {
		editArea.removeTestEditorController(testEditorController);
	}

	/**
	 * this method fills the comboboxes for the choices i.e. payment-method
	 * after the actionKeyCombobox is selected.
	 * 
	 * @param teCombo
	 *            ActionLineInputCombo
	 */
	public void actionKeySelectionModified(TECombo teCombo) {

		if (teCombo.getSelectionIndex() == -1) {
			return;
		}

		String actionKey = teCombo.getItem(teCombo.getSelectionIndex());
		String maske = editArea.getComboActionGroup().getInputText();
		String technichalBindingChoose = editArea.getComboActions().getInputText();
		ActionGroup actionGroup = actionGroupService.getActionGroup(editArea.getTestCaseController().getTestFlow()
				.getRootElement(), maske);

		List<AbstractAction> actions = actionGroup.getActions();
		for (IAction action : actions) {
			if (action instanceof Action && ((Action) action).getActionName().equalsIgnoreCase(actionKey)
					&& action.getTechnicalBindingType().getShortName().equalsIgnoreCase(technichalBindingChoose)) {
				for (Integer choicePos : action.getTechnicalBindingType().getArgPosChoices()) {
					for (ChoiceList choiceList : ((Action) action).getChoiceLists()) {
						if (choiceList.getArgumentId()
								.equalsIgnoreCase(
										action.getTechnicalBindingType().getActionParts().get(choicePos.intValue() - 1)
												.getId())) {
							String locator = action.getArguments().get(0).getLocator();
							editArea.setChoiceListWidget(choicePos, choiceList.getChoices(), locator);
						}
					}
				}
			}
		}
	}

	@Override
	public void cacheInput(@UIEventTopic(TestEditorEventConstants.CACHE_TEST_COMPONENT_OF_PART_TEMPORARY) Object obj) {
		if (editArea != null) {
			ArrayList<String> inputTexts = editArea.getActionTextsFromInputLine();
			ArrayList<Argument> arguments = editArea.getActionArgumentsFromInputLine();

			String mask = editArea.getComboActionGroup().getText();

			if (editArea.getTestCaseController() != null) {
				int selectedLineInTestCase = editArea.getTestCaseController().getChangePosition(editArea.getAddMode());
				int cursorPosInLine = editArea.getCursorPos();
				TestFlow testFlow = editArea.getTestCaseController().getTestFlow();

				TestActionGroup testComponent = actionGroupService.createTestActionGroup(testFlow.getRootElement(),
						mask, inputTexts, arguments);
				getEventBroker().post(
						TestEditorEventConstants.CACHE_TEST_COMPONENT_TEMPORARY,
						new TestEditorInputObject(testFlow, testComponent, selectedLineInTestCase, cursorPosInLine,
								editArea.getAddMode()));
			}
		}
	}

	@Override
	public void removeTestEditorController() {
		editArea.setTestCaseControler(null);
	}

	@Override
	public void cleanViewsAsynchron() {
		cleanViews();
	}
}