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
package org.testeditor.ui.parts.testsuite;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.testeditor.core.model.teststructure.TestCase;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.services.interfaces.TestProjectService;
import org.testeditor.ui.constants.CustomWidgetIdConstants;
import org.testeditor.ui.parts.commons.tree.TestStructureTree;
import org.testeditor.ui.utilities.TestEditorTranslationService;

/**
 * 
 * Dialog to select a TestSructure from the TestSctrucutre Tree.
 * 
 */
public class TestStructureSelectionDialog extends Dialog {

	@Inject
	private TestProjectService testProjectService;
	@Inject
	private TestEditorTranslationService translationService;
	@Inject
	private IEclipseContext context;
	private TestStructureTree testStructureTree;
	private IStructuredSelection selectedTestStrucutures;
	private List<ViewerFilter> filters;

	/**
	 * Constructs the Dialog.
	 * 
	 * @param shell
	 *            as parent of the dialog.
	 */
	@Inject
	public TestStructureSelectionDialog(Shell shell) {
		super(shell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		setShellStyle(getShellStyle() | SWT.SHELL_TRIM);
		filters = new ArrayList<ViewerFilter>();
	}

	@Override
	protected Control createContents(Composite parent) {
		if (getShell() != null) {
			getShell().setText(translationService.translate("%teststructureselectiondloag.title"));
		}
		testStructureTree = createTestStructureTree();
		testStructureTree.createUI(parent, testProjectService);
		testStructureTree.getTreeViewer().getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		testStructureTree.getTreeViewer().getTree().addSelectionListener(getTreeSelectionListener());
		for (ViewerFilter filter : filters) {
			testStructureTree.getTreeViewer().addFilter(filter);
		}
		createButtonBar(parent);
		getButton(IDialogConstants.OK_ID).setData(CustomWidgetIdConstants.TEST_EDITOR_WIDGET_ID_SWT_BOT_KEY,
				CustomWidgetIdConstants.SELECTION_DIALOG_TESTCASE_OK);
		getButton(IDialogConstants.CANCEL_ID).setData(CustomWidgetIdConstants.TEST_EDITOR_WIDGET_ID_SWT_BOT_KEY,
				CustomWidgetIdConstants.SELECTION_DIALOG_TESTCASE_CANCEL);
		if (selectedTestStrucutures != null) {
			testStructureTree.selectTestStructure((TestStructure) selectedTestStrucutures.getFirstElement());
		}
		return testStructureTree.getTreeViewer().getControl();
	}

	/**
	 * 
	 * @return SelectionListener for the TestStructureTree to update the ok
	 *         button of this dialog.
	 */
	protected SelectionListener getTreeSelectionListener() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button okButton = getButton(IDialogConstants.OK_ID);
				if (testStructureTree.getSelectedTestStrucuture() instanceof TestCase) {
					okButton.setEnabled(true);
				} else {
					okButton.setEnabled(false);
				}
			}
		};
	}

	/**
	 * Creates a TestStructureTree object to be used in this dialog.
	 * 
	 * @return a new instance of <code>TestStructureTree</code>
	 */
	protected TestStructureTree createTestStructureTree() {
		return ContextInjectionFactory.make(TestStructureTree.class, context);
	}

	/**
	 * 
	 * @return the <code>TestStructureTree</code> of this dialog.
	 */
	protected TestStructureTree getTestStructureTree() {
		return testStructureTree;
	}

	/**
	 * 
	 * @return selected TestCase in the TestStructure Tree.
	 */
	public IStructuredSelection getSelection() {
		return selectedTestStrucutures;
	}

	@Override
	protected void okPressed() {
		selectedTestStrucutures = testStructureTree.getSelection();
		super.okPressed();
	}

	@Override
	protected Point getInitialSize() {
		return new Point(450, 300);
	}

	/**
	 * Checks if the selection in the tree is a element, to be selected from
	 * this dialog.
	 * 
	 * @return the enabled state of the ok button.
	 */
	protected boolean isOk() {
		return getButton(IDialogConstants.OK_ID).isEnabled();
	}

	/**
	 * 
	 * @param viewerFilter
	 *            used in the Dialog after creation.
	 */
	public void addFilter(ViewerFilter viewerFilter) {
		filters.add(viewerFilter);
	}

}
