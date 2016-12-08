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
 * A part of an action (e.g. the description text or a input value).
 * 
 * 
 */
public class ActionElement {// implements Comparable<ActionElement> {
	private Integer position;
	private ActionElementType type;
	private String value;
	private String id;

	/**
	 * the actionElement default-constructor.
	 */
	public ActionElement() {
	}

	/**
	 * constructor whit parameters.
	 * 
	 * @param position
	 *            int value the position
	 * @param type
	 *            the type
	 * @param value
	 *            the value
	 * @param id
	 *            optional string-value to describe the position of a
	 *            choice-list
	 */
	public ActionElement(final int position, ActionElementType type, String value, String id) {
		this.position = position;
		this.type = type;
		this.value = value;
		this.setId(id);
	}

	/**
	 * Returns the type of this part (e.g. 'TEXT' or 'ACTIONNAME' or
	 * 'PARAMETER').
	 * 
	 * @return type
	 */
	public ActionElementType getType() {
		return type;
	}

	/**
	 * Returns the value (e.g. 'type into', 'password', 'field').
	 * 
	 * @return value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Sets the type of this part (e.g. 'TEXT' or 'PARAMETER').
	 * 
	 * @param type
	 *            type
	 */
	public void setType(ActionElementType type) {
		this.type = type;
	}

	/**
	 * Sets the value (e.g. 'type into', 'password', 'field').
	 * 
	 * @param value
	 *            value
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * 
	 * @return the position of the ActionElement.
	 */
	public Integer getPosition() {
		return position;
	}

	/**
	 * 
	 * @return the id of the actionElement.
	 */
	public String getId() {
		return id;
	}

	/**
	 * sets the id of the actionElement.
	 * 
	 * @param id
	 *            String
	 */
	public void setId(String id) {
		this.id = id;
	}
}
