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

import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GlyphMetrics;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.model.action.IAction;
import org.testeditor.core.model.action.TextType;
import org.testeditor.core.model.action.UnparsedActionLine;
import org.testeditor.core.model.teststructure.TestActionGroup;
import org.testeditor.core.model.teststructure.TestComponent;
import org.testeditor.core.model.teststructure.TestDataEvaluationReturnList;
import org.testeditor.core.model.teststructure.TestDescription;
import org.testeditor.core.model.teststructure.TestInvisibleContent;
import org.testeditor.core.model.teststructure.TestScenario;
import org.testeditor.core.model.teststructure.TestScenarioParameterTable;
import org.testeditor.ui.constants.ColorConstants;
import org.testeditor.ui.constants.IconConstants;
import org.testeditor.ui.constants.TestEditorConstants;
import org.testeditor.ui.constants.TestEditorFontConstants;
import org.testeditor.ui.constants.TestEditorUIEventConstants;
import org.testeditor.ui.handlers.OpenTestStructureHandler;
import org.testeditor.ui.parts.editor.ITestEditorController;
import org.testeditor.ui.parts.editor.view.listener.PaintImageAndTableListener;
import org.testeditor.ui.parts.editor.view.listener.TestEditorViewStyledTextModifyListener;
import org.testeditor.ui.parts.editor.viewWidgets.TestEditorButton;
import org.testeditor.ui.parts.inputparts.actioninput.TestEditorActioInputPopupDialog;
import org.testeditor.ui.parts.inputparts.descriptioninput.TestEditorDescriptionInputPopupDialog;

/**
 * Description Area is the UI of a TestCase Description used in the Testeditor.
 * 
 * @author Lothar Lipinski
 * 
 * 
 */
@Creatable
public class TestEditView extends TestEditorViewBasis {

	private PaintImageAndTableListener painter;

	private List<StyleRange> glStyleRanges;

	private StringBuffer internalText;

	private PopupDialog popupDialogDescription;
	private PopupDialog popupDialogAction;

	private boolean rowForUnparsedImageNeeded = false;
	private Image warningImage;

	private ModifyListener modifyListener;

	private static final Logger LOGGER = Logger.getLogger(TestEditView.class);

	private TestEditorViewKeyHandler keyHandler;

	/**
	 * this method creates the ui to show the Testcase.
	 * 
	 * @param compositeContent
	 *            Composite of the context
	 */
	@Override
	public void createUI(Composite compositeContent) {

		super.createUI(compositeContent);
		if (compositeContent != null) {
			keyHandler = ContextInjectionFactory.make(TestEditorViewKeyHandler.class, getContext());

			getStyledText().addKeyListener(new KeyListener() {

				@Override
				public void keyReleased(KeyEvent e) {

					if (e.keyCode == SWT.ARROW_UP || e.keyCode == SWT.ARROW_DOWN || e.keyCode == SWT.ARROW_RIGHT
							|| e.keyCode == SWT.ARROW_LEFT) {
						markSelectedLine(getStyledText().getLineAtOffset(getStyledText().getCaretOffset()));
					}
				}

				@Override
				public void keyPressed(KeyEvent e) {

					if (e.widget != getStyledText()) {
						return;
					}

					keyHandler.doHandleKeyEvent(e);
				}

			});
			initListenerForMoveActionWithKeyDownUp();
			initListenerForMenuDetect();

			initListenerForKlick();
			initListenerPaintObject();
		}
	}

	/**
	 * open a new dialog (ActionPopupDialog) for the F8-Event.
	 * 
	 * @param data
	 *            optional data
	 */
	@Inject
	@Optional
	protected void openDialogForF8(@UIEventTopic(TestEditorUIEventConstants.EDIT_CONTEXTMENU_F8) String data) {
		if (isThisPartOnTop() && !isFocusInSubComponent()) {
			showActionGroupPopupDialog();
			createActionArea();
		}
	}

	/**
	 * open a new dialog (DescriptionPopupDialog) for the F7-Event.
	 * 
	 * @param data
	 *            optional data
	 */
	@Inject
	@Optional
	protected void openDialogForF7(@UIEventTopic(TestEditorUIEventConstants.EDIT_CONTEXTMENU_F7) String data) {
		if (isThisPartOnTop() && !isFocusInSubComponent()) {
			showDescriptionPopupDialog();
			createDescriptionArea();
		}
	}

	/**
	 * will be called via the eventBroker from a table.
	 * 
	 * @param tableViewer
	 *            TestEditorViewTableViewerClipboard
	 */
	@Inject
	@Optional
	protected void redrawStyledText(
			@UIEventTopic(TestEditorUIEventConstants.TESTEDITOR_VIEW_CHNAGED_TABLE) TestEditorViewTableViewerClipboard tableViewer) {
		if (isTableInStyledText(tableViewer)) {
			getStyledText().redraw();
		}
	}

