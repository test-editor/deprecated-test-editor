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
package org.testeditor.core.model.teststructure;

import java.util.ArrayList;

import org.testeditor.core.model.action.TextType;

/**
 * POJO for test description as a part of a test case.
 */
public abstract class TestDescription implements TestComponent {

	private String description;

	/**
	 * default constructor.
	 */
	public TestDescription() {
		description = "";
	}

	/**
	 * constructor whit the description.
	 * 
	 * @param desc
	 *            description
	 */
	public TestDescription(String desc) {
		description = desc;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */

	public abstract String getSourceCode();

	/**
	 * Returns the description in plain text.
	 * 
	 * @return description description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description in plain text.
	 * 
	 * @param description
	 *            description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public ArrayList<String> getTexts() {
		ArrayList<String> array = new ArrayList<String>();
		array.add(description);
		return array;
	}

	@Override
	public ArrayList<TextType> getTextTypes() {
		ArrayList<TextType> array = new ArrayList<TextType>();
		array.add(TextType.DESCRIPTION);
		return array;
	}
}
