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

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Table;

/**
 * selectionAdapter for the parameterTable.
 * 
 * @author llipinski
 * 
 */
public class TestEditorTableColumnSelectionAdapter extends SelectionAdapter {

	private Table table;

	/**
	 * constructor.
	 * 
	 * @param table
	 *            the table
	 */
	public TestEditorTableColumnSelectionAdapter(Table table) {
		this.table = table;
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		table.redraw();
	}

}
