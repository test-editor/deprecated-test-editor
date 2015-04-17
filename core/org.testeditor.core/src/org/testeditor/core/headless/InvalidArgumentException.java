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
package org.testeditor.core.headless;

/**
 * 
 * Exception to show wrong parameter for launching test-editor in a headless
 * way.
 *
 */
public class InvalidArgumentException extends Exception {

	/**
	 * Default constructor with error message.
	 * 
	 * @param message
	 *            of the exception cause.
	 */
	public InvalidArgumentException(String message) {
		super(message);
	}

	/**
	 * Default serial id.
	 */
	private static final long serialVersionUID = 1L;

}
