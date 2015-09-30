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
package org.testeditor.ui.uiscanner.ui.table;

import java.util.ArrayList;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.widgets.Composite;
import org.testeditor.ui.uiscanner.webscanner.UiScannerWebElement;
import org.testeditor.ui.uiscanner.webscanner.WebScanner;

/**
 * Class for the table.
 * 
 * @author dkuhlmann
 * 
 */
public class UiScannerGridTableViewerCreator {

	@Inject
	private IEclipseContext context;

	/**
	 * Create the table wich will be filled with the WebElements.
	 * 
	 * @param parent
	 *            Composite: where the table should be added.
	 * @param style
	 *            int: Style for the Composites.
	 * 
	 * @param input
	 *            ArrayList<UiScannerWebElement>: List with all
	 *            UiScannerWebElements.
	 * @param webScanner
	 *            WebScanner for the highlighting.
	 * 
	 * @return GridTableViewer
	 * 
	 */
	public GridTableViewer createTable(Composite parent, int style, ArrayList<UiScannerWebElement> input,
			WebScanner webScanner) {

		final GridTableViewer tableViewer = new GridTableViewer(parent, style | SWT.V_SCROLL | SWT.H_SCROLL);
		tableViewer.getGrid().setHeaderVisible(true);
		tableViewer.getGrid().setCellSelectionEnabled(true);
		tableViewer.getGrid().setRowHeaderVisible(true);

		TextCellEditor textEditor = new UiScannerTableTextCellEditor(tableViewer.getGrid(), style);

		ColumnViewerEditorActivationStrategy strat = new UiScannerTableViewerEditorActivationSrategy(tableViewer);

		UiScannerTableColumnCreator columnCreator = ContextInjectionFactory.make(UiScannerTableColumnCreator.class,
				context);
		columnCreator.createCoulmns(tableViewer, textEditor, style);

		tableViewer.setContentProvider(ArrayContentProvider.getInstance());
		tableViewer.setInput(input);

		GridViewerEditor.create(tableViewer, strat, ColumnViewerEditor.TABBING_HORIZONTAL
				| ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR | ColumnViewerEditor.KEYBOARD_ACTIVATION
				| GridViewerEditor.SELECTION_FOLLOWS_EDITOR);

		// noch probleme beim wechseln.
		// tableViewer.getGrid().addMouseMoveListener(new
		// UiScannerTableMouseMove(webScanner, tableViewer));
		tableViewer.addSelectionChangedListener(new UiScannerTableSelectionChangedListener(webScanner));
		tableViewer.getGrid().addTraverseListener(new TraverseListener() {

			@Override
			public void keyTraversed(TraverseEvent e) {
				e.doit = false;

			}
		});
		return tableViewer;

	}
}
