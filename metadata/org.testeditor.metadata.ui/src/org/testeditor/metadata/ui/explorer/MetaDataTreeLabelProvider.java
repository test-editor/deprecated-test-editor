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

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.util.TestStateProtocolService;
import org.testeditor.metadata.core.model.MetaData;
import org.testeditor.metadata.core.model.MetaDataValue;
import org.testeditor.ui.constants.IconConstants;

/**
 * 
 * This class provides the labels for the testEditortree.
 * 
 */
public class MetaDataTreeLabelProvider extends LabelProvider implements ILabelProvider {

	@Inject
	private TestStateProtocolService testProtocolService;

	@Inject
	private IEclipseContext context;

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
			return ((TestProject) element).getName();
		}
		if (element instanceof MetaData) {
			return ((MetaData) element).getLabel();
		}
		if (element instanceof MetaDataValue) {
			return ((MetaDataValue) element).getLabel();
		}
		if (element instanceof String) {
			return (String) element;
		}
		return null;
	}
}
