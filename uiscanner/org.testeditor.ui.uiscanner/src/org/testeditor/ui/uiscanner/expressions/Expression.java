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

import java.util.ArrayList;

import org.openqa.selenium.WebElement;

/**
 * @author dkuhlmann
 * 
 */
public abstract class Expression {
	private ArrayList<Expression> expressions;
	private String attribut;
	private String value;

	/**
	 * Constructor.
	 */
	public Expression() {
		expressions = new ArrayList<>();
	}

	/**
	 * Evaluate the Attribute value from the given WebElement with the Value.
	 * 
	 * @param element
	 *            WebElement to check.
	 * @return true if the Value of the Attribute is equals the Value Attribute
	 *         of the WebElement else false.
	 */
	public abstract boolean evalute(WebElement element);

	/**
	 * @return the left
	 */
	public ArrayList<Expression> getExpressions() {
		return expressions;
	}

	/**
	 * adds a expression.
	 * 
	 * @param exp
	 *            Expression to be added.
	 */
	public void addExpression(Expression exp) {
		expressions.add(exp);
	}

	/**
	 * @return the attribut
	 */
	public String getAttribut() {
		return attribut;
	}

	/**
	 * @param attribut
	 *            the attribut to set
	 */
	public void setAttribut(String attribut) {
		this.attribut = attribut;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
}
