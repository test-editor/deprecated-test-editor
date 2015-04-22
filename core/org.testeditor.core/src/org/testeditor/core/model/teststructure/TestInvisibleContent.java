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
import java.util.List;

import org.testeditor.core.model.action.TextType;

/**
 * POJO for test invisible content as a part of a test case.
 */
public class TestInvisibleContent implements TestComponent {
	private String sourceCode;

	/**
	 * {@inheritDoc}
	 */

	public String getSourceCode() {
		return sourceCode;
	}

	/**
	 * Sets the source code in plain text.
	 * 
	 * @param sourceCode
	 *            source code
	 */
	public void setSourceCode(String sourceCode) {
		this.sourceCode = sourceCode;
	}

	@Override
	public List<String> getTexts() {
		ArrayList<String> array = new ArrayList<String>();
		return array;
	}

	@Override
	public List<TextType> getTextTypes() {
		ArrayList<TextType> array = new ArrayList<TextType>();
		return array;
	}
}
