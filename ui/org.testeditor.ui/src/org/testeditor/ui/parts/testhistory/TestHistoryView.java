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
package org.testeditor.ui.parts.testhistory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableColumn;
import org.testeditor.ui.constants.CustomWidgetIdConstants;
import org.testeditor.ui.utilities.TestEditorTranslationService;

/**
 * Part to show history of tests.
 * 
 */
public class TestHistoryView {

	@Inject
	private TestEditorTranslationService translationService;

	private TableViewer tableViewer;
	private Composite mainComposite;
	private Label nameOfTestHistory;
	private ScrolledComposite scrolledComposite;

	/**
	 * Building the ui.
	 * 
	 * @param parent
	 *            the parent {@link Composite}
	 */
	@PostConstruct
	public void createUi(Composite parent) {
		parent.setLayout(new GridLayout(1, true));
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		parent.setLayoutData(new FillLayout(SWT.NORMAL));
		mainComposite = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		mainComposite.setLayoutData(gd);
		mainComposite.setLayout(gridLayout);

		nameOfTestHistory = new Label(mainComposite, SWT.NORMAL);
		nameOfTestHistory.setText(translationService.translate("%label.testhistory.of"));
		nameOfTestHistory.setData(CustomWidgetIdConstants.TEST_EDITOR_WIDGET_ID_SWT_BOT_KEY,
				CustomWidgetIdConstants.TEST_HISTORY_LABEL);
		Composite compositeForTable = new Composite(mainComposite, SWT.NONE);
		compositeForTable.setLayout(new FillLayout(SWT.NONE));
		compositeForTable.setLayoutData(gd);
		createHistoryTable(compositeForTable);
		mainComposite.setVisible(false);
	}

	/**
	 * creates the table-view of the history.
	 * 
	 * @param parent
	 *            the composite of the parent
	 */
	private void createHistoryTable(Composite parent) {
		scrolledComposite = new ScrolledComposite(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);

		tableViewer = new TableViewer(scrolledComposite, SWT.BORDER | SWT.FULL_SELECTION);
		tableViewer.getTable().setHeaderVisible(true);
		tableViewer.getTable().setLinesVisible(true);

		new TableColumn(tableViewer.getTable(), SWT.NONE);

		TableColumn tblclmnDatetime = new TableColumn(tableViewer.getTable(), SWT.NONE);
		String columnHeaderDateTime = translationService.translate("%dateTime");
		tblclmnDatetime.setText(columnHeaderDateTime);

		TableColumn tblclmnTestergebnis = new TableColumn(tableViewer.getTable(), SWT.NONE);
		String columnHeaderTestResults = translationService.translate("%testResults");
		tblclmnTestergebnis.setText(columnHeaderTestResults);

		TableColumn tblclmnLink = new TableColumn(tableViewer.getTable(), SWT.NONE);
		tblclmnLink.setText(translationService.translate("%link"));
		scrolledComposite.setContent(tableViewer.getTable());

		new TableColumn(tableViewer.getTable(), SWT.NONE); // extraColumn
															// without contents
	}

	/**
	 * 
	 * @return the TableViewer
	 */
	public TableViewer getTableViewer() {

		return tableViewer;
	}

	/**
	 * clears the table.
	 */
	public void clearTable() {
		if (!tableViewer.getTable().isDisposed()) {
			tableViewer.getTable().clearAll();
			tableViewer.getTable().setItemCount(0);
			tableViewer.getTable().removeAll();
		}
	}

	/**
	 * 
	 * @param name
	 *            of the teststructure
	 */
	protected void setTitle(String name) {
		if (!nameOfTestHistory.isDisposed()) {
			nameOfTestHistory.setText(translationService.translate("%label.testhistory.of") + ": " + name);
			nameOfTestHistory.pack();
			nameOfTestHistory.setVisible(true);
		}
	}

	/**
	 * sets the minimum size of the scrolled composite. should be called after
	 * filling the table.
	 */
	protected void setVisible() {
		mainComposite.redraw();
		mainComposite.getParent().redraw();
		mainComposite.setVisible(true);
	}

	/**
	 * clear the testHistory view.
	 * 
	 */
	public void clearHistory() {
		clearTable();
		if (!tableViewer.getTable().isDisposed()) {
			tableViewer.getTable().setVisible(false);
		}
		setTitle("");
		if (!nameOfTestHistory.isDisposed()) {
			nameOfTestHistory.setVisible(false);
		}

	}

}
