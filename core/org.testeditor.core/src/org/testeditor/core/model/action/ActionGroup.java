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
import java.util.Collections;
import java.util.List;

/**
 * An action group represents a mask a specific view part (e.g. 'LoginMask').
 * 
 * 
 */
public class ActionGroup implements Comparable<ActionGroup> {
	private List<AbstractAction> actions;
	private String name;
	private Integer sorting;

	/**
	 * constructor.
	 */
	public ActionGroup() {
		name = "";
		actions = new ArrayList<AbstractAction>();
	}

	/**
	 * constructor with parameter.
	 * 
	 * @param actionGroup
	 *            {@link ActionGroup}
	 */
	public ActionGroup(ActionGroup actionGroup) {
		name = actionGroup.getName();
		actions = actionGroup.getActions();
	}

	/**
	 * Returns the action of the group (e.g. 'type into password field', 'type
	 * into user name field', 'click button login').
	 * 
	 * @return actions
	 */
	public List<AbstractAction> getActions() {
		return actions;
	}

	/**
	 * Returns the name of the action group (e.g. 'LoginMask').
	 * 
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the action of the group (e.g. 'type into password field', 'type into
	 * user name field', 'click button login').
	 * 
	 * @param actions
	 *            actions
	 */
	public void setActions(List<AbstractAction> actions) {
		this.actions = actions;
	}

	/**
	 * Sets the name of the action group (e.g. 'LoginMask').
	 * 
	 * @param name
	 *            name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * adds the action to the actionGroup.
	 * 
	 * @param action
	 *            {@link AbstractAction}
	 */
	public void addAction(Action action) {
		if (actions == null) {
			actions = new ArrayList<AbstractAction>();
		}
		actions.add(action);
	}

	/**
	 * this method sorts the actions by using the sorting value.
	 */
	public void sortActions() {
		Collections.sort(actions);
	}

	/**
	 * gets the sorting.
	 * 
	 * @return teh sorting
	 */
	public Integer getSorting() {
		return sorting;
	}

	/**
	 * set the sorting.
	 * 
	 * @param sorting
	 *            Integer may be null
	 */
	public void setSorting(Integer sorting) {
		this.sorting = sorting;
	}

	@Override
	public int compareTo(ActionGroup compareActionGroup) {
		if (this.getSorting() == null && compareActionGroup.getSorting() == null) {
			return compareTheNames(compareActionGroup);
		}
		if (this.getSorting() == null) {
			return 1;
		}
		if (compareActionGroup.getSorting() == null) {
			return -1;
		}
		int comp = this.getSorting().compareTo(compareActionGroup.getSorting());
		if (comp != 0) {
			return comp;
		} else {
			return compareTheNames(compareActionGroup);
		}
	}

	/**
	 * compares the names of this action whit the name of the compareAction.
	 * 
	 * @param compareActionGroup
	 *            the compareAction
	 * @return ameOfThis.compareTo(compName);
	 */
	private int compareTheNames(ActionGroup compareActionGroup) {

		return this.getName().compareTo(compareActionGroup.getName());

	}
}
