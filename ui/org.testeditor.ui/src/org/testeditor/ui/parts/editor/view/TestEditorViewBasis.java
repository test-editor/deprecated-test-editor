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

import org.apache.log4j.Logger;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.RTFTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.testeditor.core.model.teststructure.TestComponent;
import org.testeditor.core.model.teststructure.TestData;
import org.testeditor.core.model.teststructure.TestScenarioParameterTable;
import org.testeditor.ui.constants.ColorConstants;
import org.testeditor.ui.constants.CustomWidgetIdConstants;
import org.testeditor.ui.constants.TestEditorEventConstants;
import org.testeditor.ui.constants.TestEditorUIEventConstants;
import org.testeditor.ui.parts.editor.ITestEditorController;
import org.testeditor.ui.utilities.TestEditorTranslationService;

/**
 * 
 * basis class for the TestEditView-class.
 * 
 */
public abstract class TestEditorViewBasis {

	private static final Logger LOGGER = Logger.getLogger(TestEditorViewBasis.class);

	@Inject
	private TestEditorTranslationService translationService;
	@Inject
	private IEventBroker eventBroker;
	@Inject
	private IEclipseContext context;

	@Inject
	private EPartService partService;

	private ActionLineToTestCaseLineMapper codeLineMapper = new ActionLineToTestCaseLineMapper();

	private List<Integer> linesAfterScenarioTable = new ArrayList<Integer>();

	private StyledText styledText;
	private ITestEditorController testCaseController;

	private Clipboard clipboard;
	private Composite masterCompositeContent;

	private String maskTitle = "?";

	private int clickedLine = -1;
	private int releasedLine = -1;
	private boolean insertBefore = false;
	private boolean focusInSubComponent = false;
	private MenuDetectListener menuDetectListener;
	private Menu styledTextContextMenu;

	private TableToTestComponentMapper tableToTestComponentMapper = new TableToTestComponentMapper();

	/**
	 * returns the line in the view, that correspondents to the line in the test
	 * case.
	 * 
	 * @param selectedLineInTestCase
	 *            line number in the testcase
	 * @return line number in the view
	 */
	public int getCorrespondingLine(int selectedLineInTestCase) {
		// return the max line of the styledText when the selectedLineInTestCase
		// >= size of test case
		if (selectedLineInTestCase >= testCaseController.getTestFlowSize()) {
			return getStyledText().getLineCount() - 1;
		}
		return codeLineMapper.getCorrespondingLine(selectedLineInTestCase);
	}

	/**
	 * puts the selectedData to the Clipboard. Using RTFTrasfer from StyledText
	 * and the TestFlowTransfer for this project. Notice: before this method is
	 * called the method styledText.copy() should be called.
	 * 
	 * @param dataContainer
	 *            special TestEditorTestFlowDataTransferContainer
	 */
	protected void putDataToClipboard(TestEditorTestDataTransferContainer dataContainer) {
		RTFTransfer rtfTransfer = RTFTransfer.getInstance();

		Object rtfTransferObject = clipboard.getContents(rtfTransfer);
		TestEditorTestFlowTransfer testFlowTransfer = TestEditorTestFlowTransfer.getInstance();

		if (rtfTransferObject == null) {
			Object[] data = new Object[] { dataContainer };
			Transfer[] types = new Transfer[] { testFlowTransfer };
			clipboard.setContents(data, types);
		} else {
			Object[] data = new Object[] { rtfTransferObject, dataContainer };
			Transfer[] types = new Transfer[] { rtfTransfer, testFlowTransfer };
			clipboard.setContents(data, types);
		}
	}

	/**
	 * 
	 * @return the TestFlowTransfer.
	 */
	public TestEditorTestDataTransferContainer getTestFlowTranfer() {
		TestEditorTestFlowTransfer testFlowTransfer = TestEditorTestFlowTransfer.getInstance();
		Object dataTransferObject = clipboard.getContents(testFlowTransfer);
		if (dataTransferObject instanceof TestEditorTestDataTransferContainer) {
			return (TestEditorTestDataTransferContainer) dataTransferObject;
		} else {
			return null;
		}
	}

