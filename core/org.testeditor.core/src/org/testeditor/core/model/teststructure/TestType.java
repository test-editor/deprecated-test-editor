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
 * Enumeration for type of test.
 * 
 */
public enum TestType {

	SUITE("suite"), TEST("test"), TESTPROJECT("testproject"), TESTSCENARIO("scenario"), SCENARIOSUITE("sceanriosuite");

	private String name;

	/**
	 * 
	 * @param name
	 *            the name
	 */
	private TestType(String name) {

		this.name = name;
	}

	/**
	 * 
	 * @return name
	 */
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return this.name;
	}
}