	/**
	 * Opens an Description PopUp Dialog for editing the Actiongroup.
	 */
	protected void showActionGroupPopupDialog() {
		String messageAction = translate("%part.Action");
		popupDialogAction = new TestEditorActioInputPopupDialog(messageAction, getContext(), getTestCaseController(),
				getStyledText());
		popupDialogAction.open();
	}

	/**
	 * Opens an Description PopUp Dialog for editing the Description.
	 */
	protected void showDescriptionPopupDialog() {
		String messageDescription = translate("%part.Description");
		popupDialogDescription = new TestEditorDescriptionInputPopupDialog(messageDescription, getContext(),
				getTestCaseController(), getStyledText());
		popupDialogDescription.open();
	}

	/**
	 * this method adds a listener to show the contextMenu.
	 */
	@Override
	protected void initListenerForMenuDetect() {
		if (getMasterCompositeContent().getShell().isDisposed()) {
			return;
		}
		final TestEditView editView = this;
		setMenuDetectListener(new MenuDetectListener() {

			@Override
			public void menuDetected(MenuDetectEvent event) {
				new TestEditorViewContextMenu().getContextMenu(getMasterCompositeContent().getShell(), SWT.POP_UP,
						editView, event);
			}
		});
		getStyledText().addMenuDetectListener(getMenuDetectListener());
	}

	/**
	 * method to delete selected line.
	 * 
	 * @param data
	 *            optional data
	 */
	@Inject
	@Optional
	public void removeSelectedLineIfInFocus(@UIEventTopic(TestEditorUIEventConstants.EDIT_CONTEXTMENU_DEL) String data) {
		if (isThisPartOnTop() && !isFocusInSubComponent() && getTestCaseController() != null) {
			getTestCaseController().removeSelectedLinesAndCleanUp();
		}
	}

	/**
	 * executes the file import into the selected table.
	 */
	protected void executeFileImportToTable() {
		TestEditorViewTableViewer selectedTestDataTable = getSelectedTestDataTable();

		if (selectedTestDataTable != null) {
			selectedTestDataTable.handleFileImport();
		}
	}

	/**
	 * creates the area description.
	 */
	private void createDescriptionArea() {
		getTestCaseController().setAddMode(true);
		getTestCaseController().setDescriptionActive(getKlickedLineInTestCase(), getReleasedLineInTestCase());
	}

	/**
	 * creates the actionArea.
	 */
	private void createActionArea() {
		getTestCaseController().setAddMode(true);
		getTestCaseController().setActionActive();
	}

	/**
	 * initialized listeners for the mouse click-actions on the styledText.
	 */

	private void initListenerForKlick() {
		getStyledText().addMouseListener(new MouseListener() {

			@Override
			public void mouseUp(MouseEvent e) {
				if (e.button == 1) {
					setReleasedLine(getStyledText().getLineIndex(e.y));
				}
			}

			@Override
			public void mouseDown(MouseEvent e) {
				getTestCaseController().setFocus(getStyledText().getShell());
				if (e.button == 1) {
					setClickedLine(getStyledText().getLineIndex(e.y));
					markSelectedLine(getClickedLineInView());
					setInsertBefore();
				}
			}

			@Override
			public void mouseDoubleClick(MouseEvent e) {
				getTestCaseController().setFocus(getStyledText().getShell());
				int lineIndex = getStyledText().getLineIndex(e.y);
				setClickedLine(lineIndex);
				int lineInTestCase = getCodeLineMapper().getContentOfOffsetViewToTestListAt(lineIndex);
				if (isLineEditable(getClickedLineInView())) {
					try {
						showSelectedContentInInputArea(lineInTestCase);
					} catch (SystemException e1) {
						MessageDialog.openError(Display.getCurrent().getActiveShell(), "System-Exception",
								e1.getMessage());
						LOGGER.error("error by getting the scenario", e1);
					}
				}
			}
		});
	}

	/**
	 * shows the selected line in the edit-area.
	 * 
	 * @param lineNumber
	 *            number of the selected line
	 * @throws SystemException
	 *             on reading sceanrio
	 */
	void showSelectedContentInInputArea(int lineNumber) throws SystemException {
		ITestEditorController testCaseController = getTestCaseController();
		if (lineNumber >= 0 && getStyledText().getLineCount() >= lineNumber
				&& testCaseController.isLineInTestFlow(lineNumber)) {
			// find the cursor position in the text
			int insertPos = getStyledText().getCaretOffset();
			int cursorPosInSelection = insertPos
					- getStyledText().getOffsetAtLine(getStyledText().getLineAtOffset(insertPos));
			List<String> texts = testCaseController.getLine(lineNumber);

			TestComponent testComponent = testCaseController.getTestComponentAt(lineNumber);
			if (testComponent instanceof TestDescription) {
				String selText = testCaseController.getTestComponentAt(lineNumber).getTexts().get(0);
				testCaseController.putTextToInputArea(selText.toString(), getKlickedLineInTestCase(),
						getReleasedLineInTestCase(), cursorPosInSelection);
			} else if (testComponent instanceof TestActionGroup) {
				testCaseController.setActionToEditArea(lineNumber, texts, cursorPosInSelection);
			} else if (testComponent instanceof TestScenarioParameterTable
					&& ((TestScenarioParameterTable) testComponent).isScenarioOfProject()) {
				testCaseController.setScenarioToEditArea((TestScenarioParameterTable) testComponent, lineNumber);
				// TODO here new class for the editDialog for the
				// ScenarioParameterTable open.
				// System.err.println("Line in TestCase: " +
				// getKlickedLineInTestCase());
				// System.err.println(((TestScenarioParameterTable)
				// testCaseController
				// .getTestComponentAt(getKlickedLineInTestCase())).getTitel());
			}
		}
	}

