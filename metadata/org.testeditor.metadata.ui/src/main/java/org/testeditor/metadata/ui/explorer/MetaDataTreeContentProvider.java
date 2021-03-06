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
package org.testeditor.metadata.ui.explorer;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.services.interfaces.TestProjectService;
import org.testeditor.metadata.core.MetaDataService;
import org.testeditor.metadata.core.model.MetaData;
import org.testeditor.metadata.core.model.MetaDataValue;

import org.apache.log4j.Logger;

/**
 * 
 * Content Provider to navigate through the TestProjects and their
 * TestStructures.
 * 
 */
public class MetaDataTreeContentProvider implements ITreeContentProvider {

	private static final Logger logger = Logger.getLogger(MetaDataTreeContentProvider.class);

	private MetaDataService metaDataService;
	@Inject
	private TestProjectService testProjectService;

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	@Override
	public Object[] getElements(Object inputElement) {
		metaDataService = (MetaDataService) inputElement;
		List<TestProject> elements = new ArrayList<TestProject>();
		for (TestProject project : testProjectService.getProjects()) {
			elements.add(project);
		}
		return elements.toArray();
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof TestProject) {
			return metaDataService.getAllMetaData((TestProject) parentElement).toArray();
		}
		if (parentElement instanceof MetaData) {
			return ((MetaData) parentElement).getValues().toArray();
		}
		if (parentElement instanceof MetaDataValue) {
			MetaDataValue metaDataValue = (MetaDataValue) parentElement;
			List<String> testCaseNames = metaDataService.getTestCases(metaDataValue.getMetaData().getTestProject(),
					metaDataValue);

			List<TestStructure> testCases = new ArrayList<TestStructure>();
			for (String testCaseName : testCaseNames) {
				try {
					TestStructure testCase = testProjectService.findTestStructureByFullName(testCaseName);
					if (testCase != null) {
						testCases.add(testProjectService.findTestStructureByFullName(testCaseName));
					} else {
						logger.info("no testcase found for '" + testCaseName + "'");
					}
				} catch (SystemException e) {
					e.printStackTrace();
				}
			}

			return testCases.toArray();

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

		if (element instanceof TestProject) {
			return metaDataService.getAllMetaData((TestProject) element).size() > 0;
		}
		if (element instanceof MetaData) {
			return ((MetaData) element).getValues().size() > 0;
		}
		if (element instanceof MetaDataValue) {
			MetaDataValue metaDataValue = (MetaDataValue) element;
			List<String> testCases = metaDataService.getTestCases(metaDataValue.getMetaData().getTestProject(),
					metaDataValue);
			return testCases.size() > 0;
		}
		return false;
	}

	@Override
	public void dispose() {
	}

}