	/**
	 * this method copies the text to the clipboard to paste in office and to an
	 * internal variable to paste in same or other testcase.
	 * 
	 * @param data
	 *            optional data
	 */
	@Inject
	@Optional
	protected void copyText(@UIEventTopic(TestEditorUIEventConstants.EDIT_CONTEXTMENU_CNTRL_C) String data) {
		if (isThisPartOnTop() && !isFocusInSubComponent()) {
			String textData = getStyledText().getSelectionText();
			if (textData != null && textData.length() > 0) {
				TestEditorTestDataTransferContainer dataContainer = testCaseController.copySelectedTestcomponents();
				putDataToClipboard(dataContainer);
			}
		}
	}

	/**
	 * cut the selected testComponent and store the text in the clipboard.
	 * 
	 * @param data
	 *            optional data
	 */
	@Inject
	@Optional
	protected void cutText(@UIEventTopic(TestEditorUIEventConstants.EDIT_CONTEXTMENU_CNTRL_X) String data) {
		if (isThisPartOnTop() && !isFocusInSubComponent()) {
			String textData = getStyledText().getSelectionText();
			if (textData != null && textData.length() > 0) {
				TestEditorTestDataTransferContainer dataContainer = testCaseController.cutSelectedTestComponents();
				putDataToClipboard(dataContainer);
			}
		}
	}

	/**
	 * refreshing the styledText by event. Event will send from the
	 * table-popupdialog, if a row is deleted.
	 * 
	 * @param dataTable
	 *            TestData
	 */
	@Inject
	@Optional
	protected void refreshTable(@UIEventTopic(TestEditorEventConstants.REFRESH_TEST_FLOW_VIEW) TestData dataTable) {
		TestEditorViewTableViewer tableViewer = tableToTestComponentMapper
				.getTestEditorViewTableViewerToTestData(dataTable);
		if (tableViewer != null) {
			tableViewer.getGridTableViewer().setInput(dataTable.getDataRows());
		}
	}

	/**
	 * Returns the selected testdata table.
	 * 
	 * @return null if no table selected otherwise selected table
	 */
	protected TestEditorViewTableViewer getSelectedTestDataTable() {
		return tableToTestComponentMapper.getSelectedTestDataTable();
	}

	/**
	 * Returns true if the selected element is a testdata table.
	 * 
	 * @return false if no table selected otherwise true.
	 */
	protected boolean isTestDataTableSelected() {
		return getSelectedTestDataTable() != null;
	}

	/**
	 * @return true, if their are one or more testcomponents on the clipboard
	 *         from the same project.
	 */
	protected boolean isPastePossible() {
		return testCaseController.canExecutePasteTestFlow();
	}

	/**
	 * this method adds a new int to the unEditableLine.
	 * 
	 * @param unEditableLine
	 *            int number of an uneditable line
	 */

	protected void rememberUnEditableLine(int unEditableLine) {
		testCaseController.rememberUnEditableLine(unEditableLine);
	}

	/**
	 * this method adds a new int to the unEditableLine.
	 * 
	 * @param line
	 *            int number of an line after a scenarioTable
	 */

	protected void rememberLineAfterScenarioTable(int line) {
		linesAfterScenarioTable.add(line);
	}

	/**
	 * this method checks the editable of a line. a line is not editable, if it
	 * contains a add line or the mask-name
	 * 
	 * @param lineNumber
	 *            number of the line
	 * @return true if it editable else false
	 */
	protected boolean isLineEditable(int lineNumber) {
		if (testCaseController == null) {
			return false;
		}
		return testCaseController.isLineEditable(lineNumber);
	}

	/**
	 * this method checks the editable of a line. a line is not editable, if it
	 * contains a add line or the mask-name
	 * 
	 * @param lineNumber
	 *            number of the line
	 * @return true if it editable else false
	 */
	protected boolean isLineAfterScenarioTable(int lineNumber) {
		return linesAfterScenarioTable.contains(Integer.valueOf(lineNumber));
	}

	/**
	 * 
	 * @param firstLine
	 *            first line of the selection
	 * @param lastLine
	 *            last line of the selection.
	 * @return true, if the one of the selected lines is editable.
	 */
	protected boolean isSelectionEditable(int firstLine, int lastLine) {
		return testCaseController.isSelectionEditable(firstLine, lastLine);
	}