	/**
	 * initialized the listener for the paint object.
	 * 
	 */
	private void initListenerPaintObject() {
		painter = new PaintImageAndTableListener(getStyledText());
		getStyledText().addPaintObjectListener(painter);
	}

	/**
	 * dispose the system resource.
	 */
	@Override
	public void disposeAndSetToNull() {
		if (getStyledText() != null) {
			getStyledText().dispose();
		}
		disposeControls();

		if (painter != null) {
			painter = null;
		}

		if (glStyleRanges != null) {
			glStyleRanges = null;
		}

		if (internalText != null) {
			internalText = null;
		}

		disposePopupDialogDescription();

		disposePopupDialogAction();

		disposeWarningImage();

		removeStyledTextModifyListener();

		super.disposeAndSetToNull();
	}

	/**
	 * disposes the popupDialogDescription.
	 */
	private void disposePopupDialogDescription() {
		if (popupDialogDescription != null && popupDialogDescription.getShell() != null
				&& !popupDialogDescription.getShell().isDisposed()) {
			popupDialogDescription.getShell().dispose();
		}
	}

	/**
	 * Disposes the PopupDialogAction.
	 */
	private void disposePopupDialogAction() {
		if (popupDialogAction != null && popupDialogAction.getShell() != null
				&& !popupDialogAction.getShell().isDisposed()) {
			popupDialogAction.getShell().dispose();
		}
	}

	/**
	 * Disposes WarningImage.
	 */
	private void disposeWarningImage() {
		if (warningImage != null && !warningImage.isDisposed()) {
			warningImage.dispose();
		}
	}

	/**
	 * the images will now be destroyed in the central IconConstans-class. the
	 * control is in the activator-class.
	 */
	@PreDestroy
	public void preDestroy() {
		if (getTestCaseController() != null) {
			getTestCaseController().removeTestEditView(this);
		}
		disposeAndSetToNull();
	}

	/**
	 * StyledText will be synchronized with current Document.
	 * 
	 * refresh styledtext.
	 * 
	 */
	@Override
	protected void refreshStyledText() {
		preRefreshStyledText();
		int topPixel = getStyledText().getTopPixel();

		ITestEditorController testCaseController = getTestCaseController();

		int sizeTestCase = testCaseController.getTestFlowSize();
		internalText = new StringBuffer();
		if (!getStyledText().isDisposed()) {
			// creation of the ranges of the style are separated in class {
			// @TestViewRefreshText }
			try {
				glStyleRanges = new ArrayList<StyleRange>();
				if (!testCaseController.isLibraryErrorLessLoaded()) {
					// there was an error while loading the library of the
					// project. So the TestCase isn't parsed.
					// show an information to the user.
					showWarningLibraryLoadingError();
				} else if (sizeTestCase == 0
						|| (testCaseController.getTestComponentAt(0) instanceof TestInvisibleContent && sizeTestCase == 1)) {
					getTestCaseController().removeLines(0, 0);
					showWelcomeScreen();
				} else {

					refreshUnEmptyText(sizeTestCase);
				}
				getStyledText().setText(internalText.toString());
				int sizeRanges = glStyleRanges.size();
				StyleRange[] styleRanges = glStyleRanges.toArray(new StyleRange[sizeRanges]);
				getStyledText().setStyleRanges(styleRanges);
				getStyledText().redraw();
				if (modifyListener == null) {
					modifyListener = new TestEditorViewStyledTextModifyListener(getEventBroker(),
							getTestCaseController().getTestFlow());
					getStyledText().addModifyListener(modifyListener);
				}

			} catch (Exception illegalArgumentException) {
				LOGGER.error("Error refreshing StyledText", illegalArgumentException);
			}
			addRowForWarningImage();
		}
		getStyledText().setTopPixel(topPixel);
	}

	/**
	 * this method resets some internal variables before the refreshing of the
	 * styled texts.
	 * 
	 */
	protected void preRefreshStyledText() {
		getCodeLineMapper().resetOffsetToTestList();
		clearUnEditableLines();
		clearLinesAfterScenarioTable();
		disposeControls();
		rowForUnparsedImageNeeded = false;
	}

