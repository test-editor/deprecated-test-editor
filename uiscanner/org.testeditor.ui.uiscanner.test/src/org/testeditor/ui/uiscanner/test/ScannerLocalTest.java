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

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URI;

import org.junit.Ignore;
import org.junit.Test;
import org.testeditor.ui.uiscanner.webscanner.UiScannerConstants;
import org.testeditor.ui.uiscanner.webscanner.WebScanner;

/**
 * Local tests for the UiScanner, the HTMLUnitScanner for the Server dont allow
 * JavaScriptExecutor.
 * 
 * @author dkuhlmann
 * 
 */
public class ScannerLocalTest {
	private String fireFoxPath = "D:/dev/workspaces/luna_te/browser/windows/FirefoxPortable.exe";
	private String iEPath = "D:/dev/workspaces/luna_te/te/fitnesse/org.testeditor.fixture.lib/IEDriverServer_32.exe ";
	private String chromePath = "C:/Users/dkuhlmann/AppData/Local/Google/Chrome/Application/chrome.exe";
	private final URI webIndexPage = new File("resources/web/index.html").toURI();

	/**
	 * Set a outline style for a webElement and checks on the webSite if the
	 * Style have been set.
	 */
	@Ignore("only for local test")
	@Test
	public void openFireFoxBrowser() {
		System.setProperty("webdriver.firefox.bin", fireFoxPath);
		WebScanner scanner = new WebScanner();
		scanner.openBrowser(UiScannerConstants.BROWSER_FIREFOX, webIndexPage.toString());
		assertTrue(scanner.isWebDriverAktive());
		scanner.quit();
	}

	/**
	 * Set a outline style for a webElement and checks on the webSite if the
	 * Style have been set.
	 */
	@Ignore("Chrome is not suportet and only for local test")
	@Test
	public void openChromeBrowser() {
		System.setProperty("SLIM_CMD_VAR_CHROMEWEBDRIVERSERVER", chromePath);
		WebScanner scanner = new WebScanner();
		scanner.openBrowser(UiScannerConstants.BROWSER_CHROME, webIndexPage.toString());
		assertTrue(scanner.isWebDriverAktive());
		scanner.quit();
	}

	/**
	 * Set a outline style for a webElement and checks on the webSite if the
	 * Style have been set.
	 */
	@Ignore("only for local test")
	@Test
	public void openIEBrowser() {
		System.setProperty("SLIM_CMD_VAR_IEWEBDRIVERSERVER", iEPath);
		WebScanner scanner = new WebScanner();
		scanner.openBrowser(UiScannerConstants.BROWSER_IE, webIndexPage.toString());
		assertTrue(scanner.isWebDriverAktive());
		scanner.quit();
	}

}
