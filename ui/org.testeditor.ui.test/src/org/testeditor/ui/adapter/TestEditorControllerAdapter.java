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
package org.testeditor.ui.adapter;

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
import org.testeditor.ui.parts.editor.ITestEditorController;
import org.testeditor.ui.parts.editor.view.TestEditView;
import org.testeditor.ui.parts.editor.view.TestEditorTestDataTransferContainer;
import org.testeditor.ui.parts.editor.view.handler.TestEditorInputObject;
import org.testeditor.ui.parts.inputparts.actioninput.TestEditorActionInputController;
import org.testeditor.ui.parts.inputparts.descriptioninput.TestEditorDescriptionInputController;

/**
 * 
 * TestAdapter for mocking in JUNIT tests.
 * 
 */
public class TestEditorControllerAdapter implements ITestEditorController {

	@Override
	public void setTestFlow(TestFlow testCase) {
	}

	@Override
	public void setDirty() {
	}

	@Override
	public int getTestFlowSize() {
		return 0;
	}

	@Override
	public ArrayList<String> getLine(int i) {
		return null;
	}

	@Override
	public ArrayList<TextType> getTextTypes(int i) {
		return null;
	}

	@Override
	public TestComponent getTestComponentAt(int i) {
		return null;
	}

	@Override
	public TestComponent removeLine(int selectedLine) {
		return null;
	}

	@Override
	public void addLine(int position, TestComponent next) {
	}

	@Override
	public void setDescription(int i, List<String> newLines, boolean changeMode) {
	}

	@Override
	public void setActionToEditArea(int lineNumber, ArrayList<String> texts, int posInSelection) {
	}

	@Override
	public void setActionGroup(String mask, String actionName, ArrayList<String> inputTexts,
			ArrayList<Argument> arguments, int selectedLine, boolean changeMode) {
	}

	@Override
	public void setTestScenarioParameterTable(TestScenarioParameterTable testScenarioParameterTable, int selectedLine,
			boolean changeMode) {
	}

	@Override
	public void refreshStyledText() {
	}

	@Override
	public void putTextToInputArea(String selText, int selectdLine, int releasedLine, int posInSelection) {
	}

	@Override
	public ArrayList<TestComponent> removeSelectedLinesAndCleanUp() {
		return null;
	}

	@Override
	public ArrayList<TestComponent> removeLines(int klickedLine, int releasedLine) {
		return null;
	}

	@Override
	public void setDescriptionActive(int selectedLine, int releasedLine) {
	}

	@Override
	public void setScenarioSelectionActive(int selectedLine, int releasedLine) {
	}

	@Override
	public void setActionActive() {
	}

	@Override
	public void setAddMode(boolean b) {
	}

	@Override
	public void removeTestEditView(TestEditView testEditView) {
	}

	@Override
	public void moveRow(int zeileFromLowerInTestCase, int zeileFromUpperInTestCase, int zeileToInTestCase,
			boolean insertBefore) {
	}

	@Override
	public void setUnparsedActionGroup(String mask, String actionName, List<String> inputTexts, int selectedLine) {
	}

	@Override
	public int getSelectedLine() {
		return 0;
	}

	@Override
	public void setSelectedLineInView(int selectedLine) {
	}

	@Override
	public void markCorrespondingLine(int selectedLine) {
	}

	@Override
	public boolean isEmptyLineInView(int selectedLine) {
		return false;
	}

	@Override
	public boolean isLineInTestFlow(int line) {
		return false;
	}

	@Override
	public TestEditorTestDataTransferContainer copySelectedTestcomponents() {
		return null;
	}

	@Override
	public TestEditorTestDataTransferContainer cutSelectedTestComponents() {
		return null;
	}

	@Override
	public void pasteStoredTestComponents(int lineTo, boolean insertBefore, Object transferObject) {
	}

	@Override
	public void cleanupAndCloseInputAreas() {
	}

	@Override
	public int getChangePosition(boolean addMode) {
		return 0;
	}

	@Override
	public void setDescriptionControllerForPopupEditing(
			TestEditorDescriptionInputController testEditorDescriptionInputController) {
	}

	@Override
	public void setActionControllerForPopupEditing(TestEditorActionInputController testEditorInputController) {
	}

	@Override
	public void removePopupEditingControllers() {
	}

	@Override
	public void closePopupDialog() {
	}

	@Override
	public TestFlow getTestFlow() {
		return null;
	}

	@Override
	public void setScenarioToEditArea(TestScenarioParameterTable testComponentAt, int lineNumber)
			throws SystemException {
	}

	@Override
	public void pasteComponents() {
	}

	@Override
	public boolean isTestDataTableSelected() {
		return false;
	}

	@Override
	public void cutText() {
	}

	@Override
	public boolean canExecuteCutCopy() {
		return false;
	}

	@Override
	public void copyText() {
	}

	@Override
	public boolean canExecuteDelete() {
		return false;
	}

	@Override
	public void executeFileImportToTable() {
	}

	@Override
	public boolean canExecutePasteTestFlow() {
		return false;
	}

	@Override
	public void doHandleKeyEvent(KeyEvent e) {
	}

	@Override
	public TestEditorInputObject getLastUnSavedTestComponentInput(String classNameOfCachedTestComponent) {
		return null;
	}

	@Override
	public void setLastUnSavedTestComponentInput(TestEditorInputObject lastUnSavedTestComponentInput) {
	}

	@Override
	public void lostFocus() {
	}

	@Override
	public boolean isInputValid(String text) {
		return false;
	}

	@Override
	public void save() {
	}

	@Override
	public MPart getPart() {
		return null;
	}

	@Override
	public void closePart() {
	}

	@Override
	public TestScenario getScenarioByFullName(String fullNameOfScenario) throws SystemException {
		return null;
	}

	@Override
	public void rememberUnEditableLine(int unEditableLine) {
	}

	@Override
	public void clearUnEditableLines() {
	}

	@Override
	public boolean isLineEditable(int lineNumber) {
		return false;
	}

	@Override
	public boolean isSelectionEditable(int firstLine, int lastLine) {
		return false;
	}

	@Override
	public boolean isLibraryErrorLessLoaded() {
		return false;
	}

	@Override
	public void setFocus(Shell shell) {
	}

	@Override
	public String getInvalidChars() {
		return null;
	}

}
