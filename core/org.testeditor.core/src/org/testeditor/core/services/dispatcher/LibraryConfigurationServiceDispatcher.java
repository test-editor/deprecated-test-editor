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
package org.testeditor.core.services.dispatcher;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.eclipse.e4.core.services.translation.TranslationService;
import org.testeditor.core.model.action.ProjectLibraryConfig;
import org.testeditor.core.services.interfaces.FieldMappingExtension;
import org.testeditor.core.services.interfaces.LibraryConfigurationService;

public class LibraryConfigurationServiceDispatcher implements LibraryConfigurationService {

	@Override
	public ProjectLibraryConfig createProjectLibraryConfigFrom(Properties properties) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, String> getAsProperties(ProjectLibraryConfig projectLibraryConfig) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTranslatedHumanReadableLibraryPlugInName(TranslationService translationService) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<FieldMappingExtension> getConfigUIExtensions() {
		// TODO Auto-generated method stub
		return null;
	}

}
