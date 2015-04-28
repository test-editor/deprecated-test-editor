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
package org.testeditor.ui.parts.editor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Shell;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.model.action.Argument;
import org.testeditor.core.model.action.TextType;
import org.testeditor.core.model.teststructure.TestComponent;
import org.testeditor.core.model.teststructure.TestFlow;
import org.testeditor.core.model.teststructure.TestScenario;
import org.testeditor.core.model.teststructure.TestScenarioParameterTable;
import org.testeditor.ui.parts.editor.view.TestEditView;
import org.testeditor.ui.parts.editor.view.TestEditorTestDataTransferContainer;
import org.testeditor.ui.parts.editor.view.handler.TestEditorInputObject;
import org.testeditor.ui.parts.inputparts.actioninput.TestEditorActionInputController;
import org.testeditor.ui.parts.inputparts.descriptioninput.TestEditorDescriptionInputController;

/**
 * Implementations of this interface have to manage the UI and model Classes.
 * 
 * @author karsten
 * 
 */
public interface ITestEditorController {

	/**
	 * set the testcase and open it.
	 * 
	 * @param testCase
	 *            testcase
	 * 
	 */
	void setTestFlow(TestFlow testCase);

	/**
	 * set the dirtyflag.
	 */
	void setDirty();

	/**
	 * returns the size (line count the testcase).
	 * 
	 * @return size
	 */
	int getTestFlowSize();

	/**
	 * returns an array whit strings of the line-content.
	 * 
	 * @param i
	 *            linenumber
	 * @return array of stings
	 */
	List<String> getLine(int i);

	/**
	 * returns an array of textType of a line.
	 * 
	 * @param i
	 *            linenumber
	 * @return array Text_type
	 */
	List<TextType> getTextTypes(int i);

	/**
	 * returns the testcomponent at a linenumber.
	 * 
	 * @param i
	 *            linenumber
	 * @return TestComponent TestComponent
	 */
	TestComponent getTestComponentAt(int i);

	/**
	 * removes the testComponent from the selected line.
	 * 
	 * @param selectedLine
	 *            selected line
	 * @return TestComponent
	 */
	TestComponent removeLine(int selectedLine);

	/**
	 * adds the TestComponent at a position.
	 * 
	 * @param position
	 *            position
	 * @param next
	 *            TestComponent
	 */
	void addLine(int position, TestComponent next);

	/**
	 * adds the description to the given position.
	 * 
	 * @param i
	 *            position
	 * @param newLines
	 *            new lines
	 * @param changeMode
	 *            boolean = true if change Mode
	 */
	void setDescription(int i, List<String> newLines, boolean changeMode);

	/**
	 * puts an actionline of the testcaseview to the editarea.
	 * 
	 * @param lineNumber
	 *            number of line in the testcase
	 * @param texts
	 *            array of the strings of the actionline
	 * @param posInSelection
	 *            position of the cursor in the selected element
	 */
	void setActionToEditArea(int lineNumber, List<String> texts, int posInSelection);

	/**
	 * this method set the actiongroup in the testcase in the give line with the
	 * given parameters.
	 * 
	 * @param mask
	 *            name of the mask
	 * @param actionName
	 *            name of the action on the mask
	 * @param inputTexts
	 *            inputtext
	 * @param arguments
	 *            arguments
	 * @param selectedLine
	 *            number of the selected line
	 * @param changeMode
	 *            boolean = true if change Mode
	 */

	void setActionGroup(String mask, String actionName, ArrayList<String> inputTexts, ArrayList<Argument> arguments,
			int selectedLine, boolean changeMode);

	/**
	 * this method set the actiongroup in the testcase in the give line with the
	 * given parameters.
	 * 
	 * @param testScenarioParameterTable
	 *            the TestScenarioParameterTable
	 * @param selectedLine
	 *            number of the selected line
	 * @param changeMode
	 *            boolean = true if change Mode
	 */
	void setTestScenarioParameterTable(TestScenarioParameterTable testScenarioParameterTable, int selectedLine,
			boolean changeMode);

	/**
	 * refreshs the styled text.
	 */
	void refreshStyledText();

	/**
	 * puts a selected description to the input-area.
	 * 
	 * @param selText
	 *            descriptiontext
	 * @param selectdLine
	 *            number of the line begin of the selection
	 * @param releasedLine
	 *            number of the line end of selection
	 * @param posInSelection
	 *            of the cursor in the selected description
	 */
	void putTextToInputArea(String selText, int selectdLine, int releasedLine, int posInSelection);

	/**
	 * remove the selected lines.
	 * 
	 * @return ArrayList<TestComponent>
	 */
	ArrayList<TestComponent> removeSelectedLinesAndCleanUp();

