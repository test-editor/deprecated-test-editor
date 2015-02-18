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
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.testeditor.ui.uiscanner.test.mocks.ProgressMonitorMock;
import org.testeditor.ui.uiscanner.webscanner.UiScannerConstants;
import org.testeditor.ui.uiscanner.webscanner.UiScannerWebElement;
import org.testeditor.ui.uiscanner.webscanner.WebScanner;

/**
 * @author dkuhlmann
 * 
 */
public class ScannerTest {

	private static WebScanner webScanner;
	private ArrayList<UiScannerWebElement> elements;
	private ProgressMonitorMock progressMonitor = new ProgressMonitorMock();
	private static final URI WEB_INDEX_PAGE = new File("resources/web/index.html").toURI();

	/**
	 * Start the fireFoxBrowser.
	 */
	@Before
	public void startBrowser() {
		webScanner = new WebScanner();
		webScanner.openBrowser(UiScannerConstants.BROWSER_HTMLUNIT, WEB_INDEX_PAGE.toString());
	}

	/**
	 * close the opened browser.
	 */
	@After
	public void stopBrowser() {
		webScanner.quit();
	}

	/**
	 * test if the WebDriver is active.
	 */
	@Test
	public void isWebDriverActive() {
		WebScanner execptioScanner = new WebScanner();
		assertTrue(!execptioScanner.isWebDriverAktive());
		execptioScanner.openBrowser(UiScannerConstants.BROWSER_HTMLUNIT, WEB_INDEX_PAGE.toString());
		execptioScanner.quit();
		assertTrue(!execptioScanner.isWebDriverAktive());
	}

	/**
	 * test if the WebDriver is active.
	 */
	@Test
	public void closeWebDriverAndCheck() {
		assertTrue(webScanner.isWebDriverAktive());
	}

	/**
	 * test to navigate the webDriver to a URL without Http:// https or file.
	 * the webdriver should write http:// in the front.
	 */
	@Ignore("dont work on the SI server (Proxy)")
	@Test
	public void navigateTo() {
		String url = "www.google.de/";
		WebScanner navigateScanner = new WebScanner();
		navigateScanner.openBrowser(UiScannerConstants.BROWSER_HTMLUNIT, url);
		assertEquals("http://" + url, navigateScanner.getcurrentURL());
		navigateScanner.quit();
	}

	/**
	 * test the search of all button on the testSite.
	 */
	@Test
	public void searchButton() {
		ArrayList<String> filter = new ArrayList<String>();
		filter.add(UiScannerConstants.TYP_BUTTON);
		elements = webScanner.scanWebsite(filter, "", progressMonitor.getProgressMonitor());
		assertEquals(6, elements.size());
		assertEquals("normal_button", elements.get(0).getTechnicalID());
		assertEquals("login_ID", elements.get(1).getTechnicalID());
		assertEquals("reset_ID", elements.get(2).getTechnicalID());
		assertEquals("div_class_button", elements.get(3).getTechnicalID());
		assertEquals("button_in_iframe", elements.get(4).getTechnicalID());
		assertEquals("button_in_iframe2", elements.get(5).getTechnicalID());
	}

	/**
	 * test the search of all input on the testSite.
	 */
	@Test
	public void searchInput() {
		ArrayList<String> filter = new ArrayList<String>();
		filter.add(UiScannerConstants.TYP_INPUT);
		elements = webScanner.scanWebsite(filter, "", progressMonitor.getProgressMonitor());
		assertEquals(3, elements.size());
		assertEquals("user", elements.get(0).getTechnicalID());
		assertEquals("password", elements.get(1).getTechnicalID());
		assertEquals("textarea", elements.get(2).getTechnicalID());
	}

