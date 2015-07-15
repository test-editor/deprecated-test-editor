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

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.services.interfaces.TestProjectService;
import org.testeditor.ui.constants.CustomWidgetIdConstants;

/**
 * Search for a TestStructure.
 *
 */
public class SearchTestStructureDialog extends Dialog {

	private static final Logger LOGGER = Logger.getLogger(SearchTestStructureDialog.class);

	private Text searchText;
	private TableViewer result;
	private Label loadedTestsState;

	@Inject
	private TestProjectService testProjectService;
	protected String searchString = "";

	protected TestStructure selectedTestStructure;

	/**
	 * Constructor to build the Dialog.
	 * 
	 * @param parentShell
	 *            used to create the dialog.
	 */
	public SearchTestStructureDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		getShell().setText("Search for a test structure.");
		Composite cmp = (Composite) super.createDialogArea(parent);
		Composite area = new Composite(cmp, SWT.NORMAL);
		area.setLayout(new GridLayout(1, false));
		Label searchFieldLabel = new Label(area, SWT.NORMAL);
		searchFieldLabel.setText("Search test structure with name: ");
		searchText = new Text(area, SWT.BORDER);
		searchText.addModifyListener(getSearchTextModifiedListener());
		searchText.addKeyListener(getSwitchToResultViewKeyListner());
		searchText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		searchText.setData(CustomWidgetIdConstants.TEST_EDITOR_WIDGET_ID_SWT_BOT_KEY,
				CustomWidgetIdConstants.SEARCH_DIALOG_TESTSTRUCTURE_NAME);
		Label resultLabel = new Label(area, SWT.NORMAL);
		resultLabel.setText("Search results:");
		result = new TableViewer(area);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.minimumHeight = 400;
		gridData.minimumWidth = 500;
		result.getTable().setLayoutData(gridData);
		loadedTestsState = new Label(area, SWT.NORMAL);
		result.setContentProvider(new ArrayContentProvider());
		result.addFilter(getSearchFilter());
		result.addSelectionChangedListener(getResultSelectionListener());
		result.getTable().setData(CustomWidgetIdConstants.TEST_EDITOR_WIDGET_ID_SWT_BOT_KEY,
				CustomWidgetIdConstants.SEARCH_DIALOG_TESTSTRUCTURE_RESULT);
		createTestStructureNamesLoader().start();
		return cmp;
	}

	/**
	 * 
	 * @return selection listener to store the selection of the result view in a
	 *         local variable.
	 */
	private ISelectionChangedListener getResultSelectionListener() {
		return new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				selectedTestStructure = (TestStructure) ((IStructuredSelection) result.getSelection())
						.getFirstElement();
			}
		};
	}

	/**
	 * 
	 * @return Key Listener to select the key down key to switch to result view.
	 */
	private KeyListener getSwitchToResultViewKeyListner() {
		return new KeyListener() {

			@Override
			public void keyReleased(KeyEvent e) {

			}

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.ARROW_DOWN) {
					LOGGER.trace("Key down recognized. Switching to result view.");
					if (result.getTable().getItemCount() > 0) {
						result.getTable().select(0);
						result.getTable().setFocus();
						selectedTestStructure = (TestStructure) ((IStructuredSelection) result.getSelection())
								.getFirstElement();
					}
				}
			}
		};
	}

	/**
	 * 
	 * @return Filter that selects the elements which contains the search name.
	 */
	private ViewerFilter getSearchFilter() {
		return new ViewerFilter() {

			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				String string = element.toString();
				return string.toLowerCase().contains(searchString.toLowerCase());
			}
		};
	}

	/**
	 * 
	 * @return Modified Listener that refreshes the result view.
	 */
	private ModifyListener getSearchTextModifiedListener() {
		return new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				searchString = searchText.getText();
				result.refresh();
			}
		};
	}

	/**
	 * Creates a Thread to load all test structures.
	 * 
	 * @return Thread that loads all TestStructure names to the Reasult view.
	 */
	protected Thread createTestStructureNamesLoader() {
		return new Thread() {
			private List<TestStructure> list;

			@Override
			public void run() {
				updateLoadedTestsState("0%");
				List<TestProject> projects = testProjectService.getProjects();
				list = new ArrayList<TestStructure>();
				DecimalFormat df = new DecimalFormat("##.##");
				df.setRoundingMode(RoundingMode.DOWN);
				for (int i = 0; i < projects.size(); i++) {
					list.addAll(projects.get(i).getAllTestChildrenWithScenarios());
					Display.getDefault().syncExec(new Runnable() {

						@Override
						public void run() {
							if (!result.getTable().isDisposed()) {
								result.setInput(list);
							}
						}
					});
					updateLoadedTestsState(projects.get(i) + ": " + df.format(((float) i + 1) / projects.size() * 100)
							+ "%");
				}
				updateLoadedTestsState("100%");
			};
		};
	}

	/**
	 * Updates the Index Information about loading the full module.
	 * 
	 * @param state
	 *            percentage information about the loading progress.
	 */
	protected void updateLoadedTestsState(final String state) {
		Display.getDefault().syncExec(new Runnable() {

			@Override
			public void run() {
				if (!loadedTestsState.isDisposed()) {
					loadedTestsState.setText(state);
					loadedTestsState.getParent().layout(true, true);
				}
			}
		});

	}

	/**
	 * 
	 * @return the lst selection in the result view.
	 */
	public TestStructure getSelectedTestStructure() {
		return selectedTestStructure;
	}

	/**
	 * 
	 * @return table view containing the result.
	 */
	protected TableViewer getResultViewer() {
		return result;
	}

}
