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

import java.util.ArrayList;

import org.junit.BeforeClass;
import org.junit.Test;
import org.testeditor.ui.uiscanner.webscanner.ScannerReadAndWriter;
import org.testeditor.ui.uiscanner.webscanner.UiScannerConstants;
import org.testeditor.ui.uiscanner.webscanner.UiScannerWebElement;

/**
 * Test class for the ScnnerReadAndWriter. This Class tests only the generating
 * of the elementlsit.
 * 
 */
public class ElementListWriteAndReadTest {
	private static ScannerReadAndWriter readAndWriter;
	private static ArrayList<UiScannerWebElement> elements;

	/**
	 * creates a new ScannerReadAndWriter. And fills the Elements list with
	 * UiScannerWebElements.
	 */
	@BeforeClass
	public static void ini() {
		readAndWriter = new ScannerReadAndWriter();
		elements = new ArrayList<UiScannerWebElement>();
		elements.add(new UiScannerWebElement(UiScannerConstants.TYP_ALL, UiScannerConstants.TYP_ALL));
		elements.add(new UiScannerWebElement(UiScannerConstants.TYP_BUTTON, UiScannerConstants.TYP_BUTTON));
		elements.add(new UiScannerWebElement(UiScannerConstants.TYP_INPUT, UiScannerConstants.TYP_INPUT));
		elements.add(new UiScannerWebElement(UiScannerConstants.TYP_SELECT, UiScannerConstants.TYP_SELECT));
		elements.add(new UiScannerWebElement(UiScannerConstants.TYP_RADIO, UiScannerConstants.TYP_RADIO));
		elements.add(new UiScannerWebElement(UiScannerConstants.TYP_CHECKBOX, UiScannerConstants.TYP_CHECKBOX));
		elements.add(new UiScannerWebElement(UiScannerConstants.TYP_XPATH, UiScannerConstants.TYP_XPATH));
	}

	/**
	 * Generate a ElementList and Checks if all Lines were correctly.
	 */
	@Test
	public void generatElementList() {
		String[] elementList = readAndWriter.generateELementList(elements).split(System.lineSeparator());
		assertEquals(8, elementList.length);
		assertEquals(UiScannerConstants.TYP_ALL + "=" + UiScannerConstants.TYP_ALL, elementList[1]);
		assertEquals(UiScannerConstants.TYP_BUTTON + "=" + UiScannerConstants.TYP_BUTTON, elementList[2]);
		assertEquals(UiScannerConstants.TYP_INPUT + "=" + UiScannerConstants.TYP_INPUT, elementList[3]);
		assertEquals(UiScannerConstants.TYP_SELECT + "=" + UiScannerConstants.TYP_SELECT, elementList[4]);
		assertEquals(UiScannerConstants.TYP_RADIO + "=" + UiScannerConstants.TYP_RADIO, elementList[5]);
		assertEquals(UiScannerConstants.TYP_CHECKBOX + "=" + UiScannerConstants.TYP_CHECKBOX, elementList[6]);
		assertEquals(UiScannerConstants.TYP_XPATH + "=" + UiScannerConstants.TYP_XPATH, elementList[7]);
	}
}
