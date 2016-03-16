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

import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.metadata.core.model.MetaData;
import org.testeditor.metadata.core.model.MetaDataValue;
import org.testeditor.ui.constants.ColorConstants;
import org.testeditor.ui.constants.IconConstants;

/**
 * 
 * This class provides the labels for the testEditortree.
 * 
 */
public class MetaDataTreeLabelProvider extends LabelProvider implements ILabelProvider, IColorProvider {

	@Override
	public Image getImage(Object element) {
		if (element instanceof TestProject) {
			return IconConstants.ICON_PROJECT;
		}
		if (element instanceof MetaData || element instanceof MetaDataValue) {
			return IconConstants.ICON_TESTSUITE;
		}
		return IconConstants.ICON_TESTCASE;
	}

	@Override
	public String getText(Object element) {
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
			return testStructure.getName();
		}
		return null;
	}

	@Override
	public Color getForeground(Object element) {
		if (element instanceof MetaDataValue) {
			MetaDataValue metaDataValue = (MetaDataValue) element;
			if (metaDataValue.getTestCases().size() == 0) {
				return ColorConstants.COLOR_RED;
			}
		}
		return null;
	}

	@Override
	public Color getBackground(Object element) {
		return null;
	}
}
