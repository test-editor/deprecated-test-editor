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
package org.testeditor.dummylibrary;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.eclipse.e4.core.services.translation.TranslationService;
import org.testeditor.core.model.action.ProjectLibraryConfig;
import org.testeditor.core.services.interfaces.FieldMappingExtension;
import org.testeditor.core.services.plugins.LibraryConfigurationServicePlugIn;

/**
 * 
 * Example Implementation of the <code>LibraryConfigurationService</code>. This
 * class is only a dummy, to show the usage of the framework.
 * 
 */
public class DummyLibraryConfigurationService implements LibraryConfigurationServicePlugIn {

	@Override
	public ProjectLibraryConfig createProjectLibraryConfigFrom(Properties properties) {
		return null;
	}

	@Override
	public String getId() {
		return DummyProjectLibraryConfig.ID;
	}

	@Override
	public Map<String, String> getAsProperties(ProjectLibraryConfig projectLibraryConfig) {
		return null;
	}

	@Override
	public List<FieldMappingExtension> getConfigUIExtensions() {
		List<FieldMappingExtension> result = new ArrayList<FieldMappingExtension>();
		result.add(new FieldMappingExtension() {

			@Override
			public void updatePlugInConfig(Object bean, String text) {
			}

			@Override
			public String getTranslatedLabel(TranslationService translationService) {
				return "Dummy Config";
			}

			@Override
			public String getTranslatedToolTip(TranslationService translationService) {
				return "A Dummy Config";
			}

			@Override
			public String getStringValue(Object bean) {
				return "dummy Value";
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
		});
		return result;
	}

	@Override
	public ProjectLibraryConfig createEmptyProjectLibraryConfig() {
		return new DummyProjectLibraryConfig();
	}

	@Override
	public String getTranslatedHumanReadableLibraryPlugInName(TranslationService translationService) {
		return "Dummy Plug-In";
	}

	@Override
	public String getTemplateForConfiguration() {
		return "Dummytemplate";
	}

}
