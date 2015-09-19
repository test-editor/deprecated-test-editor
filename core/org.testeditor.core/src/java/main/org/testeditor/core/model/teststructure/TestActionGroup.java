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

import java.util.ArrayList;
import java.util.List;

import org.testeditor.core.model.action.IAction;
import org.testeditor.core.model.action.TextType;

/**
 * Test action includes a part of a test (e.g. a few test steps for a defined UI
 * mask).
 */
public class TestActionGroup implements TestComponent {
	private List<IAction> actionLines = new ArrayList<IAction>();
	private List<String> invisibleActionLines = new ArrayList<String>();
	private String actionGroupName = "";
	private static String startScript = "-!|script|";
	private boolean parsedActionGroup = true;

	/**
	 * Adds an action line (e.g. a specific test step).
	 * 
	 * @param action
	 *            action line
	 */
	public void addActionLine(IAction action) {
		actionLines.add(action);
	}

	/**
	 * Returns the header (e.g. the name of the current UI mask).
	 * 
	 * @return header
	 */
	public String getActionGroupName() {
		return actionGroupName;
	}

	/**
	 * Returns all action lines (e.g. specific test steps).
	 * 
	 * @return action lines
	 */
	public List<IAction> getActionLines() {
		return actionLines;
	}

	/**
	 * Sets the action lines.
	 * 
	 * @param actionLines
	 *            action lines
	 */
	public void setActionLines(List<IAction> actionLines) {
		this.actionLines = actionLines;
	}

	/**
	 * Sets the invsible action lines.
	 * 
	 * @param actionLines
	 *            action lines e.g. lines beginning with "-!|script|" should not
	 *            been shown but their are necessary for fitness
	 */
	public void setInvisibleActionLines(List<String> actionLines) {
		this.invisibleActionLines = actionLines;
	}

	/**
	 * Sets the header.
	 * 
	 * @param header
	 *            header
	 */
	public void setActionGroupName(String header) {
		this.actionGroupName = header;
	}

	/**
	 * adds an invisible actionline.
	 * 
	 * @param line
	 *            String
	 */
	public void addInvisibleActionLine(String line) {
		invisibleActionLines.add(line);
	}

	/**
	 * Returns the invisible action lines (e.g. specific test steps).
	 * 
	 * @return action lines
	 */
	public List<String> getInvisibelActionLines() {
		return invisibleActionLines;
	}

	@Override
	public List<String> getTexts() {
		ArrayList<String> stringList = new ArrayList<String>();
		for (IAction action : actionLines) {
			stringList.addAll(action.getTexts());
		}
		return stringList;
	}

	@Override
	public List<TextType> getTextTypes() {
		ArrayList<TextType> typeList = new ArrayList<TextType>();
		for (IAction action : actionLines) {
			typeList.addAll(action.getTextTypes());
		}
		return typeList;
	}

	/**
	 * removes the given actionline from the array.
	 * 
	 * @param action
	 *            the action will be removed
	 */
	public void removeActionLine(IAction action) {
		actionLines.remove(action);
	}

	/**
	 * this method returns true if the actionGroupName is in the bibliothek.
	 * 
	 * @return parsedActionGroup
	 */
	public boolean isParsedActionGroup() {
		return parsedActionGroup;
	}

	/**
	 * this method is call, if the name of the mask is not in in bibliothek.
	 * 
	 * @param parsedActionGroup
	 *            boolean
	 */
	public void setParsedActionGroup(boolean parsedActionGroup) {
		this.parsedActionGroup = parsedActionGroup;
	}

	/**
	 * compares the beginning of the source-code of this and an other
	 * {@link TestActionGroup}.
	 * 
	 * @param compareTestactionGroup
	 *            the {@link TestActionGroup}, that should be compared
	 * @return boolean true if the first lines of the source-code of both
	 *         {@link TestActionGroup} are equal
	 */
	public boolean isStartOfSourceCodeEqual(TestActionGroup compareTestactionGroup) {
		return this.getFirstLineOfSourcecode().equalsIgnoreCase(compareTestactionGroup.getFirstLineOfSourcecode());
	}

	/**
	 * gets the first line of the sourcecode of the {@link TestActionGroup}.
	 * 
	 * 
	 * @return the first line
	 */
	private String getFirstLineOfSourcecode() {
		String[] parts = getSourceCode().split("\n");
		return parts[0];
	}

	/**
	 * get the table at the third line of the sourcecode of a
	 * {@link TestActionGroup} and appends this on the sourceCode.
	 * 
	 * @return the local sourcecode of the component
	 */
	public String getTableSourcecode() {
		StringBuilder sourceCode = new StringBuilder();

		ArrayList<String> stringList = new ArrayList<String>();
		for (IAction action : actionLines) {
			stringList.addAll(action.getSourceCode());
		}

		for (String text : stringList) {
			if (text.startsWith("|")) {
				text = text.substring(1);
			}
			sourceCode.append("|").append(text.substring(0, text.length() - 1));
		}
		sourceCode.append("|");
		return sourceCode.toString();
	}

	/**
	 * 
	 * @return the static member-variable startScript
	 */
	protected static String getStartScript() {
		return startScript;
	}

	@Override
	public String getSourceCode() {
		return null;
	}

	/**
	 * 
	 * @return false, but in the subclass TestActionGroupTestScenario it returns
	 *         true.
	 */
	public boolean isScenarioTestActionGroup() {
		return false;
	}
}