	/**
	 * test the search of all checkboxs on the testSite.
	 */
	@Test
	public void searchRadioAndCheckbox() {
		ArrayList<String> filter = new ArrayList<String>();
		filter.add(UiScannerConstants.TYP_RADIO);
		filter.add(UiScannerConstants.TYP_CHECKBOX);
		elements = webScanner.scanWebsite(filter, "", progressMonitor.getProgressMonitor());
		assertEquals(5, elements.size());
		assertEquals("mastercard_ID", elements.get(0).getTechnicalID());
		assertEquals("visa_ID", elements.get(1).getTechnicalID());
		assertEquals("american_express_ID", elements.get(2).getTechnicalID());
		assertEquals("salami_ID", elements.get(3).getTechnicalID());
		assertEquals("pilze_ID", elements.get(4).getTechnicalID());
	}

	/**
	 * test the search of all selects on the testSite.
	 */
	@Test
	public void searchSelect() {
		ArrayList<String> filter = new ArrayList<String>();
		filter.add(UiScannerConstants.TYP_SELECT);
		elements = webScanner.scanWebsite(filter, "", progressMonitor.getProgressMonitor());
		assertEquals(1, elements.size());
		assertEquals("land", elements.get(0).getTechnicalID());
		assertEquals(1, elements.get(0).getValue().size());
		assertEquals("DeutschlandItalienUSASchwedenAustralien", elements.get(0).getValue().get(0));
	}

	/**
	 * test the search of all XPath matches on the testSite.
	 */
	@Test
	public void searchXPath() {
		ArrayList<String> filter = new ArrayList<String>();
		filter.add(UiScannerConstants.TYP_XPATH);
		elements = webScanner.scanWebsite(filter, "//input[contains(@class, 'form-field')]",
				progressMonitor.getProgressMonitor());
		assertEquals(2, elements.size());
		assertEquals("user", elements.get(0).getTechnicalID());
		assertEquals("password", elements.get(1).getTechnicalID());
	}

	/**
	 * test the search of all XPath matches without ID on the testSite.
	 */
	@Test
	public void searchXPathWithoutID() {
		ArrayList<String> filter = new ArrayList<String>();
		filter.add(UiScannerConstants.TYP_XPATH);
		elements = webScanner.scanWebsite(filter, "//div[contains(@class, 'capital')]",
				progressMonitor.getProgressMonitor());
		assertEquals(1, elements.size());
		assertEquals("//div[contains(@class, 'capital')]", elements.get(0).getTechnicalID());
	}

	/**
	 * test the search of all XPath matches with an Empty ID on the testSite.
	 */
	@Test
	public void searchXPathWithEmtpyID() {
		ArrayList<String> filter = new ArrayList<String>();
		filter.add(UiScannerConstants.TYP_XPATH);
		elements = webScanner.scanWebsite(filter, "//div[contains(@class, 'submit-container')]",
				progressMonitor.getProgressMonitor());
		assertEquals(1, elements.size());
		assertEquals("//div[contains(@class, 'submit-container')]", elements.get(0).getTechnicalID());
	}

	/**
	 * test the search of all Elements with ID on the testSite.
	 */
	@Test
	public void searchAllWithID() {
		ArrayList<String> filter = new ArrayList<String>();
		filter.add(UiScannerConstants.TYP_ALL);
		elements = webScanner.scanWebsite(filter, "", progressMonitor.getProgressMonitor());
		assertEquals(16, elements.size());
	}

	/**
	 * Set a outline style for a webElement and checks on the webSite if the
	 * Style have been set.
	 */
	@Test
	public void setAndReadOutlineStyle() {
		webScanner.setWebElemntOutline("user", 5, "solid", "red");
		String sytleValue = webScanner.getWebElementArgument("user", "style");
		assertTrue(sytleValue.contains("outline:"));
		assertTrue(sytleValue.contains("red"));
		assertTrue(sytleValue.contains("solid"));
		assertTrue(sytleValue.contains("5px"));
	}

	/**
	 * Set a outline style for a webElement and checks on the webSite if the
	 * Style have been set.
	 */
	@Test
	public void setAndReadOutlineStyleByValue() {
		webScanner.setWebElemntOutlineValue("user", "red solid 5px");
		String sytleValue = webScanner.getWebElementArgument("user", "style");
		assertTrue(sytleValue.contains("outline:"));
		assertTrue(sytleValue.contains("red"));
		assertTrue(sytleValue.contains("solid"));
		assertTrue(sytleValue.contains("5px"));
	}
}
