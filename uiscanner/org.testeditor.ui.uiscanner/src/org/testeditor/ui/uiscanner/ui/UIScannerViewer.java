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
package org.testeditor.ui.uiscanner.ui;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.testeditor.ui.uiscanner.expressions.Expression;
import org.testeditor.ui.uiscanner.expressions.ExpressionException;
import org.testeditor.ui.uiscanner.expressions.ExpressionReader;
import org.testeditor.ui.uiscanner.ui.handler.ScanHandler;
import org.testeditor.ui.uiscanner.ui.table.UiScannerGridTableViewerCreator;
import org.testeditor.ui.uiscanner.webscanner.ScannerReadAndWriter;
import org.testeditor.ui.uiscanner.webscanner.UiScannerConstants;
import org.testeditor.ui.uiscanner.webscanner.UiScannerWebElement;
import org.testeditor.ui.uiscanner.webscanner.WebScanner;

/**
 * UiScanner View.
 * 
 * @author dkuhlmann
 * 
 */
public class UIScannerViewer {
	private Text textUrl;
	private Text textXPath;
	private Text textCustomExpressionPath;
	private Button radioStandart;
	private Button radioCustom;
	private Button buttonBrowser;
	private Button buttonScan;
	private Button buttonReset;
	private Button checkAll;
	private Button unCheckAll;
	private Button filterXPath;
	private Button filterButton;
	private Button filterInput;
	private Button filterSelect;
	private Button filterRadio;
	private Button filterCheckbox;
	private Button filterAll;
	private WebScanner webScanner;
	private TabFolder tabfolder;

	private ArrayList<UiScannerWebElement> webElements;
	private GridTableViewer tableViewer;

	@Inject
	private UiScannerTranslationService translate;
	@Inject
	private IEclipseContext context;
	private static final Logger LOGGER = Logger.getLogger(UIScannerViewer.class);

	/**
	 * create the View.
	 * 
	 * @param parent
	 *            Composite
	 */
	@PostConstruct
	public void createUI(Composite parent) {
		webScanner = new WebScanner();
		parent.setLayout(new GridLayout());

		GridLayout layout = new GridLayout();
		layout.marginHeight = 5;
		layout.marginWidth = 5;
		// the number of pixels of vertical margin that will be placed along
		// the top and bottom edges of the layout.

		layout.makeColumnsEqualWidth = true;// make each column have same width
		layout.numColumns = 10;

		Composite browserGroup = new Composite(parent, SWT.BORDER);
		browserGroup.setLayout(layout);
		browserGroup.setLayoutData(getGridData(10, false));
		Label urlLabel = new Label(browserGroup, SWT.NORMAL);
		urlLabel.setText(translate.translate("%VIEW_LABEL_URL"));
		createTextURL(browserGroup, SWT.NORMAL);
		createButtonBrowser(browserGroup, SWT.NORMAL);

		Composite settingGroup = new Composite(parent, SWT.BORDER);
		settingGroup.setLayout(layout);
		settingGroup.setLayoutData(getGridData(10, false));
		createSettingfield(settingGroup, SWT.NORMAL);

		Composite filterGroup = new Composite(parent, SWT.BORDER);
		filterGroup.setLayout(layout);
		filterGroup.setLayoutData(getGridData(10, false));
		createCheckboxFilters(filterGroup, SWT.NORMAL);

		createTabfolder(parent, layout, SWT.NORMAL);
	}

