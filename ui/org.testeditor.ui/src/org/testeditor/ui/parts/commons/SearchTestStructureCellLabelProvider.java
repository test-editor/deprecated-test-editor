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
package org.testeditor.ui.parts.commons;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.testeditor.core.model.teststructure.TestStructure;

/**
 * CellLabel provider to show full name of {@link TestStructure} in separate
 * cell with different style.
 *
 */
public class SearchTestStructureCellLabelProvider extends CellLabelProvider implements IFontProvider {

	@Override
	public Font getFont(Object element) {
		return JFaceResources.getFont(JFaceResources.BANNER_FONT);
	}

	@Override
	public void update(ViewerCell cell) {
		Object element = cell.getElement();

		if (cell.getColumnIndex() == 0) {
			if (element != null) {
				cell.setText(element.toString());
			}
		} else {
			if (element != null) {
				cell.setText(((TestStructure) element).getFullName());
				cell.setForeground(cell.getControl().getDisplay().getSystemColor(SWT.COLOR_GRAY));
			}
		}

	}
}
