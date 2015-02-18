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
package org.testeditor.core.util;

/**
 * 
 * Util class for storing key value pairs.
 * 
 */
public class KeyValuePair implements Comparable<KeyValuePair> {
	private String key;
	private String value;

	/**
	 * Constructor.
	 * 
	 * @param key
	 *            key
	 * @param value
	 *            value
	 */
	public KeyValuePair(String key, String value) {
		this.key = key;
		this.value = value;
	}

	/**
	 * get key.
	 * 
	 * @return key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * set key.
	 * 
	 * @param key
	 *            key
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * get value.
	 * 
	 * @return value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * set value.
	 * 
	 * @param value
	 *            value
	 */
	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public int compareTo(KeyValuePair compareToObject) {
		return this.key.compareToIgnoreCase(compareToObject.getKey());
	}

}
