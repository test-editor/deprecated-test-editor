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
package org.testeditor.ui.wizardpages;

import java.util.List;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.testeditor.core.model.teststructure.TestCompositeStructure;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.services.interfaces.TestEditorReservedNamesService;
import org.testeditor.core.services.interfaces.TestProjectService;
import org.testeditor.core.services.interfaces.TestStructureService;
import org.testeditor.ui.constants.CustomWidgetIdConstants;
import org.testeditor.ui.constants.IconConstants;
import org.testeditor.ui.parts.commons.tree.TestStructureTree;
import org.testeditor.ui.utilities.TestEditorTranslationService;
import org.testeditor.ui.wizardpages.nameinspector.INameInspector;

/**
 * Abstract wizard for any operation regarding the test structure. E.g. a lower
 * class could implement a test case creation or a test suite renaming. It is
 * possible to display the wizard with or without a name entry field.In the
 * default configuration is the name field rendered
 */
public abstract class AbstractTestStructureWizardPage extends WizardPage {

	private static final Logger LOGGER = Logger.getLogger(AbstractTestStructureWizardPage.class);

	private Composite container;

	private Text nameText;
	private TestStructure selectedTS;
	private boolean renderNameField = true;
	private TestStructureTree testStructureTree;

	@Inject
	private TestEditorTranslationService translationService;

	@Inject
	private TestStructureService testStructureService;

	@Inject
	private TestProjectService testProjectService;

	@Inject
	private TestEditorReservedNamesService testEditorReservedNamesService;

	@Inject
	private IEclipseContext context;

	private TestEditorChildrenOfParentContainer childrenOfParentContainer;

	private Font hintFont;

	/**
	 * Initializes the wizard page.
	 */
	public AbstractTestStructureWizardPage() {
		super("");
	}

	/**
	 * Returns the title of the wizard (e.g. New test case wizard)
	 * 
	 * @return title
	 */
	public abstract String getTitleValue();

	/**
	 * Returns the description of the wizard (e.g. Wizard creates a new test
	 * case inside the given project and test suite).
	 * 
	 * @return description
	 */
	public abstract String getDescriptionValue();

	/**
	 * Creates the layout after the wizard is created.
	 * 
	 * @param parent
	 *            UI parent
	 */
	@Override
	public void createControl(Composite parent) {
		parent.getShell().setImage(IconConstants.ICON_TESTEDITOR);

		// sets the title and description of the wizard
		setTitle(getTitleValue());
		setDescription(getDescriptionValue());

		// Initialize the frame object of this wizard-page
		container = new Composite(parent, SWT.NORMAL);
		getWidgetContainer().setLayout(new GridLayout(2, false));

		// Show the hint text
		if (renderNameField) {
			Group hintGroup = new Group(getWidgetContainer(), SWT.NORMAL);
			FontData fontData = hintGroup.getFont().getFontData()[0];
			hintFont = new Font(Display.getCurrent(), fontData.getName(), fontData.getHeight(), SWT.BOLD);
			hintGroup.setFont(hintFont);
			hintGroup.setText(getHintTextHeaderValue());
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.horizontalSpan = 2;
			hintGroup.setLayoutData(gd);
			hintGroup.setLayout(new FillLayout());
			Label hintText = new Label(hintGroup, SWT.NORMAL);
			hintText.setText(getHintTextValue());
			hintText.setData(CustomWidgetIdConstants.TEST_EDITOR_WIDGET_ID_SWT_BOT_KEY,
					CustomWidgetIdConstants.NAME_ERROR_MESSAGE_LABEL);
			new Label(getWidgetContainer(), SWT.NORMAL).setText(translationService.translate("%wizard.project.name"));
			// string
			nameText = new Text(getWidgetContainer(), SWT.BORDER | SWT.NORMAL | SWT.SINGLE);
			nameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			nameText.setData(CustomWidgetIdConstants.TEST_EDITOR_WIDGET_ID_SWT_BOT_KEY,
					CustomWidgetIdConstants.NEW_TEST_PAGE_NAME);

			// Validate its contents after every entered key
			nameText.addModifyListener(new ModifyListener() {

				/**
				 * Disables the 'finish' button depending on the name-validation
				 * 
				 * @param e
				 *            key(board) event
				 */
				@Override
				public void modifyText(ModifyEvent e) {
					validatePageAndSetComplete();
				}
			});
			nameText.setFocus();
		}

		// On default disable the 'finish' button
		setPageComplete(false);

		// Required to avoid an error in the system
		setControl(getWidgetContainer());

		LOGGER.info("createLocationTree");
		createLocationTree();
		LOGGER.info("createLocationTree done");
	}

