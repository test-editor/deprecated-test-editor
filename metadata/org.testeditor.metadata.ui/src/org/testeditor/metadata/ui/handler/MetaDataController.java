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
package org.testeditor.metadata.ui.handler;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.testeditor.core.model.teststructure.TestFlow;
import org.testeditor.metadata.core.MetaDataService;
import org.testeditor.metadata.core.model.MetaData;
import org.testeditor.metadata.core.model.MetaDataTag;
import org.testeditor.metadata.core.model.MetaDataValue;
import org.testeditor.ui.constants.IconConstants;
import org.testeditor.ui.parts.editor.ITestEditorTab;
import org.testeditor.ui.utilities.TestEditorTranslationService;

/**
 * Control to handle all actions on the meta-control tab. The following action
 * are handled by the class:
 * <ul>
 * <li>Select a MetaData -> Display the values for the metaDataValues
 * <li>Select a MetaDatavalue -> Add a new row to the bottom of the
 * metaTagValues of the current testcase, set the editor to dirty
 * <li>Remove an entry from the metatag table of the testcase. sets the editor
 * to dirty The Controller is container for the controls of the.
 * </ul>
 * The tab contains out of the following controls.
 * <ul>
 * <li>table of metaDataTags of the current testflow
 * <li>Selector and label to select MetaData
 * <li>Selector and label to select MetaDataValue (Selection is depending on
 * selected MetaData)
 * </ul>
 * The controller has a reference to the
 * <ul>
 * <li>metaDataService to read all metaData and metaDataValues
 * <li>the testflow to read the metaDataTags from the testcase
 * <li>translation service for the internationalisation of the labels .
 * </ul>
 * The controller is implementing the selectionListener to handle the changes in
 * the drop down boxes of the metaDataList and the metaDataValuelist<br/>
 * The controller is implementing the Listener to handle the click events on the
 * metaDataTags table to delete entries from the table.
 * 
 * @author Georg Portwich
 *
 */
public class MetaDataController implements Listener, ITestEditorTab, ISelectionChangedListener {

	@Inject
	private MetaDataService metaDataService;

	private Composite composite;
	private TestFlow testFlow;

	private List<MetaDataTag> metaDataTagList = new ArrayList<MetaDataTag>();

	private MPart mpart;

	private Table metaDataTagsTable;

	private ComboViewer metaDataCB;
	private Label lblMetaDataCB;
	private ComboViewer metaDataValuesCB;
	private Label lblMetaDataValuesCB;

	private static final Logger LOGGER = Logger.getLogger(MetaDataController.class);

	/**
	 * Created the composite and the containing controls. To use the control the
	 * data of the testflow have to be set in the method setTestFlow
	 * 
	 * @param parent
	 */
	public MetaDataController() {
	}

