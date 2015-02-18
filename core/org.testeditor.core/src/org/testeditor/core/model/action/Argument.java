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

/**
 * An argument for a specific action.
 */
public class Argument {
	private String locator;
	private String value;

	/**
	 * Initializes an empty argument.
	 */
	public Argument() {
	}

	/**
	 * Initializes a new argument with the given data.
	 * 
	 * @param locator
	 *            locator
	 * @param value
	 *            value
	 */
	public Argument(String locator, String value) {
		this.locator = locator;
		this.value = value;
	}

	/**
	 * Returns the locator (e.g. the HTML ID of a field in case of web
	 * application).
	 * 
	 * @return technical locator
	 */
	public String getLocator() {
		return locator;
	}

	/**
	 * Sets the visible value (e.g. "Name" in case of a name field).
	 * 
	 * @return readable name
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Sets locator (e.g. the HTML ID of a field in case of web application).
	 * 
	 * @param locator
	 *            technical locator
	 */
	public void setLocator(String locator) {
		this.locator = locator;
	}

	/**
	 * Returns the visible value (e.g. "Name" in case of a name field).
	 * 
	 * @param value
	 *            value
	 */
	public void setValue(String value) {
		this.value = value;
	}
}
