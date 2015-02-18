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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.testeditor.ui.uiscanner.webscanner.UiScannerConstants;
import org.testeditor.ui.uiscanner.webscanner.UiScannerWebElement;

/**
 * Class to test the UiScannerWebElement.
 * 
 * @author dkuhlmann
 *
 */
public class UiScannerWebElementTest {

	private ArrayList<UiScannerWebElement> elements;
	private String idValue = "user";

	/**
	 * Set the the DefaultActions of a UiScannerWebElement and checks if they
	 * are set correctly.
	 */
	@Test
	public void setDefaultActionsForInputElement() {
		elements = new ArrayList<UiScannerWebElement>();
		elements.add(new UiScannerWebElement(UiScannerConstants.TYP_INPUT, idValue));
		elements.get(0).setDefaultActions();
		List<String> actions = elements.get(0).getActions();
		assertTrue(actions.contains(UiScannerConstants.ACTION_CLEAR_VALUR));
		assertTrue(actions.contains(UiScannerConstants.ACTION_ENTER_VALUE));
	}

	/**
	 * Set the the DefaultActions of a UiScannerWebElement and checks if they
	 * are set correctly.
	 */
	@Test
	public void setDefaultActionsForButtonElement() {
		elements = new ArrayList<UiScannerWebElement>();
		elements.add(new UiScannerWebElement(UiScannerConstants.TYP_BUTTON, idValue));
		elements.get(0).setDefaultActions();
		List<String> actions = elements.get(0).getActions();
		assertTrue(actions.contains(UiScannerConstants.ACTION_BUTTON_PRESS));
	}

	/**
	 * Set the the DefaultActions of a UiScannerWebElement and checks if they
	 * are set correctly.
	 */
	@Test
	public void setDefaultActionsForCeckboxElement() {
		elements = new ArrayList<UiScannerWebElement>();
		elements.add(new UiScannerWebElement(UiScannerConstants.TYP_CHECKBOX, idValue));
		assertEquals(idValue, elements.get(0).getLocator());
		elements.get(0).setDefaultActions();
		List<String> actions = elements.get(0).getActions();
		assertTrue(actions.contains(UiScannerConstants.ACTION_ENTER_VALUE));
	}

	/**
	 * Set the the DefaultActions of a UiScannerWebElement and checks if they
	 * are set correctly.
	 */
	@Test
	public void setDefaultActionsForRadioElement() {
		elements = new ArrayList<UiScannerWebElement>();
		elements.add(new UiScannerWebElement(UiScannerConstants.TYP_RADIO, idValue));
		assertEquals(idValue, elements.get(0).getName());
		elements.get(0).setDefaultActions();
		List<String> actions = elements.get(0).getActions();
		assertTrue(actions.contains(UiScannerConstants.ACTION_ENTER_VALUE));
	}

	/**
	 * Set the the DefaultActions of a UiScannerWebElement and checks if they
	 * are set correctly.
	 */
	@Test
	public void setDefaultActionsForSelectElement() {
		elements = new ArrayList<UiScannerWebElement>();
		elements.add(new UiScannerWebElement(UiScannerConstants.TYP_SELECT, idValue));
		assertEquals(idValue, elements.get(0).getTechnicalID());
		elements.get(0).setDefaultActions();
		List<String> actions = elements.get(0).getActions();
		assertTrue(actions.contains(UiScannerConstants.ACTION_SELECT_VALUE));
		assertTrue(actions.contains(UiScannerConstants.ACTION_CLEAR_VALUR));
		assertTrue(actions.contains(UiScannerConstants.ACTION_ENTER_VALUE));
	}

	/**
	 * Set a ActionsList of a UiScannerWebElement and checks if they are set
	 * correctly.
	 */
	@Test
	public void setActionsForElement() {
		List<String> actions = new ArrayList<String>();
		actions.add(UiScannerConstants.ACTION_BUTTON_PRESS);
		actions.add(UiScannerConstants.ACTION_SELECT_VALUE);
		actions.add(UiScannerConstants.ACTION_ENTER_VALUE);
		elements = new ArrayList<UiScannerWebElement>();
		elements.add(new UiScannerWebElement(UiScannerConstants.TYP_SELECT, idValue));
		elements.get(0).setActions(actions);
		assertEquals(idValue, elements.get(0).getTechnicalID());
		actions = elements.get(0).getActions();
		assertTrue(actions.contains(UiScannerConstants.ACTION_SELECT_VALUE));
		assertTrue(actions.contains(UiScannerConstants.ACTION_BUTTON_PRESS));
		assertTrue(actions.contains(UiScannerConstants.ACTION_ENTER_VALUE));

		assertFalse(actions.contains(UiScannerConstants.ACTION_CLEAR_VALUR));
	}

	/**
	 * Checks if the toString() gives a empty String back (would a the
	 * UiSacnnerWebElement gives a String back it would be shown in the Table).
	 */
	@Test
	public void elementToString() {
		elements = new ArrayList<UiScannerWebElement>();
		elements.add(new UiScannerWebElement(UiScannerConstants.TYP_SELECT, idValue));
		assertEquals("", elements.get(0).toString());
	}

	/**
	 * Checks if the toArray() gives all Values as a Array back.
	 */
	@Test
	public void elementToArray() {
		elements = new ArrayList<UiScannerWebElement>();
		elements.add(new UiScannerWebElement(UiScannerConstants.TYP_SELECT, "technicalID", "name", "locator"));
		assertEquals(UiScannerConstants.TYP_SELECT, elements.get(0).toArray().clone()[0]);
		assertEquals("technicalID", elements.get(0).toArray()[1]);
		assertEquals("name", elements.get(0).toArray()[2]);
		assertEquals("locator", elements.get(0).toArray()[3]);
		assertEquals("[]", elements.get(0).toArray()[4]);
	}
}
