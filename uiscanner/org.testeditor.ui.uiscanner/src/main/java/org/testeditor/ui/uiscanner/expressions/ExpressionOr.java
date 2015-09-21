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
package org.testeditor.ui.uiscanner.expressions;

import org.openqa.selenium.WebElement;

/**
 * 
 * @author dkuhlmann
 * 
 */
public class ExpressionOr extends Expression {

	/**
	 * Construct a Or Expression.
	 */
	public ExpressionOr() {
		super();
	}

	@Override
	public boolean evalute(WebElement element) {
		for (Expression exp : getExpressions()) {
			if (exp.evalute(element)) {
				return true;
			}
		}
		return false;
	}

}