	/**
	 * Returns the header value for the hint text (e.g. the hint that the
	 * specification is related to the field name).
	 * 
	 * @return hint header text
	 */
	protected String getHintTextHeaderValue() {
		return translationService.translate("%wizard.error.msgHead");
	}

	/**
	 * Returns the value for the hint text (e.g. the validation rules for the
	 * input of the name).
	 * 
	 * @return hint text
	 */
	protected String getHintTextValue() {
		return translationService.translate("%wizard.error.msg");
	}

	/**
	 * validates if the all page entries are set correctly set the PageComplete
	 * switch.
	 */
	protected void validatePageAndSetComplete() {
		if (renderNameField) {
			if (renderNameField || (isNameValid(nameText.getText()) && getSelectedTestStrucutureElement() != null)) {
				setPageComplete(true);
			} else {
				setPageComplete(false);
			}
		} else {
			setPageComplete(getSelectedTestStrucutureElement() != null);
		}
	}

	/**
	 * @return the entered name
	 */
	public String getTextInNameText() {
		if (renderNameField) {
			return nameText.getText();
		}
		throw new RuntimeException("nameText is not rendered in currentDialog");
	}

	/**
	 * Returns true if the name is valid.
	 * 
	 * @param name
	 *            of the Testflow
	 * 
	 * @return true if name is valid
	 */
	protected boolean isNameValid(String name) {
		// On default do not show the name-already-exist message
		this.setMessage(null);

		if (name == null || name.isEmpty()) {
			return false;
		}
		if (isReservedName(name)) {
			this.setErrorMessage(name + " " + translationService.translate("%wizard.error.msg.nameIsReservedWord"));
			return false;
		}

		// On renaming check if name already exist on the current tree-path,
		// only if it differs from the old name...
		if (getSelectedTestStrucutureElement() != null) {
			selectedTS = getSelectedTestStrucutureElement();
		}
		// In case all validation-check were passed
		if (getNameInspector().isNameValid(name)) {
			setErrorMessage(null);
			return true;
		} else {
			setErrorMessage(getNameInspector().nameInvalideMessage());
			return false;
		}
	}

	/**
	 * 
	 * @param name
	 *            of the new teststructure
	 * @return true if it is a reserved name of the test server or a testeditor
	 *         global reserved name.
	 */
	protected boolean isReservedName(String name) {
		// Handle reserved names in test structure
		TestStructure selectedTestStrucutureElement = getSelectedTestStrucutureElement();
		if (selectedTestStrucutureElement != null) {
			if (testStructureService.isReservedName(selectedTestStrucutureElement.getRootElement(), name)) {
				return true;
			}
		}
		// Handle globally reserved names
		return testEditorReservedNamesService.isReservedName(name);
	}

	/**
	 * Searches for the given name in the list of children's.
	 * 
	 * @param name
	 *            to search for
	 * @param testStructure
	 *            to begin search
	 * @return true if the name is found.
	 */
	protected boolean isNamePartOfChildren(String name, TestStructure testStructure) {
		if (testStructure instanceof TestCompositeStructure) {
			List<TestStructure> testChildren = null;
			if (childrenOfParentContainer != null && childrenOfParentContainer.getTestCompStructure() != null
					&& childrenOfParentContainer.getTestCompStructure().equals(testStructure)) {
				testChildren = childrenOfParentContainer.getChildren();
			} else {
				testChildren = ((TestCompositeStructure) testStructure).getTestChildren();
				childrenOfParentContainer = new TestEditorChildrenOfParentContainer(
						(TestCompositeStructure) testStructure, testChildren);
			}
			for (TestStructure ts : testChildren) {
				if (ts.getName().equals(name)) {
					this.setErrorMessage(translationService.translate("%wizard.error.double"));
					return true;
				}
			}
		}
		setErrorMessage(null);
		return false;
	}

