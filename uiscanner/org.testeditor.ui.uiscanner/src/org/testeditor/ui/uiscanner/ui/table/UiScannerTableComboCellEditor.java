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

import org.eclipse.jface.util.Assert;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.swt.custom.CCombo;
import org.testeditor.ui.uiscanner.webscanner.UiScannerConstants;

/**
 * 
 * @author dkuhlmann
 * 
 */
@SuppressWarnings("deprecation")
public class UiScannerTableComboCellEditor extends ComboBoxCellEditor {

	/**
	 * The zero-based index of the selected item.
	 */
	private int selection;

	/**
	 * The custom combo box control.
	 */
	private CCombo comboBox;

	/**
	 * The <code>ComboBoxCellEditor</code> implementation of this
	 * <code>CellEditor</code> framework method accepts a zero-based index of a
	 * selection.
	 * 
	 * @param value
	 *            the zero-based index of the selection wrapped as an
	 *            <code>Integer</code>
	 */
	@Override
	protected void doSetValue(Object value) {
		Assert.isTrue(comboBox != null);
		selection = UiScannerConstants.ALL_TYPES.indexOf(value);
		comboBox.select(selection);
	}
}
