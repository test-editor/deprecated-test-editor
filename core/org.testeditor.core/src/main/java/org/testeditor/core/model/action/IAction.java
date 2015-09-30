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
 * 
 * {@link IAction} is an abstract class.
 * 
 */
public interface IAction {

	/**
	 * 
	 * @return ArrayList of the textTypes
	 */
	ArrayList<TextType> getTextTypes();

	/**
	 * this method returns the texts of the action.
	 * 
	 * @return array of texts
	 */
	ArrayList<String> getTexts();

	/**
	 * this method returns the texts of the action.
	 * 
	 * @return array of texts
	 */
	List<String> getSourceCode();

	/**
	 * this method returns the technical-binding-type of the action.
	 * 
	 * @return TechnicalBindingtype
	 */
	TechnicalBindingType getTechnicalBindingType();

	/**
	 * set the arguments of the action.
	 * 
	 * @param arguments
	 *            arguments
	 */
	void setArguments(List<Argument> arguments);

	/**
	 * returns the arguments of the action.
	 * 
	 * @return array of arguments
	 */
	List<Argument> getArguments();

	/**
	 * return a list of parameters for a scenario.
	 * 
	 * @return a list of parameters.
	 */

	List<String> getParameters();
}
