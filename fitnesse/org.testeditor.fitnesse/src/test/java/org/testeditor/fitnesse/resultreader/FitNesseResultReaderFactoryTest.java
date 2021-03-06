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

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.testeditor.core.model.teststructure.TestType;

/**
 * 
 * Tests the FitNesseResultReaderFactory.
 */
public class FitNesseResultReaderFactoryTest {

	/**
	 * 
	 */
	@Test
	public void testFactory() {

		FitNesseResultReader reader = FitNesseResultReaderFactory.getHistoryReader(TestType.TEST);
		assertEquals(FitNesseTestHistoryResultReader.class, reader.getClass());

		reader = FitNesseResultReaderFactory.getHistoryReader(TestType.SUITE);
		assertEquals(FitNesseSuiteHistoryResultReader.class, reader.getClass());

	}

}
