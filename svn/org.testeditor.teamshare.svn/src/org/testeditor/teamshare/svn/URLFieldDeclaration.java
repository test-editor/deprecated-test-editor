/*******************************************************************************
 * Copyright (c) 2012, 2014 Signal Iduna Corporation and others.
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
import org.testeditor.core.services.interfaces.FieldDeclaration;

/**
 * 
 * Field Declaration for URL.
 * 
 */
public class URLFieldDeclaration implements FieldDeclaration {

	@Override
	public String getTranslatedLabel(TranslationService translationService) {
		return translationService.translate("%svnteamshare.url.label", "platform:/plugin/org.testeditor.teamshare.svn");
	}

	@Override
	public String getTranslatedToolTip(TranslationService translationService) {
		return translationService.translate("%svnteamshare.url.tooltip",
				"platform:/plugin/org.testeditor.teamshare.svn");
	}

	@Override
	public void updatePlugInConfig(Object bean, String newValue) {
		((SVNTeamShareConfig) bean).setUrl(newValue);
	}

	@Override
	public String getStringValue(Object bean) {
		return ((SVNTeamShareConfig) bean).getUrl();
	}

	@Override
	public boolean isPassword() {
		return false;
	}

	@Override
	public boolean isReadOnly() {
		return true;
	}

	@Override
	public String getIdConstant() {
		return WidgetIDConstants.WIZARD_SHARE_PROJEKT_URL;
	}

}
