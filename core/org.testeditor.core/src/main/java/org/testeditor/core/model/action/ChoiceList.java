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
 * this object contains choices for a input-value. i.e. for the payment-method
 * 
 * @author llipinski
 */
public class ChoiceList {
	private List<String> choises = new ArrayList<String>();
	private String argumentId = "";

	/**
	 * copy-constructor.
	 * 
	 * @param choises
	 *            list of strings
	 * @param argumentId
	 *            the argumentId.
	 */
	public ChoiceList(List<String> choises, String argumentId) {
		this.choises = choises;
		this.argumentId = argumentId;
	}

	/**
	 * 
	 * @return the choices.
	 */
	public List<String> getChoices() {
		return choises;
	}

	/**
	 * 
	 * @return the argumentId to connect to the technicalBinding.
	 */
	public String getArgumentId() {
		return argumentId;
	}

}
