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

import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;

/**
 * modify TestEditorTextCellEditor for the UiScanner.
 * 
 * @author dkuhlmann
 * 
 */
public class UiScannerTableTextCellEditor extends TextCellEditor {

	private boolean selectAll;

	/**
	 * constructor.
	 * 
	 * @param grid
	 *            Grid
	 */
	public UiScannerTableTextCellEditor(Grid grid) {
		super(grid);
	}

	/**
	 * constructor.
	 * 
	 * @param grid
	 *            Grid
	 * @param style
	 *            : int SWT.style
	 */
	public UiScannerTableTextCellEditor(Grid grid, int style) {
		super(grid, style);
	}

	@Override
	protected void doSetFocus() {
		super.doSetFocus();
		if (!selectAll) {
			Text text = (Text) getControl();
			text.clearSelection();
		}
	}

	@Override
	public void activate(ColumnViewerEditorActivationEvent activationEvent) {
		// Consumes first key event and sets first key value in cell.
		if (activationEvent.eventType == ColumnViewerEditorActivationEvent.MOUSE_DOUBLE_CLICK_SELECTION
				|| activationEvent.eventType == ColumnViewerEditorActivationEvent.MOUSE_CLICK_SELECTION
				|| activationEvent.eventType == ColumnViewerEditorActivationEvent.TRAVERSAL) {
			selectAll = true;
		}
		if (activationEvent.eventType == ColumnViewerEditorActivationEvent.KEY_PRESSED) {
			Text text = (Text) getControl();
			if (activationEvent.keyCode == SWT.CR) {
				selectAll = true;
			} else {
				// activationEvent.character gives the keyCode and the mask
				text.setText("");
				text.append(((char) activationEvent.character) + "");
				selectAll = false;
			}
		}
	}
}
