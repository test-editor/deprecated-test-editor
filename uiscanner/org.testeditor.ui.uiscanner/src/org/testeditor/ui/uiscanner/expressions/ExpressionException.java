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

/**
 * Exception Class for the ExpressionReader.
 * 
 * @author dkuhlmann
 * 
 */
public class ExpressionException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -373606768855998421L;

	/**
	 * Contructor.
	 */
	public ExpressionException() {
		super();
	}

	/**
	 * Constructs an {@code IOException} with the specified detail message.
	 * 
	 * @param message
	 *            The detail message (which is saved for later retrieval by the
	 *            {@link #getMessage()} method)
	 */
	public ExpressionException(String message) {
		super(message);
	}

	/**
	 * Constructs an {@code IOException} with the specified detail message and
	 * cause.
	 * 
	 * <p>
	 * Note that the detail message associated with {@code cause} is <i>not</i>
	 * automatically incorporated into this exception's detail message.
	 * 
	 * @param message
	 *            The detail message (which is saved for later retrieval by the
	 *            {@link #getMessage()} method)
	 * 
	 * @param cause
	 *            The cause (which is saved for later retrieval by the
	 *            {@link #getCause()} method). (A null value is permitted, and
	 *            indicates that the cause is nonexistent or unknown.)
	 * 
	 * @since 1.6
	 */
	public ExpressionException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs an {@code IOException} with the specified cause and a detail
	 * message of {@code (cause==null ? null : cause.toString())} (which
	 * typically contains the class and detail message of {@code cause}). This
	 * constructor is useful for IO exceptions that are little more than
	 * wrappers for other throwables.
	 * 
	 * @param cause
	 *            The cause (which is saved for later retrieval by the
	 *            {@link #getCause()} method). (A null value is permitted, and
	 *            indicates that the cause is nonexistent or unknown.)
	 * 
	 * @since 1.6
	 */
	public ExpressionException(Throwable cause) {
		super(cause);
	}
}
