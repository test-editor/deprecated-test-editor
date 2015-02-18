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

/**
 * 
 * specialized Class of TestActionGroup for a TestCase.
 * 
 * @author llipinski
 */
public class TestActionGroupTestCase extends TestActionGroup {

	@Override
	public String getSourceCode() {
		StringBuilder sourceCode = new StringBuilder();
		sourceCode.append("# Maske: ").append(getActionGroupName()).append("\n");
		sourceCode.append(getStartScript()).append("\n");
		sourceCode.append(getTableSourcecode());
		return sourceCode.toString();
	}

}
