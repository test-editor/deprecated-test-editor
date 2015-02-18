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
 * 
 * This class contains the Parameters of the scenario for the DataDrivenTests.
 * 
 * @author llipinski
 */
public class TestScenarioParameters implements TestComponent {

	private ArrayList<String> texts = new ArrayList<String>();

	@Override
	public ArrayList<String> getTexts() {
		ArrayList<String> retVal = new ArrayList<String>();
		retVal.add(0, "Scenario Parameter: ");
		for (int i = 0; i < texts.size(); i++) {
			if (i == 0) {
				retVal.add(texts.get(i));
			} else {
				retVal.add(", " + texts.get(i));
			}
		}
		return retVal;
	}

	@Override
	public String getSourceCode() {
		StringBuilder sourcecode = new StringBuilder();

		if (texts.size() > 0) {
			sourcecode.append(texts.get(0));
		}
		for (int i = 1; i < texts.size(); i++) {
			sourcecode.append(", ");
			sourcecode.append(texts.get(i));
		}
		return sourcecode.toString();
	}

	@Override
	public ArrayList<TextType> getTextTypes() {
		ArrayList<TextType> textTypes = new ArrayList<TextType>();
		textTypes.add(TextType.DESCRIPTION);
		for (int i = 1; i <= texts.size(); i++) {
			textTypes.add(TextType.ARGUMENT);
		}
		return textTypes;
	}

	/**
	 * this method sets the texts.
	 * 
	 * @param testParameters
	 *            the texts
	 */
	public void setTexts(ArrayList<String> testParameters) {
		this.texts = testParameters;
	}

	/**
	 * gives the parameters.
	 * 
	 * @return the parameters
	 */
	protected ArrayList<String> getParameters() {
		return texts;
	}

}
