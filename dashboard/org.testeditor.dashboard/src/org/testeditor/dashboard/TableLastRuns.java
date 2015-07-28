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
package org.testeditor.dashboard;

import java.io.IOException;
import java.text.ParseException;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.jdom2.JDOMException;
import org.testeditor.core.exceptions.SystemException;

/**
 * @author alebedev
 * 
 *         table shows only last "stand alone" runs from each suite and test
 *         case. if test case run was in suite run,it will not be displayed.
 */
public class TableLastRuns {

	@Inject
	private TranslationService translationService;

	@Inject
	private IEventBroker eventBroker;

	/**
	 * The contributor URI.
	 */
	public static final String CONTRIBUTOR_URI = "platform:/plugin/org.testeditor.dashboard";
	private MyViewerComparator comparator;
	private TreeViewer v;

	/**
	 * 
	 */
	@Inject
	public TableLastRuns() {
	}

	/**
	 * designs a LastRunTable.
	 * 
	 * @param parent
	 *            composite parent
	 * @param modelService
	 *            to find part label
	 * @param window
	 *            trimmed window
	 * @param app
	 *            org.eclipse.e4.ide.application
	 * @throws JDOMException
	 *             f one of the arguments is invalid
	 * @throws IOException
	 *             Signals that an I/O exception of some sort has occurred
	 * @throws ParseException
	 *             Signals that an error has been reached unexpectedly while
	 *             parsing.
	 * @throws SystemException
	 *             If the transaction service fails in an unexpected way
	 */
	@PostConstruct
	public void createControls(Composite parent, EModelService modelService, MWindow window, MApplication app)
			throws JDOMException, IOException, ParseException, SystemException {

		parent.setLayout(new FillLayout());
		final Tree tree = new Tree(parent, SWT.BORDER | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);
		tree.setHeaderVisible(true);
		tree.setLinesVisible(true);
		v = new TreeViewer(tree);
		comparator = new MyViewerComparator();
		v.setComparator(comparator);
		// using internal hash table speeds the lookup
		v.setUseHashlookup(true);

		String[] titles = {
				translationService.translate("%dashboard.table.label.lastrun.column.date", CONTRIBUTOR_URI),
				translationService.translate("%dashboard.table.label.lastrun.column.name", CONTRIBUTOR_URI),
				translationService.translate("%dashboard.table.label.lastrun.column.result", CONTRIBUTOR_URI),
				translationService.translate("%dashboard.table.label.lastrun.column.steps.ok", CONTRIBUTOR_URI),
				translationService.translate("%dashboard.table.label.lastrun.column.steps.failed", CONTRIBUTOR_URI),
				translationService.translate("%dashboard.table.label.lastrun.column.duration", CONTRIBUTOR_URI)
						+ " (hh:mm:ss:ms)",
				translationService.translate("%dashboard.table.label.lastrun.column.runs", CONTRIBUTOR_URI) };
		for (int i = 0; i < titles.length; i++) {
			TreeColumn column = new TreeColumn(tree, SWT.CENTER);
			column.setText(titles[i]);
			column.setAlignment(SWT.LEFT);
			if (i == 0) {
				column.setWidth(200);
				column.addSelectionListener(getSelectionAdapter(column, 0)); // new
				// //
				// SortTreeListener(v));
				column.setToolTipText(translationService.translate(
						"%dashboard.table.label.lastrun.column.tooltip.date", CONTRIBUTOR_URI));
			}
			if (i == 1) {
				column.addSelectionListener(getSelectionAdapter(column, 1));
				column.setWidth(450);
				column.setToolTipText(translationService.translate(
						"%dashboard.table.label.lastrun.column.tooltip.name", CONTRIBUTOR_URI));
			}
			if (i == 2) {
				column.setWidth(70);
				column.addSelectionListener(getSelectionAdapter(column, 2));
				column.setToolTipText(translationService.translate(
						"%dashboard.table.label.lastrun.column.tooltip.result", CONTRIBUTOR_URI));
			}
			if (i == 3) {
				column.setWidth(180);
				column.addSelectionListener(getSelectionAdapter(column, 3));
				column.setToolTipText(translationService.translate(
						"%dashboard.table.label.lastrun.column.subsettooltip", CONTRIBUTOR_URI));
			}
			if (i == 4) {
				column.setWidth(180);
				column.addSelectionListener(getSelectionAdapter(column, 4));
				column.setToolTipText(translationService.translate(
						"%dashboard.table.label.lastrun.column.subsettooltip", CONTRIBUTOR_URI));
			}
			if (i == 5) {
				column.setWidth(180);
				column.setAlignment(SWT.RIGHT);
				column.addSelectionListener(getSelectionAdapter(column, 5));
				column.setToolTipText(translationService.translate(
						"%dashboard.table.label.lastrun.column.tooltip.duration", CONTRIBUTOR_URI));
			}
			if (i == 6) {
				column.setWidth(120);
				column.addSelectionListener(getSelectionAdapter(column, 6));
				column.setToolTipText(translationService.translate(
						"%dashboard.table.label.lastrun.column.tooltip.runs", CONTRIBUTOR_URI));
			}
		}
		v.setLabelProvider(new MyLabelProvider());
		v.setContentProvider(new MyContentProvider());
		// mouse events******************************************************
		tree.addMouseListener(new MouseListener() {
			@Override
			public void mouseUp(MouseEvent e) {
			}

			@Override
			public void mouseDown(MouseEvent e) {
			}

			@Override
			public void mouseDoubleClick(MouseEvent e) {
				String fileName = ((Tree) e.getSource()).getSelection()[0].getText(1);
				// eventBroker.send("newTestResult", (TestResult) ((Tree)
				// e.getSource()).getSelection()[0].getData());
				eventBroker.send("FileName", fileName);
				// eventBroker.send("DisposeErrorTable", fileName);

			}
		});
	}

