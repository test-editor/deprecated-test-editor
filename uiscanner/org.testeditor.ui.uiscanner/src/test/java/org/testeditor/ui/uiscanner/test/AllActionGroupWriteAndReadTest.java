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

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.bind.JAXBException;

import org.junit.BeforeClass;
import org.junit.Test;
import org.testeditor.ui.uiscanner.webscanner.ScannerReadAndWriter;
import org.testeditor.ui.uiscanner.webscanner.UiScannerConstants;
import org.testeditor.ui.uiscanner.webscanner.UiScannerWebElement;

/**
 * Test class for the ScannerReadAndWriter. This Class tests only the generating
 * of the AllActionGroup.
 * 
 */
public class AllActionGroupWriteAndReadTest {
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
		elements.get(3).addValue("select_value1");
		elements.get(3).addValue("select_value2");
		elements.add(new UiScannerWebElement(UiScannerConstants.TYP_RADIO, UiScannerConstants.TYP_RADIO));
		elements.add(new UiScannerWebElement(UiScannerConstants.TYP_CHECKBOX, UiScannerConstants.TYP_CHECKBOX));
		elements.add(new UiScannerWebElement(UiScannerConstants.TYP_XPATH, UiScannerConstants.TYP_XPATH));
	}

	/**
	 * Generate a AllActionGroup and Checks if all Lines were correctly.
	 * 
	 * @throws IOException
	 *             IOException
	 * @throws JAXBException
	 *             JAXBException
	 */
	@Test
	public void generatAllActionGroup() throws JAXBException, IOException {
		String allActionGroup = readAndWriter.generateActionGroup(elements);
		assertTrue(!allActionGroup.contains(UiScannerConstants.TYP_XPATH));
		assertTrue(!allActionGroup.contains(UiScannerConstants.TYP_ALL));
		String[] allActionGroupList = allActionGroup.split(System.lineSeparator());
		assertEquals(33, allActionGroupList.length);
		assertTrue(allActionGroupList[0].equals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"));
		assertTrue(allActionGroupList[3].trim().endsWith("<action technicalBindingType=\"Button_Druecken\">"));
		assertTrue(allActionGroupList[4].trim().endsWith("<actionName locator=\"button\">button</actionName>"));
		assertTrue(allActionGroupList[6].trim().endsWith("<action technicalBindingType=\"Leere_Wert\">"));
		assertTrue(allActionGroupList[7].trim().endsWith("<actionName locator=\"input\">input</actionName>"));
		assertTrue(allActionGroupList[9].trim().endsWith("<action technicalBindingType=\"Eingabe_Wert\">"));
		assertTrue(allActionGroupList[10].trim().endsWith("<actionName locator=\"input\">input</actionName>"));
		assertTrue(allActionGroupList[12].trim().endsWith("<action technicalBindingType=\"Auswahl_Wert\">"));
		assertTrue(allActionGroupList[13].trim().endsWith("<actionName locator=\"select\">select</actionName>"));
		assertTrue(allActionGroupList[14].trim().endsWith("<argument id=\"argument_select\">"));
		assertTrue(allActionGroupList[15].trim().endsWith("<value>select_value1</value>"));
		assertTrue(allActionGroupList[16].trim().endsWith("<value>select_value2</value>"));
		assertTrue(allActionGroupList[19].trim().endsWith("<action technicalBindingType=\"Leere_Wert\">"));
		assertTrue(allActionGroupList[20].trim().endsWith("<actionName locator=\"select\">select</actionName>"));
		assertTrue(allActionGroupList[22].trim().endsWith("<action technicalBindingType=\"Eingabe_Wert\">"));
		assertTrue(allActionGroupList[23].trim().endsWith("<actionName locator=\"select\">select</actionName>"));
		assertTrue(allActionGroupList[25].trim().endsWith("<action technicalBindingType=\"Eingabe_Wert\">"));
		assertTrue(allActionGroupList[26].trim().endsWith("<actionName locator=\"radio\">radio</actionName>"));
		assertTrue(allActionGroupList[28].trim().endsWith("<action technicalBindingType=\"Eingabe_Wert\">"));
		assertTrue(allActionGroupList[29].trim().endsWith("<actionName locator=\"checkbox\">checkbox</actionName>"));
	}

}
