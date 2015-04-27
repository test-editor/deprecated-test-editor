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

import java.text.MessageFormat;
import java.util.Iterator;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.testeditor.core.constants.TestEditorCoreEventConstants;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.exceptions.TestCycleDetectException;
import org.testeditor.core.model.teststructure.BrokenTestStructure;
import org.testeditor.core.model.teststructure.TestCase;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.model.teststructure.TestSuite;
import org.testeditor.core.services.interfaces.TestProjectService;
import org.testeditor.core.services.interfaces.TestStructureContentService;
import org.testeditor.ui.ITestStructureEditor;
import org.testeditor.ui.constants.CustomWidgetIdConstants;
import org.testeditor.ui.constants.IconConstants;
import org.testeditor.ui.constants.TestEditorUIEventConstants;
import org.testeditor.ui.handlers.OpenTestStructureHandler;
import org.testeditor.ui.parts.commons.tree.TestStructureTreeLabelProvider;
import org.testeditor.ui.parts.commons.tree.filter.TestScenarioFilter;
import org.testeditor.ui.parts.inputparts.TestEditorInputPartMouseAdapter;
import org.testeditor.ui.utilities.TestEditorTranslationService;

/**
 * 
 * Part edit TestSuites.
 * 
 * TestSuites collect TestCases. There are several ways to do that:
 * <ul>
 * <li>Child Structures</li>
 * <li>Manual added References to other TestCases</li>
 * <li>Query's to find TestCases</li>
 * <li>Tag based Filters</li>
 * </ul>
 * More informations and examples are available on {@link http
 * ://www.fitnesse.org/FitNesse.UserGuide.TestSuites}
 * 
 */
public class TestSuiteEditor implements ITestStructureEditor {

	public static final String ID = "org.testeditor.ui.partdescriptor.testSuiteEditor";
	private static final Logger LOGGER = Logger.getLogger(TestSuiteEditor.class);
	protected static final String EDITOR_OBJECT_ID_FOR_RESTORE = "testsuite_for_restore_id";

	private MPart mpart;
	private TestSuite testSuite;

	@Inject
	private TestEditorTranslationService translate;

	@Inject
	private TestStructureContentService testStructureContentService;

	@Inject
	private IEclipseContext context;
	@Inject
	private EPartService partService;

	private TableViewer referredTestCasesViewer;

	private Button removeButton;
	private IEventBroker eventBroker;
	private Label tableLabel;
	private TestStructureSelectionDialog selectionDialog;

	/**
	 * 
	 * @param part
	 *            model of the UI component.
	 */
	@Inject
	public TestSuiteEditor(MPart part) {
		mpart = part;
	}

	/**
	 * will be called by the eventBroker, if the TEST_STRUCTURE_DELETED event is
	 * called.
	 * 
	 * @param testStructureFullName
	 *            the deleted TestStructure.
	 */
	@Inject
	@Optional
	public void closeChildOfDeletedTestStructure(
			@UIEventTopic(TestEditorCoreEventConstants.TESTSTRUCTURE_MODEL_CHANGED_DELETED) String testStructureFullName) {
		if (testStructureFullName != null && getTestStructure().getFullName().startsWith(testStructureFullName)) {
			closePart();
		}
	}

	/**
	 * Refresh testProject in view.
	 * 
	 * @param data
	 *            String
	 */
	@Inject
	@Optional
	public void reloadAndRefreshByRevert(@UIEventTopic(TestEditorUIEventConstants.TESTSTRUCTURE_REVERTED) String data) {
		if (data.equals(getTestStructure().getFullName())) {
			loadAndRerender();
		}
	}

	/**
	 * load the TestSuite and rerender the Ui.
	 */
	public void loadAndRerender() {
		setTestStructure(testSuite);

	}

