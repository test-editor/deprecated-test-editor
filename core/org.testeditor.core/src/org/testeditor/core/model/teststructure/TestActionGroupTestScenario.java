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

/**
 * 
 * specialized Class of TestActionGroup for a TestScenario.
 * 
 * @author llipinski
 */
public class TestActionGroupTestScenario extends TestActionGroup {

	/**
	 * specialized getSourceCode-method for the TestScenario because FitNesse
	 * does not allow interrupted testlines so it is advisable to interrupt the
	 * testflow with the description beginning with |note|. In TestScenarios you
	 * can not use a "#" for comments.
	 * 
	 * @return the sourcecode as String
	 */
	public String getSourceCode() {
		StringBuilder sourceCode = new StringBuilder();
		sourceCode.append("|note| Maske: ").append(getActionGroupName()).append("|\n");
		sourceCode.append(getTableSourcecode());
		return sourceCode.toString();
	}

	/**
	 * @return true.
	 */
	@Override
	public boolean isScenarioTestActionGroup() {
		return true;
	}

	/**
	 * 
	 * @return a list of parameters as a string.
	 */
	public List<String> getParameterFromActionGroup() {
		List<String> paramList = new ArrayList<String>();
		List<IAction> actions = getActionLines();
		for (IAction action : actions) {
			List<String> actionParameters = action.getParameters();
			if (actionParameters != null) {
				for (String param : actionParameters) {
					if (!paramList.contains(param)) {
						paramList.add(param);
					}
				}
			}
		}
		return paramList;
	}
}
