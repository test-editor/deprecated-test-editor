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
package org.testeditor.ui.parts.commons.search;

import java.math.RoundingMode;
import java.text.Collator;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.OpenEvent;
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
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.testeditor.core.model.teststructure.BrokenTestStructure;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.services.interfaces.TestProjectService;
import org.testeditor.ui.constants.CustomWidgetIdConstants;
import org.testeditor.ui.handlers.OpenTestStructureHandler;
import org.testeditor.ui.utilities.TestEditorTranslationService;

/**
 * Search for a TestStructure.
 *
 */
public class SearchTestStructureDialog extends Dialog {

	private static final Logger LOGGER = Logger.getLogger(SearchTestStructureDialog.class);

	private Text searchText;
	private TableViewer searchResults;
	private Label loadedTestsState;

	@Inject
	private TestProjectService testProjectService;
	protected String searchString = "";

	protected TestStructure selectedTestStructure;

	@Inject
	private IEclipseContext context;

	@Inject
	private TestEditorTranslationService translate;

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
		getShell().setText(translate.translate("%testexplorer.dialog.search.title"));
		Composite cmp = (Composite) super.createDialogArea(parent);
		Composite area = new Composite(cmp, SWT.NORMAL);
		area.setLayout(new GridLayout(1, false));
		Label searchFieldLabel = new Label(area, SWT.NORMAL);
		searchFieldLabel.setText(translate.translate("%testexplorer.dialog.search.label"));
		searchText = new Text(area, SWT.BORDER);
		searchText.addModifyListener(getSearchTextModifiedListener());
		searchText.addKeyListener(getSwitchToResultViewKeyListner());
		searchText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		searchText.setData(CustomWidgetIdConstants.TEST_EDITOR_WIDGET_ID_SWT_BOT_KEY,
				CustomWidgetIdConstants.SEARCH_DIALOG_TESTSTRUCTURE_NAME);
		Label resultLabel = new Label(area, SWT.NORMAL);
		resultLabel.setText(translate.translate("%testexplorer.dialog.search.results"));

		searchResults = new TableViewer(area);

		searchResults.getTable().setHeaderVisible(true);

		TableColumn column = new TableColumn(searchResults.getTable(), SWT.NONE);
		column.setWidth(200);
		column.setText(translate.translate("%testexplorer.dialog.search.column.name"));

		makeColumnSortable(column);

		column = new TableColumn(searchResults.getTable(), SWT.NONE);
		column.setWidth(600);
		column.setText(translate.translate("%testexplorer.dialog.search.column.path"));

		searchResults.addOpenListener(new IOpenListener() {

			@Override
			public void open(OpenEvent event) {
				Object element = ((IStructuredSelection) event.getSelection()).getFirstElement();

				try {
					TestStructure selected = (TestStructure) element;
					OpenTestStructureHandler handler = ContextInjectionFactory.make(OpenTestStructureHandler.class,
							context);
					handler.execute(selected, context);

					close();
				} catch (ClassCastException e) {

					if (element instanceof BrokenTestStructure) {
						MessageDialog.openInformation(Display.getCurrent().getActiveShell(), "Info",
								MessageFormat.format(
										translate.translate("%testsuiteeditor.referedtestcases.not.available"),
										((BrokenTestStructure) element).getName()));

					}
				}
			}
		});

		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.minimumHeight = 400;
		gridData.minimumWidth = 500;
		searchResults.getTable().setLayoutData(gridData);
		loadedTestsState = new Label(area, SWT.NORMAL);

		searchResults.setContentProvider(new ArrayContentProvider());
		searchResults.setLabelProvider(new SearchTestStructureCellLabelProvider());

		searchResults.addFilter(getSearchFilter());
		searchResults.addSelectionChangedListener(getResultSelectionListener());
		searchResults.getTable().setData(CustomWidgetIdConstants.TEST_EDITOR_WIDGET_ID_SWT_BOT_KEY,
				CustomWidgetIdConstants.SEARCH_DIALOG_TESTSTRUCTURE_RESULT);
		createTestStructureNamesLoader().start();
		return cmp;
	}

	/**
	 * Makes given column sortable.
	 * 
	 * @param column
	 *            to be sortable
	 */
	private void makeColumnSortable(TableColumn column) {
		new TableSortSelectionListener(searchResults, column, new AbstractInvertableTableSorter() {
			@Override
			public int compare(Viewer viewer, Object e1, Object e2) {
				return Collator.getInstance().compare(((TestStructure) e1).getName(), ((TestStructure) e2).getName());
			}

		}, SWT.UP, true).chooseColumnForSorting();
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
				selectedTestStructure = (TestStructure) ((IStructuredSelection) searchResults.getSelection())
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
					if (searchResults.getTable().getItemCount() > 0) {
						searchResults.getTable().select(0);
						searchResults.getTable().setFocus();
						selectedTestStructure = (TestStructure) ((IStructuredSelection) searchResults.getSelection())
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
				searchResults.refresh();
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
							if (!searchResults.getTable().isDisposed()) {
								searchResults.setInput(list);
							}
						}
					});
					updateLoadedTestsState(
							projects.get(i) + ": " + df.format(((float) i + 1) / projects.size() * 100) + "%");
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
		return searchResults;
	}

}