	/**
	 * tells to the user, that the library is loaded with an error and so the
	 * testflow can't shown.
	 */
	private void showWarningLibraryLoadingError() {

		rememberUnEditableLine(0);
		String libraryWarningMessage = translate("%LibraryErrors");

		warningImage = new Image(getMasterCompositeContent().getDisplay(), getMasterCompositeContent().getDisplay()
				.getSystemImage(SWT.ICON_WARNING).getImageData());
		TestEditorButton warningButton = new TestEditorButton(getStyledText(), SWT.FLAT, warningImage,
				libraryWarningMessage);

		StyleRange buttonRange = addButton(warningButton.getButton(), internalText.length(), " ");
		addNewStyleRange(buttonRange);
		addStyledRange(libraryWarningMessage, TextType.DESCRIPTION);
	}

	/**
	 * this method adds a range to the styledRanges.
	 * 
	 * @param range2
	 *            StyleRange
	 */
	private void addNewStyleRange(StyleRange range2) {
		glStyleRanges.add(range2);
	}

	/**
	 * refresh the styledText, when the testCase is not empty.
	 * 
	 * @param sizeTestCase
	 *            size of the test case.
	 * 
	 */
	private void refreshUnEmptyText(int sizeTestCase) {
		int offsetLines = 0;
		TestComponent prevTestComponent = null;
		for (int lineInTestCase = 0; lineInTestCase < sizeTestCase; lineInTestCase++) {
			TestComponent testComp = getTestCaseController().getTestComponentAt(lineInTestCase);
			if (testComp instanceof TestActionGroup) {
				String maske = ((TestActionGroup) testComp).getActionGroupName();
				offsetLines = addStyleTextActionGroup(offsetLines, prevTestComponent, lineInTestCase, maske, testComp);
			} else if (prevTestComponent != null && (testComp.getClass() != prevTestComponent.getClass())) {
				// there is a type-change of the testcomponent
				offsetLines = addSeperationLine(offsetLines, lineInTestCase);
			}
			if (testComp instanceof TestScenarioParameterTable) {
				offsetLines = addScenarioIncludeInfoLine(offsetLines, lineInTestCase);
				getCodeLineMapper().rememberLineOffset(offsetLines, lineInTestCase);
				addLinkToScenario(getTestCaseController().getTestComponentAt(lineInTestCase));
				addTextsToStyledText(sizeTestCase, lineInTestCase);
				offsetLines = createParameterTable((TestScenarioParameterTable) testComp, offsetLines, lineInTestCase);
			} else {
				addTextsToStyledText(sizeTestCase, lineInTestCase);
			}

			prevTestComponent = testComp;
			getCodeLineMapper().rememberLineOffset(offsetLines, lineInTestCase);
		}
	}

