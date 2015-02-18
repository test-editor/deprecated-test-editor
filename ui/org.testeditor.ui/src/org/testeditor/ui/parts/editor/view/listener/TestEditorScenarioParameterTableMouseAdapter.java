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
package org.testeditor.ui.parts.editor.view.listener;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.testeditor.ui.constants.IconConstants;
import org.testeditor.ui.constants.TestEditorUIEventConstants;
import org.testeditor.ui.parts.editor.view.TestEditorViewTableViewerClipboard;
import org.testeditor.ui.utilities.TestEditorTranslationService;

/**
 * 
 * this class provides the {@link MouseAdapter} for a ScenarioTable in
 * TestEditView.
 * 
 * @author llipinski
 */
public class TestEditorScenarioParameterTableMouseAdapter extends MouseAdapter {

	private Shell shell;
	private TestEditorTranslationService translationService;
	private TestEditorViewTableViewerClipboard tableViewer;
	private boolean selectAll = true;
	private boolean isPopupdialog;

	private int defaultTableHeight;
	private int defaultDescent;
	private int heightLine;
	private StyleRange style;
	private IEventBroker eventBroker;

	/**
	 * constructor with the necessary parameters.
	 * 
	 * @param shell
	 *            the shell of the table
	 * @param translationService
	 *            the TranslationService
	 * @param tableViewer
	 *            the TableViewer for the imported content
	 * @param style
	 *            the StyleRange of the table
	 * @param eventBroker
	 *            IEventBroker
	 */
	public TestEditorScenarioParameterTableMouseAdapter(Shell shell, TestEditorTranslationService translationService,
			TestEditorViewTableViewerClipboard tableViewer, StyleRange style, IEventBroker eventBroker) {
		super();
		this.shell = shell;
		this.translationService = translationService;
		this.tableViewer = tableViewer;
		this.style = style;
		defaultDescent = style.metrics.descent;
		Rectangle rect = tableViewer.getTable().getBounds();
		defaultTableHeight = rect.height;
		if (tableViewer.getTable().getItemCount() != 0) {
			heightLine = tableViewer.getTable().getItem(0).getBounds(0).height;
		} else {
			heightLine = defaultTableHeight;
		}

		this.eventBroker = eventBroker;

	}

