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
package org.testeditor.core.model.action;

import java.util.ArrayList;
import java.util.List;

import org.testeditor.core.exceptions.CorrruptLibraryException;

/**
 * An action is a suspected test step (e.g. 'type into the password field').
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * */
public class Action extends AbstractAction {
	private List<Argument> arguments;
	private TechnicalBindingType technicalBindingType;
	private Integer sorting;
	private List<ChoiceList> choiceLists;
	private String actionName = "";

	/**
	 * default constructor.
	 */
	public Action() {
		arguments = new ArrayList<Argument>();
		technicalBindingType = new TechnicalBindingType();
	}

	/**
	 * constructor with parameters.
	 * 
	 * @param actionName
	 *            the name of the action
	 * @param inArguments
	 *            arguments
	 * @param sorting
	 *            may be null
	 * @param inTechnicalBindingType
	 *            technicalBindingType
	 * @param choiceLists
	 *            List<ChoiceList>
	 * @throws CorrruptLibraryException
	 *             exception will be thrown if error in library was found
	 */
	public Action(String actionName, List<Argument> inArguments, Integer sorting,
			TechnicalBindingType inTechnicalBindingType, List<ChoiceList> choiceLists) throws CorrruptLibraryException {
		this.actionName = actionName;
		setSorting(sorting);
		arguments = new ArrayList<Argument>(inArguments);
		int argPos = 0;
		for (ActionElement actionElement : inTechnicalBindingType.getActionParts()) {
			if (actionElement.getType() == ActionElementType.ACTION_NAME && actionName.isEmpty()) {
				throw new CorrruptLibraryException(
						"Incomplete Defenition of Action. ActionName is not set. Action with TechicalBinding; "
								+ inTechnicalBindingType.getShortName());
			}
			if (actionElement.getType() == ActionElementType.ACTION_NAME
					|| actionElement.getType() == ActionElementType.ARGUMENT) {
				if (actionElement.getType() == ActionElementType.ARGUMENT && arguments.size() > argPos
						&& arguments.get(argPos) != null) {
					arguments.add(argPos, new Argument());
				}
				argPos++;
			}
		}
		technicalBindingType = inTechnicalBindingType;
		this.setChoiceLists(choiceLists);
	}

	/**
	 * constructor as a copy of an other object.
	 * 
	 * @param action
	 *            Action
	 */
	public Action(Action action) {
		arguments = new ArrayList<Argument>(action.getArguments());
		technicalBindingType = action.getTechnicalBindingType();
		this.choiceLists = action.getChoiceLists();
	}

	@Override
	public TechnicalBindingType getTechnicalBindingType() {
		return technicalBindingType;
	}

	/**
	 * setter for the technicalbindingType.
	 * 
	 * @param technicalBindingType
	 *            TechnicalBindingType
	 */
	public void setTechnicalBindingType(TechnicalBindingType technicalBindingType) {
		this.technicalBindingType = technicalBindingType;
	}

	@Override
	public List<Argument> getArguments() {
		return arguments;
	}

	@Override
	public void setArguments(List<Argument> arguments) {
		this.arguments = arguments;
	}

	/**
	 * returns an array of texts for the visualization.
	 * 
	 * @return ArrayList<String>
	 */
	@Override
	public ArrayList<String> getTexts() {
		ArrayList<String> texts = new ArrayList<String>();
		int i = 0; // Index of the position in argumentlist;
		for (ActionElement actionElement : technicalBindingType.getActionParts()) {
			if (actionElement.getType().equals(ActionElementType.TEXT)) {
				texts.add(actionElement.getValue() + " ");
			} else if (actionElement.getType().equals(ActionElementType.ACTION_NAME)) {
				texts.add(arguments.get(i).getValue() + " ");
				i++;
			} else if (actionElement.getType().equals(ActionElementType.ARGUMENT)) {
				if (arguments != null && arguments.size() > i && arguments.get(i) != null) {
					String argValue = arguments.get(i).getValue();
					if (isArgValueWithCurlyBrackets(argValue)) {
						texts.add("@" + argValue.substring(2, argValue.length() - 1) + " ");
					} else {
						texts.add(argValue + " ");
					}
				} else {
					texts.add(actionElement.getValue() + " ");
				}
				i++;
			}
		}
		return texts;
	}

	/**
	 * test the argValue.
	 * 
	 * @param argValue
	 *            String
	 * @return true, if it starts with "@{" and ends with "}"
	 */
	private boolean isArgValueWithCurlyBrackets(String argValue) {
		return argValue != null && !argValue.isEmpty() && argValue.startsWith("@{") && argValue.endsWith("}");
	}

