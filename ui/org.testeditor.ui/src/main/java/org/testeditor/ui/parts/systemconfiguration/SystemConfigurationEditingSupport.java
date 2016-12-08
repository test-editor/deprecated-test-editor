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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;
import org.testeditor.ui.constants.TestEditorUIEventConstants;

/**
 * special EditingSupport.
 * 
 * @author llipinski
 * 
 */
public class SystemConfigurationEditingSupport extends EditingSupport {
	private static final Logger LOGGER = Logger.getLogger(SystemConfigurationEditingSupport.class);

	private String mappedData;
	private TextCellEditor textCellEditor;
	private IEventBroker eventBroker;
	private boolean isKeyData;

	/**
	 * constructor.
	 * 
	 * @param viewer
	 *            ColumnViewer
	 */
	public SystemConfigurationEditingSupport(ColumnViewer viewer) {
		super(viewer);
	}

	/**
	 * special constructor with special parameters.
	 * 
	 * @param viewer
	 *            ColumnViewer
	 * @param mappedData
	 *            the name of the field in the dataModel. There should be a
	 *            getter with the name "get" + columnDataModelField and a setter
	 *            with "set" + columnDataModelField
	 * @param textCellEditor
	 *            the TextCellEditor
	 * @param eventBroker
	 *            IEventBroker
	 * @param isKeyData
	 *            special boolean for functionality for the storing of
	 *            key-value-pairs in a hashmap. Should be set to true, if the
	 *            column is the key.
	 */
	public SystemConfigurationEditingSupport(ColumnViewer viewer, String mappedData, TextCellEditor textCellEditor,
			IEventBroker eventBroker, boolean isKeyData) {
		super(viewer);
		this.mappedData = mappedData;
		this.textCellEditor = textCellEditor;
		this.eventBroker = eventBroker;
		this.isKeyData = isKeyData;
	}

	@Override
	protected void setValue(Object element, Object value) {
		String oldData;
		String message = "error on setting the value";
		try {
			Method getMethod = element.getClass().getMethod("get" + mappedData);
			oldData = (String) getMethod.invoke(element);

			if (!oldData.equals(value)) {
				Method setMethod = element.getClass().getMethod("set" + mappedData, new Class[] { value.getClass() });
				setMethod.invoke(element, (String) value);
				if (isKeyData) {
					eventBroker.send(TestEditorUIEventConstants.SYSTEMCONFIGURATION_TABLE_KEY_DELETED,
							new SystemConfigurationKeyDeletedContainer(getViewer(), oldData));
				}
				eventBroker.send(TestEditorUIEventConstants.SYSTEMCONFIGURATION_TABLE_UPDATE_ELEMENT,
						new SystemConfigurationUpdateElementContainer(getViewer(), element));
			}
		} catch (NoSuchMethodException e) {
			LOGGER.error(message, e);
		} catch (SecurityException e) {
			LOGGER.error(message, e);
		} catch (IllegalAccessException e) {
			LOGGER.error(message, e);
		} catch (IllegalArgumentException e) {
			LOGGER.error(message, e);
		} catch (InvocationTargetException e) {
			LOGGER.error(message, e);
		}
	}

	@Override
	protected Object getValue(Object element) {
		Method method;
		String message = "error on getting the value";
		try {
			method = element.getClass().getMethod("get" + mappedData);
			String value = (String) method.invoke(element);
			if (mappedData.equalsIgnoreCase("Key") && value.equalsIgnoreCase("-1")) {
				return "";
			}
			return (String) method.invoke(element);
		} catch (NoSuchMethodException e) {
			LOGGER.error(message, e);
		} catch (SecurityException e) {
			LOGGER.error(message, e);
		} catch (IllegalAccessException e) {
			LOGGER.error(message, e);
		} catch (IllegalArgumentException e) {
			LOGGER.error(message, e);
		} catch (InvocationTargetException e) {
			LOGGER.error(message, e);
		}
		return "";
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		return textCellEditor;
	}

	@Override
	protected boolean canEdit(Object element) {
		return true;
	}
}