	@Override
	public void mouseUp(MouseEvent e) {
		// send a signal to the world
		tableViewer.sendGetFocusSignal();

		if (e.button == 3) {

			Menu menu = createFileImportMenuItem();

			MenuItem menuItemAdd = new MenuItem(menu, SWT.NONE);

			menuItemAdd.setText(translate("%TestEditViewToolbar_addLine"));

			menuItemAdd.setImage(IconConstants.ICON_ADD_LINE);
			menuItemAdd.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					tableViewer.addLineInTable();
				}
			});

			MenuItem menuItemCut = new MenuItem(menu, SWT.NONE);

			menuItemCut.setText(translate("%TestEditViewToolbar_cutLine"));
			menuItemCut.setEnabled(tableViewer.isTableRowSelected());

			menuItemCut.setImage(IconConstants.ICON_CUT);
			menuItemCut.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					tableViewer.cutSelectedLinesFromTable();
				}
			});

			MenuItem menuItemCopy = new MenuItem(menu, SWT.NONE);

			menuItemCopy.setText(translate("%TestEditViewToolbar_copyLine"));
			menuItemCopy.setEnabled(tableViewer.isTableRowSelected());
			menuItemCopy.setImage(IconConstants.ICON_COPY);
			menuItemCopy.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					tableViewer.copySelectedLinesFromTable();
				}
			});

			MenuItem menuItemPaste = new MenuItem(menu, SWT.NONE);

			menuItemPaste.setText(translate("%TestEditViewToolbar_pasteLine"));
			menuItemPaste.setEnabled(tableViewer.isTableRowSelected() && tableViewer.canExecutePasteTestDataRow());
			menuItemPaste.setImage(IconConstants.ICON_PASTE);
			menuItemPaste.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					tableViewer.getSelectedRowsFromClipboardAndPasteIntoTable();
					selectAll = false;
				}
			});
			MenuItem menuItemDelete = new MenuItem(menu, SWT.NONE);

			menuItemDelete.setText(translate("%TestEditViewToolbar_deleteLine"));
			menuItemDelete.setImage(IconConstants.ICON_DELETE);
			menuItemDelete.setEnabled(tableViewer.isTableRowSelected());
			menuItemDelete.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					tableViewer.removeLineFromTable();
				}
			});

			selectDeSelectAllItems(menu);
			menu.setVisible(true);

		}
	}

	/**
	 * 
	 * @param menu
	 *            toggles between selectAll and DeselectAll.
	 */
	private void selectDeSelectAllItems(Menu menu) {
		MenuItem menuItemSelectAll = new MenuItem(menu, SWT.NONE);

		menuItemSelectAll.setText(translate("%TestEditViewToolbar_selectAllItems"));
		menuItemSelectAll.setImage(IconConstants.ICON_CHECKED);
		menuItemSelectAll.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				tableViewer.selectAllItems(true);
			}
		});

		MenuItem menuItemDeSelectAll = new MenuItem(menu, SWT.NONE);

		menuItemDeSelectAll.setText(translate("%TestEditViewToolbar_deselectAllItems"));
		menuItemDeSelectAll.setImage(IconConstants.ICON_UNCHECKED);
		menuItemDeSelectAll.setEnabled(tableViewer.isTableRowSelected());
		menuItemDeSelectAll.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				tableViewer.selectAllItems(false);
			}
		});
		MenuItem menuItemExpand = new MenuItem(menu, SWT.NONE);
		menuItemExpand.setText(translate("%TestEditViewToolbar_expandTable"));
		Rectangle rect = tableViewer.getTable().getBounds();
		menuItemExpand.setEnabled(style.metrics.descent == defaultDescent);
		menuItemExpand.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Rectangle rect = tableViewer.getTable().getBounds();
				int height = (tableViewer.getTable().getItemCount() + 3) * heightLine;

				if (height < defaultTableHeight) {
					height = defaultTableHeight;
				}

				if (height > rect.height) {
					rect.height = height;
				}
				tableViewer.getTable().setBounds(rect);

				style.metrics.descent = height - style.metrics.ascent;
				eventBroker.send(TestEditorUIEventConstants.TESTEDITOR_VIEW_CHNAGED_TABLE_EXPANDED, tableViewer);
			}
		});
		// collapse
		MenuItem menuItemCollapse = new MenuItem(menu, SWT.NONE);
		menuItemCollapse.setText(translate("%TestEditViewToolbar_collapseTable"));
		menuItemCollapse.setEnabled(rect.height > defaultTableHeight);
		menuItemCollapse.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Rectangle rect = tableViewer.getTable().getBounds();
				rect.height = defaultTableHeight;
				tableViewer.getTable().setBounds(rect);
				style.metrics.descent = defaultDescent;
				eventBroker.send(TestEditorUIEventConstants.TESTEDITOR_VIEW_CHNAGED_TABLE_COLLAPSED, tableViewer);
			}
		});
	}

	/**
	 * toggles the value of selectAll.
	 */
	protected void changeSelectAll() {
		if (selectAll) {
			selectAll = false;
		} else {
			selectAll = true;
		}

	}

	/**
	 * 
	 * @return the FileImportMenuItem.
	 */
	private Menu createFileImportMenuItem() {
		Menu menu = new Menu(shell, SWT.POP_UP);
		if (!isPopupdialog) {
			// Create the first separator
			MenuItem menuItem = new MenuItem(menu, SWT.NONE);
			menuItem.setText(translate("%TestEditViewToolbar_importfile"));

			menuItem.setImage(IconConstants.ICON_EXCEL_IMPORT);
			menuItem.setEnabled(true);
			menuItem.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {

					handleFileImport();
				}
			});
		}
		return menu;
	}

	/**
	 * returns the translation for a given string form the language-resource.
	 * 
	 * @param translateKey
	 *            the key for the translation
	 * @return the translation
	 */
	protected String translate(String translateKey) {
		return translationService.translate(translateKey);
	}

	/**
	 * Handles the file import.
	 * 
	 */
	private void handleFileImport() {
		tableViewer.handleFileImport();
	}
}