	/**
	 * 
	 * Creates the UI of the TestSuiteEditor.
	 * 
	 * @param parent
	 *            composite
	 * @param eventBroker
	 *            IEventBroker
	 */
	@PostConstruct
	public void createControls(Composite parent, IEventBroker eventBroker) {
		this.eventBroker = eventBroker;
		parent.setLayout(new GridLayout(1, false));
		tableLabel = new Label(parent, SWT.NORMAL);
		tableLabel.setText(translate.translate("%testsuiteeditor.referedtestcases"));
		referredTestCasesViewer = new TableViewer(parent);
		referredTestCasesViewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		referredTestCasesViewer.setContentProvider(new ReferredTestStructureContentProvicer());
		referredTestCasesViewer.setSorter(new ViewerSorter() {
			@Override
			public int compare(Viewer viewer, Object e1, Object e2) {
				return ((TestStructure) e1).getFullName().compareTo(((TestStructure) e2).getFullName());
			}
		});
		referredTestCasesViewer.addOpenListener(getOpenListener(referredTestCasesViewer));
		IEclipseContext subcontext = context.createChild();
		subcontext.set("FullName", true);
		referredTestCasesViewer.setLabelProvider(ContextInjectionFactory.make(TestStructureTreeLabelProvider.class,
				subcontext));
		referredTestCasesViewer.getTable().addSelectionListener(getTabeleSelectionListener());
		referredTestCasesViewer.getTable()
				.addMouseListener(new TestEditorInputPartMouseAdapter(eventBroker, testSuite));

		Composite buttonBar = new Composite(parent, SWT.NORMAL);
		buttonBar.setLayout(new FillLayout(SWT.HORIZONTAL));
		Button addButton = new Button(buttonBar, SWT.NORMAL);
		addButton.setImage(IconConstants.ICON_ADD_OBJECT);
		addButton.setText(translate.translate("%testsuiteeditor.add.testcases.button.label"));
		addButton.addSelectionListener(getAddButtonSelectionListener());
		addButton.setToolTipText(translate.translate("%testsuiteeditor.add.testcases.button"));
		addButton.setData(CustomWidgetIdConstants.TEST_EDITOR_WIDGET_ID_SWT_BOT_KEY,
				CustomWidgetIdConstants.ADD_REFERRED_TESTCASE);
		removeButton = new Button(buttonBar, SWT.NORMAL);
		removeButton.setImage(IconConstants.ICON_DELETE);
		removeButton.setText(translate.translate("%testsuiteeditor.remove.testcases.button.label"));
		removeButton.setToolTipText(translate.translate("%testsuiteeditor.remove.testcases.button"));
		removeButton.addSelectionListener(getRemoveButtonSelectionListener());
		removeButton.setData(CustomWidgetIdConstants.TEST_EDITOR_WIDGET_ID_SWT_BOT_KEY,
				CustomWidgetIdConstants.REMOVE_REFERRED_TESTCASE);
		updateRemoveButtonEnabledState();
		String testSuiteFullName = mpart.getPersistedState().get(EDITOR_OBJECT_ID_FOR_RESTORE);
		if (testSuiteFullName != null) {
			LOGGER.trace("Restoring TestSuite Editor for:" + testSuiteFullName);
			TestProjectService projectService = context.get(TestProjectService.class);
			try {
				TestSuite suite = (TestSuite) projectService.findTestStructureByFullName(testSuiteFullName);
				if (suite != null) {
					setTestSuite(suite);
				}
			} catch (SystemException e) {
				LOGGER.error("Error on reading projects", e);
				MessageDialog.openError(parent.getShell(), translate.translate("%error"),
						translate.translate("%error.loading_project", testSuiteFullName));
			}
		}
	}

	/**
	 * 
	 * @param testCaseViewer
	 *            Viewer for Testcases used to get the seleted testcase to be
	 *            opened.
	 * @return OpenListener to open the selected TestCase in the editor.
	 */
	protected IOpenListener getOpenListener(final TableViewer testCaseViewer) {
		return new IOpenListener() {

			@Override
			public void open(OpenEvent event) {
				Object element = ((IStructuredSelection) testCaseViewer.getSelection()).getFirstElement();

				try {
					TestCase selected = (TestCase) element;
					OpenTestStructureHandler handler = ContextInjectionFactory.make(OpenTestStructureHandler.class,
							context);
					handler.execute(selected, context);
				} catch (ClassCastException e) {

					if (element instanceof BrokenTestStructure) {
						MessageDialog.openInformation(Display.getCurrent().getActiveShell(), "Info", MessageFormat
								.format(translate.translate("%testsuiteeditor.referedtestcases.not.available"),
										((BrokenTestStructure) element).getName()));

					}
				}
			}
		};
	}

