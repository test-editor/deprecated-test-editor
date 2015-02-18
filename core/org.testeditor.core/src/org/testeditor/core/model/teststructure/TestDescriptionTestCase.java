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
 * specialized class of the {@link TestDescription} with special getSourceCode()
 * - method for the {@link TestCase} .
 * 
 * @author llipinski
 */
public class TestDescriptionTestCase extends TestDescription {
	/**
	 * constructor.
	 */
	public TestDescriptionTestCase() {
		super();
	}

	/**
	 * constructor.
	 * 
	 * @param string
	 *            String
	 */
	public TestDescriptionTestCase(String string) {
		super(string);
	}

	@Override
	public String getSourceCode() {
		return getDescription();
	}

	@Override
	public String toString() {
		return getDescription();
	}
}
