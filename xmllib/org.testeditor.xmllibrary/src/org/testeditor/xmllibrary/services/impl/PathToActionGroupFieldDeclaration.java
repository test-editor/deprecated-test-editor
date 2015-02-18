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
package org.testeditor.xmllibrary.services.impl;

import org.eclipse.e4.core.services.translation.TranslationService;
import org.testeditor.core.services.interfaces.FieldDeclaration;
import org.testeditor.xmllibrary.model.XMLProjectLibraryConfig;

/**
 * 
 * Field Declaration to handle the Path to an ActionGroup File.
 * 
 */
public class PathToActionGroupFieldDeclaration implements FieldDeclaration {

	@Override
	public void updatePlugInConfig(Object bean, String newValue) {
		((XMLProjectLibraryConfig) bean).setPathToActionGroupXml(newValue);
	}

	@Override
	public String getTranslatedLabel(TranslationService translationService) {
		return translationService.translate("%testprojecteditor.pathToActionGroupXML",
				"platform:/plugin/org.testeditor.ui");
	}

	@Override
	public String getTranslatedToolTip(TranslationService translationService) {
		return translationService.translate("%testprojecteditor.pathToActionGroupXML.MouseOver",
				"platform:/plugin/org.testeditor.ui");
	}

	@Override
	public String getStringValue(Object bean) {
		return ((XMLProjectLibraryConfig) bean).getPathToXmlActionGroup();
	}

	@Override
	public boolean isPassword() {
		return false;
	}

	@Override
	public boolean isReadOnly() {
		return false;
	}

	@Override
	public String getIdConstant() {
		return null;
	}

}
