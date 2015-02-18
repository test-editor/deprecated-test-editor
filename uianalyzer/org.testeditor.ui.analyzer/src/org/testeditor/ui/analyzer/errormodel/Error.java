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
package org.testeditor.ui.analyzer.errormodel;

import org.testeditor.core.model.action.IAction;
import org.testeditor.core.model.teststructure.TestFlow;

/**
 * 
 * POJO to store validation errors on TestStructures. This is a Child element of
 * <code>ErrorContainer</code>.
 * 
 */
public class Error {

	private IAction action;
	private TestFlow testFlow;

	/**
	 * 
	 * @param testFlow
	 *            which contains this error.
	 */
	public Error(TestFlow testFlow) {
		this.testFlow = testFlow;
	}

	/**
	 * 
	 * @param action
	 *            that is not parsable.
	 */
	public void setAction(IAction action) {
		this.action = action;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (String line : action.getSourceCode()) {
			sb.append(line).append("\n");
		}
		return sb.toString().trim();
	}

	/**
	 * 
	 * @return testflow with this error.
	 */
	public TestFlow getTestFlow() {
		return testFlow;
	}

}
