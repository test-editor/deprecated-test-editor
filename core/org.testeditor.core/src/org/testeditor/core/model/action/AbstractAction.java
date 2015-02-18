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

/**
 * abstract class as parent-class of action and unparsedactionline.
 * 
 * @author llipinski
 * 
 */
public abstract class AbstractAction implements IAction, Comparable<AbstractAction> {

	@Override
	public int compareTo(AbstractAction o) {
		return 0;
	}

	@Override
	public abstract ArrayList<TextType> getTextTypes();

	@Override
	public abstract ArrayList<String> getTexts();

	@Override
	public abstract List<String> getSourceCode();

	@Override
	public abstract TechnicalBindingType getTechnicalBindingType();

	@Override
	public abstract void setArguments(List<Argument> arguments);

	@Override
	public abstract List<Argument> getArguments();

	/**
	 * gets the sorting.
	 * 
	 * @return the sorting
	 */
	public Integer getSorting() {
		return Integer.valueOf(0);
	}

	/**
	 * compares the names of this action whit the name of the compareAction.
	 * 
	 * @param compareAction
	 *            the compareAction
	 * @return ameOfThis.compareTo(compName);
	 */
	public int compareTheNames(AbstractAction compareAction) {
		return 0;
	}

	/**
	 * 
	 * @return the name of the Action as String.
	 */
	public String getNameOfAction() {
		return "";
	}
}
