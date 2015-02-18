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
package org.testeditor.fitnesse.usedbyreader;

import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;
import org.junit.Test;

/**
 * 
 * tests the usedByReader.
 * 
 * @author llipinski
 */
public class FitNesseUsedByReaderImplTest {

	private static final Logger LOGGER = Logger.getLogger(FitNesseUsedByReaderImplTest.class);

	/**
	 * test the readWhereUsedResult method.
	 */
	@Test
	public void testusedByResultReader() {
		InputStream resultStream = FitNesseUsedByReaderImpl.class.getResourceAsStream("/used_by_result.html");

		// FitNesseUsedByReader fitNesseUsedByReader = new
		// FitNesseUsedByReaderImpl();
		// List<String> results = fitNesseUsedByReader.readWhereUsedResult(
		// fitNesseUsedByReader.convertStreamToString(resultStream),
		// "DemoWebTests");
		// assertEquals("DemoWebTests.LoginSzenarioSuite.FgH", results.get(0));
		// assertEquals("DemoWebTests.LoginSzenarioSuite.LoginValidMassenTest",
		// results.get(1));
		// assertEquals("DemoWebTests.LoginSuite.XdC", results.get(2));

		try {
			resultStream.close();
		} catch (IOException e) {
			LOGGER.error(e);
		}
	}
}
