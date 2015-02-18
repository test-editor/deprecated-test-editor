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
package org.testeditor.ui.parts.systemconfiguration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.widgets.grid.GridCellRenderer;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.osgi.service.prefs.BackingStoreException;
import org.testeditor.core.services.interfaces.TestEditorConfigurationService;
import org.testeditor.core.util.KeyValuePair;
import org.testeditor.ui.constants.TestEditorConstants;
import org.testeditor.ui.constants.TestEditorEventConstants;
import org.testeditor.ui.constants.TestEditorUIEventConstants;
import org.testeditor.ui.table.TestEditorTableViewerFactory;
import org.testeditor.ui.utilities.TestEditorTranslationService;

/**
 * Editor for the system variables.
 */
public class SystemConfigurationEditor {

	private static final String DUMMY_ELEMENT = "-1";

	private static final Logger LOGGER = Logger.getLogger(SystemConfigurationEditor.class);

	public static final String ID = TestEditorConstants.SYSTEM_CONFIGUTRATION_VIEW;

	protected static final int KEY_INDEX = 0;
	private MPart mpart;

	@Inject
	private TestEditorTranslationService translate;

	@Inject
	private IEventBroker eventBroker;

	@Inject
	private IEclipseContext context;

	@Inject
	private TestEditorConfigurationService testEditorConfigService;

	private boolean hasFocus = false;

	private ArrayList<KeyValuePair> tempModel;
	private List<String> oldKeys = new ArrayList<String>();

	private GridTableViewer tableViewer;

	/**
	 * 
	 * @param part
	 *            model representing this ui part.
	 */
	@Inject
	public SystemConfigurationEditor(MPart part) {
		mpart = part;
	}

	/**
	 * 
	 * @param parent
	 *            composite
	 * @throws BackingStoreException
	 *             if tempModel fails loading data.
	 */
	@PostConstruct
	public void createControls(Composite parent) throws BackingStoreException {

		Map<String, String> globalVariables = testEditorConfigService.getGlobalVariables();

		tempModel = new ArrayList<KeyValuePair>();

		String[] keysOfGlobalVariables = globalVariables.keySet().toArray(new String[] {});

		for (String key : keysOfGlobalVariables) {
			String value = globalVariables.get(key);
			tempModel.add(new KeyValuePair(key, value));
		}

		Collections.sort(tempModel);

		List<SystemconfigurationColumnDescription> columnDescriptions = new ArrayList<SystemconfigurationColumnDescription>();
		columnDescriptions.add(new SystemconfigurationColumnDescription(
				translate("%testprojecteditor.grouptestglobalvariableskey"), "Key", 1, true));
		columnDescriptions.add(new SystemconfigurationColumnDescription(
				translate("%testprojecteditor.grouptestglobalvariablesvalue"), "Value", 3, false));
		SystemConfigurationTableColumnCreater tableColumnCreater = new SystemConfigurationTableColumnCreater(
				columnDescriptions, eventBroker);
		tableViewer = TestEditorTableViewerFactory.createTableViewer(parent, tempModel, tableColumnCreater);

		tableViewer.getGrid().setEmptyCellRenderer(getEmptyCellHidingRenderer());
	}

	/**
	 * Save the SystemConfig.
	 * 
	 * @param sync
	 *            to access the ui thread.
	 */
	@Persist
	public void save(UISynchronize sync) {
		sync.syncExec(new Runnable() {

			@Override
			public void run() {
				addGlobaleVariableToSystemPreferences();
			}
		});

		setDirty(false);
	}

	/**
	 * Add new row to table.
	 * 
	 */
	public void addRow() {
		tempModel.add(new KeyValuePair(DUMMY_ELEMENT, ""));
		tableViewer.setInput(tempModel);
		tableViewer.editElement(tempModel.get(tempModel.size() - 1), 0);
		setDirty(true);
	}

	/**
	 * Remove row to table.
	 * 
	 */
	public void removeRow() {

		Point focusCell = tableViewer.getGrid().getFocusCell();
		KeyValuePair kvp = (KeyValuePair) tableViewer.getElementAt(focusCell.y);

		if (kvp != null) {
			tempModel.remove(kvp);

			tableViewer.setInput(tempModel);

			oldKeys.add(kvp.getKey());
			setDirty(true);
		}
	}

