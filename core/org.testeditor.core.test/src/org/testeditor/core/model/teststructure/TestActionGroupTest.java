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
package org.testeditor.core.model.teststructure;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.junit.Test;
import org.testeditor.core.exceptions.CorrruptLibraryException;
import org.testeditor.core.model.action.Action;
import org.testeditor.core.model.action.ActionElement;
import org.testeditor.core.model.action.ActionElementType;
import org.testeditor.core.model.action.Argument;
import org.testeditor.core.model.action.ChoiceList;
import org.testeditor.core.model.action.TechnicalBindingType;
import org.testeditor.core.model.action.TextType;

/**
 * 
 * test for the {@link TestActionGroup}.
 * 
 * @author llipinski
 */
public class TestActionGroupTest {

	/**
	 * testInitialization.
	 * 
	 * @throws Exception
	 *             Exception
	 */
	@Test
	public void testInitialization() throws Exception {
		TestActionGroupTestCase testActionGroup = new TestActionGroupTestCase();
		assertNotNull("Null Header not allowed", testActionGroup.getActionGroupName());
		assertNotNull("Null ActionLines not allowed", testActionGroup.getActionLines());
		// assertNotNull("Null SourceCode not allowed",
		// testActionGroup.getSourceCode());
	}

	/**
	 * test getSourceCode().
	 */
	@Test
	public void testGetSourceCode() {
		TestActionGroupTestCase testActionGroup = new TestActionGroupTestCase();
		testActionGroup.setActionGroupName("Test");
		assertEquals(testActionGroup.getSourceCode().substring(0, 13), "# Maske: Test");
	}

	/**
	 * test getSourceCode() long version.
	 * 
	 * @throws CorrruptLibraryException
	 *             exception will be thrown if error in library was found
	 */
	@Test
	public void testGetSourceCodeLong() throws CorrruptLibraryException {
		TestActionGroupTestCase testActionGroup = new TestActionGroupTestCase();
		testActionGroup.setActionGroupName("Test");
		ArrayList<Argument> arguments = new ArrayList<Argument>();
		arguments.add(new Argument("password_locator", "Password"));
		arguments.add(new Argument("Ufhat869#+?", "Ufhat869#+?"));

		ArrayList<ActionElement> actionParts = new ArrayList<ActionElement>();
		StringBuilder ersterpart = new StringBuilder("gebe in das Feld");
		actionParts.add(new ActionElement(0, ActionElementType.TEXT, ersterpart.toString(), ""));
		actionParts.add(new ActionElement(1, ActionElementType.ACTION_NAME, "", ""));
		actionParts.add(new ActionElement(2, ActionElementType.TEXT, "den Wert", ""));
		actionParts.add(new ActionElement(3, ActionElementType.ARGUMENT, "", ""));
		actionParts.add(new ActionElement(4, ActionElementType.TEXT, "ein", ""));

		TechnicalBindingType techType = new TechnicalBindingType("Eingabe_Wert", "Wert eingeben", actionParts, 0);
		Action action = new Action("MyTest", arguments, null, techType, new ArrayList<ChoiceList>());
		testActionGroup.addActionLine(action);

		List<String> lines = getActionLines(testActionGroup.getSourceCode());

		assertEquals("invalid first row: " + lines.get(0), lines.get(0), "# Maske: Test");
		assertEquals("invalid second row: " + lines.get(1), lines.get(1), "-!|script|");
	}

	/**
	 * test getTextTypes().
	 * 
	 * @throws CorrruptLibraryException
	 *             exception will be thrown if error in library was found
	 * 
	 */
	@Test
	public void testGetTextTypes() throws CorrruptLibraryException {
		TestActionGroupTestCase testActionGroup = new TestActionGroupTestCase();
		ArrayList<Argument> arguments = new ArrayList<Argument>();
		arguments.add(new Argument("Password", "Password"));
		arguments.add(new Argument("Ufhat869#+?", "Ufhat869#+?"));

		ArrayList<ActionElement> actionParts = new ArrayList<ActionElement>();
		StringBuilder ersterpart = new StringBuilder("gebe in das Feld");
		actionParts.add(new ActionElement(0, ActionElementType.TEXT, ersterpart.toString(), ""));
		actionParts.add(new ActionElement(1, ActionElementType.ACTION_NAME, "", ""));
		actionParts.add(new ActionElement(2, ActionElementType.TEXT, "den Wert", ""));
		actionParts.add(new ActionElement(3, ActionElementType.ARGUMENT, "", ""));
		actionParts.add(new ActionElement(4, ActionElementType.TEXT, "ein", ""));

		TechnicalBindingType techType = new TechnicalBindingType("Eingabe_Wert", "Wert eingeben", actionParts, 0);
		Action action = new Action("MyTest", arguments, null, techType, new ArrayList<ChoiceList>());
		testActionGroup.addActionLine(action);
		assertTrue(testActionGroup.getTexts().get(0).equalsIgnoreCase(ersterpart.append(" ").toString()));

		assertEquals(TextType.TEXT, testActionGroup.getActionLines().get(0).getTextTypes().get(0));
	}

	/**
	 * Creates an array of lines from a given String including \n for line
	 * separation.
	 * 
	 * @param linesAsString
	 *            input string
	 * @return lines
	 */
	private List<String> getActionLines(String linesAsString) {
		StringTokenizer stringTokenizer = new StringTokenizer(linesAsString, "\n");
		List<String> lines = new ArrayList<String>();

		while (stringTokenizer.hasMoreTokens()) {
			lines.add(stringTokenizer.nextToken());
		}

		return lines;
	}

}
