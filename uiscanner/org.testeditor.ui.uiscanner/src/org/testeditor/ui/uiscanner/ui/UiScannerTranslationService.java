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
package org.testeditor.ui.uiscanner.ui;

import java.text.MessageFormat;

import javax.inject.Inject;

import org.eclipse.e4.core.services.translation.TranslationService;

/**
 * 
 * @author dkuhlmann
 *
 */
public class UiScannerTranslationService {
	@Inject
	private TranslationService translationService;

	public static final String CONTRIBUTOR_URI = "platform:/plugin/org.testeditor.ui.uiscanner";

	/**
	 * <pre>
	 * Translates the given key into the local language.
	 * 
	 * You can invoke this method with additionaly params depends on translated
	 * text. The params will be replace the placeholder in text.
	 * 
	 * e.g: 
	 * given text with placeholder: "{0} value and {1} value"
	 * params are: "first","second"
	 * result text: first value and second value
	 * 
	 * </pre>
	 * 
	 * @param key
	 *            key
	 * @param params
	 *            params in translated text given as placeholder example: this
	 * 
	 * @return local language value
	 */
	public String translate(String key, Object... params) {
		String translatedText = translationService.translate(key, CONTRIBUTOR_URI);

		return MessageFormat.format(translatedText, params);

	}

}
