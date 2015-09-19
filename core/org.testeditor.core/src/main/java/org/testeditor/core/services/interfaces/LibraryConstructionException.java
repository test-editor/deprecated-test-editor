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
package org.testeditor.core.services.interfaces;

import org.testeditor.core.exceptions.SystemException;

/**
 * Exception on creating the library describing the application. This exception
 * is thrown on inconsistent action group and technical binding configurations.
 * Like missing technical binding.
 * 
 * 
 */
public class LibraryConstructionException extends SystemException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -910177736060465259L;

	/**
	 * constructor with the message.
	 * 
	 * @param message
	 *            string
	 */
	public LibraryConstructionException(String message) {
		super(message);
	}

	/**
	 * constructor with message an underlying excpetion.
	 * 
	 * @param message
	 *            of the exception.
	 * @param cause
	 *            Throwable nested exception.
	 */
	public LibraryConstructionException(String message, Throwable cause) {
		super(message, cause);
	}
}
