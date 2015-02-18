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

/**
 * <p>
 * Java class for anonymous complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 */
public class ProjectActionGroups {
	private String projectName;
	private ArrayList<ActionGroup> actionGroupList;
	private ArrayList<TechnicalBindingType> technicalBindingTypeList;

	/**
	 * Gets the value of the actionGroup property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the stored object. This is why there is not a
	 * <CODE>set</CODE> method for the actionGroup property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getActionGroup().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list
	 * {@link ActionGroup }
	 * 
	 * @return Returns this ActionGroupList
	 */
	public ArrayList<ActionGroup> getActionGroupList() {
		if (actionGroupList == null) {
			actionGroupList = new ArrayList<ActionGroup>();
		}
		return this.actionGroupList;
	}

	/**
	 * Gets the value of the technicalBindingType property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the technicalBindingType property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getTechnicalBindingType().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list
	 * {@link TechnicalBindingType }
	 * 
	 * @return Returns the TechnicalBindingTypeList
	 */
	public ArrayList<TechnicalBindingType> getTechnicalBindingTypeList() {
		if (technicalBindingTypeList == null) {
			technicalBindingTypeList = new ArrayList<TechnicalBindingType>();
		}
		return this.technicalBindingTypeList;
	}

	/**
	 * adds a actionGroup.
	 * 
	 * @param actionGroup
	 *            ActionGroup
	 */
	public void addActionGroup(ActionGroup actionGroup) {
		if (actionGroupList == null) {
			actionGroupList = new ArrayList<ActionGroup>();
		}
		actionGroupList.add(actionGroup);
	}

	/**
	 * adds a @link {@link TechnicalBindingType}.
	 * 
	 * @param technicalBindingType
	 *            TechnicalBindingType
	 */
	public void addTechnicalBindingType(TechnicalBindingType technicalBindingType) {
		if (technicalBindingTypeList == null) {
			technicalBindingTypeList = new ArrayList<TechnicalBindingType>();
		}
		technicalBindingTypeList.add(technicalBindingType);
	}

	/**
	 * this method sorts the actionGroupList by using the sorting value.
	 */
	public void sortActionGroups() {
		Collections.sort(actionGroupList);
	}

	/**
	 * getter for projectname.
	 * 
	 * @return projectname
	 */
	public String getProjectName() {
		return projectName;
	}

	/**
	 * setter for projectname.
	 * 
	 * @param projectName
	 *            String
	 */
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
}