	/**
	 * 
	 * @return true, if their is at least one TestComponent selected.
	 */
	protected boolean canExecuteCutCopy() {
		int[] selection = getStyledText().getSelectionRanges();
		int firstLine = getStyledText().getLineAtOffset(selection[0]);
		int lastLine = getStyledText().getLineAtOffset(selection[0] + selection[1]);

		boolean isEditable = isSelectionEditable(firstLine, lastLine);
		return isEditable && selection[1] > 0;
	}

	/**
	 * 
	 * @return true, if the cursor is on a editable line or if at last one
	 *         editable line is selected, else false.
	 */
	protected boolean canExecuteDelete() {
		if (!isFocusInSubComponent()) {
			int[] selection = getStyledText().getSelectionRanges();
			int firstLine = getStyledText().getLineAtOffset(selection[0]);
			int lastLine = getStyledText().getLineAtOffset(selection[0] + selection[1]);
			return isSelectionEditable(firstLine, lastLine);
		}
		return false;
	}

	/**
	 * paste the in the TestEditorActionInputController stored test components.
	 * 
	 * @param data
	 *            optional data
	 */
	@Inject
	@Optional
	protected void pasteTestComponents(@UIEventTopic(TestEditorUIEventConstants.EDIT_CONTEXTMENU_CNTRL_V) String data) {

		if (isThisPartOnTop() && !isFocusInSubComponent()) {

			int insertPos = getStyledText().getCaretOffset();
			if (getTestCaseController().getTestFlowSize() == 0 || !getStyledText().isEnabled()) {
				insertPos = 0;
			}
			int lineTo = getStyledText().getLineAtOffset(insertPos);

			if (getStyledText().getSelection().x != getStyledText().getSelection().y) {
				getTestCaseController().removeSelectedLinesAndCleanUp();

				// there are lines deleted
				lineTo = getClickedLineInView();
			}
			setInsertBefore();
			TestEditorTestFlowTransfer testFlowTransfer = TestEditorTestFlowTransfer.getInstance();
			Object dataTransferObject = getClipboard().getContents(testFlowTransfer);
			if (dataTransferObject != null) {
				getTestCaseController().pasteStoredTestComponents(lineTo, isInsertBefore(), dataTransferObject);
			}
		}
	}

	/**
	 * gets the part on the top.
	 * 
	 * @return the part.
	 */
	protected MPart getActivePart() {
		MPart part = partService.getActivePart();
		if (part != null && part.getElementId().equals(getId()) && part.getObject() != null) {
			return part;
		}
		return null;
	}

	/**
	 * Subclasses may override this.
	 * 
	 * @return the id of the part.
	 */
	protected abstract String getId();

	/**
	 * 
	 * @return true, if the part of this view is on top, else false.
	 */
	protected boolean isThisPartOnTop() {
		MPart partOnTop = getActivePart();
		if (partOnTop != null && getTestCaseController() != null && getTestCaseController().getPart() != null
				&& partOnTop.equals(getTestCaseController().getPart())) {
			return true;
		}
		return false;
	}

	/**
	 * set the boolean insertBefore. if the mouse is on the left hand side of
	 * the line, than insertBefore is true, else false.
	 * 
	 */
	protected void setInsertBefore() {
		insertBefore = false;
		int insertPos = getStyledText().getCaretOffset();
		int lineTo = getStyledText().getLineAtOffset(insertPos);
		// if line after a scenario-table, then set insertBefore = false
		if ((getStyledText().getOffsetAtLine(lineTo) == insertPos && !isLineAfterScenarioTable(lineTo))
				|| !isLineEditable(lineTo)) {
			insertBefore = true;
		}
	}