	/**
	 * adds a button with a link to the scenario.
	 * 
	 * @param testComponent
	 *            the Scenario
	 */
	private void addLinkToScenario(final TestComponent testComponent) {
		Image imageScenario;
		String toolTipAtImage = "";
		boolean isScenarioOfProject = ((TestScenarioParameterTable) testComponent).isScenarioOfProject();

		boolean columnHeadersOnlyInSourceTableIsEmpty = true;
		boolean columnHeadersOnlyInTargetTableIsEmpty = true;
		boolean dataRowColumnCountEqualsHeaderRowColumnCount = true;
		TestDataEvaluationReturnList testDataEvaluationReturnList = new TestDataEvaluationReturnList();
		if (isScenarioOfProject) {
			testDataEvaluationReturnList = ((TestScenarioParameterTable) testComponent)
					.getTestDataEvaluationReturnList();
			if (testDataEvaluationReturnList != null) {
				columnHeadersOnlyInSourceTableIsEmpty = testDataEvaluationReturnList
						.getColumnHeadersOnlyInSourceTable().isEmpty();
				columnHeadersOnlyInTargetTableIsEmpty = testDataEvaluationReturnList
						.getColumnHeadersOnlyInTargetTable().isEmpty();
				dataRowColumnCountEqualsHeaderRowColumnCount = testDataEvaluationReturnList
						.isDataRowColumnCountEqualsHeaderRowColumnCount();
			}
		}
		if (isScenarioOfProject && columnHeadersOnlyInTargetTableIsEmpty && columnHeadersOnlyInSourceTableIsEmpty
				&& dataRowColumnCountEqualsHeaderRowColumnCount) {
			imageScenario = IconConstants.ICON_SCENARIO;
			toolTipAtImage = translate("%TestEditView_LinkToScenario",
					((TestScenarioParameterTable) testComponent).getTitle());
		} else {
			imageScenario = IconConstants.ICON_UNPARSED_LINE;
			if (!isScenarioOfProject) {
				toolTipAtImage = translate("%TestEditView_NotProjectScenario",
						((TestScenarioParameterTable) testComponent).getTitle());
			} else if (!columnHeadersOnlyInSourceTableIsEmpty || !columnHeadersOnlyInTargetTableIsEmpty) {
				String columnsOnlyInSource = testDataEvaluationReturnList.getColumnHeadersOnlyInSourceTable()
						.toString();
				String columnsOnlyInTarget = testDataEvaluationReturnList.getColumnHeadersOnlyInTargetTable()
						.toString();
				toolTipAtImage = translate("%TestEditView_scenarioTableNotValid", columnsOnlyInTarget,
						columnsOnlyInSource);
			} else if (!dataRowColumnCountEqualsHeaderRowColumnCount) {
				toolTipAtImage = translate("%TestEditView_scenarioTableDifferentColumnCount");
			}
		}

		TestEditorButton scenarioButton = new TestEditorButton(getStyledText(), SWT.PUSH, imageScenario, toolTipAtImage);

		StyleRange buttonRange = addButton(scenarioButton.getButton(), internalText.length(), " ");
		addNewStyleRange(buttonRange);
		final String includeOfScenario = ((TestScenarioParameterTable) testComponent).getInclude();
		scenarioButton.getButton().addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				TestScenario scenario;
				try {
					if (((TestScenarioParameterTable) testComponent).isScenarioOfProject()) {
						scenario = getTestCaseController().getScenarioByFullName(includeOfScenario.substring(10));
						if (scenario != null) {
							ContextInjectionFactory.make(OpenTestStructureHandler.class, getContext()).execute(
									scenario, getContext());
						}
					} else {
						MessageDialog.openWarning(
								getStyledText().getShell(),
								translate("TestEditView_NotProjectScenarioHintTitle"),
								translate("%TestEditView_NotProjectScenario",
										((TestScenarioParameterTable) testComponent).getTitle()));
					}
				} catch (SystemException e1) {
					LOGGER.error("Error addLinkToScenario", e1);
				}
			}

		});
	}

	/**
	 * adds the ScenarioIncludeLine in the view.
	 * 
	 * @param offsetLines
	 *            offset between the view and the testFlow
	 * @param lineInTestCase
	 *            line in the testFlow
	 * @return offset between the view and the testFlow
	 */
	private int addScenarioIncludeInfoLine(int offsetLines, int lineInTestCase) {
		String scenarioIncludeInfo = translate("%TestEditView_IncludeScenario");
		addStyledRange(scenarioIncludeInfo, TextType.MASK);
		getCodeLineMapper().rememberLineOffset(offsetLines, lineInTestCase);
		return addSeperationLine(offsetLines, lineInTestCase);
	}

	/**
	 * creates a parameterTable with the headline.
	 * 
	 * @param testComp
	 *            the parameterTable
	 * 
	 * @param offsetLines
	 *            the actual offset
	 * @param line
	 *            in testcase
	 * @return int offset
	 */

	private int createParameterTable(TestScenarioParameterTable testComp, int offsetLines, int line) {
		if (!testComp.isSimpleScriptStatement()) {
			TestEditorViewTableViewerClipboard tableViewer = new TestEditorViewTableViewerClipboard(getStyledText(),
					testComp, getTranslationService(), getTestCaseController().getTestFlow(), getContext(),
					getTestCaseController().getPart(), getEventBroker());

			addTableToTableStore(tableViewer, testComp);
			int tableOffset = internalText.length();
			StyleRange range2 = addTable(tableViewer.getTable(), tableOffset, " \n");
			tableViewer.setStyleRange(range2);
			addNewStyleRange(range2);
			offsetLines++;
			rememberUnEditableLine(offsetLines + line);
			rememberLineAfterScenarioTable(offsetLines + line + 1);
			getCodeLineMapper().rememberLineOffset(offsetLines, line);

		}
		return offsetLines;

	}

	/**
	 * adds the texts to the styledText.
	 * 
	 * @param sizeTestCase
	 *            size of the testcase
	 * @param i
	 *            the line number
	 */
	private void addTextsToStyledText(int sizeTestCase, int i) {
		List<String> texts = getTestCaseController().getLine(i);
		List<TextType> types = getTestCaseController().getTextTypes(i);

		if (types.size() == 0) {
			LOGGER.error("No Texttypes for testcaseelement number = " + i);
			return;
		}

		if (types.get(0).equals(TextType.UNPARSED_ACTION_lINE)) {
			TestComponent testComponentAt = getTestCaseController().getTestComponentAt(i);
			if (testComponentAt instanceof TestActionGroup) {
				IAction action = ((TestActionGroup) testComponentAt).getActionLines().get(0);
				if (action instanceof UnparsedActionLine) {
					String translationKey = "";
					if (((UnparsedActionLine) action).getError() == UnparsedActionLine.UNCORRECT_ARGUMENT) {
						translationKey = "%TestEditViewMouseOverTipArgumentIncorrect";
					}
					String tooltip = translate(translationKey, ((UnparsedActionLine) action).getErrorParams());
					String mouseOvertip = translate("%TestEditViewMouseOverTipActionNotFound");
					addUnparsedButton(mouseOvertip + " " + tooltip);
				}
			}
		}
		for (int j = 0; j < texts.size() - 1; j++) {
			addStyledRange(texts.get(j), types.get(j));
		}
		if (texts.size() > 0) {
			if (isLineFollowing(sizeTestCase, i) || newNextLineForTableNeeded(i)) {
				addStyledRange(texts.get(texts.size() - 1) + '\n', types.get(texts.size() - 1));
			} else {
				addStyledRange(texts.get(texts.size() - 1), types.get(texts.size() - 1));
			}
		}
	}

	/**
	 * 
	 * @param line
	 *            of the testcase
	 * @return true if it's a TestScenarioPrameterTable with parameters, else
	 *         false
	 */
	private boolean newNextLineForTableNeeded(int line) {
		if (getTestCaseController().getTestComponentAt(line) instanceof TestScenarioParameterTable
				&& !((TestScenarioParameterTable) getTestCaseController().getTestComponentAt(line))
						.isSimpleScriptStatement()) {
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @param sizeTestCase
	 *            size of the testflow
	 * @param actLine
	 *            number of the actual line
	 * @return true, if at least one line is following in the testflow.
	 */
	private boolean isLineFollowing(int sizeTestCase, int actLine) {
		return actLine < sizeTestCase - 1;
	}

	/**
	 * show a welcome-screen if the testcase is empty.
	 */
	private void showWelcomeScreen() {

		rememberUnEditableLine(0);
		rememberUnEditableLine(1);
		rememberUnEditableLine(2);
		// get the filename for different languages
		String welcomeText = translate("%welcome");
		String testFlowClassname = translate("%testCase");
		if (getTestCaseController().getTestFlow() instanceof TestScenario) {
			testFlowClassname = translate("%scenario");
		}
		welcomeText = welcomeText.replace("@TestFlow", testFlowClassname);
		Image imageAction = IconConstants.ICON_ACTION;
		Image imageDescription = IconConstants.ICON_DESCRIPTION;

		String descriptionIconTarget = "@DescriptionIcon";
		int lengthDescriptionIconTarget = descriptionIconTarget.length();
		int descriptionIconPos = welcomeText.indexOf(descriptionIconTarget);

		String actionIconTarget = "@ActionIcon";
		int lengthActionIconTarget = actionIconTarget.length();
		int actionIconPos = welcomeText.indexOf(actionIconTarget);

		internalText.append(welcomeText.substring(0, descriptionIconPos));

		int offset = internalText.length();
		TestEditorButton descButton = new TestEditorButton(getStyledText(), SWT.FLAT, imageDescription,
				translate("%part.Description"));
		StyleRange range2 = addButton(descButton.getButton(), offset, " ");

		addNewStyleRange(range2);
		internalText.append(welcomeText.substring(descriptionIconPos + lengthDescriptionIconTarget, actionIconPos));

		offset = internalText.length();
		TestEditorButton actionButton = new TestEditorButton(getStyledText(), SWT.FLAT, imageAction,
				translate("%part.Action"));
		range2 = addButton(actionButton.getButton(), offset, " ");

		addNewStyleRange(range2);

		internalText.append(welcomeText.substring(actionIconPos + lengthActionIconTarget));
	}

	/**
	 * dispose and removes the tables.
	 */
	protected void disposeControls() {
		if (painter != null) {
			painter.disposeAllTables();
			painter.disposeAllButtons();
		}
		clearTableToTestComponentMapper();
	}

	/**
	 * Adds the textstyle for an testActionGroup.
	 * 
	 * @param offsetLines
	 *            the actual offset
	 * @param prevTestComponent
	 *            previous TestComponent
	 * @param line
	 *            Number of the TestComponent
	 * @param maske
	 *            Name of the Mask
	 * @param actTestComponent
	 *            the actual TestComponent
	 * @return int offset
	 */
	private int addStyleTextActionGroup(int offsetLines, TestComponent prevTestComponent, int line, String maske,
			TestComponent actTestComponent) {
		if (!(prevTestComponent instanceof TestActionGroup) // previous is not a
															// testactionGroup
				|| (!((TestActionGroup) prevTestComponent).getActionGroupName().equalsIgnoreCase(maske))) {
			// show new name in the view
			if (line > 0) {
				offsetLines = addSeperationLine(offsetLines, line);
			}
			if (!((TestActionGroup) actTestComponent).isParsedActionGroup()) {
				// show warning-icon.
				String firstPart = translate("%TestEditViewMouseOverTipActionGroupNotFoundFirstPart");
				String secondPart = translate("%TestEditViewMouseOverTippActionNotFoundSecondPart");
				addUnparsedButton(firstPart + maske + secondPart);
				addStyledRange(getMaskTitle() + maske + "\n", TextType.UNPARSED_MASK);
			} else {
				addStyledRange(getMaskTitle() + maske + "\n", TextType.MASK); //$NON-NLS-2$
			}
			getCodeLineMapper().rememberLineOffset(offsetLines, line);
			rememberUnEditableLine(offsetLines + line);
			offsetLines++;
		}
		return offsetLines;
	}

	/**
	 * adds a button with the unparsed image.
	 * 
	 * @param toolTipAtImage
	 *            String
	 */
	private void addUnparsedButton(String toolTipAtImage) {
		Image imageUnparesd = IconConstants.ICON_UNPARSED_LINE;

		TestEditorButton unparsedButton = new TestEditorButton(getStyledText(), SWT.FLAT, imageUnparesd, toolTipAtImage);

		StyleRange buttonRange = addButton(unparsedButton.getButton(), internalText.length(), " ");
		addNewStyleRange(buttonRange);

	}

	/**
	 * adds a separationline.
	 * 
	 * @param offsetLines
	 *            the actual offset
	 * @param numberOfTestComponent
	 *            Number of the TestComponent
	 * @return offset
	 */
	private int addSeperationLine(int offsetLines, int numberOfTestComponent) {
		addStyledRange("\n", TextType.DESCRIPTION);

		getCodeLineMapper().rememberLineOffset(offsetLines, numberOfTestComponent);
		rememberUnEditableLine(offsetLines + numberOfTestComponent);
		offsetLines++;

		return offsetLines;
	}

	/**
	 * adds a row for the warning image.
	 * 
	 */
	private void addRowForWarningImage() {
		if (rowForUnparsedImageNeeded) {
			int widthImageWarning = IconConstants.ICON_UNPARSED_LINE.getBounds().width;
			getStyledText().setLineIndent(0, getStyledText().getLineCount(), widthImageWarning);
			getStyledText().setLineWrapIndent(0, getStyledText().getLineCount(), widthImageWarning);
			List<Integer> buttonOffsets = painter.getButtonOffsets();
			for (Integer buttonOffset : buttonOffsets) {
				int unparsedLine = getStyledText().getLineAtOffset(buttonOffset);
				getStyledText().setLineIndent(unparsedLine, 1, 0);
			}
		}
	}

	/**
	 * creates the different Ranges whit different Styles.
	 * 
	 * @param inputString
	 *            InputString
	 * @param type
	 *            Text_type
	 */
	private void addStyledRange(String inputString, TextType type) {

		StyleRange styleRangeTxtTemp = new StyleRange();
		styleRangeTxtTemp.start = internalText.length();
		styleRangeTxtTemp.length = inputString.length();

		Font font = null;

		if (type.equals(TextType.TEXT)) {
			font = TestEditorFontConstants.FONT_NORMAL;
		} else if (type.equals(TextType.MASK)) {
			styleRangeTxtTemp.fontStyle = SWT.BOLD;
			styleRangeTxtTemp.underline = true;
		} else if (type.equals(TextType.ACTION_NAME)) {
			styleRangeTxtTemp.foreground = ColorConstants.COLOR_DARK_GREEN;
			font = TestEditorFontConstants.FONT_NORMAL;
		} else if (type.equals(TextType.ARGUMENT)) {
			styleRangeTxtTemp.foreground = ColorConstants.COLOR_BLUE;
			font = TestEditorFontConstants.FONT_NORMAL;
		} else if (type.equals(TextType.DESCRIPTION)) {
			font = TestEditorFontConstants.FONT_ITALIC;
		} else if (type.equals(TextType.UNPARSED_MASK)) {
			font = createUnparsedActionLineStyle(styleRangeTxtTemp);
		} else if (type.equals(TextType.UNPARSED_ACTION_lINE)) {
			font = createUnparsedActionLineStyle(styleRangeTxtTemp);
		} else if (type.equals(TextType.SCENARIO_TITLE)) {
			styleRangeTxtTemp.background = ColorConstants.COLOR_BLUE;
			styleRangeTxtTemp.foreground = ColorConstants.COLOR_WHITE;
		}
		styleRangeTxtTemp.font = font;

		internalText.append(inputString);

		addNewStyleRange(styleRangeTxtTemp);

	}

	/**
	 * creates the StyleRange for an unparsed ActionLine.
	 * 
	 * @param styleRange1
	 *            StyleRange
	 * @return font
	 */
	private Font createUnparsedActionLineStyle(StyleRange styleRange1) {
		rowForUnparsedImageNeeded = true;
		Font font;
		int offset = internalText.length();

		styleRange1.start = offset;
		styleRange1.underline = true;
		font = TestEditorFontConstants.FONT_UNDERLINE;
		return font;
	}

	/**
	 * this method adds a button into the styledText.
	 * 
	 * @param button
	 *            Button
	 * @param offset
	 *            offset of the position in the text
	 * @param appendedText
	 *            text to append
	 * @return StyleRange
	 */
	private StyleRange addButton(Button button, int offset, String appendedText) {
		button.pack();
		painter.addButton(button, offset);
		StyleRange style = new StyleRange();
		style.start = offset;
		style.length = 1;
		internalText.append(appendedText);

		Rectangle rect = button.getBounds();
		int ascent = 2 * rect.height / 3;
		int descent = rect.height - ascent;
		style.metrics = new GlyphMetrics(ascent, descent, rect.width);
		return style;
	}

	/**
	 * this method adds an table into the styledText.
	 * 
	 * @param grid
	 *            Nebula-Grid
	 * @param offset
	 *            offset of the position in the text
	 * @param appendedText
	 *            text to append
	 * @return StyleRange
	 */
	private StyleRange addTable(Grid grid, int offset, String appendedText) {
		for (GridColumn col : grid.getColumns()) {
			col.pack();
		}
		painter.addTable(grid, offset);
		StyleRange style = new StyleRange();
		style.start = offset;
		style.length = 1;
		grid.setSize(getStyledText().getSize().x, TestEditorConstants.TABLE_DEFAULT_HEIGTH);
		grid.setVisible(false);
		grid.getParent().setVisible(false);
		internalText.append(appendedText);

		Rectangle rect = grid.getBounds();
		int ascent = 2 * rect.height / 3;
		int descent = rect.height - ascent;
		style.metrics = new GlyphMetrics(ascent, descent, rect.width);
		return style;
	}

	/**
	 * close the popup-dialogs.
	 */
	public void closePopupDialog() {
		if (popupDialogAction != null) {
			popupDialogAction.close();
		}
		if (popupDialogDescription != null) {
			popupDialogDescription.close();
		}
	}

	/**
	 * edits the selected line in a popup-dialog.
	 * 
	 * @param data
	 *            optional data
	 */
	@Inject
	@Optional
	protected void editLineInPopupDialog(@UIEventTopic(TestEditorUIEventConstants.EDIT_CONTEXTMENU_F6) String data) {
		if (isThisPartOnTop() && !isFocusInSubComponent()) {
			ITestEditorController testCaseController = getTestCaseController();
			int lineIndex = getStyledText().getLineAtOffset(getStyledText().getSelectionRanges()[0]);
			if (lineIndex >= 0 && isLineEditable(lineIndex)) {
				int lineInTestCase = getCodeLineMapper().getContentOfOffsetViewToTestListAt(lineIndex);
				if (testCaseController.getTestComponentAt(lineInTestCase) instanceof TestDescription) {
					showDescriptionPopupDialog();
				} else if (testCaseController.getTestComponentAt(lineInTestCase) instanceof TestActionGroup) {
					showActionGroupPopupDialog();
				}
				try {
					showSelectedContentInInputArea(lineInTestCase);
				} catch (SystemException e) {
					LOGGER.error("error by getting the scenario", e);
					MessageDialog.openError(Display.getCurrent().getActiveShell(), "System-Exception", e.getMessage());
				}

			}
		}
	}

	/**
	 * removes the modifyListener from the styledText.
	 */
	public void removeStyledTextModifyListener() {
		if (modifyListener != null && !getStyledText().isDisposed()) {
			getStyledText().removeModifyListener(modifyListener);
			modifyListener = null;
		}
	}

	/**
	 * Key event for selecting text with cursor as start or end point.
	 * 
	 * @param data
	 *            optional data
	 */
	@Inject
	@Optional
	protected void selectLineAtCursor(@UIEventTopic(TestEditorUIEventConstants.EDIT_CONTEXTMENU_HOME_OR_END) String data) {
		if (isThisPartOnTop() && !isFocusInSubComponent()) {
			markSelectedLine(getStyledText().getLineAtOffset(getStyledText().getCaretOffset()));
		}
	}

	/**
	 * Key event for complete text selection.
	 * 
	 * @param data
	 *            optional data
	 */
	@Inject
	@Optional
	protected void selectAllText(@UIEventTopic(TestEditorUIEventConstants.EDIT_CONTEXTMENU_CNTRL_A) String data) {
		if (isThisPartOnTop() && !isFocusInSubComponent()) {
			getStyledText().selectAll();
			setClickedLine(0);
			setReleasedLine(getStyledText().getLineCount() - 1);
		}
	}

	/**
	 * Get lines of selection and shows content in input area.
	 * 
	 * @param data
	 *            optional data
	 */
	@Inject
	@Optional
	protected void showSelectedLineInInputArea(
			@UIEventTopic(TestEditorUIEventConstants.EDIT_CONTEXTMENU_CNTRL_INSERT) String data) {
		int lineIndex = getStyledText().getLineAtOffset(getStyledText().getCaretOffset());
		int lineInTestCase = getCodeLineMapper().getContentOfOffsetViewToTestListAt(lineIndex);
		try {
			showSelectedContentInInputArea(lineInTestCase);
		} catch (SystemException e1) {
			MessageDialog.openError(Display.getCurrent().getActiveShell(), "System-Exception", e1.getMessage());
			LOGGER.error(e1.getMessage());
		}
	}

	/**
	 * Get keyHandler.
	 * 
	 * @return keyHandler
	 */
	public TestEditorViewKeyHandler getKeyHandler() {
		return keyHandler;
	}

	/**
	 * get PopupDialogDescription.
	 * 
	 * @return popupDialogDescription
	 */
	PopupDialog getPopupDialogDescription() {
		return popupDialogDescription;
	}

	/**
	 * get popupDialogAction.
	 * 
	 * @return popupDialogAction
	 */
	PopupDialog getActionGroupPopupDialog() {
		return popupDialogAction;
	}

}
