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
package org.testeditor.xmllibrary.utils;

import static org.junit.Assert.fail;

import org.apache.log4j.Logger;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * 
 * XsdValiadtaionLoggingErrorHandler.
 * 
 * @author llipinski
 */
class XsdValidationLoggingErrorHandler implements ErrorHandler {

	private static final Logger LOGGER = Logger.getLogger(XsdValidationLoggingErrorHandler.class);

	/**
	 * logs a warning.
	 * 
	 * @param ex
	 *            SAXParseException
	 * @throws SAXException
	 *             SAXException
	 */
	@Override
	public void warning(SAXParseException ex) throws SAXException {

		LOGGER.error("Warnung: " + ex.getMessage());
		fail("Warnung: " + ex.getMessage());
	}

	/**
	 * logs a error.
	 * 
	 * @param ex
	 *            SAXParseException
	 * @throws SAXException
	 *             SAXException
	 * 
	 */
	@Override
	public void error(SAXParseException ex) throws SAXException {

		LOGGER.error("Fehler: " + ex.getMessage());
		fail("Fehler: " + ex.getMessage());
	}

	/**
	 * logs a fatalError.
	 * 
	 * @param ex
	 *            SAXParseException
	 * @throws SAXException
	 *             SAXException
	 */
	@Override
	public void fatalError(SAXParseException ex) throws SAXException {

		LOGGER.error("Fataler Fehler: " + ex.getMessage());
		fail("Fataler Fehler: " + ex.getMessage());
	}
}