	/**
	 * this method marks the selected line.
	 * 
	 * @param lineNumber
	 *            int LineNumber
	 */
	protected void markSelectedLine(int lineNumber) {

		int lineCount = getStyledText().getLineCount();

		// init all rows with initial background color
		getStyledText().setLineBackground(0, lineCount, ColorConstants.COLOR_BACKROUND_NORMAL);

		// mark linenumber
		if (lineCount - 1 < lineNumber) {
			lineNumber = lineCount - 1;
		}
		if (lineNumber != -1) {
			getStyledText().setLineBackground(lineNumber, 1, ColorConstants.COLOR_SELECTED);
			int caretOffset = getStyledText().getCaretOffset();
			int lineTo = getStyledText().getLineAtOffset(caretOffset);
			boolean insertBeforeLine = isInsertBefore();
			if (lineTo != lineNumber) {
				if (insertBeforeLine) {
					getStyledText().setSelection(getStyledText().getOffsetAtLine(lineNumber));
				} else {
					getStyledText().setSelection(
							getStyledText().getOffsetAtLine(lineNumber) + getStyledText().getLine(lineNumber).length());
				}
			}
		}
		getTestCaseController().cleanupAndCloseInputAreas();
		setClickedLine(lineNumber);
		setReleasedLine(lineNumber);
		if (lineNumber >= 0) {
			setFocusInSubComponent(false);
		} else {
			setFocusInSubComponent(true);
		}
	}

	/**
	 * this method checks the selected line. if the line is empty, return is
	 * true, else false.
	 * 
	 * @param klickedLine
	 *            selected Line in the view
	 * @return true if the line is empty, else false.
	 */
	public boolean isLineEmpty(int klickedLine) {
		if (getStyledText().getLineCount() > klickedLine && klickedLine >= 0) {
			return getStyledText().getLine(klickedLine).isEmpty();
		}
		return true;
	}

	/**
	 * put the cursor into the selected line.
	 * 
	 * @param newCursorPosition
	 *            new line for the cursor position
	 * @param endLinePos
	 *            should be set, if the cursor should be set at the end of the
	 *            line
	 */
	public void setCursor(int newCursorPosition, boolean endLinePos) {
		if (newCursorPosition > getStyledText().getLineCount() - 1) {
			newCursorPosition = getStyledText().getLineCount() - 1;
		}
		int posInLine = 0;

		if (endLinePos) {
			posInLine = getStyledText().getLine(newCursorPosition).length();
		}

		getStyledText().setSelection(getStyledText().getOffsetAtLine(newCursorPosition) + posInLine);
		setClickedLine(newCursorPosition);
	}