	/**
	 * Will be called by the event TABLE_UPDATE_ELEMENT. Sets the element in the
	 * table and the part dirty.
	 * 
	 * @param teUpdateElement
	 *            a special object, that holds a reference to the element and
	 *            the tableViewer to identify the receiver
	 */
	@Inject
	@Optional
	protected void updateTable(
			@UIEventTopic(TestEditorUIEventConstants.SYSTEMCONFIGURATION_TABLE_UPDATE_ELEMENT) SystemConfigurationUpdateElementContainer teUpdateElement) {
		if (teUpdateElement.getViewer().equals(tableViewer)) {
			tableViewer.update(teUpdateElement.getElement(), null);
			setDirty(true);
		}
	}

	/**
	 * Will be called by the event TABLE_KEY_DELETED. Stores the deleted key in
	 * the oldKey-field.
	 * 
	 * @param teKeyDeletedContainer
	 *            a special object, that holds a reference to the element and
	 *            the tableViewer to identify the receiver
	 */
	@Inject
	@Optional
	protected void keyDelete(
			@UIEventTopic(TestEditorUIEventConstants.SYSTEMCONFIGURATION_TABLE_KEY_DELETED) SystemConfigurationKeyDeletedContainer teKeyDeletedContainer) {
		if (teKeyDeletedContainer.getViewer().equals(tableViewer)) {
			oldKeys.add(teKeyDeletedContainer.getOldKey());
		}
	}

	/**
	 * this method is called, when the ProjectEditor gets the focus.
	 * 
	 * @param shell
	 *            the active shell injected
	 */
	@Focus
	public void onFocus(@Named(IServiceConstants.ACTIVE_SHELL) Shell shell) {
		shell.setDefaultButton(null);
		if (!hasFocus) {
			hasFocus = true;
			eventBroker.send(TestEditorEventConstants.GET_FOCUS_ON_INPUT_PART, null);
			mpart.setOnTop(true);
		}
	}

	/**
	 * returns the translation for a given string form the language-resource.
	 * 
	 * @param translateKey
	 *            the key for the translation
	 * @return the translation
	 */
	protected String translate(String translateKey) {
		return translate.translate(translateKey);
	}

	/**
	 * puts the input in the variable-fields into the SystemPreferences.
	 */
	private void addGlobaleVariableToSystemPreferences() {
		for (KeyValuePair keyValuePair : tempModel) {
			try {
				if (!oldKeys.isEmpty()) {
					for (String oldKey : oldKeys) {
						testEditorConfigService.clearKey(oldKey);
					}
					oldKeys.clear();
					testEditorConfigService.loadGlobalVariablesAsSystemProperties();
				}

			} catch (BackingStoreException e) {
				LOGGER.error("Error cleaning key", e);
			}
			if ((!keyValuePair.getKey().isEmpty()) && (!keyValuePair.getKey().equals("-1"))) {
				testEditorConfigService.updatePair(keyValuePair.getKey(), keyValuePair.getValue());
			} else {
				try {
					testEditorConfigService.clearKey(keyValuePair.getKey());
				} catch (BackingStoreException e) {
					LOGGER.error("Error cleaning key", e);
				}
			}
		}
		try {
			testEditorConfigService.storeChanges();
			setDirty(false);
		} catch (BackingStoreException e) {
			LOGGER.error("Error storing configuration", e);
			MessageDialog.openError(Display.getDefault().getActiveShell(), translate.translate("%error"),
					e.getMessage());
		}
	}

	/**
	 * Sets the dirty flag.
	 * 
	 * @param b
	 *            for dirty true or false.
	 */
	private void setDirty(boolean b) {
		mpart.setDirty(b);
	}

	/**
	 * sets the hasFocus-variable to false.
	 * 
	 * @param obj
	 *            Object
	 */
	@Inject
	@Optional
	public void focusLost(@UIEventTopic(TestEditorEventConstants.GET_FOCUS_ON_INPUT_PART) Object obj) {
		hasFocus = false;
	}

	/**
	 * 
	 * @return GridCellRenderer to hide empty cells.
	 */
	protected GridCellRenderer getEmptyCellHidingRenderer() {
		return new GridCellRenderer() {

			@Override
			public void paint(GC gc, Object value) {

			}

			@Override
			public Point computeSize(GC gc, int wHint, int hHint, Object value) {
				return null;
			}

			@Override
			public boolean notify(int event, Point point, Object value) {
				return false;
			}
		};
	}
}
