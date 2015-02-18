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

/**
 * 
 * This Object represants broken Teststructures. This are TestStructures which
 * can not be computed by the Test-Editor.
 * 
 * This Obejct is used to display the user an invalid Teststructure, to help him
 * to find the problem.
 * 
 */
public class BrokenTestStructure extends TestStructure {

	private String sourceCode;

	/**
	 * Used to set the unparsable source code.
	 * 
	 * @param sourceCode
	 *            which is not parseable.
	 */
	public void setSourceCode(String sourceCode) {
		this.sourceCode = sourceCode;
	}

	@Override
	public String getSourceCode() {
		return sourceCode;
	}

	@Override
	public String getPageType() {
		return "broken";
	}

	@Override
	public String getTypeName() {
		return "Broken Structure";
	}

}