	/**
	 * 
	 * 
	 * @return the sourcecode for the sourcecode for the testcase.
	 */
	@Override
	public List<String> getSourceCode() {
		ArrayList<String> texts = new ArrayList<String>();
		int i = 0; // Index of the position in argumentlist;
		for (ActionElement actionElement : technicalBindingType.getActionParts()) {
			if (actionElement.getType().equals(ActionElementType.TEXT)) {
				texts.add(actionElement.getValue() + " ");
			} else if (actionElement.getType().equals(ActionElementType.ACTION_NAME)) {
				texts.add(arguments.get(i).getLocator() + " ");
				i++;
			} else if (actionElement.getType().equals(ActionElementType.ARGUMENT)) {
				String value = arguments.get(i).getValue();
				if (value != null && !value.isEmpty() && value.startsWith("@") && !value.startsWith("@{")) {
					texts.add("@{" + value.substring(1) + "} ");
				} else {
					texts.add(value + " ");
				}
				i++;
			}
		}
		return texts;
	}

	/**
	 * returns an array of textTypes for the visualisation.
	 * 
	 * @return ArrayList<Text_Type>
	 * 
	 */

	@Override
	public ArrayList<TextType> getTextTypes() {
		ArrayList<TextType> types = new ArrayList<TextType>();
		for (ActionElement actionElement : technicalBindingType.getActionParts()) {
			if (actionElement.getType().equals(ActionElementType.TEXT)) {
				types.add(TextType.TEXT);
			} else if (actionElement.getType().equals(ActionElementType.ACTION_NAME)) {
				types.add(TextType.ACTION_NAME);
			} else if (actionElement.getType().equals(ActionElementType.ARGUMENT)) {
				types.add(TextType.ARGUMENT);
			}
		}
		return types;
	}

	@Override
	public Integer getSorting() {
		return sorting;
	}

	/**
	 * set the sorting.
	 * 
	 * @param sorting
	 *            Integer may be null
	 */
	public void setSorting(Integer sorting) {
		this.sorting = sorting;
	}

	@Override
	public int compareTo(AbstractAction compareAction) {
		// sorting by technicalbinding
		if (this.getTechnicalBindingType().compareTo(compareAction.getTechnicalBindingType()) == 0) {
			return sortingAction(compareAction);
		}
		return this.getTechnicalBindingType().compareTo(compareAction.getTechnicalBindingType());
	}

	/**
	 * this method sorts by the action.
	 * 
	 * @param compareAction
	 *            the comparable action
	 * @return the result of compareTo
	 */
	private int sortingAction(AbstractAction compareAction) {
		if (this.getSorting() == null && compareAction.getSorting() == null) {
			return compareTheNames(compareAction);
		} else if (this.getSorting() == null) {
			return 1;
		} else if (compareAction.getSorting() == null) {
			return -1;
		} else {
			int compareSorting = this.getSorting().compareTo(compareAction.getSorting());
			if (compareSorting != 0) {
				return compareSorting;
			} else {
				return compareTheNames(compareAction);
			}
		}
	}

	/**
	 * compares the names of this action whit the name of the compareAction.
	 * 
	 * @param compareAction
	 *            the compareAction
	 * @return ameOfThis.compareTo(compName);
	 */
	@Override
	public int compareTheNames(AbstractAction compareAction) {
		return getNameOfAction().compareTo(compareAction.getNameOfAction());
	}

	/**
	 * 
	 * @return the name of the Action as String.
	 */
	@Override
	public String getNameOfAction() {
		String nameOfAction = "";
		nameOfAction = getActionName();
		if (nameOfAction != "") {
			return nameOfAction;
		}
		int keyPos = 0;
		for (ActionElement element : getTechnicalBindingType().getActionParts()) {

			if (element.getType() == ActionElementType.ACTION_NAME && getArguments().size() > keyPos) {
				if (this.getArguments().get(keyPos) != null && this.getArguments().get(keyPos).getValue() != null) {
					nameOfAction = this.getArguments().get(keyPos).getValue();
				}
				break;
			}
			if (element.getType() == ActionElementType.ARGUMENT) {
				keyPos++;
			}
		}
		return nameOfAction;
	}

	/**
	 * 
	 * @return the choicelist.
	 */
	public List<ChoiceList> getChoiceLists() {
		return choiceLists;
	}

	/**
	 * sets the choice-list.
	 * 
	 * @param choiceLists
	 *            List<ChoiceList>
	 */
	public void setChoiceLists(List<ChoiceList> choiceLists) {
		this.choiceLists = choiceLists;
	}

	/**
	 * 
	 * @return the actionName.
	 */
	public String getActionName() {
		return actionName;
	}

	/**
	 * 
	 * @return a list of parameters.
	 */
	@Override
	public List<String> getParameters() {
		List<String> parameters = new ArrayList<String>();
		List<Argument> arguments = getArguments();
		for (Argument argument : arguments) {
			if (argument.getValue().startsWith("@")) {
				parameters.add(argument.getValue().substring(1));
			}
		}
		return parameters;
	}

}