	/**
	 * create the Setting part for the uiscanner.
	 * 
	 * @param parent
	 *            Composite
	 * @param swtStyle
	 *            SWT Style
	 */
	private void createSettingfield(final Composite parent, int swtStyle) {

		radioStandart = new Button(parent, swtStyle | SWT.RADIO);
		radioStandart.setSelection(true);
		radioStandart.setText(translate.translate("%VIEW_RADIO_LABEL_STANDARD"));
		radioStandart.setLayoutData(getGridData(1, true));
		radioCustom = new Button(parent, swtStyle | SWT.RADIO);
		radioCustom.setText(translate.translate("%VIEW_RADIO_LABEL_CUSTOM"));
		radioCustom.setLayoutData(getGridData(1, true));

		textCustomExpressionPath = new Text(parent, swtStyle);
		textCustomExpressionPath.setLayoutData(getGridData(6, true));

		Button openButton = new Button(parent, swtStyle);
		openButton.setText(translate.translate("%VIEW_LABEL_BUTTON_SELECTFILE"));
		openButton.setLayoutData(getGridData(2, true));
		openButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fileDialog = new FileDialog(Display.getDefault().getActiveShell());
				fileDialog.setText("Select File");
				fileDialog.setFilterExtensions(new String[] { "*.txt" });
				fileDialog.setFilterNames(new String[] { "Textfiles(*.txt)" });
				String selected = fileDialog.open();
				textCustomExpressionPath.setText(selected);
			}
		});

	}

	/**
	 * Creates the TabFolder with the Tabs Table (for the webelements),
	 * AllactionGroup and Elementlist.
	 * 
	 * @param parent
	 *            Composite
	 * @param layout
	 *            GridLayout for the tabfolder
	 * @param swtStyle
	 *            int SWT Style.
	 */
	private void createTabfolder(Composite parent, GridLayout layout, int swtStyle) {
		tabfolder = new TabFolder(parent, swtStyle);
		tabfolder.setLayoutData(getGridData(10, true));

		Composite comp = new Composite(tabfolder, swtStyle);
		comp.setLayout(layout);
		comp.setLayoutData(getGridData(10, true));

		TabItem tabWebItem = new TabItem(tabfolder, swtStyle);
		tabWebItem.setText(translate.translate("%VIEW_LABEL_TABITEM_WEBELEMENT"));
		tabWebItem.setControl(comp);

		webElements = new ArrayList<UiScannerWebElement>();

		UiScannerGridTableViewerCreator tablecreator = ContextInjectionFactory.make(
				UiScannerGridTableViewerCreator.class, context);
		tableViewer = tablecreator.createTable(comp, swtStyle | SWT.BORDER, webElements, webScanner);
		tableViewer.getGrid().setLayoutData(getGridData(10, true));

		createCheckAllTableItmesButton(comp, swtStyle);
		createUncheckAllTableItmesButton(comp, swtStyle);
		createClearTableButton(comp, swtStyle);

		SashForm sfActionList = new SashForm(tabfolder, swtStyle);
		final TabItem tabActionListItem = new TabItem(tabfolder, swtStyle);
		tabActionListItem.setText(translate.translate("%VIEW_LABEL_TABITEM_ALLACTIONGROUP"));
		tabActionListItem.setControl(sfActionList);
		final Text textActionList = new Text(sfActionList, swtStyle | SWT.MULTI | SWT.V_SCROLL);
		textActionList.addKeyListener(createKeyAdapterForText(textActionList));

		SashForm sfElementList = new SashForm(tabfolder, swtStyle);
		final TabItem tabElementListItem = new TabItem(tabfolder, swtStyle);
		tabElementListItem.setText(translate.translate("%VIEW_LABEL_TABITEM_ELEMENTLIST"));
		tabElementListItem.setControl(sfElementList);
		final Text textElementList = new Text(sfElementList, swtStyle | SWT.MULTI | SWT.V_SCROLL);
		textElementList.addKeyListener(createKeyAdapterForText(textElementList));
		tabfolder.addSelectionListener(createTabSelectionAdapter(tabElementListItem, textElementList,
				tabActionListItem, textActionList));

	}

	/**
	 * Creates a KeyAdapter which select all text in a Text field by pressing
	 * ctrl+a.
	 * 
	 * @param text
	 *            Text field where the text should be selected
	 * @return KeyAdapter
	 */
	private KeyAdapter createKeyAdapterForText(final Text text) {
		return new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.stateMask == SWT.CTRL && e.keyCode == 'a') {
					text.selectAll();
				}

			}
		};
	}

	/**
	 * 
	 * Create the a Selection Adapter for the Tabfolder. When the Tabfolder
	 * selection change to ElementList or AllActionGroup its fills the text
	 * field with the WebElements.
	 * 
	 * @param tabElementListItem
	 *            TabItem
	 * @param textElementList
	 *            Text
	 * @param tabActionListItem
	 *            TabItem
	 * @param textActionList
	 *            Text
	 * @return SelectionAdapter
	 */
	private SelectionAdapter createTabSelectionAdapter(final TabItem tabElementListItem, final Text textElementList,
			final TabItem tabActionListItem, final Text textActionList) {
		return new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (e.item.equals(tabElementListItem)) {
					ArrayList<UiScannerWebElement> elements = new ArrayList<>();
					for (GridItem elem : tableViewer.getGrid().getItems()) {
						if (elem.getChecked()) {
							elements.add((UiScannerWebElement) elem.getData());
						}
					}
					ScannerReadAndWriter writer = new ScannerReadAndWriter();
					textElementList.setText(writer.generateELementList(elements));
				}
				if (e.item.equals(tabActionListItem)) {
					ArrayList<UiScannerWebElement> elements = new ArrayList<>();
					for (GridItem elem : tableViewer.getGrid().getItems()) {
						if (elem.getChecked()) {
							elements.add((UiScannerWebElement) elem.getData());
						}
					}
					try {
						ScannerReadAndWriter writer = new ScannerReadAndWriter();
						textActionList.setText(writer.generateActionGroup(elements));
					} catch (Exception e1) {
						LOGGER.error("could not generate ActionGroup" + e1);
						MessageDialog.openError(Display.getDefault().getActiveShell(),
								translate.translate("%DIALOG_ACTIONGROUP_TITLE"),
								translate.translate("%DIALOG_ACTIONGROUP_MESSAGE"));
					}
				}
			}
		};
	}

	/**
	 * creates the Checkboxs for the filter part on the view.
	 * 
	 * @param parent
	 *            Composite: where the filter should be added.
	 * @param style
	 *            int: Style for the Composites.
	 */
	private void createCheckboxFilters(Composite parent, int style) {
		filterButton = new Button(parent, style | SWT.CHECK);
		filterButton.setText(translate.translate("%VIEW_LABEL_FILTER_BUTTON"));
		filterButton.setSelection(true);
		filterButton.setLayoutData(getGridData(1, false));

		filterInput = new Button(parent, style | SWT.CHECK);
		filterInput.setText(translate.translate("%VIEW_LABEL_FILTER_INPUT"));
		filterInput.setSelection(true);
		filterInput.setLayoutData(getGridData(1, false));

		filterSelect = new Button(parent, style | SWT.CHECK);
		filterSelect.setText(translate.translate("%VIEW_LABEL_FILTER_SELECT"));
		filterSelect.setSelection(true);
		filterSelect.setLayoutData(getGridData(1, false));

		filterRadio = new Button(parent, style | SWT.CHECK);
		filterRadio.setText(translate.translate("%VIEW_LABEL_FILTER_RADIO"));
		filterRadio.setSelection(true);
		filterRadio.setLayoutData(getGridData(1, false));

		filterCheckbox = new Button(parent, style | SWT.CHECK);
		filterCheckbox.setText(translate.translate("%VIEW_LABEL_FILTER_CHECKBOX"));
		filterCheckbox.setSelection(true);
		filterCheckbox.setLayoutData(getGridData(1, false));

		filterAll = new Button(parent, style | SWT.CHECK);
		filterAll.setText(translate.translate("%VIEW_LABEL_FILTER_ALL"));
		filterAll.setLayoutData(getGridData(1, false));

		Label space = new Label(parent, SWT.NONE);
		space.setLayoutData(getGridData(2, true));

		createScanButton(parent, style);

		filterXPath = new Button(parent, style | SWT.CHECK);
		filterXPath.setText(translate.translate("%VIEW_LABEL_FILTER_XPATH"));
		filterXPath.setLayoutData(getGridData(1, false));

		textXPath = new Text(parent, style);
		textXPath.setText(translate.translate("%DEFAULT_XPATH_SAMPLE"));
		textXPath.setLayoutData(getGridData(7, false));
	}

	/**
	 * create the Button to reset the Table.
	 * 
	 * @param parent
	 *            Composite: where the button should be added.
	 * @param style
	 *            int: Style for the Composites.
	 */
	private void createClearTableButton(Composite parent, int style) {
		buttonReset = new Button(parent, style);
		buttonReset.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				webElements.clear();
				tableViewer.refresh();
			}
		});
		buttonReset.setText(translate.translate("%VIEW_LABEL_BUTTON_TABLE_CLEAR"));
		buttonReset.setLayoutData(getGridData(2, false));
	}

	/**
	 * Check al the TableItems in the Table.
	 * 
	 * @param parent
	 *            Composite: where the button should be added.
	 * @param style
	 *            int: Style for the Composites.
	 */
	private void createCheckAllTableItmesButton(Composite parent, int style) {
		checkAll = new Button(parent, style);
		checkAll.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				for (GridItem item : tableViewer.getGrid().getItems()) {
					item.setChecked(true);
				}
				tableViewer.refresh();
			}
		});
		checkAll.setText(translate.translate("%VIEW_LABEL_BUTTON_TABLE_CHECK_ALL"));
		checkAll.setLayoutData(getGridData(2, false));
	}

	/**
	 * Dechecked all TableItems in the Table.
	 * 
	 * @param parent
	 *            Composite: where the button should be added.
	 * @param style
	 *            int: Style for the Composites.
	 */
	private void createUncheckAllTableItmesButton(Composite parent, int style) {
		unCheckAll = new Button(parent, style);
		unCheckAll.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				for (GridItem item : tableViewer.getGrid().getItems()) {
					item.setChecked(false);
				}
				tableViewer.refresh();
			}
		});
		unCheckAll.setText(translate.translate("%VIEW_LABEL_BUTTON_TABLE_UNCHECK_ALL"));
		unCheckAll.setLayoutData(getGridData(2, false));
	}

	/**
	 * create the Button to scan the Website.
	 * 
	 * @param parent
	 *            Composite: where the button should be added.
	 * @param style
	 *            int: Style for the Composites.
	 */
	private void createScanButton(Composite parent, int style) {

		buttonScan = new Button(parent, style);
		buttonScan.setText(translate.translate("%VIEW_LABEL_BUTTON_SCAN"));
		buttonScan.setLayoutData(getGridData(2, false));
		buttonScan.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (webScanner.isWebDriverAktive()) {

					ArrayList<String> filters = new ArrayList<>();
					if (filterAll.getSelection()) {
						filters.add(UiScannerConstants.TYP_ALL);
					}
					if (filterButton.getSelection()) {
						filters.add(UiScannerConstants.TYP_BUTTON);
					}
					if (filterCheckbox.getSelection()) {
						filters.add(UiScannerConstants.TYP_CHECKBOX);
					}
					if (filterRadio.getSelection()) {
						filters.add(UiScannerConstants.TYP_RADIO);
					}
					if (filterInput.getSelection()) {
						filters.add(UiScannerConstants.TYP_INPUT);
					}
					if (filterSelect.getSelection()) {
						filters.add(UiScannerConstants.TYP_SELECT);
					}
					if (filterXPath.getSelection()) {
						filters.add(UiScannerConstants.TYP_XPATH);
					}
					scanAndAddWebElements(filters, textXPath.getText());

				} else {
					MessageDialog.openError(Display.getDefault().getActiveShell(),
							translate.translate("%DIALOG_BROWSER_NOT_REACHABLE_TITLE"),
							translate.translate("%DIALOG_BROWSER_NOT_REACHABLE_MESSAGE"));
				}
			}
		});
	}

	/**
	 * Scan the open website and add the WebElements to the TableViewer.
	 * 
	 * @param filters
	 *            ArrayList<String> of all set filters.
	 * @param xPath
	 *            String of the xPath.
	 */
	private void scanAndAddWebElements(ArrayList<String> filters, String xPath) {
		if (radioStandart.getSelection()) {
			ScanHandler scanHandler = ContextInjectionFactory.make(ScanHandler.class, context);
			Display display = Display.getDefault();
			try {
				scanHandler.execute(display.getActiveShell(), webScanner, filters, xPath, webElements);
			} catch (InvocationTargetException e) {
				LOGGER.error("Could not scan website - InvocationTargetException:" + e);
			} catch (InterruptedException e) {
				LOGGER.error("Could not scan website - InterruptedException:" + e);
			}
		} else if (radioCustom.getSelection()) {
			if (!textCustomExpressionPath.getText().equals("")) {
				ExpressionReader reader = new ExpressionReader();
				HashMap<String, Expression> exprs = null;
				try {
					exprs = reader.readCheck(textCustomExpressionPath.getText());
				} catch (IOException e) {
					LOGGER.error("Could not read the Exception file (" + textCustomExpressionPath.getText() + "):" + e);
					MessageDialog.openError(Display.getDefault().getActiveShell(),
							translate.translate("%DIALOG_EXPRESSION_WRONG_PATH_TITLE"),
							translate.translate("%DIALOG_EXPRESSION_WRONG_PATH_MESSAGE"));
				} catch (ExpressionException e) {
					LOGGER.error("the Exception file has an error (" + textCustomExpressionPath.getText() + "):" + e);
					MessageDialog.openError(Display.getDefault().getActiveShell(),
							translate.translate("%DIALOG_EXPRESSION_EXEPTION_TITLE"),
							translate.translate("%DIALOG_EXPRESSION_EXEPTION_MESSAGE"));
				}
				if (exprs != null) {
					for (UiScannerWebElement elem : webScanner.scanFilteredWithExpression(
							new ArrayList<UiScannerWebElement>(), exprs, filters, xPath)) {
						// tableViewer.add(elem);
						webElements.add(elem);
					}
				}
			} else {
				MessageDialog.openError(Display.getDefault().getActiveShell(),
						translate.translate("%DIALOG_EXPRESSION_PATH_TITLE"),
						translate.translate("%DIALOG_EXPRESSION_PATH_MESSAGE"));
			}
		}
		tableViewer.refresh();
	}

	/**
	 * create the Button for to start the browser.
	 * 
	 * @param parent
	 *            Composite: where the button should be added.
	 * @param style
	 *            int: Style for the Composites.
	 */
	private void createButtonBrowser(Composite parent, int style) {
		final Combo browserSelection = new Combo(parent, style);
		browserSelection.setItems((String[]) UiScannerConstants.BROWSERS.toArray());
		browserSelection.select(0);
		browserSelection.setLayoutData(getGridData(1, false));
		buttonBrowser = new Button(parent, style);
		buttonBrowser.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				webScanner.openBrowser(browserSelection.getText(), textUrl.getText());
			}
		});
		buttonBrowser.setText(translate.translate("%VIEW_LABEL_BUTTON_START"));
		buttonBrowser.setLayoutData(getGridData(2, false));
	}

	/**
	 * create the textfield for the browser URL.
	 * 
	 * @param parent
	 *            Composite: where the textfield should be added.
	 * @param style
	 *            int: Style for the Composites.
	 */
	private void createTextURL(Composite parent, int style) {
		textUrl = new Text(parent, style);
		textUrl.setText(translate.translate("%DEFAULT_URL"));
		textUrl.setLayoutData(getGridData(6, true));

		// pahth for debugging
		// textUrl.setText("file:///D:/Workspace/org.testeditor.ui.uiscanner.test/ressources/web/index.html");
	}

	/**
	 * Returns the standart GridData for the layoutDatas.
	 * 
	 * @param horizontSize
	 *            int
	 * @param grabVerticalSpace
	 *            boolean
	 * @return GridData
	 */
	private GridData getGridData(int horizontSize, boolean grabVerticalSpace) {
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalSpan = horizontSize;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = grabVerticalSpace;
		return gridData;
	}

	/**
	 * close the open browser.
	 */
	public void closeBrowser() {
		webScanner.quit();
	}

	/**
	 * close the Scanner.
	 */
	@PreDestroy
	public void dispose() {
		closeBrowser();
	}
}
