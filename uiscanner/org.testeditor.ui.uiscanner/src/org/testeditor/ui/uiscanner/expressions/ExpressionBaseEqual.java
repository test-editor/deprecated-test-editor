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
public class ExpressionBaseEqual extends Expression {

	/**
	 * Attribute to compare from the WebElement with the value.
	 * 
	 * @param attribut
	 *            String Attribute of WebElement to check.
	 * @param value
	 *            String value of the Attribute.
	 */
	public ExpressionBaseEqual(String attribut, String value) {
		setAttribut(new String(attribut.toLowerCase()));
		setValue(new String(value));
	}

	/**
	 * Evaluate the Attribute value from the given WebElement with the Value.
	 * 
	 * @param element
	 *            WebElement to check.
	 * @return true if the Value of the Attribute is equals the Value Attribute
	 *         of the WebElement else false.
	 */
	public boolean evalute(WebElement element) {
		if (getAttribut().equals("tagname")) {
			return element.getTagName().equals(getValue());
		} else if (element.getAttribute(getAttribut()) == null) {
			return false;
		}
		return element.getAttribute(getAttribut()).equals(getValue());
	}
}
