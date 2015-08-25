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

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableColumn;
import org.testeditor.core.model.testresult.TestResult;
import org.testeditor.ui.constants.CustomWidgetIdConstants;
import org.testeditor.ui.utilities.TestEditorTranslationService;

/**
 * Part to show history of tests.
 * 
 */
public class TestHistoryView {

	@Inject
	private TestEditorTranslationService translationService;

	@Inject
	private IEclipseContext context;

	private TableViewer tableViewer;
	private Composite mainComposite;
	private Label nameOfTestHistory;

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
		nameOfTestHistory.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL));
		Composite compositeForTable = new Composite(mainComposite, SWT.NONE);
		compositeForTable.setLayout(new GridLayout(1, false));
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

		tableViewer = new TableViewer(parent, SWT.BORDER | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);
		tableViewer.getTable().setHeaderVisible(true);
		tableViewer.getTable().setLinesVisible(true);
		tableViewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));

		tableViewer.getTable().setData(CustomWidgetIdConstants.TEST_EDITOR_WIDGET_ID_SWT_BOT_KEY,
				CustomWidgetIdConstants.HISTORY_TABLE);

		tableViewer.setLabelProvider(ContextInjectionFactory.make(TestHistoryLabelProvider.class, context));
		tableViewer.setContentProvider(new ArrayContentProvider());

		TableColumn executionResultColumn = new TableColumn(tableViewer.getTable(), SWT.NONE);
		executionResultColumn.setWidth(20);

		TableColumn tblclmnDatetime = new TableColumn(tableViewer.getTable(), SWT.NONE);
		String columnHeaderDateTime = translationService.translate("%dateTime");
		tblclmnDatetime.setText(columnHeaderDateTime);
		tblclmnDatetime.setWidth(300);

		TableColumn tblclmnTestergebnis = new TableColumn(tableViewer.getTable(), SWT.NONE);
		String columnHeaderTestResults = translationService.translate("%testResults");
		tblclmnTestergebnis.setText(columnHeaderTestResults);
		tblclmnTestergebnis.setWidth(400);
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
			nameOfTestHistory.setVisible(true);
			nameOfTestHistory.getParent().layout(true, true);
		}
	}

	/**
	 * sets the minimum size of the scrolled composite. should be called after
	 * filling the table.
	 */
	protected void setVisible() {
		if (!mainComposite.isVisible()) {
			mainComposite.setVisible(true);
		}
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

	public void setTestHistory(List<TestResult> testHistory) {
		tableViewer.setInput(testHistory.toArray());
	}

}