	/**
	 * designs a LastRunTable.
	 * 
	 * @param column
	 *            table column
	 * @param index
	 *            column index
	 * @return selectionAdapter
	 */
	private SelectionAdapter getSelectionAdapter(final TreeColumn column, final int index) {
		SelectionAdapter selectionAdapter = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				comparator.setColumn(index);
				int dir = comparator.getDirection();
				v.getTree().setSortDirection(dir);
				v.getTree().setSortColumn(column);
				v.refresh();
			}
		};
		return selectionAdapter;
	}

	/**
	 * on perspective or project(drop down menu) change it retrieves last run.
	 * results data and sends events to dispose other tables
	 * 
	 * @param context
	 *            IEclipseContext context
	 * @param projectName
	 *            name of project DemoWebTests or first project when switching
	 *            perspective
	 * @param modelService
	 *            to find part label
	 * @param window
	 *            trimmed window
	 * @param app
	 *            org.eclipse.e4.ide.application
	 * @throws JDOMException
	 *             f one of the arguments is invalid
	 * @throws IOException
	 *             Signals that an I/O exception of some sort has occurred
	 * @throws ParseException
	 *             Signals that an error has been reached unexpectedly while
	 *             parsing.
	 * @throws SystemException
	 *             If the transaction service fails in an unexpected way
	 */
	public void refresh(String projectName, EModelService modelService, MWindow window, MApplication app,
			IEclipseContext context) throws JDOMException, IOException, ParseException, SystemException {
		String string = "x";
		GetDataTableLastRuns x = ContextInjectionFactory.make(GetDataTableLastRuns.class, context);
		v.setInput(x.getData(projectName, modelService, window, context, app));
		eventBroker.send("DisposeAllRunsResultTable", string);
		eventBroker.send("DisposeErrorTable1", string);
		eventBroker.send("DisposeChartTable", string);
	}
}