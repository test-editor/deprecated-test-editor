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
package org.testeditor.ui.uiscanner.webscanner;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.SessionNotFoundException;
import org.openqa.selenium.remote.UnreachableBrowserException;
import org.testeditor.ui.uiscanner.ui.UiScannerTranslationService;

/**
 * This scanner.
 * 
 * @author dkuhlmann
 * 
 */
public abstract class Scanner {
	private static final String LINUX = "Linux";
	private static final String MAC_OS = "Mac OS";
	private static final String WINDOWS = "Windows";

	private WebDriver webDriver;
	private boolean webDriverAktive = false;

	private static final Logger LOGGER = Logger.getLogger(Scanner.class);

	@Inject
	private UiScannerTranslationService translate;

	/**
	 * Checks if the webDriver is still aktive.
	 * 
	 * @return True if the webdriver is opend else false.
	 */
	public boolean isWebDriverAktive() {
		if (webDriver != null) {
			try {
				webDriver.getCurrentUrl();
			} catch (UnreachableBrowserException e1) {
				webDriverAktive = false;
			} catch (NoSuchWindowException e1) {
				webDriverAktive = false;
			} catch (SessionNotFoundException e1) {
				webDriverAktive = false;
			}
		} else {
			webDriverAktive = false;
		}
		return webDriverAktive;
	}

	/**
	 * Open the browser and a webDriver session. Navigate to the URL.
	 * 
	 * @param browserName
	 *            Browser to open.
	 * @param url
	 *            URL to the Site that should be opened.
	 */
	public void openBrowser(String browserName, String url) {

		String osName = System.getProperty("os.name");
		LOGGER.debug("open browser IN PROCESS - operating System: " + osName + ", browserName: " + browserName);

		if ((webDriver != null) && webDriverAktive) {
			try {
				navigateToURL(url);
			} catch (Exception e1) {
				webDriverAktive = false;
				openBrowser(browserName, url);
			}
		} else {
			if (UiScannerConstants.BROWSER_FIREFOX.equalsIgnoreCase(browserName)) {
				openFirefox(osName);
			} else if (UiScannerConstants.BROWSER_IE.equalsIgnoreCase(browserName)) {
				openIE(osName);
			} else if ("chrome".equalsIgnoreCase(browserName)) {
				openChrome(osName);
			} else if (UiScannerConstants.BROWSER_HTMLUNIT.equals(browserName)) {
				HtmlUnitDriver driver = new HtmlUnitDriver();
				driver.setJavascriptEnabled(true);
				webDriver = driver;
				webDriverAktive = true;
			}

			try {
				navigateToURL(url);
			} catch (Exception e1) {
				webDriverAktive = false;
				MessageDialog.openError(Display.getDefault().getActiveShell(),
						translate.translate("%DIALOG_BROWSER_NOT_STARTABLE_TITLE"),
						translate.translate("%DIALOG_BROWSER_NOT_STARTABLE_MESSAGE"));
				throw e1;
			}
		}

	}

