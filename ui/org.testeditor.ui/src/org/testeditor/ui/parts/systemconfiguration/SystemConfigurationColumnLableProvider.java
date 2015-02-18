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
import org.eclipse.jface.viewers.ColumnLabelProvider;

/**
 * special column-label-provider.
 * 
 * @author llipinski
 * 
 */
public class SystemConfigurationColumnLableProvider extends ColumnLabelProvider {
	private static final Logger LOGGER = Logger.getLogger(SystemConfigurationColumnLableProvider.class);

	private String mappedData;

	/**
	 * special constructor.
	 * 
	 * @param mappedData
	 *            the name of the field in the dataModel. There should be a
	 *            getter with the name "get" + columnDataModelField and a setter
	 *            with "set" + columnDataModelField
	 */
	public SystemConfigurationColumnLableProvider(String mappedData) {
		super();
		this.mappedData = mappedData;
	}

	@Override
	public String getText(Object element) {
		Method method;
		String message = "error on getText";
		try {
			method = element.getClass().getMethod("get" + mappedData, null);
			return (String) method.invoke(element, null);
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
}
