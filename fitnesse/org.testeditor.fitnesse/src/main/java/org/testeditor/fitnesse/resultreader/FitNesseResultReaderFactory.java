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
package org.testeditor.fitnesse.resultreader;

import org.apache.log4j.Logger;
import org.testeditor.core.model.teststructure.TestType;

/**
 * Factory class for FitNesseReader.
 * 
 */
public final class FitNesseResultReaderFactory {

	private static final Logger LOGGER = Logger.getLogger(FitNesseResultReaderFactory.class);

	/**
	 * This class should not be instanziated.
	 */
	private FitNesseResultReaderFactory() {
		//
	}

	/**
	 * Factory Method to create a FitnesseResultReader on the specified type.
	 * 
	 * @param type
	 *            for TestStrucutre
	 * @return FitnesseResultReader
	 */
	public static FitNesseResultReader getHistoryReader(TestType type) {

		if (type.equals(TestType.TEST)) {
			return new FitNesseTestHistoryResultReader();
		} else if (type.equals(TestType.SUITE)) {
			return new FitNesseSuiteHistoryResultReader();
		}

		LOGGER.error("Type of test have to be 'test' or 'suite'");

		throw new IllegalArgumentException();

	}

}
