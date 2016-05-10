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
package org.testeditor.ui.wizardpages.nameinspector;

import java.util.regex.Pattern;

import javax.inject.Inject;

import org.testeditor.ui.utilities.TestEditorTranslationService;

/**
 * abstract Class for the handling of the TestEditorTranslationService.
 * 
 * 
 */
public class DefaultNameInspector implements INameInspector {
	@Inject
	private TestEditorTranslationService translationService;

	private Pattern nameVerifier = Pattern.compile("([A-Z]+[a-z\\d]*)+");

	private String nameInvalidMessage;

	/**
	 * @param msgKey
	 *            key of the message
	 * @param params
	 *            params in translated text given as placeholder example: this
	 * @return the translated message to the key
	 */
	protected String translate(String msgKey, Object... params) {
		return translationService.translate(msgKey, params);
	}

	@Override
	public boolean isNameValid(String name) {
		if (nameVerifier.matcher(name).matches()) {
			return true;
		}
		setNameInvalidMessage(translate("%wizard.error.msg.nameDoesntMatchThePattern"));
		return false;
	}

	@Override
	public String nameInvalideMessage() {
		return nameInvalidMessage;
	}

	/**
	 * Sets the error message.
	 * 
	 * @param nameInvalidMessage
	 *            to shown in ui.
	 */
	protected void setNameInvalidMessage(String nameInvalidMessage) {
		this.nameInvalidMessage = nameInvalidMessage;
	}

}
