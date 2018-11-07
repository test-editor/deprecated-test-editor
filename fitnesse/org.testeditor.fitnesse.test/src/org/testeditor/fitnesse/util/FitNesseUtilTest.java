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
package org.testeditor.fitnesse.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

/**
 * Test for the class {@link FitNesseUtil}.
 *
 */
public class FitNesseUtilTest {

	/**
	 * Tests file with content.txt.
	 */
	@Test
	public void testConvertToFitNessePathWithContentTxt() {

		String file = "c:" + File.separatorChar + "DemoWebTests" + File.separatorChar + "FitNesseRoot"
				+ File.separatorChar + "DemoWebTests" + File.separatorChar + "LocalDemoSuite" + File.separatorChar
				+ "LoginSuite" + File.separatorChar + "content.txt";
		String fitNessePage = FitNesseUtil.convertToFitNessePath(file);

		assertEquals("DemoWebTests.LocalDemoSuite.LoginSuite", fitNessePage);

	}

	/**
	 * Tests file without content.txt.
	 */
	@Test
	public void testConvertToFitNessePathWithoutContentTxt() {

		String file = "c:" + File.separatorChar + "DemoWebTests" + File.separatorChar + "FitNesseRoot"
				+ File.separatorChar + "DemoWebTests" + File.separatorChar + "LocalDemoSuite" + File.separatorChar
				+ "LoginSuite";
		String fitNessePage = FitNesseUtil.convertToFitNessePath(file);

		assertEquals("DemoWebTests.LocalDemoSuite.LoginSuite", fitNessePage);

	}

	/**
	 * Tests file without fitnesseroot.
	 */
	@Test
	public void testConvertToFitNessePathWithoutFitNesseRoot() {

		String file = "c:" + File.separatorChar + "DemoWebTests" + File.separatorChar + "ActionGroup.xml";
		String fitNessePage = FitNesseUtil.convertToFitNessePath(file);

		assertEquals("ActionGroup.xml", fitNessePage);

	}

	/**
	 * Tests file with metadata.xml.
	 */
	@Test
	public void testConvertToFitNessePathWithMetaDataXml() {

		String file = "c:" + File.separatorChar + "DemoWebTests" + File.separatorChar + "FitNesseRoot"
				+ File.separatorChar + "DemoWebTests" + File.separatorChar + "LocalDemoSuite" + File.separatorChar
				+ "LoginSuite" + File.separatorChar + "metadata.xml";
		String fitNessePage = FitNesseUtil.convertToFitNessePath(file);

		assertEquals("DemoWebTests.LocalDemoSuite.LoginSuite", fitNessePage);

	}

	/**
	 * Tests file with properties.xml.
	 */
	@Test
	public void testConvertToFitNessePathWithPropertiesXml() {

		String file = "c:" + File.separatorChar + "DemoWebTests" + File.separatorChar + "FitNesseRoot"
				+ File.separatorChar + "DemoWebTests" + File.separatorChar + "LocalDemoSuite" + File.separatorChar
				+ "LoginSuite" + File.separatorChar + "properties.xml";
		String fitNessePage = FitNesseUtil.convertToFitNessePath(file);

		assertEquals("DemoWebTests.LocalDemoSuite.LoginSuite", fitNessePage);

	}

	/**
	 * Tests file with properties.xml.
	 */
	@Test
	public void testConvertToFitNessePathWithPropertiesXml2() {

		String input = "DemoWebTests.LocalDemoSuite.Login.A.B";
		String modified = "DemoWebTests.LocalDemoSuite.LoginSuite";
		assertFalse(FitNesseUtil.contains(input, modified));

		input = "DemoWebTests.LocalDemoSuite";
		modified = "DemoWebTests.LocalDemoSuite.LoginSuite";
		assertTrue(FitNesseUtil.contains(input, modified));

		input = "DemoWebTests.GoogleSucheSuite.LoginSuite";
		modified = "DemoWebTests.LocalDemoSuite.LoginSuite";
		assertFalse(FitNesseUtil.contains(input, modified));

		input = "DemoWebTests";
		modified = "DemoWebTests.LocalDemoSuite.LoginSuite";
		assertTrue(FitNesseUtil.contains(input, modified));

		input = "DemoWebTests.LocalDemoSuite";
		modified = "DemoWebTests.LocalDemoSuite.LoginSuite";
		assertTrue(FitNesseUtil.contains(input, modified));

		input = "DemoWebTests.LocalDemoSuite.LoginSuite";
		modified = "DemoWebTests.LocalDemoSuite.LoginSuite";
		assertTrue(FitNesseUtil.contains(input, modified));

		input = "DemoWebTests.LocalDemoSuite.LoginSuite.LocalDemoSuite.LoginSuite";
		modified = "DemoWebTests.LocalDemoSuite.LoginSuite";
		assertFalse(FitNesseUtil.contains(input, modified));

		input = "DemoWebTests.LocalDemoSuite.LoginSuiteBla";
		modified = "DemoWebTests.LocalDemoSuite.LoginSuite";
		assertFalse(FitNesseUtil.contains(input, modified));

		input = "DemoWebTests.LocalDemoSuite.LoginSui";
		modified = "DemoWebTests.LocalDemoSuite.LoginSuite.LoginInvalidTest2";
		assertFalse(FitNesseUtil.contains(input, modified));
	}
}