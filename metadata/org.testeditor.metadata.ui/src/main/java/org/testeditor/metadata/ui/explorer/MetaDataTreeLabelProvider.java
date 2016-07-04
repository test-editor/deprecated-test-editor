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
package org.testeditor.metadata.ui.explorer;

import java.text.NumberFormat;

import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.metadata.core.model.MetaData;
import org.testeditor.metadata.core.model.MetaDataValue;
import org.testeditor.ui.constants.IconConstants;

/**
 * 
 * This class provides the labels for the testEditortree.
 * 
 */
public class MetaDataTreeLabelProvider extends CellLabelProvider {

	public Image getImage(Object element) {
		if (element instanceof TestProject) {
			return IconConstants.ICON_PROJECT;
		}
		if (element instanceof MetaData || element instanceof MetaDataValue) {
			return IconConstants.ICON_TESTSUITE;
		}
		return IconConstants.ICON_TESTCASE;
	}

	public String getText(Object element, boolean tooltip) {
		if (element instanceof TestProject) {
			TestProject testProject = (TestProject) element;
			return testProject.getName();
		}
		if (element instanceof MetaData) {
			MetaData metaData = (MetaData) element;
			String label = metaData.getLabel();

			int count = 0;
			for (MetaDataValue metaDataValue : metaData.getValues()) {
				count += metaDataValue.getTestCases().size();
			}
			label += " (" + Integer.toString(count) + ")";
			return label;
		}
		if (element instanceof MetaDataValue) {
			MetaDataValue metaDataValue = (MetaDataValue) element;
			double count = 0;
			for (MetaDataValue temp : metaDataValue.getMetaData().getValues()) {
				count += temp.getTestCases().size();
			}
			double percentage = 0.0;
			if (metaDataValue.getTestCases().size() > 0) {
				percentage = metaDataValue.getTestCases().size() / count;
			}
			return metaDataValue.getLabel() + " ( " + metaDataValue.getTestCases().size() + " / "
					+ NumberFormat.getPercentInstance().format(percentage) + ")";
		}
		if (element instanceof TestStructure) {
			TestStructure testStructure = (TestStructure) element;
			if (tooltip) {
				return testStructure.getFullName();
			} else {
				return testStructure.getName();
			}
		}
		return null;
	}

	@Override
	public void update(ViewerCell cell) {
		cell.setText(getText(cell.getElement(), false));
		cell.setImage(getImage(cell.getElement()));
	}

	public String getToolTipText(Object element) {
		return getText(element, true);
	}
}
