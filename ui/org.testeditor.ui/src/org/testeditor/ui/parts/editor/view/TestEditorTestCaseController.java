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
package org.testeditor.ui.parts.editor.view;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.testeditor.core.model.action.Argument;
import org.testeditor.core.model.action.IAction;
import org.testeditor.core.model.action.UnparsedActionLine;
import org.testeditor.core.model.teststructure.TestActionGroup;
import org.testeditor.core.model.teststructure.TestDescription;
import org.testeditor.core.model.teststructure.TestDescriptionTestCase;
import org.testeditor.ui.constants.IconConstants;

/**
 * 
 * specialized {@link TestEditorController} class for the TestCase.
 * 
 */
public class TestEditorTestCaseController extends TestEditorController {

	public static final String ID = "org.testeditor.ui.partdescriptor.testCaseView";

	/**
	 * 
	 * @param part
	 *            of gui
	 */
	@Inject
	public TestEditorTestCaseController(MPart part) {
		super(part);
		part.setIconURI(IconConstants.ICON_URI_TESTCASE);
	}

	@Override
	public void setActionGroup(String mask, String actionName, ArrayList<String> inputLineParts,
			ArrayList<Argument> arguments, int selectedLine, boolean changeMode) {
		int position = selectedLine;
		TestActionGroup testComponent = getActionGroupService().createTestActionGroupTestCase(
				getTestFlow().getRootElement(), mask, inputLineParts, arguments);
		getActionInputController().setLastSelectedMask(mask);
		super.setActionGroup(mask, changeMode, position, testComponent);
	}

	/**
	 * creates the descriptionArray.
	 * 
	 * @param newLines
	 *            List<String>
	 * @return List<TestDescription>
	 */
	@Override
	protected List<TestDescription> createDescriptionsArray(List<String> newLines) {
		List<TestDescription> descriptions = new ArrayList<TestDescription>();
		for (String line : newLines) {
			TestDescription desc = new TestDescriptionTestCase(line);
			descriptions.add(desc);
		}
		return descriptions;
	}

	/**
	 * 
	 * creates an unparsedActionLine.
	 * 
	 * @param mask
	 *            Mask
	 * @param inputTexts
	 *            inputTexts
	 * @return TestActionGroup whit the unparsed Line
	 */
	@Override
	protected TestActionGroup createUnparsedActionLine(String mask, List<String> inputTexts) {
		ArrayList<Argument> arguments = new ArrayList<Argument>();
		TestActionGroup testActionGr = getActionGroupService().createTestActionGroupTestCase(
				getTestFlow().getRootElement(), mask, inputTexts, arguments);
		getActionInputController().setLastSelectedMask(mask);
		IAction lcAction = testActionGr.getActionLines().get(0);
		UnparsedActionLine unparsed = new UnparsedActionLine(inputTexts.get(0));
		testActionGr.removeActionLine(lcAction);
		testActionGr.addActionLine(unparsed);
		return testActionGr;
	}

	@Override
	public String getInvalidChars() {
		return "";
	}
}