	/**
	 * remove the selected lines.
	 * 
	 * @param klickedLine
	 *            lower border
	 * @param releasedLine
	 *            upper border of selection
	 * @return ArrayList<TestComponent>
	 */
	ArrayList<TestComponent> removeLines(int klickedLine, int releasedLine);

	/**
	 * set the change of a description active.
	 * 
	 * @param selectedLine
	 *            selectedLine in testcase
	 * @param releasedLine
	 *            relesedLine in testcase
	 */
	void setDescriptionActive(int selectedLine, int releasedLine);

	/**
	 * set scenarioSelection-view active.
	 * 
	 * @param selectedLine
	 *            selectedLine in testcase
	 * @param releasedLine
	 *            relesedLine in testcase
	 */
	void setScenarioSelectionActive(int selectedLine, int releasedLine);

	/**
	 * set the change of a action active.
	 */
	void setActionActive();

	/**
	 * this method setAddMode.
	 * 
	 * @param b
	 *            boolean
	 */
	void setAddMode(boolean b);

	/**
	 * this method removes the testEditView from the TestCaseController, if this
	 * testEditView is the active one. And cleans the testEditorInputView
	 * 
	 * @param testEditView
	 *            TestEditView
	 */
	void removeTestEditView(TestEditView testEditView);

	/**
	 * this method moves lines in the testcase.
	 * 
	 * @param zeileFromLowerInTestCase
	 *            lower border
	 * @param zeileFromUpperInTestCase
	 *            upper border of selected lines
	 * @param zeileToInTestCase
	 *            target line
	 * @param insertBefore
	 *            boolean if true, then begin of the line, else end of the line
	 */

	void moveRow(int zeileFromLowerInTestCase, int zeileFromUpperInTestCase, int zeileToInTestCase, boolean insertBefore);

	/**
	 * this method sets an unparsedActionGroup in the testcase.
	 * 
	 * @param mask
	 *            mask
	 * @param actionName
	 *            action name of the
	 * @param inputTexts
	 *            inputtexts
	 * @param selectedLine
	 *            position of the change
	 */
	void setUnparsedActionGroup(String mask, String actionName, List<String> inputTexts, int selectedLine);

	/**
	 * 
	 * @return the selected line in the testcaseView
	 */
	int getSelectedLine();

	/**
	 * sets the SelectedLine in the View.
	 * 
	 * @param selectedLine
	 *            SelectedLine
	 * 
	 */
	void setSelectedLineInView(int selectedLine);

	/**
	 * marks the correspondingLine in the view.
	 * 
	 * @param selectedLine
	 *            the selected line in the testcase
	 */

	void markCorrespondingLine(int selectedLine);

	/**
	 * checks the view. If no text is in the selectedLine, it returns true, else
	 * false
	 * 
	 * @param selectedLine
	 *            the searchLine
	 * 
	 * @return true if the selected line in the view is empty
	 */
	boolean isEmptyLineInView(int selectedLine);

	/**
	 * check the exist of a given line number in the test case. When line number
	 * < size of test case, then true.
	 * 
	 * @param line
	 *            line
	 * 
	 * @return boolean
	 */
	boolean isLineInTestFlow(int line);

	/**
	 * copies the selected {@link TestComponent} and return a
	 * {@link TestEditorTestDataTransferContainer}.
	 * 
	 * @return copied TestComponents
	 */
	TestEditorTestDataTransferContainer copySelectedTestcomponents();

	/**
	 * cuts the selected {@link TestComponent} and return a
	 * {@link TestEditorTestDataTransferContainer}.
	 * 
	 * @return cut TestComponents
	 */
	TestEditorTestDataTransferContainer cutSelectedTestComponents();

	/**
	 * paste the in the {@link TestEditorActionInputController}
	 * {@link TestComponent} in the actual {@link TestFlow}.
	 * 
	 * @param lineTo
	 *            the line where the content has to be stored
	 * 
	 * @param insertBefore
	 *            true, when the insert position is before, else false
	 * @param transferObject
	 *            the transferObject
	 */
	void pasteStoredTestComponents(int lineTo, boolean insertBefore, Object transferObject);

	/**
	 * cleans the input areas for the description and the actions and close
	 * them.
	 */
	void cleanupAndCloseInputAreas();

	/**
	 * this method estimates the new position for the input (description or
	 * action). The position where the input or change should made depends on
	 * the mode (change or add) on the size of the testcase (input-position max
	 * = size of testcase). If the selected line in the view is empty, than the
	 * input should be made in this line.
	 * 
	 * @param addMode
	 *            boolean
	 * 
	 * @return position int
	 */
	int getChangePosition(boolean addMode);

	/**
	 * open up a popup-dialog to edit a description. The
	 * TestEditorDescriptionInputController is temporary replaced by the
	 * testEditorDescriptionInputController. With the method
	 * removePopupEditingControllers the original will be rested.
	 * 
	 * @param testEditorDescriptionInputController
	 *            TestEditorDescriptionInputController
	 */
	void setDescriptionControllerForPopupEditing(
			TestEditorDescriptionInputController testEditorDescriptionInputController);

