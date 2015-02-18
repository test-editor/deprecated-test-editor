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
/**
 * 
 */
package org.testeditor.ui.parts.commons.tree;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.model.teststructure.TestCompositeStructure;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.services.interfaces.TestProjectService;
import org.testeditor.core.services.interfaces.TestStructureTreeInputService;

/**
 * 
 * Content Provider to navigate through the TestProjects and their
 * TestStructures.
 * 
 */
public class TestStructureTreeContentProvider implements ITreeContentProvider {

	private static final Logger LOGGER = Logger.getLogger(TestStructureTreeContentProvider.class);

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	@Override
	public Object[] getElements(Object inputElement) {
		TestStructureTreeInputService treeInputService = (TestStructureTreeInputService) inputElement;
		List<? extends TestStructure> elements = new ArrayList<TestStructure>();
		try {
			elements = treeInputService.getElements();
		} catch (SystemException e) {
			LOGGER.error("Error Reading Projects", e);
			MessageDialog.openError(Display.getCurrent().getActiveShell(), "System-Exception", e.getLocalizedMessage());
		}
		return elements.toArray();
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof TestCompositeStructure) {
			List<TestStructure> listTS = ((TestCompositeStructure) parentElement).getTestChildren();
			return listTS.toArray();
		}
		return null;
	}

	@Override
	public Object getParent(Object element) {
		if (element instanceof TestStructure) {
			return ((TestStructure) element).getParent();
		}
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {

		if (element instanceof TestProject
				&& ((TestProject) element).getTestProjectConfig() != null
				&& !((TestProject) element).getTestProjectConfig().getProjectConfigVersion()
						.equals(TestProjectService.UNSUPPORTED_CONFIG_VERSION)) {
			return true;
		}

		if (element instanceof TestCompositeStructure) {
			TestCompositeStructure testComp = (TestCompositeStructure) element;
			return testComp.hasChildren();
		}

		return false;
	}

	@Override
	public void dispose() {
	}

}