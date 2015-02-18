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

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Java class for TechnicalBindingType complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * 
 * 
 */
public class TechnicalBindingType {
	private List<ActionElement> actionParts;
	private String shortName;
	private String id;
	private Integer sorting;

	/**
	 * constructor.
	 */
	public TechnicalBindingType() {
		id = "";
		shortName = "";
		actionParts = new ArrayList<ActionElement>();
	}

	/**
	 * constructor with parameters.
	 * 
	 * @param id
	 *            id
	 * @param shortName
	 *            shortname
	 * @param actionParts
	 *            actionParts
	 * @param sorting
	 *            Integer sorting
	 */
	public TechnicalBindingType(String id, String shortName, List<ActionElement> actionParts, Integer sorting) {
		this.id = id;
		this.shortName = shortName;
		this.actionParts = actionParts;
		this.sorting = sorting;
	}

	/**
	 * Returns the parts of the action (e.g. output text + input field + another
	 * output text).
	 * 
	 * @return action parts
	 */
	public List<ActionElement> getActionParts() {
		return actionParts;
	}

	/**
	 * Sets the parts of the action (e.g. output text + input field + another
	 * output text).
	 * 
	 * @param actionParts
	 *            action parts
	 */
	public void setActionParts(List<ActionElement> actionParts) {
		this.actionParts = actionParts;
	}

	/**
	 * this method returns the shortname.
	 * 
	 * @return shortname
	 */
	public String getShortName() {
		return shortName;
	}

	/**
	 * this method sets the shortname.
	 * 
	 * @param shortName
	 *            string
	 */

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	/**
	 * Gets the value of the id property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the value of the id property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setId(String value) {
		this.id = value;
	}

	/**
	 * gets the sorting.
	 * 
	 * @return sorting as Integer
	 */
	public Integer getSorting() {
		return sorting;
	}

	/**
	 * sets the sorting.
	 * 
	 * @param sorting
	 *            Integer
	 */
	public void setSorting(Integer sorting) {
		this.sorting = sorting;
	}

	/**
	 * 
	 * @return the list of the positions of choicelists.
	 */
	public List<Integer> getArgPosChoices() {
		ArrayList<Integer> choiceListPos = new ArrayList<Integer>();
		for (ActionElement actionElement : getActionParts()) {
			if (actionElement.getId() != null) {
				choiceListPos.add(actionElement.getPosition());
			}
		}
		return choiceListPos;
	}

	/**
	 * compares to technichalBinding.
	 * 
	 * @param compareTechBind
	 *            TechnicalBindingType
	 * @return the result of the compareTo
	 */
	public int compareTo(TechnicalBindingType compareTechBind) {
		if (this.getSorting() == null && compareTechBind.getSorting() == null) {
			if (!this.getShortName().isEmpty() && !compareTechBind.getShortName().isEmpty()) {
				return this.getShortName().compareTo(compareTechBind.getShortName());
			}
			if (this.getShortName().isEmpty() && compareTechBind.getShortName().isEmpty()) {
				return this.getId().compareTo(compareTechBind.getId());
			}
			if (this.getShortName().isEmpty()) {
				return 1;
			}
			if (compareTechBind.getShortName().isEmpty()) {
				return -1;
			}
		}
		return getSorting(compareTechBind);
	}

	/**
	 * 
	 * compares to technichalBinding.
	 * 
	 * @param compareTechBind
	 *            TechnicalBindingType
	 * @return the result of the compareTo
	 */
	private int getSorting(TechnicalBindingType compareTechBind) {
		if (this.getSorting() == null) {
			return 1;
		}
		if (compareTechBind.getSorting() == null) {
			return -1;
		}
		int comp = this.getSorting().compareTo(compareTechBind.getSorting());
		if (comp != 0) {
			return comp;
		}

		return 0;
	}
}