	/**
	 * open up a popup-dialog to edit a action. The TestEditorInputController is
	 * temporary replaced by the testEditorInputController. With the method
	 * removePopupEditingControllers the original will be rested.
	 * 
	 * @param testEditorInputController
	 *            TestEditorInputController
	 */
	void setActionControllerForPopupEditing(TestEditorActionInputController testEditorInputController);

	/**
	 * rests the original controllers. Should be called when a popupdialog is
	 * closed.
	 */
	void removePopupEditingControllers();

	/**
	 * closes the open popupdialog.
	 */
	void closePopupDialog();

	/**
	 * returns the actual testcase.
	 * 
	 * @return TestStructure
	 */
	TestFlow getTestFlow();

	/**
	 * sets the scenario into the scenarioSelection area.
	 * 
	 * @param testComponentAt
	 *            TestScenarioParameterTable
	 * @param lineNumber
	 *            the lineNumber
	 * @throws SystemException
	 *             on reading a TestSceanrio
	 */
	void setScenarioToEditArea(TestScenarioParameterTable testComponentAt, int lineNumber) throws SystemException;

	/**
	 * pasts the in the clippboard stored components.
	 */
	void pasteComponents();

	/**
	 * 
	 * @return true, if the data table is selected, else false.
	 */
	boolean isTestDataTableSelected();

	/**
	 * cuts the selected components.
	 */
	void cutText();

	/**
	 * 
	 * @return true, if at least one component is selected, else false.
	 */
	boolean canExecuteCutCopy();

	/**
	 * copies the selected testComponents to the clipboard.
	 */
	void copyText();

	/**
	 * 
	 * @return true, if the cursor is in a editable line, else false.
	 */
	boolean canExecuteDelete();

	/**
	 * imports a file into the selected table.
	 */
	void executeFileImportToTable();

	/**
	 * 
	 * @return true, if a table is selected.
	 */
	boolean canExecutePasteTestFlow();

	/**
	 * to delegate the KeyEvent.
	 * 
	 * @param e
	 *            KeyEvent
	 */
	void doHandleKeyEvent(KeyEvent e);

	/**
	 * @param classNameOfCachedTestComponent
	 *            the classname of the TestComponent
	 * @return the last unsaved TestComponentInput
	 */
	TestEditorInputObject getLastUnSavedTestComponentInput(String classNameOfCachedTestComponent);

	/**
	 * stores the last unsaved TestComponentnInput.
	 * 
	 * @param lastUnSavedTestComponentInput
	 *            TestComponent
	 */
	void setLastUnSavedTestComponentInput(TestEditorInputObject lastUnSavedTestComponentInput);

	/**
	 * sets the hasFocus variable to false.
	 */
	void lostFocus();

	/**
	 * 
	 * @param text
	 *            the inputText
	 * @return tests the input in the widget
	 */
	boolean isInputValid(String text);

	/**
	 * saves the TestFlow.
	 */
	void save();

	/**
	 * 
	 * @return the part.
	 */
	MPart getPart();

	/**
	 * close the part.
	 */
	void closePart();

	/**
	 * 
	 * @param fullNameOfScenario
	 *            the fullName of the Scenario
	 * @return the Scenario to the parameter fullNameOfScenario or null if not
	 *         found.
	 * @throws SystemException
	 *             by reading the scenario
	 */
	TestScenario getScenarioByFullName(String fullNameOfScenario) throws SystemException;

	/**
	 * this method adds a new int to the unEditableLine.
	 * 
	 * @param unEditableLine
	 *            int number of an uneditable line
	 */
	void rememberUnEditableLine(int unEditableLine);

	/**
	 * clears the unEditableLines.
	 * 
	 */
	void clearUnEditableLines();

	/**
	 * this method checks the editable of a line. a line is not editable, if it
	 * contains a add line or the mask-name
	 * 
	 * @param lineNumber
	 *            number of the line
	 * @return true if it editable else false
	 */
	boolean isLineEditable(int lineNumber);

	/**
	 * 
	 * @param firstLine
	 *            first line of the selection
	 * @param lastLine
	 *            last line of the selection.
	 * @return true, if the one of the selected lines is editable.
	 */
	boolean isSelectionEditable(int firstLine, int lastLine);

	/**
	 * 
	 * @return true, if Library is correctly loaded
	 */
	boolean isLibraryErrorLessLoaded();

	/**
	 * sets the focus in the receiver.
	 * 
	 * @param shell
	 *            Shell
	 */

	void setFocus(Shell shell);

	/**
	 * @return the invalidChars in a String.
	 */
	String getInvalidChars();
}