	/**
	 * Access to the TeststructureTree Component.
	 * 
	 * @return the TeststructureTree of the Wizard
	 */
	protected TestStructureTree getTestStructureTree() {
		return testStructureTree;
	}

	/**
	 * 
	 * @return the in the TestStructureTree selected Teststructure.
	 */
	public TestStructure getSelectedTestStrucutureElement() {
		if (testStructureTree != null) {
			return testStructureTree.getSelectedTestStrucuture();
		}
		return getSelectedTestStructure();
	}

	/**
	 * gets the private field nameText.
	 * 
	 * @return the field nameText
	 */
	protected Text getNameText() {
		return nameText;
	}

	/**
	 * gets the selectedTestStructure.
	 * 
	 * @return the selected {@link TestStructure}
	 */
	protected TestStructure getSelectedTestStructure() {
		return selectedTS;
	}

	/**
	 * set the selected {@link TestStructure}.
	 * 
	 * @param selectedTestStructure
	 *            {@link TestStructure}
	 */
	public void setSelectedTestStructure(TestStructure selectedTestStructure) {
		selectedTS = selectedTestStructure;
	}

	/**
	 * Creates a Tree with Testssuites to allow the selection of the parent
	 * node.
	 */
	protected void createLocationTree() {
		Label treeLabel = new Label(getWidgetContainer(), SWT.NORMAL);
		treeLabel.setText(translationService.translate("%wizard.tree.label"));
		GridData gdLabel = new GridData();
		gdLabel.verticalAlignment = GridData.BEGINNING;
		treeLabel.setLayoutData(gdLabel);
		testStructureTree = ContextInjectionFactory.make(TestStructureTree.class, context);
		testStructureTree.createUI(getWidgetContainer(), testProjectService);
		testStructureTree.getTreeViewer().getTree().setData(CustomWidgetIdConstants.TEST_EDITOR_WIDGET_ID_SWT_BOT_KEY,
				CustomWidgetIdConstants.TEST_EXPLORER_TREE);
		// testStructureTree.showOnlyParentStructures();
		GridData gdTree = new GridData(GridData.FILL_BOTH);
		testStructureTree.getTreeViewer().setData(CustomWidgetIdConstants.TEST_EDITOR_WIDGET_ID_SWT_BOT_KEY,
				CustomWidgetIdConstants.TEST_EXPLORER_TREE);
		testStructureTree.getTreeViewer().getTree().setLayoutData(gdTree);
		testStructureTree.selectTestStructure(selectedTS);
		testStructureTree.getTreeViewer().addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				validatePageAndSetComplete();
			}
		});
	}

	/**
	 * 
	 * should be implemented in the child-objects.
	 * 
	 * @return the specialized name-inspector for the child-class
	 */
	protected abstract INameInspector getNameInspector();

	/**
	 * 
	 * @return SWT Composite with the content of the wizard.
	 */
	public Composite getWidgetContainer() {
		return container;
	}

	@Override
	public void dispose() {
		if (hintFont != null && !hintFont.isDisposed()) {
			hintFont.dispose();
			hintFont = null;
		}
		super.dispose();
	}

	/**
	 * Tests whether the name field is rendered.
	 * 
	 * @return - true if the name field has to be rendered.
	 */
	public boolean isRenderNameField() {
		return renderNameField;
	}

	/**
	 * Sets the parameter rendeerNameField.
	 * 
	 * @param renderNameField
	 *            -the value
	 */
	public void setRenderNameField(boolean renderNameField) {
		this.renderNameField = renderNameField;
	}

}