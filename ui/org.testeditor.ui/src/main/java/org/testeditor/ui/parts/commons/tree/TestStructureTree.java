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
package org.testeditor.ui.parts.commons.tree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.services.interfaces.TestStructureTreeModel;
import org.testeditor.ui.parts.commons.TestStructureViewerComparator;
import org.testeditor.ui.parts.commons.tree.filter.ErrorInProjectConfigFilter;
import org.testeditor.ui.parts.commons.tree.filter.ReservedNameFilter;
import org.testeditor.ui.parts.commons.tree.filter.TestCaseFilter;
import org.testeditor.ui.parts.commons.tree.filter.TestScenarioFilter;
import org.testeditor.ui.parts.commons.tree.filter.TestSuiteWithoutTestScenarioSuiteFilter;

/**
 * UI Component to display a Teststructure as a Tree. It can display TestSuites
 * and TestCases.
 * 
 */
public class TestStructureTree {

	private ReservedNameFilter reservedNameFilter;

	private TreeViewer treeViewer;

	@Inject
	private IEclipseContext context;

	/**
	 * Creates a Tree to Display TestStrucutres in the parent Composite.
	 * 
	 * @param parent
	 *            the Parent of the Treeviewer
	 * @param treeInputService
	 *            the Service is passed to the Content Provider to retrieve the
	 *            Root-Teststructures
	 * @param style
	 *            as SWT-Style
	 */
	public void createUI(Composite parent, TestStructureTreeModel treeInputService, int style) {
		treeViewer = new TreeViewer(parent, style | SWT.VIRTUAL);
		initializeInternalsToTreeViewer(treeInputService);
	}

	/**
	 * initializes some internal object to the TreeViewer.
	 * 
	 * @param treeInputService
	 *            TestStructureTreeInputService
	 */
	private void initializeInternalsToTreeViewer(TestStructureTreeModel treeInputService) {
		treeViewer.setLabelProvider(ContextInjectionFactory.make(TestStructureTreeLabelProvider.class, context));
		treeViewer.setContentProvider(ContextInjectionFactory.make(TestStructureTreeContentProvider.class, context));
		treeViewer.setComparator(new TestStructureViewerComparator());
		treeViewer.setInput(treeInputService);
		showParentTestStructesAndChildren();
	}

	/**
	 * Creates a Tree to Display TestStrucutres in the parent Composite.
	 * 
	 * @param parent
	 *            the Parent of the Treeviewer
	 * @param treeInputService
	 *            the Service is passed to the Content Provider to retrieve the
	 *            Root-Teststructures
	 */
	public void createUI(Composite parent, TestStructureTreeModel treeInputService) {
		treeViewer = new TreeViewer(parent, SWT.MULTI | SWT.VIRTUAL);
		initializeInternalsToTreeViewer(treeInputService);
	}

	/**
	 * Adds a Filter to the Treeviewer so show only TestSenarioSuites, which can
	 * contain TestScenario.
	 */
	public void showOnlyTestScenarioSuites() {
		showParentTestStructesAndChildren();
		treeViewer.addFilter(new TestCaseFilter());
		treeViewer.addFilter(new TestSuiteWithoutTestScenarioSuiteFilter());
		treeViewer.addFilter(new ErrorInProjectConfigFilter());
	}

	/**
	 * Adds a Filter to the Treeviewer so show only Teststructures, which can
	 * contain Testcases.
	 */
	public void showOnlyParentStructures() {
		showParentTestStructesAndChildren();
		treeViewer.addFilter(new TestCaseFilter());
		treeViewer.addFilter(ContextInjectionFactory.make(TestScenarioFilter.class, context));
		treeViewer.addFilter(new ErrorInProjectConfigFilter());
	}

	/**
	 * Adds a Filter to the Treeviewer so show only Teststructures, which can
	 * contain Testcases.
	 */
	public void showOnlyParentStructuresOfSuites() {
		showParentTestStructesAndChildren();
		treeViewer.addFilter(new TestCaseFilter());
		treeViewer.addFilter(ContextInjectionFactory.make(TestScenarioFilter.class, context));
		treeViewer.addFilter(new ErrorInProjectConfigFilter());
	}

	/**
	 * Removes TestCase Filter from the Viewer.
	 */
	public void showParentTestStructesAndChildren() {
		for (ViewerFilter filter : treeViewer.getFilters()) {
			treeViewer.removeFilter(filter);
		}

		if (reservedNameFilter == null) {
			reservedNameFilter = ContextInjectionFactory.make(ReservedNameFilter.class, context);
		}

		treeViewer.addFilter(reservedNameFilter);
	}

	/**
	 * 
	 * @return the TreeViewer
	 */
	public TreeViewer getTreeViewer() {
		return treeViewer;
	}

	/**
	 * Select a Teststructure Element in the Tree.
	 * 
	 * @param selectedTS
	 *            to be selected in the Treeviewer
	 */
	public void selectTestStructure(TestStructure selectedTS) {
		treeViewer.setSelection(getSelectedTreeItem(selectedTS));
	}

	/**
	 * Creates a selection Object to be set in the Tree to select the specified
	 * object.
	 * 
	 * @param selectedTS
	 *            to be used in the new Selection
	 * @return IStructuredSelection containing only selectedTS.
	 */
	private IStructuredSelection getSelectedTreeItem(final TestStructure selectedTS) {
		return new IStructuredSelection() {

			@Override
			public boolean isEmpty() {
				return false;
			}

			@Override
			public List<TestStructure> toList() {
				ArrayList<TestStructure> result = new ArrayList<TestStructure>();
				result.add(selectedTS);
				return result;
			}

			@Override
			public Object[] toArray() {
				return toList().toArray();
			}

			@Override
			public int size() {
				return 1;
			}

			@Override
			public Iterator<TestStructure> iterator() {
				return toList().iterator();
			}

			@Override
			public Object getFirstElement() {
				return selectedTS;
			}
		};
	}

	/**
	 * 
	 * @return the in the Treeviewer selected Teststructure.
	 */
	public TestStructure getSelectedTestStrucuture() {
		IStructuredSelection sel = (IStructuredSelection) treeViewer.getSelection();
		return (TestStructure) sel.getFirstElement();
	}

	/**
	 * 
	 * @return the Selection of the TreeViewer.
	 */
	public IStructuredSelection getSelection() {
		return (IStructuredSelection) treeViewer.getSelection();
	}

}
