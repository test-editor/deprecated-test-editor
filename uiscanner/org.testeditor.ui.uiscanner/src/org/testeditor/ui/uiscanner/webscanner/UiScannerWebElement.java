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
package org.testeditor.ui.uiscanner.webscanner;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author dkuhlmann
 * 
 */
public class UiScannerWebElement {
	private String technicalID;
	private String typ;
	private List<String> actions;
	private String name;
	private String locator;
	private List<String> value;

	/**
	 * Set the Default Action for the Element Typ.
	 */
	public void setDefaultActions() {
		actions = new ArrayList<>();
		if (this.typ.equals(UiScannerConstants.TYP_BUTTON)) {
			addAction(UiScannerConstants.ACTION_BUTTON_PRESS);
		} else if (this.typ.equals(UiScannerConstants.TYP_SELECT)) {
			addAction(UiScannerConstants.ACTION_SELECT_VALUE);
			addAction(UiScannerConstants.ACTION_CLEAR_VALUR);
			addAction(UiScannerConstants.ACTION_ENTER_VALUE);
		} else if (this.typ.equals(UiScannerConstants.TYP_INPUT)) {
			addAction(UiScannerConstants.ACTION_CLEAR_VALUR);
			addAction(UiScannerConstants.ACTION_ENTER_VALUE);
		} else if (this.typ.equals(UiScannerConstants.TYP_RADIO)) {
			addAction(UiScannerConstants.ACTION_ENTER_VALUE);
		} else if (this.typ.equals(UiScannerConstants.TYP_CHECKBOX)) {
			addAction(UiScannerConstants.ACTION_ENTER_VALUE);
		}
	}

	/**
	 * Add the Action.
	 * 
	 * @param action
	 *            String
	 */
	private void addAction(String action) {
		actions.add(action);
	}

	/**
	 * constructor.
	 * 
	 * @param typ
	 *            String: Typ of the Element (Button, Intput...)
	 * @param id
	 *            String: Element Id
	 */
	public UiScannerWebElement(String typ, String id) {
		this(typ, id, id);
	}

	/**
	 * constructor.
	 * 
	 * @param typ
	 *            String: Typ of the Element (Button, Intput...)
	 * @param technicalID
	 *            String: Technicalid
	 * @param name
	 *            String: name
	 */
	public UiScannerWebElement(String typ, String technicalID, String name) {
		this(typ, technicalID, name, name);
	}

	/**
	 * constructor.
	 * 
	 * @param typ
	 *            String: Typ of the Element (Button, Intput...)
	 * @param technicalID
	 *            String: Technicalid
	 * @param name
	 *            String: Name
	 * @param locator
	 *            String: locator
	 */
	public UiScannerWebElement(String typ, String technicalID, String name, String locator) {
		this.setTyp(typ);
		this.setTechnicalID(technicalID);
		this.setName(name);
		this.setLocator(locator);
		this.value = new ArrayList<String>();
	}

	/**
	 * @return the technicalID
	 */
	public String getTechnicalID() {
		return technicalID;
	}

	/**
	 * @param technicalID
	 *            the technicalID to set
	 */
	public void setTechnicalID(String technicalID) {
		this.technicalID = technicalID;
	}

	/**
	 * @return the typ
	 */
	public String getTyp() {
		return typ;
	}

	/**
	 * @param typ
	 *            the typ to set
	 */
	public void setTyp(String typ) {
		this.typ = typ;
	}

	/**
	 * @return the actions
	 */
	public List<String> getActions() {
		return actions;
	}

	/**
	 * @param actions
	 *            the actions to set
	 */
	public void setActions(List<String> actions) {
		this.actions = actions;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the locator
	 */
	public String getLocator() {
		return locator;
	}

	/**
	 * @param locator
	 *            the locator to set
	 */
	public void setLocator(String locator) {
		this.locator = locator;
	}

	/**
	 * @return the value
	 */
	public List<String> getValue() {
		return value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(List<String> value) {
		this.value = value;
	}

	/**
	 * 
	 * @param value
	 *            String value to be added
	 */
	public void addValue(String value) {
		this.value.add(value);
	}

	/**
	 * 
	 * @param value
	 *            String value to be removed
	 */
	public void removeValue(String value) {
		this.value.remove(value);
	}

	/**
	 * Returns the WebElemnt as a String.
	 * 
	 * @return a string consisting of exactly this sequence of characters from
	 *         the WebElement Values.
	 * 
	 */
	public String toString() {
		// StringBuilder str = new StringBuilder(getTyp());
		// str.append("," + getName());
		// str.append(", " + getTechnicalID());
		// str.append(", " + getLocator());
		return "";
	}

	/**
	 * Returns a Array with all the Elements.
	 * 
	 * @return Array of Strings.
	 */
	public Object[] toArray() {
		ArrayList<String> result = new ArrayList<>();
		result.add(getTyp());
		result.add(getTechnicalID());
		result.add(getName());
		result.add(getLocator());
		result.add(getValue().toString());
		return result.toArray();
	}
}