	/**
	 * Creates all controls in the tab and defines the layout. The data of the
	 * control is set in setTestFlow.
	 * 
	 * @param parent
	 *            - the tabfolder where the bat belongs to
	 * @param mpart
	 *            - the mpart object is used to inform the editor about a dirty
	 *            state.
	 * @return - the created tab
	 */
	public Composite createTab(TabFolder parent, MPart mpart, TestEditorTranslationService translationService) {

		composite = new Composite(parent, SWT.NONE);
		this.mpart = mpart;

		GridLayout gridLayout = new GridLayout();
		gridLayout.marginTop = 10;
		gridLayout.marginLeft = 10;
		composite.setLayout(gridLayout);

		// Setup the metatag table. The data is inserted in the method
		// setTestflow
		metaDataTagsTable = new Table(composite, SWT.MULTI | SWT.FULL_SELECTION | SWT.NO_SCROLL);
		metaDataTagsTable.setLinesVisible(true);
		metaDataTagsTable.setHeaderVisible(true);

		// Add headlines to the table
		String[] titles = { translationService.translate("%testeditor.tab.metadata.key"),
				translationService.translate("%testeditor.tab.metadata.value"), "" };
		for (int i = 0; i < titles.length; i++) {
			TableColumn column = new TableColumn(metaDataTagsTable, SWT.NONE);
			column.setText(titles[i]);
		}

		GridData gridTable = new GridData(SWT.FILL, SWT.TOP, true, false);
		metaDataTagsTable.setLayoutData(gridTable);
		metaDataTagsTable.getColumn(0).setWidth(200);
		metaDataTagsTable.getColumn(1).setWidth(200);
		metaDataTagsTable.getColumn(2).setWidth(20);
		metaDataTagsTable.addListener(SWT.MouseDown, this);

		Composite inputRow = new Composite(composite, SWT.NONE);

		GridLayout inputRowGridLayout = new GridLayout();
		inputRowGridLayout.numColumns = 4;
		inputRowGridLayout.marginLeft = 20;
		inputRowGridLayout.marginTop = 20;
		inputRowGridLayout.verticalSpacing = 100;
		inputRow.setLayout(inputRowGridLayout);

		GridData gridDataLabel = new GridData(SWT.NONE, SWT.CENTER, true, true);
		GridData gridDataControl = new GridData(SWT.FILL, SWT.BEGINNING, true, true);

		// Create label and drop down for metadatavalues. Controls are hidden
		lblMetaDataCB = new Label(inputRow, SWT.NONE);
		lblMetaDataCB.setText(translationService.translate("%testeditor.tab.metadata.key"));
		lblMetaDataCB.setLayoutData(gridDataLabel);

		metaDataCB = new ComboViewer(inputRow);
		metaDataCB.setLabelProvider(new LabelProvider());
		metaDataCB.setContentProvider(ArrayContentProvider.getInstance());
		metaDataCB.addSelectionChangedListener(this);
		metaDataCB.getCombo().setLayoutData(gridDataControl);

		// Create label and drop down for metadata
		lblMetaDataValuesCB = new Label(inputRow, SWT.NONE);
		lblMetaDataValuesCB.setText(translationService.translate("%testeditor.tab.metadata.value"));
		lblMetaDataValuesCB.setLayoutData(gridDataLabel);
		lblMetaDataValuesCB.setVisible(false);

		metaDataValuesCB = new ComboViewer(inputRow);
		metaDataValuesCB.setLabelProvider(new LabelProvider());
		metaDataValuesCB.setContentProvider(ArrayContentProvider.getInstance());
		metaDataValuesCB.addSelectionChangedListener(this);
		metaDataValuesCB.getCombo().setLayoutData(gridDataControl);
		metaDataValuesCB.getCombo().setVisible(false);

		return composite;
	}

	/**
	 * Sets the metadatatags of the current testFlow. The method must be called
	 * to setup the table containing the metatagtable.
	 * 
	 * @param testFlow
	 *            - the testFlow
	 */
	public void setTestFlow(TestFlow testFlow) {
		this.testFlow = testFlow;

		metaDataTagList.clear();
		metaDataTagList.addAll(getMetaDataService().getMetaDataTags(testFlow));

		if (metaDataTagList.size() == 0) {
			Label lblMessage = new Label(composite, SWT.NONE);
			String message = "Für das Projekt sind noch keine Metadaten angelegt.\n"
					+ "Bitte legen Sie im Projektverzeichnis eine metadata.properties Datei an.\n"
					+ "In dieser Pflegen Sie bitten Ihre Metadaten.\n\nBeispiel metadata.properties.\n\n"
					+ "metadata = Name der Metadaten-Kategorie\n"
					+ "metadata.schluessel1 = Name des Wertes des Wertes wie in der Oberfläche angezeigt wird\n"
					+ "metadata.schluessel2 = Nächster Wert und so weiter\n";
			lblMessage.setText(message);
			metaDataCB.getCombo().setVisible(false);
			lblMetaDataCB.setVisible(false);
			metaDataTagsTable.setVisible(false);

		} else {
			for (MetaData metaData : getMetaDataService().getAllMetaData(testFlow.getRootElement())) {
				metaDataCB.add(metaData);
			}
			metaDataCB.getCombo().pack(true);
			metaDataCB.getCombo().getParent().pack(true);

			for (MetaDataTag metaDataTag : metaDataTagList) {
				MetaDataValue metaDataValue = getMetaDataService().getMetaDataValue(metaDataTag,
						testFlow.getRootElement());
				TableItem tableRow = new TableItem(metaDataTagsTable, SWT.NONE);
				tableRow.setText(0, metaDataValue.getMetaData().getLabel());
				tableRow.setText(1, metaDataValue.getLabel());
				tableRow.setImage(2, IconConstants.ICON_DELETE);
			}
		}
	}

