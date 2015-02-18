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
package org.testeditor.ui.wizardpages.nameinspector;


/**
 * Interface for the name-inspectors for the new and rename wizards.
 * 
 * @author llipinski
 * 
 */
public interface INameInspector {
	/**
	 * 
	 * @param name
	 *            the name that should be tested
	 * @return true, if the name is valid.
	 */
	boolean isNameValid(String name);

	/**
	 * 
	 * @return as a message why the name is invalid.
	 */
	String nameInvalideMessage();
}
