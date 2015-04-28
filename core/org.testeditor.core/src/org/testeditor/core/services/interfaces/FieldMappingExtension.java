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
package org.testeditor.core.services.interfaces;

import org.eclipse.e4.core.services.translation.TranslationService;

/**
 * 
 * FieldMappingExtensions are used to allow other (non-UI) bundles to inject a
 * (form-)field into a UI Component. The object provides display relevant
 * information (tooltip, label) to the UI and in return gets notified by the UI
 * when an update event occurred.
 * 
 * This Interface is designed to show Text Widgets with a label in the UI. The
 * FieldMappingExtension also handles the Bean Updates.
 * 
 * This interface is designed with a very narrow context, i.e. it is used to
 * allow configuration values to be set in a external UI. Thus a separate UI for
 * the configuration is not necessary.
 */
public interface FieldMappingExtension {

	/**
	 * Returns the translated label for this field.
	 * 
	 * @param translationService
	 *            for Translation.
	 * @return translated Label.
	 */
	String getTranslatedLabel(TranslationService translationService);

	/**
	 * Return the translated tool tip text for this field.
	 * 
	 * @param translationService
	 *            for Translation.
	 * @return translated ToolTip.
	 */
	String getTranslatedToolTip(TranslationService translationService);

	/**
	 * Updates the Bean with newValue.
	 * 
	 * @param bean
	 *            to be updated with the new value.
	 * @param newValue
	 *            used to update on the bean.
	 */
	void updatePlugInConfig(Object bean, String newValue);

	/**
	 * 
	 * @param bean
	 *            to extract a String value from.
	 * @return String value of the bean.
	 */
	String getStringValue(Object bean);

	/**
	 * Is this field a password field an needs an echo character in the UI.
	 * 
	 * @return true if it is a password field.
	 */
	boolean isPassword();

	/**
	 * Allows the Field to be set read only in an editor context.
	 * 
	 * @return true if the field is in a read only context.
	 */
	boolean isReadOnly();

	/**
	 * Returns the ID constant (CustomWidgetIdConstants).
	 * 
	 * @return ID constant
	 */
	String getIdConstant();

}
