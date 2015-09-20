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
package org.testeditor.ui.uiscanner.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testeditor.ui.uiscanner.expressions.Expression;
import org.testeditor.ui.uiscanner.expressions.ExpressionException;
import org.testeditor.ui.uiscanner.expressions.ExpressionReader;
import org.testeditor.ui.uiscanner.webscanner.UiScannerConstants;
import org.testeditor.ui.uiscanner.webscanner.UiScannerWebElement;
import org.testeditor.ui.uiscanner.webscanner.WebScanner;

/**
 * Modul test for ExpressionScanner.
 * 
 */
public class ExpressionScannerTest {

	private static WebScanner webScanner;
	private static final URI WEB_INDEX_PAGE = new File("resources/web/index.html").toURI();

	/**
	 * Start the fireFoxBrowser.
	 */
	@BeforeClass
	public static void startBrowser() {
		webScanner = new WebScanner();
		webScanner.openBrowser(UiScannerConstants.BROWSER_HTMLUNIT, WEB_INDEX_PAGE.toString());
	}

	/**
	 * close the opened browser.
	 */
	@AfterClass
	public static void stopBrowser() {
		webScanner.quit();
	}

	/**
	 * 
	 * @throws IOException
	 *             IOException
	 * @throws ExpressionException
	 *             ExpressionException
	 */
	@Test
	public void scan() throws IOException, ExpressionException {
		ExpressionReader reader = new ExpressionReader();
		HashMap<String, Expression> exprs = reader.readCheck(new File("resources/expressions/newCheck.txt").getPath());
		ArrayList<String> filters = new ArrayList<String>();
		filters.add(UiScannerConstants.TYP_BUTTON);
		filters.add(UiScannerConstants.TYP_CHECKBOX);
		filters.add(UiScannerConstants.TYP_INPUT);
		filters.add(UiScannerConstants.TYP_RADIO);
		filters.add(UiScannerConstants.TYP_SELECT);
		ArrayList<UiScannerWebElement> elems = new ArrayList<UiScannerWebElement>();
		elems = webScanner.scanFilteredWithExpression(elems, exprs, filters, "");
		assertEquals(15, elems.size());
		assertEquals("user", elems.get(0).getTechnicalID());
		assertEquals("input", elems.get(0).getTyp());
		assertEquals("password", elems.get(1).getTechnicalID());
		assertEquals("input", elems.get(1).getTyp());
		assertEquals("land", elems.get(2).getTechnicalID());
		assertEquals("select", elems.get(2).getTyp());
		assertEquals(1, elems.get(2).getValue().size());
		assertEquals("DeutschlandItalienUSASchwedenAustralien", elems.get(2).getValue().get(0));
		assertEquals("mastercard_ID", elems.get(3).getTechnicalID());
		assertEquals("radio", elems.get(3).getTyp());
		assertEquals("visa_ID", elems.get(4).getTechnicalID());
		assertEquals("radio", elems.get(4).getTyp());
		assertEquals("american_express_ID", elems.get(5).getTechnicalID());
		assertEquals("radio", elems.get(5).getTyp());
		assertEquals("salami_ID", elems.get(6).getTechnicalID());
		assertEquals("checkbox", elems.get(6).getTyp());
		assertEquals("pilze_ID", elems.get(7).getTechnicalID());
		assertEquals("checkbox", elems.get(7).getTyp());
		assertEquals("login_ID", elems.get(8).getTechnicalID());
		assertEquals("button", elems.get(8).getTyp());
		assertEquals("reset_ID", elems.get(9).getTechnicalID());
		assertEquals("button", elems.get(9).getTyp());
		assertEquals("div_class_button", elems.get(10).getTechnicalID());
		assertEquals("button", elems.get(10).getTyp());
		assertEquals("textarea", elems.get(11).getTechnicalID());
		assertEquals("input", elems.get(11).getTyp());
		assertEquals("normal_button", elems.get(12).getTechnicalID());
		assertEquals("button", elems.get(12).getTyp());
		assertEquals("button_in_iframe", elems.get(13).getTechnicalID());
		assertEquals("button", elems.get(13).getTyp());
		assertEquals("button_in_iframe2", elems.get(14).getTechnicalID());
		assertEquals("button", elems.get(14).getTyp());
	}
}