	/**
	 * Method to delete a metadatatag from the table.
	 */
	@Override
	public void handleEvent(Event event) {
		Point point = new Point(event.x, event.y);
		TableItem selectedItem = metaDataTagsTable.getItem(point);
		if (selectedItem == null) {
			return;
		}
		// Detect if the last row was clicked (the row with the delete icon).
		Rectangle rect = selectedItem.getBounds(2);
		if (!rect.contains(point)) {
			return;
		}
		int index = 0;
		for (TableItem item : metaDataTagsTable.getItems()) {
			if (item.getText(0).equals(selectedItem.getText(0)) && item.getText(1).equals(selectedItem.getText(1))) {
				metaDataTagList.remove(index);
				metaDataTagsTable.remove(index);
				metaDataTagsTable.getParent().pack();
				if (mpart != null) {
					mpart.setDirty(true);
				}
				return;
			}
			index++;
		}
	}

	/**
	 * Stores the metadata of the current testFlow.
	 */
	@Override
	public void save() {
		getMetaDataService().storeMetaDataTags(metaDataTagList, testFlow);
	}

	@Override
	public String getLabel(TestEditorTranslationService translationService) {
		return translationService.translate("%testeditor.tab.metadata.label");
	}

	/**
	 * Listener to handle the actions.
	 * <ul>
	 * <li>The selection of a metadata -> render the metadatavalue selection
	 * <li>the selection of a metadatavalue -> add a new metadatatag to the
	 * bottom of the table an reset the selection in the comboboxes
	 * </ul>
	 */
	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		if (event.getSource().equals(metaDataCB)) {
			IStructuredSelection selection = (IStructuredSelection) metaDataCB.getSelection();
			MetaData metaData = (MetaData) selection.getFirstElement();
			for (MetaDataValue metaDataValue : metaData.getValues()) {
				metaDataValuesCB.add(metaDataValue);
			}
			lblMetaDataValuesCB.setVisible(true);
			lblMetaDataValuesCB.pack(true);

			metaDataValuesCB.getCombo().setVisible(true);
			metaDataValuesCB.getCombo().getParent().pack(true);

		} else if (event.getSource().equals(metaDataValuesCB)) {
			IStructuredSelection selection = (IStructuredSelection) metaDataValuesCB.getSelection();
			MetaDataValue metaDataValue = (MetaDataValue) selection.getFirstElement();

			TableItem item = new TableItem(metaDataTagsTable, SWT.NONE, metaDataTagsTable.getItemCount());
			item.setText(0, metaDataValue.getMetaData().getLabel());
			item.setText(1, metaDataValue.getLabel());
			item.setImage(2, IconConstants.ICON_DELETE);
			if (mpart != null) {
				mpart.setDirty(true);
			}
			metaDataTagList.add(new MetaDataTag(metaDataValue));
			metaDataTagsTable.getParent().pack();
			lblMetaDataValuesCB.setVisible(false);
			metaDataValuesCB.getCombo().setVisible(false);
			metaDataValuesCB.getCombo().removeAll();
			metaDataCB.getCombo().deselectAll();

		}
	}

	public void bindMetaDataService(MetaDataService metaDataService) {
		this.metaDataService = metaDataService;
	}

	public void unbinbMetaDataService(MetaDataService metaDataService) {
		this.metaDataService = null;
	}

	private MetaDataService getMetaDataService() {
		if (metaDataService == null) {
			throw new RuntimeException(
					"MetaDataService is not set. Probably is the plugin org.testeditor.metadata.core not activated");
		}
		return metaDataService;

	}

}
