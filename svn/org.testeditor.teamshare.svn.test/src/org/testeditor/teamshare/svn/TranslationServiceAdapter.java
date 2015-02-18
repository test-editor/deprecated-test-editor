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
package org.testeditor.teamshare.svn;

import org.eclipse.e4.core.services.translation.TranslationService;

/**
 * adapter for the translationServiceMockup.
 */
public class TranslationServiceAdapter {
	/**
	 * 
	 * @return TranslationService- mockup
	 */
	public TranslationService getTranslationService() {
		return new TranslationService() {
			public String translate(String key, String contributorURI) {
				String message = "translated key " + key + " {0} {1}";
				return message;
			}
		};
	}
}
