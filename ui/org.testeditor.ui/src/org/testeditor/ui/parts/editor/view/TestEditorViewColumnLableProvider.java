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

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.testeditor.core.model.teststructure.TestDataRow;

/**
 * simple special ColumnLabelProvider.
 * 
 * @author llipinski
 * 
 */
public class TestEditorViewColumnLableProvider extends ColumnLabelProvider {

	private int colNo;

	/**
	 * constructor.
	 * 
	 * @param colNo
	 *            int
	 */
	public TestEditorViewColumnLableProvider(int colNo) {
		this.colNo = colNo;
	}

	@Override
	public String getText(Object element) {
		return ((TestDataRow) element).getColumn(colNo);
	}
}
