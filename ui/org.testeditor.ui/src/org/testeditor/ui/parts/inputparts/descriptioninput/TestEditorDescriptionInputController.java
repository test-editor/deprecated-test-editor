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
package org.testeditor.ui.parts.inputparts.descriptioninput;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.swt.widgets.Composite;
import org.testeditor.ui.constants.TestEditorEventConstants;
import org.testeditor.ui.parts.editor.ITestEditorController;
import org.testeditor.ui.parts.editor.view.TestEditorController;
import org.testeditor.ui.parts.inputparts.AbstractTestEditorInputPartController;

/**
 * 
 * this class is the controller for the edit view part.
 * 
 */
public class TestEditorDescriptionInputController extends AbstractTestEditorInputPartController {

	public static final String ID = "org.testeditor.ui.partdescriptor.testStructureEditor.Description";

	@Inject
	private IEclipseContext context;
	@Inject
	private IEventBroker eventBroker;

	private TestEditorDescriptionInputView editArea;

	/**
	 * this method is called when this part gets the focus. This method is
	 * necessary to handle the CTRL+Enter-Event. Don't delete the empty method.
	 */
	@Focus
	public void onFocus() {
		editArea.setCommitToDefaultButton();
	}

	/**
	 * 
	 * @param parent
	 *            composite
	 */
	@PostConstruct
	public void createControls(Composite parent) {
		editArea = ContextInjectionFactory.make(TestEditorDescriptionInputView.class, context);
		editArea.createUI(parent);
	}

	/**
	 * this method is called, when the TestCaseView-part is changed.
	 * 
	 * @param testEditorController
	 *            ITestEditorController
	 */
	public void setTestCaseController(ITestEditorController testEditorController) {
		if (!editArea.isNewTestEditorController(testEditorController)) {
			editArea.cleanInput();
			editArea.setTestCaseControler(testEditorController);
			setAddMode(true);
		}
		editArea.enableViews();

	}

	/**
	 * set the input in the description input part.
	 * 
	 * @param description
	 *            the description
	 * @param lineNumber
	 *            the linenumber
	 * @param releasedLine
	 *            the releasedLine
	 * @param cursorPosInLine
	 *            the position of the cursor in the line
	 */
	public void setDescriptonTextToChangeable(String description, int lineNumber, int releasedLine, int cursorPosInLine) {
		editArea.setDescriptonTextToChangeable(description, lineNumber, releasedLine, cursorPosInLine);
	}

	/**
	 * put the text from the view area to the edit area.
	 * 
	 * @param selText
	 *            the selected text
	 * @param selectedLine
	 *            the line-number of the begin of the selection
	 * @param releasedLine
	 *            the line-number of the end of the selection
	 * @param cursorPosInLine
	 *            position of the cursor in the line.
	 * 
	 */
	public void putTextToInputArea(String selText, int selectedLine, int releasedLine, int cursorPosInLine) {
		editArea.setDescriptonTextToChangeable(selText, selectedLine, releasedLine, cursorPosInLine);
		editArea.setDescriptionActive();
		setAddMode(false);
	}

	/**
	 * adds the input, in the input area, to the test case.
	 */
	public void addInputLine() {
		editArea.changeInputInView();
	}

	/**
	 * set the description input active.
	 * 
	 * @param selectedLine
	 *            number selectedLine in testcase
	 * @param releasedLine
	 *            number relesedLine in testcase
	 * 
	 */
	public void setDescriptionActive(int selectedLine, int releasedLine) {
		editArea.setSelectedLine(selectedLine);
		editArea.setReleasedLine(releasedLine);
		editArea.cleanInput();
		editArea.setDescriptionActive();

	}

	/**
	 * set the add mode in the editArea.
	 * 
	 * @param b
	 *            boolean
	 */
	public void setAddMode(boolean b) {
		editArea.setAddMode(b);

	}

	/**
	 * clean the DescriptionInputArea and close them.
	 */
	public void cleanViewsSynchron() {
		cleanViews();
	}

	/**
	 * clean the DescriptionInputArea and close them.
	 */
	private void cleanViews() {
		editArea.cleanInput();
		setAddMode(true);
	}

	/**
	 * set the parameter enable-close-after-commit.
	 * 
	 * @param b
	 *            boolean
	 */
	public void setPopupmode(boolean b) {
		editArea.setPopupmode(b);
	}

	/**
	 * disables the views.
	 */
	public void disableViews() {
		editArea.disabelViews();

	}

	/**
	 * removes the testEditorController, if its equal to the stored in this
	 * object.
	 * 
	 * @param testEditorController
	 *            ITestEditorController
	 */
	public void removeTestCaseController(TestEditorController testEditorController) {
		editArea.removeTestEditorController(testEditorController);
	}

	@Override
	public void cacheInput(@UIEventTopic(TestEditorEventConstants.CACHE_TEST_COMPONENT_OF_PART_TEMPORARY) Object obj) {
		if (editArea != null) {
			editArea.cacheInput(eventBroker);
		}

	}

	@Override
	public void removeTestEditorController() {
		editArea.setTestCaseControler(null);
	}

	@Override
	public void cleanViewsAsynchron() {
		cleanViews();

	}

}