	/**
	 * 
	 * @return a SelectionListener for the table to activate and deactivate
	 *         operations in the UI.
	 */
	protected SelectionListener getTabeleSelectionListener() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateRemoveButtonEnabledState();
			}
		};
	}

	/**
	 * Updates the enabled state of the removeButton. The buttons is enabled if
	 * there is a selection in the TestStructureTable.
	 */
	protected void updateRemoveButtonEnabledState() {
		removeButton.setEnabled(!referredTestCasesViewer.getSelection().isEmpty());
	}

	/**
	 * 
	 * @return a SelectionListener for the Remove Referred TestCase behavior.
	 *         The SelectionListener will remove the selected Elements from the
	 *         table and sets the editor state to dirty.
	 */
	protected SelectionListener getRemoveButtonSelectionListener() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Object[] testStructures = getSelectedTestStructures();
				for (Object object : testStructures) {
					testSuite.removeReferredTestStructure((TestStructure) object);
				}
				updateTestSuiteEditor(null);
				updateRemoveButtonEnabledState();
				mpart.setDirty(true);
				referredTestCasesViewer.getControl().setFocus();
			}
		};
	}

	/**
	 * 
	 * @return the Selection in the Table.
	 */
	protected Object[] getSelectedTestStructures() {
		IStructuredSelection selection = (IStructuredSelection) referredTestCasesViewer.getSelection();
		return selection.toArray();
	}

	/**
	 * 
	 * @return a SelectionListener used by the addButton to add the selected
	 *         TestStructure of the Selection Dialog to the TestSuite.
	 */
	protected SelectionListener getAddButtonSelectionListener() {
		return new SelectionAdapter() {
			@SuppressWarnings("unchecked")
			@Override
			public void widgetSelected(SelectionEvent e) {
				TestStructureSelectionDialog selectionDialog = getTestCaseSelectionDialog();
				selectionDialog.addFilter(getNotUsableProjectFilter());
				selectionDialog.addFilter(ContextInjectionFactory.make(TestScenarioFilter.class, context));
				if (Dialog.OK == selectionDialog.open()) {
					Iterator<TestStructure> testStructures = selectionDialog.getSelection().iterator();
					while (testStructures.hasNext()) {
						TestStructure testStructure = testStructures.next();
						if (testStructure != null && testStructure instanceof TestCase) {
							testSuite.addReferredTestStructure(testStructure);
							mpart.setDirty(true);
						}
					}
					updateTestSuiteEditor(null);
					referredTestCasesViewer.getControl().setFocus();
				}
			}
		};
	}

	/**
	 * 
	 * @return ViewerFilter that hides other Projects, which can not be used in
	 *         the current TestSuite.
	 */
	protected ViewerFilter getNotUsableProjectFilter() {
		return new ViewerFilter() {

			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				TestStructure ts = (TestStructure) element;
				return ts.getRootElement().equals(testSuite.getRootElement());
			}

		};
	}

	/**
	 * 
	 * @return TestStructureSelectionDialog created by e4 ContextInjection
	 */
	protected TestStructureSelectionDialog getTestCaseSelectionDialog() {
		if (selectionDialog == null) {
			selectionDialog = ContextInjectionFactory.make(TestStructureSelectionDialog.class, context);
		}
		return selectionDialog;
	}

	/**
	 * Save the TestSuite.
	 */
	@Persist
	public void save() {
		try {
			testStructureContentService.saveTestStructureData(testSuite);
			mpart.setDirty(false);
		} catch (SystemException e) {
			LOGGER.trace("Error saving Config", e);
			MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error", e.getCause().getMessage());
		}
	}

	/**
	 * this method is called, when the TestSuiteEditor gets the focus.
	 * 
	 * @param shell
	 *            the active shell injected
	 * @param eventBroker
	 *            IEventBroker
	 */
	@Focus
	public void onFocus(@Named(IServiceConstants.ACTIVE_SHELL) Shell shell, IEventBroker eventBroker) {
		this.eventBroker = eventBroker;
		if (eventBroker != null) {
			eventBroker.send(TestEditorUIEventConstants.ACTIVE_TESTFLOW_EDITOR_CHANGED, testSuite);
		}
		shell.setDefaultButton(null);
	}

	/**
	 * Sets the TestSuite to work on with this editor.
	 * 
	 * @param testSuite
	 *            to be edited.
	 */
	public void setTestSuite(TestSuite testSuite) {
		this.testSuite = testSuite;
		referredTestCasesViewer.getTable()
				.addMouseListener(new TestEditorInputPartMouseAdapter(eventBroker, testSuite));
		mpart.setDirty(false);
		mpart.setLabel(testSuite.getName());
		try {
			testStructureContentService.refreshTestCaseComponents(testSuite);
		} catch (SystemException e) {
			LOGGER.trace("Error loading TestScenario content", e);
			MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error", e.getCause().getMessage());
		} catch (TestCycleDetectException e) {
			LOGGER.trace("Error loading TestScenario content", e);
			MessageDialog.openWarning(Display.getCurrent().getActiveShell(), "Warning", e.getCause().getMessage());
		}
		referredTestCasesViewer.setInput(testSuite);
		mpart.getPersistedState().put(EDITOR_OBJECT_ID_FOR_RESTORE, testSuite.getFullName());
		tableLabel.setText(translate.translate("%testsuiteeditor.referedtestcases") + " ("
				+ testSuite.getReferredTestStrcutures().size() + ")");
		tableLabel.getParent().layout(true, true);
	}

	/**
	 * will be called from the eventBroker, if the TEST_STRUCTURE_UPDATED event
	 * is sent.
	 * 
	 * @param testStructureFullName
	 *            updated TestStructure
	 */
	@Inject
	@Optional
	public void objectUpdatetByTeamshare(
			@UIEventTopic(TestEditorCoreEventConstants.TESTSTRUCTURE_MODEL_CHANGED) String testStructureFullName) {
		if (testStructureFullName != null && testSuite != null) {
			if (testStructureFullName.equals(testSuite.getFullName())) {
				setTestStructure(testSuite);
			}
		}
	}

	/**
	 * Will refresh the refferredTestCaseViewer if REFRESH_TEST_SUITE_EDITOR is
	 * send by eventBroker.
	 * 
	 * @param testStructure
	 *            updated TestStructure
	 */
	@Inject
	@Optional
	public void updateTestSuiteEditor(
			@UIEventTopic(TestEditorUIEventConstants.TESTSTRUCTURE_EXECUTED) TestStructure testStructure) {
		referredTestCasesViewer.refresh();
		tableLabel.setText(translate.translate("%testsuiteeditor.referedtestcases") + " ("
				+ testSuite.getReferredTestStrcutures().size() + ")");
	}

	/**
	 * 
	 * @return testSuite the Editor works on.
	 */
	public TestSuite getTestSuite() {
		return testSuite;
	}

	/**
	 * 
	 * @return model part of this ui for test purpose.
	 */
	protected MPart getMPart() {
		return mpart;
	}

	/**
	 * 
	 * @return removeButton for test purpose.
	 */
	protected Button getRemoveButton() {
		return removeButton;
	}

	@Override
	public TestStructure getTestStructure() {
		return getTestSuite();
	}

	@Override
	public void closePart() {
		mpart.setDirty(false);
		partService.hidePart(mpart, true);
	}

	@Override
	public void setTestStructure(TestStructure testStructure) {
		if (testStructure instanceof TestSuite) {
			setTestSuite((TestSuite) testStructure);
		}
	}

	/**
	 * 
	 * @return the referredTestCasesViewer for Test purpose.
	 */
	TableViewer getReferredTestCasesViewer() {
		return referredTestCasesViewer;
	}

}
