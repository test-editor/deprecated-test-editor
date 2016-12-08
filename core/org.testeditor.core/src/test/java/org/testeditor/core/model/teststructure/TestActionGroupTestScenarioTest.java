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

import java.util.ArrayList;

import org.junit.Test;
import org.testeditor.core.exceptions.CorrruptLibraryException;
import org.testeditor.core.model.action.Action;
import org.testeditor.core.model.action.ActionElement;
import org.testeditor.core.model.action.ActionElementType;
import org.testeditor.core.model.action.Argument;
import org.testeditor.core.model.action.ChoiceList;
import org.testeditor.core.model.action.TechnicalBindingType;

/**
 * 
 * test for the {@link TestActionGroupTestScenario}.
 * 
 * @author llipinski
 */
public class TestActionGroupTestScenarioTest {
	/**
	 * tests the getSource method.
	 * 
	 * @throws CorrruptLibraryException
	 *             exception will be thrown if error in library was found
	 */
	@Test
	public void getSourceCode() throws CorrruptLibraryException {
		TestActionGroupTestScenario testActionGroupTestScenario = new TestActionGroupTestScenario();
		String actionGroupname = "TestActionGroupScenario";
		testActionGroupTestScenario.setActionGroupName(actionGroupname);
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
		testActionGroupTestScenario.addActionLine(new Action("MyName", arguments, null, techType,
				new ArrayList<ChoiceList>()));

		StringBuilder sourceCode = new StringBuilder();
		sourceCode.append("|note| Maske: ").append(actionGroupname).append("|\n");
		sourceCode.append(testActionGroupTestScenario.getTableSourcecode());

		assertEquals(sourceCode.toString(), testActionGroupTestScenario.getSourceCode());

	}
}
