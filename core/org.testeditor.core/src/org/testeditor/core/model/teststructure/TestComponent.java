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

import java.util.List;

import org.testeditor.core.model.action.TextType;

/**
 * Abstract POJO for the part of a test case (e.g. test action group or test
 * description).
 */
public interface TestComponent {

	/**
	 * Returns the parts of the text of this test structure as it is used at the
	 * third party system.
	 * 
	 * @return source code
	 */
	List<String> getTexts();

	/**
	 * Returns the plain text of this test structure as it is used at the third
	 * party system.
	 * 
	 * 
	 * @return source code
	 */
	String getSourceCode();

	/**
	 * Returns a ArrayList of TextTypes.
	 * 
	 * @return ArrayList of texttyps
	 */
	List<TextType> getTextTypes();

}
