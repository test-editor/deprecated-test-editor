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
 * 
 * special exception, in case of problems while the object-tree under
 * construction.
 * 
 * @deprecated is only used while loading the "library" and thus not really
 *             needed
 */
public class ObjectTreeConstructionException extends SystemException {
	/**
	 * constructor with the message.
	 * 
	 * @param message
	 *            string
	 */
	public ObjectTreeConstructionException(String message) {
		super(message);
	}

	/**
	 * constructor.
	 * 
	 * @param message
	 *            String
	 * @param cause
	 *            Throwable
	 */
	public ObjectTreeConstructionException(String message, Throwable cause) {
		super(message, cause);
	}
}
