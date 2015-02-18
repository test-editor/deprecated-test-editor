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
package org.testeditor.xmllibrary.utils;

import java.util.ArrayList;
import java.util.List;

import org.testeditor.core.exceptions.CorrruptLibraryException;
import org.testeditor.core.model.action.Action;
import org.testeditor.core.model.action.ActionElement;
import org.testeditor.core.model.action.ActionElementType;
import org.testeditor.core.model.action.Argument;
import org.testeditor.core.model.action.ChoiceList;
import org.testeditor.core.model.action.TechnicalBindingType;
import org.testeditor.core.services.interfaces.ObjectTreeConstructionException;
import org.testeditor.xmllibrary.domain.action.ActionName;
import org.testeditor.xmllibrary.domain.binding.ActionPart;
import org.testeditor.xmllibrary.domain.binding.TechnicalBindingTypes;

/**
 * Maps objects from the scanner bundle to the core bundle.
 */
public final class ActionGroupMapper {

	/**
	 * Maps technical binding types from an internal bundle object to the core
	 * object.
	 * 
	 * @param technicalBindingType
	 *            technical binding type (scanner bundle object)
	 * @return technical binding type (core bundle object)
	 */
	public static TechnicalBindingType mapTechnicalBindingType(
			org.testeditor.xmllibrary.domain.binding.TechnicalBindingType technicalBindingType) {
		ArrayList<ActionElement> actionElements = new ArrayList<ActionElement>();

		for (ActionPart actionPart : technicalBindingType.getActionPart()) {
			ActionElementType actionElementType = ActionElementType.valueOf(actionPart.getType().name());
			ActionElement actionElement = new ActionElement(actionPart.getPosition(), actionElementType,
					actionPart.getValue(), actionPart.getId());
			actionElements.add(actionElement);
		}

		return new TechnicalBindingType(technicalBindingType.getId(), technicalBindingType.getName(), actionElements,
				technicalBindingType.getSort());
	}

	/**
	 * Maps actions from an internal bundle object to the core object.
	 * 
	 * @param actionDomain
	 *            action (scanner bundle object)
	 * @param technicalBindingTypes
	 *            all available technical binding types
	 * @return action (core bundle object)
	 * @throws ObjectTreeConstructionException
	 *             if not all entries of the actions could map to the
	 *             technichalBindings.
	 * @throws CorrruptLibraryException
	 *             , if the library parts not matching.
	 */
	public static Action mapAction(org.testeditor.xmllibrary.domain.action.Action actionDomain,
			TechnicalBindingTypes technicalBindingTypes) throws ObjectTreeConstructionException,
			CorrruptLibraryException {
		List<Argument> arguments = new ArrayList<Argument>();

		ActionName actionNameDomain = actionDomain.getActionName();
		String actionName = "";
		if (actionNameDomain != null) {
			Argument argument = new Argument(actionNameDomain.getLocator(), actionNameDomain.getValue());
			arguments.add(argument);
			actionName = actionNameDomain.getValue();
		}
		ChoiceList choiceList;
		ArrayList<ChoiceList> choiceLists = new ArrayList<ChoiceList>();
		if (actionDomain.getArgument() != null) {
			choiceList = new ChoiceList(actionDomain.getArgument().getValue(), actionDomain.getArgument().getId());
			choiceLists.add(choiceList);
		}
		TechnicalBindingType technicalBindingType = getTechnicalBindingTypeByName(
				actionDomain.getTechnicalBindingType(), technicalBindingTypes);
		if (technicalBindingType == null) {
			throw new ObjectTreeConstructionException(actionDomain.getTechnicalBindingType());
		}
		return new Action(actionName, arguments, actionDomain.getSort(), technicalBindingType, choiceLists);
	}

	/**
	 * Returns the technical binding type for a given technical binding type
	 * name.
	 * 
	 * @param technicalBindingTypeName
	 *            the unique name of the binding stored at the action
	 * @param technicalBindingTypes
	 *            all available technical binding types
	 * @return TechnicalBindingType technical binding type
	 */
	private static TechnicalBindingType getTechnicalBindingTypeByName(String technicalBindingTypeName,
			TechnicalBindingTypes technicalBindingTypes) {
		for (org.testeditor.xmllibrary.domain.binding.TechnicalBindingType technicalBindingType : technicalBindingTypes
				.getTechnicalBindingType()) {
			if (technicalBindingType.getId().equalsIgnoreCase(technicalBindingTypeName)) {
				return ActionGroupMapper.mapTechnicalBindingType(technicalBindingType);
			}
		}
		return null;
	}

	/**
	 * Don't create objects of this utility class.
	 */
	private ActionGroupMapper() {
	}
}