	/**
	 * creates the ui.
	 * 
	 * @param compositeContent
	 *            Composite
	 */
	public void createUI(Composite compositeContent) {
		if (compositeContent != null) {
			compositeContent.setData(CustomWidgetIdConstants.TEST_EDITOR_WIDGET_ID_SWT_BOT_KEY,
					CustomWidgetIdConstants.TEST_CASE_VIEW);
			setMasterCompositeContent(compositeContent);

			createSystemVariables(compositeContent);
		}

		if (compositeContent != null) {

			this.styledText = new StyledText(compositeContent, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.WRAP);
			styledText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
			styledText.setEditable(false);
			styledText.setSelection(-1, -1);
			styledText.setData(CustomWidgetIdConstants.TEST_EDITOR_WIDGET_ID_SWT_BOT_KEY,
					CustomWidgetIdConstants.TEST_CASE_VIEW_TEXT);
			styledText.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					eventBroker.send(UIEvents.REQUEST_ENABLEMENT_UPDATE_TOPIC, UIEvents.ALL_ELEMENT_ID);
				}

			});
			styledText.addModifyListener(new ModifyListener() {

				@Override
				public void modifyText(ModifyEvent e) {
					eventBroker.send(UIEvents.REQUEST_ENABLEMENT_UPDATE_TOPIC, UIEvents.ALL_ELEMENT_ID);
				}
			});
		}

	}

	/**
	 * this method creates the different colors.
	 * 
	 * @param compositeContent
	 *            the composite
	 */
	private void createSystemVariables(Composite compositeContent) {
		maskTitle = translate("%TestEditView_7");
		Display display = Display.getCurrent();
		if (display == null) {
			display = Display.getDefault();
		}
		LOGGER.trace("Using SWT Display: " + display);
		setClipboard(new Clipboard(display));
	}

	/**
	 * @return the corresponding line in the testcase to the released line in
	 *         the view.
	 */
	protected int getReleasedLineInTestCase() {
		return getCodeLineMapper().getContentOfOffsetViewToTestListAt(getReleasedLineInView());
	}

	/**
	 * 
	 * @return the corresponding line in the testcase to the klicked line in the
	 *         view.
	 */
	protected int getKlickedLineInTestCase() {
		return getCodeLineMapper().getContentOfOffsetViewToTestListAt(getClickedLineInView());
	}

	/**
	 * gets the start point of the selection in the styledtext.
	 * 
	 * @return the start of the selection.
	 */
	protected int getSelectionStartInTestCase() {
		if (!focusInSubComponent) {
			int[] selection = getStyledText().getSelectionRanges();
			return getCodeLineMapper()
					.getContentOfOffsetViewToTestListAt(getStyledText().getLineAtOffset(selection[0]));
		}
		return -1;
	}

	/**
	 * gets the end point of the selection in the styledtext.
	 * 
	 * @return the end of the selection.
	 */
	protected int getSelectionEndInTestCase() {
		if (!focusInSubComponent) {
			int[] selection = getStyledText().getSelectionRanges();
			return getCodeLineMapper().getContentOfOffsetViewToTestListAt(
					getStyledText().getLineAtOffset(selection[0] + selection[1]));
		}
		return -1;
	}

	/**
	 * Key Listener for moving Action with Key CTRL + down/up.
	 * 
	 */
	protected void initListenerForMoveActionWithKeyDownUp() {

		getStyledText().addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				if ((e.stateMask & SWT.CTRL) != 0) {
					int lineFrom1 = getStyledText().getLineAtOffset(getStyledText().getSelection().x);
					int lineFrom2 = getStyledText().getLineAtOffset(getStyledText().getSelection().y);
					int lineFromLowerBorder, lineFromUpperBorder;
					if (lineFrom1 <= lineFrom2) {
						lineFromLowerBorder = lineFrom1;
						lineFromUpperBorder = lineFrom2;
					} else {
						lineFromLowerBorder = lineFrom2;
						lineFromUpperBorder = lineFrom1;
					}
					boolean beginOrEnd = true;

					if (e.keyCode == SWT.ARROW_UP && lineFromLowerBorder > 0
							&& getCodeLineMapper().getContentOfOffsetViewToTestListAt(lineFromLowerBorder) > 0) {
						int targetLine = lineFromLowerBorder - 1;
						if (!isLineEditable(targetLine)) {
							targetLine--;
							if (!isLineEditable(targetLine)) {
								targetLine--;
							}
						}
						moveRow(lineFromLowerBorder, lineFromUpperBorder, targetLine, beginOrEnd);
					} else if (e.keyCode == SWT.ARROW_DOWN && lineFromLowerBorder < getStyledText().getLineCount() - 1) {
						beginOrEnd = false;

						moveRow(lineFromLowerBorder, lineFromUpperBorder, lineFromLowerBorder + 1, beginOrEnd);
					}
				}
			}
		});
	}

	/**
	 * Moving the action from current position to target position in the model.
	 * The LinkedList will be modified.
	 * 
	 * @param lineFromLower
	 *            line from lower border
	 * @param lineFromUpper
	 *            line from upper border
	 * @param lineTo
	 *            line to
	 * @param insertBefore
	 *            boolean if true, then begin of the line, else end of the line
	 */
	private void moveRow(int lineFromLower, int lineFromUpper, int lineTo, boolean insertBefore) {

		int zeileFromLowerInTestCase = getCodeLineMapper().getContentOfOffsetViewToTestListAt(lineFromLower);
		int zeileFromUpperInTestCase = getCodeLineMapper().getContentOfOffsetViewToTestListAt(lineFromUpper);
		int zeileToInTestCase = getCodeLineMapper().getContentOfOffsetViewToTestListAt(lineTo);

		getTestCaseController().moveRow(zeileFromLowerInTestCase, zeileFromUpperInTestCase, zeileToInTestCase,
				insertBefore);
		refreshStyledText();

		int viewSize = getStyledText().getLineCount() - 1;
		if (lineTo + 1 > viewSize) {
			lineTo = viewSize;
		}
		zeileToInTestCase = getCodeLineMapper().getContentOfOffsetViewToTestListAt(lineTo);
		// possible now more or less masks are shown in the view
		getStyledText().setSelection(getStyledText().getOffsetAtLine(getCorrespondingLine(zeileToInTestCase)));
	}

	/**
	 * refresh styledtext.
	 */
	protected abstract void refreshStyledText();

	/**
	 * returns the translation for a given string form the laguageresource.
	 * 
	 * @param key
	 *            the key for the translation
	 * @param params
	 *            params in translated text given as placeholder example: this
	 * 
	 * @return the translation
	 */
	protected String translate(String key, Object... params) {
		return translationService.translate(key, params);
	}

	/**
	 * 
	 * @return the {@link Clipboard}
	 */
	protected Clipboard getClipboard() {
		return clipboard;
	}

	/**
	 * sets the {@link Clipboard}.
	 * 
	 * @param clipboard
	 *            Clipboard
	 */
	protected void setClipboard(Clipboard clipboard) {
		this.clipboard = clipboard;
	}

	/**
	 * 
	 * @return styledText
	 */
	public StyledText getStyledText() {
		return styledText;
	}

	/**
	 * 
	 * @return ITestEditorController
	 */
	public ITestEditorController getTestCaseController() {
		return testCaseController;
	}

	/**
	 * setTestCaseController.
	 * 
	 * @param testCaseController
	 *            testCaseComtroller
	 * 
	 */
	protected void setTestCaseController(ITestEditorController testCaseController) {
		this.testCaseController = testCaseController;
	}

	/**
	 * clears the unEditableLines.
	 * 
	 */
	protected void clearUnEditableLines() {
		testCaseController.clearUnEditableLines();
	}

	/**
	 * clears the setLinesAfterScenarioTable.
	 * 
	 */
	protected void clearLinesAfterScenarioTable() {
		this.linesAfterScenarioTable = new ArrayList<Integer>();
	}

	/**
	 * 
	 * @return masterCompositeContent
	 */
	public Composite getMasterCompositeContent() {
		return masterCompositeContent;
	}

	/**
	 * sets the masterCompositeContent.
	 * 
	 * @param masterCompositeContent
	 *            Composite
	 */
	public void setMasterCompositeContent(Composite masterCompositeContent) {
		this.masterCompositeContent = masterCompositeContent;
	}

	/**
	 * 
	 * @return maskTitle
	 */
	public String getMaskTitle() {
		return maskTitle;
	}

	/**
	 * 
	 * @return clickedLine
	 */
	public int getClickedLineInView() {
		return clickedLine;
	}

	/**
	 * sets the clickedLine.
	 * 
	 * @param clickedLine
	 *            int.
	 */
	public void setClickedLine(int clickedLine) {
		this.clickedLine = clickedLine;
	}

	/**
	 * 
	 * @return releasedLine
	 */
	public int getReleasedLineInView() {
		return releasedLine;
	}

	/**
	 * sets the releasedLine.
	 * 
	 * @param releasedLine
	 *            int
	 */
	public void setReleasedLine(int releasedLine) {
		this.releasedLine = releasedLine;
	}

	/**
	 * 
	 * @return insertBefore
	 */
	public boolean isInsertBefore() {
		return insertBefore;
	}

	/**
	 * sets insertbefore.
	 * 
	 * @param insertBefore
	 *            booelan
	 */
	public void setInsertBefore(boolean insertBefore) {
		this.insertBefore = insertBefore;
	}

	/**
	 * 
	 * @return TranslationService
	 */
	protected TestEditorTranslationService getTranslationService() {
		return translationService;
	}

	/**
	 * 
	 * @return the IEventBroker.
	 */
	public IEventBroker getEventBroker() {
		return eventBroker;
	}

	/**
	 * 
	 * @return the IEclipseContext
	 */
	public IEclipseContext getContext() {
		return context;
	}

	/**
	 * 
	 * @return true, is subWidget is in Focus. i.e. a table in the styledText.
	 */
	public boolean isFocusInSubComponent() {
		return focusInSubComponent;
	}

	/**
	 * this method should be overwritten in the subclass.
	 */
	protected void initListenerForMenuDetect() {
	}

	/**
	 * sets the membervariable focusInSubComponent to the value of the
	 * parameter. If it's false, removes the menuDetec-Listener on the
	 * styledText.
	 * 
	 * @param focusInSubComponent
	 *            boolean
	 */
	public void setFocusInSubComponent(boolean focusInSubComponent) {
		this.focusInSubComponent = focusInSubComponent;
		if (this.focusInSubComponent) {
			if (getStyledText().isListening(SWT.MenuDetect) && menuDetectListener != null) {
				if (styledTextContextMenu != null && !styledTextContextMenu.isDisposed()) {
					styledTextContextMenu.dispose();
				}
				styledText.removeMenuDetectListener(menuDetectListener);
				setMenuDetectListener(null);
			}
		} else if (!getStyledText().isListening(SWT.MenuDetect) || menuDetectListener == null) {
			initListenerForMenuDetect();
		}
	}

	/**
	 * sets the marked position in the styledText behind the subcomponent.
	 * 
	 * @param tableViewer
	 *            TestEditorViewTableViewer
	 */
	public void setFocusInSubComponent(TestEditorViewTableViewer tableViewer) {
		// now find the right position in the styledText and mark it.
		TestComponent tableComponent = tableToTestComponentMapper.getTestScenarioTableToTableViewer(tableViewer);
		List<TestComponent> testComponents = getTestCaseController().getTestFlow().getTestComponents();
		int numOfComp = -1;
		for (int i = 0; i < testComponents.size(); i++) {
			if (testComponents.get(i) == tableComponent) {
				numOfComp = i;
				break;
			}
		}
		markSelectedLine(getCorrespondingLine(numOfComp));
		setCursor(getCorrespondingLine(numOfComp), true);
		setFocusInSubComponent(true);
	}

	/**
	 * 
	 * @return the MenuDetectListener.
	 */
	public MenuDetectListener getMenuDetectListener() {
		return menuDetectListener;
	}

	/**
	 * sets the member-variable menuDetectListener.
	 * 
	 * @param menuDetectListener
	 *            MenuDetectListener
	 */
	public void setMenuDetectListener(MenuDetectListener menuDetectListener) {
		this.menuDetectListener = menuDetectListener;
	}

	/**
	 * 
	 * @return the menu on the styledText.
	 */
	public Menu getStyledTextContextMenu() {
		return styledTextContextMenu;
	}

	/**
	 * sets the styledTextContextMenu.
	 * 
	 * @param styledTextContextMenu
	 *            Menu
	 */
	public void setStyledTextContextMenu(Menu styledTextContextMenu) {
		this.styledTextContextMenu = styledTextContextMenu;
	}

	/**
	 * dispose the resources.
	 */
	public void disposeAndSetToNull() {

		if (testCaseController != null) {
			testCaseController = null;
		}

		tableToTestComponentMapper.clear();
		if (clipboard != null) {
			clipboard = null;
		}
		if (maskTitle != null) {
			maskTitle = null;
		}
		if (menuDetectListener != null) {
			menuDetectListener = null;
		}
		dispose();
	}

	/**
	 * dispose the resources.
	 */
	private void dispose() {
		if (styledText != null && !styledText.isDisposed()) {
			styledText.dispose();
		}
		if (styledTextContextMenu != null && !styledTextContextMenu.isDisposed()) {
			styledTextContextMenu.dispose();
		}
		if (masterCompositeContent != null && !masterCompositeContent.isDisposed()) {
			masterCompositeContent.dispose();
		}
	}

	/**
	 * .
	 * 
	 * @return codeLineMapper
	 */
	public ActionLineToTestCaseLineMapper getCodeLineMapper() {
		return codeLineMapper;
	}

	/**
	 * clears the mapper between the tableViewers and the TestComponents.
	 */
	protected void clearTableToTestComponentMapper() {
		tableToTestComponentMapper.clear();
	}

	/**
	 * adds a table to the controlArray to show it in the styledText.
	 * 
	 * @param tableViewer
	 *            TestEditorViewTableViewer
	 * @param testComp
	 *            the testComponent
	 */
	protected void addTableToTableStore(TestEditorViewTableViewer tableViewer, TestScenarioParameterTable testComp) {
		tableToTestComponentMapper.addTableToTableStore(tableViewer, testComp);
	}

	/**
	 * checks the include of the tableViewer given by the parameter in this
	 * view.
	 * 
	 * @param tableViewer
	 *            TestEditorViewTableViewer
	 * @return true, if the table is in the view.
	 */
	protected boolean isTableInStyledText(TestEditorViewTableViewer tableViewer) {
		if (tableToTestComponentMapper.getTestScenarioTableToTableViewer(tableViewer) != null) {
			return true;
		}
		return false;
	}

}