	/**
	 * Helper method for openBrowser which handles the specifics to open IE.
	 * 
	 * @param osName
	 *            the name of the OS (should contain "Windows", "Mac OS" or
	 *            "Linux")
	 */
	private void openIE(String osName) {
		String webDriverServerPropertyPath = "SLIM_CMD_VAR_IEWEBDRIVERSERVER";
		String ieWebDriverServerPath = System.getProperty(webDriverServerPropertyPath);
		if (ieWebDriverServerPath != null && !(ieWebDriverServerPath.isEmpty())) {
			System.setProperty("webdriver.ie.driver", ieWebDriverServerPath);
		}
		DesiredCapabilities cap = DesiredCapabilities.internetExplorer();
		cap.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, "true");
		webDriver = new InternetExplorerDriver(cap);
		webDriverAktive = true;
	}

	/**
	 * Helper method for openBrowser which handles the specifics to open Chrome.
	 * 
	 * @param osName
	 *            the name of the OS (should contain "Windows", "Mac OS" or
	 *            "Linux")
	 */
	private void openChrome(String osName) {
		String driverPath = System.getProperty("SLIM_CMD_VAR_CHROMEWEBDRIVERSERVER");
		if (!(new File(driverPath)).exists()) {
			String logMessage = "driverPath: '" + driverPath + " does not exist";
			MessageDialog.openError(Display.getDefault().getActiveShell(), "UiScanner - No Chrome Driver", logMessage);
		} else {
			System.setProperty("webdriver.chrome.driver", driverPath);
			webDriver = new ChromeDriver();
			webDriverAktive = true;
		}
	}

	/**
	 * Helper method for openBrowser which handles the specifics to open
	 * Firefox.
	 * 
	 * @param osName
	 *            the name of the OS (should contain "Windows", "Mac OS" or
	 *            "Linux")
	 */
	private void openFirefox(String osName) {
		String browserPath = System.getProperty("path.browser");
		if (!(new File(browserPath).exists())) {
			if (!(new File(System.getProperty("webdriver.firefox.bin"))).exists()) {
				String logMessage = "browserPath: '" + browserPath + " does not exist";
				MessageDialog.openError(Display.getDefault().getActiveShell(), "UiScanner - No Browser", logMessage);
			}
		} else {
			if (osName.contains(WINDOWS)) {
				System.setProperty("webdriver.firefox.bin", browserPath);
			} else if (osName.contains(LINUX)) {
				System.setProperty("webdriver.firefox.bin", browserPath);
				System.setProperty("webdriver.firefox.profile", "testing");
			} else if (osName.contains(MAC_OS)) {
				System.setProperty("webdriver.firefox.bin", browserPath);
			}
		}
		webDriver = new FirefoxDriver();
		webDriverAktive = true;
	}

	/**
	 * 
	 /** Navigate to the given URl.
	 * 
	 * @param url
	 *            String url from the Website.
	 * 
	 * @throws UnreachableBrowserException
	 *             UnreachableBrowserException
	 * 
	 */
	public void navigateToURL(String url) throws UnreachableBrowserException {
		if (url.toLowerCase().startsWith("http://") || url.toLowerCase().startsWith("https://")
				|| url.toLowerCase().startsWith("file:")) {
			webDriver.get(url);
		} else {
			webDriver.get("http://" + url);
		}
	}

	/**
	 * quite and close the running webDriver.
	 */
	public void quit() {
		try {
			if (webDriver != null) {
				webDriver.close();
				webDriver.quit();
				webDriverAktive = false;
			}
		} catch (Exception e1) {
			webDriverAktive = false;
		}
	}

	/**
	 * returns the current url of the webDriver. if the webdriver is not active
	 * its will return null.
	 * 
	 * @return String current URL
	 */
	public String getcurrentURL() {
		if (isWebDriverAktive()) {
			return webDriver.getCurrentUrl();
		}
		return null;
	}

	/**
	 * Search all available values of the given WebElement.
	 * 
	 * @param elem
	 *            WebElement
	 * @return List<String> values of the given WebElement
	 */
	protected List<String> getValuesOfElement(WebElement elem) {
		List<String> values = new ArrayList<String>();
		for (String value : elem.getText().split("\n")) {
			values.add(value);
		}
		return values;
	}

	/**
	 * Set the argument from the WebElement with the ID into the chosen color.
	 * 
	 * @param id
	 *            ID from WebElement.
	 * @param styleArgument
	 *            argument for the style of the WebElement to be set (outline,
	 *            border, background).
	 * @param width
	 *            width for the outline.
	 * @param style
	 *            style for the outline (bsp: solid, none, dotted, double)
	 * @param color
	 *            color for the outline.
	 */
	public void setWebElementArgument(String id, String styleArgument, int width, String style, String color) {
		try {
			if (webDriver != null && webDriverAktive) {
				WebElement webElement = webDriver.findElement(By.id(id));
				if (webElement != null) {
					((JavascriptExecutor) webDriver).executeScript("document.getElementById('" + id + "').style."
							+ styleArgument + " = '" + width + "px " + style + " " + color + "';");
				}
			}
		} catch (UnreachableBrowserException e) {
			webDriverAktive = false;
		}
	}

	/**
	 * Set the argument from the WebElement with the ID into the chosen color.
	 * 
	 * @param id
	 *            ID from WebElement.
	 * @param styleArgument
	 *            argument for the style of the WebElement to be set (outline,
	 *            border, background).
	 * @param value
	 *            String Value for the argument.
	 */
	public void setWebElementArgument(String id, String styleArgument, String value) {
		try {
			if (webDriver != null && webDriverAktive) {
				WebElement webElement = webDriver.findElement(By.id(id));
				if (webElement != null) {
					((JavascriptExecutor) webDriver).executeScript("document.getElementById('" + id + "').style."
							+ styleArgument + " = '" + value + "';");
				}
			}
		} catch (UnreachableBrowserException e) {
			webDriverAktive = false;
		}
	}

	/**
	 * Set the argument from the WebElement with the ID into the chosen color.
	 * 
	 * @param id
	 *            ID from WebElement.
	 * @param argument
	 *            argument from WebElement to be set (outline, border,
	 *            background).
	 * @return String value of the argument
	 */
	public String getWebElementArgument(String id, String argument) {
		String result = null;
		try {
			if (webDriver != null && webDriverAktive) {
				WebElement webElement = webDriver.findElement(By.id(id));
				if (webElement != null) {
					result = webElement.getAttribute(argument);
				}
			}
		} catch (UnreachableBrowserException e) {
			webDriverAktive = false;
		}
		return result;
	}

	/**
	 * Set the outline from the WebElement with the ID into the chosen color.
	 * 
	 * @param id
	 *            ID from WebElement.
	 * @param value
	 *            String Value for the argument.
	 */
	public void setWebElemntOutlineValue(String id, String value) {
		setWebElementArgument(id, "outline", value);
	}

	/**
	 * Set the outline from the WebElement with the ID into the chosen color.
	 * 
	 * @param id
	 *            ID from WebElement.
	 * @param width
	 *            width for the outline.
	 * @param style
	 *            style for the outline (bsp: solid, none, dotted, double)
	 * @param color
	 *            color for the outline.
	 */
	public void setWebElemntOutline(String id, int width, String style, String color) {
		setWebElementArgument(id, "outline", width, style, color);
	}

	/**
	 * returns the created WebDriver.
	 * 
	 * @return WebDriver
	 */
	protected WebDriver getWebDriver() {
		return webDriver;
	}

	/**
	 * switch the WebDriver to the given iFrame.
	 * 
	 * @param elem
	 *            WebElement (iFrame).
	 */
	protected void switchToIframe(WebElement elem) {
		webDriver.switchTo().frame(elem);
	}

	/**
	 * switch back to the defaultContent.
	 * 
	 */
	protected void switchToDefaultContent() {
		webDriver.switchTo().defaultContent();
	}

	/**
	 * Find and return all checkbox WebElement on the website.
	 * 
	 * @return List<WebElement> List of all founded checkbox Elements
	 */
	protected abstract List<WebElement> findCheckbox();

	/**
	 * Find and return all radiobuttons on the website.
	 * 
	 * @return List<WebElement> founded radiobuttons.
	 */
	protected abstract List<WebElement> findRadioElement();

	/**
	 * Find and return all input WebElements on the website. That includes the
	 * inputs, textarea and fieldset.
	 * 
	 * @return List<WebElement> List of all founded input Elements
	 */
	protected abstract List<WebElement> findInputs();

	/**
	 * Find and return all button WebElements on the website. That includes the
	 * submits, rests, div buttons and normal buttons.
	 * 
	 * @return List<WebElement> List of all founded Button Elements
	 */
	protected abstract List<WebElement> findButtons();

	/**
	 * Finds and return all Select WebElements on the Website.
	 * 
	 * @return List<WebElement>
	 */
	protected abstract List<WebElement> findSelects();

	/**
	 * Finds and return all WebElements with an ID on the Website.
	 * 
	 * @return List<WebElement>
	 */
	protected abstract List<WebElement> findAll();

	/**
	 * Finds and return all WebElements with the given xPath on the Website.
	 * 
	 * @param xPath
	 *            String for the xPath search.
	 * @return List<WebElement>
	 */
	protected abstract List<WebElement> findXpath(String xPath);

	/**
	 * Finds and return all iFrame WebElements.
	 * 
	 * @return List<WebElement>
	 */
	protected abstract List<WebElement> findIframes();

}