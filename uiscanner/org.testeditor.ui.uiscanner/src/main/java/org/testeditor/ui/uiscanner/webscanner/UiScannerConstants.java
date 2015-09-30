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

import java.util.Arrays;
import java.util.List;

/**
 * Class for all the Constants of the UiScanner.
 * 
 * @author dkuhlmann
 * 
 */
public final class UiScannerConstants {

	/**
	 * construktor.
	 */
	private UiScannerConstants() {

	}

	public static final String DEFAULT_XPATH_SAMPLE = "//div[contains(@class, 'button')]";
	public static final String DEFAULT_URL = "http://www.google.de";

	// Types of Webelements
	public static final String TYP_BUTTON = "button";
	public static final String TYP_INPUT = "input";
	public static final String TYP_SELECT = "select";
	public static final String TYP_CHECKBOX = "checkbox";
	public static final String TYP_RADIO = "radio";
	public static final String TYP_XPATH = "XPath";
	public static final String TYP_ALL = "unkown";
	public static final List<String> ALL_TYPES = Arrays.asList(UiScannerConstants.TYP_BUTTON,
			UiScannerConstants.TYP_INPUT, UiScannerConstants.TYP_CHECKBOX, UiScannerConstants.TYP_RADIO,
			UiScannerConstants.TYP_SELECT);

	// Actions
	public static final String ACTION_BUTTON_PRESS = "Button_Druecken";
	public static final String ACTION_SELECT_VALUE = "Auswahl_Wert";
	public static final String ACTION_CLEAR_VALUR = "Leere_Wert";
	public static final String ACTION_ENTER_VALUE = "Eingabe_Wert";
	public static final String ACTION_START_BROWSER = "Starte_Browser";
	public static final String ACTION_CLOSE_BROWSER = "Beende_Browser";
	public static final String ACTION_NAVIGATE_TO_SITE = "Navigiere_auf_Seite";
	public static final String ACTION_WAIT_SECOUNDS = "Warte_Sekunden";
	public static final String ACTION_CHECK_VALUE = "Pruefe_Wert_vorhanden";
	public static final String ACTION_CHECK_NOT_VALUE = "Pruefe_Wert_nicht_vorhanden";

	// Browser values
	public static final String BROWSER_FIREFOX = "Firefox";
	public static final String BROWSER_IE = "IE";
	public static final List<String> BROWSERS = Arrays.asList(BROWSER_IE, BROWSER_FIREFOX);
	public static final String BROWSER_HTMLUNIT = "HTMLUNITDRIVER";
	public static final String BROWSER_CHROME = "chrome";

	// tagnames
	public static final String TAGNAME_BUTTON = "button";
	public static final String TAGNAME_INPUT = "input";
	public static final String TAGNAME_SELECT = "select";
	public static final String TAGNAME_CHECKBOX = "checkbox";
	public static final String TAGNAME_RADIO = "radio";
	public static final String TAGNAME_RESET = "reset";
	public static final String TAGNAME_SUBMIT = "submit";
	public static final String TAGNAME_TEXTAREA = "textarea";
	public static final String TAGNAME_DIV = "div";
	public static final String TAGNAME_LINK = "a";

	// web element Atributs
	public static final String ATTRIBUTE_TYPE = "type";
	public static final String ATTRIBUTE_CLASS = "class";
	public static final String ATTRIBUTE_ID = "id";

	public static final String ACTIONTRIMMER = ", ";

}
